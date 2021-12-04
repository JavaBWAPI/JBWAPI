import bwapi.BWClient;
import bwapi.DefaultBWListener;

public class SittingDuck extends DefaultBWListener {
    public static void main(String[] args) {
        new BWClient(new SittingDuck()).startGame();
    }
}
