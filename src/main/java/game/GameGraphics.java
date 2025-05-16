package game;

import javax.swing.*;
import java.awt.*;
import utils.InputHandler;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

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

		// Get the usable screen bounds (excluding taskbar)
		Rectangle usableBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds();

		// Set the frame size to fit within usable bounds
		int frameWidth = Math.min(width * blockSize, usableBounds.width);
		int frameHeight = Math.min(height * blockSize + 40, usableBounds.height);
		this.frame.setSize(frameWidth, frameHeight);

		// Maximize the frame (will respect usable bounds)
		this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

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
			grid[x][y] = color;
			// Do NOT call repaint() or paintImmediately() here for world generation
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

		// Draw grid and blocks
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (grid[x][y] == ColorConstants.MOLE) {
					g.setColor(ColorConstants.MOLE);
					g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
				} else if (grid[x][y] != null) {
					g.setColor(grid[x][y]);
					g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
				}
				// Draw gridlines only if not in the sky
				if (y >= Mole.SKY_HEIGHT) {
					g.setColor(Color.LIGHT_GRAY);
					g.drawRect(x * blockSize, y * blockSize, blockSize, blockSize);
				}
			}
		}

		// Draw clouds in the sky
		drawClouds(g);

		// Draw trees and rocks above ground
		drawTreesAndRocks(g);

		// Draw score and timer
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

	// Add this method to GameGraphics.java
	private void drawClouds(Graphics g) {
		g.setColor(new Color(255, 255, 255, 230));
		// Cloud bank 1
		g.fillOval(30, 10, 60, 30);
		g.fillOval(60, 5, 50, 25);
		g.fillOval(90, 15, 70, 35);
		g.fillOval(120, 8, 40, 20);
		g.fillOval(100, 25, 60, 25);
		// Cloud bank 2
		g.fillOval(220, 18, 80, 35);
		g.fillOval(250, 5, 60, 25);
		g.fillOval(270, 25, 70, 30);
		g.fillOval(300, 10, 50, 20);
		g.fillOval(320, 22, 60, 25);
		// Cloud bank 3
		g.fillOval(420, 12, 90, 40);
		g.fillOval(460, 5, 60, 25);
		g.fillOval(480, 25, 70, 30);
		g.fillOval(510, 15, 50, 20);
		g.fillOval(530, 28, 60, 25);
		// Small scattered clouds
		g.fillOval(180, 40, 40, 18);
		g.fillOval(380, 35, 35, 15);
		g.fillOval(600, 20, 50, 20);
		g.fillOval(700, 30, 60, 25);
	}

	private void drawTreesAndRocks(Graphics g) {
		int groundY = Mole.SKY_HEIGHT * blockSize;
		// Draw trees
		for (int i = 2; i < width; i += 7) {
			int x = i * blockSize + blockSize / 4;
			// Tree trunk
			g.setColor(new Color(101, 67, 33));
			g.fillRect(x + blockSize / 4, groundY - 18, blockSize / 4, 18);
			// Tree foliage
			g.setColor(new Color(34, 139, 34));
			g.fillOval(x, groundY - 32, blockSize, 20);
		}
		// Draw rocks (bottom of oval at groundY)
		g.setColor(new Color(120, 120, 120));
		for (int i = 5; i < width; i += 11) {
			int x = i * blockSize + blockSize / 4;
			int rockHeight = blockSize / 3;
			g.fillOval(x, groundY - rockHeight, blockSize / 2, rockHeight);
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