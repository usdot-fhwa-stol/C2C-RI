/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.generictests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.fhwa.c2cri.centermodel.EmulationDataFileProcessor;
import org.fhwa.c2cri.centermodel.RIEmulation;
import org.fhwa.c2cri.centermodel.RIEmulationEntityValueSet;
import org.fhwa.c2cri.centermodel.RIEmulationParameters;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.tmdd.TMDDAuthenticationProcessor;
import org.fhwa.c2cri.tmdd.emulation.TMDDNRTMSelections;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.DataFilter;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.ValueInSetFilter;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 *
 * @author TransCore ITS, LLC Created: Mar 24, 2016
 */
public class Tester
{

    private static long MAX_RR_RESPONSE_TIME = 750;

    private static String TEST_FILE_PATH = "C:\\TEST\\";

    public static void main(String[] args)
    {
//        System.out.println("Test Result = " + EntityCountTest.test(2, EntityEmulationData.EntityDataType.CCTVINVENTORY, true));
//        System.out.println("Test Result = " + EntityElementCountTest.test(89, EntityEmulationData.EntityDataType.CCTVINVENTORY, "cctv-123456789123456789012345675", true));
        RIEmulation.getInstance().setEmulationEnabled(true);
        ArrayList<TestResult> results = new ArrayList();

        RIParameters.getInstance().getParameterValue("JustTry");

        String entityForTest;

        entityForTest = "CCTV";

        if (entityForTest.equals("CCTV") || entityForTest.equals("ALL"))
        {
            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.CCTVINVENTORY, "string", "tmdd:cCTVInventoryMsg.cctv-inventory-item.device-inventory-header.organization-information.last-update-time.date", "20130531", "strin2", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryRequest", TEST_FILE_PATH + "Need-25-dlCCTVInventoryRequestValid.in", "tmdd:cCTVInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryRequest", TEST_FILE_PATH + "Need-25-dlCCTVInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryRequest", TEST_FILE_PATH + "Need-25-dlCCTVInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
//        results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryRequest", TEST_FILE_PATH+"Need-25-dlCCTVInventoryRequestInValid-6.in", "tmdd:errorReportMsg", "permission not granted for request","string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryRequest", TEST_FILE_PATH + "Need-25-dlCCTVInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            // Had to modify a filter value within the request message.
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryRequest", TEST_FILE_PATH + "Need-25-dlCCTVInventoryRequestInValid-8.in", "tmdd:errorReportMsg", "no valid data available", "string"));
//        results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVStatusRequest", TEST_FILE_PATH+"Need-27-dlCCTVStatusRequestValid.in", "tmdd:cCTVStatusMsg", "","string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVControlRequest", TEST_FILE_PATH + "Need-28-dlCCTVControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-30-dlDeviceCancelControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-25-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-25-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-25-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
//        results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryRequest", TEST_FILE_PATH+"Need-25-dlCCTVInventoryRequestInValid-6.in", "tmdd:errorReportMsg", "permission not granted for request","string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-25-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            // Had to modify a filter value within the request message.
//        results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH+"Need-25-dlDeviceInformationSubscriptionInValid-8.in", "tmdd:errorReportMsg", "no valid data available","string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryUpdate", TEST_FILE_PATH + "Need-25-dlDeviceInformationSubscriptionValid.in", "tmdd:cCTVInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryUpdate", TEST_FILE_PATH + "Need-25-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryUpdate", TEST_FILE_PATH + "Need-25-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
//        results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryRequest", TEST_FILE_PATH+"Need-25-dlCCTVInventoryRequestInValid-6.in", "tmdd:errorReportMsg", "permission not granted for request","string"));
//        results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryUpdate", TEST_FILE_PATH+"Need-25-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized","string2"));
            // Had to modify a filter value within the request message.
//        results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH+"Need-25-dlDeviceInformationSubscriptionInValid-8.in", "tmdd:errorReportMsg", "no valid data available","string"));
            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.CCTVSTATUS, "string", "tmdd:cCTVStatusMsg.cctv-status-item.device-status-header.organization-information.last-update-time.date", "20130531", "strin2", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVStatusRequest", TEST_FILE_PATH + "Need-27-dlCCTVStatusRequestValid.in", "tmdd:cCTVStatusMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVStatusRequest", TEST_FILE_PATH + "Need-27-dlCCTVStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVStatusRequest", TEST_FILE_PATH + "Need-27-dlCCTVStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
//        results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVInventoryRequest", TEST_FILE_PATH+"Need-25-dlCCTVInventoryRequestInValid-6.in", "tmdd:errorReportMsg", "permission not granted for request","string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVStatusRequest", TEST_FILE_PATH + "Need-27-dlCCTVStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            // Had to modify a filter value within the request message.
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVStatusRequest", TEST_FILE_PATH + "Need-27-dlCCTVStatusRequestInValid-8.in", "tmdd:errorReportMsg", "no valid data available", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-27-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-27-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-27-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
//        results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVInventoryRequest", TEST_FILE_PATH+"Need-25-dlCCTVInventoryRequestInValid-6.in", "tmdd:errorReportMsg", "permission not granted for request","string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-27-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            // Had to modify a filter value within the request message.
//        results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH+"Need-25-dlDeviceInformationSubscriptionInValid-8.in", "tmdd:errorReportMsg", "no valid data available","string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVStatusUpdate", TEST_FILE_PATH + "Need-27-dlDeviceInformationSubscriptionValid.in", "tmdd:cCTVStatusMsg", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVStatusUpdate", TEST_FILE_PATH + "Need-27-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVStatusUpdate", TEST_FILE_PATH + "Need-27-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));

            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVStatusRequest", TEST_FILE_PATH + "Need-27-dlCCTVStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVStatusRequest", TEST_FILE_PATH + "Need-27-dlCCTVStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVStatusRequest", TEST_FILE_PATH + "Need-27-dlCCTVStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVStatusRequest", TEST_FILE_PATH + "Need-27-dlCCTVStatusRequestValid.in", "tmdd:cCTVStatusMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-27-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVStatusUpdate", TEST_FILE_PATH + "Need-27-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-27-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVStatusUpdate", TEST_FILE_PATH + "Need-27-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-27-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-27-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVStatusUpdate", TEST_FILE_PATH + "Need-27-dlDeviceInformationSubscriptionValid.in", "tmdd:cCTVStatusMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVControlRequest", TEST_FILE_PATH + "Need-28-dlCCTVControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVControlRequest", TEST_FILE_PATH + "Need-28-dlCCTVControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVControlRequest", TEST_FILE_PATH + "Need-28-dlCCTVControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVControlRequest", TEST_FILE_PATH + "Need-28-dlCCTVControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-29-dlDeviceControlStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-29-dlDeviceControlStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-29-dlDeviceControlStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-29-dlDeviceControlStatusRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-30-dlDeviceCancelControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-30-dlDeviceCancelControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-30-dlDeviceCancelControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-30-dlDeviceCancelControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryRequest", TEST_FILE_PATH + "Need-25-dlCCTVInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryRequest", TEST_FILE_PATH + "Need-25-dlCCTVInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryRequest", TEST_FILE_PATH + "Need-25-dlCCTVInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryRequest", TEST_FILE_PATH + "Need-25-dlCCTVInventoryRequestValid.in", "tmdd:cCTVInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-25-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryUpdate", TEST_FILE_PATH + "Need-25-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-25-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryUpdate", TEST_FILE_PATH + "Need-25-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-25-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-25-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryUpdate", TEST_FILE_PATH + "Need-25-dlDeviceInformationSubscriptionValid.in", "tmdd:cCTVInventoryMsg", "", "string"));

            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-26-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryUpdate", TEST_FILE_PATH + "Need-26-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-26-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryUpdate", TEST_FILE_PATH + "Need-26-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-26-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-26-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.CCTVINVENTORY, "dlCCTVInventoryUpdate", TEST_FILE_PATH + "Need-26-dlDeviceInformationSubscriptionValid.in", "tmdd:cCTVInventoryMsg", "", "string"));
        }

        if (entityForTest.equals("DETECTOR") || entityForTest.equals("ALL"))
        {
            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.DETECTORDATA, "string", "tmdd:detectorDataMsg.detector-data-item.organization-information.last-update-time.date", "20130531", "strin2", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataRequest", TEST_FILE_PATH + "Need-23-dlDetectorDataRequestValid.in", "tmdd:detectorDataMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataRequest", TEST_FILE_PATH + "Need-23-dlDetectorDataRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataRequest", TEST_FILE_PATH + "Need-23-dlDetectorDataRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
//        results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVInventoryRequest", TEST_FILE_PATH+"Need-25-dlDetectorDataRequestInValid-6.in", "tmdd:errorReportMsg", "permission not granted for request","string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataRequest", TEST_FILE_PATH + "Need-23-dlDetectorDataRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            // Had to modify a filter value within the request message.
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataRequest", TEST_FILE_PATH + "Need-23-dlDetectorDataRequestInValid-8.in", "tmdd:errorReportMsg", "no valid data available", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataSubscription", TEST_FILE_PATH + "Need-23-dlDetectorDataSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataSubscription", TEST_FILE_PATH + "Need-23-dlDetectorDataSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataSubscription", TEST_FILE_PATH + "Need-23-dlDetectorDataSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
//        results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlCCTVInventoryRequest", TEST_FILE_PATH+"Need-25-dlDetectorDataRequestInValid-6.in", "tmdd:errorReportMsg", "permission not granted for request","string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataSubscription", TEST_FILE_PATH + "Need-23-dlDetectorDataSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            // Had to modify a filter value within the request message.
//        results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.CCTVSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH+"Need-25-dlDetectorDataSubscriptionInValid-8.in", "tmdd:errorReportMsg", "no valid data available","string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataUpdate", TEST_FILE_PATH + "Need-23-dlDetectorDataSubscriptionValid.in", "tmdd:detectorDataMsg", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataUpdate", TEST_FILE_PATH + "Need-23-dlDetectorDataSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataUpdate", TEST_FILE_PATH + "Need-23-dlDetectorDataSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));

            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataRequest", TEST_FILE_PATH + "Need-105-dlDetectorDataRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataRequest", TEST_FILE_PATH + "Need-105-dlDetectorDataRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataRequest", TEST_FILE_PATH + "Need-105-dlDetectorDataRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataRequest", TEST_FILE_PATH + "Need-105-dlDetectorDataRequestValid.in", "tmdd:detectorDataMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataSubscription", TEST_FILE_PATH + "Need-105-dlDetectorDataSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataUpdate", TEST_FILE_PATH + "Need-105-dlDetectorDataSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataSubscription", TEST_FILE_PATH + "Need-105-dlDetectorDataSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataUpdate", TEST_FILE_PATH + "Need-105-dlDetectorDataSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataSubscription", TEST_FILE_PATH + "Need-105-dlDetectorDataSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataSubscription", TEST_FILE_PATH + "Need-105-dlDetectorDataSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataUpdate", TEST_FILE_PATH + "Need-105-dlDetectorDataSubscriptionValid.in", "tmdd:detectorDataMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataRequest", TEST_FILE_PATH + "Need-23-dlDetectorDataRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataRequest", TEST_FILE_PATH + "Need-23-dlDetectorDataRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataRequest", TEST_FILE_PATH + "Need-23-dlDetectorDataRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataRequest", TEST_FILE_PATH + "Need-23-dlDetectorDataRequestValid.in", "tmdd:detectorDataMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataSubscription", TEST_FILE_PATH + "Need-23-dlDetectorDataSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataUpdate", TEST_FILE_PATH + "Need-23-dlDetectorDataSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataSubscription", TEST_FILE_PATH + "Need-23-dlDetectorDataSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataUpdate", TEST_FILE_PATH + "Need-23-dlDetectorDataSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataSubscription", TEST_FILE_PATH + "Need-23-dlDetectorDataSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataSubscription", TEST_FILE_PATH + "Need-23-dlDetectorDataSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORDATA, "dlDetectorDataUpdate", TEST_FILE_PATH + "Need-23-dlDetectorDataSubscriptionValid.in", "tmdd:detectorDataMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.DETECTORINVENTORY, "string", "tmdd:detectorInventoryMsg.detector-inventory-item.detector-station-inventory-header.last-update-time.date", "20130531", "strin2", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryRequest", TEST_FILE_PATH + "Need-18-dlDetectorInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryRequest", TEST_FILE_PATH + "Need-18-dlDetectorInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryRequest", TEST_FILE_PATH + "Need-18-dlDetectorInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryRequest", TEST_FILE_PATH + "Need-18-dlDetectorInventoryRequestValid.in", "tmdd:detectorInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-18-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryUpdate", TEST_FILE_PATH + "Need-18-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-18-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryUpdate", TEST_FILE_PATH + "Need-18-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-18-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-18-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryUpdate", TEST_FILE_PATH + "Need-18-dlDeviceInformationSubscriptionValid.in", "tmdd:detectorInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-19-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryUpdate", TEST_FILE_PATH + "Need-19-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-19-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryUpdate", TEST_FILE_PATH + "Need-19-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-19-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-19-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryUpdate", TEST_FILE_PATH + "Need-19-dlDeviceInformationSubscriptionValid.in", "tmdd:detectorInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryRequest", TEST_FILE_PATH + "Need-22-dlDetectorInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryRequest", TEST_FILE_PATH + "Need-22-dlDetectorInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryRequest", TEST_FILE_PATH + "Need-22-dlDetectorInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryRequest", TEST_FILE_PATH + "Need-22-dlDetectorInventoryRequestValid.in", "tmdd:detectorInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-22-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryUpdate", TEST_FILE_PATH + "Need-22-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-22-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryUpdate", TEST_FILE_PATH + "Need-22-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-22-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-22-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORINVENTORY, "dlDetectorInventoryUpdate", TEST_FILE_PATH + "Need-22-dlDeviceInformationSubscriptionValid.in", "tmdd:detectorInventoryMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.DETECTORMAINTENANCEHISTORY, "string", "tmdd:detectorMaintenanceHistoryMsg.detector-maintenance-history-item.organization-information.last-update-time.date", "20130531", "strin2", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORMAINTENANCEHISTORY, "dlDetectorMaintenanceHistoryRequest", TEST_FILE_PATH + "Need-24-dlDetectorMaintenanceHistoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORMAINTENANCEHISTORY, "dlDetectorMaintenanceHistoryRequest", TEST_FILE_PATH + "Need-24-dlDetectorMaintenanceHistoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORMAINTENANCEHISTORY, "dlDetectorMaintenanceHistoryRequest", TEST_FILE_PATH + "Need-24-dlDetectorMaintenanceHistoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORMAINTENANCEHISTORY, "dlDetectorMaintenanceHistoryRequest", TEST_FILE_PATH + "Need-24-dlDetectorMaintenanceHistoryRequestValid.in", "tmdd:detectorMaintenanceHistoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORMAINTENANCEHISTORY, "dlDetectorMaintenanceHistoryRequest", TEST_FILE_PATH + "Need-108-dlDetectorMaintenanceHistoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORMAINTENANCEHISTORY, "dlDetectorMaintenanceHistoryRequest", TEST_FILE_PATH + "Need-108-dlDetectorMaintenanceHistoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORMAINTENANCEHISTORY, "dlDetectorMaintenanceHistoryRequest", TEST_FILE_PATH + "Need-108-dlDetectorMaintenanceHistoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORMAINTENANCEHISTORY, "dlDetectorMaintenanceHistoryRequest", TEST_FILE_PATH + "Need-108-dlDetectorMaintenanceHistoryRequestValid.in", "tmdd:detectorMaintenanceHistoryMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.DETECTORSTATUS, "string", "tmdd:detectorStatusMsg.detector-status-item.detector-station-status-header.organization-information.last-update-time.date", "20130531", "strin2", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORSTATUS, "dlDetectorStatusRequest", TEST_FILE_PATH + "Need-20-dlDetectorStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORSTATUS, "dlDetectorStatusRequest", TEST_FILE_PATH + "Need-20-dlDetectorStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORSTATUS, "dlDetectorStatusRequest", TEST_FILE_PATH + "Need-20-dlDetectorStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DETECTORSTATUS, "dlDetectorStatusRequest", TEST_FILE_PATH + "Need-20-dlDetectorStatusRequestValid.in", "tmdd:detectorStatusMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-20-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORSTATUS, "dlDetectorStatusUpdate", TEST_FILE_PATH + "Need-20-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-20-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORSTATUS, "dlDetectorStatusUpdate", TEST_FILE_PATH + "Need-20-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-20-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DETECTORSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-20-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DETECTORSTATUS, "dlDetectorStatusUpdate", TEST_FILE_PATH + "Need-20-dlDeviceInformationSubscriptionValid.in", "tmdd:detectorStatusMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA, "string", "tmdd:archivedDataProcessingDocumentationMetadataMsg.archived-data-processing-documentation-metadata-item.last-update-date.date", "stringst", "strin2", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA, "dlArchivedDataProcessingDocumentationMetadataRequest", TEST_FILE_PATH + "Need-109-dlArchivedDataProcessingDocumentationMetadataRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA, "dlArchivedDataProcessingDocumentationMetadataRequest", TEST_FILE_PATH + "Need-109-dlArchivedDataProcessingDocumentationMetadataRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA, "dlArchivedDataProcessingDocumentationMetadataRequest", TEST_FILE_PATH + "Need-109-dlArchivedDataProcessingDocumentationMetadataRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA, "dlArchivedDataProcessingDocumentationMetadataRequest", TEST_FILE_PATH + "Need-109-dlArchivedDataProcessingDocumentationMetadataRequestValid.in", "tmdd:archivedDataProcessingDocumentationMetadataMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA, "dlArchivedDataProcessingDocumentationMetadataRequest", TEST_FILE_PATH + "Need-21-dlArchivedDataProcessingDocumentationMetadataRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA, "dlArchivedDataProcessingDocumentationMetadataRequest", TEST_FILE_PATH + "Need-21-dlArchivedDataProcessingDocumentationMetadataRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA, "dlArchivedDataProcessingDocumentationMetadataRequest", TEST_FILE_PATH + "Need-21-dlArchivedDataProcessingDocumentationMetadataRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA, "dlArchivedDataProcessingDocumentationMetadataRequest", TEST_FILE_PATH + "Need-21-dlArchivedDataProcessingDocumentationMetadataRequestValid.in", "tmdd:archivedDataProcessingDocumentationMetadataMsg", "", "string"));
        }

        if (entityForTest.equals("EVENTS") || entityForTest.equals("ALL"))
        {

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.EVENTSACTIONLOG, "5", "tmdd:actionLogMsg.log-entry.action-time.date", "stringst", "6", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSACTIONLOG, "dlActionLogRequest", TEST_FILE_PATH + "Need-9-dlActionLogRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSACTIONLOG, "dlActionLogRequest", TEST_FILE_PATH + "Need-9-dlActionLogRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSACTIONLOG, "dlActionLogRequest", TEST_FILE_PATH + "Need-9-dlActionLogRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSACTIONLOG, "dlActionLogRequest", TEST_FILE_PATH + "Need-9-dlActionLogRequestValid.in", "tmdd:actionLogMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSACTIONLOG, "dlActionLogSubscription", TEST_FILE_PATH + "Need-9-dlActionLogSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSACTIONLOG, "dlActionLogUpdate", TEST_FILE_PATH + "Need-9-dlActionLogSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSACTIONLOG, "dlActionLogSubscription", TEST_FILE_PATH + "Need-9-dlActionLogSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSACTIONLOG, "dlActionLogUpdate", TEST_FILE_PATH + "Need-9-dlActionLogSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSACTIONLOG, "dlActionLogSubscription", TEST_FILE_PATH + "Need-9-dlActionLogSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSACTIONLOG, "dlActionLogSubscription", TEST_FILE_PATH + "Need-9-dlActionLogSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSACTIONLOG, "dlActionLogUpdate", TEST_FILE_PATH + "Need-9-dlActionLogSubscriptionValid.in", "tmdd:actionLogMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.EVENTSINDEX, "string", "tmdd:eventIndexMsg.eventIndex.file-update-time.date", "stringst", "strin2", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSINDEX, "dlEventIndexRequest", TEST_FILE_PATH + "Need-5-dlEventIndexRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSINDEX, "dlEventIndexRequest", TEST_FILE_PATH + "Need-5-dlEventIndexRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSINDEX, "dlEventIndexRequest", TEST_FILE_PATH + "Need-5-dlEventIndexRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSINDEX, "dlEventIndexRequest", TEST_FILE_PATH + "Need-5-dlEventIndexRequestValid.in", "tmdd:eventIndexMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSINDEX, "dlEventIndexSubscription", TEST_FILE_PATH + "Need-5-dlEventIndexSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSINDEX, "dlEventIndexUpdate", TEST_FILE_PATH + "Need-5-dlEventIndexSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSINDEX, "dlEventIndexSubscription", TEST_FILE_PATH + "Need-5-dlEventIndexSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSINDEX, "dlEventIndexUpdate", TEST_FILE_PATH + "Need-5-dlEventIndexSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSINDEX, "dlEventIndexSubscription", TEST_FILE_PATH + "Need-5-dlEventIndexSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSINDEX, "dlEventIndexSubscription", TEST_FILE_PATH + "Need-5-dlEventIndexSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSINDEX, "dlEventIndexUpdate", TEST_FILE_PATH + "Need-5-dlEventIndexSubscriptionValid.in", "tmdd:eventIndexMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.EVENTSUPDATES, "7", "tmdd:fEUMsg.FEU.message-header.message-expiry-time.date", "stringst", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateRequest", TEST_FILE_PATH + "Need-6-dlFullEventUpdateRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateRequest", TEST_FILE_PATH + "Need-6-dlFullEventUpdateRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateRequest", TEST_FILE_PATH + "Need-6-dlFullEventUpdateRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateRequest", TEST_FILE_PATH + "Need-6-dlFullEventUpdateRequestValid.in", "tmdd:fEUMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateSubscription", TEST_FILE_PATH + "Need-6-dlFullEventUpdateSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateUpdate", TEST_FILE_PATH + "Need-6-dlFullEventUpdateSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateSubscription", TEST_FILE_PATH + "Need-6-dlFullEventUpdateSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateUpdate", TEST_FILE_PATH + "Need-6-dlFullEventUpdateSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateSubscription", TEST_FILE_PATH + "Need-6-dlFullEventUpdateSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateSubscription", TEST_FILE_PATH + "Need-6-dlFullEventUpdateSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateUpdate", TEST_FILE_PATH + "Need-6-dlFullEventUpdateSubscriptionValid.in", "tmdd:fEUMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateRequest", TEST_FILE_PATH + "Need-7-dlFullEventUpdateRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateRequest", TEST_FILE_PATH + "Need-7-dlFullEventUpdateRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateRequest", TEST_FILE_PATH + "Need-7-dlFullEventUpdateRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateRequest", TEST_FILE_PATH + "Need-7-dlFullEventUpdateRequestValid.in", "tmdd:fEUMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateSubscription", TEST_FILE_PATH + "Need-7-dlFullEventUpdateSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateUpdate", TEST_FILE_PATH + "Need-7-dlFullEventUpdateSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateSubscription", TEST_FILE_PATH + "Need-7-dlFullEventUpdateSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateUpdate", TEST_FILE_PATH + "Need-7-dlFullEventUpdateSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateSubscription", TEST_FILE_PATH + "Need-7-dlFullEventUpdateSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateSubscription", TEST_FILE_PATH + "Need-7-dlFullEventUpdateSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateUpdate", TEST_FILE_PATH + "Need-7-dlFullEventUpdateSubscriptionValid.in", "tmdd:fEUMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateRequest", TEST_FILE_PATH + "Need-8-dlFullEventUpdateRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateRequest", TEST_FILE_PATH + "Need-8-dlFullEventUpdateRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateRequest", TEST_FILE_PATH + "Need-8-dlFullEventUpdateRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateRequest", TEST_FILE_PATH + "Need-8-dlFullEventUpdateRequestValid.in", "tmdd:fEUMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateSubscription", TEST_FILE_PATH + "Need-8-dlFullEventUpdateSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateUpdate", TEST_FILE_PATH + "Need-8-dlFullEventUpdateSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateSubscription", TEST_FILE_PATH + "Need-8-dlFullEventUpdateSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateUpdate", TEST_FILE_PATH + "Need-8-dlFullEventUpdateSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateSubscription", TEST_FILE_PATH + "Need-8-dlFullEventUpdateSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateSubscription", TEST_FILE_PATH + "Need-8-dlFullEventUpdateSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateUpdate", TEST_FILE_PATH + "Need-8-dlFullEventUpdateSubscriptionValid.in", "tmdd:fEUMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateRequest", TEST_FILE_PATH + "Need-111-dlFullEventUpdateRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateRequest", TEST_FILE_PATH + "Need-111-dlFullEventUpdateRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateRequest", TEST_FILE_PATH + "Need-111-dlFullEventUpdateRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateRequest", TEST_FILE_PATH + "Need-111-dlFullEventUpdateRequestValid.in", "tmdd:fEUMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateSubscription", TEST_FILE_PATH + "Need-111-dlFullEventUpdateSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateUpdate", TEST_FILE_PATH + "Need-111-dlFullEventUpdateSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateSubscription", TEST_FILE_PATH + "Need-111-dlFullEventUpdateSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateUpdate", TEST_FILE_PATH + "Need-111-dlFullEventUpdateSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateSubscription", TEST_FILE_PATH + "Need-111-dlFullEventUpdateSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateSubscription", TEST_FILE_PATH + "Need-111-dlFullEventUpdateSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.EVENTSUPDATES, "dlFullEventUpdateUpdate", TEST_FILE_PATH + "Need-111-dlFullEventUpdateSubscriptionValid.in", "tmdd:fEUMsg", "", "string"));

        }

        if (entityForTest.equals("DMS") || entityForTest.equals("ALL"))
        {
            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.DMSFONTTABLE, "string", "tmdd:dMSFontTableMsg.dms-font-table-item.organization-information.last-update-time.date", "20130531", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSFONTTABLE, "dlDMSFontTableRequest", TEST_FILE_PATH + "Need-44-dlDMSFontTableRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSFONTTABLE, "dlDMSFontTableRequest", TEST_FILE_PATH + "Need-44-dlDMSFontTableRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSFONTTABLE, "dlDMSFontTableRequest", TEST_FILE_PATH + "Need-44-dlDMSFontTableRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSFONTTABLE, "dlDMSFontTableRequest", TEST_FILE_PATH + "Need-44-dlDMSFontTableRequestValid.in", "tmdd:dMSFontTableMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSFONTTABLE, "dlDMSFontTableRequest", TEST_FILE_PATH + "Need-46-dlDMSFontTableRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSFONTTABLE, "dlDMSFontTableRequest", TEST_FILE_PATH + "Need-46-dlDMSFontTableRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSFONTTABLE, "dlDMSFontTableRequest", TEST_FILE_PATH + "Need-46-dlDMSFontTableRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSFONTTABLE, "dlDMSFontTableRequest", TEST_FILE_PATH + "Need-46-dlDMSFontTableRequestValid.in", "tmdd:dMSFontTableMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.DMSINVENTORY, "string", "tmdd:dMSInventoryMsg.dms-inventory-item.device-inventory-header.device-location.latitude", "33964380", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSControlRequest", TEST_FILE_PATH + "Need-40-dlDMSControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSControlRequest", TEST_FILE_PATH + "Need-40-dlDMSControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSControlRequest", TEST_FILE_PATH + "Need-40-dlDMSControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSControlRequest", TEST_FILE_PATH + "Need-40-dlDMSControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-41-dlDeviceControlStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-41-dlDeviceControlStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-41-dlDeviceControlStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-41-dlDeviceControlStatusRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSPriorityQueueRequest", TEST_FILE_PATH + "Need-42-dlDMSPriorityQueueRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSPriorityQueueRequest", TEST_FILE_PATH + "Need-42-dlDMSPriorityQueueRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSPriorityQueueRequest", TEST_FILE_PATH + "Need-42-dlDMSPriorityQueueRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSPriorityQueueRequest", TEST_FILE_PATH + "Need-42-dlDMSPriorityQueueRequestValid.in", "tmdd:dMSPriorityQueueMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-43-dlDeviceCancelControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-43-dlDeviceCancelControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-43-dlDeviceCancelControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-43-dlDeviceCancelControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-37-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSInventoryUpdate", TEST_FILE_PATH + "Need-37-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-37-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSInventoryUpdate", TEST_FILE_PATH + "Need-37-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-37-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-37-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSInventoryUpdate", TEST_FILE_PATH + "Need-37-dlDeviceInformationSubscriptionValid.in", "tmdd:dMSInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSInventoryRequest", TEST_FILE_PATH + "Need-37-dlDMSInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSInventoryRequest", TEST_FILE_PATH + "Need-37-dlDMSInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSInventoryRequest", TEST_FILE_PATH + "Need-37-dlDMSInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSInventoryRequest", TEST_FILE_PATH + "Need-37-dlDMSInventoryRequestValid.in", "tmdd:dMSInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-38-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSInventoryUpdate", TEST_FILE_PATH + "Need-38-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-38-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSInventoryUpdate", TEST_FILE_PATH + "Need-38-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-38-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-38-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DMSINVENTORY, "dlDMSInventoryUpdate", TEST_FILE_PATH + "Need-38-dlDeviceInformationSubscriptionValid.in", "tmdd:dMSInventoryMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.DMSMESSAGEAPPEARANCE, "string", "tmdd:dMSMessageAppearanceMsg.organization-information.last-update-time.date", "20130531", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSMESSAGEAPPEARANCE, "dlDMSMessageAppearanceRequest", TEST_FILE_PATH + "Need-44-dlDMSMessageAppearanceRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSMESSAGEAPPEARANCE, "dlDMSMessageAppearanceRequest", TEST_FILE_PATH + "Need-44-dlDMSMessageAppearanceRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSMESSAGEAPPEARANCE, "dlDMSMessageAppearanceRequest", TEST_FILE_PATH + "Need-44-dlDMSMessageAppearanceRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSMESSAGEAPPEARANCE, "dlDMSMessageAppearanceRequest", TEST_FILE_PATH + "Need-44-dlDMSMessageAppearanceRequestValid.in", "tmdd:dMSFontTableMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.DMSMESSAGEINVENTORY, "string", "tmdd:dMSMessageInventoryMsg.dms-message-inventory-item.message-run-time-priority", "6", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSMESSAGEINVENTORY, "dlDMSMessageInventoryRequest", TEST_FILE_PATH + "Need-45-dlDMSMessageInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSMESSAGEINVENTORY, "dlDMSMessageInventoryRequest", TEST_FILE_PATH + "Need-45-dlDMSMessageInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSMESSAGEINVENTORY, "dlDMSMessageInventoryRequest", TEST_FILE_PATH + "Need-45-dlDMSMessageInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSMESSAGEINVENTORY, "dlDMSMessageInventoryRequest", TEST_FILE_PATH + "Need-45-dlDMSMessageInventoryRequestValid.in", "tmdd:dMSMessageInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DMSMESSAGEINVENTORY, "dlDMSMessageInventorySubscription", TEST_FILE_PATH + "Need-45-dlDMSMessageInventorySubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DMSMESSAGEINVENTORY, "dlDMSMessageInventoryUpdate", TEST_FILE_PATH + "Need-45-dlDMSMessageInventorySubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DMSMESSAGEINVENTORY, "dlDMSMessageInventorySubscription", TEST_FILE_PATH + "Need-45-dlDMSMessageInventorySubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DMSMESSAGEINVENTORY, "dlDMSMessageInventoryUpdate", TEST_FILE_PATH + "Need-45-dlDMSMessageInventorySubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DMSMESSAGEINVENTORY, "dlDMSMessageInventorySubscription", TEST_FILE_PATH + "Need-45-dlDMSMessageInventorySubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DMSMESSAGEINVENTORY, "dlDMSMessageInventorySubscription", TEST_FILE_PATH + "Need-45-dlDMSMessageInventorySubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DMSMESSAGEINVENTORY, "dlDMSMessageInventoryUpdate", TEST_FILE_PATH + "Need-45-dlDMSMessageInventorySubscriptionValid.in", "tmdd:dMSMessageInventoryMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.DMSSTATUS, "string", "tmdd:dMSStatusMsg.dms-status-item.device-status-header.organization-information.last-update-time.date", "20130531", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DMSSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-39-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DMSSTATUS, "dlDMSStatusUpdate", TEST_FILE_PATH + "Need-39-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DMSSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-39-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DMSSTATUS, "dlDMSStatusUpdate", TEST_FILE_PATH + "Need-39-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DMSSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-39-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.DMSSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-39-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.DMSSTATUS, "dlDMSStatusUpdate", TEST_FILE_PATH + "Need-39-dlDeviceInformationSubscriptionValid.in", "tmdd:dMSStatusMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSSTATUS, "dlDMSStatusRequest", TEST_FILE_PATH + "Need-39-dlDMSStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSSTATUS, "dlDMSStatusRequest", TEST_FILE_PATH + "Need-39-dlDMSStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSSTATUS, "dlDMSStatusRequest", TEST_FILE_PATH + "Need-39-dlDMSStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.DMSSTATUS, "dlDMSStatusRequest", TEST_FILE_PATH + "Need-39-dlDMSStatusRequestValid.in", "tmdd:dMSStatusMsg", "", "string"));
        }

        if (entityForTest.equals("ESS") || entityForTest.equals("ALL"))
        {
            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.ESSINVENTORY, "string", "tmdd:eSSInventoryMsg.ess-inventory-item.device-inventory-header.device-location.horizontalDatum", "nad27", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-47-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlESSInventoryUpdate", TEST_FILE_PATH + "Need-47-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-47-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlESSInventoryUpdate", TEST_FILE_PATH + "Need-47-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-47-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-47-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlESSInventoryUpdate", TEST_FILE_PATH + "Need-47-dlDeviceInformationSubscriptionValid.in", "tmdd:eSSInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlESSInventoryRequest", TEST_FILE_PATH + "Need-47-dlESSInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlESSInventoryRequest", TEST_FILE_PATH + "Need-47-dlESSInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlESSInventoryRequest", TEST_FILE_PATH + "Need-47-dlESSInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlESSInventoryRequest", TEST_FILE_PATH + "Need-47-dlESSInventoryRequestValid.in", "tmdd:eSSInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-48-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlESSInventoryUpdate", TEST_FILE_PATH + "Need-48-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-48-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlESSInventoryUpdate", TEST_FILE_PATH + "Need-48-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-48-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-48-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.ESSINVENTORY, "dlESSInventoryUpdate", TEST_FILE_PATH + "Need-48-dlDeviceInformationSubscriptionValid.in", "tmdd:eSSInventoryMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.ESSOBSERVATIONMETADATA, "string", "tmdd:eSSObservationMetadataMsg.ess-observation-metadata-item.ess-sensor-metadata-list.ess-sensor-metadata.sensor-index", "3", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSOBSERVATIONMETADATA, "dlESSObservationMetadataRequest", TEST_FILE_PATH + "Need-53-dlESSObservationMetadataRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSOBSERVATIONMETADATA, "dlESSObservationMetadataRequest", TEST_FILE_PATH + "Need-53-dlESSObservationMetadataRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSOBSERVATIONMETADATA, "dlESSObservationMetadataRequest", TEST_FILE_PATH + "Need-53-dlESSObservationMetadataRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSOBSERVATIONMETADATA, "dlESSObservationMetadataRequest", TEST_FILE_PATH + "Need-53-dlESSObservationMetadataRequestValid.in", "tmdd:eSSObservationMetadataMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSOBSERVATIONMETADATA, "dlESSObservationMetadataRequest", TEST_FILE_PATH + "Need-51-dlESSObservationMetadataRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSOBSERVATIONMETADATA, "dlESSObservationMetadataRequest", TEST_FILE_PATH + "Need-51-dlESSObservationMetadataRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSOBSERVATIONMETADATA, "dlESSObservationMetadataRequest", TEST_FILE_PATH + "Need-51-dlESSObservationMetadataRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSOBSERVATIONMETADATA, "dlESSObservationMetadataRequest", TEST_FILE_PATH + "Need-51-dlESSObservationMetadataRequestValid.in", "tmdd:eSSObservationMetadataMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "string", "tmdd:eSSObservationReportMsg.ess-observation-report-item.organization-information.last-update-time.date", "20130531", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-50-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlESSObservationReportUpdate", TEST_FILE_PATH + "Need-50-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-50-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlESSObservationReportUpdate", TEST_FILE_PATH + "Need-50-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-50-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-50-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlESSObservationReportUpdate", TEST_FILE_PATH + "Need-50-dlDeviceInformationSubscriptionValid.in", "tmdd:eSSObservationReportMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlESSObservationReportRequest", TEST_FILE_PATH + "Need-50-dlESSObservationReportRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlESSObservationReportRequest", TEST_FILE_PATH + "Need-50-dlESSObservationReportRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlESSObservationReportRequest", TEST_FILE_PATH + "Need-50-dlESSObservationReportRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlESSObservationReportRequest", TEST_FILE_PATH + "Need-50-dlESSObservationReportRequestValid.in", "tmdd:eSSObservationReportMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-52-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlESSObservationReportUpdate", TEST_FILE_PATH + "Need-52-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-52-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlESSObservationReportUpdate", TEST_FILE_PATH + "Need-52-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-52-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-52-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlESSObservationReportUpdate", TEST_FILE_PATH + "Need-52-dlDeviceInformationSubscriptionValid.in", "tmdd:eSSObservationReportMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlESSObservationReportRequest", TEST_FILE_PATH + "Need-52-dlESSObservationReportRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlESSObservationReportRequest", TEST_FILE_PATH + "Need-52-dlESSObservationReportRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlESSObservationReportRequest", TEST_FILE_PATH + "Need-52-dlESSObservationReportRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSOBSERVATIONREPORT, "dlESSObservationReportRequest", TEST_FILE_PATH + "Need-52-dlESSObservationReportRequestValid.in", "tmdd:eSSObservationReportMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.ESSSTATUS, "string", "tmdd:eSSStatusMsg.ess-status-item.ess-station-status-header.device-status", "other", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-49-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.ESSSTATUS, "dlESSStatusUpdate", TEST_FILE_PATH + "Need-49-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-49-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.ESSSTATUS, "dlESSStatusUpdate", TEST_FILE_PATH + "Need-49-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-49-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.ESSSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-49-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.ESSSTATUS, "dlESSStatusUpdate", TEST_FILE_PATH + "Need-49-dlDeviceInformationSubscriptionValid.in", "tmdd:eSSStatusMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSSTATUS, "dlESSStatusRequest", TEST_FILE_PATH + "Need-49-dlESSStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSSTATUS, "dlESSStatusRequest", TEST_FILE_PATH + "Need-49-dlESSStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSSTATUS, "dlESSStatusRequest", TEST_FILE_PATH + "Need-49-dlESSStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.ESSSTATUS, "dlESSStatusRequest", TEST_FILE_PATH + "Need-49-dlESSStatusRequestValid.in", "tmdd:eSSStatusMsg", "", "string"));
        }

        if (entityForTest.equals("GATE") || entityForTest.equals("ALL"))
        {
            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.GATECONTROLSCHEDULE, "string", "tmdd:gateControlScheduleMsg.gate-control-schedule-item.device-control-schedule-header.day-plan-hour", "6", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.GATECONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-60-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.GATECONTROLSCHEDULE, "dlGateControlScheduleUpdate", TEST_FILE_PATH + "Need-60-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.GATECONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-60-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.GATECONTROLSCHEDULE, "dlGateControlScheduleUpdate", TEST_FILE_PATH + "Need-60-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.GATECONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-60-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.GATECONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-60-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.GATECONTROLSCHEDULE, "dlGateControlScheduleUpdate", TEST_FILE_PATH + "Need-60-dlDeviceInformationSubscriptionValid.in", "tmdd:gateControlScheduleMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATECONTROLSCHEDULE, "dlGateControlScheduleRequest", TEST_FILE_PATH + "Need-60-dlGateControlScheduleRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATECONTROLSCHEDULE, "dlGateControlScheduleRequest", TEST_FILE_PATH + "Need-60-dlGateControlScheduleRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATECONTROLSCHEDULE, "dlGateControlScheduleRequest", TEST_FILE_PATH + "Need-60-dlGateControlScheduleRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATECONTROLSCHEDULE, "dlGateControlScheduleRequest", TEST_FILE_PATH + "Need-60-dlGateControlScheduleRequestValid.in", "tmdd:gateControlScheduleMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.GATEINVENTORY, "string", "tmdd:gateInventoryMsg.gate-inventory-item.device-inventory-header.device-control-type", "4", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-54-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlGateInventoryUpdate", TEST_FILE_PATH + "Need-54-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-54-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlGateInventoryUpdate", TEST_FILE_PATH + "Need-54-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-54-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-54-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlGateInventoryUpdate", TEST_FILE_PATH + "Need-54-dlDeviceInformationSubscriptionValid.in", "tmdd:gateInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlGateInventoryRequest", TEST_FILE_PATH + "Need-54-dlGateInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlGateInventoryRequest", TEST_FILE_PATH + "Need-54-dlGateInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlGateInventoryRequest", TEST_FILE_PATH + "Need-54-dlGateInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlGateInventoryRequest", TEST_FILE_PATH + "Need-54-dlGateInventoryRequestValid.in", "tmdd:gateInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-55-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlGateInventoryUpdate", TEST_FILE_PATH + "Need-55-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-55-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlGateInventoryUpdate", TEST_FILE_PATH + "Need-55-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-55-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-55-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlGateInventoryUpdate", TEST_FILE_PATH + "Need-55-dlDeviceInformationSubscriptionValid.in", "tmdd:gateInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlGateControlRequest", TEST_FILE_PATH + "Need-57-dlGateControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlGateControlRequest", TEST_FILE_PATH + "Need-57-dlGateControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlGateControlRequest", TEST_FILE_PATH + "Need-57-dlGateControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlGateControlRequest", TEST_FILE_PATH + "Need-57-dlGateControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-58-dlDeviceControlStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-58-dlDeviceControlStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-58-dlDeviceControlStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-58-dlDeviceControlStatusRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-59-dlDeviceCancelControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-59-dlDeviceCancelControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-59-dlDeviceCancelControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATEINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-59-dlDeviceCancelControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.GATESTATUS, "string", "tmdd:gateStatusMsg.gate-status-item.device-status-header.event-id", "string", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.GATESTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-56-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.GATESTATUS, "dlGateStatusUpdate", TEST_FILE_PATH + "Need-56-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.GATESTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-56-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.GATESTATUS, "dlGateStatusUpdate", TEST_FILE_PATH + "Need-56-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.GATESTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-56-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.GATESTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-56-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.GATESTATUS, "dlGateStatusUpdate", TEST_FILE_PATH + "Need-56-dlDeviceInformationSubscriptionValid.in", "tmdd:gateStatusMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATESTATUS, "dlGateStatusRequest", TEST_FILE_PATH + "Need-56-dlGateStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATESTATUS, "dlGateStatusRequest", TEST_FILE_PATH + "Need-56-dlGateStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATESTATUS, "dlGateStatusRequest", TEST_FILE_PATH + "Need-56-dlGateStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.GATESTATUS, "dlGateStatusRequest", TEST_FILE_PATH + "Need-56-dlGateStatusRequestValid.in", "tmdd:gateStatusMsg", "", "string"));
        }

        if (entityForTest.equals("HAR") || entityForTest.equals("ALL"))
        {
            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.HARCONTROLSCHEDULE, "string", "tmdd:hARControlScheduleMsg.har-control-schedule-item.device-control-schedule-header.time-base-schedule-date", "7", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-68-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.HARCONTROLSCHEDULE, "dlHARControlScheduleUpdate", TEST_FILE_PATH + "Need-68-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-68-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.HARCONTROLSCHEDULE, "dlHARControlScheduleUpdate", TEST_FILE_PATH + "Need-68-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-68-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-68-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.HARCONTROLSCHEDULE, "dlHARControlScheduleUpdate", TEST_FILE_PATH + "Need-68-dlDeviceInformationSubscriptionValid.in", "tmdd:hARControlScheduleMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARCONTROLSCHEDULE, "dlHARControlScheduleRequest", TEST_FILE_PATH + "Need-68-dlHARControlScheduleRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARCONTROLSCHEDULE, "dlHARControlScheduleRequest", TEST_FILE_PATH + "Need-68-dlHARControlScheduleRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARCONTROLSCHEDULE, "dlHARControlScheduleRequest", TEST_FILE_PATH + "Need-68-dlHARControlScheduleRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARCONTROLSCHEDULE, "dlHARControlScheduleRequest", TEST_FILE_PATH + "Need-68-dlHARControlScheduleRequestValid.in", "tmdd:hARControlScheduleMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.HARINVENTORY, "string", "tmdd:hARInventoryMsg.har-inventory-item.device-inventory-header.network-id", "string", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-61-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARInventoryUpdate", TEST_FILE_PATH + "Need-61-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-61-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARInventoryUpdate", TEST_FILE_PATH + "Need-61-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-61-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-61-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARInventoryUpdate", TEST_FILE_PATH + "Need-61-dlDeviceInformationSubscriptionValid.in", "tmdd:hARInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARInventoryRequest", TEST_FILE_PATH + "Need-61-dlHARInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARInventoryRequest", TEST_FILE_PATH + "Need-61-dlHARInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARInventoryRequest", TEST_FILE_PATH + "Need-61-dlHARInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARInventoryRequest", TEST_FILE_PATH + "Need-61-dlHARInventoryRequestValid.in", "tmdd:hARInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-62-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARInventoryUpdate", TEST_FILE_PATH + "Need-62-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-62-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARInventoryUpdate", TEST_FILE_PATH + "Need-62-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-62-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-62-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARInventoryUpdate", TEST_FILE_PATH + "Need-62-dlDeviceInformationSubscriptionValid.in", "tmdd:hARInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARControlRequest", TEST_FILE_PATH + "Need-64-dlHARControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARControlRequest", TEST_FILE_PATH + "Need-64-dlHARControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARControlRequest", TEST_FILE_PATH + "Need-64-dlHARControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARControlRequest", TEST_FILE_PATH + "Need-64-dlHARControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-65-dlDeviceControlStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-65-dlDeviceControlStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-65-dlDeviceControlStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-65-dlDeviceControlStatusRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARPriorityQueueRequest", TEST_FILE_PATH + "Need-66-dlHARPriorityQueueRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARPriorityQueueRequest", TEST_FILE_PATH + "Need-66-dlHARPriorityQueueRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARPriorityQueueRequest", TEST_FILE_PATH + "Need-66-dlHARPriorityQueueRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlHARPriorityQueueRequest", TEST_FILE_PATH + "Need-66-dlHARPriorityQueueRequestValid.in", "tmdd:hARPriorityQueueMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-67-dlDeviceCancelControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-67-dlDeviceCancelControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-67-dlDeviceCancelControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-67-dlDeviceCancelControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.HARMESSAGEINVENTORY, "string", "tmdd:hARMessageInventoryMsg.har-message-inventory-item.current-message", "string", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARMESSAGEINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-69-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.HARMESSAGEINVENTORY, "dlHARMessageInventoryUpdate", TEST_FILE_PATH + "Need-69-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARMESSAGEINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-69-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.HARMESSAGEINVENTORY, "dlHARMessageInventoryUpdate", TEST_FILE_PATH + "Need-69-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARMESSAGEINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-69-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARMESSAGEINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-69-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.HARMESSAGEINVENTORY, "dlHARMessageInventoryUpdate", TEST_FILE_PATH + "Need-69-dlDeviceInformationSubscriptionValid.in", "tmdd:hARMessageInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARMESSAGEINVENTORY, "dlHARMessageInventoryRequest", TEST_FILE_PATH + "Need-69-dlHARMessageInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARMESSAGEINVENTORY, "dlHARMessageInventoryRequest", TEST_FILE_PATH + "Need-69-dlHARMessageInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARMESSAGEINVENTORY, "dlHARMessageInventoryRequest", TEST_FILE_PATH + "Need-69-dlHARMessageInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARMESSAGEINVENTORY, "dlHARMessageInventoryRequest", TEST_FILE_PATH + "Need-69-dlHARMessageInventoryRequestValid.in", "tmdd:hARMessageInventoryMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.HARSTATUS, "string", "tmdd:hARStatusMsg.har-status-item.device-status-header.center-id", "tcore_test", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-63-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.HARSTATUS, "dlHARStatusUpdate", TEST_FILE_PATH + "Need-63-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-63-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.HARSTATUS, "dlHARStatusUpdate", TEST_FILE_PATH + "Need-63-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-63-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.HARSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-63-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.HARSTATUS, "dlHARStatusUpdate", TEST_FILE_PATH + "Need-63-dlDeviceInformationSubscriptionValid.in", "tmdd:hARStatusMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARSTATUS, "dlHARStatusRequest", TEST_FILE_PATH + "Need-63-dlHARStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARSTATUS, "dlHARStatusRequest", TEST_FILE_PATH + "Need-63-dlHARStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARSTATUS, "dlHARStatusRequest", TEST_FILE_PATH + "Need-63-dlHARStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.HARSTATUS, "dlHARStatusRequest", TEST_FILE_PATH + "Need-63-dlHARStatusRequestValid.in", "tmdd:hARStatusMsg", "", "string"));
        }

        if (entityForTest.equals("SIGNAL") || entityForTest.equals("ALL"))
        {
            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.INTERSECTIONSIGNALCONTROLSCHEDULE, "string", "tmdd:intersectionSignalControlScheduleMsg.intersection-signal-control-schedule-item.timing-pattern-id", "string", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-94-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALCONTROLSCHEDULE, "dlIntersectionSignalControlScheduleUpdate", TEST_FILE_PATH + "Need-94-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-94-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALCONTROLSCHEDULE, "dlIntersectionSignalControlScheduleUpdate", TEST_FILE_PATH + "Need-94-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-94-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-94-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALCONTROLSCHEDULE, "dlIntersectionSignalControlScheduleUpdate", TEST_FILE_PATH + "Need-94-dlDeviceInformationSubscriptionValid.in", "tmdd:intersectionSignalControlScheduleMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALCONTROLSCHEDULE, "dlIntersectionSignalControlScheduleRequest", TEST_FILE_PATH + "Need-94-dlIntersectionSignalControlScheduleRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALCONTROLSCHEDULE, "dlIntersectionSignalControlScheduleRequest", TEST_FILE_PATH + "Need-94-dlIntersectionSignalControlScheduleRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALCONTROLSCHEDULE, "dlIntersectionSignalControlScheduleRequest", TEST_FILE_PATH + "Need-94-dlIntersectionSignalControlScheduleRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALCONTROLSCHEDULE, "dlIntersectionSignalControlScheduleRequest", TEST_FILE_PATH + "Need-94-dlIntersectionSignalControlScheduleRequestValid.in", "tmdd:intersectionSignalControlScheduleMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "string", "tmdd:intersectionSignalInventoryMsg.intersection-signal-inventory-item.device-inventory-header.device-control-type", "4", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-86-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryUpdate", TEST_FILE_PATH + "Need-86-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-86-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryUpdate", TEST_FILE_PATH + "Need-86-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-86-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-86-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryUpdate", TEST_FILE_PATH + "Need-86-dlDeviceInformationSubscriptionValid.in", "tmdd:intersectionSignalInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryRequest", TEST_FILE_PATH + "Need-86-dlIntersectionSignalInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryRequest", TEST_FILE_PATH + "Need-86-dlIntersectionSignalInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryRequest", TEST_FILE_PATH + "Need-86-dlIntersectionSignalInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryRequest", TEST_FILE_PATH + "Need-86-dlIntersectionSignalInventoryRequestValid.in", "tmdd:intersectionSignalInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-87-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryUpdate", TEST_FILE_PATH + "Need-87-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-87-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryUpdate", TEST_FILE_PATH + "Need-87-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-87-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-87-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryUpdate", TEST_FILE_PATH + "Need-87-dlDeviceInformationSubscriptionValid.in", "tmdd:intersectionSignalInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalControlRequest", TEST_FILE_PATH + "Need-89-dlIntersectionSignalControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalControlRequest", TEST_FILE_PATH + "Need-89-dlIntersectionSignalControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalControlRequest", TEST_FILE_PATH + "Need-89-dlIntersectionSignalControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalControlRequest", TEST_FILE_PATH + "Need-89-dlIntersectionSignalControlRequestValid.in", "tmdd:intersectionSignalControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-90-dlDeviceControlStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-90-dlDeviceControlStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-90-dlDeviceControlStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-90-dlDeviceControlStatusRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalPriorityQueueRequest", TEST_FILE_PATH + "Need-91-dlIntersectionSignalPriorityQueueRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalPriorityQueueRequest", TEST_FILE_PATH + "Need-91-dlIntersectionSignalPriorityQueueRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalPriorityQueueRequest", TEST_FILE_PATH + "Need-91-dlIntersectionSignalPriorityQueueRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalPriorityQueueRequest", TEST_FILE_PATH + "Need-91-dlIntersectionSignalPriorityQueueRequestValid.in", "tmdd:intersectionSignalPriorityQueueMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-92-dlDeviceCancelControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-92-dlDeviceCancelControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-92-dlDeviceCancelControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-92-dlDeviceCancelControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-95-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryUpdate", TEST_FILE_PATH + "Need-95-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-95-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryUpdate", TEST_FILE_PATH + "Need-95-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-95-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-95-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryUpdate", TEST_FILE_PATH + "Need-95-dlDeviceInformationSubscriptionValid.in", "tmdd:intersectionSignalInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryRequest", TEST_FILE_PATH + "Need-95-dlIntersectionSignalInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryRequest", TEST_FILE_PATH + "Need-95-dlIntersectionSignalInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryRequest", TEST_FILE_PATH + "Need-95-dlIntersectionSignalInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryRequest", TEST_FILE_PATH + "Need-95-dlIntersectionSignalInventoryRequestValid.in", "tmdd:intersectionSignalInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-96-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryUpdate", TEST_FILE_PATH + "Need-96-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-96-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryUpdate", TEST_FILE_PATH + "Need-96-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-96-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-96-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryUpdate", TEST_FILE_PATH + "Need-96-dlDeviceInformationSubscriptionValid.in", "tmdd:intersectionSignalInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryRequest", TEST_FILE_PATH + "Need-96-dlIntersectionSignalInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryRequest", TEST_FILE_PATH + "Need-96-dlIntersectionSignalInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryRequest", TEST_FILE_PATH + "Need-96-dlIntersectionSignalInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALINVENTORY, "dlIntersectionSignalInventoryRequest", TEST_FILE_PATH + "Need-96-dlIntersectionSignalInventoryRequestValid.in", "tmdd:intersectionSignalInventoryMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "string", "tmdd:intersectionSignalStatusMsg.intersection-signal-status-item.signal-control-source", "7", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-88-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlIntersectionSignalStatusUpdate", TEST_FILE_PATH + "Need-88-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-88-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlIntersectionSignalStatusUpdate", TEST_FILE_PATH + "Need-88-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-88-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-88-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlIntersectionSignalStatusUpdate", TEST_FILE_PATH + "Need-88-dlDeviceInformationSubscriptionValid.in", "tmdd:intersectionSignalStatusMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlIntersectionSignalStatusRequest", TEST_FILE_PATH + "Need-88-dlIntersectionSignalStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlIntersectionSignalStatusRequest", TEST_FILE_PATH + "Need-88-dlIntersectionSignalStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlIntersectionSignalStatusRequest", TEST_FILE_PATH + "Need-88-dlIntersectionSignalStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlIntersectionSignalStatusRequest", TEST_FILE_PATH + "Need-88-dlIntersectionSignalStatusRequestValid.in", "tmdd:intersectionSignalStatusMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-97-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlIntersectionSignalStatusUpdate", TEST_FILE_PATH + "Need-97-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-97-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlIntersectionSignalStatusUpdate", TEST_FILE_PATH + "Need-97-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-97-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-97-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlIntersectionSignalStatusUpdate", TEST_FILE_PATH + "Need-97-dlDeviceInformationSubscriptionValid.in", "tmdd:intersectionSignalStatusMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlIntersectionSignalStatusRequest", TEST_FILE_PATH + "Need-97-dlIntersectionSignalStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlIntersectionSignalStatusRequest", TEST_FILE_PATH + "Need-97-dlIntersectionSignalStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlIntersectionSignalStatusRequest", TEST_FILE_PATH + "Need-97-dlIntersectionSignalStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALSTATUS, "dlIntersectionSignalStatusRequest", TEST_FILE_PATH + "Need-97-dlIntersectionSignalStatusRequestValid.in", "tmdd:intersectionSignalStatusMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORY, "string", "tmdd:intersectionSignalTimingPatternInventoryMsg.intersection-signal-timing-pattern-inventory-item.offset-time", "6", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORY, "dlIntersectionSignalTimingPatternInventoryRequest", TEST_FILE_PATH + "Need-93-dlIntersectionSignalTimingPatternInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORY, "dlIntersectionSignalTimingPatternInventoryRequest", TEST_FILE_PATH + "Need-93-dlIntersectionSignalTimingPatternInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORY, "dlIntersectionSignalTimingPatternInventoryRequest", TEST_FILE_PATH + "Need-93-dlIntersectionSignalTimingPatternInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORY, "dlIntersectionSignalTimingPatternInventoryRequest", TEST_FILE_PATH + "Need-93-dlIntersectionSignalTimingPatternInventoryRequestValid.in", "tmdd:intersectionSignalTimingPatternInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORY, "dlIntersectionSignalTimingPatternInventorySubscription", TEST_FILE_PATH + "Need-93-dlIntersectionSignalTimingPatternInventorySubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORY, "dlIntersectionSignalTimingPatternInventoryUpdate", TEST_FILE_PATH + "Need-93-dlIntersectionSignalTimingPatternInventorySubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORY, "dlIntersectionSignalTimingPatternInventorySubscription", TEST_FILE_PATH + "Need-93-dlIntersectionSignalTimingPatternInventorySubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORY, "dlIntersectionSignalTimingPatternInventoryUpdate", TEST_FILE_PATH + "Need-93-dlIntersectionSignalTimingPatternInventorySubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORY, "dlIntersectionSignalTimingPatternInventorySubscription", TEST_FILE_PATH + "Need-93-dlIntersectionSignalTimingPatternInventorySubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORY, "dlIntersectionSignalTimingPatternInventorySubscription", TEST_FILE_PATH + "Need-93-dlIntersectionSignalTimingPatternInventorySubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.INTERSECTIONSIGNALTIMINGPATTERNINVENTORY, "dlIntersectionSignalTimingPatternInventoryUpdate", TEST_FILE_PATH + "Need-93-dlIntersectionSignalTimingPatternInventorySubscriptionValid.in", "tmdd:intersectionSignalTimingPatternInventoryMsg", "", "string"));
        }

        if (entityForTest.equals("LCS") || entityForTest.equals("ALL"))
        {
            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.LCSCONTROLSCHEDULE, "string", "tmdd:lCSControlScheduleMsg.lcs-control-schedule-item.device-control-schedule-header.time-base-schedule-date", "7", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.LCSCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-76-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.LCSCONTROLSCHEDULE, "dlLCSControlScheduleUpdate", TEST_FILE_PATH + "Need-76-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.LCSCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-76-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.LCSCONTROLSCHEDULE, "dlLCSControlScheduleUpdate", TEST_FILE_PATH + "Need-76-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.LCSCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-76-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.LCSCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-76-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.LCSCONTROLSCHEDULE, "dlLCSControlScheduleUpdate", TEST_FILE_PATH + "Need-76-dlDeviceInformationSubscriptionValid.in", "tmdd:lCSControlScheduleMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSCONTROLSCHEDULE, "dlLCSControlScheduleRequest", TEST_FILE_PATH + "Need-76-dlLCSControlScheduleRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSCONTROLSCHEDULE, "dlLCSControlScheduleRequest", TEST_FILE_PATH + "Need-76-dlLCSControlScheduleRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSCONTROLSCHEDULE, "dlLCSControlScheduleRequest", TEST_FILE_PATH + "Need-76-dlLCSControlScheduleRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSCONTROLSCHEDULE, "dlLCSControlScheduleRequest", TEST_FILE_PATH + "Need-76-dlLCSControlScheduleRequestValid.in", "tmdd:lCSControlScheduleMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.LCSINVENTORY, "string", "tmdd:lCSInventoryMsg.lcs-inventory-item.device-inventory-header.device-control-type", "4", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-70-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlLCSInventoryUpdate", TEST_FILE_PATH + "Need-70-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-70-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlLCSInventoryUpdate", TEST_FILE_PATH + "Need-70-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-70-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-70-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlLCSInventoryUpdate", TEST_FILE_PATH + "Need-70-dlDeviceInformationSubscriptionValid.in", "tmdd:lCSInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlLCSInventoryRequest", TEST_FILE_PATH + "Need-70-dlLCSInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlLCSInventoryRequest", TEST_FILE_PATH + "Need-70-dlLCSInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlLCSInventoryRequest", TEST_FILE_PATH + "Need-70-dlLCSInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlLCSInventoryRequest", TEST_FILE_PATH + "Need-70-dlLCSInventoryRequestValid.in", "tmdd:lCSInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-71-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlLCSInventoryUpdate", TEST_FILE_PATH + "Need-71-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-71-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlLCSInventoryUpdate", TEST_FILE_PATH + "Need-71-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-71-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-71-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlLCSInventoryUpdate", TEST_FILE_PATH + "Need-71-dlDeviceInformationSubscriptionValid.in", "tmdd:lCSInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlLCSControlRequest", TEST_FILE_PATH + "Need-73-dlLCSControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlLCSControlRequest", TEST_FILE_PATH + "Need-73-dlLCSControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlLCSControlRequest", TEST_FILE_PATH + "Need-73-dlLCSControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlLCSControlRequest", TEST_FILE_PATH + "Need-73-dlLCSControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-74-dlDeviceControlStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-74-dlDeviceControlStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-74-dlDeviceControlStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-74-dlDeviceControlStatusRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-75-dlDeviceCancelControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-75-dlDeviceCancelControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-75-dlDeviceCancelControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-75-dlDeviceCancelControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.LCSSTATUS, "string", "tmdd:lCSStatusMsg.lcs-status-item.device-status-header.device-status", "other", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.LCSSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-72-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.LCSSTATUS, "dlLCSStatusUpdate", TEST_FILE_PATH + "Need-72-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.LCSSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-72-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.LCSSTATUS, "dlLCSStatusUpdate", TEST_FILE_PATH + "Need-72-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.LCSSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-72-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.LCSSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-72-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.LCSSTATUS, "dlLCSStatusUpdate", TEST_FILE_PATH + "Need-72-dlDeviceInformationSubscriptionValid.in", "tmdd:lCSStatusMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSSTATUS, "dlLCSStatusRequest", TEST_FILE_PATH + "Need-72-dlLCSStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSSTATUS, "dlLCSStatusRequest", TEST_FILE_PATH + "Need-72-dlLCSStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSSTATUS, "dlLCSStatusRequest", TEST_FILE_PATH + "Need-72-dlLCSStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.LCSSTATUS, "dlLCSStatusRequest", TEST_FILE_PATH + "Need-72-dlLCSStatusRequestValid.in", "tmdd:lCSStatusMsg", "", "string"));

        }

        if (entityForTest.equals("RAMPMETER") || entityForTest.equals("ALL"))
        {

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.RAMPMETERCONTROLSCHEDULE, "string", "tmdd:rampMeterControlScheduleMsg.ramp-meter-control-schedule-item.action-number", "6", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-84-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.RAMPMETERCONTROLSCHEDULE, "dlRampMeterControlScheduleUpdate", TEST_FILE_PATH + "Need-84-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-84-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.RAMPMETERCONTROLSCHEDULE, "dlRampMeterControlScheduleUpdate", TEST_FILE_PATH + "Need-84-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-84-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-84-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.RAMPMETERCONTROLSCHEDULE, "dlRampMeterControlScheduleUpdate", TEST_FILE_PATH + "Need-84-dlDeviceInformationSubscriptionValid.in", "tmdd:rampMeterControlScheduleMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERCONTROLSCHEDULE, "dlRampMeterControlScheduleRequest", TEST_FILE_PATH + "Need-84-dlRampMeterControlScheduleRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERCONTROLSCHEDULE, "dlRampMeterControlScheduleRequest", TEST_FILE_PATH + "Need-84-dlRampMeterControlScheduleRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERCONTROLSCHEDULE, "dlRampMeterControlScheduleRequest", TEST_FILE_PATH + "Need-84-dlRampMeterControlScheduleRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERCONTROLSCHEDULE, "dlRampMeterControlScheduleRequest", TEST_FILE_PATH + "Need-84-dlRampMeterControlScheduleRequestValid.in", "tmdd:rampMeterControlScheduleMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "string", "tmdd:rampMeterInventoryMsg.ramp-meter-inventory-item.metered-inventory-list.metered-lane.lane-type", "5", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-77-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterInventoryUpdate", TEST_FILE_PATH + "Need-77-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-77-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterInventoryUpdate", TEST_FILE_PATH + "Need-77-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-77-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-77-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterInventoryUpdate", TEST_FILE_PATH + "Need-77-dlDeviceInformationSubscriptionValid.in", "tmdd:rampMeterInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterInventoryRequest", TEST_FILE_PATH + "Need-77-dlRampMeterInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterInventoryRequest", TEST_FILE_PATH + "Need-77-dlRampMeterInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterInventoryRequest", TEST_FILE_PATH + "Need-77-dlRampMeterInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterInventoryRequest", TEST_FILE_PATH + "Need-77-dlRampMeterInventoryRequestValid.in", "tmdd:rampMeterInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-78-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterInventoryUpdate", TEST_FILE_PATH + "Need-78-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-78-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterInventoryUpdate", TEST_FILE_PATH + "Need-78-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-78-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-78-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterInventoryUpdate", TEST_FILE_PATH + "Need-78-dlDeviceInformationSubscriptionValid.in", "tmdd:rampMeterInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterControlRequest", TEST_FILE_PATH + "Need-80-dlRampMeterControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterControlRequest", TEST_FILE_PATH + "Need-80-dlRampMeterControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterControlRequest", TEST_FILE_PATH + "Need-80-dlRampMeterControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterControlRequest", TEST_FILE_PATH + "Need-80-dlRampMeterControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-81-dlDeviceControlStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-81-dlDeviceControlStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-81-dlDeviceControlStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-81-dlDeviceControlStatusRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterPriorityQueueRequest", TEST_FILE_PATH + "Need-83-dlRampMeterPriorityQueueRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterPriorityQueueRequest", TEST_FILE_PATH + "Need-83-dlRampMeterPriorityQueueRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterPriorityQueueRequest", TEST_FILE_PATH + "Need-83-dlRampMeterPriorityQueueRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlRampMeterPriorityQueueRequest", TEST_FILE_PATH + "Need-83-dlRampMeterPriorityQueueRequestValid.in", "tmdd:rampMeterPriorityQueueMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-82-dlDeviceCancelControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-82-dlDeviceCancelControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-82-dlDeviceCancelControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-82-dlDeviceCancelControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.RAMPMETERPLANINVENTORY, "string", "tmdd:rampMeterPlanInventoryMsg.ramp-meter-plan-inventory-item.meter-plan", "string", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERPLANINVENTORY, "dlRampMeterPlanInventoryRequest", TEST_FILE_PATH + "Need-85-dlRampMeterPlanInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERPLANINVENTORY, "dlRampMeterPlanInventoryRequest", TEST_FILE_PATH + "Need-85-dlRampMeterPlanInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERPLANINVENTORY, "dlRampMeterPlanInventoryRequest", TEST_FILE_PATH + "Need-85-dlRampMeterPlanInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERPLANINVENTORY, "dlRampMeterPlanInventoryRequest", TEST_FILE_PATH + "Need-85-dlRampMeterPlanInventoryRequestValid.in", "tmdd:rampMeterPlanInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERPLANINVENTORY, "dlRampMeterPlanInventorySubscription", TEST_FILE_PATH + "Need-85-dlRampMeterPlanInventorySubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.RAMPMETERPLANINVENTORY, "dlRampMeterPlanInventoryUpdate", TEST_FILE_PATH + "Need-85-dlRampMeterPlanInventorySubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERPLANINVENTORY, "dlRampMeterPlanInventorySubscription", TEST_FILE_PATH + "Need-85-dlRampMeterPlanInventorySubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.RAMPMETERPLANINVENTORY, "dlRampMeterPlanInventoryUpdate", TEST_FILE_PATH + "Need-85-dlRampMeterPlanInventorySubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERPLANINVENTORY, "dlRampMeterPlanInventorySubscription", TEST_FILE_PATH + "Need-85-dlRampMeterPlanInventorySubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERPLANINVENTORY, "dlRampMeterPlanInventorySubscription", TEST_FILE_PATH + "Need-85-dlRampMeterPlanInventorySubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.RAMPMETERPLANINVENTORY, "dlRampMeterPlanInventoryUpdate", TEST_FILE_PATH + "Need-85-dlRampMeterPlanInventorySubscriptionValid.in", "tmdd:rampMeterPlanInventoryMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.RAMPMETERSTATUS, "string", "tmdd:rampMeterStatusMsg.ramp-meter-status-item.device-status-header.last-comm-time.time", "string", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-79-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.RAMPMETERSTATUS, "dlRampMeterStatusUpdate", TEST_FILE_PATH + "Need-79-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-79-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.RAMPMETERSTATUS, "dlRampMeterStatusUpdate", TEST_FILE_PATH + "Need-79-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-79-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.RAMPMETERSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-79-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.RAMPMETERSTATUS, "dlRampMeterStatusUpdate", TEST_FILE_PATH + "Need-79-dlDeviceInformationSubscriptionValid.in", "tmdd:rampMeterStatusMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERSTATUS, "dlRampMeterStatusRequest", TEST_FILE_PATH + "Need-79-dlRampMeterStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERSTATUS, "dlRampMeterStatusRequest", TEST_FILE_PATH + "Need-79-dlRampMeterStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERSTATUS, "dlRampMeterStatusRequest", TEST_FILE_PATH + "Need-79-dlRampMeterStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.RAMPMETERSTATUS, "dlRampMeterStatusRequest", TEST_FILE_PATH + "Need-79-dlRampMeterStatusRequestValid.in", "tmdd:rampMeterStatusMsg", "", "string"));
        }

        if (entityForTest.equals("SECTION") || entityForTest.equals("ALL"))
        {
            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.SECTIONCONTROLSCHEDULE, "string", "tmdd:sectionControlScheduleMsg.section-control-schedule-item.time-base-schedule-date", "7", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {
            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.SECTIONCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-103-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.SECTIONCONTROLSCHEDULE, "dlSectionControlScheduleUpdate", TEST_FILE_PATH + "Need-103-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.SECTIONCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-103-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.SECTIONCONTROLSCHEDULE, "dlSectionControlScheduleUpdate", TEST_FILE_PATH + "Need-103-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.SECTIONCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-103-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.SECTIONCONTROLSCHEDULE, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-103-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.SECTIONCONTROLSCHEDULE, "dlSectionControlScheduleUpdate", TEST_FILE_PATH + "Need-103-dlDeviceInformationSubscriptionValid.in", "tmdd:sectionControlScheduleMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONCONTROLSCHEDULE, "dlSectionControlScheduleRequest", TEST_FILE_PATH + "Need-103-dlSectionControlScheduleRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONCONTROLSCHEDULE, "dlSectionControlScheduleRequest", TEST_FILE_PATH + "Need-103-dlSectionControlScheduleRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONCONTROLSCHEDULE, "dlSectionControlScheduleRequest", TEST_FILE_PATH + "Need-103-dlSectionControlScheduleRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONCONTROLSCHEDULE, "dlSectionControlScheduleRequest", TEST_FILE_PATH + "Need-103-dlSectionControlScheduleRequestValid.in", "tmdd:sectionControlScheduleMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.SECTIONSIGNALTIMINGPATTERNINVENTORY, "string", "tmdd:sectionSignalTimingPatternInventoryMsg.signal-section-timing-pattern-inventory-item.section-cycle-length", "6", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSIGNALTIMINGPATTERNINVENTORY, "dlSectionSignalTimingPatternInventoryRequest", TEST_FILE_PATH + "Need-104-dlSectionSignalTimingPatternInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSIGNALTIMINGPATTERNINVENTORY, "dlSectionSignalTimingPatternInventoryRequest", TEST_FILE_PATH + "Need-104-dlSectionSignalTimingPatternInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSIGNALTIMINGPATTERNINVENTORY, "dlSectionSignalTimingPatternInventoryRequest", TEST_FILE_PATH + "Need-104-dlSectionSignalTimingPatternInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSIGNALTIMINGPATTERNINVENTORY, "dlSectionSignalTimingPatternInventoryRequest", TEST_FILE_PATH + "Need-104-dlSectionSignalTimingPatternInventoryRequestValid.in", "tmdd:sectionSignalTimingPatternInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.SECTIONSIGNALTIMINGPATTERNINVENTORY, "dlSectionSignalTimingPatternInventorySubscription", TEST_FILE_PATH + "Need-104-dlSectionSignalTimingPatternInventorySubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.SECTIONSIGNALTIMINGPATTERNINVENTORY, "dlSectionSignalTimingPatternInventoryUpdate", TEST_FILE_PATH + "Need-104-dlSectionSignalTimingPatternInventorySubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.SECTIONSIGNALTIMINGPATTERNINVENTORY, "dlSectionSignalTimingPatternInventorySubscription", TEST_FILE_PATH + "Need-104-dlSectionSignalTimingPatternInventorySubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.SECTIONSIGNALTIMINGPATTERNINVENTORY, "dlSectionSignalTimingPatternInventoryUpdate", TEST_FILE_PATH + "Need-104-dlSectionSignalTimingPatternInventorySubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.SECTIONSIGNALTIMINGPATTERNINVENTORY, "dlSectionSignalTimingPatternInventorySubscription", TEST_FILE_PATH + "Need-104-dlSectionSignalTimingPatternInventorySubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.SECTIONSIGNALTIMINGPATTERNINVENTORY, "dlSectionSignalTimingPatternInventorySubscription", TEST_FILE_PATH + "Need-104-dlSectionSignalTimingPatternInventorySubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.SECTIONSIGNALTIMINGPATTERNINVENTORY, "dlSectionSignalTimingPatternInventoryUpdate", TEST_FILE_PATH + "Need-104-dlSectionSignalTimingPatternInventorySubscriptionValid.in", "tmdd:sectionSignalTimingPatternInventoryMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.SECTIONSTATUS, "string", "tmdd:sectionStatusMsg.section-status-item.cycle-time", "6", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {
            }
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionControlStatusRequest", TEST_FILE_PATH + "Need-100-dlSectionControlStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionControlRequest", TEST_FILE_PATH + "Need-99-dlSectionControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionControlRequest", TEST_FILE_PATH + "Need-99-dlSectionControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionControlRequest", TEST_FILE_PATH + "Need-99-dlSectionControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionControlRequest", TEST_FILE_PATH + "Need-99-dlSectionControlRequestValid.in", "tmdd:sectionControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionControlStatusRequest", TEST_FILE_PATH + "Need-100-dlSectionControlStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionControlStatusRequest", TEST_FILE_PATH + "Need-100-dlSectionControlStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionControlStatusRequest", TEST_FILE_PATH + "Need-100-dlSectionControlStatusRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionPriorityQueueRequest", TEST_FILE_PATH + "Need-101-dlSectionPriorityQueueRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionPriorityQueueRequest", TEST_FILE_PATH + "Need-101-dlSectionPriorityQueueRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionPriorityQueueRequest", TEST_FILE_PATH + "Need-101-dlSectionPriorityQueueRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionPriorityQueueRequest", TEST_FILE_PATH + "Need-101-dlSectionPriorityQueueRequestValid.in", "tmdd:sectionPriorityQueueMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-102-dlDeviceCancelControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-102-dlDeviceCancelControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-102-dlDeviceCancelControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-102-dlDeviceCancelControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-98-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionStatusUpdate", TEST_FILE_PATH + "Need-98-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-98-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionStatusUpdate", TEST_FILE_PATH + "Need-98-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-98-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-98-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionStatusUpdate", TEST_FILE_PATH + "Need-98-dlDeviceInformationSubscriptionValid.in", "tmdd:sectionStatusMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionStatusRequest", TEST_FILE_PATH + "Need-98-dlSectionStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionStatusRequest", TEST_FILE_PATH + "Need-98-dlSectionStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionStatusRequest", TEST_FILE_PATH + "Need-98-dlSectionStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.SECTIONSTATUS, "dlSectionStatusRequest", TEST_FILE_PATH + "Need-98-dlSectionStatusRequestValid.in", "tmdd:sectionStatusMsg", "", "string"));

            if (entityForTest.equals("VIDEOSWITCH") || entityForTest.equals("ALL"))
            {
            }
            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "string", "tmdd:videoSwitchInventoryMsg.video-switch-inventory-item.input-channel-count", "7", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-31-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlVideoSwitchInventoryUpdate", TEST_FILE_PATH + "Need-31-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-31-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlVideoSwitchInventoryUpdate", TEST_FILE_PATH + "Need-31-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-31-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-31-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlVideoSwitchInventoryUpdate", TEST_FILE_PATH + "Need-31-dlDeviceInformationSubscriptionValid.in", "tmdd:videoSwitchInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlVideoSwitchInventoryRequest", TEST_FILE_PATH + "Need-31-dlVideoSwitchInventoryRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlVideoSwitchInventoryRequest", TEST_FILE_PATH + "Need-31-dlVideoSwitchInventoryRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlVideoSwitchInventoryRequest", TEST_FILE_PATH + "Need-31-dlVideoSwitchInventoryRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlVideoSwitchInventoryRequest", TEST_FILE_PATH + "Need-31-dlVideoSwitchInventoryRequestValid.in", "tmdd:videoSwitchInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-32-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlVideoSwitchInventoryUpdate", TEST_FILE_PATH + "Need-32-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-32-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlVideoSwitchInventoryUpdate", TEST_FILE_PATH + "Need-32-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-32-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-32-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlVideoSwitchInventoryUpdate", TEST_FILE_PATH + "Need-32-dlDeviceInformationSubscriptionValid.in", "tmdd:videoSwitchInventoryMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlVideoSwitchControlRequest", TEST_FILE_PATH + "Need-34-dlVideoSwitchControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlVideoSwitchControlRequest", TEST_FILE_PATH + "Need-34-dlVideoSwitchControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlVideoSwitchControlRequest", TEST_FILE_PATH + "Need-34-dlVideoSwitchControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlVideoSwitchControlRequest", TEST_FILE_PATH + "Need-34-dlVideoSwitchControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-35-dlDeviceControlStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-35-dlDeviceControlStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-35-dlDeviceControlStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlDeviceControlStatusRequest", TEST_FILE_PATH + "Need-35-dlDeviceControlStatusRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-36-dlDeviceCancelControlRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-36-dlDeviceCancelControlRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-36-dlDeviceCancelControlRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHINVENTORY, "dlDeviceCancelControlRequest", TEST_FILE_PATH + "Need-36-dlDeviceCancelControlRequestValid.in", "tmdd:deviceControlResponseMsg", "", "string"));

            results.addAll(entityDataTester(EntityEmulationData.EntityDataType.VIDEOSWITCHSTATUS, "string", "tmdd:videoSwitchStatusMsg.video-switch-status-item.switched-channel-list.switched-channel.input-channel-id", "string", "8", "defaultEntityElement", "defaultEntityElementValue", "newEntityElement", "newEntityElementValue"));
            try
            {
                initNRTMSelections();
            }
            catch (Exception ex)
            {

            }
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.VIDEOSWITCHSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-33-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.VIDEOSWITCHSTATUS, "dlVideoSwitchStatusUpdate", TEST_FILE_PATH + "Need-33-dlDeviceInformationSubscriptionInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.VIDEOSWITCHSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-33-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.VIDEOSWITCHSTATUS, "dlVideoSwitchStatusUpdate", TEST_FILE_PATH + "Need-33-dlDeviceInformationSubscriptionInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.VIDEOSWITCHSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-33-dlDeviceInformationSubscriptionInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("SUB", EntityEmulationData.EntityDataType.VIDEOSWITCHSTATUS, "dlDeviceInformationSubscription", TEST_FILE_PATH + "Need-33-dlDeviceInformationSubscriptionValid.in", "c2c:c2cMessageReceipt", "", "string"));
            results.addAll(entityDialogTester("PUB", EntityEmulationData.EntityDataType.VIDEOSWITCHSTATUS, "dlVideoSwitchStatusUpdate", TEST_FILE_PATH + "Need-33-dlDeviceInformationSubscriptionValid.in", "tmdd:videoSwitchStatusMsg", "", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHSTATUS, "dlVideoSwitchStatusRequest", TEST_FILE_PATH + "Need-33-dlVideoSwitchStatusRequestInValid-2.in", "tmdd:errorReportMsg", "center does not support this type message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHSTATUS, "dlVideoSwitchStatusRequest", TEST_FILE_PATH + "Need-33-dlVideoSwitchStatusRequestInValid-3.in", "tmdd:errorReportMsg", "missing information prevents processing message", "string"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHSTATUS, "dlVideoSwitchStatusRequest", TEST_FILE_PATH + "Need-33-dlVideoSwitchStatusRequestInValid-7.in", "tmdd:errorReportMsg", "authentication not recognized", "string2"));
            results.addAll(entityDialogTester("RR", EntityEmulationData.EntityDataType.VIDEOSWITCHSTATUS, "dlVideoSwitchStatusRequest", TEST_FILE_PATH + "Need-33-dlVideoSwitchStatusRequestValid.in", "tmdd:videoSwitchStatusMsg", "", "string"));

        }

        System.out.println("\n\n");
        int totalTestsRun = 0;
        int totalTestsPassed = 0;
        int totalTestsFailed = 0;

        Map<String, TestCounts> testCounts = new HashMap<String, TestCounts>();

        for (TestResult thisResult: results)
        {
            TestCounts thisCounter;

            if (testCounts.containsKey(thisResult.entityName))
                thisCounter = testCounts.get(thisResult.entityName);
            else
            {
                thisCounter = new TestCounts();
                testCounts.put(thisResult.entityName, thisCounter);
            }
            totalTestsRun++;
            thisCounter.setTotalTests(thisCounter.getTotalTests() + 1);
            if (thisResult.getResult().equals("PASS"))
            {
                totalTestsPassed++;
                thisCounter.setTotalPassed(thisCounter.getTotalPassed() + 1);
            }
            else
            {
                totalTestsFailed++;
                thisCounter.setTotalFailed(thisCounter.getTotalFailed() + 1);
            }
            System.out.println("Entity: " + thisResult.getEntityName() + "  Test: " + thisResult.testType + " = " + thisResult.getResult() + (thisResult.getResult().equals("FAILED") ? "   -- " + thisResult.getErrorMessage() : ""));
        }

        System.out.println("\n\n");
        System.out.println("Total Tests = " + totalTestsRun + "    Total Passed = " + totalTestsPassed + "    Total Failed = " + totalTestsFailed);
        float percentPassed = ((float)totalTestsPassed / totalTestsRun) * 100;
        System.out.println("Percent Passed " + String.format("%.2f", percentPassed) + " %");
        System.out.println("\n\n\n");

        Map<String, TestCounts> countMap = new TreeMap<String, TestCounts>(testCounts);
        for (Map.Entry<String, TestCounts> entry: countMap.entrySet())

            System.out.println("ENTITY: " + entry.getKey() + "   Total Tests = " + entry.getValue().getTotalTests() + "    Total Passed = " + entry.getValue().getTotalPassed() + "    Total Failed = " + entry.getValue().getTotalFailed() + "    Percent Passed = " + String.format("%.2f", ((float)entry.getValue().getTotalPassed() / entry.getValue().getTotalTests()) * 100));

    }

    public static ArrayList<TestResult> entityDataTester(EntityEmulationData.EntityDataType dataType, String existingEntityId, String existingEntityElement, String existingEntityElementValue, String newEntityId, String defaultEntityElement, String defaultEntityElementValue, String newEntityElement, String newEntityElementValue)
    {
        ArrayList<TestResult> results = new ArrayList();

        TestResult initialization = new TestResult();
        initialization.setEntityName(dataType.name());
        initialization.setTestType("Initialization");
        initialization.setResult(testEntityInitialization(dataType) ? "PASS" : "FAILED");
        results.add(initialization);

        TestResult existance = new TestResult();
        existance.setEntityName(dataType.name());
        existance.setTestType("Existance");
        existance.setResult(testEntityExistence(dataType, existingEntityId, existingEntityElement, existingEntityElementValue) ? "PASS" : "FAILED");
        results.add(existance);

        TestResult addAndDelete = new TestResult();
        addAndDelete.setEntityName(dataType.name());
        addAndDelete.setTestType("AddAndDelete");
        addAndDelete.setResult(testEntityAddandDelete(dataType, newEntityId) ? "PASS" : "FAILED");
        results.add(addAndDelete);

        return results;
    }

    private static ArrayList<TestResult> entityDialogTester(String dialogType, EntityEmulationData.EntityDataType dataType, String dialog, String messageSpecFileName, String expectedResponseMessageName, String expectedErrorResponseType, String userName)
    {
        ArrayList<TestResult> results = new ArrayList();

        if (dialogType.equals("RR"))
        {
            TestResult rrDialog = new TestResult();
            rrDialog.setEntityName(dataType.name());
            rrDialog.setTestType(dialogType + "-RR" + "-" + messageSpecFileName);
            try
            {
				testEntityRRProcessing(dataType, dialog, messageSpecFileName, expectedResponseMessageName, expectedErrorResponseType, userName);
                rrDialog.setResult("PASS");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                rrDialog.setResult("FAILED");
                rrDialog.setErrorMessage(ex.getMessage());
            }
            results.add(rrDialog);
        }
        else if (dialogType.equals("SUB"))
        {
            TestResult rrDialog = new TestResult();
            rrDialog.setEntityName(dataType.name());
            rrDialog.setTestType(dialogType + "-SUB" + "-" + messageSpecFileName);
            try
            {
				testEntitySUBProcessing(dataType, dialog, messageSpecFileName, expectedResponseMessageName, expectedErrorResponseType, userName);
                rrDialog.setResult("PASS");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                rrDialog.setResult("FAILED");
                rrDialog.setErrorMessage(ex.getMessage());
            }
            results.add(rrDialog);
        }
        else if (dialogType.equals("PUB"))
        {
            TestResult rrDialog = new TestResult();
            rrDialog.setEntityName(dataType.name());
            rrDialog.setTestType(dialogType + "-PUB" + "-" + messageSpecFileName);
            try
            {
				testEntityPUBProcessing(dataType, dialog, messageSpecFileName, expectedResponseMessageName, expectedErrorResponseType, userName);
                rrDialog.setResult("PASS");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                rrDialog.setResult("FAILED");
                rrDialog.setErrorMessage(ex.getMessage());
            }
            results.add(rrDialog);
        }
        return results;
    }

    private static boolean testEntityRRProcessing(EntityEmulationData.EntityDataType dataType, String dialog, String messageSpecFileName, String expectedResponseMessageName, String expectedErrorResponseType, String userName) throws Exception
    {
        boolean results = false;

        String[] fileLines = EmulationDataFileProcessor.getContent(messageSpecFileName).toString().split("\n");
        ArrayList<String> input = new ArrayList();
        for (int ii = 0; ii < fileLines.length; ii++)
            input.add(fileLines[ii]);
        MessageSpecification thisSpec = new MessageSpecification(input);

        TMDDAuthenticationProcessor.getInstance(userName, "string", "string");
        Message requestMessage = MessageManager.getInstance().createMessage(dialog);
        requestMessage.setMessageSpecification(thisSpec);
        long startTime = System.currentTimeMillis();
        Message responseMessage = RIEmulation.getInstance().getRRResponse(dialog, requestMessage);
        long responseTime = System.currentTimeMillis() - startTime;
        if (responseTime > MAX_RR_RESPONSE_TIME)
            throw new Exception("Entity " + dataType.name() + " processing of " + dialog + " exceeded the MAX_RR_RESPONSE_TIME with value " + responseTime);

        for (String theName: responseMessage.getMessageSpecification().getMessageTypes())
            if (!theName.equals(expectedResponseMessageName))
                throw new Exception("Entity " + dataType.name() + " processing of " + dialog + " returned message " + theName + " instead of the expected " + expectedResponseMessageName
                                    + (theName.contains("errorReportMsg") ? " with error description " + responseMessage.getMessageSpecification().getElementValue("tmdd:errorReportMsg.error-text") : ""));
            else if (theName.contains("errorReportMsg"))
            {
                String errorCode = responseMessage.getMessageSpecification().getElementValue("tmdd:errorReportMsg.error-code");
                String errorCodeText = responseMessage.getMessageSpecification().getElementValue("tmdd:errorReportMsg.error-text");
                if (!errorCode.equals(expectedErrorResponseType))
                    throw new Exception("Entity " + dataType.name() + " processing of " + dialog + " returned error code " + errorCode + "(" + errorCodeText + ") instead of the expected " + expectedErrorResponseType);
            }

        results = true;

        return results;
    }

    private static boolean testEntitySUBProcessing(EntityEmulationData.EntityDataType dataType, String dialog, String messageSpecFileName, String expectedResponseMessageName, String expectedErrorResponseType, String userName) throws Exception
    {
        boolean results = false;

        String[] fileLines = EmulationDataFileProcessor.getContent(messageSpecFileName).toString().split("\n");
        ArrayList<String> input = new ArrayList();
        for (int ii = 0; ii < fileLines.length; ii++)
            input.add(fileLines[ii]);
        MessageSpecification thisSpec = new MessageSpecification(input);

        TMDDAuthenticationProcessor.getInstance(userName, "string", "string");
        Message requestMessage = MessageManager.getInstance().createMessage(dialog);
        requestMessage.setMessageSpecification(thisSpec);
        long startTime = System.currentTimeMillis();
        Message responseMessage = RIEmulation.getInstance().getSubResponse(dialog, requestMessage);
        long responseTime = System.currentTimeMillis() - startTime;
        if (responseTime > MAX_RR_RESPONSE_TIME)
            throw new Exception("Entity " + dataType.name() + " processing of " + dialog + " exceeded the MAX_RR_RESPONSE_TIME with value " + responseTime);

        for (String theName: responseMessage.getMessageSpecification().getMessageTypes())
            if (!theName.equals(expectedResponseMessageName))
                throw new Exception("Entity " + dataType.name() + " processing of " + dialog + " returned message " + theName + " instead of the expected " + expectedResponseMessageName
                                    + (theName.contains("errorReportMsg") ? " with error description " + responseMessage.getMessageSpecification().getElementValue("tmdd:errorReportMsg.error-text") : ""));
            else if (theName.contains("errorReportMsg"))
            {
                String errorCode = responseMessage.getMessageSpecification().getElementValue("tmdd:errorReportMsg.error-code");
                String errorCodeText = responseMessage.getMessageSpecification().getElementValue("tmdd:errorReportMsg.error-text");
                if (!errorCode.equals(expectedErrorResponseType))
                    throw new Exception("Entity " + dataType.name() + " processing of " + dialog + " returned error code " + errorCode + "(" + errorCodeText + ") instead of the expected " + expectedErrorResponseType);
            }

        results = true;

        return results;
    }

    private static boolean testEntityPUBProcessing(EntityEmulationData.EntityDataType dataType, String dialog, String messageSpecFileName, String expectedResponseMessageName, String expectedErrorResponseType, String userName) throws Exception
    {
        boolean results = false;

        String[] fileLines = EmulationDataFileProcessor.getContent(messageSpecFileName).toString().split("\n");
        ArrayList<String> input = new ArrayList();
        for (int ii = 0; ii < fileLines.length; ii++)
            input.add(fileLines[ii]);
        MessageSpecification thisSpec = new MessageSpecification(input);

        TMDDAuthenticationProcessor.getInstance(userName, "string", "string");
        Message requestMessage = MessageManager.getInstance().createMessage(dialog);
        requestMessage.setMessageSpecification(thisSpec);
        long startTime = System.currentTimeMillis();
        Message responseMessage = RIEmulation.getInstance().getPubMessage(dialog, requestMessage);
        long responseTime = System.currentTimeMillis() - startTime;
        if (responseTime > MAX_RR_RESPONSE_TIME)
            throw new Exception("Entity " + dataType.name() + " processing of " + dialog + " exceeded the MAX_RR_RESPONSE_TIME with value " + responseTime);

        for (String theName: responseMessage.getMessageSpecification().getMessageTypes())
            if (!theName.equals(expectedResponseMessageName))
                throw new Exception("Entity " + dataType.name() + " processing of " + dialog + " returned message " + theName + " instead of the expected " + expectedResponseMessageName
                                    + (theName.contains("errorReportMsg") ? " with error description " + responseMessage.getMessageSpecification().getElementValue("tmdd:errorReportMsg.error-text") : ""));
            else if (theName.contains("errorReportMsg"))
            {
                String errorCode = responseMessage.getMessageSpecification().getElementValue("tmdd:errorReportMsg.error-code");
                String errorCodeText = responseMessage.getMessageSpecification().getElementValue("tmdd:errorReportMsg.error-text");
                if (!errorCode.equals(expectedErrorResponseType))
                    throw new Exception("Entity " + dataType.name() + " processing of " + dialog + " returned error code " + errorCode + "(" + errorCodeText + ") instead of the expected " + expectedErrorResponseType);
            }

        results = true;

        return results;
    }

    private static boolean testEntityInitialization(EntityEmulationData.EntityDataType dataType)
    {
        boolean result = false;

        try
        {
            // Try and Clear the data
            RIEmulationParameters testParameters = new RIEmulationParameters();
            testParameters.setCommandQueueLength(1);
            ArrayList<RIEmulationEntityValueSet> theList = new ArrayList();
            RIEmulationEntityValueSet testSet = new RIEmulationEntityValueSet();
            theList.add(testSet);
            testParameters.setEntityDataMap(theList);

            testSet.setValueSetName(dataType.name());
            RIEmulation.getInstance().initialize("TMDD v3.03d", testParameters);
            int currentEntityCount = getEntityCount(dataType);
            if (currentEntityCount > 0)
                throw new Exception("The entity count for entity type " + dataType + " was " + currentEntityCount + " after blank initialization.");

            // Set the Table to something so we can confirm that we can actually clear the information.
            testSet.setEntityDataSet(EmulationDataFileProcessor.getByteArray(TEST_FILE_PATH + "" + dataType + ".out"));
            RIEmulation.getInstance().initialize("TMDD v3.03d", testParameters);

            // Verify that we have something
            currentEntityCount = getEntityCount(dataType);
            if (currentEntityCount == 0)
                throw new Exception("The entity count for entity type " + dataType + " was " + currentEntityCount + " after Data initialization.");

            // Clear the data to confirm that we actually cleared everything
            testSet.setEntityDataSet(null);
            RIEmulation.getInstance().initialize("TMDD v3.03d", testParameters);
            currentEntityCount = getEntityCount(dataType);
            if (currentEntityCount > 0)
                throw new Exception("The entity count for entity type " + dataType + " was " + currentEntityCount + " after blank initialization.");

            testSet.setEntityDataSet(EmulationDataFileProcessor.getByteArray(TEST_FILE_PATH + "" + dataType + ".out"));
            // Set the data again to use in future testing.
            RIEmulation.getInstance().initialize("TMDD v3.03d", testParameters);
            currentEntityCount = getEntityCount(dataType);
            if (currentEntityCount == 0)
                throw new Exception("The entity count for entity type " + dataType + " was " + currentEntityCount + " after Data initialization.");

            result = true;
        }
        catch (Exception ex)
        {
            result = false;
            ex.printStackTrace();
        }
        return result;
    }

    private static int getEntityElementCount(EntityEmulationData.EntityDataType dataType, String entityId) throws Exception
    {
        int result = 0;

        ArrayList<DataFilter> theFilters = new ArrayList();

        // device-id filter
        ArrayList<String> deviceIdFilterValues = new ArrayList();
        deviceIdFilterValues.add(entityId);
        ValueInSetFilter deviceIdFilter = new ValueInSetFilter(dataType, "EntityId", deviceIdFilterValues);
        theFilters.add(deviceIdFilter);

        ArrayList<EntityDataRecord> records = EntityEmulationData.getEntityElements(dataType, theFilters);
        result = records.size();

        return result;
    }

    public static int getEntityCount(EntityEmulationData.EntityDataType dataType) throws Exception
    {
        int result = 0;

        int entityCount = EntityEmulationData.getEntityCount(dataType);
        result = entityCount;

        return result;
    }

    private static boolean testEntityExistence(EntityEmulationData.EntityDataType dataType, String existingEntityId, String existingEntityElement, String existingEntityElementValue)
    {
        boolean result = false;
        // Verify that the element of the entity provided has a value that matches what is expected.

        try
        {
            String elementValue = RIEmulation.getInstance().getEntityElementValue(dataType.name(), existingEntityId, existingEntityElement);
            result = existingEntityElementValue.equals(elementValue);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            result = false;
        }

        return result;
    }

    private static boolean testEntityAddandDelete(EntityEmulationData.EntityDataType dataType, String newEntityId)
    {
        boolean result = false;
        // Verify that the element of the entity provided has a value that matches what is expected.

        try
        {
            int beforeCount = getEntityCount(dataType);
            RIEmulation.getInstance().addEntity(dataType.name(), newEntityId);
            int afterCount = getEntityCount(dataType);

            if (beforeCount >= afterCount)
                throw new Exception("Count Error with testEntityAddAndDelete");

            RIEmulation.getInstance().initialize();

            beforeCount = getEntityCount(dataType);
            RIEmulation.getInstance().addEntity(dataType.name(), newEntityId);
            afterCount = getEntityCount(dataType);

            if (beforeCount >= afterCount)
                throw new Exception("Count Error with testEntityAddAndDelete after Initialize.");

            beforeCount = getEntityCount(dataType);
            RIEmulation.getInstance().deleteEntity(dataType.name(), newEntityId);
            afterCount = getEntityCount(dataType);

            if (beforeCount <= afterCount)
                throw new Exception("Count Error with testEntityAddAndDelete after delete.");

            result = true;

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            result = false;
        }

        return result;
    }

    public static void initNRTMSelections() throws Exception
    {
        TMDDNRTMSelections nrtmSelections = TMDDNRTMSelections.getInstance();
        nrtmSelections.initialize();
        nrtmSelections.addNRTM("2.3.1.1", "3.3.1.1.1");
        nrtmSelections.addNRTM("2.3.1.1", "3.3.1.1.2");
        nrtmSelections.addNRTM("2.3.1.1", "3.3.1.1.3");
        nrtmSelections.addNRTM("2.3.1.1", "3.3.1.1.4");
        nrtmSelections.addNRTM("2.3.1.1", "3.3.1.1.4.1");
        nrtmSelections.addNRTM("2.3.1.1", "3.3.1.1.5");
        nrtmSelections.addNRTM("2.3.1.1", "3.3.1.1.5.1");
        nrtmSelections.addNRTM("2.3.1.1", "3.3.1.1.5.2.1");
        nrtmSelections.addNRTM("2.3.1.1", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.1.1", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.1.1", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.1.2", "3.3.1.2");
        nrtmSelections.addNRTM("2.3.1.2", "3.4.2");
        nrtmSelections.addNRTM("2.3.1.3", "3.3.1.3.1");
        nrtmSelections.addNRTM("2.3.1.3", "3.3.1.3.2");
        nrtmSelections.addNRTM("2.3.1.3", "3.4.1");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.1");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.2");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.3");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.4");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.4.1");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.4.2.1");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.4.2.1.1");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.4.2.2");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.4.2.3");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.1");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.1");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.2");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.3");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.1");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.1");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.2");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.3");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.4");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.5");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.6");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.7");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.8");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.9");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.10");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.11");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.12");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.13");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.14");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.15");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.16");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.4.2.17");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.5");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.5.1");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.5.2.1");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.5.2.2");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.5.2.3");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.5.2.4");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.5.2.5");
        nrtmSelections.addNRTM("2.3.2", "3.3.2.5.2.6");
        nrtmSelections.addNRTM("2.3.2", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.2", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.2", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.9.1");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.9.2");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.9.3");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.9.4");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.9.4.1");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.9.4.2.1");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.9.4.2.1.1");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.9.4.2.2");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.4.3.1");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.4.3.2");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.4.3.3");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.4.3.4");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.4.3.5");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.4.3.6");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.4.3.7");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.4.3.8");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.4.3.9");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.4.3.10");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.4.3.11");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.4.3.12");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.4.3.13");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.4.3.14");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.4.3.15");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.9.5");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.9.5.1");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.9.5.2.1");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.3.9.5.2.2");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.3.1", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.2");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.3");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.2.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.2.1.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.2.2");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.3.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.3.2");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.3.3");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.3.4");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.3.5");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.3.6");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.3.7");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.3.8");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.3.9");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.3.10");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.3.11");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.3.12");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.3.13");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.3.14");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.4.3.15");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.5");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.1.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.1.2.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.1.2.2");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.1.2.3");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.2.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.2.2.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.3.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.3.2.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.1.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.1.2.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.1.2.1.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.1.2.4");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.1.2.5");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.1.2.6");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.2");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.4");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.5");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.5.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.5.2");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.6");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.6.3.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.6.3.2");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.7");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.7.6");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.7.7.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.7.7.2");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.7.7.3");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.7.7.4");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.8");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.9");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.9.1.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.9.1.2");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.9.1.3");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.9.1.4");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.9.1.5");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.9.1.6");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.10");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.10.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.10.2.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.10.2.2");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.10.2.3");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.11");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.12");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.13");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.13.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.13.2");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.6.4.13.3");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.7.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.7.3");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.7.3.4");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.7.5");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.7.5.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.7.5.2.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.7.5.2.2");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.7.5.2.3");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.7.6");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.7.6.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.3.7.6.2.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.3.2", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.2");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.3");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4.2.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4.2.1.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4.2.2");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4.3.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4.3.2");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4.3.3");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4.3.4");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4.3.5");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4.3.6");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4.3.7");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4.3.8");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4.3.9");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4.3.10");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4.3.11");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.4.3.12");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.5");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.1.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.1.2.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.1.2.2");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.1.2.3");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.2.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.2.2.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.3.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.3.2.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.1.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.1.2.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.1.2.1.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.1.2.2");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.1.2.3");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.1.2.7");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.1.2.8");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.1.2.9");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.1.2.10");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.2");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.3");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.4");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.5");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.5.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.5.2");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.6");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.6.3.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.6.3.2");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.7");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.7.6");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.7.7.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.7.7.2");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.7.7.3");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.7.7.4");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.8");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.9");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.9.1.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.9.1.2");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.9.1.3");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.9.1.4");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.9.1.5");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.9.1.6");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.10");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.10.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.10.2.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.10.2.2");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.10.2.3");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.11");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.6.4.12");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.7.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.7.2");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.7.2.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.7.3");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.7.3.4");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.7.5");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.7.5.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.7.5.2.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.7.5.2.2");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.7.5.2.3");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.7.6");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.7.6.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.3.7.6.2.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.3.3", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.2");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.3");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4.2.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4.2.1.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4.2.2");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4.3.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4.3.2");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4.3.3");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4.3.4");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4.3.5");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4.3.6");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4.3.7");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4.3.8");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4.3.9");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4.3.10");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4.3.11");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.4.3.12");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.5");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.1.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.1.2.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.1.2.2");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.1.2.3");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.2.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.2.2.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.3.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.3.2.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.1.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.1.2.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.1.2.1.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.1.2.3");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.1.2.4");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.1.2.5");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.1.2.6");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.1.2.9");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.2");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.4");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.5");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.5.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.5.2");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.6");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.6.3.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.6.3.2");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.7");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.7.6");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.7.7.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.7.7.2");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.7.7.3");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.7.7.4");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.8");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.9");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.9.1.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.9.1.2");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.9.1.3");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.9.1.4");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.9.1.5");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.9.1.6");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.10");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.10.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.10.2.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.10.2.2");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.10.2.3");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.11");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.6.4.12");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.7.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.7.3");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.7.3.4");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.7.5");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.7.5.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.7.5.2.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.7.5.2.2");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.7.5.2.3");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.7.6");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.7.6.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.3.7.6.2.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.3.4", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.8.1");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.8.2");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.8.3");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4.1");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4.2.1");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4.2.1.1");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4.2.2");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4.3.1");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4.3.2");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4.3.3");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4.3.4");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4.3.5");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4.3.6");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4.3.7");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4.3.8");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4.3.9");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4.3.10");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4.3.11");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.4.3.12");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.8.4");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.8.4.1");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.3.8.4.2.1");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.3.5", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.2.1.1");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.2.1.2");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.2.1.3");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.1.1");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.1.1.1");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.1.1.2.1");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.1.1.2.2");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.1.1.2.3");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.2.1.4");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.2.1.5");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.2.1.5.1");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.2.1.5.2.1");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.2.1.5.2.2");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.2.1.5.2.3");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.2.1.5.2.4");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.2.1.5.2.5");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.2.1.5.2.6");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.2.1.5.2.7");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.2.1.5.2.8");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.2.1.5.2.9");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.4.2.1.5.2.10");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.4.1.1", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.1");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.2");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.3");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.1.1");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.1.1.1");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.1.1.2.1");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.1.1.2.2");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.1.1.2.3");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.4");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.1");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.1");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.2");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.3");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.4");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.5");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.6");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.7");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.8");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.9");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.10");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.11");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.12");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.13");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.14");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.15");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.16");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.17");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.4.3.1.5.2.18");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.4.1.2", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.4.1.1");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.4.1.2");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.4.1.3");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.1.1");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.1.1.1");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.1.1.2.1");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.1.1.2.2");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.1.1.2.3");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.4.1.4");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.4.1.5");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.4.1.5.1");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.4.1.5.2.1");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.4.1.5.2.2");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.4.1.5.2.3");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.4.1.5.2.4");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.4.1.5.2.5");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.4.1.5.2.6");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.4.1.5.2.7");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.4.1.5.2.8");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.4.4.1.5.2.9");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.4.1.3", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.4.2.2.1");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.4.2.2.2");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.4.2.2.3");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.4.1.1");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.4.1.1.1");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.4.1.1.2.1");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.4.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.4.1.1.2.2");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.4.1.1.2.3");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.4.2.2.4");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.4.2.2.5");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.4.2.2.5.1");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.4.2.2.5.2.1");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.4.2.2.5.2.2");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.4.2.2.5.2.3");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.4.2.2.5.2.4");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.4.2.1", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.3.2.1");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.3.2.2");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.3.2.3");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.1.1");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.1.1.1");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.1.1.2.1");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.1.1.2.2");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.1.1.2.3");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.3.2.4");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.3.2.5");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.3.2.5.1");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.3.2.5.2.1");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.3.2.5.2.2");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.3.2.5.2.3");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.3.2.5.2.4");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.3.2.5.2.19");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.3.2.5.2.37");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.4.3.2.5.2.38");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.4.2.2", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.4.2.1");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.4.2.2");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.4.2.3");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.1.1");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.1.1.1");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.1.1.2.1");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.1.1.2.2");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.1.1.2.3");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.4.2.4");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.4.2.5");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.4.2.5.1");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.4.2.5.2.1");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.4.2.5.2.2");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.4.2.5.2.3");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.4.2.5.2.21");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.4.4.2.5.2.22");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.4.2.3", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.1");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.2");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.3");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.1.1");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.1.1.1");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.1.1.2.1");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.1.1.2.2");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.1.1.2.3");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.4");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.1");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.1");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.2");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.3");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.4");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.5");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.6");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.7");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.8");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.9");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.10");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.11");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.12");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.13");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.14");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.15");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.16");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.17");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.18");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.19");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.20");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.21");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.22");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.23");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.24");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.25");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.26");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.27");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.28");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.29");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.30");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.31");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.32");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.33");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.34");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.35");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.36");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.37");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.4.3.2.5.2.38");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.4.3", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.1");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.2");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.3");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.1.1");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.1.1.1");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.1.1.2.1");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.1.1.2.2");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.1.1.2.3");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.4");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.1");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.1");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.2");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.3");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.4");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.5");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.6");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.7");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.8");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.9");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.10");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.11");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.12");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.13");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.14");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.15");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.16");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.17");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.18");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.19");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.20");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.21");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.4.4.2.5.2.22");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.4.4", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.2.1.1");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.2.1.2");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.2.1.3");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.2.1.4");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.2.1.5");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.2.1.5.1");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.2.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.2.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.2.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.2.1.5.2.4");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.2.1.5.2.5");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.2.1.5.2.6");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.2.1.5.2.7");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.2.1.5.2.8");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.5.2.1.5.2.9");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.1.1", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.2.1.2");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.2.1.3");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.2.1.4");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.2.1.5");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.2.1.5.1");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.2.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.2.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.2.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.2.1.5.2.4");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.2.1.5.2.5");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.2.1.5.2.6");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.2.1.5.2.7");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.2.1.5.2.8");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.5.2.1.5.2.9");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.1.2", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.2.2.1");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.2.2.2");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.2.2.3");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.2.2.4");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.3.1");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.3.1.1");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.3.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.3.1.2.2");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.3.1.2.3");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.3.1.2.4");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.3.1.2.5");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.3.1.2.6");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.1.3.1.2.7");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.2.2.5");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.2.2.5.1");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.2.2.5.2.1");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.2.2.5.2.2");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.2.2.5.2.3");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.5.2.2.5.2.4");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.1.3", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.4", "3.3.6.2.1");
        nrtmSelections.addNRTM("2.3.5.1.4", "3.3.6.2.2");
        nrtmSelections.addNRTM("2.3.5.1.4", "3.3.6.2.2.1");
        nrtmSelections.addNRTM("2.3.5.1.4", "3.3.6.2.3");
        nrtmSelections.addNRTM("2.3.5.1.4", "3.3.6.2.3.1");
        nrtmSelections.addNRTM("2.3.5.1.4", "3.3.6.2.3.2.1");
        nrtmSelections.addNRTM("2.3.5.1.4", "3.3.6.2.3.2.2");
        nrtmSelections.addNRTM("2.3.5.1.4", "3.3.6.2.3.2.3");
        nrtmSelections.addNRTM("2.3.5.1.4", "3.3.6.2.3.2.4");
        nrtmSelections.addNRTM("2.3.5.1.4", "3.3.6.2.3.2.5");
        nrtmSelections.addNRTM("2.3.5.1.4", "3.3.6.2.3.2.6");
        nrtmSelections.addNRTM("2.3.5.1.4", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.1.4", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.1.4", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.2.1.1");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.2.1.2");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.2.1.3");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.2.1.4");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.2.1.5");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.2.1.5.1");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.2.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.5.2.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.1.5", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.1");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.2");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.3");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.4");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.4.1");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.4.2.1");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.4.2.2");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.1");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.1");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.2");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.3");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.4");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.5");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.6");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.7");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.8");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.9");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.10");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.11");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.12");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.13");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.14");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.15");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.16");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.5.2.3.5.2.17");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.1.6", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.2.4.1");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.2.4.2");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.2.4.2.1");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.2.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.2.4.3");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.2.4.3.1");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.2.4.3.2.1");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.2.4.3.2.2");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.2.4.3.2.3");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.2.4.3.2.4");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.2.4.3.2.5");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.2.4.3.2.6");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.2.4.3.2.7");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.2.4.3.2.8");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.2.4.3.2.9");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.5.2.4.3.2.10");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.1.7", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.3.1.1");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.3.1.2");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.3.1.3");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.3.1.4");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.3.1.5");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.3.1.5.1");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.3.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.3.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.3.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.3.1.5.2.4");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.3.1.5.2.5");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.3.1.5.2.6");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.3.1.5.2.7");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.3.1.5.2.8");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.3.1.5.2.9");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.5.3.1.5.2.10");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.2.1", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.3.1.2");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.3.1.3");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.3.1.4");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.3.1.5");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.3.1.5.1");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.3.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.3.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.3.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.3.1.5.2.4");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.3.1.5.2.5");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.3.1.5.2.6");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.3.1.5.2.7");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.3.1.5.2.8");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.3.1.5.2.9");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.5.3.1.5.2.10");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.2.2", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.3.2.1");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.3.2.2");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.3.2.3");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.3.2.4");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.3.1");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.3.1.1");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.3.1.2.1");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.3.1.2.2");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.3.1.2.3");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.3.1.2.4");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.3.1.2.5");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.3.1.2.6");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.1.3.1.2.7");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.3.2.5");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.3.2.5.1");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.3.2.5.2.1");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.3.2.5.2.2");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.3.2.5.2.3");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.3.2.5.2.4");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.3.2.5.2.5");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.3.2.5.2.6");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.3.2.5.2.7");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.3.2.5.2.8");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.5.3.2.5.2.9");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.2.3", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.3.3.1");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.1.4.1");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.1.4.1.2.2");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.1.4.1.2.3");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.1.4.1.2.4");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.1.4.1.2.5");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.1.4.1.2.6");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.1.4.1.2.7");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.3.3.2");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.5.3.3.3");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.2.4", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.5.1.5.1");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.5.3.4.1");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.5.1.5.2");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.5.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.5.1.5.2.2.1");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.5.1.5.2.2.1.1");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.5.3.4.2");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.5.1.5.3");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.5.3.4.3");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.2.5", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.2.6", "3.3.5.1.6.1");
        nrtmSelections.addNRTM("2.3.5.2.6", "3.3.5.3.5.1");
        nrtmSelections.addNRTM("2.3.5.2.6", "3.3.5.1.6.2");
        nrtmSelections.addNRTM("2.3.5.2.6", "3.3.5.1.6.2.1");
        nrtmSelections.addNRTM("2.3.5.2.6", "3.3.5.1.6.2.2.1");
        nrtmSelections.addNRTM("2.3.5.2.6", "3.3.5.3.5.2");
        nrtmSelections.addNRTM("2.3.5.2.6", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.2.6", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.2.6", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.2.6", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.2.6", "3.3.5.1.6.3");
        nrtmSelections.addNRTM("2.3.5.2.6", "3.3.5.3.5.3");
        nrtmSelections.addNRTM("2.3.5.2.6", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.2.6", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.2.6", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.4.1.1");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.4.1.2");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.4.1.3");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.4.1.4");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.4.1.5");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.4.1.5.1");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.4.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.4.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.5.4.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.3.1", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.4.1.2");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.4.1.3");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.4.1.4");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.4.1.5");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.4.1.5.1");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.4.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.4.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.5.4.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.3.2", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.4.2.1");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.4.2.2");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.4.2.3");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.4.2.4");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.3.1");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.3.1.1");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.3.1.2.1");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.3.1.2.2");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.3.1.2.3");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.3.1.2.4");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.3.1.2.5");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.3.1.2.6");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.1.3.1.2.7");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.4.2.5");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.4.2.5.1");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.5.4.2.5.2.1");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.3.3", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.4.3.1");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.1.4.1");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.1.4.1.2.2");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.1.4.1.2.3");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.1.4.1.2.4");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.1.4.1.2.5");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.1.4.1.2.6");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.1.4.1.2.7");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.4.3.2");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.4.3.2.1");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.4.3.2.2.1");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.4.3.2.2.2");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.4.3.2.2.3");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.4.3.2.2.4");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.4.3.2.2.5");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.4.3.2.2.6");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.5.4.3.3");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.3.4", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.5.1.5.1");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.5.4.4.1");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.5.1.5.2");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.5.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.5.1.5.2.2.1");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.5.1.5.2.2.1.1");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.5.4.4.2");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.5.1.5.3");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.5.4.4.3");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.3.5", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.3.6", "3.3.5.1.6.1");
        nrtmSelections.addNRTM("2.3.5.3.6", "3.3.5.4.5.1");
        nrtmSelections.addNRTM("2.3.5.3.6", "3.3.5.1.6.2");
        nrtmSelections.addNRTM("2.3.5.3.6", "3.3.5.1.6.2.1");
        nrtmSelections.addNRTM("2.3.5.3.6", "3.3.5.1.6.2.2.1");
        nrtmSelections.addNRTM("2.3.5.3.6", "3.3.5.4.5.2");
        nrtmSelections.addNRTM("2.3.5.3.6", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.3.6", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.3.6", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.3.6", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.3.6", "3.3.5.1.6.3");
        nrtmSelections.addNRTM("2.3.5.3.6", "3.3.5.4.5.3");
        nrtmSelections.addNRTM("2.3.5.3.6", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.3.6", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.3.6", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.1");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.2");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.3");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.4");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.1");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.2.4");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.2.5");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.2.6");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.2.7");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.2.8");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.2.9");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.2.10");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.2.11");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.2.12");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.2.13");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.2.14");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.2.15");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.5.5.1.5.2.16");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.4.1", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.2");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.3");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.4");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.1");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.2.4");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.2.5");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.2.6");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.2.7");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.2.8");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.2.9");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.2.10");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.2.11");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.2.12");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.2.13");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.2.14");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.2.15");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.5.5.1.5.2.16");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.4.2", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.5.2.1");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.5.2.2");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.5.2.3");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.5.2.4");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.3.1");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.3.1.1");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.3.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.3.1.2.2");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.3.1.2.3");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.3.1.2.4");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.3.1.2.5");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.3.1.2.6");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.1.3.1.2.7");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.5.2.5");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.5.2.5.1");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.5.2.5.2.1");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.5.2.5.2.2");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.5.2.5.2.3");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.5.5.2.5.2.4");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.4.3", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.5.3.1");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.1.4.1");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.1.4.1.2.2");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.1.4.1.2.3");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.1.4.1.2.4");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.1.4.1.2.5");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.1.4.1.2.6");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.1.4.1.2.7");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.5.3.2");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.5.3.2.1");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.5.3.2.2.1");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.5.5.3.3");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.4.4", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.5.1.5.1");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.5.5.4.1");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.5.1.5.2");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.5.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.5.1.5.2.2.1");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.5.1.5.2.2.1.1");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.5.5.4.2");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.5.1.5.3");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.5.5.4.3");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.4.5", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.5.5.9.1");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.5.1.7.1");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.5.1.7.1.1");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.5.1.7.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.5.1.7.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.5.5.9.2");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.5.1.7.2");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.5.1.7.2.1");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.5.1.7.2.2.1");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.5.1.7.2.2.2");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.5.1.7.2.2.3");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.5.1.7.2.2.4");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.5.1.7.2.2.5");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.5.1.7.2.2.6");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.5.1.7.2.2.7");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.5.5.9.3");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.4.6", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.7", "3.3.5.1.6.1");
        nrtmSelections.addNRTM("2.3.5.4.7", "3.3.5.5.5.1");
        nrtmSelections.addNRTM("2.3.5.4.7", "3.3.5.1.6.2");
        nrtmSelections.addNRTM("2.3.5.4.7", "3.3.5.1.6.2.1");
        nrtmSelections.addNRTM("2.3.5.4.7", "3.3.5.1.6.2.2.1");
        nrtmSelections.addNRTM("2.3.5.4.7", "3.3.5.5.5.2");
        nrtmSelections.addNRTM("2.3.5.4.7", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.4.7", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.4.7", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.4.7", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.4.7", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.4.7", "3.3.5.1.6.3");
        nrtmSelections.addNRTM("2.3.5.4.7", "3.3.5.5.5.3");
        nrtmSelections.addNRTM("2.3.5.4.7", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.4.7", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.4.7", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.5.6.1");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.5.8.1");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.1.1.1.3.7");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.5.6.2");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.5.8.2");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.5.8.2.1");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.5.8.2.2.1");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.5.6.3");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.5.6.3.1");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.5.6.3.2");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.5.6.3.3.1");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.5.8.3");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.5.8.3.1");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.5.8.3.2.1");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.5.5.8.3.2.2");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.4.8", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.5.7.1");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.5.7.2");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.5.7.3");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.1.1.1.3.7");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.5.7.4");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.5.7.4.1");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.5.7.4.2.1");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.5.7.4.2.2");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.5.7.5");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.5.7.5.1");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.5.7.5.2.1");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.5.5.7.5.2.2");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.4.9", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.5.8.1");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.5.8.2");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.5.8.2.1");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.5.8.2.2.1");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.5.8.3");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.5.8.3.1");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.5.8.3.2.1");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.5.5.8.3.2.2");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.4.10", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.6.1.1");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.6.1.2");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.6.1.3");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.6.1.4");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.6.1.5");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.6.1.5.1");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.6.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.6.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.6.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.6.1.5.2.4");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.6.1.5.2.5");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.5.6.1.5.2.6");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.5.1", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.6.1.2");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.6.1.3");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.6.1.4");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.6.1.5");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.6.1.5.1");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.6.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.6.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.6.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.6.1.5.2.4");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.6.1.5.2.5");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.5.6.1.5.2.6");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.5.2", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.6.2.1");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.6.2.2");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.6.2.3");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.6.2.4");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.3.1");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.3.1.1");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.3.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.3.1.2.2");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.3.1.2.3");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.3.1.2.4");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.3.1.2.5");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.3.1.2.6");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.1.3.1.2.7");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.5.6.2.5");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.5.3", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.6.3.1");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.6.3.2");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.6.3.3");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.6.3.4");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.6.3.5");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.6.3.5.1");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.5.6.3.5.2.1");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.5.4", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.1");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.2");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.1");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.2.1");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.2.2");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.3");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.4.1");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.4.2");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.5");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.1");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.1");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.2");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.3");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.4");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.5");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.6");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.7");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.8");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.9");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.10");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.11");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.12");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.13");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.14");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.15");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.16");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.17");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.18");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.19");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.20");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.21");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.22");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.23");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.24");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.25");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.26");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.6.2.27");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.1");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.2");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.3");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.4");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.5");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.6");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.7");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.8");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.9");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.10");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.11");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.12");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.13");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.14");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.15");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.16");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.17");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.18");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.7.19");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.1");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.2");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.3");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.4");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.5");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.6");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.7");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.8");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.9");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.10");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.11");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.12");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.13");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.14");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.15");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.16");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.17");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.18");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.19");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.20");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.21");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.22");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.23");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.8.24");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.9");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.10");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.11");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.5.6.4.3.12");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.5.5", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.5.6.3.1");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.5.6.3.2");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.5.6.3.3");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.5.6.3.4");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.5.6.3.5");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.5.6.3.5.1");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.5.6", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.6.4.1");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.6.4.2");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.6.4.3");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.6.4.3.1");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.6.4.3.2.1");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.6.4.3.2.2");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.6.4.3.3");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.6.4.3.5");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.6.4.3.10");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.5.6.4.3.12");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.5.7", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.7.1.1");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.7.1.2");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.7.1.3");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.7.1.4");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.7.1.5");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.7.1.5.1");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.5.7.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.6.1", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.7.1.2");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.7.1.3");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.7.1.4");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.7.1.5");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.7.1.5.1");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.5.7.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.6.2", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.7.2.1");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.7.2.2");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.7.2.3");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.7.2.4");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.3.1");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.3.1.1");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.3.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.3.1.2.2");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.3.1.2.3");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.3.1.2.4");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.3.1.2.5");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.3.1.2.6");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.1.3.1.2.7");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.5.7.2.5");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.6.3", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.7.3.1");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.1.4.1");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.1.4.1.2.2");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.1.4.1.2.3");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.1.4.1.2.4");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.1.4.1.2.5");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.1.4.1.2.6");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.1.4.1.2.7");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.7.3.2");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.5.7.3.3");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.6.4", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.5.1.5.1");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.5.7.4.1");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.5.1.5.2");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.5.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.5.1.5.2.2.1");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.5.1.5.2.2.1.1");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.5.7.4.2");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.5.1.5.3");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.5.7.4.3");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.6.5", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.6", "3.3.5.1.6.1");
        nrtmSelections.addNRTM("2.3.5.6.6", "3.3.5.7.5.1");
        nrtmSelections.addNRTM("2.3.5.6.6", "3.3.5.1.6.2");
        nrtmSelections.addNRTM("2.3.5.6.6", "3.3.5.1.6.2.1");
        nrtmSelections.addNRTM("2.3.5.6.6", "3.3.5.1.6.2.2.1");
        nrtmSelections.addNRTM("2.3.5.6.6", "3.3.5.7.5.2");
        nrtmSelections.addNRTM("2.3.5.6.6", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.6.6", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.6.6", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.6.6", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.6.6", "3.3.5.1.6.3");
        nrtmSelections.addNRTM("2.3.5.6.6", "3.3.5.7.5.3");
        nrtmSelections.addNRTM("2.3.5.6.6", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.6.6", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.6.6", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.7.6.1");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.7.6.2");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.7.6.3");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.7.6.4");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.1.8.1.1");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.1.8.1.2.1");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.5.7.6.5");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.6.7", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.8.1.1");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.8.1.2");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.8.1.3");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.8.1.4");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.8.1.5");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.8.1.5.1");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.8.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.8.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.5.8.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.7.1", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.8.1.2");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.8.1.3");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.8.1.4");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.8.1.5");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.8.1.5.1");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.8.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.8.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.5.8.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.7.2", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.8.2.1");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.8.2.2");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.8.2.3");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.8.2.4");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.3.1");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.3.1.1");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.3.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.3.1.2.2");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.3.1.2.3");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.3.1.2.4");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.3.1.2.5");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.3.1.2.6");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.1.3.1.2.7");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.8.2.5");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.8.2.5.1");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.8.2.5.2.1");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.5.8.2.5.2.2");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.7.3", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.8.3.1");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.1.4.1");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.1.4.1.2.2");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.1.4.1.2.3");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.1.4.1.2.4");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.1.4.1.2.5");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.1.4.1.2.6");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.1.4.1.2.7");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.8.3.2");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.8.3.2.1");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.5.8.3.3");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.7.4", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.5.1.5.1");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.5.8.4.1");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.5.1.5.2");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.5.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.5.1.5.2.2.1");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.5.1.5.2.2.1.1");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.5.8.4.2");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.5.1.5.3");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.5.8.4.3");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.7.5", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.5.8.8.1");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.5.1.7.1");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.5.1.7.1.1");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.5.1.7.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.5.1.7.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.5.8.8.2");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.5.1.7.2");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.5.1.7.2.1");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.5.1.7.2.2.1");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.5.1.7.2.2.2");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.5.1.7.2.2.3");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.5.1.7.2.2.4");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.5.1.7.2.2.5");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.5.1.7.2.2.6");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.5.1.7.2.2.7");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.5.8.8.3");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.7.6", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.7", "3.3.5.1.6.1");
        nrtmSelections.addNRTM("2.3.5.7.7", "3.3.5.8.5.1");
        nrtmSelections.addNRTM("2.3.5.7.7", "3.3.5.1.6.2");
        nrtmSelections.addNRTM("2.3.5.7.7", "3.3.5.1.6.2.1");
        nrtmSelections.addNRTM("2.3.5.7.7", "3.3.5.1.6.2.2.1");
        nrtmSelections.addNRTM("2.3.5.7.7", "3.3.5.8.5.2");
        nrtmSelections.addNRTM("2.3.5.7.7", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.7.7", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.7.7", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.7.7", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.7.7", "3.3.5.1.6.3");
        nrtmSelections.addNRTM("2.3.5.7.7", "3.3.5.8.5.3");
        nrtmSelections.addNRTM("2.3.5.7.7", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.7.7", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.7.7", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.8.6.1");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.8.6.2");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.8.6.3");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.8.6.4");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.1.8.1.1");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.1.8.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.5.8.6.5");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.7.8", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.8.7.1");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.8.7.2");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.8.7.3");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.1.1.1.3.7");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.8.7.4");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.8.7.5");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.8.7.5.1");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.8.7.5.2.1");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.5.8.7.5.2.2");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.7.9", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.9.1.1");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.9.1.2");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.9.1.3");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.9.1.4");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.9.1.5");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.9.1.5.1");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.5.9.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.8.1", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.9.1.2");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.9.1.3");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.9.1.4");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.9.1.5");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.9.1.5.1");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.5.9.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.8.2", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.9.2.1");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.9.2.2");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.9.2.3");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.9.2.4");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.3.1");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.3.1.1");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.3.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.3.1.2.2");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.3.1.2.3");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.3.1.2.4");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.3.1.2.5");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.3.1.2.6");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.1.3.1.2.7");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.5.9.2.5");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.8.3", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.9.3.1");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.1.4.1");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.1.4.1.2.2");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.1.4.1.2.3");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.1.4.1.2.4");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.1.4.1.2.5");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.1.4.1.2.6");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.1.4.1.2.7");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.9.3.2");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.5.9.3.3");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.8.4", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.5.1.5.1");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.5.9.4.1");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.5.1.5.2");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.5.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.5.1.5.2.2.1");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.5.1.5.2.2.1.1");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.5.9.4.2");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.5.1.5.3");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.5.9.4.3");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.8.5", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.6", "3.3.5.1.6.1");
        nrtmSelections.addNRTM("2.3.5.8.6", "3.3.5.9.5.1");
        nrtmSelections.addNRTM("2.3.5.8.6", "3.3.5.1.6.2");
        nrtmSelections.addNRTM("2.3.5.8.6", "3.3.5.1.6.2.1");
        nrtmSelections.addNRTM("2.3.5.8.6", "3.3.5.1.6.2.2.1");
        nrtmSelections.addNRTM("2.3.5.8.6", "3.3.5.9.5.2");
        nrtmSelections.addNRTM("2.3.5.8.6", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.8.6", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.8.6", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.8.6", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.8.6", "3.3.5.1.6.3");
        nrtmSelections.addNRTM("2.3.5.8.6", "3.3.5.9.5.3");
        nrtmSelections.addNRTM("2.3.5.8.6", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.8.6", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.8.6", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.9.6.1");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.9.6.2");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.9.6.3");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.9.6.4");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.1.8.1.1");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.1.8.1.2.1");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.5.9.6.5");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.8.7", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.10.1.1");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.10.1.2");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.10.1.3");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.10.1.4");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.10.1.5");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.10.1.5.1");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.10.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.10.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.10.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.10.1.5.2.4");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.10.1.5.2.5");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.10.1.5.2.6");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.5.10.1.5.2.7");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.9.1", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.10.1.2");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.10.1.3");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.10.1.4");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.10.1.5");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.10.1.5.1");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.10.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.10.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.10.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.10.1.5.2.4");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.10.1.5.2.5");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.10.1.5.2.6");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.5.10.1.5.2.7");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.9.2", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.1");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.2");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.3");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.4");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.3.1");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.3.1.1");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.3.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.3.1.2.2");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.3.1.2.3");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.3.1.2.4");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.3.1.2.5");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.3.1.2.6");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.1.3.1.2.7");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.1");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.1");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.2");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.3");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.4");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.5");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.6");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.7");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.8");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.9");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.10");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.11");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.12");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.13");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.14");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.15");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.16");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.17");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.18");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.19");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.20");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.5.10.2.5.2.21");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.9.3", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.10.3.1");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.1.4.1");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.1.4.1.2.2");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.1.4.1.2.3");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.1.4.1.2.4");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.1.4.1.2.5");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.1.4.1.2.6");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.1.4.1.2.7");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.10.3.2");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.10.3.2.1");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.10.3.2.2.1");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.10.3.2.2.2");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.5.10.3.3");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.9.4", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.5.1.5.1");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.5.10.4.1");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.5.1.5.2");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.5.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.5.1.5.2.2.1");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.5.1.5.2.2.1.1");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.5.10.4.2");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.5.1.5.3");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.5.10.4.3");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.9.5", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.6", "3.3.5.1.6.1");
        nrtmSelections.addNRTM("2.3.5.9.6", "3.3.5.10.5.1");
        nrtmSelections.addNRTM("2.3.5.9.6", "3.3.5.1.6.2");
        nrtmSelections.addNRTM("2.3.5.9.6", "3.3.5.1.6.2.1");
        nrtmSelections.addNRTM("2.3.5.9.6", "3.3.5.1.6.2.2.1");
        nrtmSelections.addNRTM("2.3.5.9.6", "3.3.5.10.5.2");
        nrtmSelections.addNRTM("2.3.5.9.6", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.9.6", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.9.6", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.9.6", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.9.6", "3.3.5.1.6.3");
        nrtmSelections.addNRTM("2.3.5.9.6", "3.3.5.10.5.3");
        nrtmSelections.addNRTM("2.3.5.9.6", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.9.6", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.9.6", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.10.8.1");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.1.7.1");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.1.7.1.1");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.1.7.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.1.7.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.10.8.2");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.1.7.2");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.1.7.2.1");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.1.7.2.2.1");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.1.7.2.2.2");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.1.7.2.2.3");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.1.7.2.2.4");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.1.7.2.2.5");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.1.7.2.2.6");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.1.7.2.2.7");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.10.8.3");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.10.8.3.1");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.10.8.3.2.1");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.10.8.3.2.2");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.5.10.8.3.2.3");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.9.7", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.10.6.1");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.10.6.2");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.10.6.3");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.10.6.4");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.1.8.1.1");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.1.8.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.10.6.5");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.10.6.5.1");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.10.6.5.2.1");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.10.6.5.2.2");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.10.6.5.2.3");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.10.6.5.2.4");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.10.6.5.2.5");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.10.6.5.2.6");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.5.10.6.5.2.7");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.9.8", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.10.7.1");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.10.7.2");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.10.7.3");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.1.1.1.3.7");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.10.7.4");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.10.7.5");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.10.7.5.1");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.10.7.5.2.1");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.5.10.7.5.2.2");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.9.9", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.1");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.2");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.3");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.4");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.1");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.4");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.5");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.6");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.7");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.8");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.9");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.10");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.11");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.12");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.13");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.14");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.15");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.16");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.17");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.18");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.19");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.20");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.21");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.22");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.23");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.5.11.1.5.2.24");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.1", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.2");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.3");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.4");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.2.4");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.2.6");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.2.7");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.2.9");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.2.10");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.2.12");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.1");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.4");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.5");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.6");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.7");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.8");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.9");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.10");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.11");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.12");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.13");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.14");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.15");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.16");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.17");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.18");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.19");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.20");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.21");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.22");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.23");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.5.11.1.5.2.24");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.2", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.1");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.2");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.3");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.4");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.3.1");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.3.1.1");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.3.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.3.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.3.1.2.3");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.3.1.2.4");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.3.1.2.5");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.3.1.2.6");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.1.3.1.2.7");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.1");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.1");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.2");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.3");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.4");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.5");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.6");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.7");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.8");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.9");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.10");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.11");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.12");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.13");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.14");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.15");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.16");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.17");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.18");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.19");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.20");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.21");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.22");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.23");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.24");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.25");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.26");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.27");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.28");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.29");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.30");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.5.11.2.5.2.31");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.3", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.11.3.1");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.1.4.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.1.4.1.2.3");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.1.4.1.2.4");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.1.4.1.2.5");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.1.4.1.2.6");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.1.4.1.2.7");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.11.3.2");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.11.3.2.1");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.11.3.2.2.1");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.11.3.2.2.2");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.11.3.2.2.3");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.11.3.3");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.11.3.3.1");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.11.3.3.2.1");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.11.3.3.2.2");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.11.3.3.2.3");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.5.11.3.3.2.4");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.4", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.5.1.5.1");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.5.11.4.1");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.5.1.5.2");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.5.1.5.2.1");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.5.1.5.2.2.1");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.5.1.5.2.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.5.11.4.2");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.5.1.4.2.2.2");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.5.1.5.3");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.5.11.4.3");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.5", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.11.13.1");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.1.7.1");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.1.7.1.1");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.1.7.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.1.7.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.11.13.2");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.1.7.2");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.1.7.2.1");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.1.7.2.2.1");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.1.7.2.2.2");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.1.7.2.2.3");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.1.7.2.2.4");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.1.7.2.2.5");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.1.7.2.2.6");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.1.7.2.2.7");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.11.13.3");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.11.13.3.1");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.11.13.3.2.1");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.11.13.3.2.2");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.5.11.13.3.2.3");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.6", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.7", "3.3.5.1.6.1");
        nrtmSelections.addNRTM("2.3.5.10.7", "3.3.5.11.5.1");
        nrtmSelections.addNRTM("2.3.5.10.7", "3.3.5.1.6.2");
        nrtmSelections.addNRTM("2.3.5.10.7", "3.3.5.1.6.2.1");
        nrtmSelections.addNRTM("2.3.5.10.7", "3.3.5.1.6.2.2.1");
        nrtmSelections.addNRTM("2.3.5.10.7", "3.3.5.11.5.2");
        nrtmSelections.addNRTM("2.3.5.10.7", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.10.7", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.10.7", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.10.7", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.10.7", "3.3.5.1.6.3");
        nrtmSelections.addNRTM("2.3.5.10.7", "3.3.5.11.5.3");
        nrtmSelections.addNRTM("2.3.5.10.7", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.7", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.7", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.1");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.2");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.3");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.1.1.1.3.7");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.4");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.4.1");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.4.2.1");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.5");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.5.1");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.5.2.1");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.5.2.2");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.5.2.3");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.5.2.4");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.5.2.5");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.5.2.6");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.5.2.7");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.5.2.8");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.5.2.9");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.5.2.10");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.5.11.7.5.2.11");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.8", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.11.6.1");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.11.6.2");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.11.6.3");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.11.6.4");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.1.8.1.1");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.1.8.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.11.6.5");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.5.11.6.5.1");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.9", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.1");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.2");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.3");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.4");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.1");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.2.2");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.2.3");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.2.4");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.2.5");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.2.6");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.2.7");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.2.8");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.2.9");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.2.10");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.2.11");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.2.12");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.2.13");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.2.14");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.2.15");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.2.17");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.5.11.1.5.2.18");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.10", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.11.1.1");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.11.1.2");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.11.1.3");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.11.1.4");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.2.1.2.3");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.2.1.2.5");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.2.1.2.8");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.2.1.2.11");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.2.1.2.13");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.2.1.2.14");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.2.1.2.15");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.1.2.1.2.16");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.11.1.5");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.11.1.5.1");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.5.11.1.5.2.23");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.11", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.1");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.2");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.3");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.4");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.1");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.1");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.2");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.3");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.4");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.5");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.6");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.7");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.8");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.9");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.10");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.11");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.12");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.13");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.14");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.15");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.16");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.17");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.18");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.19");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.20");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.21");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.22");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.23");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.24");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.25");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.26");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.27");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.28");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.29");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.30");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.5.11.2.5.2.31");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.12", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.11.8.1");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.11.8.2");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.11.8.3");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.1.1.1.3.6");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.11.8.4");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.11.8.5");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.11.8.5.1");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.11.8.5.2.1");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.11.8.5.2.2");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.11.8.5.2.3");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.11.8.5.2.4");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.11.8.5.2.5");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.11.8.5.2.6");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.11.8.5.2.7");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.5.11.8.5.2.8");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.13", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.1");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.2");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.2.1");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.2.2.1");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.2.2.2");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.2.2.3");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.2.2.4");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.2.2.5");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.2.2.6");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.2.2.7");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.2.2.8");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.2.2.9");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.2.2.10");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.3");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.3.1");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.3.2.1");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.3.2.2");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.3.2.3");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.3.2.4");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.5.11.9.3.2.5");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.14", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.15", "3.3.5.11.10.1");
        nrtmSelections.addNRTM("2.3.5.10.15", "3.3.5.11.10.2");
        nrtmSelections.addNRTM("2.3.5.10.15", "3.3.5.11.10.2.1");
        nrtmSelections.addNRTM("2.3.5.10.15", "3.3.5.11.10.2.2.1");
        nrtmSelections.addNRTM("2.3.5.10.15", "3.3.5.11.10.2.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.15", "3.3.5.11.9.3");
        nrtmSelections.addNRTM("2.3.5.10.15", "3.3.5.11.9.3.1");
        nrtmSelections.addNRTM("2.3.5.10.15", "3.3.5.11.9.3.2.1");
        nrtmSelections.addNRTM("2.3.5.10.15", "3.3.5.11.9.3.2.2");
        nrtmSelections.addNRTM("2.3.5.10.15", "3.3.5.11.9.3.2.3");
        nrtmSelections.addNRTM("2.3.5.10.15", "3.3.5.11.9.3.2.4");
        nrtmSelections.addNRTM("2.3.5.10.15", "3.3.5.11.9.3.2.5");
        nrtmSelections.addNRTM("2.3.5.10.15", "3.3.5.11.10.3");
        nrtmSelections.addNRTM("2.3.5.10.15", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.15", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.15", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.11.14.1");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.1.7.1");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.1.7.1.1");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.1.7.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.1.7.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.11.14.2");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.1.7.2");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.1.7.2.1");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.1.7.2.2.1");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.1.7.2.2.2");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.1.7.2.2.3");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.1.7.2.2.4");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.1.7.2.2.5");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.1.7.2.2.6");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.1.7.2.2.7");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.11.14.3");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.11.14.3.1");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.11.14.3.2.1");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.11.14.3.2.2");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.5.11.14.3.2.3");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.16", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.17", "3.3.5.1.6.1");
        nrtmSelections.addNRTM("2.3.5.10.17", "3.3.5.11.11.1");
        nrtmSelections.addNRTM("2.3.5.10.17", "3.3.5.1.6.2");
        nrtmSelections.addNRTM("2.3.5.10.17", "3.3.5.1.6.2.1");
        nrtmSelections.addNRTM("2.3.5.10.17", "3.3.5.1.6.2.2.1");
        nrtmSelections.addNRTM("2.3.5.10.17", "3.3.5.11.11.2");
        nrtmSelections.addNRTM("2.3.5.10.17", "3.3.5.1.4.2");
        nrtmSelections.addNRTM("2.3.5.10.17", "3.3.5.1.4.2.1");
        nrtmSelections.addNRTM("2.3.5.10.17", "3.3.5.1.4.2.2.1");
        nrtmSelections.addNRTM("2.3.5.10.17", "3.3.5.1.4.2.2.3");
        nrtmSelections.addNRTM("2.3.5.10.17", "3.3.5.1.6.3");
        nrtmSelections.addNRTM("2.3.5.10.17", "3.3.5.11.11.3");
        nrtmSelections.addNRTM("2.3.5.10.17", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.17", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.17", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.11.12.1");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.11.12.2");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.11.12.3");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.1.1.1.3.6");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.11.12.4");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.11.12.5");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.11.12.5.1");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.5.11.12.5.2.1");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.18", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.1");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.2");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.3");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.1.1.1.3.6");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.1.1.1.3.7");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.4");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.4.1");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.5");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.5.1");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.5.2.1");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.5.2.2");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.5.2.3");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.5.2.4");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.5.2.5");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.5.2.6");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.5.2.7");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.5.2.8");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.5.2.9");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.5.2.10");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.5.11.15.5.2.11");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.5.10.19", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.1");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.2");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.3");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.1.1.1");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.4");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.4.1");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.4.2.1");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.4.2.2");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5.1");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5.2.1");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5.2.2");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5.2.3");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5.2.4");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5.2.5");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5.2.6");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5.2.7");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5.2.8");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5.2.9");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5.2.10");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5.2.11");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5.2.12");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5.2.13");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5.2.14");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.5.2.3.5.2.15");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.6.1.1", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.1");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.2");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.2.1");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.2.2.1");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.2.2.1.1");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.2.2.2");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.2.2.3");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.2.2.4");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.3");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.3.1");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.3.2.1");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.3.2.2");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.3.2.3");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.3.2.4");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.3.2.5");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.3.2.6");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.3.2.7");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.3.2.8");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.3.2.9");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.3.2.10");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.3.2.11");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.3.2.12");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.6.1.1.3.2.13");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.6.1.2", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.1");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.2");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.2.1");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.2.2.1");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.2.2.1.1");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.2.2.2");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.2.2.3");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.2.2.4");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.3");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.3.1");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.3.2.1");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.3.2.2");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.3.2.3");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.3.2.4");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.3.2.5");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.3.2.6");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.3.2.7");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.3.2.8");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.3.2.9");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.3.2.10");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.3.2.11");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.3.2.12");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.6.1.1.3.2.13");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.6.1.3", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.2.4.1");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.1.1.1.1");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.1.1.1.2.1");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.1.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.1.1.1.2.2");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.1.1.1.3.1");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.1.1.1.3.2");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.1.1.1.3.3");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.1.1.1.3.4");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.1.1.1.3.5");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.2.4.2");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.2.4.2.1");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.2.4.2.2.1");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.2.4.3");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.2.4.3.1");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.2.4.3.2.1");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.2.4.3.2.2");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.2.4.3.2.3");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.2.4.3.2.4");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.2.4.3.2.5");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.2.4.3.2.6");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.2.4.3.2.7");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.2.4.3.2.8");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.2.4.3.2.9");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.5.2.4.3.2.10");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.6.1.4", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.6.1.5", "3.3.6.2.1");
        nrtmSelections.addNRTM("2.3.6.1.5", "3.3.6.2.2");
        nrtmSelections.addNRTM("2.3.6.1.5", "3.3.6.2.2.1");
        nrtmSelections.addNRTM("2.3.6.1.5", "3.3.6.2.3");
        nrtmSelections.addNRTM("2.3.6.1.5", "3.3.6.2.3.1");
        nrtmSelections.addNRTM("2.3.6.1.5", "3.3.6.2.3.2.1");
        nrtmSelections.addNRTM("2.3.6.1.5", "3.3.6.2.3.2.2");
        nrtmSelections.addNRTM("2.3.6.1.5", "3.3.6.2.3.2.3");
        nrtmSelections.addNRTM("2.3.6.1.5", "3.3.6.2.3.2.4");
        nrtmSelections.addNRTM("2.3.6.1.5", "3.3.6.2.3.2.5");
        nrtmSelections.addNRTM("2.3.6.1.5", "3.3.6.2.3.2.6");
        nrtmSelections.addNRTM("2.3.6.1.5", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.6.1.5", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.6.1.5", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.1");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.2");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.3");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.2.1.1");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.2.1.2");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.2.1.3");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.1");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.2");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.3");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.1.1");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.1.1.1");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.1.1.2.1");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.1.1.2.1.1");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.1.1.2.2");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.1.1.2.3");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.2.1.4");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.4");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.4");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.2.1.5");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.2.1.5.1");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.2.1.5.2.1");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.2.1.5.2.2");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.2.1.5.2.3");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.2.1.5.2.4");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.2.1.5.2.5");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.2.1.5.2.6");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.2.1.5.2.7");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.2.1.5.2.8");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.2.1.5.2.9");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.2.1.5.2.10");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.1");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.1");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.2");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.3");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.4");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.5");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.6");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.7");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.8");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.9");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.10");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.11");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.12");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.13");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.14");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.15");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.16");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.17");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.1.5.2.18");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.5");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.5.1");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.5.2.2");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.5.2.3");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.5.2.5");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.5.2.6");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.5.2.7");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.5.2.8");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.5.2.9");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.5.2.10");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.5.2.11");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.5.2.13");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.5.2.14");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.4.3.2.5.2.15");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.6.1.6", "3.3.1.4.1.2.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.2");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.3");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.4");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.4.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.5");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.1.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.1.2.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.1.2.2");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.1.2.3");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.2.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.2.2.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.3.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.3.2.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.1.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.1.2.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.1.2.1.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.1.2.1.2.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.1.2.1.2.2");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.1.2.2");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.1.2.3");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.1.2.4");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.1.2.5");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.1.2.6");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.1.2.7");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.1.2.8");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.1.2.9");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.1.2.10");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.2");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.3");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.4");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.5");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.5.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.5.2");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.6");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.7");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.7.6");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.7.7.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.7.7.2");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.7.7.3");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.7.7.4");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.8");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.9");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.9.1.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.9.1.2");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.9.1.3");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.9.1.4");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.9.1.5");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.9.1.6");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.10");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.10.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.10.2.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.10.2.2");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.10.2.3");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.11");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.6.4.12");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.7.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.7.2");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.7.2.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.7.3");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.7.3.4");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.7.5");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.7.5.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.7.5.2.2");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.7.5.2.3");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.7.6");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.7.6.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.3.7.6.2.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.1.4.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.1.4.1.1");
        nrtmSelections.addNRTM("2.3.6.1.7", "3.3.1.4.1.2.1");

    }

    static class TestResult
    {

        private String entityName;
        private String testType;
        private String result;
        private String errorMessage;

        public String getEntityName()
        {
            return entityName;
        }

        public void setEntityName(String entityName)
        {
            this.entityName = entityName;
        }

        public String getTestType()
        {
            return testType;
        }

        public void setTestType(String testType)
        {
            this.testType = testType;
        }

        public String getResult()
        {
            return result;
        }

        public void setResult(String result)
        {
            this.result = result;
        }

        public String getErrorMessage()
        {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage)
        {
            this.errorMessage = errorMessage;
        }

    }

    static class TestCounts
    {
        private int totalTests;
        private int totalPassed;
        private int totalFailed;

        public int getTotalTests()
        {
            return totalTests;
        }

        public void setTotalTests(int totalTests)
        {
            this.totalTests = totalTests;
        }

        public int getTotalPassed()
        {
            return totalPassed;
        }

        public void setTotalPassed(int totalPassed)
        {
            this.totalPassed = totalPassed;
        }

        public int getTotalFailed()
        {
            return totalFailed;
        }

        public void setTotalFailed(int totalFailed)
        {
            this.totalFailed = totalFailed;
        }

    }
}
