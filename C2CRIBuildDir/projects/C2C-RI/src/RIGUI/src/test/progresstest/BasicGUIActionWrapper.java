/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.progresstest;

import org.fhwa.c2cri.gui.*;
import javax.swing.SwingWorker;

/**
 * The Class BasicGUIActionWrapper.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class BasicGUIActionWrapper extends SwingWorker<Boolean, Void> {

    /** The progress ui. */
    ProgressUI theProgressUI;
    
    /** The action name. */
    String theActionName;
    
    /** The parent frame. */
    javax.swing.JFrame parentFrame;

    /**
     * Instantiates a new basic gui action wrapper.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param parentFrame the parent frame
     * @param actionName the action name
     */
    public BasicGUIActionWrapper(javax.swing.JFrame parentFrame, String actionName) {
        theProgressUI = new ProgressUI(parentFrame, true, actionName);
        this.theActionName = actionName;
        this.parentFrame = parentFrame;
    }

    /* (non-Javadoc)
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected final Boolean doInBackground() throws Exception {
        theProgressUI.start();
        return actionMethod();
    }

    /* (non-Javadoc)
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    protected final void done() {
        theProgressUI.done();
        try {
            wrapUp(this.get());
        } catch (Exception ex) {
			if (ex instanceof InterruptedException)
				Thread.currentThread().interrupt();
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(parentFrame, "An error was encountered trying to complete the " + this.theActionName + " action.\n"+ex.getMessage());
        }
    }

    /**
     * Action method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the boolean
     * @throws Exception the exception
     */
    protected Boolean actionMethod() throws Exception {
        return true;
    }

    /**
     * Wrap up.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param result the result
     */
    protected void wrapUp(Boolean result) {
        if (!result) {
            javax.swing.JOptionPane.showMessageDialog(parentFrame, "An error was encountered trying to complete the " + this.theActionName + " action.");
        }
    }
}
