/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.transports;

import java.util.HashMap;
import java.util.Iterator;
import org.enterprisepower.net.NetUtils;
import org.enterprisepower.net.portforward.Listener;

/**
 *
 * @author TransCore ITS
 */
public class ListenerManager {

    private static ListenerManager listenerManager;
    private static String testCaseID;
    private static HashMap<Integer, Listener> listenerMap = new HashMap<Integer, Listener>();
    private static HashMap<Integer,String> listenerIdMap = new HashMap<Integer, String>();
    private static Integer listenerId=0;
	private static final Object LOCK = new Object();

    private ListenerManager() {
    }

    public static ListenerManager getInstance() {
        if (listenerManager == null) {
            listenerManager = new ListenerManager();
        }
        return listenerManager;
    }

    private Integer incrementListenerId(){
        synchronized(LOCK){
            listenerId++;
        }
        return listenerId;
    }

    public void setTestCaseID(String testCaseID) {
        this.testCaseID = testCaseID;
    }

    /**
     *
     * @param externalServerAddress 
     * @param externalServerPort 
     * @param internalServerAddress 
     * @param internalServerPort 
     * @param enableSSL - utilize SSL
     * @param connectionName
     * @throws TransportException
     */
    public Integer createServerModeListener(String externalServerAddress, Integer externalServerPort, String internalServerAddress, Integer internalServerPort, boolean enableSSL, String connectionName) throws TransportException{
        Integer thisId = -1;
        if (!listenerIdMap.containsValue(connectionName)) {

            String externalSocketAddress = externalServerAddress + ":" + externalServerPort;
            String internalSocketAddress = internalServerAddress + ":" + internalServerPort;
            try {
                Listener thisListener =
                        new Listener(NetUtils.parseInetSocketAddress(externalSocketAddress), NetUtils.parseInetSocketAddress(internalSocketAddress), this.testCaseID, connectionName, enableSSL,true);

                Thread dataTransferListenerThread = new Thread(thisListener);
                dataTransferListenerThread.setName(connectionName);
//             dataTransferListenerThread.setContextClassLoader(Thread.currentThread().getContextClassLoader());
                dataTransferListenerThread.setDaemon(true);
                dataTransferListenerThread.start();
                thisId = incrementListenerId();
                listenerIdMap.put(thisId, connectionName);
                listenerMap.put(thisId, thisListener);
            } catch (Exception ex) {
                ex.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null, "ListenerManager:createServerModeListener \nError Occurred: "+ ex.getMessage() + "\n\nInternalSocketAddress= "+internalSocketAddress+ "\n\nExternalSocketAddress= "+externalSocketAddress+"\nConnectionName= "+connectionName+"\nenableSSL= "+enableSSL, "ListenerManager", javax.swing.JOptionPane.ERROR_MESSAGE);
                throw new TransportException("ListenerManager:createClientModeListener "+ex.getMessage(),TransportException.PROTOCOL_ERROR_TYPE);
            }
        } else {
            System.err.println("ListenerManager:  Requested Listener " + connectionName + " already exists.");
            Iterator listenerIterator = listenerIdMap.keySet().iterator();
            while (listenerIterator.hasNext()){
                thisId = (Integer)listenerIterator.next();
                if (listenerIdMap.get(thisId).equals(connectionName)) break;
            }
        }
        return thisId;
    }

    /**
     *
     * @param externalServerAddress
     * @param externalServerPort
     * @param internalServerAddress
     * @param internalServerPort
     * @param enableSSL - utilize SSL
     * @param connectionName
     * @throws TransportException
     */
    public Integer createServerModeListener(String internalServerAddress, Integer internalServerPort, boolean enableSSL, String connectionName) throws TransportException{
        Integer thisId = -1;
        if (!listenerIdMap.containsValue(connectionName)) {

            String internalSocketAddress = internalServerAddress + ":" + internalServerPort;
            try {
                Listener thisListener =
                        new Listener(NetUtils.parseInetSocketAddress(internalSocketAddress), this.testCaseID, connectionName, enableSSL,true);
                Thread dataTransferListenerThread = new Thread(thisListener);
                dataTransferListenerThread.setName(connectionName);
//             dataTransferListenerThread.setContextClassLoader(Thread.currentThread().getContextClassLoader());
                dataTransferListenerThread.setDaemon(true);
                dataTransferListenerThread.start();
                thisId = incrementListenerId();
                listenerIdMap.put(thisId, connectionName);
                listenerMap.put(thisId, thisListener);
            } catch (Exception ex) {
                ex.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null, "ListenerManager:createServerModeListener \nError Occurred: "+ ex.getMessage() + "\n\nInternalSocketAddress= "+internalSocketAddress+"\nConnectionName= "+connectionName+"\nenableSSL= "+enableSSL, "ListenerManager", javax.swing.JOptionPane.ERROR_MESSAGE);
                throw new TransportException("ListenerManager:createClientModeListener "+ex.getMessage(),TransportException.PROTOCOL_ERROR_TYPE);
            }
        } else {
            System.err.println("ListenerManager:  Requested Listener " + connectionName + " already exists.");
            Iterator listenerIterator = listenerIdMap.keySet().iterator();
            while (listenerIterator.hasNext()){
                thisId = (Integer)listenerIterator.next();
                if (listenerIdMap.get(thisId).equals(connectionName)) break;
            }
        }
        return thisId;
    }


    /**
     * 
     * @param remoteServerAddress 
     * @param remoteServerPort
     * @param serverPort - the port for the server connection
     * @param enableSSL - utilize SSL
     * @param connectionName 
     */
    public Integer createClientModeListener(String remoteServerAddress, Integer remoteServerPort, boolean enableSSL, String connectionName) throws TransportException {
        Integer thisId = -1;
        if (!listenerIdMap.containsValue(connectionName)) {

            String externalSocketAddress = remoteServerAddress + ":" + remoteServerPort;
            try {
                Listener thisListener =
                        new Listener(NetUtils.parseInetSocketAddress(externalSocketAddress), this.testCaseID, connectionName, enableSSL,false);

                Thread dataTransferListenerThread = new Thread(thisListener);
//             dataTransferListenerThread.setContextClassLoader(Thread.currentThread().getContextClassLoader());
                dataTransferListenerThread.setName(connectionName);
                dataTransferListenerThread.setDaemon(true);
                dataTransferListenerThread.start();
                thisId = incrementListenerId();
                listenerIdMap.put(thisId, connectionName);
                listenerMap.put(thisId, thisListener);
            } catch (Exception ex) {
                ex.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null, "ListenerManager:createClientModeListener \nError Occurred: "+ ex.getMessage() + "\n\nExternalSocketAddress= "+externalSocketAddress+"\nConnectionName= "+connectionName+"\nenableSSL= "+enableSSL, "ListenerManager", javax.swing.JOptionPane.ERROR_MESSAGE);
                throw new TransportException("ListenerManager:createClientModeListener "+ex.getMessage(),TransportException.PROTOCOL_ERROR_TYPE);
            }
        } else {
            System.err.println("ListenerManager:  Requested Listener " + connectionName + " already exists.");
            Iterator listenerIterator = listenerIdMap.keySet().iterator();
            while (listenerIterator.hasNext()){
                thisId = (Integer)listenerIterator.next();
                if (listenerIdMap.get(thisId).equals(connectionName)) break;
            }
        }
        return thisId;
    }


    public void stopListener(String listenerName) {
        if (listenerIdMap.containsValue(listenerName)) {
            Iterator idIterator = listenerIdMap.keySet().iterator();
            while (idIterator.hasNext()){
                Integer thisId = (Integer)idIterator.next();
                if (listenerIdMap.get(thisId).equals(listenerName)){
                    Listener theListener = listenerMap.get(thisId);
                    theListener.close();
                    listenerMap.remove(thisId);
                    listenerIdMap.remove(thisId);
                    System.err.println("ListenerManager:  The Requested Listener " + listenerName + " has been stopped and removed.");
                    break;
                }
            }
        }
    }

    public void stopListener(Integer listenerId) {
        if (listenerIdMap.containsKey(listenerId)) {
                    Listener theListener = listenerMap.get(listenerId);
                    theListener.close();
                    listenerMap.remove(listenerId);
                    listenerIdMap.remove(listenerId);
                    System.err.println("ListenerManager:  The Requested Listener " + listenerId + " has been stopped and removed.");
        }
    }



    public boolean listenerExists(String listenerName) {
        return listenerIdMap.containsValue(listenerName);
    }
    
    public boolean listenerExists(Integer listenerId) {
        return listenerIdMap.containsKey(listenerId)&&listenerMap.containsKey(listenerId);
    }
    
    public String getListenerInternalServerAddress(String listenerName){
        String address = "";
        if (listenerIdMap.containsValue(listenerName)) {
            Iterator idIterator = listenerIdMap.keySet().iterator();
            while (idIterator.hasNext()){
                Integer thisId = (Integer)idIterator.next();
                if (listenerIdMap.get(thisId).equals(listenerName)){
                    Listener theListener = listenerMap.get(thisId);
                    address = theListener.getInternalServerAddress().getAddress().getHostAddress();
                    break;
                }
            }
        }        
        return address;
    }

    public String getListenerInternalServerAddress(Integer listenerId){
        String address = "";
        if (listenerMap.containsKey(listenerId)) {
            Listener theListener = listenerMap.get(listenerId);
            address = theListener.getInternalServerAddress().getAddress().getHostAddress();
        }
        return address;
    }

    public Integer getListenerInternalServerPort(String listenerName){
        Integer port=-1;
        if (listenerIdMap.containsValue(listenerName)) {
            Iterator idIterator = listenerIdMap.keySet().iterator();
            while (idIterator.hasNext()){
                Integer thisId = (Integer)idIterator.next();
                if (listenerIdMap.get(thisId).equals(listenerName)){
                    Listener theListener = listenerMap.get(thisId);
                    port = theListener.getInternalServerAddress().getPort();
                    break;
                }
            }
        }
        return port;
    }

    public Integer getListenerInternalServerPort(Integer listenerId){
        Integer port=-1;
        if (listenerMap.containsKey(listenerId)) {
            Listener theListener = listenerMap.get(listenerId);
            port = theListener.getInternalServerAddress().getPort();
        }
        return port;
    }

}
