package org.kobjects.db.jdbc;

import org.kobjects.db.*;
import org.kobjects.util.*;
import java.sql.*;
import java.util.*;

public class JdbcTable implements DbTable {

    String tableName;
    boolean exists;
    boolean open;
    Connection connection;
    PreparedStatement insertStatement;
    boolean ownConnection;
    Vector fields = new Vector();

    static {
        try {
            DriverManager.registerDriver(
                (Driver) Class
                    .forName("oracle.jdbc.driver.OracleDriver")
                    .newInstance());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JdbcTable() {
    }

    public JdbcTable(Connection connection, String name) {
        this.connection = connection;
        this.tableName = name;

        init();
    }

    public void connect(String url) {

        //throw new RuntimeException ("NYI");

        String user = null;
        String password = null;

        while (true) {
            int i0 = url.lastIndexOf(';');
            if (i0 == -1)
                break;
            int i1 = url.indexOf('=', i0);
            if (i1 == -1)
                throw new RuntimeException("illegal param format");
            String name =
                url.substring(i0 + 1, i1).trim().toLowerCase();
            String value = url.substring(i1 + 1).trim();
            url = url.substring(0, i0);

            if ("user".equals(name))
                user = value;
            else if ("table".equals(name))
                tableName = value;
            else if (
                "password".equals(name) || "pw".equals(name))
                password = value;
            else
                throw new RuntimeException(
                    "unrecognized param name: '" + name + "'");
        }

        if (user == null
            || tableName == null
            || password == null)
            throw new RuntimeException("params expected: jdbc:url;user=<user>;pw=<password>;table=<tablename>");

        try {
            connection =
                DriverManager.getConnection(url, user, password);

            connection.setAutoCommit(false);
            ownConnection = true;

            init();
        }
        catch (SQLException e) {
            throw ChainedRuntimeException.create(e, null);
        }
    }

    void init() {

        try {
            Statement statement = connection.createStatement();
            //statement.executeUpdate ("DELETE FROM "+tableName);

            ResultSet dummy;
            try {
                dummy =
                    statement.executeQuery(
                        "SELECT * FROM " + tableName);
            }
            catch (SQLException e) {
                // ok, assume db does not exist...
                return;
            }

            ResultSetMetaData meta = dummy.getMetaData();

            for (int i = 1; i <= meta.getColumnCount(); i++) {
                // meta.getColumnName (i),
                int type;
                switch (meta.getColumnType(i)) {
                    case Types.CHAR :
                    case Types.VARCHAR :
                    case Types.LONGVARCHAR :
                        type = DbField.STRING;
                        break;
                    case Types.INTEGER :
                        type = DbField.INTEGER;
                        break;
                    case Types.NUMERIC :
                        if (meta.getPrecision(i) <= 16
                            && meta.getScale(i) == 0)
                            type =
                                meta.getPrecision(i) <= 8
                                    ? DbField.INTEGER
                                    : DbField.LONG;
                        else
                            type = DbField.DOUBLE;
                        break;
                    case Types.REAL :
                        type = DbField.DOUBLE;
                        break;
                    case Types.TIMESTAMP :
                        type = DbField.DATETIME;
                        break;
                    default :
                        throw new RuntimeException(
                            "unsupported field type: "
                                + meta.getColumnType(i));
                }
                addField(meta.getColumnName(i), type);
            }
            statement.close();

        }
        catch (SQLException e) {
            throw ChainedRuntimeException.create(e, null);
        }

        exists = true;
    }

    public int findField(String name) {
        int cnt = getFieldCount();
        for (int i = 0; i < cnt; i++)
            if (getField(i).getName().equals(name))
                return i;

        return -1;
    }

    public DbField addField(String name, int type) {
        int i = findField(name);
        if (i != -1)
            return getField(i);

        DbField f = new DbField(this, fields.size(), name, type);
        fields.addElement(f);
        return f;
    }

    public boolean isOpen() {
        return open;
    }

    protected void checkOpen(boolean required)
        throws DbException {
        if (open != required)
            throw new DbException(
                "DB must "
                    + (required ? "" : "not ")
                    + "be open");
    }

    public int getIdField() {
        return -1;
    }

    public String getName() {
        return tableName;
    }

    public String toString() {
        return tableName;
    }

    public void open() throws DbException {
        if (!exists)
            throw new DbException("Db does not exist!");
        checkOpen(false);
        open = true;
    }

    public void close() throws DbException {
        open = false;
        try {
            if (insertStatement != null)
                insertStatement.close();
            if (ownConnection)
                connection.close();
        }
        catch (SQLException e) {
            throw new DbException("" + e);
        }
    }

    public void delete() throws DbException {
        if (!exists)
            return;

        try {
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE " + tableName);
            exists = false;
            fields = new Vector();
        }
        catch (SQLException e) {
            throw new DbException("" + e);
        }
    }

  
    public boolean exists() {
        return exists;
    }

    public DbField getField(int index) {
        return (DbField) fields.elementAt(index);
    }

    public int getFieldCount() {
        return fields.size();
    }

   
    public DbRecord select(boolean updated) throws DbException {
        return select(null, null, -1, false, updated);
    }

    public DbRecord select(
        int[] fields,
        DbCondition condition,
        int sortfield,
        boolean inverse,
        boolean updated)
        throws DbException {

        checkOpen(true);

        StringBuffer buf = new StringBuffer("select ");
        buf.append("* ");
        buf.append("from ");
        buf.append(tableName);
        if (condition != null)
            buf.append(" where " + condition);

        try {
            return new JdbcRecord(
                this,
                fields,
                connection
                    .createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE)
                    .executeQuery(buf.toString()));
        }
        catch (SQLException e) {
            throw new DbException("" + e);
        }
    }
    /*
    	public void setIdFields(int[] idFields) throws DbException {
    
    		checkOpen(false);
    		this.idFields = idFields;
    		index = new Hashtable();
    	}
    
    	public void addField (
    
        
        public void loadRecord (Record record) {
            throw new RuntimeException ("NYI");
        }
    */

    public void create() throws DbException {
        checkOpen(false);

        // build 

        StringBuffer buf = new StringBuffer("CREATE TABLE ");
        buf.append(tableName);

        for (int i = 0; i < getFieldCount(); i++) {
            DbField f = getField(i);
            buf.append(i == 0 ? " (" : ", ");
            buf.append(f.getName());
            buf.append(' ');
            switch (f.getType()) {
                case Field.DOUBLE :
                case Field.INTEGER :
                case Field.LONG :
                    if (f.getConstraints() == 0)
                        buf.append(
                            "NUMBER (" + f.getMaxSize() + ")");
                    else
                        buf.append(
                            "NUMBER ("
                                + f.getMaxSize()
                                + ", "
                                + f.getConstraints()
                                + ")");
                    break;

                case Field.STRING :
                    buf.append(
                        "VARCHAR (" + f.getMaxSize() + ")");
                    break;

                default :
                    throw new RuntimeException(
                        "Unsupported type: " + f.getType());
            }
        }

        buf.append(")");

        try {
            connection.createStatement().execute(buf.toString());
            exists = true;
        }
        catch (SQLException e) {
            throw new DbException("faliled: " + buf + " / " + e);
        }
    }

    /*
      CREATE TABLE MO_PARROLID ( 
      VVID            NUMBER (16)   NOT NULL, 
      PRTYP           NUMBER (5)    NOT NULL, 
      PRTYPNR         NUMBER (5)    NOT NULL, 
      LASTVERSNR      NUMBER (5)    NOT NULL, 
      LASTROL_BEGINN  NUMBER, 
      LASTROL_ENDE    NUMBER, 
      LASTPTID        NUMBER (16)   NOT NULL, 
      PRIMARY KEY ( VVID, PRTYP, PRTYPNR ) ) ; 
    */

    /*
        public void saveRecord (Record record) {
            
            if (record.getId () != -1) throw new RuntimeException ("update NYI");
    
            if (!existing) create ();
    
            try {
    
                if (insertStatement == null) {
                    StringBuffer buf = new StringBuffer ("INSERT INTO ");
                    buf.append (name);
                    for (int i = 0; i < getFieldCount (); i++) {
                        buf.append (i == 0 ? " (" : ", ");
                        buf.append (getField (i).getName ());
                    }
    
                    buf.append (") VALUES ");
    
                    for (int i = 0; i < getFieldCount (); i++) { 
                         buf.append (i == 0 ? " (?" : ", ?");
                         // buf.append (i == 0 ? "( " : ", ");
                         // buf.append (""+record.getObject (i));
                    } 
                    
                    
                    buf.append (")");
                    insertStatement = connection.prepareStatement (buf.toString ());
                }
    
                // connection.createStatement ().executeUpdate (buf.toString ());
    
                for (int i = 0; i < getFieldCount (); i++) 
                    insertStatement.setObject (i+1, record.getObject (i));
                
                insertStatement.executeUpdate ();
    
            }
            catch (SQLException e) {
                throw ChainedRuntimeException.create (e, null); //, "failed: "+buf.toString () );
            }
    
    
            //ins
    
        }
    */

}