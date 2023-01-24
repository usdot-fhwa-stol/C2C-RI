package org.fhwa.c2cri.gui.testmodel.testcasedata.editor;

import javax.swing.table.DefaultTableModel;

/**
 * The Class EditorTableModel.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class EditorTableModel extends DefaultTableModel {
    
    /** The column names. */
    static String[] columnNames = {"Attribute Name", "Attribute Value"};
    
    /**
     * Instantiates a new editor table model.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public EditorTableModel(){
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
