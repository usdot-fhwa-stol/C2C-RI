package org.fhwa.c2cri.gui.propertyeditor.formattedtable;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * The Class FormattedTableRenderer.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public abstract class FormattedTableRenderer
	implements TableCellRenderer, FormattedTableInterface {

	/** The o. */
	private JComponent o = null;

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(
		JTable table,
		Object value,
		boolean isSelected,
		boolean hasFocus,
		int row,
		int column) {

		o = createComponent(table, value, isSelected, hasFocus, row, column);

		if (isSelected && o != null) {
			o.setBackground(table.getSelectionBackground());
			o.setForeground(table.getSelectionForeground());
		}

		return o;
	}

}
