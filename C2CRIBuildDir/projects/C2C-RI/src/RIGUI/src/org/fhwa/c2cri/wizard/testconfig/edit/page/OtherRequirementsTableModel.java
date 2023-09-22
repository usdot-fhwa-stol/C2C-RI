/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.wizard.testconfig.edit.page;

import org.fhwa.c2cri.gui.*;
import java.util.ArrayList;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import org.fhwa.c2cri.testmodel.NRTM;
import org.fhwa.c2cri.testmodel.OtherRequirement;
import org.fhwa.c2cri.testmodel.OtherRequirements;
import org.fhwa.c2cri.testmodel.OtherRequirementsInterface;
import org.fhwa.c2cri.testmodel.Requirement;

/**
 * The Class OtherRequirementsTableModel.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class OtherRequirementsTableModel extends AbstractTableModel implements ListSelectionListener {

    /**
     * The Constant ReqID_Col.
     */
    public static final int ReqID_Col = 0;

    /**
     * The Constant Text_Col.
     */
    public static final int Text_Col = 1;

    /**
     * The Constant Value_Col.
     */
    public static final int Value_Col = 2;
//    private OtherRequirements otherRequirements;
    /**
     * The nrtm.
     */
    private NRTM nrtm;

    /**
     * The other requirements.
     */
    private ArrayList<OtherRequirement> otherRequirements = new ArrayList<OtherRequirement>();

    /**
     * The current need id.
     */
    private String currentNeedID;

    /**
     * The requirement list table.
     */
    private JTable requirementListTable;

    /**
     * The column names.
     */
    private String[] columnNames = {OtherRequirementsInterface.ReqID_Header,
        OtherRequirementsInterface.OtherRequirement_Header,
        OtherRequirementsInterface.Value_Header};

    /**
     * Instantiates a new other requirements table model.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    private OtherRequirementsTableModel() {
    }

    /**
     * Instantiates a new other requirements table model.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param nrtm the nrtm
     */
    public OtherRequirementsTableModel(NRTM nrtm, String selectedNeed) {
        super();
        this.nrtm = nrtm;
        this.currentNeedID = selectedNeed;
//        this.otherRequirements = otherRequirements;
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
//        System.out.println(" The number of rows was = " + otherRequirements.otherRequirements.size());
        return otherRequirements.size();
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
        OtherRequirement thisRequirement = otherRequirements.get(row);
        switch (col) {
            case ReqID_Col:
                return thisRequirement.getReqID();
            case Text_Col:
                return thisRequirement.getOtherRequirement();
            case Value_Col:
                return thisRequirement.getValue();
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
        if (col != 2) {
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
        if ((row > -1) && (col == Value_Col)) {
            otherRequirements.get(row).setValue((String) value);
            System.out.println(" Firing Row " + row);
            fireTableCellUpdated(row, col);
        }

    }

    /**
     * Sets the requirement list selection table.
     *
     * @param requirementTable the new requirement list selection table
     */
    public void setRequirementListSelectionTable(JTable requirementTable) {
        this.requirementListTable = requirementTable;
    }

    /* (non-Javadoc)
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {

        // Row selection changed
//            int row = e.getLastIndex();
        int row;

        if (!e.getValueIsAdjusting()) {

            // Requirements Table
            row = requirementListTable.getSelectedRow();
            if ((row >= 0) && (currentNeedID != null) && (!currentNeedID.isEmpty())) {
                Requirement thisRequirement = (Requirement) nrtm.getUserNeeds().getNeed(currentNeedID).getProjectRequirements().requirements.get(row);
                otherRequirements = new ArrayList(thisRequirement.getOtherRequirements().otherRequirements);
                this.fireTableDataChanged();
                System.out.println("OtherRequirementsTableModel2: Reqrow=" + row + " Selected Need " + currentNeedID + " and Requirement " + nrtm.getRequirementsList(currentNeedID).get(row) + " should show " + otherRequirements.size() + " otherRequirements.");

            } else {
                ArrayList<OtherRequirement> blankList = new ArrayList<OtherRequirement>();
                otherRequirements = blankList;
                this.fireTableDataChanged();
                System.out.println("OtherRequirementsTableModel2: Reqrow=" + row + " No Need Selected");
            }

        }
    }
}
