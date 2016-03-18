package lmdb4j.structs;

public class Constants {

    // META

    /** A stamp that identifies a file as an LMDB file.
     *  There's nothing special about this value other than that it is easily
     *  recognizable, and it will reflect any byte order mismatches.
     */
    public static final int MDB_MAGIC = 0xBEEFC0DE;

    /** The version number for a database's datafile format. */
    public static final int MDB_DATA_VERSION = 1;

    /** The version number for a database's lockfile format. */
    public static final int MDB_LOCK_VERSION = 1;

    /** Handle for the DB used to track free pages. */
    public static final int FREE_DBI = 0;

    /** Handle for the default DB. */
    public static final int MAIN_DBI = 1;

    /** Number of DBs in metapage (free and main) - also hardcoded elsewhere */
    public static final int CORE_DBS = 2;

    /** Number of meta pages - also hardcoded elsewhere */
    public static final int NUM_METAS = 2;

    // PAGE

    /** branch page */
    public static final int P_BRANCH = 0x01;

    /** leaf page */
    public static final int P_LEAF = 0x02;

    /** overflow page */
    public static final int P_OVERFLOW = 0x04;

    /** meta page */
    public static final int P_META = 0x08;

    /** dirty page, also set for P_SUBP pages */
    public static final int P_DIRTY = 0x10;

//    /** for MDB_DUPFIXED records */
//    public static final int P_LEAF2 = 0x20;

//    /** for MDB_DUPSORT sub-pages */
//    public static final int P_SUBP = 0x40;

    /** page was dirtied then freed, can be reused **/
    public static final int P_LOOSE = 0x4000;

    /** leave this page alone during spill */
    public static final int P_KEEP = 0x8000;

    // NODE

    /** data put on overflow page */
    public static final int F_BIGDATA = 0x01;

    /** data is a sub-database */
    public static final int F_SUBDATA  = 0x02;

    /** data has duplicates */
    public static final int F_DUPDATA = 0x04;
}
