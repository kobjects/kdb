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

    protected Vector fields = new Vector();

    public abstract boolean exists();

    /**
     * Factory method mit load by name? Braucht wohl zweite Methode zum
     * connecten. Namensschema: Db<typ>Table, mit <typ> erster Buchstabe groß, Rest
     * klein.
     */
    public static DbTable connect(String connector) throws DbException {
        try {
            int p = connector.indexOf(':');
            String proto = connector.substring(0, p);
            String cls   = Character.toUpperCase(proto.charAt(0)) + proto.substring(1);
            if ("https".equals(cls)) cls = "http";

            System.out.println(proto + " / " + cls + " org.kobjects.db." + proto + "." + cls + "Table");

            DbTable table = (DbTable)Class.forName("org.kobjects.db." + proto + "." + cls + "Table").newInstance();
            table.init(connector);

            return table;
        }
        catch (Exception e) {
            throw new DbException("Can't connect to table \"" + connector + "\" (" + e.getClass().getName() + ")");
        }
    }

    protected abstract void init(String connector);


    public abstract void delete() throws DbException;


    public abstract void create() throws DbException;


    public abstract void open() throws DbException;


    public DbField addField(String name, int type) throws DbException {
        //checkOpen(false);

        if (findField(name) != -1) {
            throw new DbException
                ("Field \"" + name + "\" already exists in "+this);
        }

        //        if (fields.size() == 255) {
        //    throw new DbException("Field IDs in table \"" + this.name + "\" exhausted.");
        //}

        DbField field = new DbField(fields.size(), name, type);
        fields.addElement(field);

        return field;
    }

    public int getFieldCount() {
        return fields.size();
    }

    public DbField getField(int i) {
        return (DbField)fields.elementAt(i);
    }

    public int findField(String name) {
        for (int i = 0; i < fields.size(); i++) {
            if (getField(i).getName().equals(name)) return i;
        }

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
