/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.reports.dao;

import java.io.File;
import java.sql.DriverManager;
import java.util.ArrayList;
import org.fhwa.c2cri.reports.RawOTWMessage;

/**
 * Provides support for the RawOTWMessage Table.
 *
 * @author TransCore ITS
 */
public class RawOTWMessageDAO extends ReportsDAO {
    // Keep a cache of RawOTWMessage objects.  To speed up processing they will all be written out in batches.
    private ArrayList<RawOTWMessage> rawOTWMessageEventList = new ArrayList<>();
    // The maximum number of objects to hold before writing them out.
    private int maxHold = 100;

    public RawOTWMessageDAO(File outdb) throws Exception {
        // Create a SQLite connection
        Class.forName("org.sqlite.JDBC");
        super.conn = DriverManager.getConnection("jdbc:sqlite:" + outdb.getAbsolutePath());
    }

    /**
     * Write a list of Raw Over the Wire Message objects in a batch operation.
     * 
     * @param RawOTWMessageList the list of Raw Over the Wire Message objects.
     */
    public void insert(ArrayList<RawOTWMessage> RawOTWMessageList) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = super.conn.prepareStatement("INSERT INTO " + ReportsDAOInterface.RAWOTWMESSAGE_TABLE + "("
                    + "ID, testCase, connectionName, ProcessType, SourceAddress, DestinationAddress,"
                    + "sequenceCount,message,TimeStampInMillis) VALUES (?,?,?,?,?,?,?,?,?)");


            for (RawOTWMessage thisRawOTWMessage : RawOTWMessageList) {
                int col = 1;
                pstmt.setString(col++, thisRawOTWMessage.getId().toString());
                pstmt.setString(col++, thisRawOTWMessage.getTestCase());
                pstmt.setString(col++, thisRawOTWMessage.getConnectionName());
                pstmt.setString(col++, thisRawOTWMessage.getProcessType());
                pstmt.setString(col++, thisRawOTWMessage.getSourceAddress());
                pstmt.setString(col++, thisRawOTWMessage.getDestinationAddress());
                pstmt.setString(col++, thisRawOTWMessage.getSequenceCount().toString());
                pstmt.setString(col++, thisRawOTWMessage.getMessage());
                String tmpString = thisRawOTWMessage.getTimeStampInMillis().toString();
                pstmt.setString(col++, tmpString);
                pstmt.addBatch();
            }
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Store the provided raw over the wire message data object.  If we've reached the maxHold value, write out the current records.
     * 
     * @param thisRawOTWMessage the current raw over the wire message data object.
     */
    public void insert(RawOTWMessage thisRawOTWMessage) {
        rawOTWMessageEventList.add(thisRawOTWMessage);
        if (rawOTWMessageEventList.size() == maxHold){
            insert(rawOTWMessageEventList);
            rawOTWMessageEventList.clear();
            System.out.println("Wrote out "+maxHold+" RawOTWMessage Events.");
        }

    }

    /**
     * Write out all remaining data objects in the stored raw over the wire message list.
     */
    public void flush() {
        if (rawOTWMessageEventList.size() > 0){
            insert(rawOTWMessageEventList);
            System.out.println("Wrote out the remaining "+rawOTWMessageEventList.size()+" RawOTWMessage Events.");
            rawOTWMessageEventList.clear();
        }
    }

}
