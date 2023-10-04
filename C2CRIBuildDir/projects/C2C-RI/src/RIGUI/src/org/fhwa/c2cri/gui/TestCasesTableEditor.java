/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

/**
 *
 */
/* * IntegerEditor is used by TableFTFEditDemo.java. */ import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Component;
import java.awt.Toolkit;
import javax.swing.text.DefaultFormatterFactory;

/**
 * Implements a cell editor that uses a formatted text field to edit
 * Integer values.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestCasesTableEditor extends DefaultCellEditor {

    /** The ftf. */
    JFormattedTextField ftf;
    
    /** The debug. */
    private boolean DEBUG = false;

    /**
     * Instantiates a new test cases table editor.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public TestCasesTableEditor() {
        super(new JFormattedTextField());
        ftf = (JFormattedTextField) getComponent();
        //Set up the editor for the default cells.        
        ftf.setFormatterFactory(new DefaultFormatterFactory());
        ftf.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);

        //React when the user presses Enter while the editor is        
        //active.  (Tab is handled as specified by        
        //JFormattedTextField's focusLostBehavior property.)        
        ftf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
        ftf.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!ftf.isEditValid()) {
                    //The text is invalid.                    
                    if (userSaysRevert()) {
                        //reverted                
                        ftf.postActionEvent();
                        //inform the editor
                    }
                } else {
                    try {
                        //The text is valid,  
                        ftf.commitEdit();
                        //so use it.                   
                        ftf.postActionEvent();
                        //stop editing               
                    } catch (java.text.ParseException exc) {
                    }
                }
            }
        });
    }
    //Override to invoke setValue on the formatted text field.    

    /* (non-Javadoc)
     * @see javax.swing.DefaultCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JFormattedTextField ftf = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        ftf.setValue(value);
        return ftf;
    }
    //Override to ensure that the value remains an Integer.    

    /* (non-Javadoc)
     * @see javax.swing.DefaultCellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        JFormattedTextField ftf = (JFormattedTextField) getComponent();
        Object o = ftf.getValue();
        if (o instanceof Integer) {
            return o;
        } else if (o instanceof Number) {
            return new Integer(((Number) o).intValue());
        } else {
            if (DEBUG) {
                System.out.println("getCellEditorValue: o isn't a Number");
            }
//            try {
                return o.toString();
//            } catch (ParseException exc) {
//                System.err.println("getCellEditorValue: can't parse o: " + o);
//                return null;
//            }
        }
    }
    //Override to check whether the edit is valid,    
    //setting the value if it is and complaining if    
    //it isn't.  If it's OK for the editor to go    
    //away, we need to invoke the superclass's version    
    //of this method so that everything gets cleaned up.    

    /* (non-Javadoc)
     * @see javax.swing.DefaultCellEditor#stopCellEditing()
     */
    public boolean stopCellEditing() {
        JFormattedTextField ftf = (JFormattedTextField) getComponent();
        if (ftf.isEditValid()) {
            try {
                ftf.commitEdit();
            } catch (java.text.ParseException exc) {
            }
        } else {
            //text is invalid            
            if (!userSaysRevert()) {
                //user wants to edit            
                return false;
                //don't let the editor go away        
            }
        }
        return super.stopCellEditing();
    }

    /**
     * * Lets the user know that the text they entered is * bad. Returns true
     * if the user elects to revert to * the last good value. Otherwise, returns
     * false, * indicating that the user wants to continue editing.
     *
     * @return true, if successful
     */
    protected boolean userSaysRevert() {
        Toolkit.getDefaultToolkit().beep();
        ftf.selectAll();
        Object[] options = {"Edit", "Revert"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(ftf), "The value must be a valid existing file.\n" + "You can either continue editing " + "or revert to the last valid value.", "Invalid Text Entered", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[1]);
        if (answer == 1) {
            //Revert!            
            ftf.setValue(ftf.getValue());
            return true;
        }
        return false;
    }
}
