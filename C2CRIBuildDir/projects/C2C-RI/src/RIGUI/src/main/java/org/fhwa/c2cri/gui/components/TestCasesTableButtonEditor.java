/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author TransCore ITS, LLC
 */
public class TestCasesTableButtonEditor extends DefaultCellEditor {

    enum ButtonType {
        Edit, View, Other
    }
    protected JButton button;
    TestCaseEditJButton editButton;

    ButtonType cellButtonType = ButtonType.Other;

    private String label;

    private boolean isPushed;

    public TestCasesTableButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        cellButtonType = ButtonType.Other;
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        if (value instanceof TestCaseEditJButton) {
            cellButtonType = ButtonType.Edit;
            editButton = (TestCaseEditJButton) value;
            button = (JButton) value;

            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }

        } else {
            button = (JButton) value;
            cellButtonType = ButtonType.Other;

            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }

        }

        label = (value == null) ? "" : value.toString();

        isPushed = true;
        return button;
    }

    public Object getCellEditorValue() {
        if (isPushed) {
            // 
            // 
            if (cellButtonType.equals(ButtonType.Edit)) {
                System.out.println("Test Case Button View Event Pressed.");
                if ((editButton.getTestCase().getCustomDataLocation() == null)
                        || (editButton.getTestCase().getCustomDataLocation().equals(""))) {

                    JFileChooser j = new JFileChooser();                        
                    int response = j.showOpenDialog(editButton);                       
                }
                    
            } else {
                JOptionPane.showMessageDialog(button, label + ": Ouch!");
            }
            // System.out.println(label + ": Ouch!");
        }
        isPushed = false;
        return new String(label);
    }

    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
