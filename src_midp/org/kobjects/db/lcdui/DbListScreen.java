package org.kobjects.db.lcdui;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import org.kobjects.db.*;

public class DbListScreen extends List implements CommandListener {

    public static Command OK = new Command("Ok", Command.OK, 1);
    public static Command EDIT = new Command("Edit", Command.OK, 2);
    public static Command BACK = new Command("Back", Command.BACK, 3);
    public static Command CANCEL = new Command("Cancel", Command.CANCEL, 4);
    public static Command NEW = new Command("New", Command.SCREEN, 5);
    public static Command DELETE = new Command("Delete", Command.SCREEN, 6);
    public static Command EXIT = new Command("Exit", Command.EXIT, 7);

    private String title;

    private DbRecord record;

    private Image icon;

    private int primaryField = -1;

    private int secondaryField = -1;

    private boolean sorted;

    private String editTitle;

    private String[] editFields;

    private MIDlet midlet;

    private CommandListener listener;

    private Command[] commands;

    private Command selectCommand;

    private Command lastCommand;

    private Displayable next;

    public DbListScreen(String title, DbRecord record, String icon, MIDlet midlet, CommandListener listener) throws DbException {
        super(title, Choice.IMPLICIT);

        this.record = record;

        if (icon != null) {
            try {
                this.icon = Image.createImage(icon);
            }
            catch (IOException ignored) {
            }
        }

        this.midlet = midlet;
        this.listener = listener;

        setCommands(null);
        setCommandListener(this);
    }

    public void refresh() throws DbException {
        for (int i = size() - 1; i >= 0; i--) delete(i);

        record.reset();

        while (record.hasNext()) {
            record.next();
            String s = getRecordText();

            append(s != null ? s : "(null)", icon);
        }
    }

    public void setListFields(String primaryField, String secondaryField, boolean sorted) {
        this.primaryField = record.getTable().findField(primaryField);
        this.secondaryField = record.getTable().findField(secondaryField);
        this.sorted = sorted;
    }

    public void setEditFields(String title, String[] fields) {
        this.editTitle = title;
        this.editFields = fields;
    }

    public void setCommands(Command[] cmds) {
        if (commands != null) {
            for (int i = 0; i < commands.length; i++) removeCommand(commands[i]);
        }

        if (cmds == null) {
            commands = new Command[] {EDIT, NEW, DELETE, BACK};
        }
        else {
            commands = cmds;
        }

        for (int i = 0; i < commands.length; i++) addCommand(commands[i]);
    }

    public void addCommand(Command cmd) {
        if (cmd == OK) {
            selectCommand = OK;
        }
        else if (cmd == EDIT) {
            selectCommand = EDIT;
        }
        else super.addCommand(cmd);
    }

    public void commandAction(Command cmd, Displayable dsp) {
        if ((cmd == SELECT_COMMAND) && (dsp == this) && (selectCommand != null)) {
            cmd = selectCommand;
        }

        try {
            if ((cmd == OK) || (cmd == CANCEL) || (cmd == BACK)) {
                Display.getDisplay(midlet).setCurrent(next);
            }

            else if (cmd == NEW) {
                record.insert();
                new DbEditScreen(editTitle, record, editFields, midlet, this).showScreen(this);
            }

            else if (cmd == EDIT) {
                int i = getSelectedIndex();

                if (i != -1) {
                    record.absolute(i);
                    new DbEditScreen(editTitle, record, editFields, midlet, this).showScreen(this);
                }
            }

            else if (cmd == DELETE) {
                int i = getSelectedIndex();

                if (i != -1) {
                    record.absolute(i);
                    record.delete();
                    delete(i);
                }
            }

            else if (cmd == DbEditScreen.OK) {
                if (sorted) {
                    refresh();
                }
                else {
                    if (lastCommand == NEW) {
                        append(getRecordText(), icon);
                    }
                    else {
                        set(getSelectedIndex(), getRecordText(), icon);
                    }
                }
            }

            else if (cmd == EXIT) {
                midlet.notifyDestroyed();
            }

            lastCommand = cmd;

            if (listener != null) listener.commandAction(cmd, dsp);
        }
        catch (Exception error) {
            showError(error);
        }
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

    public void showScreen(Displayable next) throws DbException {
        this.next = next;
        refresh();
        Display.getDisplay(midlet).setCurrent(this);
    }

    public int getSelectedId() throws DbException {
        int i = getSelectedIndex();

        if (i != -1) {
            return ((Integer) record.getId()).intValue ();
        }
        else {
            return -1;
        }
    }

    private String getRecordText() {
        String s = (primaryField == -1 ? "#" + record.getId() : record.getString(primaryField));
        if (secondaryField != -1) s = s + " (" + secondaryField + ")";

        return s;
    }
}
