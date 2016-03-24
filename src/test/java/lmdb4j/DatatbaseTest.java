package lmdb4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatatbaseTest {

    private Database db;

    @Before
    public void create() throws IOException {
        db = new Database(Paths.get("src", "test", "resources", "dirtree.mdb"));
        assertEquals("src/test/resources/dirtree.mdb", db.toString());
    }

    @After
    public void close() throws IOException {
        db.close();
    }

    @Test
    public void getInfo() throws DatabaseException {
        DatabaseInfo info = db.getInfo();

        assertNotNull(info); // TODO check more fields: maxReaders, numReaders
        assertEquals(0, info.mapAddress);
        assertEquals(512 << 20, info.mapSize);
        assertEquals(12413, info.lastPgNo);
        assertEquals(1, info.lastTxnId);
    }

    @Test
    public void getStat() throws DatabaseException {
        DatabaseStat stat = db.getStat();

        assertNotNull(stat);
        assertEquals(4096, stat.psize);
        assertEquals(4, stat.depth);
        assertEquals(176, stat.branchPages);
        assertEquals(12236, stat.leafPages);
        assertEquals(0, stat.overflowPages);
        assertEquals(653132, stat.entries);
    }

    @Test
    public void verifyStat() throws DatabaseException {
        db.verifyStat();
    }

    @Test
    public void dumpToString() throws DatabaseException, IOException {
        CharArrayWriter writer = new CharArrayWriter();
        db.dump(writer);
        String text = writer.toString();
        assertEquals(24983234, text.length());
    }
}
