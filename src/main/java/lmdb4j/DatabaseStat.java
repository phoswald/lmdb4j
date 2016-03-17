package lmdb4j;

public class DatabaseStat {

    public final int psize;
    public final int depth;
    public final long branchPages;
    public final long leafPages;
    public final long overflowPages;
    public final long entries;

    public DatabaseStat(int psize, int depth, long branchPages, long leafPages, long overflowPages, long entries) {
        this.psize = psize;
        this.depth = depth;
        this.branchPages = branchPages;
        this.leafPages = leafPages;
        this.overflowPages = overflowPages;
        this.entries = entries;
    }
}
