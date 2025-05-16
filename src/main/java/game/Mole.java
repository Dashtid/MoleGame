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
	private static final int SKY_HEIGHT = 5;
	private static final int POWER_UP_DURATION = 10;
	private static final int MIN_TIME_LIMIT = 5000;

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

	public void drawWorld(int level) {
		System.out.println("Starting drawWorld for level " + level);

		// Clear the grid first
		g.rectangle(0, 0, g.getGridWidth(), g.getGridHeight(), Color.WHITE);

		// Draw sky and soil
		System.out.println("Drawing sky and soil");
		g.rectangle(0, 0, g.getGridWidth(), SKY_HEIGHT, ColorConstants.SKY);
		g.rectangle(0, SKY_HEIGHT, g.getGridWidth(), g.getGridHeight() - SKY_HEIGHT, ColorConstants.SOIL);

		// Place obstacles
		int obstacleCount = INITIAL_OBSTACLE_COUNT + level * OBSTACLE_INCREASE_PER_LEVEL;
		System.out.println("Placing " + obstacleCount + " obstacles");
		placeRandomBlocks(obstacleCount, ColorConstants.OBSTACLE, SKY_HEIGHT, g.getGridHeight());

		// Place power-ups
		int powerUpCount = INITIAL_POWER_UP_COUNT + level;
		System.out.println("Placing " + powerUpCount + " power-ups");
		placeRandomBlocks(powerUpCount, ColorConstants.POWER_UP, SKY_HEIGHT, g.getGridHeight());

		// Place goal
		int goalX, goalY;
		do {
			goalX = (int) (Math.random() * g.getGridWidth());
			goalY = SKY_HEIGHT + (int) (Math.random() * (g.getGridHeight() - SKY_HEIGHT));
		} while (g.getBlockColor(goalX, goalY) != ColorConstants.SOIL);
		System.out.println("Placing goal at (" + goalX + ", " + goalY + ")");
		g.block(goalX, goalY, ColorConstants.GOAL);

		// Place the mole LAST to ensure it's visible
		int startX = g.getGridWidth() / 2;
		int startY = SKY_HEIGHT + 1;
		System.out.println("Placing mole at (" + startX + ", " + startY + ")");
		g.block(startX, startY, ColorConstants.MOLE);

		// Force a repaint
		g.repaint();
	}

	private void placeRandomBlocks(int count, Color color, int minY, int maxY) {
		for (int i = 0; i < count; i++) {
			int x = (int) (Math.random() * g.getGridWidth());
			int y = minY + (int) (Math.random() * (maxY - minY));
			if (g.getBlockColor(x, y) == ColorConstants.SOIL) { // Only place if spot is empty soil
				g.block(x, y, color);
			}
		}
	}

	private long getRemainingTime(long startTime, long timeLimit) {
		long elapsedTime = System.currentTimeMillis() - startTime;
		return timeLimit - elapsedTime;
	}

	public boolean dig(int level) {
		logger.info("Starting dig method for level " + level);
		int x = g.getGridWidth() / 2;
		int y = SKY_HEIGHT + 1; // Start just below sky
		int moves = 0;
		long startTime = System.currentTimeMillis();
		long timeLimit = Math.max(MIN_TIME_LIMIT, INITIAL_TIME_LIMIT - (level * TIME_DECREASE_PER_LEVEL));
		final Object speedBoostLock = new Object();
		final boolean[] speedBoost = { false };
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

		try {
			while (true) {
				// Update timer display
				long remainingTime = getRemainingTime(startTime, timeLimit);
				g.updateTimer(remainingTime / 1000);

				char key = inputHandler.waitForKeyPress();

				int moveDistance;
				synchronized (speedBoostLock) {
					moveDistance = speedBoost[0] ? 2 : 1;
				}

				Map<Character, int[]> movementMap = Map.of(
						'w', new int[] { 0, -1 },
						'a', new int[] { -1, 0 },
						's', new int[] { 0, 1 },
						'd', new int[] { 1, 0 });

				Color blockColor = null;
				int newX = x, newY = y;

				if (key == 'p') {
					g.showPauseScreen("Game Paused. Press 'r' to resume.");
					while (g.waitForKey() != 'r') {
						// Wait for 'r' key
					}
					g.hidePauseScreen();
					continue;
				}

				if (movementMap.containsKey(key)) {
					int[] delta = movementMap.get(key);
					newX += delta[0] * moveDistance;
					newY += delta[1] * moveDistance;

					// Check boundaries
					if (newX < 0 || newX >= g.getGridWidth() || newY < 0 || newY >= g.getGridHeight()) {
						System.out.println("Cannot move outside boundaries");
						continue;
					}

					if (isSky(newY)) {
						g.showMessage("You can't dig in the sky!");
						continue;
					}

					blockColor = g.getBlockColor(newX, newY);
					if (blockColor == ColorConstants.OBSTACLE) {
						g.showMessage("You hit an obstacle!");
						continue;
					}

					// Fill all cells between old and new position with TUNNEL
					int steps = Math.max(Math.abs(newX - x), Math.abs(newY - y));
					for (int i = 1; i <= steps; i++) {
						int intermediateX = x + (newX - x) * i / steps;
						int intermediateY = y + (newY - y) * i / steps;
						// Only fill with tunnel if not the destination
						if (i < steps) {
							g.block(intermediateX, intermediateY, ColorConstants.TUNNEL);
						}
					}
					// Remove the mole from the current position (turn to tunnel)
					g.block(x, y, ColorConstants.TUNNEL);

					// Update position and increment moves
					x = newX;
					y = newY;
					moves++;

					// Draw the mole at the new position
					g.block(x, y, ColorConstants.MOLE);

					// Handle power-up
					if (blockColor == ColorConstants.POWER_UP) {
						logger.info("Power-up collected at position (" + newX + ", " + newY + ")");
						g.showMessage("Speed boost activated!");
						synchronized (speedBoostLock) {
							speedBoost[0] = true;
						}
						g.showPowerUpEffect();
						scheduler.schedule(() -> {
							synchronized (speedBoostLock) {
								speedBoost[0] = false;
							}
						}, POWER_UP_DURATION, TimeUnit.SECONDS);
					}

					// Handle goal
					if (blockColor == ColorConstants.GOAL) {
						long endTime = System.currentTimeMillis();
						logger.info("Goal reached in " + moves + " moves and " + (endTime - startTime) / 1000
								+ " seconds.");
						g.showMessage("Level completed in " + moves + " moves!");
						score += 100 * level;
						g.updateScore(score);
						return true;
					}
				}

				// Check if time is up
				if (System.currentTimeMillis() - startTime > timeLimit) {
					logger.warning("Time's up! Level failed.");
					g.showMessage("Time's up! Level failed.");
					return false;
				}
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