package lmdb4j;

import static lmdb4j.structs.Constants.P_BRANCH;
import static lmdb4j.structs.Constants.P_LEAF;
import static lmdb4j.structs.Constants.P_META;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lmdb4j.structs.Node;
import lmdb4j.structs.Page;

public class DatatbaseTest {

    private Database db;

    @Before
    public void create() throws IOException {
        db = new Database(Paths.get("src", "test", "resources", "dir-tree.mdb"));
        assertEquals("src/test/resources/dir-tree.mdb", db.toString());
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
        assertEquals(2819, info.lastPgNo);
        assertEquals(1, info.lastTxnId);
    }

    @Test
    public void getStat() throws DatabaseException {
        DatabaseStat stat = db.getStat();

        assertNotNull(stat);
        assertEquals(4096, stat.psize);
        assertEquals(3, stat.depth);
        assertEquals(43, stat.branchPages);
        assertEquals(2775, stat.leafPages);
        assertEquals(0, stat.overflowPages);
        assertEquals(138105, stat.entries);
    }

    @Test
    public void getPages() throws DatabaseException {
        DatabaseInfo info = db.getInfo();
        DatabaseStat stat = db.getStat();
        assertEquals(2819, info.lastPgNo);

        long metaPages = 0;
        long branchPages = 0;
        long leafPages = 0;
        long entries = 0;
        for(long pgno = 0; pgno <= info.lastPgNo; pgno++) {
            Page page = db.getPage(pgno);
            assertEquals(pgno, page.getPgNo());

            int flags = page.getFlags();
            assertTrue(flags == P_META || flags == P_BRANCH || flags == P_LEAF);
            switch(flags) {
                case P_META:
                    metaPages++;
                    break;
                case P_BRANCH:
                    branchPages++;
                    break;
                case P_LEAF:
                    leafPages++;
                    int numKeys = page.getNumKeys();
                    for(int i = 0; i < numKeys; i++) {
                        entries++;
                        Node node = page.getNode(i);
                        String key = node.getKey().asString();
                        String data = node.getData().asString();
                        //System.out.println("*** '" + key + "' = '" + data + "'.");
                    }
            }
        }

        assertEquals(2, metaPages);
        assertEquals(stat.branchPages, branchPages);
        assertEquals(stat.leafPages, leafPages);
        assertEquals(stat.entries, entries);
    }
}
