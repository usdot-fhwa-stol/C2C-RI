package org.fhwa.c2cri.gui.propertyeditor.formattedtable;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * The Class FormattedTextField.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public abstract class FormattedTextField extends JTextField implements
		CaretListener, FormattedTextInterface {

	/** The o. */
	transient Object o;

	/**
	 * Instantiates a new formatted text field.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param o the o
	 */
	public FormattedTextField(Object o) {
		super(o.toString());
		this.o = o;
		setBorder(BorderFactory.createEmptyBorder(1, 2, 0, 2));
		addCaretListener(this);
	}

	/**
	 * Gets the object.
	 *
	 * @return the object
	 */
	public Object getObject() {
		return o;
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.JTextComponent#setText(java.lang.String)
	 */
	public void setText(String text) {
		super.setText(text.trim());
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
	 */
	public void caretUpdate(CaretEvent e) {
		if (o.toString().compareTo(getText()) != 0)
			caretUpdate(e, this);
	}

}

interface FormattedTextInterface {
	public void caretUpdate(CaretEvent e, FormattedTextField textField);
}