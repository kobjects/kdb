package org.kobjects.db.ls8bib;

import java.io.*;
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


	public void run () {
	}

	// let rewrite do nothing

	public void rewrite() {
	}

	protected void update (int index, Object[] entry) throws DbException {

		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
			writeEntry(bw, entry);
			bw.flush ();
		}
		catch (IOException e) {
			throw new DbException (e.toString ());
		}
 		
		super.update(index, entry);
	}



}
