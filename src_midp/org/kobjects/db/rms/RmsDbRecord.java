package org.kobjects.db.rms;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import javax.microedition.rms.*;
import org.kobjects.db.*;

class RmsDbRecord extends DbRecord {

    private RmsDbTable table;

    private RecordEnumeration enum;

    private int id;

    private Object[] values;

    private boolean modified;

    RmsDbRecord(RmsDbTable table, RecordEnumeration enum) {
        this.table = table;
        this.enum = enum;
        values = new Object[table.getFieldCount()];
    }

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

    public void refresh() throws DbException {
        values = new Object[table.getFieldCount()];
        table.loadRecord(id, values);
        modified = false;
    }

    public void update() throws DbException {
        id = table.saveRecord(id, values);
        modified = false;
    }

    public void insert() throws DbException {
        id = 0;
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

    public void delete() throws DbException {
        table.saveRecord(id, null);
    }

    public void select(int id) throws DbException {
        if (id < 3) {
            throw new IllegalArgumentException("Illegal record ID #" + id);
        }

        this.id = id;
        refresh();
    }

    public boolean isModified() {
        return modified;
    }

    public DbTable getTable() {
        return table;
    }

    public Object getId() {
        return new Integer (id);
    }

    public void reset() {
        enum.reset();
    }

    // renamed
    public boolean hasNext() {
        return enum.hasNextElement();
    }

    public void next() throws DbException {
        try {
            select(enum.nextRecordId());
        }
        catch (InvalidRecordIDException e) {
            throw new DbException("No more records");
        }
    }

    public void absolute(int position) throws DbException {
        try {
            enum.reset();
            while (position > 0) {
                int id = enum.nextRecordId();
            }
            select(id);
        }
        catch (InvalidRecordIDException e) {
            throw new DbException("Invalid record position " + position);
        }
    }

    public void destroy() {
        enum.destroy();
        id = 0;
        table = null;
        values = null;
    }
}
