package org.fhwa.c2cri.gui;

/*
 * SelectionFlagRenderer.java is used by
 * TableDialogEditDemo.java.
 */

import javax.swing.JTable;
import javax.swing.JCheckBox;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

/**
 * The Class SelectionFlagRenderer.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class SelectionFlagRenderer extends JCheckBox
                           implements TableCellRenderer {
    
    /** The mandatory column name. */
    String mandatoryColumnName;

    /**
     * Instantiates a new selection flag renderer.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param mandatoryColumnName the mandatory column name
     */
    public SelectionFlagRenderer(String mandatoryColumnName) {
        this.mandatoryColumnName = mandatoryColumnName;
        setOpaque(true); //MUST do this for background to show up.
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(
                            JTable table, Object flag,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {

        Boolean flagValue = false;
        int modelRow = table.convertRowIndexToModel(row);
        String typeSetting = (String) table.getModel().getValueAt(modelRow, table.getColumn(mandatoryColumnName).getModelIndex());
        if (typeSetting.equals("M") || typeSetting.equals("Mandatory")) {
            this.setSelected(true);
            this.setEnabled(false);
            flagValue = true;
            setToolTipText("Flag set by default");
        } else {
            this.setEnabled(true);
            flagValue = (Boolean) table.getModel().getValueAt(modelRow, column);
            this.setSelected(flagValue);
            setToolTipText("Select/Deselect Flag");
        }
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
        } else {
            this.setBackground(table.getBackground());
        }

        this.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

//        setToolTipText("Selected row #" + row + "  = : " + flagValue + " Model Row = " + table.convertRowIndexToModel(row));
        return this;
    }
}

