package org.kobjects.db;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Jörg Pleumann
 * @version 1.0
 */

import java.io.*;
import java.util.*;
//import javax.microedition.rms.*;

/**
 * Represents a database table.
 */
public interface DbTable {

    /**
     * Is called to connect to a table. Note that the given connector follows
     * the syntax used in the connect() factory method, so the actual table
     * name has to be derived from it.
     */
    public void connect(String connector) throws DbException;

    /**
     * Returns the table's name.
     */
    public String getName();

    /**
     * Returns true if the table exists and false otherwise.
     */
    public boolean exists();

    /**
     * Returns true if the table is open and false otherwise.
     */
    public boolean isOpen();

    /**
     * Deletes the table. Succeeds if the table doesn't exist. Throws an error
     * if the table cannot be deleted for some reason.
     */
    public void delete() throws DbException;

    /**
     * Creates the table. This method creates a new physical table or overwrites
     * an existing one. The method can only be called when the table is closed.
     * The new table will not automatically be opened.
     */
    public void create() throws DbException;

    /**
     * Returns the index of the Id field of this table; -1 if there is no 
     * id field. */
    
    public int getIdField ();


    /**
     * Opens the table. Must throw an exception if the table is already open or
     * some other error occurs.
     */
    public void open() throws DbException;

    /**
     * Closes the table. Always succeeds.
     */
    public void close() throws DbException;

    /**
     * Adds a field to the table. The table must be closed for this method
     * to succeed.
     */
    public DbField addField(String name, int type) throws DbException;

    /**
     * Returns the total number of fields.
     */
    public int getFieldCount();

    /**
     * Returns the field with the given index.
     */
    public DbField getField(int i);

    /**
     * Returns the index of the given field or -1 if the field does not exist.
     */
    public int findField(String name);

    /**
     * Select all records. This is a convenience method. The usual rules for
     * the "big" select() apply.
     */
    public DbResultSet select(boolean updated) throws DbException;

 
    /**
     * Select the given field indices from all records for which the
     * given condition evaluates to true. If the given orderField is a
     * valid field number, the result set is sorted according to that
     * field. If the orderField is -1, the result set is sorted
     * according to the records' IDs. The order can be reversed using
     * the correspoding parameter. The last parameter decides whether
     * the whole result set is kept updated or not. The returned
     * result set is positioned before the first record.  */

    public DbResultSet select(int [] fields, DbCondition filter, 
			   int oderBy, boolean reverse, 
			   boolean updated) throws DbException;
}






