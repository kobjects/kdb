package org.kobjects.db.samples.swing;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.kobjects.swing.*;
import org.kobjects.db.*;
import org.kobjects.db.swing.*;

public class TableBrowser extends JFrame {

    RootNode rootNode = new RootNode(this);

    TableBrowser() {

        super("TableBrowser");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                rootNode.remove();
                System.exit(0);
            }
        });

        getContentPane().add(
            new TreePane(rootNode),
            BorderLayout.CENTER);

        pack();
        show();
    }

    public static void error(Exception e, String msg) {

        Object message;

        if (e != null) {
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            JTextArea jta = new JTextArea();
            jta.setText(
                (msg != null ? (msg + "\n\n") : "")
                    + w.toString());
            jta.setEditable(false);

            message = new JScrollPane(jta);
        }
        else
            message = msg;

        JOptionPane.showMessageDialog(
            null,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE); 

    }

    public static void main(String[] argv) {
        TableBrowser tb = new TableBrowser();

        for (int i = 0; i < argv.length; i++) {
            if (argv[i].equals("-connect")) {
                tb.rootNode.openJdbc(
                    argv[i + 1],
                    argv[i + 2],
                    argv[i + 3]);
                i += 3;
            }
            else
                tb.rootNode.open(argv[i]);
        }

    }
}