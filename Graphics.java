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

    public Graphics(int w, int h, int bs) {
        this.width = w;
        this.blockSize = bs;
        this.height = h;
        this.grid = new Color[width][height];

        // Initialize the JFrame
        frame = new JFrame("Digging");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width * blockSize, height * blockSize);
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
            repaint();
        }
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y] != null) {
                    g.setColor(grid[x][y]);
                    g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
                }
            }
        }
    }
}
