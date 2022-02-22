/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.applayer;

/**
 *  Eventually publication classes should register as MessageUpdateListener's in order
 *  to support on-change publications.  
 * 
 * @author TransCore ITS
 * Last Updated: 9/2/2012
 */
public interface MessageUpdateListener {
    
    /**
     * Message update.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param messageName the message name
     * @param nameSpace the name space
     * @param message the message
     */
    public void messageUpdate(String messageName, String nameSpace, byte[] message);
}
