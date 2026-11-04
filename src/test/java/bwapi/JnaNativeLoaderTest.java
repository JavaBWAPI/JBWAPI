package bwapi;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * Verifies bundled Windows DLL selection and ordinary JNA fallback without a BWAPI server.
 * Each scenario loads the real native library in a fresh JVM because native initialization
 * cannot be reset within one JVM. Windows-specific cases are skipped on other platforms.
 */
public class JnaNativeLoaderTest {
    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    @Test
    public void jarOnlyDeploymentUsesOrdinaryJnaLoading() throws Exception {
        Path base = temporary.newFolder().toPath();
        String result = finish(start(base));
        assertExtractedLibrary(result, base);
        assertEquals("unset", value(result, "boot"));
        assertEquals("unset", value(result, "bootName"));
    }

    @Test
    public void respectsExplicitLoadingSettingsAndAlreadyLoadedJna() throws Exception {
        Path distribution = temporary.newFolder().toPath();
        String classpath = bundledClasspath(distribution);
        Path base = temporary.newFolder().toPath();
        String explicit = base.resolve("explicit").toString();
        String result = finish(startOnClasspath(base, classpath, "-Djna.boot.library.path=" + explicit));
        assertEquals(explicit, value(result, "boot"));
        assertExtractedLibrary(result, base);
        result = finish(startOnClasspath(base, classpath, "-Djna.boot.library.name=custom-name"));
        assertEquals("custom-name", value(result, "bootName"));
        assertExtractedLibrary(result, base);
        result = finish(startOnClasspath(base, classpath, "-Dprobe.preload=true"));
        assertExtractedLibrary(result, base);
    }

    @Test
    public void honorsNoUnpackWhenNoBundledLibraryIsAvailable() throws Exception {
        Path base = temporary.newFolder().toPath();
        Process child = start(base, "-Djna.nounpack=true");
        String output = output(child);
        assertNotEquals(output, 0, child.exitValue());
        assertTrue(output, output.contains("UnsatisfiedLinkError"));
    }

    @Test
    public void bundledWindowsLibraryIsSelectedWithoutFlagsFromAnotherWorkingDirectory() throws Exception {
        assumeTrue(Platform.isWindows());
        Path distribution = temporary.newFolder("bot with spaces #").toPath();
        String classpath = bundledClasspath(distribution);
        Path base = temporary.newFolder("unrelated working directory").toPath();
        String result = finish(startOnClasspath(base, classpath));
        Path expected = distribution.resolve("jnidispatch-" + Platform.RESOURCE_PREFIX + ".dll");
        assertTrue(result, Files.isSameFile(expected, Paths.get(value(result, "native"))));
        assertEquals("unset", value(result, "boot"));
        assertEquals("unset", value(result, "bootName"));
        // Disabling extraction still permits loading the existing bundled DLL.
        result = finish(startOnClasspath(base, classpath, "-Djna.nounpack=true"));
        assertTrue(result, Files.isSameFile(expected, Paths.get(value(result, "native"))));
    }

    @Test
    public void staleOrMissingBundledWindowsLibraryUsesOrdinaryJnaLoading() throws Exception {
        assumeTrue(Platform.isWindows());
        Path distribution = temporary.newFolder().toPath();
        String classpath = bundledClasspath(distribution);
        Path base = temporary.newFolder().toPath();
        Path library = distribution.resolve("jnidispatch-" + Platform.RESOURCE_PREFIX + ".dll");
        byte[] stale = Files.readAllBytes(library);
        stale[stale.length - 1] ^= 1;
        Files.write(library, stale);
        String result = finish(startOnClasspath(base, classpath));
        assertExtractedLibrary(result, base);
        assertArrayEquals(stale, Files.readAllBytes(library));
        Files.delete(library);
        assertExtractedLibrary(finish(startOnClasspath(base, classpath)), base);
    }

    @Test
    public void bundledWindowsLibrariesAreIgnoredOnOtherPlatforms() throws Exception {
        assumeTrue(!Platform.isWindows());
        Path distribution = temporary.newFolder().toPath();
        String classpath = bundledClasspath(distribution);
        Path base = temporary.newFolder().toPath();
        assertExtractedLibrary(finish(startOnClasspath(base, classpath)), base);
    }

    private void assertExtractedLibrary(String output, Path directory) throws Exception {
        Path library = Paths.get(value(output, "native"));
        assertTrue(output, Files.isSameFile(directory, library.getParent()));
    }

    /**
     * Builds a minimal distribution fixture. Loading the helper from bot.jar gives
     * it a real artifact location for finding adjacent DLLs; the rest of the classes
     * remain on the test classpath. All Windows architectures are included together.
     */
    private String bundledClasspath(Path distribution) throws Exception {
        Path jar = distribution.resolve("bot.jar");
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar));
             InputStream input = JnaNativeLoader.class.getResourceAsStream("JnaNativeLoader.class")) {
            output.putNextEntry(new JarEntry("bwapi/JnaNativeLoader.class"));
            byte[] bytes = new byte[4096];
            int size;
            while ((size = input.read(bytes)) != -1) {
                output.write(bytes, 0, size);
            }
            output.closeEntry();
        }
        for (String arch : new String[] {"win32-x86", "win32-x86-64", "win32-aarch64"}) {
            String resource = "com/sun/jna/" + arch + "/jnidispatch.dll";
            Path library = distribution.resolve("jnidispatch-" + arch + ".dll");
            Files.createDirectories(library.getParent());
            try (InputStream input = Native.class.getResourceAsStream("/" + resource)) {
                Files.copy(input, library);
            }
        }
        return jar + System.getProperty("path.separator") + testClasspath();
    }

    private String testClasspath() {
        return System.getProperty("surefire.test.class.path", System.getProperty("java.class.path"));
    }

    private Process start(Path base, String... options) throws Exception {
        return startOnClasspath(base, testClasspath(), options);
    }

    // Use the test JVM's runtime and an isolated working/temp directory for each scenario.
    private Process startOnClasspath(Path base, String classpath, String... options) throws Exception {
        String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        List<String> command = new ArrayList<>(Arrays.asList(
                Paths.get(System.getProperty("java.home"), "bin", executable).toString(),
                "-Xmx128m", "-XX:-UsePerfData", "-Djava.io.tmpdir=" + base, "-Djna.tmpdir=" + base));
        command.addAll(Arrays.asList(options));
        command.addAll(Arrays.asList("-cp", classpath, NativeLoadingProbe.class.getName()));
        ProcessBuilder builder = new ProcessBuilder(command).directory(base.toFile()).redirectErrorStream(true);
        for (String name : new String[] {"TEMP", "TMP", "TMPDIR"}) {
            builder.environment().put(name, base.toString());
        }
        return builder.start();
    }

    private String finish(Process child) throws Exception {
        String output = output(child);
        assertEquals(output, 0, child.exitValue());
        assertTrue(output, output.contains("pointer=4") || output.contains("pointer=8"));
        return output;
    }

    private String output(Process child) throws Exception {
        try {
            assertTrue("Native initialization timed out", child.waitFor(30, TimeUnit.SECONDS));
            try (InputStream input = child.getInputStream(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                byte[] bytes = new byte[4096];
                int count;
                while ((count = input.read(bytes)) != -1) {
                    output.write(bytes, 0, count);
                }
                return output.toString("UTF-8");
            }
        } finally {
            child.destroyForcibly();
        }
    }

    private String value(String output, String key) {
        for (String line : output.split("\\R")) {
            if (line.startsWith(key + "=")) {
                return line.substring(key.length() + 1);
            }
        }
        throw new AssertionError("Expected the native-loading probe to report '" + key
                + "=<value>', but that field was missing. Check the child-JVM output for initialization errors:\n"
                + output);
    }

    /**
     * Child-JVM entry point: constructs a client without connecting to a game, then
     * reports the loaded library and boot-path setting for the parent's assertions.
     */
    public static class NativeLoadingProbe {
        public static void main(String[] args) {
            if (Boolean.getBoolean("probe.preload")) {
                System.out.println("preload=" + Native.POINTER_SIZE);
            }
            new Client(new BWClient(new DefaultBWListener()));
            System.out.println("pointer=" + Native.POINTER_SIZE);
            System.out.println("native=" + System.getProperty("jnidispatch.path"));
            System.out.println("boot=" + System.getProperty("jna.boot.library.path", "unset"));
            System.out.println("bootName=" + System.getProperty("jna.boot.library.name", "unset"));
        }
    }
}
