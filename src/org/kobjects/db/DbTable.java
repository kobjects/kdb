package org.kobjects.db;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Jörg Pleumann
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import javax.microedition.rms.*;

/**
 * Represents a database table.
 */
public abstract class DbTable {

    /**
     * Holds the fields that are defined for this table.
     */
    protected Vector fields = new Vector();

    /**
     * Connects to a table. This factory method examines the given connector and
     * loads a suitable table implementation by name, connecting it to the given
     * table. The result is the same as for instatiating and connecting the
     * table explicitly, i.e. the table is not opened. The connector string
     * obeys the following URI-like naming scheme:
     * <pre>
     *   "protocol:table;parameters"
     * </pre>
     * The actual table implementation is chosen depending on the "protocol"
     * value. The package name always is "org.kobjects.db" plus a sub-package
     * named after the protocol. The class name consists of the protocol values
     * with the first letter being uppercased plus the fixed suffix "Table". As
     * an example, for a connector string
     * <pre>
     *   "rms:MyTable;user=joerg;password=secret"
     * </pre>
     * the class <code>org.kobjects.db.rms.RmsTable</code> would be instantiated
     * and the new instance connects to a table "MyTable". Interpretation of the
     * parameters following the ";" is up to the table implementation.
     */
    public static DbTable connect(String connector) throws DbException {
        DbTable table = null;

        try {
            int p = connector.indexOf(':');
            String type = connector.substring(0, p);
            if ("https".equals(type)) type = "http";
            String name = Character.toUpperCase(type.charAt(0)) + type.substring(1);

            table = (DbTable)Class.forName("org.kobjects.db." + type + "." + name + "Table").newInstance();
        }
        catch (Exception e) {
            throw new DbException("Can't connect to table \"" + connector + "\"", e);
        }

        table.init(connector);

        return table;
    }

    /**
     * Checks the table's state. The method throws an exception if the table's
     * actual open/closed state doesn't match the required state.
     */
    protected void checkState(boolean open) throws DbException {
        if (isOpen() != open) {
            throw new DbException("Table \"" + getName() + "\" not " + (open ? "open" : "closed"));
        }
    }

    /**
     * Is called to connect to a table. Note that the given connector follows
     * the syntax used in the connect() factory method, so the actual table
     * name has to be derived from it.
     */
    protected abstract void init(String connector);

    /**
     * Returns the table's name.
     */
    public abstract String getName();
    /**
     * Returns true if the table exists and false otherwise.
     */
    public abstract boolean exists();

    /**
     * Returns true if the table is open and false otherwise.
     */
    public abstract boolean isOpen();

    /**
     * Deletes the table. Succeeds if the table doesn't exist. Throws an error
     * if the table cannot be deleted for some reason.
     */
    public abstract void delete() throws DbException;

    /**
     * Creates the table. This method creates a new physical table or overwrites
     * an existing one. The method can only be called when the table is closed.
     * The new table will not automatically be opened.
     */
    public abstract void create() throws DbException;

    /**
     * Opens the table. Must throw an exception if the table is already open or
     * some other error occurs.
     */
    public abstract void open() throws DbException;

    /**
     * Closes the table. Always succeeds.
     */
    public abstract void close();

    /**
     * Adds a field to the table. The table must be closed for this method
     * to succeed.
     */
    public DbField addField(String name, int type) throws DbException {
        checkState(false);

        if (findField(name) != -1) {
            throw new DbException("Field \"" + name + "\" already exists in table \"" + getName() + "\"");
        }

        if (fields.size() == 255) {
            throw new DbException("Field IDs in table \"" + getName() + "\" exhausted.");
        }

        DbField field = new DbField(this, fields.size(), name, type);
        fields.addElement(field);

        return field;
    }

    /**
     * Returns the total number of fields.
     */
    public int getFieldCount() {
        return fields.size();
    }

    /**
     * Returns the field with the given index.
     */
    public DbField getField(int i) {
        return (DbField)fields.elementAt(i);
    }

    /**
     * Returns the index of the given field or -1 if the field does not exist.
     */
    public int findField(String name) {
        for (int i = 0; i < fields.size(); i++) {
            if (getField(i).getName().equals(name)) return i;
        }

        return -1;
    }

    /**
     * Select all records. This is a convenience method. The usual rules for
     * the "big" select() apply.
     */
    public DbRecord select(boolean updated) throws DbException {
         return select(null, -1, false, updated);
    }

    /**
     * Selects the record that bears the given ID. This is a convenience
     * method. The usual rules for the "big" select() apply.
     */
    public DbRecord select(Object id) throws DbException {
        return select(new DbCondition(DbCondition.EQ, -1, id), -1, false, false);
    }

    /**
     * Select all records for which the given condition evaluates to
     * true. If the given orderField is a valid field number, the result set
     * is sorted according to that field. If the orderField is -1, the result
     * set is sorted according to the records' IDs. The order can be reversed
     * using the correspoding parameter. The last parameter decides whether the
     * whole result set is kept updated or not. The returned result set is
     * positioned before the first record.
     */
    public abstract DbRecord select(DbCondition filter, int oderBy, boolean reverse, boolean updated) throws DbException;
}
