package org.kobjects.db.arff;

import org.kobjects.db.*;
import org.kobjects.util.Csv;
import java.io.*;

public class ArffParser {

    ArffTable table;
    BufferedReader reader;

    String cut(StringBuffer buf) {
        int i = 0;
        String result = buf.toString();
        while (i < result.length() && result.charAt(i) > ' ')
            i++;
        result = result.substring(0, i);
        while (i < buf.length() && buf.charAt(i) <= ' ')
            i++;
        int j = 0;
        while (i < buf.length())
            buf.setCharAt(j++, buf.charAt(i++));
        buf.setLength(j);
        return result;
    }

    /**
     * Constructor for ArffParser.
     */
    public ArffParser(ArffTable table, BufferedReader reader)
        throws IOException {
        this.table = table;
        this.reader = reader;

        while (true) {
            String line = reader.readLine();
            if (line == null)
                throw new RuntimeException("Unexpected EOF");
            line = line.trim();
            if (line.equals("") || line.startsWith("%"))
                continue;

            if (line.equalsIgnoreCase("@data"))
                break;

            StringBuffer buf = new StringBuffer(line);

            String cmd = cut(buf).toLowerCase();
//            System.out.println("command: " + cmd);
            if (cmd.equals("@relation"))
                table.name = buf.toString();
            else if (cmd.equals("@attribute")) {
                String name = cut(buf);
                String remainder = buf.toString().trim();
                
                String[] values = null;
                int type;
                
                if (remainder.equalsIgnoreCase("real"))
                	type = DbField.DOUBLE;
                else if (remainder.startsWith("{")) {
                	type = DbField.STRING;
                	values = org.kobjects.util.Csv.decode(remainder.substring(1, remainder.length()-1));
                }
                else {
                	System.err.println ("unrecognized type: '"+remainder+"' assuming string");
					type = DbField.STRING;
                }
                
                table.addField(name, type);
                if (values != null) {
					DbField field = table.getField(table.getFieldCount());
					field.setProperties(null, 0, 0, values);
                }
            }
        }

    }

    Object[] read() throws IOException {
 
 		String line;
 		do {   	
	        line = reader.readLine();
    	    if (line == null) return null;
 		}
 		while (line.startsWith("%"));
 		
        String [] strings = Csv.decode(line);
 		Object [] objects = new Object[table.getFieldCount()];
 		
 		for (int i = 0; i < table.getFieldCount(); i++) {
 			int type = table.getField(i+1).getType();
 			switch(type) {
 			case DbField.DOUBLE:
 				objects[i] = new Double(strings[i]);
 				break;
 			case DbField.STRING:
 				objects[i] = strings[i];
 				break;
 			default:
 				throw new RuntimeException("Unsupported type "+ type);
 			}
 		}		       
        return objects;
    }
}