public class Mole {
	private Graphics g = new Graphics(30, 50, 20); // Grid dimensions and block size
	private int score = 0;

	public static void main(String[] args) {
		Mole m = new Mole();
		m.startGame();
	}

	/**
	 * Starts the game and progresses through levels.
	 */
	public void startGame() {
		int level = 1;
		while (true) {
			System.out.println("Starting Level " + level);
			g = new Graphics(30 + level * 5, 50 + level * 5, 20); // Increase grid size with each level
			drawWorld(level);
			boolean success = dig(level);
			if (success) {
				System.out.println("Level " + level + " completed!");
				level++;
			} else {
				System.out.println("Game Over! You failed at Level " + level);
				System.out.println("Game Over! Your final score is: " + score);
				break;
			}
		}
	}

	/**
	 * Draws the initial world with soil and sky.
	 */
	public void drawWorld(int level) {
		g.rectangle(0, 5, g.getGridWidth(), g.getGridHeight() - 5, ColorConstants.SOIL); // Soil
		g.rectangle(0, 0, g.getGridWidth(), 5, ColorConstants.SKY); // Sky

		// Add random green elements to the sky
		int greenElementCount = 10; // Number of green elements
		for (int i = 0; i < greenElementCount; i++) {
			int greenX = (int) (Math.random() * g.getGridWidth());
			int greenY = (int) (Math.random() * 5); // Restrict to the sky area
			g.block(greenX, greenY, ColorConstants.GOAL); // Use green color for elements
		}

		// Add random obstacles
		int obstacleCount = 50 + level * 10; // Increase obstacles with level
		for (int i = 0; i < obstacleCount; i++) {
			int obstacleX = (int) (Math.random() * g.getGridWidth());
			int obstacleY = 5 + (int) (Math.random() * (g.getGridHeight() - 5));
			g.block(obstacleX, obstacleY, ColorConstants.OBSTACLE);
		}

		// Add power-ups
		int powerUpCount = 5; // Fixed number of power-ups
		for (int i = 0; i < powerUpCount; i++) {
			int powerUpX = (int) (Math.random() * g.getGridWidth());
			int powerUpY = 5 + (int) (Math.random() * (g.getGridHeight() - 5));
			g.block(powerUpX, powerUpY, ColorConstants.POWER_UP);
		}

		// Add a goal
		int goalX = (int) (Math.random() * g.getGridWidth());
		int goalY = 5 + (int) (Math.random() * (g.getGridHeight() - 5));
		g.block(goalX, goalY, ColorConstants.GOAL);
	}

	/**
	 * Allows the mole to dig tunnels based on user input.
	 */
	public boolean dig(int level) {
		int x = g.getGridWidth() / 2; // Start in the middle
		int y = g.getGridHeight() / 2;
		int moves = 0; // Track the number of moves
		long startTime = System.currentTimeMillis(); // Start the timer
		long timeLimit = 60000 - (level * 5000); // Decrease time limit with level
		boolean speedBoost = false; // Track speed boost

		while (true) {
			g.block(x, y, ColorConstants.MOLE); // Draw the mole
			char key = g.waitForKey(); // Wait for user input
			g.block(x, y, ColorConstants.TUNNEL); // Leave a tunnel behind

			// Pause feature
			if (key == 'p') {
				System.out.println("Game paused. Press any key to resume.");
				g.showPauseScreen();
				g.waitForKey(); // Wait for resume
				g.hidePauseScreen();
				continue;
			}

			// Move the mole based on the key press
			int newX = x, newY = y;
			int moveDistance = speedBoost ? 2 : 1; // Move twice as far with speed boost
			if (key == 'w' && y > moveDistance - 1)
				newY -= moveDistance; // Move up
			else if (key == 'a' && x > moveDistance - 1)
				newX -= moveDistance; // Move left
			else if (key == 's' && y < g.getGridHeight() - moveDistance)
				newY += moveDistance; // Move down
			else if (key == 'd' && x < g.getGridWidth() - moveDistance)
				newX += moveDistance; // Move right

			// Prevent digging in the sky
			if (newY < 5) {
				System.out.println("You can't dig in the sky!");
				continue;
			}

			// Check for power-ups
			if (g.getBlockColor(newX, newY) == ColorConstants.POWER_UP) {
				System.out.println("You collected a power-up!");
				speedBoost = true;
				g.block(newX, newY, ColorConstants.TUNNEL); // Properly fill the hole
				g.showPowerUpEffect(); // Show visual effect
			}

			// Check for goal
			if (g.getBlockColor(newX, newY) == ColorConstants.GOAL) {
				long endTime = System.currentTimeMillis(); // End the timer
				System.out.println("You reached the goal in " + moves + " moves and " + (endTime - startTime) / 1000
						+ " seconds! You win!");
				score += 100 * level; // Add points for completing the level
				g.updateScore(score); // Update score display
				return true;
			}

			// Check for obstacles
			if (g.getBlockColor(newX, newY) != ColorConstants.OBSTACLE) {
				x = newX;
				y = newY;
				moves++; // Increment moves
			} else {
				System.out.println("You hit an obstacle!");
			}

			// Check time limit
			if (System.currentTimeMillis() - startTime > timeLimit) {
				System.out.println("Time's up! You failed the level.");
				return false;
			}

			// Update timer display
			long remainingTime = timeLimit - (System.currentTimeMillis() - startTime);
			g.updateTimer(remainingTime / 1000); // Update timer in seconds
		}
	}
}
