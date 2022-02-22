/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.dbase;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import org.sqlite.SQLiteConfig;

/**
 *
 * @author TransCore ITS
 */

public class TMDDConnectionPool {
    private static String dbFileName;
    private static int maximumPoolSize;
    private static int size;
    private static Queue<Connection> connections;
    private static boolean initialized = false;
    private final static Object lockObject = new Object();

    public static void Close(){
        
        synchronized (lockObject) {
            initialized = false;

            while (connections != null && !connections.isEmpty()) {
                try{
               connections.poll().close();
               size--;
                } catch (Exception ex){
                    System.out.println("TMDDConnectionPool::Close:Exception encountered clearing connections: "+ex.getMessage());
                }
            }
        }        
    }
    public static void Initialize(String databaseFileName)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        synchronized (lockObject) {   
            if (initialized && dbFileName.equalsIgnoreCase(databaseFileName)){
                return;
            } else {
                Close();
            }
            // Create a SQLite connection
            Class.forName("org.sqlite.JDBC");
            dbFileName = databaseFileName;
            maximumPoolSize = 1;
            size = 0;
            connections = new LinkedList<>();
            initialized = true;
        }
    }

    public static Connection getConnection() throws SQLException, InterruptedException, Exception {
        if (!initialized)throw new Exception("TMDD Database has not been initialized.");
        return getConnection(300);
    }

    private static Connection createNewConnection() throws SQLException {
        try {
            synchronized (lockObject) {            
                SQLiteConfig sqlCfg = new SQLiteConfig();
                Connection conn = sqlCfg.createConnection("jdbc:sqlite:" + dbFileName);
                sqlCfg.setJournalMode(SQLiteConfig.JournalMode.MEMORY);
                sqlCfg.setSynchronous(SQLiteConfig.SynchronousMode.OFF);
    //            sqlCfg.setPageSize(4096);
                sqlCfg.setTempStore(SQLiteConfig.TempStore.MEMORY);
    //            sqlCfg.apply(conn);
                conn.setAutoCommit(false);
                conn.commit();
                DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
                connections.add(conn);

                return conn;
            }
        } catch (Throwable t) {
            synchronized (lockObject) {
                size--;
                lockObject.notifyAll();
            }
            t.printStackTrace();
            throw new SQLException("Connection not available", t);
        }
    }

    public static Connection getConnection(long timeout) throws SQLException, InterruptedException, Exception {
        if (!initialized) throw new Exception("TMDD Database has not been initialized.");
        long timestamp = System.currentTimeMillis() + timeout;

        boolean createNewConnection = false;

        synchronized (lockObject) {
            while (connections.isEmpty()) {
                if (size < maximumPoolSize) {
                    size++;
                    createNewConnection = true;
                    break;
                } else {
                    lockObject.wait(Math.max(timestamp - System.currentTimeMillis(), 1));
					
					if (timestamp <= System.currentTimeMillis()) {
						throw new SQLException("Connection not available after "+timeout + " ms.");
					}					
                }
            }

            if (!createNewConnection) {
                return connections.poll();
            }
        }

        return createNewConnection();
    }

    public static void releaseConnection(Connection connection) {
        synchronized (lockObject) {
            if (!connections.contains(connection)) connections.offer(connection);
            lockObject.notifyAll();
        }
    }
}
