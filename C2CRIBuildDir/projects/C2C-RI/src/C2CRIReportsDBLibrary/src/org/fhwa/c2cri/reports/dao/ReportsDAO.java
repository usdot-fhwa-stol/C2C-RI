/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.reports.dao;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Provides support for the Reports Table.
 *
 * @author TransCore ITS
 */
public abstract class ReportsDAO implements ReportsDAOInterface {

    protected Connection conn = null;
    protected PreparedStatement pstmt = null;

    /**
     * Execute the provided SQL statement
     * @param statement the sql statement in a string.
     */
    protected void execute(String statement) {
        try {
            pstmt = conn.prepareStatement(statement);
            pstmt.addBatch();
            pstmt.executeBatch();
        } catch (Exception ex) {
        } finally {
            try {
                pstmt.close();
            } catch (SQLException ex) {
            }
        }
    }

    /**
     * Execute the provided SQL Prepared Statement
     * @param prepStmt the prepared sql statement
     */
    protected void execute(PreparedStatement prepStmt) {
        try {

            pstmt = prepStmt;

            pstmt.addBatch();
            pstmt.executeBatch();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }

    /**
     * Execute the provided SQL statement which includes parameters
     * @param statement the sql statement
     * @param columns number of columns referenced in the sql statement
     * @param elements the values associated with the sql statement columns
     */
    protected void execute(String statement, int columns, String[] elements) {
        try {

            pstmt = conn.prepareStatement(statement);
            int col = 0;

            while (col < columns) {
                pstmt.setString(col++, elements[col]);
            }

            pstmt.addBatch();
            pstmt.executeBatch();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }

    /**
     * Execute the prepared statement in a batch 
     * @param prepStmt the prepared statement to be executed
     * @throws SQLException whenever any SQL related error is encountered
     */
    protected void executeBatch(PreparedStatement prepStmt) throws SQLException{
        try {

            pstmt = prepStmt;

            // Execute the batch
            int[] updateCounts = pstmt.executeBatch();

            // All statements were successfully executed.
            // updateCounts contains one element for each batched statement.
            // updateCounts[i] contains the number of rows affected by that statement.
            processUpdateCounts(updateCounts);

            // Since there were no errors, commit
            conn.commit();
        } catch (BatchUpdateException e) {
            // Not all of the statements were successfully executed
            int[] updateCounts = e.getUpdateCounts();

            // Some databases will continue to execute after one fails.
            // If so, updateCounts.length will equal the number of batched statements.
            // If not, updateCounts.length will equal the number of successfully executed statements
            processUpdateCounts(updateCounts);
            e.printStackTrace();
            // Either commit the successfully executed statements or rollback the entire batch
            conn.rollback();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }

    /**
     * Evaluate the provided updateCounts array to determine whether all provided updates statements executed successfully.  Prints a message if an error occurred.
     * 
     * @param updateCounts the array of updateCounts resulting from the executed batch job.
     */
    public static void processUpdateCounts(int[] updateCounts) {
        boolean processFailed = false;
        for (int i = 0; i < updateCounts.length; i++) {
            if (updateCounts[i] >= 0) {
                // Successfully executed; the number represents number of affected rows
            } else if (updateCounts[i] == Statement.SUCCESS_NO_INFO) {
                // Successfully executed; number of affected rows not available
            } else if (updateCounts[i] == Statement.EXECUTE_FAILED) {
                // Failed to execute
                processFailed = true;
            }
        }
            if (processFailed) System.out.println("ReportsDAO:  BATCH PROCESS FAILED ");
    }
}
