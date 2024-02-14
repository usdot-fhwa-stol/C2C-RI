/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

/**
 *
 * @author pearsonb
 */
import javax.swing.table.AbstractTableModel;
import org.fhwa.c2cri.centermodel.RIEmulationEntityValueSet;
import org.fhwa.c2cri.centermodel.RIEmulationParameters;
import org.fhwa.c2cri.testmodel.TestCases;

public class EmulationParametersTableModel extends AbstractTableModel {

    /**
     * The Constant Title_Col.
     */
    public static final int Title_Col = 0;

    /**
     * The Constant Source.
     */
    public static final int Source = 1;

    /**
     * The Constant Source.
     */
    public static final int Data = 2;

    /**
     * The test cases.
     */
    private TestCases testCases;

    /**
     * The column names.
     */
    private String[] columnNames = {"Entity Data Name",
        "Source"};

    private RIEmulationParameters emulationParam = new RIEmulationParameters();

    public EmulationParametersTableModel() {
    }

    /**
     * Instantiates a new test cases table model.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param emulationParameters
     * @param testCases the test cases
     */
    public EmulationParametersTableModel(RIEmulationParameters emulationParameters) {
        //super();

        emulationParam = emulationParameters;
        //System.out.println("The EmuParam is: " + emulationParameters.getCommandQueueLength());

    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        //System.out.println(" The number of rows was = "+projectRequirements.requirements.size());
        return emulationParam.getEntityDataMap().size();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
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
    @Override
    public Class getColumnClass(int c) {
        if (emulationParam.getEntityDataMap().size()>0) {
            return getValueAt(0, c).getClass();
        } else return String.class;
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
 /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col != 1) {
            return false;
        } else {
            return true;
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int row, int col) {

        if (emulationParam.getEntityDataMap().size() > 0) {
            switch (col) {
                case Title_Col:
                    return emulationParam.getEntityDataMap().get(row).getValueSetName();
                case Source:
                    if ((emulationParam.getEntityDataMap().get(row).getDataSetSource() == null) || (emulationParam.getEntityDataMap().get(row).getDataSetSource().equals(""))) {
                        return "*Default*";
                    } else {
                        return emulationParam.getEntityDataMap().get(row).getDataSetSource();
                    }
                case Data:
                    return emulationParam.getEntityDataMap().get(row).getEntityDataSet();
				default:
					throw new IllegalArgumentException("Illegal column: " + col);
            }
        } else {
            return null;
        }
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
 /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        if ((row > -1) && (col == Source)) {

            emulationParam.getEntityDataMap().get(row).setDataSetSource(RIEmulationEntityValueSet.ENTITYDATASTATE.valueOf((String) value));
//            if ((new File((String) value).exists())) 
//            {
//                testCases.testCases.get(row).setCustomDataLocation((String) value);
//                System.out.println(" Firing Row " + row);
//                fireTableCellUpdated(row, col);
//            } 
//            else if (((String) value).equals("")) 
//            {
//                testCases.testCases.get(row).setCustomDataLocation((String) value);
//                fireTableCellUpdated(row, col);
//            }
        } else if ((row > -1) && (col == Data)) {
            emulationParam.getEntityDataMap().get(row).setEntityDataSet((byte[]) value);
        }

    }

}
