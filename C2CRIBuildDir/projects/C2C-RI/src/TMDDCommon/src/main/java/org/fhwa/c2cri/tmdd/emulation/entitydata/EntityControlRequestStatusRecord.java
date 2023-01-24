/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.tmdd.emulation.entitydata;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author TransCore ITS, LLC
 * Created: Feb 14, 2016
 */
public class EntityControlRequestStatusRecord {

        private int identifier;
        private String orgId;
        private String entityId;
        private String requestId;
        private String operatorId;
        private String status;
        private String entityType;
        private int lockStatus;
        private int priority;
        private String operatorLockId;
        private String lastRevisedDate;
        private String lastRevisedTime;
        private String lastRevisedOffset;
        
        
        private EntityControlRequestStatusRecord(){};
        
        public EntityControlRequestStatusRecord(String orgId, String entityId, String requestId, String entityType){
            this.orgId = orgId;
            this.entityId = entityId;
            this.requestId = requestId;
            this.entityType = entityType;
            this.status = "request queued/not implemented";
            this.lockStatus = 0;
            this.operatorLockId="";
            this.priority = 0;
        }

        public void updateDate(){
            Date curDate = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            this.lastRevisedDate = format.format(curDate);
            
            format = new SimpleDateFormat("hh:mm:ss");
            this.lastRevisedTime = format.format(curDate);

            format = new SimpleDateFormat("XXX");
            this.lastRevisedOffset = format.format(curDate).replace("+", "").replace("-", "");
            
        }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLockStatus(int lockStatus) {
        this.lockStatus = lockStatus;
    }

    public void setOperatorLockId(String operatorLockId) {
        this.operatorLockId = operatorLockId;
    }

    public void setLastRevisedDate(String lastRevisedDate) {
        this.lastRevisedDate = lastRevisedDate;
    }

    public void setLastRevisedTime(String lastRevisedTime) {
        this.lastRevisedTime = lastRevisedTime;
    }

    public void setLastRevisedOffset(String lastRevisedOffset) {
        this.lastRevisedOffset = lastRevisedOffset;
    }

    
    
    public int getIdentifier() {
        return identifier;
    }

    public String getOrgId() {
        return orgId;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public String getStatus() {
        return status;
    }

    public String getEntityType() {
        return entityType;
    }

    public int getLockStatus() {
        return lockStatus;
    }

    public String getOperatorLockId() {
        return operatorLockId;
    }

    public String getLastRevisedDate() {
        if (lastRevisedDate == null) updateDate();
        return lastRevisedDate;
    }

    public String getLastRevisedTime() {
        if (lastRevisedTime == null) updateDate();
        return lastRevisedTime;
    }

    public String getLastRevisedOffset() {
        if (lastRevisedOffset == null)updateDate();
        return lastRevisedOffset;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
         
        
}
