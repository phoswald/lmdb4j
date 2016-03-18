package lmdb4j.structs;

import lmdb4j.mmap.MappedBuffer;

/**
 * Generic structure used for passing keys and data in and out of the database.
 *
 * Values returned from the database are valid only until a subsequent update operation, or the end of the transaction.
 * Do not modify or free them, they commonly point into the database itself.
 *
 * Key sizes must be between 1 and mdb_env_get_maxkeysize() inclusive.
 * The same applies to data sizes in databases with the MDB_DUPSORT flag.
 * Other data items can in theory be from 0 to 0xffffffff bytes long.
 */
public class Val {

    private final MappedBuffer mmap;
    private final long addr;
    private final int size;

    public Val(MappedBuffer mmap, long addr, int size) {
        this.mmap = mmap;
        this.addr = addr;
        this.size = size;

//        System.out.println("Val@"+addr+"("+size+"):");
//        for(int d = 0; d < size; d++) {
//            System.out.format(" %02x", mmap.getByte(addr+d));
//        }
//        System.out.format("\n");
    }

    public String asString() {
        return mmap.getString(addr, size);
    }
}
