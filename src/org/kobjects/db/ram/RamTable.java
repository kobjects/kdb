package org.kobjects.db.ram;

import java.util.*;
import org.kobjects.db.*;

public class RamTable implements DbTable {

    // deleted records are marked by null values

	public static final int INSERT_ROW = -2;

    protected Vector fields = new Vector();
    protected Vector records = new Vector();
//    protected Hashtable index;
    protected boolean open;
    protected boolean exists;
    protected boolean modified;
//    protected int idField = -1;

    public void connect(String connector) throws DbException {
    }

    public int findColumn(String name) {
        int cnt = getColumnCount();
        for (int i = 1; i <= cnt; i++)
            if (getColumn(i).getName().equals(name))
                return i;

        return -1;
    }

    public DbColumn addField(String name, int type) {
        int i = findColumn(name);
        if (i > 0)
            return getColumn(i);

        DbColumn f = new DbColumn(this, fields.size()+1, name, type);
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
        return exists;
    }

    public int getIdField() {
        return -1;
    }

    public DbColumn getColumn(int index) {
        return (DbColumn) fields.elementAt(index-1);
    }

    public int getColumnCount() {
        return fields.size();
    }
    
    
    public int getPhysicalFieldCount() {
    	return getColumnCount();
    }
    
    /** 
     * Overwrites the fields of the existing object with contents of the given entry */
    
    protected void update(int recordIndex, Object[] entry) throws DbException {
    	
    	if (entry == null) {
    		records.setElementAt (null, recordIndex);
    		modified = true;
    		return;
    	}
    	
    	Object[] rec = recordIndex == INSERT_ROW ? new Object[entry.length] : ((Object []) records.elementAt(recordIndex));

		System.arraycopy (entry, 0, rec, 0, entry.length);
    	
    	if (recordIndex == INSERT_ROW) {
/*    		if (idField > 0) 
	    		index.put (entry[idField], new Integer(records.size()));
*/	    		
    		records.addElement(rec);
    	}

        modified = true;    		
    }

    public DbResultSet select(boolean updated) throws DbException {
        return select(null, null, null, false, updated);
    }

    public DbResultSet select(
        int[] fields,
        DbCondition condition,
        int[] sortfield,
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

        if (sortfield != null || updated)
            throw new RuntimeException("support for sorting and updated fields is NYI");

		return getRecords(selected, fields);
 //       return new RamRecord(this, selected, fields);
    }


	protected RamResultSet getRecords (Vector selected, int [] fields) {
			return new RamResultSet (this, selected, fields);
	}


/*
    public void setIdField(int idField) throws DbException {

        checkOpen(false);
        this.idField = idField;
        index = new Hashtable();
    }
*/

}