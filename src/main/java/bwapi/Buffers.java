package bwapi;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

final class Buffers {
  private static final CharsetEncoder enc = Charset.forName("ISO-8859-1").newEncoder();

  private Buffers() {
    // Utility class
  }


  public static String toString(ByteBuffer buffer, int offset, int maxLen) {
    final byte[] buf = new byte[maxLen];
    buffer.position(offset);
    buffer.get(buf, 0, maxLen);
    buffer.position(0);
    int len = 0;
    while (len < maxLen && buf[len] != 0) {
      ++len;
    }
    return new String(buf, 0, len, StandardCharsets.ISO_8859_1);
  }

  public static void fromString(ByteBuffer buffer, int offset, int maxLen, String s) {
    final int len = s.length() + 1;
    if (len >= maxLen) {
      throw new StringIndexOutOfBoundsException();
    }
    buffer.position(offset);
    enc.encode(CharBuffer.wrap(s), buffer, true);
    buffer.rewind();
  }
}
