/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.lcs;

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
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.LCSCONTROLSCHEDULE;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.LCSINVENTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.LCSSTATUS;
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
import org.fhwa.c2cri.tmdd.emulation.lcs.dialogs.DlLCSControlRequest;
import org.fhwa.c2cri.tmdd.emulation.lcs.dialogs.DlLCSControlScheduleRequest;
import org.fhwa.c2cri.tmdd.emulation.lcs.dialogs.DlLCSControlScheduleUpdate;
import org.fhwa.c2cri.tmdd.emulation.lcs.dialogs.DlLCSInventoryRequest;
import org.fhwa.c2cri.tmdd.emulation.lcs.dialogs.DlLCSInventoryUpdate;
import org.fhwa.c2cri.tmdd.emulation.lcs.dialogs.DlLCSStatusRequest;
import org.fhwa.c2cri.tmdd.emulation.lcs.dialogs.DlLCSStatusUpdate;

/**
 * The LCSHandler class coordinates all functions related to LCS entities.
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class LCSHandler implements EntityControlRequestStatusCollector, EntityCommandProcessor {

    /**
     * The inventory of LCS devices
     */
    private LCSInventory lcsInventory = new LCSInventory(EntityEmulationData.EntityDataType.LCSINVENTORY);
    /**
     * The status of all defined LCS devices
     */
    private LCSStatus lcsStatus = new LCSStatus(EntityEmulationData.EntityDataType.LCSSTATUS);
    /**
     * The control schedule for all LCSs
     */
    private LCSControlSchedule lcsControlSchedule = new LCSControlSchedule(EntityEmulationData.EntityDataType.LCSCONTROLSCHEDULE);
    /**
     * Provides organization information related to a specific entity.
     */
    private EntityDataOrganizationInformationCollector orgInfoDataCollector;

    private LCSHandler() {
    }

    ;
    
    /**
     * Main constructor method for the LCSHandler class.
     * 
     * @param orgInfoDataCollector reference to an organization information collector.
     */
    public LCSHandler(EntityDataOrganizationInformationCollector orgInfoDataCollector) {
        this.orgInfoDataCollector = orgInfoDataCollector;
    }

//  private EntityUpdateListeners (LCSStatus and LCSInventory)   
    /**
     * Initializes the data types related to a LCS device.
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
            case LCSINVENTORY:
                lcsInventory.initialize(byteArray);
                break;
            case LCSSTATUS:
                lcsStatus.initialize(byteArray);
                break;
            case LCSCONTROLSCHEDULE:
                lcsControlSchedule.initialize(byteArray);
                break;
            default:
                throw new EntityEmulationException("The LCSHandler is unable to initialize data type " + entityDataType);
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
            case "dlLCSControlRequest":
                DlLCSControlRequest dlLCSControlRequestProcessor = new DlLCSControlRequest();
                responseMsg = dlLCSControlRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.LCSCONTROLREQUESTMSG, TMDDEntityType.EntityType.LCS, LCSINVENTORY, lcsInventory, this);
                break;
            case "dlLCSInventoryRequest":
                DlLCSInventoryRequest dlLCSInventoryRequestProcessor = new DlLCSInventoryRequest();
                responseMsg = dlLCSInventoryRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.LCS, LCSINVENTORY, lcsInventory, this);
                break;
            case "dlLCSStatusRequest":
                DlLCSStatusRequest dlLCSStatusRequestProcessor = new DlLCSStatusRequest();
                responseMsg = dlLCSStatusRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.LCS, LCSSTATUS, lcsStatus, this);
                break;
            case "dlLCSControlScheduleRequest":
                DlLCSControlScheduleRequest dlLCSControlScheduleRequestProcessor = new DlLCSControlScheduleRequest();
                responseMsg = dlLCSControlScheduleRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.LCS, LCSCONTROLSCHEDULE, lcsControlSchedule, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the LCSHandler.");
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
                        DlDeviceInformationSubscription lcsInvDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = lcsInvDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.LCS, LCSINVENTORY, lcsInventory, this);
                        break;
                    case "2":
                    case "device status":
                        DlDeviceInformationSubscription lcsStatusDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = lcsStatusDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.LCS, LCSSTATUS, lcsStatus, this);
                        break;
                    case "3":
                    case "device schedule":
                        DlDeviceInformationSubscription lcsReportDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = lcsReportDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.LCS, LCSCONTROLSCHEDULE, lcsControlSchedule, this);
                        break;
                    default:
                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlDeviceInformationSubscription dialog.");
                }
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the LCSHandler.");
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
            case "dlLCSInventoryUpdate":
                DlLCSInventoryUpdate dlLCSInventoryUpdateProcessor = new DlLCSInventoryUpdate();
                responseMsg = dlLCSInventoryUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.LCS, LCSINVENTORY, lcsInventory, this);
                break;
            case "dlLCSStatusUpdate":
                DlLCSStatusUpdate dlLCSStatusUpdateProcessor = new DlLCSStatusUpdate();
                responseMsg = dlLCSStatusUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.LCS, LCSSTATUS, lcsStatus, this);
                break;
            case "dlLCSControlScheduleUpdate":
                DlLCSControlScheduleUpdate dlLCSControlScheduleUpdateProcessor = new DlLCSControlScheduleUpdate();
                responseMsg = dlLCSControlScheduleUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.LCS, LCSCONTROLSCHEDULE, lcsControlSchedule, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the LCSHandler.");
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
        ValueInSetFilter deviceIdFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.LCSINVENTORY, "EntityId", deviceIdFilterValues);
        theFilters.add(deviceIdFilter);

        ArrayList<EntityDataRecord> theResponse = lcsInventory.getEntityElements(theFilters);

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
     * Each LCS-Inventory-Item is assigned an item index. This index is used to
     * indicate the relative order of the LCS Inventory items.
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
                case LCSINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = lcsInventory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{LCSSTATUS,LCSCONTROLSCHEDULE});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSInventoryUpdate");
                    if (updatedTypes.contains(LCSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSStatusUpdate");
                    if (updatedTypes.contains(LCSCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSControlScheduleUpdate");                        
                    break;
                case LCSSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = lcsStatus.addEntity(entityId, new EntityEmulationData.EntityDataType[]{LCSINVENTORY,LCSCONTROLSCHEDULE});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSStatusUpdate");
                    if (statusUpdatedTypes.contains(LCSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSInventoryUpdate");
                    if (statusUpdatedTypes.contains(LCSCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSControlScheduleUpdate");                        
                    break;
                case LCSCONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = lcsControlSchedule.addEntity(entityId, new EntityEmulationData.EntityDataType[]{LCSSTATUS,LCSINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(LCSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSStatusUpdate");
                    if (controlUpdatedTypes.contains(LCSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSInventoryUpdate");                        
                    break;
                default:
                    throw new EntityEmulationException("The LCSHandler is unable to process data type " + entityDataType);
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
                case LCSINVENTORY:
                    lcsInventory.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSInventoryUpdate");
                    break;
                case LCSSTATUS:
                    lcsStatus.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSStatusUpdate");
                    break;
                case LCSCONTROLSCHEDULE:
                    lcsControlSchedule.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSControlScheduleUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The LCSHandler is unable to process data type " + entityDataType);
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
                case LCSINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = lcsInventory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{LCSSTATUS,LCSCONTROLSCHEDULE});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSInventoryUpdate");
                    if (updatedTypes.contains(LCSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSStatusUpdate");
                    if (updatedTypes.contains(LCSCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSControlScheduleUpdate");                        
                    break;
                case LCSSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = lcsStatus.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{LCSINVENTORY,LCSCONTROLSCHEDULE});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSStatusUpdate");
                    if (statusUpdatedTypes.contains(LCSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSInventoryUpdate");
                    if (statusUpdatedTypes.contains(LCSCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSControlScheduleUpdate");                        
                    break;
                case LCSCONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = lcsControlSchedule.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{LCSSTATUS,LCSINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(LCSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSStatusUpdate");
                    if (controlUpdatedTypes.contains(LCSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSInventoryUpdate");                        
                    break;
                default:
                    throw new EntityEmulationException("The LCSHandler is unable to process data type " + entityDataType);
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
                case LCSINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = lcsInventory.updateEntityElement(new EntityEmulationData.EntityDataType[]{LCSSTATUS,LCSCONTROLSCHEDULE}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSInventoryUpdate");
                    if (updatedTypes.contains(LCSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSStatusUpdate");
                    if (updatedTypes.contains(LCSCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSControlScheduleUpdate");                        
                    break;
                case LCSSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = lcsStatus.updateEntityElement(new EntityEmulationData.EntityDataType[]{LCSINVENTORY,LCSCONTROLSCHEDULE}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSStatusUpdate");
                    if (statusUpdatedTypes.contains(LCSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSInventoryUpdate");
                    if (statusUpdatedTypes.contains(LCSCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSControlScheduleUpdate");                        
                    break;
                case LCSCONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = lcsControlSchedule.updateEntityElement(new EntityEmulationData.EntityDataType[]{LCSSTATUS,LCSINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(LCSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSStatusUpdate");
                    if (controlUpdatedTypes.contains(LCSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSInventoryUpdate");                        
                   break;
                default:
                    throw new EntityEmulationException("The LCSHandler is unable to process data type " + entityDataType);
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
                case LCSINVENTORY:
                    lcsInventory.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSInventoryUpdate");
                    break;
                case LCSSTATUS:
                    lcsStatus.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSStatusUpdate");
                    break;
                case LCSCONTROLSCHEDULE:
                    lcsControlSchedule.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlLCSControlScheduleUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The LCSHandler is unable to process data type " + entityDataType);
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
                case LCSINVENTORY:
                    entityValue = lcsInventory.getEntityElementValue(entityId, entityElement);
                    break;
                case LCSSTATUS:
                    entityValue = lcsStatus.getEntityElementValue(entityId, entityElement);
                    break;
                case LCSCONTROLSCHEDULE:
                    entityValue = lcsControlSchedule.getEntityElementValue(entityId, entityElement);
                    break;
                default:
                    throw new EntityEmulationException("The LCSHandler is unable to process data type " + entityDataType);
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
        String orgId = commandMessage.getElementValue("tmdd:lCSControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:lCSControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:lCSControlRequestMsg.device-control-request-header.request-id");

        // Retrieve the desired LCS Request Command
        String lcsRequestCommand = commandMessage.getElementValue("tmdd:lCSControlRequestMsg.lcs-request-command");

        String operatorId = commandMessage.getElementValue("tmdd:lCSControlRequestMsg.device-control-request-header.authentication.operator-id");

        // Set the command control status 
        String lcsCommandControlStatus = "other";        

        try {
            switch (lcsRequestCommand) {
                case "1":
                case "open the lane":
                    updateEntityElement(LCSSTATUS, entityId, "lane-current-state", "open");
                    break;
                case "2":
                case "close the lane":
                    updateEntityElement(LCSSTATUS, entityId, "lane-current-state", "closed");
                    break;
                case "3":
                case "other":
                default:
                    break;
            }        
           lcsCommandControlStatus = "requested changes completed";
        } catch (InvalidValueException ex) {
            lcsCommandControlStatus = "request rejected invalid command parameters";
        } catch (EntityEmulationException ex){
            lcsCommandControlStatus = "other";            
        }     
        
        
        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.LCS.name());
        statusRecord.setStatus(lcsCommandControlStatus);
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

        String orgId = commandMessage.getElementValue("tmdd:lCSControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:lCSControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:lCSControlRequestMsg.device-control-request-header.request-id");

        String operatorId = commandMessage.getElementValue("tmdd:lCSControlRequestMsg.device-control-request-header.authentication.operator-id");

        String priority = commandMessage.getElementValue("tmdd:lCSControlRequestMsg.device-control-request-header.command-request-priority");

        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.LCS.name());
        statusRecord.setStatus("request queued/not implemented");
        statusRecord.setLockStatus(lockStatus);
        statusRecord.setOperatorId(operatorId);
        statusRecord.setOperatorLockId(operatorLockId);
        statusRecord.setPriority((priority==null||priority.isEmpty())?0:Integer.parseInt(priority));
        statusRecord.updateDate();
        EntityCommandQueue.getInstance().updateControlRequestStatus(statusRecord, commandMessage, true);
    }

    @Override
    public MessageSpecification getControlResponseMessage(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, EntityEmulationException {

        String orgId = commandMessage.getElementValue("tmdd:lCSControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:lCSControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:lCSControlRequestMsg.device-control-request-header.request-id");

        // get the matching control Request Status record
        EntityControlRequestStatusRecord statusRecord = EntityCommandQueue.getInstance().getControlRequestStatus(orgId, entityId, requestId);
        TMDDEntityType.EntityType entityType = TMDDEntityType.EntityType.valueOf(statusRecord.getEntityType());

        return TMDDMessageProcessor.createControlResponseMessage(entityType, statusRecord, orgInfoDataCollector);

    }
   
}
