package org.kobjects.db;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Jörg Pleumann
 * @version 1.0
 */

public class DbException extends Exception {

    private Exception chained;

    public DbException(String message) {
        super(message);
    }

    public DbException(String message, Exception chained) {
        super(message + " (" + chained + ")");
        this.chained = chained;
    }
}
