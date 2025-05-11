package MoleGame.src.test;

import org.junit.jupiter.api.Test;
import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

class ColorConstantsTest {

    @Test
    void testMoleColor() {
        assertEquals(new Color(51, 51, 0), ColorConstants.MOLE);
    }

    @Test
    void testSoilColor() {
        assertEquals(new Color(153, 102, 51), ColorConstants.SOIL);
    }

    @Test
    void testSkyColor() {
        assertEquals(new Color(135, 206, 235), ColorConstants.SKY);
    }

    @Test
    void testPauseOverlayColor() {
        assertEquals(new Color(0, 0, 0, 150), ColorConstants.PAUSE_OVERLAY);
    }
}