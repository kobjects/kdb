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
 */

import java.io.*;
//import javax.microedition.rms.*;

/**
 * A subset of the J2SE SQL Result Set, except from the additional
 * methods getRowCount, getColumnCount, getField, getTable, and getSize  */

public interface DbResultSet {

    public Object getObject(int column);

    public void updateObject(int column, Object value);

    public boolean getBoolean(int column);

	public int getColumnCount();
	
	public DbField getField(int column);
	
	public int findColumn(String name);

    public int getInt(int column);

    public long getLong(int column);

	/** 
	 * Returns the size of an binary field. -1 means unknown, -2 means
	 * the field is null. Some storages may not make a distinction
	 * between -2 and 0 */

	public long getSize(int column);

    public String getString(int column);

    public InputStream getBinaryStream(int column);

    public void updateBoolean(int column, boolean value);

    public void updateInteger(int column, int value);

    public void updateLong(int column, long value);

    public void updateString(int column, String value);

    public void updateBinaryStream(int column, InputStream value);

    public void refreshRow() throws DbException;

    public void updateRow() throws DbException;

	public void insertRow() throws DbException;

    public void moveToInsertRow() throws DbException;

//    public void insert(Object[] values) throws DbException;

    /**
     * Aktuellen Record löschen. Ändert nicht die Position innerhalb des
     * Result Sets. Stattdessen wird einfach nur "deleted" auf true gesetzt.
     */
    public void deleteRow() throws DbException;

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

    public boolean isLast();

	public boolean isAfterLast();

    /**
     * Proceeds to the next element in this result set, returning its record ID.
     */
    public boolean next() throws DbException;
    
    /* Maps the given column name to a column index 
     * 
    public int findColumn(String name);*/
    

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
    public void close();

    /*
     * returns an int array containing the selected field
     * indices, or null, if all fields are selected 

    public int [] getSelectedFields (); */
}
