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
public abstract class ReportsDAOInterface {
	
	private ReportsDAOInterface()
	{
	}
	
    public static final String EVENTSET_TABLE = "eventSet";
    public static final String CONFIGURATIONFILE_TABLE = "ConfigurationFile";
    public static final String INITEVENT_TABLE = "InitEvent";
    public static final String LOGGEDMESSAGE_TABLE = "LoggedMessage";
    public static final String MESSAGES_TABLE = "Messages";
    public static final String RAWOTWMESSAGE_TABLE = "RawOTWMessage";
    public static final String SCRIPTEVENT_TABLE = "ScriptEvent";
    public static final String TESTRESULTNEEDS_TABLE = "TestResultNeeds";
    public static final String TESTRESULTREQUIREMENTS_TABLE = "TestResultRequirements";
    public static final String TESTRESULTSTESTCASE_TABLE = "TestResultsTestCase";
    public static final String USEREVENT_TABLE = "UserEvent";
    public static final String APPLAYERTESTCASEDESCRIPTIONS_TABLE = "C2CRI_AppLayerTestCaseDescriptions";
    public static final String APPLAYERTESTPROCEDURES_TABLE = "C2CRI_AppLayerTestProcedures";
    public static final String APPLAYERTESTPROCEDURESTEPS_TABLE = "C2CRI_AppLayerTestProcedureSteps";
    public static final String INFOLAYERTESTCASEDESCRIPTIONS_TABLE = "C2CRI_InfoLayerTestCaseDescriptions";
    public static final String INFOLAYERTESTPROCEDURES_TABLE = "C2CRI_InfoLayerTestProcedures";
    public static final String INFOLAYERTESTPROCEDURESTEPS_TABLE = "C2CRI_InfoLayerTestProcedureSteps";
    public static final String INFOLAYERTESTCASEDATA_TABLE = "C2CRI_InfoLayerTestCaseData";
    public static final String APPLAYERTESTCASEDATA_TABLE = "C2CRI_AppLayerTestCaseData";

}
