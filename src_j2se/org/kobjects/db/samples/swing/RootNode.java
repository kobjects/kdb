package org.kobjects.db.samples.swing;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.tree.*;

import org.kobjects.db.*;
import org.kobjects.db.swing.*;


public class RootNode extends AbstractNode {

    JPanel panel = new JPanel (new GridBagLayout ());
    JTextField urlField = new JTextField (40);
    JTextArea messageArea = new JTextArea ();
    JButton openButton = new JButton ("open");
    TableBrowser browser;

    public RootNode (TableBrowser browser) {
	super (null, true);

	this.browser = browser;

	GridBagConstraints c = new GridBagConstraints ();
	c.gridx = 0;
	c.gridy = 0;
	c.weightx = 1;
	c.weighty = 0;
	c.fill = c.HORIZONTAL;
	panel.add (new JLabel ("table address:"), c);

	c.gridy++;
	panel.add (urlField, c);

	c.gridx++;
	c.fill = c.NONE;
	c.weightx = 0;
	panel.add (new JButton (new InvokeAction ("open", this, "open")), c);

	c.gridx = 0;
	c.gridy++;
	c.weightx = 1;
	c.weighty = 1;
	c.fill = c.BOTH;
	panel.add (new JPanel (), c);
    }


    public void open () {
	open (urlField.getText ());
    }


    public void open (String connector) {
	try {
	    DbTable table = DbManager.connect (connector);
	    table.open ();

	    add (new TableNode (this, table, connector), true);
	}
	catch (Exception e) {
	    TableBrowser.error (e, null);
	}
    }
    

    public Component getComponent () {
	return panel;
    }

    public String toString () {
	return "TableBrowser";
    }
}



