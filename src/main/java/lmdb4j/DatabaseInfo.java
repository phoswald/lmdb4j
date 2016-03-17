package lmdb4j;

public class DatabaseInfo {

    public final long mapAddress;
    public final long mapSize;
    public final long lastPgNo;
    public final long lastTxnId;
    public final int maxReaders;
    public final int numReaders;

    public DatabaseInfo(long mapAddress, long mapSize, long lastPgNo, long lastTxnId, int maxReaders, int numReaders) {
        this.mapAddress = mapAddress;
        this.mapSize = mapSize;
        this.lastPgNo = lastPgNo;
        this.lastTxnId = lastTxnId;
        this.maxReaders = maxReaders;
        this.numReaders = numReaders;
    }
}
