package lmdb4j;

import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.ShortBuffer;

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
}
