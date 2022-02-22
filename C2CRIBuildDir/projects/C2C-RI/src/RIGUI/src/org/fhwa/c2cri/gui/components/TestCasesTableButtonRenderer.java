/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.components;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * This class provides a renderer which will show JButtons as buttons in a
 * table.
 *
 * @author TransCore ITS, LLC
 */
public class TestCasesTableButtonRenderer implements TableCellRenderer {

    // The renderer currently associated with the table.
    private TableCellRenderer defaultRenderer;

    public TestCasesTableButtonRenderer(TableCellRenderer renderer) {
        defaultRenderer = renderer;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Component) {
            if (value instanceof JButton) {
                JButton button = (JButton) value;
                if (isSelected) {
                    button.setForeground(table.getSelectionForeground());
                    button.setBackground(table.getSelectionBackground());
                } else {
                    button.setForeground(table.getForeground());
                    button.setBackground(table.getBackground());
                }
            }
            return (Component) value;
        }
        return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

    }
}
