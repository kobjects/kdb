package org.kobjects.db.swing;

import java.awt.*;
import javax.swing.*;

import org.kobjects.db.*;
import org.kobjects.swing.*;


public class SelectComponent extends JPanel {

    public static final int CONDITION = 1;
    public static final int FIELDS = 2;
    public static final int ORDER = 4;

    DbTable table;
    String[] fieldNames;

    TreePane conditionPane = null;
    JPanel fieldPane = null;
    JCheckBox [] fieldBoxes;
    JComboBox orderBox;
    JCheckBox inverseBox;
    
    public abstract class ConditionNode extends AbstractNode {
	abstract String getExpression ();

	public ConditionNode (AbstractNode parent, boolean may) {
	    super (parent, may);
	}

	String getExpressionAt (int i) {
	    return ((ConditionNode) getChildAt (i)).getExpression ();
	}
    }

    public class Node extends ConditionNode {

	JPanel panel = new JPanel (new GridBagLayout ());
	JComboBox combo;

	Node (Node parent) {
	    super (parent, true);

	    GridBagConstraints c = new GridBagConstraints ();
	    
	    //c.weightx = 1;
	    c.weighty = 0;
	    c.gridx = 0;
	    c.gridy = 0;
	    c.fill = GridBagConstraints.HORIZONTAL;

	    combo = new JComboBox 
		(parent == null
		 ? new String[] {"(root)", "AND", "OR", "NOT", "XOR"}
		 : new String[] {"AND", "OR", "NOT", "XOR"});

	    panel.add (new JLabel ("Node Type:"), c);
	    
	    c.gridy++;
	    panel.add (combo, c);

	    /*  c.fill = c.BOTH;
	    c.gridy++;
	    c.weighty = 1;
	    panel.add (Box.createGlue (), c);

	    c.fill = c.HORIZONTAL;
	    c.weighty = 0;*/

	    if (parent != null) {
		c.gridy++;
		panel.add (new JButton 
		    (new InvokeAction ("remove", this, "remove")), c);
	    }
	    c.gridy++;
	    panel.add (new JButton 
		(new InvokeAction ("add node", this, "addNode")), c);
	    c.gridy++;
	    panel.add (new JButton 
		(new InvokeAction ("add leaf", this, "addLeaf")), c);
	}
	

	public Component getComponent () {
	    return panel;
	}


	public void addNode () {
	    add (new Node (this), true);
	}

	public void addLeaf () {
	    add (new Leaf (this), true);
	}

	public String toString () {
	    return ""+combo.getSelectedItem ();
	}

	public String getExpression () {
	    String name = toString ();

	    if (getChildCount () == 0) return "";

	    if (name.equals ("(root)")) {
		if (getChildCount() != 1) 
		    throw new RuntimeException 
			("(root) must have exactly one child element");

		return getExpressionAt (0);
	    }

	    if (name.equals ("NOT")) {
		if (getChildCount() != 1) 
		    throw new RuntimeException 
			("NOT must have exactly one child element");

		return "NOT(" + getExpressionAt (0) + ")";
	    }

	    StringBuffer buf = new StringBuffer ();
	    buf.append ('(');
	    buf.append (getExpressionAt (0));
	    buf.append (") ");
	    for (int i = 1; i < getChildCount (); i++) {
		buf.append (name);
		buf.append (" (");
		buf.append (getExpressionAt (i));
		buf.append (") ");
	    }
	    return buf.toString ();
	}
    }

    public class Leaf extends ConditionNode {
	
	JPanel panel = new JPanel (new GridBagLayout ());
	JComboBox fieldBox = new JComboBox (fieldNames);
	JComboBox opBox = new JComboBox (new String [] 
	    {"=", "==", "!=", "<", ">", "<=", ">="});
	JTextField valueField = new JTextField (32);

	Leaf (Node parent) {
	    super (parent, false);
	    
	    GridBagConstraints c = new GridBagConstraints ();
	    c.gridx = 0;
	    c.gridy = 0;
	   	    c.fill = GridBagConstraints.HORIZONTAL;

	    panel.add (fieldBox, c);
	    c.gridy++;
	    panel.add (opBox, c);
	    c.gridy++;
	    panel.add (valueField, c);
	}

	public String getExpression () {
	    return ""+fieldBox.getSelectedItem () 
		+ " " + opBox.getSelectedItem () + " " 
		+ (table.getColumn (fieldBox.getSelectedIndex()).getType () 
		   == DbColumn.STRING 
		   ? ("'" + valueField.getText() + "'") 
		   : valueField.getText ());
	}

	public String toString () {
	    return getExpression ();
	}

	public Component getComponent () {
	    return panel;
	}
    }

    


    public SelectComponent (DbTable table, int options) {
	super (new BorderLayout ());

	this.table = table;
	fieldNames = new String[table.getColumnCount ()]; 
	for (int i = 0; i < fieldNames.length; i++) 
	    fieldNames [i] = table.getColumn (i).getName ();

	if ((options & CONDITION) != 0) {
	    conditionPane = new TreePane (new Node (null));
	}
	
	if ((options & (FIELDS | ORDER)) != 0) {
	    fieldPane = new JPanel (new BorderLayout ());

	    if ((options & FIELDS) != 0) {
		JPanel check = new JPanel (new GridLayout (0, 1));
		fieldBoxes = new JCheckBox [fieldNames.length];
		for (int i = 0; i < fieldNames.length; i++) {
		    fieldBoxes [i] = new JCheckBox (fieldNames [i], true);
		    check.add (fieldBoxes [i]);
		}
		JScrollPane sp = new JScrollPane (check);
		sp.setPreferredSize (new Dimension (300, 300));
		fieldPane.add (sp, BorderLayout.CENTER);
	    }
	    if ((options & ORDER) != 0) {
		Box box = new Box (BoxLayout.X_AXIS);
		box.add (new JLabel ("Order by: "));

		String [] fn = new String [fieldNames.length + 1];
		fn [0] = "(unordered)";
		System.arraycopy (fieldNames, 0, fn, 1, fieldNames.length);
		orderBox = new JComboBox (fn);
		box.add (orderBox);

		inverseBox = new JCheckBox ("inverse");
		box.add (inverseBox);

		fieldPane.add (box, BorderLayout.SOUTH);
		
	    }
	}
	

	if (conditionPane != null && fieldPane != null) {
	    JTabbedPane tabPane = new JTabbedPane ();
	    tabPane.add ("Condition", conditionPane);
	    tabPane.add ("Fields", fieldPane);
	    add (tabPane, BorderLayout.CENTER);
	}
	else if (conditionPane != null) {
	    add (conditionPane, BorderLayout.CENTER);
	}
	else if (fieldPane != null) {
	    add (fieldPane, BorderLayout.CENTER);
	}
    }



    public String getCondition () {
	return conditionPane == null 
	    ? null
	    : ((ConditionNode) (conditionPane.getRoot ())).getExpression ();
    }


    public int[] getFields () {
	if (fieldBoxes == null) return null;
	int cnt = 0;
	for (int i = 0; i < fieldBoxes.length; i++)
	    if (fieldBoxes [i].isSelected ()) cnt++;

	int [] result = new int [cnt];

	cnt = 0;
	for (int i = 0; i < fieldBoxes.length; i++)
	    if (fieldBoxes [i].isSelected ()) result[cnt++] = i;
	
	return result;
    }


    public boolean getInverse () {
	return inverseBox != null && inverseBox.isSelected ();
    }

    
    public int getOrder () {
	return orderBox == null ?
	    -1 : (orderBox.getSelectedIndex () -1);
    }


    public boolean showDialog (Component parent) {
	return JOptionPane.showOptionDialog 
	    (parent, this, "Please Choose", 
	     JOptionPane.OK_CANCEL_OPTION,
	     JOptionPane.PLAIN_MESSAGE,			  
	     null, null, null) == JOptionPane.OK_OPTION;
    }
}






