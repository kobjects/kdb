package org.kobjects.db;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Jörg Pleumann
 * @version 1.0
 */

/**
 * Represents a compound condition for filtering a table. Basically the
 * WHERE [...] part in an SQL SELECT statment.
 */
public class DbCondition {

    /**
     * Type constant for "less than".
     */
    public static final int LT   = 1;

    /**
     * Type constant for "greater than".
     */
    public static final int GT   = 2;

    /**
     * Type constant for "less or equal".
     */
    public static final int LE  = 3;

    /**
     * Type constant for "greater or equal".
     */
    public static final int GE   = 4;

    /**
     * Type constant for "equal".
     */
    public static final int EQ   = 5;

    /**
     * Type constant for "not equal".
     */
    public static final int NE   = 6;

    /**
     * Type constant for "textually equal". Results in a case-insensitive String
     * comparison.
     */
    public static final int EQ_TEXT = 7;

    /**
     * Type constant for AND operator. The value of a node of type AND is
     * true if and only if all its children evaluate to true.
     */
    public static final int AND  = 16;

    /**
     * Type constant for OR operator. The value of a node of type OR is
     * true if at least one its children evaluates to true, and false otherwise.
     */
    public static final int OR   = 17;

    /**
     * Type constant for XOR operator. A node of type XOR must have exactly two
     * children. It evaluates to true if and only if its children evaluate
     * to different truth values.
     */
    public static final int XOR  = 18;

    /**
     * Type constant for NOT operator. A node of type NOT must have exactly one
     * child. It evaluates to true if and only if this child evaluates to false.
     */
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
     * Holds the node's field type, in case this is a leaf node. This value
     * is set when the Condition is assigned a table.
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
    private DbCondition[] children;


	private DbTable table;

    /**
     * Creates a new Condition node for a relation between a field (given by its
     * number) and a value. The operator must be one of LT, GT, LE, GE, EQ, NE,
     * or EQIC. 
     */
    public DbCondition(int operator, int field, Object value) throws DbException {
        if ((operator < LT) || (operator > EQ_TEXT)) {
            throw new DbException("Illegal type code \"" + type + "\" for leaf node.");
        }

        if (operator == EQ_TEXT) {
            value = value.toString().toUpperCase();
        }

        this.operator = operator;
        this.field = field;
        this.value = value;
    }

    /**
     * Creates a new Condition node for an AND, OR, XOR or NOT operator.
     */
    public DbCondition(int operator, DbCondition[] children) throws DbException {
        if ((operator < AND) || (operator > NOT)) {
            throw new DbException("Illegal type code \"" + type + "\" for inner node.");
        }

        this.operator = operator;
        this.children = children;
    }

    /**
     * Assigns this condition a table.
     */
    public void setTable(DbTable table) {
		this.table = table;
        if (operator >= AND) {
            for (int i = 0; i < children.length; i++) {
                children[i].setTable(table);
            }
        }
        else {
            type = table.getField(field).getType();
        }
    }

    /**
     * Evaluates this Condition for the given record.
     */
    public boolean evaluate(Object[] values) {
        //System.out.println("evaluate(): " + this.toString() + " of type " + operator);

        if (operator < AND) {
            Object obj =  values[field];

            switch (operator) {
                case LT: {
                    return DbField.compare(type, obj, value) < 0;
                }

                case GT: {
                    return DbField.compare(type, obj, value) > 0;
                }

                case LE: {
                    return DbField.compare(type, obj, value) <= 0;
                }

                case GE: {
                    return DbField.compare(type, obj, value) >= 0;
                }

                case EQ: {
                    return DbField.compare(type, obj, value) == 0;
                }

                case NE: {
                    return DbField.compare(type, obj, value) != 0;
                }

                case EQ_TEXT: {
                    return obj.toString().toUpperCase().equals(value.toString());
                }
            }
        }
        else {
            switch (operator) {
                case AND: {
                    for (int i = 0; i < children.length; i++) {
                        if (!children[i].evaluate(values)) return false;
                    }

                    return true;
                }

                case OR: {
                    for (int i = 0; i < children.length; i++) {
                        if (children[i].evaluate(values)) return true;
                    }

                    return false;
                }

                case XOR: {
                    return children[0].evaluate(values) ^ children[1].evaluate(values);
                }

                case NOT: {
                    return !children[0].evaluate(values);
                }
            }
        }

        return false; // To make compiler happy
    }
    
    
    
    public String toString () {

        if (operator < AND) {
            String f = table.getField (field).getName ();

			String v = value instanceof String ? "'"+value+"'" : ""+value;

            switch (operator) {
                case LT: return f + " < " + v;
                case GT: return f + " > " + v;
                case LE: return f + " <= " + v;
                case GE: return f + " >= " + v;
                case EQ: return f + " = " + v;
                case NE: return f + " != " + v;
                case EQ_TEXT: throw new RuntimeException ("NYI");//return f + " = " + v;
			    default: throw new RuntimeException ("illegal operator: "+operator);
            }
        }
        else if (operator == NOT) {
			return "NOT("+children [0]+")";            
        }
        else {
            StringBuffer buf = new StringBuffer ("(");
            buf.append (children [0].toString ());
			for (int i = 1; i < children.length; i ++) {
			    switch (operator) {
			    case AND: buf.append (" AND "); break;
			    case OR: buf.append (" OR "); break;
			    case XOR: buf.append (" XOR "); break;
			    default: throw new RuntimeException ("illegal operator: "+operator);
		    	}
				buf.append (children [i].toString ());
			}
			buf.append (")");			
			return buf.toString ();
	    }
    }
    
}
