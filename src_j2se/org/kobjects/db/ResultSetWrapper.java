package org.kobjects.db;

import java.io.*;
import java.util.Map;
import java.util.Calendar;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;

import org.kobjects.db.*;

// (C) 2002 by Stefan Haustein 
// Rolandstrasse 27, D-46045 Oberhausen, Germany
// All rights reserved.
//
// For licensing details, please refer to the file "license.txt",
// distributed with this file.

/**
 * @author Stefan Haustein */
public class ResultSetWrapper
    implements ResultSet, ResultSetMetaData {

	final static String NYI = "Method not (yet) implemented/supported";

    DbResultSet resultSet;

    /**
     * Constructor for ResultSetWrapper.
     */
    public ResultSetWrapper(String connector)
        throws SQLException {
        try {
        	DbTable table = DbManager.connect(connector);
        	table.open();
            resultSet = table.select(false);
        }
        catch (DbException e) {
            throw new SQLException(e.toString());
        }
    }

    /**
     * @see java.sql.ResultSet#next()
     */
    public boolean next() throws SQLException {
        try {
            return resultSet.next();
        }
        catch (DbException e) {
            throw new SQLException(e.toString());
        }
    }

    /**
     * @see java.sql.ResultSet#close()
     */
    public void close() throws SQLException {
        resultSet.close();
    }

    /**
     * @see java.sql.ResultSet#wasNull()
     */
    public boolean wasNull() throws SQLException {
        throw new RuntimeException("wasNull not supported!");
    }

    /**
     * @see java.sql.ResultSet#getString(int)
     */
    public String getString(int columnIndex)
        throws SQLException {

        return resultSet.getString(columnIndex);

    }

    /**
     * @see java.sql.ResultSet#getBoolean(int)
     */
    public boolean getBoolean(int columnIndex)
        throws SQLException {
        return resultSet.getBoolean(columnIndex);
    }

    /**
     * @see java.sql.ResultSet#getByte(int)
     */
    public byte getByte(int columnIndex) throws SQLException {
        return (byte) resultSet.getInt(columnIndex);
    }

    /**
     * @see java.sql.ResultSet#getShort(int)
     */
    public short getShort(int columnIndex) throws SQLException {
        return (short) resultSet.getInt(columnIndex);
    }

    /**
     * @see java.sql.ResultSet#getInt(int)
     */
    public int getInt(int columnIndex) throws SQLException {
        return resultSet.getInt(columnIndex);
    }

    /**
     * @see java.sql.ResultSet#getLong(int)
     */
    public long getLong(int columnIndex) throws SQLException {
        return resultSet.getLong(columnIndex);
    }

    /**
     * @see java.sql.ResultSet#getFloat(int)
     */
    public float getFloat(int columnIndex) throws SQLException {
        return ((Number) resultSet.getObject(columnIndex))
            .floatValue();
    }

    /**
     * @see java.sql.ResultSet#getDouble(int)
     */
    public double getDouble(int columnIndex)
        throws SQLException {
        return ((Number) resultSet.getObject(columnIndex))
            .doubleValue();
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(int, int)
     * @deprecated
     */
    public BigDecimal getBigDecimal(int columnIndex, int scale)
        throws SQLException {
        throw new RuntimeException("BigDecimal not supported!");
    }

    /**
     * @see java.sql.ResultSet#getBytes(int)
     */
    public byte[] getBytes(int columnIndex)
        throws SQLException {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getDate(int)
     */
    public Date getDate(int columnIndex) throws SQLException {
        throw new RuntimeException("getDate not supported");
    }

    /**
     * @see java.sql.ResultSet#getTime(int)
     */
    public Time getTime(int columnIndex) throws SQLException {
        throw new RuntimeException("getTime not supported");
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(int)
     */
    public Timestamp getTimestamp(int columnIndex)
        throws SQLException {
        throw new RuntimeException("getTimestamp not supported");
    }

    /**
     * @see java.sql.ResultSet#getAsciiStream(int)
     */
    public InputStream getAsciiStream(int columnIndex)
        throws SQLException {
        throw new RuntimeException("getAsciiStream not supported");
    }

    /**
     * @see java.sql.ResultSet#getUnicodeStream(int)
     * @deprecated
     */
    public InputStream getUnicodeStream(int columnIndex)
        throws SQLException {
        throw new RuntimeException("getUnicodeStream not supported");
    }

    /**
     * @see java.sql.ResultSet#getBinaryStream(int)
     */
    public InputStream getBinaryStream(int columnIndex)
        throws SQLException {
        return resultSet.getBinaryStream(columnIndex);
    }

    /**
     * @see java.sql.ResultSet#getString(String)
     */
    public String getString(String columnName)
        throws SQLException {
        return resultSet.getString(
            resultSet.findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getBoolean(String)
     */
    public boolean getBoolean(String columnName)
        throws SQLException {
        return resultSet.getBoolean(
            resultSet.findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getByte(String)
     */
    public byte getByte(String columnName) throws SQLException {
        return getByte(resultSet.findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getShort(String)
     */
    public short getShort(String columnName)
        throws SQLException {
        return getShort(resultSet.findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getInt(String)
     */
    public int getInt(String columnName) throws SQLException {
        return resultSet.getInt(
            resultSet.findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getLong(String)
     */
    public long getLong(String columnName) throws SQLException {
        return resultSet.getLong(
            resultSet.findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getFloat(String)
     */
    public float getFloat(String columnName)
        throws SQLException {
        return getFloat(resultSet.findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getDouble(String)
     */
    public double getDouble(String columnName)
        throws SQLException {
        return getDouble(resultSet.findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(String, int)
     * @deprecated
     */
    public BigDecimal getBigDecimal(
        String columnName,
        int scale)
        throws SQLException {
        return getBigDecimal(findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getBytes(String)
     */
    public byte[] getBytes(String columnName)
        throws SQLException {
        return getBytes(findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getDate(String)
     */
    public Date getDate(String columnName) throws SQLException {
        return getDate(findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getTime(String)
     */
    public Time getTime(String columnName) throws SQLException {
        return getTime(findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(String)
     */
    public Timestamp getTimestamp(String columnName)
        throws SQLException {
        return getTimestamp(findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getAsciiStream(String)
     */
    public InputStream getAsciiStream(String columnName)
        throws SQLException {
        return getAsciiStream(findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getUnicodeStream(String)
     * @deprecated
     */
    public InputStream getUnicodeStream(String columnName)
        throws SQLException {
        return getUnicodeStream(findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getBinaryStream(String)
     */
    public InputStream getBinaryStream(String columnName)
        throws SQLException {
        return getBinaryStream(findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getWarnings()
     */
    public SQLWarning getWarnings() throws SQLException {
        throw new RuntimeException("getWarnings not supported");
    }

    /**
     * @see java.sql.ResultSet#clearWarnings()
     */
    public void clearWarnings() throws SQLException {
        throw new RuntimeException("clearWarnings not supported");
    }

    /**
     * @see java.sql.ResultSet#getCursorName()
     */
    public String getCursorName() throws SQLException {
        throw new RuntimeException("getCursorName not supported");
    }

    /**
     * @see java.sql.ResultSet#getMetaData()
     */
    public ResultSetMetaData getMetaData() throws SQLException {
        return this;
    }

    /**
     * @see java.sql.ResultSet#getObject(int)
     */
    public Object getObject(int columnIndex)
        throws SQLException {
        return resultSet.getObject(columnIndex);
    }

    /**
     * @see java.sql.ResultSet#getObject(String)
     */
    public Object getObject(String columnName)
        throws SQLException {
        return resultSet.getObject(findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#findColumn(String)
     */
    public int findColumn(String columnName)
        throws SQLException {
        return resultSet.findColumn(columnName);
    }

    /**
     * @see java.sql.ResultSet#getCharacterStream(int)
     */
    public Reader getCharacterStream(int columnIndex)
        throws SQLException {
        throw new RuntimeException("getCharacterStream() not supported");
    }

    /**
     * @see java.sql.ResultSet#getCharacterStream(String)
     */
    public Reader getCharacterStream(String columnName)
        throws SQLException {
        return getCharacterStream(findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(int)
     */
    public BigDecimal getBigDecimal(int columnIndex)
        throws SQLException {
        throw new RuntimeException("getBigDecimal not supported!");
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(String)
     */
    public BigDecimal getBigDecimal(String columnName)
        throws SQLException {
        return getBigDecimal(findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#isBeforeFirst()
     */
    public boolean isBeforeFirst() throws SQLException {
        throw new RuntimeException("isBeforeFirst() not supported");
    }

    /**
     * @see java.sql.ResultSet#isAfterLast()
     */
    public boolean isAfterLast() throws SQLException {
        return resultSet.getRow() == 0;
    }

    /**
     * @see java.sql.ResultSet#isFirst()
     */
    public boolean isFirst() throws SQLException {
        return resultSet.getRow() == 1;
    }

    /**
     * @see java.sql.ResultSet#isLast()
     */
    public boolean isLast() throws SQLException {
        return resultSet.isLast();
    }

    /**
     * @see java.sql.ResultSet#beforeFirst()
     */
    public void beforeFirst() throws SQLException {
        try {
            resultSet.beforeFirst();
        }
        catch (DbException e) {
            throw new SQLException(e.toString());
        }
    }

    /**
     * @see java.sql.ResultSet#afterLast()
     */
    public void afterLast() throws SQLException {
        try {
            resultSet.absolute(resultSet.getRowCount() + 1);
        }
        catch (DbException e) {
            throw new SQLException(e.toString());
        }
    }

    /**
     * @see java.sql.ResultSet#first()
     */
    public boolean first() throws SQLException {
        try {
            resultSet.beforeFirst();
            return resultSet.next();
        }
        catch (DbException e) {
            throw new SQLException(e.toString());
        }
    }

    /**
     * @see java.sql.ResultSet#last()
     */
    public boolean last() throws SQLException {
        try {
            if (resultSet.getColumnCount() == 0)
                return false;
            resultSet.absolute(resultSet.getColumnCount());
            return true;
        }
        catch (DbException e) {
            throw new SQLException(e.toString());
        }
    }

    /**
     * @see java.sql.ResultSet#getRow()
     */
    public int getRow() throws SQLException {
        return resultSet.getRow();
    }

    /**
     * @see java.sql.ResultSet#absolute(int)
     */
    public boolean absolute(int row) throws SQLException {
    	try {
        return resultSet.absolute (row);
    	}
		catch (DbException e) {
			throw new SQLException (e.toString());
		}
    }

    /**
     * @see java.sql.ResultSet#relative(int)
     */
    public boolean relative(int rows) throws SQLException {
        return absolute (resultSet.getRow() + rows);
    }

    /**
     * @see java.sql.ResultSet#previous()
     */
    public boolean previous() throws SQLException {
		return relative (-1);
    }

    /**
     * @see java.sql.ResultSet#setFetchDirection(int)
     */
    public void setFetchDirection(int direction) {
		throw new RuntimeException(NYI);		
    }

    /**
     * @see java.sql.ResultSet#getFetchDirection()
     */
    public int getFetchDirection() throws SQLException {
		throw new RuntimeException(NYI);		
    }

    /**
     * @see java.sql.ResultSet#setFetchSize(int)
     */
    public void setFetchSize(int rows) throws SQLException {
		throw new RuntimeException(NYI);		
    }

    /**
     * @see java.sql.ResultSet#getFetchSize()
     */
    public int getFetchSize() throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getType()
     */
    public int getType() throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getConcurrency()
     */
    public int getConcurrency() throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#rowUpdated()
     */
    public boolean rowUpdated() throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#rowInserted()
     */
    public boolean rowInserted() throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#rowDeleted()
     */
    public boolean rowDeleted() throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateNull(int)
     */
    public void updateNull(int columnIndex)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateBoolean(int, boolean)
     */
    public void updateBoolean(int columnIndex, boolean x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateByte(int, byte)
     */
    public void updateByte(int columnIndex, byte x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateShort(int, short)
     */
    public void updateShort(int columnIndex, short x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateInt(int, int)
     */
    public void updateInt(int columnIndex, int x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateLong(int, long)
     */
    public void updateLong(int columnIndex, long x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateFloat(int, float)
     */
    public void updateFloat(int columnIndex, float x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateDouble(int, double)
     */
    public void updateDouble(int columnIndex, double x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateBigDecimal(int, BigDecimal)
     */
    public void updateBigDecimal(int columnIndex, BigDecimal x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateString(int, String)
     */
    public void updateString(int columnIndex, String x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateBytes(int, byte[])
     */
    public void updateBytes(int columnIndex, byte[] x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateDate(int, Date)
     */
    public void updateDate(int columnIndex, Date x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateTime(int, Time)
     */
    public void updateTime(int columnIndex, Time x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateTimestamp(int, Timestamp)
     */
    public void updateTimestamp(int columnIndex, Timestamp x)
        throws SQLException {
    }

    /**
     * @see java.sql.ResultSet#updateAsciiStream(int, InputStream, int)
     */
    public void updateAsciiStream(
        int columnIndex,
        InputStream x,
        int length)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateBinaryStream(int, InputStream, int)
     */
    public void updateBinaryStream(
        int columnIndex,
        InputStream x,
        int length)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateCharacterStream(int, Reader, int)
     */
    public void updateCharacterStream(
        int columnIndex,
        Reader x,
        int length)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateObject(int, Object, int)
     */
    public void updateObject(
        int columnIndex,
        Object x,
        int scale)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateObject(int, Object)
     */
    public void updateObject(int columnIndex, Object x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateNull(String)
     */
    public void updateNull(String columnName)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateBoolean(String, boolean)
     */
    public void updateBoolean(String columnName, boolean x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateByte(String, byte)
     */
    public void updateByte(String columnName, byte x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateShort(String, short)
     */
    public void updateShort(String columnName, short x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateInt(String, int)
     */
    public void updateInt(String columnName, int x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateLong(String, long)
     */
    public void updateLong(String columnName, long x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateFloat(String, float)
     */
    public void updateFloat(String columnName, float x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateDouble(String, double)
     */
    public void updateDouble(String columnName, double x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateBigDecimal(String, BigDecimal)
     */
    public void updateBigDecimal(
        String columnName,
        BigDecimal x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateString(String, String)
     */
    public void updateString(String columnName, String x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateBytes(String, byte[])
     */
    public void updateBytes(String columnName, byte[] x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateDate(String, Date)
     */
    public void updateDate(String columnName, Date x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateTime(String, Time)
     */
    public void updateTime(String columnName, Time x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateTimestamp(String, Timestamp)
     */
    public void updateTimestamp(String columnName, Timestamp x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateAsciiStream(String, InputStream, int)
     */
    public void updateAsciiStream(
        String columnName,
        InputStream x,
        int length)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateBinaryStream(String, InputStream, int)
     */
    public void updateBinaryStream(
        String columnName,
        InputStream x,
        int length)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateCharacterStream(String, Reader, int)
     */
    public void updateCharacterStream(
        String columnName,
        Reader reader,
        int length)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateObject(String, Object, int)
     */
    public void updateObject(
        String columnName,
        Object x,
        int scale)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateObject(String, Object)
     */
    public void updateObject(String columnName, Object x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#insertRow()
     */
    public void insertRow() throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateRow()
     */
    public void updateRow() throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#deleteRow()
     */
    public void deleteRow() throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#refreshRow()
     */
    public void refreshRow() throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#cancelRowUpdates()
     */
    public void cancelRowUpdates() throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#moveToInsertRow()
     */
    public void moveToInsertRow() throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#moveToCurrentRow()
     */
    public void moveToCurrentRow() throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getStatement()
     */
    public Statement getStatement() throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getObject(int, Map)
     */
    public Object getObject(int i, Map map)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getRef(int)
     */
    public Ref getRef(int i) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getBlob(int)
     */
    public Blob getBlob(int i) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getClob(int)
     */
    public Clob getClob(int i) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getArray(int)
     */
    public Array getArray(int i) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getObject(String, Map)
     */
    public Object getObject(String colName, Map map)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getRef(String)
     */
    public Ref getRef(String colName) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getBlob(String)
     */
    public Blob getBlob(String colName) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getClob(String)
     */
    public Clob getClob(String colName) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getArray(String)
     */
    public Array getArray(String colName) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getDate(int, Calendar)
     */
    public Date getDate(int columnIndex, Calendar cal)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getDate(String, Calendar)
     */
    public Date getDate(String columnName, Calendar cal)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getTime(int, Calendar)
     */
    public Time getTime(int columnIndex, Calendar cal)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getTime(String, Calendar)
     */
    public Time getTime(String columnName, Calendar cal)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(int, Calendar)
     */
    public Timestamp getTimestamp(int columnIndex, Calendar cal)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(String, Calendar)
     */
    public Timestamp getTimestamp(
        String columnName,
        Calendar cal)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getURL(int)
     */
    public URL getURL(int columnIndex) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#getURL(String)
     */
    public URL getURL(String columnName) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateRef(int, Ref)
     */
    public void updateRef(int columnIndex, Ref x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }


    /**
     * @see java.sql.ResultSet#updateRef(String, Ref)
     */
    public void updateRef(String columnName, Ref x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateBlob(int, Blob)
     */
    public void updateBlob(int columnIndex, Blob x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateBlob(String, Blob)
     */
    public void updateBlob(String columnName, Blob x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateClob(int, Clob)
     */
    public void updateClob(int columnIndex, Clob x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateClob(String, Clob)
     */
    public void updateClob(String columnName, Clob x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateArray(int, Array)
     */
    public void updateArray(int columnIndex, Array x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSet#updateArray(String, Array)
     */
    public void updateArray(String columnName, Array x)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#getColumnCount()
     */
    public int getColumnCount() throws SQLException {
        return resultSet.getColumnCount();
    }

    /**
     * @see java.sql.ResultSetMetaData#isAutoIncrement(int)
     */
    public boolean isAutoIncrement(int column)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#isCaseSensitive(int)
     */
    public boolean isCaseSensitive(int column)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#isSearchable(int)
     */
    public boolean isSearchable(int column)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#isCurrency(int)
     */
    public boolean isCurrency(int column) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#isNullable(int)
     */
    public int isNullable(int column) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#isSigned(int)
     */
    public boolean isSigned(int column) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#getColumnDisplaySize(int)
     */
    public int getColumnDisplaySize(int column)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#getColumnLabel(int)
     */
    public String getColumnLabel(int column)
        throws SQLException {
        return resultSet.getField(column).getLabel();
    }

    /**
     * @see java.sql.ResultSetMetaData#getColumnName(int)
     */
    public String getColumnName(int column)
        throws SQLException {
        return resultSet.getField(column).getName();
    }

    /**
     * @see java.sql.ResultSetMetaData#getSchemaName(int)
     */
    public String getSchemaName(int column)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#getPrecision(int)
     */
    public int getPrecision(int column) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#getScale(int)
     */
    public int getScale(int column) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#getTableName(int)
     */
    public String getTableName(int column) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#getCatalogName(int)
     */
    public String getCatalogName(int column)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#getColumnType(int)
     */
    public int getColumnType(int column) throws SQLException {
        return resultSet.getField(column).getType();
    }

    /**
     * @see java.sql.ResultSetMetaData#getColumnTypeName(int)
     */
    public String getColumnTypeName(int column)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#isReadOnly(int)
     */
    public boolean isReadOnly(int column) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#isWritable(int)
     */
    public boolean isWritable(int column) throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#isDefinitelyWritable(int)
     */
    public boolean isDefinitelyWritable(int column)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

    /**
     * @see java.sql.ResultSetMetaData#getColumnClassName(int)
     */
    public String getColumnClassName(int column)
        throws SQLException {
		throw new RuntimeException(NYI);
    }

}
