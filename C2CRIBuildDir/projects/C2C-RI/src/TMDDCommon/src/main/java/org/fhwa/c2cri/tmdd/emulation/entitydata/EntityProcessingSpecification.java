/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.entitydata;

/**
 * This class defines the entity information necessary to add, delete and update
 * individual and related entity information.
 *
 * @author TransCore ITS, LLC
 */
public class EntityProcessingSpecification {

    /**
     * The data type for the entity to be processed.
     */
    final private EntityEmulationData.EntityDataType dataType;
    /**
     * The unique identifier for the entity to be processed for the given entity
     * data type.
     */
    final private String entityId;
    /**
     * The element name that should be included in the processing of the entity.
     * The values are included in the EntityEmulationElementsLookupTable in the
     * TMDD database file and examples include EventId, NetowkId, LinkId, etc.
     */
    final private String relatedElementName;
    /**
     * The value that should be associated with the related Element Name.
     */
    final private String relatedElementValue;

    /**
     * Creates an instance of an EntityProcessingSpecification object that
     * includes only the dataType and entityId.
     *
     * @param dataType - The EntityDataType that is being referenced.
     * @param entityId - The unique identifier for the entity.
     */
    public EntityProcessingSpecification(EntityEmulationData.EntityDataType dataType, String entityId) {
        this.dataType = dataType;
        this.entityId = entityId;
        this.relatedElementName = "";
        this.relatedElementValue = "";
    }

    /**
     * Creates an instance of an EntityProcessingSpecification object that
     * includes the dataType and entityId as well as related entity information.
     *
     * @param dataType - The EntityDataType that is being referenced.
     * @param entityId - The unique identifier for the entity.
     * @param relatedElementName - The element name within the entity data that
     * should be included in the processing of the entity.
     * @param relatedElementValue - The value that should be used for the
     * relatedElementName.
     */
    public EntityProcessingSpecification(EntityEmulationData.EntityDataType dataType, String entityId, String relatedElementName, String relatedElementValue) {
        this.dataType = dataType;
        this.entityId = entityId;
        this.relatedElementName = relatedElementName;
        this.relatedElementValue = relatedElementValue;
    }

    /**
     * @return true when the specification includes related element information
     * values.
     */
    public boolean hasRelatedElementData() {
        if (relatedElementName.isEmpty() || relatedElementValue.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @return {@link EntityProcessingSpecification#dataType}
     */
    public EntityEmulationData.EntityDataType getDataType() {
        return dataType;
    }

    /**
     * @return {@link EntityProcessingSpecification#entityId}
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * @return {@link EntityProcessingSpecification#relatedElementName}
     */
    public String getRelatedElementName() {
        return relatedElementName;
    }

    /**
     * @return {@link EntityProcessingSpecification#relatedElementValue}
     */
    public String getRelatedElementValue() {
        return relatedElementValue;
    }

}
