package bwapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.InflaterOutputStream;

public class GameBuilder {

    public static Game createGame() throws IOException {
        return createGame("(2)Benzene.scx");
    }

    public static Game createGame(String mapName) throws IOException {
        final WrappedBuffer buffer = binToBuffer("src/test/resources/" + mapName + "_frame0_buffer.bin");
        return createGame(new Client(buffer));
    }

    public static WrappedBuffer binToBuffer(String binLocation) throws IOException {
        final byte[] compressedBytes = Files.readAllBytes(Paths.get(binLocation));
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final InflaterOutputStream zin = new InflaterOutputStream(out);
        zin.write(compressedBytes);
        zin.flush();
        zin.close();
        final byte[] bytes = out.toByteArray();
        final WrappedBuffer buffer = new WrappedBuffer(bytes.length);
        buffer.getBuffer().put(bytes);
        return buffer;
    }

    public static Game createGame(Client client) throws IOException {
        final Game game = new Game(client);
        game.init();
        return game;
    }
}
