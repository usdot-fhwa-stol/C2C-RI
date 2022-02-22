/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.http;


/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.http.annotation.Immutable;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.fhwa.c2cri.applayer.ListenerManager;
import org.fhwa.c2cri.applayer.TransportException;

/**
 * The default class for creating plain (unencrypted) sockets.
 * <p>
 * The following parameters can be used to customize the behavior of this
 * class:
 * <ul>
 *  <li>{@link org.apache.http.params.CoreConnectionPNames#CONNECTION_TIMEOUT}</li>
 * </ul>
 *
 * @since 4.0
 */
@Immutable
public final class NTCIP2306SocketFactory implements SocketFactory {

    /**
     * The default factory.
     */
    private static final
        NTCIP2306SocketFactory DEFAULT_FACTORY = new NTCIP2306SocketFactory();

    private final HostNameResolver nameResolver;
    private String remoteAdd;
    private String localAdd;
    private boolean secureMode=false;
    private String listenerName="No Name Provided";
    private Integer listenerId=-1;

    /**
     * Gets the default factory. Usually there should be no reason for creating
     * multiple instances of this class.
     *
     * @return the default factory
     */
    public static NTCIP2306SocketFactory getSocketFactory() {
        return DEFAULT_FACTORY;
    }

    public NTCIP2306SocketFactory(final HostNameResolver nameResolver) {
        super();
        this.nameResolver = nameResolver;
    }


    public NTCIP2306SocketFactory() {
        this(null);
    }

    public Socket createSocket() {
        return new Socket();
    }

    public Socket connectSocket(Socket sock, String host, int port,
                                InetAddress localAddress, int localPort,
                                HttpParams params)
        throws IOException {

        if (host == null) {
            throw new IllegalArgumentException("Target host may not be null.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null.");
        }

        if (sock == null)
            sock = createSocket();

        if ((localAddress != null) || (localPort > 0)) {

            // we need to bind explicitly
            if (localPort < 0)
                localPort = 0; // indicates "any"

            InetSocketAddress isa =
                new InetSocketAddress(localAddress, localPort);
            sock.bind(isa);
            System.out.println("TrialSocketFactory::connectSocket FYI socket bound to INET:"+sock.getInetAddress()+" Local: "+sock.getLocalAddress());

        }

        int timeout = HttpConnectionParams.getConnectionTimeout(params);

        InetSocketAddress remoteAddress;
        if (this.nameResolver != null) {
            remoteAddress = new InetSocketAddress(this.nameResolver.resolve(host), port);
        } else {
            remoteAddress = new InetSocketAddress(host, port);
        }


        try {
            this.listenerId = ListenerManager.getInstance().createClientModeListener(remoteAddress.getAddress().getHostAddress(),
                    remoteAddress.getPort(),isSecureMode(), getListenerName());
            InetSocketAddress clientAddress;
            clientAddress = new InetSocketAddress(ListenerManager.getInstance().getListenerInternalServerAddress(listenerId),
                    ListenerManager.getInstance().getListenerInternalServerPort(listenerId));

            sock.connect(remoteAddress, timeout);

            this.localAdd = sock.getLocalAddress().getHostAddress()+":"+sock.getLocalPort();
            System.out.println("TrialSocketFactory::connectSocket localAdd set to "+localAdd);

            this.remoteAdd = sock.getInetAddress().getHostAddress()+":"+String.valueOf(sock.getPort());
            System.out.println("TrialSocketFactory::connectSocket remoteAdd set to "+remoteAdd);
        } catch (SocketTimeoutException ex) {
            throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
        } catch (TransportException tex){
            throw new IOException("TrialSocketFactory could not create a listener."+tex);
        }
        return sock;
    }

    /**
     * Checks whether a socket connection is secure.
     * This factory creates plain socket connections
     * which are not considered secure.
     *
     * @param sock      the connected socket
     *
     * @return  <code>false</code>
     *
     * @throws IllegalArgumentException if the argument is invalid
     */
    public final boolean isSecure(Socket sock)
        throws IllegalArgumentException {

        if (sock == null) {
            throw new IllegalArgumentException("Socket may not be null.");
        }
        // This check is performed last since it calls a method implemented
        // by the argument object. getClass() is final in java.lang.Object.
        if (sock.isClosed()) {
            throw new IllegalArgumentException("Socket is closed.");
        }
        return false;
    }

    public String getLocalAddress(){
          return localAdd != null? localAdd:"NA";
    }
    public String getLocalPort(){
          return localAdd != null? localAdd:"";
    }
    public String getRemoteAddress(){
          return remoteAdd != null? remoteAdd:"NA";
    }
    public String getRemotePort(){
          return remoteAdd != null? remoteAdd:"";
    }
    public void setSecureMode(boolean secureTransmission){
        this.secureMode = secureTransmission;
    }
    private boolean isSecureMode(){
        return this.secureMode;
    }

    public void setListenerName(String listenerName){
        this.listenerName = listenerName;
    }

    private String getListenerName(){
        return this.listenerName;
    }

    public Integer getListenerId(){
        return this.listenerId;
    }
}
