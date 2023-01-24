package org.fhwa.c2cri.gui.propertyeditor;

import java.awt.Color;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.UIManager;

/**
 * The Class EditorLabel.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
class EditorLabel extends JLabel {


	/**
	 * Instantiates a new editor label.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param p the p
	 */
	public EditorLabel(ParameterFile p) {

		setText(p.toString());
		File f = new File(p.toString());
		if (p.toString().equals(""))
			setText("(edit)");
		else if (!f.exists()) {
			setForeground(Color.red);
			setText(getText() + " (invalid)");
		}

		if (p.getFType() == ParameterFile.FILE)
			setIcon(UIManager.getIcon("FileView.fileIcon"));
		else
			setIcon(UIManager.getIcon("FileView.directoryIcon"));

	}

	/* (non-Javadoc)
	 * @see javax.swing.JLabel#setText(java.lang.String)
	 */
	public void setText(String s) {
		if (s.length() > 40) {
			String stretchText =
				s.substring(0, 8) + " ... " + s.substring(s.length() - 8);
			super.setText(stretchText);
		} else
			super.setText(s);
	}

}