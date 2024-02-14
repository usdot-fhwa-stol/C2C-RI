/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.messaging;

/**
 * The Class BinarySemaphore is used to establish locks for communicating threads.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class BinarySemaphore {
    
    /** The locked. */
    private boolean locked = false;

    /**
     * Instantiates a new binary semaphore.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param initial the initial
     */
    BinarySemaphore(int initial) {
        locked = (initial == 0);
    }

    /**
     * Wait for notify.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @throws InterruptedException the interrupted exception
     */
    public synchronized void waitForNotify() throws InterruptedException{
        while (locked) {
            wait();
        }
        locked = true;
    }

    /**
     * Wait for notify with timeout.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param timeoutInMillis the timeout in millis
     * @return true, if successful
     * @throws InterruptedException the interrupted exception
     */
    public synchronized boolean waitForNotifyWithTimeout(int timeoutInMillis) throws InterruptedException{
        while (locked) {
            wait(timeoutInMillis);
        }
        if (!locked) locked = true;
        return locked;
    }


    /**
     * Notify to wakeup.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public synchronized void notifyToWakeup(){
        if (locked) {
            notifyAll();
        }
        locked = false;
    }
}
