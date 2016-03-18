package lmdb4j.structs;

import lmdb4j.mmap.MappedBuffer;

/**
 * Header for a single key/data pair within a page.
 *
 * Used in pages of type P_BRANCH and P_LEAF without P_LEAF2.
 * We guarantee 2-byte alignment for nodes.
 */
public class Node {

    public static final int SIZE = 8;

    private final MappedBuffer mmap;
    private final long addr;

    public Node(MappedBuffer mmap, long addr) {
        this.mmap = mmap;
        this.addr = addr;

//        System.out.println("Node@"+addr+":");
//        for(int d = 0; d < SIZE; d+=8) {
//            System.out.format(" %4d: %016x", d, mmap.getLong(addr+d));
//            System.out.format(" / %08x %08x", mmap.getInt(addr+d), mmap.getInt(addr+d+4));
//            System.out.format(" / %04x %04x %04x %04x", mmap.getShort(addr+d), mmap.getShort(addr+d+2), mmap.getShort(addr+d+4), mmap.getShort(addr+d+6));
//            System.out.format(" / %02x %02x %02x %02x %02x %02x %02x %02x\n", mmap.getByte(addr+d), mmap.getByte(addr+d+1), mmap.getByte(addr+d+2), mmap.getByte(addr+d+3), mmap.getByte(addr+d+4), mmap.getByte(addr+d+5), mmap.getByte(addr+d+6), mmap.getByte(addr+d+7));
//        }
    }

    /** lo and hi are used for data size on leaf nodes and for child pgno on branch nodes.
     * On 64 bit platforms, flags is also used for pgno. (Branch nodes have no flags).
     * They are in host byte order in case that lets some accesses be optimized into a 32-bit word access. */
    public short getLo() {
        return mmap.getShort(addr);
    }

    /** part of data size or pgno */
    public short getHi() {
        return mmap.getShort(addr + 2);
    }

    /** node flags */
    public short getFlags() {
        return mmap.getShort(addr + 4);
    }

    /** key size */
    public short getKSize() {
        return mmap.getShort(addr + 6);
    }

    public int getDSize() {
        return getLo() + (getHi() << 16);
    }

    public long getPgNo() {
        return getLo() + (getHi() << 16) /*+ ((long) getFlags() << 32)*/;
    }

    /** key and data are appended here */

    public Val getKey() {
        return new Val(mmap, addr + SIZE, getKSize());
    }

    public Val getData() {
        return new Val(mmap, addr + SIZE + getKSize(), getDSize());
    }
}
