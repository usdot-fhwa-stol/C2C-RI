/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.applayer;

import org.fhwa.c2cri.messagemanager.Message;


/**
 * Represents the ApplicationLayerOperationResults.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public interface ApplicationLayerOperationResults {

    /**
     * Checks if is subscription active.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is subscription active
     */
    public boolean isSubscriptionActive();

    /**
     * Checks if is publication complete.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is publication complete
     */
    public boolean isPublicationComplete();
 
    /**
     * Gets the publication count.
     *
     * @return the publication count
     */
    public long getPublicationCount();
    
    /**
     * Gets the millis since last periodic publication.
     *
     * @return the millis since last periodic publication
     */
    public long getMillisSinceLastPeriodicPublication();

    /**
     * Gets the subscription periodic frequency.
     *
     * @return the subscription periodic frequency
     */
    public long getSubscriptionPeriodicFrequency();
    
    /**
     * Gets the request message.
     *
     * @return the request message
     */
    public Message getRequestMessage();
    
    /**
     * Gets the response message.
     *
     * @return the response message
     */
    public Message getResponseMessage();
    
    /**
     * Gets the operation details.
     *
     * @return the operation details
     */
    public String getOperationDetails();
    
    /**
     * Checks if is transport error encountered.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is transport error encountered
     */
    public boolean isTransportErrorEncountered();
    
    /**
     * Gets the transport error description.
     *
     * @return the transport error description
     */
    public String getTransportErrorDescription();
}
