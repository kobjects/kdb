package org.kobjects.db;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class DbManager {

    /**
     * Connects to a table. This factory method examines the given connector and
     * loads a suitable table implementation by name, connecting it to the given
     * table. The result is the same as for instatiating and connecting the
     * table explicitly, i.e. the table is not opened. The connector string
     * obeys the following URI-like naming scheme:
     * <pre>
     *   "protocol:table;parameters"
     * </pre>
     * The actual table implementation is chosen depending on the "protocol"
     * value. The package name always is "org.kobjects.db" plus a sub-package
     * named after the protocol. The class name consists of the protocol values
     * with the first letter being uppercased plus the fixed suffix "Table". As
     * an example, for a connector string
     * <pre>
     *   "rms:MyTable;user=joerg;password=secret"
     * </pre>
     * the class <code>org.kobjects.db.rms.RmsTable</code> would be instantiated
     * and the new instance connects to a table "MyTable". Interpretation of the
     * parameters following the ";" is up to the table implementation.
     */
    public static DbTable connect(String connector) throws DbException {
        DbTable table = null;

        try {
            int p = connector.indexOf(':');
            String type = connector.substring(0, p);
            if ("https".equals(type)) type = "http";
            String name = Character.toUpperCase(type.charAt(0)) + type.substring(1);

            table = (DbTable)Class.forName("org.kobjects.db." + type + "." + name + "Table").newInstance();
        }
        catch (Exception e) {
            throw new DbException("Can't connect to table \"" + connector + "\"", e);
        }

        table.connect(connector);

        return table;
    }
}
