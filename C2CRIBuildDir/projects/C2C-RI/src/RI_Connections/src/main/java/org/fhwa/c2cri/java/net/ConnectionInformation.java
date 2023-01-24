/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.java.net;

import java.net.InetAddress;

/**
 * The Class ConnectionInformation stores data about the connection.
 *
 * @author TransCore ITS, LLC
 */
public class ConnectionInformation {
    
    /** The remote address. */
    private InetAddress remoteAddress;
    
    /** The local address. */
    private InetAddress localAddress;
    
    /** The remote port. */
    private int remotePort;
    
    /** The local port. */
    private int localPort;
    
    /** The server socket. */
    private boolean serverSocket;
    
    /** The sequence count. */
    private volatile long sequenceCount=0;
    
    /** The connection name. */
    private volatile String connectionName="";
    
    /** The test case name. */
    private volatile String testCaseName = "UNKNOWN";
    
    /** Live Test Connection **/
    private volatile boolean liveTestConnection = false;
    
    /**
     * Instantiates a new connection information.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param localAddress the local address
     * @param localPort the local port
     * @param remoteAddress the remote address
     * @param remotePort the remote port
     * @param serverSocket the server socket
     */
    public ConnectionInformation(InetAddress localAddress, int localPort, InetAddress remoteAddress, int remotePort, boolean serverSocket){
        this.remoteAddress = remoteAddress;
        this.localAddress = localAddress;
        this.serverSocket = serverSocket;
        this.remotePort = remotePort;
        this.localPort = localPort;
    }
        
    /**
     * Checks if is connected to remote socket.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param hostAddress the host address
     * @param port the port
     * @return true, if is connected to remote socket
     */
    public boolean isConnectedToRemoteSocket(String hostAddress, int port){        
        boolean results = hostAddress.equalsIgnoreCase(remoteAddress.getHostAddress())||hostAddress.equalsIgnoreCase(remoteAddress.getHostName());        
        return results && (remotePort==port);
    }

    /**
     * Checks if is connected to local socket.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param hostAddress the host address
     * @param port the port
     * @return true, if is connected to local socket
     */
    public boolean isConnectedToLocalSocket(String hostAddress, int port){        
        boolean results = hostAddress.equalsIgnoreCase(localAddress.getHostAddress())||hostAddress.equalsIgnoreCase(localAddress.getHostName());        
        return results && (localPort==port);
    }

    /**
     * Checks if is matching connection.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param remoteAddress the remote address
     * @param remotePort the remote port
     * @param localAddress the local address
     * @param localPort the local port
     * @return true, if is matching connection
     */
    public boolean isMatchingConnection(String remoteAddress, int remotePort, String localAddress, int localPort){
        return isConnectedToRemoteSocket(remoteAddress, remotePort) &&
               isConnectedToLocalSocket(localAddress, localPort);
    }    
    
    /**
     * Gets the remote host name.
     *
     * @return the remote host name
     */
    public String getRemoteHostName(){
        return remoteAddress.getHostName();
    }
    
    /**
     * Gets the remote ip address.
     *
     * @return the remote ip address
     */
    public String getRemoteIPAddress(){
        return remoteAddress.getHostAddress();
    }
    
    
    /**
     * Gets the local host name.
     *
     * @return the local host name
     */
    public String getLocalHostName(){
        return localAddress.getHostName();
    }
    
    /**
     * Gets the local ip address.
     *
     * @return the local ip address
     */
    public String getLocalIPAddress(){
        return localAddress.getHostAddress();
    }
        
    
    /**
     * Gets the remote address.
     *
     * @return the remote address
     */
    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    /**
     * Sets the remote address.
     *
     * @param remoteAddress the new remote address
     */
    public void setRemoteAddress(InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    /**
     * Gets the local address.
     *
     * @return the local address
     */
    public InetAddress getLocalAddress() {
        return localAddress;
    }

    /**
     * Sets the local address.
     *
     * @param localAddress the new local address
     */
    public void setLocalAddress(InetAddress localAddress) {
        this.localAddress = localAddress;
    }

    /**
     * Gets the remote port.
     *
     * @return the remote port
     */
    public int getRemotePort() {
        return remotePort;
    }

    /**
     * Sets the remote port.
     *
     * @param remotePort the new remote port
     */
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    /**
     * Gets the local port.
     *
     * @return the local port
     */
    public int getLocalPort() {
        return localPort;
    }

    /**
     * Sets the local port.
     *
     * @param localPort the new local port
     */
    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    /**
     * Checks if is server socket.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is server socket
     */
    public boolean isServerSocket() {
        return serverSocket;
    }

    /**
     * Gets the sequence count.
     *
     * @return the sequence count
     */
    public long getSequenceCount() {
        return sequenceCount;
    }

    /**
     * Increment sequence count.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public synchronized void incrementSequenceCount(){
        sequenceCount = sequenceCount + 1;
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
     * Gets the test case name.
     *
     * @return the test case name
     */
    public String getTestCaseName() {
        return testCaseName;
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
     * Sets the live Test Connection Flag.
     *
     * @return the status of the Live Test Connection flag
     */
    public boolean isLiveTestConnection() {
        return liveTestConnection;
    }

    /**
     * Gets the live test Connection Flag.
     *
     * @param liveTestConnection indicates whether live testing is underway and that data should be logged.
     */
    public void setLiveTestConnection(boolean liveTestConnection) {
        this.liveTestConnection = liveTestConnection;
    }



    
}
