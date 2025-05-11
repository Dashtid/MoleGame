package MoleGame.src.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

class GameGraphicsTest {
    private GameGraphics gameGraphics;

    @BeforeEach
    void setUp() {
        gameGraphics = new GameGraphics(10, 10, 20); // 10x10 grid with block size 20
    }

    @Test
    void testBlockAndGetBlockColor() {
        gameGraphics.block(5, 5, Color.RED);
        assertEquals(Color.RED, gameGraphics.getBlockColor(5, 5));
    }

    @Test
    void testGetBlockColorOutOfBounds() {
        assertNull(gameGraphics.getBlockColor(-1, -1));
        assertNull(gameGraphics.getBlockColor(100, 100));
    }

    @Test
    void testResizeGrid() {
        gameGraphics.resizeGrid(20, 20);
        assertEquals(20, gameGraphics.getGridWidth());
        assertEquals(20, gameGraphics.getGridHeight());
    }

}

public void resizeGrid(int newWidth, int newHeight) {
    this.width = newWidth;
    this.height = newHeight;
    this.grid = new Color[width][height]; // Create a new grid with the updated size
    this.setPreferredSize(new Dimension(width * blockSize, height * blockSize));
    frame.pack(); // Adjust the frame size
    repaint(); // Redraw the updated grid
}