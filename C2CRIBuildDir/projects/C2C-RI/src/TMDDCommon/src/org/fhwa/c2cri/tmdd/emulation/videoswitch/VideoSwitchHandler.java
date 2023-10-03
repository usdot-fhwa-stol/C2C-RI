/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.videoswitch;

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
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.VIDEOSWITCHSTATUS;
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
import org.fhwa.c2cri.tmdd.emulation.videoswitch.dialogs.DlVideoSwitchControlRequest;
import org.fhwa.c2cri.tmdd.emulation.videoswitch.dialogs.DlVideoSwitchInventoryRequest;
import org.fhwa.c2cri.tmdd.emulation.videoswitch.dialogs.DlVideoSwitchInventoryUpdate;
import org.fhwa.c2cri.tmdd.emulation.videoswitch.dialogs.DlVideoSwitchStatusRequest;
import org.fhwa.c2cri.tmdd.emulation.videoswitch.dialogs.DlVideoSwitchStatusUpdate;

/**
 * The VIDEOSWITCHHandler class coordinates all functions related to VIDEOSWITCH
 * entities.
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class VideoSwitchHandler implements EntityControlRequestStatusCollector, EntityCommandProcessor {

    /**
     * The inventory of VIDEOSWITCH devices
     */
    private VideoSwitchInventory videoswitchInventory = new VideoSwitchInventory(EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY);
    /**
     * The status of all defined VIDEOSWITCH devices
     */
    private VideoSwitchStatus videoswitchStatus = new VideoSwitchStatus(EntityEmulationData.EntityDataType.VIDEOSWITCHSTATUS);
    /**
     * Provides organization information related to a specific entity.
     */
    private EntityDataOrganizationInformationCollector orgInfoDataCollector;

    private VideoSwitchHandler() {
    }

    ;
    
    /**
     * Main constructor method for the VIDEOSWITCHHandler class.
     * 
     * @param orgInfoDataCollector reference to an organization information collector.
     */
    public VideoSwitchHandler(EntityDataOrganizationInformationCollector orgInfoDataCollector) {
        this.orgInfoDataCollector = orgInfoDataCollector;
    }

//  private EntityUpdateListeners (VIDEOSWITCHStatus and VIDEOSWITCHInventory)   
    /**
     * Initializes the data types related to a VIDEOSWITCH device.
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
            case VIDEOSWITCHINVENTORY:
                videoswitchInventory.initialize(byteArray);
                break;
            case VIDEOSWITCHSTATUS:
                videoswitchStatus.initialize(byteArray);
                break;
            default:
                throw new EntityEmulationException("The VideoSwitchHandler is unable to initialize data type " + entityDataType);
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
            case "dlVideoSwitchControlRequest":
                DlVideoSwitchControlRequest dlVideoSwitchControlRequestProcessor = new DlVideoSwitchControlRequest();
                responseMsg = dlVideoSwitchControlRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.VIDEOSWITCHCONTROLREQUESTMSG, TMDDEntityType.EntityType.VIDEOSWITCH, VIDEOSWITCHINVENTORY, videoswitchInventory, this);
                break;
            case "dlVideoSwitchInventoryRequest":
                DlVideoSwitchInventoryRequest dlVideoSwitchInventoryRequestProcessor = new DlVideoSwitchInventoryRequest();
                responseMsg = dlVideoSwitchInventoryRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.VIDEOSWITCH, VIDEOSWITCHINVENTORY, videoswitchInventory, this);
                break;
            case "dlVideoSwitchStatusRequest":
                DlVideoSwitchStatusRequest dlVideoSwitchStatusRequestProcessor = new DlVideoSwitchStatusRequest();
                responseMsg = dlVideoSwitchStatusRequestProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.VIDEOSWITCH, VIDEOSWITCHSTATUS, videoswitchStatus, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the VideoSwitchHandler.");
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
                        DlDeviceInformationSubscription videoswitchInvDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = videoswitchInvDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.VIDEOSWITCH, VIDEOSWITCHINVENTORY, videoswitchInventory, this);
                        break;
                    case "2":
                    case "device status":
                        DlDeviceInformationSubscription videoswitchStatusDialogProcessor = new DlDeviceInformationSubscription();
                        responseMsg = videoswitchStatusDialogProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.VIDEOSWITCH, VIDEOSWITCHSTATUS, videoswitchStatus, this);
                        break;
                    default:
                        TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                        responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "The message received did not contain correct device-type and device-information-type values for the dlDeviceInformationSubscription dialog.");
                }
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the VideoSwitchHandler.");
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
            case "dlVideoSwitchInventoryUpdate":
                DlVideoSwitchInventoryUpdate dlVideoSwitchInventoryUpdateProcessor = new DlVideoSwitchInventoryUpdate();
                responseMsg = dlVideoSwitchInventoryUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.VIDEOSWITCH, VIDEOSWITCHINVENTORY, videoswitchInventory, this);
                break;
            case "dlVideoSwitchStatusUpdate":
                DlVideoSwitchStatusUpdate dlVideoSwitchStatusUpdateProcessor = new DlVideoSwitchStatusUpdate();
                responseMsg = dlVideoSwitchStatusUpdateProcessor.handle(requestMessage, EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG, TMDDEntityType.EntityType.VIDEOSWITCH, VIDEOSWITCHSTATUS, videoswitchStatus, this);
                break;
            default:
                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", "Dialog " + dialog + " is not currently supported by the VideoSwitchHandler.");
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
        ValueInSetFilter deviceIdFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "EntityId", deviceIdFilterValues);
        theFilters.add(deviceIdFilter);

        ArrayList<EntityDataRecord> theResponse = videoswitchInventory.getEntityElements(theFilters);

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
     * Each VIDEOSWITCH-Inventory-Item is assigned an item index. This index is
     * used to indicate the relative order of the VIDEOSWITCH Inventory items.
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
                case VIDEOSWITCHINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = videoswitchInventory.addEntity(entityId, new EntityEmulationData.EntityDataType[]{VIDEOSWITCHSTATUS});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlVideoSwitchInventoryUpdate");
                    if (updatedTypes.contains(VIDEOSWITCHSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlVideoSwitchStatusUpdate");
                    break;
                case VIDEOSWITCHSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = videoswitchStatus.addEntity(entityId, new EntityEmulationData.EntityDataType[]{VIDEOSWITCHINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlVideoSwitchStatusUpdate");
                    if (statusUpdatedTypes.contains(VIDEOSWITCHINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlVideoSwitchInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The VideoSwitchHandler is unable to process data type " + entityDataType);
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
                case VIDEOSWITCHINVENTORY:
                    videoswitchInventory.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlVideoSwitchInventoryUpdate");
                    break;
                case VIDEOSWITCHSTATUS:
                    videoswitchStatus.addEntityElement(entityId, elementName, elementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlVideoSwitchStatusUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The VideoSwitchHandler is unable to process data type " + entityDataType);
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
                case VIDEOSWITCHINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = videoswitchInventory.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{VIDEOSWITCHSTATUS});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlVideoSwitchInventoryUpdate");
                    if (updatedTypes.contains(VIDEOSWITCHSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlVideoSwitchStatusUpdate");
                    break;
                case VIDEOSWITCHSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = videoswitchStatus.deleteEntity(entityId, new EntityEmulationData.EntityDataType[]{VIDEOSWITCHINVENTORY});
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlVideoSwitchStatusUpdate");
                    if (statusUpdatedTypes.contains(VIDEOSWITCHINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlVideoSwitchInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The VideoSwitchHandler is unable to process data type " + entityDataType);
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
                case VIDEOSWITCHINVENTORY:
                    List<EntityEmulationData.EntityDataType> updatedTypes = videoswitchInventory.updateEntityElement(new EntityEmulationData.EntityDataType[]{VIDEOSWITCHSTATUS}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlVideoSwitchInventoryUpdate");
                    if (updatedTypes.contains(VIDEOSWITCHSTATUS))RIEmulation.getInstance().operationRelatedDataUpdate("dlVideoSwitchStatusUpdate");
                    break;
                case VIDEOSWITCHSTATUS:
                    List<EntityEmulationData.EntityDataType> statusUpdatedTypes = videoswitchStatus.updateEntityElement(new EntityEmulationData.EntityDataType[]{VIDEOSWITCHINVENTORY}, entityId, entityElementName, entityElementValue);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlVideoSwitchStatusUpdate");
                    if (statusUpdatedTypes.contains(VIDEOSWITCHINVENTORY))RIEmulation.getInstance().operationRelatedDataUpdate("dlVideoSwitchInventoryUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The VideoSwitchHandler is unable to process data type " + entityDataType);
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
                case VIDEOSWITCHINVENTORY:
                    videoswitchInventory.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlVideoSwitchInventoryUpdate");
                    break;
                case VIDEOSWITCHSTATUS:
                    videoswitchStatus.deleteEntityElement(entityId, entityElementName);
                    RIEmulation.getInstance().operationRelatedDataUpdate("dlVideoSwitchStatusUpdate");
                    break;
                default:
                    throw new EntityEmulationException("The VideoSwitchHandler is unable to process data type " + entityDataType);
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
                case VIDEOSWITCHINVENTORY:
                    entityValue = videoswitchInventory.getEntityElementValue(entityId, entityElement);
                    break;
                case VIDEOSWITCHSTATUS:
                    entityValue = videoswitchStatus.getEntityElementValue(entityId, entityElement);
                    break;
                default:
                    throw new EntityEmulationException("The VideoSwitchHandler is unable to process data type " + entityDataType);
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
		// original implementation was empty
    }

    @Override
    public void executeCommand(MessageSpecification commandMessage) throws EntityEmulationException {
        int lockStatus = 0;
        String operatorLockId = "";

        // retrieve the key elements from the request Message
        // get the org Id from the message
        String orgId = commandMessage.getElementValue("tmdd:videoSwitchControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:videoSwitchControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:videoSwitchControlRequestMsg.device-control-request-header.request-id");

        
        // Retrieve the desired VIDEOSWITCH Request Command
        String videoswitchRequestCommand = commandMessage.getElementValue("tmdd:videoswitchControlRequestMsg.videoswitch-request-command");
        
        // Select the paramaters associated with the requested command
        String videoswitchInputParameter = "";
        String videoswitchOutputParameter = "";
        String channelTitlingText = "";
        String setOutputChannelLock = "";


        // Set the command control status 
        String videoswitchCommandControlStatus = "other";        

        try {
            videoswitchInputParameter = commandMessage.getElementValue("tmdd:videoswitchControlRequestMsg.input-channel-id");
            videoswitchOutputParameter = commandMessage.getElementValue("tmdd:videoswitchControlRequestMsg.output-channel-id");
            channelTitlingText = commandMessage.getElementValue("tmdd:videoswitchControlRequestMsg.channel-titling-text");
            setOutputChannelLock = commandMessage.getElementValue("tmdd:videoswitchControlRequestMsg.set-output-channel-lock");
 
            if ((setOutputChannelLock != null)&&(!setOutputChannelLock.isEmpty())){
                if (!setOutputChannelLock.equals("0")){
                    lockStatus = 1;
                }
            }

            updateEntityElement(VIDEOSWITCHSTATUS, entityId, "switched-channel-list.switched-channel.input-channel-id", videoswitchInputParameter);
            updateEntityElement(VIDEOSWITCHSTATUS, entityId, "switched-channel-list.switched-channel.output-channel-id", videoswitchOutputParameter);

            if ((channelTitlingText != null)&&(!channelTitlingText.isEmpty())){
                updateEntityElement(VIDEOSWITCHSTATUS, entityId, "switched-channel-list.switched-channel.channel-titling-text", videoswitchOutputParameter);                
            }
            
            videoswitchCommandControlStatus = "requested changes completed";
        } catch (InvalidValueException ex) {
            videoswitchCommandControlStatus = "request rejected invalid command parameters";
        } catch (EntityEmulationException ex){
            videoswitchCommandControlStatus = "other";            
        }

        
        
        String operatorId = commandMessage.getElementValue("tmdd:videoSwitchControlRequestMsg.device-control-request-header.authentication.operator-id");

        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.VIDEOSWITCH.name());
        statusRecord.setStatus("other");
        statusRecord.setLockStatus(lockStatus);
        statusRecord.setOperatorId(operatorId);
        statusRecord.setOperatorLockId(operatorLockId);
        statusRecord.updateDate();

        // Update the ControlRequestStatus record to reflect the current command status.
        EntityCommandQueue.getInstance().updateControlRequestStatus(statusRecord, commandMessage, false);
        //throw new EntityEmulationException("For some reason I just can't bring myself to do it!!");   
    }

    @Override
    public void queueCommand(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, InvalidEntityControlRequestLogException, EntityEmulationException {
        int lockStatus = 0;
        String operatorLockId = "";

        String orgId = commandMessage.getElementValue("tmdd:videoSwitchControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:videoSwitchControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:videoSwitchControlRequestMsg.device-control-request-header.request-id");

        String operatorId = commandMessage.getElementValue("tmdd:videoSwitchControlRequestMsg.device-control-request-header.authentication.operator-id");

        String priority = commandMessage.getElementValue("tmdd:videoSwitchControlRequestMsg.device-control-request-header.command-request-priority");

        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, TMDDEntityType.EntityType.VIDEOSWITCH.name());
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

        String orgId = commandMessage.getElementValue("tmdd:videoSwitchControlRequestMsg.device-control-request-header.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = commandMessage.getElementValue("tmdd:videoSwitchControlRequestMsg.device-control-request-header.device-id");

        // get the request ID from the message
        String requestId = commandMessage.getElementValue("tmdd:videoSwitchControlRequestMsg.device-control-request-header.request-id");

        // get the matching control Request Status record
        EntityControlRequestStatusRecord statusRecord = EntityCommandQueue.getInstance().getControlRequestStatus(orgId, entityId, requestId);
        TMDDEntityType.EntityType entityType = TMDDEntityType.EntityType.valueOf(statusRecord.getEntityType());

        return TMDDMessageProcessor.createControlResponseMessage(entityType, statusRecord, orgInfoDataCollector);

    }
  
}
