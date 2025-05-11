package game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Handles user input for the MoleGame.
 */
public class InputHandler extends KeyAdapter {
    private char lastKeyPressed;
    private final Object keyLock = new Object();

    /**
     * Called when a key is pressed. Stores the key and notifies waiting threads.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        synchronized (keyLock) {
            lastKeyPressed = e.getKeyChar();
            keyLock.notifyAll(); // Notify any thread waiting for input
        }
    }

    /**
     * Waits for a key press and returns the pressed key.
     *
     * @return The character of the key pressed.
     */
    public char waitForKeyPress() {
        synchronized (keyLock) {
            try {
                keyLock.wait(); // Wait for a key press
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return lastKeyPressed;
        }
    }
}
