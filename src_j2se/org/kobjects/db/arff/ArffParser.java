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
            System.out.println("command: " + cmd);
            if (cmd.equals("@relation"))
                table.name = buf.toString();
            else if (cmd.equals("@attribute")) {
                String name = cut(buf);
                System.out.println("adding field: " + name);
                table.addField((String) name, DbField.STRING);

            }
        }

    }

    Object[] read() throws IOException {
        String line = reader.readLine();
        return (line != null) ? Csv.decode(line) : null;
    }
}