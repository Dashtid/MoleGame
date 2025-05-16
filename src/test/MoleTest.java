package MoleGame.src.test;

import MoleGame.src.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MoleTest {
    private Mole mole;
    private GameGraphics mockGraphics;

    @BeforeEach
    void setUp() {
        mockGraphics = mock(GameGraphics.class);
        mole = new Mole();
        mole.g = mockGraphics; // Inject the mock GameGraphics
    }

    @Test
    void testConstructorInitializesGameGraphics() {
        Mole newMole = new Mole();
        assertNotNull(newMole.g, "GameGraphics should be initialized in the constructor.");
    }

    @Test
    void testDrawWorld() {
        mole.drawWorld(1);

        // Verify soil and sky rectangles are drawn
        verify(mockGraphics).rectangle(0, SKY_HEIGHT, mockGraphics.getGridWidth(),
                mockGraphics.getGridHeight() - SKY_HEIGHT, ColorConstants.SOIL);
        verify(mockGraphics).rectangle(0, 0, mockGraphics.getGridWidth(), SKY_HEIGHT, ColorConstants.SKY);

        // Verify obstacles and power-ups are placed
        verify(mockGraphics, atLeast(50)).block(anyInt(), anyInt(), eq(ColorConstants.OBSTACLE));
        verify(mockGraphics, atLeast(5)).block(anyInt(), anyInt(), eq(ColorConstants.POWER_UP));

        // Verify goal is placed
        verify(mockGraphics, atLeastOnce()).block(anyInt(), anyInt(), eq(ColorConstants.GOAL));
    }

    @Test
    void testStartGameProgression() {
        when(mockGraphics.showInputDialog(anyString())).thenReturn("y"); // Simulate "yes" to play again
        when(mockGraphics.getGridWidth()).thenReturn(10);
        when(mockGraphics.getGridHeight()).thenReturn(10);
        when(mockGraphics.getBlockColor(anyInt(), anyInt())).thenReturn(ColorConstants.GOAL);

        mole.startGame();

        // Verify that the game progresses through levels
        verify(mockGraphics, atLeastOnce()).showMessage(contains("Starting Level"));
        verify(mockGraphics, atLeastOnce()).resizeGrid(anyInt(), anyInt());
        verify(mockGraphics, atLeastOnce()).showMessage(contains("Level 1 completed!"));
    }

    @Test
    void testStartGameGameOver() {
        when(mockGraphics.showInputDialog(anyString())).thenReturn("n"); // Simulate "no" to stop playing
        when(mockGraphics.getGridWidth()).thenReturn(10);
        when(mockGraphics.getGridHeight()).thenReturn(10);
        when(mockGraphics.getBlockColor(anyInt(), anyInt())).thenReturn(null);

        mole.startGame();

        // Verify that the game ends with a game-over screen
        verify(mockGraphics).showGameOverScreen(anyInt());
    }

    @Test
    void testDigGoalReached() {
        when(mockGraphics.getGridWidth()).thenReturn(10);
        when(mockGraphics.getGridHeight()).thenReturn(10);
        when(mockGraphics.getBlockColor(anyInt(), anyInt())).thenReturn(ColorConstants.GOAL);

        boolean result = mole.dig(1);
        assertTrue(result);
        verify(mockGraphics).showMessage(contains("You win!"));
    }

    @Test
    void testDigTimeUp() {
        when(mockGraphics.getGridWidth()).thenReturn(10);
        when(mockGraphics.getGridHeight()).thenReturn(10);
        when(mockGraphics.getBlockColor(anyInt(), anyInt())).thenReturn(null);

        boolean result = mole.dig(1);
        assertFalse(result);
        verify(mockGraphics).showMessage(contains("Time's up!"));
    }
}