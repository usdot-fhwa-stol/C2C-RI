/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.intersection;

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
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.INTERSECTIONSIGNALCONTROLSCHEDULE;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORY;
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
import org.fhwa.c2cri.tmdd.emulation.intersection.dialogs.DlIntersectionSignalControlRequest;
import org.fhwa.c2cri.tmdd.emulation.intersection.dialogs.DlIntersectionSignalControlScheduleRequest;
import org.fhwa.c2cri.tmdd.emulation.intersection.dialogs.DlIntersectionSignalControlScheduleUpdate;
import org.fhwa.c2cri.tmdd.emulation.intersection.dialogs.DlIntersectionSignalInventoryRequest;
import org.fhwa.c2cri.tmdd.emulation.intersection.dialogs.DlIntersectionSignalInventoryUpdate;
import org.fhwa.c2cri.tmdd.emulation.intersection.dialogs.DlIntersectionSignalPriorityQueueRequest;
import org.fhwa.c2cri.tmdd.emulation.intersection.dialogs.DlIntersectionSignalStatusRequest;
import org.fhwa.c2cri.tmdd.emulation.intersection.dialogs.DlIntersectionSignalStatusUpdate;
import org.fhwa.c2cri.tmdd.emulation.intersection.dialogs.DlIntersectionSignalTimingPatternInventoryRequest;
import org.fhwa.c2cri.tmdd.emulation.intersection.dialogs.DlIntersectionSignalTimingPatternInventorySubscription;
import org.fhwa.c2cri.tmdd.emulation.intersection.dialogs.DlIntersectionSignalTimingPatternInventoryUpdate;

/**
 * The INTERSECTIONSIGNALHandler class coordinates all functions related to
 * INTERSECTIONSIGNAL entities.
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class IntersectionSignalHandler implements EntityControlRequestStatusCollector, EntityCommandProcessor {

    /**
     * The inventory of INTERSECTIONSIGNAL devices
     */
    private IntersectionSignalInventory intersectionsignalInventory = new IntersectionSignalInventory(EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY);
    /**
     * The status of all defined INTERSECTIONSIGNAL devices
     */
    private IntersectionSignalStatus intersectionsignalStatus = new IntersectionSignalStatus(EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS);
    /**
     * The control schedule for all IntersectionSignals
     */
    private IntersectionSignalControlSchedule intersectionsignalControlSchedule = new IntersectionSignalControlSchedule(EntityEmulationData.EntityDataType.INTERSECTIONSIGNALCONTROLSCHEDULE);
    /**
     * The control schedule for all IntersectionSignals
     */
    private IntersectionSignalTimingPatternInventory intersectionsignalTimingPatternInventory = new IntersectionSignalTimingPatternInventory(EntityEmulationData.EntityDataType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORY);
    /**
     * Provides organization information related to a specific entity.
     */
    private EntityDataOrganizationInformationCollector orgInfoDataCollector;

    private IntersectionSignalHandler() {
    }

    ;
    
    /**
     * Main constructor method for the INTERSECTIONSIGNALHandler class.
     * 
     * @param orgInfoDataCollector reference to an organization information collector.
     */
    public IntersectionSignalHandler(EntityDataOrganizationInformationCollector orgInfoDataCollector) {
        this.orgInfoDataCollector = orgInfoDataCollector;
    }

//  private EntityUpdateListeners (INTERSECTIONSIGNALStatus and INTERSECTIONSIGNALInventory)   
    /**
     * Initializes the data types related to a INTERSECTIONSIGNAL device.
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
            case INTERSECTIONSIGNALINVENTORY:
                intersectionsignalInventory.initialize(byteArray);
                break;
            case INTERSECTIONSIGNALSTATUS:
                intersectionsignalStatus.initialize(byteArray);
                break;
            case INTERSECTIONSIGNALCONTROLSCHEDULE:
                intersectionsignalControlSchedule.initialize(byteArray);
                break;
            case INTERSECTIONSIGNALTIMINGPATTERNINVENTORY:
                intersectionsignalTimingPatternInventory.initialize(byteArray);
                break;
            default:
                throw new EntityEmulationException("The IntersectionSignalHandler is unable to initialize data type " + entityDataType);
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
            case "dlIntersectionSignalControlRequest":
                DlIntersectionSignalControlRequest dlIntersectionSignalControlRequestProcessor = new DlIntersectionSignalControlRequest();
                responseMsg = dlIntersectionSignalControlRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.INTERSECTIONSIGNALCONTROLREQUESTMSG, TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER, INTERSECTIONSIGNALINVENTORY, intersectionsignalInventory, this);
                break;
            case "dlIntersectionSignalInventoryRequest":
                DlIntersectionSignalInventoryRequest dlIntersectionSignalInventoryRequestProcessor = new DlIntersectionSignalInventoryRequest();
                responseMsg = dlIntersectionSignalInventoryRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER, INTERSECTIONSIGNALINVENTORY, intersectionsignalInventory, this);
                break;
            case "dlIntersectionSignalStatusRequest":
                DlIntersectionSignalStatusRequest dlIntersectionSignalStatusRequestProcessor = new DlIntersectionSignalStatusRequest();
                responseMsg = dlIntersectionSignalStatusRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER, INTERSECTIONSIGNALSTATUS, intersectionsignalStatus, this);
                break;
            case "dlIntersectionSignalControlScheduleRequest":
                DlIntersectionSignalControlScheduleRequest dlIntersectionSignalControlScheduleRequestProcessor = new DlIntersectionSignalControlScheduleRequest();
                responseMsg = dlIntersectionSignalControlScheduleRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER, INTERSECTIONSIGNALCONTROLSCHEDULE, intersectionsignalControlSchedule, this);
                break;
            case "dlIntersectionSignalPriorityQueueRequest":
                DlIntersectionSignalPriorityQueueRequest dlIntersectionSignalPriorityQueueRequestProcessor = new DlIntersectionSignalPriorityQueueRequest();
                responseMsg = dlIntersectionSignalPriorityQueueRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEPRIORITYQUEUEREQUESTMSG, TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER, INTERSECTIONSIGNALINVENTORY, intersectionsignalInventory, this);
                break;
            case "dlIntersectionSignalTimingPatternInventoryRequest":
                DlIntersectionSignalTimingPatternInventoryRequest dlIntersectionSignalTimingPatternInventoryRequestProcessor = new DlIntersectionSignalTimingPatternInventoryRequest();
                responseMsg = dlIntersectionSignalTimingPatternInventoryRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORYREQUESTMSG, TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER, INTERSECTIONSIGNALTIMINGPATTERNINVENTORY, intersectionsignalTimingPatternInventory, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the IntersectionSignalHandler.");
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
            case "dlIntersectionSignalTimingPatternInventorySubscription":
                DlIntersectionSignalTimingPatternInventorySubscription dlIntersectionSignalTimingPatternInventorySubscriptionProcessor = new DlIntersectionSignalTimingPatternInventorySubscription();
                responseMsg = dlIntersectionSignalTimingPatternInventorySubscriptionProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORYREQUESTMSG, TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER, INTERSECTIONSIGNALTIMINGPATTERNINVENTORY, intersectionsignalTimingPatternInventory, this);
                break;
            case "dlDeviceInformationSubscription":
                String msgDeviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type"));
                switch (msgDeviceInformationType) {
                    case "1":
                    case "device inventory":
                        DlDeviceInformationSubscription signalcontrollerInvDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = signalcontrollerInvDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER, INTERSECTIONSIGNALINVENTORY, intersectionsignalInventory, this);
                        break;
                    case "2":
                    case "device status":
                        DlDeviceInformationSubscription signalcontrollerStatusDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = signalcontrollerStatusDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER, INTERSECTIONSIGNALSTATUS, intersectionsignalStatus, this);
                        break;
                    case "3":
                    case "device schedule":
                        DlDeviceInformationSubscription signalcontrollerScheduleDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = signalcontrollerScheduleDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER, INTERSECTIONSIGNALCONTROLSCHEDULE, intersectionsignalControlSchedule, this);
                        break;
                    default:
                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlDeviceInformationSubscription dialog.");
                }
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the IntersectionSignalHandler.");
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
            case "dlIntersectionSignalInventoryUpdate":
                DlIntersectionSignalInventoryUpdate dlIntersectionSignalInventoryUpdateProcessor = new DlIntersectionSignalInventoryUpdate();
                responseMsg = dlIntersectionSignalInventoryUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER, INTERSECTIONSIGNALINVENTORY, intersectionsignalInventory, this);
                break;
            case "dlIntersectionSignalStatusUpdate":
                DlIntersectionSignalStatusUpdate dlIntersectionSignalStatusUpdateProcessor = new DlIntersectionSignalStatusUpdate();
                responseMsg = dlIntersectionSignalStatusUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER, INTERSECTIONSIGNALSTATUS, intersectionsignalStatus, this);
                break;
            case "dlIntersectionSignalControlScheduleUpdate":
                DlIntersectionSignalControlScheduleUpdate dlIntersectionSignalControlScheduleUpdateProcessor = new DlIntersectionSignalControlScheduleUpdate();
                responseMsg = dlIntersectionSignalControlScheduleUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER, INTERSECTIONSIGNALCONTROLSCHEDULE, intersectionsignalControlSchedule, this);
                break;
            case "dlIntersectionSignalTimingPatternInventoryUpdate":
                DlIntersectionSignalTimingPatternInventoryUpdate dlIntersectionSignalTimingPatternInventoryUpdateProcessor = new DlIntersectionSignalTimingPatternInventoryUpdate();
                responseMsg = dlIntersectionSignalTimingPatternInventoryUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORYREQUESTMSG, TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER, INTERSECTIONSIGNALTIMINGPATTERNINVENTORY, intersectionsignalTimingPatternInventory, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the IntersectionSignalHandler.");
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
        ValueInSetFilter deviceIdFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "EntityId", deviceIdFilterValues);
        theFilters.add(deviceIdFilter);

        ArrayList<EntityDataRecord> theResponse = intersectionsignalInventory.getEntityElements(theFilters);

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
     * Each INTERSECTIONSIGNAL-Inventory-Item is assigned an item index. This
     * index is used to indicate the relative order of the INTERSECTIONSIGNAL
     * Inventory items.
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
                case INTERSECTIONSIGNALINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = intersectionsignalInventory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{INTERSECTIONSIGNALSTATUS,INTERSECTIONSIGNALCONTROLSCHEDULE,INTERSECTIONSIGNALTIMINGPATTERNINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalInventoryUpdate");
                    if (updatedTypes.contains(INTERSECTIONSIGNALSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalStatusUpdate");
                    if (updatedTypes.contains(INTERSECTIONSIGNALCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalControlScheduleUpdate");                        
                    if (updatedTypes.contains(INTERSECTIONSIGNALTIMINGPATTERNINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalTimingPatternInventoryUpdate");                        
                    break;
                case INTERSECTIONSIGNALSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = intersectionsignalStatus.addEntity(entityId, new EntityEmulationData.EntityDataType[]{INTERSECTIONSIGNALINVENTORY,INTERSECTIONSIGNALCONTROLSCHEDULE,INTERSECTIONSIGNALTIMINGPATTERNINVENTORY});
                     RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalStatusUpdate");
                    if (statusUpdatedTypes.contains(INTERSECTIONSIGNALINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalInventoryUpdate");
                    if (statusUpdatedTypes.contains(INTERSECTIONSIGNALCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalControlScheduleUpdate");                        
                    if (statusUpdatedTypes.contains(INTERSECTIONSIGNALTIMINGPATTERNINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalTimingPatternInventoryUpdate");                        
                    break;
                case INTERSECTIONSIGNALCONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = intersectionsignalControlSchedule.addEntity(entityId, new EntityEmulationData.EntityDataType[]{INTERSECTIONSIGNALSTATUS,INTERSECTIONSIGNALINVENTORY,INTERSECTIONSIGNALTIMINGPATTERNINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(INTERSECTIONSIGNALSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalStatusUpdate");
                    if (controlUpdatedTypes.contains(INTERSECTIONSIGNALINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalInventoryUpdate");                        
                    if (controlUpdatedTypes.contains(INTERSECTIONSIGNALTIMINGPATTERNINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalTimingPatternInventoryUpdate");                        
                    break;
                case INTERSECTIONSIGNALTIMINGPATTERNINVENTORY:
                    List<EntityEmulationData.EntityDataType> timingUpdatedTypes = intersectionsignalTimingPatternInventory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{INTERSECTIONSIGNALSTATUS,INTERSECTIONSIGNALCONTROLSCHEDULE,INTERSECTIONSIGNALINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalTimingPatternInventoryUpdate");
                    if (timingUpdatedTypes.contains(INTERSECTIONSIGNALSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalStatusUpdate");
                    if (timingUpdatedTypes.contains(INTERSECTIONSIGNALCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalControlScheduleUpdate");                        
                    if (timingUpdatedTypes.contains(INTERSECTIONSIGNALINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalInventoryUpdate");                        
                    break;
                default:
                    throw new EntityEmulationException("The IntersectionSignalHandler is unable to process data type " + entityDataType);
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
                case INTERSECTIONSIGNALINVENTORY:
                    intersectionsignalInventory.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalInventoryUpdate");
                    break;
                case INTERSECTIONSIGNALSTATUS:
                    intersectionsignalStatus.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalStatusUpdate");
                    break;
                case INTERSECTIONSIGNALCONTROLSCHEDULE:
                    intersectionsignalControlSchedule.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalControlScheduleUpdate");
                    break;
                case INTERSECTIONSIGNALTIMINGPATTERNINVENTORY:
                    intersectionsignalTimingPatternInventory.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalTimingPatternInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The IntersectionSignalHandler is unable to process data type " + entityDataType);
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
                case INTERSECTIONSIGNALINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = intersectionsignalInventory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{INTERSECTIONSIGNALSTATUS,INTERSECTIONSIGNALCONTROLSCHEDULE,INTERSECTIONSIGNALTIMINGPATTERNINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalInventoryUpdate");
                    if (updatedTypes.contains(INTERSECTIONSIGNALSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalStatusUpdate");
                    if (updatedTypes.contains(INTERSECTIONSIGNALCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalControlScheduleUpdate");                        
                    if (updatedTypes.contains(INTERSECTIONSIGNALTIMINGPATTERNINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalTimingPatternInventoryUpdate");                        
                    break;
                case INTERSECTIONSIGNALSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = intersectionsignalStatus.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{INTERSECTIONSIGNALINVENTORY,INTERSECTIONSIGNALCONTROLSCHEDULE,INTERSECTIONSIGNALTIMINGPATTERNINVENTORY});
                     RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalStatusUpdate");
                    if (statusUpdatedTypes.contains(INTERSECTIONSIGNALINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalInventoryUpdate");
                    if (statusUpdatedTypes.contains(INTERSECTIONSIGNALCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalControlScheduleUpdate");                        
                    if (statusUpdatedTypes.contains(INTERSECTIONSIGNALTIMINGPATTERNINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalTimingPatternInventoryUpdate");                        
                    break;
                case INTERSECTIONSIGNALCONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = intersectionsignalControlSchedule.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{INTERSECTIONSIGNALSTATUS,INTERSECTIONSIGNALINVENTORY,INTERSECTIONSIGNALTIMINGPATTERNINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(INTERSECTIONSIGNALSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalStatusUpdate");
                    if (controlUpdatedTypes.contains(INTERSECTIONSIGNALINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalInventoryUpdate");                        
                    if (controlUpdatedTypes.contains(INTERSECTIONSIGNALTIMINGPATTERNINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalTimingPatternInventoryUpdate");                        
                    break;
                case INTERSECTIONSIGNALTIMINGPATTERNINVENTORY:
                    List<EntityEmulationData.EntityDataType> timingUpdatedTypes = intersectionsignalTimingPatternInventory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{INTERSECTIONSIGNALSTATUS,INTERSECTIONSIGNALCONTROLSCHEDULE,INTERSECTIONSIGNALINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalTimingPatternInventoryUpdate");
                    if (timingUpdatedTypes.contains(INTERSECTIONSIGNALSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalStatusUpdate");
                    if (timingUpdatedTypes.contains(INTERSECTIONSIGNALCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalControlScheduleUpdate");                        
                    if (timingUpdatedTypes.contains(INTERSECTIONSIGNALINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalInventoryUpdate");         
                    break;
                default:
                    throw new EntityEmulationException("The IntersectionSignalHandler is unable to process data type " + entityDataType);
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
                case INTERSECTIONSIGNALINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = intersectionsignalInventory.updateEntityElement(new EntityEmulationData.EntityDataType[]{INTERSECTIONSIGNALSTATUS,INTERSECTIONSIGNALCONTROLSCHEDULE,INTERSECTIONSIGNALTIMINGPATTERNINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalInventoryUpdate");
                    if (updatedTypes.contains(INTERSECTIONSIGNALSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalStatusUpdate");
                    if (updatedTypes.contains(INTERSECTIONSIGNALCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalControlScheduleUpdate");                        
                    if (updatedTypes.contains(INTERSECTIONSIGNALTIMINGPATTERNINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalTimingPatternInventoryUpdate");                        
                    break;
                case INTERSECTIONSIGNALSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = intersectionsignalStatus.updateEntityElement(new EntityEmulationData.EntityDataType[]{INTERSECTIONSIGNALINVENTORY,INTERSECTIONSIGNALCONTROLSCHEDULE,INTERSECTIONSIGNALTIMINGPATTERNINVENTORY}, entityId, entityElementName, entityElementValue);
                     RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalStatusUpdate");
                    if (statusUpdatedTypes.contains(INTERSECTIONSIGNALINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalInventoryUpdate");
                    if (statusUpdatedTypes.contains(INTERSECTIONSIGNALCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalControlScheduleUpdate");                        
                    if (statusUpdatedTypes.contains(INTERSECTIONSIGNALTIMINGPATTERNINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalTimingPatternInventoryUpdate");                        
                    break;
                case INTERSECTIONSIGNALCONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = intersectionsignalControlSchedule.updateEntityElement(new EntityEmulationData.EntityDataType[]{INTERSECTIONSIGNALSTATUS,INTERSECTIONSIGNALINVENTORY,INTERSECTIONSIGNALTIMINGPATTERNINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(INTERSECTIONSIGNALSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalStatusUpdate");
                    if (controlUpdatedTypes.contains(INTERSECTIONSIGNALINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalInventoryUpdate");                        
                    if (controlUpdatedTypes.contains(INTERSECTIONSIGNALTIMINGPATTERNINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalTimingPatternInventoryUpdate");                        
                    break;
                case INTERSECTIONSIGNALTIMINGPATTERNINVENTORY:
                    List<EntityEmulationData.EntityDataType> timingUpdatedTypes = intersectionsignalTimingPatternInventory.updateEntityElement(new EntityEmulationData.EntityDataType[]{INTERSECTIONSIGNALSTATUS,INTERSECTIONSIGNALCONTROLSCHEDULE,INTERSECTIONSIGNALINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalTimingPatternInventoryUpdate");
                    if (timingUpdatedTypes.contains(INTERSECTIONSIGNALSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalStatusUpdate");
                    if (timingUpdatedTypes.contains(INTERSECTIONSIGNALCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalControlScheduleUpdate");                        
                    if (timingUpdatedTypes.contains(INTERSECTIONSIGNALINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalInventoryUpdate");         
                    break;
                default:
                    throw new EntityEmulationException("The IntersectionSignalHandler is unable to process data type " + entityDataType);
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
                case INTERSECTIONSIGNALINVENTORY:
                    intersectionsignalInventory.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalInventoryUpdate");
                    break;
                case INTERSECTIONSIGNALSTATUS:
                    intersectionsignalStatus.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalStatusUpdate");
                    break;
                case INTERSECTIONSIGNALCONTROLSCHEDULE:
                    intersectionsignalControlSchedule.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalControlScheduleUpdate");
                    break;
                case INTERSECTIONSIGNALTIMINGPATTERNINVENTORY:
                    intersectionsignalTimingPatternInventory.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlIntersectionSignalTimingPatternInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The IntersectionSignalHandler is unable to process data type " + entityDataType);
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
                case INTERSECTIONSIGNALINVENTORY:
                    entityValue = intersectionsignalInventory.getEntityElementValue(entityId, entityElement);
                    break;
                case INTERSECTIONSIGNALSTATUS:
                    entityValue = intersectionsignalStatus.getEntityElementValue(entityId, entityElement);
                    break;
                case INTERSECTIONSIGNALCONTROLSCHEDULE:
                    entityValue = intersectionsignalControlSchedule.getEntityElementValue(entityId, entityElement);
                    break;
                case INTERSECTIONSIGNALTIMINGPATTERNINVENTORY:
                    entityValue = intersectionsignalTimingPatternInventory.getEntityElementValue(entityId, entityElement);
                    break;
                default:
                    throw new EntityEmulationException("The IntersectionSignalHandler is unable to process data type " + entityDataType);
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
        if (commandMessage == null) {
            throw new InvalidCommandException("No valid Command Request was provided.");
        }
    }

    @Override
    public void executeCommand(MessageSpecification commandMessage) throws EntityEmulationException {
        int lockStatus = 0;
        String operatorLockId = "";

        // retrieve the key elements from the request Message
        // get the org Id from the message
        String orgId = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.device-control-request-header.request-id");

        // Retrieve the desired Intersection Request Command
        String intersectionRequestCommand = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.intersection-request-command");

        String operatorId = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.device-control-request-header.authentication.operator-id");

        // Retrieve the desired INTERSECTIONSIGNAL Request Command
        String intersectionsignalRequestCommand = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.intersection-request-command");

        // Select the paramaters associated with the requested command
        String intersectionsignalRequestParameter = "";

        // Set the command control status 
        String intersectionsignalCommandControlStatus = "other";

        try {
            switch (intersectionsignalRequestCommand) {
                case "1":
                case "change signal timing mode":
                    intersectionsignalRequestParameter = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.intersection-command-parameters.request-timing-mode");
                    updateEntityElement(INTERSECTIONSIGNALSTATUS, entityId, "current-signal-timing-mode", intersectionsignalRequestParameter);
                    break;
                case "2":
                case "change signal timing pattern":
                    intersectionsignalRequestParameter = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.intersection-command-parameters.timing-pattern-id");
                    updateEntityElement(INTERSECTIONSIGNALSTATUS, entityId, "timing-pattern-id-current", intersectionsignalRequestParameter);
                    break;
                case "3":
                case "make offset adjustment":
                    intersectionsignalRequestParameter = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.intersection-command-parameters.offset-adjustment");
                    updateEntityElement(INTERSECTIONSIGNALSTATUS, entityId, "offset-time-current", intersectionsignalRequestParameter);
                    break;
                case "4":
                case "other":
                    intersectionsignalRequestParameter = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.intersectionsignal-command-parameters.intersectionsignal-position-preset");
					break;
                default:
                    break;
            }

            intersectionsignalCommandControlStatus = "requested changes completed";
        } catch (InvalidValueException ex) {
            intersectionsignalCommandControlStatus = "request rejected invalid command parameters";
        } catch (EntityEmulationException ex) {
            intersectionsignalCommandControlStatus = "other";
        }
        
        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER.name());
        statusRecord.setStatus(intersectionsignalCommandControlStatus);
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

        String orgId = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.device-control-request-header.request-id");

        String operatorId = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.device-control-request-header.authentication.operator-id");

        String priority = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.device-control-request-header.command-request-priority");

        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER.name());
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

        String orgId = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

            // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.device-control-request-header.device-id");

            // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:intersectionSignalControlRequestMsg.device-control-request-header.request-id");

        // get the matching control Request Status record
        EntityControlRequestStatusRecord statusRecord = EntityCommandQueue.getInstance().getControlRequestStatus(orgId, entityId, requestId);
        TMDDEntityType.EntityType entityType = TMDDEntityType.EntityType.valueOf(statusRecord.getEntityType());

        ArrayList<String> intersectionSignalControlResponseList = new ArrayList();
        for (String thisElement: TMDDMessageProcessor.createControlResponseMessage(entityType, statusRecord, orgInfoDataCollector).getMessageSpec() ){

                // replace tmdd:deviceControlResponse elements with the appropriate intersectionSignalControlResponse Elements
                thisElement = thisElement.replace("tmdd:deviceControlResponseMsg", "tmdd:intersectionSignalControlResponseMsg.device-control-response-header");

                // Add all elements that come from DeviceControlResponse
                intersectionSignalControlResponseList.add(thisElement);
            }

            // Add section-id
            // NOTE:  The section that an intersectionis associated with is not included in the control request or in the inventory information.
            // Since this field is optional we will skip adding this at this time.
//        String sectionId = commandMessage.getElementValue("tmdd:intersectionSignalControlRequest.??");
//        if ((sectionId != null)&&(!sectionId.isEmpty())){
//            intersectionSignalControlResponseList.add("tmdd:intersectionSignalControlResponseMsg.section-id = "+sectionId);
//        }
            // Add request-control-mode
            String requestControlMode = commandMessage.getElementValue("tmdd:intersectionSignalControlRequest.intersection-command-parameters.request-timing-mode");
            if ((requestControlMode != null) && (!requestControlMode.isEmpty())) {
                intersectionSignalControlResponseList.add("tmdd:intersectionSignalControlResponseMsg.request-control-mode = " + requestControlMode);
            }

            // Add timing-pattern-id
            String timingPatternId = commandMessage.getElementValue("tmdd:intersectionSignalControlRequest.intersection-command-parameters.timing-pattern-id");
            if ((timingPatternId != null) && (!timingPatternId.isEmpty())) {
                intersectionSignalControlResponseList.add("tmdd:intersectionSignalControlResponseMsg.timing-pattern-id = " + timingPatternId);
            }

            // Add offset-adjustment
            String offsetAdjustment = commandMessage.getElementValue("tmdd:intersectionSignalControlRequest.intersection-command-parameters.timing-pattern-id");
            if ((offsetAdjustment != null) && (!offsetAdjustment.isEmpty())) {
                intersectionSignalControlResponseList.add("tmdd:intersectionSignalControlResponseMsg.offset-adjustment = " + offsetAdjustment);
            }

        MessageSpecification intersectionSignalControlResponse = new MessageSpecification(intersectionSignalControlResponseList);

        return intersectionSignalControlResponse;

    }
   
}
