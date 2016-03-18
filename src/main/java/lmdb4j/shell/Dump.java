package lmdb4j.shell;

import java.io.IOException;
import java.nio.file.Paths;

import lmdb4j.Database;
import lmdb4j.DatabaseException;

public class Dump {
    public static void main(String[] args) throws IOException, DatabaseException {
        try(Database db = new Database(Paths.get(args[0]))) {
            db.dump(System.out);
        }
    }
}
