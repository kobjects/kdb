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

				setIdFields(new int[] { 1 });

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
					index.put(getId(i), new Integer(i));
				}
			} catch (IOException e) {
				throw new DbException(e.toString());
			}
		}
	}



	public static void main(String argv[]) throws DbException {

		DbTable table = DbManager.connect("bibtex:" + argv[0]);

		table.open();

		DbRecord r = table.select(false);

		while (r.hasNext()) {
			r.next();

			System.out.println(r.getId());
		}
	}

}