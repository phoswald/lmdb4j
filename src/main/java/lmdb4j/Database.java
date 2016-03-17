package lmdb4j;

import static lmdb4j.Constants.MAIN_DBI;
import static lmdb4j.Constants.NUM_METAS;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Database implements AutoCloseable {

    private final Path file;
    private final FileChannel channel;
    private final MappedBuffer mmap;

    private StructMeta meta;
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

    private StructMeta readEnvHeader() throws DatabaseException {
        StructMeta meta = null;
        int off = 0;
        // We don't know the page size yet, so use a minimum value.
        // Read both meta pages so we can use the latest one.
        for(int i = 0; i < NUM_METAS; i++, off+=meta.getPageSize()) {
            StructMeta m = new StructMeta(mmap, off + StructHeader.SIZE);
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
        StructDB db = meta.getDB(MAIN_DBI);
        return new DatabaseStat(psize, db.getDepth(), db.getBranchPages(), db.getLeafPages(), db.getOverflowPages(), db.getEntries());
    }
}
