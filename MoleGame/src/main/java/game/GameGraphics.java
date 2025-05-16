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

		// Initialize grid
		this.grid = new Color[width][height];

		// Set layout
		setLayout(new BorderLayout());

		// Initialize timer label
		this.timerLabel = new JLabel("Time: 0s");
		add(timerLabel, BorderLayout.NORTH);

		// Set panel properties
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(width * blockSize, height * blockSize + 40));
		setFocusable(true);
		addKeyListener(inputHandler);

		// Initialize frame
		this.frame = new JFrame("Mole Game");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.add(this);
		this.frame.pack();
		this.frame.setLocationRelativeTo(null);
		this.frame.setVisible(true);

		// Request focus
		requestFocusInWindow();

		// Initial repaint
		repaint();
	}

	public void resizeGrid(int newWidth, int newHeight) {
		this.width = newWidth;
		this.height = newHeight;
		this.grid = new Color[width][height];
		this.setPreferredSize(new Dimension(width * blockSize, height * blockSize + 40));
		frame.pack();
		repaint();
	}

	public void hidePauseScreen() {
		isPaused = false;
		repaint();
	}

	public int getGridWidth() {
		return width;
	}

	public int getGridHeight() {
		return height;
	}

	public void rectangle(int x, int y, int width, int height, Color c) {
		System.out.println("Drawing rectangle at (" + x + "," + y + ") with dimensions " + width + "x" + height);
		for (int yy = y; yy < y + height; yy++) {
			for (int xx = x; xx < x + width; xx++) {
				block(xx, yy, c);
			}
		}
	}

	public char waitForKey() {
		return inputHandler.waitForKeyPress();
	}

	public void block(int x, int y, Color color) {
		if (x >= 0 && x < width && y >= 0 && y < height) {
			System.out.println("Drawing block at (" + x + "," + y + ") with color " + color);
			grid[x][y] = color;

			// Calculate pixel coordinates
			int pixelX = x * blockSize;
			int pixelY = y * blockSize;

			// Request a repaint of just this block's area
			repaint(pixelX, pixelY, blockSize, blockSize);

			// Force immediate repaint
			paintImmediately(pixelX, pixelY, blockSize, blockSize);
		} else {
			System.out.println("Attempted to draw block outside bounds at (" + x + "," + y + ")");
		}
	}

	public Color getBlockColor(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height) {
			return grid[x][y];
		}
		return null;
	}

	public void updateScore(int newScore) {
		this.score = newScore;
		repaint();
	}

	public void updateTimer(long secondsRemaining) {
		this.timer = secondsRemaining;
		timerLabel.setText("Time: " + secondsRemaining + "s");
		repaint();
	}

	public void updateTitle(String title) {
		frame.setTitle(title);
	}

	public void highlightBlock(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height) {
			Color originalColor = grid[x][y];
			new Thread(() -> {
				try {
					for (int i = 0; i < 3; i++) {
						block(x, y, ColorConstants.GLOW_EFFECT);
						Thread.sleep(200);
						block(x, y, originalColor);
						Thread.sleep(200);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}).start();
		}
	}

	public void showPauseScreen(String message) {
		isPaused = true;
		repaint();
		JOptionPane.showMessageDialog(frame, message, "Paused", JOptionPane.INFORMATION_MESSAGE);
	}

	public void showPowerUpEffect() {
		JOptionPane.showMessageDialog(frame, "Power-Up Activated!", "Power-Up", JOptionPane.INFORMATION_MESSAGE);
	}

	public void showGameOverScreen(int finalScore) {
		int option = JOptionPane.showConfirmDialog(frame,
				"Game Over! Final Score: " + finalScore + "\nRestart?",
				"Game Over",
				JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			synchronized (this) {
				notify();
			}
		} else {
			frame.dispose();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Fill background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		// Draw grid
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				// Draw cell background
				g.setColor(Color.LIGHT_GRAY);
				g.drawRect(x * blockSize, y * blockSize, blockSize, blockSize);

				// Draw block if it exists
				if (grid[x][y] != null) {
					g.setColor(grid[x][y]);
					g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);

					// Draw border around block
					g.setColor(Color.BLACK);
					g.drawRect(x * blockSize, y * blockSize, blockSize, blockSize);
				}
			}
		}

		// Draw score and timer with better visibility
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.drawString("Score: " + score, 10, height * blockSize + 20);
		g.drawString("Time: " + timer + "s", 120, height * blockSize + 20);

		if (isPaused) {
			g.setColor(new Color(0, 0, 0, 128));
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.WHITE);
			g.setFont(g.getFont().deriveFont(Font.BOLD, 36f));
			g.drawString("PAUSED", getWidth() / 2 - 80, getHeight() / 2);
		}
	}

	public void showMessage(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

	public String showInputDialog(String message) {
		return JOptionPane.showInputDialog(frame, message);
	}

	public void showError(String message) {
		JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
}