package org.kobjects.db.ram;

import java.util.*;
import java.io.*;
import org.kobjects.db.*;

public class RamRecord implements DbRecord {

	protected Vector selection;
	/** index in selected */
	protected int current = -1;
	/** index in table.records */
	protected int index; // index in records vector
	protected RamTable table;

	/** copy of current values */
	protected Object[] values;

	protected boolean modified;
	boolean deleted;
	int[] selectedFields;

	protected RamRecord(
		RamTable table,
		Vector selection,
		int[] selectedFields) {
		this.table = table;
		this.selection = selection;
		this.selectedFields = selectedFields;
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


	protected Object getObjectImpl(int column) {
		if (selectedFields == null)
			return values[column];

		for (int i = 0; i < selectedFields.length; i++)
			if (selectedFields[i] == column)
				return values[column];
				
		if (column == table.idField)
			return values[column];

		throw new IllegalArgumentException(
			"Field " + column + " not in selection!");				
	}

	public Object getObject(int column) {

		Object value = getObjectImpl(column);
		
		return (value instanceof byte[])
			? new ByteArrayInputStream((byte[]) values[column])
			: values[column];

		// provide always access to id field if available
	}

	public boolean getBoolean(int column) {
		Boolean b = (Boolean) getObject(column);
		return (b == null) ? false : b.booleanValue();
	}

	public long getSize(int column) {
		Object value = getObjectImpl(column);

		if (value == null) return -2;
		return (value instanceof byte[]) 
			? ((byte[]) value).length					
			: -1;
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

	public int[] getSelectedFields() {
		return selectedFields;
	}

	public String getString(int column) {
		Object o = getObject(column);
		return (o == null) ? null : o.toString();
	}

	public InputStream getBinary(int column) {
		return (InputStream) getObject(column);
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
		if (value instanceof InputStream) {
			try {
				InputStream is = (InputStream) value;
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] buf = new byte[128];
				while (true) {
					int cnt = is.read(buf);
					if (cnt == -1)
						break;
					bos.write(buf, 0, cnt);
				}
			} catch (IOException e) {
				throw new RuntimeException(e.toString());
			}
		}
		values[column] = value;
		modified = true;
	}

	public void setString(int column, String value) {
		setObject(column, value);
	}

	public void setBinary(int column, InputStream value) {
		//byte[] bytes = new byte[value.length];
		//System.arraycopy(value, 0, bytes, 0, value.length);

		setObject(column, value); // was: bytes
	}

	public void insert() throws DbException {
		modified = true;
		index = table.INSERT_ROW;

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
		index = ((Integer) selection.elementAt(current)).intValue();
		Object[] content = (Object[]) table.records.elementAt(index);

		deleted = content == null;

		for (int i = 0; i < content.length; i++)
			values[i] = deleted ? null : content[i];

		modified = false;
	}

	public void update() throws DbException {

		/*        Object[] content = null;
		
		        if (!deleted) {
		            content = new Object[values.length];
		            for (int i = 0; i < values.length; i++)
		                content[i] = values[i];
		        } */

		table.update(index, deleted ? null : values);

	}

	public void delete() {
		deleted = true;
	}

	public int getRow() {
		return current + 1;
	}

	public DbTable getTable() {
		return table;
	}

	public boolean hasNext() {
		return selection != null && current < selection.size() - 1;
	}

	public void next() throws DbException {
		if (!hasNext())
			throw new DbException("no next available!");
		index = ((Integer) selection.elementAt(++current)).intValue();
		refresh();
	}

	/** Places the cursor before the first record */

	public void beforeFirst() throws DbException {
		current = -1;
	}

	/** Dispose does not need to do much in the case of 
	ramtable */

	public void dispose() {
		//throw new RuntimeException ("NYI");
	}
}