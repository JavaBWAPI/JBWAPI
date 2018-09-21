package bwapi;


import JavaBWAPIBackend.Client;
import bwapi.point.Position;

class EventHandler implements Client.EventHandler {
    private final BWEventListener eventListener;
    private final Client.GameData data;
    private Game game;

    public EventHandler(final BWEventListener eventListener, final Client.GameData data) {
        this.eventListener = eventListener;
        this.data = data;
    }

    public void operation(Client.GameData.Event event) {
        switch (event.type()) {
            case 0: //MatchStart
                //recreate a new game instance every onStart instead of a "clear" method in Game
                game = new Game(data);
                eventListener.onStart();
                break;
            case 1: //MatchEnd
                eventListener.onEnd(event.v1() != 0);
                break;
            case 2: //MatchFrame
                eventListener.onFrame();
                break;
            //case 3: //MenuFrame
            case 4: //SendText
                //TODO eventListener.onSendText();
                break;
            case 5: //ReceiveText
                //TODO eventListener.onReceiveText();
                break;
            case 6: //PlayerLeft
                eventListener.onPlayerLeft(game.getPlayer(event.v1()));
                //TODO remove the player from the Game?
                break;
            case 7: //NukeDetect
                eventListener.onNukeDetect(new Position(event.v1(), event.v2()));
                break;
            case 8: //UnitDiscover
                eventListener.onUnitDiscover(game.getUnit(event.v1()));
                break;
            case 9: //UnitEvade
                eventListener.onUnitEvade(game.getUnit(event.v1()));
                break;
            case 10: // UnitShow
                eventListener.onUnitShow(game.getUnit(event.v1()));
                break;
            case 11: //UnitHide
                eventListener.onUnitHide(game.getUnit(event.v1()));
                break;
            case 12: //UnitCreate
                eventListener.onUnitCreate(game.getUnit(event.v1()));
                break;
            case 13: //UnitDestroy
                eventListener.onUnitDestroy(game.getUnit(event.v1()));
                break;
            case 14: //UnitMorph
                eventListener.onUnitMorph(game.getUnit(event.v1()));
                break;
            case 15: //UnitRenegade
                eventListener.onUnitRenegade(game.getUnit((event.v1())));
                break;
            case 16: //SaveGame
                //TODO eventListener.onSaveGame();
                break;
            case 17: //UnitComplete
                eventListener.onUnitComplete(game.getUnit((event.v1())));
                break;
        }
    }

    public Game getGame() {
        return game;
    }
}