/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.applayer;

import java.util.HashMap;
import org.fhwa.c2cri.messagemanager.Message;

/**
 * The InformationLayerAdapter specifies an interface that application layer standards must implement to support
 * information layer standards.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public interface InformationLayerAdapter {
    
    /**
     * Perform get as an EC.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @return the application layer operation results
     * @throws Exception the exception
     */
    public ApplicationLayerOperationResults performGetEC(String dialog) throws Exception;
    
    /**
     * Perform get as an oc.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param responseMessage the response message
     * @return the application layer operation results
     * @throws Exception the exception
     */
    public ApplicationLayerOperationResults performGetOC(String dialog, Message responseMessage) throws Exception;

    /**
     * Perform request response as an ec.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param requestMessage the request message
     * @return the application layer operation results
     * @throws Exception the exception
     */
    public ApplicationLayerOperationResults performRequestResponseEC(String dialog, Message requestMessage) throws Exception;
    
    /**
     * Perform the receive portion of a request response as an oc.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @return the application layer operation results
     * @throws Exception the exception
     */
    public ApplicationLayerOperationResults performRequestResponseOCReceive(String dialog) throws Exception;

    /**
     * Perform the response portion of a request response as an oc.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param responseMessage the response message
     * @param isErrorResponse the is error response
     * @return the application layer operation results
     * @throws Exception the exception
     */
    public ApplicationLayerOperationResults performRequestResponseOCResponse(String dialog, Message responseMessage, boolean isErrorResponse) throws Exception;
    
    /**
     * Perform subscription as an ec.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param requestMessage the request message
     * @return the application layer operation results
     * @throws Exception the exception
     */
    public ApplicationLayerOperationResults performSubscriptionEC(String dialog, Message requestMessage) throws Exception;
    
    /**
     * Perform the receive portion of a subscription as an oc.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @return the application layer operation results
     * @throws Exception the exception
     */
    public ApplicationLayerOperationResults performSubscriptionOCReceive(String dialog) throws Exception;

    /**
     * Perform response portion of a subscription as an oc.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param responseMessage the response message
     * @param isErrorResponse the is error response
     * @return the application layer operation results
     * @throws Exception the exception
     */
    public ApplicationLayerOperationResults performSubscriptionOCResponse(String dialog, Message responseMessage, boolean isErrorResponse) throws Exception;

    /**
     * Perform publication as an oc.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param requestMessage the request message
     * @return the application layer operation results
     * @throws Exception the exception
     */
    public ApplicationLayerOperationResults performPublicationOC(String dialog, Message requestMessage) throws Exception;
    
    /**
     * Perform the receive portion of a publication as an ec.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @return the application layer operation results
     * @throws Exception the exception
     */
    public ApplicationLayerOperationResults performPublicationECReceive(String dialog) throws Exception;

    /**
     * Perform the response portion of a publication as an ec.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param responseMessage the response message
     * @param isErrorResponse the is error response
     * @return the application layer operation results
     * @throws Exception the exception
     */
    public ApplicationLayerOperationResults performPublicationECResponse(String dialog, Message responseMessage, boolean isErrorResponse) throws Exception;

    /**
     * Disable application layer encoding of messages sent to the application layer.
     *
     * @param disableAppLayerEncoding the new disable app layer encoding
     */
    public void setDisableAppLayerEncoding(boolean disableAppLayerEncoding);
    
    /**
     * Provides a set of namespace definitions to the application layer.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param prefixToURIMap the prefix to uri map
     */
    public void setNameSpaceMap(HashMap<String, String> prefixToURIMap);
    
    /**
     * Shutdown the application layer service.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void shutdown();
    
}
