/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import org.fhwa.c2cri.testmodel.NRTM;
import org.fhwa.c2cri.testmodel.ProjectRequirementsInterface;
import org.fhwa.c2cri.testmodel.Requirement;

/**
 * The Class RequirementsTableModel.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class RequirementsTableModel extends AbstractTableModel implements ListSelectionListener {

    /** The Constant Title_Col. */
    public static final int Title_Col = 0;
    
    /** The Constant Text_Col. */
    public static final int Text_Col = 1;
    
    /** The Constant UN_Selected_Col. */
    public static final int UN_Selected_Col = 2;
    
    /** The Constant FlagVal_Col. */
    public static final int FlagVal_Col = 3;
//        private ProjectRequirements projectRequirements;
    /** The requirement list. */
private ArrayList<Requirement> requirementList = new ArrayList<Requirement>();
    
    /** The nrtm. */
    private NRTM nrtm;
    
    /** The need list table. */
    private JTable needListTable;
    
    /** The column names. */
    private String[] columnNames = {ProjectRequirementsInterface.title_Header,
        ProjectRequirementsInterface.text_Header,
        ProjectRequirementsInterface.type_Header,
        ProjectRequirementsInterface.flagValue_Header};

    /**
     * Instantiates a new requirements table model.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private RequirementsTableModel() {
    }

    /**
     * Instantiates a new requirements table model.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param nrtm the nrtm
     */
    public RequirementsTableModel(NRTM nrtm) {
        super();
        this.nrtm = nrtm;
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
        return requirementList.size();
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
        Requirement projectRequirement = requirementList.get(row);
        switch (col) {
            case Title_Col:
                return projectRequirement.getTitle();
            case Text_Col:
                return projectRequirement.getText();
            case UN_Selected_Col:
                return projectRequirement.getType();
            case FlagVal_Col:
                return projectRequirement.getFlagValue();
			default:
				throw new IllegalArgumentException("Illegal column: " + col);
        }
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
            requirementList.get(row).setFlagValue((Boolean) value);
            System.out.println(" Firing Row " + row);
            fireTableCellUpdated(row, col);
        }

    }

    /**
     * Sets the need list selection table.
     *
     * @param needTable the new need list selection table
     */
    public void setNeedListSelectionTable(JTable needTable) {
        this.needListTable = needTable;
    }


    /* (non-Javadoc)
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {

        // Row selection changed
//            int row = e.getLastIndex();
        int row = needListTable.getSelectedRow();
        if (row > -1) {
            if (!e.getValueIsAdjusting()) {
                row = needListTable.getSelectedRow();
                if (row >= 0) {
                    requirementList = nrtm.getNeedRelatedRequirements(nrtm.getUserNeeds().needs.get(row).getTitle());
                    System.out.println("RequirementsTableModel2: row=" + row + " Selected Need " + nrtm.getUserNeeds().needs.get(row).getTitle() + " should show " + requirementList.size() + " requirements.");
                    this.fireTableDataChanged();

                } else {
                    ArrayList<Requirement> blankList = new ArrayList<Requirement>();
                    requirementList = blankList;
                    this.fireTableDataChanged();
                    System.out.println("RequirementsTableModel2: row=" + row + " Blanking the list.");
                }
            }
        } else {
            ArrayList<Requirement> blankList = new ArrayList<Requirement>();
            requirementList = blankList;
            this.fireTableDataChanged();
            System.out.println("RequirementsTableModel2: row=" + row + " Blanking the list.");
        }
    }
}
