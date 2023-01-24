/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.reports.dao;

/**
 * Defines the interface for all ReportsDAO objects.  Includes definition of each of the tables.
 *
 * @author TransCore ITS
 */
public interface ReportsDAOInterface {
    public static String EVENTSET_TABLE = "eventSet";
    public static String CONFIGURATIONFILE_TABLE = "ConfigurationFile";
    public static String INITEVENT_TABLE = "InitEvent";
    public static String LOGGEDMESSAGE_TABLE = "LoggedMessage";
    public static String MESSAGES_TABLE = "Messages";
    public static String RAWOTWMESSAGE_TABLE = "RawOTWMessage";
    public static String SCRIPTEVENT_TABLE = "ScriptEvent";
    public static String TESTRESULTNEEDS_TABLE = "TestResultNeeds";
    public static String TESTRESULTREQUIREMENTS_TABLE = "TestResultRequirements";
    public static String TESTRESULTSTESTCASE_TABLE = "TestResultsTestCase";
    public static String USEREVENT_TABLE = "UserEvent";
    public static String APPLAYERTESTCASEDESCRIPTIONS_TABLE = "C2CRI_AppLayerTestCaseDescriptions";
    public static String APPLAYERTESTPROCEDURES_TABLE = "C2CRI_AppLayerTestProcedures";
    public static String APPLAYERTESTPROCEDURESTEPS_TABLE = "C2CRI_AppLayerTestProcedureSteps";
    public static String INFOLAYERTESTCASEDESCRIPTIONS_TABLE = "C2CRI_InfoLayerTestCaseDescriptions";
    public static String INFOLAYERTESTPROCEDURES_TABLE = "C2CRI_InfoLayerTestProcedures";
    public static String INFOLAYERTESTPROCEDURESTEPS_TABLE = "C2CRI_InfoLayerTestProcedureSteps";
    public static String INFOLAYERTESTCASEDATA_TABLE = "C2CRI_InfoLayerTestCaseData";
    public static String APPLAYERTESTCASEDATA_TABLE = "C2CRI_AppLayerTestCaseData";

}
