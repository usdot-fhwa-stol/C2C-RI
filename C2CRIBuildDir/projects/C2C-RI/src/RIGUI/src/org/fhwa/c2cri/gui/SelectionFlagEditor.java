/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * The Class SelectionFlagEditor.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class SelectionFlagEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    /** This is the component that will handle the editing of the cell value */
    JComponent component = new JCheckBox();
    
    /** The mandatory column name. */
    String mandatoryColumnName;
    
    /** The background color. */
    Color backgroundColor;
    
    /** The Constant EDIT. */
    protected static final String EDIT = "edit";
    
    /** The notify on clear. */
    boolean notifyOnClear = false;
    
    /** The flag listener. */
    transient SelectionFlagListener flagListener = null;
    
    /** The current editing row. */
    int currentEditingRow=-1;

    /**
     * Instantiates a new selection flag editor.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param mandatoryColumnName the mandatory column name
     */
    public SelectionFlagEditor(String mandatoryColumnName) {
        super();
        this.mandatoryColumnName = mandatoryColumnName;
        backgroundColor = Color.BLUE;
    }

    // This method is called when a cell value is edited by the user.
    /* (non-Javadoc)
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // 'value' is value contained in the cell located at (rowIndex, vColIndex)
        // cell (and perhaps other cells) are selected
        int modelRow = table.convertRowIndexToModel(row);
        String typeSetting = (String) table.getModel().getValueAt(modelRow, table.getColumn(mandatoryColumnName).getModelIndex());
        backgroundColor = table.getSelectionBackground();
        if (typeSetting.equals("M") || typeSetting.equals("Mandatory")) {
            component.setEnabled(false);
            ((JCheckBox) component).setSelected(true);
            component.setBackground(table.getSelectionBackground());
        } else {
            component.setBackground(Color.blue);
            component.setEnabled(true);
            ((JCheckBox) component).setSelected((Boolean) value);
            ((JCheckBox) component).addActionListener(this);
        }
        ((JCheckBox) component).setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ((JCheckBox) component).setToolTipText("Selected row #" + row + "  = : " + (Boolean) value + " Model Row = " + modelRow);

        currentEditingRow = modelRow;

        // Return the configured component
        return component;

    }

    // This method is called when editing is completed.
    // It must return the new value to be stored in the cell.
    /* (non-Javadoc)
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        component.setBackground(backgroundColor);
        if (flagListener != null) {
            if (!((JCheckBox) component).isSelected()) {
                flagListener.flagValueClearedUpdate(currentEditingRow);
            } else {
                flagListener.flagValueSetUpdate(currentEditingRow);
            }
        }

        return ((JCheckBox) component).isSelected();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        //As soon as the user clicks in the checkbox, stop the cell Editing.
        //The new toggled state of the checkbox gets saved when this call is made.
        stopCellEditing();
    }

    /**
     * Register selection flag listener.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param theListener the the listener
     */
    public void registerSelectionFlagListener(SelectionFlagListener theListener) {
        flagListener = theListener;
    }

    /**
     * Un register selection flag listener.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void unRegisterSelectionFlagListener() {
        flagListener = null;
    }
}
