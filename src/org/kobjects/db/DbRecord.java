package org.kobjects.db;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Jörg Pleumann
 * @version 1.0
 */

/**
 * To do:
 *
 * - Warum stehen hier alle Zugriffsmethoden quasi für RMS drin. Für andere DBs
 *   wird der Zugriff mit dem Umweg über Objekte doch eher lästig sein. Dito für
 *   absolute etc.
 * - Es fehlen: Get RowCount
 * - Sollte das nicht eher ein Interface sein?

  unklare semantik:

   - darf refresh aufgerufen werden falls modified? -> Cancel
   - insert: next?!?!, insbes. bei einzelnem record
 */

import java.io.*;
import java.util.*;
//import javax.microedition.rms.*;

/**
 * Represents a record in a table. Actually, this class resembles an
 * SQL result set more than a record. However, there are a few
 * differences: The indices always correspond to the column indices of
 * the original table. Also, there are separate next and hasNext
 * methods.  */

public interface DbRecord {

    public Object getObject(int column);

    public void setObject(int column, Object value);

    public boolean getBoolean(int column);

    public int getInteger(int column);

    public long getLong(int column);

    public String getString(int column);

    public InputStream getBinary(int column);

    public void setBoolean(int column, boolean value);

    public void setInteger(int column, int value);

    public void setLong(int column, long value);

    public void setString(int column, String value);

    public void setBinary(int column, InputStream value);

    public void refresh() throws DbException;

    public void update() throws DbException;

    public void insert() throws DbException;

    public void insert(Object[] values) throws DbException;

    /**
     * Aktuellen Record löschen. Ändert nicht die Position innerhalb des
     * Result Sets. Stattdessen wird einfach nur "deleted" auf true gesetzt.
     */
    public void delete() throws DbException;

    /**
     * Alle Records in der Enumeration löschen.
     */
    public void deleteAll() throws DbException;

    public boolean isModified();

    public boolean isDeleted();

    /**
     * Returns the table this record belongs to.
     */
    public DbTable getTable();

    /**
     * Resets the iterator before the first row.
     */
    public void beforeFirst() throws DbException;

    /**
     * Queries whether the is a next element in this result set.
     */
    public boolean hasNext();

    /**
     * Proceeds to the next element in this result set, returning its record ID.
     */
    public void next() throws DbException;

    /**
     * Returns the total number of rows. Influences by update(), insert() and
     * delete(), in case the Record is kept updated.
     */
    public int getRowCount();

    /**
     * Returns the current row number.
     */
    public int getRow();

    /**
     * Jumps to a given row. Please note: Row counting starts with 1
     */
    public void absolute(int row) throws DbException;

    /**
     * Throws away the record and all resources it has reserved.
     */
    public void dispose();

    /** 
     * returns an int array containing the selected field
     * indices, or null, if all fields are selected */

    public int [] getSelectedFields ();
}
