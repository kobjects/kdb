package org.kobjects.db.samples.swing;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.tree.*;

import org.kobjects.db.*;
import org.kobjects.db.statistics.*;
import org.kobjects.db.swing.*;


class RecordNode extends AbstractRecordNode {

    JPanel panel = new JPanel (new BorderLayout ());
    JTextField refineField = new JTextField ();
    JTable metaTable;

    RecordNode (AbstractNode parent, DbTable dbTable, 
		String query) throws DbException {

	super (parent, dbTable, query);

	JTable table = new JTable (new DbTableModel (record));
	
	panel.add (new JScrollPane (table), BorderLayout.CENTER);

	Box refine = new Box (BoxLayout.X_AXIS);
	refine.add (new JLabel ("refine: "));
	refine.add (refineField);

	Box buttons = new Box (BoxLayout.X_AXIS);
	buttons.add (Box.createGlue ());
	buttons.add (new JButton (new InvokeAction 
	    ("refine", this, "refine")));
	buttons.add (new JButton (new InvokeAction 
	    ("statistics", this, "statistics")));
	buttons.add (new JButton (new InvokeAction 
	    ("close", this, "close")));

	Box bottom = new Box (BoxLayout.Y_AXIS);	
	bottom.add (refine);
	bottom.add (buttons);
	panel.add (bottom, BorderLayout.SOUTH);
    }


    public Component getComponent () {
	return panel;
    }

    public String toString () {
	return query + " from "+ dbTable.getName ();
    }

    public void refine () {
	String refinement = refineField.getText ();
	if (refinement == null || refinement.trim ().length () == 0) {
	    TableBrowser.error (null, "No refinement given!");
	    return;
	}

	try {
	    add (new RecordNode (this, 
				 dbTable, "(" + query 
				 + ") AND (" + refinement
				 +")"), true);    
	}
	catch (Exception e) {
	    TableBrowser.error (e, null);
	}
    }


    public void statistics () {
	try {
	    add (new StatsNode (this, dbTable, query), true);
	}
	catch (Exception e) {
	    TableBrowser.error (e, null);
	}
    }

    public void close () {
    }
}



