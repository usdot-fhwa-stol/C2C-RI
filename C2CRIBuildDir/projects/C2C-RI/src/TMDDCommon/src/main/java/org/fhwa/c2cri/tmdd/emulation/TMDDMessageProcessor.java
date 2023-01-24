/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation;

import java.util.ArrayList;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.infolayer.MessageSpecificationItem;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityCommandProcessor;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityCommandQueue;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatusRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataInformationCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataOrganizationInformationCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationRequests;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityPriorityQueueInformation;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.DataFilter;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.ValueInDateRangeFilter;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.ValueInRangeFilter;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.ValueInSetFilter;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidCommandException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityControlRequestStatusException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.NoMatchingDataException;

/**
 *
 * @author TransCore ITS, LLC Created: Feb 12, 2016
 */
public class TMDDMessageProcessor {

    private static boolean tmddv31AndLater;
    
    public static void setVersion(String tmddVersion){
        if (tmddVersion.equalsIgnoreCase("TMDDv3.03c")||tmddVersion.equalsIgnoreCase("TMDDv3.03d")){
            tmddv31AndLater = false;
        } else tmddv31AndLater = true;
    }
    
    public static MessageSpecification processDeviceInformationRequestMsg(MessageSpecification requestMessage, EntityEmulationData.EntityDataType entityDataType, EntityDataInformationCollector dataCollector, boolean isPublication) throws NoMatchingDataException, EntityEmulationException {

        ArrayList<DataFilter> theFilters = new ArrayList();
        MessageSpecification responseMsg = null;

        // device-id filter
        ArrayList<String> deviceIdFilterValues = requestMessage.getValuesOfInstances("tmdd:deviceInformationRequestMsg.device-filter.device-id-list.device-id[*]");
        if (deviceIdFilterValues.size() > 0) {
            ValueInSetFilter deviceIdFilter = new ValueInSetFilter(entityDataType, "EntityId", deviceIdFilterValues);
            theFilters.add(deviceIdFilter);
        }

        // network-id filter
        ArrayList<String> networkIdFilterValues = requestMessage.getValuesOfInstances("tmdd:deviceInformationRequestMsg.device-filter.network-id-list.network-id[*]");
        if (networkIdFilterValues.size() > 0) {
            ValueInSetFilter networkIdFilter = new ValueInSetFilter(entityDataType, "NetworkId", networkIdFilterValues);
            theFilters.add(networkIdFilter);
        }

        // link filter
        ArrayList<String> linkFilterValues = requestMessage.getValuesOfInstances("tmdd:deviceInformationRequestMsg.device-filter.link-id-list.link[*]");
        if (linkFilterValues.size() > 0) {
            ValueInSetFilter linkFilter = new ValueInSetFilter(entityDataType, "LinkId", linkFilterValues);
            theFilters.add(linkFilter);
        }

        // link-designator filter
        ArrayList<String> linkDesignatorFilterValues = requestMessage.getValuesOfInstances("tmdd:deviceInformationRequestMsg.device-filter.link-designator-list.link-designator[*]");
        if (linkDesignatorFilterValues.size() > 0) {
            ValueInSetFilter linkDesignatorFilter = new ValueInSetFilter(entityDataType, "LinkDesignator", linkDesignatorFilterValues);
            theFilters.add(linkDesignatorFilter);
        }

        // linear reference filter
        String linearReferenceStart = requestMessage.getElementValue("tmdd:deviceInformationRequestMsg.device-filter.linear-reference.linear-reference-start");
        String linearReferenceEnd = requestMessage.getElementValue("tmdd:deviceInformationRequestMsg.device-filter.linear-reference.linear-reference-end");
        if ((linearReferenceStart != null) && (linearReferenceEnd != null) && (!linearReferenceStart.isEmpty()) && (!linearReferenceEnd.isEmpty())) {
            ValueInRangeFilter linearReferenceFilter = new ValueInRangeFilter(entityDataType, "LinearReference", linearReferenceStart, linearReferenceEnd);
            theFilters.add(linearReferenceFilter);
        }

        // section-id filter
        ArrayList<String> sectionIdFilterValues = requestMessage.getValuesOfInstances("tmdd:deviceInformationRequestMsg.device-filter.section-id-list.section-id[*]");
        if (sectionIdFilterValues.size() > 0) {
            ValueInSetFilter sectionIdFilter = new ValueInSetFilter(entityDataType, "SectionId", sectionIdFilterValues);
            theFilters.add(sectionIdFilter);
        }

        // pattern-id filter
        ArrayList<String> patternIdFilterValues = requestMessage.getValuesOfInstances("tmdd:deviceInformationRequestMsg.device-filter.pattern-id-list.pattern-id[*]");
        if (patternIdFilterValues.size() > 0) {
            ValueInSetFilter patternIdFilter = new ValueInSetFilter(entityDataType, "PatternId", patternIdFilterValues);
            theFilters.add(patternIdFilter);
        }

        // center-id filter        
        ArrayList<String> centerIdFilterValues = requestMessage.getValuesOfInstances("tmdd:deviceInformationRequestMsg.device-filter.center-id-list.center-id[*]");
        if (centerIdFilterValues.size() > 0) {
            ValueInSetFilter centerIdFilter = new ValueInSetFilter(entityDataType, "CenterId", centerIdFilterValues);
            theFilters.add(centerIdFilter);
        }

        // tmddv3.1 time-range filter        
        if (tmddv31AndLater){
            String timeRangeStartDate = requestMessage.getElementValue("tmdd:deviceInformationRequestMsg.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.date");
            String timeRangeStartTime = requestMessage.getElementValue("tmdd:deviceInformationRequestMsg.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.time");
            String timeRangeOffset = requestMessage.getElementValue("tmdd:deviceInformationRequestMsg.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.offset");
            String timeRangePeriodInSeconds = requestMessage.getElementValue("tmdd:deviceInformationRequestMsg.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.period-seconds");
            if ((timeRangeStartDate != null) && (timeRangeStartTime != null) && (!timeRangeStartDate.isEmpty()) && (!timeRangeStartTime.isEmpty())) {
                ValueInDateRangeFilter timeRangeFilter = new ValueInDateRangeFilter("TimeRangeDate", "TimeRangeTime", "TimeRangeOffset", timeRangeStartDate, timeRangeStartTime, timeRangeOffset, timeRangePeriodInSeconds);
                theFilters.add(timeRangeFilter);
            }
        }        
        ArrayList<EntityDataRecord> theResponse = dataCollector.getEntityElements(theFilters);

        ArrayList<String> generatedResponse = new ArrayList();
        for (EntityDataRecord thisRecord : theResponse) {
            generatedResponse.add(thisRecord.getEntityElement() + " = " + thisRecord.getEntityElementValue());
        }
        responseMsg = new MessageSpecification(generatedResponse);

        // If the request message is part of a subscription message we respond with the C2C Receipt instead.
        // We needed to complete the above process of generating a full response, just to ensure that some error would not have occured.
        if (!isPublication && requestMessage.containsMessageOfType("c2c:c2cMessageSubscription")) {
            ArrayList<String> c2cResponse = new ArrayList();
            c2cResponse.add("c2c:c2cMessageReceipt.informationalText = OK (Emulation Mode)");
            responseMsg = new MessageSpecification(c2cResponse);
        }

        return responseMsg;
    }

   public static MessageSpecification processCenterActiveVerificationRequestMsg(MessageSpecification requestMessage, EntityEmulationData.EntityDataType entityDataType, EntityDataInformationCollector dataCollector, boolean isPublication) throws NoMatchingDataException, EntityEmulationException {

        ArrayList<DataFilter> theFilters = new ArrayList();
        MessageSpecification responseMsg = null;
        
        ArrayList<EntityDataRecord> theResponse = dataCollector.getEntityElements(theFilters);

        ArrayList<String> generatedResponse = new ArrayList();
        for (EntityDataRecord thisRecord : theResponse) {
            generatedResponse.add(thisRecord.getEntityElement() + " = " + thisRecord.getEntityElementValue());
        }
        responseMsg = new MessageSpecification(generatedResponse);

        // If the request message is part of a subscription message we respond with the C2C Receipt instead.
        // We needed to complete the above process of generating a full response, just to ensure that some error would not have occured.
        if (!isPublication && requestMessage.containsMessageOfType("c2c:c2cMessageSubscription")) {
            ArrayList<String> c2cResponse = new ArrayList();
            c2cResponse.add("c2c:c2cMessageReceipt.informationalText = OK (Emulation Mode)");
            responseMsg = new MessageSpecification(c2cResponse);
        }

        return responseMsg;
    }
    
    
    public static MessageSpecification processDeviceControlStatusRequestMsg(MessageSpecification requestMessage, EntityCommandProcessor controlStatusCollector) throws NoMatchingDataException, EntityEmulationException {
        MessageSpecification responseMsg = null;

        responseMsg = controlStatusCollector.getControlResponseMessage(requestMessage);
//        responseMsg = createControlResponseMessage(TMDDEntityType.EntityType.CCTV, statusRecord, orgInfoDataCollector);

//        TMDDEntityType.EntityType entityType = TMDDEntityType.EntityType.valueOf(statusRecord.getEntityType());
//
//        // get the organization information related to this entity
//        MessageSpecification orgInformation = orgInfoDataCollector.getEntityOrganizationInformation(entityType, entityId);
//
//        // modify the organization information element names to fit the deviceControlStatusResponse Message
//        for (MessageSpecificationItem thisItem : orgInformation.getMessageSpecItems()) {
//            thisItem.setValueName("tmdd:deviceControlResponseMsg." + thisItem.getValueName().substring(thisItem.getValueName().indexOf("organization-information")));
//        }
//
//        // add the request elements to the organization information message specification        
//        responseMsg = orgInformation;
//
//        responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.device-id = " + entityId);
//        responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.request-id = " + requestId);
//        if (statusRecord.getOperatorId() != null) {
//            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.operator-id = " + statusRecord.getOperatorId());
//        }
//        if (statusRecord.getOperatorLockId() != null) {
//            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.operator-lock-id = " + statusRecord.getOperatorLockId());
//        }
//        if (statusRecord.getStatus() != null) {
//            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.request-status = " + statusRecord.getStatus());
//        }
//        if (statusRecord.getOperatorId() != null) {
//            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.operator-last-revised.date = " + statusRecord.getLastRevisedDate());
//        }
//        if (statusRecord.getOperatorId() != null) {
//            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.operator-last-revised.time = " + statusRecord.getLastRevisedTime());
//        }
//        if (statusRecord.getOperatorId() != null) {
//            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.operator-last-revised.offset = " + statusRecord.getLastRevisedOffset());
//        }
        return responseMsg;
    }

    public static MessageSpecification processDeviceCancelControlRequestMsg(MessageSpecification requestMessage, EntityCommandProcessor controlStatusCollector) throws NoMatchingDataException, EntityEmulationException {
        MessageSpecification responseMsg = null;
               
//        controlStatusCollector.executeCommand(requestMessage);
        String controlStatus = "requested changes completed";
        // retrieve the key elements from the request Message
        // get the org Id from the message
        String orgId = requestMessage.getElementValue("tmdd:deviceCancelControlRequestMsg.organization-requesting.organization-id");

        // get the entity ID from the message
        String entityId = requestMessage.getElementValue("tmdd:deviceCancelControlRequestMsg.device-id");

        // get the request ID from the message
        String requestId = requestMessage.getElementValue("tmdd:deviceCancelControlRequestMsg.request-id");
        
        String operatorId = requestMessage.getElementValue("tmdd:deviceCancelControlRequestMsg.authentication.operator-id");

        // get the matching control Request Status record
        EntityControlRequestStatusRecord statusRecord = EntityCommandQueue.getInstance().getControlRequestStatus(orgId, entityId, requestId);
        
//        EntityControlRequestStatusRecord statusRecord = new EntityControlRequestStatusRecord(orgId, entityId, requestId, controlStatusCollector.getEntityType().name());
        statusRecord.setStatus(controlStatus);
        statusRecord.setLockStatus(0);
        statusRecord.setOperatorId(operatorId);
        statusRecord.setOperatorLockId("");
        statusRecord.updateDate();

        // Update the ControlRequestStatus record to reflect the current command status.
        EntityCommandQueue.getInstance().updateControlRequestStatus(statusRecord, requestMessage, false);
//        EntityControlRequestLog.storeControlRequest(statusRecord.getIdentifier(), requestMessage);

        //       TMDDEntityType.EntityType entityType = TMDDEntityType.EntityType.valueOf(statusRecord.getEntityType());
        responseMsg = controlStatusCollector.getControlResponseMessage(requestMessage);

//// get the organization information related to this entity
//        MessageSpecification orgInformation = orgInfoDataCollector.getEntityOrganizationInformation(entityType, entityId);
//
//        // modify the organization information element names to fit the deviceControlStatusResponse Message
//        for (MessageSpecificationItem thisItem : orgInformation.getMessageSpecItems()) {
//            thisItem.setValueName("tmdd:deviceControlResponseMsg." + thisItem.getValueName().substring(thisItem.getValueName().indexOf("organization-information")));
//        }
//
//        // add the request elements to the organization information message specification        
//        responseMsg = orgInformation;
//
//        responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.device-id = " + entityId);
//        responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.request-id = " + requestId);
//        if ((statusRecord.getOperatorId() != null) && !statusRecord.getOperatorId().isEmpty()) {
//            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.operator-id = " + statusRecord.getOperatorId());
//        }
//        if ((statusRecord.getOperatorLockId() != null) && !statusRecord.getOperatorLockId().isEmpty()) {
//            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.operator-lock-id = " + statusRecord.getOperatorLockId());
//        }
//        if ((statusRecord.getStatus() != null) && !statusRecord.getStatus().isEmpty()) {
//            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.request-status = " + statusRecord.getStatus());
//        }
//        if ((statusRecord.getLastRevisedDate() != null) && !statusRecord.getLastRevisedDate().isEmpty()) {
//            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.operator-last-revised.date = " + statusRecord.getLastRevisedDate());
//        }
//        if ((statusRecord.getLastRevisedTime() != null) && !statusRecord.getLastRevisedTime().isEmpty()) {
//            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.operator-last-revised.time = " + statusRecord.getLastRevisedTime());
//        }
//        if ((statusRecord.getLastRevisedOffset() != null) && !statusRecord.getLastRevisedOffset().isEmpty()) {
//            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.operator-last-revised.offset = " + statusRecord.getLastRevisedOffset());
//        }
        return responseMsg;
    }

    public static MessageSpecification processCCTVControlRequestMsg(MessageSpecification requestMessage, EntityCommandProcessor controlStatusCollector) throws NoMatchingDataException, EntityEmulationException {
        MessageSpecification responseMsg = null;

        // Confirm that the referenced CCTV supports the requested command and that the parameters are valid for the selected CCTV
        controlStatusCollector.verifyCommand(requestMessage);

        //  Create/Update a Control Request Status Record for this request    
        controlStatusCollector.queueCommand(requestMessage);

        try {
            // Perform the next CCTV Command that is ready to be processed right now.
            MessageSpecification nextCommand = EntityCommandQueue.getInstance().retrieveNextCommand(TMDDEntityType.EntityType.CCTV);
            // Confirm that the referenced CCTV supports the requested command
            controlStatusCollector.verifyCommand(nextCommand);
            // Execute the command
            controlStatusCollector.executeCommand(nextCommand);
//            EntityControlRequestStatusRecord statusRecord = controlStatusCollector.getControlRequestStatus(orgId, entityId, requestId);
//            statusRecord.setStatus("request queued/not implemented");
//            statusRecord.setLockStatus(lockStatus);
//            statusRecord.setOperatorId(operatorId);
//            statusRecord.setOperatorLockId(operatorLockId);
//            statusRecord.updateDate();
//            controlStatusCollector.updateControlRequestStatus(statusRecord, requestMessage);
//            responseMsg = createControlResponseMessage(TMDDEntityType.EntityType.CCTV, statusRecord, orgInfoDataCollector);
//        } catch (InvalidEntityControlRequestStatusException ex){

            // Confirm that the parameters are valid for the selected CCTV
            // Update the status           
//            statusRecord = controlStatusCollector.getControlRequestStatus(orgId, entityId, requestId);           
//            responseMsg = createControlResponseMessage(TMDDEntityType.EntityType.CCTV, statusRecord, orgInfoDataCollector);            
        } catch (InvalidEntityControlRequestStatusException ex) {
            ex.printStackTrace();
        } catch (InvalidCommandException ex) {
            ex.printStackTrace();
        } catch (EntityEmulationException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        responseMsg = controlStatusCollector.getControlResponseMessage(requestMessage);
        return responseMsg;
    }

    public static MessageSpecification createControlResponseMessage(TMDDEntityType.EntityType entityType, EntityControlRequestStatusRecord statusRecord, EntityDataOrganizationInformationCollector orgInfoDataCollector) throws NoMatchingDataException, EntityEmulationException {
        MessageSpecification responseMsg = null;

        // get the organization information related to this entity
        MessageSpecification orgInformation = orgInfoDataCollector.getEntityOrganizationInformation(entityType, statusRecord.getEntityId());

        // modify the organization information element names to fit the deviceControlStatusResponse Message
        for (MessageSpecificationItem thisItem : orgInformation.getMessageSpecItems()) {
            thisItem.setValueName("tmdd:deviceControlResponseMsg." + thisItem.getValueName().substring(thisItem.getValueName().indexOf("organization-information")));
        }

        // add the request elements to the organization information message specification        
        responseMsg = orgInformation;

        responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.device-id = " + statusRecord.getEntityId());
        responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.request-id = " + statusRecord.getRequestId());
        if ((statusRecord.getOperatorId() != null) && !statusRecord.getOperatorId().isEmpty()) {
            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.operator-id = " + statusRecord.getOperatorId());
        }
        if ((statusRecord.getOperatorLockId() != null) && !statusRecord.getOperatorLockId().isEmpty()) {
            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.operator-lock-id = " + statusRecord.getOperatorLockId());
        } else {  // Added this because when the Operator-Lock-ID requirement is selected, this field must be present.
            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.operator-lock-id = .");
        }
        if ((statusRecord.getStatus() != null) && !statusRecord.getStatus().isEmpty()) {
            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.request-status = " + statusRecord.getStatus());
        }
        if ((statusRecord.getLastRevisedDate() != null) && !statusRecord.getLastRevisedDate().isEmpty()) {
            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.operator-last-revised.date = " + statusRecord.getLastRevisedDate());
        }
        if ((statusRecord.getLastRevisedTime() != null) && !statusRecord.getLastRevisedTime().isEmpty()) {
            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.operator-last-revised.time = " + statusRecord.getLastRevisedTime());
        }
        if ((statusRecord.getLastRevisedOffset() != null) && !statusRecord.getLastRevisedOffset().isEmpty()) {
            responseMsg.addElementToSpec("tmdd:deviceControlResponseMsg.operator-last-revised.offset = " + statusRecord.getLastRevisedOffset());
        }

        return responseMsg;
    }

    /**
     * This method is responsible for processing the detectorDataRequestMsg
     * Message and generating an appropriate response message.
     *
     * @param requestMessage the request message received
     * @param entityDataType the type of entity data that this message is
     * related to
     * @param dataCollector the object that is capable of collecting the
     * required information
     * @param isPublication flag indicating whether the request is related to a
     * publication dialog.
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processDetectorDataRequestMsg(MessageSpecification requestMessage, EntityEmulationData.EntityDataType entityDataType, EntityDataInformationCollector dataCollector, boolean isPublication) throws NoMatchingDataException, EntityEmulationException {

        ArrayList<DataFilter> theFilters = new ArrayList();
        MessageSpecification responseMsg = null;

        // device-id filter
        ArrayList<String> deviceIdFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorDataRequestMsg.device-information-request-header.device-filter.device-id-list.device-id[*]");
        if (deviceIdFilterValues.size() > 0) {
            ValueInSetFilter deviceIdFilter = new ValueInSetFilter(entityDataType, "EntityId", deviceIdFilterValues);
            theFilters.add(deviceIdFilter);
        }

        // network-id filter
        ArrayList<String> networkIdFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorDataRequestMsg.device-information-request-header.device-filter.network-id-list.network-id[*]");
        if (networkIdFilterValues.size() > 0) {
            ValueInSetFilter networkIdFilter = new ValueInSetFilter(entityDataType, "NetworkId", networkIdFilterValues);
            theFilters.add(networkIdFilter);
        }

        // link filter
        ArrayList<String> linkFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorDataRequestMsg.device-information-request-header.device-filter.link-id-list.link[*]");
        if (linkFilterValues.size() > 0) {
            ValueInSetFilter linkFilter = new ValueInSetFilter(entityDataType, "LinkId", linkFilterValues);
            theFilters.add(linkFilter);
        }

        // link-designator filter
        ArrayList<String> linkDesignatorFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorDataRequestMsg.device-information-request-header.device-filter.link-designator-list.link-designator[*]");
        if (linkDesignatorFilterValues.size() > 0) {
            ValueInSetFilter linkDesignatorFilter = new ValueInSetFilter(entityDataType, "LinkDesignator", linkDesignatorFilterValues);
            theFilters.add(linkDesignatorFilter);
        }

        // linear reference filter
        String linearReferenceStart = requestMessage.getElementValue("tmdd:detectorDataRequestMsg.device-information-request-header.device-filter.linear-reference.linear-reference-start");
        String linearReferenceEnd = requestMessage.getElementValue("tmdd:detectorDataRequestMsg.device-information-request-header.device-filter.linear-reference.linear-reference-end");
        if ((linearReferenceStart != null) && (linearReferenceEnd != null) && (!linearReferenceStart.isEmpty()) && (!linearReferenceEnd.isEmpty())) {
            ValueInRangeFilter linearReferenceFilter = new ValueInRangeFilter(entityDataType, "LinearReference", linearReferenceStart, linearReferenceEnd);
            theFilters.add(linearReferenceFilter);
        }

        // section-id filter
        ArrayList<String> sectionIdFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorDataRequestMsg.device-information-request-header.device-filter.section-id-list.section-id[*]");
        if (sectionIdFilterValues.size() > 0) {
            ValueInSetFilter sectionIdFilter = new ValueInSetFilter(entityDataType, "SectionId", sectionIdFilterValues);
            theFilters.add(sectionIdFilter);
        }

        // pattern-id filter
        ArrayList<String> patternIdFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorDataRequestMsg.device-information-request-header.device-filter.pattern-id-list.pattern-id[*]");
        if (patternIdFilterValues.size() > 0) {
            ValueInSetFilter patternIdFilter = new ValueInSetFilter(entityDataType, "PatternId", patternIdFilterValues);
            theFilters.add(patternIdFilter);
        }

        // center-id filter        
        ArrayList<String> centerIdFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorDataRequestMsg.device-information-request-header.device-filter.center-id-list.center-id[*]");
        if (centerIdFilterValues.size() > 0) {
            ValueInSetFilter centerIdFilter = new ValueInSetFilter(entityDataType, "CenterId", centerIdFilterValues);
            theFilters.add(centerIdFilter);
        }

        // detector-station-id filter        
        ArrayList<String> detectorStationIdFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorDataRequestMsg.detector-station-id");
        if (detectorStationIdFilterValues.size() > 0) {
            ValueInSetFilter detectorStationIdFilter = new ValueInSetFilter(entityDataType, "DetectorStationId", detectorStationIdFilterValues);
            theFilters.add(detectorStationIdFilter);
        }

        // detector-data-type filter        
        ArrayList<String> detectorDataTypeFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorDataRequestMsg.detector-data-type");
        if (detectorDataTypeFilterValues.size() > 0) {
            ValueInSetFilter detectorDataTypeFilter = new ValueInSetFilter(entityDataType, "DetectorDataTypeId", detectorDataTypeFilterValues);
            theFilters.add(detectorDataTypeFilter);
        }

        // tmddv3.1 time-range filter        
        if (tmddv31AndLater){
            String timeRangeStartDate = requestMessage.getElementValue("tmdd:detectorDataRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.date");
            String timeRangeStartTime = requestMessage.getElementValue("tmdd:detectorDataRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.time");
            String timeRangeOffset = requestMessage.getElementValue("tmdd:detectorDataRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.offset");
            String timeRangePeriodInSeconds = requestMessage.getElementValue("tmdd:detectorDataRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.period-seconds");
            if ((timeRangeStartDate != null) && (timeRangeStartTime != null) && (!timeRangeStartDate.isEmpty()) && (!timeRangeStartTime.isEmpty())) {
                ValueInDateRangeFilter timeRangeFilter = new ValueInDateRangeFilter("TimeRangeDate", "TimeRangeTime", "TimeRangeOffset", timeRangeStartDate, timeRangeStartTime, timeRangeOffset, timeRangePeriodInSeconds);
                theFilters.add(timeRangeFilter);
            }      
        }        
        ArrayList<EntityDataRecord> theResponse = dataCollector.getEntityElements(theFilters);

        ArrayList<String> generatedResponse = new ArrayList();
        for (EntityDataRecord thisRecord : theResponse) {
            generatedResponse.add(thisRecord.getEntityElement() + " = " + thisRecord.getEntityElementValue());
        }
        responseMsg = new MessageSpecification(generatedResponse);

        // If the request message is part of a subscription message we respond with the C2C Receipt instead.
        // We needed to complete the above process of generating a full response, just to ensure that some error would not have occured.
        if (!isPublication && requestMessage.containsMessageOfType("c2c:c2cMessageSubscription")) {
            ArrayList<String> c2cResponse = new ArrayList();
            c2cResponse.add("c2c:c2cMessageReceipt.informationalText = OK (Emulation Mode)");
            responseMsg = new MessageSpecification(c2cResponse);
        }

        return responseMsg;
    }

    public static MessageSpecification processArchivedDataProcessingDocumentationMetadataRequestMsg(MessageSpecification requestMessage, EntityDataInformationCollector dataCollector) throws NoMatchingDataException, EntityEmulationException {
        ArrayList<DataFilter> theFilters = new ArrayList();
        MessageSpecification responseMsg = null;

        ArrayList<EntityDataRecord> theResponse = dataCollector.getEntityElements(theFilters);

        ArrayList<String> generatedResponse = new ArrayList();
        for (EntityDataRecord thisRecord : theResponse) {
            generatedResponse.add(thisRecord.getEntityElement() + " = " + thisRecord.getEntityElementValue());
        }
        responseMsg = new MessageSpecification(generatedResponse);

        return responseMsg;

    }

    /**
     * This method is responsible for processing the
     * detectorMaintenanceHistoryRequestMsg Message and generating an
     * appropriate response message.
     *
     * @param requestMessage the request message received
     * @param entityDataType the type of entity data that this message is
     * related to
     * @param dataCollector the object that is capable of collecting the
     * required information
     * @param isPublication flag indicating whether the request is related to a
     * publication dialog.
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processDetectorMaintenanceHistoryRequestMsg(MessageSpecification requestMessage, EntityEmulationData.EntityDataType entityDataType, EntityDataInformationCollector dataCollector, boolean isPublication) throws NoMatchingDataException, EntityEmulationException {

        ArrayList<DataFilter> theFilters = new ArrayList();
        MessageSpecification responseMsg = null;

        // device-id filter
        ArrayList<String> deviceIdFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorMaintenanceHistoryRequestMsg.device-information-request-header.device-filter.device-id-list.device-id[*]");
        if (deviceIdFilterValues.size() > 0) {
            ValueInSetFilter deviceIdFilter = new ValueInSetFilter(entityDataType, "EntityId", deviceIdFilterValues);
            theFilters.add(deviceIdFilter);
        }

        // network-id filter
        ArrayList<String> networkIdFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorMaintenanceHistoryRequestMsg.device-information-request-header.device-filter.network-id-list.network-id[*]");
        if (networkIdFilterValues.size() > 0) {
            ValueInSetFilter networkIdFilter = new ValueInSetFilter(entityDataType, "NetworkId", networkIdFilterValues);
            theFilters.add(networkIdFilter);
        }

        // link filter
        ArrayList<String> linkFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorMaintenanceHistoryRequestMsg.device-information-request-header.device-filter.link-id-list.link[*]");
        if (linkFilterValues.size() > 0) {
            ValueInSetFilter linkFilter = new ValueInSetFilter(entityDataType, "LinkId", linkFilterValues);
            theFilters.add(linkFilter);
        }

        // link-designator filter
        ArrayList<String> linkDesignatorFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorMaintenanceHistoryRequestMsg.device-information-request-header.device-filter.link-designator-list.link-designator[*]");
        if (linkDesignatorFilterValues.size() > 0) {
            ValueInSetFilter linkDesignatorFilter = new ValueInSetFilter(entityDataType, "LinkDesignator", linkDesignatorFilterValues);
            theFilters.add(linkDesignatorFilter);
        }

        // linear reference filter
        String linearReferenceStart = requestMessage.getElementValue("tmdd:detectorMaintenanceHistoryRequestMsg.device-information-request-header.device-filter.linear-reference.linear-reference-start");
        String linearReferenceEnd = requestMessage.getElementValue("tmdd:detectorMaintenanceHistoryRequestMsg.device-information-request-header.device-filter.linear-reference.linear-reference-end");
        if ((linearReferenceStart != null) && (linearReferenceEnd != null) && (!linearReferenceStart.isEmpty()) && (!linearReferenceEnd.isEmpty())) {
            ValueInRangeFilter linearReferenceFilter = new ValueInRangeFilter(entityDataType, "LinearReference", linearReferenceStart, linearReferenceEnd);
            theFilters.add(linearReferenceFilter);
        }

        // section-id filter
        ArrayList<String> sectionIdFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorMaintenanceHistoryRequestMsg.device-information-request-header.device-filter.section-id-list.section-id[*]");
        if (sectionIdFilterValues.size() > 0) {
            ValueInSetFilter sectionIdFilter = new ValueInSetFilter(entityDataType, "SectionId", sectionIdFilterValues);
            theFilters.add(sectionIdFilter);
        }

        // pattern-id filter
        ArrayList<String> patternIdFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorMaintenanceHistoryRequestMsg.device-information-request-header.device-filter.pattern-id-list.pattern-id[*]");
        if (patternIdFilterValues.size() > 0) {
            ValueInSetFilter patternIdFilter = new ValueInSetFilter(entityDataType, "PatternId", patternIdFilterValues);
            theFilters.add(patternIdFilter);
        }

        // center-id filter        
        ArrayList<String> centerIdFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorMaintenanceHistoryRequestMsg.device-information-request-header.device-filter.center-id-list.center-id[*]");
        if (centerIdFilterValues.size() > 0) {
            ValueInSetFilter centerIdFilter = new ValueInSetFilter(entityDataType, "CenterId", centerIdFilterValues);
            theFilters.add(centerIdFilter);
        }

        // detector-station-id filter        
        ArrayList<String> detectorStationIdFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorMaintenanceHistoryRequestMsg.detector-station-id");
        if (detectorStationIdFilterValues.size() > 0) {
            ValueInSetFilter detectorStationIdFilter = new ValueInSetFilter(entityDataType, "DetectorStationId", detectorStationIdFilterValues);
            theFilters.add(detectorStationIdFilter);
        }

        // detector-data-type filter        
        ArrayList<String> detectorDataTypeFilterValues = requestMessage.getValuesOfInstances("tmdd:detectorMaintenanceHistoryRequestMsg.detector-data-type");
        if (detectorDataTypeFilterValues.size() > 0) {
            ValueInSetFilter detectorDataTypeFilter = new ValueInSetFilter(entityDataType, "DetectorDataTypeId", detectorDataTypeFilterValues);
            theFilters.add(detectorDataTypeFilter);
        }

        // tmddv3.1 time-range filter  
        if (tmddv31AndLater){
            String timeRangeStartDate = requestMessage.getElementValue("tmdd:detectorMaintenanceHistoryRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.date");
            String timeRangeStartTime = requestMessage.getElementValue("tmdd:detectorMaintenanceHistoryRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.time");
            String timeRangeOffset = requestMessage.getElementValue("tmdd:detectorMaintenanceHistoryRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.offset");
            String timeRangePeriodInSeconds = requestMessage.getElementValue("tmdd:detectorMaintenanceHistoryRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.period-seconds");
            if ((timeRangeStartDate != null) && (timeRangeStartTime != null) && (!timeRangeStartDate.isEmpty()) && (!timeRangeStartTime.isEmpty())) {
                ValueInDateRangeFilter timeRangeFilter = new ValueInDateRangeFilter("TimeRangeDate", "TimeRangeTime", "TimeRangeOffset", timeRangeStartDate, timeRangeStartTime, timeRangeOffset, timeRangePeriodInSeconds);
                theFilters.add(timeRangeFilter);
            }
        }
        ArrayList<EntityDataRecord> theResponse = dataCollector.getEntityElements(theFilters);

        ArrayList<String> generatedResponse = new ArrayList();
        for (EntityDataRecord thisRecord : theResponse) {
            generatedResponse.add(thisRecord.getEntityElement() + " = " + thisRecord.getEntityElementValue());
        }
        responseMsg = new MessageSpecification(generatedResponse);

        // If the request message is part of a subscription message we respond with the C2C Receipt instead.
        // We needed to complete the above process of generating a full response, just to ensure that some error would not have occured.
        if (!isPublication && requestMessage.containsMessageOfType("c2c:c2cMessageSubscription")) {
            ArrayList<String> c2cResponse = new ArrayList();
            c2cResponse.add("c2c:c2cMessageReceipt.informationalText = OK (Emulation Mode)");
            responseMsg = new MessageSpecification(c2cResponse);
        }

        return responseMsg;
    }

    /**
     * This method is responsible for processing the
     * devicePriorityQueueRequestMsg Message and generating an appropriate
     * response message.
     *
     * @param requestMessage the request message received
     * @param entityType the type of entity data that this message is related to
     * @param dataCollector the object that is capable of collecting the
     * required information
     * @param isPublication flag indicating whether the request is related to a
     * publication dialog.
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processDevicePriorityQueueRequestMsg(MessageSpecification requestMessage, TMDDEntityType.EntityType entityType, EntityDataInformationCollector dataCollector) throws NoMatchingDataException, EntityEmulationException {

        ArrayList<String> responseElements = new ArrayList();
        MessageSpecification responseMsg = null;

        // device-id filter
        ArrayList<String> deviceIdValues = requestMessage.getValuesOfInstances("tmdd:devicePriorityQueueRequestMsg.device-id-list.device-id[*]");

        System.out.println("TMDDMessageProcessor::processDevicePriorityQueueRequestMsg: entityType = "+entityType + "  deviceIdValues = "+deviceIdValues);
        final EntityPriorityQueueInformation queuedRequests = EntityCommandQueue.getInstance().retrieveQueuedCommands(entityType, deviceIdValues);
        System.out.println("TMDDMessageProcessor::processDevicePriorityQueueRequestMsg: queued Header spec is null? = "+(queuedRequests.getQueueHeadRequest(deviceIdValues.get(0))==null?"true":"false"));

        String responseMessageName;
        String queueItemName;
        String deviceType;
        int devicePriorityQueueItemIndex = 1;

        for (String deviceId : queuedRequests.getDeviceIDList()) {
            switch (entityType) {
                case RAMPMETER:
                    responseMessageName = "tmdd:rampMeterPriorityQueueMsg";
                    queueItemName = "ramp-meter-priority-queue-item";
                    deviceType = "ramp meter";
                    for (String headerElement : createDevicePriorityQueueHeaderElements(EntityEmulationRequests.EntityRequestMessageType.RAMPMETERCONTROLREQUESTMSG.relatedMessage() + ".device-control-request-header", deviceType, deviceId, queuedRequests)) {
                        responseElements.add(responseMessageName + "." + queueItemName + "[" + devicePriorityQueueItemIndex + "].device-priority-queue-header." + headerElement.replace("authentication.operator-id", "operator-id"));
                    }

                    String meteredLaneIdentifier = "";
                    String meterRequestCommand = "";
                    String meterRequestedPlan = "";
                    String meterRequestedRate = "";

//  Add the device-priority-queue-item for the request at the top of the queue
                    for (String thisElement : queuedRequests.getQueueHeadRequest(deviceId).getMessageSpec()) {
                        if (thisElement.contains(".metered-lane-identifier")) {
                            if (meteredLaneIdentifier.isEmpty()) {
                                meteredLaneIdentifier = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                            }
                        } else if (thisElement.contains(".meter-request-command")) {
                            if (meterRequestCommand.isEmpty()) {
                                // The control request command for a meter is based on NTCIP:RmcRequestAction.  However,
                                // the request command for the priority queue is based on tmdd:Meter-operational-mode.  As
                                // a result we may have to make some value transformations in a couple of instances.                                
                                meterRequestCommand = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                                if (meterRequestCommand.equals("insert-extension-values-here")){
                                    meterRequestCommand = "dark";
                                } else if (meterRequestCommand.equals("restInGreen")){
                                    meterRequestCommand = "rest in green";
                                } else if (meterRequestCommand.equals("fixedRate")){
                                    meterRequestCommand = "fixed rate";
                                } else if (meterRequestCommand.equals("trafficResponsive")){
                                    meterRequestCommand = "traffic responsive";
                                } else if (meterRequestCommand.equals("emergencyGreen")){
                                    meterRequestCommand = "emergency green";
                                }
                            }
                        } else if (thisElement.contains(".meter-requested-plan")) {
                            if (meterRequestedPlan.isEmpty()) {
                                meterRequestedPlan = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                            }
                        } else if (thisElement.contains(".meter-requested-rate")) {
                            if (meterRequestedRate.isEmpty()) {
                                meterRequestedRate = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                            }
                        }
                    }

                    if (!meteredLaneIdentifier.isEmpty()) {
                        responseElements.add(responseMessageName + "." + queueItemName + "[" + devicePriorityQueueItemIndex + "].metered-lane-identifier = " + meteredLaneIdentifier);
                    }
                    if (!meterRequestCommand.isEmpty()) {
                        responseElements.add(responseMessageName + "." + queueItemName + "[" + devicePriorityQueueItemIndex + "].meter-request-command = " + meterRequestCommand);
                    }
                    if (!meterRequestedPlan.isEmpty()) {
                        responseElements.add(responseMessageName + "." + queueItemName + "[" + devicePriorityQueueItemIndex + "].meter-queue-parameters.meter-requested-plan = " + meterRequestedPlan);
                    }
                    if (!meterRequestedRate.isEmpty()) {
                        responseElements.add(responseMessageName + "." + queueItemName + "[" + devicePriorityQueueItemIndex + "].meter-queue-parameters.meter-requested-rate = " + meterRequestedRate);
                    }
                    break;
                case DMS:
                    responseMessageName = "tmdd:dMSPriorityQueueMsg";
                    queueItemName = "dms-priority-queue-item";
                    deviceType = "dynamic message sign";

                    for (String headerElement : createDevicePriorityQueueHeaderElements(EntityEmulationRequests.EntityRequestMessageType.DMSCONTROLREQUESTMSG.relatedMessage() + ".device-control-request-header", deviceType, deviceId, queuedRequests)) {
                        responseElements.add(responseMessageName + "."+queueItemName + "[" + devicePriorityQueueItemIndex + "].device-priority-queue-header." + headerElement.replace("authentication.operator-id", "operator-id"));
                    }

                    String dmsRequestCommand = "";
                    String dmsMessage = "";
                    String messageNumber = "";
//                    String dmsBeaconControl = "";

//  Add the device-priority-queue-item for the request at the top of the queue
                    for (String thisElement : queuedRequests.getQueueHeadRequest(deviceId).getMessageSpec()) {
                        if (thisElement.contains(".dms-request-command")) {
                            if (dmsRequestCommand.isEmpty()) {
                                dmsRequestCommand = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                            }
                        } else if (thisElement.contains(".dms-message")) {
                            if (dmsMessage.isEmpty()) {
                                dmsMessage = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                            }
                        } else if (thisElement.contains(".message-number")) {
                            if (messageNumber.isEmpty()) {
                                messageNumber = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                            }
//                        } else if (thisElement.contains(".dms-beacon-control")) {
//                            if (dmsBeaconControl.isEmpty()) {
//                                dmsBeaconControl = thisElement.substring(thisElement.lastIndexOf("=") + 1);
//                            }
                        }
                    }

                    if (!dmsRequestCommand.isEmpty()) {
                        responseElements.add(responseMessageName + "." + queueItemName + "[" + devicePriorityQueueItemIndex + "].dms-request-command = " + dmsRequestCommand);
                    }
                    if (!dmsMessage.isEmpty()) {
                        responseElements.add(responseMessageName + "." + queueItemName + "[" + devicePriorityQueueItemIndex + "].dms-queue-parameters.dms-message = " + dmsMessage);
                    }
                    if (!messageNumber.isEmpty()) {
                        responseElements.add(responseMessageName + "." + queueItemName + "[" + devicePriorityQueueItemIndex + "].dms-queue-parameters.message-number = " + messageNumber);
                    }
//                    if (!dmsBeaconControl.isEmpty()) {
//                        responseElements.add(responseMessageName + queueItemName + "[" + devicePriorityQueueItemIndex + "].meter-requested-rate = " + dmsBeaconControl);
//                    }

                    break;
                case HAR:
                    responseMessageName = "tmdd:hARPriorityQueueMsg";
                    queueItemName = "har-priority-queue-item";
                    deviceType = "highway advisory radio";

                    for (String headerElement : createDevicePriorityQueueHeaderElements(EntityEmulationRequests.EntityRequestMessageType.HARCONTROLREQUESTMSG.relatedMessage() + ".device-control-request-header", deviceType, deviceId, queuedRequests)) {
                        responseElements.add(responseMessageName + "." + queueItemName + "[" + devicePriorityQueueItemIndex + "].device-priority-queue-header." + headerElement.replace("authentication.operator-id", "operator-id"));
                    }

                    String harRequestCommand = "";
                    String harMessage = "";
                    String harMessageNumber = "";

//  Add the device-priority-queue-item for the request at the top of the queue
                    for (String thisElement : queuedRequests.getQueueHeadRequest(deviceId).getMessageSpec()) {
                        if (thisElement.contains(".har-request-command")) {
                            if (harRequestCommand.isEmpty()) {
                                harRequestCommand = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                            }
                        } else if (thisElement.contains(".har-message")) {
                            if (harMessage.isEmpty()) {
                                harMessage = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                            }
                        } else if (thisElement.contains(".har-message-number")) {
                            if (harMessageNumber.isEmpty()) {
                                harMessageNumber = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                            }
                        }
                    }

                    if (!harRequestCommand.isEmpty()) {
                        responseElements.add(responseMessageName + "." + queueItemName + "[" + devicePriorityQueueItemIndex + "].har-request-command = " + harRequestCommand);
                    }
                    if (!harMessage.isEmpty()) {
                        responseElements.add(responseMessageName + "." + queueItemName + "[" + devicePriorityQueueItemIndex + "].har-queue-parameters.har-message = " + harMessage);
                    }
                    if (!harMessageNumber.isEmpty()) {
                        responseElements.add(responseMessageName + "." + queueItemName + "[" + devicePriorityQueueItemIndex + "].har-queue-parameters.message-number = " + harMessageNumber);
                    }

                    break;
                case SECTION:
                    responseMessageName = "tmdd:sectionPriorityQueueMsg";
                    queueItemName = "";
                    deviceType = "signal section";
                    if (devicePriorityQueueItemIndex == 1) {

                        for (String headerElement : createDevicePriorityQueueHeaderElements(EntityEmulationRequests.EntityRequestMessageType.SECTIONCONTROLREQUESTMSG.relatedMessage(), deviceType, deviceId, queuedRequests)) {
                            responseElements.add(responseMessageName + ".device-priority-queue-header." + headerElement.replace("authentication.operator-id", "operator-id"));
                        }

                        String sectionRequestCommand = "";
                        String requestControlMode = "";
                        String timingPatternId = "";
                        String sectionOffsetAdjustment = "";

                        //  Add the device-priority-queue-item for the request at the top of the queue
                        for (String thisElement : queuedRequests.getQueueHeadRequest(deviceId).getMessageSpec()) {
                            if (thisElement.contains(".section-request-command")) {
                                if (sectionRequestCommand.isEmpty()) {
                                    sectionRequestCommand = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                                }
                            } else if (thisElement.contains(".request-control-mode")) {
                                if (requestControlMode.isEmpty()) {
                                    requestControlMode = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                                }
                            } else if (thisElement.contains(".timing-pattern-id")) {
                                if (timingPatternId.isEmpty()) {
                                    timingPatternId = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                                }
                            } else if (thisElement.contains(".section-offset-adjustment")) {
                                if (sectionOffsetAdjustment.isEmpty()) {
                                    sectionOffsetAdjustment = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                                }
                            }
                        }

                        if (!sectionRequestCommand.isEmpty()) {
                            responseElements.add(responseMessageName + ".section-request-command = " + sectionRequestCommand);
                        }
                        if (!requestControlMode.isEmpty()) {
                            responseElements.add(responseMessageName + ".section-queue-parameters.request-control-mode = " + requestControlMode);
                        }
                        if (!timingPatternId.isEmpty()) {
                            responseElements.add(responseMessageName + ".section-queue-parameters.timing-pattern-id = " + timingPatternId);
                        }
                        if (!sectionOffsetAdjustment.isEmpty()) {
                            responseElements.add(responseMessageName + ".section-queue-parameters.section-offset-adjustment = " + sectionOffsetAdjustment);
                        }
                    }

                    break;
                case TRAFFICSIGNALCONTROLLER:
                    responseMessageName = "tmdd:intersectionSignalPriorityQueueMsg";
                    queueItemName = "intersection-signal-priority-queue-item";
                    deviceType = "signal controller";

                    for (String headerElement : createDevicePriorityQueueHeaderElements(EntityEmulationRequests.EntityRequestMessageType.INTERSECTIONSIGNALCONTROLREQUESTMSG.relatedMessage()+ ".device-control-request-header", deviceType, deviceId, queuedRequests)) {
                        responseElements.add(responseMessageName + "."+queueItemName + "[" + devicePriorityQueueItemIndex + "].device-priority-queue-header." + headerElement.replace("authentication.operator-id", "operator-id"));
                    }

                    String intersectionRequestCommand = "";
                    String requestTimingMode = "";
                    String timingPatternId = "";
                    String offsetAdjustment = "";

//  Add the device-priority-queue-item for the request at the top of the queue
                    for (String thisElement : queuedRequests.getQueueHeadRequest(deviceId).getMessageSpec()) {
                        if (thisElement.contains(".intersection-request-command")) {
                            if (intersectionRequestCommand.isEmpty()) {
                                intersectionRequestCommand = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                            }
                        } else if (thisElement.contains(".request-timing-mode")) {
                            if (requestTimingMode.isEmpty()) {
                                requestTimingMode = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                            }
                        } else if (thisElement.contains(".timing-pattern-id")) {
                            if (timingPatternId.isEmpty()) {
                                timingPatternId = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                            }
                        } else if (thisElement.contains(".offset-adjustment")) {
                            if (offsetAdjustment.isEmpty()) {
                                offsetAdjustment = thisElement.substring(thisElement.lastIndexOf("=") + 1);
                            }
                        }
                    }

                    if (!intersectionRequestCommand.isEmpty()) {
                        responseElements.add(responseMessageName + "." + queueItemName + "[" + devicePriorityQueueItemIndex + "].intersection-request-command = " + intersectionRequestCommand);
                    }
                    if (!requestTimingMode.isEmpty()) {
                        responseElements.add(responseMessageName + "." + queueItemName + "[" + devicePriorityQueueItemIndex + "].intersection-queue-parameters.request-timing-mode = " + requestTimingMode);
                    }
                    if (!timingPatternId.isEmpty()) {
                        responseElements.add(responseMessageName + "." + queueItemName + "[" + devicePriorityQueueItemIndex + "].intersection-queue-parameters.timing-pattern-id = " + timingPatternId);
                    }
                    if (!offsetAdjustment.isEmpty()) {
                        responseElements.add(responseMessageName + "." + queueItemName + "[" + devicePriorityQueueItemIndex + "].intersection-queue-parameters.offset-adjustment = " + offsetAdjustment);
                    }

                    break;
                default:
                    throw new EntityEmulationException("No Priority Queue exists for device type " + entityType);
            }
            devicePriorityQueueItemIndex++;
        }

        System.out.println("TMDDMessageProcessor::processDevicePriorityQueueRequestMsg returned the following:");
        for (String thisString: responseElements){
            System.out.println(thisString);
        }
        responseMsg = new MessageSpecification(responseElements);

        return responseMsg;
    }

    private static ArrayList<String> createDevicePriorityQueueHeaderElements(String requestMessagePrefix, String deviceType, String deviceId, final EntityPriorityQueueInformation queueInfo) {
        ArrayList<String> queueHeaderElements = new ArrayList();

        // First process the primary Queue Event
        System.out.println("TMDDMessageProcessor::createDevicePriorityQueueHeaderElemnts  deviceId value = "+deviceId);
        MessageSpecification headerSpec = queueInfo.getQueueHeadRequest(deviceId);       
        System.out.println("TMDDMessageProcessor::createDevicePriorityQueueHeaderElemnts  headerSpec is null ? = "+(headerSpec==null?"true":"false"));
        if (headerSpec == null)return queueHeaderElements;
        
        // Add restrictions
        queueHeaderElements.add("restrictions.organization-information-forwarding-restrictions = unrestricted");
        // Add device-id
        queueHeaderElements.add("device-id = " + deviceId);

        // Add the device-type
        queueHeaderElements.add("device-type = " + deviceType);

        // Add the current device priority
        queueHeaderElements.add("current-device-priority = 10");

        ArrayList<String> commandStartTime = new ArrayList<String>();
        ArrayList<String> commandEndTime = new ArrayList<String>();
        String commandRequestPriority = "";
        String operatorId = "";
        String requestId = "";
        String eventId = "";
        String responsePlanId = "";

        //  Add the device-priority-queue-item for the request at the top of the queue
        for (String thisElement : headerSpec.getMessageSpec()) {
            if (thisElement.startsWith(requestMessagePrefix + ".organization-requesting")) {
                queueHeaderElements.add("device-priority-queue-list.device-priority-queue-item[1]" + thisElement.replace(requestMessagePrefix, ""));
            } else if (thisElement.startsWith(requestMessagePrefix + ".command-request-priority")) {
                commandRequestPriority = thisElement.replace(requestMessagePrefix, "");
            } else if (thisElement.startsWith(requestMessagePrefix + ".authentication.operator-id")) {
                operatorId = thisElement.replace(requestMessagePrefix, "");
            } else if (thisElement.startsWith(requestMessagePrefix + ".request-id")) {
                requestId = thisElement.replace(requestMessagePrefix, "");
            } else if (thisElement.startsWith(requestMessagePrefix + ".event-id")) {
                eventId = thisElement.replace(requestMessagePrefix, "");
            } else if (thisElement.startsWith(requestMessagePrefix + ".responsePlanId")) {
                responsePlanId = thisElement.replace(requestMessagePrefix, "");
            } else if (thisElement.startsWith(requestMessagePrefix + ".command-start-time")) {
                commandStartTime.add(thisElement.replace(requestMessagePrefix, ""));
            } else if (thisElement.startsWith(requestMessagePrefix + ".command-end-time")) {
                commandEndTime.add(thisElement.replace(requestMessagePrefix, ""));
            }
        }

        if (!commandRequestPriority.isEmpty()) {
            queueHeaderElements.add("device-priority-queue-list.device-priority-queue-item[1]" + commandRequestPriority);
        }

        if (!operatorId.isEmpty()) {
            queueHeaderElements.add("device-priority-queue-list.device-priority-queue-item[1]" + operatorId);
        }
        if (!requestId.isEmpty()) {
            queueHeaderElements.add("device-priority-queue-list.device-priority-queue-item[1]" + requestId);
        }
        if (!eventId.isEmpty()) {
            queueHeaderElements.add("device-priority-queue-list.device-priority-queue-item[1]" + eventId);
        }
        if (!responsePlanId.isEmpty()) {
            queueHeaderElements.add("device-priority-queue-list.device-priority-queue-item[1]" + responsePlanId);
        }
        if (!commandStartTime.isEmpty()) {
            for (String thisElement : commandStartTime) {
                queueHeaderElements.add("device-priority-queue-list.device-priority-queue-item[1]" + thisElement);
            }
        }
        if (!commandEndTime.isEmpty()) {
            for (String thisElement : commandEndTime) {
                queueHeaderElements.add("device-priority-queue-list.device-priority-queue-item[1]" + thisElement);
            }
        }

        //  Add the device-priority-queue-item for the remaining queued requests (up to 7).  The TMDD standard design limits the 
        // total number allowed to 8.
        int currentRequestIndex = 2;

        for (MessageSpecification nextSpecification : queueInfo.getOtherQueueMessages(deviceId)) {
            if (currentRequestIndex <= 8) {
                commandStartTime.clear();
                commandEndTime.clear();
                commandRequestPriority = "";
                operatorId = "";
                requestId = "";
                eventId = "";
                responsePlanId = "";

                for (String nextElement : nextSpecification.getMessageSpec()) {

                    //  Add the device-priority-queue-item for the request at the top of the queue
                    if (nextElement.startsWith(requestMessagePrefix + ".organization-requesting")) {
                        queueHeaderElements.add("device-priority-queue-list.device-priority-queue-item[" + currentRequestIndex + "]" + nextElement.replace(requestMessagePrefix, ""));
                    } else if (nextElement.startsWith(requestMessagePrefix + ".command-request-priority")) {
                        commandRequestPriority = nextElement.replace(requestMessagePrefix, "");
                    } else if (nextElement.startsWith(requestMessagePrefix + ".authentication.operator-id")) {
                        operatorId = nextElement.replace(requestMessagePrefix, "");
                    } else if (nextElement.startsWith(requestMessagePrefix + ".request-id")) {
                        requestId = nextElement.replace(requestMessagePrefix, "");
                    } else if (nextElement.startsWith(requestMessagePrefix + ".event-id")) {
                        eventId = nextElement.replace(requestMessagePrefix, "");
                    } else if (nextElement.startsWith(requestMessagePrefix + ".responsePlanId")) {
                        responsePlanId = nextElement.replace(requestMessagePrefix, "");
                    } else if (nextElement.startsWith(requestMessagePrefix + ".command-start-time")) {
                        commandStartTime.add(nextElement.replace(requestMessagePrefix, ""));
                    } else if (nextElement.startsWith(requestMessagePrefix + ".command-end-time")) {
                        commandEndTime.add(nextElement.replace(requestMessagePrefix, ""));
                    }
                }

                if (!commandRequestPriority.isEmpty()) {
                    queueHeaderElements.add("device-priority-queue-list.device-priority-queue-item[" + currentRequestIndex + "]" + commandRequestPriority);
                }

                if (!operatorId.isEmpty()) {
                    queueHeaderElements.add("device-priority-queue-list.device-priority-queue-item[" + currentRequestIndex + "]" + operatorId);
                }
                if (!requestId.isEmpty()) {
                    queueHeaderElements.add("device-priority-queue-list.device-priority-queue-item[" + currentRequestIndex + "]" + requestId);
                }
                if (!eventId.isEmpty()) {
                    queueHeaderElements.add("device-priority-queue-list.device-priority-queue-item[" + currentRequestIndex + "]" + eventId);
                }
                if (!responsePlanId.isEmpty()) {
                    queueHeaderElements.add("device-priority-queue-list.device-priority-queue-item[" + currentRequestIndex + "]" + responsePlanId);
                }
                if (!commandStartTime.isEmpty()) {
                    for (String nextElement : commandStartTime) {
                        queueHeaderElements.add("device-priority-queue-list.device-priority-queue-item[" + currentRequestIndex + "]" + nextElement);
                    }
                }
                if (!commandEndTime.isEmpty()) {
                    for (String nextElement : commandEndTime) {
                        queueHeaderElements.add("device-priority-queue-list.device-priority-queue-item[" + currentRequestIndex + "]" + nextElement);
                    }
                }

            }

            currentRequestIndex++;

        }

        return queueHeaderElements;
    }

    /**
     * This method is responsible for processing the dMSControlRequestMsg
     * Message and generating an appropriate response message.
     *
     * @param requestMessage the request message received
     * @param entityDataType the type of entity data that this message is
     * related to
     * @param dataCollector the object that is capable of collecting the
     * required information
     * @param isPublication flag indicating whether the request is related to a
     * publication dialog.
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processDMSControlRequestMsg(MessageSpecification requestMessage, EntityCommandProcessor controlStatusCollector) throws NoMatchingDataException, EntityEmulationException {

        MessageSpecification responseMsg = null;

        // Confirm that the referenced DMS supports the requested command and that the parameters are valid for the selected DMS
        controlStatusCollector.verifyCommand(requestMessage);

        //  Create/Update a Control Request Status Record for this request    
        controlStatusCollector.queueCommand(requestMessage);

        try {
            // Perform the next CCTV Command that is ready to be processed right now.
            MessageSpecification nextCommand = EntityCommandQueue.getInstance().retrieveNextCommand(TMDDEntityType.EntityType.DMS);
            // Confirm that the referenced CCTV supports the requested command
            controlStatusCollector.verifyCommand(nextCommand);
            // Execute the command
            controlStatusCollector.executeCommand(nextCommand);
        } catch (InvalidEntityControlRequestStatusException ex) {
             ex.printStackTrace();
       } catch (InvalidCommandException ex) {
            ex.printStackTrace();
        } catch (EntityEmulationException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        responseMsg = controlStatusCollector.getControlResponseMessage(requestMessage);
        return responseMsg;

    }

    /**
     * This method is responsible for processing the dMSFontTableRequestMsg
     * Message and generating an appropriate response message.
     *
     * @param requestMessage the request message received
     * @param entityDataType the type of entity data that this message is
     * related to
     * @param dataCollector the object that is capable of collecting the
     * required information
     * @param isPublication flag indicating whether the request is related to a
     * publication dialog.
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processDMSFontTableRequestMsg(MessageSpecification requestMessage, EntityEmulationData.EntityDataType entityDataType, EntityDataInformationCollector dataCollector, boolean isPublication) throws NoMatchingDataException, EntityEmulationException {

        ArrayList<DataFilter> theFilters = new ArrayList();
        MessageSpecification responseMsg = null;

        // device-id filter
        ArrayList<String> deviceIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSFontTableRequestMsg.device-information-request-header.device-filter.device-id-list.device-id[*]");
        if (deviceIdFilterValues.size() > 0) {
            ValueInSetFilter deviceIdFilter = new ValueInSetFilter(entityDataType, "EntityId", deviceIdFilterValues);
            theFilters.add(deviceIdFilter);
        }

        // network-id filter
        ArrayList<String> networkIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSFontTableRequestMsg.device-information-request-header.device-filter.network-id-list.network-id[*]");
        if (networkIdFilterValues.size() > 0) {
            ValueInSetFilter networkIdFilter = new ValueInSetFilter(entityDataType, "NetworkId", networkIdFilterValues);
            theFilters.add(networkIdFilter);
        }

        // link filter
        ArrayList<String> linkFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSFontTableRequestMsg.device-information-request-header.device-filter.link-id-list.link[*]");
        if (linkFilterValues.size() > 0) {
            ValueInSetFilter linkFilter = new ValueInSetFilter(entityDataType, "LinkId", linkFilterValues);
            theFilters.add(linkFilter);
        }

        // link-designator filter
        ArrayList<String> linkDesignatorFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSFontTableRequestMsg.device-information-request-header.device-filter.link-designator-list.link-designator[*]");
        if (linkDesignatorFilterValues.size() > 0) {
            ValueInSetFilter linkDesignatorFilter = new ValueInSetFilter(entityDataType, "LinkDesignator", linkDesignatorFilterValues);
            theFilters.add(linkDesignatorFilter);
        }

        // linear reference filter
        String linearReferenceStart = requestMessage.getElementValue("tmdd:dMSFontTableRequestMsg.device-information-request-header.device-filter.linear-reference.linear-reference-start");
        String linearReferenceEnd = requestMessage.getElementValue("tmdd:dMSFontTableRequestMsg.device-information-request-header.device-filter.linear-reference.linear-reference-end");
        if ((linearReferenceStart != null) && (linearReferenceEnd != null) && (!linearReferenceStart.isEmpty()) && (!linearReferenceEnd.isEmpty())) {
            ValueInRangeFilter linearReferenceFilter = new ValueInRangeFilter(entityDataType, "LinearReference", linearReferenceStart, linearReferenceEnd);
            theFilters.add(linearReferenceFilter);
        }

        // section-id filter
        ArrayList<String> sectionIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSFontTableRequestMsg.device-information-request-header.device-filter.section-id-list.section-id[*]");
        if (sectionIdFilterValues.size() > 0) {
            ValueInSetFilter sectionIdFilter = new ValueInSetFilter(entityDataType, "SectionId", sectionIdFilterValues);
            theFilters.add(sectionIdFilter);
        }

        // pattern-id filter
        ArrayList<String> patternIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSFontTableRequestMsg.device-information-request-header.device-filter.pattern-id-list.pattern-id[*]");
        if (patternIdFilterValues.size() > 0) {
            ValueInSetFilter patternIdFilter = new ValueInSetFilter(entityDataType, "PatternId", patternIdFilterValues);
            theFilters.add(patternIdFilter);
        }

        // center-id filter        
        ArrayList<String> centerIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSFontTableRequestMsg.device-information-request-header.device-filter.center-id-list.center-id[*]");
        if (centerIdFilterValues.size() > 0) {
            ValueInSetFilter centerIdFilter = new ValueInSetFilter(entityDataType, "CenterId", centerIdFilterValues);
            theFilters.add(centerIdFilter);
        }

        // detector-station-id filter        
        ArrayList<String> detectorStationIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSFontTableRequestMsg.detector-station-id");
        if (detectorStationIdFilterValues.size() > 0) {
            ValueInSetFilter detectorStationIdFilter = new ValueInSetFilter(entityDataType, "DetectorStationId", detectorStationIdFilterValues);
            theFilters.add(detectorStationIdFilter);
        }

        // detector-data-type filter        
        ArrayList<String> detectorDataTypeFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSFontTableRequestMsg.detector-data-type");
        if (detectorDataTypeFilterValues.size() > 0) {
            ValueInSetFilter detectorDataTypeFilter = new ValueInSetFilter(entityDataType, "DetectorDataTypeId", detectorDataTypeFilterValues);
            theFilters.add(detectorDataTypeFilter);
        }

        // tmddv3.1 time-range filter    
        if (tmddv31AndLater){
            String timeRangeStartDate = requestMessage.getElementValue("tmdd:dMSFontTableRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.date");
            String timeRangeStartTime = requestMessage.getElementValue("tmdd:dMSFontTableRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.time");
            String timeRangeOffset = requestMessage.getElementValue("tmdd:dMSFontTableRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.offset");
            String timeRangePeriodInSeconds = requestMessage.getElementValue("tmdd:dMSFontTableRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.period-seconds");
            if ((timeRangeStartDate != null) && (timeRangeStartTime != null) && (!timeRangeStartDate.isEmpty()) && (!timeRangeStartTime.isEmpty())) {
                ValueInDateRangeFilter timeRangeFilter = new ValueInDateRangeFilter("TimeRangeDate", "TimeRangeTime", "TimeRangeOffset", timeRangeStartDate, timeRangeStartTime, timeRangeOffset, timeRangePeriodInSeconds);
                theFilters.add(timeRangeFilter);
            }      
        }                
        ArrayList<EntityDataRecord> theResponse = dataCollector.getEntityElements(theFilters);

        ArrayList<String> generatedResponse = new ArrayList();
        for (EntityDataRecord thisRecord : theResponse) {
            generatedResponse.add(thisRecord.getEntityElement() + " = " + thisRecord.getEntityElementValue());
        }
        responseMsg = new MessageSpecification(generatedResponse);

        // If the request message is part of a subscription message we respond with the C2C Receipt instead.
        // We needed to complete the above process of generating a full response, just to ensure that some error would not have occured.
        if (!isPublication && requestMessage.containsMessageOfType("c2c:c2cMessageSubscription")) {
            ArrayList<String> c2cResponse = new ArrayList();
            c2cResponse.add("c2c:c2cMessageReceipt.informationalText = OK (Emulation Mode)");
            responseMsg = new MessageSpecification(c2cResponse);
        }

        return responseMsg;
    }

    /**
     * This method is responsible for processing the
     * dMSMessageAppearanceRequestMsg Message and generating an appropriate
     * response message.
     *
     * @param requestMessage the request message received
     * @param entityDataType the type of entity data that this message is
     * related to
     * @param dataCollector the object that is capable of collecting the
     * required information
     * @param isPublication flag indicating whether the request is related to a
     * publication dialog.
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processDMSMessageAppearanceRequestMsg(MessageSpecification requestMessage, EntityEmulationData.EntityDataType entityDataType, EntityDataInformationCollector dataCollector, boolean isPublication) throws NoMatchingDataException, EntityEmulationException {

        ArrayList<DataFilter> theFilters = new ArrayList();
        MessageSpecification responseMsg = null;

        // device-id filter
        ArrayList<String> deviceIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageAppearanceRequestMsg.device-information-request-header.device-filter.device-id-list.device-id[*]");
        if (deviceIdFilterValues.size() > 0) {
            ValueInSetFilter deviceIdFilter = new ValueInSetFilter(entityDataType, "EntityId", deviceIdFilterValues);
            theFilters.add(deviceIdFilter);
        }

        // network-id filter
        ArrayList<String> networkIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageAppearanceRequestMsg.device-information-request-header.device-filter.network-id-list.network-id[*]");
        if (networkIdFilterValues.size() > 0) {
            ValueInSetFilter networkIdFilter = new ValueInSetFilter(entityDataType, "NetworkId", networkIdFilterValues);
            theFilters.add(networkIdFilter);
        }

        // link filter
        ArrayList<String> linkFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageAppearanceRequestMsg.device-information-request-header.device-filter.link-id-list.link[*]");
        if (linkFilterValues.size() > 0) {
            ValueInSetFilter linkFilter = new ValueInSetFilter(entityDataType, "LinkId", linkFilterValues);
            theFilters.add(linkFilter);
        }

        // link-designator filter
        ArrayList<String> linkDesignatorFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageAppearanceRequestMsg.device-information-request-header.device-filter.link-designator-list.link-designator[*]");
        if (linkDesignatorFilterValues.size() > 0) {
            ValueInSetFilter linkDesignatorFilter = new ValueInSetFilter(entityDataType, "LinkDesignator", linkDesignatorFilterValues);
            theFilters.add(linkDesignatorFilter);
        }

        // linear reference filter
        String linearReferenceStart = requestMessage.getElementValue("tmdd:dMSMessageAppearanceRequestMsg.device-information-request-header.device-filter.linear-reference.linear-reference-start");
        String linearReferenceEnd = requestMessage.getElementValue("tmdd:dMSMessageAppearanceRequestMsg.device-information-request-header.device-filter.linear-reference.linear-reference-end");
        if ((linearReferenceStart != null) && (linearReferenceEnd != null) && (!linearReferenceStart.isEmpty()) && (!linearReferenceEnd.isEmpty())) {
            ValueInRangeFilter linearReferenceFilter = new ValueInRangeFilter(entityDataType, "LinearReference", linearReferenceStart, linearReferenceEnd);
            theFilters.add(linearReferenceFilter);
        }

        // section-id filter
        ArrayList<String> sectionIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageAppearanceRequestMsg.device-information-request-header.device-filter.section-id-list.section-id[*]");
        if (sectionIdFilterValues.size() > 0) {
            ValueInSetFilter sectionIdFilter = new ValueInSetFilter(entityDataType, "SectionId", sectionIdFilterValues);
            theFilters.add(sectionIdFilter);
        }

        // pattern-id filter
        ArrayList<String> patternIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageAppearanceRequestMsg.device-information-request-header.device-filter.pattern-id-list.pattern-id[*]");
        if (patternIdFilterValues.size() > 0) {
            ValueInSetFilter patternIdFilter = new ValueInSetFilter(entityDataType, "PatternId", patternIdFilterValues);
            theFilters.add(patternIdFilter);
        }

        // center-id filter        
        ArrayList<String> centerIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageAppearanceRequestMsg.device-information-request-header.device-filter.center-id-list.center-id[*]");
        if (centerIdFilterValues.size() > 0) {
            ValueInSetFilter centerIdFilter = new ValueInSetFilter(entityDataType, "CenterId", centerIdFilterValues);
            theFilters.add(centerIdFilter);
        }

        // detector-station-id filter        
        ArrayList<String> detectorStationIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageAppearanceRequestMsg.detector-station-id");
        if (detectorStationIdFilterValues.size() > 0) {
            ValueInSetFilter detectorStationIdFilter = new ValueInSetFilter(entityDataType, "DetectorStationId", detectorStationIdFilterValues);
            theFilters.add(detectorStationIdFilter);
        }

        // detector-data-type filter        
        ArrayList<String> detectorDataTypeFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageAppearanceRequestMsg.detector-data-type");
        if (detectorDataTypeFilterValues.size() > 0) {
            ValueInSetFilter detectorDataTypeFilter = new ValueInSetFilter(entityDataType, "DetectorDataTypeId", detectorDataTypeFilterValues);
            theFilters.add(detectorDataTypeFilter);
        }

        // tmddv3.1 time-range filter  (No Matching time elements are available in the Response Message definition)      
//        String timeRangeStartDate = requestMessage.getElementValue("tmdd:dMSMessageAppearanceRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.date");
//        String timeRangeStartTime = requestMessage.getElementValue("tmdd:dMSMessageAppearanceRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.time");
//        String timeRangeOffset = requestMessage.getElementValue("tmdd:dMSMessageAppearanceRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.offset");
//        String timeRangePeriodInSeconds = requestMessage.getElementValue("tmdd:dMSMessageAppearanceRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.period-seconds");
//        if ((timeRangeStartDate != null) && (timeRangeStartTime != null) && (!timeRangeStartDate.isEmpty()) && (!timeRangeStartTime.isEmpty())) {
//            ValueInDateRangeFilter timeRangeFilter = new ValueInDateRangeFilter("TimeRangeDate", "TimeRangeTime", "TimeRangeOffset", timeRangeStartDate, timeRangeStartTime, timeRangeOffset, timeRangePeriodInSeconds);
//            theFilters.add(timeRangeFilter);
//        }      
        
        ArrayList<EntityDataRecord> theResponse = dataCollector.getEntityElements(theFilters);

        ArrayList<String> generatedResponse = new ArrayList();
        for (EntityDataRecord thisRecord : theResponse) {
            generatedResponse.add(thisRecord.getEntityElement() + " = " + thisRecord.getEntityElementValue());
        }
        responseMsg = new MessageSpecification(generatedResponse);

        // If the request message is part of a subscription message we respond with the C2C Receipt instead.
        // We needed to complete the above process of generating a full response, just to ensure that some error would not have occured.
        if (!isPublication && requestMessage.containsMessageOfType("c2c:c2cMessageSubscription")) {
            ArrayList<String> c2cResponse = new ArrayList();
            c2cResponse.add("c2c:c2cMessageReceipt.informationalText = OK (Emulation Mode)");
            responseMsg = new MessageSpecification(c2cResponse);
        }

        return responseMsg;
    }

    /**
     * This method is responsible for processing the
     * dMSMessageInventoryRequestMsg Message and generating an appropriate
     * response message.
     *
     * @param requestMessage the request message received
     * @param entityDataType the type of entity data that this message is
     * related to
     * @param dataCollector the object that is capable of collecting the
     * required information
     * @param isPublication flag indicating whether the request is related to a
     * publication dialog.
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processDMSMessageInventoryRequestMsg(MessageSpecification requestMessage, EntityEmulationData.EntityDataType entityDataType, EntityDataInformationCollector dataCollector, boolean isPublication) throws NoMatchingDataException, EntityEmulationException {

        ArrayList<DataFilter> theFilters = new ArrayList();
        MessageSpecification responseMsg = null;

        // device-id filter
        ArrayList<String> deviceIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageInventoryRequestMsg.device-information-request-header.device-filter.device-id-list.device-id[*]");
        if (deviceIdFilterValues.size() > 0) {
            ValueInSetFilter deviceIdFilter = new ValueInSetFilter(entityDataType, "EntityId", deviceIdFilterValues);
            theFilters.add(deviceIdFilter);
        }

        // network-id filter
        ArrayList<String> networkIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageInventoryRequestMsg.device-information-request-header.device-filter.network-id-list.network-id[*]");
        if (networkIdFilterValues.size() > 0) {
            ValueInSetFilter networkIdFilter = new ValueInSetFilter(entityDataType, "NetworkId", networkIdFilterValues);
            theFilters.add(networkIdFilter);
        }

        // link filter
        ArrayList<String> linkFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageInventoryRequestMsg.device-information-request-header.device-filter.link-id-list.link[*]");
        if (linkFilterValues.size() > 0) {
            ValueInSetFilter linkFilter = new ValueInSetFilter(entityDataType, "LinkId", linkFilterValues);
            theFilters.add(linkFilter);
        }

        // link-designator filter
        ArrayList<String> linkDesignatorFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageInventoryRequestMsg.device-information-request-header.device-filter.link-designator-list.link-designator[*]");
        if (linkDesignatorFilterValues.size() > 0) {
            ValueInSetFilter linkDesignatorFilter = new ValueInSetFilter(entityDataType, "LinkDesignator", linkDesignatorFilterValues);
            theFilters.add(linkDesignatorFilter);
        }

        // linear reference filter
        String linearReferenceStart = requestMessage.getElementValue("tmdd:dMSMessageInventoryRequestMsg.device-information-request-header.device-filter.linear-reference.linear-reference-start");
        String linearReferenceEnd = requestMessage.getElementValue("tmdd:dMSMessageInventoryRequestMsg.device-information-request-header.device-filter.linear-reference.linear-reference-end");
        if ((linearReferenceStart != null) && (linearReferenceEnd != null) && (!linearReferenceStart.isEmpty()) && (!linearReferenceEnd.isEmpty())) {
            ValueInRangeFilter linearReferenceFilter = new ValueInRangeFilter(entityDataType, "LinearReference", linearReferenceStart, linearReferenceEnd);
            theFilters.add(linearReferenceFilter);
        }

        // section-id filter
        ArrayList<String> sectionIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageInventoryRequestMsg.device-information-request-header.device-filter.section-id-list.section-id[*]");
        if (sectionIdFilterValues.size() > 0) {
            ValueInSetFilter sectionIdFilter = new ValueInSetFilter(entityDataType, "SectionId", sectionIdFilterValues);
            theFilters.add(sectionIdFilter);
        }

        // pattern-id filter
        ArrayList<String> patternIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageInventoryRequestMsg.device-information-request-header.device-filter.pattern-id-list.pattern-id[*]");
        if (patternIdFilterValues.size() > 0) {
            ValueInSetFilter patternIdFilter = new ValueInSetFilter(entityDataType, "PatternId", patternIdFilterValues);
            theFilters.add(patternIdFilter);
        }

        // center-id filter        
        ArrayList<String> centerIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageInventoryRequestMsg.device-information-request-header.device-filter.center-id-list.center-id[*]");
        if (centerIdFilterValues.size() > 0) {
            ValueInSetFilter centerIdFilter = new ValueInSetFilter(entityDataType, "CenterId", centerIdFilterValues);
            theFilters.add(centerIdFilter);
        }

        // detector-station-id filter        
        ArrayList<String> detectorStationIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageInventoryRequestMsg.detector-station-id");
        if (detectorStationIdFilterValues.size() > 0) {
            ValueInSetFilter detectorStationIdFilter = new ValueInSetFilter(entityDataType, "DetectorStationId", detectorStationIdFilterValues);
            theFilters.add(detectorStationIdFilter);
        }

        // detector-data-type filter        
        ArrayList<String> detectorDataTypeFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageInventoryRequestMsg.detector-data-type");
        if (detectorDataTypeFilterValues.size() > 0) {
            ValueInSetFilter detectorDataTypeFilter = new ValueInSetFilter(entityDataType, "DetectorDataTypeId", detectorDataTypeFilterValues);
            theFilters.add(detectorDataTypeFilter);
        }

        // tmddv3.1 time-range filter  
        if (tmddv31AndLater){
            String timeRangeStartDate = requestMessage.getElementValue("tmdd:dMSMessageInventoryRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.date");
            String timeRangeStartTime = requestMessage.getElementValue("tmdd:dMSMessageInventoryRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.time");
            String timeRangeOffset = requestMessage.getElementValue("tmdd:dMSMessageInventoryRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.offset");
            String timeRangePeriodInSeconds = requestMessage.getElementValue("tmdd:dMSMessageInventoryRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.period-seconds");
            if ((timeRangeStartDate != null) && (timeRangeStartTime != null) && (!timeRangeStartDate.isEmpty()) && (!timeRangeStartTime.isEmpty())) {
                ValueInDateRangeFilter timeRangeFilter = new ValueInDateRangeFilter("TimeRangeDate", "TimeRangeTime", "TimeRangeOffset", timeRangeStartDate, timeRangeStartTime, timeRangeOffset, timeRangePeriodInSeconds);
                theFilters.add(timeRangeFilter);
            }      
        }                
        ArrayList<EntityDataRecord> theResponse = dataCollector.getEntityElements(theFilters);

        ArrayList<String> generatedResponse = new ArrayList();
        for (EntityDataRecord thisRecord : theResponse) {
            generatedResponse.add(thisRecord.getEntityElement() + " = " + thisRecord.getEntityElementValue());
        }
        responseMsg = new MessageSpecification(generatedResponse);

        // If the request message is part of a subscription message we respond with the C2C Receipt instead.
        // We needed to complete the above process of generating a full response, just to ensure that some error would not have occured.
        if (!isPublication && requestMessage.containsMessageOfType("c2c:c2cMessageSubscription")) {
            ArrayList<String> c2cResponse = new ArrayList();
            c2cResponse.add("c2c:c2cMessageReceipt.informationalText = OK (Emulation Mode)");
            responseMsg = new MessageSpecification(c2cResponse);
        }

        return responseMsg;
    }

    /**
     * This method is responsible for processing the eventRequestMsg Message and
     * generating an appropriate response message.
     *
     * @param requestMessage the request message received
     * @param entityDataType the type of entity data that this message is
     * related to
     * @param dataCollector the object that is capable of collecting the
     * required information
     * @param isPublication flag indicating whether the request is related to a
     * publication dialog.
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processEventRequestMsg(MessageSpecification requestMessage, EntityEmulationData.EntityDataType entityDataType, EntityDataInformationCollector dataCollector, boolean isPublication) throws NoMatchingDataException, EntityEmulationException {
        long startTime = System.currentTimeMillis();
        long filterTime = 0;
        long filteredResponseTime = 0;
        long generatedResponseTime = 0;
        
        ArrayList<DataFilter> theFilters = new ArrayList();
        MessageSpecification responseMsg = null;

/*
EntityId
NetworkId  -
LinkId -
LinkDesignator 
LinearReference -
LinearReferenceEnd  -
SectionId -
PatternId -
CenterId
Severity
HazmatCode
PlacardCode
OrganizationsRequestedOrgId
OrganizationsRequestedOrgName
OrganizationsRequestedOrgLocation
OrganizationsRequestedOrgFunction        
        */        
        
        // event-id filter
        ArrayList<String> eventIdFilterValues = requestMessage.getValuesOfInstances("tmdd:eventRequestMsg.request-type.event-ids.event-id[*]");
        if (eventIdFilterValues.size() > 0) {
            ValueInSetFilter eventIdFilter = new ValueInSetFilter(entityDataType, "EventId", eventIdFilterValues);
            theFilters.add(eventIdFilter);
        }

        // link-designator filter
        ArrayList<String> linkDesignatorFilterValues = requestMessage.getValuesOfInstances("tmdd:eventRequestMsg.request-locations.request-location[*].link-designator.link-designator-item[*]");
        if (linkDesignatorFilterValues.size() > 0) {
            ValueInSetFilter linkDesignatorFilter = new ValueInSetFilter(entityDataType, "LinkDesignator", linkDesignatorFilterValues);
            theFilters.add(linkDesignatorFilter);
        }

        // center-id filter        
        ArrayList<String> centerIdFilterValues = requestMessage.getValuesOfInstances("tmdd:eventRequestMsg.request-filters.request-filter.organizations-requested.organization-requested-item.center-contact-list.center-contact-details[*].center-id");
        if (centerIdFilterValues.size() > 0) {
            ValueInSetFilter centerIdFilter = new ValueInSetFilter(entityDataType, "CenterId", centerIdFilterValues);
            theFilters.add(centerIdFilter);
        }

        // severity filter        
        ArrayList<String> severityFilterValues = requestMessage.getValuesOfInstances("tmdd:eventRequestMsg.request-filters.request-filter[*].severity");
        if (severityFilterValues.size() > 0) {
            ValueInSetFilter severityFilter = new ValueInSetFilter(entityDataType, "Severity", severityFilterValues);
            theFilters.add(severityFilter);
        }

        // Org Id filter        
        ArrayList<String> orgIdFilterValues = requestMessage.getValuesOfInstances("tmdd:eventRequestMsg.request-filters.request-filter.organizations-requested.organizations-requested-item[*].organization-id");
        if (orgIdFilterValues.size() > 0) {
            ValueInSetFilter orgIdFilter = new ValueInSetFilter(entityDataType, "OrganizationsRequestedOrgId", orgIdFilterValues);
            theFilters.add(orgIdFilter);
        }
        
        // Org Name filter
        ArrayList<String> orgNameFilterValues = requestMessage.getValuesOfInstances("tmdd:eventRequestMsg.request-filters.request-filter.organizations-requested.organizations-requested-item[*].organization-name");
        if (orgNameFilterValues.size() > 0) {
            ValueInSetFilter orgNameFilter = new ValueInSetFilter(entityDataType, "OrganizationsRequestedOrgName", orgNameFilterValues);
            theFilters.add(orgNameFilter);
        }

        // Org Location filter
        ArrayList<String> orgLocationFilterValues = requestMessage.getValuesOfInstances("tmdd:eventRequestMsg.request-filters.request-filter.organizations-requested.organizations-requested-item[*].organization-location");
        if (orgLocationFilterValues.size() > 0) {
            ValueInSetFilter orgLocationFilter = new ValueInSetFilter(entityDataType, "OrganizationsRequestedOrgLocation", orgLocationFilterValues);
            theFilters.add(orgLocationFilter);
        }

        // Org Function filter
        ArrayList<String> orgFunctionFilterValues = requestMessage.getValuesOfInstances("tmdd:eventRequestMsg.request-filters.request-filter.organizations-requested.organizations-requested-item[*].organization-function");
        if (orgFunctionFilterValues.size() > 0) {
            ValueInSetFilter orgFunctionFilter = new ValueInSetFilter(entityDataType, "OrganizationsRequestedOrgFunction", orgFunctionFilterValues);
            theFilters.add(orgFunctionFilter);
        }

//        // Org Function filter
//        String linearReferenceStart = requestMessage.getElementValue("tmdd:eventRequestMsg.device-information-request-header.device-filter.linear-reference.linear-reference-start");
//        String linearReferenceEnd = requestMessage.getElementValue("tmdd:eventRequestMsg.device-information-request-header.device-filter.linear-reference.linear-reference-end");
//        if ((linearReferenceStart != null) && (linearReferenceEnd != null) && (!linearReferenceStart.isEmpty()) && (!linearReferenceEnd.isEmpty())) {
//            ValueInRangeFilter linearReferenceFilter = new ValueInRangeFilter(entityDataType, "LinearReference", linearReferenceStart, linearReferenceEnd);
//            theFilters.add(linearReferenceFilter);
//        }
//
//        // pattern-id filter
//        ArrayList<String> patternIdFilterValues = requestMessage.getValuesOfInstances("tmdd:eventRequestMsg.device-information-request-header.device-filter.pattern-id-list[*].pattern-id");
//        if (patternIdFilterValues.size() > 0) {
//            ValueInSetFilter patternIdFilter = new ValueInSetFilter(entityDataType, "PatternId", patternIdFilterValues);
//            theFilters.add(patternIdFilter);
//        }

        filterTime = System.currentTimeMillis();

        ArrayList<EntityDataRecord> theResponse = dataCollector.getEntityElements(theFilters);
        
        filteredResponseTime = System.currentTimeMillis();

        ArrayList<String> generatedResponse = new ArrayList();
        for (EntityDataRecord thisRecord : theResponse) {
            generatedResponse.add(thisRecord.getEntityElement() + " = " + thisRecord.getEntityElementValue());
        }
        responseMsg = new MessageSpecification(generatedResponse);

        generatedResponseTime = System.currentTimeMillis();
        
        System.out.println("Event Request Time reported the following durations (ms): \nIntial Filter = "+(filterTime-startTime) +"\nFilteredResponse = "+(filteredResponseTime - filterTime)+"\nResponseTime = "+(generatedResponseTime - filteredResponseTime));
        
        // If the request message is part of a subscription message we respond with the C2C Receipt instead.
        // We needed to complete the above process of generating a full response, just to ensure that some error would not have occured.
        if (!isPublication && requestMessage.containsMessageOfType("c2c:c2cMessageSubscription")) {
            ArrayList<String> c2cResponse = new ArrayList();
            c2cResponse.add("c2c:c2cMessageReceipt.informationalText = OK (Emulation Mode)");
            responseMsg = new MessageSpecification(c2cResponse);
        }

        return responseMsg;
    }

    /**
     * This method is responsible for processing the gateControlRequestMsg
     * Message and generating an appropriate response message.
     *
     * @param requestMessage the request message received
     * @param controlStatusCollector
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processGateControlRequestMsg(MessageSpecification requestMessage, EntityCommandProcessor controlStatusCollector) throws NoMatchingDataException, EntityEmulationException {

        MessageSpecification responseMsg;

        // Confirm that the referenced GATE supports the requested command and that the parameters are valid for the selected GATE
        controlStatusCollector.verifyCommand(requestMessage);

        //  Create/Update a Control Request Status Record for this request    
        controlStatusCollector.queueCommand(requestMessage);

        try {
            // Perform the next CCTV Command that is ready to be processed right now.
            MessageSpecification nextCommand = EntityCommandQueue.getInstance().retrieveNextCommand(TMDDEntityType.EntityType.GATES);
            // Confirm that the referenced CCTV supports the requested command
            controlStatusCollector.verifyCommand(nextCommand);
            // Execute the command
            controlStatusCollector.executeCommand(nextCommand);
        } catch (InvalidEntityControlRequestStatusException ex) {
            ex.printStackTrace();
        } catch (InvalidCommandException ex) {
            ex.printStackTrace();
        } catch (EntityEmulationException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        responseMsg = controlStatusCollector.getControlResponseMessage(requestMessage);
        return responseMsg;

    }

    /**
     * This method is responsible for processing the hARControlRequestMsg
     * Message and generating an appropriate response message.
     *
     * @param requestMessage the request message received
     * @param controlStatusCollector
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processHARControlRequestMsg(MessageSpecification requestMessage, EntityCommandProcessor controlStatusCollector) throws NoMatchingDataException, EntityEmulationException {

        MessageSpecification responseMsg;

        // Confirm that the referenced HAR supports the requested command and that the parameters are valid for the selected HAR
        controlStatusCollector.verifyCommand(requestMessage);

        //  Create/Update a Control Request Status Record for this request    
        controlStatusCollector.queueCommand(requestMessage);

        try {
            // Perform the next CCTV Command that is ready to be processed right now.
            MessageSpecification nextCommand = EntityCommandQueue.getInstance().retrieveNextCommand(TMDDEntityType.EntityType.HAR);
            // Confirm that the referenced CCTV supports the requested command
            controlStatusCollector.verifyCommand(nextCommand);
            // Execute the command
            controlStatusCollector.executeCommand(nextCommand);
        } catch (InvalidEntityControlRequestStatusException ex) {
            ex.printStackTrace();
        } catch (InvalidCommandException ex) {
            ex.printStackTrace();
        } catch (EntityEmulationException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        responseMsg = controlStatusCollector.getControlResponseMessage(requestMessage);
        return responseMsg;

    }

    /**
     * This method is responsible for processing the
     * intersectionSignalControlRequestMsg Message and generating an appropriate
     * response message.
     *
     * @param requestMessage the request message received
     * @param controlStatusCollector
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processIntersectionSignalControlRequestMsg(MessageSpecification requestMessage, EntityCommandProcessor controlStatusCollector) throws NoMatchingDataException, EntityEmulationException {

        MessageSpecification responseMsg;

        // Confirm that the referenced TRAFFIC SIGNAL supports the requested command and that the parameters are valid for the selected INTERSECTION SIGNAL
        controlStatusCollector.verifyCommand(requestMessage);

        //  Create/Update a Control Request Status Record for this request    
        controlStatusCollector.queueCommand(requestMessage);

        try {
            // Perform the next Traffic Signal Command that is ready to be processed right now.
            MessageSpecification nextCommand = EntityCommandQueue.getInstance().retrieveNextCommand(TMDDEntityType.EntityType.TRAFFICSIGNALCONTROLLER);
            // Confirm that the referenced Traffic Signal supports the requested command
            controlStatusCollector.verifyCommand(nextCommand);
            // Execute the command
            controlStatusCollector.executeCommand(nextCommand);
        } catch (InvalidEntityControlRequestStatusException ex) {
            ex.printStackTrace();
        } catch (InvalidCommandException ex) {
            ex.printStackTrace();
        } catch (EntityEmulationException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        responseMsg = controlStatusCollector.getControlResponseMessage(requestMessage);
        return responseMsg;

    }

    /**
     * This method is responsible for processing the
     * intersectionSignalTimingPatternInventoryRequestMsg Message and generating
     * an appropriate response message.
     *
     * @param requestMessage the request message received
     * @param entityDataType the type of entity data that this message is
     * related to
     * @param dataCollector the object that is capable of collecting the
     * required information
     * @param isPublication flag indicating whether the request is related to a
     * publication dialog.
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processIntersectionSignalTimingPatternInventoryRequestMsg(MessageSpecification requestMessage, EntityEmulationData.EntityDataType entityDataType, EntityDataInformationCollector dataCollector, boolean isPublication) throws NoMatchingDataException, EntityEmulationException {

        ArrayList<DataFilter> theFilters = new ArrayList();
        MessageSpecification responseMsg = null;

        // device-id filter
        ArrayList<String> deviceIdFilterValues = requestMessage.getValuesOfInstances("tmdd:intersectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.device-id-list.device-id[*]");
        if (deviceIdFilterValues.size() > 0) {
            ValueInSetFilter deviceIdFilter = new ValueInSetFilter(entityDataType, "EntityId", deviceIdFilterValues);
            theFilters.add(deviceIdFilter);
        }

        // network-id filter
        ArrayList<String> networkIdFilterValues = requestMessage.getValuesOfInstances("tmdd:intersectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.network-id-list.network-id[*]");
        if (networkIdFilterValues.size() > 0) {
            ValueInSetFilter networkIdFilter = new ValueInSetFilter(entityDataType, "NetworkId", networkIdFilterValues);
            theFilters.add(networkIdFilter);
        }

        // link filter
        ArrayList<String> linkFilterValues = requestMessage.getValuesOfInstances("tmdd:intersectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.link-id-list.link[*]");
        if (linkFilterValues.size() > 0) {
            ValueInSetFilter linkFilter = new ValueInSetFilter(entityDataType, "LinkId", linkFilterValues);
            theFilters.add(linkFilter);
        }

        // link-designator filter
        ArrayList<String> linkDesignatorFilterValues = requestMessage.getValuesOfInstances("tmdd:intersectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.link-designator-list.link-designator[*]");
        if (linkDesignatorFilterValues.size() > 0) {
            ValueInSetFilter linkDesignatorFilter = new ValueInSetFilter(entityDataType, "LinkDesignator", linkDesignatorFilterValues);
            theFilters.add(linkDesignatorFilter);
        }

        // linear reference filter
        String linearReferenceStart = requestMessage.getElementValue("tmdd:intersectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.linear-reference.linear-reference-start");
        String linearReferenceEnd = requestMessage.getElementValue("tmdd:intersectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.linear-reference.linear-reference-end");
        if ((linearReferenceStart != null) && (linearReferenceEnd != null) && (!linearReferenceStart.isEmpty()) && (!linearReferenceEnd.isEmpty())) {
            ValueInRangeFilter linearReferenceFilter = new ValueInRangeFilter(entityDataType, "LinearReference", linearReferenceStart, linearReferenceEnd);
            theFilters.add(linearReferenceFilter);
        }

        // section-id filter
        ArrayList<String> sectionIdFilterValues = requestMessage.getValuesOfInstances("tmdd:intersectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.section-id-list.section-id[*]");
        if (sectionIdFilterValues.size() > 0) {
            ValueInSetFilter sectionIdFilter = new ValueInSetFilter(entityDataType, "SectionId", sectionIdFilterValues);
            theFilters.add(sectionIdFilter);
        }

        // pattern-id filter
        ArrayList<String> patternIdFilterValues = requestMessage.getValuesOfInstances("tmdd:intersectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.pattern-id-list.pattern-id[*]");
        if (patternIdFilterValues.size() > 0) {
            ValueInSetFilter patternIdFilter = new ValueInSetFilter(entityDataType, "PatternId", patternIdFilterValues);
            theFilters.add(patternIdFilter);
        }

        // center-id filter        
        ArrayList<String> centerIdFilterValues = requestMessage.getValuesOfInstances("tmdd:intersectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.center-id-list.center-id[*]");
        if (centerIdFilterValues.size() > 0) {
            ValueInSetFilter centerIdFilter = new ValueInSetFilter(entityDataType, "CenterId", centerIdFilterValues);
            theFilters.add(centerIdFilter);
        }

        // detector-station-id filter        
        ArrayList<String> detectorStationIdFilterValues = requestMessage.getValuesOfInstances("tmdd:intersectionSignalTimingPatternInventoryRequestMsg.detector-station-id");
        if (detectorStationIdFilterValues.size() > 0) {
            ValueInSetFilter detectorStationIdFilter = new ValueInSetFilter(entityDataType, "DetectorStationId", detectorStationIdFilterValues);
            theFilters.add(detectorStationIdFilter);
        }

        // detector-data-type filter        
        ArrayList<String> detectorDataTypeFilterValues = requestMessage.getValuesOfInstances("tmdd:intersectionSignalTimingPatternInventoryRequestMsg.detector-data-type");
        if (detectorDataTypeFilterValues.size() > 0) {
            ValueInSetFilter detectorDataTypeFilter = new ValueInSetFilter(entityDataType, "DetectorDataTypeId", detectorDataTypeFilterValues);
            theFilters.add(detectorDataTypeFilter);
        }

        // tmddv3.1 time-range filter   
        if (tmddv31AndLater){
            String timeRangeStartDate = requestMessage.getElementValue("tmdd:intersectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.date");
            String timeRangeStartTime = requestMessage.getElementValue("tmdd:intersectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.time");
            String timeRangeOffset = requestMessage.getElementValue("tmdd:intersectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.offset");
            String timeRangePeriodInSeconds = requestMessage.getElementValue("tmdd:intersectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.period-seconds");
            if ((timeRangeStartDate != null) && (timeRangeStartTime != null) && (!timeRangeStartDate.isEmpty()) && (!timeRangeStartTime.isEmpty())) {
                ValueInDateRangeFilter timeRangeFilter = new ValueInDateRangeFilter("TimeRangeDate", "TimeRangeTime", "TimeRangeOffset", timeRangeStartDate, timeRangeStartTime, timeRangeOffset, timeRangePeriodInSeconds);
                theFilters.add(timeRangeFilter);
            }              
        }        
        
        ArrayList<EntityDataRecord> theResponse = dataCollector.getEntityElements(theFilters);

        ArrayList<String> generatedResponse = new ArrayList();
        for (EntityDataRecord thisRecord : theResponse) {
            generatedResponse.add(thisRecord.getEntityElement() + " = " + thisRecord.getEntityElementValue());
        }
        responseMsg = new MessageSpecification(generatedResponse);

        // If the request message is part of a subscription message we respond with the C2C Receipt instead.
        // We needed to complete the above process of generating a full response, just to ensure that some error would not have occured.
        if (!isPublication && requestMessage.containsMessageOfType("c2c:c2cMessageSubscription")) {
            ArrayList<String> c2cResponse = new ArrayList();
            c2cResponse.add("c2c:c2cMessageReceipt.informationalText = OK (Emulation Mode)");
            responseMsg = new MessageSpecification(c2cResponse);
        }

        return responseMsg;
    }

    /**
     * This method is responsible for processing the lCSControlRequestMsg
     * Message and generating an appropriate response message.
     *
     * @param requestMessage the request message received
     * @param controlStatusCollector
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processLCSControlRequestMsg(MessageSpecification requestMessage, EntityCommandProcessor controlStatusCollector) throws NoMatchingDataException, EntityEmulationException {

        MessageSpecification responseMsg;

        // Confirm that the referenced LCS supports the requested command and that the parameters are valid for the selected LCS
        controlStatusCollector.verifyCommand(requestMessage);

        //  Create/Update a Control Request Status Record for this request    
        controlStatusCollector.queueCommand(requestMessage);

        try {
            // Perform the next CCTV Command that is ready to be processed right now.
            MessageSpecification nextCommand = EntityCommandQueue.getInstance().retrieveNextCommand(TMDDEntityType.EntityType.LCS);
            // Confirm that the referenced CCTV supports the requested command
            controlStatusCollector.verifyCommand(nextCommand);
            // Execute the command
            controlStatusCollector.executeCommand(nextCommand);
        } catch (InvalidEntityControlRequestStatusException ex) {
            ex.printStackTrace();
        } catch (InvalidCommandException ex) {
            ex.printStackTrace();
        } catch (EntityEmulationException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        responseMsg = controlStatusCollector.getControlResponseMessage(requestMessage);
        return responseMsg;
    }

    /**
     * This method is responsible for processing the rampMeterControlRequestMsg
     * Message and generating an appropriate response message.
     *
     * @param requestMessage the request message received
     * @param controlStatusCollector
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processRampMeterControlRequestMsg(MessageSpecification requestMessage, EntityCommandProcessor controlStatusCollector) throws NoMatchingDataException, EntityEmulationException {

        MessageSpecification responseMsg;

        // Confirm that the referenced RAMP METER supports the requested command and that the parameters are valid for the selected RAMP METER
        controlStatusCollector.verifyCommand(requestMessage);

        //  Create/Update a Control Request Status Record for this request    
        controlStatusCollector.queueCommand(requestMessage);

        try {
            // Perform the next CCTV Command that is ready to be processed right now.
            MessageSpecification nextCommand = EntityCommandQueue.getInstance().retrieveNextCommand(TMDDEntityType.EntityType.RAMPMETER);
            // Confirm that the referenced CCTV supports the requested command
            controlStatusCollector.verifyCommand(nextCommand);
            // Execute the command
            controlStatusCollector.executeCommand(nextCommand);
        } catch (InvalidEntityControlRequestStatusException ex) {
            ex.printStackTrace();
        } catch (InvalidCommandException ex) {
            ex.printStackTrace();
        } catch (EntityEmulationException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        responseMsg = controlStatusCollector.getControlResponseMessage(requestMessage);
        return responseMsg;
    }

    /**
     * This method is responsible for processing the sectionControlRequestMsg
     * Message and generating an appropriate response message.
     *
     * @param requestMessage the request message received
     * @param controlStatusCollector
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processSectionControlRequestMsg(MessageSpecification requestMessage, EntityCommandProcessor controlStatusCollector) throws NoMatchingDataException, EntityEmulationException {

        MessageSpecification responseMsg;

        // Confirm that the referenced SECTION supports the requested command and that the parameters are valid for the selected SECTION
        controlStatusCollector.verifyCommand(requestMessage);

        //  Create/Update a Control Request Status Record for this request    
        controlStatusCollector.queueCommand(requestMessage);

        try {
            // Perform the next CCTV Command that is ready to be processed right now.
            MessageSpecification nextCommand = EntityCommandQueue.getInstance().retrieveNextCommand(TMDDEntityType.EntityType.SECTION);
            // Confirm that the referenced CCTV supports the requested command
            controlStatusCollector.verifyCommand(nextCommand);
            // Execute the command
            controlStatusCollector.executeCommand(nextCommand);
        } catch (InvalidEntityControlRequestStatusException ex) {
            ex.printStackTrace();
        } catch (InvalidCommandException ex) {
            ex.printStackTrace();
        } catch (EntityEmulationException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        responseMsg = controlStatusCollector.getControlResponseMessage(requestMessage);
        return responseMsg;

    }

    /**
     * This method is responsible for processing the
     * sectionControlStatusRequestMsg Message and generating an appropriate
     * response message.
     *
     * @param requestMessage the request message received
     * @param entityDataType the type of entity data that this message is
     * related to
     * @param dataCollector the object that is capable of collecting the
     * required information
     * @param isPublication flag indicating whether the request is related to a
     * publication dialog.
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processSectionControlStatusRequestMsg(MessageSpecification requestMessage, EntityCommandProcessor controlStatusCollector) throws NoMatchingDataException, EntityEmulationException {

        MessageSpecification responseMsg;

        responseMsg = controlStatusCollector.getControlResponseMessage(requestMessage);
        return responseMsg;
    }

    /**
     * This method is responsible for processing the
     * sectionSignalTimingPatternInventoryRequestMsg Message and generating an
     * appropriate response message.
     *
     * @param requestMessage the request message received
     * @param entityDataType the type of entity data that this message is
     * related to
     * @param dataCollector the object that is capable of collecting the
     * required information
     * @param isPublication flag indicating whether the request is related to a
     * publication dialog.
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processSectionSignalTimingPatternInventoryRequestMsg(MessageSpecification requestMessage, EntityEmulationData.EntityDataType entityDataType, EntityDataInformationCollector dataCollector, boolean isPublication) throws NoMatchingDataException, EntityEmulationException {

        ArrayList<DataFilter> theFilters = new ArrayList();
        MessageSpecification responseMsg = null;

        // device-id filter
        ArrayList<String> deviceIdFilterValues = requestMessage.getValuesOfInstances("tmdd:sectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.device-id-list.device-id[*]");
        if (deviceIdFilterValues.size() > 0) {
            ValueInSetFilter deviceIdFilter = new ValueInSetFilter(entityDataType, "EntityId", deviceIdFilterValues);
            theFilters.add(deviceIdFilter);
        }

        // network-id filter
        ArrayList<String> networkIdFilterValues = requestMessage.getValuesOfInstances("tmdd:sectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.network-id-list.network-id[*]");
        if (networkIdFilterValues.size() > 0) {
            ValueInSetFilter networkIdFilter = new ValueInSetFilter(entityDataType, "NetworkId", networkIdFilterValues);
            theFilters.add(networkIdFilter);
        }

        // link filter
        ArrayList<String> linkFilterValues = requestMessage.getValuesOfInstances("tmdd:sectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.link-id-list.link[*]");
        if (linkFilterValues.size() > 0) {
            ValueInSetFilter linkFilter = new ValueInSetFilter(entityDataType, "LinkId", linkFilterValues);
            theFilters.add(linkFilter);
        }

        // link-designator filter
        ArrayList<String> linkDesignatorFilterValues = requestMessage.getValuesOfInstances("tmdd:sectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.link-designator-list.link-designator[*]");
        if (linkDesignatorFilterValues.size() > 0) {
            ValueInSetFilter linkDesignatorFilter = new ValueInSetFilter(entityDataType, "LinkDesignator", linkDesignatorFilterValues);
            theFilters.add(linkDesignatorFilter);
        }

        // linear reference filter
        String linearReferenceStart = requestMessage.getElementValue("tmdd:sectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.linear-reference.linear-reference-start");
        String linearReferenceEnd = requestMessage.getElementValue("tmdd:sectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.linear-reference.linear-reference-end");
        if ((linearReferenceStart != null) && (linearReferenceEnd != null) && (!linearReferenceStart.isEmpty()) && (!linearReferenceEnd.isEmpty())) {
            ValueInRangeFilter linearReferenceFilter = new ValueInRangeFilter(entityDataType, "LinearReference", linearReferenceStart, linearReferenceEnd);
            theFilters.add(linearReferenceFilter);
        }

        // section-id filter
        ArrayList<String> sectionIdFilterValues = requestMessage.getValuesOfInstances("tmdd:sectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.section-id-list.section-id[*]");
        if (sectionIdFilterValues.size() > 0) {
            ValueInSetFilter sectionIdFilter = new ValueInSetFilter(entityDataType, "SectionId", sectionIdFilterValues);
            theFilters.add(sectionIdFilter);
        }

        // pattern-id filter
        ArrayList<String> patternIdFilterValues = requestMessage.getValuesOfInstances("tmdd:sectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.pattern-id-list.pattern-id[*]");
        if (patternIdFilterValues.size() > 0) {
            ValueInSetFilter patternIdFilter = new ValueInSetFilter(entityDataType, "PatternId", patternIdFilterValues);
            theFilters.add(patternIdFilter);
        }

        // center-id filter        
        ArrayList<String> centerIdFilterValues = requestMessage.getValuesOfInstances("tmdd:sectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.center-id-list.center-id[*]");
        if (centerIdFilterValues.size() > 0) {
            ValueInSetFilter centerIdFilter = new ValueInSetFilter(entityDataType, "CenterId", centerIdFilterValues);
            theFilters.add(centerIdFilter);
        }

        // section-timing-pattern-id filter        
        ArrayList<String> sectionTimingPatternIdFilterValues = requestMessage.getValuesOfInstances("tmdd:sectionSignalTimingPatternInventoryRequestMsg.section-timing-pattern-id");
        if (sectionTimingPatternIdFilterValues.size() > 0) {
            ValueInSetFilter sectionTimingPatternIdFilter = new ValueInSetFilter(entityDataType, "TimingPatternId", sectionTimingPatternIdFilterValues);
            theFilters.add(sectionTimingPatternIdFilter);
        }

        // tmddv3.1 time-range filter    
        if (tmddv31AndLater){
            String timeRangeStartDate = requestMessage.getElementValue("tmdd:sectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.date");
            String timeRangeStartTime = requestMessage.getElementValue("tmdd:sectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.time");
            String timeRangeOffset = requestMessage.getElementValue("tmdd:sectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.start-time.offset");
            String timeRangePeriodInSeconds = requestMessage.getElementValue("tmdd:sectionSignalTimingPatternInventoryRequestMsg.device-information-request-header.device-filter.tmddX:deviceInformationRequestFilterExt.time-range.period-seconds");
            if ((timeRangeStartDate != null) && (timeRangeStartTime != null) && (!timeRangeStartDate.isEmpty()) && (!timeRangeStartTime.isEmpty())) {
                ValueInDateRangeFilter timeRangeFilter = new ValueInDateRangeFilter("TimeRangeDate", "TimeRangeTime", "TimeRangeOffset", timeRangeStartDate, timeRangeStartTime, timeRangeOffset, timeRangePeriodInSeconds);
                theFilters.add(timeRangeFilter);
            }              
        }
        
        ArrayList<EntityDataRecord> theResponse = dataCollector.getEntityElements(theFilters);

        ArrayList<String> generatedResponse = new ArrayList();
        for (EntityDataRecord thisRecord : theResponse) {
            generatedResponse.add(thisRecord.getEntityElement() + " = " + thisRecord.getEntityElementValue());
        }
        responseMsg = new MessageSpecification(generatedResponse);

        // If the request message is part of a subscription message we respond with the C2C Receipt instead.
        // We needed to complete the above process of generating a full response, just to ensure that some error would not have occured.
        if (!isPublication && requestMessage.containsMessageOfType("c2c:c2cMessageSubscription")) {
            ArrayList<String> c2cResponse = new ArrayList();
            c2cResponse.add("c2c:c2cMessageReceipt.informationalText = OK (Emulation Mode)");
            responseMsg = new MessageSpecification(c2cResponse);
        }

        return responseMsg;
    }

    
    public static MessageSpecification processTrafficNetworkInformationRequestMsg(MessageSpecification requestMessage, EntityEmulationData.EntityDataType entityDataType, EntityDataInformationCollector dataCollector, boolean isPublication) throws NoMatchingDataException, EntityEmulationException {

        ArrayList<DataFilter> theFilters = new ArrayList();
        MessageSpecification responseMsg = null;

        // network-id filter
        ArrayList<String> networkIdFilterValues = requestMessage.getValuesOfInstances("tmdd:trafficNetworkInformationRequestMsg.network-identifiers.network-identifier[*]");
        if (networkIdFilterValues.size() > 0) {
            ValueInSetFilter deviceIdFilter = new ValueInSetFilter(entityDataType, "NetworkId", networkIdFilterValues);
            theFilters.add(deviceIdFilter);
        }

        // roadway-network-id filter (EntityId)
        ArrayList<String> roadwayNetworkIdFilterValues = requestMessage.getValuesOfInstances("tmdd:trafficNetworkInformationRequestMsg.roadway-network-id-list.roadway-network-id[*]");
        if (roadwayNetworkIdFilterValues.size() > 0) {
            ValueInSetFilter networkIdFilter = new ValueInSetFilter(entityDataType, "EntityId", roadwayNetworkIdFilterValues);
            theFilters.add(networkIdFilter);
        }
        
        ArrayList<EntityDataRecord> theResponse = dataCollector.getEntityElements(theFilters);

        ArrayList<String> generatedResponse = new ArrayList();
        for (EntityDataRecord thisRecord : theResponse) {
            generatedResponse.add(thisRecord.getEntityElement() + " = " + thisRecord.getEntityElementValue());
        }
        responseMsg = new MessageSpecification(generatedResponse);

        // If the request message is part of a subscription message we respond with the C2C Receipt instead.
        // We needed to complete the above process of generating a full response, just to ensure that some error would not have occured.
        if (!isPublication && requestMessage.containsMessageOfType("c2c:c2cMessageSubscription")) {
            ArrayList<String> c2cResponse = new ArrayList();
            c2cResponse.add("c2c:c2cMessageReceipt.informationalText = OK (Emulation Mode)");
            responseMsg = new MessageSpecification(c2cResponse);
        }

        return responseMsg;
    }
    
    /**
     * This method is responsible for processing the
     * videoSwitchControlRequestMsg Message and generating an appropriate
     * response message.
     *
     * @param requestMessage the request message received
     * @param controlStatusCollector
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException
     */
    public static MessageSpecification processVideoSwitchControlRequestMsg(MessageSpecification requestMessage, EntityCommandProcessor controlStatusCollector) throws NoMatchingDataException, EntityEmulationException {


        MessageSpecification responseMsg;

        // Confirm that the referenced VIDEO SWITCH supports the requested command and that the parameters are valid for the selected VIDEO SWITCH
        controlStatusCollector.verifyCommand(requestMessage);

        //  Create/Update a Control Request Status Record for this request    
        controlStatusCollector.queueCommand(requestMessage);

        try {
            // Perform the next CCTV Command that is ready to be processed right now.
            MessageSpecification nextCommand = EntityCommandQueue.getInstance().retrieveNextCommand(TMDDEntityType.EntityType.VIDEOSWITCH);
            // Confirm that the referenced CCTV supports the requested command
            controlStatusCollector.verifyCommand(nextCommand);
            // Execute the command
            controlStatusCollector.executeCommand(nextCommand);
        } catch (InvalidEntityControlRequestStatusException ex) {
            ex.printStackTrace();
        } catch (InvalidCommandException ex) {
            ex.printStackTrace();
        } catch (EntityEmulationException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        responseMsg = controlStatusCollector.getControlResponseMessage(requestMessage);
        return responseMsg;

    }

}
