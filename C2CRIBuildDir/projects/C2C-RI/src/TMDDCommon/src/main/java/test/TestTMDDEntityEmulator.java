/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import org.fhwa.c2cri.centermodel.EmulationDataFileProcessor;
import org.fhwa.c2cri.centermodel.RIEmulation;
import org.fhwa.c2cri.centermodel.RIEmulationEntityValueSet;
import org.fhwa.c2cri.centermodel.RIEmulationParameters;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.ntcip2306v109.messaging.MessageSpecificationProcessor;
import org.fhwa.c2cri.testmodel.testcasedata.TestCaseDataSource;
import org.fhwa.c2cri.tmdd.TMDDAuthenticationProcessor;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import org.fhwa.c2cri.tmdd.interfaces.ntcip2306.NTCIP2306XMLValidator;

/**
 *
 * @author TransCore ITS, LLC Created: Feb 23, 2016
 */
public class TestTMDDEntityEmulator {

    public static void main(String[] args) throws Exception {
        boolean writeFile = false;
        boolean readFile = true;
        boolean updateValues = false;
        long startTime = System.currentTimeMillis();

        String requestFilesPath = "C:\\C2CRI-Phase3\\C2CRIBuildDir\\projects\\C2C-RI\\src\\RIGUI\\emulationDataFiles\\TMDDv31\\EntityFiltered";
        String emulationFilesPath = "C:\\C2CRI-Phase3\\C2CRIBuildDir\\projects\\C2C-RI\\src\\TMDDv31\\src\\InfoLayer\\EmulationData";

        RIEmulation.getInstance().setEmulationEnabled(true);
//        RIEmulatorService theService = RIEmulatorService.getInstance();
//        RIEmulator emulator = theService.getEmulator("TMDDv31d");
//        TMDDv31EntityEmulator emulator = new TMDDv31EntityEmulator();
        VideoSwitchControlRequestTestCase vscrtc = new VideoSwitchControlRequestTestCase();
        vscrtc.dialogName = "dlVideoSwitchControlRequest";
        vscrtc.entityFilePath = emulationFilesPath;
        vscrtc.requestFilePath = Paths.get(requestFilesPath, "TCS-34-dlVideoSwitchControlRequest-OC-Valid.data").toString();
        vscrtc.responseMessageExpected = "tmdd:deviceControlResponseMsg";
//        testEntity(vscrtc);
        
        DMSMessageAppearanceRequestTestCase martc = new DMSMessageAppearanceRequestTestCase();
        martc.dialogName = "dlDMSMessageAppearanceRequest";
        martc.entityFilePath = emulationFilesPath;
        martc.requestFilePath = Paths.get(requestFilesPath, "TCS-44-dlDMSMessageAppearanceRequest-OC-Valid.data").toString();
        martc.responseMessageExpected = "tmdd:dMSMessageAppearanceMsg";
//        testEntity(martc);

        RampMeterInventoryRequestTestCase rmitc = new RampMeterInventoryRequestTestCase();
        rmitc.dialogName = "dlRampMeterInventoryRequest";
        rmitc.entityFilePath = emulationFilesPath;
        rmitc.requestFilePath = Paths.get(requestFilesPath, "TCS-77-dlRampMeterInventoryRequest-OC-Valid.data").toString();
        rmitc.responseMessageExpected = "tmdd:rampMeterInventoryMsg";
//        testEntity(rmitc);
    
        rmitc.dialogName = "dlRampMeterControlRequest";
        rmitc.entityFilePath = emulationFilesPath;
        rmitc.requestFilePath = Paths.get(requestFilesPath, "TCS-80-dlRampMeterControlRequest-OC-Valid.data").toString();
        rmitc.responseMessageExpected = "tmdd:ControlStatusResponseMsg";
//        testEntity(rmitc);

        DetectorDataRequestTestCase ddrtc = new DetectorDataRequestTestCase();
        ddrtc.dialogName = "dlDetectorDataRequest";
        ddrtc.entityFilePath = emulationFilesPath;
        ddrtc.requestFilePath = Paths.get(requestFilesPath, "TCS-23-dlDetectorDataRequest-OC-Valid.data").toString();
        ddrtc.responseMessageExpected = "tmdd:detectorDataMsg";
//        testEntity(ddrtc);

        HARControlRequestTestCase hcrtc = new HARControlRequestTestCase();
        hcrtc.dialogName = "dlHARControlRequest";
        hcrtc.entityFilePath = emulationFilesPath;
        hcrtc.requestFilePath = Paths.get(requestFilesPath, "TCS-64-dlHARControlRequest-OC-Valid.data").toString();
        hcrtc.responseMessageExpected = "tmdd:ControlStatusResponseMsg";
        testEntity(hcrtc);

//        testCCTVInventory();
//        testCCTVStatus();
//        testDetectorData();
    }

    private static void testEntity(TestCase entityTestCase) throws EntityEmulationException, MalformedURLException {
        TestCaseDataSource thisSource = new TestCaseDataSource((new File(entityTestCase.getRequestFileName()).toURI()).toURL());
        ArrayList<String> inputName = thisSource.getMessageList(0, "RequestMessage");
        String msgSpec = thisSource.getMessageSpec(0, inputName.get(0).replace("#MESSAGESPEC#", "")).replace("#RIMessageSpec#", "");
        String[] fileLines = msgSpec.split(";");
        ArrayList<String> input = new ArrayList();
        for (int ii = 0; ii < fileLines.length; ii++) {
            input.add(fileLines[ii]);
        }

        MessageSpecification thisSpec = new MessageSpecification(input);

//        for (String theName : thisSpec.getMessageTypes()) {
//            System.out.println(theName);
//        }
//        System.out.println(EmulationDataFileProcessor.getContent(EntityEmulationData.EntityDataType.CCTVINVENTORY + ".out").toString());
        RIEmulationParameters testParameters = new RIEmulationParameters();
        testParameters.setCommandQueueLength(0);
        ArrayList<RIEmulationEntityValueSet> theList = new ArrayList();
        RIEmulationEntityValueSet testSet = new RIEmulationEntityValueSet();
        theList.add(testSet);
        testParameters.setEntityDataMap(theList);

        testSet.setValueSetName(entityTestCase.getEntityTypeName());
        testSet.setEntityDataSet(EmulationDataFileProcessor.getByteArray(entityTestCase.getEntityFileName()));
        RIEmulation.getInstance().initialize("TMDD v3.1", testParameters);

        for (TestCaseUpdate tcu : entityTestCase.getTestCaseUpdateCommands()) {
            switch (tcu.getUpdateType()) {
                case AddEntity:
                    RIEmulation.getInstance().addEntity(entityTestCase.getEntityTypeName(), ((TestCaseAddEntityUpdate) tcu).getEntityName());
                    break;
                case DeleteEntity:
                    RIEmulation.getInstance().deleteEntity(entityTestCase.getEntityTypeName(), ((TestCaseDeleteEntityUpdate) tcu).getEntityName());
                    break;
                case AddEntityElement:
                    TestCaseAddEntityElementUpdate addEeu = (TestCaseAddEntityElementUpdate) tcu;
                    RIEmulation.getInstance().addEntityElement(entityTestCase.getEntityTypeName(),
                            addEeu.getEntityName(), addEeu.getEntityElementName(), addEeu.getEntityElementValue());
                    break;
                case UpdateEntityElement:
                    TestCaseUpdateEntityElementUpdate updateEeu = (TestCaseUpdateEntityElementUpdate) tcu;
                    RIEmulation.getInstance().updateEntityElement(entityTestCase.getEntityTypeName(),
                            updateEeu.getEntityName(), updateEeu.getElementName(), updateEeu.getElementValue());
                case DeleteEntityElement:
                    TestCaseDeleteEntityElementUpdate deleteEeu = (TestCaseDeleteEntityElementUpdate) tcu;
                    RIEmulation.getInstance().deleteEntityElement(entityTestCase.getEntityTypeName(),
                            deleteEeu.getEntityName(), deleteEeu.getEntityElement());
                    break;
                default:
                    break;
            }
        }

        TMDDAuthenticationProcessor.getInstance("string", "string", "string");
        Message requestMessage = MessageManager.getInstance().createMessage(entityTestCase.getRequestDialogName());
        requestMessage.setMessageSpecification(thisSpec);
        Message responseMessage = RIEmulation.getInstance().getRRResponse(entityTestCase.getRequestDialogName(), requestMessage);

        HashMap<String, String> nameSpaceMap = new HashMap();
        nameSpaceMap.put("tmdd", "http://www.tmdd.org/303/messages");
        nameSpaceMap.put("c2c", "http://www.ntcip.org/c2c-message-administration");
        nameSpaceMap.put("tmddX", "http://www.tmdd.org/X");
        MessageSpecificationProcessor msp = new MessageSpecificationProcessor(nameSpaceMap);
        msp.updateMessagefromElementSpecList(responseMessage.getMessageSpecification().getMessageSpec());

        String results = msp.getMessage();

        boolean result = NTCIP2306XMLValidator.getInstance().isXMLValidatedToSchema(results.getBytes());
        System.out.println("Validation Result = " + result);

        try {
            MessageSpecification msresults = msp.convertXMLtoMessageSpecification(results.getBytes("UTF-8"));
//            for (String element : msresults.getMessageSpec()){
//             System.out.println(element);   
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (String theName : responseMessage.getMessageSpecification().getMessageTypes()) {
            System.out.println("Response Message Type = " + theName + " with Size " + responseMessage.getMessageSpecification().getMessageSpecItems().size());
        }

    }

    private static void testCCTVInventory() throws EntityEmulationException {
        boolean writeFile = false;
        boolean readFile = true;
        boolean updateValues = false;
        long startTime = System.currentTimeMillis();

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
        } else {
            testSet.setValueSetName(EntityEmulationData.EntityDataType.CCTVINVENTORY.name());
            testSet.setEntityDataSet(EmulationDataFileProcessor.getByteArray(EntityEmulationData.EntityDataType.CCTVINVENTORY + ".out"));
        }
        RIEmulation.getInstance().initialize("TMDD v3.1", testParameters);

        System.out.println("*** CCTVInventory Initialized at " + (System.currentTimeMillis() - startTime) + " ms!");

        if (updateValues) {
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345674");
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345675");
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345676");
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345677");
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345678");
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345679");
            RIEmulation.getInstance().deleteEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345678");
            RIEmulation.getInstance().deleteEntity(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345674");
            RIEmulation.getInstance().addEntityElement(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345675", "tmdd:cCTVInventoryMsg.cctv-inventory-item.cctv-image-list[2].cctv-image", "jpeg");
            RIEmulation.getInstance().updateEntityElement(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345675", "tmdd:cCTVInventoryMsg.cctv-inventory-item.cctv-image-list[2].cctv-image", "tiff");
            System.out.println(RIEmulation.getInstance().getEntityElementValue(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345675", "tmdd:cCTVInventoryMsg.cctv-inventory-item.cctv-image-list[2].cctv-image"));
            RIEmulation.getInstance().deleteEntityElement(EntityEmulationData.EntityDataType.CCTVINVENTORY.name(), "cctv-123456789123456789012345675", "tmdd:cCTVInventoryMsg.cctv-inventory-item.cctv-image-list[2].cctv-image");
        }

        System.out.println("*** Update Values Completed after " + (System.currentTimeMillis() - startTime) + " ms!");

        TMDDAuthenticationProcessor.getInstance("string", "string", "string");
        Message requestMessage = MessageManager.getInstance().createMessage("dlCCTVInventoryRequest");
        requestMessage.setMessageSpecification(thisSpec);
        System.out.println("*** Prepped for Request after " + (System.currentTimeMillis() - startTime) + " ms!");
        Message responseMessage = RIEmulation.getInstance().getRRResponse("dlCCTVInventoryRequest", requestMessage);
        System.out.println("*** Request Message Processed after " + (System.currentTimeMillis() - startTime) + " ms!");
        for (String theName : responseMessage.getMessageSpecification().getMessageTypes()) {
            System.out.println("Response Message Type = " + theName + " with Size " + responseMessage.getMessageSpecification().getMessageSpecItems().size());
        }

        fileLines = EmulationDataFileProcessor.getContent("ValidCCTV-deviceControlStatusRequestMsg.in").toString().split("\n");
        input = new ArrayList();
        for (int ii = 0; ii < fileLines.length; ii++) {
            input.add(fileLines[ii]);
        }
        thisSpec = new MessageSpecification(input);
        Message statusRequestMessage = MessageManager.getInstance().createMessage("dlDeviceControlStatusRequest");
        statusRequestMessage.setMessageSpecification(thisSpec);

        responseMessage = RIEmulation.getInstance().getRRResponse("dlDeviceControlStatusRequest", statusRequestMessage);
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

        responseMessage = RIEmulation.getInstance().getRRResponse("dlCCTVControlRequest", controlRequestMessage);
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

        responseMessage = RIEmulation.getInstance().getRRResponse("dlDeviceCancelControlRequest", cancelRequestMessage);
        for (String theName : responseMessage.getMessageSpecification().getMessageTypes()) {
            System.out.println("Status Response Message Type = " + theName + " with Size " + responseMessage.getMessageSpecification().getMessageSpecItems().size());
        }

        if (writeFile) {
//            EmulationDataFileProcessor.writeFile(EntityEmulationData.EntityDataType.CCTVINVENTORY + ".out", results);
        }

    }

    private static void testCCTVStatus() throws EntityEmulationException {
        boolean writeFile = false;
        boolean readFile = true;
        boolean updateValues = false;
        long startTime = System.currentTimeMillis();

        String[] fileLines = EmulationDataFileProcessor.getContent("ValidCCTV-Status-deviceinformationRequestMsg.in").toString().split("\n");
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
//        System.out.println(EmulationDataFileProcessor.getContent(EntityEmulationData.EntityDataType.CCTVSTATUS + ".out").toString());

        RIEmulationParameters testParameters = new RIEmulationParameters();
        testParameters.setCommandQueueLength(0);
        ArrayList<RIEmulationEntityValueSet> theList = new ArrayList();
        RIEmulationEntityValueSet testSet = new RIEmulationEntityValueSet();
        theList.add(testSet);
        testParameters.setEntityDataMap(theList);

        if (!readFile) {
            testSet.setValueSetName(EntityEmulationData.EntityDataType.CCTVSTATUS.name());
        } else {
            testSet.setValueSetName(EntityEmulationData.EntityDataType.CCTVSTATUS.name());
            testSet.setEntityDataSet(EmulationDataFileProcessor.getByteArray(EntityEmulationData.EntityDataType.CCTVSTATUS + ".out"));
        }
        RIEmulation.getInstance().initialize("TMDD v3.1", testParameters);

        System.out.println("*** CCTVStatus Initialized at " + (System.currentTimeMillis() - startTime) + " ms!");

        if (updateValues) {
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.CCTVSTATUS.name(), "cctv-123456789123456789012345674");
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.CCTVSTATUS.name(), "cctv-123456789123456789012345675");
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.CCTVSTATUS.name(), "cctv-123456789123456789012345676");
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.CCTVSTATUS.name(), "cctv-123456789123456789012345677");
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.CCTVSTATUS.name(), "cctv-123456789123456789012345678");
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.CCTVSTATUS.name(), "cctv-123456789123456789012345679");
            RIEmulation.getInstance().deleteEntity(EntityEmulationData.EntityDataType.CCTVSTATUS.name(), "cctv-123456789123456789012345678");
            RIEmulation.getInstance().deleteEntity(EntityEmulationData.EntityDataType.CCTVSTATUS.name(), "cctv-123456789123456789012345674");
            RIEmulation.getInstance().addEntityElement(EntityEmulationData.EntityDataType.CCTVSTATUS.name(), "cctv-123456789123456789012345675", "tmdd:cCTVStatusMsg.cctv-status-item.cctv-image-list[2].cctv-image", "jpeg");
            RIEmulation.getInstance().updateEntityElement(EntityEmulationData.EntityDataType.CCTVSTATUS.name(), "cctv-123456789123456789012345675", "tmdd:cCTVStatusMsg.cctv-status-item.cctv-image-list[2].cctv-image", "tiff");
            System.out.println(RIEmulation.getInstance().getEntityElementValue(EntityEmulationData.EntityDataType.CCTVSTATUS.name(), "cctv-123456789123456789012345675", "tmdd:cCTVStatusMsg.cctv-status-item.cctv-image-list[2].cctv-image"));
            RIEmulation.getInstance().deleteEntityElement(EntityEmulationData.EntityDataType.CCTVSTATUS.name(), "cctv-123456789123456789012345675", "tmdd:cCTVStatusMsg.cctv-status-item.cctv-image-list[2].cctv-image");
        }

        System.out.println("*** Update Values Completed after " + (System.currentTimeMillis() - startTime) + " ms!");

        TMDDAuthenticationProcessor.getInstance("string", "string", "string");
        Message requestMessage = MessageManager.getInstance().createMessage("dlCCTVStatusRequest");
        requestMessage.setMessageSpecification(thisSpec);
        System.out.println("*** Prepped for Request after " + (System.currentTimeMillis() - startTime) + " ms!");
        Message responseMessage = RIEmulation.getInstance().getRRResponse("dlCCTVStatusRequest", requestMessage);
        System.out.println("*** Request Message Processed after " + (System.currentTimeMillis() - startTime) + " ms!");
        for (String theName : responseMessage.getMessageSpecification().getMessageTypes()) {
            System.out.println("Response Message Type = " + theName + " with Size " + responseMessage.getMessageSpecification().getMessageSpecItems().size());
        }

        if (writeFile) {
//            EmulationDataFileProcessor.writeFile(EntityEmulationData.EntityDataType.CCTVINVENTORY + ".out", results);
        }

    }

    private static void testDetectorData() throws EntityEmulationException {
        boolean writeFile = false;
        boolean readFile = true;
        boolean updateValues = false;
        long startTime = System.currentTimeMillis();

        String[] fileLines = EmulationDataFileProcessor.getContent(".\\test\\" + EntityEmulationData.EntityDataType.DETECTORDATA.name() + ".in").toString().split("\n");
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
//        System.out.println(EmulationDataFileProcessor.getContent(EntityEmulationData.EntityDataType.CCTVSTATUS + ".out").toString());

        RIEmulationParameters testParameters = new RIEmulationParameters();
        testParameters.setCommandQueueLength(0);
        ArrayList<RIEmulationEntityValueSet> theList = new ArrayList();
        RIEmulationEntityValueSet testSet = new RIEmulationEntityValueSet();
        theList.add(testSet);
        testParameters.setEntityDataMap(theList);

        if (!readFile) {
            testSet.setValueSetName(EntityEmulationData.EntityDataType.DETECTORDATA.name());
        } else {
            testSet.setValueSetName(EntityEmulationData.EntityDataType.DETECTORDATA.name());
            testSet.setEntityDataSet(EmulationDataFileProcessor.getByteArray(".\\test\\" + EntityEmulationData.EntityDataType.DETECTORDATA + ".out"));
        }
        RIEmulation.getInstance().initialize("TMDD v3.1", testParameters);

        System.out.println("*** DetectorData Initialized at " + (System.currentTimeMillis() - startTime) + " ms!");

        if (updateValues) {
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.DETECTORDATA.name(), "det-123456789123456789012345674");
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.DETECTORDATA.name(), "det-123456789123456789012345675");
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.DETECTORDATA.name(), "det-123456789123456789012345676");
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.DETECTORDATA.name(), "det-123456789123456789012345677");
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.DETECTORDATA.name(), "det-123456789123456789012345678");
            RIEmulation.getInstance().addEntity(EntityEmulationData.EntityDataType.DETECTORDATA.name(), "det-123456789123456789012345679");
            RIEmulation.getInstance().deleteEntity(EntityEmulationData.EntityDataType.DETECTORDATA.name(), "det-123456789123456789012345678");
            RIEmulation.getInstance().deleteEntity(EntityEmulationData.EntityDataType.DETECTORDATA.name(), "det-123456789123456789012345674");
//            RIEmulation.getInstance().addEntityElement(EntityEmulationData.EntityDataType.DETECTORDATA.name(), "det-123456789123456789012345675", "tmdd:cCTVStatusMsg.cctv-status-item.cctv-image-list[2].cctv-image", "jpeg");
//            RIEmulation.getInstance().updateEntityElement(EntityEmulationData.EntityDataType.DETECTORDATA.name(), "det-123456789123456789012345675", "tmdd:cCTVStatusMsg.cctv-status-item.cctv-image-list[2].cctv-image", "tiff");
            System.out.println(RIEmulation.getInstance().getEntityElementValue(EntityEmulationData.EntityDataType.DETECTORDATA.name(), "det-123456789123456789012345675", "tmdd:cCTVStatusMsg.cctv-status-item.cctv-image-list[2].cctv-image"));
//            RIEmulation.getInstance().deleteEntityElement(EntityEmulationData.EntityDataType.DETECTORDATA.name(), "det-123456789123456789012345675", "tmdd:cCTVStatusMsg.cctv-status-item.cctv-image-list[2].cctv-image");
        }

        System.out.println("*** Update Values Completed after " + (System.currentTimeMillis() - startTime) + " ms!");

        TMDDAuthenticationProcessor.getInstance("string", "string", "string");
        Message requestMessage = MessageManager.getInstance().createMessage("dlDetectorDataRequest");
        requestMessage.setMessageSpecification(thisSpec);
        System.out.println("*** Prepped for Request after " + (System.currentTimeMillis() - startTime) + " ms!");
        Message responseMessage = RIEmulation.getInstance().getRRResponse("dlDetectorDataRequest", requestMessage);
        System.out.println("*** Request Message Processed after " + (System.currentTimeMillis() - startTime) + " ms!");
        for (String theName : responseMessage.getMessageSpecification().getMessageTypes()) {
            System.out.println("Response Message Type = " + theName + " with Size " + responseMessage.getMessageSpecification().getMessageSpecItems().size());
        }

        if (writeFile) {
//            EmulationDataFileProcessor.writeFile(EntityEmulationData.EntityDataType.CCTVINVENTORY + ".out", results);
        }

    }

    public static enum CommandType {
        AddEntity, AddEntityElement, UpdateEntityElement, DeleteEntity, DeleteEntityElement, Initialize
    };

    public interface TestCase {

        public String getEntityTypeName();

        public String getEntityFileName();

        public String getRequestFileName();

        public String getRequestDialogName();

        public String getExpectedMessageResponseType();

        public ArrayList<TestCaseUpdate> getTestCaseUpdateCommands();
    }

    public interface TestCaseUpdate {

        public CommandType getUpdateType();
    }

    public interface TestCaseAddEntityUpdate extends TestCaseUpdate {

        public String getEntityName();
    }

    public interface TestCaseAddEntityElementUpdate extends TestCaseUpdate {

        public String getEntityName();

        public String getEntityElementName();

        public String getEntityElementValue();
    }

    public interface TestCaseDeleteEntityUpdate extends TestCaseUpdate {

        public String getEntityName();
    }

    public interface TestCaseDeleteEntityElementUpdate extends TestCaseUpdate {

        public String getEntityName();

        public String getEntityElement();
    }

    public interface TestCaseUpdateEntityElementUpdate extends TestCaseUpdate {

        public String getEntityName();

        public String getElementName();

        public String getElementValue();
    }

    public static class VideoSwitchControlRequestTestCase implements TestCase {

        String requestFilePath = "";
        String entityFilePath = "";
        String dialogName = "dlVideoSwitchControlRequest";
        String responseMessageExpected = "";

        @Override
        public String getEntityTypeName() {
            return EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY.name();
        }

        @Override
        public String getEntityFileName() {
            return Paths.get(entityFilePath, EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY.name()).toString();
        }

        @Override
        public String getRequestFileName() {
            return requestFilePath;
        }

        @Override
        public String getRequestDialogName() {
            return dialogName;
        }

        @Override
        public String getExpectedMessageResponseType() {
            return responseMessageExpected;
        }

        @Override
        public ArrayList<TestCaseUpdate> getTestCaseUpdateCommands() {
            return new ArrayList<TestCaseUpdate>();
        }
    }

    public static class DMSMessageAppearanceRequestTestCase implements TestCase {

        String requestFilePath = "";
        String entityFilePath = "";
        String dialogName = "dlMessageAppearanceRequest";
        String responseMessageExpected = "";

        @Override
        public String getEntityTypeName() {
            return EntityEmulationData.EntityDataType.DMSMESSAGEAPPEARANCE.name();
        }

        @Override
        public String getEntityFileName() {
            return Paths.get(entityFilePath, EntityEmulationData.EntityDataType.DMSMESSAGEAPPEARANCE.name()).toString();
        }

        @Override
        public String getRequestFileName() {
            return requestFilePath;
        }

        @Override
        public String getRequestDialogName() {
            return dialogName;
        }

        @Override
        public String getExpectedMessageResponseType() {
            return responseMessageExpected;
        }

        @Override
        public ArrayList<TestCaseUpdate> getTestCaseUpdateCommands() {
            return new ArrayList<TestCaseUpdate>();
        }
    }

   public static class RampMeterInventoryRequestTestCase implements TestCase {

        String requestFilePath = "";
        String entityFilePath = "";
        String dialogName = "dlRampMeterInventoryRequest";
        String responseMessageExpected = "";

        @Override
        public String getEntityTypeName() {
            return EntityEmulationData.EntityDataType.RAMPMETERINVENTORY.name();
        }

        @Override
        public String getEntityFileName() {
            return Paths.get(entityFilePath, EntityEmulationData.EntityDataType.RAMPMETERINVENTORY.name()).toString();
        }

        @Override
        public String getRequestFileName() {
            return requestFilePath;
        }

        @Override
        public String getRequestDialogName() {
            return dialogName;
        }

        @Override
        public String getExpectedMessageResponseType() {
            return responseMessageExpected;
        }

        @Override
        public ArrayList<TestCaseUpdate> getTestCaseUpdateCommands() {
            return new ArrayList<TestCaseUpdate>();
        }
    }
    

  public static class HARControlRequestTestCase implements TestCase {

        String requestFilePath = "";
        String entityFilePath = "";
        String dialogName = "dlHARControlRequest";
        String responseMessageExpected = "";

        @Override
        public String getEntityTypeName() {
            return EntityEmulationData.EntityDataType.HARSTATUS.name();
        }

        @Override
        public String getEntityFileName() {
            return Paths.get(entityFilePath, EntityEmulationData.EntityDataType.HARSTATUS.name()).toString();
        }

        @Override
        public String getRequestFileName() {
            return requestFilePath;
        }

        @Override
        public String getRequestDialogName() {
            return dialogName;
        }

        @Override
        public String getExpectedMessageResponseType() {
            return responseMessageExpected;
        }

        @Override
        public ArrayList<TestCaseUpdate> getTestCaseUpdateCommands() {
            return new ArrayList<TestCaseUpdate>();
        }
    }
       
   
   public static class DetectorDataRequestTestCase implements TestCase {

        String requestFilePath = "";
        String entityFilePath = "";
        String dialogName = "dlDetectorDataRequest";
        String responseMessageExpected = "";

        @Override
        public String getEntityTypeName() {
            return EntityEmulationData.EntityDataType.DETECTORDATA.name();
        }

        @Override
        public String getEntityFileName() {
            return Paths.get(entityFilePath, EntityEmulationData.EntityDataType.DETECTORDATA.name()).toString();
        }

        @Override
        public String getRequestFileName() {
            return requestFilePath;
        }

        @Override
        public String getRequestDialogName() {
            return dialogName;
        }

        @Override
        public String getExpectedMessageResponseType() {
            return responseMessageExpected;
        }

        @Override
        public ArrayList<TestCaseUpdate> getTestCaseUpdateCommands() {
            return new ArrayList<TestCaseUpdate>();
        }
    }   
}
