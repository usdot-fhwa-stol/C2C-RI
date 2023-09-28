/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.reports.dao;

import java.io.File;
import java.sql.DriverManager;
import java.util.ArrayList;
import org.fhwa.c2cri.reports.C2CRIAppLayerTestCaseData;

/**
 * Provides support for the C2CRI_AppLayerTestCaseData Table.
 * @author TransCore ITS
 */
public class C2CRIAppLayerTestCaseDataDAO extends ReportsDAO {

    // Keep a cache of C2CRIAppLayerTestCaseData objects.  To speed up processing they will all be written out in batches.
    private ArrayList<C2CRIAppLayerTestCaseData> storedTestCaseDataList = new ArrayList<>();
    // The maximum number of objects to hold before writing them out.
    private int maxHold = 100;
    
    public C2CRIAppLayerTestCaseDataDAO(File outdb) throws Exception {
        // Create a SQLite connection
        Class.forName("org.sqlite.JDBC");
        super.conn = DriverManager.getConnection("jdbc:sqlite:" + outdb.getAbsolutePath());
    }

    /**
     * Write a list of Test Case Data objects in a batch operation.
     * 
     * @param testCaseDataList the list of test case data objects.
     */
    public void insert(ArrayList<C2CRIAppLayerTestCaseData> testCaseDataList) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = super.conn.prepareStatement("INSERT INTO " + ReportsDAOInterface.APPLAYERTESTCASEDATA_TABLE + "("
                    + "id, Standard, TestCase, Iteration, VariableName, Description, "
                    + "DataType, VariableValue, ValidValues, TestCaseIndex) VALUES (?,?,?,?,?,?,?,?,?,?)");


            for (C2CRIAppLayerTestCaseData testCaseData : testCaseDataList) {
                int col = 1;
                pstmt.setInt(col++, testCaseData.getId());
                pstmt.setString(col++, testCaseData.getStandard());
                pstmt.setString(col++, testCaseData.getTestCase());
                pstmt.setString(col++, testCaseData.getIteration());
                pstmt.setString(col++, testCaseData.getVariableName());
                pstmt.setString(col++, testCaseData.getDescription());
                pstmt.setString(col++, testCaseData.getDataType());
                pstmt.setString(col++, testCaseData.getVariableValue());
                pstmt.setString(col++, testCaseData.getValidValues());
                pstmt.setInt(col++, testCaseData.getTestCaseIndex());
                pstmt.addBatch();
            }
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Store the provided test case data object.  If we've reached the maxHold value, write out the current records.
     * 
     * @param testCaseData the current test case data object.
     */
    public void insert(C2CRIAppLayerTestCaseData testCaseData) {
        storedTestCaseDataList.add(testCaseData);
        if (storedTestCaseDataList.size() == maxHold){
            insert(storedTestCaseDataList);
            storedTestCaseDataList.clear();
            System.out.println("Wrote out "+maxHold+" test Data Events.");
        }
        
    }
    
    /**
     * Write out all remaining data objects in the stored test case list.
     */
    public void flush() {
        if (storedTestCaseDataList.size() > 0){
            insert(storedTestCaseDataList);
            System.out.println("Wrote out the remaining "+storedTestCaseDataList.size()+" Test Case Data  Elements.");
            storedTestCaseDataList.clear();
        }
    }
    
}
