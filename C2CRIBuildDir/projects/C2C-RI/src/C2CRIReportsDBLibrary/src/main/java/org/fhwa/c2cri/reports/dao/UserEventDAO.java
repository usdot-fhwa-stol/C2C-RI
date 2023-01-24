/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.reports.dao;

import java.io.File;
import java.sql.DriverManager;
import java.util.ArrayList;
import org.fhwa.c2cri.reports.UserEvent;

/**
 * Provides support for the UserEvent Table.
 *
 * @author TransCore ITS
 */
public class UserEventDAO extends ReportsDAO {

    public UserEventDAO(File outdb) throws Exception {
        // Create a SQLite connection
        Class.forName("org.sqlite.JDBC");
        super.conn = DriverManager.getConnection("jdbc:sqlite:" + outdb.getAbsolutePath());
    }

    /**
     * Write a list of User Event Data objects in a batch operation.
     * 
     * @param userEventList the list of user event data objects.
     */
    public void insert(ArrayList<UserEvent> userEventList) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = super.conn.prepareStatement("INSERT INTO " + USEREVENT_TABLE + "("
                    + "ID, Description) VALUES (?,?)");


            for (UserEvent thisUserEvent : userEventList) {
                int col = 1;
                pstmt.setString(col++, thisUserEvent.getId().toString());
                pstmt.setString(col++, thisUserEvent.getDescription());
                pstmt.addBatch();
            }
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Write out the provided user event data object.  
     * 
     * @param thisUserEvent the user event data object.
     */
    public void insert(UserEvent thisUserEvent) {
        try {

            // Disable auto-commit
            conn.setAutoCommit(false);

            pstmt = super.conn.prepareStatement("INSERT INTO " + USEREVENT_TABLE + "("
                    + "ID, Description) VALUES (?,?)");

            int col = 1;

                pstmt.setString(col++, thisUserEvent.getId().toString());
                pstmt.setString(col++, thisUserEvent.getDescription());
                pstmt.addBatch();
            executeBatch(pstmt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
