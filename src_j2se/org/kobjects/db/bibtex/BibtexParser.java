package org.kobjects.db.bibtex;

import java.io.*;
import java.util.*;



/** abstract bibtex parser. best point for interception is closeEntry
    () */


class BibtexParser {

    Reader reader;
    int peek;

    Hashtable indices = new Hashtable ();
    Vector entries = new Vector ();
    Vector currentEntry;
    Vector fieldNames = new Vector ();

    public BibtexParser (Reader reader) throws IOException {
        this.reader = reader;
        peek = reader.read ();
    }


    String readTo (String chars) throws IOException {

        StringBuffer buf = new StringBuffer ();
        
        while (peek != -1 && chars.indexOf ((char) peek) == -1) {
            buf.append ((char) peek);
            peek = reader.read ();
        }
               
        return buf.toString ();
    }


    String readTo (char c) throws IOException {
        StringBuffer buf = new StringBuffer ();
        
        while (peek != -1 && peek != c) {
            buf.append ((char) peek);
            peek = reader.read ();
        }
               
        return buf.toString ();
    }

    int read () throws IOException {
        int result = peek;
        peek = reader.read ();
        return result;
    }


    public void addEntry (String type, String id) {
        currentEntry = new Vector ();

        addProperty ("type", type);
        addProperty ("id", id);
    }

    
    public void addProperty (String id, String value) {
        
        id = id.toLowerCase ().trim ();
        Integer index = (Integer) indices.get (id);

        if (index == null) {
            index = new Integer (indices.size ());
            indices.put (id, index);
            fieldNames.addElement (id);
        }

        int i = index.intValue ();

        if (currentEntry.size () <= i)
            currentEntry.setSize (i+1);

        currentEntry.setElementAt (value, i);
    }
    

    public void closeEntry () {
        String [] entry = new String [currentEntry.size ()];
        for (int i = 0; i < currentEntry.size (); i++) {
            entry [i] = (String) currentEntry.elementAt (i);
            //System.out.println (""+entry [i]);
        }

        entries.addElement (entry);
        currentEntry = null;

    }


    void recurse (StringBuffer buf) throws IOException {

	while (true) {
	    buf.append (readTo ("{}"));
	    
	    if (read () != '{') {
		break;
	    }
	    
	    buf.append ('{');
	    recurse (buf);
	    buf.append ('}');
	}
    }



    boolean readLine () throws IOException {
	StringBuffer idBuf = new StringBuffer ();
	StringBuffer valueBuf = new StringBuffer ();
	
	String id = readTo ("}=").trim ().toLowerCase ();
	
	if (read () == '}') {
	    return false;
	}
	
	valueBuf.append (readTo ("{,}\""));
	
	int c = read ();
	
	if (c == '{') {
	    recurse (valueBuf);
	    valueBuf.append (readTo (",}"));
	    
	    c = read ();
	}
	else if (c == '"') {
	    while (true) {
		valueBuf.append (readTo ("\"\\"));
		int d = read ();
		
		if (d == '"' || d == -1) break;
		
		valueBuf.append ((char) d);
		valueBuf.append ((char) read ());
	    }
	    
	    readTo (",}");
	    
	    c = read ();
	}
	
	String value = valueBuf.toString ().trim ();
	
        addProperty (id, value);
	
	return (c == ',');
    }
    
    
    public void parse () throws IOException {
	while (true) {
            readTo ("@<");
            int i = read ();
	    if (i != '@') break;     // mit '<' wird <ende> vom emacs erkannt...   
	    //if (peek == -1) break; redundant with i != '@'
	    
	    StringBuffer type = new StringBuffer ();
	    StringBuffer id = new StringBuffer ();
	    
	    type.append (readTo ('{'));
	    read ();
	    
	    id.append (readTo (",}"));
	    int c = read ();
	    
	    //	  System.out.println ("type: " + type.toString () + " id: " + id.toString ().trim ());
	    
	    // "eintrag"
	    
	    addEntry (type.toString ().trim().toLowerCase (), 
		      id.toString ().trim ());
	    
	    if (c == ',') {
		while (readLine ()) {
		    // System.out.println ("rl");
		} 
	    }
	    
	    //      System.out.println ("c");
	    
	    closeEntry ();
	}
    }


    public static void main (String [] argv) throws IOException {

        new BibtexParser 
            (new BufferedReader 
             (new FileReader 
              ("/app/unido-i08/share/bibserver/database/literatur.bib"))).parse ();

    }


}


