/*
 * Created on Jun 18, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.fhwa.c2cri.gui.testmodel.testcasedata.editor.formattedtable;

import javax.swing.JComponent;
import javax.swing.JTable;

/**
 * @author David_211245
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface FormattedTableInterface {

	public JComponent createComponent(
		JTable table,
		Object value,
		boolean isSelected,
		boolean hasFocus,
		int row,
		int column);

}
