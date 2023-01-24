/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.enterprisepower.net.portforward;


/**
 * The listener interface for receiving socketAssignment events.
 * The class that is interested in processing a socketAssignment
 * event implements this interface. When
 * the socketAssignment event occurs, that object's appropriate
 * method is invoked.
 *
 *
 * @author TransCore ITS, LLC
 * Last Updated 10/2/2012
 */
public interface SocketAssignmentListener {
    
    /**
     * Adds the internal address mapping.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param internalAddress the internal address
     * @param externalAddress the external address
     */
    public void addInternalAddressMapping(String internalAddress, String externalAddress);
}
