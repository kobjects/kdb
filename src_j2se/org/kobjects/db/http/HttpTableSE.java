package org.kobjects.db.http;

import org.kobjects.util.*;
import org.kobjects.db.*;
import java.net.*;
import java.io.*;

import java.util.*;

public class HttpTableSE implements DbTable {

    String url;
    String name;
    Vector fields = new Vector();
   // int[] idFields;
    char conjunction;
    boolean exists;
    boolean open;
    int idField = -1;

    public HttpTableSE() {
    }

    public HttpTableSE(String connector) throws DbException {
        connect(connector);
    }

    public DbField addField(String name, int type) {
        int i = findField(name);
        if (i != -1)
            return getField(i);

        DbField f = new DbField(this, fields.size(), name, type);
        fields.addElement(f);
        return f;
    }

    protected void checkOpen(boolean required)
        throws DbException {
        if (open != required)
            throw new DbException(
                "DB must "
                    + (required ? "" : "not ")
                    + "be open");
    }

    public void connect(String connector) throws DbException {

        url = connector;
        conjunction = connector.indexOf('?') == -1 ? '?' : '&';

        try {
            HttpURLConnection connection =
                (HttpURLConnection) new URL(url
                    + conjunction
                    + "cmd=describe")
                    .openConnection();

            InputStream is = connection.getInputStream();
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(is));

            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                String[] f = Csv.decode(line);
                
                System.out.println("line: "+line);
                for (int i = 0; i < f.length; i++) {
                    System.out.println ("part "+i+": "+f[i]);
                }
                
                addField(f[0], Field.STRING);
                // 1 type
                // 2 len
                // 3 dec
                // 4 isId
                if ("true".equals(f[4])) {
                    if (idField != -1) 
                        System.err.println ("Only first ID field was accepted!");
                    else
                        idField = getFieldCount()-1;
                }
            }

            /* determine primaryKey			
            connection =
                (HttpURLConnection) new URL(url
                    + conjunction
                    + "cmd=primaryKey")
                    .openConnection();

            is = connection.getInputStream();
            reader =
                new BufferedReader(new InputStreamReader(is));

            String line = reader.readLine();
            if (line == null)
                throw new DbException("no primary key!?!");
            else {
                String key = Csv.decode(line)[0];
                int index = findField(key);
                System.out.println(
                    "id field is: " + key + " index: " + index);
                setIdFields(new int[] { index });
            }
            */

            exists = true;

        }
        catch (IOException e) {
            throw new DbException(e.toString(), e);
        }
    }

    public void create() {
        throw new RuntimeException("NYI");
    }

    public void delete() {
        throw new RuntimeException("NYI");
    }

    public boolean exists() {
        return exists;
    }

    public int getIdField() {
        return idField;
    }

    public int findField(String name) {
        int cnt = getFieldCount();
        for (int i = 0; i < cnt; i++)
            if (getField(i).getName().equals(name))
                return i;

        return -1;
    }

    public String getName() {
        return url;
    }

    public boolean isOpen() {
        return open;
    }
/*
    protected Object getId(Object[] record) {
        if (idFields == null)
            throw new RuntimeException("ID must be defined for HTTPConnection!");

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < idFields.length; i++)
            buf.append(record[idFields[i]].toString());

        return buf.toString();
    }
*/
    public DbField getField(int index) {
        return (DbField) fields.elementAt(index);
    }

    public int getFieldCount() {
        return fields.size();
    }

    public DbRecord select(boolean update) throws DbException {
        return select(null, null, -1, false, update);
    }



    static String urlEncode(String s) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ' ')
                buf.append("%20");
            else
                buf.append(c);
        }
        return buf.toString();
    }

    public DbRecord select(
        int[] fields,
        DbCondition condition,
        int sortfield,
        boolean inverse,
        boolean updated)
        throws DbException {

        try {
            StringBuffer buf = new StringBuffer(url);
            buf.append(conjunction);
            buf.append("cmd=select");

            if (fields != null) {
                buf.append("&fields=");
                buf.append(getField (fields[0]).getName());
                boolean addId = idField != -1;
                for (int i = 1; i < fields.length; i++) {
                    buf.append(",");
                    if (fields [i] == idField) addId = false;
                    buf.append(getField (fields[i]).getName());
                }
                if (addId) {
                    buf.append(",");
                    buf.append(getField (idField).getName());
                }
            }
            if (condition != null) {
                buf.append("&where=");
                buf.append(urlEncode(condition.toString()));
            }

            HttpURLConnection connection =
                (HttpURLConnection) new URL(buf.toString ())
                    .openConnection();

            InputStream is = connection.getInputStream();
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(is));

            Vector selected = new Vector();

            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                Object[] src = Csv.decode(line);
                if (fields == null)
                    selected.addElement(src);
                else {
                    Object[] dst = new Object[getFieldCount()];
                    for (int i = 0; i < fields.length; i++)
                        dst[fields[i]] = src[i];

                    selected.addElement(dst);
                }
            }

            is.close();
            //connection.close ();

            return new HttpRecord(this, fields, selected);
        }
        catch (IOException e) {
            throw new DbException(e.toString(), e);
        }
    }

/*
    public void setIdFields(int[] idFields) throws DbException {
        checkOpen(false);
        this.idFields = idFields;
    }
*/

    public void open() {
        open = true;
    }

    public static void main(String[] argv) throws DbException {
        HttpTableSE table =
            new HttpTableSE("http://localhost:1234/Westkunden");

        for (int i = 0; i < table.getFieldCount(); i++) {
            System.out.println(table.getField(i));
        }
    }

    /*	public void loadRecord(Record record) {
    		if (record.getId() == 0 || record.getId() > allRecords.size())
    			throw new IllegalArgumentException();
    
    		String[] o = (String[]) allRecords.elementAt(record.getId() - 1);
    		for (int i = 0; i < getFieldCount(); i++) {
    			record.setString(i, o[i]);
    		}
    	}
    
    	public void saveRecord(Record record) {
    		throw new RuntimeException("NYI");
    	}
    */
    public void close() {
    }

}