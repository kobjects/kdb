package org.kobjects.db.ram;

import java.util.*;
import org.kobjects.db.*;

public class RamTable implements DbTable {

    // deleted records are marked by null values

	public static final int INSERT_ROW = -2;

    protected Vector fields = new Vector();
    protected Vector records = new Vector();
    protected Hashtable index;
    protected boolean open;
    protected boolean exists;
    protected boolean modified;
    protected int idField = -1;

    public void connect(String connector) throws DbException {
    }

    public int findField(String name) {
        int cnt = getFieldCount();
        for (int i = 0; i < cnt; i++)
            if (getField(i).getName().equals(name))
                return i;

        return -1;
    }

    public DbField addField(String name, int type) {
        int i = findField(name);
        if (i != -1)
            return getField(i);

        DbField f = new DbField(this, fields.size(), name, type);
        fields.addElement(f);
        return f;
    }

    public boolean isOpen() {
        return open;
    }

    protected void checkOpen(boolean required)
        throws DbException {
        if (open != required)
            throw new DbException(
                "DB must "
                    + (required ? "" : "not ")
                    + "be open");
    }

    public String getName() {
        return "RamTable";
    }

    public void open() throws DbException {
        if (!exists)
            throw new DbException("Db does not exist!");
        checkOpen(false);
        open = true;
    }

    public void create() throws DbException {
        checkOpen(false);
        exists = true;
    }

    public void close() throws DbException {
        open = false;
        records = null;
    }

    public void delete() throws DbException {
        close();
    }

    public boolean exists() {
        return !open;
    }

    public int getIdField() {
        return idField;
    }

    public DbField getField(int index) {
        return (DbField) fields.elementAt(index);
    }

    public int getFieldCount() {
        return fields.size();
    }
    
    
    /** 
     * Stores a *copy* of the given record in the records vector 
     * a copy is generated because the record hands over
     * its internal buffer in order to allow id genertation etc. */
    
    protected void update(int i, Object[] entry) throws DbException {
    	
    	Object[] cpy = null;
    	
    	if (entry != null) {
    		cpy = new Object[entry.length];
    		System.arraycopy (entry, 0, cpy, 0, entry.length);	
    	}
    	
    	if (i == INSERT_ROW) {
    		
    		if (idField != -1) 
	    		index.put (entry[idField], new Integer(records.size()));
	    		
    		records.addElement(cpy);
    	}
    	else		
	        records.setElementAt(cpy, i);

        modified = true;    		
    }

    public DbRecord select(boolean updated) throws DbException {
        return select(null, null, -1, false, updated);
    }

    public DbRecord select(
        int[] fields,
        DbCondition condition,
        int sortfield,
        boolean inverse,
        boolean updated)
        throws DbException {

        checkOpen(true);

        Vector selected = new Vector();

        for (int i = 0; i < records.size(); i++) {

            Object[] r = (Object[]) records.elementAt(i);

            if (r != null
                && (condition == null || condition.evaluate(r)))
                selected.addElement(new Integer(i));
        }

        if (sortfield != -1 || updated)
            throw new RuntimeException("NYI");

        return new RamRecord(this, selected, fields);
    }

    public void setIdField(int idField) throws DbException {

        checkOpen(false);
        this.idField = idField;
        index = new Hashtable();
    }

}