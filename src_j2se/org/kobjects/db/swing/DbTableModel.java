package org.kobjects.db.swing;

import java.util.Date;
import javax.swing.table.*;
import org.kobjects.db.*;
import org.kobjects.util.*;

public class DbTableModel extends AbstractTableModel {

    DbResultSet record;
  //  int[] indices;

    public DbTableModel(DbResultSet record) {
        this.record = record;
     //   indices = new int[record.getColumnCount()];

     //   for (int i = 0; i < indices.length; i++)
      //      indices[i] = record.getField(i+1).getNumber();
        
    }

    public int getRowCount() {
        //	try {
        return record.getRowCount();
        //	}
        //catch (DbException e) {
        //    throw ChainedRuntimeException.create (e, null);
        //}

    }

    public int getColumnCount() {
        //	try {
        return record.getColumnCount();

        //}
        //catch (DbException e) {
        //    throw ChainedRuntimeException.create (e, null);
        //}
    }

    public Class getColumnClass(int column) {
        switch (record.getField(column+1)
            .getType()) {
            case DbColumn.STRING :
                return String.class;
            case DbColumn.INTEGER :
                return Integer.class;
            case DbColumn.LONG :
                return Long.class;
            case DbColumn.BOOLEAN :
                return Boolean.class;
            case DbColumn.GRAPHICS :
            case DbColumn.BINARY :
                return byte[].class;
            case DbColumn.BITSET :
                return Integer.class;
            case DbColumn.DATETIME :
                return Date.class;
            default :
                return Object.class;
        }
    }

    public String getColumnName(int column) {
        return record
            .getField(column+1)
            .getName();
    }

    public Object getValueAt(int row, int column) {
        try {
            record.absolute(row + 1);
            return record.getObject(column+1);
        }
        catch (DbException e) {
            throw ChainedRuntimeException.create(e, null);
        }
    }

    public boolean isCellEditable(int row, int column) {
        return true;
    }

    public void setValueAt(Object value, int row, int column) {
        try {
            record.absolute(row + 1);
            record.updateObject(column+1, value);
            record.updateRow();
        }
        catch (DbException e) {
            throw ChainedRuntimeException.create(e, null);
        }
    }
}
