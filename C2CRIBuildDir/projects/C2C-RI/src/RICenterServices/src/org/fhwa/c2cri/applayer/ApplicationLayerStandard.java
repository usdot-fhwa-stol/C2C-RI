/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.applayer;

import java.net.URL;
import net.xeoh.plugins.base.Plugin;
import org.fhwa.c2cri.infolayer.SubPubMapper;
import org.fhwa.c2cri.messagemanager.Message;

/**
 * The Interface ApplicationLayerStandard.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public interface ApplicationLayerStandard extends Plugin{

    /**
     * Gets the name.
     *
     * @return The name of the standard.
     */
    public String getName();

    /**
     * Initialize standard.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param serviceSpecificationURL the service specification url
     * @param baseStandardSpecificationURL the base standard specification url
     * @param centerMode the center mode
     * @param informationLayerStandard the information layer standard
     * @param testCaseName the test case name
     * @param requestDialog the request dialog
     * @param subscriptionDialog the subscription dialog
     * @param publicationDialog the publication dialog
     * @throws Exception the exception
     */
    public void initializeStandard(URL serviceSpecificationURL, 
                                   URL baseStandardSpecificationURL,
                                   String centerMode,
                                   String informationLayerStandard,
                                   String testCaseName,
                                   String requestDialog,
                                   String subscriptionDialog,
                                   String publicationDialog) throws Exception;

/**
 * Gets the information layer adapter.
 *
 * @return The InformationLayerAdapter Interface provided by this standard.
 */
    public InformationLayerAdapter getInformationLayerAdapter();

    /**
     * Request_response_ec.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param requestMessage the request message
     * @return the message
     * @throws Exception the exception
     */
    public Message request_response_ec(String dialog, Message requestMessage) throws Exception;

    /**
     * Request_response_oc_receive.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @return the message
     * @throws Exception the exception
     */
    public Message request_response_oc_receive(String dialog) throws Exception;

    /**
     * Request_response_oc_reply.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param responseMessage the response message
     * @throws Exception the exception
     */
    public void request_response_oc_reply(String dialog, Message responseMessage) throws Exception;

    /**
     * Subscription_ec.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param subscriptionMessage the subscription message
     * @return the message
     * @throws Exception the exception
     */
    public Message subscription_ec(String dialog, Message subscriptionMessage) throws Exception;

    /**
     * Subcription_oc_receive.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @return the message
     * @throws Exception the exception
     */
    public Message subcription_oc_receive(String dialog) throws Exception;

    /**
     * Subscription_oc_reply.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param responseMessage the response message
     * @throws Exception the exception
     */
    public void subscription_oc_reply(String dialog, Message responseMessage) throws Exception;

    /**
     * Publication_oc.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param publicationMessage the publication message
     * @return the message
     * @throws Exception the exception
     */
    public Message publication_oc(String dialog, Message publicationMessage) throws Exception;

    /**
     * Publication_ec_receive.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @return the message
     * @throws Exception the exception
     */
    public Message publication_ec_receive(String dialog) throws Exception;

    /**
     * Publication_ec_reply.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param responseMessage the response message
     * @throws Exception the exception
     */
    public void publication_ec_reply(String dialog, Message responseMessage) throws Exception;

    /**
     * Gets the dialog results.
     *
     * @return the dialog results
     */
    public DialogResults getDialogResults();

    
    /**
     * Sets the message content generator.
     *
     * @param msgGenerator the new message content generator
     * @throws Exception the exception
     */
    public void setMessageContentGenerator(MessageContentGenerator msgGenerator) throws Exception;

    /**
     * Sets the sub pub mapper.
     *
     * @param subPubMapper the new sub pub mapper
     */
    public void setSubPubMapper(SubPubMapper subPubMapper);
    
    /**
     * Stop services.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void stopServices();
}
