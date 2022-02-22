/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.dms;

import java.util.ArrayList;
import java.util.List;
import org.fhwa.c2cri.centermodel.RIEmulation;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.tmdd.TMDDErrorResponseProcessor;
import org.fhwa.c2cri.tmdd.emulation.TMDDEntityType;
import org.fhwa.c2cri.tmdd.emulation.TMDDMessageContentGenerator;
import org.fhwa.c2cri.tmdd.emulation.TMDDMessageProcessor;
import org.fhwa.c2cri.tmdd.emulation.dms.dialogs.DlDMSControlRequest;
import org.fhwa.c2cri.tmdd.emulation.dms.dialogs.DlDMSFontTableRequest;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityCommandProcessor;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityCommandQueue;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatus;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatusCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatusRecord;
import org.fhwa.c2cri.tmdd.emulation.dms.dialogs.DlDMSInventoryRequest;
import org.fhwa.c2cri.tmdd.emulation.dms.dialogs.DlDMSInventoryUpdate;
import org.fhwa.c2cri.tmdd.emulation.dms.dialogs.DlDMSMessageAppearanceRequest;
import org.fhwa.c2cri.tmdd.emulation.dms.dialogs.DlDMSMessageInventoryRequest;
import org.fhwa.c2cri.tmdd.emulation.dms.dialogs.DlDMSMessageInventorySubscription;
import org.fhwa.c2cri.tmdd.emulation.dms.dialogs.DlDMSMessageInventoryUpdate;
import org.fhwa.c2cri.tmdd.emulation.dms.dialogs.DlDMSPriorityQueueRequest;
import org.fhwa.c2cri.tmdd.emulation.dms.dialogs.DlDMSStatusRequest;
import org.fhwa.c2cri.tmdd.emulation.dms.dialogs.DlDMSStatusUpdate;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataOrganizationInformationCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.DMSFONTTABLE;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.DMSINVENTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.DMSMESSAGEAPPEARANCE;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.DMSMESSAGEINVENTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.DMSSTATUS;
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

/**
 * The DMSHandler class coordinates all functions related to DMS entities.
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class DMSHandler implements EntityControlRequestStatusCollector, EntityCommandProcessor {

    /**
     * The inventory of DMS devices
     */
    private DMSInventory dmsInventory = new DMSInventory(EntityEmulationData.EntityDataType.DMSINVENTORY);
    /**
     * The status of all defined DMS devices
     */
    private DMSStatus dmsStatus = new DMSStatus(EntityEmulationData.EntityDataType.DMSSTATUS);
    /**
     * The DMS fonts for all devices
     */
    private DMSFontTable dmsFontTable = new DMSFontTable(EntityEmulationData.EntityDataType.DMSFONTTABLE);
    /**
     * The appearance for Messages on DMS devices
     */
    private DMSMessageAppearance dmsMessageAppearance = new DMSMessageAppearance(EntityEmulationData.EntityDataType.DMSMESSAGEAPPEARANCE);
    /**
     * The inventory of messages in DMS devices
     */
    private DMSMessageInventory dmsMessageInventory = new DMSMessageInventory(EntityEmulationData.EntityDataType.DMSMESSAGEINVENTORY);
    /**
     * Provides organization information related to a specific entity.
     */
    private EntityDataOrganizationInformationCollector orgInfoDataCollector;

    private DMSHandler() {
    }

    ;
    
    /**
     * Main constructor method for the DMSHandler class.
     * 
     * @param orgInfoDataCollector reference to an organization information collector.
     */
    public DMSHandler(EntityDataOrganizationInformationCollector orgInfoDataCollector) {
        this.orgInfoDataCollector = orgInfoDataCollector;
    }

//  private EntityUpdateListeners (DMSStatus and DMSInventory)   
    /**
     * Initializes the data types related to a DMS device.
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
            case DMSINVENTORY:
                dmsInventory.initialize(byteArray);
                break;
            case DMSSTATUS:
                dmsStatus.initialize(byteArray);
                break;
            case DMSFONTTABLE:
                dmsFontTable.initialize(byteArray);
                break;
            case DMSMESSAGEAPPEARANCE:
                dmsMessageAppearance.initialize(byteArray);
                break;
            case DMSMESSAGEINVENTORY:
                dmsMessageInventory.initialize(byteArray);
                break;
            default:
                throw new EntityEmulationException("The DMSHandler is unable to initialize data type " + entityDataType);
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
            case "dlDMSInventoryRequest":
                DlDMSInventoryRequest dialogProcessor = new DlDMSInventoryRequest();
                responseMsg = dialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DMS, DMSINVENTORY, dmsInventory, this);
                break;
            case "dlDMSStatusRequest":
                DlDMSStatusRequest statusDialogProcessor = new DlDMSStatusRequest();
                responseMsg = statusDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DMS, DMSSTATUS, dmsStatus, this);
                break;
            case "dlDMSControlRequest":
                DlDMSControlRequest controlDialogProcessor = new DlDMSControlRequest();
                responseMsg = controlDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DMSCONTROLREQUESTMSG, TMDDEntityType.EntityType.DMS, DMSINVENTORY, dmsInventory, this);
                break;
            case "dlDMSMessageInventoryRequest":
                DlDMSMessageInventoryRequest miDialogProcessor = new DlDMSMessageInventoryRequest();
                responseMsg = miDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DMSMESSAGEINVENTORYREQUESTMSG, TMDDEntityType.EntityType.DMS, DMSMESSAGEINVENTORY, dmsMessageInventory, this);
                break;
            case "dlDMSMessageAppearanceRequest":
                DlDMSMessageAppearanceRequest maDialogProcessor = new DlDMSMessageAppearanceRequest();
                responseMsg = maDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DMSMESSAGEAPPEARANCEREQUESTMSG, TMDDEntityType.EntityType.DMS, DMSMESSAGEAPPEARANCE, dmsMessageAppearance, this);
                break;
            case "dlDMSFontTableRequest":
                DlDMSFontTableRequest ftDialogProcessor = new DlDMSFontTableRequest();
                responseMsg = ftDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DMSFONTTABLEREQUESTMSG, TMDDEntityType.EntityType.DMS, DMSFONTTABLE, dmsFontTable, this);
                break;
            case "dlDMSPriorityQueueRequest":
                DlDMSPriorityQueueRequest pqDialogProcessor = new DlDMSPriorityQueueRequest();
                responseMsg = pqDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEPRIORITYQUEUEREQUESTMSG, TMDDEntityType.EntityType.DMS, DMSINVENTORY, dmsInventory, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the DMSHandler.");
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
            case "dlDMSMessageInventorySubscription":
                DlDMSMessageInventorySubscription dialogProcessor = new DlDMSMessageInventorySubscription();
                responseMsg = dialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DMSMESSAGEINVENTORYREQUESTMSG, TMDDEntityType.EntityType.DMS, DMSMESSAGEINVENTORY, dmsMessageInventory, this);
                break;
            case "dlDeviceInformationSubscription":
                String msgDeviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type"));
                switch (msgDeviceInformationType) {
                    case "1":
                    case "device inventory":
                        DlDeviceInformationSubscription dmsInvDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = dmsInvDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DMS, DMSMESSAGEINVENTORY, dmsMessageInventory, this);
                        break;
                    case "2":
                    case "device status":
                        DlDeviceInformationSubscription dmsStatusDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = dmsStatusDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DMS, DMSSTATUS, dmsStatus, this);
                        break;
                    default:
                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlDMSStatusRequest dialog.");
                }
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the DMSHandler.");
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
            case "dlDMSInventoryUpdate":
                DlDMSInventoryUpdate dialogProcessor = new DlDMSInventoryUpdate();
                responseMsg = dialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DMS, DMSINVENTORY, dmsInventory, this);
                break;
            case "dlDMSStatusUpdate":
                DlDMSStatusUpdate statusDialogProcessor = new DlDMSStatusUpdate();
                responseMsg = statusDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.DMS, DMSSTATUS, dmsStatus, this);
                break;
            case "dlDMSMessageInventoryUpdate":
                DlDMSMessageInventoryUpdate miDialogProcessor = new DlDMSMessageInventoryUpdate();
                responseMsg = miDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DMSMESSAGEINVENTORYREQUESTMSG, TMDDEntityType.EntityType.DMS, DMSMESSAGEINVENTORY, dmsMessageInventory, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the DMSHandler.");
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
        ValueInSetFilter deviceIdFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.DMSINVENTORY, "EntityId", deviceIdFilterValues);
        theFilters.add(deviceIdFilter);

        ArrayList<EntityDataRecord> theResponse = dmsInventory.getEntityElements(theFilters);

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
     * Each DMS-Inventory-Item is assigned an item index. This index is used to
     * indicate the relative order of the DMS Inventory items.
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
                case DMSINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = dmsInventory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{DMSSTATUS,DMSMESSAGEINVENTORY, DMSFONTTABLE, DMSMESSAGEAPPEARANCE});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    if (updatedTypes.contains(DMSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");
                    if (updatedTypes.contains(DMSMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");                        
                     break;
                case DMSSTATUS:
                   List<EntityEmulationData.EntityDataType> statusUpdatedTypes = dmsStatus.addEntity(entityId, new EntityEmulationData.EntityDataType[]{DMSINVENTORY,DMSMESSAGEINVENTORY, DMSFONTTABLE, DMSMESSAGEAPPEARANCE});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");
                    if (statusUpdatedTypes.contains(DMSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    if (statusUpdatedTypes.contains(DMSMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");                        
                    break;
                case DMSFONTTABLE:
                   List<EntityEmulationData.EntityDataType> fontUpdatedTypes = dmsFontTable.addEntity(entityId, new EntityEmulationData.EntityDataType[]{DMSINVENTORY,DMSMESSAGEINVENTORY, DMSSTATUS, DMSMESSAGEAPPEARANCE});
                    if (fontUpdatedTypes.contains(DMSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    if (fontUpdatedTypes.contains(DMSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");
                    if (fontUpdatedTypes.contains(DMSMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");                        
                     break;
                case DMSMESSAGEAPPEARANCE:
                    List<EntityEmulationData.EntityDataType> msgAppUpdatedTypes = dmsMessageAppearance.addEntity(entityId, new EntityEmulationData.EntityDataType[]{DMSINVENTORY,DMSMESSAGEINVENTORY, DMSFONTTABLE, DMSSTATUS});
                    if (msgAppUpdatedTypes.contains(DMSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    if (msgAppUpdatedTypes.contains(DMSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");
                    if (msgAppUpdatedTypes.contains(DMSMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");                        
                    break;
                case DMSMESSAGEINVENTORY:
                    List<EntityEmulationData.EntityDataType> msgInvUpdatedTypes = dmsMessageInventory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{DMSSTATUS,DMSINVENTORY, DMSFONTTABLE, DMSMESSAGEAPPEARANCE});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");
                    if (msgInvUpdatedTypes.contains(DMSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    if (msgInvUpdatedTypes.contains(DMSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");                        
                    break;
                default:
                    throw new EntityEmulationException("The DMSHandler is unable to process data type " + entityDataType);
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
                case DMSINVENTORY:
                    dmsInventory.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    break;
                case DMSSTATUS:
                    dmsStatus.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");
                    break;
                case DMSFONTTABLE:
                    dmsFontTable.addEntityElement(entityId, elementName, elementValue);
                    break;
                case DMSMESSAGEAPPEARANCE:
                    dmsMessageAppearance.addEntityElement(entityId, elementName, elementValue);
                    break;
                case DMSMESSAGEINVENTORY:
                    dmsMessageInventory.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The DMSHandler is unable to process data type " + entityDataType);
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
                case DMSINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = dmsInventory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{DMSSTATUS,DMSMESSAGEINVENTORY, DMSFONTTABLE, DMSMESSAGEAPPEARANCE});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    if (updatedTypes.contains(DMSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");
                    if (updatedTypes.contains(DMSMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");                        
                     break;
                case DMSSTATUS:
                   List<EntityEmulationData.EntityDataType> statusUpdatedTypes = dmsStatus.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{DMSINVENTORY,DMSMESSAGEINVENTORY, DMSFONTTABLE, DMSMESSAGEAPPEARANCE});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");
                    if (statusUpdatedTypes.contains(DMSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    if (statusUpdatedTypes.contains(DMSMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");                        
                    break;
                case DMSFONTTABLE:
                   List<EntityEmulationData.EntityDataType> fontUpdatedTypes = dmsFontTable.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{DMSINVENTORY,DMSMESSAGEINVENTORY, DMSSTATUS, DMSMESSAGEAPPEARANCE});
                    if (fontUpdatedTypes.contains(DMSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    if (fontUpdatedTypes.contains(DMSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");
                    if (fontUpdatedTypes.contains(DMSMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");                        
                    break;
                case DMSMESSAGEAPPEARANCE:
                   List<EntityEmulationData.EntityDataType> msgAppUpdatedTypes = dmsMessageAppearance.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{DMSINVENTORY,DMSMESSAGEINVENTORY, DMSSTATUS, DMSFONTTABLE});
                    if (msgAppUpdatedTypes.contains(DMSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    if (msgAppUpdatedTypes.contains(DMSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");
                    if (msgAppUpdatedTypes.contains(DMSMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");                        
                    break;
                case DMSMESSAGEINVENTORY:
                    List<EntityEmulationData.EntityDataType> msgInvUpdatedTypes = dmsMessageInventory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{DMSINVENTORY, DMSSTATUS, DMSFONTTABLE, DMSMESSAGEAPPEARANCE});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");
                    if (msgInvUpdatedTypes.contains(DMSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    if (msgInvUpdatedTypes.contains(DMSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");                         
                    break;
                default:
                    throw new EntityEmulationException("The DMSHandler is unable to process data type " + entityDataType);
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
                case DMSINVENTORY:
                   List<EntityEmulationData.EntityDataType> updatedTypes = dmsInventory.updateEntityElement(new EntityEmulationData.EntityDataType[]{DMSSTATUS,DMSMESSAGEINVENTORY, DMSFONTTABLE, DMSMESSAGEAPPEARANCE}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    if (updatedTypes.contains(DMSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");
                    if (updatedTypes.contains(DMSMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");                        
                     break;
                case DMSSTATUS:
                   List<EntityEmulationData.EntityDataType> statusUpdatedTypes = dmsStatus.updateEntityElement(new EntityEmulationData.EntityDataType[]{DMSINVENTORY,DMSMESSAGEINVENTORY, DMSFONTTABLE, DMSMESSAGEAPPEARANCE}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");
                    if (statusUpdatedTypes.contains(DMSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    if (statusUpdatedTypes.contains(DMSMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");                        
                    break;
                case DMSFONTTABLE:
                    List<EntityEmulationData.EntityDataType> fontUpdatedTypes = dmsFontTable.updateEntityElement(new EntityEmulationData.EntityDataType[]{DMSSTATUS, DMSINVENTORY, DMSMESSAGEINVENTORY, DMSMESSAGEAPPEARANCE}, entityId, entityElementName, entityElementValue);
                    if (fontUpdatedTypes.contains(DMSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    if (fontUpdatedTypes.contains(DMSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");
                    if (fontUpdatedTypes.contains(DMSMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");                        
                    break;
                case DMSMESSAGEAPPEARANCE:
                    List<EntityEmulationData.EntityDataType> msgAppUpdatedTypes = dmsMessageAppearance.updateEntityElement(new EntityEmulationData.EntityDataType[]{DMSSTATUS,DMSINVENTORY, DMSFONTTABLE, DMSMESSAGEINVENTORY}, entityId, entityElementName, entityElementValue);
                    if (msgAppUpdatedTypes.contains(DMSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    if (msgAppUpdatedTypes.contains(DMSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");
                    if (msgAppUpdatedTypes.contains(DMSMESSAGEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");                        
                    break;
                case DMSMESSAGEINVENTORY:
                    List<EntityEmulationData.EntityDataType> msgInvUpdatedTypes = dmsMessageInventory.updateEntityElement(new EntityEmulationData.EntityDataType[]{DMSSTATUS,DMSINVENTORY, DMSFONTTABLE, DMSMESSAGEAPPEARANCE}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");
                    if (msgInvUpdatedTypes.contains(DMSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    if (msgInvUpdatedTypes.contains(DMSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");      
                    break;
                default:
                    throw new EntityEmulationException("The DMSHandler is unable to process data type " + entityDataType);
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
                case DMSINVENTORY:
                    dmsInventory.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSInventoryUpdate");
                    break;
                case DMSSTATUS:
                    dmsStatus.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSStatusUpdate");
                    break;
                case DMSFONTTABLE:
                    dmsFontTable.deleteEntityElement(entityId, entityElementName);
                    break;
                case DMSMESSAGEAPPEARANCE:
                    dmsMessageAppearance.deleteEntityElement(entityId, entityElementName);
                    break;
                case DMSMESSAGEINVENTORY:
                    dmsMessageInventory.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlDMSMessageInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The DMSHandler is unable to process data type " + entityDataType);
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
                case DMSINVENTORY:
                    entityValue = dmsInventory.getEntityElementValue(entityId, entityElement);
                    break;
                case DMSSTATUS:
                    entityValue = dmsStatus.getEntityElementValue(entityId, entityElement);
                    break;
                case DMSFONTTABLE:
                    entityValue = dmsFontTable.getEntityElementValue(entityId, entityElement);
                    break;
                case DMSMESSAGEAPPEARANCE:
                    entityValue = dmsMessageAppearance.getEntityElementValue(entityId, entityElement);
                    break;
                case DMSMESSAGEINVENTORY:
                    entityValue = dmsMessageInventory.getEntityElementValue(entityId, entityElement);
                    break;
                default:
                    throw new EntityEmulationException("The DMSHandler is unable to process data type " + entityDataType);
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
        String orgId = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.device-control-request-header.request-id");

        // Retrieve the desired DMS Request Command
        String dmsRequestCommand = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.dms-request-command");

        // Retrieve the desired DMS Beacon Command
        String dmsBeaconCommand = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.dms-beacon-control");
        
        // Select the paramaters associated with the requested command
        String dmsRequestParameter = "";

        // Set the command control status 
        String dmsCommandControlStatus = "other";        

        try {
            switch (dmsRequestCommand) {
                case "1":
                case "put up custom message":
                    dmsRequestParameter = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.dms-command-parameters.dms-message");
                    updateEntityElement(DMSSTATUS, entityId, "current-message", dmsRequestParameter);
                    break;
                case "2":
                case "put up a library message":
                    dmsRequestParameter = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.dms-command-parameters.message-number");
                    updateEntityElement(DMSSTATUS, entityId, "message-number", dmsRequestParameter);
                    break;
                case "3":
                case "remove message":
                    dmsRequestParameter = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.dms-command-parameters.dms-message");
                    updateEntityElement(DMSSTATUS, entityId, "current-message", ".");
                    break;
                case "4":
                case "other":
                    dmsRequestParameter = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.dms-command-parameters.dms-position-preset");
                default:
                    break;
            }
            
            if (dmsBeaconCommand != null){               
               updateEntityElement(DMSSTATUS,entityId, "message-beacon", dmsBeaconCommand);                
            }
            
            dmsCommandControlStatus = "requested changes completed";
        } catch (InvalidValueException ex) {
            dmsCommandControlStatus = "request rejected invalid command parameters";
        } catch (EntityEmulationException ex){
            dmsCommandControlStatus = "other";            
        }

        
        String operatorId = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.device-control-request-header.authentication.operator-id");

        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.DMS.name());
        statusRecord.setStatus(dmsCommandControlStatus);
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

        String orgId = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.device-control-request-header.request-id");

        String operatorId = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.device-control-request-header.authentication.operator-id");

        String priority = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.device-control-request-header.command-request-priority");
        
        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.DMS.name());
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

        String orgId = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:dMSControlRequestMsg.device-control-request-header.request-id");

        // get the matching control Request Status record
        EntityControlRequestStatusRecord statusRecord = EntityCommandQueue.getInstance().getControlRequestStatus(orgId, entityId, requestId);
        TMDDEntityType.EntityType entityType = TMDDEntityType.EntityType.valueOf(statusRecord.getEntityType());

        return TMDDMessageProcessor.createControlResponseMessage(entityType, statusRecord, orgInfoDataCollector);

    }
    
}
