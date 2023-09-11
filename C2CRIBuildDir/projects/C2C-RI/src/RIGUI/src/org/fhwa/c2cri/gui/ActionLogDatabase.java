/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 *
 * @author TransCore ITS, LLC
 */
    /**
     * 
     */
    public class ActionLogDatabase {

    final static Object instancelockObject = new Object();   
    static ActionLogDatabase instance;

     /**
         * The message database file path.
         */
        static String actionLogDBFilePath;
        static final Object lockObject = new Object();
        static int nextDatabaseActionIndex = 1;
        int currentActionIndex = 0;
        static int nextDatabaseStatusIndex = 1;
        int currentStatusIndex = 0;
        boolean databaseCreated = false;
    
        
    public static ActionLogDatabase getInstance(){
        synchronized (instancelockObject){
            if (instance == null){
                instance = new ActionLogDatabase();
            } 
            return instance;
        }
    }        
    
        private ActionLogDatabase() {
            createActionLogDatabase();
        }

        /**
         * Generate filename for the message database.
         *
         * Pre-Conditions: N/A Post-Conditions: N/A
         *
         * @return the string
         */
        private String generateFilename() {
            String fileName = "NotGenerated.db3";
            try {
                File dirPath = new File(".\\");
                fileName = File.createTempFile("ActionLog", ".db3", dirPath).getAbsolutePath();
            } catch (Exception ex) {
                logException(ex);
                ex.printStackTrace();
            }
            return fileName;
        }

        /**
         * Returns the number of records currently maintained in the action log;
         * 
         * @return 
         */
        public int getCurrentActionRowCount(){
            synchronized (lockObject) {
                if (!databaseCreated) createActionLogDatabase();
                return currentActionIndex;
            }            
        }

        /**
         * Returns the number of records currently maintained in the action log;
         * 
         * @return 
         */
        public int getCurrentStatusRowCount(){
            synchronized (lockObject) {
                if (!databaseCreated) createActionLogDatabase();
                return currentStatusIndex;
            }            
        }
        
        /**
         * Creates the message Database for storage C2C RI messages outside of
         * process memory.
         *
         * Pre-Conditions: N/A Post-Conditions: N/A
         *
         */
        private void createActionLogDatabase() {
            synchronized (lockObject) {
                boolean fileNotFound = true;
                while (fileNotFound) {
                    // First determine a name for the message database
                    String testPath = generateFilename();
                    actionLogDBFilePath = testPath;
                    try {
                        File f = new File(testPath);
                        if (f.exists()) {
                            Connection con = connect();
                            con.close();
                            createNewActionsTable();
                            createNewStatusTable();
                            fileNotFound = false;
                            databaseCreated = true;
                        }
                        nextDatabaseActionIndex = 1;
                        currentActionIndex = 0;
                        nextDatabaseStatusIndex = 1;
                        currentStatusIndex = 0;
                    } catch (Exception ex) {
                        logException(ex);
                        ex.printStackTrace();
                    }

                }
            }

        }

        /**
         * Create a new table in the test database for storing actions.
         *
         */
        private void createNewActionsTable() {
            // SQL statement for creating a new table
            String sql = "CREATE TABLE IF NOT EXISTS Actions (ActionID     INTEGER NOT NULL,\n"
                    + "                       TimeStamp TEXT NOT NULL,\n"
                    + "                       Message   TEXT NOT NULL,\n"
                    + "                       Result TEXT NOT NULL,\n"
                    + "                       TestId INTEGER NOT NULL,\n"
                    + "                       CONSTRAINT pk PRIMARY KEY (ActionID));";

            try ( Connection conn = connect();  Statement stmt = conn.createStatement()) {
                // create a new table
                stmt.execute(sql);
            } catch (SQLException e) {
                logException(e);
                System.out.println(e.getMessage());
            }

        }

        /**
         * Create a new table in the test database for storing status updates.
         *
         */
        private void createNewStatusTable() {
            // SQL statement for creating a new table
            String sql = "CREATE TABLE IF NOT EXISTS Status (StatusID     INTEGER NOT NULL,\n"
                    + "                       TimeStamp TEXT NOT NULL,\n"
                    + "                       Message   TEXT NOT NULL,\n"
                    + "                       TestId INTEGER NOT NULL,\n"
                    + "                       CONSTRAINT pk PRIMARY KEY (StatusID));";

            try ( Connection conn = connect();  Statement stmt = conn.createStatement()) {
                // create a new table
                stmt.execute(sql);
            } catch (SQLException e) {
                logException(e);
                System.out.println(e.getMessage());
            }

        }
        
        /**
         * Establishes a connection to the message database
         *
         * @return
         */
        private Connection connect() {
            File msgDB = new File(actionLogDBFilePath);

            // SQLite connection string
            String url = "jdbc:sqlite:" + msgDB.getAbsolutePath();
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(url);
            } catch (SQLException e) {
                logException(e);
                System.out.println(e.getMessage());
            }
            return conn;

        }

        /**
         * insert the action into the action database
         *
         * @param timestamp
         * @param message
         * @param result
         * @param testIndex
         */
        final public int insertAction(String timestamp, String message, String result, int testIndex) {
            synchronized (lockObject) {
                if (!databaseCreated) {
                    createActionLogDatabase();
                }

                System.out.println("Writting record " + nextDatabaseActionIndex);
                // update sql
                try ( Connection conn = connect();  PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Actions" + "("
                        + "ActionID, TimeStamp, Message, Result, TestId) VALUES (?,?,?,?,?)")) {
                    pstmt.setInt(1, nextDatabaseActionIndex);
                    pstmt.setString(2, timestamp);
                    pstmt.setString(3, message);
                    pstmt.setString(4, result);
                    pstmt.setInt(5, testIndex);
                    pstmt.execute();

                    currentActionIndex = nextDatabaseActionIndex;
                    nextDatabaseActionIndex++;
                    return currentActionIndex;
                } catch (SQLException ex) {
                    logException(ex);
                    System.out.println(ex.getMessage());
                }
            }
            return 0;
        }


        /**
         * Returns the designated column content of an action.
         *
         * Pre-Conditions: N/A Post-Conditions: N/A
         *
         * @return the requested action column;
         */
        public String getActionContent(int messageId, int column) {

            String selectSQL = "SELECT Timestamp, Message, Result, TestId FROM Actions WHERE ActionID=?";
            ResultSet rs = null;
            Connection conn = null;
            String response = "N/A";
            synchronized (lockObject) {
                try {
                    if (!databaseCreated) createActionLogDatabase();
                    conn = connect();
					if (conn != null)
					{
						try (PreparedStatement pstmt = conn.prepareStatement(selectSQL))
						{
							pstmt.setInt(1, messageId);
							rs = pstmt.executeQuery();

							while (rs.next()) {
								String timestamp = rs.getString("TimeStamp");
								String message = rs.getString("Message");
								String result = rs.getString("Result");
								int index = rs.getInt("TestId");
								switch (column){
									case 0: response = timestamp;
										break;
									case 1: response = message;
										break;
									case 2: response = result;
										break;
									case 3: response = String.valueOf(index);
										break;
									default: response = "N/A";
									break;
								}
							}
						}
					}
                } catch (SQLException e) {
                    logException(e);
                    System.out.println(e.getMessage());
                } catch (Exception ex) {
                    logException(ex);
                    ex.printStackTrace();

                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }

                        if (conn != null) {
                            conn.close();
                        }

                    } catch (Exception ex) {
                        logException(ex);
                        ex.printStackTrace();
                    }
                }
            }
            return response;
        }

        
       /**
         * insert the action into the status database table.
         *
         * @param timestamp
         * @param message
         * @param result
         */
        final public int insertStatus(String timestamp, String message, int testId) {
            synchronized (lockObject) {
                if (!databaseCreated) {
                    createActionLogDatabase();
                }

                System.out.println("Writing record " + nextDatabaseStatusIndex);
                // update sql
                try ( Connection conn = connect();  PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Status" + "("
                        + "StatusID, TimeStamp, Message, TestId) VALUES (?,?,?,?)")) {
                    pstmt.setInt(1, nextDatabaseStatusIndex);
                    pstmt.setString(2, timestamp);
                    pstmt.setString(3, message);
                    pstmt.setInt(4, testId);
                    pstmt.execute();

                    currentStatusIndex = nextDatabaseStatusIndex;
                    nextDatabaseStatusIndex++;
                    return currentStatusIndex;
                } catch (SQLException ex) {
                    logException(ex);
                    System.out.println(ex.getMessage());
                }
            }
            return 0;
        }


        /**
         * Returns the designated column content of an status entry.
         *
         * Pre-Conditions: N/A Post-Conditions: N/A
         *
         * @return the requested action column;
         */
        public String getStatusContent(int statusId, int column) {

            String selectSQL = "SELECT Timestamp, Message, TestId FROM Status WHERE StatusID=?";
            ResultSet rs = null;
            Connection conn = null;
            String response = "N/A";
            synchronized (lockObject) {
                try {
                    if (!databaseCreated) createActionLogDatabase();
                    conn = connect();
					if (conn != null)
					{
						try (PreparedStatement pstmt = conn.prepareStatement(selectSQL))
						{
							pstmt.setInt(1, statusId);
							rs = pstmt.executeQuery();

							while (rs.next()) {
								String timestamp = rs.getString("TimeStamp");
								String message = rs.getString("Message");
								int testId = rs.getInt("TestId");
								switch (column){
									case 0: response = timestamp;
										break;
									case 1: response = message;
										break;
									case 2: response = String.valueOf(testId);
										break;
									default: response = "N/A";
									break;
								}
							}
						}
					}
                } catch (SQLException e) {
                    logException(e);
                    System.out.println(e.getMessage());
                } catch (Exception ex) {
                    logException(ex);
                    ex.printStackTrace();

                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }

                        if (conn != null) {
                            conn.close();
                        }

                    } catch (Exception ex) {
                        logException(ex);
                        ex.printStackTrace();
                    }
                }
            }
            return response;
        }        

        /**
         *
         * @param messageNumber
         */
        public void closeDatabase() {
            synchronized (lockObject) {
                if (databaseCreated){
                    File tmpFile = new File(actionLogDBFilePath);
                    if (tmpFile.exists()) {
                        System.out.println("Finalize is deleting the file " + actionLogDBFilePath);
                        if (!tmpFile.delete()){
                            javax.swing.JOptionPane.showConfirmDialog(null, "Unable to delete "+tmpFile.getAbsolutePath());                            
                        };
                    }
                    tmpFile = new File(actionLogDBFilePath + "-journal");
                    if (tmpFile.exists()) {
                        System.out.println("Finalize is deleting the journal file " + actionLogDBFilePath);
                        if (!tmpFile.delete()){
                            javax.swing.JOptionPane.showConfirmDialog(null, "Unable to delete "+tmpFile.getAbsolutePath());                                                        
                        };
                    }
                    nextDatabaseActionIndex = 1;
                    currentActionIndex = 0;
                    nextDatabaseStatusIndex = 1;
                    currentStatusIndex = 0;   
                    databaseCreated = false;                    
                }
             
            }
        }
        
        private void logException(Exception ex){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);            
            javax.swing.JOptionPane.showConfirmDialog(null, ex.getMessage()+"\n"+pw.toString());
        }

    }
