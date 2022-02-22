/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.reports.dao;

import java.io.File;
import java.sql.DriverManager;
import java.util.ArrayList;
import org.fhwa.c2cri.reports.EventSet;

/**
 * Provides support for the EventSet Table.
 *
 * @author TransCore ITS
 */
public class EventSetDAO extends ReportsDAO {

    public EventSetDAO(File outdb) throws Exception {
        // Create a SQLite connection
        Class.forName("org.sqlite.JDBC");
        super.conn = DriverManager.getConnection("jdbc:sqlite:" + outdb.getAbsolutePath());
    }

    /**
     * Write a list of event set objects in a batch operation.
     * 
     * @param eventSetList the list of event set objects.
     */
    public void insert(ArrayList<EventSet> eventSetList) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = super.conn.prepareStatement("INSERT INTO " + EVENTSET_TABLE + "("
                    + "EventID, level, logger, thread, timestamp, eventType, "
                    + "eventTypeID, debugInfo) VALUES (?,?,?,?,?,?,?,?)");


            for (EventSet thisEventSet : eventSetList) {
                int col = 1;
                pstmt.setString(col++, thisEventSet.getEventID().toString());
                pstmt.setString(col++, thisEventSet.getLevel());
                pstmt.setString(col++, thisEventSet.getLogger());
                pstmt.setString(col++, thisEventSet.getThread());
                pstmt.setString(col++, thisEventSet.getTimestamp());
                pstmt.setString(col++, thisEventSet.getEventType());
                pstmt.setString(col++, thisEventSet.getEventTypeID().toString());
                pstmt.setString(col++, thisEventSet.getDebugInfo());
                pstmt.addBatch();
            }
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Write out the provided event set data object.  
     * 
     * @param eventSet the current event set data object.
     */
    public void insert(EventSet eventSet) {
        try {
            pstmt = super.conn.prepareStatement("INSERT INTO " + EVENTSET_TABLE + "("
                    + "EventID, level, logger, thread, timestamp, eventType, "
                    + "eventTypeID, debugInfo) VALUES (?,?,?,?,?,?,?,?)");

            int col = 1;

            pstmt.setString(col++, eventSet.getEventID().toString());
            pstmt.setString(col++, eventSet.getLevel());
            pstmt.setString(col++, eventSet.getLogger());
            pstmt.setString(col++, eventSet.getThread());
            pstmt.setString(col++, eventSet.getTimestamp());
            pstmt.setString(col++, eventSet.getEventType());
            pstmt.setString(col++, eventSet.getEventTypeID().toString());
            pstmt.setString(col++, eventSet.getDebugInfo());

            execute(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
