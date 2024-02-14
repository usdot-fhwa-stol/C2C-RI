
package org.fhwa.c2cri.plugin.c2cri.event;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The Class VerificationEventHandler notifies registered listeners when a Verification Event has occurred.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/2/2012
 */
public class VerificationEventHandler{

    /** The event handler. */
    private static VerificationEventHandler eventHandler;
    
    /** The verification listeners. */
    private final List verificationListeners = Collections.synchronizedList(new LinkedList());

    /**
     * Instantiates a new verification event handler.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private VerificationEventHandler(){}

    /**
     * Gets the single instance of VerificationEventHandler.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return single instance of VerificationEventHandler
     */
    public static VerificationEventHandler getInstance(){
        if (eventHandler == null) {
            eventHandler = new VerificationEventHandler();
        }
        return eventHandler;
    }

    /**
     * Clear instance.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public static void clearInstance(){
        eventHandler = null;
    }

    /**
     * Adds the verification listener.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param vl the vl
     */
    public void addVerificationListener(VerificationListener vl){
        if (vl != null && !verificationListeners.contains(vl)){
            verificationListeners.add(vl);
        }
    }

    /**
     * Gets the verification listeners.
     *
     * @return the verification listeners
     */
    public List getVerificationListeners(){
        return verificationListeners;
    }

    /**
     * Removes the verification listener.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param vl the vl
     */
    public void removeVerificationListener(VerificationListener vl){
        verificationListeners.remove(vl);
    }

    /**
     * Verification update.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param vu the vu
     */
    public void verificationUpdate(StringBuffer vu){
        VerificationEvent ve = new VerificationEvent(vu);
        synchronized(verificationListeners){
            Iterator it = verificationListeners.iterator();
            VerificationListener vl;
            while (it.hasNext()) {
                vl = (VerificationListener)it.next();
                vl.verificationUpdate(ve);
            }
        }
    }


}
