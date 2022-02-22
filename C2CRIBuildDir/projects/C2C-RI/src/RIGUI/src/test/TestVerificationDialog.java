/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import org.fhwa.c2cri.gui.VerificationDialog;

/**
 * The Class TestVerificationDialog.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestVerificationDialog {
    
    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                VerificationDialog dialog = new VerificationDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVerificationInstruction("This is the verification Instruction...............................aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nThe End");
                dialog.setRawMessage("This is the Raw Message.........................................................bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nThe End");
                dialog.setMessageSpecification("This is the Message Specification.....................................ccccccccccccccccccccccccccccccccccc\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nThe End");
                dialog.setVisible(true);
                System.out.println("Test Result Was "+dialog.isPassed());
                System.out.println("The skip checkbox was set to "+dialog.isSkipRemainingUserVerifications());
                System.exit(0);
            }
        });
    }

}
