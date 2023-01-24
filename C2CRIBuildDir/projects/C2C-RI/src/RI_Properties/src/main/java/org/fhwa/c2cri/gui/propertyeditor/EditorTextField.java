package org.fhwa.c2cri.gui.propertyeditor;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class EditorTextField extends JTextField implements CaretListener {

	Parameter p;

	/**
	 * Instantiates a new editor text field.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param p the p
	 */
	public EditorTextField(Parameter p) {
		super(p.toString());

		this.p = p;

		setBorder(BorderFactory.createEmptyBorder(1, 2, 0, 2));
		addCaretListener(this);
	}

	public void caretUpdate(CaretEvent e) {
		if (p.getValue().compareTo(getText()) != 0) {
			p.setValue(getText());
			PropEditor.eFile.setModified(true);
		}
	}

}