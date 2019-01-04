package bwapi;


class EventHandler implements Client.EventHandler {
    private final BWEventListener eventListener;
    private final Game game;
    private final Client client;

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
                game.init();
                game.setLatCom(false);
                eventListener.onStart();
                break;
            case MatchEnd:
                eventListener.onEnd(event.getV1() != 0);
                break;
            case MatchFrame:
                game.onFrame();
                eventListener.onFrame();
                break;
            //case MenuFrame:
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
                u.updateType();
                u.updatePlayer();
                eventListener.onUnitDiscover(u);
                break;
            case UnitEvade:
                u = game.getUnit(event.getV1());
                eventListener.onUnitEvade(u);
                break;
            case UnitShow:
                game.unitShow(event.getV1());
                u = game.getUnit(event.getV1());
                u.updateType();
                u.updatePlayer();
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
                u.updateType();
                u.updatePlayer();
                eventListener.onUnitCreate(u);
                break;
            case UnitDestroy:
                game.unitHide(event.getV1());
                u = game.getUnit(event.getV1());
                eventListener.onUnitDestroy(u);
                break;
            case UnitMorph:
                u = game.getUnit(event.getV1());
                u.updateType();
                eventListener.onUnitMorph(u);
                break;
            case UnitRenegade:
                u = game.getUnit(event.getV1());
                u.updatePlayer();
                eventListener.onUnitRenegade(u);
                break;
            case UnitComplete:
                game.unitCreate(event.getV1());
                u = game.getUnit(event.getV1());
                u.updatePlayer();
                eventListener.onUnitComplete(u);
                break;
        }
    }

    public Game getGame() {
        return game;
    }
}