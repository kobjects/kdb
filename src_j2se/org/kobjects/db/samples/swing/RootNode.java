package org.kobjects.db.samples.swing;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.tree.*;

import org.kobjects.swing.*;
import org.kobjects.db.*;
import org.kobjects.db.swing.*;


public class RootNode extends AbstractNode {

    static final String [] FILE_TYPES = {"arff", "bibtex"};

    JPanel panel = new JPanel (new GridBagLayout ());
    JTextField urlField = new JTextField (40);
    JTextArea messageArea = new JTextArea ();
    JButton openButton = new JButton ("open");
    TableBrowser browser;
    JComboBox typeComboBox = new JComboBox (FILE_TYPES);
    JFileChooser fileChooser = new JFileChooser ();

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

	Box box = new Box (BoxLayout.X_AXIS);
	box.add (urlField);
	box.add (new JButton (new InvokeAction ("open", this)));

	c.gridy++;
	panel.add (box, c);

	box = new Box (BoxLayout.X_AXIS);
	box.add (typeComboBox);
	box.add (new JButton 
	    (new InvokeAction ("select file", this, "selectFile")));
	box.add (Box.createGlue ());
	c.gridy++;
	panel.add (box, c);
	

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
    
    public void selectFile () {
	if (fileChooser.showOpenDialog (panel) == JFileChooser.APPROVE_OPTION) 
	    urlField.setText (""+typeComboBox.getSelectedItem () + ":" + fileChooser.getSelectedFile ());
    }


    public Component getComponent () {
	return panel;
    }

    public String toString () {
	return "TableBrowser";
    }
}



