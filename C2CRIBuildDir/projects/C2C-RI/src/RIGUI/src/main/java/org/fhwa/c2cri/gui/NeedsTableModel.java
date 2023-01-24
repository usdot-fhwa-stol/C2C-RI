/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import javax.swing.table.AbstractTableModel;
import org.fhwa.c2cri.testmodel.NRTM;
import org.fhwa.c2cri.testmodel.Need;
import org.fhwa.c2cri.testmodel.UserNeeds;
import org.fhwa.c2cri.testmodel.UserNeedsInterface;

/**
 * The Class NeedsTableModel.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class NeedsTableModel extends AbstractTableModel {

    /** The Constant Title_Col. */
    public static final int Title_Col = 0;
    
    /** The Constant Text_Col. */
    public static final int Text_Col = 1;
    
    /** The Constant UN_Selected_Col. */
    public static final int UN_Selected_Col = 2;
    
    /** The Constant FlagVal_Col. */
    public static final int FlagVal_Col = 3;
    
    /** The nrtm. */
    private NRTM nrtm;
    
    /** The user needs. */
    private UserNeeds userNeeds;
    
    /** The column names. */
    private String[] columnNames = {UserNeedsInterface.title_Header,
        UserNeedsInterface.text_Header,
        UserNeedsInterface.type_Header,
        UserNeedsInterface.flagValue_Header};

    /**
     * Instantiates a new needs table model.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param nrtm the nrtm
     */
    public NeedsTableModel(NRTM nrtm) {
        super();
        try {
            this.nrtm = nrtm;
            this.userNeeds = nrtm.getUserNeeds();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * private String[] columnNames = {"First Name",
     * "Last Name",
     * "Sport",
     * "# of Years",
     * "Vegetarian","Mandatory/Optional"};.
     *
     * @return the column count
     */
    public int getColumnCount() {
        return columnNames.length;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
//        System.out.println(" The number of rows was = " + userNeeds.needs.size());
        return nrtm.getUserNeeds().needs.size();
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
        Need userNeed = nrtm.getUserNeeds().needs.get(row);
        switch (col) {
            case Title_Col:
                return userNeed.getTitle();
            case Text_Col:
                return userNeed.getText();
            case UN_Selected_Col:
                return userNeed.getType();
            case FlagVal_Col:
                return userNeed.getFlagValue();
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

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object value, int row, int col) {
        if ((row > -1) && (col == FlagVal_Col)) {
            nrtm.getUserNeeds().needs.get(row).setFlagValue((Boolean) value);
            System.out.println(" Firing Row " + row);
            fireTableCellUpdated(row, col);
        }

    }
}


