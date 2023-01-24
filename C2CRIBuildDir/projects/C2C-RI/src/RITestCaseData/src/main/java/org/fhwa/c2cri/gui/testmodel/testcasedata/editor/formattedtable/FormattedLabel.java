package org.fhwa.c2cri.gui.testmodel.testcasedata.editor.formattedtable;

import javax.swing.JLabel;

/**
 * The Class FormattedLabel.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class FormattedLabel extends JLabel {

	/**
	 * Instantiates a new formatted label.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param o the o
	 * @param columnWidth the column width
	 */
	public FormattedLabel(Object o, int columnWidth) {
		setText(o.toString());
	}
}