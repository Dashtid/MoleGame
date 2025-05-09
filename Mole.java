import java.awt.Color;

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

	private Graphics g;
	private int score = 0;

	public Mole() {
		try {
			this.g = new Graphics(INITIAL_GRID_WIDTH, INITIAL_GRID_HEIGHT, BLOCK_SIZE);
		} catch (Exception e) {
			System.err.println("Failed to initialize Graphics: " + e.getMessage());
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		while (true) {
			Mole m = new Mole();
			m.startGame();
			System.out.println("Do you want to play again? (y/n)");
			char choice = new java.util.Scanner(System.in).next().toLowerCase().charAt(0);
			if (choice != 'y') {
				System.out.println("Thanks for playing!");
				break;
			}
		}
	}

	public void startGame() {
		int level = 1;
		while (true) {
			System.out.println("Starting Level " + level);
			int newGridWidth = INITIAL_GRID_WIDTH + level * 5;
			int newGridHeight = INITIAL_GRID_HEIGHT + level * 5;
			g.resizeGrid(newGridWidth, newGridHeight);
			drawWorld(level);
			boolean success = dig(level);
			if (success) {
				System.out.println("Level " + level + " completed!");
				level++;
			} else {
				System.out.println("Game Over! Your final score is: " + score);
				break;
			}
		}
	}

	public void drawWorld(int level) {
		g.rectangle(0, SKY_HEIGHT, g.getGridWidth(), g.getGridHeight() - SKY_HEIGHT, ColorConstants.SOIL);
		g.rectangle(0, 0, g.getGridWidth(), SKY_HEIGHT, ColorConstants.SKY);
		int obstacleCount = INITIAL_OBSTACLE_COUNT + level * OBSTACLE_INCREASE_PER_LEVEL;
		for (int i = 0; i < obstacleCount; i++) {
			int obstacleX = (int) (Math.random() * g.getGridWidth());
			int obstacleY = SKY_HEIGHT + (int) (Math.random() * (g.getGridHeight() - SKY_HEIGHT));
			g.block(obstacleX, obstacleY, ColorConstants.OBSTACLE);
		}
		int powerUpCount = INITIAL_POWER_UP_COUNT + level;
		for (int i = 0; i < powerUpCount; i++) {
			int powerUpX = (int) (Math.random() * g.getGridWidth());
			int powerUpY = SKY_HEIGHT + (int) (Math.random() * (g.getGridHeight() - SKY_HEIGHT));
			g.block(powerUpX, powerUpY, ColorConstants.POWER_UP);
		}
		int goalX = (int) (Math.random() * g.getGridWidth());
		int goalY = SKY_HEIGHT + (int) (Math.random() * (g.getGridHeight() - SKY_HEIGHT));
		g.block(goalX, goalY, ColorConstants.GOAL);
	}

	public boolean dig(int level) {
		int x = g.getGridWidth() / 2;
		int y = g.getGridHeight() / 2;
		int moves = 0;
		long startTime = System.currentTimeMillis();
		long timeLimit = Math.max(5000, INITIAL_TIME_LIMIT - (level * TIME_DECREASE_PER_LEVEL));
		boolean speedBoost = false;

		while (true) {
			g.block(x, y, ColorConstants.MOLE);
			char key = g.waitForKey();
			g.block(x, y, ColorConstants.TUNNEL);

			if (key == 'p') {
				System.out.println("Game paused. Press 'r' to resume.");
				g.showPauseScreen("Game Paused. Press 'r' to resume.");
				while (g.waitForKey() != 'r') {
				}
				g.hidePauseScreen();
				continue;
			}

			int newX = x, newY = y;
			int moveDistance = speedBoost ? 2 : 1;
			if (key == 'w' && y > moveDistance - 1)
				newY -= moveDistance;
			else if (key == 'a' && x > moveDistance - 1)
				newX -= moveDistance;
			else if (key == 's' && y < g.getGridHeight() - moveDistance)
				newY += moveDistance;
			else if (key == 'd' && x < g.getGridWidth() - moveDistance)
				newX += moveDistance;

			if (newY < SKY_HEIGHT) {
				System.out.println("You can't dig in the sky!");
				continue;
			}

			Color blockColor = g.getBlockColor(newX, newY);
			if (blockColor != null && blockColor == ColorConstants.POWER_UP) {
				System.out.println("You collected a power-up!");
				speedBoost = true;
				g.block(newX, newY, ColorConstants.TUNNEL);
				g.showPowerUpEffect();
				new java.util.Timer().schedule(new java.util.TimerTask() {
					@Override
					public void run() {
						speedBoost = false;
					}
				}, 10000);
			}

			if (blockColor != null && blockColor == ColorConstants.GOAL) {
				long endTime = System.currentTimeMillis();
				System.out.println("You reached the goal in " + moves + " moves and " + (endTime - startTime) / 1000
						+ " seconds! You win!");
				score += 100 * level;
				g.updateScore(score);
				return true;
			}

			if (blockColor != null && blockColor != ColorConstants.OBSTACLE) {
				x = newX;
				y = newY;
				moves++;
			} else if (blockColor == ColorConstants.OBSTACLE) {
				System.out.println("You hit an obstacle!");
			}

			if (System.currentTimeMillis() - startTime > timeLimit) {
				System.out.println("Time's up! You failed the level.");
				g.showGameOverScreen(score);
				return false;
			}

			long remainingTime = timeLimit - (System.currentTimeMillis() - startTime);
			g.updateTimer(remainingTime / 1000);
		}
	}
}

public class Graphics {
	private int gridWidth;
	private int gridHeight;
	private int blockSize;

	public Graphics(int width, int height, int blockSize) {
		this.gridWidth = width;
		this.gridHeight = height;
		this.blockSize = blockSize;
	}

	public void resizeGrid(int width, int height) {
		this.gridWidth = width;
		this.gridHeight = height;
	}

	public int getGridWidth() {
		return gridWidth;
	}

	public int getGridHeight() {
		return gridHeight;
	}

	public void rectangle(int x, int y, int width, int height, Color color) {
		for (int i = x; i < x + width; i++) {
			for (int j = y; j < y + height; j++) {
				block(i, j, color);
			}
		}
	}

	public void block(int x, int y, Color color) {
		// Add implementation to draw a block at position x,y with the specified color
	}
}
