package org.kobjects.db.ram;

import java.util.*;
import java.io.*;
import org.kobjects.db.*;

public class RamRecord implements DbResultSet {

    protected Vector selection;

    /** current row in selection (first index is 1) */

    protected int currentRow = 0;
    /** 0 based index in table.records */

    protected int recordIndex; // index in records vector

    protected RamTable table;

    /** (complete) copy of current values */
    protected Object[] values;

    protected boolean modified;
    boolean deleted;
    int[] selectedFields;

    protected RamRecord(
        RamTable table,
        Vector selection,
        int[] selectedFields) {
        this.table = table;
        this.selection = selection;

        if (selectedFields == null) {
            selectedFields = new int[table.getFieldCount()];
            for (int i = 1; i <= table.getFieldCount(); i++)
                selectedFields[i - 1] = i;
        }

        System.out.println(
            "first selected field: " + selectedFields[0]);

        this.selectedFields = selectedFields;

        values = new Object[table.getPhysicalFieldCount()];
    }

    public void clear() {
        values = new Object[table.getPhysicalFieldCount()];
        modified = true;
    }

    public int findColumn(String name) {
        for (int i = 1; i <= getColumnCount(); i++) {
            if (getField(i).getName().equals(name))
                return i;
        }
        return -1;
    }

    // XXX does deleteRow move the cursor?!??!

    public void deleteAll() throws DbException {
        beforeFirst();
        while (next()) {
            deleteRow();
        }
    }

    protected Object getObjectImpl(int column) {
        //System.out.println ("column: "+column + " selectedFields[column-1]="+selectedFields[column-1]);

        return values[selectedFields[column - 1] - 1];
    }

    public Object getObject(int column) {

        Object value = getObjectImpl(column);

        return (value instanceof byte[])
            ? new ByteArrayInputStream((byte[]) value)
            : value;

        // provide always access to id field if available
    }

    public boolean getBoolean(int column) {
        Boolean b = (Boolean) getObject(column);
        return (b == null) ? false : b.booleanValue();
    }

    public DbField getField(int index) {
        return table.getField(selectedFields[index - 1]);
    }

    public int getColumnCount() {
        return selectedFields.length;
    }

    public long getSize(int column) {
        Object value = getObjectImpl(column);

        if (value == null)
            return -2;
        return (value instanceof byte[])
            ? ((byte[]) value).length
            : -1;
    }

    public int getInt(int column) {
        Integer i = (Integer) getObject(column);
        return (i == null) ? 0 : i.intValue();
    }

    public long getLong(int column) {
        Long l = (Long) getObject(column);
        return (l == null) ? 0 : l.longValue();
    }

    public int getRowCount() {
        return selection.size();
    }

    public int[] getSelectedFields() {
        return selectedFields;
    }

    public String getString(int column) {
        Object o = getObject(column);
        return (o == null) ? null : o.toString();
    }

    public InputStream getBinaryStream(int column) {
        return (InputStream) getObject(column);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void updateBoolean(int column, boolean value) {
        updateObject(column, new Boolean(value));
    }

    public void updateInteger(int column, int value) {
        updateObject(column, new Integer(value));
    }

    public void updateLong(int column, long value) {
        updateObject(column, new Long(value));
    }

    public void updateObject(int column, Object value) {
        if (value instanceof InputStream) {
            try {
                InputStream is = (InputStream) value;
                ByteArrayOutputStream bos =
                    new ByteArrayOutputStream();
                byte[] buf = new byte[128];
                while (true) {
                    int cnt = is.read(buf);
                    if (cnt == -1)
                        break;
                    bos.write(buf, 0, cnt);
                }
                value = bos.toByteArray();
            }
            catch (IOException e) {
                throw new RuntimeException(e.toString());
            }
        }
        if (column < 1 || column > selectedFields.length) 
        	throw new IndexOutOfBoundsException ("colum "+column+ " out of range 1.."+selectedFields.length);
        values[selectedFields[column - 1] - 1] = value;
        modified = true;
    }

    public void updateString(int column, String value) {
        updateObject(column, value);
    }

    public void updateBinaryStream(
        int column,
        InputStream value) {
        //byte[] bytes = new byte[value.length];
        //System.arraycopy(value, 0, bytes, 0, value.length);

        updateObject(column, value); // was: bytes
    }

    public void moveToInsertRow() throws DbException {
        modified = true;
        recordIndex = RamTable.INSERT_ROW;

        int cnt = table.getPhysicalFieldCount();
        for (int i = 0; i < cnt; i++) {
            values[i] = table.getField(i).getDefault();
        }
    }
    /*
    	public void insert(Object[] values) throws DbException {
    		moveToInsertRow();
    
    		for (int i = 0; i < values.length; i++) {
    			updateObject(i, values[i]);
    		}
    	}
    */
    public boolean isModified() {
        return modified;
    }

    public boolean absolute(int position) throws DbException {
        beforeFirst();
        for (int i = 0; i < position; i++)
            if (!next())
                return false;

        return true;
    }

    public void refreshRow() {
        recordIndex =
            ((Integer) selection.elementAt(currentRow - 1))
                .intValue();
        Object[] content =
            (Object[]) table.records.elementAt(recordIndex);

        deleted = content == null;

        for (int i = 0; i < content.length; i++) {
            //	System.out.println("values["+i+"]:"+content[i]);
            values[i] = deleted ? null : content[i];
        }

        modified = false;
    }

    public void insertRow() throws DbException {
        if (recordIndex != RamTable.INSERT_ROW)
            throw new DbException("Not on Insert Row");

        table.update(recordIndex, deleted ? null : values);
    }

    public void updateRow() throws DbException {
        if (recordIndex == RamTable.INSERT_ROW)
            throw new DbException("use insertRow for inserting records");

        table.update(recordIndex, deleted ? null : values);
    }

    public void deleteRow() {
        deleted = true;
    }

    public int getRow() {
        return currentRow;
    }

    public DbTable getTable() {
        return table;
    }

    public boolean isAfterLast() {
        return currentRow > selection.size();
    }

    public boolean isLast() {
        return currentRow == selection.size();
    }

    public boolean next() throws DbException {
        if (isAfterLast())
            return false;
        currentRow++; // if on last, just go to afterlast
        if (isAfterLast())
            return false;
        recordIndex =
            ((Integer) selection.elementAt(currentRow - 1))
                .intValue();
        refreshRow();
        return true;
    }

    /** Places the cursor before the first record */

    public void beforeFirst() throws DbException {
        currentRow = 0;
    }

    /** Dispose does not need to do much in the case of 
    ramtable */

    public void close() {
        //throw new RuntimeException ("NYI");
    }
}