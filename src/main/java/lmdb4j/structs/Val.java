package lmdb4j.structs;

import java.io.IOException;
import java.io.Writer;

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

    private static final char[] HEX = "0123456789ABCDEF".toCharArray();

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

    public void writeTo(Writer writer) throws IOException {
        for(int i = 0; i < size; i++) {
            int c = mmap.getByte(addr+i) & 0xFF;
            if(c >= ' ' && c <= 0x7F) {
                if(c == '\\' || c == '*' || c == ':') {
                    writer.write('\\');
                }
                writer.write(c);
            } else if(c == '\t') {
                writer.write('\\');
                writer.write('t');
            } else if(c == '\r') {
                writer.write('\\');
                writer.write('r');
            } else if(c == '\n') {
                writer.write('\\');
                writer.write('n');
            } else {
                writer.write("\\x");
                writer.write(HEX[c >> 4]);
                writer.write(HEX[c & 0xF]);
            }
        }
    }
}
