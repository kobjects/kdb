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
import javax.microedition.rms.*;

public abstract class DbRecord {

    private DbTable table;
    private Object[] values;
    private boolean modified;


    public void clear() {
        values = new Object[table.getFieldCount()];
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

        for (int i = 0; i < table.getFieldCount(); i++) {
            values[i] = table.getField(i).getDefault();
        }
    }


    public void insert(Object[] values) throws DbException {
        insert();

        for (int i = 0; i < values.length; i++) {
            setObject(i, values[i]);
        }
    }


    abstract public void delete() throws DbException;


    public boolean isModified() {
        return modified;
    }


    public DbTable getTable() {
        return table;
    }


    public abstract Object getId();


    public abstract boolean hasNext ();


    public abstract void next() throws DbException;


    public abstract void reset (); 


    public void absolute(int position) throws DbException {
	reset ();
	for (int i = 0; i < position; i++) 
	    next ();
    }


    public abstract void destroy();
}
