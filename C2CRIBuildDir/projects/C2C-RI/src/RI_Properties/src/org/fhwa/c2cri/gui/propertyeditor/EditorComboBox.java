package org.fhwa.c2cri.gui.propertyeditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 * The Class EditorComboBox.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class EditorComboBox extends JComboBox implements ItemListener {
    
    /** The p. */
    Parameter p;
    
    /** The j. */
    JLabel j;
    
    /**
     * Instantiates a new editor combo box.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param p the p
     * @param f the f
     */
    public EditorComboBox(ParameterSymbol p, Font f) {
        super(p.getAllowedValues());
        
        this.p = p;
        
        setBackground(Color.white);
        
        setFont(f);
        setBorder(BorderFactory.createEmptyBorder());
        
        setSelectedItem(p.toString());
        
        
        addItemListener(this);
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent e) {
        if(p.getValue().compareTo(getSelectedItem().toString()) != 0) {
            p.setValue(getSelectedItem().toString());
            PropEditor.eFile.setModified(true);
        }
    }
    
}