package game;

import bwapi.*;

class DrawTest extends DefaultBWListener {
    final BWClient bwClient;

    Game game;

    DrawTest() {
        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    public void onStart() {
        game = bwClient.getGame();
    }

    @Override
    public void onFrame() {
        game.drawTextScreen(50, 50, "frame: " + game.getFrameCount());


        game.drawCircleScreen(200, 110, 80, Color.Purple, true);
        game.drawCircleScreen(350, 110, 80, Color.Purple, true);

        game.drawTriangleScreen(143, 167,407, 167, 275, 350, Color.Purple, true);

        game.drawBoxScreen(200, 110, 350, 210, Color.Purple, true);
        game.drawTextScreen(50, 50, "%cHello %cWorld!", Text.Red, Text.Green);

        Player self = game.self();
        Player enemy = game.enemy();
        String out = "%c"+ self.getName() + " (" + self.getRace() + ")%c vs %c" + enemy.getName() + " (" + enemy.getRace() + ")";
        game.drawTextScreen(20, 20, out, self.getTextColor(), Text.Default, enemy.getTextColor());
    }

    public static void main(String[] args) {
        new DrawTest();
    }
}
