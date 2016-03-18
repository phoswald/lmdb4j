package lmdb4j;

import static lmdb4j.structs.Constants.MAIN_DBI;
import static lmdb4j.structs.Constants.NUM_METAS;
import static lmdb4j.structs.Constants.P_BRANCH;
import static lmdb4j.structs.Constants.P_LEAF;
import static lmdb4j.structs.Constants.P_META;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import lmdb4j.mmap.MappedBuffer;
import lmdb4j.structs.Constants;
import lmdb4j.structs.DB;
import lmdb4j.structs.Meta;
import lmdb4j.structs.Node;
import lmdb4j.structs.Page;

public class Database implements AutoCloseable {

    private final Path file;
    private final FileChannel channel;
    private final MappedBuffer mmap;

    private Meta meta;
    private int psize;

    public Database(Path file) throws IOException {
        this.file = file;
        this.channel = FileChannel.open(file, StandardOpenOption.READ);
        try {
            this.mmap = new MappedBuffer(channel.map(MapMode.READ_ONLY, 0, channel.size()));
        } catch(IOException | RuntimeException e) {
            channel.close();
            throw e;
        }
    }

    @Override
    public String toString() {
        return file.toString();
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

    private void ensureOpen() throws DatabaseException {
        if(meta == null) {
            meta = readEnvHeader();
            psize = meta.getPageSize();
        }
    }

    private Meta readEnvHeader() throws DatabaseException {
        Meta meta = null;
        int off = 0;
        // We don't know the page size yet, so use a minimum value.
        // Read both meta pages so we can use the latest one.
        for(int i = 0; i < NUM_METAS; i++, off+=meta.getPageSize()) {
            // Page p = new Page(mmap, off);
            Meta m = new Meta(mmap, off + Page.SIZE);
            if(m.getMagic() != Constants.MDB_MAGIC) {
                throw new DatabaseException("meta has invalid magic");
            }
            if(m.getVersion() != Constants.MDB_DATA_VERSION) {
                throw new DatabaseException("database is version " + meta.getVersion() + ", expected version " + Constants.MDB_DATA_VERSION);
            }
            if(meta == null || m.getTxnId() > meta.getTxnId()) {
                meta = m;
            }
        }
        return meta;
    }

    public DatabaseInfo getInfo() throws DatabaseException {
        ensureOpen();
        return new DatabaseInfo(meta.getAddress(), meta.getMapSize(), meta.getLastPgNo(), meta.getTxnId(), -1, -1);
    }

    public DatabaseStat getStat() throws DatabaseException {
        ensureOpen();
        DB db = meta.getDB(MAIN_DBI);
        return new DatabaseStat(psize, db.getDepth(), db.getBranchPages(), db.getLeafPages(), db.getOverflowPages(), db.getEntries());
    }

    private Page getPage(long pgno) throws DatabaseException {
        return new Page(mmap, pgno * psize);
    }

    private long getRoot() throws DatabaseException {
        return meta.getDB(MAIN_DBI).getRoot();
    }

    public void verifyStat() throws DatabaseException {
        ensureOpen();
        DatabaseStat stat = getStat();
        long lastPgNo = meta.getLastPgNo();
        long metaPages = 0;
        long branchPages = 0;
        long leafPages = 0;
        long entries = 0;
        for(long pgno = 0; pgno <= lastPgNo; pgno++) {
            Page page = getPage(pgno);
            assertEquals("pgno", pgno, page.getPgNo());

            int flags = page.getFlags();
            if(flags != P_META && flags != P_BRANCH && flags != P_LEAF) {
                throw new IllegalStateException("invalid page flags: " + flags);
            }
            switch(flags) {
                case P_META:
                    metaPages++;
                    break;
                case P_BRANCH: {
                    branchPages++;
                    int numKeys = page.getNumKeys();
                    for(int i = 0; i < numKeys; i++) {
                        Node node = page.getNode(i);
                        /*String key =*/ node.getKey().asString();
                        /*long refPgNo =*/ node.getPgNo();
                    }
                }   break;
                case P_LEAF: {
                    leafPages++;
                    int numKeys = page.getNumKeys();
                    for(int i = 0; i < numKeys; i++) {
                        entries++;
                        Node node = page.getNode(i);
                        /*String key =*/ node.getKey().asString();
                        /*String data =*/ node.getData().asString();
                    }
                }   break;
            }
        }
        assertEquals("metaPages", 2, metaPages);
        assertEquals("branchPages", stat.branchPages, branchPages);
        assertEquals("leafPages", stat.leafPages, leafPages);
        assertEquals("entries", stat.entries, entries);
    }

    private void assertEquals(String value, long expected, long actual) {
        if(expected != actual) {
            throw new IllegalStateException(value + ": exepcted="+expected+", actual="+actual);
        }
    }

    public void dump(OutputStream stream) throws IOException, DatabaseException {
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        dump(writer);
        writer.flush();
    }

    public void dump(Writer writer) throws IOException, DatabaseException {
        ensureOpen();
        dump(writer, getRoot());
    }

    private void dump(Writer writer, long pgno) throws IOException, DatabaseException {
        Page page = getPage(pgno);
        switch(page.getFlags()) {
            case Constants.P_BRANCH: {
                int numKeys = page.getNumKeys();
                for(int i = 0; i < numKeys; i++) {
                    dump(writer, page.getNode(i).getPgNo());
                }
            }   break;
            case Constants.P_LEAF: {
                int numKeys = page.getNumKeys();
                for(int i = 0; i < numKeys; i++) {
                    Node node = page.getNode(i);
                    writer.write('+');
                    node.getKey().writeTo(writer);
                    writer.write(':');
                    node.getData().writeTo(writer);
                    writer.write('\n');
                }
            }   break;
        }
    }
}
