package org.kobjects.db.http;

import org.kobjects.db.*;
import java.util.Vector;

public class HttpRecord implements DbRecord {

	Vector selection;
	int current = -1;
	HttpTableSE table;

	Object[] values;
	boolean modified;
	boolean deleted;

	HttpRecord(HttpTableSE table, Vector selection) {
		this.table = table;
		this.selection = selection;
		values = new Object[table.getFieldCount()];
	}

	public void clear() {
		values = new Object[getTable().getFieldCount()];
		modified = true;
	}

	public void deleteAll() throws DbException {
		beforeFirst();
		while (hasNext()) {
			next();
			delete();
		}
	}

	public Object getObject(int column) {
		return values[column];
	}

	public boolean getBoolean(int column) {
		Boolean b = (Boolean) getObject(column);
		return (b == null) ? false : b.booleanValue();
	}

	public int getInteger(int column) {
		Integer i = (Integer) getObject(column);
		return (i == null) ? 0 : i.intValue();
	}

	public long getLong(int column) {
		Long l = (Long) getObject(column);
		return (l == null) ? 0 : l.longValue();
	}

	public int getRowCount() {
		return selection.size();
	}

	public String getString(int column) {
		Object o = getObject(column);
		return (o == null) ? null : o.toString();
	}

	public byte[] getBinary(int column) {
		return (byte[]) getObject(column);
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setBoolean(int column, boolean value) {
		setObject(column, new Boolean(value));
	}

	public void setInteger(int column, int value) {
		setObject(column, new Integer(value));
	}

	public void setLong(int column, long value) {
		setObject(column, new Long(value));
	}

	public void setObject(int column, Object value) {
		values[column] = value;
		modified = true;
	}

	public void setString(int column, String value) {
		setObject(column, value);
	}

	public void setBinary(int column, byte[] value) {
		//byte[] bytes = new byte[value.length];
		//System.arraycopy(value, 0, bytes, 0, value.length);
		setObject(column, value); // was: bytes
	}

	public void insert() throws DbException {
		modified = true;

		int cnt = table.getFieldCount();
		for (int i = 0; i < cnt; i++) {
			values[i] = table.getField(i).getDefault();
		}
	}

	public void insert(Object[] values) throws DbException {
		insert();

		for (int i = 0; i < values.length; i++) {
			setObject(i, values[i]);
		}
	}

	public boolean isModified() {
		return modified;
	}

	public void absolute(int position) throws DbException {
		beforeFirst();
		for (int i = 0; i < position; i++)
			next();
	}

	public void refresh() {
		Object[] content = (Object[]) selection.elementAt(current);
		for (int i = 0; i < content.length; i++)
			values[i] = content[i];

		modified = false;
	}

	public void update() {
		throw new RuntimeException("NYI");
	}

	public void delete() {
		throw new RuntimeException("NYI");
	}

	public Object getId() {
		return (table.getId(values));
	}

	public int getRow() {
		return current + 1;
	}

	public DbTable getTable() {
		return table;
	}

	public boolean hasNext() {
		return current < selection.size() - 1;
	}

	public void next() throws DbException {
		if (!hasNext())
			throw new DbException("no next available!");
		current++;
		refresh();
	}

	/** Places the cursor before the first record */

	public void beforeFirst() throws DbException {
		current = -1;
	}

	public void dispose() {
		//throw new RuntimeException ("NYI");
	}
}
