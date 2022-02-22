/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.fhwa.c2cri.applayer.ApplicationLayerOperationResults;
import org.fhwa.c2cri.applayer.DialogResults;
import org.fhwa.c2cri.applayer.MessageContentGenerator;
import org.fhwa.c2cri.applayer.MessageUpdateListener;
import org.fhwa.c2cri.centermodel.RIEmulation;
import org.fhwa.c2cri.infolayer.InformationLayerOperationResults;
import org.fhwa.c2cri.infolayer.MessageProvider;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import org.fhwa.c2cri.tmdd.TMDDErrorResponseProcessor;
import org.fhwa.c2cri.tmdd.TMDDOperationResults;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationRequests;

/**
 *
 * @author TransCore ITS, LLC Created: Apr 15, 2016
 */
public class TMDDMessageContentGenerator implements MessageContentGenerator {

    /**
     * The log.
     */
    protected static Logger log = Logger.getLogger(TMDDMessageContentGenerator.class.getName());

    final static String[] STANDARDSSUPPORTED = {"NTCIP 2306v1", "NTCIP 2306 v1.69"};

    static final Map<String, String> SUPPORTEDDIALOGSMAP;

    static {
        Map<String, String> supportedDialogs = new HashMap<>();
        supportedDialogs.put("dlDMSControlRequest", "dMSControlRequestMsg");
        supportedDialogs.put("dlDetectorDataRequest", "detectorDataRequestMsg");
        supportedDialogs.put("dlIntersectionSignalTimingPatternInventoryRequest", "intersectionSignalTimingPatternInventoryRequestMsg");
        supportedDialogs.put("dlEventIndexRequest", "eventRequestMsg");
        supportedDialogs.put("dlIntersectionSignalTimingPatternInventorySubscription", "intersectionSignalTimingPatternInventoryRequestMsg");
        supportedDialogs.put("dlFullEventUpdateRequest", "eventRequestMsg");
        supportedDialogs.put("dlLCSControlRequest", "lCSControlRequestMsg");
        supportedDialogs.put("dlDMSPriorityQueueRequest", "devicePriorityQueueRequestMsg");
        supportedDialogs.put("dlHARPriorityQueueRequest", "devicePriorityQueueRequestMsg");
        supportedDialogs.put("dlGateControlRequest", "gateControlRequestMsg");
        supportedDialogs.put("dlDetectorStatusRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlCCTVInventoryRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlLCSControlScheduleRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlSectionStatusRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlLCSStatusRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlArchivedDataProcessingDocumentationMetadataRequest", "archivedDataProcessingDocumentationMetadataRequestMsg");
        supportedDialogs.put("dlCCTVStatusRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlActionLogRequest", "eventRequestMsg");
        supportedDialogs.put("dlRampMeterPriorityQueueRequest", "devicePriorityQueueRequestMsg");
        supportedDialogs.put("dlRampMeterControlRequest", "rampMeterControlRequestMsg");
        supportedDialogs.put("dlDMSMessageAppearanceRequest", "dMSMessageAppearanceRequestMsg");
        supportedDialogs.put("dlDMSMessageInventorySubscription", "dMSMessageInventoryRequestMsg");
        supportedDialogs.put("dlDMSInventoryRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlDeviceControlStatusRequest", "deviceControlStatusRequestMsg");
        supportedDialogs.put("dlHARControlScheduleRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlEventIndexSubscription", "eventRequestMsg");
        supportedDialogs.put("dlDeviceInformationSubscription", "deviceInformationRequestMsg");
        supportedDialogs.put("dlGateStatusRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlRampMeterStatusRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlCCTVControlRequest", "cCTVControlRequestMsg");
        supportedDialogs.put("dlVideoSwitchControlRequest", "videoSwitchControlRequestMsg");
        supportedDialogs.put("dlRampMeterPlanInventorySubscription", "deviceInformationRequestMsg");
        supportedDialogs.put("dlESSObservationReportRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlSectionPriorityQueueRequest", "devicePriorityQueueRequestMsg");
        supportedDialogs.put("dlLCSInventoryRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlHARMessageInventoryRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlActionLogSubscription", "eventRequestMsg");
        supportedDialogs.put("dlESSStatusRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlIntersectionSignalInventoryRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlDetectorMaintenanceHistoryRequest", "detectorMaintenanceHistoryRequestMsg");
        supportedDialogs.put("dlSectionControlScheduleRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlIntersectionSignalStatusRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlVideoSwitchStatusRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlESSInventoryRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlSectionSignalTimingPatternInventorySubscription", "sectionSignalTimingPatternInventoryRequestMsg");
        supportedDialogs.put("dlIntersectionSignalControlRequest", "intersectionSignalControlRequestMsg");
        supportedDialogs.put("dlGateInventoryRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlIntersectionSignalPriorityQueueRequest", "devicePriorityQueueRequestMsg");
        supportedDialogs.put("dlHARStatusRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlDeviceCancelControlRequest", "deviceCancelControlRequestMsg");
        supportedDialogs.put("dlDMSStatusRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlSectionControlStatusRequest", "sectionControlStatusRequestMsg");
        supportedDialogs.put("dlDMSFontTableRequest", "dMSFontTableRequestMsg");
        supportedDialogs.put("dlRampMeterControlScheduleRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlRampMeterPlanInventoryRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlSectionControlRequest", "sectionControlRequestMsg");
        supportedDialogs.put("dlDetectorInventoryRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlHARInventoryRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlESSObservationMetadataRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlHARControlRequest", "hARControlRequestMsg");
        supportedDialogs.put("dlGateControlScheduleRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlFullEventUpdateSubscription", "eventRequestMsg");
        supportedDialogs.put("dlRampMeterInventoryRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlIntersectionSignalControlScheduleRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlDMSMessageInventoryRequest", "dMSMessageInventoryRequestMsg");
        supportedDialogs.put("dlDetectorDataSubscription", "deviceInformationRequestMsg");
        supportedDialogs.put("dlVideoSwitchInventoryRequest", "deviceInformationRequestMsg");
        supportedDialogs.put("dlSectionSignalTimingPatternInventoryRequest", "sectionSignalTimingPatternInventoryRequestMsg");
        supportedDialogs.put("dlCenterActiveVerificationRequest", "centerActiveVerificationRequestMsg");        
        supportedDialogs.put("dlCenterActiveVerificationSubscription", "centerActiveVerificationRequestMsg"); 
        supportedDialogs.put("dlLinkInventoryRequest", "trafficNetworkInformationRequestMsg");        
        supportedDialogs.put("dlLinkStatusRequest", "trafficNetworkInformationRequestMsg");        
        supportedDialogs.put("dlTrafficNetworkInformationSubscription", "trafficNetworkInformationRequestMsg"); 
        supportedDialogs.put("dlNodeInventoryRequest", "trafficNetworkInformationRequestMsg");        
        supportedDialogs.put("dlNodeStatusRequest", "trafficNetworkInformationRequestMsg"); 
        supportedDialogs.put("dlHARInventoryUpdate", "hARInventoryMsg");
        supportedDialogs.put("dlIntersectionSignalTimingPatternInventoryUpdate", "intersectionSignalTimingPatternInventoryMsg");
        supportedDialogs.put("dlActionLogUpdate", "actionLogMsg");
        supportedDialogs.put("dlESSObservationReportUpdate", "eSSObservationReportMsg");
        supportedDialogs.put("dlDetectorStatusUpdate", "detectorStatusMsg");
        supportedDialogs.put("dlDetectorDataUpdate", "detectorDataMsg");
        supportedDialogs.put("dlIntersectionSignalControlScheduleUpdate", "intersectionSignalControlScheduleMsg");
        supportedDialogs.put("dlIntersectionSignalStatusUpdate", "intersectionSignalStatusMsg");
        supportedDialogs.put("dlSectionSignalTimingPatternInventoryUpdate", "sectionSignalTimingPatternInventoryMsg");
        supportedDialogs.put("dlDMSInventoryUpdate", "dMSInventoryMsg");
        supportedDialogs.put("dlSectionStatusUpdate", "sectionStatusMsg");
        supportedDialogs.put("dlESSStatusUpdate", "eSSStatusMsg");
        supportedDialogs.put("dlLCSInventoryUpdate", "lCSInventoryMsg");
        supportedDialogs.put("dlCCTVStatusUpdate", "cCTVStatusMsg");
        supportedDialogs.put("dlRampMeterPlanInventoryUpdate", "rampMeterPlanInventoryMsg");
        supportedDialogs.put("dlDMSMessageInventoryUpdate", "dMSMessageInventoryMsg");
        supportedDialogs.put("dlSectionControlScheduleUpdate", "sectionControlScheduleMsg");
        supportedDialogs.put("dlCCTVInventoryUpdate", "cCTVInventoryMsg");
        supportedDialogs.put("dlRampMeterStatusUpdate", "rampMeterStatusMsg");
        supportedDialogs.put("dlESSInventoryUpdate", "eSSInventoryMsg");
        supportedDialogs.put("dlHARControlScheduleUpdate", "hARControlScheduleMsg");
        supportedDialogs.put("dlRampMeterInventoryUpdate", "rampMeterInventoryMsg");
        supportedDialogs.put("dlVideoSwitchStatusUpdate", "videoSwitchStatusMsg");
        supportedDialogs.put("dlDetectorInventoryUpdate", "detectorInventoryMsg");
        supportedDialogs.put("dlHARMessageInventoryUpdate", "hARMessageInventoryMsg");
        supportedDialogs.put("dlGateInventoryUpdate", "gateInventoryMsg");
        supportedDialogs.put("dlVideoSwitchInventoryUpdate", "videoSwitchInventoryMsg");
        supportedDialogs.put("dlFullEventUpdateUpdate", "fEUMsg");
        supportedDialogs.put("dlEventIndexUpdate", "eventIndexMsg");
        supportedDialogs.put("dlGateStatusUpdate", "gateStatusMsg");
        supportedDialogs.put("dlRampMeterControlScheduleUpdate", "rampMeterControlScheduleMsg");
        supportedDialogs.put("dlIntersectionSignalInventoryUpdate", "intersectionSignalInventoryMsg");
        supportedDialogs.put("dlLCSStatusUpdate", "lCSStatusMsg");
        supportedDialogs.put("dlDMSStatusUpdate", "dMSStatusMsg");
        supportedDialogs.put("dlHARStatusUpdate", "hARStatusMsg");
        supportedDialogs.put("dlLCSControlScheduleUpdate", "lCSControlScheduleMsg");
        supportedDialogs.put("dlGateControlScheduleUpdate", "gateControlScheduleMsg");
        supportedDialogs.put("dlCenterActiveVerificationUpdate", "centerActiveVerificationResponseMsg");
        supportedDialogs.put("dlLinkInventoryUpdate", "linkInventoryMsg");
        supportedDialogs.put("dlLinkStatusUpdate", "linkStatusMsg");
        supportedDialogs.put("dlNodeInventoryUpdate", "nodeInventoryMsg");
        supportedDialogs.put("dlNodeStatusUpdate", "nodeStatusMsg");
        SUPPORTEDDIALOGSMAP = Collections.unmodifiableMap(supportedDialogs);
    }

    private static TMDDMessageContentGenerator messageGenerator;
    private HashMap<MessageUpdateListener,String> listeners = new HashMap();
    private final Object MUTEX = new Object();
    
    /**
     * The name space map.
     */
    private HashMap<String, String> nameSpaceMap = new HashMap();
    
    private TMDDMessageContentGenerator(){        
    }
    
    public static TMDDMessageContentGenerator getInstance(){
        if (messageGenerator == null){
            messageGenerator = new TMDDMessageContentGenerator();
        }
        return messageGenerator;
    }

    @Override
    public String[] getApplicationStandardsSupported() {
        return STANDARDSSUPPORTED;
    }

    @Override
    public boolean isMessageSupported(String operation, String nameSpace, String messageName, MessageProvider messageProvider) {
        // Only generate messages if entity emulation is enabled.
        if (!RIEmulation.getInstance().isEmulationEnabled()) return false;
        
        if (nameSpace.equals("http://www.tmdd.org/303/messages")) {
            if (SUPPORTEDDIALOGSMAP.containsKey(operation)) {
                if (SUPPORTEDDIALOGSMAP.get(operation).equals(messageName)) {
                    
                    
                    // If the operation is trafficNetworkInformationRequestMsg, we currently support only links and nodes.  If it's not one of these,
                    // then we don't support it.
                    if (operation.equals("dlTrafficNetworkInformationSubscription")){
                        try{
                            String networkInformationType = (messageProvider.getMessage().getMessageSpecification().getElementValue(EntityEmulationRequests.EntityRequestMessageType.TRAFFICNETWORKINFORMATIONREQUESTMSG.relatedMessage() + "." + "network-information-type"));
                            switch (networkInformationType)
                            {
                                case "1":
                                case "node inventory":
                                case "2":
                                case "node status":
                                case "3":
                                case "link inventory":
                                case "4":
                                case "link status":
                                    return true;
                                default:
                                    return false;
                            }
                        } catch (Exception ex){
                            return false;
                        }
                    }
                    
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void registerMessageUpdateListener(MessageUpdateListener msgListener, String operationName) {
        if (msgListener != null){
            synchronized(MUTEX){
                System.out.println(this.getClass().toString()+"::registerMessageUpdateListener A Listener "+msgListener.toString()+" has registered for operation "+operationName);
                if (!listeners.containsKey(msgListener)){
                    listeners.put(msgListener, operationName);
                } else {
                    System.out.println(this.getClass().getName()+"::registerMessageUpdateListener The Message Listener "+msgListener.toString()+" already exists on the listener list. ");
                }
            }
        }
    }

    @Override
    public void unregisterMessageUpdateListener(MessageUpdateListener msgListener) {
        if (msgListener != null){
            synchronized(MUTEX){
                System.out.println(this.getClass().toString()+"::registerMessageUpdateListener A Listener "+msgListener.toString()+"has unregistered for an operation.");
                listeners.remove(msgListener);
            }
        }
    }

    @Override
    public Message getMessage(String operation, String nameSpace, String messageName) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Message getErrorMessage(String operation, String nameSpace, String errorDescription) throws Exception {
        nameSpaceMap.clear();
        nameSpaceMap.put("tmdd", "http://www.tmdd.org/303/messages");
        nameSpaceMap.put("c2c", "http://www.ntcip.org/c2c-message-administration");

        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
        MessageSpecification responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + operation + " failed with error: " + errorDescription);
        Message errorMessage = MessageManager.getInstance().createMessage(operation);
        errorMessage.setMessageSpecification(responseMsg);
        return errorMessage;
    }

    @Override
    public Message getResponseMessage(String operation, String nameSpace, String messageName, Message requestMessage, ApplicationLayerOperationResults opResults) {
        
        nameSpaceMap.clear();
        nameSpaceMap.put("tmdd", "http://www.tmdd.org/303/messages");
        nameSpaceMap.put("c2c", "http://www.ntcip.org/c2c-message-administration");
        nameSpaceMap.put("tmddX", "http://www.tmdd.org/X");

        Message responseMsgResult = null;
        InformationLayerOperationResults infoOpResults = new TMDDOperationResults(operation, opResults);

        DialogResults results = infoOpResults.getDialogResults(Message.MESSAGETYPE.Request);
        
        if (operation.endsWith("Request") || operation.endsWith("Subscription")) {
//            log.info(requestMessage.toXML());
            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
            // Check the request message for errors.
            if (errorProcessor.requestContainsError(results)) {

                // Generate an errorResponseMessage for erroneous message that was received.
                responseMsgResult = errorProcessor.getErrorResponseMsg(operation);

                MessageManager.getInstance().addMessage(responseMsgResult, true);
                responseMsgResult.setMessageName("RESPONSE");
                C2CRIMessageAdapter.updateMessageFromSpec(responseMsgResult, nameSpaceMap);
                responseMsgResult.setMessageType("errorReportMsg");
                responseMsgResult.setEncodedMessageType("errorReportMsg");

            }
        }

        if (responseMsgResult == null) {
            try {
                if (operation.endsWith("Request")) {
                    responseMsgResult = RIEmulation.getInstance().getRRResponse(operation, requestMessage);
                    
                    C2CRIMessageAdapter.updateMessageFromSpec(responseMsgResult, nameSpaceMap);


                    responseMsgResult.setMessageType(getPrimaryMessageType(responseMsgResult.getMessageSpecification()));
                } else if (operation.endsWith("Subscription")) {
                    responseMsgResult = RIEmulation.getInstance().getSubResponse(operation, requestMessage);
                    C2CRIMessageAdapter.updateMessageFromSpec(responseMsgResult, nameSpaceMap);
                    String messageType = getPrimaryMessageType(responseMsgResult.getMessageSpecification());
                    responseMsgResult.setMessageType(messageType);
                    responseMsgResult.setEncodedMessageType(messageType);
                } else {
                    responseMsgResult = RIEmulation.getInstance().getPubMessage(operation, requestMessage);
                    C2CRIMessageAdapter.updateMessageFromSpec(responseMsgResult, nameSpaceMap);
                    String messageType = getPrimaryMessageType(responseMsgResult.getMessageSpecification());
                    responseMsgResult.setMessageType(messageType);
                    responseMsgResult.setEncodedMessageType(messageType);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                MessageSpecification responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + operation + " failed with error: " + ex.getMessage());
                Message errorMessage = MessageManager.getInstance().createMessage(operation);
                errorMessage.setMessageSpecification(responseMsg);
                responseMsgResult = errorMessage;
                C2CRIMessageAdapter.updateMessageFromSpec(requestMessage, nameSpaceMap);
                responseMsgResult.setMessageType("errorReportMsg");
            }
        }
//        log.info(responseMsgResult.toXML());
        
        return responseMsgResult;
    }

    /**
     * Returns the primary message type for the given message specification.
     *
     * @param messageSpec
     * @return messageType
     */
    private String getPrimaryMessageType(MessageSpecification messageSpec) {
        String results = "";

        for (String thisMessageType : messageSpec.getMessageTypes()) {
            // remove the namespace prefix
            results = thisMessageType.replace("tmdd:", "").replace("c2c:", "");
        }

        return results;
    }
    
    public void operationRelatedDataUpdate(String operation){
        System.out.println(this.getClass().toString()+"::operationRelatedDataUpdate Update reported for operation "+operation);
        HashMap<MessageUpdateListener,String> listenersLocal = null;
        
        synchronized (MUTEX){
            listenersLocal = new HashMap(listeners);            
        }
        if (listenersLocal.containsValue(operation)){
            System.out.println("TMDDMessageContentGenerator::operationRelatedDataUpdate A listener has registered for operation  "+operation);
            for (Map.Entry<MessageUpdateListener,String> thisEntry : listenersLocal.entrySet()){
                if (operation.equals(thisEntry.getValue())){
                    if (thisEntry.getKey()!= null){
                        thisEntry.getKey().messageUpdate(operation, null, null);
                        System.out.println("TMDDMessageContentGenerator::operationRelatedDataUpdate Provided notification for an update of operation "+operation);
                    }
                }
            }
            System.out.println(this.getClass().toString()+"::operationRelatedDataUpdate No listeners have registered for operation  "+operation);            
        }
        
    }

}
