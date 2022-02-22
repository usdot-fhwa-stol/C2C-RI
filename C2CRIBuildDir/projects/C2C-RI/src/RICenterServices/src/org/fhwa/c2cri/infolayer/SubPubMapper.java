/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.infolayer;

import org.fhwa.c2cri.messagemanager.Message;


/**
 * The Interface SubPubMapper defines an object that can identify the appropriate relationships between subscriptions and publications.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public interface SubPubMapper {
    
    /**
     * Gets the related publication name.
     *
     * @param subscriptionOperationName the subscription operation name
     * @param requestMessage the request message
     * @return the related publication name
     * @throws SubPubMappingException the sub pub mapping exception
     */
    public String getRelatedPublicationName(String subscriptionOperationName, Message requestMessage) throws SubPubMappingException;
    
    /**
     * Gets the related subscription name.
     *
     * @param publicationOperationName the publication operation name
     * @return the related subscription name
     * @throws SubPubMappingException the sub pub mapping exception
     */
    public String getRelatedSubscriptionName(String publicationOperationName) throws SubPubMappingException;
}
