package org.kobjects.db.rms;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import javax.microedition.rms.*;
import org.kobjects.db.*;

public class RmsDbIndex implements RecordFilter, RecordComparator {

    private RmsDbTable table;

    private DbExpression filter;

    private int orderField = -1;

    private boolean orderReverse = false;

    private int orderType;

    public RmsDbIndex(RmsDbTable table) {
        this.table = table;
    }

    public void setFilter(DbExpression expression) throws DbException {
        filter = expression;
        if (filter != null) filter.setTable(table);
    }

    public void setFilter(int field, Object value, boolean inverse) throws DbException {
        DbExpression expr = new DbExpression(DbExpression.EQ, field, value);
        if (inverse) {
            expr = new DbExpression(DbExpression.NOT, new DbExpression[] {expr});
        }

        setFilter(expr);
    }

    public void setOrder(int field, boolean reverse) {
        orderField = field;
        orderReverse = reverse;
        orderType = table.getField(orderField).getType();
    }

    public boolean matches(byte[] candidate) {
        System.out.println("[SQL4ME] Filtering record");

        int id = candidate[0] | candidate[1] | candidate[2] | candidate[3];

        if (id == 0) return false;

        try {
            if (filter != null) {
                Object[] values = new Object[table.getFieldCount()];
                table.unpack(candidate, values);
                if (!filter.evaluate(values)) {
                    System.out.println("FALSE");
                    return false;
                }
            }
        }
        catch (Exception error) {
            System.out.println("Exception while filtering:");
            error.printStackTrace();
            return false;
        }

        System.out.println("TRUE");
        return true;
    }

    public int compare(byte[] rec1, byte[] rec2) {
        if (orderField == -1) {
            int id1 = rec1[0] << 24 | rec1[1] << 16 | rec1[2] << 8 | rec1[3];
            int id2 = rec2[0] << 24 | rec2[1] << 16 | rec2[2] << 8 | rec2[3];

            if (id1 < id2) {
                return (orderReverse ? RecordComparator.FOLLOWS : RecordComparator.PRECEDES);
            }
            else if (id1 > id2) {
                return (orderReverse ? RecordComparator.PRECEDES : RecordComparator.FOLLOWS);
            }
            else {
                return RecordComparator.EQUIVALENT;
            }
        }
        else {
            try {
                Object[] values1 = new Object[table.getFieldCount()];
                table.unpack(rec1, values1);

                Object[] values2 = new Object[table.getFieldCount()];
                table.unpack(rec2, values2);

                if ((values1[orderField] == null) || (values2[orderField] == null)) return RecordComparator.EQUIVALENT;

                int result = DbExpression.compare(orderType, values1[orderField], values2[orderField]);
                if (orderReverse) result = -result;

                if (result < 0) return RecordComparator.PRECEDES;
                else if (result > 0) return RecordComparator.FOLLOWS;
                else return RecordComparator.EQUIVALENT;
            }
            catch (Exception error) {
                System.out.println("Exception while Comparing:");
                error.printStackTrace();
                return RecordComparator.EQUIVALENT;
            }
        }
    }

    public boolean isSorting() {
        return (orderField != -1);
    }
}
