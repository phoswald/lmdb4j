package lmdb4j.mmap;

import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;

public final class MappedBuffer {

    private final MappedByteBuffer bytes;
    private final ShortBuffer shorts;
    private final IntBuffer ints;
    private final LongBuffer longs;

    public MappedBuffer(MappedByteBuffer bytes) {
        bytes.order(ByteOrder.LITTLE_ENDIAN);
        this.bytes = bytes;
        this.shorts = bytes.asShortBuffer();
        this.ints = bytes.asIntBuffer();
        this.longs = bytes.asLongBuffer();
    }

    public final byte getByte(long addr) {
        return bytes.get((int) addr /* currently limited to 4 GB */);
    }

    public final void putByte(long addr, byte value) {
        bytes.put((int) addr /* currently limited to 4 GB */, value);
    }

    public final short getShort(long addr) {
        return shorts.get((int) (addr >> 1) /* currently limited to 4 GB */);
    }

    public final void putShort(long addr, short value) {
        shorts.put((int) (addr >> 1) /* currently limited to 4 GB */, value);
    }

    public final int getInt(long addr) {
        return ints.get((int) (addr >> 2) /* currently limited to 4 GB */);
    }

    public final void putInt(long addr, int value) {
        ints.put((int) (addr >> 2) /* currently limited to 4 GB */, value);
    }

    public final long getLong(long addr) {
        return longs.get((int) (addr >> 3) /* currently limited to 4 GB */);
    }

    public final void putLong(long addr, long value) {
        longs.put((int) (addr >> 3) /* currently limited to 4 GB */, value);
    }

    public final String getString(long addr, int size) {
        byte[] val = new byte[size];
        for(int i = 0; i < size; i++) {
            val[i] = getByte(addr+i);
        }
        return new String(val, StandardCharsets.UTF_8);
    }
}
