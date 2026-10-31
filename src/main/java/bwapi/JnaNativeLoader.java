package bwapi;

import com.sun.jna.Native;
import com.sun.jna.Platform;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardOpenOption.READ;

/**
 * Loads a matching Windows JNA DLL beside the bot's JAR before connecting to BWAPI.
 * Shipping the DLL separately avoids extracting it to a temporary file on each launch.
 * The file must match the JAR's embedded library for the running JVM's architecture.
 *
 * <p>Missing, stale, or unusable DLLs leave JNA's ordinary loading path available.
 * Other platforms also use ordinary JNA loading. Explicit JNA loading settings
 * and an already initialized JNA take precedence.
 */
final class JnaNativeLoader {
    private static final String BOOT_PATH = "jna.boot.library.path";
    private static final String BOOT_NAME = "jna.boot.library.name";

    private JnaNativeLoader() {
    }

    /**
     * Call before constructing a platform connector or otherwise initializing JNA.
     */
    static synchronized void initialize() {
        String path;
        String bootName;
        try {
            if (!Platform.isWindows() || isConfigured()) {
                return;
            }
            byte[] binary = readEmbeddedLibrary();
            if (binary == null) {
                return;
            }
            Path library = bundledLibrary(binary);
            if (library == null || isConfigured()) {
                return;
            }
            path = library.getParent().toAbsolutePath().toString();
            bootName = "jnidispatch-" + Platform.RESOURCE_PREFIX;
        } catch (IOException | RuntimeException unavailable) {
            // An optional optimization must not prevent JNA's ordinary loading path.
            return;
        }
        loadPreparedLibrary(path, bootName);
    }

    private static byte[] readEmbeddedLibrary() throws IOException {
        String resource = "/com/sun/jna/" + Platform.RESOURCE_PREFIX + "/jnidispatch.dll";
        // A class literal does not initialize Native; use its loader's exact binary.
        try (InputStream input = Native.class.getResourceAsStream(resource);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            if (input == null) {
                return null;
            }
            byte[] buffer = new byte[32768];
            int size;
            while ((size = input.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            return output.toByteArray();
        }
    }

    private static void loadPreparedLibrary(String path, String bootName) {
        try {
            try {
                System.setProperty(BOOT_PATH, path);
                System.setProperty(BOOT_NAME, bootName);
            } catch (RuntimeException unavailable) {
                return;
            }
            // JNA itself falls back to ordinary extraction if this binary cannot be loaded.
            Class.forName(Native.class.getName(), true, Native.class.getClassLoader());
        } catch (ClassNotFoundException impossible) {
            throw new IllegalStateException("Cannot initialize JNA: its class loader could not find "
                    + "com.sun.jna.Native, which is required to load the native library. Ensure JNA is included "
                    + "in the bot's runtime classpath and is visible to its class loader.", impossible);
        } finally {
            // Do not leave a process-wide path pointing at this class loader's JNA version.
            if (path.equals(System.getProperty(BOOT_PATH))) {
                System.clearProperty(BOOT_PATH);
            }
            if (bootName.equals(System.getProperty(BOOT_NAME))) {
                System.clearProperty(BOOT_NAME);
            }
        }
    }

    private static boolean isConfigured() {
        if (Boolean.getBoolean("jna.loaded")) {
            return true;
        }
        for (String setting : new String[] {BOOT_PATH, BOOT_NAME,
                "jna.noclasspath", "jna.nosys", "jna.prefix", "jnidispatch.preserve"}) {
            if (System.getProperty(setting) != null) {
                return true;
            }
        }
        return false;
    }

    private static Path bundledLibrary(byte[] binary) {
        try {
            // Resolve beside the JAR (or wrapped EXE), never relative to a launcher's working directory.
            Path artifact = Paths.get(JnaNativeLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path library = artifact.resolveSibling("jnidispatch-" + Platform.RESOURCE_PREFIX + ".dll");
            // A replaced JAR must not accidentally load a DLL left over from another JNA version.
            return matches(library, binary) ? library : null;
        } catch (IOException | URISyntaxException | RuntimeException unavailable) {
            return null;
        }
    }

    private static boolean matches(Path file, byte[] expected) throws IOException {
        if (!Files.exists(file, NOFOLLOW_LINKS)) {
            return false;
        }
        try (SeekableByteChannel channel = Files.newByteChannel(file, READ, NOFOLLOW_LINKS)) {
            if (channel.size() != expected.length) {
                return false;
            }
            ByteBuffer bytes = ByteBuffer.allocate(expected.length);
            while (bytes.hasRemaining()) {
                if (channel.read(bytes) < 0) {
                    return false;
                }
            }
            return Arrays.equals(expected, bytes.array());
        }
    }
}
