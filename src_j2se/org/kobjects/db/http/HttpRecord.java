package org.kobjects.db.http;

import java.util.Vector;
import java.io.*;
import java.net.*;
import org.kobjects.db.*;
import org.kobjects.util.*;

public class HttpRecord implements DbResultSet {

    Vector selection;
    int current = -1;
    HttpTableSE table;

    Object[] values;
    boolean modified;
    boolean deleted;
    int[] fields;

    HttpRecord(HttpTableSE table, int[] fields, Vector selection) {
        this.table = table;
        
        if (fields == null) {
            fields = new int[table.getFieldCount()];
            for (int i = 0; i < fields.length; i++) {
                fields[i] = i+1;
            }
        }
        
        this.fields = fields;
        this.selection = selection;
        values = new Object[fields.length];
    }

    public void clear() {
        values = new Object[fields.length];
        modified = true;
    }

    public void deleteAll() throws DbException {
        beforeFirst();
        while (next()) {
            deleteRow();
        }
    }

    
    public int findColumn(String name) {
        for (int i = 1; i <= getColumnCount(); i++)
            if (getField(i).getName().equals (name))
                return i;
                
        return -1;
    }


    public Object getObject(int column) {
        return values[column-1];
    }

    public boolean getBoolean(int column) {
        Boolean b = (Boolean) getObject(column);
        return (b == null) ? false : b.booleanValue();
    }
    
    
    public DbField getField(int column) {
        return table.getField (fields[column-1]);
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

    public String getString(int column) {
        Object o = getObject(column);
        return (o == null) ? null : o.toString();
    }

	

    public InputStream getBinaryStream(int column) {
        throw new RuntimeException ("NI");
//        return (InputStream) getBinaryStream (column);
    }

	public long getSize(int column) {
		return -1;
	}
    
    public int getColumnCount() {
        return fields.length;
    }
    
    
 

/*    public int[] getSelectedFields() {
        return fields;
    }*/


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
        values[column-1] = value;
        modified = true;
    }

    public void updateString(int column, String value) {
        updateObject(column, value);
    }

    public void updateBinaryStream(int column, InputStream value) {
        //byte[] bytes = new byte[value.length];
        //System.arraycopy(value, 0, bytes, 0, value.length);
        updateObject(column, value); // was: bytes
    }

    public void insertRow() {
        throw new RuntimeException("NYI");
    }

    public void moveToInsertRow() throws DbException {
        throw new RuntimeException("NYI");
        /*
        		modified = true;
        
        		int cnt = table.getFieldCount();
        		for (int i = 0; i < cnt; i++) {
        			values[i] = table.getField(i).getDefault();
        		}*/
    }


    public boolean isModified() {
        return modified;
    }


    public void absolute(int position) throws DbException {
        beforeFirst();
        for (int i = 0; i < position; i++)
            next();
    }

    public void refreshRow() {
        Object[] content = (Object[]) selection.elementAt(current);
        for (int i = 0; i < content.length; i++)
            values[i] = content[i];

        modified = false;
    }

    public void updateRow() throws DbException {
        if (!modified)
            return;

        Object[] content = (Object[]) selection.elementAt(current);

        try {
            StringBuffer url = new StringBuffer(table.url);
            url.append(table.conjunction);
            url.append("cmd=update&where=id=");
            url.append(values[table.idField]);

            HttpURLConnection connection =
                (HttpURLConnection) new URL(url.toString ())
                    .openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            BufferedWriter w = new BufferedWriter (new OutputStreamWriter (connection.getOutputStream()));
            
            for (int i = 0; i < content.length; i++) {
                if ((content[i] != values[i])
                    && (content[i] == null || !content[i].equals(values[i]))) {
                    w.write (table.getField(i).getName()+"=");
                    if (values[i] != null) 
                    	w.write(Csv.encode(values[i].toString(), (char)0));
                    w.write ("\r\n");
                    content[i] = values[i];
                }
            }
            w.write ("\r\n");
            w.write ("\r\n");
            w.flush();   
            if (connection.getResponseCode() != 200) 
                throw new IOException ("bad response code: "+connection.getResponseCode());         
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new DbException(e.toString());
        }

    }

    public void deleteRow() {
        throw new RuntimeException("NYI");
    }

    /*public Object getId() {
    	return (table.getId(values));
    }*/

    public int getRow() {
        return current + 1;
    }

    public DbTable getTable() {
        return table;
    }

    public boolean isAfterLast() {
        return current >= selection.size();
    }
    
    public boolean isLast() {
        return current == selection.size() -1;
    }
    
    public boolean next() throws DbException {
        if (isAfterLast()) return false;
        current++;
        if (isAfterLast()) return false;
        
        refreshRow();
        return true;
    }

    /** Places the cursor before the first record */

    public void beforeFirst() throws DbException {
        current = -1;
    }

    public void close() {
        //throw new RuntimeException ("NYI");
    }
}
