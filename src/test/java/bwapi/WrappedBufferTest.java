package bwapi;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public class WrappedBufferTest {
    private WrappedBuffer sut = new WrappedBuffer(ByteBuffer.allocateDirect(1024));

    @Test
    public void shouldGetAndSetStrings() {
        // GIVEN
        String testString = "Test";
        sut.putString(123, 100, testString);

        // WHEN
        String readString = sut.getString(123, 100);

        // THEN
        assertThat(readString).isEqualTo(testString);
    }

    @Test
    public void shouldCutOffAtMaxLength() {
        // GIVEN
        String testString = "Test";
        sut.putString(123, 100, testString);

        // WHEN
        String readString = sut.getString(123, 3);

        // THEN
        assertThat(readString).isEqualTo(testString.substring(0, 3));
    }
}