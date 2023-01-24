package org.fhwa.c2cri.gui.propertyeditor;


import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import org.fhwa.c2cri.gui.propertyeditor.formattedtable.FormattedTable;

/**
 * The Class EditorTable.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class EditorTable extends FormattedTable {

	/**
	 * Instantiates a new editor table.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param tm the tm
	 */
	public EditorTable(TableModel tm) {
		super(tm);
		setColumnRenderer(1, new EditorTableCellRenderer());
		setColumnEditor(1, new EditorTableCellEditor());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(false);
	}
}
