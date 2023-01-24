/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.infolayer;

import java.util.ArrayList;
import org.fhwa.c2cri.applayer.DialogResults;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.testmodel.verification.MessageResults;


/**
 * The Interface InformationLayerOperationResults.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public interface InformationLayerOperationResults {

        /**
         * Gets the message results.
         *
         * @param messageType the message type
         * @return the message results
         */

	   public MessageResults getMessageResults(Message.MESSAGETYPE messageType);

	   /**
         * Gets the total operation delay.
         *
         * @return the total operation delay
         */
        public long getTotalOperationDelay();
        
        /**
         * Checks if is valid tmdd operation.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @return true, if is valid tmdd operation
         */
        public boolean isValidTMDDOperation();
        
        /**
         * Gets the TMDD operation errors.
         *
         * @return the tMDD operation errors
         */
        public ArrayList<String> getTMDDOperationErrors();
        
        /**
         * Checks if is valid TMDD extension.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @return true, if is valid tmdd extension
         */
        public boolean isValidTMDDExtension();
        
        /**
         * Gets the TMDD extension errors.
         *
         * @return the TMDD extension errors
         */
        public ArrayList<String> getTMDDExtensionErrors();
        
        /**
         * Gets the dialog results.
         *
         * @param messageType the message type
         * @return the dialog results
         */
        public DialogResults getDialogResults(Message.MESSAGETYPE messageType);
        
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
         * Determines whether the operation contains the correct request and repsonse messages
         * as defined by TMDD, or is a dialog not defined by TMDD.  If it is not defined by TMDD it passes.
         */
        public void checkTMDDOperationResults() throws Exception;        
}
