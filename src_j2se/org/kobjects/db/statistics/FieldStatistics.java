package org.kobjects.db.statistics;

import java.util.Hashtable;
import org.kobjects.db.*;

public class FieldStatistics {


    public Hashtable values = new Hashtable ();
    public double min = Double.POSITIVE_INFINITY;
    public double max = Double.NEGATIVE_INFINITY;

    public long count;
    public long nullCount;
    public boolean numeric = true;
    public int decimals;
    

    public static FieldStatistics [] generate (DbResultSet record) throws DbException{

	DbTable table = record.getTable ();
        FieldStatistics stat[] = new FieldStatistics [table.getColumnCount ()];

        for (int i = 0; i < stat.length; i++) 
            stat [i] = new FieldStatistics ();

	record.beforeFirst ();
        while (!record.isLast () && !record.isAfterLast()) {
	    record.next ();
            for (int i = 0; i < stat.length; i++) 
                stat [i].update (record.getObject (i+1));
        }


        return stat;
    }


    
    public void update (Object v) {
        count++;

        if (v == null) 
            nullCount++;
        else {
            String value = v.toString ();
            
            if (values != null) {
                
                if (values.size () > 1000) 
                    values = null;
                else { 
                    Integer count = (Integer) values.get (value);
                    if (count == null) count = new Integer (1);
                    else count = new Integer (count.intValue () + 1);
                    values.put (value, count);
                }                
            }   
            if (numeric) {
                value = value.trim ();
                        
                int dot = value.indexOf ('.');
                if (dot != -1) {
                    if (value.length () - dot > decimals) 
                        decimals = value.length () - dot;
                }
                
                if (value.equals ("")) 
                            nullCount++;
                else {
                    try {
                        double d = Double.parseDouble (value);
                        if (d > max) max = d;
                                if (d < min) min = d;
                    }
                    catch (NumberFormatException e) {
                        numeric = false;
                    }
                }
            }
        }
    }

    

    public String toString () {
        return "count: "+count + " nullCount: "+ nullCount 
            + " distinct values: "
            + (values == null 
               ? " >1000" 
               : (values.size() < 10 ? ""+values : ""+values.size ()))
            + "numeric: " 
            + (numeric ? ("yes;  min: "+ min + " max: "+ max) : "no");
    }



}



