/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.fhwa.c2cri.centermodel.EmulationDataFileProcessor;
import org.fhwa.c2cri.centermodel.RIEmulationEntityValueSet;
import org.fhwa.c2cri.testmodel.TestConfiguration;

/**
 * The Class ConfigFileOutput.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class EmulationConfigFileOutput {

    private static String TEST_FILE_PATH = "C:\\C2CRI-Phase2\\C2CRIBuildDir\\projects\\C2C-RI\\src\\TMDDv303\\src\\InfoLayer\\EmulationData\\";

    /**
     * The main method.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        try {
            String configFileName = "C:\\C2CRI-Phase2\\C2CRIBuildDir\\projects\\C2C-RI\\src\\RIGUI\\TestConfigurationFiles\\TMDDv303cEntityEmuDefaultsECS.ricfg";
			TestConfiguration tc;
            try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(configFileName))))
			{
				tc = (TestConfiguration) input.readObject();
			}

//            ArrayList<String> targetedTestCases = new ArrayList();
//
//            targetedTestCases.add("TCS-5-dlEventIndexRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-5-dlEventIndexRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-5-dlEventIndexRequest-OC-Valid");
//            targetedTestCases.add("TCS-5-dlEventIndexSubscription-OC-Valid");
//            targetedTestCases.add("TCS-100-dlSectionControlStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-100-dlSectionControlStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-100-dlSectionControlStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-101-dlSectionPriorityQueueRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-101-dlSectionPriorityQueueRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-101-dlSectionPriorityQueueRequest-OC-Valid");
//            targetedTestCases.add("TCS-102-dlDeviceCancelControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-102-dlDeviceCancelControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-102-dlDeviceCancelControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-103-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-103-dlSectionControlScheduleRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-103-dlSectionControlScheduleRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-103-dlSectionControlScheduleRequest-OC-Valid");
//            targetedTestCases.add("TCS-104-dlSectionSignalTimingPatternInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-104-dlSectionSignalTimingPatternInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-104-dlSectionSignalTimingPatternInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-104-dlSectionSignalTimingPatternInventorySubscription-OC-Valid");
//            targetedTestCases.add("TCS-105-dlDetectorDataRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-105-dlDetectorDataRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-105-dlDetectorDataRequest-OC-Valid");
//            targetedTestCases.add("TCS-105-dlDetectorDataSubscription-OC-Valid");
//            targetedTestCases.add("TCS-108-dlDetectorMaintenanceHistoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-108-dlDetectorMaintenanceHistoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-108-dlDetectorMaintenanceHistoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-109-dlArchivedDataProcessingDocumentationMetadataRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-109-dlArchivedDataProcessingDocumentationMetadataRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-109-dlArchivedDataProcessingDocumentationMetadataRequest-OC-Valid");
//            targetedTestCases.add("TCS-111-dlFullEventUpdateRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-111-dlFullEventUpdateRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-111-dlFullEventUpdateRequest-OC-Valid");
//            targetedTestCases.add("TCS-111-dlFullEventUpdateSubscription-OC-Valid");
//            targetedTestCases.add("TCS-18-dlDetectorInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-18-dlDetectorInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-18-dlDetectorInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-18-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-19-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-20-dlDetectorStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-20-dlDetectorStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-20-dlDetectorStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-20-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-21-dlArchivedDataProcessingDocumentationMetadataRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-21-dlArchivedDataProcessingDocumentationMetadataRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-21-dlArchivedDataProcessingDocumentationMetadataRequest-OC-Valid");
//            targetedTestCases.add("TCS-22-dlDetectorInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-22-dlDetectorInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-22-dlDetectorInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-22-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-23-dlDetectorDataRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-23-dlDetectorDataRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-23-dlDetectorDataRequest-OC-Valid");
//            targetedTestCases.add("TCS-23-dlDetectorDataSubscription-OC-Valid");
//            targetedTestCases.add("TCS-24-dlDetectorMaintenanceHistoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-24-dlDetectorMaintenanceHistoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-24-dlDetectorMaintenanceHistoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-25-dlCCTVInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-25-dlCCTVInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-25-dlCCTVInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-25-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-26-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-27-dlCCTVStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-27-dlCCTVStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-27-dlCCTVStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-27-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-28-dlCCTVControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-28-dlCCTVControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-28-dlCCTVControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-29-dlDeviceControlStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-29-dlDeviceControlStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-29-dlDeviceControlStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-30-dlDeviceCancelControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-30-dlDeviceCancelControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-30-dlDeviceCancelControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-31-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-31-dlVideoSwitchInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-31-dlVideoSwitchInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-31-dlVideoSwitchInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-32-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-33-dlDeviceInformationSubscription-OC-InValid-7");
//            targetedTestCases.add("TCS-33-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-33-dlVideoSwitchStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-33-dlVideoSwitchStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-33-dlVideoSwitchStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-34-dlVideoSwitchControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-34-dlVideoSwitchControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-34-dlVideoSwitchControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-35-dlDeviceControlStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-35-dlDeviceControlStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-35-dlDeviceControlStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-36-dlDeviceCancelControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-36-dlDeviceCancelControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-36-dlDeviceCancelControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-37-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-37-dlDMSInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-37-dlDMSInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-37-dlDMSInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-38-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-39-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-39-dlDMSStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-39-dlDMSStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-39-dlDMSStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-40-dlDMSControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-40-dlDMSControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-40-dlDMSControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-41-dlDeviceControlStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-41-dlDeviceControlStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-41-dlDeviceControlStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-42-dlDMSPriorityQueueRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-42-dlDMSPriorityQueueRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-42-dlDMSPriorityQueueRequest-OC-Valid");
//            targetedTestCases.add("TCS-43-dlDeviceCancelControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-43-dlDeviceCancelControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-43-dlDeviceCancelControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-44-dlDMSFontTableRequest-OC-Valid");
//            targetedTestCases.add("TCS-44-dlDMSMessageAppearanceRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-44-dlDMSMessageAppearanceRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-44-dlDMSMessageAppearanceRequest-OC-Valid");
//            targetedTestCases.add("TCS-45-dlDMSMessageInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-45-dlDMSMessageInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-45-dlDMSMessageInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-45-dlDMSMessageInventorySubscription-OC-Valid");
//            targetedTestCases.add("TCS-46-dlDMSFontTableRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-46-dlDMSFontTableRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-46-dlDMSFontTableRequest-OC-Valid");
//            targetedTestCases.add("TCS-47-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-47-dlESSInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-47-dlESSInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-47-dlESSInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-48-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-49-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-49-dlESSStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-49-dlESSStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-49-dlESSStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-50-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-50-dlESSObservationReportRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-50-dlESSObservationReportRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-50-dlESSObservationReportRequest-OC-Valid");
//            targetedTestCases.add("TCS-51-dlESSObservationMetadataRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-51-dlESSObservationMetadataRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-51-dlESSObservationMetadataRequest-OC-Valid");
//            targetedTestCases.add("TCS-52-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-52-dlESSObservationReportRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-52-dlESSObservationReportRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-52-dlESSObservationReportRequest-OC-Valid");
//            targetedTestCases.add("TCS-53-dlESSObservationMetadataRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-53-dlESSObservationMetadataRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-53-dlESSObservationMetadataRequest-OC-Valid");
//            targetedTestCases.add("TCS-54-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-54-dlGateInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-54-dlGateInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-54-dlGateInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-55-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-56-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-56-dlGateStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-56-dlGateStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-56-dlGateStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-57-dlGateControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-57-dlGateControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-57-dlGateControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-58-dlDeviceControlStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-58-dlDeviceControlStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-58-dlDeviceControlStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-59-dlDeviceCancelControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-59-dlDeviceCancelControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-59-dlDeviceCancelControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-60-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-60-dlGateControlScheduleRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-60-dlGateControlScheduleRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-60-dlGateControlScheduleRequest-OC-Valid");
//            targetedTestCases.add("TCS-61-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-61-dlHARInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-61-dlHARInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-61-dlHARInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-62-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-63-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-63-dlHARStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-63-dlHARStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-63-dlHARStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-64-dlHARControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-64-dlHARControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-64-dlHARControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-65-dlDeviceControlStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-65-dlDeviceControlStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-65-dlDeviceControlStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-66-dlHARPriorityQueueRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-66-dlHARPriorityQueueRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-66-dlHARPriorityQueueRequest-OC-Valid");
//            targetedTestCases.add("TCS-67-dlDeviceCancelControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-67-dlDeviceCancelControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-67-dlDeviceCancelControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-68-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-68-dlHARControlScheduleRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-68-dlHARControlScheduleRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-68-dlHARControlScheduleRequest-OC-Valid");
//            targetedTestCases.add("TCS-69-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-69-dlHARMessageInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-69-dlHARMessageInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-69-dlHARMessageInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-6-dlFullEventUpdateRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-6-dlFullEventUpdateRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-6-dlFullEventUpdateRequest-OC-Valid");
//            targetedTestCases.add("TCS-6-dlFullEventUpdateSubscription-OC-Valid");
//            targetedTestCases.add("TCS-70-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-70-dlLCSInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-70-dlLCSInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-70-dlLCSInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-71-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-72-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-72-dlLCSStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-72-dlLCSStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-72-dlLCSStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-73-dlLCSControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-73-dlLCSControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-73-dlLCSControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-74-dlDeviceControlStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-74-dlDeviceControlStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-74-dlDeviceControlStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-75-dlDeviceCancelControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-75-dlDeviceCancelControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-75-dlDeviceCancelControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-76-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-76-dlLCSControlScheduleRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-76-dlLCSControlScheduleRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-76-dlLCSControlScheduleRequest-OC-Valid");
//            targetedTestCases.add("TCS-77-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-77-dlRampMeterInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-77-dlRampMeterInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-77-dlRampMeterInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-78-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-79-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-79-dlRampMeterStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-79-dlRampMeterStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-79-dlRampMeterStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-7-dlFullEventUpdateRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-7-dlFullEventUpdateRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-7-dlFullEventUpdateRequest-OC-Valid");
//            targetedTestCases.add("TCS-7-dlFullEventUpdateSubscription-OC-Valid");
//            targetedTestCases.add("TCS-80-dlRampMeterControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-80-dlRampMeterControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-80-dlRampMeterControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-81-dlDeviceControlStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-81-dlDeviceControlStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-81-dlDeviceControlStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-82-dlDeviceCancelControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-82-dlDeviceCancelControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-82-dlDeviceCancelControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-83-dlRampMeterPriorityQueueRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-83-dlRampMeterPriorityQueueRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-83-dlRampMeterPriorityQueueRequest-OC-Valid");
//            targetedTestCases.add("TCS-84-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-84-dlRampMeterControlScheduleRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-84-dlRampMeterControlScheduleRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-84-dlRampMeterControlScheduleRequest-OC-Valid");
//            targetedTestCases.add("TCS-85-dlRampMeterPlanInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-85-dlRampMeterPlanInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-85-dlRampMeterPlanInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-85-dlRampMeterPlanInventorySubscription-OC-Valid");
//            targetedTestCases.add("TCS-86-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-86-dlIntersectionSignalInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-86-dlIntersectionSignalInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-86-dlIntersectionSignalInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-87-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-88-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-88-dlIntersectionSignalStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-88-dlIntersectionSignalStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-88-dlIntersectionSignalStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-89-dlIntersectionSignalControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-89-dlIntersectionSignalControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-89-dlIntersectionSignalControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-8-dlFullEventUpdateRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-8-dlFullEventUpdateRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-8-dlFullEventUpdateRequest-OC-Valid");
//            targetedTestCases.add("TCS-8-dlFullEventUpdateSubscription-OC-Valid");
//            targetedTestCases.add("TCS-90-dlDeviceControlStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-90-dlDeviceControlStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-90-dlDeviceControlStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-91-dlIntersectionSignalPriorityQueueRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-91-dlIntersectionSignalPriorityQueueRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-91-dlIntersectionSignalPriorityQueueRequest-OC-Valid");
//            targetedTestCases.add("TCS-92-dlDeviceCancelControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-92-dlDeviceCancelControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-92-dlDeviceCancelControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-93-dlIntersectionSignalTimingPatternInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-93-dlIntersectionSignalTimingPatternInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-93-dlIntersectionSignalTimingPatternInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-93-dlIntersectionSignalTimingPatternInventorySubscription-OC-Valid");
//            targetedTestCases.add("TCS-94-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-94-dlIntersectionSignalControlScheduleRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-94-dlIntersectionSignalControlScheduleRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-94-dlIntersectionSignalControlScheduleRequest-OC-Valid");
//            targetedTestCases.add("TCS-95-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-95-dlIntersectionSignalInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-95-dlIntersectionSignalInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-95-dlIntersectionSignalInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-96-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-96-dlIntersectionSignalInventoryRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-96-dlIntersectionSignalInventoryRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-96-dlIntersectionSignalInventoryRequest-OC-Valid");
//            targetedTestCases.add("TCS-97-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-97-dlIntersectionSignalStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-97-dlIntersectionSignalStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-97-dlIntersectionSignalStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-98-dlDeviceInformationSubscription-OC-Valid");
//            targetedTestCases.add("TCS-98-dlSectionStatusRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-98-dlSectionStatusRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-98-dlSectionStatusRequest-OC-Valid");
//            targetedTestCases.add("TCS-99-dlSectionControlRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-99-dlSectionControlRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-99-dlSectionControlRequest-OC-Valid");
//            targetedTestCases.add("TCS-9-dlActionLogRequest-OC-InValid-7");
//            targetedTestCases.add("TCS-9-dlActionLogRequest-OC-InValid-8");
//            targetedTestCases.add("TCS-9-dlActionLogRequest-OC-Valid");
//            targetedTestCases.add("TCS-9-dlActionLogSubscription-OC-Valid");
//
////            for (TestCase thisTestCase : tc.getInfoLayerParams().getApplicableTestCases("OC")) {
////                    thisTestCase.setCustomDataLocation("");
////            }            
//            
//            for (TestCase thisTestCase : tc.getInfoLayerParams().getApplicableTestCases("EC")) {
//                if (targetedTestCases.contains(thisTestCase.getName())) {
//                    String dataFileName = "c:\\c2cri\\emulationDataFiles\\" + thisTestCase.getName() + ".data";
//                    String sourceFileName = "C:\\C2CRI-Phase2\\C2CRIBuildDir\\projects\\C2C-RI\\src\\TMDDv303\\src\\InfoLayer\\Data\\" + thisTestCase.getName() + ".data";
////                    thisTestCase.setCustomDataLocation("c:\\c2cri\\emulationDataFiles\\" + thisTestCase.getName() + ".data");
//                    if (!thisTestCase.isOverriden()) {
//                        System.out.println("Here's a problem.");
//                    }
////                    Path source = Paths.get(new File(sourceFileName).toURI());
////                    Path out = Paths.get(new File(dataFileName).toURI());
////                    Files.copy(source, out);
//                }
//            }

            for (RIEmulationEntityValueSet thisEntity : tc.getEmulationParameters().getEntityDataMap()){
                System.out.println("updating "+thisEntity.getValueSetName());
                thisEntity.setEntityDataSet(
                 EmulationDataFileProcessor.getByteArray(TEST_FILE_PATH+thisEntity.getValueSetName()));
                thisEntity.setDataSetSource(RIEmulationEntityValueSet.ENTITYDATASTATE.Default);
//                thisEntity.setDataSetSource(RIEmulationEntityValueSet.ENTITYDATASTATE.Updated);   
            }
			

//            long startTime = System.currentTimeMillis();
//            RIEmulation.getInstance().initialize(TestSuites.getInstance().getBaselineTestSuite(tc.getSelectedInfoLayerTestSuite()), tc.getEmulationParameters());
//            System.out.println("It took "+(System.currentTimeMillis() - startTime )+ " ms to complete.");
            try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(configFileName + ".updated")))
			{
				output.writeObject(tc);
			}
//            FileOutputStream output = new FileOutputStream("c:\\c2cri\\EntityEmulationOut.xml");
//            output.write(tc.to_LogFormat().getBytes());
//            output.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
