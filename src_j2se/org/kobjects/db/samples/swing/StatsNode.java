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


class StatsNode extends AbstractRecordNode {

    JPanel panel = new JPanel (new BorderLayout ());
    JTable jTable = new JTable ();
    FieldStatistics [] stats;

    StatsNode (AbstractNode parent, 
	       DbTable dbTable, String query) throws DbException {

	super (parent, dbTable, query);
	
	stats = FieldStatistics.generate (record);
	jTable.setModel (new DbMetaTableModel (dbTable, stats));

	panel.add (new JScrollPane (jTable), BorderLayout.CENTER);
	Box buttons = new Box (BoxLayout.X_AXIS);
	buttons.add (Box.createGlue ());
	buttons.add (new JButton (new InvokeAction ("close", this, "close")));
	panel.add (buttons, BorderLayout.SOUTH);

	for (int i = 0; i < stats.length; i++) {
	    if (stats [i].values != null) 
		add (new ValuesNode (this, dbTable.getField (i).getName (), stats[i].values), false);
	}
    }

    public void close () {

    }

    public Component getComponent () {
	return panel;
    }

    public String toString () {
	return "Stats for "+ parent;
    }

}


