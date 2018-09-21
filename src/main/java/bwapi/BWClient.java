package bwapi;

import JavaBWAPIBackend.Client;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

class EventHandler implements Client.EventHandler {
    private final BWEventListener eventListener;
    private Set<Integer> visibleUnits = new TreeSet<>();


    public EventHandler(BWEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /*
    MatchStart(0),
	MatchEnd(1),
	MatchFrame(2),
	MenuFrame(3),
	SendText(4),
	ReceiveText(5),
	PlayerLeft(6),
	NukeDetect(7),
	UnitDiscover(8),
	UnitEvade(9),
	UnitShow(10),
	UnitHide(11),
	UnitCreate(12),
	UnitDestroy(13),
	UnitMorph(14),
	UnitRenegade(15),
	SaveGame(16),
	UnitComplete(17);
     */
    public void operation(Client.GameData.Event event) {
        switch (event.type()) {
            case 0: //MatchStart
                eventListener.onStart();
                break;
            case 1: //MatchEnd
                eventListener.onEnd(event.v1() != 0);
                break;
            case 2: //MatchFrame

            case 10: // UnitShow
                visibleUnits.add(event.v1());

                //eventListener.onUnitShow();

            case 11: // UnitHide
                visibleUnits.add(event.v1());
                break;
        }
    }
}


public class BWClient {
    private BWEventListener eventListener;

    private Client client;
    private Game game;


    public BWClient(final BWEventListener eventListener) {
        Objects.requireNonNull(eventListener);
        this.eventListener = eventListener;
    }

    public Game getGame() {
        return game;
    }

    public void startGame() {
        while(client == null) {
            try {
                client = new Client();
            }
            catch (Throwable t) {
                System.err.println("Game table mapping not found.");
                try {
                    Thread.sleep(1000);
                }
                catch (Throwable ignored) { }            }
        }

        final EventHandler handler = new EventHandler(eventListener);

        try {
            while(!client.data().isInGame()) {
                System.out.println("Game started? " + client.data().isInGame());
                client.update(handler);
            }

            game = new Game(client.data());

            System.out.println("Game started!");

            while(client.data().isInGame()) {
                client.update(handler);
            }
        }
        catch (Throwable exception) {}
    }
}