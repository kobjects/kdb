package org.kobjects.db.ls8bib;

import java.io.*;
import java.net.*;
import org.kobjects.db.bibtex.BibtexTable;
import org.kobjects.db.*;

/**
 * @author Stefan Haustein
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class Ls8bibTable extends BibtexTable {

    // let run do nothing

    public void run() {
    }

    // let rewrite do nothing

    public void rewrite() {
    }

    protected void update(int index, Object[] entry)
        throws DbException {

        Object[] old = null;

        if (index > 0)
            old = (Object[]) records.elementAt(index);


        if (old != null || entry != null) {

            try {

				int keyField = findField("key");

                Socket socket =
                    new Socket("kiew.cs.uni-dortmund.de", 8410);

                BufferedReader reader =
                    new BufferedReader(
                        new InputStreamReader(
                            socket.getInputStream()));

            /*    while (true) {
                    String line = reader.readLine();
                    System.out.println ("waiting for start: "+line);
					if(line.trim().equals("Start!")) break;
                } */

                BufferedWriter bw =
                    new BufferedWriter(
                        new OutputStreamWriter(
                            socket.getOutputStream()));


                if (entry != null)
                    writeEntry(bw, entry);
                else
                    bw.write("*" + old[keyField]);

                bw.write("\n\n<ende>\n");
                bw.flush();

                while (true) {
                    String line = reader.readLine();
                    System.out.println("ls8bibline:" + line);
                    if (line == null)
                        break;

                    line = line.trim();
					if (line.endsWith ("wurde geloescht!")
					|| line.equals ("*****************************************")) break;
					

					if (line.trim().startsWith ("@")) {
						int cut0 = line.indexOf ('{');
						if (cut0 != -1) 
							entry [keyField] = line.substring (cut0+1);

						System.out.println ("newkey: "+entry[keyField]);
													
					}                    
                }
                System.out.println ("closing connection");
                bw.close();
                reader.close();
                socket.close();

            }
            catch (IOException e) {
                e.printStackTrace();
                throw new DbException(e.toString());
            }
        }
        super.update(index, entry);
    }
}
