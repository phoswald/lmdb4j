package lmdb4j;

import static lmdb4j.Constants.FREE_DBI;

/**
 * Meta page content.
 *
 * A meta page is the start point for accessing a database snapshot. Pages 0-1 are meta pages. Transaction N writes meta page #(N % 2).
 */
class StructMeta {

    static final int SIZE = 82 /* 136 */;

    private final MappedBuffer mmap;
    private final long addr;

    StructMeta(MappedBuffer mmap, long addr) {
        this.mmap = mmap;
        this.addr = addr;

        System.out.println("struct Meta@"+addr+":");
        for(int d = 0; d < SIZE; d+=8) {
            System.out.format(" %4d: %016x", d, mmap.getLong(addr+d));
            System.out.format(" / %08x %08x", mmap.getInt(addr+d), mmap.getInt(addr+d+4));
            System.out.format(" / %04x %04x %04x %04x", mmap.getShort(addr+d), mmap.getShort(addr+d+2), mmap.getShort(addr+d+4), mmap.getShort(addr+d+6));
            System.out.format(" / %02x %02x %02x %02x %02x %02x %02x %02x\n", mmap.getByte(addr+d), mmap.getByte(addr+d+1), mmap.getByte(addr+d+2), mmap.getByte(addr+d+3), mmap.getByte(addr+d+4), mmap.getByte(addr+d+5), mmap.getByte(addr+d+6), mmap.getByte(addr+d+7));
        }
    }

    /**
     * Stamp identifying this as an LMDB file. It must be set to MDB_MAGIC.
     */
    int getMagic() {
        return mmap.getInt(addr);
    }

    /**
     * Version number of this file. Must be set to MDB_DATA_VERSION.
     */
    int getVersion() {
        return mmap.getInt(addr + 4);
    }

    /**
     * address for fixed mapping
     */
    long getAddress() {
        //return mmap.getLong(addr + 8);
        return mmap.getInt(addr + 8);
    }

    /**
     * size of mmap region
     */
    long getMapSize() {
        //return mmap.getLong(addr + 16);
        return mmap.getInt(addr + 12);
    }

    /**
     * first is free space, 2nd is main db
     */
    StructDB getDB(int index) {
        //return new StructDB(mmap, addr + 24 + index * StructDB.SIZE);
        return new StructDB(mmap, addr + 16 + index * StructDB.SIZE);
    }

    /**
     * The size of pages used in this DB
     */
    int getPageSize() {
        return getDB(FREE_DBI).getPad();
    }

    /**
     * last used page in file
     */
    long getLastPgNo() {
        //return mmap.getLong(addr + 120);
        return mmap.getInt(addr + 72);
    }

    /**
     * txnid that committed this page
     */
    long getTxnId() {
        //return mmap.getLong(addr + 128);
        return mmap.getInt(addr + 78);
    }
}
