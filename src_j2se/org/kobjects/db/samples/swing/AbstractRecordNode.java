package org.kobjects.db.samples.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

import org.kobjects.swing.*;
import org.kobjects.util.ChainedRuntimeException;
import org.kobjects.db.*;
import org.kobjects.db.sql.*;
import org.kobjects.db.swing.*;


abstract class AbstractRecordNode extends AbstractNode {

    DbTable dbTable;
    String query;
    DbResultSet record;
    int [] fields;
    int order;
    boolean inverse;
	boolean calculating = true;
	JPanel panel = new JPanel (new BorderLayout ());
    
    public AbstractRecordNode (AbstractNode parent,
			       DbTable dbTable, 
			       int [] fields, 
			       String query,
			       int order,
			       boolean inverse) {

	super (parent, true);

	this.dbTable = dbTable;
	this.fields = fields;
	this.query = query;
	this.order = order;
	this.inverse = inverse;

	System.out.println ("query: "+query);
	panel.add (new JLabel ("please wait: "+toString ()), BorderLayout.NORTH);

	new Thread () {
		public void run () {
			if (calculating) {
				calculate (); 
				calculating = false;
				try {
  					EventQueue.invokeAndWait (this);
				}
				catch (Exception e) {
					e.printStackTrace ();
				}
			}
			else AbstractRecordNode.this.init ();
		}	 
		}.start ();
    }


	public void calculate () {
		try {
			DbCondition condition = null;
			if (query != null && query.trim().length () > 0) { 
	    		condition = new DbSqlParser (dbTable).parse (query);
	    		condition.setTable (dbTable);
			}
			record = dbTable.select (fields, condition, order, inverse, false);
			record.getRowCount ();
		}
		catch (DbException e) {
			ChainedRuntimeException.create (e, null);
		}
	}


	public abstract void init ();
}




