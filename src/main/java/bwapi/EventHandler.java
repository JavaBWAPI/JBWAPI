package bwapi;


class EventHandler implements Client.EventHandler {
    private final BWEventListener eventListener;
    private final Game game;
    private final Client.GameData data;
    private int frames;

    public EventHandler(final BWEventListener eventListener, final Client.GameData data) {
        this.eventListener = eventListener;
        game = new Game(data);
        this.data = data;
    }

    public void operation(final Client.GameData.Event event) {
        final Unit u;
        switch (event.type()) {
            case 0: //MatchStart
                frames = 0;
                game.init();
                game.setLatCom(false);
                eventListener.onStart();
                break;
            case 1: //MatchEnd
                eventListener.onEnd(event.v1() != 0);
                break;
            case 2: //MatchFrame
                game.updateUnitPositions(frames);
                eventListener.onFrame();
                frames += 1;
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
            case 16: //SaveGame
                eventListener.onSaveGame(data.eventString(event.v1()));
                break;
            case 8: //UnitDiscover
                game.unitCreate(event.v1());
                u = game.getUnit(event.v1());
                u.updatePosition(frames);
                u.updateType(frames);
                u.updatePlayer(frames);
                eventListener.onUnitDiscover(u);
                break;
            case 9: //UnitEvade
                u = game.getUnit(event.v1());
                u.updatePosition(frames);
                eventListener.onUnitEvade(u);
                break;
            case 10: // UnitShow
                game.unitShow(event.v1());
                u = game.getUnit(event.v1());
                u.updatePosition(frames);
                u.updateType(frames);
                u.updatePlayer(frames);
                eventListener.onUnitShow(u);
                break;
            case 11: //UnitHide
                game.unitHide(event.v1());
                u = game.getUnit(event.v1());
                eventListener.onUnitHide(u);
                break;
            case 12: //UnitCreate
                game.unitCreate(event.v1());
                u = game.getUnit(event.v1());
                u.updatePosition(frames);
                u.updateType(frames);
                u.updatePlayer(frames);
                eventListener.onUnitCreate(u);
                break;
            case 13: //UnitDestroy
                game.unitHide(event.v1());
                u = game.getUnit(event.v1());
                eventListener.onUnitDestroy(u);
                break;
            case 14: //UnitMorph
                u = game.getUnit(event.v1());
                u.updatePosition(frames);
                u.updateType(frames);
                eventListener.onUnitMorph(u);
                break;
            case 15: //UnitRenegade
                u = game.getUnit(event.v1());
                u.updatePlayer(frames);
                eventListener.onUnitRenegade(u);
                break;
            case 17: //UnitComplete
                game.unitCreate(event.v1());
                u = game.getUnit(event.v1());
                u.updatePlayer(frames);
                eventListener.onUnitComplete(u);
                break;
        }
    }

    public Game getGame() {
        return game;
    }
}