package bwapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.InflaterOutputStream;

public class GameBuilder {

    public static Game createGame() throws IOException {
        return createGame("(2)Benzene.scx");
    }

    public static Game createGame(String mapName) throws IOException {
        final String location = "src/test/resources/" + mapName + "_frame0_buffer.bin";

        // load bytebuffer
        final byte[] compressedBytes = Files.readAllBytes(Paths.get(location));
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final InflaterOutputStream zin = new InflaterOutputStream(out);
        zin.write(compressedBytes);
        zin.flush();
        zin.close();
        final byte[] bytes = out.toByteArray();
        final ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
        buffer.put(bytes);

        final Client client = new Client(buffer);
        final Game game = new Game(client);
        game.init();
        return game;
    }
}
