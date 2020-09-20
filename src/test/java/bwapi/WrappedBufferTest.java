package bwapi;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WrappedBufferTest {
    private final WrappedBuffer sut = new WrappedBuffer(1024);

    @Test
    public void shouldGetAndSetStrings() {
        // GIVEN
        String testString = "@µöú";
        sut.putString(123, 100, testString);

        // WHEN
        String readString = sut.getString(123, 100);

        // THEN
        assertThat(readString).isEqualTo(testString);
    }

    @Test
    public void shouldCutOffAtMaxLength() {
        // GIVEN
        String testString = "@µöú";
        sut.putString(123, 100, testString);

        // WHEN
        String readString = sut.getString(123, 3);

        // THEN
        assertThat(readString).isEqualTo(testString.substring(0, 3));
    }
}