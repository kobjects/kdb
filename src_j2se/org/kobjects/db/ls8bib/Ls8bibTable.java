package org.kobjects.db.ls8bib;

import java.io.*;
import java.net.*;
import java.util.*;
import org.kobjects.db.bibtex.BibtexTable;
import org.kobjects.bibtex.*;
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
                        Writer writer =
                            new OutputStreamWriter(
                                socket.getOutputStream());

                        writer.write("Start!\r\n");
                        writer.flush();

                        if (!socket
                            .getInetAddress()
                            .getHostAddress()
                            .startsWith("129.217.30."))
                            writer.write(
                                "connection refused outside ls8 net\r\n");
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

                                if (modified) {
                                    writer.write(
                                        "The Bibtex file will be rewritten in up to 15 seconds\r\n");
                                    writer.write(
                                        "PLEASE VERIFY THE LITERATURE FILE THEN!\r\n");
                                    writer.flush();
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace(
                                    new PrintWriter(writer));
                            }
                        }

                        try {
                            writer.close();
                            reader.close();
                            socket.close();
                        }
                        catch (IOException e) {
                            // ignore closing problems	
                        }

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
            String key = (String) entry[BIBKEY_INDEX];

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

                        String k2 = (String) r[BIBKEY_INDEX];

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
                    entry[BIBKEY_INDEX] = key + c;
                }
            }
        }

        super.update(recordIndex, entry);
    }

    /** add or update an entry received via the socket connection */

    void update(Hashtable entry, Writer out)
        throws IOException, DbException {

        // first, determine the recordIndex. set 
        // recordIndex to -1 for new records

        int recordIndex = INSERT_ROW;

        String key = (String) entry.get("bibkey");
        String id = (String) entry.get("id");

        if (key == null) {
            out.write("entry without key not accepted!\r\n");
            out.flush();
            return;
        }

        Integer box = null;
        boolean delete = false;

        if (key.startsWith("*")) {
            key = key.substring(1);

            box = (Integer) keyTable.get(key);

            if (box == null)
                out.write(
                    "Deleting entry: "
                        + key
                        + " failed; key not found!\r\n\r\n");
            else {
                out.write("Deleting entry:\r\n\r\n");
                recordIndex = box.intValue();

                writeEntry(
                    new BibtexWriter(out),
                    (Object[]) records.get(recordIndex));

                out.flush();

                update(recordIndex, null);
            }
            return;
        }

        box = (Integer) keyTable.get(key);

        if (box != null) {
            recordIndex = box.intValue();

            Object[] oldEntry =
                (Object[]) records.get(recordIndex);

            out.write("Replacing old entry:\r\n\r\n");

            writeEntry(new BibtexWriter(out), oldEntry);

            out.flush();

            if (id == null || id.equals(""))
                id = (String) oldEntry[ID_INDEX];
            else if (!id.equals(oldEntry[ID_INDEX])) {
                out.write(
                    "ERROR: keys match, but ids do not match!\r\n");
                out.flush();

                return;
            }
        }

		out.write("New entry:\r\n");
        out.flush();

        // ok, now fill the record from the entry

        Object[] record = new Object[getPhysicalFieldCount()];

        for (Enumeration e = entry.keys();
            e.hasMoreElements();
            ) {
            String name = (String) e.nextElement();

            int i = findField(name);
            if (i <= 0 && name.startsWith("opt")) {
                i = findField(name.substring(3));
            }

            if (i > 0) {
                String value = (String) entry.get(name);
                record[i - 1] = value;
            }
            else {
                out.write(
                    "(ignoring unknown field: "
                        + name
                        + ")\r\n");
                out.flush();
            }
        }

        if (id != null && !id.equals(""))
            record[ID_INDEX] = id;

        update(recordIndex, record);

        out.write("\r\n");
        writeEntry(new BibtexWriter(out), record);

        out.flush();
    }

}