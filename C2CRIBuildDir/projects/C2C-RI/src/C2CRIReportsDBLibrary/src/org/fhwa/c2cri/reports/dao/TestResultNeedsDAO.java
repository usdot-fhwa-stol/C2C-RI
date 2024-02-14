/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.reports.dao;

import java.io.File;
import java.sql.DriverManager;
import java.util.ArrayList;
import org.fhwa.c2cri.reports.TestResultNeeds;

/**
 * Provides support for the TestResultNeeds Table. 
 *
 * @author TransCore ITS
 */
public class TestResultNeedsDAO extends ReportsDAO {

    public TestResultNeedsDAO(File outdb) throws Exception {
        // Create a SQLite connection
        Class.forName("org.sqlite.JDBC");
        super.conn = DriverManager.getConnection("jdbc:sqlite:" + outdb.getAbsolutePath());
    }

    /**
     * Write a list of Test Result Needs objects in a batch operation.
     * 
     * @param testResultNeedsList the list of test result needs objects.
     */
    public void insert(ArrayList<TestResultNeeds> testResultNeedsList) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = super.conn.prepareStatement("INSERT INTO " + ReportsDAOInterface.TESTRESULTNEEDS_TABLE + "("
                    + "needID, standardType, extension, results, unID, unSelected,"
                    + "userNeed) VALUES (?,?,?,?,?,?,?)");


            for (TestResultNeeds thisTestResultNeed : testResultNeedsList) {
                int col = 1;
                pstmt.setString(col++, thisTestResultNeed.getTestResultNeedsPK().getNeedID().toString());
                pstmt.setString(col++, thisTestResultNeed.getTestResultNeedsPK().getStandardType());
                pstmt.setString(col++, thisTestResultNeed.getExtension());
                pstmt.setString(col++, thisTestResultNeed.getResults());
                pstmt.setString(col++, thisTestResultNeed.getUnID());
                pstmt.setString(col++, thisTestResultNeed.getUnSelected());
                pstmt.setString(col++, thisTestResultNeed.getUserNeed());
                pstmt.addBatch();
            }
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Store the provided test result needs data object.  If we've reached the maxHold value, write out the current records.
     * 
     * @param testResultNeed the current test result need data object.
     */
    public void insert(TestResultNeeds thisTestResultNeed) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = super.conn.prepareStatement("INSERT INTO " + ReportsDAOInterface.TESTRESULTNEEDS_TABLE + "("
                    + "needID, standardType, extension, results, unID, unSelected,"
                    + "userNeed) VALUES (?,?,?,?,?,?,?)");


            int col = 1;

                pstmt.setString(col++, thisTestResultNeed.getTestResultNeedsPK().getNeedID().toString());
                pstmt.setString(col++, thisTestResultNeed.getTestResultNeedsPK().getStandardType());
                pstmt.setString(col++, thisTestResultNeed.getExtension());
                pstmt.setString(col++, thisTestResultNeed.getResults());
                pstmt.setString(col++, thisTestResultNeed.getUnID());
                pstmt.setString(col++, thisTestResultNeed.getUnSelected());
                pstmt.setString(col++, thisTestResultNeed.getUserNeed());
                pstmt.addBatch();
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
