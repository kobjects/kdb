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
            
            open = true;
        }
        catch (IOException e) {
            throw new DbException (e.toString ());
        }
    }
} 
