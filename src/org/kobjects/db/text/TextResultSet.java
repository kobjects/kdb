package org.kobjects.db.text;

import java.sql.SQLException;

import org.kobjects.db.*;



public abstract class TextResultSet extends AbstractResultSet {

	Object[] currentRow;
	Object[] writeRow;
	
	boolean wasNull;
	int row;
	boolean insertRow;
 	DbTable table;
 
    TextResultSet(DbTable table, int[] fields) {
        super(table, fields);    
        this.table = table;
    }


	public abstract Object[] nextRow();


	public abstract void appendRow(Object[] row);


    /**
     * @see java.sql.ResultSet#next()
     */
    public boolean next() throws SQLException {
		currentRow = nextRow();
        return currentRow != null;
    }
 
    /**
     * @see java.sql.ResultSet#wasNull()
     */
    public boolean wasNull() throws SQLException {
        return wasNull;
    }
    /**
     * @see java.sql.ResultSet#getObject(int)
     */
    public Object getObject(int columnIndex)
        throws SQLException {

        Object result = 
        	writeRow == null
        	? currentRow[fields[columnIndex-1]]
        	: writeRow[fields[columnIndex-1]];
        	
        wasNull = result == null;
        return result;
    }
    
    /**
     * @see java.sql.ResultSet#isAfterLast()
     */
    public boolean isAfterLast() throws SQLException {
        return currentRow == null;
    }

    /**
     * @see java.sql.ResultSet#getRow()
     */
    public int getRow() throws SQLException {
        return row;
    }
    
    /**
     * @see java.sql.ResultSet#absolute(int)
     */
    public boolean absolute(int row) throws SQLException {
        throw new SQLException("Not supported!");
    }
    
    /**
     * @see java.sql.ResultSet#rowUpdated()
     */
    public boolean rowUpdated() throws SQLException {
        throw new SQLException("NYI");
    }
    
    /**
     * @see java.sql.ResultSet#rowInserted()
     */
    public boolean rowInserted() throws SQLException {
        throw new SQLException("NYI");
    }

    /**
     * @see java.sql.ResultSet#rowDeleted()
     */
    public boolean rowDeleted() throws SQLException {
        return false;
    }

    /**
     * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
     */

    public void updateObject(int columnIndex, Object x)
        throws SQLException {
            
        if (writeRow == null) {
            writeRow = new Object[table.getColumnCount()];
            if (currentRow != null)
            	System.arraycopy(currentRow, 0, writeRow, 0, writeRow.length);
        }
			
    	currentRow [fields[columnIndex-1]] = x;        
    }
    
    
    /**
     * @see java.sql.ResultSet#insertRow()
     */
    public void insertRow() throws SQLException {
        appendRow(currentRow);
        moveToCurrentRow();
        moveToInsertRow();
    }
    
    
    /**
     * @see java.sql.ResultSet#updateRow()
     */

    public void updateRow() throws SQLException {
      	throw new SQLException("Only Insert supported");
    }
    
    /**
     * @see java.sql.ResultSet#deleteRow()
     */
    public void deleteRow() throws SQLException {
        throw new SQLException("Only Insert supported");
    }

    /**
     * @see java.sql.ResultSet#refreshRow()
     */
    public void refreshRow() throws SQLException {
        writeRow = null;
    }

    /**
     * @see java.sql.ResultSet#moveToInsertRow()
     */
    public void moveToInsertRow() throws SQLException {
        if(!insertRow) {
            insertRow = true;
            writeRow = new Object[table.getColumnCount()];
        }
    }

    /**
     * @see java.sql.ResultSet#moveToCurrentRow()
     */
    public void moveToCurrentRow() throws SQLException {
		if(insertRow) {
		    insertRow = false;
		    writeRow = null;
		}
    }

}
