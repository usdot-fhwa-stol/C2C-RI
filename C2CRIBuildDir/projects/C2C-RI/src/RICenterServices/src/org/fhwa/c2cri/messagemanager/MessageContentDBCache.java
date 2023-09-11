/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.messagemanager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author TransCore ITS
 */
public class MessageContentDBCache implements MessageContent {

    final static MessageContentDatabase messageDatabase = new MessageContentDatabase();

    final int messageId;

    /**
     * Instantiates a new temporary message file.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param inMessage the in message
     */
    public MessageContentDBCache(String namespace, InputStream inMessage) {

        messageId = messageDatabase.insertMessage(namespace, inMessage);
    }

    /**
     * Instantiates a new temporary message file.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param inMessage the in message
     */
    public MessageContentDBCache(String namespace, byte[] inMessage) {
        messageId = messageDatabase.insertMessageFromBytes(namespace, inMessage);
    }

    /**
     * The main method.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        File f = new File("C:\\C2CRIDev\\C2CRIBuildDir\\projects\\C2C-RI\\src\\RIGUI\\C2CRIDebugTest.dbg");
        FileInputStream fis = new FileInputStream(f);

        // Get current size of heap in bytes
        long heapSize = Runtime.getRuntime().totalMemory();

        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
        long heapMaxSize = Runtime.getRuntime().maxMemory();

        // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
        long heapFreeSize = Runtime.getRuntime().freeMemory();

        System.out.println("Before --> Current Heap: " + heapSize + "  MaxHeap: " + heapMaxSize + "  FreeHeap: " + heapFreeSize);

        heapSize = Runtime.getRuntime().totalMemory();
        heapMaxSize = Runtime.getRuntime().maxMemory();
        heapFreeSize = Runtime.getRuntime().freeMemory();
        MessageContentDBCache tmdb = new MessageContentDBCache("org.fhwa.fake", fis);
        System.out.println("During --> Current Heap: " + heapSize + "  MaxHeap: " + heapMaxSize + "  FreeHeap: " + heapFreeSize);

        fis.close();
        fis = new FileInputStream(f);
        heapSize = Runtime.getRuntime().totalMemory();
        heapMaxSize = Runtime.getRuntime().maxMemory();
        heapFreeSize = Runtime.getRuntime().freeMemory();
        MessageContentDBCache tmdb2 = new MessageContentDBCache("org.fhwa.fake", fis);
        System.out.println("During Second --> Current Heap: " + heapSize + "  MaxHeap: " + heapMaxSize + "  FreeHeap: " + heapFreeSize);

        fis.close();
        fis = new FileInputStream(f);
        heapSize = Runtime.getRuntime().totalMemory();
        heapMaxSize = Runtime.getRuntime().maxMemory();
        heapFreeSize = Runtime.getRuntime().freeMemory();
        MessageContentDBCache tmdb3 = new MessageContentDBCache("org.fhwa.fake", fis);
        System.out.println("During Third --> Current Heap: " + heapSize + "  MaxHeap: " + heapMaxSize + "  FreeHeap: " + heapFreeSize);

        heapSize = Runtime.getRuntime().totalMemory();
        heapMaxSize = Runtime.getRuntime().maxMemory();
        heapFreeSize = Runtime.getRuntime().freeMemory();
        tmdb.readMessage(1, "C:\\C2CRIDev\\C2CRIBuildDir\\projects\\C2C-RI\\src\\RIGUI\\C2CRIDebugTest-copied.dbg");
        System.out.println("After --> Current Heap: " + heapSize + "  MaxHeap: " + heapMaxSize + "  FreeHeap: " + heapFreeSize);

        heapSize = Runtime.getRuntime().totalMemory();
        heapMaxSize = Runtime.getRuntime().maxMemory();
        heapFreeSize = Runtime.getRuntime().freeMemory();
        tmdb.readMessage(2, "C:\\C2CRIDev\\C2CRIBuildDir\\projects\\C2C-RI\\src\\RIGUI\\C2CRIDebugTest-copied2.dbg");
        System.out.println("After Second --> Current Heap: " + heapSize + "  MaxHeap: " + heapMaxSize + "  FreeHeap: " + heapFreeSize);

        heapSize = Runtime.getRuntime().totalMemory();
        heapMaxSize = Runtime.getRuntime().maxMemory();
        heapFreeSize = Runtime.getRuntime().freeMemory();
        tmdb.readMessage(3, "C:\\C2CRIDev\\C2CRIBuildDir\\projects\\C2C-RI\\src\\RIGUI\\C2CRIDebugTest-copied3.dbg");
        System.out.println("After Third --> Current Heap: " + heapSize + "  MaxHeap: " + heapMaxSize + "  FreeHeap: " + heapFreeSize);

        tmdb = null;
        tmdb2 = null;
        tmdb3 = null;
        System.gc();

//        try {
//            messageDatabase.finalize();
//        } catch (Throwable ex) {
//            ex.printStackTrace();
//        }
//        tmpTest();
//        System.gc();
//        tmpTest();
//        System.gc();
        System.out.println("Exiting Now.");
    }

    /**
     * Converts the content of this message to byte array.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the byte[]
     */
    @Override
    public byte[] toByteArray() {

        return messageDatabase.toByteArray(messageId);
    }

    /**
     * Provides the content of this message via an InputStream.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the byte[]
     */
    @Override
    public InputStream toInputStream() {

        return messageDatabase.toInputStream(messageId);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String result = new String(toByteArray());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    public void finalize() throws Throwable {
        super.finalize();
        System.out.println("Finalize was called");

        messageDatabase.closeMessage(messageId);
//        } 
    }

    public void readMessage(int messageId, String filename) {
        try {
            messageDatabase.readMessage(messageId, filename);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    ;
    
    static class MessageContentDatabase {

        /**
         * The message database file path.
         */
        static String messageFilePath;
        static Object lockObject = new Object();
        static int nextDatabaseMessageIndex = 1;
        int currentMessageIndex = 0;
        int closedMessagesIndex = 0;
        boolean databaseCreated = false;

        private MessageContentDatabase() {
            createMessageDatabase();
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
                fileName = File.createTempFile("Message", ".db3", dirPath).getAbsolutePath();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return fileName;
        }

        /**
         * Creates the message Database for storage C2C RI messages outside of
         * process memory.
         *
         * Pre-Conditions: N/A Post-Conditions: N/A
         *
         */
        private void createMessageDatabase() {
            Connection con = null;
            synchronized (lockObject) {
                boolean fileNotFound = true;
                while (fileNotFound) {
                    // First determine a name for the message database
                    String testPath = generateFilename();
                    messageFilePath = testPath;
                    try {
                        File f = new File(testPath);
                        if (f.exists()) {
                            con = connect();
                            con.close();
                            con = null;
                            createNewMessageTable();
                            fileNotFound = false;
                            databaseCreated = true;
                        }

                    } catch (Exception ex) {

                        ex.printStackTrace();
                    } finally {
                        try{
                            if (con!= null)con.close();
                        } catch (Exception ex){                            
                        }
                    }

                }
            }

        }

        /**
         * Create a new table in the test database for storing messages.
         *
         */
        private void createNewMessageTable() {
            // SQL statement for creating a new table
            String sql = "CREATE TABLE IF NOT EXISTS Messages (MsgID     INTEGER NOT NULL,\n"
                    + "                       Namespace TEXT NOT NULL,\n"
                    + "                       Message   BLOB NOT NULL,\n"
                    + "                       CONSTRAINT pk PRIMARY KEY (MsgID));";

            try ( Connection conn = connect();  Statement stmt = conn.createStatement()) {
                // create a new table
                stmt.execute(sql);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        }

        /**
         * Establishes a connection to the message database
         *
         * @return
         */
        private Connection connect() {
            File msgDB = new File(messageFilePath);

            // SQLite connection string
            String url = "jdbc:sqlite:" + msgDB.getAbsolutePath();
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(url);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            return conn;

        }

        /**
         * insert the message into the message database
         *
         * @param namespace
         * @param message
         */
        final private int insertMessage(String namespace, InputStream message) {
            synchronized (lockObject) {
                if (!databaseCreated) {
                    createMessageDatabase();
                }

                System.out.println("Writting record " + nextDatabaseMessageIndex);
                // update sql
                try ( Connection conn = connect();  PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Messages" + "("
                        + "MsgID, Namespace, Message) VALUES (?,?,?)")) {
                    pstmt.setInt(1, nextDatabaseMessageIndex);
                    StringBuilder stackHistory = new StringBuilder();
                    for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                        stackHistory.append(ste.toString() + "\n");
                    }
                    pstmt.setString(2, stackHistory.toString());
                    pstmt.setBytes(3, message.readAllBytes());

                    pstmt.execute();

                    currentMessageIndex = nextDatabaseMessageIndex;
                    nextDatabaseMessageIndex++;
                    return currentMessageIndex;
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            return 0;
        }

        /**
         * insert the message into the message database
         *
         * @param namespace
         * @param message
         */
        final private int insertMessageFromBytes(String namespace, byte[] message) {
            synchronized (lockObject) {
                if (!databaseCreated) {
                    createMessageDatabase();
                }

                System.out.println("Writting record " + nextDatabaseMessageIndex);
                // update sql
                try ( Connection conn = connect();  PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Messages" + "("
                        + "MsgID, Namespace, Message) VALUES (?,?,?)")) {
                    pstmt.setInt(1, nextDatabaseMessageIndex);
                    StringBuilder stackHistory = new StringBuilder();
                    for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                        stackHistory.append(ste.toString() + "\n");
                    }
                    pstmt.setString(2, stackHistory.toString());
                    pstmt.setBytes(3, message);

                    pstmt.execute();

                    currentMessageIndex = nextDatabaseMessageIndex;
                    nextDatabaseMessageIndex++;
                    return currentMessageIndex;

                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            return 0;
        }

        /**
         * read the message associated with the given message identifier and
         * insert into the file specified
         *
         * @param messageId
         * @param filename
         */
        private void readMessage(int messageId, String filename) throws Exception {
            // update sql
            String selectSQL = "SELECT Message FROM Messages WHERE MsgID=?";
            ResultSet rs = null;

            Connection conn = null;

            synchronized (lockObject) {
                try {
                    conn = connect();
					if (conn != null)
					{
						try (PreparedStatement pstmt = conn.prepareStatement(selectSQL))
						{
							pstmt.setInt(1, messageId);
							rs = pstmt.executeQuery();

							// write binary stream into file
							File file = new File(filename);
							try (FileOutputStream fos = new FileOutputStream(file))
							{

								System.out.println("Writing BLOB to file " + file.getAbsolutePath() + " with messageID " + messageId);
								while (rs.next()) {
									InputStream input = rs.getBinaryStream("Message");
									byte[] buffer = new byte[1024];
									while (input.read(buffer) > 0) {
										fos.write(buffer);
									}
								}
							}
						}
					}
                } catch (SQLException | IOException e) {
                    System.out.println(e.getMessage());
                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }

                        if (conn != null) {
                            conn.close();
                        }

                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }

        /**
         * Converts the content of this message to byte array.
         *
         * Pre-Conditions: N/A Post-Conditions: N/A
         *
         * @return the byte[]
         */
        public byte[] toByteArray(int messageId) {

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];

            String selectSQL = "SELECT Message FROM Messages WHERE MsgID=?";
            ResultSet rs = null;
            Connection conn = null;
            synchronized (lockObject) {
                try {
                    conn = connect();
					if (conn != null)
					{
						try (PreparedStatement pstmt = conn.prepareStatement(selectSQL))
						{
							pstmt.setInt(1, messageId);
							rs = pstmt.executeQuery();

							while (rs.next()) {
								InputStream input = rs.getBinaryStream("Message");
								while ((nRead = input.read(data, 0, data.length)) != -1) {
									buffer.write(data, 0, nRead);
								}

								buffer.flush();
								input.close();

							}
						}
					}
                } catch (SQLException | IOException e) {
                    javax.swing.JOptionPane.showMessageDialog(null, selectSQL + "("+messageId+")"+e.getMessage(), "MessageContentDBCache: toByteArray SQLException", javax.swing.JOptionPane.ERROR_MESSAGE);
                    System.out.println(e.getMessage());
                } catch (Exception ex) {
                    javax.swing.JOptionPane.showMessageDialog(null, selectSQL + "("+messageId+")"+ex.getMessage(), "MessageContentDBCache: toByteArray Exception", javax.swing.JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();

                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }

                        if (conn != null) {
                            conn.close();
                        }
//                if (buffer != null) {
//                    buffer.close();
//                }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return buffer.toByteArray();
        }

        /**
         * Provides the content of this message via an InputStream.
         *
         * Pre-Conditions: N/A Post-Conditions: N/A
         *
         * @return the byte[]
         */
        public InputStream toInputStream(int messageId) {

            InputStream input = null;

            String selectSQL = "SELECT Message FROM Messages WHERE MsgID=?";
            ResultSet rs = null;
            Connection conn = null;

            synchronized (lockObject) {
                try {
                    conn = connect();
					if (conn != null)
					{
						try (PreparedStatement pstmt = conn.prepareStatement(selectSQL))
						{
						  pstmt.setInt(1, messageId);
						  rs = pstmt.executeQuery();

						  while (rs.next()) {
							  input = rs.getBinaryStream("Message");
						  }
						}
					}
                } catch (SQLException e) {
                    javax.swing.JOptionPane.showMessageDialog(null, selectSQL + "("+messageId+")"+e.getMessage(), "MessageContentDBCache: toByteArray SQLException", javax.swing.JOptionPane.ERROR_MESSAGE);
                    System.out.println(e.getMessage());
                } catch (Exception ex) {
                    javax.swing.JOptionPane.showMessageDialog(null, selectSQL + "("+messageId+")"+ex.getMessage(), "MessageContentDBCache: toByteArray Exception", javax.swing.JOptionPane.ERROR_MESSAGE);
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
                        ex.printStackTrace();
                    }
                }
            }
            return input;
        }

        /**
         *
         * @param messageNumber
         */
        public void closeMessage(int messageNumber) {
            synchronized (lockObject) {
                closedMessagesIndex++;
                if (closedMessagesIndex == currentMessageIndex) {
                    File tmpFile = new File(messageFilePath);
                    if (tmpFile.exists()) {
                        System.out.println("Finalize is deleting the file " + messageFilePath);
                        tmpFile.delete();
                    }
                    tmpFile = new File(messageFilePath + "-journal");
                    if (tmpFile.exists()) {
                        System.out.println("Finalize is deleting the journal file " + messageFilePath);
                        tmpFile.delete();
                    }
                    databaseCreated = false;
                }
            }
        }

    }
}
