package org.kobjects.db.ram;

import java.util.*;
import org.kobjects.db.*;

class RamRecord extends DbRecord {

    Vector selection;
    int current = -1;
    int index;  // index in records vector
    RamTable table;


    RamRecord (RamTable table, Vector selection) {
	this.table = table;
        this.selection = selection;
    }


    public void refresh () {
	Object [] content = (Object []) table.records.elementAt (index);
	for (int i = 0; i < content.length; i++) 
	    values [i] = content [i];

        modified = false;
    }
    
    
    public void update () {
	Object [] content = new Object [values.length];
	for (int i = 0; i < values.length; i++) 
	    content [i] = values [i];

	table.records.setElementAt (content, index);
    }


    public void delete () {
	throw new RuntimeException ("NYI");
    }


    public Object getId () {
	return (table.getId (index));
    }


    public DbTable getTable () {
	return table;
    }


    public boolean hasNext () {
        return selection != null && current < selection.size ()-1;
    }


    public void next () throws DbException {
	if (!hasNext ()) throw new DbException ("no next available!"); 
	index = ((Integer) selection.elementAt (++current)).intValue ();
	refresh ();
    }


    /** Places the cursor before the first record */

    public void reset () throws DbException {
	if (selection == null) throw new DbException ("no cursor!");
	if (selection != null) current = -1;
    }


    public void destroy () {
        throw new RuntimeException ("NYI");
    }
}



