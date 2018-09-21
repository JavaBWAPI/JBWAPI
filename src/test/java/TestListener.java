import bwapi.DefaultBWListener;
import bwapi.BWClient;
import bwapi.Game;

public class TestListener extends DefaultBWListener {
	final BWClient bwClient;

	Game game;

	TestListener() {
		bwClient = new BWClient(this);
		bwClient.startGame();
	}

	public void onStart() {
		game = bwClient.getGame();
	}

	public void onFrame() {
		System.out.println("onFrame");
		System.out.println(bwClient.getGame().self().getName());

	}

	public static void main(String[] args) {
		new TestListener();
	}
}
