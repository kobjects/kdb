package org.kobjects.db.swing;

import java.util.Date;
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

    public Class getColumnClass (int column) {
	switch (record.getTable ().getField (column).getType ()) {
	case DbField.STRING: return String.class;
	case DbField.INTEGER: return Integer.class;
	case DbField.LONG: return Long.class;
	case DbField.BOOLEAN: return Boolean.class;
	case DbField.GRAPHICS: 
	case DbField.BINARY: return byte[].class;
	case DbField.BITSET: return Integer.class;
	case DbField.DATETIME: return Date.class;
	default: return Object.class;
	}
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

    public boolean isCellEditable (int row, int column) {
	return true;
    }

    public void setValueAt (Object value, int row, int column) {
	try {
	    record.absolute (row+1);
	    record.setObject (column, value);
	    record.update ();
	}
	catch (DbException e) {
	    throw ChainedRuntimeException.create (e, null);
	}
    }
}
