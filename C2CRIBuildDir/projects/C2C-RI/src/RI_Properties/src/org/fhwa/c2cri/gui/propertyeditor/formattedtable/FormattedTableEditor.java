package org.fhwa.c2cri.gui.propertyeditor.formattedtable;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * The Class FormattedTableEditor.
 *
 * @author Unknown
 * Last Updated:  1/8/2012
 */
public abstract class FormattedTableEditor extends AbstractCellEditor implements TableCellEditor, FormattedTableInterface {
    
    /** The cell value. */
    private Object cellValue;
    
    /** The o. */
    private JComponent o = null;
    
    /* (non-Javadoc)
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        cellValue = value;
        o = createComponent(table, value, isSelected, true, row, column);
        //hasFocus is true because the cell is being edited so it must have the focus.
        return o;
    }
    
	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		return cellValue;
	}
    
}
