/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.reports.dao;

import java.io.File;
import java.sql.DriverManager;
import java.util.ArrayList;
import org.fhwa.c2cri.reports.ScriptEvent;

/**
 * Provides support for the ScriptEvent Table.
 *
 * @author TransCore ITS
 */
public class ScriptEventDAO extends ReportsDAO {

    // Keep a cache of ScriptEvent objects.  To speed up processing they will all be written out in batches.
    private ArrayList<ScriptEvent> storedScriptEventList = new ArrayList<>();
    // The maximum number of objects to hold before writing them out.
    private int maxHold = 100;
    
    public ScriptEventDAO(File outdb) throws Exception {
        // Create a SQLite connection
        Class.forName("org.sqlite.JDBC");
        super.conn = DriverManager.getConnection("jdbc:sqlite:" + outdb.getAbsolutePath());
    }

     /**
     * Write a list of Script Event objects in a batch operation.
     * 
     * @param scriptEventList the list of script event objects.
     */
    public void insert(ArrayList<ScriptEvent> scriptEventList) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = super.conn.prepareStatement("INSERT INTO " + ReportsDAOInterface.SCRIPTEVENT_TABLE + "("
                    + "EventID, src, tag, type, line, column, [execution-time], [execution-time-millis], "
                    + "eventTypeID, file, functionId, [test-case-name], outcome, error) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");


            for (ScriptEvent thisScriptEvent : scriptEventList) {
                int col = 1;
                pstmt.setString(col++, thisScriptEvent.getEventID().toString());
                pstmt.setString(col++, thisScriptEvent.getSrc());
                pstmt.setString(col++, thisScriptEvent.getTag());
                pstmt.setString(col++, thisScriptEvent.getType());
                pstmt.setString(col++, thisScriptEvent.getLine());
                pstmt.setString(col++, thisScriptEvent.getColumn());
                pstmt.setString(col++, thisScriptEvent.getExecutionTime());
                pstmt.setString(col++, thisScriptEvent.getExecutionTimeMillis().toString());
                pstmt.setString(col++, thisScriptEvent.getEventTypeID().toString());
                pstmt.setString(col++, thisScriptEvent.getFile());
                pstmt.setString(col++, thisScriptEvent.getFunctionId());
                pstmt.setString(col++, thisScriptEvent.getTestCaseName());
                pstmt.setString(col++, thisScriptEvent.getOutcome());
                pstmt.setString(col++, thisScriptEvent.getError());
                pstmt.addBatch();
            }
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Store the script event data object.  If we've reached the maxHold value, write out the current records.
     * 
     * @param thisScriptEvent the current script event data object.
     */
    public void insert(ScriptEvent thisScriptEvent) {
        storedScriptEventList.add(thisScriptEvent);
        if (storedScriptEventList.size() == maxHold){
            insert(storedScriptEventList);
            storedScriptEventList.clear();
            System.out.println("Wrote out "+maxHold+" script Events.");
        }

    }
    
    /**
     * Write out all remaining data objects in the stored script event list.
     */
    public void flush() {
        if (storedScriptEventList.size() > 0){
            insert(storedScriptEventList);
            System.out.println("Wrote out the remaining "+storedScriptEventList.size()+" script Events.");
            storedScriptEventList.clear();
        }
    }
}
