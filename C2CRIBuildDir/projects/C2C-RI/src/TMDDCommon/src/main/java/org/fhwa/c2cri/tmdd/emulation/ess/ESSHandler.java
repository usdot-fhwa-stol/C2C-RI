/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.ess;

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
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.ESSINVENTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.ESSOBSERVATIONMETADATA;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.ESSSTATUS;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationRequests;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.DataFilter;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.ValueInSetFilter;
import org.fhwa.c2cri.tmdd.emulation.ess.dialogs.DlESSInventoryRequest;
import org.fhwa.c2cri.tmdd.emulation.ess.dialogs.DlESSInventoryUpdate;
import org.fhwa.c2cri.tmdd.emulation.ess.dialogs.DlESSObservationMetadataRequest;
import org.fhwa.c2cri.tmdd.emulation.ess.dialogs.DlESSObservationReportRequest;
import org.fhwa.c2cri.tmdd.emulation.ess.dialogs.DlESSObservationReportUpdate;
import org.fhwa.c2cri.tmdd.emulation.ess.dialogs.DlESSStatusRequest;
import org.fhwa.c2cri.tmdd.emulation.ess.dialogs.DlESSStatusUpdate;
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
 * The ESSHandler class coordinates all functions related to ESS entities.
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class ESSHandler implements EntityControlRequestStatusCollector, EntityCommandProcessor {

    /**
     * The inventory of ESS devices
     */
    private ESSInventory essInventory = new ESSInventory(EntityEmulationData.EntityDataType.ESSINVENTORY);
    /**
     * The status of all defined ESS devices
     */
    private ESSStatus essStatus = new ESSStatus(EntityEmulationData.EntityDataType.ESSSTATUS);
    /**
     * The ESS observation metadata
     */
    private ESSObservationMetadata essObservationMetadata = new ESSObservationMetadata(EntityEmulationData.EntityDataType.ESSOBSERVATIONMETADATA);
    /**
     * The ESS Observation report
     */
    private ESSObservationReport essObservationReport = new ESSObservationReport(EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT);
    /**
     * Provides organization information related to a specific entity.
     */
    private EntityDataOrganizationInformationCollector orgInfoDataCollector;

    private ESSHandler() {
    }

    ;
    
    /**
     * Main constructor method for the ESSHandler class.
     * 
     * @param orgInfoDataCollector reference to an organization information collector.
     */
    public ESSHandler(EntityDataOrganizationInformationCollector orgInfoDataCollector) {
        this.orgInfoDataCollector = orgInfoDataCollector;
    }

//  private EntityUpdateListeners (ESSStatus and ESSInventory)   
    /**
     * Initializes the data types related to a ESS device.
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
            case ESSINVENTORY:
                essInventory.initialize(byteArray);
                break;
            case ESSSTATUS:
                essStatus.initialize(byteArray);
                break;
            case ESSOBSERVATIONMETADATA:
                essObservationMetadata.initialize(byteArray);
                break;
            case ESSOBSERVATIONREPORT:
                essObservationReport.initialize(byteArray);
                break;
            default:
                throw new EntityEmulationException("The ESSHandler is unable to initialize data type " + entityDataType);
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
            case "dlESSInventoryRequest":
                DlESSInventoryRequest dialogProcessor = new DlESSInventoryRequest();
                responseMsg = dialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.ESS, ESSINVENTORY, essInventory, this);
                break;
            case "dlESSStatusRequest":
                DlESSStatusRequest statusDialogProcessor = new DlESSStatusRequest();
                responseMsg = statusDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.ESS, ESSSTATUS, essStatus, this);
                break;
            case "dlESSObservationMetadataRequest":
                DlESSObservationMetadataRequest obMetaDialogProcessor = new DlESSObservationMetadataRequest();
                responseMsg = obMetaDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.ESS, ESSOBSERVATIONMETADATA, essObservationMetadata, this);
                break;
            case "dlESSObservationReportRequest":
                DlESSObservationReportRequest obRepDialogProcessor = new DlESSObservationReportRequest();
                responseMsg = obRepDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.ESS, ESSOBSERVATIONREPORT, essObservationReport, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the ESSHandler.");
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
                        DlDeviceInformationSubscription essInvDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = essInvDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.ESS, ESSINVENTORY, essInventory, this);
                        break;
                    case "2":
                    case "device status":
                        DlDeviceInformationSubscription essStatusDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = essStatusDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.ESS, ESSSTATUS, essStatus, this);
                        break;
                    case "6":
                    case "device data":
                        DlDeviceInformationSubscription essReportDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = essReportDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.ESS, ESSOBSERVATIONREPORT, essObservationReport, this);
                        break;
                    case "7":
                    case "device metadata":
                        DlDeviceInformationSubscription essMetadataDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = essMetadataDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.ESS, ESSOBSERVATIONMETADATA, essObservationMetadata, this);
                        break;
                    default:
                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlCCTVStatusRequest dialog.");
                }
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the ESSHandler.");
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
            case "dlESSInventoryUpdate":
                DlESSInventoryUpdate dialogProcessor = new DlESSInventoryUpdate();
                responseMsg = dialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.ESS, ESSINVENTORY, essInventory, this);
                break;
            case "dlESSStatusUpdate":
                DlESSStatusUpdate statusDialogProcessor = new DlESSStatusUpdate();
                responseMsg = statusDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.ESS, ESSSTATUS, essStatus, this);
                break;
            case "dlESSObservationReportUpdate":
                DlESSObservationReportUpdate essReportDialogProcessor = new DlESSObservationReportUpdate();
                responseMsg = essReportDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.ESS, ESSOBSERVATIONREPORT, essObservationReport, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the ESSHandler.");
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
        ValueInSetFilter deviceIdFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.ESSINVENTORY, "EntityId", deviceIdFilterValues);
        theFilters.add(deviceIdFilter);

        ArrayList<EntityDataRecord> theResponse = essInventory.getEntityElements(theFilters);

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
     * Each ESS-Inventory-Item is assigned an item index. This index is used to
     * indicate the relative order of the ESS Inventory items.
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
                case ESSINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = essInventory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{ESSSTATUS,ESSOBSERVATIONMETADATA,ESSOBSERVATIONREPORT});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlESSInventoryUpdate");
                    if (updatedTypes.contains(ESSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSStatusUpdate");
                    if (updatedTypes.contains(ESSOBSERVATIONREPORT))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSObservationReportUpdate");                        
                    break;
                case ESSSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = essStatus.addEntity(entityId, new EntityEmulationData.EntityDataType[]{ESSINVENTORY,ESSOBSERVATIONMETADATA,ESSOBSERVATIONREPORT});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlESSStatusUpdate");
                    if (statusUpdatedTypes.contains(ESSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSInventoryUpdate");
                    if (statusUpdatedTypes.contains(ESSOBSERVATIONREPORT))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSObservationReportUpdate");                        
                    break;
                case ESSOBSERVATIONMETADATA:
                    List<EntityEmulationData.EntityDataType> dataUpdatedTypes = essObservationMetadata.addEntity(entityId, new EntityEmulationData.EntityDataType[]{ESSSTATUS,ESSINVENTORY,ESSOBSERVATIONREPORT});
                    if (dataUpdatedTypes.contains(ESSOBSERVATIONREPORT))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSObservationReportUpdate");
                    if (dataUpdatedTypes.contains(ESSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSInventoryUpdate");
                    if (dataUpdatedTypes.contains(ESSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSStatusUpdate");                        
                    break;
                case ESSOBSERVATIONREPORT:
                    List<EntityEmulationData.EntityDataType> reportUpdatedTypes = essObservationReport.addEntity(entityId, new EntityEmulationData.EntityDataType[]{ESSSTATUS,ESSINVENTORY,ESSOBSERVATIONMETADATA});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlESSObservationReportUpdate");
                    if (reportUpdatedTypes.contains(ESSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSInventoryUpdate");
                    if (reportUpdatedTypes.contains(ESSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSStatusUpdate");                        
                    break;
                default:
                    throw new EntityEmulationException("The ESSHandler is unable to process data type " + entityDataType);
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
                case ESSINVENTORY:
                    essInventory.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlESSInventoryUpdate");
                    break;
                case ESSSTATUS:
                    essStatus.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlESSStatusUpdate");
                    break;
                case ESSOBSERVATIONMETADATA:
                    essObservationMetadata.addEntityElement(entityId, elementName, elementValue);
                    break;
                case ESSOBSERVATIONREPORT:
                    essObservationReport.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlESSObservationReportUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The ESSHandler is unable to process data type " + entityDataType);
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
                case ESSINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = essInventory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{ESSSTATUS,ESSOBSERVATIONMETADATA,ESSOBSERVATIONREPORT});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlESSInventoryUpdate");
                    if (updatedTypes.contains(ESSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSStatusUpdate");
                    if (updatedTypes.contains(ESSOBSERVATIONREPORT))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSObservationReportUpdate");                        
                    break;
                case ESSSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = essStatus.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{ESSINVENTORY,ESSOBSERVATIONMETADATA,ESSOBSERVATIONREPORT});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlESSStatusUpdate");
                    if (statusUpdatedTypes.contains(ESSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSInventoryUpdate");
                    if (statusUpdatedTypes.contains(ESSOBSERVATIONREPORT))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSObservationReportUpdate");                        
                    break;
                case ESSOBSERVATIONMETADATA:
                    List<EntityEmulationData.EntityDataType> dataUpdatedTypes = essObservationMetadata.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{ESSSTATUS,ESSINVENTORY,ESSOBSERVATIONREPORT});
                    if (dataUpdatedTypes.contains(ESSOBSERVATIONREPORT))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSObservationReportUpdate");
                    if (dataUpdatedTypes.contains(ESSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSInventoryUpdate");
                    if (dataUpdatedTypes.contains(ESSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSStatusUpdate");                        break;
                case ESSOBSERVATIONREPORT:
                    List<EntityEmulationData.EntityDataType> reportUpdatedTypes = essObservationReport.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{ESSSTATUS,ESSOBSERVATIONMETADATA,ESSINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlESSObservationReportUpdate");
                    if (reportUpdatedTypes.contains(ESSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSInventoryUpdate");
                    if (reportUpdatedTypes.contains(ESSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSStatusUpdate");    
                    break;
                default:
                    throw new EntityEmulationException("The ESSHandler is unable to process data type " + entityDataType);
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
                case ESSINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = essInventory.updateEntityElement(new EntityEmulationData.EntityDataType[]{ESSSTATUS,ESSOBSERVATIONMETADATA,ESSOBSERVATIONREPORT}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlESSInventoryUpdate");
                    if (updatedTypes.contains(ESSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSStatusUpdate");
                    if (updatedTypes.contains(ESSOBSERVATIONREPORT))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSObservationReportUpdate");                        
                    break;
                case ESSSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = essStatus.updateEntityElement(new EntityEmulationData.EntityDataType[]{ESSINVENTORY,ESSOBSERVATIONMETADATA,ESSOBSERVATIONREPORT}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlESSStatusUpdate");
                    if (statusUpdatedTypes.contains(ESSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSInventoryUpdate");
                    if (statusUpdatedTypes.contains(ESSOBSERVATIONREPORT))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSObservationReportUpdate");                        
                    break;
                case ESSOBSERVATIONMETADATA:
                    List<EntityEmulationData.EntityDataType> dataUpdatedTypes = essObservationMetadata.updateEntityElement(new EntityEmulationData.EntityDataType[]{ESSSTATUS,ESSINVENTORY,ESSOBSERVATIONREPORT}, entityId, entityElementName, entityElementValue);
                    if (dataUpdatedTypes.contains(ESSOBSERVATIONREPORT))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSObservationReportUpdate");
                    if (dataUpdatedTypes.contains(ESSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSInventoryUpdate");
                    if (dataUpdatedTypes.contains(ESSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSStatusUpdate");                        
                    break;
                case ESSOBSERVATIONREPORT:
                    List<EntityEmulationData.EntityDataType> reportUpdatedTypes = essObservationReport.updateEntityElement(new EntityEmulationData.EntityDataType[]{ESSSTATUS,ESSOBSERVATIONMETADATA,ESSINVENTORY,}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlESSObservationReportUpdate");
                    if (reportUpdatedTypes.contains(ESSINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSInventoryUpdate");
                    if (reportUpdatedTypes.contains(ESSSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlESSStatusUpdate");    
                    break;
                default:
                    throw new EntityEmulationException("The ESSHandler is unable to process data type " + entityDataType);
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
                case ESSINVENTORY:
                    essInventory.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlESSInventoryUpdate");
                    break;
                case ESSSTATUS:
                    essStatus.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlESSStatusUpdate");
                    break;
                case ESSOBSERVATIONMETADATA:
                    essObservationMetadata.deleteEntityElement(entityId, entityElementName);
                    break;
                case ESSOBSERVATIONREPORT:
                    essObservationReport.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlESSObservationReportUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The ESSHandler is unable to process data type " + entityDataType);
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
                case ESSINVENTORY:
                    entityValue = essInventory.getEntityElementValue(entityId, entityElement);
                    break;
                case ESSSTATUS:
                    entityValue = essStatus.getEntityElementValue(entityId, entityElement);
                    break;
                case ESSOBSERVATIONMETADATA:
                    entityValue = essObservationMetadata.getEntityElementValue(entityId, entityElement);
                    break;
                case ESSOBSERVATIONREPORT:
                    entityValue = essObservationReport.getEntityElementValue(entityId, entityElement);
                    break;
                default:
                    throw new EntityEmulationException("The ESSHandler is unable to process data type " + entityDataType);
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
    }

    @Override
    public void executeCommand(MessageSpecification commandMessage) throws EntityEmulationException {
    }

    @Override
    public void queueCommand(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, InvalidEntityControlRequestLogException, EntityEmulationException {
    }

    @Override
    public MessageSpecification getControlResponseMessage(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, EntityEmulationException {

        String orgId = commandMessage.getElementValue("tmdd:ESSControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:ESSControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:ESSControlRequestMsg.device-control-request-header.request-id");

        // get the matching control Request Status record
        EntityControlRequestStatusRecord statusRecord = EntityCommandQueue.getInstance().getControlRequestStatus(orgId, entityId, requestId);
        TMDDEntityType.EntityType entityType = TMDDEntityType.EntityType.valueOf(statusRecord.getEntityType());

        return TMDDMessageProcessor.createControlResponseMessage(entityType, statusRecord, orgInfoDataCollector);

    }

}