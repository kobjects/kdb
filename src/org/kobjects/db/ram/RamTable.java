package org.kobjects.db.ram;

import java.util.*;
import org.kobjects.db.*;



public class RamTable implements DbTable {


    // deleted records are marked by null values
    
    protected Vector fields = new Vector ();
    protected Vector records = new Vector ();
    protected Hashtable index;
    protected boolean open;
    protected boolean exists;

    protected int [] idFields;


    public void connect (String connector) {
        //if (name != null) 
        //   throw new DbException ("connector must be null!");
    }


    public int findField(String name) {
        int cnt = getFieldCount ();
        for (int i = 0; i < cnt; i++) 
            if (getField (i).getName ().equals (name)) 
                return i;

        return -1;
    }


    public DbField addField (String name, int type) {
        DbField f = new DbField (this, fields.size (), name, type);
        fields.addElement (f);
        return f;
    }


    public boolean isOpen () {
        return open;
    }


    protected void checkOpen (boolean required) throws DbException {
	if (open != required) throw new DbException 
	    ("DB must "+(required ? "" : "not ")+"be open");
    }


    protected Object getId (int index) {
	if (idFields == null) return new Integer (index);
	Object [] record = (Object[]) records.elementAt (index); 
	StringBuffer buf = new StringBuffer ();
	for (int i = 0; i < idFields.length; i++) 
	    buf.append (record [idFields [i]].toString ());
	
	return buf.toString ();
    }


    public String getName () {
        return "RamTable";
    }


    public void open () throws DbException {
	checkOpen (false);
        open = true;
    }


    public void create () throws DbException {
	checkOpen (false);
        exists = true;
    }


    public void close () {
        open = false;
        records = null;
    }


    public void delete () {
        close ();
    }

    
    public void deleteRecord (Object id) throws DbException {
        select (id).delete ();
    }

    
    public boolean exists () {
        return !open;
    }


    public DbField getField (int index) {
        return (DbField) fields.elementAt (index);
    }
    

    public int getFieldCount () {
        return fields.size ();
    }


    public DbRecord select (Object id) {
	Vector selected = new Vector ();
	
	if (idFields == null) 
	    selected.addElement (idFields == null ? id : index.get (id));

	return new RamRecord (this, selected);
    }


    public DbRecord select (boolean updated) throws DbException {
        return select (null, -1, false, updated);
    }


    public DbRecord select (DbCondition condition, int sortfield, boolean inverse, boolean updated) throws DbException {

	checkOpen (true);

	Vector selected = new Vector ();

	for (int i = 0; i < records.size (); i++) {

	    Object [] r = (Object []) records.elementAt (i);

	    if (r != null 
                && (condition == null || condition.evaluate (getId (i), r))) 
		selected.addElement (new Integer (i));
	}

	if (sortfield != -1 || updated) 
	    throw new RuntimeException ("NYI");

	return new RamRecord (this, selected);
    }

    
    public void setIdFields (int [] idFields) throws DbException {
	
	checkOpen (false);
	this.idFields = idFields;
	index = new Hashtable ();
    }

}




