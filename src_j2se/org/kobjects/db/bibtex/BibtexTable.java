package org.kobjects.db.bibtex;


import java.io.*;
import java.util.*;
import org.kobjects.db.*;
import org.kobjects.db.ram.*;


public class BibtexTable extends RamTable {

    String fileName;
    

    public void init (String connector) {
        
        fileName = connector;
    }

     

    public void open () throws DbException {

        try {
            BibtexParser parser = new BibtexParser 
                (new BufferedReader (new FileReader (fileName)));
            
            parser.parse ();
            
            records = parser.entries;
            
            for (int i = 0; i < parser.fieldNames.size (); i++) {
                
                addField ((String) parser.fieldNames.elementAt (i), DbField.STRING);
            } 
            
	    // ensure equal record sizes

	    for (int i = 0; i < records.size (); i++) {
		Object [] r = (Object []) records.elementAt (i);
		if (r.length < getFieldCount ()) {
		    Object [] n = new Object [getFieldCount ()];
		    for (int j = 0; j < r.length; j++) 
			n [j] = r [j];

		    records.setElementAt (n, i);
		}
	    }

            open = true;
        }
        catch (IOException e) {
            throw new DbException (e.toString ());
        }
    }
} 



