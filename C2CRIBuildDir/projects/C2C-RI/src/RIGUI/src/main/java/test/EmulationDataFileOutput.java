/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * The Class ConfigFileOutput.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class EmulationDataFileOutput {

    private static String TEST_FILE_PATH = "C:\\TEST\\";
    private static enum COPYTYPE {DEFAULT,NONFILTERED,FILTERED};

    /**
     * The main method.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        try {

            ArrayList<String> targetedTestCases = new ArrayList();
            
targetedTestCases.add("TCS-5-dlEventIndexRequest-OC-InValid-7");
targetedTestCases.add("TCS-5-dlEventIndexRequest-OC-InValid-8");
targetedTestCases.add("TCS-5-dlEventIndexRequest-OC-Valid");
targetedTestCases.add("TCS-5-dlEventIndexSubscription-OC-Valid");
targetedTestCases.add("TCS-100-dlSectionControlStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-100-dlSectionControlStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-100-dlSectionControlStatusRequest-OC-Valid");
targetedTestCases.add("TCS-101-dlSectionPriorityQueueRequest-OC-InValid-7");
targetedTestCases.add("TCS-101-dlSectionPriorityQueueRequest-OC-InValid-8");
targetedTestCases.add("TCS-101-dlSectionPriorityQueueRequest-OC-Valid");
targetedTestCases.add("TCS-102-dlDeviceCancelControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-102-dlDeviceCancelControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-102-dlDeviceCancelControlRequest-OC-Valid");
targetedTestCases.add("TCS-103-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-103-dlSectionControlScheduleRequest-OC-InValid-7");
targetedTestCases.add("TCS-103-dlSectionControlScheduleRequest-OC-InValid-8");
targetedTestCases.add("TCS-103-dlSectionControlScheduleRequest-OC-Valid");
targetedTestCases.add("TCS-104-dlSectionSignalTimingPatternInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-104-dlSectionSignalTimingPatternInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-104-dlSectionSignalTimingPatternInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-104-dlSectionSignalTimingPatternInventorySubscription-OC-Valid");
targetedTestCases.add("TCS-105-dlDetectorDataRequest-OC-InValid-7");
targetedTestCases.add("TCS-105-dlDetectorDataRequest-OC-InValid-8");
targetedTestCases.add("TCS-105-dlDetectorDataRequest-OC-Valid");
targetedTestCases.add("TCS-105-dlDetectorDataSubscription-OC-Valid");
targetedTestCases.add("TCS-108-dlDetectorMaintenanceHistoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-108-dlDetectorMaintenanceHistoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-108-dlDetectorMaintenanceHistoryRequest-OC-Valid");
targetedTestCases.add("TCS-109-dlArchivedDataProcessingDocumentationMetadataRequest-OC-InValid-7");
targetedTestCases.add("TCS-109-dlArchivedDataProcessingDocumentationMetadataRequest-OC-InValid-8");
targetedTestCases.add("TCS-109-dlArchivedDataProcessingDocumentationMetadataRequest-OC-Valid");
targetedTestCases.add("TCS-111-dlFullEventUpdateRequest-OC-InValid-7");
targetedTestCases.add("TCS-111-dlFullEventUpdateRequest-OC-InValid-8");
targetedTestCases.add("TCS-111-dlFullEventUpdateRequest-OC-Valid");
targetedTestCases.add("TCS-111-dlFullEventUpdateSubscription-OC-Valid");
targetedTestCases.add("TCS-18-dlDetectorInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-18-dlDetectorInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-18-dlDetectorInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-18-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-19-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-20-dlDetectorStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-20-dlDetectorStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-20-dlDetectorStatusRequest-OC-Valid");
targetedTestCases.add("TCS-20-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-21-dlArchivedDataProcessingDocumentationMetadataRequest-OC-InValid-7");
targetedTestCases.add("TCS-21-dlArchivedDataProcessingDocumentationMetadataRequest-OC-InValid-8");
targetedTestCases.add("TCS-21-dlArchivedDataProcessingDocumentationMetadataRequest-OC-Valid");
targetedTestCases.add("TCS-22-dlDetectorInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-22-dlDetectorInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-22-dlDetectorInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-22-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-23-dlDetectorDataRequest-OC-InValid-7");
targetedTestCases.add("TCS-23-dlDetectorDataRequest-OC-InValid-8");
targetedTestCases.add("TCS-23-dlDetectorDataRequest-OC-Valid");
targetedTestCases.add("TCS-23-dlDetectorDataSubscription-OC-Valid");
targetedTestCases.add("TCS-24-dlDetectorMaintenanceHistoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-24-dlDetectorMaintenanceHistoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-24-dlDetectorMaintenanceHistoryRequest-OC-Valid");
targetedTestCases.add("TCS-25-dlCCTVInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-25-dlCCTVInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-25-dlCCTVInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-25-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-26-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-27-dlCCTVStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-27-dlCCTVStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-27-dlCCTVStatusRequest-OC-Valid");
targetedTestCases.add("TCS-27-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-28-dlCCTVControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-28-dlCCTVControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-28-dlCCTVControlRequest-OC-Valid");
targetedTestCases.add("TCS-29-dlDeviceControlStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-29-dlDeviceControlStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-29-dlDeviceControlStatusRequest-OC-Valid");
targetedTestCases.add("TCS-30-dlDeviceCancelControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-30-dlDeviceCancelControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-30-dlDeviceCancelControlRequest-OC-Valid");
targetedTestCases.add("TCS-31-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-31-dlVideoSwitchInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-31-dlVideoSwitchInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-31-dlVideoSwitchInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-32-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-33-dlDeviceInformationSubscription-OC-InValid-7");
targetedTestCases.add("TCS-33-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-33-dlVideoSwitchStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-33-dlVideoSwitchStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-33-dlVideoSwitchStatusRequest-OC-Valid");
targetedTestCases.add("TCS-34-dlVideoSwitchControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-34-dlVideoSwitchControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-34-dlVideoSwitchControlRequest-OC-Valid");
targetedTestCases.add("TCS-35-dlDeviceControlStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-35-dlDeviceControlStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-35-dlDeviceControlStatusRequest-OC-Valid");
targetedTestCases.add("TCS-36-dlDeviceCancelControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-36-dlDeviceCancelControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-36-dlDeviceCancelControlRequest-OC-Valid");
targetedTestCases.add("TCS-37-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-37-dlDMSInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-37-dlDMSInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-37-dlDMSInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-38-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-39-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-39-dlDMSStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-39-dlDMSStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-39-dlDMSStatusRequest-OC-Valid");
targetedTestCases.add("TCS-40-dlDMSControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-40-dlDMSControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-40-dlDMSControlRequest-OC-Valid");
targetedTestCases.add("TCS-41-dlDeviceControlStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-41-dlDeviceControlStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-41-dlDeviceControlStatusRequest-OC-Valid");
targetedTestCases.add("TCS-42-dlDMSPriorityQueueRequest-OC-InValid-7");
targetedTestCases.add("TCS-42-dlDMSPriorityQueueRequest-OC-InValid-8");
targetedTestCases.add("TCS-42-dlDMSPriorityQueueRequest-OC-Valid");
targetedTestCases.add("TCS-43-dlDeviceCancelControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-43-dlDeviceCancelControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-43-dlDeviceCancelControlRequest-OC-Valid");
targetedTestCases.add("TCS-44-dlDMSFontTableRequest-OC-Valid");
targetedTestCases.add("TCS-44-dlDMSMessageAppearanceRequest-OC-InValid-7");
targetedTestCases.add("TCS-44-dlDMSMessageAppearanceRequest-OC-InValid-8");
targetedTestCases.add("TCS-44-dlDMSMessageAppearanceRequest-OC-Valid");
targetedTestCases.add("TCS-45-dlDMSMessageInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-45-dlDMSMessageInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-45-dlDMSMessageInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-45-dlDMSMessageInventorySubscription-OC-Valid");
targetedTestCases.add("TCS-46-dlDMSFontTableRequest-OC-InValid-7");
targetedTestCases.add("TCS-46-dlDMSFontTableRequest-OC-InValid-8");
targetedTestCases.add("TCS-46-dlDMSFontTableRequest-OC-Valid");
targetedTestCases.add("TCS-47-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-47-dlESSInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-47-dlESSInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-47-dlESSInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-48-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-49-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-49-dlESSStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-49-dlESSStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-49-dlESSStatusRequest-OC-Valid");
targetedTestCases.add("TCS-50-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-50-dlESSObservationReportRequest-OC-InValid-7");
targetedTestCases.add("TCS-50-dlESSObservationReportRequest-OC-InValid-8");
targetedTestCases.add("TCS-50-dlESSObservationReportRequest-OC-Valid");
targetedTestCases.add("TCS-51-dlESSObservationMetadataRequest-OC-InValid-7");
targetedTestCases.add("TCS-51-dlESSObservationMetadataRequest-OC-InValid-8");
targetedTestCases.add("TCS-51-dlESSObservationMetadataRequest-OC-Valid");
targetedTestCases.add("TCS-52-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-52-dlESSObservationReportRequest-OC-InValid-7");
targetedTestCases.add("TCS-52-dlESSObservationReportRequest-OC-InValid-8");
targetedTestCases.add("TCS-52-dlESSObservationReportRequest-OC-Valid");
targetedTestCases.add("TCS-53-dlESSObservationMetadataRequest-OC-InValid-7");
targetedTestCases.add("TCS-53-dlESSObservationMetadataRequest-OC-InValid-8");
targetedTestCases.add("TCS-53-dlESSObservationMetadataRequest-OC-Valid");
targetedTestCases.add("TCS-54-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-54-dlGateInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-54-dlGateInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-54-dlGateInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-55-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-56-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-56-dlGateStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-56-dlGateStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-56-dlGateStatusRequest-OC-Valid");
targetedTestCases.add("TCS-57-dlGateControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-57-dlGateControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-57-dlGateControlRequest-OC-Valid");
targetedTestCases.add("TCS-58-dlDeviceControlStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-58-dlDeviceControlStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-58-dlDeviceControlStatusRequest-OC-Valid");
targetedTestCases.add("TCS-59-dlDeviceCancelControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-59-dlDeviceCancelControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-59-dlDeviceCancelControlRequest-OC-Valid");
targetedTestCases.add("TCS-60-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-60-dlGateControlScheduleRequest-OC-InValid-7");
targetedTestCases.add("TCS-60-dlGateControlScheduleRequest-OC-InValid-8");
targetedTestCases.add("TCS-60-dlGateControlScheduleRequest-OC-Valid");
targetedTestCases.add("TCS-61-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-61-dlHARInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-61-dlHARInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-61-dlHARInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-62-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-63-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-63-dlHARStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-63-dlHARStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-63-dlHARStatusRequest-OC-Valid");
targetedTestCases.add("TCS-64-dlHARControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-64-dlHARControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-64-dlHARControlRequest-OC-Valid");
targetedTestCases.add("TCS-65-dlDeviceControlStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-65-dlDeviceControlStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-65-dlDeviceControlStatusRequest-OC-Valid");
targetedTestCases.add("TCS-66-dlHARPriorityQueueRequest-OC-InValid-7");
targetedTestCases.add("TCS-66-dlHARPriorityQueueRequest-OC-InValid-8");
targetedTestCases.add("TCS-66-dlHARPriorityQueueRequest-OC-Valid");
targetedTestCases.add("TCS-67-dlDeviceCancelControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-67-dlDeviceCancelControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-67-dlDeviceCancelControlRequest-OC-Valid");
targetedTestCases.add("TCS-68-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-68-dlHARControlScheduleRequest-OC-InValid-7");
targetedTestCases.add("TCS-68-dlHARControlScheduleRequest-OC-InValid-8");
targetedTestCases.add("TCS-68-dlHARControlScheduleRequest-OC-Valid");
targetedTestCases.add("TCS-69-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-69-dlHARMessageInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-69-dlHARMessageInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-69-dlHARMessageInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-6-dlFullEventUpdateRequest-OC-InValid-7");
targetedTestCases.add("TCS-6-dlFullEventUpdateRequest-OC-InValid-8");
targetedTestCases.add("TCS-6-dlFullEventUpdateRequest-OC-Valid");
targetedTestCases.add("TCS-6-dlFullEventUpdateSubscription-OC-Valid");
targetedTestCases.add("TCS-70-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-70-dlLCSInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-70-dlLCSInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-70-dlLCSInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-71-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-72-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-72-dlLCSStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-72-dlLCSStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-72-dlLCSStatusRequest-OC-Valid");
targetedTestCases.add("TCS-73-dlLCSControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-73-dlLCSControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-73-dlLCSControlRequest-OC-Valid");
targetedTestCases.add("TCS-74-dlDeviceControlStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-74-dlDeviceControlStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-74-dlDeviceControlStatusRequest-OC-Valid");
targetedTestCases.add("TCS-75-dlDeviceCancelControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-75-dlDeviceCancelControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-75-dlDeviceCancelControlRequest-OC-Valid");
targetedTestCases.add("TCS-76-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-76-dlLCSControlScheduleRequest-OC-InValid-7");
targetedTestCases.add("TCS-76-dlLCSControlScheduleRequest-OC-InValid-8");
targetedTestCases.add("TCS-76-dlLCSControlScheduleRequest-OC-Valid");
targetedTestCases.add("TCS-77-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-77-dlRampMeterInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-77-dlRampMeterInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-77-dlRampMeterInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-78-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-79-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-79-dlRampMeterStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-79-dlRampMeterStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-79-dlRampMeterStatusRequest-OC-Valid");
targetedTestCases.add("TCS-7-dlFullEventUpdateRequest-OC-InValid-7");
targetedTestCases.add("TCS-7-dlFullEventUpdateRequest-OC-InValid-8");
targetedTestCases.add("TCS-7-dlFullEventUpdateRequest-OC-Valid");
targetedTestCases.add("TCS-7-dlFullEventUpdateSubscription-OC-Valid");
targetedTestCases.add("TCS-80-dlRampMeterControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-80-dlRampMeterControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-80-dlRampMeterControlRequest-OC-Valid");
targetedTestCases.add("TCS-81-dlDeviceControlStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-81-dlDeviceControlStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-81-dlDeviceControlStatusRequest-OC-Valid");
targetedTestCases.add("TCS-82-dlDeviceCancelControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-82-dlDeviceCancelControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-82-dlDeviceCancelControlRequest-OC-Valid");
targetedTestCases.add("TCS-83-dlRampMeterPriorityQueueRequest-OC-InValid-7");
targetedTestCases.add("TCS-83-dlRampMeterPriorityQueueRequest-OC-InValid-8");
targetedTestCases.add("TCS-83-dlRampMeterPriorityQueueRequest-OC-Valid");
targetedTestCases.add("TCS-84-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-84-dlRampMeterControlScheduleRequest-OC-InValid-7");
targetedTestCases.add("TCS-84-dlRampMeterControlScheduleRequest-OC-InValid-8");
targetedTestCases.add("TCS-84-dlRampMeterControlScheduleRequest-OC-Valid");
targetedTestCases.add("TCS-85-dlRampMeterPlanInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-85-dlRampMeterPlanInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-85-dlRampMeterPlanInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-85-dlRampMeterPlanInventorySubscription-OC-Valid");
targetedTestCases.add("TCS-86-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-86-dlIntersectionSignalInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-86-dlIntersectionSignalInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-86-dlIntersectionSignalInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-87-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-88-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-88-dlIntersectionSignalStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-88-dlIntersectionSignalStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-88-dlIntersectionSignalStatusRequest-OC-Valid");
targetedTestCases.add("TCS-89-dlIntersectionSignalControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-89-dlIntersectionSignalControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-89-dlIntersectionSignalControlRequest-OC-Valid");
targetedTestCases.add("TCS-8-dlFullEventUpdateRequest-OC-InValid-7");
targetedTestCases.add("TCS-8-dlFullEventUpdateRequest-OC-InValid-8");
targetedTestCases.add("TCS-8-dlFullEventUpdateRequest-OC-Valid");
targetedTestCases.add("TCS-8-dlFullEventUpdateSubscription-OC-Valid");
targetedTestCases.add("TCS-90-dlDeviceControlStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-90-dlDeviceControlStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-90-dlDeviceControlStatusRequest-OC-Valid");
targetedTestCases.add("TCS-91-dlIntersectionSignalPriorityQueueRequest-OC-InValid-7");
targetedTestCases.add("TCS-91-dlIntersectionSignalPriorityQueueRequest-OC-InValid-8");
targetedTestCases.add("TCS-91-dlIntersectionSignalPriorityQueueRequest-OC-Valid");
targetedTestCases.add("TCS-92-dlDeviceCancelControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-92-dlDeviceCancelControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-92-dlDeviceCancelControlRequest-OC-Valid");
targetedTestCases.add("TCS-93-dlIntersectionSignalTimingPatternInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-93-dlIntersectionSignalTimingPatternInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-93-dlIntersectionSignalTimingPatternInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-93-dlIntersectionSignalTimingPatternInventorySubscription-OC-Valid");
targetedTestCases.add("TCS-94-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-94-dlIntersectionSignalControlScheduleRequest-OC-InValid-7");
targetedTestCases.add("TCS-94-dlIntersectionSignalControlScheduleRequest-OC-InValid-8");
targetedTestCases.add("TCS-94-dlIntersectionSignalControlScheduleRequest-OC-Valid");
targetedTestCases.add("TCS-95-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-95-dlIntersectionSignalInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-95-dlIntersectionSignalInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-95-dlIntersectionSignalInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-96-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-96-dlIntersectionSignalInventoryRequest-OC-InValid-7");
targetedTestCases.add("TCS-96-dlIntersectionSignalInventoryRequest-OC-InValid-8");
targetedTestCases.add("TCS-96-dlIntersectionSignalInventoryRequest-OC-Valid");
targetedTestCases.add("TCS-97-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-97-dlIntersectionSignalStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-97-dlIntersectionSignalStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-97-dlIntersectionSignalStatusRequest-OC-Valid");
targetedTestCases.add("TCS-98-dlDeviceInformationSubscription-OC-Valid");
targetedTestCases.add("TCS-98-dlSectionStatusRequest-OC-InValid-7");
targetedTestCases.add("TCS-98-dlSectionStatusRequest-OC-InValid-8");
targetedTestCases.add("TCS-98-dlSectionStatusRequest-OC-Valid");
targetedTestCases.add("TCS-99-dlSectionControlRequest-OC-InValid-7");
targetedTestCases.add("TCS-99-dlSectionControlRequest-OC-InValid-8");
targetedTestCases.add("TCS-99-dlSectionControlRequest-OC-Valid");
targetedTestCases.add("TCS-9-dlActionLogRequest-OC-InValid-7");
targetedTestCases.add("TCS-9-dlActionLogRequest-OC-InValid-8");
targetedTestCases.add("TCS-9-dlActionLogRequest-OC-Valid");
targetedTestCases.add("TCS-9-dlActionLogSubscription-OC-Valid");
            
           

            for (String thisTestCase : targetedTestCases) {
                // Copy the default data (as is ...)
                String dataFileName = "c:\\c2cri\\emulationDataFiles\\Default\\" + thisTestCase + ".data";
                String sourceFileName = "C:\\C2CRI-Phase2\\C2CRIBuildDir\\projects\\C2C-RI\\src\\TMDDv303\\src\\InfoLayer\\Data\\" + thisTestCase + ".data";
                Path source = Paths.get(new File(sourceFileName).toURI());
                Path out = Paths.get(new File(dataFileName).toURI());
                Files.copy(source, out);
                
                dataFileName = "c:\\c2cri\\emulationDataFiles\\NoFiltering\\" + thisTestCase + ".data";
                copyFiles(sourceFileName, dataFileName, COPYTYPE.NONFILTERED);

                dataFileName = "c:\\c2cri\\emulationDataFiles\\EntityFiltered\\" + thisTestCase + ".data";
                copyFiles(sourceFileName, dataFileName, COPYTYPE.FILTERED);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    
    
    public static void copyFiles(String inputFile, String outputFile, COPYTYPE copyType) {
        System.out.println("Copying "+inputFile+" as type "+copyType.name()+" ...");
        try {
            FileReader inputFileReader = new FileReader(inputFile);
            BufferedReader br1 = new BufferedReader(inputFileReader);
            // Read the first line from the file.
            String line1 = br1.readLine();

            StringBuilder holdHere = new StringBuilder();

            BufferedWriter bw1 = new BufferedWriter(new FileWriter(outputFile, true));
            boolean beforeMessage = true;
            String lastNonBlankLine = "";
            // As long as the line isn�t null, keep going.
            while (line1 != null) {
                if (beforeMessage) {
                    if (line1.contains("#GROUP NAME = Message")) {
                        line1 = "#GROUP NAME = EmulationMessage";
                        beforeMessage = false;
                    } else if (line1.contains("RequestMessage = #MESSAGESPEC#Message#MESSAGESPEC#")){
                        line1 = "RequestMessage = #MESSAGESPEC#EmulationMessage#MESSAGESPEC#";
                    } else if (line1.contains("SubscriptionMessage = #MESSAGESPEC#Message#MESSAGESPEC#")){
                        line1 = "SubscriptionMessage = #MESSAGESPEC#EmulationMessage#MESSAGESPEC#";                        
                    }
                        bw1.write(line1);
                        bw1.write("\n");
                    
                } else {
                // When we find a blank line check to see if the prior lines should be 
                // copied over or discarded/modified.
                    if (line1.isEmpty()) {
                        if (keepLines(lastNonBlankLine, copyType)){
                            // add the blank line to the buffer.
                            holdHere.append(line1+"\n");
                            bw1.write(updatedLine(holdHere.toString(), lastNonBlankLine, copyType));   
                            holdHere = new StringBuilder();
                        }
                    } else {
                        holdHere.append(line1+"\n");
                        lastNonBlankLine = line1;
                    }
                }
                line1 = br1.readLine();
            }
            bw1.close();
            br1.close();
            inputFileReader.close();
        } catch (FileNotFoundException fne) {
            fne.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static boolean keepLines(String input, COPYTYPE copyType) {
        boolean results = false;
        switch (copyType) {
            case FILTERED:
            case NONFILTERED:
                if (input.contains("detectorDataRequestMsg")){    
                    results = keepDetectorDataRequestMsg(input,copyType);
                } else if (input.contains("detectorMaintenanceHistoryRequestMsg")) {
                    results = keepDetectorMaintenanceHistoryRequestMsg(input,copyType);
                } else if (input.contains("deviceInformationRequestMsg")) {
                    results = keepDeviceInformationRequestMsg(input,copyType);
                } else if (input.contains("dMSFontTableRequestMsg")) {
                    results = keepDMSFontTableRequestMsg(input,copyType);
                } else if (input.contains("dMSMessageInventoryRequestMsg")) {
                    results = keepDMSMessageInventoryRequestMsg(input,copyType);
                } else if (input.contains("eventRequestMsg")) {
                    results = keepEventRequestMsg(input,copyType);
                } else if (input.contains("sectionSignalTimingPatternInventoryRequestMsg")) {                    
                    results = keepSectionSignalTimingPatternInventoryRequestMsg(input,copyType);
                } else if (input.contains("ControlRequest")||(input.contains("ControlStatusRequest"))) {                    
                    results = true;
                } else if (input.contains("PriorityQueue")) {                    
                    results = true;

                }
                break;
            default:
                break;                
        }

        return results;
    }

    public static String updatedLine(String input, String lastNonBlankLine, COPYTYPE copyType) {
        String results = input;
        switch (copyType) {
            case FILTERED:
            case NONFILTERED:
                if (input.contains("detectorDataRequestMsg")){       
                    results = updateDetectorDataRequestMsg(input,lastNonBlankLine,copyType);
                } else if (input.contains("detectorMaintenanceHistoryRequestMsg")) {
                    results = updateDetectorMaintenanceHistoryRequestMsg(input,lastNonBlankLine,copyType);
                } else if (input.contains("deviceInformationRequestMsg")) {
                    results = updateDeviceInformationRequestMsg(input,lastNonBlankLine,copyType);
                } else if (input.contains("dMSFontTableRequestMsg")) {
                    results = updateDMSFontTableRequestMsg(input,lastNonBlankLine,copyType);
                } else if (input.contains("dMSMessageInventoryRequestMsg")) {
                    results = updateDMSMessageInventoryRequestMsg(input,lastNonBlankLine,copyType);
                } else if (input.contains("eventRequestMsg")) {
                    results = updateEventRequestMsg(input,lastNonBlankLine,copyType);
                } else if (input.contains("sectionSignalTimingPatternInventoryRequestMsg")) {                    
                    results = updateSectionSignalTimingPatternInventoryRequestMsg(input,lastNonBlankLine,copyType);
                } else if (input.contains("ControlRequest")||input.contains("ControlStatusRequest")) {                    
                    results = updateControlRequestMsg(input,lastNonBlankLine,copyType);
                } else if (input.contains("PriorityQueue")) {                    
                    results = updatePriorityQueueRequestMsg(input,lastNonBlankLine,copyType);
                }
                break;
            default:
                break;                
        }
        
        return results;
    }

    private static boolean keepDetectorDataRequestMsg(String line, COPYTYPE copyType){
        boolean results = true;
            if (line.contains("device-filter")){
                    results = false;
            } else if (line.contains("detector-station-id =")){
                if (copyType.equals(COPYTYPE.NONFILTERED)){
                    results = false;
                }
                results = false;
            } else if (line.contains("detector-data-type =")){
                results = false;
            }
        
        return results;        
    }
    
    private static String updateDetectorDataRequestMsg(String input, String line, COPYTYPE copyType){
        String updateResults = input;
            if (line.contains("detector-station-id")){
                if (copyType.equals(COPYTYPE.FILTERED)){
                    String newLine = line.substring(0,line.indexOf("= "))+"= 3";
                    updateResults = input.replace(line, newLine);                    
                }
            }        
        return updateResults;
    }

    private static boolean keepDetectorMaintenanceHistoryRequestMsg(String line, COPYTYPE copyType){
        boolean results = true;
            if (line.contains("device-filter")){
                    results = false;
            } else if (line.contains("detector-station-id =")){
                if (copyType.equals(COPYTYPE.NONFILTERED)){
                    results = false;
                    // look for a parameter with a path that ends with device-id before the = .
                } 
            }
        
        return results;        
    }
    
    private static String updateDetectorMaintenanceHistoryRequestMsg(String input, String line, COPYTYPE copyType){
        String updateResults = input;
            if (line.contains("detector-station-id")){
                if (copyType.equals(COPYTYPE.FILTERED)){
                    String newLine = line.substring(0,line.indexOf("= "))+"= 3";
                    updateResults = input.replace(line, newLine);                    
                }
            }        
        return updateResults;
    }

    private static boolean keepDeviceInformationRequestMsg(String line, COPYTYPE copyType){
        boolean results = true;
            if (line.contains("device-filter")){
                if (copyType.equals(COPYTYPE.NONFILTERED)){
                    results = false;
                    // look for a parameter with a path that ends with device-id before the = .
                } else if (!line.contains("device-id =")){
                    results = false;
                }
            }
        
        return results;        
    }
    
    private static String updateDeviceInformationRequestMsg(String input, String line, COPYTYPE copyType){
        String updateResults = input;
            if (line.contains("device-filter")){
                if (copyType.equals(COPYTYPE.FILTERED)&& (line.contains("device-id ="))){
                    String newLine = line.substring(0,line.indexOf("= "))+"= 3";
                    updateResults = input.replace(line, newLine);                    
                }
            }        
        return updateResults;
    }

    private static boolean keepDMSFontTableRequestMsg(String line, COPYTYPE copyType){
        boolean results = true;
            if (line.contains("device-filter")){
                if (copyType.equals(COPYTYPE.NONFILTERED)){
                    results = false;
                    // look for a parameter with a path that ends with device-id before the = .
                } else if (!line.contains("device-id =")){
                    results = false;
                }
            } else if (line.contains("fontNumber =")){
                results = false;
            }
        
        return results;        
    }
    
    private static String updateDMSFontTableRequestMsg(String input, String line, COPYTYPE copyType){
        String updateResults = input;
            if (line.contains("device-filter")){
                if (copyType.equals(COPYTYPE.FILTERED)&& (line.contains("device-id ="))){
                    String newLine = line.substring(0,line.indexOf("= "))+"= 3";
                    updateResults = input.replace(line, newLine);                    
                }
            }        
        return updateResults;
    }

    private static boolean keepDMSMessageInventoryRequestMsg(String line, COPYTYPE copyType){
        boolean results = true;
            if (line.contains("device-filter")){
                if (copyType.equals(COPYTYPE.NONFILTERED)){
                    results = false;
                    // look for a parameter with a path that ends with device-id before the = .
                } else if (!line.contains("device-id =")){
                    results = false;
                }
            } else if (line.contains("message-number =")){
                results = false;
            } else if (line.contains("message-memory-type =")){
                results = false;
            }
        
        return results;        
    }
    
    private static String updateDMSMessageInventoryRequestMsg(String input, String line, COPYTYPE copyType){
        String updateResults = input;
            if (line.contains("device-filter")){
                if (copyType.equals(COPYTYPE.FILTERED)&& (line.contains("device-id ="))){
                    String newLine = line.substring(0,line.indexOf("= "))+"= 3";
                    updateResults = input.replace(line, newLine);                    
                }
            }        
        return updateResults;
    }

    private static boolean keepEventRequestMsg(String line, COPYTYPE copyType){
        boolean results = true;
            if (line.contains("message-number")){
                if (copyType.equals(COPYTYPE.NONFILTERED)){
                    results = false;
                } 
            } else if (line.contains("event-id =")){
                results = false;
            } else if (line.contains("response-plan-id =")){
                results = false;
            } else if (line.contains("request-filters")){
                results = false;
            } else if (line.contains("request-locations")){
                results = false;
            } else if (line.contains("request-times")){
                results = false;
            }
        
        return results;        
    }
    
    private static String updateEventRequestMsg(String input, String line, COPYTYPE copyType){
        String updateResults = input;
                if (copyType.equals(COPYTYPE.FILTERED)&& (line.contains("message-number ="))){
                    String newLine = line.substring(0,line.indexOf("= "))+"= 3";
                    updateResults = input.replace(line, newLine);                    
                }
        return updateResults;
    }

    private static boolean keepSectionSignalTimingPatternInventoryRequestMsg(String line, COPYTYPE copyType){
        boolean results = true;
            if (line.contains("device-filter")){
                if (copyType.equals(COPYTYPE.NONFILTERED)){
                    results = false;
                    // look for a parameter with a path that ends with device-id before the = .
                } else if (!line.contains("device-id =")){
                    results = false;
                }
            } else if (line.contains("section-timing-pattern-id =")){
                results = false;
            }
        
        return results;        
    }
    
    private static String updateSectionSignalTimingPatternInventoryRequestMsg(String input, String line, COPYTYPE copyType){
        String updateResults = input;
            if (line.contains("device-filter")){
                if (copyType.equals(COPYTYPE.FILTERED)&& (line.contains("device-id ="))){
                    String newLine = line.substring(0,line.indexOf("= "))+"= 3";
                    updateResults = input.replace(line, newLine);                    
                }
            }        
        return updateResults;
    }

    private static String updateControlRequestMsg(String input, String line, COPYTYPE copyType){
        String updateResults = input;
            if (line.contains("device-id =")){
                    String newLine = line.substring(0,line.indexOf("= "))+"= 3";
                    updateResults = input.replace(line, newLine);                    
            } else if (line.contains("section-id =")){
                    String newLine = line.substring(0,line.indexOf("= "))+"= 3";
                    updateResults = input.replace(line, newLine);                    
            } else if (line.contains("request-id =")){
                    String newLine = line.substring(0,line.indexOf("= "))+"= 3-1";
                    updateResults = input.replace(line, newLine);                                    
            }       
        return updateResults;
    }

    private static String updatePriorityQueueRequestMsg(String input, String line, COPYTYPE copyType){
        String updateResults = input;
            if (line.contains("device-id =")){
                    String newLine = line.substring(0,line.indexOf("= "))+"= 3";
                    updateResults = input.replace(line, newLine);                    
            }       
        return updateResults;
    }    
}
