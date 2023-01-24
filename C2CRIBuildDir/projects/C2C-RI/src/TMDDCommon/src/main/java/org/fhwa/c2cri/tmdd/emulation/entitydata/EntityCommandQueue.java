/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.entitydata;

import java.util.ArrayList;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.tmdd.emulation.TMDDEntityType;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityControlRequestStatusException;

/**
 *
 * @author TransCore ITS, LLC Created: Feb 19, 2016
 */
public class EntityCommandQueue {

    private int queueLength = 0;
    private EntityPriorityQueueInformation queuedCommands;
    private static EntityCommandQueue commandQueue;

    private EntityCommandQueue() {
    }

    ;
    
    public static EntityCommandQueue getInstance() {
        if (commandQueue == null) {
            commandQueue = new EntityCommandQueue();
        }
        return commandQueue;
    }

    public void initialize(int queueLength) {
        this.queueLength = queueLength;
    }

    public MessageSpecification retrieveNextCommand(TMDDEntityType.EntityType entityType) throws InvalidEntityControlRequestStatusException, EntityEmulationException {
        EntityControlRequestStatusRecord controlStatusRecord = EntityControlRequestStatus.getQueuedControlRequestStatus(entityType, queueLength);
        if (controlStatusRecord == null)return null;
        return EntityControlRequestLog.retrieveControlRequest(controlStatusRecord.getIdentifier());
    }

    public EntityPriorityQueueInformation retrieveQueuedCommands(TMDDEntityType.EntityType entityType, ArrayList<String> deviceList) throws InvalidEntityControlRequestStatusException, EntityEmulationException {       
        queuedCommands = new EntityPriorityQueueInformation();
        for (String deviceID : deviceList) {
            ArrayList<MessageSpecification> messageList = new ArrayList<MessageSpecification>();
            for (EntityControlRequestStatusRecord thisStatusRecord : EntityControlRequestStatus.getQueuedControlRequests(entityType, deviceID)) {
                System.out.println("EntityCommandQueue::retrieveQueuedCommands: processing device id "+deviceID+" for status record identifier "+thisStatusRecord.getIdentifier());
                if (!queuedCommands.containsQueueHeader(deviceID)) {
                    queuedCommands.setQueueHeadRequest(deviceID, EntityControlRequestLog.retrieveControlRequest(thisStatusRecord.getIdentifier()));
                    System.out.println("EntityCommandQueue::retrieveQueuedCommands: Check for null queue?  "+(queuedCommands.getQueueHeadRequest(deviceID)==null?"true":"false"));
                    System.out.println("EntityCommandQueue::retrieveQueuedCommands: Double-Check for null queue?  "+(queuedCommands.getQueueHeadRequest(deviceID)==null?"true":"false"));
               } else {
                    messageList.add(EntityControlRequestLog.retrieveControlRequest(thisStatusRecord.getIdentifier()));
                    System.out.println("EntityCommandQueue::retrieveQueuedCommands: Check for null queue after message list update?  "+(queuedCommands.getQueueHeadRequest(deviceID)==null?"true":"false"));
                }
                System.out.println("EntityCommandQueue::retrieveQueuedCommands: Triple-Check for null queue?  "+(queuedCommands.getQueueHeadRequest(deviceID)==null?"true":"false"));
            }
            System.out.println("EntityCommandQueue::retrieveQueuedCommands: Check for null queue before other Queue update?  "+(queuedCommands.getQueueHeadRequest(deviceID)==null?"true":"false"));
            queuedCommands.setOtherQueueMessages(deviceID, messageList);
            System.out.println("EntityCommandQueue::retrieveQueuedCommands: Check for null queue after other Queue update?  "+(queuedCommands.getQueueHeadRequest(deviceID)==null?"true":"false"));
        }

        System.out.println("EntityCommandQueue::retrieveQueuedCommands: returning null queue?  "+(queuedCommands.getQueueHeadRequest("3")==null?"true":"false"));
        return queuedCommands;
    }

    public EntityControlRequestStatusRecord getControlRequestStatus(String orgId, String entityId, String requestId) throws EntityEmulationException {
        return EntityControlRequestStatus.getControlRequestStatus(orgId, entityId, requestId);
    }

    public void updateControlRequestStatus(EntityControlRequestStatusRecord statusRecord, MessageSpecification requestMsg, boolean logNewRequest) throws EntityEmulationException {
        EntityControlRequestStatus.updateControlRequestStatus(statusRecord, requestMsg, logNewRequest);
    }

    public TMDDEntityType.EntityType getControlRequestEntityType(String orgId, String entityId, String requestId) throws EntityEmulationException {
        return EntityControlRequestStatus.getControlRequestEntityType(orgId, entityId, requestId);
    }

}
