package org.kobjects.db.jdbc;

import org.kobjects.db.*;
import org.kobjects.util.*;
import java.sql.*;

class JdbcRecord implements DbRecord {

	JdbcTable table; 
	ResultSet resultSet;
	int [] fields;
	int [] fieldMap;
	Object [] current;
	boolean empty;

	// !!!!!!!!! Need a "current" Object array because of getRowCount issue

    /**
     * Constructor for JdbcRecord.
     */
    public JdbcRecord(JdbcTable table, int [] fields, ResultSet resultSet) throws SQLException {
		this.table = table;
		this.fields = fields;
		this.resultSet = resultSet;

		empty = !resultSet.isBeforeFirst ();
		current = new Object [table.getFieldCount ()];

		fieldMap = new int [table.getFieldCount ()]; 
		if (fields == null) {
			for (int i = 0; i < fieldMap.length; i++) {
				fieldMap [i] = i+1;
			}
		}
		else {
			for (int i = 0; i < fields.length; i++) {
				fieldMap [fields [i]] = i+1;
			}
		}
    }

    /**
     * @see DbRecord#getObject(int)
     */
    public Object getObject(int column) {
		return current [column];
    }

    /**
     * @see DbRecord#setObject(int, Object)
     */
    public void setObject(int column, Object value) {
		throw new RuntimeException ("NYI");
    }

    /**
     * @see DbRecord#getBoolean(int)
     */
    public boolean getBoolean(int column) {
		return ((Boolean) getObject (column)).booleanValue();
    }

    /**
     * @see DbRecord#getInteger(int)
     */
    public int getInteger(int column) {
		return ((Integer) getObject (column)).intValue();
    }

    /**
     * @see DbRecord#getLong(int)
     */
    public long getLong(int column) {
		return ((Long) getObject (column)).longValue();
    }

    /**
     * @see DbRecord#getString(int)
     */
    public String getString(int column) {
		Object o = getObject (column);
		return (o == null) ? null : o.toString ();
    }

    /**
     * @see DbRecord#getBinary(int)
     */
    public byte[] getBinary(int column) {
		return (byte[]) getObject (column);	
    }

    /**
     * @see DbRecord#setBoolean(int, boolean)
     */
    public void setBoolean(int column, boolean value) {
		setObject (column, new Boolean (value));
    }

    /**
     * @see DbRecord#setInteger(int, int)
     */
    public void setInteger(int column, int value) {
		setObject (column, new Integer (value));    	
    }

    /**
     * @see DbRecord#setLong(int, long)
     */
    public void setLong(int column, long value) {
		setObject (column, new Long (value));
    }

    /**
     * @see DbRecord#setString(int, String)
     */
    public void setString(int column, String value) {
		setObject (column, value == null ? null : value.toString ());
    }

    /**
     * @see DbRecord#setBinary(int, byte[])
     */
    public void setBinary(int column, byte[] value) {
		setObject (column, value);
    }

	Object getObj (int column) throws SQLException {
		int dbc = fieldMap [column];
		switch (table.getField (column).getType ()) {
			
			case DbField.STRING: return resultSet.getString (dbc);
			case DbField.DOUBLE: return new Double (resultSet.getDouble(dbc));
			case DbField.INTEGER: return new Integer (resultSet.getInt (dbc));
			case DbField.LONG: return new Long (resultSet.getLong(dbc)); 
			case DbField.DATETIME: return resultSet.getDate(dbc);
			default: 
				System.err.println ("returning null for NYI type: "
					+table.getField (column).getType ());
				return null;
		}
			
	}


    /**
     * @see DbRecord#refresh()
     */
    public void refresh() throws DbException {
		try {
			for (int i = 0; i < table.getFieldCount (); i++) 
				current [i] = getObj (i);
		}
		catch (SQLException e) {
			throw new DbException (""+e);
		}
    }

    /**
     * @see DbRecord#update()
     */
    public void update() throws DbException {
		throw new RuntimeException ("NYI");
    }

    /**
     * @see DbRecord#insert()
     */
    public void insert() throws DbException {
		throw new RuntimeException ("NYI");
    }

    /**
     * @see DbRecord#insert(Object[])
     */
    public void insert(Object[] values) throws DbException {
		throw new RuntimeException ("NYI");
    }

    /**
     * @see DbRecord#delete()
     */
    public void delete() throws DbException {
		throw new RuntimeException ("NYI");
    }

    /**
     * @see DbRecord#deleteAll()
     */
    public void deleteAll() throws DbException {
		throw new RuntimeException ("NYI");
    }

    /**
     * @see DbRecord#isModified()
     */
    public boolean isModified() {
		throw new RuntimeException ("NYI");
    }

    /**
     * @see DbRecord#isDeleted()
     */
    public boolean isDeleted() {
		throw new RuntimeException ("NYI");
    }

    /**
     * @see DbRecord#getTable()
     */
    public DbTable getTable() {
        return table;
    }

    /**
     * @see DbRecord#getId()
     */
    public Object getId() {
		throw new RuntimeException ("NYI");
    }

    /**
     * @see DbRecord#beforeFirst()
     */
    public void beforeFirst() throws DbException {
		try {	
			resultSet.beforeFirst ();
		}
		catch (SQLException e) {
			throw new DbException (""+e);
		}
    }

    /**
     * @see DbRecord#hasNext()
     */
    public boolean hasNext() {
		try {
			return !(empty || resultSet.isLast () || resultSet.isAfterLast ());
		}
		catch (SQLException e) {
			throw ChainedRuntimeException.create (e, null);
		}
    }

    /**
     * @see DbRecord#next()
     */
    public void next() throws DbException {
		try {
			if (!resultSet.next())
				throw new DbException ("Read past eof");

			refresh ();
		}
		catch (SQLException e) {
			throw new DbException (""+e);
		}
    }

    /**
     * @see DbRecord#getRowCount()
     */
    public int getRowCount() {
		try {
			int save = resultSet.getRow();
			resultSet.absolute (-1);
			int result = resultSet.getRow ();
			if (save == 0) resultSet.beforeFirst ();
			else resultSet.absolute (save);
			return result;
		}
		catch (SQLException e) {
			throw ChainedRuntimeException.create (e, null);
		}		
    }

    /**
     * @see DbRecord#getRow()
     */
    public int getRow() {
		try {
			return resultSet.getRow ();
		}
		catch (SQLException e) {
			throw ChainedRuntimeException.create (e, null);
		}
    }

    /**
     * @see DbRecord#absolute(int)
     */
    public void absolute(int row) throws DbException {
		try {
			resultSet.absolute (row);
			refresh ();
		}
		catch (SQLException e) {
			throw new DbException ("" + e);
		}
    }

    /**
     * @see DbRecord#dispose()
     */
    public void dispose() {
    	try {
			resultSet.close();
    	}
    	catch (SQLException e) {
    		throw new RuntimeException (""+e);
    	}
    }

    /**
     * @see DbRecord#getSelectedFields()
     */
    public int[] getSelectedFields() {
		return fields;
    }

}
