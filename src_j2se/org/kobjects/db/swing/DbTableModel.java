package org.kobjects.db.swing;

import javax.swing.table.*;
import org.kobjects.db.*;
import org.kobjects.util.*;

public class DbTableModel extends AbstractTableModel {

    DbRecord record;

    public DbTableModel (DbRecord record) {
	this.record = record;
    }

    public int getRowCount () {
	//	try {
	return record.getRowCount ();
	//	}
	//catch (DbException e) {
	//    throw ChainedRuntimeException.create (e, null);
	//}

    }

    public int getColumnCount () {
	//	try {
	return record.getTable ().getFieldCount ();
	//}
	//catch (DbException e) {
	//    throw ChainedRuntimeException.create (e, null);
	//}
    }

    public String getColumnName (int column) {
	return record.getTable ().getField (column).getName ();
    }

    public Object getValueAt (int row, int column) {
	try {
	    record.absolute (row+1);
	    return record.getObject (column);
	}
	catch (DbException e) {
	    throw ChainedRuntimeException.create (e, null);
	}
    }
    
}
