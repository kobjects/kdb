package org.kobjects.db;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;


/** Maps all methods get and update methods to getObject/updateObject */


public abstract class AbstractResultSet
    implements ResultSet, ResultSetMetaData {

    private DbTable table;
    protected int[] fields;
    private Hashtable names;

    protected AbstractResultSet(DbTable table, int[] fields) {
        this.table = table;
        this.fields = fields;
    }

    /**
     * @see java.sql.ResultSet#getString(int)
     */
    public String getString(int columnIndex)
        throws SQLException {
        return (String) getObject(columnIndex);
    }
    /**
     * @see java.sql.ResultSet#getBoolean(int)
     */
    public boolean getBoolean(int columnIndex)
        throws SQLException {
        return ((Boolean) getObject(columnIndex)).booleanValue();
    }
    /**
     * @see java.sql.ResultSet#getByte(int)
     */
    public byte getByte(int columnIndex) throws SQLException {
        return ((Number) getObject(columnIndex)).byteValue();
    }
    /**
     * @see java.sql.ResultSet#getShort(int)
     */
    public short getShort(int columnIndex) throws SQLException {
        return ((Number) getObject(columnIndex)).shortValue();
    }
    /**
     * @see java.sql.ResultSet#getInt(int)
     */
    public int getInt(int columnIndex) throws SQLException {
        return ((Number) getObject(columnIndex)).intValue();
    }
    /**
     * @see java.sql.ResultSet#getLong(int)
     */
    public long getLong(int columnIndex) throws SQLException {
        return ((Number) getObject(columnIndex)).longValue();
    }
    /**
     * @see java.sql.ResultSet#getFloat(int)
     */
    public float getFloat(int columnIndex) throws SQLException {
        return ((Number) getObject(columnIndex)).floatValue();
    }
    /**
     * @see java.sql.ResultSet#getDouble(int)
     */
    public double getDouble(int columnIndex)
        throws SQLException {
        return ((Number) getObject(columnIndex)).doubleValue();
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(int, int)
     * @deprecated
     */
    public BigDecimal getBigDecimal(int columnIndex, int scale)
        throws SQLException {
        return (BigDecimal) getObject(columnIndex);
    }
    /**
     * @see java.sql.ResultSet#getBytes(int)
     */
    public byte[] getBytes(int columnIndex)
        throws SQLException {
        return (byte[]) getObject(columnIndex);
    }
    /**
     * @see java.sql.ResultSet#getDate(int)
     */
    public Date getDate(int columnIndex) throws SQLException {
        return (Date) getObject(columnIndex);
    }
    /**
     * @see java.sql.ResultSet#getTime(int)
     */
    public Time getTime(int columnIndex) throws SQLException {
        return (Time) getObject(columnIndex);
    }
    /**
     * @see java.sql.ResultSet#getTimestamp(int)
     */
    public Timestamp getTimestamp(int columnIndex)
        throws SQLException {
        return (Timestamp) getObject(columnIndex);
    }
    /**
     * @see java.sql.ResultSet#getAsciiStream(int)
     */
    public InputStream getAsciiStream(int columnIndex)
        throws SQLException {
        throw new RuntimeException("not supported");
    }
    /**
     * @see java.sql.ResultSet#getUnicodeStream(int)
     * @deprecated
     */
    public InputStream getUnicodeStream(int columnIndex)
        throws SQLException {
        throw new RuntimeException("not supported");
    }
    /**
     * @see java.sql.ResultSet#getBinaryStream(int)
     */
    public InputStream getBinaryStream(int columnIndex)
        throws SQLException {
        return new ByteArrayInputStream(getBytes(columnIndex));
    }
    /**
     * @see java.sql.ResultSet#getString(java.lang.String)
     */
    public String getString(String columnName)
        throws SQLException {
        return getString(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#getBoolean(java.lang.String)
     */
    public boolean getBoolean(String columnName)
        throws SQLException {
        return getBoolean(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#getByte(java.lang.String)
     */
    public byte getByte(String columnName) throws SQLException {
        return getByte(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#getShort(java.lang.String)
     */
    public short getShort(String columnName)
        throws SQLException {
        return getShort(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#getInt(java.lang.String)
     */
    public int getInt(String columnName) throws SQLException {
        return getInt(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#getLong(java.lang.String)
     */
    public long getLong(String columnName) throws SQLException {
        return getLong(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#getFloat(java.lang.String)
     */
    public float getFloat(String columnName)
        throws SQLException {
        return getFloat(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#getDouble(java.lang.String)
     */
    public double getDouble(String columnName)
        throws SQLException {
        return getDouble(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
     * @deprecated
     */
    public BigDecimal getBigDecimal(
        String columnName,
        int scale)
        throws SQLException {
        return getBigDecimal(findColumn(columnName), scale);
    }
    /**
     * @see java.sql.ResultSet#getBytes(java.lang.String)
     */
    public byte[] getBytes(String columnName)
        throws SQLException {
        return getBytes(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#getDate(java.lang.String)
     */
    public Date getDate(String columnName) throws SQLException {
        return getDate(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#getTime(java.lang.String)
     */
    public Time getTime(String columnName) throws SQLException {
        return getTime(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#getTimestamp(java.lang.String)
     */
    public Timestamp getTimestamp(String columnName)
        throws SQLException {
        return getTimestamp(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
     */
    public InputStream getAsciiStream(String columnName)
        throws SQLException {
        return getAsciiStream(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
     * @deprecated
     */
    public InputStream getUnicodeStream(String columnName)
        throws SQLException {
        return getUnicodeStream(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
     */
    public InputStream getBinaryStream(String columnName)
        throws SQLException {
        return getBinaryStream(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#getWarnings()
     */
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }
    /**
     * @see java.sql.ResultSet#clearWarnings()
     */
    public void clearWarnings() throws SQLException {
    }
    /**
     * @see java.sql.ResultSet#getCursorName()
     */
    public String getCursorName() throws SQLException {
        return null;
    }
    /**
     * @see java.sql.ResultSet#getMetaData()
     */
    public ResultSetMetaData getMetaData() throws SQLException {
        return this;
    }

    /**
     * @see java.sql.ResultSet#getObject(java.lang.String)
     */
    public Object getObject(String columnName)
        throws SQLException {
        return getObject(findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#findColumn(java.lang.String)
     */
    public int findColumn(String columnName)
        throws SQLException {
        if (names == null) {
            names = new Hashtable();
            for (int i = 0; i < fields.length; i++) {
                names.put(
                    table.getColumn(fields[i]).getName(),
                    new Integer(fields[i]));
            }
        }

        return ((Integer) names.get(columnName)).intValue();
    }

    /**
     * @see java.sql.ResultSet#getCharacterStream(int)
     */
    public Reader getCharacterStream(int columnIndex)
        throws SQLException {
        return new StringReader(getString(columnIndex));
    }
    /**
     * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
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
        return (BigDecimal) getObject(columnIndex);
    }
    /**
     * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
     */
    public BigDecimal getBigDecimal(String columnName)
        throws SQLException {
        return getBigDecimal(findColumn(columnName));
    }

    /**
     * @see java.sql.ResultSet#isBeforeFirst()
     */
    public boolean isBeforeFirst() throws SQLException {
        return getRow() < 1;
    }

    /**
     * @see java.sql.ResultSet#isFirst()
     */
    public boolean isFirst() throws SQLException {
        return getRow() == 1 && isAfterLast();
    }
    /*
     * @see java.sql.ResultSet#isLast()
    public boolean isLast() throws SQLException {
        return false;
    }
     */

    /**
     * @see java.sql.ResultSet#beforeFirst()
     */
    public void beforeFirst() throws SQLException {
        absolute(0);
    }

    /**
     * @see java.sql.ResultSet#first()
     */
    public boolean first() throws SQLException {
        return absolute(1);
    }

    /**
     * @see java.sql.ResultSet#relative(int)
     */
    public boolean relative(int rows) throws SQLException {
        return absolute(getRow() + rows);
    }
    /**
     * @see java.sql.ResultSet#previous()
     */
    public boolean previous() throws SQLException {
        return absolute(getRow() - 1);
    }

    /**
     * @see java.sql.ResultSet#setFetchDirection(int)
     */
    public void setFetchDirection(int direction)
        throws SQLException {
    }

    /**
     * @see java.sql.ResultSet#getFetchDirection()
     */
    public int getFetchDirection() throws SQLException {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#setFetchSize(int)
     */
    public void setFetchSize(int rows) throws SQLException {
    }

    /**
     * @see java.sql.ResultSet#getFetchSize()
     */
    public int getFetchSize() throws SQLException {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getType()
     */
    public int getType() throws SQLException {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getConcurrency()
     */
    public int getConcurrency() throws SQLException {
        return 0;
    }

    public void updateNull(int columnIndex)
        throws SQLException {
        updateObject(columnIndex, null);
    }

    /**
     * @see java.sql.ResultSet#updateBoolean(int, boolean)
     */

    public void updateBoolean(int columnIndex, boolean x)
        throws SQLException {
        updateObject(columnIndex, new Boolean(x));
    }
    /**
     * @see java.sql.ResultSet#updateByte(int, byte)
     */
    public void updateByte(int columnIndex, byte x)
        throws SQLException {
        updateObject(columnIndex, new Byte(x));
    }
    /**
     * @see java.sql.ResultSet#updateShort(int, short)
     */
    public void updateShort(int columnIndex, short x)
        throws SQLException {
        updateObject(columnIndex, new Short(x));
    }
    /**
     * @see java.sql.ResultSet#updateInt(int, int)
     */
    public void updateInt(int columnIndex, int x)
        throws SQLException {
        updateObject(columnIndex, new Integer(x));
    }
    /**
     * @see java.sql.ResultSet#updateLong(int, long)
     */
    public void updateLong(int columnIndex, long x)
        throws SQLException {
        updateObject(columnIndex, new Long(x));
    }
    /**
     * @see java.sql.ResultSet#updateFloat(int, float)
     */
    public void updateFloat(int columnIndex, float x)
        throws SQLException {
        updateObject(columnIndex, new Float(x));
    }
    /**
     * @see java.sql.ResultSet#updateDouble(int, double)
     */
    public void updateDouble(int columnIndex, double x)
        throws SQLException {
        updateObject(columnIndex, new Double(x));
    }
    /**
     * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
     */
    public void updateBigDecimal(int columnIndex, BigDecimal x)
        throws SQLException {
        updateObject(columnIndex, x);
    }
    /**
     * @see java.sql.ResultSet#updateString(int, java.lang.String)
     */
    public void updateString(int columnIndex, String x)
        throws SQLException {
        updateObject(columnIndex, x);
    }
    /**
     * @see java.sql.ResultSet#updateBytes(int, byte[])
     */
    public void updateBytes(int columnIndex, byte[] x)
        throws SQLException {
        updateObject(columnIndex, x);
    }
    /**
     * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
     */
    public void updateDate(int columnIndex, Date x)
        throws SQLException {
        updateObject(columnIndex, x);
    }
    /**
     * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
     */
    public void updateTime(int columnIndex, Time x)
        throws SQLException {
        updateObject(columnIndex, x);
    }
    /**
     * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
     */
    public void updateTimestamp(int columnIndex, Timestamp x)
        throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
     */
    public void updateAsciiStream(
        int columnIndex,
        InputStream x,
        int length)
        throws SQLException {
        throw new SQLException("Not Supported");
    }

    /**
     * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
     */
    public void updateBinaryStream(
        int columnIndex,
        InputStream x,
        int length)
        throws SQLException {

        byte[] buf = new byte[length];

        int pos = 0;
        try {
            while (pos < length) {
                if (x.read(buf, pos, length - pos) <= 0)
                    throw new SQLException("Unexpected EOF");
            }
        }
        catch (IOException e) {
            throw new SQLException(e.toString());
        }
        updateObject(columnIndex, buf);
    }
    
    /**
     * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
     */
    public void updateCharacterStream(
        int columnIndex,
        Reader x,
        int length)
        throws SQLException {

		char[] buf=new char[length];
        int pos = 0;
        try {
            while (pos < length) {
                if (x.read(buf, pos, length - pos) <= 0)
                    throw new SQLException("Unexpected EOF");
            }
        }
        catch (IOException e) {
            throw new SQLException(e.toString());
        }
        updateObject(columnIndex, new String(buf));
    }
    /**
     * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
     */
    public void updateObject(
        int columnIndex,
        Object x,
        int scale)
        throws SQLException {
		throw new SQLException("Not Supported");
    }

    /*
     * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
    public void updateObject(int columnIndex, Object x)
        throws SQLException {
    }
     */

    
            /**
     * @see java.sql.ResultSet#updateNull(java.lang.String)
     */
    public void updateNull(String columnName)
        throws SQLException {
		updateNull(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
     */
    public void updateBoolean(String columnName, boolean x)
        throws SQLException {
            updateBoolean(findColumn(columnName), x);
    }
    /**
     * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
     */
    public void updateByte(String columnName, byte x)
        throws SQLException {
            updateByte(columnName, x);
    }
    /**
     * @see java.sql.ResultSet#updateShort(java.lang.String, short)
     */
    public void updateShort(String columnName, short x)
        throws SQLException {
            updateShort(findColumn(columnName), x);
    }
    /**
     * @see java.sql.ResultSet#updateInt(java.lang.String, int)
     */
    public void updateInt(String columnName, int x)
        throws SQLException {
            updateInt(findColumn(columnName), x);
    }
    /**
     * @see java.sql.ResultSet#updateLong(java.lang.String, long)
     */
    public void updateLong(String columnName, long x)
        throws SQLException {
            updateLong(findColumn(columnName), x);
    }
    /**
     * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
     */
    public void updateFloat(String columnName, float x)
        throws SQLException {
            updateFloat(findColumn(columnName), x);
    }
    /**
     * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
     */
    public void updateDouble(String columnName, double x)
        throws SQLException {
            updateDouble(findColumn(columnName), x);

    }
    /**
     * @see java.sql.ResultSet#updateBigDecimal(java.lang.String, java.math.BigDecimal)
     */
    public void updateBigDecimal(
        String columnName,
        BigDecimal x)
        throws SQLException {
            updateBigDecimal(findColumn(columnName), x);

    }
    /**
     * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
     */
    public void updateString(String columnName, String x)
        throws SQLException {
            updateString(findColumn(columnName), x);
    }
    /**
     * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
     */
    public void updateBytes(String columnName, byte[] x)
        throws SQLException {
            updateBytes(findColumn(columnName), x);

    }
    /**
     * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
     */
    public void updateDate(String columnName, Date x)
        throws SQLException {
            updateDate(findColumn(columnName), x);

    }
    /**
     * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
     */
    public void updateTime(String columnName, Time x)
        throws SQLException {
            updateTime(findColumn(columnName), x);
    }
    /**
     * @see java.sql.ResultSet#updateTimestamp(java.lang.String, java.sql.Timestamp)
     */
    public void updateTimestamp(String columnName, Timestamp x)
        throws SQLException {
            updateTimestamp(findColumn(columnName), x);
    }
    /**
     * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, int)
     */
    public void updateAsciiStream(
        String columnName,
        InputStream x,
        int length)
        throws SQLException {
            updateAsciiStream(findColumn(columnName), x, length);

    }
    /**
     * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, int)
     */
    public void updateBinaryStream(
        String columnName,
        InputStream x,
        int length)
        throws SQLException {
            updateBinaryStream(findColumn(columnName), x, length);
    }
    /**
     * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, int)
     */
    public void updateCharacterStream(
        String columnName,
        Reader reader,
        int length)
        throws SQLException {
            updateCharacterStream(findColumn(columnName), reader, length);

    }
    /**
     * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object, int)
     */
    public void updateObject(
        String columnName,
        Object x,
        int scale)
        throws SQLException {
            updateObject(findColumn(columnName), x);
    }
    /**
     * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
     */
    public void updateObject(String columnName, Object x)
        throws SQLException {
            updateObject(findColumn(columnName), x);
    }

    public void cancelRowUpdates() throws SQLException {
		throw new SQLException("not supported!");
    }

    public Statement getStatement() throws SQLException {
        throw new SQLException("not supported!");
    }
    /**
     * @see java.sql.ResultSet#getObject(int, java.util.Map)
     */
    public Object getObject(int i, Map map)
        throws SQLException {
            throw new SQLException("not supported!");

    }
    /**
     * @see java.sql.ResultSet#getRef(int)
     */
    public Ref getRef(int i) throws SQLException {
        throw new SQLException("not supported!");
    }
    /**
     * @see java.sql.ResultSet#getBlob(int)
     */
    public Blob getBlob(int i) throws SQLException {
        throw new SQLException("not supported!");
    }
    /**
     * @see java.sql.ResultSet#getClob(int)
     */
    public Clob getClob(int i) throws SQLException {
        throw new SQLException("not supported!");
    }
    /**
     * @see java.sql.ResultSet#getArray(int)
     */
    public Array getArray(int i) throws SQLException {
        throw new SQLException("not supported!");
    }
    /**
     * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
     */
    public Object getObject(String colName, Map map)
        throws SQLException {
        return getObject(findColumn(colName), map);
    }

    /**
     * @see java.sql.ResultSet#getRef(java.lang.String)
     */
    public Ref getRef(String colName) throws SQLException {
        throw new SQLException("not supported!");
    }

    /**
     * @see java.sql.ResultSet#getBlob(java.lang.String)
     */
    public Blob getBlob(String colName) throws SQLException {
        return getBlob(findColumn(colName));
    }

    /**
     * @see java.sql.ResultSet#getClob(java.lang.String)
     */
    public Clob getClob(String colName) throws SQLException {
        return getClob(findColumn(colName));
    }

    /**
     * @see java.sql.ResultSet#getArray(java.lang.String)
     */
    public Array getArray(String colName) throws SQLException {
        return getArray(findColumn(colName));
    }
    /**
     * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
     */
    public Date getDate(int columnIndex, Calendar cal)
        throws SQLException {
            throw new SQLException("not supported!");
    }
    /**
     * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
     */
    public Date getDate(String columnName, Calendar cal)
        throws SQLException {
        return getDate(findColumn(columnName), cal);
    }
    /**
     * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
     */
    public Time getTime(int columnIndex, Calendar cal)
        throws SQLException {
            throw new SQLException("not supported!");
    }
    /**
     * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
     */
    public Time getTime(String columnName, Calendar cal)
        throws SQLException {
            return getTime(findColumn(columnName), cal);
    }
    /**
     * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
     */
    public Timestamp getTimestamp(int columnIndex, Calendar cal)
        throws SQLException {
            throw new SQLException("not supported!");
    }
    /**
     * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
     */
    public Timestamp getTimestamp(
        String columnName,
        Calendar cal)
        throws SQLException {
            return getTimestamp(findColumn(columnName), cal);
    }
    /**
     * @see java.sql.ResultSet#getURL(int)
     */
    public URL getURL(int columnIndex) throws SQLException {
        throw new SQLException("not supported!");
    }
    /**
     * @see java.sql.ResultSet#getURL(java.lang.String)
     */
    public URL getURL(String columnName) throws SQLException {
        return getURL(findColumn(columnName));
    }
    /**
     * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
     */
    public void updateRef(int columnIndex, Ref x)
        throws SQLException {
            throw new SQLException("not supported!");
    }
    
    /**
     * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
     */
    public void updateRef(String columnName, Ref x)
        throws SQLException {
		updateRef(findColumn(columnName), x);
    }
    
    /**
     * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
     */
    public void updateBlob(int columnIndex, Blob x)
        throws SQLException {
    }
    
    /**
     * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
     */
    public void updateBlob(String columnName, Blob x)
        throws SQLException {
    	updateBlob(findColumn(columnName), x);
    }
    
    /**
     * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
     */
    public void updateClob(int columnIndex, Clob x)
        throws SQLException {
            throw new SQLException("not supported!");
    }
    /**
     * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
     */
    public void updateClob(String columnName, Clob x)
        throws SQLException {
    }
    /**
     * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
     */
    public void updateArray(int columnIndex, Array x)
        throws SQLException {
            throw new SQLException("not supported!");
    }
    /**
     * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
     */
    public void updateArray(String columnName, Array x)
        throws SQLException {
    }
    /*
     * @see java.sql.ResultSet#next()

    public boolean next() throws SQLException {
        return false;
    }

     * @see java.sql.ResultSet#close()

    public void close() throws SQLException {
    }

     * @see java.sql.ResultSet#wasNull()

    public boolean wasNull() throws SQLException {
        return false;
    }

    /**
     * @see java.sql.ResultSetMetaData#getColumnCount()
     */
    public int getColumnCount() throws SQLException {
        return fields.length;
    }
    /**
     * @see java.sql.ResultSetMetaData#isAutoIncrement(int)
     */
    public boolean isAutoIncrement(int column)
        throws SQLException {
            throw new SQLException("not supported!");    }
    /**
     * @see java.sql.ResultSetMetaData#isCaseSensitive(int)
     */
    public boolean isCaseSensitive(int column)
        throws SQLException {
        return true;
    }
    /**
     * @see java.sql.ResultSetMetaData#isSearchable(int)
     */
    public boolean isSearchable(int column)
        throws SQLException {
        return true;
    }
    /**
     * @see java.sql.ResultSetMetaData#isCurrency(int)
     */
    public boolean isCurrency(int column) throws SQLException {
        throw new SQLException("not supported!");    }
    /**
     * @see java.sql.ResultSetMetaData#isNullable(int)
     */
    public int isNullable(int column) throws SQLException {
        throw new SQLException("not supported!");    }
    /**
     * @see java.sql.ResultSetMetaData#isSigned(int)
     */
    public boolean isSigned(int column) throws SQLException {
        throw new SQLException("not supported!");    }
    /**
     * @see java.sql.ResultSetMetaData#getColumnDisplaySize(int)
     */
    public int getColumnDisplaySize(int column)
        throws SQLException {
            throw new SQLException("not supported!");    
    }
    /**
     * @see java.sql.ResultSetMetaData#getColumnLabel(int)
     */
    public String getColumnLabel(int column)
        throws SQLException {
            return table.getColumn(column).getLabel();
        }
    /**
     * @see java.sql.ResultSetMetaData#getColumnName(int)
     */
    public String getColumnName(int column)
        throws SQLException {
        return table.getColumn(column).getName();
    }
    /**
     * @see java.sql.ResultSetMetaData#getSchemaName(int)
     */
    public String getSchemaName(int column)
        throws SQLException {
            throw new SQLException("not supported!");    
    }
    /**
     * @see java.sql.ResultSetMetaData#getPrecision(int)
     */
    public int getPrecision(int column) throws SQLException {
        throw new SQLException("not supported!");    
    }
    /**
     * @see java.sql.ResultSetMetaData#getScale(int)
     */
    public int getScale(int column) throws SQLException {
        throw new SQLException("not supported!");    
    }
    /**
     * @see java.sql.ResultSetMetaData#getTableName(int)
     */
    public String getTableName(int column) throws SQLException {
        return table.getName();
    }
    /**
     * @see java.sql.ResultSetMetaData#getCatalogName(int)
     */
    public String getCatalogName(int column)
        throws SQLException {
            throw new SQLException("not supported!");    
    }
    /**
     * @see java.sql.ResultSetMetaData#getColumnType(int)
     */
    public int getColumnType(int column) throws SQLException {
        return table.getColumn(column).getType();
    }
    /**
     * @see java.sql.ResultSetMetaData#getColumnTypeName(int)
     */
    public String getColumnTypeName(int column)
        throws SQLException {
            throw new SQLException("not supported!");    
    }
    /**
     * @see java.sql.ResultSetMetaData#isReadOnly(int)
     */
    public boolean isReadOnly(int column) throws SQLException {
        throw new SQLException("not supported!");    
    }
    /**
     * @see java.sql.ResultSetMetaData#isWritable(int)
     */
    public boolean isWritable(int column) throws SQLException {
        throw new SQLException("not supported!");    
    }
    /**
     * @see java.sql.ResultSetMetaData#isDefinitelyWritable(int)
     */
    public boolean isDefinitelyWritable(int column)
        throws SQLException {
            throw new SQLException("not supported!");    
    }
    /**
     * @see java.sql.ResultSetMetaData#getColumnClassName(int)
     */
    public String getColumnClassName(int column)
        throws SQLException {
            throw new SQLException("not supported!");    
    }


	public boolean last() throws SQLException {
	    while(!isLast() && next());
	    	
	    return !isAfterLast();
	}

	public void afterLast() throws SQLException {
	    while(next());
	}

}
