package org.kobjects.db.ram;

import java.util.*;
import org.kobjects.db.*;



public class RamTable extends DbTable {

    
    protected Vector fields;
    protected Vector records;
    protected Hashtable index;
    protected boolean open;
    protected boolean exists;

    public void init (String connector) {
        //if (name != null) 
        //   throw new DbException ("connector must be null!");
    }


    public DbField addField (String name, int type) {
        DbField f = new DbField (fields.size (), name, type);
        fields.addElement (f);
        return f;
    }


    public void open () throws DbException {
        open = true;
    }


    public void create () {
        exists = true;
    }


    public void close () {
        open = false;
        records = null;
        index = null;
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
        throw new RuntimeException ("NYI");
    }

    public DbRecord select (DbCondition condition, int sortfield, boolean inverse, boolean updated) {
        throw new RuntimeException ("NYI");
    }
}


