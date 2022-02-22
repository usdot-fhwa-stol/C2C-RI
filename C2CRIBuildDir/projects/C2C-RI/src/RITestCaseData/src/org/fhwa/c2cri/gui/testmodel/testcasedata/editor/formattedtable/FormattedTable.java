package org.fhwa.c2cri.gui.testmodel.testcasedata.editor.formattedtable;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * The Class FormattedTable.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class FormattedTable extends JTable {

	/**
	 * Instantiates a new formatted table.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param tm the tm
	 */
	public FormattedTable(TableModel tm) {
		super(tm);
		setRowHeight(22);
	}

	/**
	 * Sets the column renderer.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param columnIndex the column index
	 * @param renderer the renderer
	 */
	public void setColumnRenderer(
		int columnIndex,
		TableCellRenderer renderer) {
		if (columnIndex < 0 || columnIndex > getColumnCount())
			return;

		TableColumn column = getColumnModel().getColumn(columnIndex);
		column.setCellRenderer(renderer);
	}

	/**
	 * Sets the column editor.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param columnIndex the column index
	 * @param editor the editor
	 */
	public void setColumnEditor(int columnIndex, TableCellEditor editor) {
		if (columnIndex < 0 || columnIndex > getColumnCount())
			return;

		TableColumn column = getColumnModel().getColumn(columnIndex);
		column.setCellEditor(editor);
	}
}
