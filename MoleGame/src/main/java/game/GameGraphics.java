package game;

import javax.swing.*;
import java.awt.*;
import utils.InputHandler;

/**
 * Handles rendering for the mole game using Swing and AWT.
 */
public class GameGraphics extends JPanel {
	private JFrame frame;
	private int width;
	private int blockSize;
	private int height;
	private Color[][] grid;
	private int score;
	private long timer;
	private boolean isPaused = false;
	private JLabel timerLabel;
	private InputHandler inputHandler;

	public GameGraphics(int width, int height, int blockSize, InputHandler inputHandler) {
		this.width = width;
		this.height = height;
		this.blockSize = blockSize;
		this.inputHandler = inputHandler;
		this.frame = new JFrame("Mole Game");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setSize(width * blockSize + 100, height * blockSize + 100);
		this.frame.add(this);
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.addKeyListener(inputHandler); // Use the shared handler
		this.frame.setVisible(true);

		// ADD THIS LINE:
		this.grid = new Color[width][height];
	}

	/**
	 * Resizes the game grid to the specified width and height.
	 */
	public void resizeGrid(int newWidth, int newHeight) {
		this.width = newWidth;
		this.height = newHeight;
		this.grid = new Color[width][height]; // Create a new grid with the updated size
		this.setPreferredSize(new Dimension(width * blockSize, height * blockSize));
		frame.pack(); // Adjust the frame size
		repaint(); // Redraw the updated grid
	}

	/**
	 * Hides the pause screen overlay.
	 */
	public void hidePauseScreen() {
		isPaused = false;
		repaint();
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
	 * Waits for a key press and returns the pressed key asynchronously.
	 */
	public char waitForKey() {
		return inputHandler.waitForKeyPress();
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
			new Thread(() -> {
				try {
					for (int i = 0; i < 3; i++) {
						block(x, y, ColorConstants.GLOW_EFFECT); // Highlight with glow effect
						Thread.sleep(200);
						block(x, y, originalColor); // Restore original color
						Thread.sleep(200);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}).start();
		}
	}

	/**
	 * Displays a pause screen overlay with a custom message.
	 */
	public void showPauseScreen(String message) {
		isPaused = true;
		repaint();
		JOptionPane.showMessageDialog(frame, message, "Paused", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Displays a power-up collection effect.
	 */
	public void showPowerUpEffect() {
		JOptionPane.showMessageDialog(frame, "Power-Up Activated!", "Power-Up", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Updates the timer display.
	 */
	public void updateTimer(long secondsRemaining) {
		this.timer = secondsRemaining;
		timerLabel.setText("Time: " + secondsRemaining + "s");
		repaint();
	}

	/**
	 * Displays a game-over screen with the final score and restart option.
	 */
	public void showGameOverScreen(int finalScore) {
		int option = JOptionPane.showConfirmDialog(frame, "Game Over! Final Score: " + finalScore + "\nRestart?",
				"Game Over", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			synchronized (this) {
				notify(); // Notify the main thread to restart
			}
		} else {
			frame.dispose(); // Close the game window gracefully
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (grid[x][y] != null) {
					g.setColor(grid[x][y]);
					g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
				}
			}
		}
		// Draw score and timer
		g.setColor(Color.WHITE);
		g.drawString("Score: " + score, 10, height * blockSize + 20);
		g.setColor(ColorConstants.TIMER_TEXT);
		g.drawString("Time: " + timer + "s", 120, height * blockSize + 20);

		// Draw pause overlay if paused
		if (isPaused) {
			g.setColor(new Color(0, 0, 0, 128));
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.WHITE);
			g.setFont(g.getFont().deriveFont(Font.BOLD, 36f));
			g.drawString("PAUSED", getWidth() / 2 - 80, getHeight() / 2);
		}
	}

	/**
	 * Displays a message to the user in a dialog box.
	 */
	public void showMessage(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

	/**
	 * Prompts the user for input using a dialog box.
	 */
	public String showInputDialog(String message) {
		return JOptionPane.showInputDialog(frame, message);
	}

	/**
	 * Displays an error message to the user in a dialog box.
	 */
	public void showError(String message) {
		JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

}
