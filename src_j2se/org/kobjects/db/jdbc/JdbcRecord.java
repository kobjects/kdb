package org.kobjects.db.jdbc;

import java.io.*;
import java.sql.*;
import org.kobjects.db.*;
import org.kobjects.util.*;

class JdbcRecord implements DbResultSet {

	JdbcTable table; 
	Statement statement;
	ResultSet resultSet;
	
	/** list of selected db field indices */
	
	int [] fields;

	/** map from db field indices to Jdbc result set field indices */

	int [] fieldMap;
	Object [] current;
	boolean empty;

	// !!!!!!!!! Need a "current" Object array because of getRowCount issue

    /**
     * Constructor for JdbcRecord.
     */
    public JdbcRecord(JdbcTable table, int [] fields, String query) throws SQLException {
		this.table = table;
		this.fields = fields;

		statement = table.connection
                    .createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                        
	    resultSet = statement.executeQuery(query);

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
    public void updateObject(int column, Object value) {
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
    public int getInt(int column) {
		return ((Integer) getObject (column)).intValue();
    }

    /**
     * @see DbRecord#getLong(int)
     */
    public long getLong(int column) {
		return ((Long) getObject (column)).longValue();
    }


	public long getSize(int column) {
		return -1;
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
    public InputStream getBinaryStream(int column) {
		return (InputStream) getObject (column);	
    }

    /**
     * @see DbRecord#setBoolean(int, boolean)
     */
    public void updateBoolean(int column, boolean value) {
		updateObject (column, new Boolean (value));
    }

    /**
     * @see DbRecord#setInteger(int, int)
     */
    public void updateInteger(int column, int value) {
		updateObject (column, new Integer (value));    	
    }

    /**
     * @see DbRecord#setLong(int, long)
     */
    public void updateLong(int column, long value) {
		updateObject (column, new Long (value));
    }

    /**
     * @see DbRecord#setString(int, String)
     */
    public void updateString(int column, String value) {
		updateObject (column, value == null ? null : value.toString ());
    }

    /**
     * @see DbRecord#setBinary(int, byte[])
     */
    public void updateBinaryStream(int column, InputStream value) {
		updateObject (column, value);
    }

	Object getObj (int column) throws SQLException {
		int dbc = fieldMap [column];
		if (dbc == 0) {
			//System.out.println ("fieldMap ["+column+"] = null!!!");
			return null;
		}
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
    public void refreshRow() throws DbException {
		try {
			for (int i = 0; i < fieldMap.length; i++) {
				current [i] = getObj (i);
				//System.out.println ("current ["+i+"]="+current [i]);
			}
		}
		catch (SQLException e) {
			throw new DbException (""+e);
		}
    }

    /**
     * @see DbRecord#update()
     */
    public void updateRow() throws DbException {
		throw new RuntimeException ("NYI");
    }

    /**
     * @see DbRecord#insert()
     */
    public void moveToInsertRow() throws DbException {
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
    public void deleteRow() throws DbException {
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

			refreshRow ();
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
			refreshRow ();
		}
		catch (SQLException e) {
			throw new DbException ("" + e);
		}
    }

    /**
     * @see DbRecord#dispose()
     */
    public void close() {
    	try {
			resultSet.close();
			statement.close ();
			resultSet = null;
			statement = null;
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
