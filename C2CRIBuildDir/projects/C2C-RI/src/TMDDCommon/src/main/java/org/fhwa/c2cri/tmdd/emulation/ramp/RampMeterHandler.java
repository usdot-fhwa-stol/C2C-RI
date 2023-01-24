/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.ramp;

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
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.RAMPMETERCONTROLSCHEDULE;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.RAMPMETERINVENTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.RAMPMETERPLANINVENTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.RAMPMETERSTATUS;
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
import org.fhwa.c2cri.tmdd.emulation.ramp.dialogs.DlRampMeterControlRequest;
import org.fhwa.c2cri.tmdd.emulation.ramp.dialogs.DlRampMeterControlScheduleRequest;
import org.fhwa.c2cri.tmdd.emulation.ramp.dialogs.DlRampMeterControlScheduleUpdate;
import org.fhwa.c2cri.tmdd.emulation.ramp.dialogs.DlRampMeterInventoryRequest;
import org.fhwa.c2cri.tmdd.emulation.ramp.dialogs.DlRampMeterInventoryUpdate;
import org.fhwa.c2cri.tmdd.emulation.ramp.dialogs.DlRampMeterPlanInventoryRequest;
import org.fhwa.c2cri.tmdd.emulation.ramp.dialogs.DlRampMeterPlanInventorySubscription;
import org.fhwa.c2cri.tmdd.emulation.ramp.dialogs.DlRampMeterPlanInventoryUpdate;
import org.fhwa.c2cri.tmdd.emulation.ramp.dialogs.DlRampMeterPriorityQueueRequest;
import org.fhwa.c2cri.tmdd.emulation.ramp.dialogs.DlRampMeterStatusRequest;
import org.fhwa.c2cri.tmdd.emulation.ramp.dialogs.DlRampMeterStatusUpdate;

/**
 * The RAMPMETERHandler class coordinates all functions related to RAMPMETER
 * entities.
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class RampMeterHandler implements EntityControlRequestStatusCollector, EntityCommandProcessor {

    /**
     * The inventory of RAMPMETER devices
     */
    private RampMeterInventory rampmeterInventory = new RampMeterInventory(EntityEmulationData.EntityDataType.RAMPMETERINVENTORY);
    /**
     * The status of all defined RAMPMETER devices
     */
    private RampMeterStatus rampmeterStatus = new RampMeterStatus(EntityEmulationData.EntityDataType.RAMPMETERSTATUS);
    /**
     * The control schedule for all RampMeters
     */
    private RampMeterControlSchedule rampmeterControlSchedule = new RampMeterControlSchedule(EntityEmulationData.EntityDataType.RAMPMETERCONTROLSCHEDULE);
    /**
     * The control schedule for all RampMeters
     */
    private RampMeterPlanInventory rampmeterPlanInventory = new RampMeterPlanInventory(EntityEmulationData.EntityDataType.RAMPMETERPLANINVENTORY);
    /**
     * Provides organization information related to a specific entity.
     */
    private EntityDataOrganizationInformationCollector orgInfoDataCollector;

    private RampMeterHandler() {
    }

    ;
    
    /**
     * Main constructor method for the RAMPMETERHandler class.
     * 
     * @param orgInfoDataCollector reference to an organization information collector.
     */
    public RampMeterHandler(EntityDataOrganizationInformationCollector orgInfoDataCollector) {
        this.orgInfoDataCollector = orgInfoDataCollector;
    }

//  private EntityUpdateListeners (RAMPMETERStatus and RAMPMETERInventory)   
    /**
     * Initializes the data types related to a RAMPMETER device.
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
            case RAMPMETERINVENTORY:
                rampmeterInventory.initialize(byteArray);
                break;
            case RAMPMETERSTATUS:
                rampmeterStatus.initialize(byteArray);
                break;
            case RAMPMETERCONTROLSCHEDULE:
                rampmeterControlSchedule.initialize(byteArray);
                break;
            case RAMPMETERPLANINVENTORY:
                rampmeterPlanInventory.initialize(byteArray);
                break;
            default:
                throw new EntityEmulationException("The RampMeterHandler is unable to initialize data type " + entityDataType);
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
            case "dlRampMeterControlRequest":
                DlRampMeterControlRequest dlRampMeterControlRequestProcessor = new DlRampMeterControlRequest();
                responseMsg = dlRampMeterControlRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.RAMPMETERCONTROLREQUESTMSG, TMDDEntityType.EntityType.RAMPMETER, RAMPMETERINVENTORY, rampmeterInventory, this);
                break;
            case "dlRampMeterInventoryRequest":
                DlRampMeterInventoryRequest dlRampMeterInventoryRequestProcessor = new DlRampMeterInventoryRequest();
                responseMsg = dlRampMeterInventoryRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.RAMPMETER, RAMPMETERINVENTORY, rampmeterInventory, this);
                break;
            case "dlRampMeterStatusRequest":
                DlRampMeterStatusRequest dlRampMeterStatusRequestProcessor = new DlRampMeterStatusRequest();
                responseMsg = dlRampMeterStatusRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.RAMPMETER, RAMPMETERSTATUS, rampmeterStatus, this);
                break;
            case "dlRampMeterControlScheduleRequest":
                DlRampMeterControlScheduleRequest dlRampMeterControlScheduleRequestProcessor = new DlRampMeterControlScheduleRequest();
                responseMsg = dlRampMeterControlScheduleRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.RAMPMETER, RAMPMETERCONTROLSCHEDULE, rampmeterControlSchedule, this);
                break;
            case "dlRampMeterPriorityQueueRequest":
                DlRampMeterPriorityQueueRequest dlRampMeterPriorityQueueRequestProcessor = new DlRampMeterPriorityQueueRequest();
                responseMsg = dlRampMeterPriorityQueueRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEPRIORITYQUEUEREQUESTMSG, TMDDEntityType.EntityType.RAMPMETER, RAMPMETERINVENTORY, rampmeterInventory, this);
                break;
            case "dlRampMeterPlanInventoryRequest":
                DlRampMeterPlanInventoryRequest dlRampMeterPlanInventoryRequestProcessor = new DlRampMeterPlanInventoryRequest();
                responseMsg = dlRampMeterPlanInventoryRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.RAMPMETER, RAMPMETERPLANINVENTORY, rampmeterPlanInventory, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the RampMeterHandler.");
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
            case "dlRampMeterPlanInventorySubscription":
                DlRampMeterPlanInventorySubscription dlRampMeterPlanInventorySubscriptionProcessor = new DlRampMeterPlanInventorySubscription();
                responseMsg = dlRampMeterPlanInventorySubscriptionProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.RAMPMETER, RAMPMETERPLANINVENTORY, rampmeterPlanInventory, this);
                break;
            case "dlDeviceInformationSubscription":
                String msgDeviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type"));
                switch (msgDeviceInformationType) {
                    case "1":
                    case "device inventory":
                        DlDeviceInformationSubscription rampmeterInvDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = rampmeterInvDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.RAMPMETER, RAMPMETERINVENTORY, rampmeterInventory, this);
                        break;
                    case "2":
                    case "device status":
                        DlDeviceInformationSubscription rampmeterStatusDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = rampmeterStatusDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.RAMPMETER, RAMPMETERSTATUS, rampmeterStatus, this);
                        break;
                    case "3":
                    case "device schedule":
                        DlDeviceInformationSubscription rampmeterScheduleDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = rampmeterScheduleDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.RAMPMETER, RAMPMETERCONTROLSCHEDULE, rampmeterControlSchedule, this);
                        break;
                    case "4":
                    case "device plan":
                        DlDeviceInformationSubscription rampmeterPlanDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = rampmeterPlanDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.RAMPMETER, RAMPMETERPLANINVENTORY, rampmeterPlanInventory, this);
                        break;
                    default:
                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlDeviceInformationSubscription dialog.");
                }
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the RampMeterHandler.");
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
            case "dlRampMeterPlanInventoryUpdate":
                DlRampMeterPlanInventoryUpdate dlRampMeterPlanInventoryUpdateProcessor = new DlRampMeterPlanInventoryUpdate();
                responseMsg = dlRampMeterPlanInventoryUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.RAMPMETER, RAMPMETERPLANINVENTORY, rampmeterPlanInventory, this);
                break;
            case "dlRampMeterStatusUpdate":
                DlRampMeterStatusUpdate dlRampMeterStatusUpdateProcessor = new DlRampMeterStatusUpdate();
                responseMsg = dlRampMeterStatusUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.RAMPMETER, RAMPMETERSTATUS, rampmeterStatus, this);
                break;
            case "dlRampMeterControlScheduleUpdate":
                DlRampMeterControlScheduleUpdate dlRampMeterControlScheduleUpdateProcessor = new DlRampMeterControlScheduleUpdate();
                responseMsg = dlRampMeterControlScheduleUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.RAMPMETER, RAMPMETERCONTROLSCHEDULE, rampmeterControlSchedule, this);
                break;
            case "dlRampMeterInventoryUpdate":
                DlRampMeterInventoryUpdate dlRampMeterInventoryUpdateProcessor = new DlRampMeterInventoryUpdate();
                responseMsg = dlRampMeterInventoryUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.RAMPMETER, RAMPMETERINVENTORY, rampmeterInventory, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the RampMeterHandler.");
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
        ValueInSetFilter deviceIdFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "EntityId", deviceIdFilterValues);
        theFilters.add(deviceIdFilter);

        ArrayList<EntityDataRecord> theResponse = rampmeterInventory.getEntityElements(theFilters);

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
     * Each RAMPMETER-Inventory-Item is assigned an item index. This index is
     * used to indicate the relative order of the RAMPMETER Inventory items.
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
                case RAMPMETERINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = rampmeterInventory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{RAMPMETERSTATUS,RAMPMETERCONTROLSCHEDULE, RAMPMETERPLANINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterInventoryUpdate");
                    if (updatedTypes.contains(RAMPMETERSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterStatusUpdate");
                    if (updatedTypes.contains(RAMPMETERCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterControlScheduleUpdate");                        
                    if (updatedTypes.contains(RAMPMETERPLANINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterPlanInventoryUpdate");                        
                    break;
                case RAMPMETERSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = rampmeterStatus.addEntity(entityId, new EntityEmulationData.EntityDataType[]{RAMPMETERINVENTORY,RAMPMETERCONTROLSCHEDULE, RAMPMETERPLANINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterStatusUpdate");
                    if (statusUpdatedTypes.contains(RAMPMETERINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterInventoryUpdate");
                    if (statusUpdatedTypes.contains(RAMPMETERCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterControlScheduleUpdate");                        
                    if (statusUpdatedTypes.contains(RAMPMETERPLANINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterPlanInventoryUpdate");                        
                    break;
                case RAMPMETERCONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = rampmeterControlSchedule.addEntity(entityId, new EntityEmulationData.EntityDataType[]{RAMPMETERSTATUS,RAMPMETERINVENTORY, RAMPMETERPLANINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(RAMPMETERSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterStatusUpdate");
                    if (controlUpdatedTypes.contains(RAMPMETERINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterInventoryUpdate");                        
                    if (controlUpdatedTypes.contains(RAMPMETERPLANINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterPlanInventoryUpdate");                        
                    break;
                case RAMPMETERPLANINVENTORY:
                    List<EntityEmulationData.EntityDataType> planUpdatedTypes = rampmeterPlanInventory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{RAMPMETERSTATUS,RAMPMETERCONTROLSCHEDULE, RAMPMETERINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterPlanInventoryUpdate");
                    if (planUpdatedTypes.contains(RAMPMETERSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterStatusUpdate");
                    if (planUpdatedTypes.contains(RAMPMETERCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterControlScheduleUpdate");                        
                    if (planUpdatedTypes.contains(RAMPMETERINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterInventoryUpdate");                        
                    break;
                default:
                    throw new EntityEmulationException("The RampMeterHandler is unable to process data type " + entityDataType);
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
                case RAMPMETERINVENTORY:
                    rampmeterInventory.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterInventoryUpdate");
                    break;
                case RAMPMETERSTATUS:
                    rampmeterStatus.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterStatusUpdate");
                    break;
                case RAMPMETERCONTROLSCHEDULE:
                    rampmeterControlSchedule.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterControlScheduleUpdate");
                    break;
                case RAMPMETERPLANINVENTORY:
                    rampmeterPlanInventory.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterPlanInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The RampMeterHandler is unable to process data type " + entityDataType);
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
                case RAMPMETERINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = rampmeterInventory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{RAMPMETERSTATUS,RAMPMETERCONTROLSCHEDULE, RAMPMETERPLANINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterInventoryUpdate");
                    if (updatedTypes.contains(RAMPMETERSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterStatusUpdate");
                    if (updatedTypes.contains(RAMPMETERCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterControlScheduleUpdate");                        
                    if (updatedTypes.contains(RAMPMETERPLANINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterPlanInventoryUpdate");                        
                    break;
                case RAMPMETERSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = rampmeterStatus.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{RAMPMETERINVENTORY,RAMPMETERCONTROLSCHEDULE, RAMPMETERPLANINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterStatusUpdate");
                    if (statusUpdatedTypes.contains(RAMPMETERINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterInventoryUpdate");
                    if (statusUpdatedTypes.contains(RAMPMETERCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterControlScheduleUpdate");                        
                    if (statusUpdatedTypes.contains(RAMPMETERPLANINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterPlanInventoryUpdate");                        
                    break;
                case RAMPMETERCONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = rampmeterControlSchedule.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{RAMPMETERSTATUS,RAMPMETERINVENTORY, RAMPMETERPLANINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(RAMPMETERSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterStatusUpdate");
                    if (controlUpdatedTypes.contains(RAMPMETERINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterInventoryUpdate");                        
                    if (controlUpdatedTypes.contains(RAMPMETERPLANINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterPlanInventoryUpdate");                        
                    break;
                case RAMPMETERPLANINVENTORY:
                    List<EntityEmulationData.EntityDataType> planUpdatedTypes = rampmeterPlanInventory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{RAMPMETERSTATUS,RAMPMETERCONTROLSCHEDULE, RAMPMETERINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterPlanInventoryUpdate");
                    if (planUpdatedTypes.contains(RAMPMETERSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterStatusUpdate");
                    if (planUpdatedTypes.contains(RAMPMETERCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterControlScheduleUpdate");                        
                    if (planUpdatedTypes.contains(RAMPMETERINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterInventoryUpdate");          
                    break;
                default:
                    throw new EntityEmulationException("The RampMeterHandler is unable to process data type " + entityDataType);
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
                case RAMPMETERINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = rampmeterInventory.updateEntityElement(new EntityEmulationData.EntityDataType[]{RAMPMETERSTATUS,RAMPMETERCONTROLSCHEDULE, RAMPMETERPLANINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterInventoryUpdate");
                    if (updatedTypes.contains(RAMPMETERSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterStatusUpdate");
                    if (updatedTypes.contains(RAMPMETERCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterControlScheduleUpdate");                        
                    if (updatedTypes.contains(RAMPMETERPLANINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterPlanInventoryUpdate");                        
                    break;
                case RAMPMETERSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = rampmeterStatus.updateEntityElement(new EntityEmulationData.EntityDataType[]{RAMPMETERINVENTORY,RAMPMETERCONTROLSCHEDULE, RAMPMETERPLANINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterStatusUpdate");
                    if (statusUpdatedTypes.contains(RAMPMETERINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterInventoryUpdate");
                    if (statusUpdatedTypes.contains(RAMPMETERCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterControlScheduleUpdate");                        
                    if (statusUpdatedTypes.contains(RAMPMETERPLANINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterPlanInventoryUpdate");                        
                    break;
                case RAMPMETERCONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = rampmeterControlSchedule.updateEntityElement(new EntityEmulationData.EntityDataType[]{RAMPMETERSTATUS,RAMPMETERINVENTORY, RAMPMETERPLANINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(RAMPMETERSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterStatusUpdate");
                    if (controlUpdatedTypes.contains(RAMPMETERINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterInventoryUpdate");                        
                    if (controlUpdatedTypes.contains(RAMPMETERPLANINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterPlanInventoryUpdate");                        
                    break;
                case RAMPMETERPLANINVENTORY:
                    List<EntityEmulationData.EntityDataType> planUpdatedTypes = rampmeterPlanInventory.updateEntityElement(new EntityEmulationData.EntityDataType[]{RAMPMETERSTATUS,RAMPMETERCONTROLSCHEDULE, RAMPMETERINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterPlanInventoryUpdate");
                    if (planUpdatedTypes.contains(RAMPMETERSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterStatusUpdate");
                    if (planUpdatedTypes.contains(RAMPMETERCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterControlScheduleUpdate");                        
                    if (planUpdatedTypes.contains(RAMPMETERINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterInventoryUpdate");          
                    break;
                default:
                    throw new EntityEmulationException("The RampMeterHandler is unable to process data type " + entityDataType);
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
                case RAMPMETERINVENTORY:
                    rampmeterInventory.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterInventoryUpdate");
                    break;
                case RAMPMETERSTATUS:
                    rampmeterStatus.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterStatusUpdate");
                    break;
                case RAMPMETERCONTROLSCHEDULE:
                    rampmeterControlSchedule.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterControlScheduleUpdate");
                    break;
                case RAMPMETERPLANINVENTORY:
                    rampmeterPlanInventory.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlRampMeterPlanInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The RampMeterHandler is unable to process data type " + entityDataType);
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
                case RAMPMETERINVENTORY:
                    entityValue = rampmeterInventory.getEntityElementValue(entityId, entityElement);
                    break;
                case RAMPMETERSTATUS:
                    entityValue = rampmeterStatus.getEntityElementValue(entityId, entityElement);
                    break;
                case RAMPMETERCONTROLSCHEDULE:
                    entityValue = rampmeterControlSchedule.getEntityElementValue(entityId, entityElement);
                    break;
                case RAMPMETERPLANINVENTORY:
                    entityValue = rampmeterPlanInventory.getEntityElementValue(entityId, entityElement);
                    break;
                default:
                    throw new EntityEmulationException("The RampMeterHandler is unable to process data type " + entityDataType);
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
        String orgId = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.device-control-request-header.request-id");

        
        // Retrieve the desired RAMPMETER Request Command
        String rampmeterRequestCommand = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.rampmeter-request-command");
        
        // Select the paramaters associated with the requested command
        String rampmeterRequestParameter = "";

        // Set the command control status 
        String rampmeterCommandControlStatus = "other";        

        try {
            switch (rampmeterRequestCommand) {
                case "1":
                case "dark":
                    rampmeterRequestParameter = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.metered-lane-list.metered-lane.meter-command-parameters.meter-requested-plan");
                    updateEntityElement(RAMPMETERSTATUS, entityId, "metered-status-list.metered-lane.meter-implemented-plan", rampmeterRequestParameter);
                    break;
                case "2":
                case "restInGreen":
                    rampmeterRequestParameter = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.metered-lane-list.metered-lane.meter-command-parameters.meter-requested-plan");
                    updateEntityElement(RAMPMETERSTATUS, entityId, "metered-status-list.metered-lane.meter-implemented-plan", rampmeterRequestParameter);
                    break;
                case "3":
                case "fixedRate":
                    rampmeterRequestParameter = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.metered-lane-list.metered-lane.meter-command-parameters.meter-requested-rate");
                    updateEntityElement(RAMPMETERSTATUS, entityId, "metered-status-list.metered-lane.meter-implemented-rate", rampmeterRequestParameter);
                    break;
                case "4":
                case "trafficResponsive":
                    rampmeterRequestParameter = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.metered-lane-list.metered-lane.meter-command-parameters.meter-requested-plan");
                    updateEntityElement(RAMPMETERSTATUS, entityId, "metered-status-list.metered-lane.meter-implemented-plan", rampmeterRequestParameter);
                    break;
                case "5":
                case "emergencyGreen":
                    rampmeterRequestParameter = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.metered-lane-list.metered-lane.meter-command-parameters.meter-requested-plan");
                    updateEntityElement(RAMPMETERSTATUS, entityId, "metered-status-list.metered-lane.meter-implemented-plan", rampmeterRequestParameter);
                    break;
                default:
                    break;
            }
            
            
            rampmeterCommandControlStatus = "requested changes completed";
        } catch (InvalidValueException ex) {
            rampmeterCommandControlStatus = "request rejected invalid command parameters";
        } catch (EntityEmulationException ex){
            rampmeterCommandControlStatus = "other";            
        }

        String operatorId = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.device-control-request-header.authentication.operator-id");

        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.RAMPMETER.name());
        statusRecord.setStatus(rampmeterCommandControlStatus);
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

        String orgId = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.device-control-request-header.request-id");

        String operatorId = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.device-control-request-header.authentication.operator-id");

        String priority = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.device-control-request-header.command-request-priority");

        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.RAMPMETER.name());
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

        String orgId = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:rampMeterControlRequestMsg.device-control-request-header.request-id");

        // get the matching control Request Status record
        EntityControlRequestStatusRecord statusRecord = EntityCommandQueue.getInstance().getControlRequestStatus(orgId, entityId, requestId);
        TMDDEntityType.EntityType entityType = TMDDEntityType.EntityType.valueOf(statusRecord.getEntityType());

        return TMDDMessageProcessor.createControlResponseMessage(entityType, statusRecord, orgInfoDataCollector);

    }
  
}
