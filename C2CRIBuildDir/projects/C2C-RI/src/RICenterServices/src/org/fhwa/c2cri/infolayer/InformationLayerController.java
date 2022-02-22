/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.infolayer;

import org.fhwa.c2cri.messagemanager.Message;


/**
 * The Interface InformationLayerController provides the methods that must be available
 * to control a center from an Information Layer perspective.  This layer does not deal with
 * encoding or transport, but it still has dialog patterns.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public interface InformationLayerController {

    /**
     * Perform get ec.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @return the information layer operation results
     * @throws Exception the exception
     */
    public InformationLayerOperationResults performGetEC(String dialog) throws Exception;
    
    /**
     * Perform get oc.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param responseMessage the response message
     * @return the information layer operation results
     * @throws Exception the exception
     */
    public InformationLayerOperationResults performGetOC(String dialog, Message responseMessage) throws Exception;

    /**
     * Perform request response ec.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param requestMessage the request message
     * @return the information layer operation results
     * @throws Exception the exception
     */
    public InformationLayerOperationResults performRequestResponseEC(String dialog, Message requestMessage) throws Exception;
    
    /**
     * Perform request response oc receive.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @return the information layer operation results
     * @throws Exception the exception
     */
    public InformationLayerOperationResults performRequestResponseOCReceive(String dialog) throws Exception;

    /**
     * Perform request response oc response.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param responseMessage the response message
     * @param isErrorResponse the is error response
     * @return the information layer operation results
     * @throws Exception the exception
     */
    public InformationLayerOperationResults performRequestResponseOCResponse(String dialog, Message responseMessage, boolean isErrorResponse) throws Exception;
    
    /**
     * Perform subscription ec.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param requestMessage the request message
     * @return the information layer operation results
     * @throws Exception the exception
     */
    public InformationLayerOperationResults performSubscriptionEC(String dialog, Message requestMessage) throws Exception;
    
    /**
     * Perform subscription oc receive.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @return the information layer operation results
     * @throws Exception the exception
     */
    public InformationLayerOperationResults performSubscriptionOCReceive(String dialog) throws Exception;

    /**
     * Perform subscription oc response.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param responseMessage the response message
     * @param isErrorResponse the is error response
     * @return the information layer operation results
     * @throws Exception the exception
     */
    public InformationLayerOperationResults performSubscriptionOCResponse(String dialog, Message responseMessage, boolean isErrorResponse) throws Exception;

    /**
     * Perform publication oc.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param requestMessage the request message
     * @return the information layer operation results
     * @throws Exception the exception
     */
    public InformationLayerOperationResults performPublicationOC(String dialog, Message requestMessage) throws Exception;
    
    /**
     * Perform publication ec receive.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @return the information layer operation results
     * @throws Exception the exception
     */
    public InformationLayerOperationResults performPublicationECReceive(String dialog) throws Exception;

    /**
     * Perform publication ec response.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param responseMessage the response message
     * @param isErrorResponse the is error response
     * @return the information layer operation results
     * @throws Exception the exception
     */
    public InformationLayerOperationResults performPublicationECResponse(String dialog, Message responseMessage, boolean isErrorResponse) throws Exception;

    /**
     * Sets the disable app layer encoding.
     *
     * @param disableAppLayerEncoding the new disable app layer encoding
     */
    public void setDisableAppLayerEncoding(boolean disableAppLayerEncoding);
    
    /**
     * Shutdown.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void shutdown();
        
}
