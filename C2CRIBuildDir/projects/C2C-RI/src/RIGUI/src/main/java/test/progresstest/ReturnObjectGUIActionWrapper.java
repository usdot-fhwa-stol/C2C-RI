/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.progresstest;

import org.fhwa.c2cri.gui.*;
import javax.swing.SwingWorker;

/**
 * The Class ReturnObjectGUIActionWrapper.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ReturnObjectGUIActionWrapper extends SwingWorker<Object, Void> {

    /** The progress ui. */
    ProgressUI theProgressUI;
    
    /** The action name. */
    String theActionName;
    
    /** The parent frame. */
    javax.swing.JFrame parentFrame;

    /**
     * Instantiates a new return object gui action wrapper.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param parentFrame the parent frame
     * @param actionName the action name
     */
    public ReturnObjectGUIActionWrapper(javax.swing.JFrame parentFrame, String actionName) {
        theProgressUI = new ProgressUI(parentFrame, true, actionName);
        this.theActionName = actionName;
        this.parentFrame = parentFrame;
    }

    /* (non-Javadoc)
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected final Object doInBackground() throws Exception {
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
     * @return the object
     * @throws Exception the exception
     */
    protected Object actionMethod() throws Exception {
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
    protected void wrapUp(Object result) {
    }
}
