/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.gate;

import java.util.ArrayList;
import java.util.List;
import org.fhwa.c2cri.centermodel.RIEmulation;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.tmdd.TMDDErrorResponseProcessor;
import org.fhwa.c2cri.tmdd.emulation.TMDDEntityType;
import org.fhwa.c2cri.tmdd.emulation.TMDDMessageContentGenerator;
import org.fhwa.c2cri.tmdd.emulation.TMDDMessageProcessor;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityCommandProcessor;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityCommandQueue;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatus;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatusCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatusRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataOrganizationInformationCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.GATECONTROLSCHEDULE;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.GATEINVENTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.GATESTATUS;
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
import org.fhwa.c2cri.tmdd.emulation.exceptions.NoMatchingDataException;
import org.fhwa.c2cri.tmdd.emulation.gate.dialogs.DlGateControlRequest;
import org.fhwa.c2cri.tmdd.emulation.gate.dialogs.DlGateControlScheduleRequest;
import org.fhwa.c2cri.tmdd.emulation.gate.dialogs.DlGateControlScheduleUpdate;
import org.fhwa.c2cri.tmdd.emulation.gate.dialogs.DlGateInventoryRequest;
import org.fhwa.c2cri.tmdd.emulation.gate.dialogs.DlGateInventoryUpdate;
import org.fhwa.c2cri.tmdd.emulation.gate.dialogs.DlGateStatusRequest;
import org.fhwa.c2cri.tmdd.emulation.gate.dialogs.DlGateStatusUpdate;
import org.fhwa.c2cri.tmdd.emulation.generic.dialogs.DlDeviceInformationSubscription;

/**
 * The GATEHandler class coordinates all functions related to GATE entities.
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class GateHandler implements EntityControlRequestStatusCollector, EntityCommandProcessor {

    /**
     * The inventory of GATE devices
     */
    private GateInventory gateInventory = new GateInventory(EntityEmulationData.EntityDataType.GATEINVENTORY);
    /**
     * The status of all defined GATE devices
     */
    private GateStatus gateStatus = new GateStatus(EntityEmulationData.EntityDataType.GATESTATUS);
    /**
     * The control schedule for all Gates
     */
    private GateControlSchedule gateControlSchedule = new GateControlSchedule(EntityEmulationData.EntityDataType.GATECONTROLSCHEDULE);
    /**
     * Provides organization information related to a specific entity.
     */
    private EntityDataOrganizationInformationCollector orgInfoDataCollector;

    private GateHandler() {
    }

    ;
    
    /**
     * Main constructor method for the GATEHandler class.
     * 
     * @param orgInfoDataCollector reference to an organization information collector.
     */
    public GateHandler(EntityDataOrganizationInformationCollector orgInfoDataCollector) {
        this.orgInfoDataCollector = orgInfoDataCollector;
    }

//  private EntityUpdateListeners (GATEStatus and GATEInventory)   
    /**
     * Initializes the data types related to a GATE device.
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
            case GATEINVENTORY:
                gateInventory.initialize(byteArray);
                break;
            case GATESTATUS:
                gateStatus.initialize(byteArray);
                break;
            case GATECONTROLSCHEDULE:
                gateControlSchedule.initialize(byteArray);
                break;
            default:
                throw new EntityEmulationException("The GateHandler is unable to initialize data type " + entityDataType);
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

        switch (dialog) {
            case "dlGateControlRequest":
                DlGateControlRequest dlGateControlRequestProcessor = new DlGateControlRequest();
                responseMsg = dlGateControlRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.GATECONTROLREQUESTMSG, TMDDEntityType.EntityType.GATES, GATEINVENTORY, gateInventory, this);
                break;
            case "dlGateInventoryRequest":
                DlGateInventoryRequest dlGateInventoryRequestProcessor = new DlGateInventoryRequest();
                responseMsg = dlGateInventoryRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.GATES, GATEINVENTORY, gateInventory, this);
                break;
            case "dlGateStatusRequest":
                DlGateStatusRequest dlGateStatusRequestProcessor = new DlGateStatusRequest();
                responseMsg = dlGateStatusRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.GATES, GATESTATUS, gateStatus, this);
                break;
            case "dlGateControlScheduleRequest":
                DlGateControlScheduleRequest dlGateControlScheduleRequestProcessor = new DlGateControlScheduleRequest();
                responseMsg = dlGateControlScheduleRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.GATES, GATECONTROLSCHEDULE, gateControlSchedule, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the GateHandler.");
                break;
        }

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

        switch (dialog) {
            case "dlDeviceInformationSubscription":
                String msgDeviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type"));
                switch (msgDeviceInformationType) {
                    case "1":
                    case "device inventory":
                        DlDeviceInformationSubscription gateInvDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = gateInvDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.GATES, GATEINVENTORY, gateInventory, this);
                        break;
                    case "2":
                    case "device status":
                        DlDeviceInformationSubscription gateStatusDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = gateStatusDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.GATES, GATESTATUS, gateStatus, this);
                        break;
                    case "3":
                    case "device schedule":
                        DlDeviceInformationSubscription gateReportDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = gateReportDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.GATES, GATECONTROLSCHEDULE, gateControlSchedule, this);
                        break;

                    default:
                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlDeviceInformationSubscription dialog.");
                }
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the GateHandler.");
                break;
        }

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

        switch (dialog) {
            case "dlGateInventoryUpdate":
                DlGateInventoryUpdate dlGateInventoryUpdateProcessor = new DlGateInventoryUpdate();
                responseMsg = dlGateInventoryUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.GATES, GATEINVENTORY, gateInventory, this);
                break;
            case "dlGateStatusUpdate":
                DlGateStatusUpdate dlGateStatusUpdateProcessor = new DlGateStatusUpdate();
                responseMsg = dlGateStatusUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.GATES, GATESTATUS, gateStatus, this);
                break;
            case "dlGateControlScheduleUpdate":
                DlGateControlScheduleUpdate dlGateControlScheduleUpdateProcessor = new DlGateControlScheduleUpdate();
                responseMsg = dlGateControlScheduleUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.GATES, GATECONTROLSCHEDULE, gateControlSchedule, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the GateHandler.");
                break;
        }

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
        ValueInSetFilter deviceIdFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.GATEINVENTORY, "EntityId", deviceIdFilterValues);
        theFilters.add(deviceIdFilter);

        ArrayList<EntityDataRecord> theResponse = gateInventory.getEntityElements(theFilters);

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
     * Each GATE-Inventory-Item is assigned an item index. This index is used to
     * indicate the relative order of the GATE Inventory items.
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
                case GATEINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = gateInventory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{GATESTATUS,GATECONTROLSCHEDULE});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlGateInventoryUpdate");
                    if (updatedTypes.contains(GATESTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateStatusUpdate");
                    if (updatedTypes.contains(GATECONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateControlScheduleUpdate");                        
                    break;
                case GATESTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = gateStatus.addEntity(entityId, new EntityEmulationData.EntityDataType[]{GATEINVENTORY,GATECONTROLSCHEDULE});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlGateStatusUpdate");
                    if (statusUpdatedTypes.contains(GATEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateInventoryUpdate");
                    if (statusUpdatedTypes.contains(GATECONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateControlScheduleUpdate");                        
                    break;
                case GATECONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = gateControlSchedule.addEntity(entityId, new EntityEmulationData.EntityDataType[]{GATESTATUS,GATEINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlGateControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(GATESTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateStatusUpdate");
                    if (controlUpdatedTypes.contains(GATEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateInventoryUpdate");                        
                    break;
                default:
                    throw new EntityEmulationException("The GateHandler is unable to process data type " + entityDataType);
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
                case GATEINVENTORY:
                    gateInventory.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlGateInventoryUpdate");
                    break;
                case GATESTATUS:
                    gateStatus.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlGateStatusUpdate");
                    break;
                case GATECONTROLSCHEDULE:
                    gateControlSchedule.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlGateControlScheduleUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The GateHandler is unable to process data type " + entityDataType);
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
                case GATEINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = gateInventory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{GATESTATUS,GATECONTROLSCHEDULE});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlGateInventoryUpdate");
                    if (updatedTypes.contains(GATESTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateStatusUpdate");
                    if (updatedTypes.contains(GATECONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateControlScheduleUpdate");                        
                    break;
                case GATESTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = gateStatus.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{GATEINVENTORY,GATECONTROLSCHEDULE});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlGateStatusUpdate");
                    if (statusUpdatedTypes.contains(GATEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateInventoryUpdate");
                    if (statusUpdatedTypes.contains(GATECONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateControlScheduleUpdate");                        
                    break;
                case GATECONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = gateControlSchedule.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{GATESTATUS,GATEINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlGateControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(GATESTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateStatusUpdate");
                    if (controlUpdatedTypes.contains(GATEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateInventoryUpdate");   
                    break;
                default:
                    throw new EntityEmulationException("The GateHandler is unable to process data type " + entityDataType);
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
                case GATEINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = gateInventory.updateEntityElement(new EntityEmulationData.EntityDataType[]{GATESTATUS,GATECONTROLSCHEDULE}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlGateInventoryUpdate");
                    if (updatedTypes.contains(GATESTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateStatusUpdate");
                    if (updatedTypes.contains(GATECONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateControlScheduleUpdate");                        
                    break;
                case GATESTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = gateStatus.updateEntityElement(new EntityEmulationData.EntityDataType[]{GATEINVENTORY,GATECONTROLSCHEDULE}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlGateStatusUpdate");
                    if (statusUpdatedTypes.contains(GATEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateInventoryUpdate");
                    if (statusUpdatedTypes.contains(GATECONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateControlScheduleUpdate");                        
                    break;
                case GATECONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = gateControlSchedule.updateEntityElement(new EntityEmulationData.EntityDataType[]{GATESTATUS,GATEINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlGateControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(GATESTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateStatusUpdate");
                    if (controlUpdatedTypes.contains(GATEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlGateInventoryUpdate");   
                    break;
                default:
                    throw new EntityEmulationException("The GateHandler is unable to process data type " + entityDataType);
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
                case GATEINVENTORY:
                    gateInventory.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlGateInventoryUpdate");
                    break;
                case GATESTATUS:
                    gateStatus.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlGateStatusUpdate");
                    break;
                case GATECONTROLSCHEDULE:
                    gateControlSchedule.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlGateControlScheduleUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The GateHandler is unable to process data type " + entityDataType);
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
                case GATEINVENTORY:
                    entityValue = gateInventory.getEntityElementValue(entityId, entityElement);
                    break;
                case GATESTATUS:
                    entityValue = gateStatus.getEntityElementValue(entityId, entityElement);
                    break;
                case GATECONTROLSCHEDULE:
                    entityValue = gateControlSchedule.getEntityElementValue(entityId, entityElement);
                    break;
                default:
                    throw new EntityEmulationException("The GateHandler is unable to process data type " + entityDataType);
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
    }

    @Override
    public void executeCommand(MessageSpecification commandMessage) throws EntityEmulationException {
        int lockStatus = 0;
        String operatorLockId = "";

        // retrieve the key elements from the request Message
        // get the org Id from the message
        String orgId = commandMessage.getElementValue("tmdd:gateControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:gateControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:gateControlRequestMsg.device-control-request-header.request-id");

        // Retrieve the desired Gate Request Command
        String gateRequestCommand = commandMessage.getElementValue("tmdd:gateControlRequestMsg.gate-request-command");

        String operatorId = commandMessage.getElementValue("tmdd:gateControlRequestMsg.device-control-request-header.authentication.operator-id");

        // Set the command control status 
        String gateCommandControlStatus = "other";        

        try {
            switch (gateRequestCommand) {
                case "1":
                case "open gate":
                    updateEntityElement(GATESTATUS, entityId, "gate-status", "gate open");
                    break;
                case "2":
                case "close gate":
                    updateEntityElement(GATESTATUS, entityId, "gate-status", "gate closed");
                    break;
                case "3":
                case "other":
                default:
                    break;
            }        
           gateCommandControlStatus = "requested changes completed";
        } catch (InvalidValueException ex) {
            gateCommandControlStatus = "request rejected invalid command parameters";
        } catch (EntityEmulationException ex){
            gateCommandControlStatus = "other";            
        }
        
        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.GATES.name());
        statusRecord.setStatus(gateCommandControlStatus);
        statusRecord.setLockStatus(lockStatus);
        statusRecord.setOperatorId(operatorId);
        statusRecord.setOperatorLockId(operatorLockId);
        statusRecord.updateDate();

        // Update the ControlRequestStatus record to reflect the current command status.
        EntityCommandQueue.getInstance().updateControlRequestStatus(statusRecord, commandMessage, false);
    }

    @Override
    public void queueCommand(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, InvalidEntityControlRequestLogException, EntityEmulationException {
        int lockStatus = 0;
        String operatorLockId = "";

        String orgId = commandMessage.getElementValue("tmdd:gateControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:gateControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:gateControlRequestMsg.device-control-request-header.request-id");

        String operatorId = commandMessage.getElementValue("tmdd:gateControlRequestMsg.device-control-request-header.authentication.operator-id");

        String priority = commandMessage.getElementValue("tmdd:gateControlRequestMsg.device-control-request-header.command-request-priority");

        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.GATES.name());
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

        String orgId = commandMessage.getElementValue("tmdd:gateControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:gateControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:gateControlRequestMsg.device-control-request-header.request-id");

        // get the matching control Request Status record
        EntityControlRequestStatusRecord statusRecord = EntityCommandQueue.getInstance().getControlRequestStatus(orgId, entityId, requestId);
        TMDDEntityType.EntityType entityType = TMDDEntityType.EntityType.valueOf(statusRecord.getEntityType());

        return TMDDMessageProcessor.createControlResponseMessage(entityType, statusRecord, orgInfoDataCollector);

    }
  
}
