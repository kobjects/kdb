package org.kobjects.db.samples.swing;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.tree.*;

import org.kobjects.swing.*;
import org.kobjects.db.*;
import org.kobjects.util.*;
import java.sql.*;


public class JdbcNode extends AbstractNode {

    JPanel panel = new JPanel(new BorderLayout ());
	Connection connection;
	JList tableList;
	
	static {
        try {
            DriverManager.registerDriver(
                (Driver) Class
                    .forName("oracle.jdbc.driver.OracleDriver")
                    .newInstance());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
	

    public JdbcNode(
        AbstractNode parent,
        String url,
        String user,
        String password) {

        super(parent, true);
        
        try {
        	connection =
                DriverManager.getConnection(url, user, password);

			DatabaseMetaData meta = connection.getMetaData ();
			ResultSet tables = meta.getTables(null, null, null, null);
			
			Vector names = new Vector ();
			while (tables.next ()) {
				String schema = tables.getString ("TABLE_SCHEM");
				String name = tables.getString ("TABLE_NAME");
				names.add (schema == null ? name : (schema + ":" + name));		
			}		
			//System.out.println ("table names: " + names);
			tables.close ();
			
			tableList = new JList (names);
			panel.add (new JScrollPane (tableList), BorderLayout.CENTER);			
        }
        catch (SQLException e) {
        	throw ChainedRuntimeException.create (e, null);
        }
    }

    public Component getComponent() {
        return panel;
    }

}