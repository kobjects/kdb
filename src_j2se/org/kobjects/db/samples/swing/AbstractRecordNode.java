package org.kobjects.db.samples.swing;


import javax.swing.*;
import javax.swing.tree.*;

import org.kobjects.db.*;
import org.kobjects.db.sql.*;
import org.kobjects.db.swing.*;


abstract class AbstractRecordNode extends AbstractNode {

    DbTable dbTable;
    String query;
    DbRecord record;
 
    public AbstractRecordNode (AbstractNode parent,
			       DbTable dbTable, 
			       String query) throws DbException {

	super (parent, true);

	this.dbTable = dbTable;
	this.query = query;

	if (query == null || query.trim ().length () == 0) {
	    record = dbTable.select (false);
	}
	else {
	    DbCondition condition = new DbSqlParser (dbTable).parse (query);
	    condition.setTable (dbTable);
	    record = dbTable.select (condition, -1, false, false);
	}
    }
}

