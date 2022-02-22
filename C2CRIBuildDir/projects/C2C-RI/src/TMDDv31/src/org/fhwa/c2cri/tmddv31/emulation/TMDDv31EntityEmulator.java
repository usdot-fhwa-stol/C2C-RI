/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmddv31.emulation;

import java.io.InputStream;
import java.util.ArrayList;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.fhwa.c2cri.centermodel.EmulationDataFileProcessor;
import org.fhwa.c2cri.centermodel.RIEmulationEntityValueSet;
import org.fhwa.c2cri.centermodel.RIEmulationParameters;
import org.fhwa.c2cri.centermodel.RIEmulator;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.tmdd.TMDDAuthenticationProcessor;
import org.fhwa.c2cri.tmdd.emulation.TMDDEntityEmulator;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataOrganizationInformationCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityContentException;

/**
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
@PluginImplementation
public class TMDDv31EntityEmulator
        extends TMDDEntityEmulator {

    @Override
    public String getDatabaseFileName() {
        return "TMDDv31SQLLite.db3";
    }

    @Override
    public InputStream getDatabaseStream() {
        return this.getClass().getResourceAsStream("/org/fhwa/c2cri/tmddv31/dbase/TMDDv31SQLLite.db3");
    }

    @Override
    protected String getTMDDEmulatorStandard() {
        return "TMDD v3.1"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTMDDSchemaDetailTableName(){
        return "TMDDv31SchemaDetail";
    };  
    
    public static void main(String[] args) throws Exception {
        boolean writeFile = false;
        boolean readFile = true;
        boolean updateValues = false;
        long startTime = System.currentTimeMillis();

        TMDDv31EntityEmulator emulator = new TMDDv31EntityEmulator();

        if (true) {
            java.io.File folder = new java.io.File("C:\\C2CRI-Phase2\\C2CRIBuildDir\\projects\\C2C-RI\\src\\TMDDv31\\src\\InfoLayer\\EmulationData");
            java.io.File[] listOfFiles = folder.listFiles();
            RIEmulationParameters testParameters = new RIEmulationParameters();
            testParameters.setCommandQueueLength(0);
            ArrayList<RIEmulationEntityValueSet> theList = new ArrayList();
            ArrayList<String> validationResults = new ArrayList();

            System.out.println("\n\n\n Load.....\n\n\n");
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    RIEmulationEntityValueSet testSet = new RIEmulationEntityValueSet();
                    testSet.setValueSetName(listOfFiles[i].getName());
                    testSet.setEntityDataSet(EmulationDataFileProcessor.getByteArray(listOfFiles[i].getPath()));
                    theList.add(testSet);
                    System.out.println("File " + listOfFiles[i].getName());
                } else if (listOfFiles[i].isDirectory()) {
                    System.out.println("Directory " + listOfFiles[i].getName());
                }
            }
            testParameters.setEntityDataMap(theList);
            System.out.println("\n\n\n Initialize.....\n\n\n");
            emulator.initialize(testParameters);

            System.out.println("\n\n\n Validate.....\n\n\n");
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile())
                    try {
                    emulator.validate(listOfFiles[i].getName());
                    validationResults.add(listOfFiles[i].getName() + " was successfully validated.");
                    System.out.println(listOfFiles[i].getName() + " was successfully validated.");
                } catch (InvalidEntityContentException ex) {
                    validationResults.add(listOfFiles[i].getName() + " has validation error " + ex.getMessage());
                    System.err.println(listOfFiles[i].getName() + " has validation error " + ex.getMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    validationResults.add(listOfFiles[i].getName() + " has validation error " + ex.getMessage());
                    System.err.println(listOfFiles[i].getName() + " has validation error " + ex.getMessage());
                }
            }

            testParameters = new RIEmulationParameters();
            testParameters.setCommandQueueLength(0);
            theList = new ArrayList();

            System.out.println("\n\n\n Re-Initialize.....\n\n\n");

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    RIEmulationEntityValueSet testSet = new RIEmulationEntityValueSet();
                    testSet.setValueSetName(listOfFiles[i].getName());
                    theList.add(testSet);
                    System.out.println("File " + listOfFiles[i].getName());
                } else if (listOfFiles[i].isDirectory()) {
                    System.out.println("Directory " + listOfFiles[i].getName());
                }
            }
            testParameters.setEntityDataMap(theList);
            emulator.initialize(testParameters);

            System.out.println("\n\n\n Final Results.....\n\n\n");
            System.out.println(validationResults.toString());

        } else {
            String[] fileLines = EmulationDataFileProcessor.getContent("ValidCCTV-deviceinformationRequestMsg.in").toString().split("\n");
            ArrayList<String> input = new ArrayList();
            for (int ii = 0; ii < fileLines.length; ii++) {
                input.add(fileLines[ii]);
            }
            MessageSpecification thisSpec = new MessageSpecification(input);

            System.out.println("Number of records in file = " + fileLines.length + " with message ");
//        for (String theName : thisSpec.getMessageTypes()) {
//            System.out.println(theName);
//        }
            System.out.println("*** File Reading took " + (System.currentTimeMillis() - startTime) + " ms!");
//        System.out.println(EmulationDataFileProcessor.getContent(EntityEmulationData.EntityDataType.CCTVINVENTORY + ".out").toString());

            RIEmulationParameters testParameters = new RIEmulationParameters();
            testParameters.setCommandQueueLength(0);
            ArrayList<RIEmulationEntityValueSet> theList = new ArrayList();
            RIEmulationEntityValueSet testSet = new RIEmulationEntityValueSet();
            theList.add(testSet);
            testParameters.setEntityDataMap(theList);

            if (!readFile) {
                testSet.setValueSetName(EntityEmulationData.EntityDataType.CCTVINVENTORY.name());
                emulator.initialize(testParameters);
            } else {
                testSet.setValueSetName(EntityEmulationData.EntityDataType.CCTVINVENTORY.name());
                testSet.setEntityDataSet(EmulationDataFileProcessor.getByteArray(EntityEmulationData.EntityDataType.CCTVINVENTORY + ".out"));
                emulator.initialize(testParameters);
            }

            System.out.println("*** CCTVInventory Initialized at " + (System.currentTimeMillis() - startTime) + " ms!");

            if (updateValues) {
                emulator.addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345674");
                emulator.addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345675");
                emulator.addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345676");
                emulator.addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345677");
                emulator.addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345678");
                emulator.addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345679");
                emulator.deleteEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345678");
                emulator.deleteEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345674");
                emulator.addEntityElement(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345675", "tmdd:cCTVInventoryMsg.cctv-inventory-item.cctv-image-list[2].cctv-image", "jpeg");
                emulator.updateEntityElement(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345675", "tmdd:cCTVInventoryMsg.cctv-inventory-item.cctv-image-list[2].cctv-image", "tiff");
                System.out.println(emulator.getEntityElementValue(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345675", "tmdd:cCTVInventoryMsg.cctv-inventory-item.cctv-image-list[2].cctv-image"));
                emulator.deleteEntityElement(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345675", "tmdd:cCTVInventoryMsg.cctv-inventory-item.cctv-image-list[2].cctv-image");
            }

            System.out.println("*** Update Values Completed after " + (System.currentTimeMillis() - startTime) + " ms!");

            TMDDAuthenticationProcessor.getInstance("string", "string", "string");
            Message requestMessage = MessageManager.getInstance().createMessage("dlCCTVInventoryRequest");
            requestMessage.setMessageSpecification(thisSpec);
            System.out.println("*** Prepped for Request after " + (System.currentTimeMillis() - startTime) + " ms!");
            Message responseMessage = emulator.getRRResponse("dlCCTVInventoryRequest", requestMessage);
            System.out.println("*** Request Message Processed after " + (System.currentTimeMillis() - startTime) + " ms!");
            for (String theName : responseMessage.getMessageSpecification().getMessageTypes()) {
                System.out.println("Response Message Type = " + theName + " with Size " + responseMessage.getMessageSpecification().getMessageSpecItems().size());
            }

//            MessageSpecification orgInfoRecords = emulator.cctvHandler.getEntityOrganizationInformation("cctv-123456789123456789012345676");
//            System.out.println("Number of Org Info Records = " + orgInfoRecords.getMessageSpec().size());
            fileLines = EmulationDataFileProcessor.getContent("ValidCCTV-deviceControlStatusRequestMsg.in").toString().split("\n");
            input = new ArrayList();
            for (int ii = 0; ii < fileLines.length; ii++) {
                input.add(fileLines[ii]);
            }
            thisSpec = new MessageSpecification(input);
            Message statusRequestMessage = MessageManager.getInstance().createMessage("dlDeviceControlStatusRequest");
            statusRequestMessage.setMessageSpecification(thisSpec);

            responseMessage = emulator.getRRResponse("dlDeviceControlStatusRequest", statusRequestMessage);
            for (String theName : responseMessage.getMessageSpecification().getMessageTypes()) {
                System.out.println("Status Response Message Type = " + theName + " with Size " + responseMessage.getMessageSpecification().getMessageSpecItems().size());
            }

            fileLines = EmulationDataFileProcessor.getContent("ValidCCTV-deviceControlRequestMsg-Lock.in").toString().split("\n");
            input = new ArrayList();
            for (int ii = 0; ii < fileLines.length; ii++) {
                input.add(fileLines[ii]);
            }
            thisSpec = new MessageSpecification(input);
            Message controlRequestMessage = MessageManager.getInstance().createMessage("dlCCTVControlRequest");
            controlRequestMessage.setMessageSpecification(thisSpec);

            responseMessage = emulator.getRRResponse("dlCCTVControlRequest", controlRequestMessage);
            for (String theName : responseMessage.getMessageSpecification().getMessageTypes()) {
                System.out.println("Control Status Response Message Type = " + theName + " with Size " + responseMessage.getMessageSpecification().getMessageSpecItems().size());
            }

            fileLines = EmulationDataFileProcessor.getContent("ValidCCTV-deviceCancelControlRequestMsg.in").toString().split("\n");
            input = new ArrayList();
            for (int ii = 0; ii < fileLines.length; ii++) {
                input.add(fileLines[ii]);
            }
            thisSpec = new MessageSpecification(input);
            Message cancelRequestMessage = MessageManager.getInstance().createMessage("dlDeviceCancelControlRequest");
            cancelRequestMessage.setMessageSpecification(thisSpec);

            responseMessage = emulator.getRRResponse("dlDeviceCancelControlRequest", cancelRequestMessage);
            for (String theName : responseMessage.getMessageSpecification().getMessageTypes()) {
                System.out.println("Status Response Message Type = " + theName + " with Size " + responseMessage.getMessageSpecification().getMessageSpecItems().size());
            }

            if (writeFile) {
//            EmulationDataFileProcessor.writeFile(EntityEmulationData.EntityDataType.CCTVINVENTORY + ".out", results);
            }
        }
    }

}
