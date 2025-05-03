public class Mole {
    private Graphics g = new Graphics(30, 50, 20); // Grid dimensions and block size

    public static void main(String[] args) {
        Mole m = new Mole();
        m.drawWorld();
        m.dig();
    }

    /**
     * Draws the initial world with soil and sky.
     */
    public void drawWorld() {
        g.rectangle(0, 5, g.getWidth(), g.getHeight() - 5, ColorConstants.SOIL); // Soil
        g.rectangle(0, 0, g.getWidth(), 5, ColorConstants.SKY); // Sky
    }

    /**
     * Allows the mole to dig tunnels based on user input.
     */
    public void dig() {
        int x = g.getWidth() / 2; // Start in the middle
        int y = g.getHeight() / 2;

        while (true) {
            g.block(x, y, ColorConstants.MOLE); // Draw the mole
            char key = g.waitForKey(); // Wait for user input
            g.block(x, y, ColorConstants.TUNNEL); // Leave a tunnel behind

            // Move the mole based on the key press
            if (key == 'w' && y > 0) { // Move up
                y -= 1;
            } else if (key == 'a' && x > 0) { // Move left
                x -= 1;
            } else if (key == 's' && y < g.getHeight() - 1) { // Move down
                y += 1;
            } else if (key == 'd' && x < g.getWidth() - 1) { // Move right
                x += 1;
            } else {
                System.out.println("Invalid move or out of bounds!");
            }
        }
    }
}
