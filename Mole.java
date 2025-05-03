public class Mole {
	private Graphics g = new Graphics(30, 50, 20); // Grid dimensions and block size

	public static void main(String[] args) {
		Mole m = new Mole();
		m.drawWorld();
		m.dig();
	}

	/**
	 * Draws the initial world with soil and sky.
	 */
	public void drawWorld() {
		g.rectangle(0, 5, g.getGridWidth(), g.getGridHeight() - 5, ColorConstants.SOIL); // Soil
		g.rectangle(0, 0, g.getGridWidth(), 5, ColorConstants.SKY); // Sky

		// Add random obstacles
		for (int i = 0; i < 50; i++) { // Add 50 obstacles
			int obstacleX = (int) (Math.random() * g.getGridWidth());
			int obstacleY = 5 + (int) (Math.random() * (g.getGridHeight() - 5));
			g.block(obstacleX, obstacleY, ColorConstants.OBSTACLE);
		}

		// Add a goal
		int goalX = (int) (Math.random() * g.getGridWidth());
		int goalY = 5 + (int) (Math.random() * (g.getGridHeight() - 5));
		g.block(goalX, goalY, ColorConstants.GOAL);
	}

	/**
	 * Allows the mole to dig tunnels based on user input.
	 */
	public void dig() {
		int x = g.getGridWidth() / 2; // Start in the middle
		int y = g.getGridHeight() / 2;
		int moves = 0; // Track the number of moves
		long startTime = System.currentTimeMillis(); // Start the timer

		while (true) {
			g.block(x, y, ColorConstants.MOLE); // Draw the mole
			char key = g.waitForKey(); // Wait for user input
			g.block(x, y, ColorConstants.TUNNEL); // Leave a tunnel behind

			// Move the mole based on the key press
			int newX = x, newY = y;
			if (key == 'w' && y > 0)
				newY -= 1; // Move up
			else if (key == 'a' && x > 0)
				newX -= 1; // Move left
			else if (key == 's' && y < g.getGridHeight() - 1)
				newY += 1; // Move down
			else if (key == 'd' && x < g.getGridWidth() - 1)
				newX += 1; // Move right

			// Check for goal
			if (g.getBlockColor(newX, newY) == ColorConstants.GOAL) {
				long endTime = System.currentTimeMillis(); // End the timer
				System.out.println("You reached the goal in " + moves + " moves and " + (endTime - startTime) / 1000
						+ " seconds! You win!");
				break;
			}

			// Check for obstacles
			if (g.getBlockColor(newX, newY) != ColorConstants.OBSTACLE) {
				x = newX;
				y = newY;
				moves++; // Increment moves
			} else {
				System.out.println("You hit an obstacle!");
			}
		}
	}
}
