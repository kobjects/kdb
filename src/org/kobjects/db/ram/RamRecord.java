package org.kobjects.db.ram;

import java.util.*;
import org.kobjects.db.*;

class RamRecord extends DbRecord {

    Vector selection;
    int index = -1;
    int id;  // id in records vector
    RamTable table;


    RamRecord (RamTable table, int id) {
	this.table = table;
	this.id = id;
	refresh ();
    }
    

    RamRecord (RamTable table, Vector selection) {
	this.table = table;
        this.selection = selection;
    }


    public void refresh () {
	Object [] content = (Object []) table.records.elementAt (id);
	for (int i = 0; i < content.length; i++) 
	    values [i] = content [i];

        modified = false;
    }
    
    
    public void update () {
	Object [] content = new Object [values.length];
	for (int i = 0; i < values.length; i++) 
	    content [i] = values [i];

	table.records.setElementAt (content, id);
    }


    public void delete () {
	throw new RuntimeException ("NYI");
    }


    public Object getId () {
	return new Integer (id);
    }


    public DbTable getTable () {
	return table;
    }


    public boolean hasNext () {
        return selection != null && index < selection.size ()-1;
    }


    public void next () throws DbException {
	if (!hasNext ()) throw new DbException ("no next available!"); 
	id = ((Integer) selection.elementAt (++index)).intValue ();
	refresh ();
    }


    /** Places the cursor before the first record */

    public void reset () throws DbException {
	if (selection == null) throw new DbException ("no cursor!");
	if (selection != null) index = -1;
    }


    public void destroy () {
        throw new RuntimeException ("NYI");
    }
}



