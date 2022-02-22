/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.fhwa.c2cri.applayer.ApplicationLayerOperationResults;
import org.fhwa.c2cri.applayer.MessageContentGenerator;
import org.fhwa.c2cri.applayer.MessageUpdateListener;
import org.fhwa.c2cri.centermodel.EmulationDataFileProcessor;
import org.fhwa.c2cri.centermodel.RIEmulation;
import org.fhwa.c2cri.centermodel.RIEmulationEntityValueSet;
import org.fhwa.c2cri.centermodel.RIEmulationParameters;
import org.fhwa.c2cri.infolayer.MessageProvider;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType;

/**
 *
 * @author TransCore ITS, LLC
 */
public class TestTMDDEntityIDPropagation {
    
    static int entityValueOffset = 10;    
    static EventCheckDefinition[] eventAddSequence = new EventCheckDefinition[]{new EventCheckDefinition(EntityDataType.EVENTSACTIONLOG,"10"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSINDEX,"Event-11"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSUPDATES,"12"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSINDEX,"Event-21")};
    
    static EventCheckDefinition[] eventAddCheckSequence = new EventCheckDefinition[]{new EventCheckDefinition(EntityDataType.EVENTSACTIONLOG,"10"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSINDEX,"StateDOTEventRef"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSUPDATES,"6"),
        
                                                                                new EventCheckDefinition(EntityDataType.EVENTSINDEX,"Event-11"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSUPDATES,"7"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSACTIONLOG,"11"),


                                                                                new EventCheckDefinition(EntityDataType.EVENTSUPDATES,"12"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSINDEX,"StateDOTEventRef"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSACTIONLOG,"10"),


                                                                                new EventCheckDefinition(EntityDataType.EVENTSINDEX,"Event-21"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSUPDATES,"13"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSACTIONLOG,"12")};

    static EventCheckDefinition[] eventUpdateSequence = new EventCheckDefinition[]{new EventCheckDefinition(EntityDataType.EVENTSACTIONLOG,"10","14"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSINDEX,"Event-11", "Event-111"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSUPDATES,"12","22"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSINDEX,"StateDOTEventRef","StateDOTEventRefUpdate")};
    
    
    static EventCheckDefinition[] eventUpdateCheckSequence = new EventCheckDefinition[]{new EventCheckDefinition(EntityDataType.EVENTSACTIONLOG,"14"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSINDEX,"Event-111"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSUPDATES,"22"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSINDEX,"StateDOTEventRefUpdate")};

    static EventCheckDefinition[] eventDeleteSequence = new EventCheckDefinition[]{new EventCheckDefinition(EntityDataType.EVENTSACTIONLOG,"14"),
//                                                                                new EventCheckDefinition(EntityDataType.EVENTSINDEX,"StateDOTEventRefUpdate"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSUPDATES,"22"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSINDEX,"Event-111")};
    
    static EventCheckDefinition[] eventDeleteCheckSequence = new EventCheckDefinition[]{new EventCheckDefinition(EntityDataType.EVENTSACTIONLOG,"14"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSINDEX,"StateDOTEventRefUpdate"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSUPDATES,"22"),
                                                                                new EventCheckDefinition(EntityDataType.EVENTSINDEX,"Event-111")};
    
    
    
    public static void main(String[] args){
        
        String entityFilePath = "C:\\C2CRIDev\\C2CRIBuildDir\\projects\\C2C-RI\\src\\TMDDv31\\src\\InfoLayer\\EmulationData";
        
        RIEmulation.getInstance().setEmulationEnabled(true);
        TestTMDDDialogListener tmddListener = new TestTMDDDialogListener();
        RIEmulation.getInstance().setEmulationContentGenerator(tmddListener);
        
        RIEmulationParameters testParameters = new RIEmulationParameters();     
        testParameters.setCommandQueueLength(0);
        ArrayList<RIEmulationEntityValueSet> theList = new ArrayList();
        for (EntityEmulationData.EntityDataType entityType : EntityEmulationData.EntityDataType.values()){
            RIEmulationEntityValueSet testSet = new RIEmulationEntityValueSet();
            theList.add(testSet);
            testParameters.setEntityDataMap(theList);                
            testSet.setValueSetName(entityType.name());
            try{
                testSet.setEntityDataSet(EmulationDataFileProcessor.getByteArray(Paths.get(entityFilePath, entityType.name()).toString()));
            } catch (Exception ex){
                System.err.println("Unable to load entity "+entityType.name());
                ex.printStackTrace();
            }
        }   
        
        try{
            RIEmulation.getInstance().initialize("TMDD v3.1", testParameters);        
            
            // CCTV
            if (testEntitySet(new EntityDataType[]{EntityDataType.CCTVINVENTORY, EntityDataType.CCTVSTATUS})){
                System.out.println("CCTV ENTITY TESTING PASSED!!");
            } else {
                System.out.println("***CCTV ENTITY TESTING FAILED!!***");                
            }
            
            // Detector
            if (testEntitySet(new EntityDataType[]{EntityDataType.DETECTORDATA, EntityDataType.DETECTORINVENTORY,EntityDataType.DETECTORMAINTENANCEHISTORY, EntityDataType.DETECTORDATA})){
                System.out.println("DETECTOR ENTITY TESTING PASSED!!");
            } else {
                System.out.println("***DETECTOR ENTITY TESTING FAILED!!***");                
            }            

            // DMS
            if (testEntitySet(new EntityDataType[]{EntityDataType.DMSINVENTORY, EntityDataType.DMSSTATUS,EntityDataType.DMSMESSAGEAPPEARANCE, EntityDataType.DMSMESSAGEINVENTORY, EntityDataType.DMSFONTTABLE})){
                System.out.println("DMS ENTITY TESTING PASSED!!");
            } else {
                System.out.println("***DMS ENTITY TESTING FAILED!!***");                
            }                      
            // ESS
            if (testEntitySet(new EntityDataType[]{EntityDataType.ESSINVENTORY, EntityDataType.ESSSTATUS,EntityDataType.ESSOBSERVATIONMETADATA, EntityDataType.ESSOBSERVATIONREPORT})){
                System.out.println("ESS ENTITY TESTING PASSED!!");
            } else {
                System.out.println("***ESS ENTITY TESTING FAILED!!***");                
            }                         
            // Events
            if (testEventsEntitySet(new EntityDataType[]{EntityDataType.EVENTSACTIONLOG, EntityDataType.EVENTSINDEX,EntityDataType.EVENTSUPDATES})){
                System.out.println("EVENTS ENTITY TESTING PASSED!!");
            } else {
                System.out.println("***EVENTS ENTITY TESTING FAILED!!***");                
            }                           
            // Gate
            if (testEntitySet(new EntityDataType[]{EntityDataType.GATEINVENTORY, EntityDataType.GATESTATUS})){
                System.out.println("GATE ENTITY TESTING PASSED!!");
            } else {
                System.out.println("***GATE ENTITY TESTING FAILED!!***");                
            }                           
            // HAR
            if (testEntitySet(new EntityDataType[]{EntityDataType.HARINVENTORY, EntityDataType.HARSTATUS,EntityDataType.HARCONTROLSCHEDULE, EntityDataType.HARMESSAGEINVENTORY})){
                System.out.println("HAR ENTITY TESTING PASSED!!");
            } else {
                System.out.println("***HAR ENTITY TESTING FAILED!!***");                
            }                           
            // IntersectionSignal
            if (testEntitySet(new EntityDataType[]{EntityDataType.INTERSECTIONSIGNALINVENTORY, EntityDataType.INTERSECTIONSIGNALSTATUS,EntityDataType.INTERSECTIONSIGNALCONTROLSCHEDULE, EntityDataType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORY})){
                System.out.println("INTERSECTION SIGNAL ENTITY TESTING PASSED!!");
            } else {
                System.out.println("***INTERSECTION SIGNAL ENTITY TESTING FAILED!!***");                
            }                           
            // LCS
            if (testEntitySet(new EntityDataType[]{EntityDataType.LCSINVENTORY, EntityDataType.LCSSTATUS,EntityDataType.LCSCONTROLSCHEDULE})){
                System.out.println("LCS ENTITY TESTING PASSED!!");
            } else {
                System.out.println("***LCS ENTITY TESTING FAILED!!***");                
            }                           
            // Link
            if (testEntitySet(new EntityDataType[]{EntityDataType.LINKINVENTORY, EntityDataType.LINKSTATUS})){
                System.out.println("LINK ENTITY TESTING PASSED!!");
            } else {
                System.out.println("***LINK ENTITY TESTING FAILED!!***");                
            }                           
            // Node
            if (testEntitySet(new EntityDataType[]{EntityDataType.NODEINVENTORY, EntityDataType.NODESTATUS})){
                System.out.println("NODE ENTITY TESTING PASSED!!");
            } else {
                System.out.println("***NODE ENTITY TESTING FAILED!!***");                
            }                           
            // Ramp
            if (testEntitySet(new EntityDataType[]{EntityDataType.RAMPMETERINVENTORY, EntityDataType.RAMPMETERSTATUS,EntityDataType.RAMPMETERCONTROLSCHEDULE, EntityDataType.RAMPMETERPLANINVENTORY})){
                System.out.println("RAMP METER ENTITY TESTING PASSED!!");
            } else {
                System.out.println("***RAMP METER ENTITY TESTING FAILED!!***");                
            }                           
            // Section
            if (testEntitySet(new EntityDataType[]{EntityDataType.SECTIONSTATUS,EntityDataType.SECTIONCONTROLSCHEDULE, EntityDataType.SECTIONSIGNALTIMINGPATTERNINVENTORY})){
                System.out.println("SECTION ENTITY TESTING PASSED!!");
            } else {
                System.out.println("***SECTION ENTITY TESTING FAILED!!***");                
            }                           
            // VideoSwitch
            if (testEntitySet(new EntityDataType[]{EntityDataType.VIDEOSWITCHINVENTORY, EntityDataType.VIDEOSWITCHSTATUS})){
                System.out.println("VIDEO SWITCH ENTITY TESTING PASSED!!");
            } else {
                System.out.println("***VIDEO SWITCH ENTITY TESTING FAILED!!***");                
            }                           
            
            tmddListener.printResults();
            
        } catch (Exception ex){
                System.err.println("Unable to Complete the test: "+ex.getMessage());
                ex.printStackTrace();            
        }
        
    }
    
    private static boolean testEntitySet(EntityEmulationData.EntityDataType[] entityArray){
        boolean addPassed = testAddEntity(entityArray);
        boolean updatePassed = testUpdateEntityElement(entityArray);
        boolean deletePassed = testDeleteEntity(entityArray);
       return addPassed && updatePassed && deletePassed;
    }
    
    private static boolean testAddEntity(EntityEmulationData.EntityDataType[] entityArray){
        boolean precheckPassed = true;
        boolean additionsPassed = true;
        boolean postcheckPassed = true;
        
        // Verify that the identifiers don't already exist in the database.
        for (int ii = 0; ii<= entityArray.length; ii++){            
            try{
                RIEmulation.getInstance().getEntityElementValue(entityArray[ii].name(), createEntityName(entityArray[ii].name()+"-"+ii), lookupEntityIdElement(entityArray[ii]));
                precheckPassed = false;
            } catch (Exception ex){
                // They all should fail with exceptions.
            }
            
        }
        
        if (precheckPassed){
            for (int ii = 0; ii< entityArray.length; ii++){            
                try{

                    RIEmulation.getInstance().addEntity(entityArray[ii].name(), createEntityName(entityArray[ii].name()+"-"+ii));       
                    System.out.println("TestAddEntity: Succesfully added entity " +createEntityName(entityArray[ii].name()+"-"+ii));

                } catch (Exception ex){
                    additionsPassed = false;

                    if (entityArray[ii].equals(EntityEmulationData.EntityDataType.EVENTSACTIONLOG)||entityArray[ii].equals(EntityEmulationData.EntityDataType.EVENTSUPDATES)){
                        System.err.println("Unable to add entity "+entityArray[ii].name()+" "+String.valueOf(ii+20) + ": "+ex.getMessage());
                    } else {
                        System.err.println("Unable to add entity "+createEntityName(entityArray[ii].name()+"-"+ii) + ": "+ex.getMessage());
                    
                    }
//                    ex.printStackTrace();                   
                }            
            }           
        }
        
        if (additionsPassed){
            
            
            
            // Verify that the identifiers now exist in the database.
            for (int ii = 0; ii< entityArray.length; ii++){            
                try{
                    // Verify that the new entities now exist in all related data type tables.
                    for (int jj = 0; jj< entityArray.length; jj++){                        

                        String resultValue = RIEmulation.getInstance().getEntityElementValue(entityArray[jj].name(), createEntityName(entityArray[ii].name()+"-"+ii), lookupEntityIdElement(entityArray[jj]));                                                            

                    }
                } catch (Exception ex){
                    // None should fail
                    ex.printStackTrace();
                    postcheckPassed = false;
                }
            
            }        
        }
        return precheckPassed && additionsPassed && postcheckPassed;
    }
    

    
    private static boolean testUpdateEntityElement(EntityEmulationData.EntityDataType[] entityArray){
        boolean precheckPassed = true;
        boolean updatesPassed = true;
        boolean postcheckPassed = true;
        
        // Verify that the identifiers don't already exist in the database.
        for (int ii = 0; ii< entityArray.length; ii++){            
            try{

                RIEmulation.getInstance().getEntityElementValue(entityArray[ii].name(), createEntityName(entityArray[ii].name()+"-"+(ii+entityValueOffset)), lookupEntityIdElement(entityArray[ii]));                    

                precheckPassed = false;
            } catch (Exception ex){
                // They all should fail with exceptions.
            }
            
        }             
        
        if (precheckPassed){
            for (int ii = 0; ii< entityArray.length; ii++){            
                try{

                    RIEmulation.getInstance().updateEntityElement(entityArray[ii].name(), createEntityName(entityArray[ii].name()+"-"+ii), lookupEntityIdElement(entityArray[ii]), createEntityName(entityArray[ii].name()+"-"+(ii+entityValueOffset)));       
                    System.out.println("TestUpdateEntityElement: Succesfully updated entity " +createEntityName(entityArray[ii].name()+"-"+ii) +  " to "+ createEntityName(entityArray[ii].name()+"-"+(ii+entityValueOffset)));

                } catch (Exception ex){
                    updatesPassed = false;
                    System.err.println("Unable to update entity "+createEntityName(entityArray[ii].name()+"-"+ii)+  " to "+ createEntityName(entityArray[ii].name()+"-"+(ii+entityValueOffset)) + ": "+ex.getMessage());
//                    ex.printStackTrace();                   
                }            
            }           
        }
        
        if (updatesPassed){
            // Verify that the identifiers now exist in the database.
            for (int ii = 0; ii< entityArray.length; ii++){            
                try{
                    // Verify that the new entities now exist in all related data type tables.
                    for (int jj = 0; jj< entityArray.length; jj++){                        

                        RIEmulation.getInstance().getEntityElementValue(entityArray[jj].name(), createEntityName(entityArray[ii].name()+"-"+(ii+entityValueOffset)), lookupEntityIdElement(entityArray[jj]));

                    }
                } catch (Exception ex){
                    // None should fail
                    postcheckPassed = false;
                }
            
            }        
        }
        return precheckPassed && updatesPassed && postcheckPassed;        
    }
    
    private static boolean testDeleteEntity(EntityEmulationData.EntityDataType[] entityArray){
        boolean precheckPassed = true;
        boolean deletionsPassed = true;
        boolean postcheckPassed = true;
        
        // Verify that the identifiers already exist in the database.
        for (int ii = 0; ii< entityArray.length; ii++){            
            try{

                RIEmulation.getInstance().getEntityElementValue(entityArray[ii].name(), createEntityName(entityArray[ii].name()+"-"+(ii+entityValueOffset)), lookupEntityIdElement(entityArray[ii]));                    

            } catch (Exception ex){
                precheckPassed = false;
            }
            
        }
        
        if (precheckPassed){
            for (int ii = 0; ii< entityArray.length; ii++){            
                try{

                    RIEmulation.getInstance().deleteEntity(entityArray[ii].name(), createEntityName(entityArray[ii].name()+"-"+(ii+entityValueOffset)));       
                    System.out.println("TestDeleteEntity: Succesfully deleted entity " +createEntityName(entityArray[ii].name()+"-"+(ii+entityValueOffset)));                    

                } catch (Exception ex){
                    deletionsPassed = false;
                    if (entityArray[ii].equals(EntityEmulationData.EntityDataType.EVENTSACTIONLOG)||entityArray[ii].equals(EntityEmulationData.EntityDataType.EVENTSUPDATES)){
                        System.err.println("Unable to delete entity "+entityArray[ii].name()+" " +String.valueOf(ii+entityValueOffset) + ": "+ex.getMessage());                        
                    } else {
                        System.err.println("Unable to delete entity "+createEntityName(entityArray[ii].name()+"-"+(ii+entityValueOffset)) + ": "+ex.getMessage());                        
                    }
 //                   ex.printStackTrace();                   
                }            
            }           
        }
        
        if (deletionsPassed){
            // Verify that the identifiers no longer exist in the database.
            for (int ii = 0; ii< entityArray.length; ii++){            
                try{
                    // Verify that the new entities now exist in all related data type tables.
                    for (int jj = 0; jj< entityArray.length; jj++){                        

                        RIEmulation.getInstance().getEntityElementValue(entityArray[jj].name(), createEntityName(entityArray[ii].name()+"-"+(ii+entityValueOffset)), lookupEntityIdElement(entityArray[jj]));                            

                        postcheckPassed = false;
                    }
                } catch (Exception ex){
                    // All should fail

                }
            
            }        
        }
        return precheckPassed && deletionsPassed && postcheckPassed;        
    }    
    
    
    private static boolean testEventsEntitySet(EntityEmulationData.EntityDataType[] entityArray){
        boolean addPassed = testEventsAddEntity(entityArray);
        boolean updatePassed = testEventsUpdateEntityElement(entityArray);
        boolean deletePassed = testEventsDeleteEntity(entityArray);
       return addPassed && updatePassed && deletePassed;
    }
    
    private static boolean testEventsAddEntity(EntityEmulationData.EntityDataType[] entityArray){
        boolean precheckPassed = true;
        boolean additionsPassed = true;
        boolean postcheckPassed = true;
        
        // Verify that the identifiers don't already exist in the database.
        for (EventCheckDefinition preAddCheck : eventAddCheckSequence){            
            try{
                RIEmulation.getInstance().getEntityElementValue(preAddCheck.getDataType().name(), preAddCheck.getEntityId(), lookupEntityIdElement(preAddCheck.getDataType()));
                precheckPassed = false;
            } catch (Exception ex){
                // They all should fail with exceptions.
            }
            
        }
        
        if (precheckPassed){
            for (EventCheckDefinition addCheck : eventAddSequence){            
                try{

                    RIEmulation.getInstance().addEntity(addCheck.getDataType().name(), addCheck.getEntityId());       
                    System.out.println("TestAddEntity: Succesfully added entity " +addCheck.getEntityId());

                } catch (Exception ex){
                    additionsPassed = false;


                    System.err.println("Unable to add entity "+addCheck.getEntityId() + ": "+ex.getMessage());
                    

//                    ex.printStackTrace();                   
                }            
            }           
        }
        
        if (additionsPassed){
            
            
            
            // Verify that the identifiers now exist in the database.
            for (EventCheckDefinition postAddCheck : eventAddCheckSequence){            
                try{
                    // Verify that the new entities now exist in all related data type tables.

                    String resultValue = RIEmulation.getInstance().getEntityElementValue(postAddCheck.getDataType().name(), postAddCheck.getEntityId(), lookupEntityIdElement(postAddCheck.getDataType()));                                                            

                } catch (Exception ex){
                    // None should fail
//                    ex.printStackTrace();
                    System.err.println("Failed to add entity postCheck:  "+postAddCheck.getDataType().name()+" "+postAddCheck.getEntityId() + ": "+ex.getMessage());
                    postcheckPassed = false;
                }
            
            }        
        }
        return precheckPassed && additionsPassed && postcheckPassed;
    }
    

    
    private static boolean testEventsUpdateEntityElement(EntityEmulationData.EntityDataType[] entityArray){
        boolean precheckPassed = true;
        boolean updatesPassed = true;
        boolean postcheckPassed = true;
        
        // Verify that the identifiers don't already exist in the database.
        for (EventCheckDefinition preUpdateCheck : eventUpdateCheckSequence){            
            try{

                RIEmulation.getInstance().getEntityElementValue(preUpdateCheck.getDataType().name(), preUpdateCheck.getEntityId(), lookupEntityIdElement(preUpdateCheck.getDataType()));                    

                precheckPassed = false;
            } catch (Exception ex){
                // They all should fail with exceptions.
            }
            
        }             
        
        if (precheckPassed){
            for (EventCheckDefinition updateSequence : eventUpdateSequence){            
                try{

                    RIEmulation.getInstance().updateEntityElement(updateSequence.getDataType().name(), updateSequence.getPriorEntityId(), lookupEntityIdElement(updateSequence.getDataType()), updateSequence.getEntityId());       
                    System.out.println("TestUpdateEntityElement: Succesfully updated entity " +updateSequence.getPriorEntityId() +  " to "+ updateSequence.getEntityId());

                } catch (Exception ex){
                    updatesPassed = false;
                    System.err.println("Unable to update entity "+updateSequence.getPriorEntityId()+  " to "+ updateSequence.getEntityId() + ": "+ex.getMessage());
//                    ex.printStackTrace();                   
                }            
            }           
        }
        
        if (updatesPassed){
            // Verify that the identifiers now exist in the database.      
            for (EventCheckDefinition updateCheckSequence : eventUpdateCheckSequence){            
                try{
                    // Verify that the new entities now exist in all related data type tables.

                    RIEmulation.getInstance().getEntityElementValue(updateCheckSequence.getDataType().name(), updateCheckSequence.getEntityId(), lookupEntityIdElement(updateCheckSequence.getDataType()));

                } catch (Exception ex){
                    // None should fail
                    postcheckPassed = false;
                }
            
            }        
        }
        return precheckPassed && updatesPassed && postcheckPassed;        
    }
    
    private static boolean testEventsDeleteEntity(EntityEmulationData.EntityDataType[] entityArray){
        boolean precheckPassed = true;
        boolean deletionsPassed = true;
        boolean postcheckPassed = true;
        
        // Verify that the identifiers already exist in the database.         
        for (EventCheckDefinition deleteCheckSequence : eventDeleteCheckSequence){            
            try{

                RIEmulation.getInstance().getEntityElementValue(deleteCheckSequence.getDataType().name(), deleteCheckSequence.getEntityId(), lookupEntityIdElement(deleteCheckSequence.getDataType()));                    

            } catch (Exception ex){
                precheckPassed = false;
            }
            
        }
        
        if (precheckPassed){      
            for (EventCheckDefinition deleteSequence : eventDeleteSequence){            
                try{

                    RIEmulation.getInstance().deleteEntity(deleteSequence.getDataType().name(), deleteSequence.getEntityId());       
                    System.out.println("TestDeleteEntity: Succesfully deleted entity " +deleteSequence.getEntityId());                    

                } catch (Exception ex){
                    deletionsPassed = false;

                    System.err.println("Unable to delete "+deleteSequence.getDataType().name()+" entity "+deleteSequence.getEntityId() + ": "+ex.getMessage());                        

 //                   ex.printStackTrace();                   
                }            
            }           
        }
        
        if (deletionsPassed){
            // Verify that the identifiers no longer exist in the database.
            for (EventCheckDefinition deleteCheckSequence : eventDeleteCheckSequence){            
                try{
                    // Verify that the new entities now exist in all related data type tables.

                    RIEmulation.getInstance().getEntityElementValue(deleteCheckSequence.getDataType().name(), deleteCheckSequence.getEntityId(), lookupEntityIdElement(deleteCheckSequence.getDataType()));                            
                    postcheckPassed = false;

                } catch (Exception ex){
                    // All should fail

                }
            
            }        
        }
        return precheckPassed && deletionsPassed && postcheckPassed;        
    }    
    
    private static String createEntityName(String entityName){
        if (entityName.length() > 32){
            return entityName.substring(entityName.length()-32);
        }
        return entityName;
    }
    
    private static String lookupEntityIdElement(EntityEmulationData.EntityDataType entityType) throws Exception{
        String elementName = "";
        switch (entityType){
            case CCTVINVENTORY:  elementName = "tmdd:cCTVInventoryMsg.cctv-inventory-item.device-inventory-header.device-id"; break;
            case CCTVSTATUS:  elementName = "tmdd:cCTVStatusMsg.cctv-status-item.device-status-header.device-id"; break;
            case ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA:  elementName = "tmdd:archivedDataProcessingDocumentationMetadataMsg.archived-data-processing-documentation-metadata-item.processing-metadata-name"; break;
            case DETECTORDATA:  elementName = "tmdd:detectorDataMsg.detector-data-item.detector-data-list.detector-data-detail.detector-id"; break;
            case DETECTORINVENTORY:  elementName = "tmdd:detectorInventoryMsg.detector-inventory-item.detector-inventory-list.detector.detector-inventory-header.device-id"; break;
            case DETECTORMAINTENANCEHISTORY:  elementName = "tmdd:detectorMaintenanceHistoryMsg.detector-maintenance-history-item.detector-history-list.detector.detector-id"; break;
            case DETECTORSTATUS:  elementName = "tmdd:detectorStatusMsg.detector-status-item.detector-status-list.detector.detector-status-header.device-id"; break;
            case DMSFONTTABLE:  elementName = "tmdd:dMSFontTableMsg.dms-font-table-item.device-id"; break;
            case DMSINVENTORY:  elementName = "tmdd:dMSInventoryMsg.dms-inventory-item.device-inventory-header.device-id"; break;
            case DMSMESSAGEAPPEARANCE:  elementName = "tmdd:dMSMessageAppearanceMsg.device-id"; break;
            case DMSMESSAGEINVENTORY:  elementName = "tmdd:dMSMessageInventoryMsg.dms-message-inventory-item.device-id"; break;
            case DMSSTATUS:  elementName = "tmdd:dMSStatusMsg.dms-status-item.device-status-header.device-id"; break;
            case ESSINVENTORY:  elementName = "tmdd:eSSInventoryMsg.ess-inventory-item.device-inventory-header.device-id"; break;
            case ESSOBSERVATIONMETADATA:  elementName = "tmdd:eSSObservationMetadataMsg.ess-observation-metadata-item.ess-observation-data-set-metadata.ess-data-set-file-name"; break;
            case ESSOBSERVATIONREPORT:  elementName = "tmdd:eSSObservationReportMsg.ess-observation-report-item.station-id"; break;
            case ESSSTATUS:  elementName = "tmdd:eSSStatusMsg.ess-status-item.ess-station-status-header.device-id"; break;
            case EVENTSACTIONLOG:  elementName = "tmdd:actionLogMsg.log-entry.action-log-element-id"; break;
            case EVENTSINDEX:  elementName = "tmdd:eventIndexMsg.eventIndex.event-id"; break;
            case EVENTSUPDATES:  elementName = "tmdd:fEUMsg.FEU.message-header.message-number"; break;
            case GATECONTROLSCHEDULE:  elementName = "tmdd:gateControlScheduleMsg.gate-control-schedule-item.device-control-schedule-header.device-id"; break;
            case GATEINVENTORY:  elementName = "tmdd:gateInventoryMsg.gate-inventory-item.device-inventory-header.device-id"; break;
            case GATESTATUS:  elementName = "tmdd:gateStatusMsg.gate-status-item.device-status-header.device-id"; break;
            case HARCONTROLSCHEDULE:  elementName = "tmdd:hARControlScheduleMsg.har-control-schedule-item.device-control-schedule-header.device-id"; break;
            case HARINVENTORY:  elementName = "tmdd:hARInventoryMsg.har-inventory-item.device-inventory-header.device-id"; break;
            case HARMESSAGEINVENTORY:  elementName = "tmdd:hARMessageInventoryMsg.har-message-inventory-item.device-id"; break;
            case HARSTATUS:  elementName = "tmdd:hARStatusMsg.har-status-item.device-status-header.device-id"; break;
            case INTERSECTIONSIGNALCONTROLSCHEDULE:  elementName = "tmdd:intersectionSignalControlScheduleMsg.intersection-signal-control-schedule-item.device-control-schedule-header.device-id"; break;
            case INTERSECTIONSIGNALINVENTORY:  elementName = "tmdd:intersectionSignalInventoryMsg.intersection-signal-inventory-item.device-inventory-header.device-id"; break;
            case INTERSECTIONSIGNALSTATUS:  elementName = "tmdd:intersectionSignalStatusMsg.intersection-signal-status-item.device-status-header.device-id"; break;
            case INTERSECTIONSIGNALTIMINGPATTERNINVENTORY:  elementName = "tmdd:intersectionSignalTimingPatternInventoryMsg.intersection-signal-timing-pattern-inventory-item.device-id"; break;
            case LCSCONTROLSCHEDULE:  elementName = "tmdd:lCSControlScheduleMsg.lcs-control-schedule-item.device-control-schedule-header.device-id"; break;
            case LCSINVENTORY:  elementName = "tmdd:lCSInventoryMsg.lcs-inventory-item.device-inventory-header.device-id"; break;
            case LCSSTATUS:  elementName = "tmdd:lCSStatusMsg.lcs-status-item.device-status-header.device-id"; break;
            case RAMPMETERCONTROLSCHEDULE:  elementName = "tmdd:rampMeterControlScheduleMsg.ramp-meter-control-schedule-item.device-control-schedule-header.device-id"; break;
            case RAMPMETERINVENTORY:  elementName = "tmdd:rampMeterInventoryMsg.ramp-meter-inventory-item.device-inventory-header.device-id"; break;
            case RAMPMETERPLANINVENTORY:  elementName = "tmdd:rampMeterPlanInventoryMsg.ramp-meter-plan-inventory-item.device-id"; break;
            case RAMPMETERSTATUS:  elementName = "tmdd:rampMeterStatusMsg.ramp-meter-status-item.device-status-header.device-id"; break;
            case SECTIONCONTROLSCHEDULE:  elementName = "tmdd:sectionControlScheduleMsg.section-control-schedule-item.node-id-list.device-id"; break;
            case SECTIONSIGNALTIMINGPATTERNINVENTORY:  elementName = "tmdd:sectionSignalTimingPatternInventoryMsg.signal-section-timing-pattern-inventory-item.section-id"; break;
            case SECTIONSTATUS:  elementName = "tmdd:sectionStatusMsg.section-status-item.node-id-list.device-id"; break;
            case VIDEOSWITCHINVENTORY:  elementName = "tmdd:videoSwitchInventoryMsg.video-switch-inventory-item.device-inventory-header.device-id"; break;
            case VIDEOSWITCHSTATUS:  elementName = "tmdd:videoSwitchStatusMsg.video-switch-status-item.device-status-header.device-id"; break;
            case CENTERACTIVEVERIFICATION:  elementName = "tmdd:centerActiveVerificationResponseMsg.center-id"; break;
            case LINKINVENTORY:  elementName = "tmdd:linkInventoryMsg.link-inventory-item.link-inventory-list.link.link-id"; break;
            case LINKSTATUS:  elementName = "tmdd:linkStatusMsg.link-status-item.link-status-list.link.link-id"; break;
            case NODEINVENTORY:  elementName = "tmdd:nodeInventoryMsg.node-inventory-item.node-inventory-list.node.node-id"; break;
            case NODESTATUS:  elementName = "tmdd:nodeStatusMsg.node-status-item.node-status-list.node.node-id"; break;            
            default:
                throw new Exception("No matching entity type found matching "+entityType.name());
        }
        return elementName;
    }
    
    static class EventCheckDefinition {
        private EntityEmulationData.EntityDataType dataType;
        private String entityId;
        private String priorEntityId;
        
        public EventCheckDefinition(EntityEmulationData.EntityDataType dataType, String entityId){
            this.dataType = dataType;
            this.entityId = entityId;           
            this.priorEntityId = "";
        }

        public EventCheckDefinition(EntityEmulationData.EntityDataType dataType, String priorEntityId, String entityId){
            this.dataType = dataType;
            this.entityId = entityId;
            this.priorEntityId = priorEntityId;
        }
        
        
        public EntityDataType getDataType() {
            return dataType;
        }

        public String getEntityId() {
            return entityId;
        }

        public String getPriorEntityId() {
            return priorEntityId;
        }                        
        
    }
    
    static class TestTMDDDialogListener implements MessageContentGenerator{
        HashMap<String,Integer> dialogUpdates = new HashMap();
        ArrayList<String> updateSequence = new ArrayList();
        
        Map<String, String> supportedDialogs = new HashMap<>();
        
        public TestTMDDDialogListener(){
        supportedDialogs.put("dlHARInventoryUpdate", "HAR");
        supportedDialogs.put("dlIntersectionSignalTimingPatternInventoryUpdate", "INTERSECTIONSIGNAL");
        supportedDialogs.put("dlActionLogUpdate", "EVENTS");
        supportedDialogs.put("dlESSObservationReportUpdate", "ESS");
        supportedDialogs.put("dlDetectorStatusUpdate", "DETECTOR");
        supportedDialogs.put("dlDetectorDataUpdate", "DETECTOR");
        supportedDialogs.put("dlIntersectionSignalControlScheduleUpdate", "INTERSECTIONSIGNAL");
        supportedDialogs.put("dlIntersectionSignalStatusUpdate", "INTERSECTIONSIGNAL");
        supportedDialogs.put("dlSectionSignalTimingPatternInventoryUpdate", "SECTION");
        supportedDialogs.put("dlDMSInventoryUpdate", "DMS");
        supportedDialogs.put("dlSectionStatusUpdate", "SECTION");
        supportedDialogs.put("dlESSStatusUpdate", "ESS");
        supportedDialogs.put("dlLCSInventoryUpdate", "LCS");
        supportedDialogs.put("dlCCTVStatusUpdate", "CCTV");
        supportedDialogs.put("dlRampMeterPlanInventoryUpdate", "RAMPMETER");
        supportedDialogs.put("dlDMSMessageInventoryUpdate", "DMS");
        supportedDialogs.put("dlSectionControlScheduleUpdate", "SECTION");
        supportedDialogs.put("dlCCTVInventoryUpdate", "CCTV");
        supportedDialogs.put("dlRampMeterStatusUpdate", "RAMPMETER");
        supportedDialogs.put("dlESSInventoryUpdate", "ESS");
        supportedDialogs.put("dlHARControlScheduleUpdate", "HAR");
        supportedDialogs.put("dlRampMeterInventoryUpdate", "RAMPMETER");
        supportedDialogs.put("dlVideoSwitchStatusUpdate", "VIDEOSWITCH");
        supportedDialogs.put("dlDetectorInventoryUpdate", "DETECTOR");
        supportedDialogs.put("dlHARMessageInventoryUpdate", "HAR");
        supportedDialogs.put("dlGateInventoryUpdate", "GATE");
        supportedDialogs.put("dlVideoSwitchInventoryUpdate", "VIDEOSWITCH");
        supportedDialogs.put("dlFullEventUpdateUpdate", "EVENTS");
        supportedDialogs.put("dlEventIndexUpdate", "EVENTS");
        supportedDialogs.put("dlGateStatusUpdate", "GATE");
        supportedDialogs.put("dlRampMeterControlScheduleUpdate", "RAMPMETER");
        supportedDialogs.put("dlIntersectionSignalInventoryUpdate", "INTERSECTIONSIGNAL");
        supportedDialogs.put("dlLCSStatusUpdate", "LCS");
        supportedDialogs.put("dlDMSStatusUpdate", "DMS");
        supportedDialogs.put("dlHARStatusUpdate", "HAR");
        supportedDialogs.put("dlLCSControlScheduleUpdate", "LCS");
        supportedDialogs.put("dlGateControlScheduleUpdate", "GATE");
        supportedDialogs.put("dlCenterActiveVerificationUpdate", "CAV");
        supportedDialogs.put("dlLinkInventoryUpdate", "LINK");
        supportedDialogs.put("dlLinkStatusUpdate", "LINK");
        supportedDialogs.put("dlNodeInventoryUpdate", "NODE");
        supportedDialogs.put("dlNodeStatusUpdate", "NODE");       
        }        
        
        
        @Override
        public String[] getApplicationStandardsSupported() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isMessageSupported(String arg0, String arg1, String arg2, MessageProvider arg3) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void registerMessageUpdateListener(MessageUpdateListener arg0, String arg1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void unregisterMessageUpdateListener(MessageUpdateListener arg0) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Message getMessage(String arg0, String arg1, String arg2) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Message getErrorMessage(String arg0, String arg1, String arg2) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Message getResponseMessage(String arg0, String arg1, String arg2, Message arg3, ApplicationLayerOperationResults arg4) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void operationRelatedDataUpdate(String arg0) {
            updateSequence.add(arg0);
            if (dialogUpdates.containsKey(arg0)){
                Integer currentCount = dialogUpdates.get(arg0);
                currentCount = currentCount + 1;
                dialogUpdates.put(arg0, currentCount);
            } else {
                Integer firstEntry = 1;
                dialogUpdates.put(arg0, firstEntry);
            }
        }
        
        public void printResults(){
            String[] entityTypes = new String[]{"CAV","CCTV",
                "DETECTOR", "DMS", "ESS", "EVENTS", "GATE", "HAR", "INTERSECTIONSIGNAL", "LCS",
                "LINK", "NODE", "RAMPMETER", "SECTION","VIDEOSWITCH"};
            
            
            System.out.println("\n\nDialog\t\t\tCount");            
            for (String entityType : entityTypes){
                for (HashMap.Entry<String,Integer> entry : dialogUpdates.entrySet()){
                    if (supportedDialogs.containsKey(entry.getKey())){
                        if (supportedDialogs.get(entry.getKey()).equals(entityType))
                            System.out.println(entityType + "-" +entry.getKey() + "\t\t\t: "+entry.getValue());
                    }
                }
                System.out.println("");
            }
            
            System.out.println("\n\n");
            for (String entityType : updateSequence){
                
                System.out.println(entityType);
            }            
        }
        
    }
}
