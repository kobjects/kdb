package org.kobjects.db.samples.swing;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import org.kobjects.db.*;
import org.kobjects.db.swing.*;


public class TableBrowser extends JFrame implements TreeSelectionListener {

    RootNode rootNode = new RootNode (this);
    JSplitPane splitPane = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT);
    JTree tree = new JTree (rootNode);
    Component current = rootNode.getComponent ();

    TableBrowser () {

	super ("TableBrowser");

	addWindowListener (new WindowAdapter () {
		public void windowClosing (WindowEvent e) {
		    System.exit (0);
		}
	    });

	getContentPane ().add (splitPane, BorderLayout.CENTER);
	splitPane.setLeftComponent (new JScrollPane (tree));
	splitPane.setRightComponent (current);

	tree.addTreeSelectionListener (this);

	pack ();
	show ();
    }

    public static void error (Exception e, String msg) {

	Object message;
	
	if (e != null) {
	    StringWriter w = new StringWriter ();
	    e.printStackTrace (new PrintWriter (w));
	    JTextArea jta = new JTextArea ();
	    jta.setText ((msg != null ? (msg+"\n\n") : "") + w.toString ());
	    jta.setEditable (false);

	    message = new JScrollPane (jta); 
	}
	else 
	    message = msg;


	JOptionPane.showMessageDialog (null, message, "Error", 
				       JOptionPane.ERROR_MESSAGE);


    }


    public void valueChanged (TreeSelectionEvent e) {

	AbstractNode node = (AbstractNode) e.getPath ().getLastPathComponent ();

	current = node.getComponent ();
	setTitle (node.toString ());
	int l = splitPane.getDividerLocation ();
	splitPane.setRightComponent (current);
	splitPane.setDividerLocation (l);
	//	root.add (current, BorderLayout.CENTER);
	//root.validate ();
	//current.repaint ();
    }

    public static void main (String [] argv) {
	TableBrowser tb = new TableBrowser ();
	if (argv.length > 0) 
	    tb.rootNode.open (argv [0]);
    }


}
