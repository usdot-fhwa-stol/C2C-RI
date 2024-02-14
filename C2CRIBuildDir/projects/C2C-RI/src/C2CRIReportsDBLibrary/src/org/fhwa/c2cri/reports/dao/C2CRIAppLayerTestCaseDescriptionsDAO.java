/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.reports.dao;

import java.io.File;
import java.sql.DriverManager;
import java.util.ArrayList;
import org.fhwa.c2cri.reports.C2CRIAppLayerTestCaseDescription;

/**
 * Provides support for the C2CRI_AppLayerTestCaseDescriptions Table.
 *
 * @author TransCore ITS
 */
public class C2CRIAppLayerTestCaseDescriptionsDAO extends ReportsDAO {

    // Keep a cache of C2CRIAppLayerTestCaseDescription objects.  To speed up processing they will all be written out in batches.
    private ArrayList<C2CRIAppLayerTestCaseDescription> storedTestCaseDescriptionsList = new ArrayList<>();
    // The maximum number of objects to hold before writing them out.
    private int maxHold = 100;
    
    public C2CRIAppLayerTestCaseDescriptionsDAO(File outdb) throws Exception {
        // Create a SQLite connection
        Class.forName("org.sqlite.JDBC");
        super.conn = DriverManager.getConnection("jdbc:sqlite:" + outdb.getAbsolutePath());
    }

    /**
     * Write a list of Test Case Description objects in a batch operation.
     * 
     * @param testCaseList the list of test case description objects.
     */
    public void insert(ArrayList<C2CRIAppLayerTestCaseDescription> testCaseList) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = super.conn.prepareStatement("INSERT INTO " + ReportsDAOInterface.APPLAYERTESTCASEDESCRIPTIONS_TABLE + "("
                    + "id, TestCaseName, TestCaseDescription, ItemList, SpecialProcedureRequirements, DependencyDescription, "
                    + "HardwareEnvironmental, SoftwareEnvironmental, OtherEnvironmental, Inputs, InputProcedures, Outputs, OutputProcedures) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");


            for (C2CRIAppLayerTestCaseDescription testCase : testCaseList) {
                int col = 1;
                pstmt.setInt(col++, testCase.getId());
                pstmt.setString(col++, testCase.getTestCaseName());
                pstmt.setString(col++, testCase.getTestCaseDescription());
                pstmt.setString(col++, testCase.getItemList());
                pstmt.setString(col++, testCase.getSpecialProcedureRequirements());
                pstmt.setString(col++, testCase.getDependencyDescription());
                pstmt.setString(col++, testCase.getHardwareEnvironmental());
                pstmt.setString(col++, testCase.getSoftwareEnvironmental());
                pstmt.setString(col++, testCase.getOtherEnvironmental());
                pstmt.setString(col++, testCase.getInputs());
                pstmt.setString(col++, testCase.getInputProcedures());
                pstmt.setString(col++, testCase.getOutputs());
                pstmt.setString(col++, testCase.getOutputProcedures());
                pstmt.addBatch();
            }
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    
    /**
     * Store the provided test case descriptions data object.  If we've reached the maxHold value, write out the current records.
     * 
     * @param testCaseSet the current test case description data object.
     */
    public void insert(C2CRIAppLayerTestCaseDescription testCaseSet) {
        storedTestCaseDescriptionsList.add(testCaseSet);
        if (storedTestCaseDescriptionsList.size() == maxHold){
            insert(storedTestCaseDescriptionsList);
            storedTestCaseDescriptionsList.clear();
            System.out.println("Wrote out "+maxHold+" test Step Events.");
        }
        
    }
    
    /**
     * Write out all remaining data objects in the stored test case description list.
     */
    public void flush() {
        if (storedTestCaseDescriptionsList.size() > 0){
            insert(storedTestCaseDescriptionsList);
            System.out.println("Wrote out the remaining "+storedTestCaseDescriptionsList.size()+" Test Steps.");
            storedTestCaseDescriptionsList.clear();
        }
    }
    
}
