package org.kobjects.db.bibtex;

import java.io.*;
import java.util.*;

import org.kobjects.db.*;
import org.kobjects.db.ram.*;
import org.kobjects.bibtex.*;

import java.net.*;

/** 
 * A DbTable implementation for bibtex databases. Creates an
 * id automatically. */

public class BibtexTable extends RamTable implements Runnable {

	protected String filename;
	protected String documentDir;
	protected long lastModified;
	int fileIndex = -1;
	int keyIndex = -1;

	static final String[] DEFAULT_FIELDS =
		{
			"author",
			"id",
			"key",
			"booktitle",
			"editor",
			"year",
			"month",
			"issue",
			"type" };

	public BibtexTable() {
	}

	public BibtexTable(String filename) throws DbException {
		connect("bibtex:" + filename);
	}

	static void hex(StringBuffer buf, long l, int digits) {
		String h = Long.toHexString(l);
		for (int i = h.length(); i < digits; i++)
			buf.append('0');

		buf.append(h);
	}

	static String generateId() {
		long time0 = System.currentTimeMillis();
		long time = System.currentTimeMillis();

		while (time == time0) {
			Thread.yield();
			time = System.currentTimeMillis();
		}

		while (time == System.currentTimeMillis()) {
			Thread.yield();
		}

		byte[] adr;

		try {
			adr = InetAddress.getLocalHost().getAddress();
		} catch (Exception e) {
			adr = new byte[0];
		}

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < adr.length; i++)
			hex(buf, ((int) adr[i]) & 255, 2);

		hex(buf, time, 16);

		return buf.toString();
	}

	protected synchronized void reload() throws DbException {

		File file = new File(filename);

		exists = file.exists();

		System.out.println(
			"trying to (re)load bib file: "
				+ file.getAbsoluteFile()
				+ " existing:"
				+ exists);

		if (exists) {
			try {
				lastModified = file.lastModified();

				org.kobjects.bibtex.BibtexParser parser =
					new org.kobjects.bibtex.BibtexParser(
						new BufferedReader(new FileReader(file)));

				if (getFieldCount() == 0) {

					for (int i = 0; i < DEFAULT_FIELDS.length; i++) {
						if (findField(DEFAULT_FIELDS[i]) <= 0)
							addField(DEFAULT_FIELDS[i], DbField.STRING);
					}

					setIdField(findField("id"));
					keyIndex = findField("key");

					fileIndex = getFieldCount();
					addField("pdfFile", DbField.BINARY);
				}

				int fields = getFieldCount();
				int lastInc = 0;

				while (true) {
					Hashtable entry = parser.nextEntry();
					Object[] dst = new Object[fields];
					if (entry == null)
						break;
					for (Enumeration e = entry.keys(); e.hasMoreElements();) {
						String name = (String) e.nextElement();

						int i = findField(name);
						if (i <= 0) {
							addField(name, DbField.STRING).getNumber();
							fields++;
							Object[] tmp = new Object[fields];
							System.arraycopy(dst, 0, tmp, 0, dst.length);
							dst = tmp;
							lastInc = records.size();
						}
						dst[i - 1] = entry.get(name);
					}
					records.addElement(dst);
				}

				// ensure equal record sizes

				for (int i = 0; i < lastInc; i++) {

					Object[] r = (Object[]) records.elementAt(i);
					Object[] s = new Object[fields];

					System.arraycopy(r, 0, s, 0, r.length);
					records.setElementAt(s, i);
				}
			} catch (IOException e) {
				throw new DbException(e.toString());
			}
		}

	}

	public void connect(String connector) throws DbException {
		filename = connector.substring(connector.indexOf(':') + 1);

		documentDir = null;

		int cut = filename.indexOf(";");
		if (cut != -1) {
			documentDir = filename.substring(cut + 1);
			filename = filename.substring(0, cut);
		}

		reload();
	}

	public void open() throws DbException {
		super.open();
		new Thread(this).start();
	}

	public void run() {
		while (open) {
			try {
				Thread.sleep(15000);
				if (modified)
					rewrite();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void update(int i, Object[] entry) throws DbException {
		if (entry[idField] == null)
			entry[idField] = generateId();
		super.update(i, entry);
		
		modified = true;
	}

	protected void writeEntry(BufferedWriter w, Object[] entry)
		throws IOException {
			
		BibtexWriter bw = new BibtexWriter(w);

		bw.startEntry((String) entry[0], (String) entry[1]);
		for (int j = 2; j < entry.length; j++) {
			if (entry[j] == null || "".equals(entry[j]))
				continue;
			bw.writeField(getField(j).getName(), entry[j].toString());
		}
	}

	public synchronized void rewrite() throws DbException {
		System.out.println("BibtexTable: rewrite() triggered");
		try {
			File nf = new File(filename + ".new");
			BufferedWriter w = new BufferedWriter(new FileWriter(nf));
			for (int i = 0; i < records.size(); i++) {
				writeEntry(w, (Object[]) records.elementAt(i));
			}
			w.close();

			new File(filename + ".bak").delete();
			new File(filename).renameTo(new File(filename + ".bak"));
			nf.renameTo(new File(filename));

			modified = false;
		} catch (IOException e) {
			throw new DbException("" + e);
		}
		System.out.println("BibtexTable: rewrite() finished");
	}

	public void close() throws DbException {
		if (modified) {
			rewrite();
		}
		super.close();
	}

	protected RamRecord getRecords(Vector selected, int[] fields) {
		return new BibtexRecord(this, selected, fields);
	}

	/*
		public static void main(String argv[]) throws DbException {
	
			DbTable table = DbManager.connect("bibtex:" + argv[0]);
	
			table.open();
	
			DbRecord r = table.select(false);
	
			while (r.hasNext()) {
				r.next();
	
				System.out.println(r.getId());
			}
		}
	*/
}