package org.kobjects.db.swing;

import javax.swing.table.*;

import org.kobjects.db.*;
import org.kobjects.db.statistics.*;

public class DbMetaTableModel extends AbstractTableModel {

    DbTable table;
    FieldStatistics [] stats;

    public DbMetaTableModel (DbTable table, FieldStatistics [] stats) {
	this.table = table;
	this.stats = stats;
    }


    public int getRowCount () {
	//	try {
	return table.getColumnCount ();
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
	    DbColumn f = table.getColumn (row+1);
	    FieldStatistics fs = stats != null ? stats [row] : null;
	    switch (column) {
	    case 0: return ""+f.getNumber ();
	    case 1: return f.getName ();
	    case 2: 
		switch (f.getType ()) {
		case DbColumn.BOOLEAN: return "boolean";
		case DbColumn.STRING: return "string";
		case DbColumn.INTEGER: return "integer";
		case DbColumn.LONG: return "long";
		case DbColumn.BITSET: return "bitset";
		case DbColumn.DATETIME: return "datetime";
		case DbColumn.BINARY: return "binary";
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

