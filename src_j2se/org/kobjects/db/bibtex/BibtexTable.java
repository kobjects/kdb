package org.kobjects.db.bibtex;

import java.io.*;
import java.util.*;

import org.kobjects.db.*;
import org.kobjects.db.ram.*;
import org.kobjects.bibtex.*;

import java.net.*;

/** 
 * A DbTable implementation for bibtex databases. Creates an
 * id automatically. */

public class BibtexTable extends RamTable {

    class SaveThread extends Thread {
        public void run() {
            while (open) {
                try {
                    Thread.sleep(15000);
                    if (modified)
                        rewrite();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // type and key MUST be first field (assumed by writer)

    static final String[] DEFAULT_FIELDS =
        {
            "bibtype",
            "bibkey",
            "id",
            "address",
            "author",
            "title",
            "chapter",
            "crossref",
            "edition",
            "editor",
            "howpublished",
            "institution",
            "journal",
            "key",
            "month",
            "note",
            "number",
            "organization",
            "pages",
            "publisher",
            "school",
            "series",
            "title",
            "type",
            "volume",
            "year" };

    /** Base 0 Key field index */

    protected static final int BIBKEY_INDEX = 1;

    /** Base 0 id field index */

    protected static final int ID_INDEX = 2;

    /** Maps IDs to record numbers (base 1) */

    protected Hashtable idTable = new Hashtable();

    /** Maps keys to record numbers (base 1) */

    protected TreeMap keyTable = new TreeMap();

    int physicalFields = -1;

    protected String filename;
    protected String documentDir;

    public BibtexTable() {
    }

    public BibtexTable(String filename) throws DbException {
        connect("bibtex:" + filename);
    }

    static void hex(StringBuffer buf, long l, int digits) {
        String h = Long.toHexString(l);
        for (int i = h.length(); i < digits; i++)
            buf.append('0');

        buf.append(h);
    }

    protected static String generateId() {
        long time0 = System.currentTimeMillis();
        long time = System.currentTimeMillis();

        while (time == time0) {
            Thread.yield();
            time = System.currentTimeMillis();
        }

        while (time == System.currentTimeMillis()) {
            Thread.yield();
        }

        byte[] adr;

        try {
            adr = InetAddress.getLocalHost().getAddress();
        }
        catch (Exception e) {
            adr = new byte[0];
        }

        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < adr.length; i++)
            hex(buf, ((int) adr[i]) & 255, 2);

        hex(buf, time, 16);

        return buf.toString();
    }

    public void connect(String connector) throws DbException {
        filename =
            connector.substring(connector.indexOf(':') + 1);

        documentDir = null;

        int cut = filename.indexOf(";");
        if (cut != -1) {
            documentDir = filename.substring(cut + 1);
            filename = filename.substring(0, cut);
        }

        File file = new File(filename);

        exists = file.exists();

        System.out.println(
            "trying to (re)load bib file: "
                + file.getAbsoluteFile()
                + " existing:"
                + exists);

        for (int i = 0; i < DEFAULT_FIELDS.length; i++) {
            if (findColumn(DEFAULT_FIELDS[i]) <= 0)
                addField(DEFAULT_FIELDS[i], DbColumn.STRING);
        }

        if (exists) {
            try {
                Reader reader =
                    new BufferedReader(new FileReader(file));
                BibtexParser parser = new BibtexParser(reader);

                int fields = getColumnCount();
                int lastInc = 0;

                while (true) {
                    Hashtable entry = parser.nextEntry();
                    if (entry == null)
                        break;
                    Object[] dst = new Object[fields];

                    for (Enumeration e = entry.keys();
                        e.hasMoreElements();
                        ) {
                        String name = (String) e.nextElement();

                        int i = findColumn(name);

                        if (i <= 0) {
                            addField(name, DbColumn.STRING)
                                .getNumber();
                            fields++;
                            Object[] tmp = new Object[fields];
                            System.arraycopy(
                                dst,
                                0,
                                tmp,
                                0,
                                dst.length);
                            dst = tmp;
                            i = fields;
                        }
                        dst[i - 1] = entry.get(name);
                    }

                    if (dst[ID_INDEX] == null)
                        dst[ID_INDEX] = generateId();

                    Integer box = new Integer(records.size());
                    idTable.put(dst[ID_INDEX], box);

                    if (dst[BIBKEY_INDEX] != null) {
                        keyTable.put(dst[BIBKEY_INDEX], box);
                    }

                    records.addElement(dst);
                }

                reader.close();
            }
            catch (IOException e) {
                throw new DbException(e.toString());
            }

            // ensure equal record sizes

            for (int i = 0; i < records.size(); i++) {

                Object[] r = (Object[]) records.elementAt(i);
                Object[] s = new Object[getColumnCount()];
                if (r.length == s.length)
                    break;
                System.arraycopy(r, 0, s, 0, r.length);
                records.setElementAt(s, i);
            }
        }

        physicalFields = getColumnCount();
        addField("pdfFile", DbColumn.BINARY);
    }

    public int getPhysicalFieldCount() {
        return physicalFields == -1
            ? getColumnCount()
            : physicalFields;
    }

    public void open() throws DbException {
        super.open();
        new SaveThread().start();
    }

    protected synchronized void update(
        int recordIndex,
        Object[] entry)
        throws DbException {

        if (recordIndex != INSERT_ROW) {
            Object[] old =
                (Object[]) records.elementAt(recordIndex);
            idTable.remove(old[ID_INDEX]);
            keyTable.remove(old[BIBKEY_INDEX]);
        }

        if (entry != null && entry[ID_INDEX] == null)
            entry[ID_INDEX] = generateId();

        super.update(recordIndex, entry);

        if (entry != null) {
            Integer box =
                new Integer(
                    recordIndex == INSERT_ROW
                        ? records.size() - 1
                        : recordIndex);

            idTable.put(entry[ID_INDEX], box);

            if (entry[BIBKEY_INDEX] != null)
                keyTable.put(entry[BIBKEY_INDEX], box);
        }

        modified = true;
    }

    protected void writeEntry(BibtexWriter bw, Object[] entry)
        throws IOException {
        	
        if (entry == null) return;
        	
        bw.startEntry((String) entry[0], (String) entry[1]);

        for (int j = 2; j < entry.length; j++) {

            if (entry[j] == null || "".equals(entry[j]))
                continue;

            bw.writeField(
                getColumn(j + 1).getName(),
                entry[j].toString());
        }

        bw.endEntry();
    }

    public synchronized void rewrite() throws DbException {
        System.out.println("BibtexTable: rewrite() triggered");
        try {
            File nf = new File(filename + ".new");
            BufferedWriter w =
                new BufferedWriter(new FileWriter(nf));

            BibtexWriter bw = new BibtexWriter(w);

            modified = false;

            for (Iterator i = keyTable.keySet().iterator();
                i.hasNext();
                ) {
                Integer box = (Integer) keyTable.get(i.next());
                writeEntry(
                    bw,
                    (Object[]) records.elementAt(
                        box.intValue()));
            }
            bw.close();
            w.close();

            new File(filename + ".bak").delete();
            new File(filename).renameTo(
                new File(filename + ".bak"));
            nf.renameTo(new File(filename));
        }
        catch (IOException e) {
            modified = true;
            throw new DbException("" + e);
        }
        System.out.println("BibtexTable: rewrite() finished");
    }

    public void close() throws DbException {
        if (modified) {
            rewrite();
        }
        super.close();
    }

    public int getIdField() {
        return ID_INDEX + 1;
    }

    protected RamResultSet getRecords(
        Vector selected,
        int[] fields) {
        return new BibtexRecord(this, selected, fields);
    }

    /*
    	public static void main(String argv[]) throws DbException {
    
    		DbTable table = DbManager.connect("bibtex:" + argv[0]);
    
    		table.open();
    
    		DbRecord r = table.select(false);
    
    		while (r.hasNext()) {
    			r.next();
    
    			System.out.println(r.getId());
    		}
    	}
    */
}