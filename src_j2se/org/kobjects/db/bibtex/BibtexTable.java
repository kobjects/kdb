package org.kobjects.db.bibtex;

import java.io.*;
import java.util.*;

import org.kobjects.db.*;
import org.kobjects.db.ram.*;

import java.net.*;

/** 
 * A DbTable implementation for bibtex databases. Creates an
 * id automatically. */

public class BibtexTable extends RamTable implements Runnable {

    protected String filename;
    protected String documentDir;
    protected long lastModified;
    
    static final String[] DEFAULT_FIELDS =
        {
            "author",
            "booktitle",
            "editor",
            "year",
            "month",
            "issue",
            "type"};

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

    static String generateId() {
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

    protected synchronized void reload() throws DbException {

        Vector rNew = new Vector();
        Hashtable iNew = new Hashtable();


		File file = new File(filename);
		
		
        exists = file.exists();
        
       System.out.println ("trying to (re)load bib file: "+file.getAbsoluteFile()+" existing:"+exists);
        
        if (exists) {
            try {
            	lastModified = file.lastModified();
            	
                BibtexParser parser =
                    new BibtexParser(
                        new BufferedReader(
                            new FileReader(file)));

                parser.parse();

                if (getFieldCount() == 0) {

                    for (int i = 0;
                        i < parser.fieldNames.size();
                        i++) {
                        String name =
                            (
                                String) parser
                                    .fieldNames
                                    .elementAt(
                                i);
                        if ("id".equals(name))
                            setIdField(i);
                        addField(name, DbField.STRING);
                    }

                    if (idField == -1) {
                        setIdField(getFieldCount());
                        addField("id", DbField.STRING);
                    }

                    for (int i = 0;
                        i < DEFAULT_FIELDS.length;
                        i++) {
                        if (findField(DEFAULT_FIELDS[i]) == -1)
                            addField(
                                DEFAULT_FIELDS[i],
                                DbField.STRING);
                    }
                    
                    addField("pdfFile", DbField.BINARY);
                    
                }

                // ensure equal record sizes

                for (int i = 0;
                    i < parser.entries.size();
                    i++) {

                    Object[] r =
                        (Object[]) parser.entries.elementAt(i);

                    if (r.length < getFieldCount()) {
                        Object[] n = new Object[getFieldCount()];
                        for (int j = 0; j < r.length; j++)
                            n[j] = r[j];

                        r = n;
                    }

                    if (r[idField] == null) {
                        r[idField] = generateId();
                    }

                    rNew.addElement(r);

                    iNew.put(r[idField], new Integer(i));
                }

                records = rNew;
                index = iNew;

            }
            catch (IOException e) {
                throw new DbException(e.toString());
            }
        }

    }

    public void connect(String connector) throws DbException {
        filename =
            connector.substring(connector.indexOf(':') + 1);

		documentDir = null;
		
		int cut = filename.indexOf(";");
		if (cut != -1) {
			documentDir	= filename.substring(cut+1);
			filename = filename.substring(0, cut);	
		}
		
        reload ();

    }

    public void open() throws DbException {
        super.open();
        new Thread(this).start();
    }

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

    protected void update(int i, Object[] entry)
        throws DbException {
        if (entry[idField] == null)
            entry[idField] = generateId();
        super.update(i, entry);
    }

    protected void writeEntry(BufferedWriter w, Object[] entry)
        throws IOException {

        w.write("@" + entry[0] + "{" + entry[1]);
        for (int j = 2; j < entry.length; j++) {
            if (entry[j] == null || "".equals(entry[j]))
                continue;
            w.write(',');
            w.newLine();
            w.write("  " + getField(j).getName() + " = ");
            String e = entry[j].toString();
            for (int k = 0; k < e.length(); k++) {
                char c = e.charAt(k);
                if ((c < '0' || c > '9')
                    && (c < 'a' || c > 'z')
                    && (c < 'A' || c > 'Z')) {
                    e = "{" + e + "}";
                    break;
                }
            }
            w.write(e);
        }
        w.newLine();
        w.write('}');
        w.newLine();
        w.newLine();
        w.newLine();

    }

    public synchronized void rewrite() throws DbException {
        System.out.println("BibtexTable: rewrite() triggered");
        try {
            File nf = new File(filename + ".new");
            BufferedWriter w =
                new BufferedWriter(new FileWriter(nf));
            for (int i = 0; i < records.size(); i++) {
                writeEntry(w, (Object[]) records.elementAt(i));
            }
            w.close();

            new File(filename + ".bak").delete();
            new File(filename).renameTo(
                new File(filename + ".bak"));
            nf.renameTo(new File(filename));

            modified = false;
        }
        catch (IOException e) {
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

    protected RamRecord getRecords(Vector selected, int[] fields) {
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