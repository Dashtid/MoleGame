import javax.swing.*;
import java.awt.*;

/**
 * Handles rendering for the mole game using Swing and AWT.
 */
public class GameGraphics extends JPanel {
	private JFrame frame;
	private int width;
	private int blockSize;
	private int height;
	private Color[][] grid;
	private char lastKeyPressed;
	private int score;
	private long timer;

	public GameGraphics(int w, int h, int bs) {
		this.width = w;
		this.blockSize = bs;
		this.height = h;
		this.grid = new Color[width][height];
		this.score = 0;

		// Initialize the JFrame
		frame = new JFrame("Mole Game - Advanced Edition");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width * blockSize, height * blockSize + 50); // Add space for score panel
		frame.add(this);
		frame.setVisible(true);

		// Add key listener for user input
		frame.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyPressed(java.awt.event.KeyEvent e) {
				lastKeyPressed = e.getKeyChar();
				synchronized (GameGraphics.this) {
					GameGraphics.this.notify(); // Notify waiting thread
				}
			}
		});
	}

	/**
	 * Resizes the game grid to the specified width and height.
	 */
	public void resizeGrid(int newWidth, int newHeight) {
		this.width = newWidth;
		this.height = newHeight;
		this.grid = new Color[width][height]; // Create a new grid with the updated size
		frame.setSize(width * blockSize, height * blockSize + 50); // Update the frame size
		repaint(); // Redraw the updated grid
	}

	/**
	 * Hides the pause screen overlay.
	 */
	public void hidePauseScreen() {
		repaint(); // Redraw the game screen to remove any pause overlay
	}

	/**
	 * Returns the width of the grid in blocks.
	 */
	public int getGridWidth() {
		return width;
	}

	/**
	 * Returns the height of the grid in blocks.
	 */
	public int getGridHeight() {
		return height;
	}

	/**
	 * Draws a rectangle by filling it with blocks of the specified color.
	 */
	public void rectangle(int x, int y, int width, int height, Color c) {
		for (int yy = y; yy < y + height; yy++) {
			for (int xx = x; xx < x + width; xx++) {
				block(xx, yy, c);
			}
		}
	}

	/**
	 * Waits for a key press and returns the pressed key.
	 */
	public char waitForKey() {
		try {
			synchronized (this) {
				wait(); // Wait for key press
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return lastKeyPressed;
	}

	/**
	 * Draws a single block at the specified position with the given color.
	 */
	public void block(int x, int y, Color color) {
		if (x >= 0 && x < width && y >= 0 && y < height) { // Ensure within bounds
			grid[x][y] = color;
			repaint(); // Trigger repaint to update the display
		}
	}

	/**
	 * Returns the color of the block at the specified position.
	 */
	public Color getBlockColor(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height) { // Ensure within bounds
			return grid[x][y];
		}
		return null; // Return null if out of bounds
	}

	/**
	 * Updates the score and refreshes the display.
	 */
	public void updateScore(int newScore) {
		this.score = newScore;
		repaint();
	}

	/**
	 * Updates the title of the game window.
	 */
	public void updateTitle(String title) {
		frame.setTitle(title);
	}

	/**
	 * Highlights a block with a glowing effect.
	 */
	public void highlightBlock(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height) {
			Color originalColor = grid[x][y];
			for (int i = 0; i < 3; i++) {
				block(x, y, Color.YELLOW); // Highlight with yellow
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				block(x, y, originalColor); // Restore original color
			}
		}
	}

	/**
	 * Displays a pause screen overlay with a custom message.
	 */
	public void showPauseScreen(String message) {
		JOptionPane.showMessageDialog(frame, message, "Paused", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Displays a power-up collection effect.
	 */
	public void showPowerUpEffect() {
		// Add logic for a visual effect (e.g., flashing colors)
	}

	/**
	 * Updates the timer display.
	 */
	public void updateTimer(long secondsRemaining) {
		this.timer = secondsRemaining;
		repaint();
	}

	/**
	 * Displays a game-over screen with the final score and restart option.
	 */
	public void showGameOverScreen(int finalScore) {
		int option = JOptionPane.showConfirmDialog(frame, "Game Over! Final Score: " + finalScore + "\nRestart?",
				"Game Over", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			System.exit(0); // Restart the game
		} else {
			System.exit(0); // Exit the game
		}
	}

	@Override
	protected void paintComponent(java.awt.Graphics g) {
		super.paintComponent(g);

		// Draw the game grid
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (grid[x][y] != null) {
					g.setColor(grid[x][y]);
					g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
				}
			}
		}

		// Draw the score panel
		g.setColor(ColorConstants.SCORE_PANEL);
		g.fillRect(0, height * blockSize, width * blockSize, 50);
		g.setColor(ColorConstants.SCORE_TEXT);
		g.drawString("Score: " + score, 10, height * blockSize + 30);

		// Draw the timer
		g.setColor(ColorConstants.TIMER_TEXT);
		g.drawString("Time Left: " + timer + "s", width * blockSize - 150, height * blockSize + 30);
	}
}
