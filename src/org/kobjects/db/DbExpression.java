package org.kobjects.db;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Jörg Pleumann
 * @version 1.0
 */

public class DbExpression {

    public static final int LT   = 1;
    public static final int GT   = 2;
    public static final int LEQ  = 3;
    public static final int GEQ  = 4;
    public static final int EQ   = 5;
    public static final int NEQ  = 6;
    public static final int EQIC = 7;

    public static final int AND  = 16;
    public static final int OR   = 17;
    public static final int XOR  = 18;
    public static final int NOT  = 19;

    /**
     * Denotes the node's type. Legal values are the constants above.
     */
    private int operator;

    /**
     * Holds the node's field number, in case this is a leaf node.
     */
    private int field;

    /**
     * Holds the node's field number, in case this is a leaf node.
     */
    private int type;

    /**
     * Holds the value to compare the given field with, in case this is a leaf
     * node.
     */
    private Object value;

    /**
     * Holds the array of subordinate notes, in case this in an inner node.
     */
    private DbExpression[] children;

    public DbExpression(int operator, int field, Object value) throws DbException {
        if ((operator < LT) || (operator > EQIC)) {
            throw new DbException("Illegal type code \"" + type + "\" for leaf node.");
        }

        this.operator = operator;
        this.field = field;
        this.value = value;
    }

    public DbExpression(int operator, DbExpression[] children) throws DbException {
        if ((operator < AND) || (operator > NOT)) {
            throw new DbException("Illegal type code \"" + type + "\" for inner node.");
        }

        this.operator = operator;
        this.children = children;
    }

    public void setTable(DbTable table) {
        System.out.println("setTable(): " + this.toString());

        if (operator >= AND) {
            for (int i = 0; i < children.length; i++) {
                children[i].setTable(table);
            }
        }
        else {
            type = table.getField(field).getType();
        }
    }

    public boolean evaluate(Object[] record) {
        System.out.println("evaluate(): " + this.toString() + " of type " + operator);

        if (operator < AND) {
            switch (operator) {
                case LT: {
                    return compare(type, record[field], value) < 0;
                }

                case GT: {
                    return compare(type, record[field], value) > 0;
                }

                case LEQ: {
                    return compare(type, record[field], value) <= 0;
                }

                case GEQ: {
                    return compare(type, record[field], value) >= 0;
                }

                case EQ: {
                    return compare(type, record[field], value) == 0;
                }

                case NEQ: {
                    return compare(type, record[field], value) != 0;
                }

                case EQIC: {
                    return compare(type, record[field].toString().toUpperCase(), value) == 0;
                }
            }
        }
        else {
            switch (operator) {
                case AND: {
                    for (int i = 0; i < children.length; i++) {
                        if (!children[i].evaluate(record)) return false;
                    }

                    return true;
                }

                case OR: {
                    for (int i = 0; i < children.length; i++) {
                        if (children[i].evaluate(record)) return true;
                    }

                    return false;
                }

                case XOR: {
                    return children[0].evaluate(record) ^ children[1].evaluate(record);
                }

                case NOT: {
                    return !children[0].evaluate(record);
                }
            }
        }

        return false; // Dummy
    }

    public static int compare(int type, Object actual, Object formal) {
        System.out.println("compare(): " + actual + " / " + formal);

        switch (type) {
            case DbField.INTEGER:
            case DbField.BITSET: {
                return ((Integer)actual).intValue() - ((Integer)formal).intValue();
            }

            case DbField.LONG:
            case DbField.DATETIME: {
                long l = ((Long)actual).longValue() - ((Long)formal).longValue();

                if (l < 0) return -1; else if (l > 0) return 1; else return 0;
            }

            case DbField.STRING:
            case DbField.BOOLEAN: {
                return actual.toString().compareTo(formal.toString());
            }
        }

        return 0; // Default
    }
}
