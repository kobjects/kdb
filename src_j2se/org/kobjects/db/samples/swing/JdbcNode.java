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

public class JdbcNode extends AbstractNode {

    JPanel panel = new JPanel ();

    public JdbcNode (AbstractNode parent, String url, String user, String password) {

	super (parent, true);
    }

    public Component getComponent () {
	return panel;
    }

}
