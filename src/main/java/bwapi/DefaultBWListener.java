package bwapi;

/**
 * Convenience class that extends all methods in {@link BWEventListener}.
 * Not all of the methods need an implementation.
 */
public class DefaultBWListener implements BWEventListener {

    public void onStart() {
    }

    public void onEnd(final boolean isWinner) {
    }

    public void onFrame() {
    }

    public void onSendText(final String text) {
    }

    public void onReceiveText(final Player player, final String text) {
    }

    public void onPlayerLeft(final Player player) {
    }

    public void onNukeDetect(final Position position) {
    }

    public void onUnitDiscover(final Unit unit) {
    }

    public void onUnitEvade(final Unit unit) {
    }

    public void onUnitShow(final Unit unit) {
    }

    public void onUnitHide(final Unit unit) {
    }

    public void onUnitCreate(final Unit unit) {
    }

    public void onUnitDestroy(final Unit unit) {
    }

    public void onUnitMorph(final Unit unit) {
    }

    public void onUnitRenegade(final Unit unit) {
    }

    public void onSaveGame(final String text) {
    }

    public void onUnitComplete(final Unit unit) {
    }

    public void onPlayerDropped(final Player player) {
    }
}
