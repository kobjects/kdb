package org.kobjects.db.samples.wrapper;

import java.sql.*;

public class KDBTest {

    public static void main(String[] argv) throws Exception {
	//ResultSet rs = new org.kobjects.db.ResultSetWrapper("bibtex:/app/unido-i08/share/bibserver/database/literatur.bib");
	ResultSet rs = new org.kobjects.db.ResultSetWrapper("arff:iris.arff");
	while (rs.next()) {
	    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++)
		System.out.print(rs.getObject(i)+"\t");
	    System.out.println();
	}
    }

}
