package org.kobjects.db.samples.swing;


import javax.swing.*;
import javax.swing.tree.*;

import org.kobjects.swing.*;
import org.kobjects.db.*;
import org.kobjects.db.sql.*;
import org.kobjects.db.swing.*;


abstract class AbstractRecordNode extends AbstractNode {

    DbTable dbTable;
    String query;
    DbRecord record;
    int [] fields;
    int order;
    boolean inverse;
    
    public AbstractRecordNode (AbstractNode parent,
			       DbTable dbTable, 
			       int [] fields, 
			       String query,
			       int order,
			       boolean inverse) throws DbException {

	super (parent, true);

	this.dbTable = dbTable;
	this.fields = fields;
	this.query = query;
	this.order = order;
	this.inverse = inverse;

	System.out.println ("query: "+query);

	DbCondition condition = null;
	if (query != null && query.trim().length () > 0) { 
	    condition = new DbSqlParser (dbTable).parse (query);
	    condition.setTable (dbTable);
	}
	record = dbTable.select (fields, condition, order, inverse, false);
    }
}




