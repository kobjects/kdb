package org.kobjects.db.swing;

import javax.swing.table.*;

import org.kobjects.db.*;
import org.kobjects.db.statistics.*;
import org.kobjects.util.*;

public class DbMetaTableModel extends AbstractTableModel {

    DbTable table;
    FieldStatistics [] stats;

    public DbMetaTableModel (DbTable table, FieldStatistics [] stats) {
	this.table = table;
	this.stats = stats;
    }


    public int getRowCount () {
	//	try {
	return table.getFieldCount ();
	//	}
	//catch (DbException e) {
	//    throw ChainedRuntimeException.create (e, null);
	//}

    }

    public int getColumnCount () {
	//	try {
	return stats == null ? 6 : 13;
	//}
	//catch (DbException e) {
	//    throw ChainedRuntimeException.create (e, null);
	//}
    }

    public String getColumnName (int column) {
	switch (column) {
	case 0: return "nr";
	case 1: return "name";
	case 2: return "type";
	case 3: return "max size";
	case 4: return "constr.";
	case 5: return "values";

	case 6: return "count";
	case 7: return "numeric";
	case 8: return "dec";
	case 9: return "min";
	case 10: return "max";
	case 11: return "distinct";
	case 12: return "null";
	default: return "";
	}
    }

    public Object getValueAt (int row, int column) {
	//try {
	    DbField f = table.getField (row+1);
	    FieldStatistics fs = stats != null ? stats [row] : null;
	    switch (column) {
	    case 0: return ""+f.getNumber ();
	    case 1: return f.getName ();
	    case 2: 
		switch (f.getType ()) {
		case DbField.BOOLEAN: return "boolean";
		case DbField.STRING: return "string";
		case DbField.INTEGER: return "integer";
		case DbField.LONG: return "long";
		case DbField.BITSET: return "bitset";
		case DbField.DATETIME: return "datetime";
		case DbField.BINARY: return "binary";
		default: return "unknown/"+f.getType ();
		}
	    case 3: return ""+f.getMaxSize ();
	    case 4: return ""+f.getConstraints ();
	    case 5: return f.getValues () == null 
			? "-" : (""+f.getValues ().length);
	    
	    case 6: return ""+fs.count;
	    case 7: return ""+fs.numeric;
	    case 8: return ""+fs.decimals;
	    case 9: return ""+fs.min;
	    case 10: return ""+fs.max;
	    case 11: return fs.values != null ? (""+fs.values.size ()) : ">1000";
	    case 12: return ""+fs.nullCount;
	    default: return "";
	    }
	    //catch (DbException e) {
	    //throw ChainedRuntimeException.create (e, null);
	    //}
    }
    
}

