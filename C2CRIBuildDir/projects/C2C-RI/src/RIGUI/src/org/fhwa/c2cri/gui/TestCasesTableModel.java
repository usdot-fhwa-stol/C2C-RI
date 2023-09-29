/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.io.File;
import javax.swing.table.AbstractTableModel;
import org.fhwa.c2cri.gui.components.TestCaseCreationListener;
import org.fhwa.c2cri.gui.components.TestCaseEditJButton;
import org.fhwa.c2cri.gui.components.TestCaseViewJButton;
import org.fhwa.c2cri.testmodel.TestCase;
import org.fhwa.c2cri.testmodel.TestCases;

/**
 * The Class TestCasesTableModel.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestCasesTableModel extends AbstractTableModel implements TestCaseCreationListener {

    /** The Constant Title_Col. */
    public static final int Title_Col = 0;
    
    /** The Constant Source. */
    public static final int Source = 1;

    /** The Constant Source. */
    public static final int Edit = 2;

    /** The Constant Source. */
    public static final int View = 3;
    
    /** The test cases. */
    private TestCases testCases;
    
    /** The column names. */
    private String[] columnNames = {"Test Case",
        "Source","",""};

    /**
     * Instantiates a new test cases table model.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private TestCasesTableModel() {
    }

    /**
     * Instantiates a new test cases table model.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testCases the test cases
     */
    public TestCasesTableModel(TestCases testCases) {
        super();
        this.testCases = testCases;
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
        return testCases.testCases.size();
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
        final TestCase testCase = testCases.testCases.get(row);
        switch (col) {
            case Title_Col:
                return testCase.getName();
            case Source:
                if ((testCase.getCustomDataLocation() == null)
                        || (testCase.getCustomDataLocation().equals(""))) {
                    return "default";
                } else {
                    return testCase.getCustomDataLocation();
                }
            case Edit:
                final TestCaseEditJButton editButton = new TestCaseEditJButton(testCase, this, row);
                editButton.setEnabled(true);
                return editButton;
            case View:
                final TestCaseViewJButton viewButton = new TestCaseViewJButton(testCase);
                viewButton.setEnabled(true);
                return viewButton;
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
        if (col < 1) {
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
        if ((row > -1) && (col == Source)) {

            if ((new File((String) value).exists())) {
                testCases.testCases.get(row).setCustomDataLocation((String) value);
                System.out.println(" Firing Row " + row);
                fireTableCellUpdated(row, col);
            } else if (((String) value).equals("")) {
                testCases.testCases.get(row).setCustomDataLocation((String) value);
                fireTableCellUpdated(row, col);
            }
        }

    }

    @Override
    /**
     * Update 
     */
    public void testCaseCreatedUpdate(int row) {
        fireTableCellUpdated(row,Source);
    }    
}
