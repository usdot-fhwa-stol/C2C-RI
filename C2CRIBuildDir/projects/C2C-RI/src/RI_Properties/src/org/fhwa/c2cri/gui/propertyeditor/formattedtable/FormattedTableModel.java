package org.fhwa.c2cri.gui.propertyeditor.formattedtable;

import javax.swing.table.DefaultTableModel;

/**
 * The Class FormattedTableModel.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class FormattedTableModel extends DefaultTableModel {
    
    /** The column names. */
    static String[] columnNames = {"Attribute Name", "Attribute Value"};
    
    /**
     * Instantiates a new formatted table model.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public FormattedTableModel(){
        super(columnNames, 0);
    }
   
    /* (non-Javadoc)
     * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int row, int col) {
        if (col < 1) return false;
        else return true;
        
    }
}
