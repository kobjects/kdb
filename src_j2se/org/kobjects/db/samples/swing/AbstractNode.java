package org.kobjects.db.samples.swing;

import java.util.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;


abstract class AbstractNode implements TreeNode {
    AbstractNode parent;
    Vector children;

    AbstractNode (AbstractNode parent, boolean mayHaveChildren) {
	this.parent = parent;
	if (mayHaveChildren) 
	    children = new Vector ();
    }

    public abstract Component getComponent ();

    public TreePath getPath () {
	return parent == null 
	    ? new TreePath (this)
		: parent.getPath ().pathByAddingChild (this);
    }

    public void add (AbstractNode node, boolean select) {
	if (node.parent != this) 
	    throw new RuntimeException ("inconsistent parent");
	children.addElement (node);
	RootNode rn = getRoot ();
	JTree tree = rn.browser.tree;
	DefaultTreeModel dtm = (DefaultTreeModel) tree.getModel ();
	dtm.nodeChanged (this);
	dtm.nodesWereInserted (this, new int[] {children.size ()-1});

	TreePath path = node.getPath ();
	tree.makeVisible (path);
	if (select) tree.setSelectionPath (path);
    }


    public RootNode getRoot () {
	AbstractNode node = this;
	while (node.parent != null) node = node.parent;
	return (RootNode) node;
    }

    public Enumeration children () {
	return children == null 
	    ? new Vector ().elements () 
		: children.elements ();
    }

    public boolean getAllowsChildren () {
	return children != null;
    }

    public TreeNode getChildAt (int index) {
	return (TreeNode) children.elementAt(index);
    }


    public int getChildCount () {
	return children == null ? 0 : children.size ();
    }

    public int getIndex (TreeNode n) {
	return children == null ? -1 : children.indexOf (n);
    }

    public TreeNode getParent () {
	return parent;
    }

    public boolean isLeaf () {
	return children == null || children.size () == 0;
    }
}


