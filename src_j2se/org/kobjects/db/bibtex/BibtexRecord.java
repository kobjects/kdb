package org.kobjects.db.bibtex;

// (C) 2002 by Stefan Haustein 
// Rolandstrasse 27, D-46045 Oberhausen, Germany
// All rights reserved.
//
// For licensing details, please refer to the file "license.txt",
// distributed with this file.


import java.util.*;
import org.kobjects.db.ram.*;

/**
 * @author Stefan Haustein */

public class BibtexRecord extends RamRecord {


	BibtexRecord(BibtexTable table, Vector selected, int[] fields) {
		super(table, selected, fields);
	}

}
