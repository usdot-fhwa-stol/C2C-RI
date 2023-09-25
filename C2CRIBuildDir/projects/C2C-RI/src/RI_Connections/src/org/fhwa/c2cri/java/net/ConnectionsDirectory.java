/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.java.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Class ConnectionsDirectory maintains a current list of connections that may be logged.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  12/3/2013
 */
public class ConnectionsDirectory {
    
    
/** The all sockets. */
final List<ConnectionInformation> allSockets = Collections.synchronizedList(new ArrayList<ConnectionInformation>());    

/** The traffic logger. */
private static TrafficLogger trafficLogger;

/** The test case name. */
private volatile static String testCaseName;

private volatile static boolean enableLiveLogging;

/** The defined connection names. */
final List<DefinedConnectionName> definedConnectionNames = Collections.synchronizedList(new ArrayList<DefinedConnectionName>());    


 private static enum DefinedConnectionState {NOTDEFINED,ACTIVE,INACTIVE};

  /** The Constant instance. */
  private final static ConnectionsDirectory instance =
      new ConnectionsDirectory();

    /**
     * Gets the single instance of ConnectionsDirectory.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return single instance of ConnectionsDirectory
     */
    public static ConnectionsDirectory getInstance() {
    return instance;
  }
  
  /**
   * Instantiates a new connections directory.
   * 
   * Pre-Conditions: N/A
   * Post-Conditions: N/A
   */
  private ConnectionsDirectory(){      
  }

    /**
     * Adds the connection.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param newConnection the new connection
     */
    public void addConnection(ConnectionInformation newConnection){
                            System.out.print("ConnectionsDirectory::addConnection-StackTrace");
                            for (StackTraceElement element:Thread.currentThread().getStackTrace()){
                                System.out.println(element.toString());
                            }
        
      // Indicate whether this connection should be logged.
          if (newConnection.isServerSocket()){
              if (getConnectionNameAvailability(newConnection.getLocalHostName(), newConnection.getLocalIPAddress(), newConnection.getLocalPort())){
                  System.out.println("ConnectionsDirectory::addConnection  SetLiveTestConnection to True");
                  newConnection.setLiveTestConnection(true);
                  setConnectionNameAvailability(newConnection.getLocalHostName(), newConnection.getLocalIPAddress(), newConnection.getLocalPort(), false);
              } else {
                  System.out.println("ConnectionsDirectory::addConnection  SetLiveTestConnection to False");
                  newConnection.setLiveTestConnection(false);
              }
          } 
      if (enableLiveLogging) {
          System.out.println("ConnectionsDirectory::addConnection  enableLiveLogging = "+enableLiveLogging);
              if (getConnectionNameAvailability(newConnection.getRemoteHostName(), newConnection.getRemoteIPAddress(), newConnection.getRemotePort())){
                  newConnection.setLiveTestConnection(true);
                  setConnectionNameAvailability(newConnection.getRemoteHostName(), newConnection.getRemoteIPAddress(), newConnection.getRemotePort(), false);
                  System.out.println("ConnectionsDirectory::addConnection  SetLiveTestConnection to True");
              } else {
                  newConnection.setLiveTestConnection(false);
                  System.out.println("ConnectionsDirectory::addConnection  SetLiveTestConnection to False");
              }              
          
      }      
      
      if (!allSockets.contains(newConnection)){
         if (newConnection.getConnectionName().isEmpty()){
              if (newConnection.isServerSocket()){
                  newConnection.setConnectionName(getConnectionName(
                          newConnection.getLocalHostName(), newConnection.getLocalIPAddress(),newConnection.getLocalPort()));                 
              } else {
                  newConnection.setConnectionName(getConnectionName(
                          newConnection.getRemoteHostName(), newConnection.getRemoteIPAddress(),newConnection.getRemotePort()));                  
              }
         }
         allSockets.add(newConnection);         
       }  
  }

    /**
     * Removes the connection.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param oldConnection the old connection
     */
    public void removeConnection(ConnectionInformation oldConnection){
      if (oldConnection == null) return;
      if (allSockets.contains(oldConnection)){
          allSockets.remove(oldConnection);
      }
          if (oldConnection.isServerSocket()){
              if (getConnectionNameAvailability(oldConnection.getLocalHostName(), oldConnection.getLocalIPAddress(), oldConnection.getLocalPort())){
                  setConnectionNameAvailability(oldConnection.getLocalHostName(), oldConnection.getLocalIPAddress(), oldConnection.getLocalPort(), true);
              } 
          } else {
              if (getConnectionNameAvailability(oldConnection.getRemoteHostName(), oldConnection.getRemoteIPAddress(), oldConnection.getRemotePort())){
                  setConnectionNameAvailability(oldConnection.getRemoteHostName(), oldConnection.getRemoteIPAddress(), oldConnection.getRemotePort(), true);
              }              
          }
      
  }

    
    /**
     * Clear all connections.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void clearAllConnections(){
        allSockets.clear();
        definedConnectionNames.clear();
    }
    
    /**
     * Gets the connection info for local host.
     *
     * @param hostAddress the host address
     * @param port the port
     * @return the connection info for local host
     */
    public synchronized ConnectionInformation getConnectionInfoForLocalHost(String hostAddress, int port){
      for (ConnectionInformation thisConnection : allSockets){
          if (thisConnection.isConnectedToLocalSocket(hostAddress, port)){
              return thisConnection;
          }
      }
      return null;
  }

    /**
     * Gets the connection info for remote host.
     *
     * @param hostAddress the host address
     * @param port the port
     * @return the connection info for remote host
     */
    public synchronized ConnectionInformation getConnectionInfoForRemoteHost(String hostAddress, int port){
      for (ConnectionInformation thisConnection : allSockets){
          if (thisConnection.isConnectedToRemoteSocket(hostAddress, port)){
              return thisConnection;
          }
      }
      return null;
  }

    /**
     * Adds the defined connection name.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param targetAddress the target address
     * @param targetPort the target port
     * @param connectionName the connection name
     */
    public void addDefinedConnectionName(String targetAddress, int targetPort, String connectionName){
      DefinedConnectionName thisName = new DefinedConnectionName(targetAddress, targetPort, connectionName);
      if (!definedConnectionNames.contains(thisName)){
         definedConnectionNames.add(thisName);
         System.out.println("Added Connection: "+connectionName+" for targetAddress:Port "+targetAddress+":"+targetPort);
       }  
  }

    /**
     * Removes the defined connection name.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param targetAddress the target address
     * @param targetPort the target port
     * @param connectionName the connection name
     */
    public void removeDefinedConnectionName(String targetAddress, int targetPort, String connectionName){
      DefinedConnectionName thisName = new DefinedConnectionName(targetAddress, targetPort, connectionName);
      if (definedConnectionNames.contains(thisName)){
          definedConnectionNames.remove(thisName);
      }
  }
  
    
    /**
     * Gets the traffic logger.
     *
     * @return the traffic logger
     */
    public synchronized TrafficLogger getTrafficLogger() {
        return trafficLogger;
    }

    /**
     * Sets the traffic logger.
     *
     * @param inTrafficLogger the new traffic logger
     */
    public synchronized void setTrafficLogger(TrafficLogger inTrafficLogger) {
            this.trafficLogger = inTrafficLogger;                          
    }

    /**
     * Sets the test case name.
     *
     * @param testCaseName the new test case name
     */
    public void setTestCaseName(String testCaseName) {
      this.testCaseName = testCaseName;
    }        

    /**
     * Gets the test case name.
     *
     * @return the test case name
     */
    public String getTestCaseName() {
        return testCaseName;
    }

    /**
     * Gets the connection name.
     *
     * @param hostname the hostname
     * @param address the address
     * @param port the port
     * @return the connection name
     */
    public String getConnectionName(String hostname, String address, int port){
        String results = "";
        for (DefinedConnectionName thisConnName : definedConnectionNames){
            if (thisConnName.isConnectionMatch(hostname, address, port)){
                return thisConnName.getConnectionName();
            }
        }
        return results;
    }
    
    /**
     * Sets whether the connection associated with the related connection name is active.
     *
     * @param hostname the hostname
     * @param address the address
     * @param port the port
     * @param available flag indicating whether the connection is currently available.
     */
    public void setConnectionNameAvailability(String hostname, String address, int port, boolean available){
        for (DefinedConnectionName thisConnName : definedConnectionNames){
            if (thisConnName.isConnectionMatch(hostname, address, port)){
                if (!available){
                    thisConnName.setConnectionActive(DefinedConnectionState.ACTIVE);
                } else {
                    thisConnName.setConnectionActive(DefinedConnectionState.INACTIVE);                    
                }
            }
        }
    }

    /**
     * Sets whether the connection associated with the related connection name is available.
     *
     * @param hostname the hostname
     * @param address the address
     * @param port the port
     * @return the connection state of the connection name.
     */
    public boolean getConnectionNameAvailability(String hostname, String address, int port){
        boolean results = false;
        for (DefinedConnectionName thisConnName : definedConnectionNames){
            if (thisConnName.isConnectionMatch(hostname, address, port)){
                System.out.println("ConnectionsDirectory::getConnectionNameAvailability("+hostname+","+ address+","+ port+")   CurrentAvailability = "+thisConnName.getConnectionActive().name());
                if(thisConnName.getConnectionActive().equals(DefinedConnectionState.INACTIVE)) results = true;
            }
        }
        if (!results) System.out.println("ConnectionsDirectory::getConnectionNameAvailability("+hostname+","+ address+","+ port+")   No open defined connections were found.");
        return results;
    }
    
    
    /**
     * Checks if is defined connection.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param hostname the hostname
     * @param address the address
     * @param port the port
     * @return true, if is defined connection
     */
    public boolean isDefinedConnection(String hostname, String address, int port){
        boolean results = false;
        for (DefinedConnectionName thisConnName : definedConnectionNames){
            if (thisConnName.isConnectionMatch(hostname, address, port)){
                return true;
            }
        }
        return results;
    }
    
    public void setEnableLiveLogging(boolean enableLiveLogging) {
        ConnectionsDirectory.enableLiveLogging = enableLiveLogging;
    }
    
    /**
     * The Class DefinedConnectionName.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    class DefinedConnectionName {

        /** The target address. */
        private String targetAddress = "Not Cconnected";
        
        /** The target port. */
        private int targetPort = -2;
        
        /** The connection name. */
        private String connectionName = "";
        
        /** The connection state of this defined connection */
        private DefinedConnectionState connectionActive = DefinedConnectionState.NOTDEFINED;
        /**
         * Instantiates a new defined connection name.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param remoteIPAddress the remote ip address
         * @param remotePort the remote port
         * @param connectionName the connection name
         */
        public DefinedConnectionName(String remoteIPAddress, int remotePort, String connectionName){
            this.targetAddress = remoteIPAddress;
            this.targetPort = remotePort;
            this.connectionName = connectionName;
            this.connectionActive = DefinedConnectionState.INACTIVE;
        }
        
        
        /**
         * Instantiates a new defined connection name.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         */
        private DefinedConnectionName(){            
        }
        
        /**
         * Checks if is connection match.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param hostName the host name
         * @param ipAddress the ip address
         * @param port the port
         * @return true, if is connection match
         */
        public boolean isConnectionMatch(String hostName, String ipAddress, int port) {
            return (targetAddress.equalsIgnoreCase(ipAddress)||(targetAddress.equalsIgnoreCase(hostName))) && (targetPort == port);
        }


        /**
         * Gets the target ip address.
         *
         * @return the target ip address
         */
        public String getTargetIPAddress() {
            return targetAddress;
        }

        /**
         * Sets the target ip address.
         *
         * @param remoteIPAddress the new target ip address
         */
        public void setTargetIPAddress(String remoteIPAddress) {
            this.targetAddress = remoteIPAddress;
        }

        /**
         * Gets the target port.
         *
         * @return the target port
         */
        public int getTargetPort() {
            return targetPort;
        }

        /**
         * Sets the target port.
         *
         * @param remotePort the new target port
         */
        public void setTargetPort(int remotePort) {
            this.targetPort = remotePort;
        }

        /**
         * Gets the connection name.
         *
         * @return the connection name
         */
        public String getConnectionName() {
            return connectionName;
        }

        /**
         * Sets the connection name.
         *
         * @param connectionName the new connection name
         */
        public void setConnectionName(String connectionName) {
            this.connectionName = connectionName;
        }

        /**
         * Returns the current connection state for this Defined Connection.
         * 
         * @return 
         */
        public DefinedConnectionState getConnectionActive() {
            return connectionActive;
        }

        /**
         * Sets the current connection state for this Defined Connection.
         * 
         * @param connectionActive 
         */
        public void setConnectionActive(DefinedConnectionState connectionActive) {
            this.connectionActive = connectionActive;
        }

     
    }

}
