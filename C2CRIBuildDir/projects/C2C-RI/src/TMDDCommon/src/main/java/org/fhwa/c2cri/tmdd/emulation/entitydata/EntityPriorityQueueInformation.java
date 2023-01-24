/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.entitydata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.fhwa.c2cri.infolayer.MessageSpecification;

/**
 *
 * @author TransCore ITS, LLC Created: Apr 8, 2016
 */
public class EntityPriorityQueueInformation {
    Map<String, DeviceQueue> entityPriorityQueue = new HashMap<String, DeviceQueue>();


        public ArrayList<String> getDeviceIDList(){
            return new ArrayList<String>(entityPriorityQueue.keySet());
        }
    
        public boolean containsQueueHeader(String deviceId){
            boolean results = false;
            
            if (entityPriorityQueue.containsKey(deviceId)){
                System.out.println("EntityPriorityQueueInformation::containsQueueHeadRequest:  The entity priority queue contains device id "+deviceId);
                DeviceQueue thisQueue = entityPriorityQueue.get(deviceId);
                if ((thisQueue !=null)&&(thisQueue.getQueueHeadRequest()!=null)){
                    System.out.println("EntityPriorityQueueInformation::containsQueueHeadRequest:  The entity priority queue contains a non-null DeviceQueue");
                    results = true;
                }
            }            
            return results;
        };

        public boolean containsOtherQueueMessages(String deviceId){
            boolean results = false;
            
            if (entityPriorityQueue.containsKey(deviceId)){
                if ((entityPriorityQueue.get(deviceId).getOtherQueueMessages()!=null)&& !entityPriorityQueue.get(deviceId).getOtherQueueMessages().isEmpty()){
                    results = true;
                }
            }            
            return results;
        };


        /**
         * Returns the request that is at the top of the queue for this entity
         * type.
         *
         * @return
         */
        public MessageSpecification getQueueHeadRequest(String deviceID) {
            MessageSpecification results = null;
            
            if (containsQueueHeader(deviceID)){
                DeviceQueue thisDeviceQueue = entityPriorityQueue.get(deviceID);
                results = thisDeviceQueue.getQueueHeadRequest();
                System.out.println("EntityPriorityQueueInformation::getQueueHeadRequest:  The entity priority queue contains a non-null DeviceQueue "+(results!=null?"true":"false"));
            }
            return results;
        }

        /**
         * Sets the request that is at the top of the queue for this entity
         * type.
         *
         * @param queueHeadRequest
         */
        public void setQueueHeadRequest(String deviceID, MessageSpecification queueHeadRequest) {
            if (containsQueueHeader(deviceID)){
                entityPriorityQueue.get(deviceID).setQueueHeadRequest(queueHeadRequest);
                System.out.println("EntityPriorityQueueInformation::setQueueHeadRequest:  Modified existing entry on queue with the following count of elements." + queueHeadRequest.getMessageSpec().size());
            } else {
                DeviceQueue newDeviceQueue = new DeviceQueue();
                newDeviceQueue.setQueueHeadRequest(queueHeadRequest);
                entityPriorityQueue.put(deviceID, newDeviceQueue);
                System.out.println("EntityPriorityQueueInformation::setQueueHeadRequest:  Added new entry to queue with the following count of elements." + newDeviceQueue.getQueueHeadRequest().getMessageSpec().size());
            }
                System.out.println("EntityPriorityQueueInformation::setQueueHeadRequest:  The entity priority queue contains the following count of elements " + entityPriorityQueue.size()+ " for device id "+deviceID + " and MessageSpec Present = "+(entityPriorityQueue.get(deviceID).getQueueHeadRequest()!=null?"true":"false"));
            
        }

        /**
         * retrieves any requests that are not at the top of the queue for this
         * entity type
         *
         * @return
         */
        public ArrayList<MessageSpecification> getOtherQueueMessages(String deviceID) {
            ArrayList<MessageSpecification> results = null;
            
            if (containsOtherQueueMessages(deviceID)){
                results = entityPriorityQueue.get(deviceID).getOtherQueueMessages();
            } else if (entityPriorityQueue.get(deviceID)!=null){
                results = entityPriorityQueue.get(deviceID).getOtherQueueMessages();
            }
            return results;
        }

        /**
         * Sets the requests that are not at the top of the queue for this
         * entity type
         *
         * @param otherQueueMessages
         */
        public void setOtherQueueMessages(String deviceID, ArrayList<MessageSpecification> otherQueueMessages) {
            if (containsOtherQueueMessages(deviceID)){
                for (MessageSpecification thisSpec : otherQueueMessages){
                   entityPriorityQueue.get(deviceID).getOtherQueueMessages().add(thisSpec);
                }
            } else {
                entityPriorityQueue.get(deviceID).setOtherQueueMessages(otherQueueMessages);
            }
        }
        
        
    
    
    private class DeviceQueue {
        // The control request that is at the top of the queue for this entity

        private MessageSpecification queueHeadRequest;

        // Other control requests that are pending for this enitity.
        private ArrayList<MessageSpecification> otherQueueMessages;

        /**
         * Returns the request that is at the top of the queue for this entity
         * type.
         *
         * @return
         */
        public MessageSpecification getQueueHeadRequest() {
            return this.queueHeadRequest;
        }

        /**
         * Sets the request that is at the top of the queue for this entity
         * type.
         *
         * @param queueHeadRequest
         */
        public void setQueueHeadRequest(MessageSpecification queueHeadRequestEntry) {
            this.queueHeadRequest = new MessageSpecification(queueHeadRequestEntry.getMessageSpec());
            System.out.println("EntityPriorityQueueInformation::setqueueHeader queueHeadRequest was set and not null "+(this.queueHeadRequest!=null?"true":"false"));
        }

        /**
         * retrieves any requests that are not at the top of the queue for this
         * entity type
         *
         * @return
         */
        public ArrayList<MessageSpecification> getOtherQueueMessages() {
            return this.otherQueueMessages;
        }

        /**
         * Sets the requests that are not at the top of the queue for this
         * entity type
         *
         * @param otherQueueMessages
         */
        public void setOtherQueueMessages(ArrayList<MessageSpecification> otherQueueMessagesEntry) {
            ArrayList<MessageSpecification> otherMessages = new ArrayList();
            for (MessageSpecification thisSpec : otherQueueMessagesEntry){
                otherMessages.add(new MessageSpecification(thisSpec.getMessageSpec()));
            }
            this.otherQueueMessages = otherMessages;
            System.out.println("EntityPriorityQueueInformation::setOtherQueueHeader otherQueueHeadRequest was set and not null "+(this.otherQueueMessages!=null?"true":"false"));
            System.out.println("EntityPriorityQueueInformation::setOtherQueueHeader QueueHeadRequest was set and not null "+(this.queueHeadRequest!=null?"true":"false"));
        }

    }

}
