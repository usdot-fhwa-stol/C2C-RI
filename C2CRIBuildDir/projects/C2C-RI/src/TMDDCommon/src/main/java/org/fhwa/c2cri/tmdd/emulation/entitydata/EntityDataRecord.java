/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.tmdd.emulation.entitydata;

/**
 * 
 * @author TransCore ITS, LLC
 * Created: Feb 4, 2016
 */
public class EntityDataRecord {
    private int entityIndex;
    private String entityElement;
    private int schemaDetailId;
    private String entityElementValue;

    public int getEntityIndex() {
        return entityIndex;
    }

    public void setEntityIndex(int entityIndex) {
        this.entityIndex = entityIndex;
    }

    public String getEntityElement() {
        return entityElement;
    }

    public void setEntityElement(String entityElement) {
        this.entityElement = entityElement;
    }

    public int getSchemaDetailId() {
        return schemaDetailId;
    }

    public void setSchemaDetailId(int schemaDetailId) {
        this.schemaDetailId = schemaDetailId;
    }

    public String getEntityElementValue() {
        return entityElementValue;
    }

    public void setEntityElementValue(String entityElementValue) {
        this.entityElementValue = entityElementValue;
    }
    
    
    
}
