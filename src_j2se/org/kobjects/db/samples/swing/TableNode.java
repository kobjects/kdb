package org.kobjects.db.samples.swing;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.tree.*;

import org.kobjects.db.*;
import org.kobjects.db.sql.*;
import org.kobjects.db.swing.*;


class TableNode extends AbstractNode {

    DbTable dbTable;
    String connector;
    JTable jTable;
    JPanel panel = new JPanel (new GridBagLayout ());
    JTextField queryField = new JTextField (40);

    
    TableNode (AbstractNode parent, DbTable table, String connector) {
	super (parent, true);
	this.connector = connector;
	this.dbTable = table;

	jTable = new JTable (new DbMetaTableModel (dbTable, null));

	GridBagConstraints c = new GridBagConstraints ();
	c.gridy = 0;
	c.gridx = 0;
	c.weightx = 1;
	c.weighty = 1;
	c.fill = c.BOTH;
	panel.add (new JScrollPane (jTable), c);

	Box qBox = new Box (BoxLayout.X_AXIS);
	qBox.add (new JLabel ("Query: "), c);
	qBox.add (queryField);


	c.gridy++;
	c.weighty = 0;
	c.fill = c.HORIZONTAL;
	panel.add (qBox, c);


	Box buttons = new Box (BoxLayout.X_AXIS);

	buttons.add (Box.createGlue ());
	buttons.add (new JButton (new InvokeAction 
	    ("select", this, "query")));	
	buttons.add (new JButton (new InvokeAction 
	    ("statistics", this, "statistics")));	
	buttons.add (new JButton (new InvokeAction 
	    ("close", this, "close")));

	c.gridy++;
        panel.add (buttons, c);
    }

		     
    public void query () {
	try {
	    add (new RecordNode (this, dbTable, 
				 queryField.getText ()), true);
	}
	catch (Exception e) {
	    TableBrowser.error (e, "error processing query: "
				+queryField.getText ());
	}
    }

    public void statistics () {
	try {
	    add (new StatsNode (this, dbTable, ""), true);
	}
	catch (Exception e) {
	    TableBrowser.error (e, null);
	}
    }

    public void close () {
	super.close ();
	try {
	    dbTable.close ();
	}
	catch (Exception e) {
	    TableBrowser.error (e, null);
	}
    }


    public Component getComponent () {
	return panel;
    }

    public String toString () {
	return dbTable.getName () + " ("+connector+")";
    }
}


