package bwapi;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;

/**
 * Verifies the Windows mapping binding's JNA signatures against a real shared-memory object.
 * The existing JNA Kernel32 binding creates the mapping and its first view;
 * MappingKernel opens a second view. Reads and writes must agree in both directions.
 * A Unicode name checks Windows string marshalling, and a missing mapping must
 * return null. These tests require Windows but do not require a BWAPI server.
 */
public class MappingKernelTest {
    @Before
    public void requireWindows() {
        assumeTrue(System.getProperty("os.name").startsWith("Windows"));
    }

    @Test
    public void opensUnicodeNamedMappingAndSharesWritesBetweenViews() {
        // Include Unicode to exercise the same W32API string mapping used by the client.
        String name = "Local\\jbwapi-mapping-\u03bb-" + System.nanoTime();
        int access = WinNT.SECTION_MAP_READ | WinNT.SECTION_MAP_WRITE;
        WinNT.HANDLE created = Kernel32.INSTANCE.CreateFileMapping(
                WinBase.INVALID_HANDLE_VALUE, null, WinNT.PAGE_READWRITE, 0, 4, name);
        assertNotNull(created);
        WinNT.HANDLE opened = null;
        Pointer originalView = null;
        Pointer clientView = null;
        try {
            originalView = Kernel32.INSTANCE.MapViewOfFile(created, access, 0, 0, 4);
            assertNotNull(originalView);
            originalView.setInt(0, 10003);

            opened = ClientConnectionW32.MappingKernel.INSTANCE.OpenFileMapping(access, false, name);
            assertNotNull(opened);
            clientView = ClientConnectionW32.MappingKernel.INSTANCE.MapViewOfFile(opened, access, 0, 0, 4);
            assertNotNull(clientView);
            assertEquals(10003, clientView.getInt(0));

            clientView.setInt(0, 42);
            assertEquals(42, originalView.getInt(0));
        } finally {
            if (clientView != null) {
                Kernel32.INSTANCE.UnmapViewOfFile(clientView);
            }
            if (originalView != null) {
                Kernel32.INSTANCE.UnmapViewOfFile(originalView);
            }
            if (opened != null) {
                Kernel32.INSTANCE.CloseHandle(opened);
            }
            Kernel32.INSTANCE.CloseHandle(created);
        }
    }

    @Test
    public void returnsNullForMissingMapping() {
        assertNull(ClientConnectionW32.MappingKernel.INSTANCE.OpenFileMapping(
                WinNT.SECTION_MAP_READ, false, "Local\\jbwapi-missing-" + System.nanoTime()));
    }
}
