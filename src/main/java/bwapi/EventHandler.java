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
            case MatchStart:
                frames = 0;
                game.init();
                game.setLatCom(false);
                eventListener.onStart();
                break;
            case MatchEnd:
                eventListener.onEnd(event.getV1() != 0);
                break;
            case MatchFrame:
                game.onFrame(frames);
                eventListener.onFrame();
                frames += 1;
                break;
            //case 3: //MenuFrame
            case SendText:
                eventListener.onSendText(client.eventString(event.getV1()));
                break;
            case ReceiveText:
                eventListener.onReceiveText(game.getPlayer(event.getV1()), client.eventString(event.getV2()));
                break;
            case PlayerLeft:
                eventListener.onPlayerLeft(game.getPlayer(event.getV1()));
                break;
            case NukeDetect:
                eventListener.onNukeDetect(new Position(event.getV1(), event.getV2()));
                break;
            case SaveGame:
                eventListener.onSaveGame(client.eventString(event.getV1()));
                break;
            case UnitDiscover:
                game.unitCreate(event.getV1());
                u = game.getUnit(event.getV1());
                u.updatePosition(frames);
                u.updateType(frames);
                u.updatePlayer(frames);
                eventListener.onUnitDiscover(u);
                break;
            case UnitEvade:
                u = game.getUnit(event.getV1());
                u.updatePosition(frames);
                eventListener.onUnitEvade(u);
                break;
            case UnitShow:
                game.unitShow(event.getV1());
                u = game.getUnit(event.getV1());
                u.updatePosition(frames);
                u.updateType(frames);
                u.updatePlayer(frames);
                eventListener.onUnitShow(u);
                break;
            case UnitHide:
                game.unitHide(event.getV1());
                u = game.getUnit(event.getV1());
                eventListener.onUnitHide(u);
                break;
            case UnitCreate:
                game.unitCreate(event.getV1());
                u = game.getUnit(event.getV1());
                u.updatePosition(frames);
                u.updateType(frames);
                u.updatePlayer(frames);
                eventListener.onUnitCreate(u);
                break;
            case UnitDestroy:
                game.unitHide(event.getV1());
                u = game.getUnit(event.getV1());
                eventListener.onUnitDestroy(u);
                break;
            case UnitMorph:
                u = game.getUnit(event.getV1());
                u.updatePosition(frames);
                u.updateType(frames);
                eventListener.onUnitMorph(u);
                break;
            case UnitRenegade:
                u = game.getUnit(event.getV1());
                u.updatePlayer(frames);
                eventListener.onUnitRenegade(u);
                break;
            case UnitComplete:
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