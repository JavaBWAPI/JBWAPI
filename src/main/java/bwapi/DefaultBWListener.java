package bwapi;

public class DefaultBWListener implements BWListener {

    public void onStart() {}

    public void onEnd(boolean b) {}

    public void onFrame() {}

    public void onSendText(String s) {}

    public void onReceiveText(Player player, String s) {}

    public void onPlayerLeft(Player player) {}

    public void onNukeDetect(Position position) {}

    public void onUnitDiscover(Unit unit) {}

    public void onUnitEvade(Unit unit) {}

    public void onUnitShow(Unit unit) {}

    public void onUnitHide(Unit unit) {}

    public void onUnitCreate(Unit unit) {}

    public void onUnitDestroy(Unit unit) { }

    public void onUnitMorph(Unit unit) {}

    public void onUnitRenegade(Unit unit) {}

    public void onSaveGame(String s) {}

    public void onUnitComplete(Unit unit) {}

    public void onPlayerDropped(Player player) { }
}
