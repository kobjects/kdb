package org.kobjects.db.ls8bib;

import java.io.*;
import java.net.*;
import java.util.*;
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

	Vector update = new Vector();

	// let rewrite do nothing

	public void rewrite() {
	}

	protected void update(int index, Object[] entry) throws DbException {

		int keyField = findField("key");

		if (entry == null) {
			Object[] old = (Object[]) records.elementAt(index);
			update.addElement(old[keyField]);
		} else {

			String key = (String) entry [keyField];

			if (key != null && key.length() > 0) {
				char c = key.charAt (key.length()-1);
				
				Object[] addCopy = new Object[entry.length];
				System.arraycopy(entry, 0, addCopy, 0, entry.length); 

				// determine the new key locally

				if (c >= '0' && c <= '9') {
					c = 'a';
					for (int i = 0; i < records.size(); i++) {
						Object [] r = (Object[]) records.elementAt(i);
						if (r == null) continue;
						
						String k2 = (String) r[keyField];
						
						if (k2 == null) continue;
						
						if (k2.startsWith (key) && k2.length() == key.length()+1) {
							char c2 = k2.charAt (k2.length()-1);
							// make sure key is not incremented if already assigned
							if (index == i) {
								c = c2;
								break;
							} 
							if (c2 >= c) c = (char) (((int) c2) + 1);	
						}
					}	
					entry[keyField] = key + c;
				}

				update.addElement(addCopy);
			}
		}

		super.update(index, entry);
	}

	public void run() {
		while (true) {

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			};

			if (update.size() == 0) {

				if (new File(filename).lastModified() > lastModified) {
					try {
						reload();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				continue;				
			}

			Vector v = update;
			update = new Vector();

			try {

				Socket socket = new Socket("kiew.cs.uni-dortmund.de", 8410);

				BufferedReader reader =
					new BufferedReader(
						new InputStreamReader(socket.getInputStream()));

				/*    while (true) {
				        String line = reader.readLine();
				        System.out.println ("waiting for start: "+line);
						if(line.trim().equals("Start!")) break;
				    } */

				BufferedWriter bw =
					new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream()));

				for (int i = 0; i < v.size(); i++) {
					Object o = v.elementAt(i);
					bw.write("\n\n");

					if (o instanceof String)
						bw.write("\n\n*" + o + "\n\n");
					else {
						writeEntry(bw, (Object[]) o);
					}
				}

				bw.write("\n\n<ende>\n");
				bw.flush();

				while (true) {
					String line = reader.readLine();
					System.out.println("ls8bibline:" + line);

					if (line == null)
						break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				//			throw new DbException(e.toString());
			}
		}
	}
}