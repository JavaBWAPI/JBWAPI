import bwapi.BWClient;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Player;

class MinimalListener extends DefaultBWListener {
	final BWClient bwClient;

	Game game;

	MinimalListener() {
		bwClient = new BWClient(this);
		bwClient.startGame();
	}

	public void onStart() {
		game = bwClient.getGame();
		Player self = game.self();
		Player enemy = game.enemy();
		System.out.println(self.getName() + " (" + self.getRace() + ") vs " + enemy.getName() + " (" + enemy.getRace() + ")");
	}

	public static void main(String[] args) {
		new MinimalListener();
	}
}
