package game;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import utils.InputHandler;

public class Mole {
	private static final int INITIAL_GRID_WIDTH = 30;
	private static final int INITIAL_GRID_HEIGHT = 50;
	private static final int BLOCK_SIZE = 20;
	private static final int INITIAL_TIME_LIMIT = 60000;
	private static final int TIME_DECREASE_PER_LEVEL = 5000;
	private static final int INITIAL_OBSTACLE_COUNT = 50;
	private static final int OBSTACLE_INCREASE_PER_LEVEL = 10;
	private static final int INITIAL_POWER_UP_COUNT = 5;
	private static final int SKY_HEIGHT = 5; // Constant for sky height
	private static final int POWER_UP_DURATION = 10; // seconds
	private static final int MIN_TIME_LIMIT = 5000; // milliseconds

	private GameGraphics g;
	private int score = 0;
	private static final Logger logger = Logger.getLogger(Mole.class.getName());
	private InputHandler inputHandler;

	public Mole() {
		try {
			this.inputHandler = new InputHandler();
			this.g = new GameGraphics(INITIAL_GRID_WIDTH, INITIAL_GRID_HEIGHT, BLOCK_SIZE, inputHandler);
		} catch (Exception e) {
			if (g != null) {
				g.showError("Failed to initialize GameGraphics: " + e.getMessage());
			} else {
				System.err.println("Failed to initialize GameGraphics: " + e.getMessage());
			}
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		while (true) {
			Mole m = new Mole();
			m.startGame();
			String choice = m.g.showInputDialog("Do you want to play again? (y/n)");
			if (choice == null || !choice.equalsIgnoreCase("y")) {
				m.g.showMessage("Thanks for playing!");
				break;
			}
		}
	}

	public void startGame() {
		int level = 1;
		while (true) {
			g.showMessage("Starting Level " + level);
			int newGridWidth = INITIAL_GRID_WIDTH + level * 5;
			int newGridHeight = INITIAL_GRID_HEIGHT + level * 5;
			g.resizeGrid(newGridWidth, newGridHeight);
			drawWorld(level);
			boolean success = dig(level);
			if (success) {
				g.showMessage("Level " + level + " completed!");
				level++;
			} else {
				g.showGameOverScreen(score);
				break;
			}
		}
	}

	private void placeRandomBlocks(int count, Color color, int minY, int maxY) {
		for (int i = 0; i < count; i++) {
			int x = (int) (Math.random() * g.getGridWidth());
			int y = minY + (int) (Math.random() * (maxY - minY));
			g.block(x, y, color);
		}
	}

	public void drawWorld(int level) {
		g.rectangle(0, SKY_HEIGHT, g.getGridWidth(), g.getGridHeight() - SKY_HEIGHT, ColorConstants.SOIL);
		g.rectangle(0, 0, g.getGridWidth(), SKY_HEIGHT, ColorConstants.SKY);

		int obstacleCount = INITIAL_OBSTACLE_COUNT + level * OBSTACLE_INCREASE_PER_LEVEL;
		placeRandomBlocks(obstacleCount, ColorConstants.OBSTACLE, SKY_HEIGHT, g.getGridHeight());

		int powerUpCount = INITIAL_POWER_UP_COUNT + level;
		placeRandomBlocks(powerUpCount, ColorConstants.POWER_UP, SKY_HEIGHT, g.getGridHeight());

		int goalX, goalY;
		do {
			goalX = (int) (Math.random() * g.getGridWidth());
			goalY = SKY_HEIGHT + (int) (Math.random() * (g.getGridHeight() - SKY_HEIGHT));
		} while (g.getBlockColor(goalX, goalY) != null); // Ensure no overlap
		g.block(goalX, goalY, ColorConstants.GOAL);

		// Ensure the mole's starting cell is empty (tunnel)
		int startX = g.getGridWidth() / 2;
		int startY = g.getGridHeight() / 2;
		g.block(startX, startY, ColorConstants.TUNNEL);
	}

	private long getRemainingTime(long startTime, long timeLimit) {
		long elapsedTime = System.currentTimeMillis() - startTime;
		return timeLimit - elapsedTime;
	}

	public boolean dig(int level) {
		logger.info("Starting dig method for level " + level);
		int x = g.getGridWidth() / 2;
		int y = g.getGridHeight() / 2;
		int moves = 0;
		long startTime = System.currentTimeMillis();
		long timeLimit = Math.max(MIN_TIME_LIMIT, INITIAL_TIME_LIMIT - (level * TIME_DECREASE_PER_LEVEL));
		final Object speedBoostLock = new Object(); // Lock for thread-safe access
		final boolean[] speedBoost = { false }; // Use an array to allow modification inside the lambda
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

		// Draw the mole at the starting position (after world is drawn)
		g.block(x, y, ColorConstants.MOLE);

		try {
			while (true) {
				char key = inputHandler.waitForKeyPress();

				// Remove the mole from the current position (turn to tunnel)
				g.block(x, y, ColorConstants.TUNNEL);

				if (key == 'p') {
					g.showPauseScreen("Game Paused. Press 'r' to resume.");
					while (g.waitForKey() != 'r') {
					}
					g.hidePauseScreen();
					// Redraw the mole after pause
					g.block(x, y, ColorConstants.MOLE);
					continue;
				}

				int newX = x, newY = y;
				int moveDistance;
				synchronized (speedBoostLock) {
					moveDistance = speedBoost[0] ? 2 : 1;
				}

				Map<Character, int[]> movementMap = Map.of(
						'w', new int[] { 0, -1 },
						'a', new int[] { -1, 0 },
						's', new int[] { 0, 1 },
						'd', new int[] { 1, 0 });

				if (movementMap.containsKey(key)) {
					int[] delta = movementMap.get(key);
					newX += delta[0] * moveDistance;
					newY += delta[1] * moveDistance;
				}

				if (isSky(newY)) {
					g.showMessage("You can't dig in the sky!");
					// Redraw the mole at the current position
					g.block(x, y, ColorConstants.MOLE);
					continue;
				}

				Color blockColor = g.getBlockColor(newX, newY);
				if (blockColor != null && blockColor == ColorConstants.POWER_UP) {
					logger.info("Power-up collected at position (" + newX + ", " + newY + ")");
					g.showMessage("You collected a power-up!");
					synchronized (speedBoostLock) {
						speedBoost[0] = true;
					}
					g.block(newX, newY, ColorConstants.TUNNEL);
					g.showPowerUpEffect();
					scheduler.schedule(() -> {
						synchronized (speedBoostLock) {
							speedBoost[0] = false;
						}
					}, POWER_UP_DURATION, TimeUnit.SECONDS);
				}

				if (blockColor != null && blockColor == ColorConstants.GOAL) {
					long endTime = System.currentTimeMillis();
					logger.info(
							"Goal reached in " + moves + " moves and " + (endTime - startTime) / 1000 + " seconds.");
					g.showMessage("You reached the goal in " + moves + " moves and " + (endTime - startTime) / 1000
							+ " seconds! You win!");
					score += 100 * level;
					g.updateScore(score);
					return true;
				}

				if (blockColor != null && blockColor != ColorConstants.OBSTACLE) {
					// Update position and increment moves
					x = newX;
					y = newY;
					moves++;
				} else if (blockColor == ColorConstants.OBSTACLE) {
					// Notify the player about hitting an obstacle
					g.showMessage("You hit an obstacle!");
				}

				long elapsedTime = System.currentTimeMillis() - startTime;

				// Check if time is up
				if (elapsedTime > timeLimit) {
					logger.warning("Time's up! Level failed.");
					g.showMessage("Time's up! You failed the level.");
					g.showGameOverScreen(score);
					return false;
				}

				// Draw the mole at the new position
				g.block(x, y, ColorConstants.MOLE);

				// Update the timer display with remaining time
				long remainingTime = getRemainingTime(startTime, timeLimit);
				g.updateTimer(remainingTime / 1000);
			}
		} finally {
			scheduler.shutdown();
			try {
				if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
					scheduler.shutdownNow();
				}
			} catch (InterruptedException e) {
				scheduler.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}

	private boolean isSky(int y) {
		return y < SKY_HEIGHT;
	}
}
