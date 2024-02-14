/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import org.fhwa.c2cri.testmodel.TestCaseResults;
import org.fhwa.c2cri.testmodel.TestStepResult;
import org.fhwa.c2cri.utilities.DateUtils;


/**
 * The Class TestStepResultsTableModel.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestStepResultsTableModel extends AbstractTableModel implements ListSelectionListener {

    /** The Constant TimeStamp_Col. */
    public static final int TimeStamp_Col = 0;
    
    /** The Constant Description_Col. */
    public static final int Description_Col = 1;
    
    /** The Constant Result_Col. */
    public static final int Result_Col = 2;
    
    /** The tc results. */
    private transient TestCaseResults tcResults;
    
    /** The test case results table. */
    private JTable testCaseResultsTable;
    
    /** The test step list. */
    private transient ArrayList<TestStepResult> testStepList = new ArrayList<TestStepResult>();
    
    /**  The test case description Text Area Reference  */
    private JTextArea testDescriptionText;
    
    /** The column names. */
    private String[] columnNames = {"TimeStamp",
        "Description",
        "Result"};

    /**
     * Instantiates a new test step results table model.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private TestStepResultsTableModel() {
    }

    /**
     * Instantiates a new test step results table model.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param tcResults the tc results
     */
    public TestStepResultsTableModel(TestCaseResults tcResults) {
        super();
        this.tcResults = tcResults;
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
        if (testCaseResultsTable.getSelectedRow()>-1){

        } else {  // Not called based on a selection event.
           int row = testCaseResultsTable.getRowCount() - 1;
           if (row >= 0) {
               testStepList = tcResults.getFullResultsList().get(row).getTestStepResults();
               testDescriptionText.setText(tcResults.getFullResultsList().get(row).getTestCaseDescription());
           } else {
               ArrayList<TestStepResult> blankList = new ArrayList<TestStepResult>();
               testStepList = blankList;
               testDescriptionText.setText("");
           }

        }

        return testStepList.size();
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
        TestStepResult testStepResult = testStepList.get(row);
        switch (col) {
            case TimeStamp_Col:
                return DateUtils.millisecondToDate(testStepResult.getTimeStamp());
            case Description_Col:
                return testStepResult.getTestStepDescription();
            case Result_Col:
                return testStepResult.getResult().equals("FAILED")? testStepResult.getResult() + " - " + testStepResult.getErrorDescription():testStepResult.getResult();
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
    /**
     * public void setValueAt(Object value, int row, int col) {
     * if ((row > -1) && (col == FlagVal_Col)) {
     * requirementList.get(row).setFlagValue((Boolean) value);
     * System.out.println(" Firing Row " + row);
     * fireTableCellUpdated(row, col);
     * }
     * 
     * }
     *
     * @param resultsTable the new need list selection table
     */
    public void setNeedListSelectionTable(JTable resultsTable) {
        this.testCaseResultsTable = resultsTable;
    }

    /** set the reference to the Test Case Description Text Area */
    public void setTestDescriptionText(JTextArea testDescriptionText) {
        this.testDescriptionText = testDescriptionText;
    }
   
    /* (non-Javadoc)
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {

        // Row selection changed
//            int row = e.getLastIndex();
        System.out.println("***TestStepResultsTableModel valueChanged: Started");
        int row = testCaseResultsTable.getSelectedRow();
        if (row > -1) {
            if (!e.getValueIsAdjusting()) {
                row = testCaseResultsTable.getSelectedRow();
                System.out.println("***TestStepResultsTableModel valueChanged: Row " + row + " Selected");
                if (row >= 0) {
                    testStepList = tcResults.getFullResultsList().get(row).getTestStepResults();
                    testDescriptionText.setText(tcResults.getFullResultsList().get(row).getTestCaseDescription());
                    this.fireTableDataChanged();
                } else {
                    ArrayList<TestStepResult> blankList = new ArrayList<TestStepResult>();
                    testStepList = blankList;
                    testDescriptionText.setText("");
                    this.fireTableDataChanged();
                }
            }
        }
    }
}
