package org.kobjects.db.samples.swing;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

class ValuesNode extends AbstractNode {

    JPanel panel = new JPanel (new BorderLayout ());
    String name;

    ValuesNode (AbstractNode parent, String name, Hashtable occurances) {
	super (parent, false);
	this.name = name;

	DefaultTableModel dtm = new DefaultTableModel 
	    (new String [] {"Value", "Occurances"}, 0);

	for (Enumeration e = occurances.keys (); e.hasMoreElements ();) {
	    String key = (String) e.nextElement ();
	    dtm.addRow (new Object [] {key, occurances.get (key)});
	}

	panel.add (new JScrollPane (new JTable (dtm)), BorderLayout.CENTER);
	
	Box buttons = new Box (BoxLayout.X_AXIS);
	buttons.add (Box.createGlue ());
	buttons.add (new JButton (new InvokeAction ("close", this)));
    }


    public String toString () {
	return name;
    }

    public Component getComponent () {
	return panel;
    }
}




