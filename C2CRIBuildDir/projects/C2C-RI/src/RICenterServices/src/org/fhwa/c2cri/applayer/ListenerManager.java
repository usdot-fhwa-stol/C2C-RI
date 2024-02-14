/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.applayer;

import org.fhwa.c2cri.java.net.ConnectionsDirectory;
import org.fhwa.c2cri.java.net.ConnectionInformation;
import java.util.HashMap;
import java.util.Map.Entry;
import org.enterprisepower.net.portforward.SocketAssignmentListener;

/**
 * The ListenerManager is used to obtain/manage the destination and source addresses for message traffic.  It is a 
 * key part of being able to properly log messages during a test.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ListenerManager implements SocketAssignmentListener {

    /** The listener manager. */
    private static ListenerManager listenerManager;
    
    /** The internal to external address map. */
    private static HashMap<String,String>internalToExternalAddressMap = new HashMap();
    
    /** The listener map. */
    private static final HashMap<Integer, ListenerInfo> listenerMap = new HashMap();
    
    /** The listener id map. */
    private static final HashMap<Integer,String> listenerIdMap = new HashMap();
    
    /** The listener id. */
    private static Integer listenerId=0;

	private static final Object LOCK = new Object();
    /**
     * Instantiates a new listener manager.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private ListenerManager() {
    }

    /**
     * Gets the single instance of ListenerManager.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return single instance of ListenerManager
     */
    public static ListenerManager getInstance() {
        if (listenerManager == null) {
            listenerManager = new ListenerManager();
        }
        return listenerManager;
    }

    /**
     * Adds the internal address mapping.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param internalAddress the internal address
     * @param externalAddress the external address
     */
    @Override
    public void addInternalAddressMapping(String internalAddress, String externalAddress) {
        synchronized(internalToExternalAddressMap){
           internalToExternalAddressMap.put(internalAddress, externalAddress);
           System.out.println("ListenrManager::addInternalAddressMapping "+internalAddress + " =>"+externalAddress);
        }
    }

        
    /**
     * Gets the external address.
     *
     * @param internalAddress the internal address
     * @return the external address
     */
    public String getExternalAddress(String internalAddress){
        return internalAddress;
//        String returnAddress = internalAddress;
//        if (internalToExternalAddressMap.containsKey(internalAddress)){
//            returnAddress = internalToExternalAddressMap.get(internalAddress);
//        }
//        return returnAddress;
    }
    
    /**
     * Increment listener id.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the integer
     */
    private Integer incrementListenerId(){
        synchronized(LOCK){
            listenerId++;
        }
        return listenerId;
    }

    /**
     * Sets the test case id.
     *
     * @param testCaseID the new test case id
     */
    public void setTestCaseID(String testCaseID) {
        ConnectionsDirectory.getInstance().setTestCaseName(testCaseID);
    }

    /**
     * Creates the server mode listener.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param externalServerAddress the external server address
     * @param externalServerPort the external server port
     * @param internalServerAddress the internal server address
     * @param internalServerPort the internal server port
     * @param enableSSL - utilize SSL
     * @param connectionName the connection name
     * @return the integer
     * @throws TransportException the transport exception
     */
    public Integer createServerModeListener(String externalServerAddress, Integer externalServerPort, String internalServerAddress, Integer internalServerPort, boolean enableSSL, String connectionName) throws TransportException{
        Integer thisId = -1;
		ListenerInfo listInfo = null;
		boolean bAdd = false;
		synchronized (listenerIdMap)
		{
			if (!listenerIdMap.containsValue(connectionName)) {
				ConnectionInformation connInfo = ConnectionsDirectory.getInstance().getConnectionInfoForLocalHost(externalServerAddress, externalServerPort);
				if (connInfo != null){
					connInfo.setConnectionName(connectionName);
					listInfo = new ListenerInfo(connInfo, connectionName);
					ConnectionsDirectory.getInstance().addDefinedConnectionName(connInfo.getLocalIPAddress(), connInfo.getLocalPort(), connectionName);
				} else {
					listInfo = new ListenerInfo(externalServerAddress, externalServerPort, connectionName);
					ConnectionsDirectory.getInstance().addDefinedConnectionName(externalServerAddress, externalServerPort, connectionName);
				}

				String externalSocketAddress = externalServerAddress + ":" + externalServerPort;
				String internalSocketAddress = internalServerAddress + ":" + internalServerPort;
				try {
					thisId = incrementListenerId();
					listenerIdMap.put(thisId, connectionName);
					bAdd = true;
					
				} catch (Exception ex) {
					ex.printStackTrace();
					javax.swing.JOptionPane.showMessageDialog(null, "ListenerManager:createServerModeListener \nError Occurred: "+ ex.getMessage() + "\n\nExternalSocketAddress= "+externalSocketAddress+"\nConnectionName= "+connectionName+"\nenableSSL= "+enableSSL, "ListenerManager", javax.swing.JOptionPane.ERROR_MESSAGE);
					throw new TransportException("ListenerManager:createClientModeListener "+ex.getMessage(),TransportException.PROTOCOL_ERROR_TYPE);
				}
			} else {
				System.err.println("ListenerManager:  Requested Listener " + connectionName + " already exists.");
				for (Entry<Integer, String> oEntry : listenerIdMap.entrySet())
				{
					thisId = oEntry.getKey();
					if (oEntry.getValue().equals(connectionName))
						break;
				}
			}
		}
		if (bAdd)
		{
			synchronized (listenerMap)
			{
				listenerMap.put(thisId, listInfo);
			}
		}
        return thisId;
    }

    /**
     * Creates the server mode listener.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param internalServerAddress the internal server address
     * @param internalServerPort the internal server port
     * @param enableSSL - utilize SSL
     * @param connectionName the connection name
     * @return the integer
     * @throws TransportException the transport exception
     */
    public Integer createServerModeListener(String internalServerAddress, Integer internalServerPort, boolean enableSSL, String connectionName) throws TransportException{
        Integer thisId = -1;
		ListenerInfo listInfo = null;
		boolean bAdd = false;
		synchronized (listenerIdMap)
		{
			if (!listenerIdMap.containsValue(connectionName)) {

				String internalSocketAddress = internalServerAddress + ":" + internalServerPort;
				ConnectionInformation connInfo = ConnectionsDirectory.getInstance().getConnectionInfoForLocalHost(internalServerAddress, internalServerPort);

				if (connInfo != null){
					connInfo.setConnectionName(connectionName);
					listInfo = new ListenerInfo(connInfo, connectionName);
					ConnectionsDirectory.getInstance().addDefinedConnectionName(connInfo.getLocalIPAddress(), connInfo.getLocalPort(), connectionName);
				} else {
					listInfo = new ListenerInfo(internalServerAddress, internalServerPort, connectionName);
					ConnectionsDirectory.getInstance().addDefinedConnectionName(internalServerAddress, internalServerPort, connectionName);
				}
				try {
	//                Listener thisListener =
	//                        new Listener(NetUtils.parseInetSocketAddress(internalSocketAddress), this.testCaseID, connectionName, enableSSL,true);
	//                Thread dataTransferListenerThread = new Thread(thisListener);
	//                dataTransferListenerThread.setName(connectionName);
	//             dataTransferListenerThread.setContextClassLoader(Thread.currentThread().getContextClassLoader());
	//                dataTransferListenerThread.setDaemon(true);
	//                thisListener.registerAddressListener(this);
	//                dataTransferListenerThread.start();
					thisId = incrementListenerId();
					listenerIdMap.put(thisId, connectionName);
					bAdd = true;
				} catch (Exception ex) {
					ex.printStackTrace();
					javax.swing.JOptionPane.showMessageDialog(null, "ListenerManager:createServerModeListener \nError Occurred: "+ ex.getMessage() + "\n\nInternalSocketAddress= "+internalSocketAddress+"\nConnectionName= "+connectionName+"\nenableSSL= "+enableSSL, "ListenerManager", javax.swing.JOptionPane.ERROR_MESSAGE);
					throw new TransportException("ListenerManager:createClientModeListener "+ex.getMessage(),TransportException.PROTOCOL_ERROR_TYPE);
				}
			} else {
				System.err.println("ListenerManager:  Requested Listener " + connectionName + " already exists.");
				for (Entry<Integer, String> oEntry : listenerIdMap.entrySet())
				{
					thisId = oEntry.getKey();
					if (oEntry.getValue().equals(connectionName))
						break;
				}
			}
		}
		if (bAdd)
		{
			synchronized (listenerMap)
			{
				listenerMap.put(thisId, listInfo);
			}
		}
        return thisId;
    }


    /**
     * Creates the client mode listener.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param remoteServerAddress the remote server address
     * @param remoteServerPort the remote server port
     * @param enableSSL - utilize SSL
     * @param connectionName the connection name
     * @return the integer
     * @throws TransportException the transport exception
     */
    public Integer createClientModeListener(String remoteServerAddress, Integer remoteServerPort, boolean enableSSL, String connectionName) throws TransportException {
        Integer thisId = -1;
		boolean bAdd = false;
		ListenerInfo listInfo = null;
		synchronized (listenerIdMap)
		{
			if (!listenerIdMap.containsValue(connectionName)) {

				String externalSocketAddress = remoteServerAddress + ":" + remoteServerPort;
				ConnectionInformation connInfo = ConnectionsDirectory.getInstance().getConnectionInfoForLocalHost(remoteServerAddress, remoteServerPort);
				if (connInfo != null){
					connInfo.setConnectionName(connectionName);
					listInfo = new ListenerInfo(connInfo, connectionName);
					ConnectionsDirectory.getInstance().addDefinedConnectionName(connInfo.getLocalIPAddress(), connInfo.getLocalPort(), connectionName);
				} else {
					listInfo = new ListenerInfo(remoteServerAddress, remoteServerPort, connectionName);
					listInfo.setLocalIPAddress(remoteServerAddress);
					listInfo.setLocalPort(remoteServerPort);
					ConnectionsDirectory.getInstance().addDefinedConnectionName(remoteServerAddress, remoteServerPort, connectionName);
				}
				try {
	//                Listener thisListener =
	//                        new Listener(NetUtils.parseInetSocketAddress(externalSocketAddress), this.testCaseID, connectionName, enableSSL,false);

	//                Thread dataTransferListenerThread = new Thread(thisListener);
	//             dataTransferListenerThread.setContextClassLoader(Thread.currentThread().getContextClassLoader());
	//                dataTransferListenerThread.setName(connectionName);
	//                dataTransferListenerThread.setDaemon(true);
	//                thisListener.registerAddressListener(this);
	//                dataTransferListenerThread.start();
					thisId = incrementListenerId();
					listenerIdMap.put(thisId, connectionName);
					bAdd = true;
					
				} catch (Exception ex) {
					ex.printStackTrace();
					javax.swing.JOptionPane.showMessageDialog(null, "ListenerManager:createClientModeListener \nError Occurred: "+ ex.getMessage() + "\n\nExternalSocketAddress= "+externalSocketAddress+"\nConnectionName= "+connectionName+"\nenableSSL= "+enableSSL, "ListenerManager", javax.swing.JOptionPane.ERROR_MESSAGE);
					throw new TransportException("ListenerManager:createClientModeListener "+ex.getMessage(),TransportException.PROTOCOL_ERROR_TYPE);
				}
			} else {
				System.err.println("ListenerManager:  Requested Listener " + connectionName + " already exists.");
				for (Entry<Integer, String> oEntry : listenerIdMap.entrySet())
				{
					thisId = oEntry.getKey();
					if (oEntry.getValue().equals(connectionName))
						break;
				}
			}
		}
		if (bAdd)
		{
			synchronized (listenerMap)
			{
				listenerMap.put(thisId, listInfo);
			}
		}
        return thisId;
    }


    /**
     * Stop listener.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param listenerName the listener name
     */
    public void stopListener(String listenerName) {
		Integer nId = getListenerId(listenerName);
		if (nId != Integer.MIN_VALUE)
			stopListener(nId);
    }

    /**
     * Stop listener.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param listenerId the listener id
     */
    public void stopListener(Integer listenerId) {
		boolean bRemoved = false;
		synchronized (listenerIdMap)
		{
			if (listenerIdMap.containsKey(listenerId))
			{
				listenerIdMap.remove(listenerId);
				bRemoved = true;
			}
		}
		synchronized (listenerMap)
		{
			if (listenerMap.containsKey(listenerId))
				listenerMap.remove(listenerId);
		}
        if (bRemoved) {
                    System.err.println("ListenerManager:  The Requested Listener " + listenerId + " has been stopped and removed.");
        }
    }



    /**
     * Listener exists.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param listenerName the listener name
     * @return true, if successful
     */
    public boolean listenerExists(String listenerName) {
		synchronized (listenerIdMap)
		{
			return listenerIdMap.containsValue(listenerName);
		}
    }
    
    /**
     * Listener exists.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param listenerId the listener id
     * @return true, if successful
     */
    public boolean listenerExists(Integer listenerId) {
		boolean bInIdMap = false;
		boolean bInMap = false;
		synchronized (listenerIdMap)
		{
			bInIdMap = listenerIdMap.containsKey(listenerId);
		}
		if (bInIdMap)
		{
			synchronized (listenerMap)
			{
				bInMap = listenerMap.containsKey(listenerId);
			}
		}
        return bInIdMap && bInMap;
    }
    
    /**
     * Gets the listener internal server address.
     *
     * @param listenerName the listener name
     * @return the listener internal server address
     */
    public String getListenerInternalServerAddress(String listenerName){
        String address = "";
		Integer nId = getListenerId(listenerName);
		
		if (nId != Integer.MIN_VALUE)
		{
			synchronized (listenerMap)
			{
				if (listenerMap.containsKey(nId))
					address = listenerMap.get(nId).getLocalIPAddress();
			}
		}
        
        return address;
    }

    /**
     * Gets the listener internal server address.
     *
     * @param listenerId the listener id
     * @return the listener internal server address
     */
    public String getListenerInternalServerAddress(Integer listenerId){
        String address = "";
		synchronized (listenerMap)
		{
			if (listenerMap.containsKey(listenerId)) {
				ListenerInfo theListener = listenerMap.get(listenerId);
				address = theListener.getLocalIPAddress();
			}
		}
        return address;
    }

	private Integer getListenerId(String listenerName)
	{
		Integer nId = Integer.MIN_VALUE;
		synchronized (listenerIdMap)
		{
			for (Entry<Integer, String> oEntry : listenerIdMap.entrySet())
			{
				if (oEntry.getValue().equals(listenerName))
				{
					nId = oEntry.getKey();
					break;
				}
			}
		}
		
		return nId;
	}
    /**
     * Gets the listener internal server port.
     *
     * @param listenerName the listener name
     * @return the listener internal server port
     */
    public Integer getListenerInternalServerPort(String listenerName){
        Integer port=-1;
		Integer nId = getListenerId(listenerName);
		if (nId != Integer.MIN_VALUE)
		{
			synchronized (listenerMap)
			{
				if (listenerMap.containsKey(nId))
					port = listenerMap.get(nId).getLocalPort();
			}
		}

        return port;
    }

    /**
     * Gets the listener internal server port.
     *
     * @param listenerId the listener id
     * @return the listener internal server port
     */
    public Integer getListenerInternalServerPort(Integer listenerId){
        Integer port=-1;
		synchronized (listenerMap)
		{
			if (listenerMap.containsKey(listenerId)) {
				ListenerInfo theListener = listenerMap.get(listenerId);
				port = theListener.getLocalPort();
			}
		}
        return port;
    }

    /**
     * Gets the connection name.
     *
     * @param serverAdddress the server adddress
     * @param serverPort the server port
     * @return the connection name
     */
    public String getConnectionName(String serverAdddress, int serverPort){
        String results="";
		synchronized (listenerMap)
		{
			for (Integer listenerId : listenerMap.keySet()){
				ListenerInfo theListener = listenerMap.get(listenerId);
				if (theListener.isConnectionMatch(serverAdddress, serverPort)){
					return theListener.getConnectionName();
				}
			}
		}
        return results;
    }
    
    /**
     * The Class ListenerInfo.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    class ListenerInfo {

        /** The local ip address. */
        private String localIPAddress = "Not Connected";
        
        /** The local port. */
        private int localPort = -2;
        
        /** The remote ip address. */
        private String remoteIPAddress = "Not Cconnected";
        
        /** The remote port. */
        private int remotePort = -2;
        
        /** The connection name. */
        private String connectionName = "Undefined";

        
        /**
         * Instantiates a new listener info.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param remoteIPAddress the remote ip address
         * @param remotePort the remote port
         * @param connectionName the connection name
         */
        public ListenerInfo(String remoteIPAddress, int remotePort, String connectionName){
            this.remoteIPAddress = remoteIPAddress;
            this.remotePort = remotePort;
            this.connectionName = connectionName;
        }
        
        /**
         * Instantiates a new listener info.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param connInfo the conn info
         * @param connectionName the connection name
         */
        public ListenerInfo(ConnectionInformation connInfo, String connectionName){
            this.remoteIPAddress = connInfo.getRemoteIPAddress();
            this.remotePort = connInfo.getRemotePort();
            this.localIPAddress = connInfo.getLocalIPAddress();
            this.localPort = connInfo.getLocalPort();
            this.connectionName = connInfo.getConnectionName();
            
        }
        
        /**
         * Instantiates a new listener info.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         */
        private ListenerInfo(){            
        }
        
        /**
         * Checks if is connection match.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param ipAddress the ip address
         * @param port the port
         * @return true, if is connection match
         */
        public boolean isConnectionMatch(String ipAddress, int port) {
            return remoteIPAddress.equalsIgnoreCase(ipAddress) && (remotePort == port);
        }

        /**
         * Gets the local ip address.
         *
         * @return the local ip address
         */
        public String getLocalIPAddress() {
            return localIPAddress;
        }

        /**
         * Sets the local ip address.
         *
         * @param localIPAddress the new local ip address
         */
        public void setLocalIPAddress(String localIPAddress) {
            this.localIPAddress = localIPAddress;
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
         * Gets the remote ip address.
         *
         * @return the remote ip address
         */
        public String getRemoteIPAddress() {
            return remoteIPAddress;
        }

        /**
         * Sets the remote ip address.
         *
         * @param remoteIPAddress the new remote ip address
         */
        public void setRemoteIPAddress(String remoteIPAddress) {
            this.remoteIPAddress = remoteIPAddress;
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
    }
}
