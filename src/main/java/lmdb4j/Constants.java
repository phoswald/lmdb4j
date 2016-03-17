package lmdb4j;

class Constants {

    /** A stamp that identifies a file as an LMDB file.
     *  There's nothing special about this value other than that it is easily
     *  recognizable, and it will reflect any byte order mismatches.
     */
    static final int MDB_MAGIC = 0xBEEFC0DE;

    /** The version number for a database's datafile format. */
    static final int MDB_DATA_VERSION = 1;

    /** The version number for a database's lockfile format. */
    static final int MDB_LOCK_VERSION = 1;

    /** Handle for the DB used to track free pages. */
    static final int FREE_DBI = 0;

    /** Handle for the default DB. */
    static final int MAIN_DBI = 1;

    /** Number of DBs in metapage (free and main) - also hardcoded elsewhere */
    static final int CORE_DBS = 2;

    /** Number of meta pages - also hardcoded elsewhere */
    static final int NUM_METAS = 2;
}
