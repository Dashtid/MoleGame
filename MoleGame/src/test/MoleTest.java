package MoleGame.src.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void testDrawWorld() {
        mole.drawWorld(1);
        verify(mockGraphics, atLeastOnce()).rectangle(anyInt(), anyInt(), anyInt(), anyInt(), eq(ColorConstants.SOIL));
        verify(mockGraphics, atLeastOnce()).rectangle(anyInt(), anyInt(), anyInt(), anyInt(), eq(ColorConstants.SKY));
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