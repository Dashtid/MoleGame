import javax.swing.*;
import java.awt.*;

/**
 * Handles rendering for the mole game using Swing and AWT.
 */
public class Graphics extends JPanel {
	private JFrame frame;
	private int width;
	private int blockSize;
	private int height;
	private Color[][] grid;
	private char lastKeyPressed;
	private int score;
	private long timer;

	public Graphics(int w, int h, int bs) {
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
				synchronized (Graphics.this) {
					Graphics.this.notify(); // Notify waiting thread
				}
			}
		});
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
		// Add glowing effect logic here (e.g., alternating colors)
	}

	/**
	 * Displays a pause screen overlay.
	 */
	public void showPauseScreen() {
		// Add logic to display a pause overlay
	}

	/**
	 * Hides the pause screen overlay.
	 */
	public void hidePauseScreen() {
		// Add logic to hide the pause overlay
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
	 * Displays a game-over screen with the final score.
	 */
	public void showGameOverScreen(int finalScore) {
		JOptionPane.showMessageDialog(frame, "Game Over! Final Score: " + finalScore, "Game Over",
				JOptionPane.INFORMATION_MESSAGE);
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
		g.drawString("Time Left: " + timer + "s", width * blockSize - 100, height * blockSize + 30);
	}
}
