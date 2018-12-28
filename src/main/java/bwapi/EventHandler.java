package bwapi;


class EventHandler implements Client.EventHandler {
    private final BWEventListener eventListener;
    private final Game game;
    private final Client client;
    private int frames;

    public EventHandler(final BWEventListener eventListener, final Client client) {
        this.eventListener = eventListener;
        game = new Game(client);
        this.client = client;
    }

    @Override
    public void operation(final ClientData.Event event) {
        final Unit u;
        switch (event.getType()) {
            case MatchStart: //MatchStart
                frames = 0;
                game.init();
                game.setLatCom(false);
                eventListener.onStart();
                break;
            case MatchEnd: //MatchEnd
                eventListener.onEnd(event.getV1() != 0);
                break;
            case MatchFrame: //MatchFrame
                game.updateUnitPositions(frames);
                eventListener.onFrame();
                frames += 1;
                break;
            //case 3: //MenuFrame
            case SendText: //SendText
                eventListener.onSendText(client.eventString(event.getV1()));
                break;
            case ReceiveText: //ReceiveText
                eventListener.onReceiveText(game.getPlayer(event.getV1()), client.eventString(event.getV2()));
                break;
            case PlayerLeft: //PlayerLeft
                eventListener.onPlayerLeft(game.getPlayer(event.getV1()));
                break;
            case NukeDetect: //NukeDetect
                eventListener.onNukeDetect(new Position(event.getV1(), event.getV2()));
                break;
            case SaveGame: //SaveGame
                eventListener.onSaveGame(client.eventString(event.getV1()));
                break;
            case UnitDiscover: //UnitDiscover
                game.unitCreate(event.getV1());
                u = game.getUnit(event.getV1());
                u.updatePosition(frames);
                u.updateType(frames);
                u.updatePlayer(frames);
                eventListener.onUnitDiscover(u);
                break;
            case UnitEvade: //UnitEvade
                u = game.getUnit(event.getV1());
                u.updatePosition(frames);
                eventListener.onUnitEvade(u);
                break;
            case UnitShow: // UnitShow
                game.unitShow(event.getV1());
                u = game.getUnit(event.getV1());
                u.updatePosition(frames);
                u.updateType(frames);
                u.updatePlayer(frames);
                eventListener.onUnitShow(u);
                break;
            case UnitHide: //UnitHide
                game.unitHide(event.getV1());
                u = game.getUnit(event.getV1());
                eventListener.onUnitHide(u);
                break;
            case UnitCreate: //UnitCreate
                game.unitCreate(event.getV1());
                u = game.getUnit(event.getV1());
                u.updatePosition(frames);
                u.updateType(frames);
                u.updatePlayer(frames);
                eventListener.onUnitCreate(u);
                break;
            case UnitDestroy: //UnitDestroy
                game.unitHide(event.getV1());
                u = game.getUnit(event.getV1());
                eventListener.onUnitDestroy(u);
                break;
            case UnitMorph: //UnitMorph
                u = game.getUnit(event.getV1());
                u.updatePosition(frames);
                u.updateType(frames);
                eventListener.onUnitMorph(u);
                break;
            case UnitRenegade: //UnitRenegade
                u = game.getUnit(event.getV1());
                u.updatePlayer(frames);
                eventListener.onUnitRenegade(u);
                break;
            case UnitComplete: //UnitComplete
                game.unitCreate(event.getV1());
                u = game.getUnit(event.getV1());
                u.updatePlayer(frames);
                eventListener.onUnitComplete(u);
                break;
        }
    }

    public Game getGame() {
        return game;
    }
}