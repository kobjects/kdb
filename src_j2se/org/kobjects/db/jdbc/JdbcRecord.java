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

    int[] fields;
    int rowCount;

    /** map from db field indices to Jdbc result set field indices */

    /**
     * Constructor for JdbcRecord.
     */
    public JdbcRecord(
        JdbcTable table,
        int[] fields,
        String query)
        throws SQLException {
        this.table = table;
        this.fields = fields;

        int fromIndex = query.indexOf("from");
        Statement sc = table.connection.createStatement();
        ResultSet rc =
            sc.executeQuery(
                "SELECT COUNT(*) AS rowcount "
                    + query.substring(fromIndex));

        rc.next();
        rowCount = rc.getInt("rowcount");
        rc.close();
        sc.close();

        statement =
            table.connection.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);

        resultSet = statement.executeQuery(query);
    }

    public int findColumn(String name) {
        try {
            return resultSet.findColumn(name);
        }
        catch (SQLException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public int getColumnCount() {
        return fields.length;
    }

    public int getRowCount() {
        return rowCount;
    }

    public DbField getField(int column) {
        return table.getField(fields[column - 1]);
    }

    /**
     * @see DbRecord#getObject(int)
     */

    public Object getObject(int column) {
        try {
            int dbc = fields[column-1];

            switch (table.getField(column).getType()) {

                case DbField.STRING :
                    return resultSet.getString(column);
                case DbField.DOUBLE :
                    return new Double(
                        resultSet.getDouble(column));
                case DbField.INTEGER :
                    return new Integer(resultSet.getInt(column));
                case DbField.LONG :
                    return new Long(resultSet.getLong(column));
                case DbField.DATETIME :
                    return resultSet.getDate(column);
                default :
                    System.err.println(
                        "returning null for NYI type: "
                            + table.getField(column).getType());
                    return null;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * @see DbRecord#setObject(int, Object)
     */
    public void updateObject(int column, Object value) {
        try {
            resultSet.updateObject(column, value);
        }
        catch (SQLException e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * @see DbRecord#getBoolean(int)
     */
    public boolean getBoolean(int column) {
        try {
            return resultSet.getBoolean(column);
        }
        catch (SQLException e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * @see DbRecord#getInteger(int)
     */
    public int getInt(int column) {
        try {
            return resultSet.getInt(column);
        }
        catch (SQLException e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * @see DbRecord#getLong(int)
     */
    public long getLong(int column) {
        return ((Long) getObject(column)).longValue();
    }

    public long getSize(int column) {
        return -1;
    }

    /**
     * @see DbRecord#getString(int)
     */
    public String getString(int column) {
        Object o = getObject(column);
        return (o == null) ? null : o.toString();
    }

    /**
     * @see DbRecord#getBinary(int)
     */
    public InputStream getBinaryStream(int column) {
        try {
            return resultSet.getBinaryStream(column);
        }
        catch (SQLException e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * @see DbRecord#setBoolean(int, boolean)
     */
    public void updateBoolean(int column, boolean value) {
        updateObject(column, new Boolean(value));
    }

    /**
     * @see DbRecord#setInteger(int, int)
     */
    public void updateInteger(int column, int value) {
        updateObject(column, new Integer(value));
    }

    /**
     * @see DbRecord#setLong(int, long)
     */
    public void updateLong(int column, long value) {
        updateObject(column, new Long(value));
    }

    /**
     * @see DbRecord#setString(int, String)
     */
    public void updateString(int column, String value) {
        updateObject(
            column,
            value == null ? null : value.toString());
    }

    /**
     * @see DbRecord#setBinary(int, byte[])
     */
    public void updateBinaryStream(
        int column,
        InputStream value) {
        updateObject(column, value);
    }
    /*
    	Object getObj (int column) throws SQLException {
    		
    		switch (table.getField (column).getType ()) {
    			
    			case DbField.STRING: return resultSet.getString (column);
    			case DbField.DOUBLE: return new Double (resultSet.getDouble(column));
    			case DbField.INTEGER: return new Integer (resultSet.getInt (column));
    			case DbField.LONG: return new Long (resultSet.getLong(column)); 
    			case DbField.DATETIME: return resultSet.getDate(column);
    			default: 
    				System.err.println ("returning null for NYI type: "
    					+table.getField (column).getType ());
    				return null;
    		}			
    	}
    */

    /**
     * @see DbRecord#refresh()
     */
    public void refreshRow() throws DbException {
        try {
            resultSet.refreshRow();
        }
        catch (SQLException e) {
            throw new DbException("" + e);
        }
    }

    /**
     * @see DbRecord#update()
     */
    public void updateRow() throws DbException {
        try {
            resultSet.updateRow();
        }
        catch (SQLException e) {
            throw new DbException("" + e);
        }
    }

    /**
     * @see DbRecord#insert()
     */
    public void moveToInsertRow() throws DbException {
        throw new RuntimeException("NYI");
    }

    public void insertRow() throws DbException {
        throw new RuntimeException("NYI");
    }

    /**
     * @see DbRecord#delete()
     */
    public void deleteRow() throws DbException {
        throw new RuntimeException("NYI");
    }

    /**
     * @see DbRecord#deleteAll()
     */
    public void deleteAll() throws DbException {
        throw new RuntimeException("NYI");
    }

    /**
     * @see DbRecord#isModified()
     */
    public boolean isModified() {
        throw new RuntimeException("NYI");
    }

    /**
     * @see DbRecord#isDeleted()
     */
    public boolean isDeleted() {
        throw new RuntimeException("NYI");
    }

    /**
     * @see DbRecord#getTable()
     */
    public DbTable getTable() {
        return table;
    }

    /*
     * @see DbRecord#getId()
    public Object getId() {
    	throw new RuntimeException ("NYI");
    }
     */

    /**
     * @see DbRecord#beforeFirst()
     */
    public void beforeFirst() throws DbException {
        try {
            resultSet.beforeFirst();
        }
        catch (SQLException e) {
            throw new DbException("" + e);
        }
    }

    public boolean isLast() {
        try {
            return resultSet.isLast();
        }
        catch (SQLException e) {
            throw ChainedRuntimeException.create(e, null);
        }
    }

    public boolean isAfterLast() {
        try {
            return resultSet.isAfterLast();
        }
        catch (SQLException e) {
            throw ChainedRuntimeException.create(e, null);
        }
    }

    /**
     * @see DbRecord#next()
     */
    public boolean next() throws DbException {
        try {
            return resultSet.next();
        }
        catch (SQLException e) {
            throw new DbException(e.toString());
        }
    }

    /**
     * @see DbRecord#getRow()
     */
    public int getRow() {
        try {
            return resultSet.getRow();
        }
        catch (SQLException e) {
            throw ChainedRuntimeException.create(e, null);
        }
    }

    /**
     * @see DbRecord#absolute(int)
     */
    public boolean absolute(int row) throws DbException {
        try {
            return resultSet.absolute(row);
            //	refreshRow ();
        }
        catch (SQLException e) {
            throw new DbException("" + e);
        }
    }

    /**
     * @see DbRecord#dispose()
     */
    public void close() {
        try {
            resultSet.close();
            statement.close();
            resultSet = null;
            statement = null;
        }
        catch (SQLException e) {
            throw new RuntimeException("" + e);
        }
    }

    /**
     * @see DbRecord#getSelectedFields()
     */
    public int[] getSelectedFields() {
        return fields;
    }

}
