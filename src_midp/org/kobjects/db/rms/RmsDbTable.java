package org.kobjects.db.rms;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import javax.microedition.rms.*;
import org.kobjects.db.*;

public class RmsDbTable extends DbTable {

    private static final String SIGNATURE = "RMS IS CRAP!";

    private static final int RECORD_ID_HEADER = 1;
    private static final int RECORD_ID_FUTURE = 2;
    private static final int RECORD_ID_RECORD = 3;

    private String name;

    private RecordStore store;

    private Vector fields = new Vector();

    public RmsDbTable () {
    }

    public RmsDbTable(String name) {
	connect (name);
    }

    public void connect (String name) {
        this.name = name;
    }

    public boolean exists() {
        String[] names = RecordStore.listRecordStores();

        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                if (names[i].equals(name)) return true;
            }
        }

        return false;
    }

    private void checkOpen(boolean open) throws DbException {
        if ((store == null) == open) {
            throw new DbException("Table \"" + name + "\" not " + (open ? "open" : "closed"));
        }
    }

    public void delete() throws DbException {
        checkOpen(false);

        try {
            RecordStore.deleteRecordStore(name);
        }
        catch (Exception e) {
            throw new DbException("Cannot delete table \"" + name + "\" (" + e.getClass().getName() + ")");
        }
    }

    public void create() throws DbException {
        checkOpen(false);

        if (exists()) delete();

        try {
            saveHeader();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new DbException("Cannot create table \"" + name + "\" (" + e.getClass().getName() + ")");
        }
    }

    public void open() throws DbException {
        checkOpen(false);

        try {
            store = RecordStore.openRecordStore(name, false);
            loadHeader();
        }
        catch (Exception e) {
            store = null;
            throw new DbException("Cannot open table \"" + name + "\" (" + e.getClass().getName() + ")");
        }
    }

    private void saveHeader() throws IOException, RecordStoreException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);

        data.writeInt(0);
        data.writeUTF(SIGNATURE);

        data.writeByte(fields.size());

        Object[] defaults = new Object[fields.size()];

        for (int i = 0; i < fields.size(); i++) {
            DbField field = (DbField)fields.elementAt(i);

            data.writeUTF(field.getName());
            data.writeByte(field.getType());

            if (field.getLabel() != null) {
                data.writeBoolean(true);
                data.writeUTF(field.getLabel());
            }
            else {
                data.writeBoolean(false);
            }
            data.writeInt(field.getMaxSize());
            data.writeInt(field.getConstraints());

            String[] values = field.getValues();
            if (values != null) {
                data.writeInt(values.length);

                for (int j = 0; j < values.length; j++) {
                    data.writeUTF(values[j]);
                }
            }
            else data.writeInt(0);

            defaults[i] = field.getDefault();
        }

        byte[] packedDefaults = pack(0, defaults);
        data.writeInt(packedDefaults.length);
        data.write(packedDefaults);

        RecordStore store = RecordStore.openRecordStore(name, true);

        try {
            if (store.getNextRecordID() == RECORD_ID_HEADER) {
                store.addRecord(bytes.toByteArray(), 0, bytes.size());
                store.addRecord(new byte[4], 0, 4);
            }
            else {
                store.setRecord(RECORD_ID_HEADER, bytes.toByteArray(), 0, bytes.size());
            }
        }
        finally {
            store.closeRecordStore();
        }
    }

    private void loadHeader() throws IOException, RecordStoreException, DbException {
        ByteArrayInputStream bytes = new ByteArrayInputStream(store.getRecord(1));
        DataInputStream data = new DataInputStream(bytes);

        int zero = data.readInt();
        String signature = data.readUTF();
        if ((zero != 0) || !SIGNATURE.equals(signature)) {
            throw new DbException("Illegal signature \"" + zero + ", " + signature + "\" in table \"" + name + "\"");
        }

        int fieldCount = data.readByte();

        for (int i = 0; i < fieldCount; i++) {
            
	    DbField field = addField ( data.readUTF(), data.readByte());
	    // new DbField(i, data.readUTF(), data.readByte());
            //fields.addElement(field);

            String label = (data.readBoolean() ? data.readUTF() : null);
            int maxSize = data.readInt();
            int constraints = data.readInt();

            String[] values = null;

            int numValues = data.readInt();
            if (numValues != 0) {
                values = new String[numValues];

                for (int j = 0; j < numValues; j++) {
                    values[j] = data.readUTF();
                }
            }

            field.setProperties(label, maxSize, constraints, values);
        }

        byte[] packedDefaults = new byte[data.readInt()];
        data.readFully(packedDefaults);

        Object[] defaults = new Object[fields.size()];
        unpack(packedDefaults, defaults);

        for (int i = 0; i < fields.size(); i++) {
            ((DbField)fields.elementAt(i)).setDefault(defaults[i]);
        }
    }

    public DbField addField(String name, int type) throws DbException {
        checkOpen(false);

	// if (findField(name) != -1) {
	//     throw new DbException("Field \"" + name + "\" already exists in table \"" + this.name + "\"");
	// }

        if (fields.size() == 255) {
            throw new DbException("Field IDs in table \"" + this.name + "\" exhausted.");
        }

	//        DbField field = addField (name, type); 
	//new DbField(fields.size(), name, type);
	// fields.addElement(field);

        return super.addField (name, type);
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

    byte[] pack(int id, Object[] values) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);

        data.writeInt(id);

        for (int i = 0; i < getFieldCount(); i++) {
            Object obj = values[i];

            if (obj != null) {
                data.writeByte(i);

                switch (getField(i).getType()) {
                    case DbField.BOOLEAN: {
                        data.writeBoolean(((Boolean)obj).booleanValue());
                        break;
                    }

                    case DbField.INTEGER:
                    case DbField.BITSET:{
                        data.writeInt(((Integer)obj).intValue());
                        break;
                    }

                    case DbField.LONG:
                    case DbField.DATETIME: {
                        data.writeLong(((Long)obj).longValue());
                        break;
                    }

                    case DbField.STRING: {
                        data.writeUTF(obj.toString());
                        break;
                    }

                    case DbField.BINARY:
                    case DbField.GRAPHICS: {
                        byte[] bin = (byte[])obj;
                        data.writeInt(bin.length);
                        data.write(bin);
                        break;
                    }
                }
            }
        }

        return bytes.toByteArray();
    }

    int unpack(byte[] record, Object[] values) throws IOException {
        ByteArrayInputStream bytes = new ByteArrayInputStream(record);
        DataInputStream data = new DataInputStream(bytes);

        int id = data.readInt();

        while (data.available() != 0) {
            int column = data.readByte();

            if (column != -1) {
                int type = getField(column).getType();

                Object o = null;

                switch (type) {

                    case DbField.BOOLEAN: {
                        o = new Boolean(data.readBoolean());
                        break;
                    }

                    case DbField.INTEGER:
                    case DbField.BITSET: {
                        o = new Integer(data.readInt());
                        break;
                    }

                    case DbField.LONG:
                    case DbField.DATETIME: {
                        o = new Long(data.readLong());
                        break;
                    }

                    case DbField.STRING: {
                        o = new String(data.readUTF());
                        break;
                    }

                    case DbField.BINARY:
                    case DbField.GRAPHICS: {
                        byte[] bin = new byte[data.readInt()];
                        data.readFully(bin);
                        o = bin;
                        break;
                    }
                }

                values[column] = o;
            }
        }

        return id;
    }

    public void close() {
        try {
            store.closeRecordStore();
            store = null;
        }
        catch (RecordStoreException e) {
        }
    }

    void loadRecord(int id, Object[] values) throws DbException {
        checkOpen(true);

        try {
            byte[] record = store.getRecord(id);
            unpack(record, values);
        }
        catch (Exception e) {
            throw new DbException("Error reading record #" + id + " (" + e.getClass().getName() + ")");
        }
    }

    int saveRecord(int id, Object[] values) throws DbException {
        checkOpen(true);

        try {
            if (values == null) {
                store.deleteRecord(id);
            }
            else if (id == 0) {
                id = store.getNextRecordID();
                byte[] packed = pack(id, values);
                store.addRecord(packed, 0, packed.length);
            }
            else {
                byte[] packed = pack(id, values);
                store.setRecord(id, packed, 0, packed.length);
            }

            return id;
        }
        catch (Exception e) {
            throw new DbException("Error writing record #" + id + " (" + e.getClass().getName() + ")");
        }
    }

    public void deleteRecord(int id) throws DbException {
        checkOpen(true);
        saveRecord(id, null);
    }


    public DbRecord select(Object id) throws DbException {
	throw new RuntimeException ("NYI");
    }

    public DbRecord select(DbExpression filter, int orderField, boolean orderReverse, boolean updated) throws DbException {
        checkOpen(true);

        try {
            RmsDbIndex index = new RmsDbIndex(this);
            index.setFilter(filter);
            if (orderField != -1) index.setOrder(orderField, orderReverse);
            RecordEnumeration enum = store.enumerateRecords(index, (orderField != -1 ? index : null), updated);
            return new RmsDbRecord(this, enum);
        }
        catch (RecordStoreException e) {
            throw new DbException("Error selecting from table " + name + " (" + e.getClass().getName() + ")");
        }
    }

    public DbRecord select(int filterField, Object filterValue, boolean filterInverse, int orderField, boolean orderReverse, boolean updated) throws DbException {
        checkOpen(true);

        try {
            RmsDbIndex index = new RmsDbIndex(this);
            if (filterField != -1) index.setFilter(filterField, filterValue, filterInverse);
            if (orderField != -1) index.setOrder(orderField, orderReverse);
            RecordEnumeration enum = store.enumerateRecords(index, (orderField != -1 ? index : null), updated);
            return new RmsDbRecord(this, enum);
        }
        catch (RecordStoreException e) {
            throw new DbException("Error selecting from table " + name + " (" + e.getClass().getName() + ")");
        }
    }
}
