/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.cctv;

import java.util.ArrayList;
import java.util.List;
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
import org.fhwa.c2cri.centermodel.EmulationDataFileProcessor;
import org.fhwa.c2cri.centermodel.RIEmulation;
import org.fhwa.c2cri.tmdd.emulation.TMDDMessageContentGenerator;
import org.fhwa.c2cri.tmdd.emulation.cctv.dialogs.DlCCTVControlRequest;
import org.fhwa.c2cri.tmdd.emulation.cctv.dialogs.DlCCTVInventoryRequest;
import org.fhwa.c2cri.tmdd.emulation.cctv.dialogs.DlCCTVInventoryUpdate;
import org.fhwa.c2cri.tmdd.emulation.cctv.dialogs.DlCCTVStatusRequest;
import org.fhwa.c2cri.tmdd.emulation.cctv.dialogs.DlCCTVStatusUpdate;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataFile;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataOrganizationInformationCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.CCTVINVENTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.CCTVSTATUS;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationRequests;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.DataFilter;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.ValueInSetFilter;
import org.fhwa.c2cri.tmdd.emulation.exceptions.DuplicateEntityIdException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidCommandException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityControlRequestLogException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityControlRequestStatusException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityElementException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityIdValueException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidValueException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.MessageAuthenticationException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.NRTMViolationException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.NoMatchingDataException;
import org.fhwa.c2cri.tmdd.emulation.generic.dialogs.DlDeviceInformationSubscription;

/**
 * The CCTVHandler class coordinates all functions related to CCTV entities.
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class CCTVHandler implements EntityControlRequestStatusCollector, EntityCommandProcessor {

    /**
     * The inventory of CCTV devices
     */
    private CCTVInventory cctvInventory = new CCTVInventory(EntityEmulationData.EntityDataType.CCTVINVENTORY);
    /**
     * The status of all defined CCTV devices
     */
    private CCTVStatus cctvStatus = new CCTVStatus(EntityEmulationData.EntityDataType.CCTVSTATUS);
    /**
     * Provides organization information related to a specific entity.
     */
    private EntityDataOrganizationInformationCollector orgInfoDataCollector;

    private CCTVHandler() {
    }

    ;
    
    /**
     * Main constructor method for the CCTVHandler class.
     * 
     * @param orgInfoDataCollector reference to an organization information collector.
     */
    public CCTVHandler(EntityDataOrganizationInformationCollector orgInfoDataCollector) {
        this.orgInfoDataCollector = orgInfoDataCollector;
    }

//  private EntityUpdateListeners (CCTVStatus and CCTVInventory)   
    /**
     * This method is used for testing this single class.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        boolean writeFile = Boolean.parseBoolean(args[0]);
        boolean readFile = Boolean.parseBoolean(args[1]);
        boolean updateValues = Boolean.parseBoolean(args[2]);

        CCTVHandler cctv = new CCTVHandler();

        String[] fileLines = EmulationDataFileProcessor.getContent("deviceinformationRequestMsg.in").toString().split("\n");
        ArrayList<String> input = new ArrayList();
        for (int ii = 0; ii < fileLines.length; ii++) {
            input.add(fileLines[ii]);
        }
        MessageSpecification thisSpec = new MessageSpecification(input);
        System.out.println("Number of records in file = " + fileLines.length + " with message ");
        for (String theName : thisSpec.getMessageTypes()) {
            System.out.println(theName);
        }
//        System.out.println(EmulationDataFileProcessor.getContent(EntityEmulationData.EntityDataType.CCTVINVENTORY + ".out").toString());

        if (!readFile) {
            cctv.initialize(EntityEmulationData.EntityDataType.CCTVINVENTORY, null);
        } else {
            cctv.initialize(EntityEmulationData.EntityDataType.CCTVINVENTORY, EmulationDataFileProcessor.getByteArray(EntityEmulationData.EntityDataType.CCTVINVENTORY + ".out"));
        }

        if (updateValues) {
            cctv.addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY, "cctv-123456789123456789012345674");
            cctv.addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY, "cctv-123456789123456789012345675");
            cctv.addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY, "cctv-123456789123456789012345676");
            cctv.addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY, "cctv-123456789123456789012345677");
            cctv.addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY, "cctv-123456789123456789012345678");
            cctv.addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY, "cctv-123456789123456789012345679");
            cctv.deleteEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY, "cctv-123456789123456789012345678");
            cctv.deleteEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY, "cctv-123456789123456789012345674");
            cctv.addEntityElement(EntityEmulationData.EntityDataType.CCTVINVENTORY, "cctv-123456789123456789012345675", "tmdd:cCTVInventoryMsg.cctv-inventory-item.cctv-image-list[2].cctv-image", "jpeg");
            cctv.updateEntityElement(EntityEmulationData.EntityDataType.CCTVINVENTORY, "cctv-123456789123456789012345675", "tmdd:cCTVInventoryMsg.cctv-inventory-item.cctv-image-list[2].cctv-image", "tiff");
            System.out.println(cctv.getEntityElementValue(EntityEmulationData.EntityDataType.CCTVINVENTORY, "cctv-123456789123456789012345675", "tmdd:cCTVInventoryMsg.cctv-inventory-item.cctv-image-list[2].cctv-image"));
            cctv.deleteEntityElement(EntityEmulationData.EntityDataType.CCTVINVENTORY, "cctv-123456789123456789012345675", "tmdd:cCTVInventoryMsg.cctv-inventory-item.cctv-image-list[2].cctv-image");
        }

        ArrayList<DataFilter> theFilters = new ArrayList();
        ArrayList<EntityDataRecord> results = cctv.cctvInventory.getEntityElements(theFilters);
        System.out.println("Records Returned = " + results.size());

        ArrayList<String> filterValues = new ArrayList();
        filterValues.add("cctv-123456789123456789012345676");
        filterValues.add("cctv-123456789123456789012345675");
        ValueInSetFilter firstFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.CCTVINVENTORY, "EntityId", filterValues);
        theFilters.add(firstFilter);
        results = cctv.cctvInventory.getEntityElements(theFilters);
        System.out.println("Records Returned = " + results.size());

        if (writeFile) {
            EntityDataFile.writeFile(EntityEmulationData.EntityDataType.CCTVINVENTORY + ".out", results);
        }
    }

    /**
     * Initializes the data types related to a CCTV device.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param entityDataType the data type to be initialized
     * @param byteArray the data associated with this type
     * @throws EntityEmulationException if an error occurs when initializing the
     * entity data.
     */
    public void initialize(EntityEmulationData.EntityDataType entityDataType, byte[] byteArray) throws EntityEmulationException {
        switch (entityDataType) {
            case CCTVINVENTORY:
                cctvInventory.initialize(byteArray);
                break;
            case CCTVSTATUS:
                cctvStatus.initialize(byteArray);
                break;
            default:
                throw new EntityEmulationException("The CCTVHandler is unable to initialize data type " + entityDataType);
        }
    }

    /**
     * For the dialog and message received, generate the proper response
     * message.
     *
     * @param dialog
     * @param requestMessage
     * @return
     */
    public MessageSpecification getRRResponse(String dialog, MessageSpecification requestMessage) {
        MessageSpecification responseMsg = null;
//        try {

        switch (dialog) {
            case "dlCCTVControlRequest":
                // Process Control Message
                DlCCTVControlRequest dlCCTVControlRequestProcessor = new DlCCTVControlRequest();
                responseMsg = dlCCTVControlRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.CCTVCONTROLREQUESTMSG, TMDDEntityType.EntityType.CCTV, CCTVINVENTORY, cctvInventory, this);
                break;
//                    if (requestMessage.containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.CCTVCONTROLREQUESTMSG.relatedMessage())) {
//                        String cctvRequestType = requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.CCTVCONTROLREQUESTMSG.relatedMessage() + "." + "cctv-request-command");
//                        // Verify that the request message satisfies the project NRTM Settings
//                        TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, requestMessage, TMDDEntityType.EntityType.CCTV);
//
//                        // Verify that the request message satisfies any project Authentication requirements.
//                        TMDDAuthenticationProcessor.getInstance().processRequestMessageAuthentication(dialog, requestMessage, TMDDEntityType.EntityType.CCTV);
//                        responseMsg = TMDDMessageProcessor.processCCTVControlRequestMsg(requestMessage, this);
//                    } else {
//                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "center does not support this type message", "The message received did not contain the correct message type for the dlCCTVControlRequest dialog.");
//                    }
//                    break;
            case "dlCCTVInventoryRequest":
                DlCCTVInventoryRequest dlCCTVInventoryRequestProcessor = new DlCCTVInventoryRequest();
                responseMsg = dlCCTVInventoryRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.CCTV, CCTVINVENTORY, cctvInventory, this);
//                    if (requestMessage.containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage())) {
//                        String msgDeviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type"));
//                        String msgDeviceType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-type"));
//                        if ((msgDeviceInformationType.equals("1") || msgDeviceInformationType.equals("device inventory"))
//                                && (msgDeviceType.equals("2") || msgDeviceType.equals("cctv camera"))) {
//                            // Verify that the request message satisfies the project NRTM Settings
//                            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, requestMessage, TMDDEntityType.EntityType.CCTV);
//
//                            // Verify that the request message satisfies any project Authentication requirements.
//                            TMDDAuthenticationProcessor.getInstance().processRequestMessageAuthentication(dialog, requestMessage, TMDDEntityType.EntityType.CCTV);
//                            responseMsg = TMDDMessageProcessor.processDeviceInformationRequestMsg(requestMessage, EntityEmulationData.EntityDataType.CCTVINVENTORY, cctvInventory, false);
//
//                        } else {
//                            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlCCTVInventoryRequest dialog.");
//                        }
//
//                    } else {
//                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "center does not support this type message", "The message received did not contain the correct message type for the dlCCTVInventoryRequest dialog.");
//                    }
                break;
            case "dlCCTVStatusRequest":
                DlCCTVStatusRequest dlCCTVStatusRequestProcessor = new DlCCTVStatusRequest();
                responseMsg = dlCCTVStatusRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.CCTV, CCTVSTATUS, cctvStatus, this);
//                    if (requestMessage.containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage())) {
//                        String deviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type"));
//                        String deviceType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-type"));
//                        if ((deviceInformationType.equals("1") || deviceInformationType.equals("device status"))
//                                && (deviceType.equals("2") || deviceType.equals("cctv camera"))) {
//                            // Verify that the request message satisfies the project NRTM Settings
//                            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, requestMessage, TMDDEntityType.EntityType.CCTV);
//
//                            // Verify that the request message satisfies any project Authentication requirements.
//                            TMDDAuthenticationProcessor.getInstance().processRequestMessageAuthentication(dialog, requestMessage, TMDDEntityType.EntityType.CCTV);
//                            responseMsg = TMDDMessageProcessor.processDeviceInformationRequestMsg(requestMessage, EntityEmulationData.EntityDataType.CCTVSTATUS, cctvStatus, false);
//
//                        } else {
//                            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlCCTVStatusRequest dialog.");
//                        }
//                    } else {
//                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "center does not support this type message", "The message received did not contain the correct message type for the dlCCTVStatusRequest dialog.");
//                    }

                break;
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
//        /**
//         * Verify that the response message conforms to the project NRTM
//         * specification.
//         */
//        try {
//            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, responseMsg, TMDDEntityType.EntityType.CCTV);
//        } catch (NRTMViolationException ex) {
//            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", ex.getMessage());
//        } catch (EntityEmulationException ex) {
//            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", ex.getMessage());
//        }
        return responseMsg;
    }

    /**
     * For the dialog and message received, generate the proper subscription
     * response message.
     *
     * @param dialog
     * @param requestMessage
     * @return
     */
    public MessageSpecification getSubResponse(String dialog, MessageSpecification requestMessage) {
        MessageSpecification responseMsg = null;

//        try {
        switch (dialog) {
            case "dlDeviceInformationSubscription":
//      
                String msgDeviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type"));
                switch (msgDeviceInformationType) {
                    case "1":
                    case "device inventory":
                        DlDeviceInformationSubscription dlDeviceInformationProcessor = new DlDeviceInformationSubscription();
                        responseMsg = dlDeviceInformationProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.CCTV, CCTVINVENTORY, cctvInventory, this);

//                            // Verify that the request message satisfies the project NRTM Settings
//                            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, requestMessage, TMDDEntityType.EntityType.CCTV);
//
//                            // Verify that the request message satisfies any project Authentication requirements.
//                            TMDDAuthenticationProcessor.getInstance().processRequestMessageAuthentication(dialog, requestMessage, TMDDEntityType.EntityType.CCTV);
//                            responseMsg = TMDDMessageProcessor.processDeviceInformationRequestMsg(requestMessage, EntityEmulationData.EntityDataType.CCTVINVENTORY, cctvInventory, false);
                        break;
                    case "2":
                    case "device status":
                        DlDeviceInformationSubscription dlDeviceInformationStatusProcessor = new DlDeviceInformationSubscription();
                        responseMsg = dlDeviceInformationStatusProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.CCTV, CCTVSTATUS, cctvStatus, this);
//                            // Verify that the request message satisfies the project NRTM Settings
//                            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, requestMessage, TMDDEntityType.EntityType.CCTV);
//
//                            // Verify that the request message satisfies any project Authentication requirements.
//                            TMDDAuthenticationProcessor.getInstance().processRequestMessageAuthentication(dialog, requestMessage, TMDDEntityType.EntityType.CCTV);
//                            responseMsg = TMDDMessageProcessor.processDeviceInformationRequestMsg(requestMessage, EntityEmulationData.EntityDataType.CCTVSTATUS, cctvStatus, false);

                        break;
                    default:
                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlCCTVStatusRequest dialog.");
                }
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the CCTVHandler.");
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
//        /**
//         * Verify that the response message conforms to the project NRTM
//         * specification.
//         */
//        try {
//            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, responseMsg, TMDDEntityType.EntityType.CCTV);
//        } catch (NRTMViolationException ex) {
//            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", ex.getMessage());
//        } catch (EntityEmulationException ex) {
//            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", ex.getMessage());
//        }

        return responseMsg;
    }

    /**
     * For the dialog and message received, generate the proper publication
     * message update.
     *
     * @param dialog
     * @param requestMessage
     * @return
     */
    public MessageSpecification getPubResponse(String dialog, MessageSpecification requestMessage) {
        MessageSpecification responseMsg = null;

//        try {
//            // Verify that the request message satisfies the project NRTM Settings
//            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, requestMessage, TMDDEntityType.EntityType.CCTV);
//
//            // Verify that the request message satisfies any project Authentication requirements.
//            TMDDAuthenticationProcessor.getInstance().processRequestMessageAuthentication(dialog, requestMessage, TMDDEntityType.EntityType.CCTV);
        switch (dialog) {
            case "dlCCTVInventoryUpdate":
                DlCCTVInventoryUpdate dlInventoryUpdateProcessor = new DlCCTVInventoryUpdate();
                responseMsg = dlInventoryUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.CCTV, CCTVINVENTORY, cctvInventory, this);
//                    if (requestMessage.containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage())) {
//                        String msgDeviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type"));
//                        String msgDeviceType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-type"));
//                        if ((msgDeviceInformationType.equals("1") || msgDeviceInformationType.equals("device inventory"))
//                                && (msgDeviceType.equals("2") || msgDeviceType.equals("cctv camera"))) {
//                            responseMsg = TMDDMessageProcessor.processDeviceInformationRequestMsg(requestMessage, EntityEmulationData.EntityDataType.CCTVINVENTORY, cctvInventory, true);
//
//                        } else {
//                            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlCCTVInventoryRequest dialog.");
//                        }
//                    } else {
//                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "center does not support this type message", "The message received did not contain the correct message type for the dlCCTVInventoryUpdate dialog.");
//                    }
                break;
            case "dlCCTVStatusUpdate":
                DlCCTVStatusUpdate dlStatusUpdateProcessor = new DlCCTVStatusUpdate();
                responseMsg = dlStatusUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.CCTV, CCTVSTATUS, cctvStatus, this);
//                    if (requestMessage.containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage())) {
//                        String deviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type"));
//                        String deviceType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-type"));
//                        if ((deviceInformationType.equals("1") || deviceInformationType.equals("device status"))
//                                && (deviceType.equals("2") || deviceType.equals("cctv camera"))) {
//                            responseMsg = TMDDMessageProcessor.processDeviceInformationRequestMsg(requestMessage, EntityEmulationData.EntityDataType.CCTVSTATUS, cctvStatus, true);
//
//                        } else {
//                            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlCCTVStatusRequest dialog.");
//                        }
//
//                    } else {
//                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "center does not support this type message", "The message received did not contain the correct message type for the dlCCTVStatusUpdate dialog.");
//                    }
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the CCTVHandler.");
//                    throw new EntityEmulationException("The CCTVHandler is unable to process dialog " + dialog);
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

        return responseMsg;
    }

    /**
     * Returns the organization information elements for the given entity
     * identification information.
     *
     * @param entityId
     * @return
     * @throws NoMatchingDataException when no matching entity can be found
     * @throws EntityEmulationException when some other error occurs trying to
     * complete the operation
     */
    public MessageSpecification getEntityOrganizationInformation(String entityId) throws NoMatchingDataException, EntityEmulationException {
        ArrayList<DataFilter> theFilters = new ArrayList();
        MessageSpecification responseMsg = null;

        // device-id filter
        ArrayList<String> deviceIdFilterValues = new ArrayList<>();
        deviceIdFilterValues.add(entityId);
        ValueInSetFilter deviceIdFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.CCTVINVENTORY, "EntityId", deviceIdFilterValues);
        theFilters.add(deviceIdFilter);

        ArrayList<EntityDataRecord> theResponse = cctvInventory.getEntityElements(theFilters);

        // Only return items that are part of the Organization Information Data Frame
        ArrayList<String> generatedResponse = new ArrayList();
        for (EntityDataRecord thisRecord : theResponse) {
            if (thisRecord.getEntityElement().contains("device-inventory-header.organization-information")) {
                generatedResponse.add(thisRecord.getEntityElement() + " = " + thisRecord.getEntityElementValue());
            }
        }
        responseMsg = new MessageSpecification(generatedResponse);

        return responseMsg;
    }

    /**
     * Add a new entity to the set using the provided entityId. The elements
     * which make up the entity are pulled from the default entity definition.
     * The entityID will be used for the device-id value.
     *
     * Each CCTV-Inventory-Item is assigned an item index. This index is used to
     * indicate the relative order of the CCTV Inventory items.
     *
     * Checks: If entityID already exists throw an exception instead of adding
     * it. If the entityId is not valid (null or too long) throw an exception
     * instead of adding it.
     *
     * @param entityId
     * @throws org.fhwa.c2cri.centermodel.exceptions.DuplicateEntityIdException
     * @throws
     * org.fhwa.c2cri.centermodel.exceptions.InvalidEntityIdValueException
     */
    public void addEntity(EntityEmulationData.EntityDataType entityDataType, String entityId) throws DuplicateEntityIdException, InvalidEntityIdValueException {
        // check the Entity Id Value against the current entity information and requirements for the entity ID field
        try {
            switch (entityDataType) {
                case CCTVINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = cctvInventory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{CCTVSTATUS});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlCCTVInventoryUpdate");
                    if (updatedTypes.contains(CCTVSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlCCTVStatusUpdate");
                    break;
                case CCTVSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = cctvStatus.addEntity(entityId, new EntityEmulationData.EntityDataType[]{CCTVINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlCCTVStatusUpdate");
                    if (statusUpdatedTypes.contains(CCTVINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlCCTVInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The CCTVHandler is unable to process data type " + entityDataType);
            }

            // Notify Listeners for the data type
        } catch (InvalidValueException ex) {
            throw new InvalidEntityIdValueException(ex);
        } catch (DuplicateEntityIdException ex) {
            throw ex;
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }
    }

    /**
     * Create a new data element related to the specified entity.
     *
     * @param entityDataType
     * @param entityId
     * @param elementName
     * @param elementValue
     * @throws InvalidEntityIdValueException
     * @throws InvalidValueException
     */
    public void addEntityElement(EntityEmulationData.EntityDataType entityDataType, String entityId, String elementName, String elementValue) throws InvalidEntityIdValueException, InvalidValueException {
        try {
            switch (entityDataType) {
                case CCTVINVENTORY:
                    cctvInventory.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlCCTVInventoryUpdate");
                    break;
                case CCTVSTATUS:
                    cctvStatus.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlCCTVStatusUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The CCTVHandler is unable to process data type " + entityDataType);
            }
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }

    /**
     * Delete the entity from the data set.
     *
     * @param entityDataType
     * @param entityId
     * @throws InvalidEntityIdValueException
     */
    public void deleteEntity(EntityEmulationData.EntityDataType entityDataType, String entityId) throws InvalidEntityIdValueException {
        try {
            switch (entityDataType) {
                case CCTVINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = cctvInventory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{CCTVSTATUS});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlCCTVInventoryUpdate");
                    if (updatedTypes.contains(CCTVSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlCCTVStatusUpdate");
                    break;
                case CCTVSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = cctvStatus.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{CCTVINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlCCTVStatusUpdate");
                    if (statusUpdatedTypes.contains(CCTVINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlCCTVInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The CCTVHandler is unable to process data type " + entityDataType);
            }
        } catch (InvalidValueException ex) {
            throw new InvalidEntityIdValueException(ex);
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }

    /**
     * Modify the value for the given data element of an entity.
     *
     * @param entityDataType
     * @param entityId
     * @param entityElementName
     * @param entityElementValue
     * @throws InvalidEntityIdValueException when the entity can not be found
     * @throws InvalidValueException when the value provided is not valid based
     * on the data element type
     */
    public void updateEntityElement(EntityEmulationData.EntityDataType entityDataType, String entityId, String entityElementName, String entityElementValue) throws InvalidEntityIdValueException, InvalidValueException {
        try {
            switch (entityDataType) {
                case CCTVINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = cctvInventory.updateEntityElement(new EntityEmulationData.EntityDataType[]{CCTVSTATUS}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlCCTVInventoryUpdate");
                    if (updatedTypes.contains(CCTVSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlCCTVStatusUpdate");
                    break;
                case CCTVSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = cctvStatus.updateEntityElement(new EntityEmulationData.EntityDataType[]{CCTVINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlCCTVStatusUpdate");
                    if (statusUpdatedTypes.contains(CCTVINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlCCTVInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The CCTVHandler is unable to process data type " + entityDataType);
            }
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }

    /**
     * Delete the entity from the data set.
     *
     * @param entityDataType
     * @param entityId
     * @param entityElementName
     * @throws InvalidEntityIdValueException
     * @throws InvalidEntityElementException
     */
    public void deleteEntityElement(EntityEmulationData.EntityDataType entityDataType, String entityId, String entityElementName) throws InvalidEntityIdValueException, InvalidEntityElementException {
        try {
            switch (entityDataType) {
                case CCTVINVENTORY:
                    cctvInventory.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlCCTVInventoryUpdate");
                    break;
                case CCTVSTATUS:
                    cctvStatus.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlCCTVStatusUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The CCTVHandler is unable to process data type " + entityDataType);
            }
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }

    /**
     * Get the value of the specified entity data element
     *
     * @param entityDataType
     * @param entityId
     * @param entityElement
     * @return
     * @throws InvalidEntityIdValueException when the specified entity does not
     * exist.
     * @throws InvalidEntityElementException when the specified entity element
     * does not exist.
     * @throws EntityEmulationException when some other error occurs.
     */
    public String getEntityElementValue(EntityEmulationData.EntityDataType entityDataType, String entityId, String entityElement) throws InvalidEntityIdValueException, InvalidEntityElementException, EntityEmulationException {
        String entityValue = "";
        try {
            switch (entityDataType) {
                case CCTVINVENTORY:
                    entityValue = cctvInventory.getEntityElementValue(entityId, entityElement);
                    break;
                case CCTVSTATUS:
                    entityValue = cctvStatus.getEntityElementValue(entityId, entityElement);
                    break;
                default:
                    throw new EntityEmulationException("The CCTVHandler is unable to process data type " + entityDataType);
            }
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

        return entityValue;
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
        if (commandMessage == null)throw new InvalidCommandException("No valid Command Request was provided.");

        int lockStatus = 0;
        String operatorLockId = "";

        // retrieve the key elements from the request Message
        // get the org Id from the message
        String orgId = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.device-control-request-header.request-id");

        // Retrieve the desired CCTV Request Command
        String cctvRequestCommand = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-request-command");

        // Select the paramaters associated with the requested command
        String cctvRequestParameter = "";
        switch (cctvRequestCommand) {
            case "1":
            case "preset":
                cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-preset");
                break;
            case "2":
            case "jog positioning":
                cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-preset");
                break;
            case "3":
            case "direction":
                cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-preset");
                break;
            case "4":
            case "focus":
                cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-focus-lens");
                break;
            case "5":
            case "manual iris":
                cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-iris-lens");
                break;
            case "6":
            case "environmental on off":
                cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-environment");
                break;
            case "7":
            case "lock for the camera":
                cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-lock");
                break;
            case "8":
            case "pan":
                cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-pan");
                break;
            case "9":
            case "tilt":
                cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-tilt");
                break;
            case "10":
            case "zoom":
                cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-zoom-lens");
                break;
            case "11":
            case "text overlay":
                cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-text");
                break;
            case "12":
            case "other":
                cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-preset");
				break;
            default:
                break;

        }

        String operatorId = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.device-control-request-header.authentication.operator-id");

        // We could confirm that the command parameters are consistent with the device type here, if necessary.
        
    }

    @Override
    public void executeCommand(MessageSpecification commandMessage) throws EntityEmulationException {
        int lockStatus = 0;
        String operatorLockId = "";

        // retrieve the key elements from the request Message
        // get the org Id from the message
        String orgId = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.device-control-request-header.request-id");

        // Retrieve the desired CCTV Request Command
        String cctvRequestCommand = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-request-command");

        // Select the paramaters associated with the requested command
        String cctvRequestParameter = "";

        // Set the command control status 
        String cctvCommandControlStatus = "other";        
        
        try {
            switch (cctvRequestCommand) {
                case "1":
                case "preset":
                    cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-preset");
                    updateEntityElement(CCTVSTATUS,entityId, "cctv-position-preset", cctvRequestParameter);
                    break;
                case "2":
                case "jog positioning":
                    cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-preset");
                    // Note there is no Jog Position element present in the CCTVSTATUS.
//                cctvStatus.updateEntityElement(entityId, "cctv-posistion-preset", cctvRequestParameter);
                    break;
                case "3":
                case "direction":
                    cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-preset");
                    // Note there is no direction element present in the CCTVSTATUS.
                    break;
                case "4":
                case "focus":
                    cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-focus-lens");
                    updateEntityElement(CCTVSTATUS,entityId, "cctv-focus-lens", cctvRequestParameter);
                    break;
                case "5":
                case "manual iris":
                    cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-iris-lens");
                    updateEntityElement(CCTVSTATUS,entityId, "cctv-position-iris-lens", cctvRequestParameter);
                    break;
                case "6":
                case "environmental on off":
                    cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-environment");
                    updateEntityElement(CCTVSTATUS, entityId, "cctv-environmental-status", cctvRequestParameter);
                    break;
                case "7":
                case "lock for the camera":
                    cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-lock");
                    // Note there is no Lock element present in the CCTVSTATUS.  This is handled at the command queue level.
                    break;
                case "8":
                case "pan":
                    cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-pan");
                    updateEntityElement(CCTVSTATUS, entityId, "cctv-position-pan", cctvRequestParameter);
                    break;
                case "9":
                case "tilt":
                    cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-tilt");
                    updateEntityElement(CCTVSTATUS, entityId, "cctv-position-tilt", cctvRequestParameter);
                    break;
                case "10":
                case "zoom":
                    cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-zoom-lens");
                    updateEntityElement(CCTVSTATUS, entityId, "cctv-position-zoom-lens", cctvRequestParameter);
                    break;
                case "11":
                case "text overlay":
                    cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-text");
                    // Note there is no text overlay element present in the CCTVSTATUS.
                    break;
                case "12":
                case "other":
                    cctvRequestParameter = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.cctv-command-parameters.cctv-position-preset");
					break;
                default:
                    break;

            }
            cctvCommandControlStatus = "requested changes completed";
        } catch (InvalidValueException ex) {
            cctvCommandControlStatus = "request rejected invalid command parameters";
        } catch (EntityEmulationException ex){
            cctvCommandControlStatus = "other";            
        }

        
        String operatorId = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.device-control-request-header.authentication.operator-id");

        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.CCTV.name());
        statusRecord.setStatus(cctvCommandControlStatus);
        statusRecord.setLockStatus(lockStatus);
        statusRecord.setOperatorId(operatorId);
        statusRecord.setOperatorLockId(operatorLockId);
        statusRecord.updateDate();

        // Update the ControlRequestStatus record to reflect the current command status.
        EntityCommandQueue.getInstance().updateControlRequestStatus(statusRecord, commandMessage, false);
//        throw new EntityEmulationException("For some reason I just can't bring myself to do it!!");
    }

    @Override
    public void queueCommand(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, InvalidEntityControlRequestLogException, EntityEmulationException {
        int lockStatus = 0;
        String operatorLockId = "";

        String orgId = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.device-control-request-header.request-id");

        String operatorId = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.device-control-request-header.authentication.operator-id");

        String priority = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.device-control-request-header.command-request-priority");
        
        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.CCTV.name());
        statusRecord.setStatus("request queued/not implemented");
        statusRecord.setLockStatus(lockStatus);
        statusRecord.setOperatorId(operatorId);
        statusRecord.setOperatorLockId(operatorLockId);
        statusRecord.setPriority(priority==null?0:Integer.parseInt(priority));
        statusRecord.updateDate();
        EntityCommandQueue.getInstance().updateControlRequestStatus(statusRecord, commandMessage, true);
    }

    @Override
    public MessageSpecification getControlResponseMessage(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, EntityEmulationException {

        String orgId = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:cCTVControlRequestMsg.device-control-request-header.request-id");

        // get the matching control Request Status record
        EntityControlRequestStatusRecord statusRecord = EntityCommandQueue.getInstance().getControlRequestStatus(orgId, entityId, requestId);
        TMDDEntityType.EntityType entityType = TMDDEntityType.EntityType.valueOf(statusRecord.getEntityType());

        return TMDDMessageProcessor.createControlResponseMessage(entityType, statusRecord, orgInfoDataCollector);

    }
    
}
