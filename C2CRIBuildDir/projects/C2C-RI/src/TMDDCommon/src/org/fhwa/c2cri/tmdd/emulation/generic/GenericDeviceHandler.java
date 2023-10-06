/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.generic;

import java.util.ArrayList;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.tmdd.TMDDAuthenticationProcessor;
import org.fhwa.c2cri.tmdd.TMDDErrorResponseProcessor;
import org.fhwa.c2cri.tmdd.emulation.TMDDEntityType;
import org.fhwa.c2cri.tmdd.emulation.TMDDMessageProcessor;
import org.fhwa.c2cri.tmdd.emulation.TMDDNRTMSelections;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityCommandProcessor;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityCommandQueue;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatus;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatusCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatusRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataOrganizationInformationCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationRequests;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidCommandException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityControlRequestLogException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityControlRequestStatusException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.MessageAuthenticationException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.NRTMViolationException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.NoMatchingDataException;
import org.fhwa.c2cri.tmdd.emulation.generic.dialogs.DlDeviceCancelControlRequest;
import org.fhwa.c2cri.tmdd.emulation.generic.dialogs.DlDeviceControlStatusRequest;
import org.fhwa.c2cri.tmdd.errorcodes.TMDDMissingInformationError;

/**
 *
 * @author TransCore ITS, LLC Created: Feb 15, 2016
 */
public class GenericDeviceHandler implements EntityControlRequestStatusCollector, EntityCommandProcessor {

    private EntityDataOrganizationInformationCollector orgInfoDataCollector;

    public void initialize() throws EntityEmulationException {
		// original implementation was empty
    }

    private GenericDeviceHandler() {
    }

    ;
    
    public GenericDeviceHandler(EntityDataOrganizationInformationCollector orgInfoCollector) {
        this.orgInfoDataCollector = orgInfoCollector;
    }

    public MessageSpecification getRRResponse(String dialog, MessageSpecification requestMessage) {
        MessageSpecification responseMsg = null;
//        try {
//
//            // Verify that the request message satisfies the project NRTM Settings
//            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, requestMessage, TMDDEntityType.EntityType.GENERIC);
//
//            // Verify that the request message satisfies any project Authentication requirements.
//            TMDDAuthenticationProcessor.getInstance().processRequestMessageAuthentication(dialog, requestMessage, TMDDEntityType.EntityType.GENERIC);

            switch (dialog) {
                case "dlDeviceCancelControlRequest":
                    String msgCancelDeviceID = (requestMessage.getElementValue("*." + "device-id"));
                    String msgCancelRequestID = (requestMessage.getElementValue("*." + "request-id"));
                    String msgCancelOrgID = (requestMessage.getElementValue("*." + "organization-requesting.organization-id"));
                    if ((msgCancelDeviceID != null) && !msgCancelDeviceID.isEmpty()&& (msgCancelRequestID != null) && !msgCancelRequestID.isEmpty() && (msgCancelOrgID != null) && !msgCancelOrgID.isEmpty()){
                        try {
                            // Figure out which Entity Type is associated with this device id.
                            TMDDEntityType.EntityType entityType = EntityCommandQueue.getInstance().getControlRequestEntityType(msgCancelOrgID, msgCancelDeviceID, msgCancelRequestID);
                            DlDeviceCancelControlRequest controlCancelDialogProcessor = new DlDeviceCancelControlRequest();
                            responseMsg = controlCancelDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICECANCELCONTROLREQUESTMSG, entityType, null, null, this);
                        } catch (Exception ex){  // Error finding a matching entityType.  Just use Generic ...
                            DlDeviceCancelControlRequest controlCancelDialogProcessor = new DlDeviceCancelControlRequest();
                            responseMsg = controlCancelDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICECANCELCONTROLREQUESTMSG, TMDDEntityType.EntityType.GENERIC, null, null, this);
                        }
                    } else {
                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-id, request-id or organization-id values for the dlCancelControlRequest dialog.");
                    }                                        
                        break;
//                    responseMsg = TMDDMessageProcessor.processDeviceCancelControlRequestMsg(requestMessage, this);
//                    break;
                case "dlDeviceControlStatusRequest":
                    String msgDeviceID = (requestMessage.getElementValue("*." + "device-id"));
                    String msgRequestID = (requestMessage.getElementValue("*." + "request-id"));
                    String msgOrgID = (requestMessage.getElementValue("*." + "organization-requesting.organization-id"));
                    if ((msgDeviceID != null) && !msgDeviceID.isEmpty()&& (msgRequestID != null) && !msgRequestID.isEmpty() && (msgOrgID != null) && !msgOrgID.isEmpty()){
                        try {
                            // Figure out which Entity Type is associated with this device id.
                            TMDDEntityType.EntityType entityType = EntityCommandQueue.getInstance().getControlRequestEntityType(msgOrgID, msgDeviceID, msgRequestID);
                            DlDeviceControlStatusRequest controlStatusDialogProcessor = new DlDeviceControlStatusRequest();
                            responseMsg = controlStatusDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICECONTROLSTATUSREQUESTMSG, entityType, null, null, this);
                        } catch (Exception ex){  // Error finding a matching entityType.  Just use Generic ...
                            DlDeviceControlStatusRequest controlStatusDialogProcessor = new DlDeviceControlStatusRequest();
                            responseMsg = controlStatusDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICECONTROLSTATUSREQUESTMSG, TMDDEntityType.EntityType.GENERIC, null, null, this);
                        }
                    } else {
                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-id, request-id or organization-id values for the dlControlStatusRequest dialog.");
                    }                                        
                        break;

                    
//                    responseMsg = TMDDMessageProcessor.processDeviceControlStatusRequestMsg(requestMessage, this);
//                    break;

                default:
                    TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                    responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the CCTVHandler.");
                    break;
            }

//        } catch (NRTMViolationException ex) {
//            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", ex.getMessage());
//        } catch (MessageAuthenticationException ex) {
//            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "authentication not recognized", ex.getMessage());
//        } catch (NoMatchingDataException ex) {
//            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "no valid data available", ex.getMessage());
//        } catch (EntityEmulationException ex) {
//            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", ex.getMessage());
//        }
//
//        try {
//            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, responseMsg, TMDDEntityType.EntityType.GENERIC);
//        } catch (NRTMViolationException ex) {
//            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", ex.getMessage());
//        } catch (EntityEmulationException ex) {
//            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", ex.getMessage());
//        }
        return responseMsg;
    }

    @Override
    public EntityControlRequestStatusRecord getControlRequestStatus(String orgId, String entityId, String requestId) throws EntityEmulationException {
        return EntityControlRequestStatus.getControlRequestStatus(orgId, entityId, requestId);
    }

    @Override
    public void updateControlRequestStatus(EntityControlRequestStatusRecord statusRecord, MessageSpecification requestMsg) throws EntityEmulationException {
        EntityControlRequestStatus.updateControlRequestStatus(statusRecord, requestMsg, true);
    }

    @Override
    public void verifyCommand(MessageSpecification commandMessage) throws InvalidCommandException, EntityEmulationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void executeCommand(MessageSpecification commandMessage) throws EntityEmulationException {
        if (commandMessage.containsMessageOfType("tmdd:deviceControlStatusRequestMsg")) {
            // get the org Id from the message
            String orgId = commandMessage.getElementValue("tmdd:deviceCancelControlRequestMsg.organization-requesting.organization-id");

            // get the entity ID from the message
            String entityId = commandMessage.getElementValue("tmdd:deviceCancelControlRequestMsg.device-id");

            // get the request ID from the message
            String requestId = commandMessage.getElementValue("tmdd:deviceCancelControlRequestMsg.request-id");

            // get the matching control Request Status record
            EntityControlRequestStatusRecord statusRecord = EntityCommandQueue.getInstance().getControlRequestStatus(orgId, entityId, requestId);

            // If there is no queued control request matching the cancel request throw an exception.
            if (!statusRecord.getStatus().equals("3") && !statusRecord.getStatus().equals("request queued/not implemented")) {
                throw new NoMatchingDataException("No matching control requests are currently queued.");
            }

            statusRecord.setStatus("other");
            statusRecord.setLockStatus(0);
            statusRecord.updateDate();
            EntityCommandQueue.getInstance().updateControlRequestStatus(statusRecord, commandMessage, true);
        }        
    }

    @Override
    public void queueCommand(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, InvalidEntityControlRequestLogException, EntityEmulationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MessageSpecification getControlResponseMessage(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, EntityEmulationException {

        // get the org Id from the message
        String orgId = "";
        // get the entity ID from the message
        String entityId = "";
        // get the request ID from the message
        String requestId = "";

        if (commandMessage.containsMessageOfType("tmdd:deviceControlStatusRequestMsg")) {
            // get the org Id from the message
            orgId = commandMessage.getElementValue("tmdd:deviceControlStatusRequestMsg.organization-requesting.organization-id");

            // get the entity ID from the message
            entityId = commandMessage.getElementValue("tmdd:deviceControlStatusRequestMsg.device-id");

            // get the request ID from the message
            requestId = commandMessage.getElementValue("tmdd:deviceControlStatusRequestMsg.request-id");

        } else {
            // get the org Id from the message
            orgId = commandMessage.getElementValue("tmdd:deviceCancelControlRequestMsg.organization-requesting.organization-id");

            // get the entity ID from the message
            entityId = commandMessage.getElementValue("tmdd:deviceCancelControlRequestMsg.device-id");

            // get the request ID from the message
            requestId = commandMessage.getElementValue("tmdd:deviceCancelControlRequestMsg.request-id");

        }

        // get the matching control Request Status record
        EntityControlRequestStatusRecord statusRecord = EntityCommandQueue.getInstance().getControlRequestStatus(orgId, entityId, requestId);
        TMDDEntityType.EntityType entityType = TMDDEntityType.EntityType.valueOf(statusRecord.getEntityType());

        ArrayList<String> controlResponseList = new ArrayList();

        System.out.println("SectionHandler::getControlResponseMessage Is tmdd:deviceCancelControlRequestMsg");
        for (String thisElement : TMDDMessageProcessor.createControlResponseMessage(entityType, statusRecord, orgInfoDataCollector).getMessageSpec()) {
            // Add all elements that come from DeviceControlResponse unless it is organization-information-forwarding-restrictions
            if (!thisElement.contains("organization-information-forwarding-restrictions")) {
                controlResponseList.add(thisElement);
            }
        }
        
        
        return new MessageSpecification(controlResponseList);

    }
   
}
