package org.kobjects.db;

/*
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Jörg Pleumann
 * @version 1.0
 */

/**
 * Describes a field in a table.
 */
public class DbField {

    /**
     * Is the type constant for boolean fields.
     */
    public static final byte BOOLEAN = 1;

    /**
     * Is the type constant for integer fields.
     */
    public static final byte INTEGER = 2;

    /**
     * Is the type constant for long values.
     */
    public static final byte LONG = 3;

    /**
     * Is the type constant for String values.
     */
    public static final byte STRING = 4;

    /**
     * Is the type constant for binary values.
     */
    public static final byte BINARY = 5;

    /**
     * Is the type constant for sets of up to 32 items. Fields of type BITSET
     * are stored like INTEGER fields, but are handled differently in the user
     * interface.
     */
    public static final byte BITSET = 6;

    /**
     * Is the type constant used for date/time values. Fields of type DATETIME
     * are stored like LONG fields, but are handled differently in the user
     * interface.
     */
    public static final byte DATETIME = 7;

    /**
     * Is the type constant used for graphics values. Fields of type GRAPHICS
     * are stored like BINARY fields, but are handled differently in the user
     * interface.
     */
    public static final byte GRAPHICS = 8;

    /**
     * Holds the table this field belongs to.
     */
    private DbTable table;

    /**
     * Holds the field's number.
     */
    private int number;

    /**
     * Holds the field's name.
     */
    private String name;

    /**
     * Holds the field's type.
     */
    private int type;

    /**
     * Holds the field's maximum size. This is most often used for strings and
     * currently only interpreted in the UI.
     */
    private int maxSize;

    /**
     * Holds the field's input constraints. This is most often used for strings and
     * currently only interpreted in the UI.
     */
    private int constraints;

    /**
     * Holds the field's label. This is only interpreted in the UI. If the label
     * is null, the field's name is displayed instead.
     */
    private String label;

    /**
     * Holds the field's possible values. This is only interpreted in the UI,
     * and its handling depends on the field type. Basically, a ChoiceGroup
     * holding the given values is displayed, and the user can select one of
     * these values. Free-text input is not possible.
     */
    private String[] values;

    /**
     * Holds the field's default value. This value is assigned to the
     * corresponding column of a newly inserted record.
     */
    private Object defValue;

    /**
     * Creates a new field. This constructor is package-visible. From a user's
     * perspective, new fields are created using the table's <code>addField()</code>
     * factory method.
     */

    DbField(DbTable table, int number, String name, int type) {
        this.table = table;
        this.number = number;
        this.name = name;
        this.type = type;
    }

    /**
     * Returns the field's number. Field numbering starts from zero.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns the field's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the field's type.
     */
    public int getType() {
        return type;
    }

    /**
     * Changes the field's default value. Note that:
     * <ul>
     *   <li>Wrapper classes have to be used for primitive types.</li>
     *   <li>The actual class has to match the field type. Otherwise a
     *       <code>ClassCastException</code> will occur later.</li>
     * </li>
     */
    public void setDefault(Object value) {
        this.defValue = value;
    }

    /**
     * Returns the field's default value.
     */
    public Object getDefault() {
        return defValue;
    }

    /**
     * Returns the field's label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the field's maximum size.
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Returns the field's input constraints.
     */
    public int getConstraints() {
        return constraints;
    }

    /**
     * Returns the field's possible values.
     */
    public String[] getValues() {
        if (values == null) return null;

        String[] result = new String[values.length];
        System.arraycopy(values, 0, result, 0, values.length);

        return values;
    }

    /**
     * Changes the field's UI settings.
     */
    public void setProperties(String label, int maxSize, int constraints, String[] values) {
        this.label = label;
        this.maxSize = maxSize;
        this.constraints = constraints;
        this.values = values;
    }

    /**
     * Utility method to compare two field values. The type of the values must
     * be specified. The method returns a value smaller than zero if value1 < value2,
     * a value greater than zero if if value1 > value2, and zero otherwiese.
     * Binary values are compared according to their length, just to have a
     * defined behaviour for all types.
     */
    public static int compare(int type, Object value1, Object value2) {
        switch (type) {
            case DbField.INTEGER:
            case DbField.BITSET: {
                return ((Integer)value1).intValue() - ((Integer)value2).intValue();
            }

            case DbField.LONG:
            case DbField.DATETIME: {
                long l = ((Long)value1).longValue() - ((Long)value2).longValue();

                if (l < 0) return -1; else if (l > 0) return 1; else return 0;
            }

            case DbField.STRING:
            case DbField.BOOLEAN: {
                return value1.toString().compareTo(value2.toString());
            }

            case DbField.BINARY:
            case DbField.GRAPHICS:
                return ((byte[])value1).length - ((byte[])value2).length;

            default: {
                throw new IllegalArgumentException("Illegal type \"" + type + "\"");
            }
        }
    }
}
