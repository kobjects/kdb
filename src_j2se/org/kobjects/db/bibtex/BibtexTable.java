package org.kobjects.db.bibtex;

import java.io.*;
import java.util.*;
import org.kobjects.db.*;
import org.kobjects.db.ram.*;

public class BibtexTable extends RamTable {

	String filename;

	public BibtexTable() {
	}


	public BibtexTable(String filename) throws DbException {
		connect ("bibtex:"+filename);
	}
	

	public void connect(String connector) throws DbException {
		filename = connector.substring(connector.indexOf(':') + 1);
		exists = new File (filename).exists ();
		if (exists) {
			try {
				BibtexParser parser =
					new BibtexParser(new BufferedReader(new FileReader(filename)));

				parser.parse();

				for (int i = 0; i < parser.fieldNames.size(); i++) {
					addField((String) parser.fieldNames.elementAt(i), DbField.STRING);

				}

				setIdField(1);

				// ensure equal record sizes

				for (int i = 0; i < parser.entries.size(); i++) {
					Object[] r = (Object[]) parser.entries.elementAt(i);
					if (r.length < getFieldCount()) {
						Object[] n = new Object[getFieldCount()];
						for (int j = 0; j < r.length; j++)
							n[j] = r[j];

						r = n;
					}

					records.addElement(r);
					index.put(r[1], new Integer(i));
				}
			} catch (IOException e) {
				throw new DbException(e.toString());
			}
		}
	}


    public void close () throws DbException {
	if (modified) {
	    try {
		File nf = new File (filename + ".new");
		BufferedWriter w = new BufferedWriter (new FileWriter (nf));
		for (int i = 0; i < records.size (); i ++) {
		    Object [] entry = (Object []) records.elementAt (i);
		    w.write ("@" + entry [0] + "{" + entry [1]);
		    for (int j = 2; j < entry.length; j++) {
			if (entry [j] == null 
			    || "".equals (entry[j])) continue;
			w.write (',');
			w.newLine ();
			w.write ("  "+getField (j).getName () + " = ");
			String e = entry[j].toString ();
			for (int k = 0; k < e.length (); k++) {
			    char c = e.charAt (k);
			    if ((c < '0' || c > '9') 
				&& (c < 'a' || c > 'z') 
				&& (c < 'A' || c > 'Z')) {
				e = "{"+e+"}";
				break;
			    }
			}
			w.write (e);
		    }
		    w.newLine ();
		    w.write ('}');
		    w.newLine ();
		    w.newLine ();
		    w.newLine ();
		}
		w.close ();
		
		new File (filename + ".bak").delete ();
		new File (filename).renameTo (new File (filename + ".bak"));
		nf.renameTo (new File (filename));
		
		modified = false;
	    }
	    catch (IOException e) {
		throw new DbException (""+e);
	    }
	}
	super.close ();
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




