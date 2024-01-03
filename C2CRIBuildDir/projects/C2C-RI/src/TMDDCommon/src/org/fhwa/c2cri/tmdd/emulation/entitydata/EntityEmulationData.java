/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.entitydata;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.tmdd.TMDDSettingsImpl;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.DataFilter;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.MultipleElementDataFilter;
import org.fhwa.c2cri.tmdd.emulation.exceptions.DuplicateEntityIdException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityElementException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityIdValueException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidValueException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.NoMatchingDataException;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 *
 * @author TransCore ITS, LLC Created: Jan 31, 2016
 */
public class EntityEmulationData {

    public enum EntityDataType {
        ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA("archivedDataProcessingDocumentationMetadataMsg", "archived-data-processing-documentation-metadata-item"),
        CCTVINVENTORY("cCTVInventoryMsg", "cctv-inventory-item"),
        CCTVSTATUS("cCTVStatusMsg", "cctv-status-item"),
        CENTERACTIVEVERIFICATION("centerActiveVerificationResponseMsg", ""),
        DETECTORDATA("detectorDataMsg", "detector-data-item"),
        DETECTORINVENTORY("detectorInventoryMsg", "detector-inventory-item"),
        DETECTORMAINTENANCEHISTORY("detectorMaintenanceHistoryMsg", "detector-maintenance-history-item"),
        DETECTORSTATUS("detectorStatusMsg", "detector-status-item"),
        DMSFONTTABLE("dMSFontTableMsg", "dms-font-table-item"),
        DMSINVENTORY("dMSInventoryMsg", "dms-inventory-item"),
        DMSMESSAGEAPPEARANCE("dMSMessageAppearanceMsg", ""),
        DMSMESSAGEINVENTORY("dMSMessageInventoryMsg", "dms-message-inventory-item"),
        DMSSTATUS("dMSStatusMsg", "dms-status-item"),
        ESSINVENTORY("eSSInventoryMsg", "ess-inventory-item"),
        ESSOBSERVATIONMETADATA("eSSObservationMetadataMsg", "ess-observation-metadata-item"),
        ESSOBSERVATIONREPORT("eSSObservationReportMsg", "ess-observation-report-item"),
        ESSSTATUS("eSSStatusMsg", "ess-status-item"),
        EVENTSACTIONLOG("actionLogMsg", "log-entry"),
        EVENTSINDEX("eventIndexMsg", "eventIndex"),
        EVENTSUPDATES("fEUMsg", "FEU"),
        GATECONTROLSCHEDULE("gateControlScheduleMsg", "gate-control-schedule-item"),
        GATEINVENTORY("gateInventoryMsg", "gate-inventory-item"),
        GATESTATUS("gateStatusMsg", "gate-status-item"),
        HARCONTROLSCHEDULE("hARControlScheduleMsg", "har-control-schedule-item"),
        HARINVENTORY("hARInventoryMsg", "har-inventory-item"),
        HARMESSAGEINVENTORY("hARMessageInventoryMsg", "har-message-inventory-item"),
        HARSTATUS("hARStatusMsg", "har-status-item"),
        INTERSECTIONSIGNALCONTROLSCHEDULE("intersectionSignalControlScheduleMsg", "intersection-signal-control-schedule-item"),
        INTERSECTIONSIGNALINVENTORY("intersectionSignalInventoryMsg", "intersection-signal-inventory-item"),
        INTERSECTIONSIGNALSTATUS("intersectionSignalStatusMsg", "intersection-signal-status-item"),
        INTERSECTIONSIGNALTIMINGPATTERNINVENTORY("intersectionSignalTimingPatternInventoryMsg", "intersection-signal-timing-pattern-inventory-item"),
        LCSCONTROLSCHEDULE("lCSControlScheduleMsg", "lcs-control-schedule-item"),
        LCSINVENTORY("lCSInventoryMsg", "lcs-inventory-item"),
        LCSSTATUS("lCSStatusMsg", "lcs-status-item"),
        LINKINVENTORY("linkInventoryMsg", "link-inventory-item"),
        LINKSTATUS("linkStatusMsg", "link-status-item"),
        NODEINVENTORY("nodeInventoryMsg", "node-inventory-item"),
        NODESTATUS("nodeStatusMsg", "node-status-item"),        
        RAMPMETERCONTROLSCHEDULE("rampMeterControlScheduleMsg", "ramp-meter-control-schedule-item"),
        RAMPMETERINVENTORY("rampMeterInventoryMsg", "ramp-meter-inventory-item"),
        RAMPMETERPLANINVENTORY("rampMeterPlanInventoryMsg", "ramp-meter-plan-inventory-item"),
        RAMPMETERSTATUS("rampMeterStatusMsg", "ramp-meter-status-item"),
        SECTIONCONTROLSCHEDULE("sectionControlScheduleMsg", "section-control-schedule-item"),
        SECTIONSIGNALTIMINGPATTERNINVENTORY("sectionSignalTimingPatternInventoryMsg", "signal-section-timing-pattern-inventory-item"),
        SECTIONSTATUS("sectionStatusMsg", "section-status-item"),
        VIDEOSWITCHINVENTORY("videoSwitchInventoryMsg", "video-switch-inventory-item"),
        VIDEOSWITCHSTATUS("videoSwitchStatusMsg", "video-switch-status-item");

        private final String messageName;  // the message name associated with this entity data type
        private final String entityItemMessageReference;  // the message name associated with this entity data type

        EntityDataType(String messageName, String entityItemReference) {
            this.messageName = messageName;
            this.entityItemMessageReference = entityItemReference;
        }

        String relatedMessage() {
            return messageName;
        }

        String entityItemReference() {
            return entityItemMessageReference;
        }
    }

    private static String tMDDSchemaTableName;
    
    public static void setTMDDSchemaTableName(String tableName){
        tMDDSchemaTableName = tableName;
    }
    
    public static void initialize(EntityDataType entityDataType, byte[] entityData) throws EntityEmulationException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = EntityEmulationRepository.getConnection();
            
            // Clear all existing records from the entity data type table
            long startTime = System.currentTimeMillis();
            String deleteEntityElementsCommand = "delete from " + entityDataType.name();
            System.out.println("   Committing ");
            conn.commit();
            System.out.println("   Committing before " + entityDataType + " took " + (System.currentTimeMillis() - startTime) + " ms.");

            System.out.println("   Deleting " + entityDataType);
            stmt = conn.createStatement();
            stmt.execute(deleteEntityElementsCommand);
            stmt.close();
            conn.commit();
            EntityEmulationRepository.releaseConnection(conn);
            System.out.println("   Deleting " + entityDataType + " took " + (System.currentTimeMillis() - startTime) + " ms.");

            startTime = System.currentTimeMillis();
            ArrayList<EntityDataRecord> dataRecords = convertByteArrayToEntityRecords(entityDataType, entityData);
            System.out.println("   Verifying " + entityDataType + " data took " + (System.currentTimeMillis() - startTime) + " ms.");

            startTime = System.currentTimeMillis();
            insertEntityElement(entityDataType, dataRecords);
            System.out.println("   Inserting " + entityDataType + " data took " + (System.currentTimeMillis() - startTime) + " ms.");

            // Just in case the entity data received has skipped entity index positions... close the gaps and adjust element names.
            // If this is not done, then the same entity index could be applied to multiple entity elements by mistake.
            startTime = System.currentTimeMillis();
            reorderEntityElements(entityDataType);
            System.out.println("   Reordering " + entityDataType + " data took " + (System.currentTimeMillis() - startTime) + " ms.");


        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            System.out.println("SQLException !!\n" + Thread.currentThread().getStackTrace());
            Writer result = new StringWriter();
            PrintWriter printWriter = new PrintWriter(result);
            ex.printStackTrace(printWriter);
            System.out.println(result.toString());
            ex.printStackTrace();
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new EntityEmulationException(ex);
        } finally{
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex){                    
                }
            }

            if (conn != null) {
                try {
                    EntityEmulationRepository.releaseConnection(conn);
                } catch (Exception ex){                    
                }
            }

        }

    }

    public static void addEntity(EntityDataType entityDataType, String entityId) throws DuplicateEntityIdException, InvalidValueException, EntityEmulationException {

        // first verify the EntityId is valid for adding.  
        verifyEntityIdValue(entityDataType, entityId);
        // verify that the EntityId value is not already being used.
        verifyEntityIdIsUnique(entityDataType, entityId);

        // Next add the default entity emulation information
        createEntityElements(entityDataType, entityId);
    }

    public static List<EntityDataType> addEntity(EntityDataType primaryDataType, EntityDataType[] entityDataTypes, String entityId) throws DuplicateEntityIdException, InvalidValueException, EntityEmulationException {

        ArrayList<EntityProcessingSpecification> entityProcessingList = new ArrayList();
        ArrayList<EntityDataType> entityProcessedList = new ArrayList();

        // first verify the EntityId is valid for adding.  
        verifyEntityIdValue(primaryDataType, entityId);
        // verify that the EntityId value is not already being used.
        verifyEntityIdIsUnique(primaryDataType, entityId);

        entityProcessingList.add(new EntityProcessingSpecification(primaryDataType,entityId));
        entityProcessedList.add(primaryDataType);
        
        for (EntityDataType secondaryType : entityDataTypes){
            try{
                verifyEntityIdValue(secondaryType, entityId);
                verifyEntityIdIsUnique(secondaryType, entityId);
                entityProcessingList.add(new EntityProcessingSpecification(secondaryType,entityId));
                entityProcessedList.add(secondaryType);
            } catch (Exception ex){
                // The entity addition is not applicable for this data type.
            }
        }        
        
        // Next add the default entity emulation information
        createEntityElements(entityProcessingList.toArray(new EntityProcessingSpecification[entityProcessingList.size()]));
        return entityProcessedList;
    }    
    
    public static List<EntityDataType> addEntity(EntityProcessingSpecification primaryDataTypeSpec, EntityProcessingSpecification[] secondaryDataTypeSpecs) throws DuplicateEntityIdException, InvalidValueException, EntityEmulationException {

        ArrayList<EntityProcessingSpecification> entityProcessingList = new ArrayList();
        ArrayList<EntityDataType> entityProcessedList = new ArrayList();

        // first verify the EntityId is valid for adding.  
        verifyEntityIdValue(primaryDataTypeSpec.getDataType(), primaryDataTypeSpec.getEntityId());
        // verify that the EntityId value is not already being used.
        verifyEntityIdIsUnique(primaryDataTypeSpec.getDataType(), primaryDataTypeSpec.getEntityId());

        entityProcessingList.add(new EntityProcessingSpecification(primaryDataTypeSpec.getDataType(), primaryDataTypeSpec.getEntityId()));
        entityProcessedList.add(primaryDataTypeSpec.getDataType());
        
        for (EntityProcessingSpecification secondaryType : secondaryDataTypeSpecs){
            try{
                verifyEntityIdValue(secondaryType.getDataType(), secondaryType.getEntityId());
                verifyEntityIdIsUnique(secondaryType.getDataType(), secondaryType.getEntityId());
                entityProcessingList.add(secondaryType);
                entityProcessedList.add(secondaryType.getDataType());
            } catch (Exception ex){
                System.err.println("Verification error with secondaryType "+secondaryType.getDataType().name() + " with ID "+secondaryType.getEntityId());
                // Entity addition is not applicable for this data type.
            }
        }        
        
        // Next add the entity emulation information
        createEntityElements(entityProcessingList.toArray(new EntityProcessingSpecification[entityProcessingList.size()]));
        return entityProcessedList;
    }    
    
    public static List<EntityDataType> deleteEntity(EntityDataType primaryDataType, EntityDataType[] entityDataTypes, String entityId) throws InvalidValueException, EntityEmulationException {

         // verify that the EntityId value already exists.
        verifyEntityIdExists(primaryDataType, entityId);       
        
        ArrayList<EntityDataType> entityDataTypeList = new ArrayList();
        entityDataTypeList.add(primaryDataType);
        
        for (EntityDataType secondaryType : entityDataTypes){
            try{
                 // verify that the EntityId value already exists.
                verifyEntityIdExists(secondaryType, entityId);      
                entityDataTypeList.add(secondaryType);
            } catch (Exception ex){
                // The entity addition is not applicable for this data type.
            }
        }        

        // Next delete the entity emulation information
        deleteEntityElements(entityDataTypeList.toArray(new EntityDataType[entityDataTypeList.size()]), entityId);
        return entityDataTypeList;
    }

    
    public static List<EntityDataType> deleteEntity(EntityProcessingSpecification primaryDataTypeSpec, EntityProcessingSpecification[] secondaryDataTypeSpecs) throws DuplicateEntityIdException, InvalidValueException, EntityEmulationException {

        ArrayList<EntityProcessingSpecification> entityProcessingList = new ArrayList();
        ArrayList<EntityDataType> entityProcessedList = new ArrayList();

        // first verify the EntityId already exists.  
        verifyEntityIdExists(primaryDataTypeSpec.getDataType(), primaryDataTypeSpec.getEntityId());


        entityProcessingList.add(new EntityProcessingSpecification(primaryDataTypeSpec.getDataType(), primaryDataTypeSpec.getEntityId()));
        entityProcessedList.add(primaryDataTypeSpec.getDataType());
        
        for (EntityProcessingSpecification secondaryType : secondaryDataTypeSpecs){
            try{
                verifyEntityIdExists(secondaryType.getDataType(), secondaryType.getEntityId());
                entityProcessingList.add(secondaryType);
                entityProcessedList.add(secondaryType.getDataType());
            } catch (Exception ex){
                // Entity addition is not applicable for this data type.
            }
        }        
        
        // Next delete the entity emulation information
        deleteEntityElements(entityProcessingList.toArray(new EntityProcessingSpecification[entityProcessingList.size()]));
        return entityProcessedList;
    }    
    
    public static List<EntityDataType> updateEntityElement(EntityDataType primaryDataType, EntityDataType[] entityDataTypes, String entityId, String elementName, String elementValue) throws InvalidValueException, EntityEmulationException {

        // first verify the EntityId is valid.  
        verifyEntityIdValue(primaryDataType, entityId);
  
        // verify that the EntityId value already exists.
        verifyEntityIdExists(primaryDataType, entityId);
                
        // verify that the element name and value is valid
        int elementDefinitionId = verifyEntityElement(primaryDataType, elementName, elementValue);
                
        ArrayList<EntityDataType> entityDataTypeList = new ArrayList();
        entityDataTypeList.add(primaryDataType);
        if (isElementType(primaryDataType, elementName, entityId, "EntityId")){
            for (EntityDataType secondaryType : entityDataTypes){
                try{
                    // first verify the EntityId is valid.  
                    verifyEntityIdValue(secondaryType, entityId);
  
                    // verify that the EntityId value already exists.
                    verifyEntityIdExists(secondaryType, entityId);
        
                    entityDataTypeList.add(secondaryType);
                } catch (Exception ex){
                    // The entity addition is not applicable for this data type.
                }
            }
            updateEntityIDs(entityDataTypeList.toArray(new EntityDataType[entityDataTypeList.size()]), entityId, elementValue);
        } else {
            // find the record and update the value
            updateElementOfEntity(primaryDataType, entityId, elementDefinitionId, elementName, elementValue);           
        }

        return entityDataTypeList;
    }
    
    public static List<EntityDataType> updateEntityElement(EntityProcessingSpecification primaryDataType, EntityProcessingSpecification[] entityDataTypes, String elementName, String elementValue) throws InvalidValueException, EntityEmulationException {

        // first verify the EntityId is valid.  
        verifyEntityIdValue(primaryDataType.getDataType(), primaryDataType.getEntityId());
  
        // verify that the EntityId value already exists.
        verifyEntityIdExists(primaryDataType.getDataType(), primaryDataType.getEntityId());
                
        // verify that the element name and value is valid
        int elementDefinitionId = verifyEntityElement(primaryDataType.getDataType(), elementName, elementValue);
                
        ArrayList<EntityProcessingSpecification> entityProcessingList = new ArrayList();
        ArrayList<EntityDataType> entityDataTypeList = new ArrayList();
        entityProcessingList.add(primaryDataType);
        entityDataTypeList.add(primaryDataType.getDataType());
        
        if (isElementType(primaryDataType.getDataType(), elementName, primaryDataType.getEntityId(),"EntityId")){
            for (EntityProcessingSpecification secondaryType : entityDataTypes){
                try{
                    
                    // first verify the EntityId is valid.  
                    verifyEntityIdValue(secondaryType.getDataType(), secondaryType.getEntityId());
  
                    // verify that the EntityId value already exists.
                    verifyEntityIdExists(secondaryType.getDataType(), secondaryType.getEntityId());
        
                    entityProcessingList.add(secondaryType);
                    entityDataTypeList.add(secondaryType.getDataType());
                } catch (Exception ex){
                    // The entity addition is not applicable for this data type.
                }
            }
            updateEntityIDs(entityProcessingList.toArray(new EntityProcessingSpecification[entityDataTypeList.size()]), elementValue);
        } else {
            // find the record and update the value
            updateElementOfEntity(primaryDataType.getDataType(), primaryDataType.getEntityId(), elementDefinitionId, elementName, elementValue);           
        }

        return entityDataTypeList;
    }    
    
    public static void deleteEntity(EntityDataType entityDataType, String entityId) throws InvalidValueException, EntityEmulationException {

        // first verify the EntityId is valid for adding.  
        verifyEntityIdValue(entityDataType, entityId);

        // Next add the default entity emulation information
        deleteEntityElements(entityDataType, entityId);
    }
    
    public static void addEntityElement(EntityDataType entityDataType, String entityId, String elementName, String elementValue) throws InvalidValueException, EntityEmulationException {

        // first verify the EntityId is valid.  
        verifyEntityIdValue(entityDataType, entityId);

        // verify that the EntityId value already exists.
        verifyEntityIdExists(entityDataType, entityId);

        // verify that the element name and value is valid
        // add the entity element
        int elementDefinitionId = verifyEntityElement(entityDataType, elementName, elementValue);
        addElementToEntity(entityDataType, entityId, elementDefinitionId, elementName, elementValue);

    }

    public static void updateEntityElement(EntityDataType entityDataType, String entityId, String elementName, String elementValue) throws InvalidValueException, EntityEmulationException {

        // first verify the EntityId is valid.  
        verifyEntityIdValue(entityDataType, entityId);

        // verify that the EntityId value already exists.
        verifyEntityIdExists(entityDataType, entityId);
        
        // verify that the element name and value is valid
        int elementDefinitionId = verifyEntityElement(entityDataType, elementName, elementValue);

        // find the record and update the value
        updateElementOfEntity(entityDataType, entityId, elementDefinitionId, elementName, elementValue);

    }

    public static String getEntityElementValue(EntityDataType entityDataType, String entityId, String elementName) throws InvalidValueException, EntityEmulationException {
        String entityValue = "";

        // first verify the EntityId is valid.  
        verifyEntityIdValue(entityDataType, entityId);

        // verify that the EntityId value already exists.
        verifyEntityIdExists(entityDataType, entityId);

        // gather the element value
        entityValue = getValueOfEntityElement(entityDataType, entityId, elementName);

        return entityValue;
    }

    public static void deleteEntityElement(EntityDataType entityDataType, String entityId, String elementName) throws InvalidValueException, InvalidEntityElementException, EntityEmulationException {

        // first verify the EntityId is valid.  
        verifyEntityIdValue(entityDataType, entityId);

        // verify that the EntityId value already exists.
        verifyEntityIdExists(entityDataType, entityId);

        if (!isElementType(entityDataType, elementName, entityId, "EntityId")){        
            // find the record and update the value
            deleteElementOfEntity(entityDataType, entityId, elementName);
        } else {
            throw new EntityEmulationException("Element "+elementName+" specifies the "+entityDataType + " entity id and can not be deleted individually.  To delete this element the entire entity must be deleted.");
        }
    }

    public static ArrayList<EntityDataRecord> getEntityElements(EntityDataType entityDataType, ArrayList<DataFilter> filters) throws NoMatchingDataException, EntityEmulationException {
        ArrayList<EntityDataRecord> filteredResults = new ArrayList();

        // gather the elements
        filteredResults = getFilteredEntityElements(entityDataType, filters);

        return filteredResults;
    }

    public static ArrayList<EntityDataRecord> getRelatedEntityElementsWithValueType(EntityDataType entityDataType, ArrayList<DataFilter> filters, String entityValueType) throws NoMatchingDataException, EntityEmulationException {
        ArrayList<EntityDataRecord> filteredResults = new ArrayList();

        // gather the elements
        filteredResults = getFilteredEntityIdElements(entityDataType, filters, entityValueType);

        return filteredResults;
    }    
    
    public static int getEntityCount(EntityDataType entityDataType) throws NoMatchingDataException, EntityEmulationException {

        return getNumberOfEntitiesInDataType(entityDataType);
    }

    public static String getMaxEntityIdValue(EntityDataType entityDataType) throws NoMatchingDataException, EntityEmulationException {
        return getMaxEntityIdInDataType(entityDataType);
    }

    public static String getDefaultEntityElementNameValue(EntityDataType entityDataType, String elementName) throws NoMatchingDataException, EntityEmulationException {
        return getDefaultEntityIdElementValue(entityDataType, elementName);
    }
    
    public static boolean isElementTypeMatch(EntityDataType entityDataType, String elementName, String entityId, String elementType)throws EntityEmulationException{
        return isElementType(entityDataType, elementName, entityId, elementType);
    }
    private static ArrayList<EntityDataRecord> convertByteArrayToEntityRecords(EntityEmulationData.EntityDataType entityDataType, byte[] entityData) throws EntityEmulationException {
        ArrayList<EntityDataRecord> results = new ArrayList<EntityDataRecord>();
        if (entityData != null) {
            try {
                ByteArrayInputStream bInput = new ByteArrayInputStream(entityData);
                BufferedReader bfReader = null;
                bfReader = new BufferedReader(new InputStreamReader(bInput, "UTF-8"));
                String temp = null;

                String entityReference = "tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference();

                while ((temp = bfReader.readLine()) != null) {
                    // Each line should have be in an 'elementName = elementValue' format.
                    // For each element, lookup it's SchemaId and determine it's entityId
                    String[] elementParts = temp.split("=");
                    //Only process the record if it came in a proper format.
                    //With TMDDv31 elements can have null values, so we need to allow for that.
                    String elementName = elementParts[0].trim();
                    String elementValue = elementParts.length > 1 ? temp.substring(temp.indexOf("=")+1).trim() : "";
                    int schemaId = verifyEntityElement(entityDataType, elementName, elementValue);

                    int entityIndex = 0;
                    // Strip the initial elements from the element Name.  The next character must either be
                    // a "." or "[" which indicates an index reference is being provided.
                    String strippedElementName = elementName.replace(entityReference, "");
                    if (strippedElementName.startsWith(".")) {
                        entityIndex = 1;
                    } else if (strippedElementName.startsWith("[") && strippedElementName.contains("]")) {
                        // get the characters between the brackets.  If the characters are an integer then
                        // use this value as the index.
                        String indexValue = strippedElementName.substring(strippedElementName.indexOf("[") + 1, strippedElementName.indexOf("]"));
                        entityIndex = Integer.parseInt(indexValue);
                    }
                    EntityDataRecord newRecord = new EntityDataRecord();
                    newRecord.setEntityIndex(entityIndex);
                    newRecord.setEntityElement(elementName);
                    newRecord.setEntityElementValue(elementValue);
                    newRecord.setSchemaDetailId(schemaId);
                    results.add(newRecord);
                }

                bInput.close();

            } catch (IOException ex) {
                throw new EntityEmulationException(ex);
            }
        }
        return results;
    }

    private static void verifyEntityIdIsUnique(EntityDataType entityDataType, String entityId) throws DuplicateEntityIdException, InvalidEntityIdValueException, EntityEmulationException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {

            String newStatement = "SELECT count(*) FROM " + entityDataType.name() + " where SchemaDetailId in (select schemaId from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType.name() + "\" and elementName = \"EntityId\") and EntityElementValue = \"" + entityId + "\"";
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(newStatement);

            // If the returned value is greater than 0, then this entityId provided
            // already exists.  Throw an exception.
            if (rs.getInt(1) > 0) {
                throw new DuplicateEntityIdException("An entity with id " + entityId + " already exists in " + entityDataType.name() + ".");
            }

            rs.close();
            stmt.close();
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally {
            if (conn != null){
                try{
                    EntityEmulationRepository.releaseConnection(conn);                
                } catch (SQLException ex){
                    System.err.println("Unable to release connection: "+ ex.getMessage());                    
                }
            }
            
            try{
                if (rs != null)rs.close();
            } catch (SQLException ex){
                System.err.println("Unable to close resultset: "+ ex.getMessage());                                    
            }
            
            try{
                if (stmt != null)stmt.close();
            } catch (SQLException ex){
                System.err.println("Unable to close Max Entity Statement: "+ ex.getMessage());                                    
            }            
      
        }

    }

    private static void verifyEntityIdExists(EntityDataType entityDataType, String entityId) throws InvalidEntityIdValueException, EntityEmulationException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {

            String newStatement = "SELECT count(*) FROM " + entityDataType.name() + " where SchemaDetailId in (select schemaId from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType.name() + "\" and elementName = \"EntityId\") and EntityElementValue = \"" + entityId + "\"";
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(newStatement);

            // If the returned value is greater than 0, then this entityId provided
            // already exists.  Throw an exception.
            if (rs.getInt(1) == 0) {
                throw new InvalidEntityIdValueException("No entity with id " + entityId + " currently exists in " + entityDataType.name() + ".");
            }
            
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }

    }
   
    private static void verifyEntityIdValue(EntityDataType entityDataType, String entityId) throws InvalidValueException, EntityEmulationException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {

            int schemaId = 0;
            String schemaIdCommand = "select schemaId from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType.name() + "\" and elementName = \"EntityId\"";
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(schemaIdCommand);
            schemaId = rs.getInt("schemaId");
            stmt.close();
            rs.close();

            String schemaIdValidationRulesCommand = "select BaseType, minOccurs, maxOccurs, minLength, maxLength, minInclusive, maxInclusive, enumeration from "+tMDDSchemaTableName+" where id = " + schemaId;
            stmt = conn.createStatement();
            rs = stmt.executeQuery(schemaIdValidationRulesCommand);

            String baseType = rs.getString("BaseType");
            String minLength = rs.getString("minLength");
            String maxLength = rs.getString("maxLength");
            String minInclusive = rs.getString("minInclusive");
            String maxInclusive = rs.getString("maxInclusive");
            String enumeration = rs.getString("enumeration");

            EntityDataValidator.validateValue(baseType, minLength, maxLength, minInclusive, maxInclusive, enumeration, entityId);
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }


    }

    private static int verifyEntityElement(EntityDataType entityDataType, String entityElement, String entityValue) throws InvalidValueException, EntityEmulationException {
        int schemaId = 0;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {

            // Remove any index indicators from the entityElement.
            String simplifiedElement = entityElement.replaceAll("\\[\\d+\\]", "").replace("[]", "").replace("[*]", "");
            String extendedElement = "tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "." + simplifiedElement;
            String schemaIdCommand = "select schemaId, BaseType, minOccurs, maxOccurs, minLength, maxLength, minInclusive, maxInclusive, enumeration from TMDDMessageElementDefinitionQuery where Message = \"" + entityDataType.relatedMessage() + "\" and (ShortPath = \"" + simplifiedElement + "\" or ShortPath = \"" + extendedElement + "\")";
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(schemaIdCommand);

            if (!rs.isBeforeFirst()) {
                stmt.close();
                rs.close();
                System.out.println(schemaIdCommand);
                throw new InvalidEntityElementException("Unable to locate a defined element similar to " + entityElement + " for Entity Data Type " + entityDataType);
            } else {
                schemaId = rs.getInt("schemaId");
                String baseType = rs.getString("BaseType");
                String minLength = rs.getString("minLength");
                String maxLength = rs.getString("maxLength");
                String minInclusive = rs.getString("minInclusive");
                String maxInclusive = rs.getString("maxInclusive");
                String enumeration = rs.getString("enumeration");

                EntityDataValidator.validateValue(baseType, minLength, maxLength, minInclusive, maxInclusive, enumeration, entityValue);
                stmt.close();
                rs.close();
            }
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex){
            System.out.println("Error with Entity Element "+entityElement + " of Data Type "+entityDataType+ex.getMessage());
            throw new EntityEmulationException(ex);
        } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }

        return schemaId;
    }

        private static void createEntityElements(EntityDataType entityDataType, String entityId) throws EntityEmulationException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            String messageName = entityDataType.relatedMessage();

            int maxEntityIndex = 0;
            String getMaxEntityIndexCommand = "Select max(EntityIndex) from " + entityDataType.name();

            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(getMaxEntityIndexCommand);

            maxEntityIndex = rs.getInt(1) + 1;

            stmt.close();
            rs.close();
            String insertEntityElementsCommand = "INSERT INTO " + entityDataType.name() + " ( EntityIndex, EntityElement, SchemaDetailId, EntityElementValue ) SELECT "
                    + maxEntityIndex + " as Expr1, replace(replace(replace(replace(replace(["+tMDDSchemaTableName+"].Path,\"/tmddX\",\".tmddX\"),\"/tmdd\",\"tmdd\"),\"/\",\".\"),\"" + entityDataType.entityItemMessageReference + "[*]\",\"" + entityDataType.entityItemMessageReference + "[" + maxEntityIndex + "]\"),\"[*]\",\"\") as Expr2, ["+tMDDSchemaTableName+"].id, ["+tMDDSchemaTableName+"].value "
                    + "FROM ["+tMDDSchemaTableName+"] "
                    + "WHERE (((["+tMDDSchemaTableName+"].Message)=\"" + messageName + "\") AND ((["+tMDDSchemaTableName+"].DataConceptType)in(\"data-element\",\"data-frame\")) AND (["+tMDDSchemaTableName+"].includeAsDefaultData IS NOT NULL));";
//            System.out.println(insertEntityElementsCommand);
            stmt = conn.createStatement();
            stmt.execute(insertEntityElementsCommand);
            stmt.close();
            conn.commit();

            String updateEntityIDCommand = "Update " + entityDataType.name() + " set EntityElementValue = \"" + entityId + "\" where EntityIndex = " + maxEntityIndex + " and SchemaDetailId in (select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType.name() + "\" and elementName = \"EntityId\")";
            stmt = conn.createStatement();
            stmt.execute(updateEntityIDCommand);
            stmt.close();

            conn.commit();
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }

    }
        
    private static void createEntityElements(EntityProcessingSpecification[] entitySpecifications) throws EntityEmulationException {
        Connection conn = null;
        ResultSet[] resultSets = new ResultSet[entitySpecifications.length];
        Statement[] maxEntityStatement = new Statement[entitySpecifications.length];
        Statement[] insertElementsStatement = new Statement[entitySpecifications.length];
        Statement[] updateElementsStatement = new Statement[entitySpecifications.length];  
        Statement[] updateRelatedElementsStatement = new Statement[entitySpecifications.length];  
        int entityDataTypeIndex = 0;
        
        try {
           conn = EntityEmulationRepository.getConnection();
           for (EntityProcessingSpecification entitySpecification : entitySpecifications){
                String messageName = entitySpecification.getDataType().relatedMessage();             

                int maxEntityIndex = 0;
                String getMaxEntityIndexCommand = "Select max(EntityIndex) from " + entitySpecification.getDataType().name();

                maxEntityStatement[entityDataTypeIndex] = conn.createStatement();
                resultSets[entityDataTypeIndex] = maxEntityStatement[entityDataTypeIndex].executeQuery(getMaxEntityIndexCommand);

                maxEntityIndex = resultSets[entityDataTypeIndex].getInt(1) + 1;


                String insertEntityElementsCommand = "INSERT INTO " + entitySpecification.getDataType().name() + " ( EntityIndex, EntityElement, SchemaDetailId, EntityElementValue ) SELECT "
                    + maxEntityIndex + " as Expr1, replace(replace(replace(replace(replace(["+tMDDSchemaTableName+"].Path,\"/tmddX\",\".tmddX\"),\"/tmdd\",\"tmdd\"),\"/\",\".\"),\"" + entitySpecification.getDataType().entityItemMessageReference + "[*]\",\"" + entitySpecification.getDataType().entityItemMessageReference + "[" + maxEntityIndex + "]\"),\"[*]\",\"\") as Expr2, ["+tMDDSchemaTableName+"].id, ["+tMDDSchemaTableName+"].value "
                    + "FROM ["+tMDDSchemaTableName+"] "
                    + "WHERE (((["+tMDDSchemaTableName+"].Message)=\"" + messageName + "\") AND ((["+tMDDSchemaTableName+"].DataConceptType)in(\"data-element\",\"data-frame\")) AND (["+tMDDSchemaTableName+"].includeAsDefaultData IS NOT NULL));";

                insertElementsStatement[entityDataTypeIndex] = conn.createStatement();
                insertElementsStatement[entityDataTypeIndex].execute(insertEntityElementsCommand);

                String updateEntityIDCommand = "Update " + entitySpecification.getDataType().name() + " set EntityElementValue = \"" + entitySpecification.getEntityId() + "\" where EntityIndex = " + maxEntityIndex + " and SchemaDetailId in (select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entitySpecification.getDataType().name() + "\" and elementName = \"EntityId\")";
                updateElementsStatement[entityDataTypeIndex] = conn.createStatement();
                updateElementsStatement[entityDataTypeIndex].execute(updateEntityIDCommand);         

                if (entitySpecification.hasRelatedElementData()){
                    String updateRelatedElementCommand = "Update " + entitySpecification.getDataType().name() + " set EntityElementValue = \"" + entitySpecification.getRelatedElementValue() + "\" where EntityIndex = " + maxEntityIndex + " and SchemaDetailId in (select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entitySpecification.getDataType().name() + "\" and elementName = \""+entitySpecification.getRelatedElementName()+"\")";
                    updateRelatedElementsStatement[entityDataTypeIndex] = conn.createStatement();
                    updateRelatedElementsStatement[entityDataTypeIndex].execute(updateRelatedElementCommand);         
                }                
                
                entityDataTypeIndex++;
            }
            
            conn.commit(); 
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            rollbackTransaction(conn);
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            rollbackTransaction(conn);
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            rollbackTransaction(conn);
            throw new EntityEmulationException(ex);
        } finally {
            if (conn != null){
                try{
                EntityEmulationRepository.releaseConnection(conn);                
                } catch (SQLException ex){
                    System.err.println("Unable to release connection: "+ ex.getMessage());                    
                }
            }
            
            try{
                for (ResultSet rs : resultSets){
                    if (rs != null)rs.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close resultset: "+ ex.getMessage());                                    
            }
            
            try{
                for (Statement stmt : maxEntityStatement){
                    if (stmt != null)stmt.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close Max Entity Statement: "+ ex.getMessage());                                    
            }            

            try{
                for (Statement stmt : insertElementsStatement){
                    if (stmt != null)stmt.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close Insert Elements Statement: "+ ex.getMessage());                                    
            }            
            
             try{
                for (Statement stmt : updateElementsStatement){
                    if (stmt != null)stmt.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close Update Elements Statement: "+ ex.getMessage());                                    
            }            

             try{
                for (Statement stmt : updateRelatedElementsStatement){
                    if (stmt != null)stmt.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close Update Related Elements Statement: "+ ex.getMessage());                                    
            }                       
        }
    }

    private static void rollbackTransaction(Connection conn){
        try{
            if (conn != null) conn.rollback();
            EntityEmulationRepository.releaseConnection(conn);
        } catch (Exception ex){
            System.err.println("Unable to complete transaction rollback: "+ ex.getMessage());
        }
    }
    private static void deleteEntityElements(EntityDataType entityDataType, String entityId) throws EntityEmulationException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
                
        try {
            String selectEntityIDCommand = "Select EntityIndex from " + entityDataType.name() + " where EntityElementValue = \"" + entityId + "\" and SchemaDetailId in (select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType + "\" and elementName = \"EntityId\")";
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectEntityIDCommand);
            int entityIndex = rs.getInt("EntityIndex");
            stmt.close();

            String deleteEntityElementsCommand = "delete from " + entityDataType.name() + " where EntityIndex = " + entityIndex;
            stmt = conn.createStatement();
            stmt.execute(deleteEntityElementsCommand);
            stmt.close();
            conn.commit();

            reorderEntityElements(entityDataType);
            conn.commit();
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }

    }

    private static void deleteEntityElements(EntityDataType[] entityDataTypes, String entityId) throws EntityEmulationException {
        Connection conn = null;
        ResultSet[] resultSets = new ResultSet[entityDataTypes.length];
        Statement[] selectEntityStatement = new Statement[entityDataTypes.length];
        Statement[] deleteElementsStatement = new Statement[entityDataTypes.length];
        int entityDataTypeIndex = 0;
        
        try {
           conn = EntityEmulationRepository.getConnection();
           for (EntityDataType entityDataType : entityDataTypes){
            String selectEntityIDCommand = "Select EntityIndex from " + entityDataType.name() + " where EntityElementValue = \"" + entityId + "\" and SchemaDetailId in (select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType + "\" and elementName = \"EntityId\")";
            selectEntityStatement[entityDataTypeIndex] = conn.createStatement();
            resultSets[entityDataTypeIndex] = selectEntityStatement[entityDataTypeIndex].executeQuery(selectEntityIDCommand);
            int entityIndex = resultSets[entityDataTypeIndex].getInt("EntityIndex");

            String deleteEntityElementsCommand = "delete from " + entityDataType.name() + " where EntityIndex = " + entityIndex;
            deleteElementsStatement[entityDataTypeIndex] = conn.createStatement();
            deleteElementsStatement[entityDataTypeIndex].execute(deleteEntityElementsCommand);
            
            entityDataTypeIndex++;
           }
           
            conn.commit();
            EntityEmulationRepository.releaseConnection(conn);

            for (EntityDataType entityDataType : entityDataTypes){
                reorderEntityElements(entityDataType);
            }

        } catch (ClassNotFoundException ex) {
            rollbackTransaction(conn);
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            rollbackTransaction(conn);
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            rollbackTransaction(conn);
            throw new EntityEmulationException(ex);
        } finally {
            if (conn != null){
                try{
                EntityEmulationRepository.releaseConnection(conn);                
                } catch (SQLException ex){
                    System.err.println("Unable to release connection: "+ ex.getMessage());                    
                }
            }
            
            try{
                for (ResultSet rs : resultSets){
                    if (rs != null)rs.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close resultset: "+ ex.getMessage());                                    
            }
            
            try{
                for (Statement stmt : selectEntityStatement){
                    if (stmt != null)stmt.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close Select Entity Statement: "+ ex.getMessage());                                    
            }            

            try{
                for (Statement stmt : deleteElementsStatement){
                    if (stmt != null)stmt.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close Delete Elements Statement: "+ ex.getMessage());                                    
            }            
        }
                     
    }    


    private static void deleteEntityElements(EntityProcessingSpecification[] entityDataTypes) throws EntityEmulationException {
        Connection conn = null;
        ResultSet[] resultSets = new ResultSet[entityDataTypes.length];
        Statement[] selectEntityStatement = new Statement[entityDataTypes.length];
        Statement[] deleteElementsStatement = new Statement[entityDataTypes.length];
        int entityDataTypeIndex = 0;
        
        try {
           conn = EntityEmulationRepository.getConnection();
           for (EntityProcessingSpecification entityDataType : entityDataTypes){
            String selectEntityIDCommand = "Select EntityIndex from " + entityDataType.getDataType().name() + " where EntityElementValue = \"" + entityDataType.getEntityId() + "\" and SchemaDetailId in (select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType.getDataType().name() + "\" and elementName = \"EntityId\")";
            selectEntityStatement[entityDataTypeIndex] = conn.createStatement();
            resultSets[entityDataTypeIndex] = selectEntityStatement[entityDataTypeIndex].executeQuery(selectEntityIDCommand);
            int entityIndex = resultSets[entityDataTypeIndex].getInt("EntityIndex");

            String deleteEntityElementsCommand = "delete from " + entityDataType.getDataType().name() + " where EntityIndex = " + entityIndex;
            deleteElementsStatement[entityDataTypeIndex] = conn.createStatement();
            deleteElementsStatement[entityDataTypeIndex].execute(deleteEntityElementsCommand);
            
            entityDataTypeIndex++;
           }
           
            conn.commit();
            EntityEmulationRepository.releaseConnection(conn);

            for (EntityProcessingSpecification entityDataType : entityDataTypes){
                reorderEntityElements(entityDataType.getDataType());
            }

        } catch (ClassNotFoundException ex) {
            rollbackTransaction(conn);
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            rollbackTransaction(conn);
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            rollbackTransaction(conn);
            throw new EntityEmulationException(ex);
        } finally {
            if (conn != null){
                try{
                EntityEmulationRepository.releaseConnection(conn);                
                } catch (SQLException ex){
                    System.err.println("Unable to release connection: "+ ex.getMessage());                    
                }
            }
            
            try{
                for (ResultSet rs : resultSets){
                    if (rs != null)rs.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close resultset: "+ ex.getMessage());                                    
            }
            
            try{
                for (Statement stmt : selectEntityStatement){
                    if (stmt != null)stmt.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close Select Entity Statement: "+ ex.getMessage());                                    
            }            

            try{
                for (Statement stmt : deleteElementsStatement){
                    if (stmt != null)stmt.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close Delete Elements Statement: "+ ex.getMessage());                                    
            }            
        }
                     
    }        
    
    private static void addElementToEntity(EntityDataType entityDataType, String entityId, int definitionId, String elementName, String elementValue) throws EntityEmulationException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {

            String selectEntityIDCommand = "Select EntityIndex from " + entityDataType.name() + " where EntityElementValue = \"" + entityId + "\" and SchemaDetailId in (select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType + "\" and elementName = \"EntityId\")";
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectEntityIDCommand);
            int entityIndex = rs.getInt("EntityIndex");
            stmt.close();

            String processedElementName = elementName;
            if (!elementName.startsWith("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference())) {
                processedElementName = "tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "[" + entityIndex + "]." + processedElementName;
            } else if (elementName.startsWith("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + ".")) {
                processedElementName = processedElementName.replace("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + ".", "tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "[" + entityIndex + "].");
            }

            String insertEntityElementsCommand = "INSERT INTO " + entityDataType.name() + " ( EntityIndex, EntityElement, SchemaDetailId, EntityElementValue ) VALUES ("
                    + entityIndex + ",\"" + processedElementName + "\"," + definitionId + ",\"" + elementValue + "\")";
//            System.out.println(insertEntityElementsCommand);

            stmt = conn.createStatement();
            stmt.execute(insertEntityElementsCommand);
            stmt.close();

            conn.commit();
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }

    }

    private static void insertEntityElement(EntityDataType entityDataType, ArrayList<EntityDataRecord> dataRecords) throws EntityEmulationException {
        Connection conn = null;
        PreparedStatement insertEntityElementsCommand = null;
        
        try {
//            Statement stmt = EntityEmulationRepository.getConnection().createStatement();
            conn = EntityEmulationRepository.getConnection();
            insertEntityElementsCommand = conn.prepareStatement("INSERT INTO " + entityDataType.name() + " ( EntityIndex, EntityElement, SchemaDetailId, EntityElementValue ) VALUES (?,?,?,?)");

            for (EntityDataRecord thisRecord : dataRecords) {
                int entityIndex = thisRecord.getEntityIndex();
                String elementName = thisRecord.getEntityElement();
                String elementValue = thisRecord.getEntityElementValue();
                int definitionId = thisRecord.getSchemaDetailId();

                insertEntityElementsCommand.setInt(1, entityIndex);
                insertEntityElementsCommand.setString(2, elementName);
                insertEntityElementsCommand.setInt(3, definitionId);
                insertEntityElementsCommand.setString(4, elementValue);
                insertEntityElementsCommand.addBatch();

                //            String insertEntityElementsCommand = "INSERT INTO " + entitySpecification.name() + " ( EntityIndex, EntityElement, SchemaDetailId, EntityElementValue ) VALUES ("
                //                    + entityIndex + ",\"" + elementName + "\"," + definitionId + ",\"" + elementValue + "\")";
//            System.out.println(insertEntityElementsCommand);
//                stmt.addBatch(insertEntityElementsCommand);
            }
            insertEntityElementsCommand.executeBatch();
            insertEntityElementsCommand.close();
//            stmt.executeBatch();
//            stmt.close();
            conn.commit();
            EntityEmulationRepository.releaseConnection(conn);

        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally {
            
            try{
                if (insertEntityElementsCommand != null) insertEntityElementsCommand.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }


    }

    private static void updateElementOfEntity(EntityDataType entityDataType, String entityId, int definitionId, String elementName, String elementValue) throws EntityEmulationException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {

            String selectEntityIDCommand = "Select EntityIndex from " + entityDataType.name() + " where EntityElementValue = \"" + entityId + "\" and SchemaDetailId in (select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType + "\" and elementName = \"EntityId\")";
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectEntityIDCommand);
            int entityIndex = rs.getInt("EntityIndex");
            stmt.close();

            String processedElementName = elementName;
            if (!elementName.startsWith("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference())) {
                processedElementName = "tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "[" + entityIndex + "]." + processedElementName;
            } else if (elementName.startsWith("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + ".")) {
                processedElementName = processedElementName.replace("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + ".", "tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "[" + entityIndex + "].");
            }

            String updateEntityElementsCommand = "UPDATE " + entityDataType.name() + " set EntityElementValue = \"" + elementValue + "\" where EntityIndex = " + entityIndex + " and EntityElement = \"" + processedElementName + "\" and SchemaDetailId =\"" + definitionId + "\"";
//            System.out.println(updateEntityElementsCommand);

            stmt = conn.createStatement();
            int rowCount = stmt.executeUpdate(updateEntityElementsCommand);
            stmt.close();

            if (rowCount == 0) {
                throw new EntityEmulationException("No matching elements were updated.");
            }

            conn.commit();
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }

    }

    
    private static void updateEntityIDs(EntityDataType[] entityDataTypes, String entityId, String newEntityId) throws EntityEmulationException {
        Connection conn = null;
        ResultSet[] resultSets = new ResultSet[entityDataTypes.length];
        Statement[] selectEntityStatement = new Statement[entityDataTypes.length];
        Statement[] updateEntityIDStatement = new Statement[entityDataTypes.length];
        int entityDataTypeIndex = 0;
        
        try {
           conn = EntityEmulationRepository.getConnection();
           for (EntityDataType entityDataType : entityDataTypes){
            String selectEntityIDCommand = "Select EntityIndex, SchemaDetailId from " + entityDataType.name() + " where EntityElementValue = \"" + entityId + "\" and SchemaDetailId in (select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType + "\" and elementName = \"EntityId\")";
            selectEntityStatement[entityDataTypeIndex] = conn.createStatement();
            resultSets[entityDataTypeIndex] = selectEntityStatement[entityDataTypeIndex].executeQuery(selectEntityIDCommand);
            int entityIndex = resultSets[entityDataTypeIndex].getInt("EntityIndex");
            int schemaDetailId = resultSets[entityDataTypeIndex].getInt("SchemaDetailId");
            

            String updateEntityElementsCommand = "Update " + entityDataType.name() + " set EntityElementValue = \"" + newEntityId +"\" where EntityIndex = " + entityIndex + " and SchemaDetailId = "+schemaDetailId + " and EntityElementValue = \"" + entityId + "\"" ;
            updateEntityIDStatement[entityDataTypeIndex] = conn.createStatement();
            updateEntityIDStatement[entityDataTypeIndex].execute(updateEntityElementsCommand);
            
            entityDataTypeIndex++;
           }
           
            conn.commit();
            EntityEmulationRepository.releaseConnection(conn);

        } catch (ClassNotFoundException ex) {
            rollbackTransaction(conn);
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            rollbackTransaction(conn);
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            rollbackTransaction(conn);
            throw new EntityEmulationException(ex);
        } finally {
            if (conn != null){
                try{
                EntityEmulationRepository.releaseConnection(conn);                
                } catch (SQLException ex){
                    System.err.println("Unable to release connection: "+ ex.getMessage());                    
                }
            }
            
            try{
                for (ResultSet rs : resultSets){
                    if (rs != null)rs.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close resultset: "+ ex.getMessage());                                    
            }
            
            try{
                for (Statement stmt : selectEntityStatement){
                    if (stmt != null)stmt.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close Select Entity Statement: "+ ex.getMessage());                                    
            }            

            try{
                for (Statement stmt : updateEntityIDStatement){
                    if (stmt != null)stmt.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close Update Elements Statement: "+ ex.getMessage());                                    
            }            
        }
                     
    }    
    
    private static void updateEntityIDs(EntityProcessingSpecification[] entityDataTypes, String newEntityId) throws EntityEmulationException {
        Connection conn = null;
        ResultSet[] resultSets = new ResultSet[entityDataTypes.length];
        Statement[] selectEntityStatement = new Statement[entityDataTypes.length];
        Statement[] updateEntityIDStatement = new Statement[entityDataTypes.length];
        int entityDataTypeIndex = 0;
        
        try {
           conn = EntityEmulationRepository.getConnection();
           for (EntityProcessingSpecification entityDataType : entityDataTypes){
                if (!entityDataType.hasRelatedElementData()){
                    String selectEntityIDCommand = "Select EntityIndex, SchemaDetailId from " + entityDataType.getDataType().name() + " where EntityElementValue = \"" + entityDataType.getEntityId() + "\" and SchemaDetailId in (select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType.getDataType().name() + "\" and elementName = \"EntityId\")";
                    selectEntityStatement[entityDataTypeIndex] = conn.createStatement();
                    resultSets[entityDataTypeIndex] = selectEntityStatement[entityDataTypeIndex].executeQuery(selectEntityIDCommand);
                    int entityIndex = resultSets[entityDataTypeIndex].getInt("EntityIndex");
                    int schemaDetailId = resultSets[entityDataTypeIndex].getInt("SchemaDetailId");

                    String updateEntityElementsCommand = "Update " + entityDataType.getDataType().name() + " set EntityElementValue = \"" + newEntityId +"\" where EntityIndex = " + entityIndex + " and SchemaDetailId = "+schemaDetailId + " and EntityElementValue = \"" + entityDataType.getEntityId() + "\"" ;
                    updateEntityIDStatement[entityDataTypeIndex] = conn.createStatement();
                    updateEntityIDStatement[entityDataTypeIndex].execute(updateEntityElementsCommand);                


                } else {
                    String selectEntityIDCommand = "Select EntityIndex, SchemaDetailId from " + entityDataType.getDataType().name() + " where EntityElementValue = \"" + entityDataType.getRelatedElementValue() + "\" and SchemaDetailId in (select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType.getDataType().name() + "\" and elementName = \""+entityDataType.getRelatedElementName()+"\")";
                    selectEntityStatement[entityDataTypeIndex] = conn.createStatement();
                    resultSets[entityDataTypeIndex] = selectEntityStatement[entityDataTypeIndex].executeQuery(selectEntityIDCommand);
                    int entityIndex = resultSets[entityDataTypeIndex].getInt("EntityIndex");
                    int schemaDetailId = resultSets[entityDataTypeIndex].getInt("SchemaDetailId");                

                    String updateEntityElementsCommand = "Update " + entityDataType.getDataType().name() + " set EntityElementValue = \"" + newEntityId +"\" where EntityIndex = " + entityIndex + " and SchemaDetailId = "+schemaDetailId + " and EntityElementValue = \"" + entityDataType.getRelatedElementValue() + "\"" ;
                    updateEntityIDStatement[entityDataTypeIndex] = conn.createStatement();
                    updateEntityIDStatement[entityDataTypeIndex].execute(updateEntityElementsCommand);                
                }

                entityDataTypeIndex++;
           }
           
            conn.commit();
            EntityEmulationRepository.releaseConnection(conn);

        } catch (ClassNotFoundException ex) {
            rollbackTransaction(conn);
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            rollbackTransaction(conn);
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            rollbackTransaction(conn);
            throw new EntityEmulationException(ex);
        } finally {
            if (conn != null){
                try{
                EntityEmulationRepository.releaseConnection(conn);                
                } catch (SQLException ex){
                    System.err.println("Unable to release connection: "+ ex.getMessage());                    
                }
            }
            
            try{
                for (ResultSet rs : resultSets){
                    if (rs != null)rs.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close resultset: "+ ex.getMessage());                                    
            }
            
            try{
                for (Statement stmt : selectEntityStatement){
                    if (stmt != null)stmt.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close Select Entity Statement: "+ ex.getMessage());                                    
            }            

            try{
                for (Statement stmt : updateEntityIDStatement){
                    if (stmt != null)stmt.close();
                }
            } catch (SQLException ex){
                System.err.println("Unable to close Update Elements Statement: "+ ex.getMessage());                                    
            }            
        }
                     
    }        
    
    private static void deleteElementOfEntity(EntityDataType entityDataType, String entityId, String elementName) throws EntityEmulationException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {

            String selectEntityIDCommand = "Select EntityIndex from " + entityDataType.name() + " where EntityElementValue = \"" + entityId + "\" and SchemaDetailId in (select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType + "\" and elementName = \"EntityId\")";
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectEntityIDCommand);
            int entityIndex = rs.getInt("EntityIndex");
            rs.close();
            stmt.close();

            String processedElementName = elementName;
            if (!elementName.startsWith("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference())) {
                processedElementName = "tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "[" + entityIndex + "]." + processedElementName;
            } else if (elementName.startsWith("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + ".")) {
                processedElementName = processedElementName.replace("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + ".", "tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "[" + entityIndex + "].");
            }

            String deleteEntityElementsCommand = "Delete from " + entityDataType.name() + " where EntityIndex = " + entityIndex + " and EntityElement = \"" + processedElementName + "\"";
//            System.out.println(deleteEntityElementsCommand);

            stmt = conn.createStatement();
            int rowCount = stmt.executeUpdate(deleteEntityElementsCommand);
            stmt.close();

            if (rowCount == 0) {
                throw new InvalidEntityElementException("No matching elements were deleted.");
            }

            conn.commit();
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }
    }

    private static boolean isElementType(EntityDataType entityDataType, String elementName, String entityId, String elementType) throws EntityEmulationException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            boolean results = false;
            String selectEntityIDCommand = "Select EntityIndex, SchemaDetailId from " + entityDataType.name() + " where EntityElementValue = \"" + entityId + "\" and SchemaDetailId in (select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType + "\" and elementName = \""+elementType+"\")";
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectEntityIDCommand);
            int entityIndex = rs.getInt("EntityIndex");
            int schemaDetailId = rs.getInt("SchemaDetailId");
            rs.close();
            stmt.close();

            String processedElementName = elementName;
            if (!elementName.startsWith("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference())) {
                processedElementName = "tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "[" + entityIndex + "]." + processedElementName;
            } else if (elementName.startsWith("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + ".")) {
                processedElementName = processedElementName.replace("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + ".", "tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "[" + entityIndex + "].");
            }

            String selectEntityElementsCommand = "Select SchemaDetailId from " + entityDataType.name() + " where EntityIndex = " + entityIndex + " and EntityElement = \"" + processedElementName + "\"";
//            System.out.println(selectEntityElementsCommand);

            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectEntityElementsCommand);

            // Assuming you are working with a newly returned ResultSet whose cursor is pointing before the first row, an easier way to check this is to just call isBeforeFirst(). This avoids having to back-track if the data is to be read.
            // As explained in the documentation, this returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            if (!rs.isBeforeFirst()) {
                rs.close();
                stmt.close();
                results = false;
            } else {
                int schemaIdResult = rs.getInt("SchemaDetailId");
                if (schemaIdResult == schemaDetailId){
                    results = true;
                } else {
                    results = false;
                }
                rs.close();
                stmt.close();
            }
            EntityEmulationRepository.releaseConnection(conn);
            return results;
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        }  finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }

    }
             
    
    private static String getValueOfEntityElement(EntityDataType entityDataType, String entityId, String elementName) throws EntityEmulationException {
        String valueResult = "";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {

            String selectEntityIDCommand = "Select EntityIndex from " + entityDataType.name() + " where EntityElementValue = \"" + entityId + "\" and SchemaDetailId in (select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType + "\" and elementName = \"EntityId\")";
//            System.out.println(selectEntityIDCommand);
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectEntityIDCommand);
            int entityIndex = rs.getInt("EntityIndex");
            rs.close();
            stmt.close();

            String processedElementName = elementName;
            if (!elementName.startsWith("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference())) {
                processedElementName = "tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "[" + entityIndex + "]." + processedElementName;
            } else if (elementName.startsWith("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + ".")) {
                processedElementName = processedElementName.replace("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + ".", "tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "[" + entityIndex + "].");
            }

            String selectEntityElementsCommand = "Select EntityElementValue from " + entityDataType.name() + " where EntityIndex = " + entityIndex + " and EntityElement = \"" + processedElementName + "\"";
//            System.out.println(selectEntityElementsCommand);

            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectEntityElementsCommand);

            // Assuming you are working with a newly returned ResultSet whose cursor is pointing before the first row, an easier way to check this is to just call isBeforeFirst(). This avoids having to back-track if the data is to be read.
            // As explained in the documentation, this returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            if (!rs.isBeforeFirst()) {
                rs.close();
                stmt.close();
                throw new InvalidEntityElementException("No matching elements were found.");
            } else {
                valueResult = rs.getString("EntityElementValue");
                rs.close();
                stmt.close();
            }
            EntityEmulationRepository.releaseConnection(conn);
            return valueResult;

        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new EntityEmulationException(ex);
         } catch (Exception ex) {
            throw new EntityEmulationException(ex);
       } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }
    }

    /**
     * Process entity elements that include filter specifications.
     * @param entityDataType
     * @param filters
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException 
     */
    private static ArrayList<EntityDataRecord> getFilteredEntityElements(EntityDataType entityDataType, ArrayList<DataFilter> filters) throws NoMatchingDataException, EntityEmulationException {
        ArrayList<EntityDataRecord> filteredResults = new ArrayList();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {

            String filterPhrase = "";

            if (filters != null) {
                for (DataFilter thisFilter : filters) {
                    if (thisFilter instanceof MultipleElementDataFilter) {
                        // Create the list of items that will be used in the filter
                        try {
                            processMultipleElementDataFilter(entityDataType, (MultipleElementDataFilter) thisFilter);
                        } catch (NoMatchingDataException nmde) {
                            String throwError = RIParameters.getInstance().getParameterValue(TMDDSettingsImpl.getTMDD_SETTINGS_GROUP(), TMDDSettingsImpl.getTMDD_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_PARAMETER(),
                                    TMDDSettingsImpl.getTMDD_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_DEFAULT_VALUE());
                            if (!Boolean.parseBoolean(throwError)) {
                                continue;
                            } else {
                                throw nmde;
                            }
                        }

                    } else {
                        try {
                            processDataFilter(entityDataType, thisFilter);
                        } catch (NoMatchingDataException nmde) {
                            String throwError = RIParameters.getInstance().getParameterValue(TMDDSettingsImpl.getTMDD_SETTINGS_GROUP(), TMDDSettingsImpl.getTMDD_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_PARAMETER(),
                                    TMDDSettingsImpl.getTMDD_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_DEFAULT_VALUE());
                            if (!Boolean.parseBoolean(throwError)) {
                                continue;
                            } else {
                                throw nmde;
                            }
                        }
                    }

                    if (filterPhrase.equals("")) {
                        filterPhrase = filterPhrase + " where ";
                    } else {
                        filterPhrase = filterPhrase + " and ";
                    }

                    filterPhrase = filterPhrase + thisFilter.getFilterSpecification();
                }
            }
            String selectFilteredElementsCommand = "Select EntityIndex, EntityElement, SchemaDetailId, EntityElementValue from " + entityDataType.name() + filterPhrase + " order by EntityIndex, SchemaDetailId, EntityElement asc";
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
//            System.out.println(selectFilteredElementsCommand);
            rs = stmt.executeQuery(selectFilteredElementsCommand);
            int firstMissingIndex = 0;
            int lastIndex = 0;
            while (rs.next()) {
                EntityDataRecord thisRecord = new EntityDataRecord();
                int ii = rs.getInt("EntityIndex");
                if (ii != lastIndex) {
                    lastIndex = ii;
                    firstMissingIndex++;
                }
                thisRecord.setEntityIndex(ii);

                String thisEntityElement = rs.getString("EntityElement");
                thisEntityElement = thisEntityElement.replace("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "[" + ii + "]", "tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "[" + firstMissingIndex + "]").replace("tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + ".", "tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "[" + firstMissingIndex + "].");

                thisRecord.setEntityElement(thisEntityElement);
                thisRecord.setSchemaDetailId(rs.getInt("SchemaDetailId"));
                thisRecord.setEntityElementValue(rs.getString("EntityElementValue"));
                filteredResults.add(thisRecord);
            }
            rs.close();
            stmt.close();
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }

        if (filteredResults.size() == 0) {
            throw new NoMatchingDataException("No Data was available that matched the request.");
        }

        return filteredResults;
    }

   /**
     * Return the entity id elements which match filter specifications.
     * @param entityDataType
     * @param filters
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException 
     */
    private static ArrayList<EntityDataRecord> getFilteredEntityIdElements(EntityDataType entityDataType, ArrayList<DataFilter> filters, String entityValueType) throws NoMatchingDataException, EntityEmulationException {
        ArrayList<EntityDataRecord> filteredResults = new ArrayList();
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        int schemaDetailId;
        
        try {
          
            String selectEntityIDCommand = "Select min(schemaId) as SchemaDetailId from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType + "\" and elementName = \""+entityValueType+"\"";
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectEntityIDCommand);
            schemaDetailId = rs.getInt("SchemaDetailId");
            rs.close();
            stmt.close();            
            EntityEmulationRepository.releaseConnection(conn);            
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }            

        // gather the elements
        ArrayList<EntityDataRecord> allResults = getFilteredEntityElements(entityDataType, filters);
        for (EntityDataRecord result : allResults){
            if (result.getSchemaDetailId() == schemaDetailId){
                filteredResults.add(result);
            }
        }
        
        
        if (filteredResults.size() == 0) {
            throw new NoMatchingDataException("No Data was available that matched the request.");
        }

        return filteredResults;
    }    
    
    
    
   /**
     * Return the entity id elements which match filter specifications.
     * @param entityDataType
     * @param filters
     * @return
     * @throws NoMatchingDataException
     * @throws EntityEmulationException 
     */
    private static String getDefaultEntityIdElementValue(EntityDataType entityDataType, String entityElementName) throws NoMatchingDataException, EntityEmulationException {
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        int schemaDetailId;
        String valueResults = "";
        
        try {
          
            String selectEntityIDCommand = "Select min(schemaId) as SchemaDetailId from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType + "\" and elementName = \""+entityElementName+"\"";
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectEntityIDCommand);
            schemaDetailId = rs.getInt("SchemaDetailId");
            rs.close();
            stmt.close();    
            
            String selectDefaultValueCommand = "select value, valueStored from "+tMDDSchemaTableName+" where id = " + schemaDetailId;
            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectDefaultValueCommand);
            String defaultValue = rs.getString("value");
            String valueStored = rs.getString("valueStored");
            if (Boolean.valueOf(valueStored))valueResults = defaultValue;
            rs.close();
            stmt.close();               
            EntityEmulationRepository.releaseConnection(conn);            
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }            

        return valueResults;
    }    
        
    
    
    /**
     * Process data filters that utilize multiple distinct data elements
     *
     * @param entityDataType
     * @param meDataFilter
     * @throws NoMatchingDataException
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws EntityEmulationException
     */
    private static void processMultipleElementDataFilter(EntityDataType entityDataType, MultipleElementDataFilter meDataFilter) throws NoMatchingDataException, SQLException, ClassNotFoundException, EntityEmulationException, Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try{
            StringBuilder queryList = new StringBuilder();
            for (String element : meDataFilter.getFilteredItems()) {
                if (queryList.length() == 0) {
                    queryList.append("'").append(element).append("'");
                } else {
                    queryList.append(",'").append(element).append("'");
                }
            }

            HashMap<String, Integer> filterMap = new HashMap();

            String schemaIdCommand = "select distinct elementName, schemaId from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType + "\" and elementName in (" + queryList.toString() + ")";

            StringBuilder schemaIdQueryList = new StringBuilder();
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(schemaIdCommand);
            while (rs.next()) {
                filterMap.put(rs.getString("elementName"), rs.getInt("schemaId"));
                if (schemaIdQueryList.length() == 0) {
                    schemaIdQueryList.append(rs.getInt("schemaId"));
                } else {
                    schemaIdQueryList.append(",").append(rs.getInt("schemaId"));
                }
            }
            rs.close();
            stmt.close();

            if (schemaIdQueryList.length() == 0) {
                throw new NoMatchingDataException("No Data was available that matched the request.  The " + queryList.toString() + " filters are not supported by the " + entityDataType + " entity.");
            } else {
                ArrayList<EntityDataRecord> matchingResults = new ArrayList();
                String selectElementsCommand = "Select EntityIndex, EntityElement, SchemaDetailId, EntityElementValue from " + entityDataType.name() + " where SchemaDetailId in (" + schemaIdQueryList.toString() + ") order by EntityIndex, SchemaDetailId, EntityElement asc";
                stmt = conn.createStatement();
                rs = stmt.executeQuery(selectElementsCommand);
                while (rs.next()) {
                    EntityDataRecord thisRecord = new EntityDataRecord();
                    int ii = rs.getInt("EntityIndex");
                    thisRecord.setEntityIndex(ii);
                    thisRecord.setEntityElement(rs.getString("EntityElement"));
                    thisRecord.setSchemaDetailId(rs.getInt("SchemaDetailId"));
                    thisRecord.setEntityElementValue(rs.getString("EntityElementValue"));
                    matchingResults.add(thisRecord);
                }
                rs.close();
                stmt.close();
                meDataFilter.setFilterItems(matchingResults, filterMap);
            }
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw ex;
        } catch (SQLException ex) {
            throw ex;
        } catch (NoMatchingDataException ex) {
            throw ex;
        } catch (EntityEmulationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ex;
        } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }
    }

    /**
     * Process simple data filters
     *
     * @param entityDataType
     * @param dataFilter
     * @throws NoMatchingDataException
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws EntityEmulationException
     */
    private static void processDataFilter(EntityDataType entityDataType, DataFilter dataFilter) throws NoMatchingDataException, SQLException, ClassNotFoundException, EntityEmulationException, Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try{
            String schemaIdCommand = "select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType + "\" and elementName = \"" + dataFilter.getFilteredItem() + "\"";
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(schemaIdCommand);
            int schemaId = 0;
            if (rs.isBeforeFirst()) {
                schemaId = rs.getInt(1);
            } else {
                rs.close();
                stmt.close();
                throw new NoMatchingDataException("No Data was available that matched the request.  The " + dataFilter.getFilteredItem() + " filter is not supported by the " + entityDataType + " entity.");

            }
            rs.close();
            stmt.close();

            String valueDetailCommand = "select BaseType, enumeration from TMDDMessageElementDefinitionQuery where Message = \"" + entityDataType.relatedMessage() + "\" and schemaId= " + schemaId;
//            System.out.println(schemaIdCommand);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(valueDetailCommand);
            String baseType = "";
            String enumeration = "";
            if (rs.isBeforeFirst()) {
                baseType = rs.getString("BaseType");
                enumeration = rs.getString("enumeration");
            } else {
                rs.close();
                stmt.close();
                throw new NoMatchingDataException("No Data was available that matched the request.  The " + dataFilter.getFilteredItem() + " filter is not supported by the " + entityDataType + " entity.");

            }
            rs.close();
            stmt.close();

            DataFilter.BaseType elementType = DataFilter.BaseType.valueOf(baseType.toUpperCase());
            dataFilter.setFilterItemIdentifier(schemaId, elementType, enumeration);
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw ex;
        } catch (SQLException ex) {
            throw ex;
        } catch (NoMatchingDataException ex) {
            throw ex;
        } catch (EntityEmulationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ex;
        } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }
    }
    
    private static int getNumberOfEntitiesInDataType(EntityDataType entityDataType) throws NoMatchingDataException, EntityEmulationException {
        int result = 0;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {

            String schemaIdCommand = "select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType + "\" and elementName = \"EntityId\"";
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(schemaIdCommand);
            int schemaId = 0;
            if (rs.isBeforeFirst()) {
                schemaId = rs.getInt(1);
            } else {
                rs.close();
                stmt.close();
                throw new NoMatchingDataException("No Data was available that matched the request.");
            }
            rs.close();
            stmt.close();

            String selectFilteredElementsCommand = "select count(*) from " + entityDataType.name() + " where SchemaDetailId = " + schemaId;
            stmt = conn.createStatement();
//            System.out.println(selectFilteredElementsCommand);
            rs = stmt.executeQuery(selectFilteredElementsCommand);
            while (rs.next()) {
                result = rs.getInt(1);
            }
            rs.close();
            stmt.close();
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }

        return result;
    }

    private static String getMaxEntityIdInDataType(EntityDataType entityDataType) throws NoMatchingDataException, EntityEmulationException {
        String result = "";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {

            String schemaIdCommand = "select min(schemaId) from EntityEmulationElementLookupTable where entityDataType = \"" + entityDataType + "\" and elementName = \"EntityId\"";
            conn = EntityEmulationRepository.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(schemaIdCommand);
            int schemaId = 0;
            if (rs.isBeforeFirst()) {
                schemaId = rs.getInt(1);
            } else {
                rs.close();
                stmt.close();
                throw new NoMatchingDataException("No Data was available that matched the request.");
            }
            rs.close();
            stmt.close();

            String selectFilteredElementsCommand = "select max(CAST(EntityElementValue as INT)) from " + entityDataType.name() + " where SchemaDetailId = " + schemaId;
            stmt = conn.createStatement();
//            System.out.println(selectFilteredElementsCommand);
            rs = stmt.executeQuery(selectFilteredElementsCommand);
            while (rs.next()) {
                result = rs.getString(1);
            }
            rs.close();
            stmt.close();
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }

        return result;
    }
    
    
    public static String getEntityElementEnumerationValues(int schemaId) throws EntityEmulationException {
        String result = "";

        return result;
    }

    private static void reorderEntityElements(EntityDataType entityDataType) throws EntityEmulationException {
        Connection conn = null;
        ResultSet rs = null;
        
        try {
            String getMaxMinEntityIndexCommand = "Select max(EntityIndex) from " + entityDataType.name();
            conn = EntityEmulationRepository.getConnection();
			int maxEntityIndex;
            try (Statement stmt = conn.createStatement())
			{
				rs = stmt.executeQuery(getMaxMinEntityIndexCommand);
				maxEntityIndex = rs.getInt(1);
				rs.close();
			}

            if (maxEntityIndex > 1) {
                int firstMissingIndex = 1;
                boolean firstEmptyResult = false;
                for (int ii = 1; ii <= maxEntityIndex; ii++) {
                    String checkIndexCommand = "Select count(*) from " + entityDataType.name() + " where EntityIndex =" + ii;
                    try (Statement stmt = conn.createStatement())
                    {
                        rs = stmt.executeQuery(checkIndexCommand);
                        if (rs.getInt(1) == 0) 
                        {
                            if (!firstEmptyResult) 
                            {
                                firstEmptyResult = true;
                                firstMissingIndex = ii;
                            }
                        } 
                        else // Update the Index to the first missing index value
                        {
                            if (firstEmptyResult) 
                            {
                                String replacementString = "replace(replace(EntityElement,'tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "[" + ii + "]', 'tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "[" + firstMissingIndex + "]'),'tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + ".', 'tmdd:" + entityDataType.relatedMessage() + "." + entityDataType.entityItemReference() + "[" + firstMissingIndex + "].')";

                                String updateEntityIndexCommand = "Update " + entityDataType.name() + " set EntityIndex = \"" + firstMissingIndex + "\", EntityElement = " + replacementString + " where EntityIndex = " + ii;
    //                            System.out.println(updateEntityIndexCommand);
                                try (Statement oUpdateStmt = conn.createStatement())
                                {
                                        oUpdateStmt.execute(updateEntityIndexCommand);
                                }

                                conn.commit();

                                firstMissingIndex = firstMissingIndex + 1;
                            }
                        }
                    }
                }

            }
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally {
            try{
                if (rs != null) rs.close();   
            } catch (Exception ex1){
            }
            
            try{
                if (conn != null) EntityEmulationRepository.releaseConnection(conn);
            } catch (Exception ex1){                
            }
        }
    }

}
