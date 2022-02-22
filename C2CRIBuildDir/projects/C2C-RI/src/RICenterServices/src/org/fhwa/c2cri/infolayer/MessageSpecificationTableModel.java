/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.infolayer;

import javax.swing.table.AbstractTableModel;

/**
 * The Class MessageSpecificationTableModel provides the set of message specifications to a table.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class MessageSpecificationTableModel extends AbstractTableModel {

    /** The Constant ValueElementName_Col. */
    public static final int ValueElementName_Col = 0;
    
    /** The Constant Value_Col. */
    public static final int Value_Col = 1;
    
    /** The spec. */
    private MessageSpecification spec;
    /** The column names. */
private String[] columnNames = {"Value Element Name",
        "Value"};

    /**
     * Instantiates a new message specification table model.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private MessageSpecificationTableModel() {
    }

    /**
     * Instantiates a new message specification table model.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param specification the specification
     */
    public MessageSpecificationTableModel(MessageSpecification specification) {
        super();
        this.spec = specification;
//            this.projectRequirements = projectRequirements;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return columnNames.length;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
//            System.out.println(" The number of rows was = "+projectRequirements.requirements.size());
        return spec.getMessageSpecItems().size();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(int col) {
        return columnNames[col];
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col) {
        switch (col) {
            case ValueElementName_Col:
                return spec.getMessageSpecItems().get(row).getValueName();
            case Value_Col:
                return spec.getMessageSpecItems().get(row).getValue();
        }
        throw new IllegalArgumentException("Illegal column: "
                + col);
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col != 3) {
            return false;
        } else {
            return true;
        }
    }




}
