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

// must be abstract class not interface because of addField

public abstract class DbTable {

    public abstract boolean exists();

    /**
     * Factory method mit load by name? Braucht wohl zweite Methode zum
     * connecten. Namensschema: Db<typ>Table, mit <typ> erster Buchstabe groß, Rest
     * klein.
     */
    public static DbTable connect(String connector) throws DbException {
        DbTable table = null;

        try {
            int p = connector.indexOf(':');
            String proto = connector.substring(0, p);
            String cls   = Character.toUpperCase(proto.charAt(0)) + proto.substring(1);
            if ("https".equals(cls)) cls = "http";

            System.out.println(proto + " / " + cls + " org.kobjects.db." + proto + "." + cls + "Table");

            table = (DbTable)Class.forName("org.kobjects.db." + proto + "." + cls + "Table").newInstance();
        }
        catch (Exception e) {
            throw new DbException("Can't connect to table \"" + connector + "\" (" + e.getClass().getName() + ")");
        }

        table.init(connector);
        return table;
    }

    protected abstract void init(String connector);

    public abstract void delete() throws DbException;

    public abstract void create() throws DbException;

    public abstract void open() throws DbException;

    public abstract DbField addField(String name, int type) throws DbException;

    public abstract int getFieldCount();

    public abstract DbField getField(int i);

    public int findField(String name) {
        int cnt = getFieldCount ();
        for (int i = 0; i < cnt; i++) 
            if (getField (i).getName ().equals (name)) 
                return i;

        return -1;
    }

    public abstract void close();

    /**
     * To delete records without having to refresh them first. This method's
     * semantics is as follows: select(id).next.delete();
     */
    public abstract void deleteRecord(Object id) throws DbException;

    /**
     * Select all.
     */
    public DbRecord select(boolean updated) throws DbException {
         return select(null, -1, false, updated);
    }

    public abstract DbRecord select (Object id) throws DbException;

    public abstract DbRecord select (DbCondition filter, int orderField, boolean orderReverse, boolean updated) throws DbException;
}
