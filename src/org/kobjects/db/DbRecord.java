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
public abstract class DbRecord {

    protected DbTable table;

    protected Object[] values;

    protected boolean modified;

    protected boolean deleted;

    protected DbRecord(DbTable table) {
        this.table = table;
    }

    protected Object[] getValues() {
        return values;
    }

    protected void setValues(Object[] values) {
        for (int i = 0; i < values.length; i++) {
            this.values[i] = values[i];
        }
    }

    public void clear() {
        values = new Object[getTable().getFieldCount()];
        modified = true;
    }

    public Object getObject(int column) {
        return values[column];
    }

    public void setObject(int column, Object value) {
        values[column] = value;
        modified = true;
    }

    public boolean getBoolean(int column) {
        Boolean b = (Boolean)getObject(column);
        return (b == null) ? false : b.booleanValue();
    }


    public int getInteger(int column) {
        Integer i = (Integer)getObject(column);
        return (i == null) ? 0 : i.intValue();
    }


    public long getLong(int column) {
        Long l = (Long)getObject(column);
        return (l == null) ? 0 : l.longValue();
    }

    public String getString(int column) {
        Object o = getObject(column);
        return (o == null) ? null : o.toString();
    }

    public byte[] getBinary(int column) {
        return (byte[])getObject(column);
    }

    public void setBoolean(int column, boolean value) {
        setObject(column, new Boolean(value));
    }

    public void setInteger(int column, int value) {
        setObject(column, new Integer(value));
    }

    public void setLong(int column, long value) {
        setObject(column, new Long(value));
    }

    public void setString(int column, String value) {
        setObject(column, value);
    }

    public void setBinary(int column, byte[] value) {
        //byte[] bytes = new byte[value.length];
        //System.arraycopy(value, 0, bytes, 0, value.length);
        setObject(column, value); // was: bytes
    }

    public abstract void refresh() throws DbException;

    public abstract void update() throws DbException;

    public void insert() throws DbException {
        modified = true;

        DbTable table = getTable ();
        int cnt = table.getFieldCount ();
        for (int i = 0; i < cnt; i++) {
            values[i] = table.getField(i).getDefault();
        }
    }

    public void insert(Object[] values) throws DbException {
        insert();

        for (int i = 0; i < values.length; i++) {
            setObject(i, values[i]);
        }
    }

    /**
     * Aktuellen Record löschen. Ändert nicht die Position innerhalb des
     * Result Sets. Stattdessen wird einfach nur "deleted" auf true gesetzt.
     */
    abstract public void delete() throws DbException;

    /**
     * Alle Records in der Enumeration löschen.
     */
    abstract public void deleteAll() throws DbException;

    public boolean isModified() {
        return modified;
    }

    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Returns the table this record belongs to.
     */
    public abstract DbTable getTable();

    /**
     * Returns the ID of the current record.
     */
    public abstract Object getId();

    /**
     * Resets the iterator before the first row.
     */
    public abstract void reset() throws DbException;

    /**
     * Queries whether the is a next element in this result set.
     */
    public abstract boolean hasNextElement();

    /**
     * Proceeds to the next element in this result set, returning its record ID.
     */
    public abstract Object nextElement() throws DbException;

    /**
     * Returns the total number of rows.
     */
    public abstract int getRowCount();

    /**
     * Returns the current row number.
     */
    public abstract int getRow();

    /**
     * Jumps to a given row.
     */
    public abstract void absolute(int row) throws DbException;

    /**
     * Throws away the record and all resources it has reserved.
     */
    public void dispose() {
        table = null;
        values = null;
    }
}
