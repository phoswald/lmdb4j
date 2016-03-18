package lmdb4j.structs;

import lmdb4j.mmap.MappedBuffer;

/**
 * Common header for all page types.
 *
 * Overflow records occupy a number of contiguous pages with no headers on any page after the first.
 */
public class Page {

    public static final int SIZE = 12 /* ?? */;

    private final MappedBuffer mmap;
    private final long addr;

    public Page(MappedBuffer mmap, long addr) {
        this.mmap = mmap;
        this.addr = addr;

//        System.out.println("Page@"+addr+":");
//        for(int d = 0; d < SIZE; d+=8) {
//            System.out.format(" %4d: %016x", d, mmap.getLong(addr+d));
//            System.out.format(" / %08x %08x", mmap.getInt(addr+d), mmap.getInt(addr+d+4));
//            System.out.format(" / %04x %04x %04x %04x", mmap.getShort(addr+d), mmap.getShort(addr+d+2), mmap.getShort(addr+d+4), mmap.getShort(addr+d+6));
//            System.out.format(" / %02x %02x %02x %02x %02x %02x %02x %02x\n", mmap.getByte(addr+d), mmap.getByte(addr+d+1), mmap.getByte(addr+d+2), mmap.getByte(addr+d+3), mmap.getByte(addr+d+4), mmap.getByte(addr+d+5), mmap.getByte(addr+d+6), mmap.getByte(addr+d+7));
//        }
    }

    /** page number */
    public int getPgNo() {
        return mmap.getInt(addr);
    }

    public short getPad() {
        return mmap.getShort(addr + 4);
    }

    /** page flags */
    public short getFlags() {
        return mmap.getShort(addr + 6);
    }

    /** lower bound of free space */
    public short getLower() {
        return mmap.getShort(addr + 8);
    }

    /** upper bound of free space */
    public short getUpper() {
        return mmap.getShort(addr + 10);
    }

    /** number of overflow pages */
    public int getPages() {
        return mmap.getInt(addr + 8);
    }

    /** Number of nodes on a page */
    public int getNumKeys() {
        return (getLower() - SIZE) >> 1;
    }

    public int getNodePtr(int i) {
        return mmap.getShort(addr + SIZE + (i<<1));
    }

    /** Address of node i in page p */
    public Node getNode(int i) {
        return new Node(mmap, addr + getNodePtr(i));
    }
}
