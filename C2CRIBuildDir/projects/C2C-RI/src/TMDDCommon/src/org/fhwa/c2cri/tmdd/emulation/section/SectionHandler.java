/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.section;

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
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.SECTIONCONTROLSCHEDULE;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.SECTIONSIGNALTIMINGPATTERNINVENTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.SECTIONSTATUS;
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
import org.fhwa.c2cri.tmdd.emulation.section.dialogs.DlSectionControlRequest;
import org.fhwa.c2cri.tmdd.emulation.section.dialogs.DlSectionControlScheduleRequest;
import org.fhwa.c2cri.tmdd.emulation.section.dialogs.DlSectionControlScheduleUpdate;
import org.fhwa.c2cri.tmdd.emulation.section.dialogs.DlSectionControlStatusRequest;
import org.fhwa.c2cri.tmdd.emulation.section.dialogs.DlSectionPriorityQueueRequest;
import org.fhwa.c2cri.tmdd.emulation.section.dialogs.DlSectionSignalTimingPatternInventoryRequest;
import org.fhwa.c2cri.tmdd.emulation.section.dialogs.DlSectionSignalTimingPatternInventorySubscription;
import org.fhwa.c2cri.tmdd.emulation.section.dialogs.DlSectionSignalTimingPatternInventoryUpdate;
import org.fhwa.c2cri.tmdd.emulation.section.dialogs.DlSectionStatusRequest;
import org.fhwa.c2cri.tmdd.emulation.section.dialogs.DlSectionStatusUpdate;

/**
 * The SECTIONHandler class coordinates all functions related to SECTION
 * entities.
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class SectionHandler implements EntityControlRequestStatusCollector, EntityCommandProcessor {

    /**
     * The status of all defined SECTION devices
     */
    private SectionStatus sectionStatus = new SectionStatus(EntityEmulationData.EntityDataType.SECTIONSTATUS);
    /**
     * The control schedule for all Sections
     */
    private SectionControlSchedule sectionControlSchedule = new SectionControlSchedule(EntityEmulationData.EntityDataType.SECTIONCONTROLSCHEDULE);
    /**
     * The control schedule for all Sections
     */
    private SectionSignalTimingPatternInventory sectionSignalTimingPatternInventory = new SectionSignalTimingPatternInventory(EntityEmulationData.EntityDataType.SECTIONSIGNALTIMINGPATTERNINVENTORY);
    /**
     * Provides organization information related to a specific entity.
     */
    private EntityDataOrganizationInformationCollector orgInfoDataCollector;

    private SectionHandler() {
    }

    ;
    
    /**
     * Main constructor method for the SECTIONHandler class.
     * 
     * @param orgInfoDataCollector reference to an organization information collector.
     */
    public SectionHandler(EntityDataOrganizationInformationCollector orgInfoDataCollector) {
        this.orgInfoDataCollector = orgInfoDataCollector;
    }

//  private EntityUpdateListeners (SECTIONStatus and SECTIONInventory)   
    /**
     * Initializes the data types related to a SECTION device.
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
            case SECTIONSTATUS:
                sectionStatus.initialize(byteArray);
                break;
            case SECTIONCONTROLSCHEDULE:
                sectionControlSchedule.initialize(byteArray);
                break;
            case SECTIONSIGNALTIMINGPATTERNINVENTORY:
                sectionSignalTimingPatternInventory.initialize(byteArray);
                break;
            default:
                throw new EntityEmulationException("The SectionHandler is unable to initialize data type " + entityDataType);
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
            case "dlSectionControlRequest":
                DlSectionControlRequest dlSectionControlRequestProcessor = new DlSectionControlRequest();
                responseMsg = dlSectionControlRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.SECTIONCONTROLREQUESTMSG, TMDDEntityType.EntityType.SECTION, SECTIONSTATUS, sectionStatus, this);
                break;
            case "dlSectionControlStatusRequest":
                DlSectionControlStatusRequest dlSectionControlStatusRequestProcessor = new DlSectionControlStatusRequest();
                responseMsg = dlSectionControlStatusRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.SECTIONCONTROLSTATUSREQUESTMSG, TMDDEntityType.EntityType.SECTION, SECTIONSTATUS, sectionStatus, this);
                break;
            case "dlSectionStatusRequest":
                DlSectionStatusRequest dlSectionStatusRequestProcessor = new DlSectionStatusRequest();
                responseMsg = dlSectionStatusRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.SECTION, SECTIONSTATUS, sectionStatus, this);
                break;
            case "dlSectionControlScheduleRequest":
                DlSectionControlScheduleRequest dlSectionControlScheduleRequestProcessor = new DlSectionControlScheduleRequest();
                responseMsg = dlSectionControlScheduleRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.SECTION, SECTIONCONTROLSCHEDULE, sectionControlSchedule, this);
                break;
            case "dlSectionPriorityQueueRequest":
                DlSectionPriorityQueueRequest dlSectionPriorityQueueRequestProcessor = new DlSectionPriorityQueueRequest();
                responseMsg = dlSectionPriorityQueueRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEPRIORITYQUEUEREQUESTMSG, TMDDEntityType.EntityType.SECTION, SECTIONSTATUS, sectionStatus, this);
                break;
            case "dlSectionSignalTimingPatternInventoryRequest":
                DlSectionSignalTimingPatternInventoryRequest dlSectionSignalTimingPatternInventoryRequestProcessor = new DlSectionSignalTimingPatternInventoryRequest();
                responseMsg = dlSectionSignalTimingPatternInventoryRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.SECTIONSIGNALTIMINGPATTERNINVENTORYREQUESTMSG, TMDDEntityType.EntityType.SECTION, SECTIONSIGNALTIMINGPATTERNINVENTORY, sectionSignalTimingPatternInventory, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the SectionHandler.");
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
            case "dlSectionSignalTimingPatternInventorySubscription":
                DlSectionSignalTimingPatternInventorySubscription dlSectionSignalTimingPatternInventorySubscriptionProcessor = new DlSectionSignalTimingPatternInventorySubscription();
                responseMsg = dlSectionSignalTimingPatternInventorySubscriptionProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.SECTIONSIGNALTIMINGPATTERNINVENTORYREQUESTMSG, TMDDEntityType.EntityType.SECTION, SECTIONSIGNALTIMINGPATTERNINVENTORY, sectionSignalTimingPatternInventory, this);
                break;
            case "dlDeviceInformationSubscription":
                String msgDeviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type"));
                switch (msgDeviceInformationType) {
                    case "2":
                    case "device status":
                        DlDeviceInformationSubscription sectionStatusDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = sectionStatusDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.SECTION, SECTIONSTATUS, sectionStatus, this);
                        break;
                    case "3":
                    case "device schedule":
                        DlDeviceInformationSubscription sectionScheduleDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = sectionScheduleDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.SECTION, SECTIONCONTROLSCHEDULE, sectionControlSchedule, this);
                        break;
                    default:
                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlDeviceInformationSubscription dialog.");
                }
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the SectionHandler.");
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
            case "dlSectionStatusUpdate":
                DlSectionStatusUpdate dlSectionStatusUpdateProcessor = new DlSectionStatusUpdate();
                responseMsg = dlSectionStatusUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.SECTION, SECTIONSTATUS, sectionStatus, this);
                break;
            case "dlSectionControlScheduleUpdate":
                DlSectionControlScheduleUpdate dlSectionControlScheduleUpdateProcessor = new DlSectionControlScheduleUpdate();
                responseMsg = dlSectionControlScheduleUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.SECTION, SECTIONCONTROLSCHEDULE, sectionControlSchedule, this);
                break;
            case "dlSectionSignalTimingPatternInventoryUpdate":
                DlSectionSignalTimingPatternInventoryUpdate dlSectionSignalTimingPatternInventoryUpdateProcessor = new DlSectionSignalTimingPatternInventoryUpdate();
                responseMsg = dlSectionSignalTimingPatternInventoryUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.SECTIONSIGNALTIMINGPATTERNINVENTORYREQUESTMSG, TMDDEntityType.EntityType.SECTION, SECTIONSIGNALTIMINGPATTERNINVENTORY, sectionSignalTimingPatternInventory, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the SectionHandler.");
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
        ValueInSetFilter deviceIdFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.SECTIONSTATUS, "EntityId", deviceIdFilterValues);
        theFilters.add(deviceIdFilter);

        ArrayList<EntityDataRecord> theResponse = sectionStatus.getEntityElements(theFilters);

        // Only return items that are part of the Organization Information Data Frame
        ArrayList<String> generatedResponse = new ArrayList();
        for (EntityDataRecord thisRecord : theResponse) {
            if (thisRecord.getEntityElement().contains("organization-information")) {
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
     * Each SECTION-Inventory-Item is assigned an item index. This index is used
     * to indicate the relative order of the SECTION Inventory items.
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
                case SECTIONSTATUS:
                    List<EntityEmulationData.EntityDataType> updatedTypes = sectionStatus.addEntity(entityId, new EntityEmulationData.EntityDataType[]{SECTIONCONTROLSCHEDULE,SECTIONSIGNALTIMINGPATTERNINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionStatusUpdate");
                    if (updatedTypes.contains(SECTIONCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionControlScheduleUpdate");
                    if (updatedTypes.contains(SECTIONSIGNALTIMINGPATTERNINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionSignalTimingPatternInventoryUpdate");                        
                    break;
                case SECTIONCONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = sectionControlSchedule.addEntity(entityId, new EntityEmulationData.EntityDataType[]{SECTIONSTATUS,SECTIONSIGNALTIMINGPATTERNINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(SECTIONSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionStatusUpdate");
                    if (controlUpdatedTypes.contains(SECTIONSIGNALTIMINGPATTERNINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionSignalTimingPatternInventoryUpdate");                        
                    break;
                case SECTIONSIGNALTIMINGPATTERNINVENTORY:
                    List<EntityEmulationData.EntityDataType> patternUpdatedTypes = sectionSignalTimingPatternInventory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{SECTIONCONTROLSCHEDULE,SECTIONSTATUS});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionSignalTimingPatternInventoryUpdate");
                    if (patternUpdatedTypes.contains(SECTIONCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionControlScheduleUpdate");
                    if (patternUpdatedTypes.contains(SECTIONSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionStatusUpdate");                        
                    break;
                default:
                    throw new EntityEmulationException("The SectionHandler is unable to process data type " + entityDataType);
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
                case SECTIONSTATUS:
                    sectionStatus.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionStatusUpdate");
                    break;
                case SECTIONCONTROLSCHEDULE:
                    sectionControlSchedule.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionControlScheduleUpdate");
                    break;
                case SECTIONSIGNALTIMINGPATTERNINVENTORY:
                    sectionSignalTimingPatternInventory.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionSignalTimingPatternInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The SectionHandler is unable to process data type " + entityDataType);
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
                case SECTIONSTATUS:
                    List<EntityEmulationData.EntityDataType> updatedTypes = sectionStatus.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{SECTIONCONTROLSCHEDULE,SECTIONSIGNALTIMINGPATTERNINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionStatusUpdate");
                    if (updatedTypes.contains(SECTIONCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionControlScheduleUpdate");
                    if (updatedTypes.contains(SECTIONSIGNALTIMINGPATTERNINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionSignalTimingPatternInventoryUpdate");                        
                    break;
                case SECTIONCONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = sectionControlSchedule.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{SECTIONSTATUS,SECTIONSIGNALTIMINGPATTERNINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(SECTIONSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionStatusUpdate");
                    if (controlUpdatedTypes.contains(SECTIONSIGNALTIMINGPATTERNINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionSignalTimingPatternInventoryUpdate");                        
                    break;
                case SECTIONSIGNALTIMINGPATTERNINVENTORY:
                    List<EntityEmulationData.EntityDataType> patternUpdatedTypes = sectionSignalTimingPatternInventory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{SECTIONCONTROLSCHEDULE,SECTIONSTATUS});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionSignalTimingPatternInventoryUpdate");
                    if (patternUpdatedTypes.contains(SECTIONCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionControlScheduleUpdate");
                    if (patternUpdatedTypes.contains(SECTIONSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionStatusUpdate");                        

                    break;
                default:
                    throw new EntityEmulationException("The SectionHandler is unable to process data type " + entityDataType);
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
                case SECTIONSTATUS:
                    List<EntityEmulationData.EntityDataType> updatedTypes = sectionStatus.updateEntityElement(new EntityEmulationData.EntityDataType[]{SECTIONCONTROLSCHEDULE,SECTIONSIGNALTIMINGPATTERNINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionStatusUpdate");
                    if (updatedTypes.contains(SECTIONCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionControlScheduleUpdate");
                    if (updatedTypes.contains(SECTIONSIGNALTIMINGPATTERNINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionSignalTimingPatternInventoryUpdate");                        
                    break;
                case SECTIONCONTROLSCHEDULE:
                    List<EntityEmulationData.EntityDataType> controlUpdatedTypes = sectionControlSchedule.updateEntityElement(new EntityEmulationData.EntityDataType[]{SECTIONSTATUS,SECTIONSIGNALTIMINGPATTERNINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionControlScheduleUpdate");
                    if (controlUpdatedTypes.contains(SECTIONSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionStatusUpdate");
                    if (controlUpdatedTypes.contains(SECTIONSIGNALTIMINGPATTERNINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionSignalTimingPatternInventoryUpdate");                        
                    break;
                case SECTIONSIGNALTIMINGPATTERNINVENTORY:
                    List<EntityEmulationData.EntityDataType> patternUpdatedTypes = sectionSignalTimingPatternInventory.updateEntityElement(new EntityEmulationData.EntityDataType[]{SECTIONCONTROLSCHEDULE,SECTIONSTATUS}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionSignalTimingPatternInventoryUpdate");
                    if (patternUpdatedTypes.contains(SECTIONCONTROLSCHEDULE))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionControlScheduleUpdate");
                    if (patternUpdatedTypes.contains(SECTIONSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionStatusUpdate");                        

                    break;
                default:
                    throw new EntityEmulationException("The SectionHandler is unable to process data type " + entityDataType);
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
                case SECTIONSTATUS:
                    sectionStatus.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionStatusUpdate");
                    break;
                case SECTIONCONTROLSCHEDULE:
                    sectionControlSchedule.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionControlScheduleUpdate");
                    break;
                case SECTIONSIGNALTIMINGPATTERNINVENTORY:
                    sectionSignalTimingPatternInventory.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlSectionSignalTimingPatternInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The SectionHandler is unable to process data type " + entityDataType);
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
                case SECTIONSTATUS:
                    entityValue = sectionStatus.getEntityElementValue(entityId, entityElement);
                    break;
                case SECTIONCONTROLSCHEDULE:
                    entityValue = sectionControlSchedule.getEntityElementValue(entityId, entityElement);
                    break;
                case SECTIONSIGNALTIMINGPATTERNINVENTORY:
                    entityValue = sectionSignalTimingPatternInventory.getEntityElementValue(entityId, entityElement);
                    break;
                default:
                    throw new EntityEmulationException("The SectionHandler is unable to process data type " + entityDataType);
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
        String orgId = commandMessage.getElementValue("tmdd:sectionControlRequestMsg.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:sectionControlRequestMsg.section-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:sectionControlRequestMsg.request-id");

        // Retrieve the desired Section Request Command
        String sectionRequestCommand = commandMessage.getElementValue("tmdd:sectionControlRequestMsg.section-request-command");

        // Select the paramaters associated with the requested command
        String sectionRequestParameter = "";

        // Set the command control status 
        String sectionCommandControlStatus = "other";

        try {
            switch (sectionRequestCommand) {
                case "1":
                case "change section timing mode":
                    sectionRequestParameter = commandMessage.getElementValue("tmdd:sectionControlRequestMsg.section-command-parameters.request-control-mode");
                    updateEntityElement(SECTIONSTATUS, entityId, "section-control-mode", sectionRequestParameter);
                    break;
                case "2":
                case "change section timing pattern":
                    sectionRequestParameter = commandMessage.getElementValue("tmdd:sectionControlRequestMsg.section-command-parameters.timing-pattern-id");
                    updateEntityElement(SECTIONSTATUS, entityId, "timing-pattern-id", sectionRequestParameter);
                    break;
                case "3":
                case "make offset adjustment":
                    // Don't see anything in the Section Status that matches this.
//                    sectionRequestParameter = commandMessage.getElementValue("tmdd:sectionControlRequestMsg.section-command-parameters.section-offset-adjustment");
//                    updateEntityElement(SECTIONSTATUS, entityId, "offset-time-current", sectionRequestParameter);
                    break;
                case "4":
                case "other":
                default:
                    break;
            }

            sectionCommandControlStatus = "requested changes completed";
        } catch (InvalidValueException ex) {
            sectionCommandControlStatus = "request rejected invalid command parameters";
        } catch (EntityEmulationException ex) {
            sectionCommandControlStatus = "other";
        }

        String operatorId = commandMessage.getElementValue("tmdd:sectionControlRequestMsg.authentication.operator-id");

        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.SECTION.name());
        statusRecord.setStatus(sectionCommandControlStatus);
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

        String orgId = commandMessage.getElementValue("tmdd:sectionControlRequestMsg.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:sectionControlRequestMsg.section-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:sectionControlRequestMsg.request-id");

        String operatorId = commandMessage.getElementValue("tmdd:sectionControlRequestMsg.authentication.operator-id");

        String priority = commandMessage.getElementValue("tmdd:sectionControlRequestMsg.command-request-priority");

        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.SECTION.name());
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
        String orgId = "";
        String entityId = "";
        String requestId = "";

        
        // This method handles both sectionControlRequestMsg and sectionControlStatusRequestMessages.
        if (commandMessage.containsMessageOfType("tmdd:sectionControlRequestMsg")) {
            orgId = commandMessage.getElementValue("tmdd:sectionControlRequestMsg.organization-requesting.organization-id");

            // get the entity ID from the message
            entityId = commandMessage.getElementValue("tmdd:sectionControlRequestMsg.section-id");

            // get the request ID from the message
            requestId = commandMessage.getElementValue("tmdd:sectionControlRequestMsg.request-id");
        } else if (commandMessage.containsMessageOfType("tmdd:sectionControlStatusRequestMsg")) {
            orgId = commandMessage.getElementValue("tmdd:sectionControlStatusRequestMsg.organization-requesting.organization-id");

            // get the entity ID from the message
            entityId = commandMessage.getElementValue("tmdd:sectionControlStatusRequestMsg.section-id");

            // get the request ID from the message
            requestId = commandMessage.getElementValue("tmdd:sectionControlStatusRequestMsg.request-id");
        }
        
        // get the matching control Request Status record
        EntityControlRequestStatusRecord statusRecord = EntityCommandQueue.getInstance().getControlRequestStatus(orgId, entityId, requestId);
        TMDDEntityType.EntityType entityType = TMDDEntityType.EntityType.valueOf(statusRecord.getEntityType());

        ArrayList<String> sectionControlResponseList = new ArrayList();
        // 
            sectionControlResponseList.add("tmdd:sectionControlResponseMsg.restrictions.organization-information-forwarding-restrictions = unrestricted");

            for (String thisElement : TMDDMessageProcessor.createControlResponseMessage(entityType, statusRecord, orgInfoDataCollector).getMessageSpec()) {

                // replace tmdd:deviceControlResponse elements with the appropriate sectionControlResponse Elements
                thisElement = thisElement.replace("tmdd:deviceControlResponseMsg", "tmdd:sectionControlResponseMsg").replace(".device-id", ".section-id");

                // Add all elements that come from DeviceControlResponse except the operator-lock-id
                if (!thisElement.contains("operator-lock-id")) {

                    // Handle the change in forwarding restrictions structure
                    if (!thisElement.startsWith("tmdd:sectionControlResponseMsg.organization-information-forwarding-restrictions")) {
                        thisElement = thisElement.replace("tmdd:sectionControlResponseMsg.organization-information-forwarding-restrictions", "tmdd:sectionControlResponseMsg.restrictions.organization-information-forwarding-restrictions");
                        sectionControlResponseList.add(thisElement);
                    }

                }
            }
            // Add request-control-mode
            String requestControlMode = commandMessage.getElementValue("tmdd:sectionControlRequest.section-command-parameters.request-control-mode");
            if ((requestControlMode != null) && (!requestControlMode.isEmpty())) {
                sectionControlResponseList.add("tmdd:sectionControlResponseMsg.request-control-mode = " + requestControlMode);
            }

            // Add timing-pattern-id
            String timingPatternId = commandMessage.getElementValue("tmdd:sectionControlRequest.section-command-parameters.timing-pattern-id");
            if ((timingPatternId != null) && (!timingPatternId.isEmpty())) {
                sectionControlResponseList.add("tmdd:sectionControlResponseMsg.timing-pattern-id = " + timingPatternId);
            }

        MessageSpecification sectionControlResponse = new MessageSpecification(sectionControlResponseList);

        return sectionControlResponse;

    }

}
