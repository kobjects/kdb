package org.kobjects.db.mp3;

import java.io.*;

import org.kobjects.db.*;
import org.kobjects.db.ram.*;
import java.util.*;

/**
 * @author haustein
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Mp3Table extends RamTable {

    String base;
    static int counter;

    /**
     * @see org.kobjects.db.DbTable#connect(String)
     */
    public void connect(String connector) throws DbException {

        base = connector.substring(4);
        exists = true;

        addField("id", DbField.STRING); // 0
        addField("file", DbField.STRING); // 1 
        addField("track", DbField.STRING); // 2
        addField("artist", DbField.STRING); // 3
        addField("album", DbField.STRING); // 4
        addField("year", DbField.STRING); // 5
        addField("comment", DbField.STRING); // 6 
        addField("data", DbField.BINARY); // 7

    }

    /**
     * @see org.kobjects.db.DbTable#getName()
     */
    public String getName() {
        return "mp3";
    }

    String clean(String s) {
        if (s == null)
            return null;
        int cut = s.indexOf('\0');
        if (cut != -1)
            s = s.substring(0, cut);
        return s.trim();
    }

    void addFile(File file) throws DbException {
        Object[] data = new Object[getFieldCount()];
        data[0] = "" + (counter++);
        data[1] = file.toString();
        records.addElement(data);

        if (file.length() > 128) {
            try {
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.seek(file.length() - 128);
                byte[] buf = new byte[128];
                raf.readFully(buf);
                raf.close();
                if (new String(buf, 0, 3).equals("TAG")) {
                    data[2] = clean(new String(buf, 3, 30)); // track
                    data[3] = clean(new String(buf, 33, 30)); //artist
                    data[4] = clean(new String(buf, 63, 30)); // album
                    data[5] = clean(new String(buf, 93, 4)); //year
                    data[6] = clean(new String(buf, 97, 127 - 97)); // comment
                }
            }
            catch (IOException e) {
                throw new DbException(e.toString());
            }
        }
    }

    void recurse(File dir) throws DbException {
        File[] files = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory())
                recurse(file);
            else if (file.getName().toLowerCase().endsWith(".mp3")) {
                addFile(file);
            }
        }
    }

    public void open() throws DbException {
        super.open();
        recurse(new File(base));
    }

    public static void main(String[] argv) throws DbException {

        DbTable table = DbManager.connect("mp3:/Users/haustein/Music");
        table.open();
        DbResultSet record = table.select(false);

        while (record.next()) {
            for (int i = 0; i < table.getFieldCount() - 1; i++) {
                System.out.println(record.getString(i));
            }
        }
    }

    protected RamRecord getRecords(Vector selected, int[] fields) {
        return new Mp3Record(this, selected, fields);
    }
}
