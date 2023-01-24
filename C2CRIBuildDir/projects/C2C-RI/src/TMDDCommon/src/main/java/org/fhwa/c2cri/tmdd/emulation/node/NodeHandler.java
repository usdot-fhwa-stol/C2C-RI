/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.node;

import java.util.ArrayList;
import java.util.List;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.tmdd.TMDDErrorResponseProcessor;
import org.fhwa.c2cri.tmdd.emulation.TMDDEntityType;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityCommandProcessor;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatusCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatusRecord;
import org.fhwa.c2cri.centermodel.RIEmulation;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataOrganizationInformationCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.NODEINVENTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.NODESTATUS;
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
import org.fhwa.c2cri.tmdd.emulation.generic.dialogs.DlTrafficNetworkInformationSubscription;
import org.fhwa.c2cri.tmdd.emulation.node.dialogs.DlNodeInventoryRequest;
import org.fhwa.c2cri.tmdd.emulation.node.dialogs.DlNodeInventoryUpdate;
import org.fhwa.c2cri.tmdd.emulation.node.dialogs.DlNodeStatusRequest;
import org.fhwa.c2cri.tmdd.emulation.node.dialogs.DlNodeStatusUpdate;

/**
 * The Center Active Verification Handler class coordinates all functions related to Center Active Verification information.
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class NodeHandler implements EntityControlRequestStatusCollector, EntityCommandProcessor {

    /**
     * The inventory of Node devices
     */
    private NodeInventory nodeInventory = new NodeInventory(NODEINVENTORY);
 
    /**
     * The status of Node devices
     */
    private NodeStatus nodeStatus = new NodeStatus(NODESTATUS);
     
    /**
     * Provides organization information related to a specific entity.
     */
    private EntityDataOrganizationInformationCollector orgInfoDataCollector;

    private NodeHandler() {
    }

    ;
    
    /**
     * Main constructor method for the CenterActiveVerificationHandler class.
     * 
     * @param orgInfoDataCollector reference to an organization information collector.
     */
    public NodeHandler(EntityDataOrganizationInformationCollector orgInfoDataCollector) {
        this.orgInfoDataCollector = orgInfoDataCollector;
    }


    /**
     * Initializes the data types related to a Node device.
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
            case NODEINVENTORY:
                nodeInventory.initialize(byteArray);
                break;
            case NODESTATUS:
                nodeStatus.initialize(byteArray);
                break;
            default:
                throw new EntityEmulationException("The nodeHandler is unable to initialize data type " + entityDataType);
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
//        try {

        switch (dialog) {
            case "dlNodeInventoryRequest":
                DlNodeInventoryRequest dlNodeInventoryRequestProcessor = new DlNodeInventoryRequest();
                responseMsg = dlNodeInventoryRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.TRAFFICNETWORKINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.NODE, NODEINVENTORY, nodeInventory, this);
                break;
            case "dlNodeStatusRequest":
                DlNodeStatusRequest dlNodeStatusRequestProcessor = new DlNodeStatusRequest();
                responseMsg = dlNodeStatusRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.TRAFFICNETWORKINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.NODE, NODESTATUS, nodeStatus, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the nodeHandler.");
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

//        try {
        switch (dialog) {
            case "dlTrafficNetworkInformationSubscription":
//      
                String msgNetworkInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.TRAFFICNETWORKINFORMATIONREQUESTMSG.relatedMessage() + "." + "network-information-type"));
                switch (msgNetworkInformationType) {
                    case "1":
                    case "node inventory":
                        DlTrafficNetworkInformationSubscription dlTrafficNetworkInformationProcessor = new DlTrafficNetworkInformationSubscription();
                        responseMsg = dlTrafficNetworkInformationProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.NODE, NODEINVENTORY, nodeInventory, this);

                        break;

                    case "2":
                    case "node status":
                        DlTrafficNetworkInformationSubscription dlTrafficNetworkInformationStatusProcessor = new DlTrafficNetworkInformationSubscription();
                        responseMsg = dlTrafficNetworkInformationStatusProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.NODE, NODESTATUS, nodeStatus, this);

                        break;
                    default:
                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain s correct network-information-type value for the dlTrafficNetworkSubscription dialog.");
                }
                break;

            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the CenterActiveVerificationHandler.");
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
            case "dlNodeInventoryUpdate":
                DlNodeInventoryUpdate dlInventoryUpdateProcessor = new DlNodeInventoryUpdate();
                responseMsg = dlInventoryUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.TRAFFICNETWORKINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.NODE, NODEINVENTORY, nodeInventory, this);

                break;
            case "dlNodeStatusUpdate":
                DlNodeStatusUpdate dlStatusUpdateProcessor = new DlNodeStatusUpdate();
                responseMsg = dlStatusUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.TRAFFICNETWORKINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.NODE, NODESTATUS, nodeStatus, this);

                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the nodeHandler.");
//                    throw new EntityEmulationException("The NodeHandler is unable to process dialog " + dialog);
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
        ValueInSetFilter deviceIdFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.NODEINVENTORY, "EntityId", deviceIdFilterValues);
        theFilters.add(deviceIdFilter);

        ArrayList<EntityDataRecord> theResponse = nodeInventory.getEntityElements(theFilters);

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
     * Each Node-Inventory-Item is assigned an item index. This index is used to
     * indicate the relative order of the Node Inventory items.
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
                case NODEINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = nodeInventory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{NODESTATUS});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlNodeInventoryUpdate");
                    if (updatedTypes.contains(NODESTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlNodeStatusUpdate");
                    break;
                case NODESTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = nodeStatus.addEntity(entityId, new EntityEmulationData.EntityDataType[]{NODEINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlNodeStatusUpdate");
                    if (statusUpdatedTypes.contains(NODEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlNodeInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The nodeHandler is unable to process data type " + entityDataType);
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
                case NODEINVENTORY:
                    nodeInventory.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlNodeInventoryUpdate");
                    break;
                case NODESTATUS:
                    nodeStatus.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlNodeStatusUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The nodeHandler is unable to process data type " + entityDataType);
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
                case NODEINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = nodeInventory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{NODESTATUS});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlNodeInventoryUpdate");
                    if (updatedTypes.contains(NODESTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlNodeStatusUpdate");
                    break;
                case NODESTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = nodeStatus.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{NODEINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlNodeStatusUpdate");
                    if (statusUpdatedTypes.contains(NODEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlNodeInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The nodeHandler is unable to process data type " + entityDataType);
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
                case NODEINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = nodeInventory.updateEntityElement(new EntityEmulationData.EntityDataType[]{NODESTATUS}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlNodeInventoryUpdate");
                    if (updatedTypes.contains(NODESTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlNodeStatusUpdate");
                    break;
                case NODESTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = nodeStatus.updateEntityElement(new EntityEmulationData.EntityDataType[]{NODEINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlNodeStatusUpdate");
                    if (statusUpdatedTypes.contains(NODEINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlNodeInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The nodeHandler is unable to process data type " + entityDataType);
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
                case NODEINVENTORY:
                    nodeInventory.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlNodeInventoryUpdate");
                    break;
                case NODESTATUS:
                    nodeStatus.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlNodeStatusUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The nodeHandler is unable to process data type " + entityDataType);
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
                case NODEINVENTORY:
                    entityValue = nodeInventory.getEntityElementValue(entityId, entityElement);
                    break;
                case NODESTATUS:
                    entityValue = nodeStatus.getEntityElementValue(entityId, entityElement);
                    break;
                default:
                    throw new EntityEmulationException("The nodeHandler is unable to process data type " + entityDataType);
            }
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

        return entityValue;
    }

    @Override
    public EntityControlRequestStatusRecord getControlRequestStatus(String orgId, String entityId, String requestId) throws EntityEmulationException {
        throw new EntityEmulationException("No Control Requests are associated with this entity.");
    }

    @Override
    public void updateControlRequestStatus(EntityControlRequestStatusRecord statusRecord, MessageSpecification requestMsg) throws EntityEmulationException {
        throw new EntityEmulationException("No Control Requests are associated with this entity.");
    }

    @Override
    public void verifyCommand(MessageSpecification commandMessage) throws InvalidCommandException, EntityEmulationException {
        throw new EntityEmulationException("No Commands are associated with this entity.");        
    }

    @Override
    public void executeCommand(MessageSpecification commandMessage) throws EntityEmulationException {
        throw new EntityEmulationException("No Commands are associated with this entity.");
    }

    @Override
    public void queueCommand(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, InvalidEntityControlRequestLogException, EntityEmulationException {
        throw new EntityEmulationException("No Command Queue is associated with this entity.");
    }

    @Override
    public MessageSpecification getControlResponseMessage(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, EntityEmulationException {
        throw new EntityEmulationException("No Control Response is associated with this entity.");
    }
    
}
