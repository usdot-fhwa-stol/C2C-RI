/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.utilities;

/**
 * The Class ErrorMessage.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ErrorMessage {

    /**
     * Show error.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param message the message
     */
    public static void showError(String message){
        javax.swing.JOptionPane.showMessageDialog(null, message, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}
