/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.reports.dao;

import java.io.File;
import java.sql.DriverManager;
import java.util.ArrayList;
import org.fhwa.c2cri.reports.LoggedMessage;

/**
 * Provides support for the LoggedMessage Table.
 *
 * @author TransCore ITS
 */
public class LoggedMessageDAO extends ReportsDAO {

    public LoggedMessageDAO(File outdb) throws Exception {
        // Create a SQLite connection
        Class.forName("org.sqlite.JDBC");
        super.conn = DriverManager.getConnection("jdbc:sqlite:" + outdb.getAbsolutePath());
    }

    /**
     * Write a list of Logged Message objects in a batch operation.
     * 
     * @param loggedMessageList the list of logged message objects.
     */
    public void insert(ArrayList<LoggedMessage> loggedMessageList) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = super.conn.prepareStatement("INSERT INTO " + ReportsDAOInterface.LOGGEDMESSAGE_TABLE + "("
                    + "ID, parentDialog, messageName, messageType, messageEncoding, viaTransportProtocol,"
                    + "messageSourceAddress, messageDestinationAddress, relatedCommand, MessageBody) VALUES (?,?,?,?,?,?,?,?,?,?)");


            for (LoggedMessage thisLoggedMessage : loggedMessageList) {
                int col = 1;
                pstmt.setString(col++, thisLoggedMessage.getId().toString());
                pstmt.setString(col++, thisLoggedMessage.getParentDialog());
                pstmt.setString(col++, thisLoggedMessage.getMessageName());
                pstmt.setString(col++, thisLoggedMessage.getMessageType());
                pstmt.setString(col++, thisLoggedMessage.getMessageEncoding());
                pstmt.setString(col++, thisLoggedMessage.getViaTransportProtocol());
                pstmt.setString(col++, thisLoggedMessage.getMessageSourceAddress());
                pstmt.setString(col++, thisLoggedMessage.getMessageDestinationAddress());
                pstmt.setString(col++, thisLoggedMessage.getRelatedCommand());
                pstmt.setString(col++, thisLoggedMessage.getMessageBody());
                pstmt.addBatch();
            }
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Write out the provided logged message data object.  
     * 
     * @param thisLoggedMessage the current logged message data object.
     */
    public void insert(LoggedMessage thisLoggedMessage) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = super.conn.prepareStatement("INSERT INTO " + ReportsDAOInterface.LOGGEDMESSAGE_TABLE + "("
                    + "ID, parentDialog, messageName, messageType, messageEncoding, viaTransportProtocol,"
                    + "messageSourceAddress, messageDestinationAddress, relatedCommand, MessageBody) VALUES (?,?,?,?,?,?,?,?,?,?)");


            int col = 1;

                pstmt.setString(col++, thisLoggedMessage.getId().toString());
                pstmt.setString(col++, thisLoggedMessage.getParentDialog());
                pstmt.setString(col++, thisLoggedMessage.getMessageName());
                pstmt.setString(col++, thisLoggedMessage.getMessageType());
                pstmt.setString(col++, thisLoggedMessage.getMessageEncoding());
                pstmt.setString(col++, thisLoggedMessage.getViaTransportProtocol());
                pstmt.setString(col++, thisLoggedMessage.getMessageSourceAddress());
                pstmt.setString(col++, thisLoggedMessage.getMessageDestinationAddress());
                pstmt.setString(col++, thisLoggedMessage.getRelatedCommand());
                pstmt.setString(col++, thisLoggedMessage.getMessageBody());
                pstmt.addBatch();
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
