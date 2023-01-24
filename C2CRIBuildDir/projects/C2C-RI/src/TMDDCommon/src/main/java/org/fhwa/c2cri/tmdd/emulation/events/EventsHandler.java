/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.events;

import java.util.ArrayList;
import java.util.List;
import org.fhwa.c2cri.centermodel.RIEmulation;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.tmdd.TMDDAuthenticationProcessor;
import org.fhwa.c2cri.tmdd.TMDDErrorResponseProcessor;
import org.fhwa.c2cri.tmdd.emulation.TMDDEntityType;
import org.fhwa.c2cri.tmdd.emulation.TMDDMessageContentGenerator;
import org.fhwa.c2cri.tmdd.emulation.TMDDNRTMSelections;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityCommandProcessor;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatusCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatusRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataOrganizationInformationCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.EVENTSACTIONLOG;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.EVENTSINDEX;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.EVENTSUPDATES;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationRequests;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityProcessingSpecification;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.DataFilter;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.ValueInSetFilter;
import org.fhwa.c2cri.tmdd.emulation.events.dialogs.DlActionLogRequest;
import org.fhwa.c2cri.tmdd.emulation.events.dialogs.DlActionLogSubscription;
import org.fhwa.c2cri.tmdd.emulation.events.dialogs.DlActionLogUpdate;
import org.fhwa.c2cri.tmdd.emulation.events.dialogs.DlEventIndexRequest;
import org.fhwa.c2cri.tmdd.emulation.events.dialogs.DlEventIndexSubscription;
import org.fhwa.c2cri.tmdd.emulation.events.dialogs.DlEventIndexUpdate;
import org.fhwa.c2cri.tmdd.emulation.events.dialogs.DlFullEventUpdateRequest;
import org.fhwa.c2cri.tmdd.emulation.events.dialogs.DlFullEventUpdateSubscription;
import org.fhwa.c2cri.tmdd.emulation.events.dialogs.DlFullEventUpdateUpdate;
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

/**
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class EventsHandler implements EntityControlRequestStatusCollector, EntityCommandProcessor {

    private EventsIndex eventsIndex = new EventsIndex(EVENTSINDEX);
    private EventsUpdates eventsUpdates = new EventsUpdates(EVENTSUPDATES);
    private EventsActionLog eventsActionLog = new EventsActionLog(EVENTSACTIONLOG);
    private EntityDataOrganizationInformationCollector orgInfoDataCollector;

    private EventsHandler() {
    }

    ;
    
    public EventsHandler(EntityDataOrganizationInformationCollector orgInfoDataCollector) {
        this.orgInfoDataCollector = orgInfoDataCollector;
    }

    public void initialize(EntityEmulationData.EntityDataType entityDataType, byte[] byteArray) throws EntityEmulationException {
        switch (entityDataType) {
            case EVENTSINDEX:
                eventsIndex.initialize(byteArray);
                break;
            case EVENTSUPDATES:
                eventsUpdates.initialize(byteArray);
                break;
            case EVENTSACTIONLOG:
                eventsActionLog.initialize(byteArray);
                break;
            default:
                throw new EntityEmulationException("The EventsHandler is unable to initialize data type " + entityDataType);
        }
    }

    public MessageSpecification getRRResponse(String dialog, MessageSpecification requestMessage) {
        MessageSpecification responseMsg = null;
        switch (dialog) {
            case "dlFullEventUpdateRequest":
                DlFullEventUpdateRequest dlFullEventUpdateRequestProcessor = new DlFullEventUpdateRequest();
                responseMsg = dlFullEventUpdateRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG, TMDDEntityType.EntityType.EVENT, EVENTSUPDATES, eventsUpdates, this);
                break;
            case "dlActionLogRequest":
                DlActionLogRequest dlActionLogRequestProcessor = new DlActionLogRequest();
                responseMsg = dlActionLogRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG, TMDDEntityType.EntityType.EVENT, EVENTSACTIONLOG, eventsActionLog, this);
                break;
            case "dlEventIndexRequest":
                DlEventIndexRequest dlEventIndexRequestProcessor = new DlEventIndexRequest();
                responseMsg = dlEventIndexRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG, TMDDEntityType.EntityType.EVENT, EVENTSINDEX, eventsIndex, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the EVENTHandler.");
                break;
        }

        return responseMsg;
    }

    public MessageSpecification getSubResponse(String dialog, MessageSpecification requestMessage) {
        MessageSpecification responseMsg = null;

        switch (dialog) {
            case "dlFullEventUpdateSubscription":
                DlFullEventUpdateSubscription dlFullEventUpdateSubscriptionProcessor = new DlFullEventUpdateSubscription();
                responseMsg = dlFullEventUpdateSubscriptionProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG, TMDDEntityType.EntityType.EVENT, EVENTSUPDATES, eventsUpdates, this);
                break;
            case "dlEventIndexSubscription":
                DlEventIndexSubscription dlEventIndexSubscriptionProcessor = new DlEventIndexSubscription();
                responseMsg = dlEventIndexSubscriptionProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG, TMDDEntityType.EntityType.EVENT, EVENTSINDEX, eventsIndex, this);
                break;
            case "dlActionLogSubscription":
                DlActionLogSubscription dlActionLogSubscriptionProcessor = new DlActionLogSubscription();
                responseMsg = dlActionLogSubscriptionProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG, TMDDEntityType.EntityType.EVENT, EVENTSACTIONLOG, eventsActionLog, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the EVENTHandler.");
                break;
        }

        return responseMsg;
    }

    public MessageSpecification getPubResponse(String dialog, MessageSpecification requestMessage) {
        MessageSpecification responseMsg = null;

        switch (dialog) {
            case "dlFullEventUpdateUpdate":
                DlFullEventUpdateUpdate dlFullEventUpdateUpdateProcessor = new DlFullEventUpdateUpdate();
                responseMsg = dlFullEventUpdateUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG, TMDDEntityType.EntityType.EVENT, EVENTSUPDATES, eventsUpdates, this);
                break;
            case "dlEventIndexUpdate":
                DlEventIndexUpdate dlEventIndexUpdateProcessor = new DlEventIndexUpdate();
                responseMsg = dlEventIndexUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG, TMDDEntityType.EntityType.EVENT, EVENTSINDEX, eventsIndex, this);
                break;
            case "dlActionLogUpdate":
                DlActionLogUpdate dlActionLogUpdateProcessor = new DlActionLogUpdate();
                responseMsg = dlActionLogUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG, TMDDEntityType.EntityType.EVENT, EVENTSACTIONLOG, eventsActionLog, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the EVENTHandler.");
                break;
        }

        return responseMsg;
    }

    public MessageSpecification getEntityOrganizationInformation(String entityId) throws NoMatchingDataException, EntityEmulationException {
        ArrayList<DataFilter> theFilters = new ArrayList();
        MessageSpecification responseMsg = null;

        // device-id filter
        ArrayList<String> deviceIdFilterValues = new ArrayList<>();
        deviceIdFilterValues.add(entityId);
        ValueInSetFilter deviceIdFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.EVENTSINDEX, "EntityId", deviceIdFilterValues);
        theFilters.add(deviceIdFilter);

        ArrayList<EntityDataRecord> theResponse = eventsIndex.getEntityElements(theFilters);

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
     * Each EVENT-Inventory-Item is assigned an item index. This index is used
     * to indicate the relative order of the EVENT Inventory items.
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
                case EVENTSINDEX:
//                    eventsIndex.processAdd(entityId);

                    String actionLogIndex = eventsIndex.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSACTIONLOG, entityId);
                    EntityProcessingSpecification actionSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSACTIONLOG, actionLogIndex,
                                    "EventId",entityId);
                    String eventsUpdatesIndex = eventsIndex.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSUPDATES, entityId);
                    EntityProcessingSpecification updatesSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSUPDATES, eventsUpdatesIndex,
                                    "EventId",entityId);
                    List<EntityEmulationData.EntityDataType> updatedTypes = eventsIndex.addEntity( new EntityProcessingSpecification[]{updatesSpec, actionSpec},entityId);
//                    List<EntityEmulationData.EntityDataType> updatedTypes = eventsIndex.addEntity(entityId, new EntityEmulationData.EntityDataType[]{EVENTSUPDATES,EVENTSACTIONLOG});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlEventIndexUpdate");
                    if (updatedTypes.contains(EVENTSUPDATES))RIEmulation.getInstance().operationRelatedDataUpdate("dlFullEventUpdateUpdate");
                    if (updatedTypes.contains(EVENTSACTIONLOG))RIEmulation.getInstance().operationRelatedDataUpdate("dlActionLogUpdate");                        
                    break;
                case EVENTSUPDATES:

                    String eventsIdx = eventsUpdates.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSINDEX, entityId);
                    EntityProcessingSpecification indexSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSINDEX, eventsIdx);
                    String actionLogIdx = eventsUpdates.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSACTIONLOG, eventsIdx);
                    EntityProcessingSpecification actionLogSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSACTIONLOG, actionLogIdx,
                                    "EventId",eventsIdx);

                    List<EntityEmulationData.EntityDataType> eventUpdatedTypes = eventsUpdates.addEntity( new EntityProcessingSpecification[]{indexSpec, actionLogSpec},entityId);
                                        
//                    eventsUpdates.addEntity(entityId);
//                    List<EntityEmulationData.EntityDataType> eventUpdatedTypes = eventsUpdates.addEntity(entityId, new EntityEmulationData.EntityDataType[]{EVENTSINDEX,EVENTSACTIONLOG});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlFullEventUpdateUpdate");
                    if (eventUpdatedTypes.contains(EVENTSINDEX))RIEmulation.getInstance().operationRelatedDataUpdate("dlEventIndexUpdate");
                    if (eventUpdatedTypes.contains(EVENTSACTIONLOG))RIEmulation.getInstance().operationRelatedDataUpdate("dlActionLogUpdate");                        
                    break;
                case EVENTSACTIONLOG:

                    String eventIdx = eventsActionLog.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSINDEX, entityId);
                    EntityProcessingSpecification eventsSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSINDEX, eventIdx);
                    String updateLogIdx = eventsActionLog.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSUPDATES, eventIdx);
                    EntityProcessingSpecification updatesLogSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSUPDATES, updateLogIdx,
                                    "EventId",eventIdx);

                    List<EntityEmulationData.EntityDataType> actionUpdatedTypes = eventsActionLog.addEntity( new EntityProcessingSpecification[]{eventsSpec, updatesLogSpec},entityId);
//                    eventsActionLog.addEntity(entityId);
//                    List<EntityEmulationData.EntityDataType> actionUpdatedTypes = eventsActionLog.addEntity(entityId, new EntityEmulationData.EntityDataType[]{EVENTSUPDATES,EVENTSINDEX});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlActionLogUpdate");
                    if (actionUpdatedTypes.contains(EVENTSINDEX))RIEmulation.getInstance().operationRelatedDataUpdate("dlEventIndexUpdate");
                    if (actionUpdatedTypes.contains(EVENTSUPDATES))RIEmulation.getInstance().operationRelatedDataUpdate("dlFullEventUpdateUpdate");                        
                    break;
                default:
                    throw new EntityEmulationException("The EventsHandler is unable to process data type " + entityDataType);
            }

            // Notify Listeners for the data type
        } catch (InvalidValueException ex) {
            throw new InvalidEntityIdValueException(ex);
        } catch (DuplicateEntityIdException ex) {
            throw ex;
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex.getMessage());
        }
    }

    public void addEntityElement(EntityEmulationData.EntityDataType entityDataType, String entityId, String elementName, String elementValue) throws InvalidEntityIdValueException, InvalidValueException {
        try {
            switch (entityDataType) {
                case EVENTSINDEX:
                    eventsIndex.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlEventIndexUpdate");
                    break;
                case EVENTSUPDATES:
                    eventsUpdates.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlFullEventUpdateUpdate");
                    break;
                case EVENTSACTIONLOG:
                    eventsActionLog.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlActionLogUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The EventsHandler is unable to process data type " + entityDataType);
            }
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex.getMessage());
        }

    }

    public void deleteEntity(EntityEmulationData.EntityDataType entityDataType, String entityId) throws InvalidEntityIdValueException {
        try {
            switch (entityDataType) {
                case EVENTSINDEX:
                    String actionLogIndex = eventsIndex.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSACTIONLOG, entityId);
                    EntityProcessingSpecification actionSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSACTIONLOG, actionLogIndex);
                    String eventsUpdatesIndex = eventsIndex.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSUPDATES, entityId);
                    EntityProcessingSpecification updatesSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSUPDATES, eventsUpdatesIndex);
                    List<EntityEmulationData.EntityDataType> updatedTypes = eventsIndex.deleteEntity(entityId, new EntityProcessingSpecification[]{updatesSpec, actionSpec});
//                    List<EntityEmulationData.EntityDataType> updatedTypes = eventsIndex.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{EVENTSUPDATES,EVENTSACTIONLOG});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlEventIndexUpdate");
                    if (updatedTypes.contains(EVENTSUPDATES))RIEmulation.getInstance().operationRelatedDataUpdate("dlFullEventUpdateUpdate");
                    if (updatedTypes.contains(EVENTSACTIONLOG))RIEmulation.getInstance().operationRelatedDataUpdate("dlActionLogUpdate");                        
                    break;
                case EVENTSUPDATES:
                    String eventsIdx = eventsUpdates.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSINDEX, entityId);
                    EntityProcessingSpecification indexSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSINDEX, eventsIdx);
                    String actionLogIdx = eventsUpdates.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSACTIONLOG, entityId);
                    EntityProcessingSpecification actionLogSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSACTIONLOG, actionLogIdx);
                    List<EntityEmulationData.EntityDataType> eventUpdatedTypes = eventsUpdates.deleteEntity( entityId, new EntityProcessingSpecification[]{indexSpec, actionLogSpec});
       //             List<EntityEmulationData.EntityDataType> eventUpdatedTypes = eventsUpdates.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{EVENTSINDEX,EVENTSACTIONLOG});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlFullEventUpdateUpdate");
                    if (eventUpdatedTypes.contains(EVENTSINDEX))RIEmulation.getInstance().operationRelatedDataUpdate("dlEventIndexUpdate");
                    if (eventUpdatedTypes.contains(EVENTSACTIONLOG))RIEmulation.getInstance().operationRelatedDataUpdate("dlActionLogUpdate");                        
                    break;
                case EVENTSACTIONLOG:
                    String eventIdx = eventsActionLog.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSINDEX, entityId);
                    EntityProcessingSpecification eventsSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSINDEX, eventIdx);
                    String updateLogIdx = eventsActionLog.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSUPDATES, entityId);
                    EntityProcessingSpecification updatesLogSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSUPDATES, updateLogIdx);

                    List<EntityEmulationData.EntityDataType> actionUpdatedTypes = eventsActionLog.deleteEntity(entityId, new EntityProcessingSpecification[]{eventsSpec, updatesLogSpec});
       //             List<EntityEmulationData.EntityDataType> actionUpdatedTypes = eventsActionLog.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{EVENTSUPDATES,EVENTSINDEX});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlActionLogUpdate");
                    if (actionUpdatedTypes.contains(EVENTSINDEX))RIEmulation.getInstance().operationRelatedDataUpdate("dlEventIndexUpdate");
                    if (actionUpdatedTypes.contains(EVENTSUPDATES))RIEmulation.getInstance().operationRelatedDataUpdate("dlFullEventUpdateUpdate");     
                    break;
                default:
                    throw new EntityEmulationException("The EventsHandler is unable to process data type " + entityDataType);
            }
        } catch (InvalidValueException ex) {
            throw new InvalidEntityIdValueException(ex.getMessage());
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex.getMessage());
        }

    }

    public void updateEntityElement(EntityEmulationData.EntityDataType entityDataType, String entityId, String entityElementName, String entityElementValue) throws InvalidEntityIdValueException, InvalidValueException {
        try {
            switch (entityDataType) {
                case EVENTSINDEX:
                    if (eventsIndex.isElementTypeMatch(entityDataType, entityElementName, entityId, "EntityId")){
                        String actionLogIndex = eventsIndex.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSACTIONLOG, entityId);
                        EntityProcessingSpecification actionSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSACTIONLOG, actionLogIndex,
                                        "EventId",entityId);
                        String eventsUpdatesIndex = eventsIndex.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSUPDATES, entityId);
                        EntityProcessingSpecification updatesSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSUPDATES, eventsUpdatesIndex,
                                        "EventId",entityId);
                        List<EntityEmulationData.EntityDataType> updatedTypes = eventsIndex.updateEntityElement(new EntityProcessingSpecification[]{updatesSpec, actionSpec}, entityId, entityElementName, entityElementValue);

                        RIEmulation.getInstance().operationRelatedDataUpdate("dlEventIndexUpdate");
                        if (updatedTypes.contains(EVENTSUPDATES))RIEmulation.getInstance().operationRelatedDataUpdate("dlFullEventUpdateUpdate");
                        if (updatedTypes.contains(EVENTSACTIONLOG))RIEmulation.getInstance().operationRelatedDataUpdate("dlActionLogUpdate");     
                    } else {
                        List<EntityEmulationData.EntityDataType> updatedTypes = eventsIndex.updateEntityElement(new EntityEmulationData.EntityDataType[]{}, entityId, entityElementName, entityElementValue);
                        RIEmulation.getInstance().operationRelatedDataUpdate("dlEventIndexUpdate");                        
                    }                    
                    break;
                case EVENTSUPDATES:
                    if (eventsUpdates.isElementTypeMatch(entityDataType, entityElementName, entityId, "EventId")){
                        String eventsIdx = eventsUpdates.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSINDEX, entityId);
                        EntityProcessingSpecification indexSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSINDEX, eventsIdx);
                        String actionLogIdx = eventsUpdates.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSACTIONLOG, eventsIdx);
                        EntityProcessingSpecification actionLogSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSACTIONLOG, actionLogIdx,
                                        "EventId",eventsIdx);
                        List<EntityEmulationData.EntityDataType> eventUpdatedTypes = eventsUpdates.updateEntityElement( new EntityProcessingSpecification[]{indexSpec, actionLogSpec}, entityId, entityElementName, entityElementValue);
    //                    List<EntityEmulationData.EntityDataType> eventUpdatedTypes = eventsUpdates.updateEntityElement(new EntityEmulationData.EntityDataType[]{EVENTSINDEX,EVENTSACTIONLOG}, entityId, entityElementName, entityElementValue);
                        RIEmulation.getInstance().operationRelatedDataUpdate("dlFullEventUpdateUpdate");
                        if (eventUpdatedTypes.contains(EVENTSINDEX))RIEmulation.getInstance().operationRelatedDataUpdate("dlEventIndexUpdate");
                        if (eventUpdatedTypes.contains(EVENTSACTIONLOG))RIEmulation.getInstance().operationRelatedDataUpdate("dlActionLogUpdate");  
                    } else {
                        List<EntityEmulationData.EntityDataType> eventUpdatedTypes = eventsUpdates.updateEntityElement(new EntityEmulationData.EntityDataType[]{}, entityId, entityElementName, entityElementValue);
                        RIEmulation.getInstance().operationRelatedDataUpdate("dlFullEventUpdateUpdate");                        
                    }
                    break;
                case EVENTSACTIONLOG:
                    if (eventsActionLog.isElementTypeMatch(entityDataType, entityElementName, entityId, "EventId")){
                        String eventIdx = eventsActionLog.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSINDEX, entityId);
                        EntityProcessingSpecification eventsSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSINDEX, eventIdx);
                        String updateLogIdx = eventsActionLog.getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType.EVENTSUPDATES, eventIdx);
                        EntityProcessingSpecification updatesLogSpec = new EntityProcessingSpecification(EntityEmulationData.EntityDataType.EVENTSUPDATES, updateLogIdx,
                                        "EventId",eventIdx);

                        List<EntityEmulationData.EntityDataType> actionUpdatedTypes = eventsActionLog.updateEntityElement(new EntityProcessingSpecification[]{eventsSpec, updatesLogSpec}, entityId, entityElementName, entityElementValue);
    //                    List<EntityEmulationData.EntityDataType> actionUpdatedTypes = eventsActionLog.updateEntityElement(new EntityEmulationData.EntityDataType[]{EVENTSUPDATES,EVENTSINDEX}, entityId, entityElementName, entityElementValue);
                        RIEmulation.getInstance().operationRelatedDataUpdate("dlActionLogUpdate");
                        if (actionUpdatedTypes.contains(EVENTSINDEX))RIEmulation.getInstance().operationRelatedDataUpdate("dlEventIndexUpdate");
                        if (actionUpdatedTypes.contains(EVENTSUPDATES))RIEmulation.getInstance().operationRelatedDataUpdate("dlFullEventUpdateUpdate");     
                    } else {
                        List<EntityEmulationData.EntityDataType> actionUpdatedTypes = eventsActionLog.updateEntityElement(new EntityEmulationData.EntityDataType[]{}, entityId, entityElementName, entityElementValue);
                        RIEmulation.getInstance().operationRelatedDataUpdate("dlActionLogUpdate");
                        
                    }
                    break;
                default:
                    throw new EntityEmulationException("The EventsHandler is unable to process data type " + entityDataType);
            }
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }

    public void deleteEntityElement(EntityEmulationData.EntityDataType entityDataType, String entityId, String entityElementName) throws InvalidEntityIdValueException, InvalidEntityElementException {
        try {
            switch (entityDataType) {
                case EVENTSINDEX:
                    eventsIndex.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlEventIndexUpdate");
                    break;
                case EVENTSUPDATES:
                    eventsUpdates.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlFullEventUpdateUpdate");
                    break;
                case EVENTSACTIONLOG:
                    eventsActionLog.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlActionLogUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The EventsHandler is unable to process data type " + entityDataType);
            }
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }

    public String getEntityElementValue(EntityEmulationData.EntityDataType entityDataType, String entityId, String entityElement) throws InvalidEntityIdValueException, InvalidEntityElementException, EntityEmulationException {
        String entityValue = "";
        try {
            switch (entityDataType) {
                case EVENTSINDEX:
                    entityValue = eventsIndex.getEntityElementValue(entityId, entityElement);
                    break;
                case EVENTSUPDATES:
                    entityValue = eventsUpdates.getEntityElementValue(entityId, entityElement);
                    break;
                case EVENTSACTIONLOG:
                    entityValue = eventsActionLog.getEntityElementValue(entityId, entityElement);
                    break;
                default:
                    throw new EntityEmulationException("The EventsHandler is unable to process data type " + entityDataType);
            }
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex.getMessage());
        }

        return entityValue;
    }

    @Override
    public EntityControlRequestStatusRecord getControlRequestStatus(String orgId, String entityId, String requestId) throws EntityEmulationException {
        throw new EntityEmulationException("Events do not support commands.");
    }

    @Override
    public void updateControlRequestStatus(EntityControlRequestStatusRecord statusRecord, MessageSpecification requestMsg) throws EntityEmulationException {
        throw new EntityEmulationException("Events do not support commands.");
    }

    @Override
    public void verifyCommand(MessageSpecification commandMessage) throws InvalidCommandException, EntityEmulationException {
        throw new EntityEmulationException("Events do not support commands.");
    }

    @Override
    public void executeCommand(MessageSpecification commandMessage) throws EntityEmulationException {
        throw new EntityEmulationException("Events do not support commands.");
    }

    @Override
    public void queueCommand(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, InvalidEntityControlRequestLogException, EntityEmulationException {
        throw new EntityEmulationException("Events do not support commands.");
    }

    @Override
    public MessageSpecification getControlResponseMessage(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, EntityEmulationException {
        throw new EntityEmulationException("Events do not support commands.");
    }
    
}
