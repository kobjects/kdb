package org.kobjects.db.samples.swing;

import java.awt.*;

import javax.swing.*;

import org.kobjects.swing.*;
import org.kobjects.util.*;
import org.kobjects.db.*;
import org.kobjects.db.statistics.*;
import org.kobjects.db.swing.*;


class StatsNode extends AbstractRecordNode {

    JTable jTable = new JTable ();
    FieldStatistics [] stats;

    StatsNode (AbstractNode parent, DbTable dbTable, 
    		int[] fields, String query) throws DbException {

		super (parent, dbTable, fields, query, -1, false);
  	}
    		
    public void calculate () {	
    	super.calculate ();
    	try {
			stats = FieldStatistics.generate (record);
    	}
    	catch (DbException e) {
    		throw ChainedRuntimeException.create (e, null);
    	}
    }
    
    public void init () {
		panel.removeAll ();
		
		jTable.setModel (new DbMetaTableModel (dbTable, stats));

		panel.add (new JScrollPane (jTable), BorderLayout.CENTER);
		Box buttons = new Box (BoxLayout.X_AXIS);
		buttons.add (Box.createGlue ());
		buttons.add (new JButton (new InvokeAction ("close", this, "remove")));
		panel.add (buttons, BorderLayout.SOUTH);

		for (int i = 0; i < stats.length; i++) {
		    if (stats [i].values != null) 
			add (new ValuesNode (this, dbTable.getColumn (i).getName (), stats[i].values), false);
		}
    }


    public Component getComponent () {
	return panel;
    }

    public String toString () {
		return (calculating ? "Calculating Stats for " : "Stats for ")
			+ getParent ();
    }

}


