package org.kobjects.db.arff;

import java.io.*;
import org.kobjects.db.*;
import org.kobjects.db.ram.RamTable;

public class ArffTable extends RamTable {

    String name;
    String filename;

    public ArffTable() {
    }

    public ArffTable(String filename) throws DbException {
        connect("arff:" + filename);
    }

    public void connect(String connector) throws DbException {
        filename =
            connector.substring(connector.indexOf(':') + 1);
        exists = new File(filename).exists();
        if (exists) {
            try {
                ArffParser parser =
                    new ArffParser(
                        this,
                        new BufferedReader(
                            new FileReader(filename)));

                while (true) {
                    Object[] data = parser.read();
                    if (data == null)
                        break;
                    records.addElement(data);
                }
                /*
                		records.addElement(r);
                	} */
            }
            catch (IOException e) {
                throw new DbException(e.toString());
            }
        }
    }

    public String getName() {
        return name;
    }

    public static void main(String[] args) throws DbException {
        DbTable table =
            new ArffTable("/home/haustein/projects/infolayer/samples/arff/weather.arff");

        table.open();
        System.out.println("table: " + table);

        for (int i = 0; i < table.getColumnCount(); i++) {
            System.out.println("field: " + table.getColumn(i));
        }

        DbResultSet record = table.select(false);

        while (record.next()) {
            System.out.println("record: " + record);
        }
    }

}
