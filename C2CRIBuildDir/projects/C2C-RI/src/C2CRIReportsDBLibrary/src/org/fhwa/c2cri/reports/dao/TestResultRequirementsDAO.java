/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.reports.dao;

import java.io.File;
import java.sql.DriverManager;
import java.util.ArrayList;
import org.fhwa.c2cri.reports.TestResultRequirements;

/**
 * Provides support for the TestResultRequirements Table.
 *
 * @author TransCore ITS
 */
public class TestResultRequirementsDAO extends ReportsDAO {

    public TestResultRequirementsDAO(File outdb) throws Exception {
        // Create a SQLite connection
        Class.forName("org.sqlite.JDBC");
        super.conn = DriverManager.getConnection("jdbc:sqlite:" + outdb.getAbsolutePath());
    }

    /**
     * Write a list of Test Result Requirements objects in a batch operation.
     * 
     * @param testResultRequirementsList the list of test result requirements objects.
     */
    public void insert(ArrayList<TestResultRequirements> testResultRequirementsList) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = super.conn.prepareStatement("INSERT INTO " + TESTRESULTREQUIREMENTS_TABLE + "("
                    + "requirementID, needID, conformance, extension, otherReq, reqID, reqText,"
                    + "results, supported) VALUES (?,?,?,?,?,?,?,?,?)");


            for (TestResultRequirements thisTestResultRequirements : testResultRequirementsList) {
                int col = 1;
                pstmt.setString(col++, thisTestResultRequirements.getTestResultRequirementsPK().getRequirementID().toString());
                pstmt.setString(col++, thisTestResultRequirements.getTestResultRequirementsPK().getNeedID().toString());
                pstmt.setString(col++, thisTestResultRequirements.getConformance());
                pstmt.setString(col++, thisTestResultRequirements.getExtension());
                pstmt.setString(col++, thisTestResultRequirements.getOtherReq());
                pstmt.setString(col++, thisTestResultRequirements.getReqID());
                pstmt.setString(col++, thisTestResultRequirements.getReqText());
                pstmt.setString(col++, thisTestResultRequirements.getResults());
                pstmt.setString(col++, thisTestResultRequirements.getSupported());
                pstmt.addBatch();
            }
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Write out the provided test results requirements data object.  
     * 
     * @param thisTestResultRequirements the current result requirements data object.
     */
    public void insert(TestResultRequirements thisTestResultRequirements) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = super.conn.prepareStatement("INSERT INTO " + TESTRESULTREQUIREMENTS_TABLE + "("
                    + "requirementID, needID, conformance, extension, otherReq, reqID, reqText,"
                    + "results, supported) VALUES (?,?,?,?,?,?,?,?,?)");


            int col = 1;

                pstmt.setString(col++, thisTestResultRequirements.getTestResultRequirementsPK().getRequirementID().toString());
                pstmt.setString(col++, thisTestResultRequirements.getTestResultRequirementsPK().getNeedID().toString());
                pstmt.setString(col++, thisTestResultRequirements.getConformance());
                pstmt.setString(col++, thisTestResultRequirements.getExtension());
                pstmt.setString(col++, thisTestResultRequirements.getOtherReq());
                pstmt.setString(col++, thisTestResultRequirements.getReqID());
                pstmt.setString(col++, thisTestResultRequirements.getReqText());
                pstmt.setString(col++, thisTestResultRequirements.getResults());
                pstmt.setString(col++, thisTestResultRequirements.getSupported());
                pstmt.addBatch();
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
