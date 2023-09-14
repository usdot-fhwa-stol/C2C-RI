/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.enterprisepower.net.portforward;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

/**
 * 
 * @author Kenneth Xu
 * 
 */
public class Listener implements Runnable {
//	private static Log log = LogFactory.getLog(Listener.class);

    private static Log log = LogFactory.getLog("net.sf.jameleon");
    private ServerSocket serverSocket;
    private InetSocketAddress from, to;
    private Throwable exception;
    private Cleaner cleaner = new Cleaner();
    private String testCase;
    private String connectionName;
    private boolean enableSSL;
    private boolean serverConnection;
    private ArrayList<SocketAssignmentListener> addressListeners = new ArrayList<>();
	private boolean running = true;

    public Throwable getException() {
        return exception;
    }

    public void registerAddressListener(SocketAssignmentListener theListener){
        if(!addressListeners.contains(theListener)){
            addressListeners.add(theListener);
        }
    }
    
    public void unRegisterAddressListener(SocketAssignmentListener theListener){
        if(addressListeners.contains(theListener)){
            addressListeners.remove(theListener);
        }        
    }
    
    private void notifyListeners(String internalAddress, String externalAddress){
        for (SocketAssignmentListener thisListener : addressListeners){
            if (thisListener != null){
                thisListener.addInternalAddressMapping(internalAddress, externalAddress);
            }
        }
    }
    
    public Listener(InetSocketAddress from, InetSocketAddress to, String testCase, String connectionName)
            throws IOException {
        this.testCase = testCase;
        this.connectionName = connectionName;

        this.from = from;
        this.to = to;
        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(from);
        String hostname = from.getHostName();
        if (hostname == null) {
            hostname = "*";
        }
        log.info("Ready to accept client connection on " + hostname + ":"
                + from.getPort());
    }

    public Listener(InetSocketAddress from, InetSocketAddress to, String testCase, String connectionName, boolean enableSSL, boolean serverConnection)
            throws IOException {
        this.testCase = testCase;
        this.connectionName = connectionName;
        this.serverConnection = serverConnection;
        this.from = from;
        this.to = to;
        this.enableSSL = enableSSL;
        if (enableSSL) {
            ServerSocketFactory ssocketFactory = SSLServerSocketFactory.getDefault();
            serverSocket = ssocketFactory.createServerSocket();
        } else {
            serverSocket = new ServerSocket();
        }
        serverSocket.setReuseAddress(true);
        serverSocket.bind(from);
        String hostname = from.getHostName();
        if (hostname == null) {
            hostname = "*";
        }
        log.info("Ready to accept client connection on " + hostname + ":"
                + from.getPort());
    }

    public Listener(InetSocketAddress to, String testCase, String connectionName, boolean enableSSL, boolean serverConnection)
            throws IOException {
        this.testCase = testCase;
        this.connectionName = connectionName;
        this.serverConnection = serverConnection;

        this.to = to;
        this.enableSSL = enableSSL;
        if (enableSSL) {
            ServerSocketFactory ssocketFactory = SSLServerSocketFactory.getDefault();
            serverSocket = ssocketFactory.createServerSocket();
        } else {
            serverSocket = new ServerSocket();
        }
        serverSocket.setReuseAddress(true);
        InetSocketAddress theResult = new InetSocketAddress(InetAddress.getLocalHost(),0);
        System.out.println("Listener:: Trying to bind to Address ->"+theResult.toString());
        serverSocket.bind(theResult); // Let the system pick the address
        //        if (serverIsRemote){
        this.from = new InetSocketAddress(serverSocket.getInetAddress(), serverSocket.getLocalPort());
        //        } else {
        //        }

        String hostname = from.getHostName();
        if (hostname == null) {
            hostname = "*";
        }
        log.info("Ready to accept client connection on " + hostname + ":"
                + from.getPort());
    }

    /**
     *
     * @return The internal address to use for the server address
     */
    public InetSocketAddress getInternalServerAddress() {
        InetSocketAddress returnAddress = new InetSocketAddress(serverSocket.getInetAddress(), serverSocket.getLocalPort());
        return returnAddress;
    }
    
    
    public void run() {
        Socket source = null;
        new Thread(cleaner).start();
        while (running) {
            try {
                TargetConnector connector = new TargetConnector(to);
                source = serverSocket.accept();
                log.trace("accepted client connection");
                Socket target = connector.openSocket(enableSSL);
                if (serverConnection){
                    
                    notifyListeners(target.getLocalAddress().getHostAddress()+":"+target.getLocalPort(),
                                    source.getLocalAddress().getHostAddress()+":"+source.getLocalPort());
                    notifyListeners(target.getInetAddress().getHostAddress()+":"+target.getPort(),
                                    source.getInetAddress().getHostAddress()+":"+source.getPort());
                    new Processor(source, target, from, cleaner, serverConnection, testCase, connectionName).process();
                } else {
                    notifyListeners(source.getLocalAddress().getHostAddress()+":"+source.getLocalPort(),
                                    target.getLocalAddress().getHostAddress()+":"+target.getLocalPort());
                    notifyListeners(source.getInetAddress().getHostAddress()+":"+source.getPort(),
                                    target.getInetAddress().getHostAddress()+":"+target.getPort());
                    new Processor(source, target, new InetSocketAddress(target.getLocalAddress(), target.getLocalPort()), cleaner, serverConnection, testCase, connectionName).process();
                }
            } catch (IOException e) {
                String msg = connectionName + " Listener: Failed to accept a client connection on port "
                        + from.getPort() + "because of :" + e.getMessage();
                log.error(msg, e);
                exception = e;
                return;
            }
        }
    }
	
    public void close() {
        if (!serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                log.error("Listener.close:  " + e.getMessage(), e);
            }
        }
    }
    
	
		
	public void stopRun()
	{
		running = false;
	}
}
