package org.kobjects.db.ls8bib;

import java.io.*;
import java.net.*;
import java.util.*;
import org.kobjects.db.bibtex.BibtexTable;
import org.kobjects.bibtex.BibtexParser;
import org.kobjects.db.*;

/**
 * @author Stefan Haustein
 *
 * Provides an additional emulation of eduards bibserver
 */

public class Ls8bibTable extends BibtexTable {

    class EmacsServer extends Thread {
        public void run() {

            try {
                ServerSocket serverSocket =
                    new ServerSocket(8410);

                while (true) {

                    try {
                        Socket socket = serverSocket.accept();
                        Reader reader =
                            new InputStreamReader(
                                socket.getInputStream());
                        PrintStream writer =
                            new PrintStream(
                                socket.getOutputStream());

                        if (!socket.getInetAddress().getHostAddress()
                            .startsWith("129.217.30."))
                            writer.println(
                                "connection refused outside ls8 net");
                        else {

                            BibtexParser bp =
                                new BibtexParser(reader);

                            try {
                                while (true) {
                                    Hashtable entry =
                                        bp.nextEntry();
                                    if (entry == null)
                                        break;
                                    update(entry, writer);
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace(writer);
                            }
                        }
                        reader.close();
                        writer.close();
                        socket.close();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void open() throws DbException {
        super.open();
        // necessary since BibServer has its own separate thread 
        new EmacsServer().start();
    }

    protected void update(int recordIndex, Object[] entry)
        throws DbException {

        if (entry != null) {
            String key = (String) entry[KEY_INDEX];

            if (key != null && key.length() > 0) {
                char c = key.charAt(key.length() - 1);

                //				Object[] addCopy = new Object[entry.length];
                //				System.arraycopy(entry, 0, addCopy, 0, entry.length); 

                // determine the new key locally

                if (c >= '0' && c <= '9') {
                    c = 'a';
                    for (int i = 0; i < records.size(); i++) {
                        Object[] r =
                            (Object[]) records.elementAt(i);
                        if (r == null)
                            continue;

                        String k2 = (String) r[KEY_INDEX];

                        if (k2 == null)
                            continue;

                        if (k2.startsWith(key)
                            && k2.length() == key.length() + 1) {
                            char c2 = k2.charAt(k2.length() - 1);

                            // make sure key is not incremented if already assigned

                            if (recordIndex == i) {
                                c = c2;
                                break;
                            }
                            if (c2 >= c)
                                c = (char) (((int) c2) + 1);
                        }
                    }
                    entry[KEY_INDEX] = key + c;
                }
            }
        }

        super.update(recordIndex, entry);
    }

    /** add or update an entry received via the socket connection */

    void update(Hashtable entry, PrintStream out)
        throws DbException {

        // first, determine the recordIndex. set 
        // recordIndex to -1 for new records

        int recordIndex = INSERT_ROW;

        String key = (String) entry.get("key");
        String id = (String) entry.get("id");

		if (key == null) {
			out.println ("entry without key not accepted!");
			return;
		}
		

        Integer box = null;
        boolean delete = false;

        if (key != null) {
            if (key.startsWith("*")) {
                key = key.substring(1);
                out.println("deleting entry: " + key);
                update(
                    ((Integer) idTable.get(key)).intValue(),
                    null);
                return;
            }
            box = (Integer) idTable.get(key);
        }

        if (box == null && id != null)
            box = (Integer) idTable.get(id);

        if (box != null) {
            recordIndex = box.intValue();
            out.print("replacing entry;");
        }
        else {
            out.print("adding new entry;");
        }

        out.println(" id: " + id + " key: " + key);

        // ok, now fill the record from the entry

        Object[] record = new Object[getPhysicalFieldCount()];

        for (Enumeration e = entry.keys();
            e.hasMoreElements();
            ) {
            String name = (String) e.nextElement();
            int i = findField(name);
            if (i > 0) {
                String value = (String) entry.get(name);
                record[i-1] = value;
                out.println("- " + name + ": " + value);
            }
            else {
                out.println("- ignoring unknown field: " + name);
            }
        }

        update(recordIndex, record);

        out.println(
            "record (new key: "
                + record[KEY_INDEX]
                + ") updated; bibtex file will be rewritten in up to 15 seconds");
        out.println();
    }

}