/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.har;

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
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.HARCONTROLSCHEDULE;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.HARINVENTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.HARMESSAGEINVENTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.HARSTATUS;
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
import org.fhwa.c2cri.tmdd.emulation.generic.dialogs.DlDeviceInformationSubscription;
import org.fhwa.c2cri.tmdd.emulation.har.dialogs.DlHARControlRequest;
import org.fhwa.c2cri.tmdd.emulation.har.dialogs.DlHARControlScheduleRequest;
import org.fhwa.c2cri.tmdd.emulation.har.dialogs.DlHARControlScheduleUpdate;
import org.fhwa.c2cri.tmdd.emulation.har.dialogs.DlHARInventoryRequest;
import org.fhwa.c2cri.tmdd.emulation.har.dialogs.DlHARInventoryUpdate;
import org.fhwa.c2cri.tmdd.emulation.har.dialogs.DlHARMessageInventoryRequest;
import org.fhwa.c2cri.tmdd.emulation.har.dialogs.DlHARMessageInventoryUpdate;
import org.fhwa.c2cri.tmdd.emulation.har.dialogs.DlHARPriorityQueueRequest;
import org.fhwa.c2cri.tmdd.emulation.har.dialogs.DlHARStatusRequest;
import org.fhwa.c2cri.tmdd.emulation.har.dialogs.DlHARStatusUpdate;

/**
 * The HARHandler class coordinates all functions related to HAR entities.
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class HARHandler implements EntityControlRequestStatusCollector, EntityCommandProcessor {

    /**
     * The inventory of HAR devices
     */
    private HARInventory harInventory = new HARInventory(EntityEmulationData.EntityDataType.HARINVENTORY);
    /**
     * The status of all defined HAR devices
     */
    private HARStatus harStatus = new HARStatus(EntityEmulationData.EntityDataType.HARSTATUS);
    /**
     * The appearance for Messages on HAR devices
     */
    private HARControlSchedule harControlSchedule = new HARControlSchedule(EntityEmulationData.EntityDataType.HARCONTROLSCHEDULE);
    /**
     * The inventory of messages in HAR devices
     */
    private HARMessageInventory harMessageInventory = new HARMessageInventory(EntityEmulationData.EntityDataType.HARMESSAGEINVENTORY);
    /**
     * Provides organization information related to a specific entity.
     */
    private EntityDataOrganizationInformationCollector orgInfoDataCollector;

    private HARHandler() {
    }

    ;
    
    /**
     * Main constructor method for the HARHandler class.
     * 
     * @param orgInfoDataCollector reference to an organization information collector.
     */
    public HARHandler(EntityDataOrganizationInformationCollector orgInfoDataCollector) {
        this.orgInfoDataCollector = orgInfoDataCollector;
    }

//  private EntityUpdateListeners (HARStatus and HARInventory)   
    /**
     * Initializes the data types related to a HAR device.
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
            case HARINVENTORY:
                harInventory.initialize(byteArray);
                break;
            case HARSTATUS:
                harStatus.initialize(byteArray);
                break;
            case HARCONTROLSCHEDULE:
                harControlSchedule.initialize(byteArray);
                break;
            case HARMESSAGEINVENTORY:
                harMessageInventory.initialize(byteArray);
                break;
            default:
                throw new EntityEmulationException("The HARHandler is unable to initialize data type " + entityDataType);
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
            case "dlHARControlRequest":
                DlHARControlRequest dlHARControlRequestProcessor = new DlHARControlRequest();
                responseMsg = dlHARControlRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.HARCONTROLREQUESTMSG, TMDDEntityType.EntityType.HAR, HARINVENTORY, harInventory, this);
                break;
            case "dlHARInventoryRequest":
                DlHARInventoryRequest dlHARInventoryRequestProcessor = new DlHARInventoryRequest();
                responseMsg = dlHARInventoryRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.HAR, HARINVENTORY, harInventory, this);
                break;
            case "dlHARStatusRequest":
                DlHARStatusRequest dlHARStatusRequestProcessor = new DlHARStatusRequest();
                responseMsg = dlHARStatusRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.HAR, HARSTATUS, harStatus, this);
                break;
            case "dlHARMessageInventoryRequest":
                DlHARMessageInventoryRequest dlHARMessageInventoryRequestProcessor = new DlHARMessageInventoryRequest();
                responseMsg = dlHARMessageInventoryRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.HAR, HARMESSAGEINVENTORY, harMessageInventory, this);
                break;
            case "dlHARControlScheduleRequest":
                DlHARControlScheduleRequest dlHARControlScheduleRequestProcessor = new DlHARControlScheduleRequest();
                responseMsg = dlHARControlScheduleRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.HAR, HARCONTROLSCHEDULE, harControlSchedule, this);
                break;
            case "dlHARPriorityQueueRequest":
                DlHARPriorityQueueRequest dlHARPriorityQueueRequestProcessor = new DlHARPriorityQueueRequest();
                responseMsg = dlHARPriorityQueueRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEPRIORITYQUEUEREQUESTMSG, TMDDEntityType.EntityType.HAR, HARINVENTORY, harInventory, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the HARHandler.");
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
                        DlDeviceInformationSubscription harInvDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = harInvDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.HAR, HARINVENTORY, harInventory, this);
                        break;
                    case "2":
                    case "device status":
                        DlDeviceInformationSubscription harStatusDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = harStatusDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.HAR, HARSTATUS, harStatus, this);
                        break;
                    case "3":
                    case "device schedule":
                        DlDeviceInformationSubscription harReportDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = harReportDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.HAR, HARCONTROLSCHEDULE, harControlSchedule, this);
                        break;
                    case "4":
                    case "device plan":
                        DlDeviceInformationSubscription harMetadataDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = harMetadataDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.HAR, HARMESSAGEINVENTORY, harMessageInventory, this);
                        break;
                    default:
                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlDeviceInformationSubscription dialog.");
                }
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the HARHandler.");
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
            case "dlHARInventoryUpdate":
                DlHARInventoryUpdate dlHARInventoryUpdateProcessor = new DlHARInventoryUpdate();
                responseMsg = dlHARInventoryUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.HAR, HARINVENTORY, harInventory, this);
                break;
            case "dlHARStatusUpdate":
                DlHARStatusUpdate dlHARStatusUpdateProcessor = new DlHARStatusUpdate();
                responseMsg = dlHARStatusUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.HAR, HARSTATUS, harStatus, this);
                break;
            case "dlHARMessageInventoryUpdate":
                DlHARMessageInventoryUpdate dlHARMessageInventoryUpdateProcessor = new DlHARMessageInventoryUpdate();
                responseMsg = dlHARMessageInventoryUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.HAR, HARMESSAGEINVENTORY, harMessageInventory, this);
                break;
            case "dlHARControlScheduleUpdate":
                DlHARControlScheduleUpdate dlHARControlScheduleUpdateProcessor = new DlHARControlScheduleUpdate();
                responseMsg = dlHARControlScheduleUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.HAR, HARCONTROLSCHEDULE, harControlSchedule, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the HARHandler.");
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
        ValueInSetFilter deviceIdFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.HARINVENTORY, "EntityId", deviceIdFilterValues);
        theFilters.add(deviceIdFilter);

        ArrayList<EntityDataRecord> theResponse = harInventory.getEntityElements(theFilters);

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
     * Each HAR-Inventory-Item is assigned an item index. This index is used to
     * indicate the relative order of the HAR Inventory items.
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
                case HARINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = harInventory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{HARSTATUS,HARCONTROLSCHEDULE,HARMESSAGEINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARInventoryUpdate");
                    if (updatedTypes.contains(HARSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARStatusUpdate");
                    if (updatedTypes.contains(HARCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARControlScheduleUpdate");                        
                    if (updatedTypes.contains(HARMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARMessageInventoryUpdate");                        
                    break;
                case HARSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = harStatus.addEntity(entityId, new EntityEmulationData.EntityDataType[]{HARINVENTORY,HARCONTROLSCHEDULE,HARMESSAGEINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARStatusUpdate");
                    if (statusUpdatedTypes.contains(HARINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARInventoryUpdate");
                    if (statusUpdatedTypes.contains(HARCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARControlScheduleUpdate");                        
                    if (statusUpdatedTypes.contains(HARMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARMessageInventoryUpdate");                      
                    break;
                case HARCONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = harControlSchedule.addEntity(entityId, new EntityEmulationData.EntityDataType[]{HARSTATUS,HARINVENTORY,HARMESSAGEINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(HARSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARStatusUpdate");
                    if (controlUpdatedTypes.contains(HARINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARInventoryUpdate");                        
                    if (controlUpdatedTypes.contains(HARMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARMessageInventoryUpdate");                      
                    break;
                case HARMESSAGEINVENTORY:
                    List<EntityEmulationData.EntityDataType> msgUpdatedTypes = harMessageInventory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{HARSTATUS,HARCONTROLSCHEDULE,HARINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARMessageInventoryUpdate");
                    if (msgUpdatedTypes.contains(HARSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARStatusUpdate");
                    if (msgUpdatedTypes.contains(HARCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARControlScheduleUpdate");                        
                    if (msgUpdatedTypes.contains(HARINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARInventoryUpdate");                      
                    break;
                default:
                    throw new EntityEmulationException("The HARHandler is unable to process data type " + entityDataType);
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
                case HARINVENTORY:
                    harInventory.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARInventoryUpdate");
                    break;
                case HARSTATUS:
                    harStatus.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARStatusUpdate");
                    break;
                case HARCONTROLSCHEDULE:
                    harControlSchedule.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARControlScheduleUpdate");
                    break;
                case HARMESSAGEINVENTORY:
                    harMessageInventory.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARMessageInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The HARHandler is unable to process data type " + entityDataType);
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
                case HARINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = harInventory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{HARSTATUS,HARCONTROLSCHEDULE,HARMESSAGEINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARInventoryUpdate");
                    if (updatedTypes.contains(HARSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARStatusUpdate");
                    if (updatedTypes.contains(HARCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARControlScheduleUpdate");                        
                    if (updatedTypes.contains(HARMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARMessageInventoryUpdate");                        
                    break;
                case HARSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = harStatus.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{HARINVENTORY,HARCONTROLSCHEDULE,HARMESSAGEINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARStatusUpdate");
                    if (statusUpdatedTypes.contains(HARINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARInventoryUpdate");
                    if (statusUpdatedTypes.contains(HARCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARControlScheduleUpdate");                        
                    if (statusUpdatedTypes.contains(HARMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARMessageInventoryUpdate");                      
                    break;
                case HARCONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = harControlSchedule.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{HARSTATUS,HARINVENTORY,HARMESSAGEINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(HARSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARStatusUpdate");
                    if (controlUpdatedTypes.contains(HARINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARInventoryUpdate");                        
                    if (controlUpdatedTypes.contains(HARMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARMessageInventoryUpdate");                      
                    break;
                case HARMESSAGEINVENTORY:
                    List<EntityEmulationData.EntityDataType> msgUpdatedTypes = harMessageInventory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{HARSTATUS,HARCONTROLSCHEDULE,HARINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARMessageInventoryUpdate");
                    if (msgUpdatedTypes.contains(HARSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARStatusUpdate");
                    if (msgUpdatedTypes.contains(HARCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARControlScheduleUpdate");                        
                    if (msgUpdatedTypes.contains(HARINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARInventoryUpdate");                      
                    break;
                default:
                    throw new EntityEmulationException("The HARHandler is unable to process data type " + entityDataType);
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
                case HARINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = harInventory.updateEntityElement(new EntityEmulationData.EntityDataType[]{HARSTATUS,HARCONTROLSCHEDULE,HARMESSAGEINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARInventoryUpdate");
                    if (updatedTypes.contains(HARSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARStatusUpdate");
                    if (updatedTypes.contains(HARCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARControlScheduleUpdate");                        
                    if (updatedTypes.contains(HARMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARMessageInventoryUpdate");                        
                    break;
                case HARSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = harStatus.updateEntityElement(new EntityEmulationData.EntityDataType[]{HARINVENTORY,HARCONTROLSCHEDULE,HARMESSAGEINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARStatusUpdate");
                    if (statusUpdatedTypes.contains(HARINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARInventoryUpdate");
                    if (statusUpdatedTypes.contains(HARCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARControlScheduleUpdate");                        
                    if (statusUpdatedTypes.contains(HARMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARMessageInventoryUpdate");                      
                    break;
                case HARCONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = harControlSchedule.updateEntityElement(new EntityEmulationData.EntityDataType[]{HARSTATUS,HARINVENTORY,HARMESSAGEINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(HARSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARStatusUpdate");
                    if (controlUpdatedTypes.contains(HARINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARInventoryUpdate");                        
                    if (controlUpdatedTypes.contains(HARMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARMessageInventoryUpdate");                      
                    break;
                case HARMESSAGEINVENTORY:
                    List<EntityEmulationData.EntityDataType> msgUpdatedTypes = harMessageInventory.updateEntityElement(new EntityEmulationData.EntityDataType[]{HARSTATUS,HARCONTROLSCHEDULE,HARINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARMessageInventoryUpdate");
                    if (msgUpdatedTypes.contains(HARSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARStatusUpdate");
                    if (msgUpdatedTypes.contains(HARCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARControlScheduleUpdate");                        
                    if (msgUpdatedTypes.contains(HARINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlHARInventoryUpdate");                      
                    break;
                default:
                    throw new EntityEmulationException("The HARHandler is unable to process data type " + entityDataType);
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
                case HARINVENTORY:
                    harInventory.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARInventoryUpdate");
                    break;
                case HARSTATUS:
                    harStatus.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARStatusUpdate");
                    break;
                case HARCONTROLSCHEDULE:
                    harControlSchedule.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARControlScheduleUpdate");
                    break;
                case HARMESSAGEINVENTORY:
                    harMessageInventory.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlHARMessageInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The HARHandler is unable to process data type " + entityDataType);
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
                case HARINVENTORY:
                    entityValue = harInventory.getEntityElementValue(entityId, entityElement);
                    break;
                case HARSTATUS:
                    entityValue = harStatus.getEntityElementValue(entityId, entityElement);
                    break;
                case HARCONTROLSCHEDULE:
                    entityValue = harControlSchedule.getEntityElementValue(entityId, entityElement);
                    break;
                case HARMESSAGEINVENTORY:
                    entityValue = harMessageInventory.getEntityElementValue(entityId, entityElement);
                    break;
                default:
                    throw new EntityEmulationException("The HARHandler is unable to process data type " + entityDataType);
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
        String orgId = commandMessage.getElementValue("tmdd:hARControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:hARControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:hARControlRequestMsg.device-control-request-header.request-id");

        // Retrieve the desired HAR Request Command
        String harRequestCommand = commandMessage.getElementValue("tmdd:hARControlRequestMsg.har-request-command");
        
        // Select the paramaters associated with the requested command
        String harRequestParameter = "";

        // Set the command control status 
        String harCommandControlStatus = "other";        

        try {
            switch (harRequestCommand) {
                case "1":
                case "put up custom message":
                    harRequestParameter = commandMessage.getElementValue("tmdd:hARControlRequestMsg.har-command-parameters.har-message");
                    updateEntityElement(HARSTATUS, entityId, "current-message", harRequestParameter);
                    break;
                case "2":
                case "put up a library message":
                    harRequestParameter = commandMessage.getElementValue("tmdd:hARControlRequestMsg.har-command-parameters.message-number");
                    updateEntityElement(HARSTATUS, entityId, "message-number", harRequestParameter);
                    break;
                case "3":
                case "remove message":
                    harRequestParameter = commandMessage.getElementValue("tmdd:hARControlRequestMsg.har-command-parameters.har-message");
                    updateEntityElement(HARSTATUS, entityId, "current-message", ".");
                    break;
                case "4":
                case "other":
                    harRequestParameter = commandMessage.getElementValue("tmdd:hARControlRequestMsg.har-command-parameters.har-message");
					break;
                default:
                    break;
            }
            
            // Process the HAR Beacon Control List
            String harBeaconControl1 = commandMessage.getElementValue("tmdd:hARControlRequestMsg.tmddX:hARControlRequestExt.har-beacon-control-list.har-beacon-control");
            if (!harBeaconControl1.isEmpty())updateEntityElement(HARSTATUS, entityId, "message-beacon", harBeaconControl1);      
            String harBeaconControl1_1 = commandMessage.getElementValue("tmdd:hARControlRequestMsg.tmddX:hARControlRequestExt.har-beacon-control-list[1].har-beacon-control");
            if (!harBeaconControl1_1.isEmpty())updateEntityElement(HARSTATUS, entityId, "message-beacon", harBeaconControl1_1);      
            String harBeaconControl2 = commandMessage.getElementValue("tmdd:hARControlRequestMsg.tmddX:hARControlRequestExt.har-beacon-control-list[2].har-beacon-control");
            if (!harBeaconControl2.isEmpty())updateEntityElement(HARSTATUS, entityId, "tmddX:hARStatusExt.message-beacon2", harBeaconControl2);
            String harBeaconControl3 = commandMessage.getElementValue("tmdd:hARControlRequestMsg.tmddX:hARControlRequestExt.har-beacon-control-list[3].har-beacon-control");
            if (!harBeaconControl3.isEmpty())updateEntityElement(HARSTATUS, entityId, "tmddX:hARStatusExt.message-beacon3", harBeaconControl3);
            String harBeaconControl4 = commandMessage.getElementValue("tmdd:hARControlRequestMsg.tmddX:hARControlRequestExt.har-beacon-control-list[4].har-beacon-control");
            if (!harBeaconControl4.isEmpty())updateEntityElement(HARSTATUS, entityId, "tmddX:hARStatusExt.message-beacon4", harBeaconControl4);
            String harBeaconControl5 = commandMessage.getElementValue("tmdd:hARControlRequestMsg.tmddX:hARControlRequestExt.har-beacon-control-list[5].har-beacon-control");
            if (!harBeaconControl5.isEmpty())updateEntityElement(HARSTATUS, entityId, "tmddX:hARStatusExt.message-beacon5", harBeaconControl5);
            String harBeaconControl6 = commandMessage.getElementValue("tmdd:hARControlRequestMsg.tmddX:hARControlRequestExt.har-beacon-control-list[6].har-beacon-control");
            if (!harBeaconControl6.isEmpty())updateEntityElement(HARSTATUS, entityId, "tmddX:hARStatusExt.message-beacon6", harBeaconControl6);
            String harBeaconControl7 = commandMessage.getElementValue("tmdd:hARControlRequestMsg.tmddX:hARControlRequestExt.har-beacon-control-list[7].har-beacon-control");
            if (!harBeaconControl7.isEmpty())updateEntityElement(HARSTATUS, entityId, "tmddX:hARStatusExt.message-beacon7", harBeaconControl7);
            String harBeaconControl8 = commandMessage.getElementValue("tmdd:hARControlRequestMsg.tmddX:hARControlRequestExt.har-beacon-control-list[8].har-beacon-control");
            if (!harBeaconControl8.isEmpty())updateEntityElement(HARSTATUS, entityId, "tmddX:hARStatusExt.message-beacon8", harBeaconControl8);

            
            
            
            harCommandControlStatus = "requested changes completed";
        } catch (InvalidValueException ex) {
            harCommandControlStatus = "request rejected invalid command parameters";
        } catch (EntityEmulationException ex){
            harCommandControlStatus = "other";            
        }

        
        String operatorId = commandMessage.getElementValue("tmdd:hARControlRequestMsg.device-control-request-header.authentication.operator-id");

        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.HAR.name());
        statusRecord.setStatus(harCommandControlStatus);
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

        String orgId = commandMessage.getElementValue("tmdd:hARControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:hARControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:hARControlRequestMsg.device-control-request-header.request-id");

        String operatorId = commandMessage.getElementValue("tmdd:hARControlRequestMsg.device-control-request-header.authentication.operator-id");

        String priority = commandMessage.getElementValue("tmdd:hARControlRequestMsg.device-control-request-header.command-request-priority");
        
        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.HAR.name());
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

        String orgId = commandMessage.getElementValue("tmdd:hARControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:hARControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:hARControlRequestMsg.device-control-request-header.request-id");

        // get the matching control Request Status record
        EntityControlRequestStatusRecord statusRecord = EntityCommandQueue.getInstance().getControlRequestStatus(orgId, entityId, requestId);
        TMDDEntityType.EntityType entityType = TMDDEntityType.EntityType.valueOf(statusRecord.getEntityType());

        return TMDDMessageProcessor.createControlResponseMessage(entityType, statusRecord, orgInfoDataCollector);

    }
    
}
