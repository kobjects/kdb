package org.kobjects.db.ls8bib;

import java.io.*;
import java.net.*;
//import java.util.*;
import org.kobjects.db.bibtex.BibtexTable;
import org.kobjects.db.*;

/**
 * @author Stefan Haustein
 *
 * Provides an additional emulation of eduards bibserver
 */


public class Ls8bibTable extends BibtexTable {


	protected void update(int index, Object[] entry) throws DbException {

		if (entry != null) {
			int keyField = findField("key");
			String key = (String) entry [keyField];

			if (key != null && key.length() > 0) {
				char c = key.charAt (key.length()-1);
				
//				Object[] addCopy = new Object[entry.length];
//				System.arraycopy(entry, 0, addCopy, 0, entry.length); 

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
			}
		}

		super.update(index, entry);
	}


	public void run() {

		try {
			ServerSocket serverSocket = new ServerSocket (8410);

			while (true) {

				try {
					Socket socket = serverSocket.accept ();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}			
		}
		catch (IOException e) { 
			e.printStackTrace();
		}
	}
}