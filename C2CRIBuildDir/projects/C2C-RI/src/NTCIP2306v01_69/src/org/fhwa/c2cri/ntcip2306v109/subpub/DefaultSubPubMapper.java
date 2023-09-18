/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.subpub;

import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message;
import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.fhwa.c2cri.infolayer.SubPubMapper;
import org.fhwa.c2cri.infolayer.SubPubMappingException;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.Center;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecCollection;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;


/**
 * The Class DefaultSubPubMapper is a basic mapping between subscriptions and publications based on input provided.  
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class DefaultSubPubMapper implements SubPubMapper {

    /** The this generator. */
    private static DefaultSubPubMapper thisGenerator;
    
    /** The external sub pub mapper. */
    private static SubPubMapper externalSubPubMapper;
    
    /** The operation spec collection. */
    private static OperationSpecCollection operationSpecCollection = new OperationSpecCollection();
    
    /** The ri center mode. */
    private static Center.RICENTERMODE riCenterMode = Center.RICENTERMODE.EC;
    
    /** The local map. */
    private static HashMap<OperationIdentifier, OperationIdentifier> localMap = new HashMap<>();
	
	private static final Object LOCK = new Object();

    /**
     * Instantiates a new default sub pub mapper.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private DefaultSubPubMapper() {
    }

    /**
     * Gets the single instance of DefaultSubPubMapper.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return single instance of DefaultSubPubMapper
     */
    public static SubPubMapper getInstance() {
        if (thisGenerator == null) {
            thisGenerator = new DefaultSubPubMapper();
        }
        return thisGenerator;
    }

    /**
     * Sets the external sub pub mapper.
     *
     * @param subPubMapper the new external sub pub mapper
     */
    public static void setExternalSubPubMapper(SubPubMapper subPubMapper) {
        externalSubPubMapper = subPubMapper;
    }

    /**
     * Sets the center mode.
     *
     * @param centerMode the new center mode
     */
    public static void setCenterMode(Center.RICENTERMODE centerMode) {
        riCenterMode = centerMode;
    }

    /**
     * Sets the operation spec collection.
     *
     * @param opSpecCollection the new operation spec collection
     */
    public static void setOperationSpecCollection(OperationSpecCollection opSpecCollection) {
        operationSpecCollection = opSpecCollection;

    }

    /**
     * Sets the local sub pub mapping.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param subPubMap the sub pub map
     */
    public static void setLocalSubPubMapping(HashMap<OperationIdentifier, OperationIdentifier> subPubMap) {
        synchronized (LOCK) {
            localMap = subPubMap;
        }
    }

    /**
     * Gets the related publication name.
     *
     * @param subscriptionOperationName the subscription operation name
     * @param requestMessage the request message
     * @return the related publication name
     * @throws SubPubMappingException the sub pub mapping exception
     */
    @Override
    public String getRelatedPublicationName(String subscriptionOperationName, Message requestMessage) throws SubPubMappingException {
        String publicationName;
        if (externalSubPubMapper != null) {
            publicationName = externalSubPubMapper.getRelatedPublicationName(subscriptionOperationName, requestMessage);
            return publicationName;
        } else {
            synchronized (LOCK) {
                Iterator<OperationIdentifier> subIterator = localMap.keySet().iterator();
                while (subIterator.hasNext()) {
                    OperationIdentifier thisSubOpId = subIterator.next();
                    if (thisSubOpId.getOperationName().equalsIgnoreCase(subscriptionOperationName)) {
                        OperationIdentifier thisPubOpId = localMap.get(thisSubOpId);
                        return thisPubOpId.getOperationName();
                    }
                }
                throw new SubPubMappingException("DefaultSubPubMapper:: Could not find a match for Subscription Operation " + subscriptionOperationName);
            }
        }
    }

    /**
     * Gets the related publication name.
     *
     * @param subscriptionOperationName the subscription operation name
     * @param requestMsg the request msg
     * @return the related publication name
     * @throws SubPubMappingException the sub pub mapping exception
     */
    public String getRelatedPublicationName(String subscriptionOperationName, NTCIP2306Message requestMsg) throws SubPubMappingException {
        String publicationName;
        if (externalSubPubMapper != null) {
            // Convert the NTCIP2306 Message to a standard Message.
            publicationName = externalSubPubMapper.getRelatedPublicationName(
                    subscriptionOperationName,
                    C2CRIMessageAdapter.toC2CRIMessage(subscriptionOperationName, requestMsg));
            return publicationName;
        } else {
            synchronized (LOCK) {
                Iterator<OperationIdentifier> subIterator = localMap.keySet().iterator();
                while (subIterator.hasNext()) {
                    OperationIdentifier thisSubOpId = subIterator.next();
                    if (thisSubOpId.getOperationName().equalsIgnoreCase(subscriptionOperationName)) {
                        OperationIdentifier thisPubOpId = localMap.get(thisSubOpId);
                        return thisPubOpId.getOperationName();
                    }
                }
                throw new SubPubMappingException("DefaultSubPubMapper:: Could not find a match for Subscription Operation " + subscriptionOperationName);
            }
        }
    }

    /**
     * Gets the related publication operation id.
     *
     * @param subscriptionOperationName the subscription operation name
     * @param inputMessage the input message
     * @return the related publication operation id
     * @throws SubPubMappingException the sub pub mapping exception
     */
    public static OperationIdentifier getRelatedPublicationOperationId(String subscriptionOperationName, NTCIP2306Message inputMessage) throws SubPubMappingException {
        String publicationName;
        if (externalSubPubMapper != null) {
            publicationName = externalSubPubMapper.getRelatedPublicationName(subscriptionOperationName,
                    C2CRIMessageAdapter.toC2CRIMessage(subscriptionOperationName, inputMessage));
            ArrayList<OperationSpecification> pubSpecs = operationSpecCollection.getSpecsWithOperationName(publicationName);
            if (pubSpecs.size() > 0) {
                OperationSpecification pubOpSpec = pubSpecs.get(0);
                return new OperationIdentifier(pubOpSpec.getRelatedToService(), pubOpSpec.getRelatedToPort(), pubOpSpec.getOperationName(), OperationIdentifier.SOURCETYPE.LISTENER);
            }
            throw new SubPubMappingException("DefaultSubPubMapper:: Could not find a valid Publication Identifier for Operation " + subscriptionOperationName);
        } else {
            synchronized (LOCK) {
                Iterator<OperationIdentifier> subIterator = localMap.keySet().iterator();
                while (subIterator.hasNext()) {
                    OperationIdentifier thisSubOpId = subIterator.next();
                    if (thisSubOpId.getOperationName().equalsIgnoreCase(subscriptionOperationName)) {
                        OperationIdentifier thisPubOpId = localMap.get(thisSubOpId);
                        return thisPubOpId;
                    }
                }
                throw new SubPubMappingException("DefaultSubPubMapper:: Could not find a match for Subscription Operation " + subscriptionOperationName);
            }
        }

    }

    /**
     * Gets the related subscription name.
     *
     * @param publicationOperationName the publication operation name
     * @return the related subscription name
     * @throws SubPubMappingException the sub pub mapping exception
     */
    @Override
    public String getRelatedSubscriptionName(String publicationOperationName) throws SubPubMappingException {
        String subscriptionName;
        if (externalSubPubMapper != null) {
            subscriptionName = externalSubPubMapper.getRelatedSubscriptionName(publicationOperationName);
            return subscriptionName;
        } else {
            synchronized (LOCK) {
                Iterator<OperationIdentifier> subIterator = localMap.keySet().iterator();
                while (subIterator.hasNext()) {
                    OperationIdentifier thisSubOpId = subIterator.next();
                    if (localMap.get(thisSubOpId).getOperationName().equalsIgnoreCase(publicationOperationName)) {
                        return thisSubOpId.getOperationName();
                    }
                }
                throw new SubPubMappingException("DefaultSubPubMapper:: Could not find a match for Publication Operation " + publicationOperationName);
            }
        }
    }

    /**
     * Gets the related subscription operation id.
     *
     * @param publicationOperationName the publication operation name
     * @return the related subscription operation id
     * @throws SubPubMappingException the sub pub mapping exception
     */
    public static OperationIdentifier getRelatedSubscriptionOperationId(String publicationOperationName) throws SubPubMappingException {
        String subscriptionName;
        if (externalSubPubMapper != null) {
            subscriptionName = externalSubPubMapper.getRelatedSubscriptionName(publicationOperationName);
            ArrayList<OperationSpecification> subSpecs = operationSpecCollection.getSpecsWithOperationName(subscriptionName);
            if (subSpecs.size() > 0) {
                OperationSpecification pubOpSpec = subSpecs.get(0);
                if (riCenterMode.equals(Center.RICENTERMODE.EC)) {
                    return new OperationIdentifier(pubOpSpec.getRelatedToService(), pubOpSpec.getRelatedToPort(), pubOpSpec.getOperationName(), OperationIdentifier.SOURCETYPE.LISTENER);
                } else {
                    return new OperationIdentifier(pubOpSpec.getRelatedToService(), pubOpSpec.getRelatedToPort(), pubOpSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
                }
            }
            throw new SubPubMappingException("DefaultSubPubMapper:: Could not find a valid Subscription Identifier for Operation " + publicationOperationName);
        } else {
            synchronized (LOCK) {
                Iterator<OperationIdentifier> subIterator = localMap.keySet().iterator();
                while (subIterator.hasNext()) {
                    OperationIdentifier thisSubOpId = subIterator.next();
                    if (localMap.get(thisSubOpId).getOperationName().equalsIgnoreCase(publicationOperationName)) {
                        return thisSubOpId;
                    }
                }
                throw new SubPubMappingException("DefaultSubPubMapper:: Could not find a match for Publication Operation " + publicationOperationName);
            }
        }
    }
}
