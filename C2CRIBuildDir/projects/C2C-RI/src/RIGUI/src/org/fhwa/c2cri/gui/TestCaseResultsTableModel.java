/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import javax.swing.table.AbstractTableModel;
import org.fhwa.c2cri.testmodel.TestCaseResult;
import org.fhwa.c2cri.testmodel.TestCaseResults;
import org.fhwa.c2cri.utilities.DateUtils;

/**
 * The Class TestCaseResultsTableModel.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestCaseResultsTableModel extends AbstractTableModel {

    /** The Constant Number_Col. */
    public static final int Number_Col = 0;
    
    /** The Constant TestCaseID_Col. */
    public static final int TestCaseID_Col = 1;
    
    /** The Constant TestCaseStatus_Col. */
    public static final int TestCaseStatus_Col = 2;
    
    /** The Constant TestCaseRun_Col. */
    public static final int TestCaseRun_Col = 3;
    
    /** The Constant TestCaseRunTotal_Col. */
    public static final int TestCaseRunTotal_Col = 4;
    
    /** The Constant TestCaseFailTotal_Col. */
    public static final int TestCaseFailTotal_Col = 5;
    
    /** The Constant TestCaseExecutionTime_Col. */
    public static final int TestCaseExecutionTime_Col = 6;
    
    /** The tc results. */
    private TestCaseResults tcResults;

    /** The column names. */
    private String[] columnNames = {"#",
        "TestCaseID",
        "Status",
        "Run ID",
        "Run Counts",
        "Fail Counts",
        "Execution Time"};

    /**
     * Instantiates a new test case results table model.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param tcResults the tc results
     */
    public TestCaseResultsTableModel(TestCaseResults tcResults) {
        super();
        try {
            this.tcResults = tcResults;
        } catch (Exception e) {
            e.printStackTrace();
        }
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
//        System.out.println(" The number of rows was = " + userNeeds.needs.size());
        return tcResults.getFullResultsList().size();
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
        TestCaseResult tcResult = tcResults.getFullResultsList().get(row);

        switch (col) {
            case Number_Col:
                return row+1;
            case TestCaseID_Col:
                return tcResult.getTestCaseID();
            case TestCaseStatus_Col:
                return tcResult.getResult().equals("FAILED")?tcResult.getResult()+" - "+ tcResult.getErrorDescription():tcResult.getResult();
            case TestCaseRun_Col:
                return tcResult.getTestCaseRunID();
            case TestCaseRunTotal_Col:
                return tcResults.getRunCount(tcResult.getTestCaseID());
            case TestCaseFailTotal_Col:
                return tcResults.getFailCount(tcResult.getTestCaseID());
            case TestCaseExecutionTime_Col:
                if (tcResult.getEndTime() != null){
                    return DateUtils.executionTimeToString(tcResult.getEndTime()-tcResult.getStartTime());
                } else {
                   return " ";
                }
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
            return false;
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
/**
    public void setValueAt(Object value, int row, int col) {
        if (row > -1) {

        switch (col) {
            case Number_Col:
                return row+1;
            case TestCaseID_Col:
                return tcResult.getTestCaseID();
            case TestCaseStatus_Col:
                return tcResult.getResult();
            case TestCaseRun_Col:
                return 1;
            case TestCaseRunTotal_Col:
                return 55;
            case TestCaseFailTotal_Col:
                return 5;
            case TestCaseExecutionTime_Col:
                return 0;
        }
        throw new IllegalArgumentException("Illegal column: "
                + col);

            System.out.println(" Firing Row " + row);
            fireTableCellUpdated(row, col);
        }

    }
 */
}


