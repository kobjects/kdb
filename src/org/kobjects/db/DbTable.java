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


    protected boolean open;


    protected Vector fields = new Vector();



    public abstract boolean exists();


    /**
     * Factory method mit load by name? Braucht wohl zweite Methode zum
     * connecten. Namensschema: Db<typ>Table, mit <typ> erster Buchstabe groß, Rest
     * klein.
     */
    public abstract void connect (String connector);


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


    /*
    public void deleteRecord(int id) throws DbException {
        checkOpen(true);
        saveRecord(id, null);
    }
    */


    public abstract DbRecord select (Object id) throws DbException;


    public abstract DbRecord select (DbCondition filter, int orderField, boolean orderReverse, boolean updated) throws DbException;
    /* {
        checkOpen(true);

        try {
            DBIndex index = new DBIndex(this);
            index.setFilter(filter);
            if (orderField != -1) index.setOrder(orderField, orderReverse);
            RecordEnumeration enum = store.enumerateRecords(index, (orderField != -1 ? index : null), updated);
            return new DBRecord(this, enum);
        }
        catch (RecordStoreException e) {
            throw new DbException("Error selecting from table " + name + " (" + e.getClass().getName() + ")");
        }
        }*/
}
