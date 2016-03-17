package lmdb4j;

/**
 * Information about a single database in the environment.
 */
class StructDB {

    static final int SIZE = 28 /* 48 */;

    private final MappedBuffer mmap;
    private final long addr;

    StructDB(MappedBuffer mmap, long addr) {
        this.mmap = mmap;
        this.addr = addr;

        System.out.println("struct StructDB@"+addr+":");
        for(int d = 0; d < SIZE; d+=8) {
            System.out.format(" %4d: %016x", d, mmap.getLong(addr+d));
            System.out.format(" / %08x %08x", mmap.getInt(addr+d), mmap.getInt(addr+d+4));
            System.out.format(" / %04x %04x %04x %04x", mmap.getShort(addr+d), mmap.getShort(addr+d+2), mmap.getShort(addr+d+4), mmap.getShort(addr+d+6));
            System.out.format(" / %02x %02x %02x %02x %02x %02x %02x %02x\n", mmap.getByte(addr+d), mmap.getByte(addr+d+1), mmap.getByte(addr+d+2), mmap.getByte(addr+d+3), mmap.getByte(addr+d+4), mmap.getByte(addr+d+5), mmap.getByte(addr+d+6), mmap.getByte(addr+d+7));
        }
    }

    /**
     * also ksize for LEAF2 pages
     */
    int getPad() {
        return mmap.getInt(addr);
    }

    /**
     * database flags
     */
    short getFlags() {
        return mmap.getShort(addr + 4);
    }

    /**
     * depth of this tree
     */
    short getDepth() {
        return mmap.getShort(addr + 6);
    }

    /**
     * number of internal pages
     */
    long getBranchPages() {
        // return mmap.getLong(addr + 8);
        return mmap.getInt(addr + 8);
    }

    /**
     * number of leaf pages
     */
    long getLeafPages() {
        //return mmap.getLong(addr + 16);
        return mmap.getInt(addr + 12);
    }

    /**
     * number of overflow pages
     */
    long getOverflowPages() {
        //return mmap.getLong(addr + 24);
        return mmap.getInt(addr + 16);
    }

    /**
     * number of data items
     */
    long getEntries() {
        //return mmap.getLong(addr + 32);
        return mmap.getInt(addr + 20);
    }

    /**
     * the root page of this tree
     */
    long getRoot() {
        //return mmap.getLong(addr + 40);
        return mmap.getInt(addr + 24);
    }
}
