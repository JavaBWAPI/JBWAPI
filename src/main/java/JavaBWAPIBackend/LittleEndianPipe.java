package JavaBWAPIBackend;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

class LittleEndianPipe {
    RandomAccessFile pipe;
    LittleEndianPipe(String name, String mode) throws FileNotFoundException {
        pipe = new RandomAccessFile(name, mode);
    }

    final int readByte() throws IOException {
        return pipe.readByte();
    }

    final void writeByte(int b) throws IOException {
        pipe.writeByte(b);
    }

    final int readInt() throws IOException {
        int b1 = readByte(), b2 = readByte(), b3 = readByte(), b4 = readByte();
        return (b4 << 24) | (b3 << 16) | (b2 << 8) | b1;
    }

    final void writeInt(int i) throws IOException {
        writeByte(i >> 24);
        writeByte((i >> 16) & 0xff);
        writeByte((i >> 8) & 0xff);
        writeByte(i & 0xff);
    }
}
