/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.detector;

import java.util.ArrayList;
import java.util.List;
import org.fhwa.c2cri.centermodel.RIEmulation;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.tmdd.TMDDAuthenticationProcessor;
import org.fhwa.c2cri.tmdd.TMDDErrorResponseProcessor;
import org.fhwa.c2cri.tmdd.emulation.TMDDEntityType;
import org.fhwa.c2cri.tmdd.emulation.TMDDMessageContentGenerator;
import org.fhwa.c2cri.tmdd.emulation.TMDDMessageProcessor;
import org.fhwa.c2cri.tmdd.emulation.TMDDNRTMSelections;
import org.fhwa.c2cri.tmdd.emulation.detector.dialogs.DlArchivedDataProcessingDocumentationMetadataRequest;
import org.fhwa.c2cri.tmdd.emulation.detector.dialogs.DlDetectorDataRequest;
import org.fhwa.c2cri.tmdd.emulation.detector.dialogs.DlDetectorDataSubscription;
import org.fhwa.c2cri.tmdd.emulation.detector.dialogs.DlDetectorDataUpdate;
import org.fhwa.c2cri.tmdd.emulation.detector.dialogs.DlDetectorInventoryRequest;
import org.fhwa.c2cri.tmdd.emulation.detector.dialogs.DlDetectorInventoryUpdate;
import org.fhwa.c2cri.tmdd.emulation.detector.dialogs.DlDetectorMaintenanceHistoryRequest;
import org.fhwa.c2cri.tmdd.emulation.detector.dialogs.DlDetectorStatusRequest;
import org.fhwa.c2cri.tmdd.emulation.detector.dialogs.DlDetectorStatusUpdate;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityCommandProcessor;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatusCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatusRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataOrganizationInformationCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.DETECTORDATA;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.DETECTORINVENTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.DETECTORMAINTENANCEHISTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.DETECTORSTATUS;
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
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class DetectorHandler implements EntityControlRequestStatusCollector, EntityCommandProcessor {

    private DetectorInventory detectorInventory = new DetectorInventory(EntityEmulationData.EntityDataType.DETECTORINVENTORY);
    private DetectorStatus detectorStatus = new DetectorStatus(EntityEmulationData.EntityDataType.DETECTORSTATUS);
    private DetectorData detectorData = new DetectorData(EntityEmulationData.EntityDataType.DETECTORDATA);
    private DetectorMaintenanceHistory detectorMaintenanceHistory = new DetectorMaintenanceHistory(EntityEmulationData.EntityDataType.DETECTORMAINTENANCEHISTORY);
    private ArchiveDataProcessingDocumentationMetaData archivedDataProcessingDocumentationMetaData = new ArchiveDataProcessingDocumentationMetaData(EntityEmulationData.EntityDataType.ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA);
    private EntityDataOrganizationInformationCollector orgInfoDataCollector;

    private DetectorHandler() {
    }

    ;
    
    public DetectorHandler(EntityDataOrganizationInformationCollector orgInfoDataCollector) {
        this.orgInfoDataCollector = orgInfoDataCollector;
    }

    public void initialize(EntityEmulationData.EntityDataType entityDataType, byte[] byteArray) throws EntityEmulationException {
        switch (entityDataType) {

            case DETECTORINVENTORY:
                detectorInventory.initialize(byteArray);
                break;
            case DETECTORDATA:
                detectorData.initialize(byteArray);
                break;
            case DETECTORSTATUS:
                detectorStatus.initialize(byteArray);
                break;
            case DETECTORMAINTENANCEHISTORY:
                detectorMaintenanceHistory.initialize(byteArray);
                break;
            case ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA:
                archivedDataProcessingDocumentationMetaData.initialize(byteArray);
                break;
            default:
                throw new EntityEmulationException("The DetectorHandler is unable to initialize data type " + entityDataType);
        }
    }

    public MessageSpecification getRRResponse(String dialog, MessageSpecification requestMessage) {
        MessageSpecification responseMsg = null;
//        try {

            switch (dialog) {
                case "dlArchivedDataProcessingDocumentationMetadataRequest":
                    DlArchivedDataProcessingDocumentationMetadataRequest metadataProcessor = new DlArchivedDataProcessingDocumentationMetadataRequest();
                    responseMsg = metadataProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATAREQUESTMSG, TMDDEntityType.EntityType.DETECTOR, EntityEmulationData.EntityDataType.ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA, archivedDataProcessingDocumentationMetaData, this);
                    break;
//                    if (requestMessage.containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATAREQUESTMSG.relatedMessage())) {
//                        // Verify that the request message satisfies the project NRTM Settings
//                        TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, requestMessage, TMDDEntityType.EntityType.DETECTOR);
//
//                        // Verify that the request message satisfies any project Authentication requirements.
//                        TMDDAuthenticationProcessor.getInstance().processRequestMessageAuthentication(dialog, requestMessage, TMDDEntityType.EntityType.DETECTOR);
//                        responseMsg = TMDDMessageProcessor.processArchivedDataProcessingDocumentationMetadataRequestMsg(requestMessage, archivedDataProcessingDocumentationMetaData);
//                    } else {
//                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "center does not support this type message", "The message received did not contain the correct message type for the dlArchivedDataProcessingDocumentationMetadataRequest dialog.");
//                    }
//                    break;
                case "dlDetectorInventoryRequest":
                    DlDetectorInventoryRequest inventoryProcessor = new DlDetectorInventoryRequest();
                    responseMsg = inventoryProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DETECTOR, EntityEmulationData.EntityDataType.DETECTORINVENTORY, detectorInventory, this);
                    break;
//                    if (requestMessage.containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.CCTVCONTROLREQUESTMSG.relatedMessage())) {
//                        String msgDeviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type"));
//                        String msgDeviceType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-type"));
//                        if ((msgDeviceInformationType.equals("1") || msgDeviceInformationType.equals("device inventory"))
//                                && (msgDeviceType.equals("1") || msgDeviceType.equals("detector"))) {
//                            // Verify that the request message satisfies the project NRTM Settings
//                            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, requestMessage, TMDDEntityType.EntityType.DETECTOR);
//
//                            // Verify that the request message satisfies any project Authentication requirements.
//                            TMDDAuthenticationProcessor.getInstance().processRequestMessageAuthentication(dialog, requestMessage, TMDDEntityType.EntityType.DETECTOR);
//                            responseMsg = TMDDMessageProcessor.processDeviceInformationRequestMsg(requestMessage, EntityEmulationData.EntityDataType.DETECTORINVENTORY, detectorInventory,false);
//
//                        } else {
//                            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlDetectorInventoryRequest dialog.");
//                        }
//                    } else {
//                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "center does not support this type message", "The message received did not contain the correct message type for the dlDetectorInventoryRequest dialog.");
//                    }
//                    break;
                case "dlDetectorDataRequest":
                    DlDetectorDataRequest dataProcessor = new DlDetectorDataRequest();
                    responseMsg = dataProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DETECTOR, EntityEmulationData.EntityDataType.DETECTORDATA, detectorData, this);
                    break;
//                    if (requestMessage.containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.DETECTORDATAREQUESTMSG.relatedMessage())) {
//                        String dataDeviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DETECTORDATAREQUESTMSG.relatedMessage() + ".device-information-request-header." + "device-information-type"));
//                        String dataDeviceType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DETECTORDATAREQUESTMSG.relatedMessage() + ".device-information-request-header." + "device-type"));
//                        if ((dataDeviceInformationType.equals("6") || dataDeviceInformationType.equals("device data"))
//                                && (dataDeviceType.equals("1") || dataDeviceType.equals("detector"))) {
//                            // Verify that the request message satisfies the project NRTM Settings
//                            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, requestMessage, TMDDEntityType.EntityType.DETECTOR);
//
//                            // Verify that the request message satisfies any project Authentication requirements.
//                            TMDDAuthenticationProcessor.getInstance().processRequestMessageAuthentication(dialog, requestMessage, TMDDEntityType.EntityType.DETECTOR);
//                            responseMsg = TMDDMessageProcessor.processDetectorDataRequestMsg(requestMessage, EntityEmulationData.EntityDataType.DETECTORDATA, detectorData, false);
//
//                        } else {
//                            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlDetectorDataRequest dialog.");
//                        }
//                    } else {
//                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "center does not support this type message", "The message received did not contain the correct message type for the dlDetectorDataRequest dialog.");
//                    }
//                    break;
                case "dlDetectorStatusRequest":
                    DlDetectorStatusRequest statusProcessor = new DlDetectorStatusRequest();
                    responseMsg = statusProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DETECTOR, EntityEmulationData.EntityDataType.DETECTORSTATUS, detectorStatus, this);
                    break;
//                    if (requestMessage.containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage())) {
//                        String deviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type"));
//                        String deviceType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-type"));
//                        if ((deviceInformationType.equals("2") || deviceInformationType.equals("device status"))
//                                && (deviceType.equals("1") || deviceType.equals("detector"))) {
//                            // Verify that the request message satisfies the project NRTM Settings
//                            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, requestMessage, TMDDEntityType.EntityType.DETECTOR);
//
//                            // Verify that the request message satisfies any project Authentication requirements.
//                            TMDDAuthenticationProcessor.getInstance().processRequestMessageAuthentication(dialog, requestMessage, TMDDEntityType.EntityType.DETECTOR);
//                            responseMsg = TMDDMessageProcessor.processDeviceInformationRequestMsg(requestMessage, EntityEmulationData.EntityDataType.DETECTORSTATUS, detectorStatus,false);
//
//                        } else {
//                            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlDetectorStatusRequest dialog.");
//                        }
//                    } else {
//                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "center does not support this type message", "The message received did not contain the correct message type for the dlDetectorStatusRequest dialog.");
//                    }
//
//                    break;
                case "dlDetectorMaintenanceHistoryRequest":
                    DlDetectorMaintenanceHistoryRequest historyProcessor = new DlDetectorMaintenanceHistoryRequest();
                    responseMsg = historyProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DETECTOR, EntityEmulationData.EntityDataType.DETECTORMAINTENANCEHISTORY, detectorMaintenanceHistory, this);
                    break;
//                    throw new EntityEmulationException("The DetectorHandler is unable to process dialog "+dialog);
                default:
                    TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                    responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the DetectorHandler.");
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

//        try {
//            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, responseMsg, TMDDEntityType.EntityType.DETECTOR);
//        } catch (NRTMViolationException ex) {
//            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", ex.getMessage());
//        } catch (EntityEmulationException ex) {
//            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", ex.getMessage());
//        }
        return responseMsg;
    }

    public MessageSpecification getSubResponse(String dialog, MessageSpecification requestMessage) {
        MessageSpecification responseMsg = null;

//        try {

            switch (dialog) {
                case "dlDetectorDataSubscription":
                    DlDetectorDataSubscription dataProcessor = new DlDetectorDataSubscription();
                    responseMsg = dataProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DETECTOR, EntityEmulationData.EntityDataType.DETECTORDATA, detectorData, this);
                    break;
//                   if (requestMessage.containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage())) {
//                        String dataDeviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + ".device-information-type"));
//                        String dataDeviceType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + ".device-type"));
//                        if ((dataDeviceInformationType.equals("6") || dataDeviceInformationType.equals("device data"))
//                                && (dataDeviceType.equals("1") || dataDeviceType.equals("detector"))) {
//                            // Verify that the request message satisfies the project NRTM Settings
//                            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, requestMessage, TMDDEntityType.EntityType.DETECTOR);
//
//                            // Verify that the request message satisfies any project Authentication requirements.
//                            TMDDAuthenticationProcessor.getInstance().processRequestMessageAuthentication(dialog, requestMessage, TMDDEntityType.EntityType.DETECTOR);
//                            responseMsg = TMDDMessageProcessor.processDetectorDataRequestMsg(requestMessage, EntityEmulationData.EntityDataType.DETECTORDATA, detectorData, false);
//
//                        } else {
//                            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlDetectorDataSubscription dialog.");
//                        }
//                    } else {
//                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "center does not support this type message", "The message received did not contain the correct message type for the dlDetectorDataSubscription dialog.");
//                    }
//                    break;                    
                case "dlDeviceInformationSubscription":
                    String msgDeviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type"));
                    switch (msgDeviceInformationType) {
                        case "1":
                        case "device inventory":
                           DlDeviceInformationSubscription dlDeviceInformationProcessor = new DlDeviceInformationSubscription();
                           responseMsg = dlDeviceInformationProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DETECTOR, EntityEmulationData.EntityDataType.DETECTORINVENTORY, detectorInventory, this);
                           break;
                        case "2":
                        case "device status":
                           DlDeviceInformationSubscription dlDeviceStatusProcessor = new DlDeviceInformationSubscription();
                           responseMsg = dlDeviceStatusProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DETECTOR, EntityEmulationData.EntityDataType.DETECTORSTATUS, detectorStatus, this);
                           break;
                        case "6":
                        case "device data":
                           DlDeviceInformationSubscription dlDeviceDataProcessor = new DlDeviceInformationSubscription();
                           responseMsg = dlDeviceDataProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DETECTOR, EntityEmulationData.EntityDataType.DETECTORDATA, detectorData, this);
                           break;
                    }
                    break;   
//                    throw new EntityEmulationException("The DetectorHandler is unable to process dialog "+dialog);
                 default:
                    TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                    responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog "+ dialog+" is not currently supported by the DetectorHandler.");
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

        return responseMsg;
    }

    public MessageSpecification getPubResponse(String dialog, MessageSpecification requestMessage) {
        MessageSpecification responseMsg = null;

//        try {
//
//            // Verify that the request message satisfies the project NRTM Settings
//            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, requestMessage, TMDDEntityType.EntityType.DETECTOR);
//
//            // Verify that the request message satisfies any project Authentication requirements.
//            TMDDAuthenticationProcessor.getInstance().processRequestMessageAuthentication(dialog, requestMessage, TMDDEntityType.EntityType.DETECTOR);

            switch (dialog) {
                case "dlDetectorDataUpdate":
                           DlDetectorDataUpdate dlDataProcessor = new DlDetectorDataUpdate();
                           responseMsg = dlDataProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DETECTOR, EntityEmulationData.EntityDataType.DETECTORDATA, detectorData, this);
                           break;
//                   if (requestMessage.containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage())) {
//                        String dataDeviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() +  ".device-information-type"));
//                        String dataDeviceType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() +  ".device-type"));
//                        if ((dataDeviceInformationType.equals("6") || dataDeviceInformationType.equals("device data"))
//                                && (dataDeviceType.equals("1") || dataDeviceType.equals("detector"))) {
//                            // Verify that the request message satisfies the project NRTM Settings
//                            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialog, requestMessage, TMDDEntityType.EntityType.DETECTOR);
//
//                            // Verify that the request message satisfies any project Authentication requirements.
//                            TMDDAuthenticationProcessor.getInstance().processRequestMessageAuthentication(dialog, requestMessage, TMDDEntityType.EntityType.DETECTOR);
//                            responseMsg = TMDDMessageProcessor.processDetectorDataRequestMsg(requestMessage, EntityEmulationData.EntityDataType.DETECTORDATA, detectorData, true);
//
//                        } else {
//                            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlDetectorDataUpdate dialog.");
//                        }
//                    } else {
//                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "center does not support this type message", "The message received did not contain the correct message type for the dlDetectorDataUpdate dialog.");
//                    }
//                    break;                                        
                case "dlDetectorInventoryUpdate":
                           DlDetectorInventoryUpdate dlInventoryProcessor = new DlDetectorInventoryUpdate();
                           responseMsg = dlInventoryProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DETECTOR, EntityEmulationData.EntityDataType.DETECTORINVENTORY, detectorInventory, this);
                           break;
                case "dlDetectorStatusUpdate":
                           DlDetectorStatusUpdate dlStatusProcessor = new DlDetectorStatusUpdate();
                           responseMsg = dlStatusProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DETECTOR, EntityEmulationData.EntityDataType.DETECTORSTATUS, detectorStatus, this);
                           break;
//                    throw new EntityEmulationException("The EventHandler is unable to process dialog "+dialog);
                 default:
                    TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                    responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog "+ dialog+" is not currently supported by the DetectorHandler.");
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

        return responseMsg;
    }

    public MessageSpecification getEntityOrganizationInformation(String entityId) throws NoMatchingDataException, EntityEmulationException {
        ArrayList<DataFilter> theFilters = new ArrayList();
        MessageSpecification responseMsg = null;

        // device-id filter
        ArrayList<String> deviceIdFilterValues = new ArrayList<>();
        deviceIdFilterValues.add(entityId);
        ValueInSetFilter deviceIdFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.DETECTORINVENTORY, "EntityId", deviceIdFilterValues);
        theFilters.add(deviceIdFilter);

        ArrayList<EntityDataRecord> theResponse = detectorInventory.getEntityElements(theFilters);

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
     * Each DETECTOR-Inventory-Item is assigned an item index. This index is
     * used to indicate the relative order of the DETECTOR Inventory items.
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
                case DETECTORINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = detectorInventory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{DETECTORSTATUS,DETECTORDATA,DETECTORMAINTENANCEHISTORY});
                        RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorInventoryUpdate");
                    if (updatedTypes.contains(DETECTORSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorStatusUpdate");
                    if (updatedTypes.contains(DETECTORDATA))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorDataUpdate");                        
                    break;
                case DETECTORSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = detectorStatus.addEntity(entityId, new EntityEmulationData.EntityDataType[]{DETECTORINVENTORY,DETECTORDATA,DETECTORMAINTENANCEHISTORY});
                        RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorStatusUpdate");
                    if (statusUpdatedTypes.contains(DETECTORINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorInventoryUpdate");
                    if (statusUpdatedTypes.contains(DETECTORDATA))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorDataUpdate");                        
                    break;
                case DETECTORDATA:
                    List<EntityEmulationData.EntityDataType> dataUpdatedTypes = detectorData.addEntity(entityId, new EntityEmulationData.EntityDataType[]{DETECTORINVENTORY,DETECTORSTATUS,DETECTORMAINTENANCEHISTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorDataUpdate");
                    if (dataUpdatedTypes.contains(DETECTORINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorInventoryUpdate");
                    if (dataUpdatedTypes.contains(DETECTORSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorStatusUpdate");                        
                    break;
                case DETECTORMAINTENANCEHISTORY:
                    List<EntityEmulationData.EntityDataType> maintHistUpdatedTypes = detectorMaintenanceHistory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{DETECTORINVENTORY,DETECTORSTATUS,DETECTORDATA});
                    if (maintHistUpdatedTypes.contains(DETECTORDATA))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorDataUpdate");
                    if (maintHistUpdatedTypes.contains(DETECTORINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorInventoryUpdate");
                    if (maintHistUpdatedTypes.contains(DETECTORSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorStatusUpdate");                        
                    break;
                case ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA:
                    archivedDataProcessingDocumentationMetaData.addEntity(entityId);                     
                    break;
                default:
                    throw new EntityEmulationException("The DetectorHandler is unable to process data type " + entityDataType);
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

    public void addEntityElement(EntityEmulationData.EntityDataType entityDataType, String entityId, String elementName, String elementValue) throws InvalidEntityIdValueException, InvalidValueException {
        try {
            switch (entityDataType) {
                case DETECTORINVENTORY:
                    detectorInventory.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorInventoryUpdate");
                    break;
                case DETECTORSTATUS:
                    detectorStatus.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorStatusUpdate");
                    break;
                case DETECTORDATA:
                    detectorData.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorDataUpdate");
                    break;
                case DETECTORMAINTENANCEHISTORY:
                    detectorMaintenanceHistory.addEntityElement(entityId, elementName, elementValue);
                    break;
                case ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA:
                    archivedDataProcessingDocumentationMetaData.addEntityElement(entityId, elementName, elementValue);
                    break;
                default:
                    throw new EntityEmulationException("The DetectorHandler is unable to process data type " + entityDataType);
            }
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }

    public void deleteEntity(EntityEmulationData.EntityDataType entityDataType, String entityId) throws InvalidEntityIdValueException {
        try {
            switch (entityDataType) {
                case DETECTORINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = detectorInventory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{DETECTORSTATUS,DETECTORDATA,DETECTORMAINTENANCEHISTORY});
                        RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorInventoryUpdate");
                    if (updatedTypes.contains(DETECTORSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorStatusUpdate");
                    if (updatedTypes.contains(DETECTORDATA))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorDataUpdate");                        
                    break;
                case DETECTORSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = detectorStatus.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{DETECTORINVENTORY,DETECTORDATA,DETECTORMAINTENANCEHISTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorStatusUpdate");
                    if (statusUpdatedTypes.contains(DETECTORINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorInventoryUpdate");
                    if (statusUpdatedTypes.contains(DETECTORDATA))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorDataUpdate");                        
                    break;
                case DETECTORDATA:
                    List<EntityEmulationData.EntityDataType> dataUpdatedTypes = detectorData.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{DETECTORINVENTORY,DETECTORSTATUS,DETECTORMAINTENANCEHISTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorDataUpdate");
                    if (dataUpdatedTypes.contains(DETECTORINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorInventoryUpdate");
                    if (dataUpdatedTypes.contains(DETECTORSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorStatusUpdate");                        
                    break;
                case DETECTORMAINTENANCEHISTORY:
                    List<EntityEmulationData.EntityDataType> maintHistUpdatedTypes = detectorMaintenanceHistory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{DETECTORINVENTORY,DETECTORSTATUS,DETECTORDATA});
                    if (maintHistUpdatedTypes.contains(DETECTORDATA))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorDataUpdate");
                    if (maintHistUpdatedTypes.contains(DETECTORINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorInventoryUpdate");
                    if (maintHistUpdatedTypes.contains(DETECTORSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorStatusUpdate");                        
                    break;
                case ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA:
                    archivedDataProcessingDocumentationMetaData.deleteEntity(entityId);
                    break;
                default:
                    throw new EntityEmulationException("The DetectorHandler is unable to process data type " + entityDataType);
            }
        } catch (InvalidValueException ex) {
            throw new InvalidEntityIdValueException(ex);
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }

    public void updateEntityElement(EntityEmulationData.EntityDataType entityDataType, String entityId, String entityElementName, String entityElementValue) throws InvalidEntityIdValueException, InvalidValueException {
        try {
            switch (entityDataType) {
                case DETECTORINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = detectorInventory.updateEntityElement(new EntityEmulationData.EntityDataType[]{DETECTORSTATUS,DETECTORDATA,DETECTORMAINTENANCEHISTORY}, entityId, entityElementName, entityElementValue);
                        RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorInventoryUpdate");
                    if (updatedTypes.contains(DETECTORSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorStatusUpdate");
                    if (updatedTypes.contains(DETECTORDATA))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorDataUpdate");                        
                    break;
                case DETECTORSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = detectorStatus.updateEntityElement(new EntityEmulationData.EntityDataType[]{DETECTORINVENTORY,DETECTORDATA,DETECTORMAINTENANCEHISTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorStatusUpdate");
                    if (statusUpdatedTypes.contains(DETECTORINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorInventoryUpdate");
                    if (statusUpdatedTypes.contains(DETECTORDATA))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorDataUpdate");                        
                    break;
                case DETECTORDATA:
                    List<EntityEmulationData.EntityDataType> dataUpdatedTypes = detectorData.updateEntityElement(new EntityEmulationData.EntityDataType[]{DETECTORINVENTORY,DETECTORSTATUS,DETECTORMAINTENANCEHISTORY}, entityId, entityElementName, entityElementValue);
                     RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorDataUpdate");
                    if (dataUpdatedTypes.contains(DETECTORINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorInventoryUpdate");
                    if (dataUpdatedTypes.contains(DETECTORSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorStatusUpdate");                        
                    break;
                case DETECTORMAINTENANCEHISTORY:
                    List<EntityEmulationData.EntityDataType> maintHistUpdatedTypes = detectorMaintenanceHistory.updateEntityElement(new EntityEmulationData.EntityDataType[]{DETECTORINVENTORY,DETECTORSTATUS,DETECTORDATA}, entityId, entityElementName, entityElementValue);
                    if (maintHistUpdatedTypes.contains(DETECTORDATA))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorDataUpdate");
                    if (maintHistUpdatedTypes.contains(DETECTORINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorInventoryUpdate");
                    if (maintHistUpdatedTypes.contains(DETECTORSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorStatusUpdate");                        
                    break;
                case ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA:
                    archivedDataProcessingDocumentationMetaData.updateEntityElement(entityId, entityElementName, entityElementValue);                       
                    break;
                default:
                    throw new EntityEmulationException("The DetectorHandler is unable to process data type " + entityDataType);
            }
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }

    public void deleteEntityElement(EntityEmulationData.EntityDataType entityDataType, String entityId, String entityElementName) throws InvalidEntityIdValueException, InvalidEntityElementException {
        try {
            switch (entityDataType) {
                case DETECTORINVENTORY:
                    detectorInventory.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorInventoryUpdate");
                    break;
                case DETECTORSTATUS:
                    detectorStatus.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorStatusUpdate");
                    break;
                case DETECTORDATA:
                    detectorData.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDetectorDataUpdate");
                    break;
                case DETECTORMAINTENANCEHISTORY:
                    detectorMaintenanceHistory.deleteEntityElement(entityId, entityElementName);
                    break;
                case ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA:
                    archivedDataProcessingDocumentationMetaData.deleteEntityElement(entityId, entityElementName);
                    break;
                default:
                    throw new EntityEmulationException("The DetectorHandler is unable to process data type " + entityDataType);
            }
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }

    public String getEntityElementValue(EntityEmulationData.EntityDataType entityDataType, String entityId, String entityElement) throws InvalidEntityIdValueException, InvalidEntityElementException, EntityEmulationException {
        String entityValue = "";
        try {
            switch (entityDataType) {
                case DETECTORINVENTORY:
                    entityValue = detectorInventory.getEntityElementValue(entityId, entityElement);
                    break;
                case DETECTORSTATUS:
                    entityValue = detectorStatus.getEntityElementValue(entityId, entityElement);
                    break;
                case DETECTORDATA:
                    entityValue = detectorData.getEntityElementValue(entityId, entityElement);
                    break;
                case DETECTORMAINTENANCEHISTORY:
                    entityValue = detectorMaintenanceHistory.getEntityElementValue(entityId, entityElement);
                    break;
                case ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA:
                    entityValue = archivedDataProcessingDocumentationMetaData.getEntityElementValue(entityId, entityElement);
                    break;
                default:
                    throw new EntityEmulationException("The DetectorHandler is unable to process data type " + entityDataType);
            }
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

        return entityValue;
    }

    @Override
    public EntityControlRequestStatusRecord getControlRequestStatus(String orgId, String entityId, String requestId) throws EntityEmulationException {
        throw new EntityEmulationException("Detectors do not support commands.");
    }

    @Override
    public void updateControlRequestStatus(EntityControlRequestStatusRecord statusRecord, MessageSpecification requestMsg) throws EntityEmulationException {
        throw new EntityEmulationException("Detectors do not support commands.");
    }

    @Override
    public void verifyCommand(MessageSpecification commandMessage) throws InvalidCommandException, EntityEmulationException {

        throw new EntityEmulationException("Detectors do not support commands.");
    }

    @Override
    public void executeCommand(MessageSpecification commandMessage) throws EntityEmulationException {
        throw new EntityEmulationException("Detectors do not support commands.");
    }

    @Override
    public void queueCommand(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, InvalidEntityControlRequestLogException, EntityEmulationException {
        throw new EntityEmulationException("Detectors do not support commands.");
    }

    @Override
    public MessageSpecification getControlResponseMessage(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, EntityEmulationException {
        throw new EntityEmulationException("Detectors do not support commands.");

    }

}    
