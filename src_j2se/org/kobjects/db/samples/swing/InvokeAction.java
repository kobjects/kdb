package org.kobjects.db.samples.swing;

import java.lang.reflect.*;
import java.awt.event.*;
import javax.swing.*;

public class InvokeAction extends AbstractAction {

    Object target;
    Method method;
    boolean parameter;

    public InvokeAction (String title, Object target, String methodName) {
	super (title);
	this.target = target;
	try {
	    this.method = target.getClass ().getMethod 
		(methodName, new Class[] {ActionEvent.class});
	    parameter = true;
	}
	catch (NoSuchMethodException e) {
	    try {
		this.method = target.getClass ().getMethod 
		    (methodName, new Class[0]); 
	    }
	    catch (NoSuchMethodException e2) {
		throw new RuntimeException 
		    ("found neither "+methodName 
		     + "(java.awt.event.ActionEvent) nor "+methodName
		     +"() in "+target.getClass ());
	    }
	}
    }


    public InvokeAction (String name, Object target) {
	this (name, target, name);
    }


    public void actionPerformed (ActionEvent ev) {
	try {
	    method.invoke (target, parameter 
			   ? new Object []{ev} 
			   : new Object [0]);
	}
	catch (Exception e) {
	    throw new RuntimeException (""+e);
	}
    }
}

