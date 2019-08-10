package bwapi;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.ByteBuffer;
import java.util.SplittableRandom;
import java.util.stream.Collectors;

@Warmup(iterations = 3, time = 5)
@Measurement(iterations = 3, time = 5)
@Fork(3)
public class ClientDataBenchmark {
    @State(Scope.Thread)
    public static class EmptyState {
        Client client;
        Game game;
        String[] strings;

        @Setup(Level.Invocation)
        public void setup() {
            client = new Client(ByteBuffer.allocateDirect(ClientData.GameData.SIZE));
            game = new Game(client);
            strings = buildStrings();
        }

    }

    @State(Scope.Thread)
    public static class FilledWithStrings {
        Client client;
        ClientData.GameData data;
        Game game;

        @Setup(Level.Invocation)
        public void setup() {
            client = new Client(ByteBuffer.allocateDirect(ClientData.GameData.SIZE));
            data = client.data();
            game = new Game(client);
            String[] strings = buildStrings();
            for (String s : strings) {
                client.addString(s);
            }
        }
    }

    private static String[] buildStrings() {
        SplittableRandom rnd = new SplittableRandom(987654321L);
        String[] strings = new String[Client.MAX_COUNT];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = rnd.ints(1022, 0, 9)
                    .mapToObj(Integer::toString)
                    .collect(Collectors.joining());
        }
        return strings;
    }

    @Benchmark
    @OperationsPerInvocation(Client.MAX_COUNT)
    public int addUnitCommand(EmptyState s) {
        for (int i = 0; i < Client.MAX_COUNT; i++) {
            s.game.addUnitCommand(0, 1, 2, 3, 4, 5);
        }
        return s.client.data().getCommandCount();
    }

    @Benchmark
    @OperationsPerInvocation(Client.MAX_COUNT)
    public int addString(EmptyState s) {
        for (int i = 0; i < Client.MAX_COUNT; i++) {
            s.client.addString(s.strings[i]);
        }
        return s.client.data().getStringCount();
    }

    @Benchmark
    @OperationsPerInvocation(Client.MAX_COUNT)
    public void getString(FilledWithStrings s, Blackhole blackhole) {
        for (int i = 0; i < Client.MAX_COUNT; i++) {
            blackhole.consume(s.data.getStrings(i));
        }
    }
}
