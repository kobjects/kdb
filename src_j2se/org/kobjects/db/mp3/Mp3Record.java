package org.kobjects.db.mp3;

/**
 * @author haustein
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import java.util.*;
import java.io.*;
import org.kobjects.db.ram.*;

public class Mp3Record extends RamResultSet {

	Mp3Record(RamTable table, Vector selected, int[] fields) {
		super(table, selected, fields);
	}

	public Object getObject(int index) {
		if (index == 7) {
			try {
				return new FileInputStream((String) values[1]);
			} catch (IOException e) {
				throw new RuntimeException(e.toString());
			}
		} else
			return super.getObject(index);
	}

}
