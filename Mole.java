public class Mole {
	private Graphics g = new Graphics(30, 50, 20);

	public static void main(String[] args) {
		Mole m = new Mole();
		m.drawWorld();
		m.dig();
	}

	public void drawWorld() {
		g.rectangle(0, 5, g.getWidth(), g.getHeight(), Coler.SOIL);
		g.rectangle(0, 0, g.getWidth(), 5, Coler.SKY);
	}

	public void dig() {
		int x = g.getWidth() / 2; // För att börja på mitten
		int y = g.getHeight() / 2;
		while (true) {
			g.block(x, y, Coler.MOLE);
			char key = g.waitForKey();
			g.block(x, y, Coler.TUNNEL);
			if (key == 'w') {
				y = y - 1;
			} else if (key == 'a') {
				x = x - 1;
			} else if (key == 's') {
				y = y + 1;

			} else if (key == 'd') {
				x = x + 1;

			}
		}
	}
}
