package bwapi;


import JavaBWAPIBackend.Client;
import bwapi.point.Position;

class EventHandler implements Client.EventHandler {
    private final BWListener eventListener;
    private Game game;
    private Client.GameData data;

    public EventHandler(final BWListener eventListener, final Client.GameData data) {
        this.eventListener = eventListener;
        game = new Game(data);
        this.data = data;
    }

    public void operation(Client.GameData.Event event) {
        switch (event.type()) {
            case 0: //MatchStart
                game.reset();
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
                eventListener.onSendText(data.eventString(event.v1()));
                break;
            case 5: //ReceiveText
                eventListener.onReceiveText(game.getPlayer(event.v1()), data.eventString(event.v2()));
                break;
            case 6: //PlayerLeft
                eventListener.onPlayerLeft(game.getPlayer(event.v1()));
                break;
            case 7: //NukeDetect
                eventListener.onNukeDetect(new Position(event.v1(), event.v2()));
                break;
            case 8: //UnitDiscover
                game.unitShow(event.v1());
                eventListener.onUnitDiscover(game.getUnit(event.v1()));
                break;
            case 9: //UnitEvade
                eventListener.onUnitEvade(game.getUnit(event.v1()));
                break;
            case 10: // UnitShow
                game.unitShow(event.v1());
                eventListener.onUnitShow(game.getUnit(event.v1()));
                break;
            case 11: //UnitHide
                game.unitHide(event.v1());
                eventListener.onUnitHide(game.getUnit(event.v1()));
                break;
            case 12: //UnitCreate
                game.unitShow(event.v1());
                eventListener.onUnitCreate(game.getUnit(event.v1()));
                break;
            case 13: //UnitDestroy
                game.unitHide(event.v1());
                eventListener.onUnitDestroy(game.getUnit(event.v1()));
                break;
            case 14: //UnitMorph
                eventListener.onUnitMorph(game.getUnit(event.v1()));
                break;
            case 15: //UnitRenegade
                eventListener.onUnitRenegade(game.getUnit((event.v1())));
                break;
            case 16: //SaveGame
                eventListener.onSaveGame(data.eventString(event.v1()));
                break;
            case 17: //UnitComplete
                game.unitShow(event.v1());
                eventListener.onUnitComplete(game.getUnit((event.v1())));
                break;
        }
    }

    public Game getGame() {
        return game;
    }
}