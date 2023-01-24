/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.reports.dao;

import java.io.File;
import java.sql.DriverManager;
import org.fhwa.c2cri.reports.InitEvent;

/**
 * Provides support for the InitEvent Table.
 *
 * @author TransCore ITS
 */
public class InitEventDAO extends ReportsDAO {

    public InitEventDAO(File outdb) throws Exception {
        // Create a SQLite connection
        Class.forName("org.sqlite.JDBC");
        super.conn = DriverManager.getConnection("jdbc:sqlite:" + outdb.getAbsolutePath());
    }

    /**
     * Write the test initialization event to the database.
     * 
     * @param initEvent the test initialization event.
     */   
    public void insert(InitEvent initEvent) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement("INSERT INTO " + INITEVENT_TABLE + "("
                    + "ID, EventID, fileName, startTime, creator, description, c2criVersion, configFileName, "
                    + "checksum, selectedAppLayerTestSuite, selectedInfoLayerTestSuite, configVersion, configurationAuthor, externalCenterOperation, emulationEnabled, emulationInitialization) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            int col = 1;

                pstmt.setString(col++, initEvent.getInitEventPK().getId().toString());
                pstmt.setString(col++, initEvent.getInitEventPK().getEventID().toString());
                pstmt.setString(col++, initEvent.getFileName());
                pstmt.setString(col++, initEvent.getStartTime());
                pstmt.setString(col++, initEvent.getCreator());
                pstmt.setString(col++, initEvent.getDescription());
                pstmt.setString(col++, initEvent.getC2criVersion());
                pstmt.setString(col++, initEvent.getConfigFileName());
                pstmt.setString(col++, initEvent.getChecksum());
                pstmt.setString(col++, initEvent.getSelectedAppLayerTestSuite());
                pstmt.setString(col++, initEvent.getSelectedInfoLayerTestSuite());
                pstmt.setString(col++, initEvent.getConfigVersion());
                pstmt.setString(col++, initEvent.getConfigurationAuthor());
                pstmt.setString(col++, initEvent.getExternalCenterOperation());
                pstmt.setString(col++, initEvent.getEnableEmulation());
                pstmt.setString(col++, initEvent.getReInitializeEmulation());
                pstmt.addBatch();
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
