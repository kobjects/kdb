package org.kobjects.db;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author J�rg Pleumann
 * @version 1.0
 */

/**
 * To do:
 *
 * - Warum stehen hier alle Zugriffsmethoden quasi f�r RMS drin. F�r andere DBs
 *   wird der Zugriff mit dem Umweg �ber Objekte doch eher l�stig sein. Dito f�r
 *   absolute etc.
 * - Es fehlen: Get RowCount
 * - Sollte das nicht eher ein Interface sein?

  unklare semantik:

   - darf refresh aufgerufen werden falls modified? -> Cancel
   - insert: next?!?!, insbes. bei einzelnem record
   - IMHO sollte statt insert besser "createRecord" in der tabelle
     sein (aber erst wirklich ausgef. wenn update aufgerufen wird.

 */

import java.io.*;
import java.util.*;
import javax.microedition.rms.*;

/**
 * Represents a record in a table. Actually, this class resembles an SQL result
 * set more than a record.
 */
public interface DbRecord {

    public void clear();

    public Object getObject(int column);

    public void setObject(int column, Object value);

    public boolean getBoolean(int column);

    public int getInteger(int column);

    public long getLong(int column);

    public String getString(int column);

    public byte[] getBinary(int column);

    public void setBoolean(int column, boolean value);

    public void setInteger(int column, int value);

    public void setLong(int column, long value);

    public void setString(int column, String value);

    public void setBinary(int column, byte[] value);

    public void refresh() throws DbException;

    public void update() throws DbException;

    public void insert() throws DbException;

    public void insert(Object[] values) throws DbException;

    /**
     * Aktuellen Record l�schen. �ndert nicht die Position innerhalb des
     * Result Sets. Stattdessen wird einfach nur "deleted" auf true gesetzt.
     */
    public void delete() throws DbException;

    /**
     * Alle Records in der Enumeration l�schen.
     */
    public void deleteAll() throws DbException;

    public boolean isModified();

    public boolean isDeleted();

    /**
     * Returns the table this record belongs to.
     */
    public DbTable getTable();

    /**
     * Returns the ID of the current record.
     */
    public Object getId();

    /**
     * Resets the iterator before the first row.
     */
    public void reset() throws DbException;

    /**
     * Queries whether the is a next element in this result set.
     */
    public boolean hasNextElement();

    /**
     * Proceeds to the next element in this result set, returning its record ID.
     */
    public Object nextElement() throws DbException;

    /**
     * Returns the total number of rows.
     */
    public int getRowCount();

    /**
     * Returns the current row number.
     */
    public int getRow();

    /**
     * Jumps to a given row.
     */
    public void absolute(int row) throws DbException;

    /**
     * Throws away the record and all resources it has reserved.
     */
    public void dispose();
}
