/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.tmdd.emulation;

import java.util.ArrayList;
import java.util.List;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataInformationCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityProcessingSpecification;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.DataFilter;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.ValueInSetFilter;
import org.fhwa.c2cri.tmdd.emulation.exceptions.DuplicateEntityIdException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityElementException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityIdValueException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidValueException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.NoMatchingDataException;

/**
 * 
 * @author TransCore ITS, LLC
 * Created: Mar 27, 2016
 */
public class TMDDDefaultEntityDataType implements EntityDataInformationCollector{
    private EntityEmulationData.EntityDataType entityDataType = EntityEmulationData.EntityDataType.DMSFONTTABLE;
    
    public TMDDDefaultEntityDataType(EntityEmulationData.EntityDataType entityDataType){
        this.entityDataType = entityDataType;
    };
    
    public void initialize(byte[] byteArray) throws EntityEmulationException {
        EntityEmulationData.initialize(entityDataType, byteArray);

    }

    /**
     * Add a new entity to the set using the provided entityId. The elements
     * which make up the entity are pulled from the default entity definition.
     * The entityID will be used for the device-id value.
     *
     * Each DMS-Inventory-Item is assigned an item index. This index is used to
     * indicate the relative order of the DMS Inventory items.
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
    public void addEntity(String entityId) throws DuplicateEntityIdException, InvalidEntityIdValueException {
        // check the Entity Id Value against the current entity information and requirements for the entity ID field
        try {
            EntityEmulationData.addEntity(entityDataType, entityId);
        } catch (InvalidValueException ex) {
            throw new InvalidEntityIdValueException(ex);
        } catch (DuplicateEntityIdException ex) {
            throw ex;
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }
    }
    
    public List<EntityDataType> addEntity(String entityId, EntityDataType[] relatedTypes) throws DuplicateEntityIdException, InvalidEntityIdValueException {
        // check the Entity Id Value against the current entity information and requirements for the entity ID field
        try {
            return EntityEmulationData.addEntity(entityDataType, relatedTypes, entityId);
        } catch (InvalidValueException ex) {
            throw new InvalidEntityIdValueException(ex);
        } catch (DuplicateEntityIdException ex) {
            throw ex;
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }
    }    
    
    
    public List<EntityDataType> addEntity(EntityProcessingSpecification[] relatedEntityDataTypeSpecifications, String entityId) throws DuplicateEntityIdException, InvalidEntityIdValueException {
        // check the Entity Id Value against the current entity information and requirements for the entity ID field
        try {
            return EntityEmulationData.addEntity(new EntityProcessingSpecification(entityDataType, entityId), relatedEntityDataTypeSpecifications);
        } catch (InvalidValueException ex) {
            throw new InvalidEntityIdValueException(ex);
        } catch (DuplicateEntityIdException ex) {
            throw ex;
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }
    }        
    public void addEntityElement(String entityId, String elementName, String elementValue) throws InvalidEntityIdValueException, InvalidValueException {
        try {
            EntityEmulationData.addEntityElement(entityDataType, entityId, elementName, elementValue);
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }

    public void deleteEntity(String entityId) throws InvalidEntityIdValueException {
        try {
            EntityEmulationData.deleteEntity(entityDataType, entityId);
        } catch (InvalidValueException ex) {
            throw new InvalidEntityIdValueException(ex);
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }

    public List<EntityDataType> deleteEntity(String entityId, EntityDataType[] relatedTypes) throws InvalidEntityIdValueException {
        try {
            return EntityEmulationData.deleteEntity(entityDataType, relatedTypes, entityId);
        } catch (InvalidValueException ex) {
            throw new InvalidEntityIdValueException(ex);
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }

    public List<EntityDataType> deleteEntity(String entityId, EntityProcessingSpecification[] relatedTypes) throws InvalidEntityIdValueException {
        try {
            return EntityEmulationData.deleteEntity(new EntityProcessingSpecification(entityDataType, entityId), relatedTypes);
        } catch (InvalidValueException ex) {
            throw new InvalidEntityIdValueException(ex);
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }
    
    
    public void updateEntityElement(String entityId, String entityElementName, String entityElementValue) throws InvalidEntityIdValueException, InvalidValueException {
        try {
            EntityEmulationData.updateEntityElement(entityDataType, entityId, entityElementName, entityElementValue);
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }

    public List<EntityDataType> updateEntityElement(EntityDataType[] relatedTypes, String entityId, String entityElementName, String entityElementValue) throws InvalidEntityIdValueException, InvalidValueException {
        try {
            return EntityEmulationData.updateEntityElement(entityDataType, relatedTypes, entityId, entityElementName, entityElementValue);
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }
    
    public List<EntityDataType> updateEntityElement(EntityProcessingSpecification[] relatedTypes, String entityId, String entityElementName, String entityElementValue) throws InvalidEntityIdValueException, InvalidValueException {
        try {
            return EntityEmulationData.updateEntityElement(new EntityProcessingSpecification(entityDataType, entityId), relatedTypes, entityElementName, entityElementValue);
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }
        
    public void deleteEntityElement(String entityId, String entityElementName) throws InvalidEntityIdValueException, InvalidEntityElementException {
        try {
            EntityEmulationData.deleteEntityElement(entityDataType, entityId, entityElementName);
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

    }

    public String getEntityElementValue(String entityId, String entityElement) throws InvalidEntityIdValueException, InvalidEntityElementException, EntityEmulationException {
        String entityValue = "";
        try {
            entityValue = EntityEmulationData.getEntityElementValue(entityDataType, entityId, entityElement);
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

        return entityValue;
    }

    public boolean isElementTypeMatch(EntityDataType entityDataType, String elementName, String entityId, String elementType) throws EntityEmulationException{
        try{
            return EntityEmulationData.isElementTypeMatch(entityDataType, elementName, entityId, elementType);            
        } catch (Exception ex){
            return false;
        }
    }
    
    @Override
    public ArrayList<EntityDataRecord> getEntityElements(ArrayList<DataFilter> filters) throws NoMatchingDataException, EntityEmulationException {
        ArrayList<EntityDataRecord> resultList = new ArrayList();
        try {
            resultList = EntityEmulationData.getEntityElements(entityDataType, filters);
        } catch (NoMatchingDataException ex) {
            throw ex;
        } catch (EntityEmulationException ex) {
            throw new InvalidEntityIdValueException(ex);
        }

        return resultList;
    }
   
}
