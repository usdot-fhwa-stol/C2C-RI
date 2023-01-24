package org.fhwa.c2cri.gui.propertyeditor;

import java.awt.Insets;
import javax.swing.JButton;

/**
 * The Class EditorButton.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class EditorButton extends JButton {
        
    /**
     * Instantiates a new editor button.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param s the s
     */
    public EditorButton(String s){
        super(s);
        setMargin(new Insets(2, 2, 2, 2));
    }
    
  
}



