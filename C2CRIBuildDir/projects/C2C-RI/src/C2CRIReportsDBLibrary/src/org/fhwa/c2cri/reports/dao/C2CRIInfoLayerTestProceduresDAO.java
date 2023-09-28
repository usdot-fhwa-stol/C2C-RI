/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.reports.dao;

import java.io.File;
import java.sql.DriverManager;
import java.util.ArrayList;
import org.fhwa.c2cri.reports.C2CRIInfoLayerTestProcedure;

/**
 * Provides support for the C2CRI_InfoLayerTestProcedures Table. 
 *
 * @author TransCore ITS
 */
public class C2CRIInfoLayerTestProceduresDAO extends ReportsDAO {

    // Keep a cache of C2CRInfoLayerTestProcedure objects.  To speed up processing they will all be written out in batches.
    private ArrayList<C2CRIInfoLayerTestProcedure> storedTestProcedureList = new ArrayList<>();
    // The maximum number of objects to hold before writing them out.
    private int maxHold = 100;
    
    public C2CRIInfoLayerTestProceduresDAO(File outdb) throws Exception {
        // Create a SQLite connection
        Class.forName("org.sqlite.JDBC");
        super.conn = DriverManager.getConnection("jdbc:sqlite:" + outdb.getAbsolutePath());
    }

    /**
     * Write a list of Test Procedure objects in a batch operation.
     * 
     * @param testProcedureList the list of test procedure objects.
     */
    public void insert(ArrayList<C2CRIInfoLayerTestProcedure> testProcedureList) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = super.conn.prepareStatement("INSERT INTO " + ReportsDAOInterface.INFOLAYERTESTPROCEDURES_TABLE + "("
                    + "id, Procedure, ProcedureId, Description, Variables, Requirements, "
                    + "PassFailCriteria) VALUES (?,?,?,?,?,?,?)");


            for (C2CRIInfoLayerTestProcedure testProcedure : testProcedureList) {
                int col = 1;
                pstmt.setInt(col++, testProcedure.getId());
                pstmt.setString(col++, testProcedure.getProcedure());
                pstmt.setString(col++, testProcedure.getProcedureId());
                pstmt.setString(col++, testProcedure.getDescription());
                pstmt.setString(col++, testProcedure.getVariables());
                pstmt.setString(col++, testProcedure.getRequirements());
                pstmt.setString(col++, testProcedure.getPassFailCriteria());
                pstmt.addBatch();
            }
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Store the provided test procedures data object.  If we've reached the maxHold value, write out the current records.
     * 
     * @param testProcedure the current test procedure data object.
     */
    public void insert(C2CRIInfoLayerTestProcedure testProcedure) {
        storedTestProcedureList.add(testProcedure);
        if (storedTestProcedureList.size() == maxHold){
            insert(storedTestProcedureList);
            storedTestProcedureList.clear();
            System.out.println("Wrote out "+maxHold+" test Step Events.");
        }
        
    }
    
    /**
     * Write out all remaining data objects in the stored test procedure list.
     */
    public void flush() {
        if (storedTestProcedureList.size() > 0){
            insert(storedTestProcedureList);
            System.out.println("Wrote out the remaining "+storedTestProcedureList.size()+" Test Procedures.");
            storedTestProcedureList.clear();
        }
    }
    
}
