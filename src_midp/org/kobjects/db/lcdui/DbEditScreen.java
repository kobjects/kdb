package org.kobjects.db.lcdui;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Joerg Pleumann
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
//import javax.microedition.rms.*;
import org.kobjects.db.*;

public class DbEditScreen extends Form implements CommandListener {

    public static Command OK = new Command("Save", Command.OK, 1);
    public static Command CANCEL = new Command("Cancel", Command.CANCEL, 2);

    private MIDlet midlet;

    private DbRecord record;

    private int[] fields;

    private Object[] widgets;

    private CommandListener listener;

    private Displayable next;

    public DbEditScreen(String title, DbRecord record, String[] fields, MIDlet midlet, CommandListener listener) {
        super(title);

        this.record = record;
        this.fields = new int[fields.length];
        this.widgets = new Object[fields.length];

        addWidgets(fields);

        this.midlet = midlet;
        this.listener = listener;

        setCommandListener(this);

        addCommand(OK);
        addCommand(CANCEL);
    }

    private void addWidgets(String[] names) {
        DbTable table = record.getTable();

        for (int i = 0; i < fields.length; i++) {
            fields[i] = table.findField(names[i]);

            String label = table.getField(fields[i]).getLabel();
            if ((label == null) || "".equals(label)) label = table.getField(fields[i]).getName();
            label = label + ":";

            if ((table.getField(fields[i]).getType() == DbField.BOOLEAN) || (table.getField(fields[i]).getValues() != null)) {
                String[] values = table.getField(fields[i]).getValues();
                if (values == null) values = new String[] {"No", "Yes"};

                int mode = table.getField(fields[i]).getType() == DbField.BITSET ? Choice.MULTIPLE : Choice.EXCLUSIVE;
                ChoiceGroup group = new ChoiceGroup(label, mode, values, null);
                append(group);
                widgets[i] = group;
            }
            else if (table.getField(fields[i]).getType() == DbField.GRAPHICS) {
                Image img = Image.createImage(10, 10);
                ImageItem image = new ImageItem(label, img, table.getField(fields[i]).getConstraints(), "Image");
                append(image);
                widgets[i] = image;
            }
            else if (table.getField(fields[i]).getType() == DbField.DATETIME) {
                DateField daf = new DateField(label, table.getField(fields[i]).getConstraints(), null);
                append(daf);
                widgets[i] = daf;
            }
            else {
                TextField entry = new TextField(label, "", 64, table.getField(fields[i]).getConstraints());
                if (table.getField(fields[i]).getMaxSize() != 0) entry.setMaxSize(table.getField(fields[i]).getMaxSize());
                append(entry);
                widgets[i] = entry;
            }
        }

        /* Workaround for scroll problem on iDEN phones */

        if ((widgets.length != 0) && (widgets[widgets.length - 1] instanceof ImageItem)) {
            append("\n\n\n ");
        }
    }

    private void refresh() { //throws IOException, RecordStoreException {
        DbTable table = record.getTable();

        for (int i = 0; i < fields.length; i++) {
            if (widgets[i] instanceof ChoiceGroup) {
                ChoiceGroup group = (ChoiceGroup)widgets[i];
                int type = table.getField(fields[i]).getType();

                if (type == DbField.BOOLEAN) {
                    group.setSelectedIndex(record.getBoolean(fields[i]) ? 1 : 0, true);
                }
                else if (type == DbField.INTEGER) {
                    group.setSelectedIndex(record.getInteger(fields[i]), true);
                }
                else if (type == DbField.BITSET) {
                    boolean[] selects = new boolean[group.size()];
                    int value = record.getInteger(fields[i]);
                    for (int j = 0; j < selects.length; j++) {
                        if ((value & (1 << j)) != 0) selects[j] = true;
                    }
                    group.setSelectedFlags(selects);
                }
            }
            else if (widgets[i] instanceof ImageItem) {
                byte[] bytes = record.getBinary(fields[i]);
                ((ImageItem)widgets[i]).setImage(Image.createImage(bytes, 0, bytes.length));
            }
            else if (widgets[i] instanceof DateField) {
                long date = record.getLong(fields[i]);
                ((DateField)widgets[i]).setDate(new Date(date));
            }
            else {
                ((TextField)widgets[i]).setString(record.getString(fields[i]));
            }
        }
    }

    private void update() throws DbException {
        DbTable table = record.getTable();

        for (int i = 0; i < fields.length; i++) {
            int type = table.getField(fields[i]).getType();

            if (widgets[i] instanceof ChoiceGroup) {
                ChoiceGroup group = (ChoiceGroup)widgets[i];

                if (type == DbField.BOOLEAN) {
                    record.setBoolean(fields[i], group.isSelected(1));
                }
                else if (type == DbField.INTEGER) {
                    record.setInteger(fields[i], group.getSelectedIndex());
                }
                else if (type == DbField.BITSET) {
                    boolean[] selects = new boolean[group.size()];
                    group.getSelectedFlags(selects);
                    int value = 0;
                    for (int j = 0; j < selects.length; j++) {
                        if (selects[j]) value = value | (1 << j);

                        System.out.println(value);

                    }
                    record.setInteger(fields[i], value);
                }
            }
            else {
                if (type == DbField.INTEGER) {
                    record.setInteger(fields[i], Integer.parseInt(((TextField)widgets[i]).getString()));
                }
                else if (type == DbField.DATETIME) {
                    record.setLong(fields[i], ((DateField)widgets[i]).getDate().getTime());
                }
                else if (type == DbField.STRING) {
                    record.setString(fields[i], ((TextField)widgets[i]).getString());
                }
            }
        }

        record.update();
    }

    public void commandAction(Command cmd, Displayable dsp) {
        try {
            if (cmd == OK) update();

            Display.getDisplay(midlet).setCurrent(next);
        }
        catch (Exception error) {
            showError(error);
        }

        if (listener != null) listener.commandAction(cmd, dsp);
    }

    private void showError(Exception error) {
        error.printStackTrace();

        String cls = '.' + error.getClass().getName();
        String msg = error.getMessage();

        String out = msg + " (" + cls.substring(cls.lastIndexOf('.') + 1) + ")";

        Alert alert = new Alert("Error", out, null, AlertType.ERROR);
        alert.setTimeout(Alert.FOREVER);
        Display display = Display.getDisplay(midlet);
        display.setCurrent(alert, display.getCurrent());
    }

    public void showScreen(Displayable next) { //throws RecordStoreException, IOException {
        this.next = next;
        refresh();
        Display.getDisplay(midlet).setCurrent(this);
    }
}
