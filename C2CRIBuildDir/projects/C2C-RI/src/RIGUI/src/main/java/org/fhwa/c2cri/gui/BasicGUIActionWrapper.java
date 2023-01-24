/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import javax.swing.SwingWorker;
import org.fhwa.c2cri.utilities.ProgressMonitor;

/**
 * BasicGUIActionWrapper is used to bundle actions that are triggered by
 * user interface events that might take some time to process.
 * It implements the functions of the SwingWorker, but also adds the use
 * of a ProgressMonitor object to display progress to the user when an action
 * takes more than a pre-defined amount of time (specified within ProgressMonitor to
 * complete.  Users of this class will typically override the actionMethod and
 * wrapUp methods.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class BasicGUIActionWrapper extends SwingWorker<Boolean, Void> {

    /** The progress ui. */
    ProgressUI theProgressUI;
    
    /** The monitor. */
    ProgressMonitor monitor;
    
    /** The action name. */
    String theActionName;
    
    /** The parent frame. */
    javax.swing.JFrame parentFrame;
    
    /** The timeoutinsecs. */
    private static int TIMEOUTINSECS = 2;

    /**
     *  Constructor for the BasicGUIActionWrapper.
     * @param parentFrame - the parent JFrame that this progressUI will be associated with.
     * @param actionName - the name provided for this action.
     */
    public BasicGUIActionWrapper(javax.swing.JFrame parentFrame, String actionName) {
//        theProgressUI = new ProgressUI(parentFrame, true, actionName);
        monitor = ProgressMonitor.getInstance(
                parentFrame,
                "C2C RI",
                actionName,
                ProgressMonitor.Options.MODAL,
                ProgressMonitor.Options.CENTER,
                ProgressMonitor.Options.SHOW_STATUS,
                ProgressMonitor.Options.SHOW_PERCENT_COMPLETE);


        this.theActionName = actionName;
        this.parentFrame = parentFrame;
    }

    /* (non-Javadoc)
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected final Boolean doInBackground() throws Exception {
//        theProgressUI.start();
        monitor.showAfterDelay(TIMEOUTINSECS);
        return actionMethod();
    }

    /* (non-Javadoc)
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    protected final void done() {
//        theProgressUI.done();
        try {
            monitor.dispose();
            wrapUp(this.get());
        } catch (Exception ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(parentFrame, "An error was encountered trying to complete the " + this.theActionName + " action.\n"+ex.getMessage());
        }
    }

    /**
     * This method is usually overridden by users of this class, and contains the actions
     * that are to be performed in response to a user interface event.
     *
     * @return - indicator of whether the actionMethod completed successfully.
     * @throws Exception the exception
     */
    protected Boolean actionMethod() throws Exception {
        return true;
    }

    /**
     * This method sometimes overridden by users of this class, and contains close out steps for the Progress UI monitor.
     * @param result - indicates whether the action completed successfully.
     */
    protected void wrapUp(Boolean result) {
        if (!result) {
            javax.swing.JOptionPane.showMessageDialog(parentFrame, "An error was encountered trying to complete the " + this.theActionName + " action.");
        }
    }
}
