package org.kobjects.db.bibtex;

// (C) 2002 by Stefan Haustein 
// Rolandstrasse 27, D-46045 Oberhausen, Germany
// All rights reserved.
//
// For licensing details, please refer to the file "license.txt",
// distributed with this file.


import java.util.*;
import java.io.*;
import org.kobjects.db.*;
import org.kobjects.db.ram.*;

/**
 * @author Stefan Haustein */

public class BibtexRecord extends RamRecord {

	BibtexTable table;

	BibtexRecord(BibtexTable table, Vector selected, int[] fields) {
		super(table, selected, fields);
		this.table = table;
	}



	public Object getObject (int index)  {
		DbField field = getField(index);
		if (field.getNumber() < table.fileIndex) 
			return super.getObject(index);

		try {
			
			File file = new File 
				(table.documentDir, 
				 values[table.keyIndex-1]
				 +"."
				 +field.getName().substring (0, 3));
			
			System.out.println("trying file: "+file);
			
			if (!file.exists()) return null;

			return new FileInputStream 
				(file); 
		}
		catch(IOException e) {
			throw new RuntimeException (e.toString());
		}
	}

}
