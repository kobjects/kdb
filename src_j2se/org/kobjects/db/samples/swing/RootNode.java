package org.kobjects.db.samples.swing;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.tree.*;

import org.kobjects.swing.*;
import org.kobjects.db.*;
import org.kobjects.db.swing.*;

public class RootNode extends AbstractNode {

    static final String[] FILE_TYPES = { "arff", "bibtex" };

    JPanel panel = new JPanel(new GridBagLayout());
    JTextField urlField = new JTextField(40);
    JTextArea messageArea = new JTextArea();
    JButton openButton = new JButton("open");
    TableBrowser browser;
    JComboBox typeComboBox = new JComboBox(FILE_TYPES);
    JFileChooser fileChooser = new JFileChooser();

    JTextField jdbcUrlField = new JTextField(40);
    JTextField jdbcUserField = new JTextField(40);
    JTextField jdbcPasswordField = new JTextField(40);

    public RootNode(TableBrowser browser) {
        super(null, true);

        this.browser = browser;

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = c.HORIZONTAL;

        panel.add(new JLabel(" "), c);

        c.gridy++;
        panel.add(new JLabel("Table URL:"), c);

        Box box = new Box(BoxLayout.X_AXIS);
        box.add(urlField);
        box.add(new JButton(new InvokeAction("open", this)));

        c.gridy++;
        panel.add(box, c);

        c.gridy++;
        panel.add(new JLabel(" "), c);

        c.gridy++;
        panel.add(new JLabel("Select File:"), c);

        box = new Box(BoxLayout.X_AXIS);
        box.add(typeComboBox);
        box.add(
            new JButton(
                new InvokeAction("open", this, "selectFile")));
        box.add(Box.createGlue());
        c.gridy++;
        panel.add(box, c);

        c.gridy++;
        panel.add(new JLabel(" "), c);

        JPanel jdbcPanel = new JPanel(new BorderLayout());

        JPanel labels = new JPanel(new GridLayout(0, 1));
        JPanel fields = new JPanel(new GridLayout(0, 1));

        labels.add(new JLabel("JDBC URL:"));
        labels.add(new JLabel("JDBC User:"));
        labels.add(new JLabel("JDBC Password"));
        fields.add(jdbcUrlField);
        fields.add(jdbcUserField);
        fields.add(jdbcPasswordField);

        jdbcPanel.add(labels, BorderLayout.WEST);
        jdbcPanel.add(fields, BorderLayout.CENTER);

        c.gridy++;
        panel.add(jdbcPanel, c);

        c.anchor = c.WEST;
        c.fill = c.NONE;
        c.gridy++;
        panel.add(
            new JButton(
                new InvokeAction(
                    "open jdbc connection",
                    this,
                    "openJdbc")),
            c);

        c.gridy++;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = c.BOTH;
        panel.add(new JPanel(), c);
    }

    public void open() {
        open(urlField.getText());
    }

	public void openJdbc (String url, String user, String pw) {
        add(new JdbcNode(this, url, user, pw), true);			
	}

    public void openJdbc() {
        /*        open(
                    jdbcUrlField.getText()
                        + ";user="
                        + jdbcUserField.getText()
                        + ";password="
                        + jdbcPasswordField.getText()
                        + ";table=ALL_TABLES");*/
        

		openJdbc (jdbcUrlField.getText(),
                jdbcUserField.getText(),
                jdbcPasswordField.getText());
    }

    public void open(String connector) {
        try {
            DbTable table = DbManager.connect(connector);
            table.open();

            add(new TableNode(this, table, connector), true);
        }
        catch (Exception e) {
            TableBrowser.error(e, null);
        }
    }

    public void selectFile() {
        if (fileChooser.showOpenDialog(panel)
            == JFileChooser.APPROVE_OPTION)
            urlField.setText(
                ""
                    + typeComboBox.getSelectedItem()
                    + ":"
                    + fileChooser.getSelectedFile());
    }

    public Component getComponent() {
        return panel;
    }

    public String toString() {
        return "TableBrowser";
    }
}