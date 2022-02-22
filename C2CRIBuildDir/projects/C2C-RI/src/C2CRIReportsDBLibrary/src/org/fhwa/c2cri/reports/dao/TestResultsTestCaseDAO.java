/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.reports.dao;

import java.io.File;
import java.sql.DriverManager;
import java.util.ArrayList;
import org.fhwa.c2cri.reports.TestResultsTestCase;

/**
 * Provides support for the TestREsultsTestCase Table.
 *
 * @author TransCore ITS
 */
public class TestResultsTestCaseDAO extends ReportsDAO {

    public TestResultsTestCaseDAO(File outdb) throws Exception {
        // Create a SQLite connection
        Class.forName("org.sqlite.JDBC");
        super.conn = DriverManager.getConnection("jdbc:sqlite:" + outdb.getAbsolutePath());
    }

    /**
     * Write a list of Test Results Test Case objects in a batch operation.
     * 
     * @param testResultTestCaseList the list of test results test case data objects.
     */
    public void insert(ArrayList<TestResultsTestCase> testResultTestCaseList) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = super.conn.prepareStatement("INSERT INTO " + TESTRESULTSTESTCASE_TABLE + "("
                    + "TCID, RequirementID, NeedID, errorDescription, result, TestCaseID,"
                    + "timeStamp) VALUES (?,?,?,?,?,?,?)");


            for (TestResultsTestCase thisTestResultsTestCase : testResultTestCaseList) {
                int col = 1;
                pstmt.setString(col++, thisTestResultsTestCase.getTcid().toString());
                pstmt.setString(col++, thisTestResultsTestCase.getRequirementID().toString());
                pstmt.setString(col++, thisTestResultsTestCase.getNeedID().toString());
                pstmt.setString(col++, thisTestResultsTestCase.getErrorDescription());
                pstmt.setString(col++, thisTestResultsTestCase.getResult());
                pstmt.setString(col++, thisTestResultsTestCase.getTestCaseID());
                pstmt.setString(col++, thisTestResultsTestCase.getTimeStamp());
                pstmt.addBatch();
            }
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Write out the provided test results test case data object.  
     * 
     * @param thisTestResultsTestCase the current test results test case data object.
     */
    public void insert(TestResultsTestCase thisTestResultsTestCase) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = super.conn.prepareStatement("INSERT INTO " + TESTRESULTSTESTCASE_TABLE + "("
                    + "TCID, RequirementID, NeedID, errorDescription, result, TestCaseID,"
                    + "timeStamp) VALUES (?,?,?,?,?,?,?)");


            int col = 1;

                pstmt.setString(col++, thisTestResultsTestCase.getTcid().toString());
                pstmt.setString(col++, thisTestResultsTestCase.getRequirementID().toString());
                pstmt.setString(col++, thisTestResultsTestCase.getNeedID().toString());
                pstmt.setString(col++, thisTestResultsTestCase.getErrorDescription());
                pstmt.setString(col++, thisTestResultsTestCase.getResult());
                pstmt.setString(col++, thisTestResultsTestCase.getTestCaseID());
                pstmt.setString(col++, thisTestResultsTestCase.getTimeStamp());
                pstmt.addBatch();
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
