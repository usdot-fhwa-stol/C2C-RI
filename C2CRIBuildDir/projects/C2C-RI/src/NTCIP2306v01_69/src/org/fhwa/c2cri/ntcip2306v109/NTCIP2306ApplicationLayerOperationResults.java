/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109;

import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import org.fhwa.c2cri.applayer.ApplicationLayerOperationResults;
import org.fhwa.c2cri.messagemanager.Message;

/**
 * This class represents the NTCIP 2306 application layer operation results that
 * are provided as an ApplicationLayerOperationResults object.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class NTCIP2306ApplicationLayerOperationResults implements ApplicationLayerOperationResults{
    
    /** The results. */
    private NTCIP2306ControllerResults results;
    
    /**
     * Constructor.
     *
     * @param results the results
     */
    public NTCIP2306ApplicationLayerOperationResults(NTCIP2306ControllerResults results){
        this.results = results;
    }
    
    /**
     * Checks if is subscription active.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return whether the subscription is active.
     */
    @Override
    public boolean isSubscriptionActive() {
        return results.isSubscriptionActive();
    }

    /**
     * Checks if is publication complete.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return whether the publication has completed.
     */
    @Override
    public boolean isPublicationComplete() {
        return results.isPublicationComplete();
    }

    /**
     * Gets the publication count.
     *
     * @return the number of publications that have occurred.
     */
    @Override
    public long getPublicationCount() {
        return results.getPublicationCount();
    }

    /**
     * Gets the millis since last periodic publication.
     *
     * @return the time in milliseconds since the previous publication.
     */
    @Override
    public long getMillisSinceLastPeriodicPublication(){
        return results.getMillisSinceLastPeriodicPublication();
    }
    
    /**
     * Gets the subscription periodic frequency.
     *
     * @return the publication frequency requested in the subscription.
     */
    @Override
    public long getSubscriptionPeriodicFrequency() {
        return results.getSubscriptionPeriodicFrequency();
    }
    
    /**
     * Gets the request message.
     *
     * @return the request message received in the dialog.
     */
    @Override
    public Message getRequestMessage() {
        String dialogName="Unknown";
        if (results!=null){
            dialogName = results.getOperationName();
        }
        return C2CRIMessageAdapter.toC2CRIMessage(dialogName, results != null ? results.getRequestMessage() : null);
    }

    /**
     * Gets the response message.
     *
     * @return the response message received in the dialog.
     */
    @Override
    public Message getResponseMessage() {
        String dialogName="Unknown";
        if (results!=null){
            dialogName = results.getOperationName();
        }
        return C2CRIMessageAdapter.toC2CRIMessage(dialogName, results != null ? results.getResponseMessage() : null);
    }

    /**
     * Gets the operation details.
     *
     * @return the operation details.
     */
    @Override
    public String getOperationDetails() {
        return results.toString();
    }


    /**
     * Checks if is transport error encountered.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return whether a transport error was encountered during the dialog.
     */
    @Override
    public boolean isTransportErrorEncountered() {
        if (!results.isTransportError()){
            return false;
        }else if (results.getResponseMessage()==null){
            return true;
        }
        return true;
    }

    /**
     * Gets the transport error description.
     *
     * @return the description for any transport errors.
     */
    @Override
    public String getTransportErrorDescription(){
        return results.getTransportErrorDescription();
    }
}
