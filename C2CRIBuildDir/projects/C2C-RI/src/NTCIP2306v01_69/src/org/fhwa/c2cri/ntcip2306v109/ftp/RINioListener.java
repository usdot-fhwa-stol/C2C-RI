/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.ftp;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.FtpServerConfigurationException;
import org.apache.ftpserver.impl.FtpHandler;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.ipfilter.IpFilter;
import org.apache.ftpserver.ipfilter.MinaIpFilter;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.listener.nio.AbstractListener;
import org.apache.ftpserver.listener.nio.FtpHandlerAdapter;
import org.apache.ftpserver.listener.nio.FtpLoggingFilter;
import org.apache.ftpserver.listener.nio.FtpServerProtocolCodecFactory;
import org.apache.ftpserver.ssl.ClientAuth;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.firewall.Subnet;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.fhwa.c2cri.applayer.ListenerManager;
import org.fhwa.c2cri.applayer.TransportException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * <strong>Internal class, do not use directly.</strong>
 *
 * The default {@link Listener} implementation.
 * 
 * Modified by the RI Project to get addresses and ports for logging.
 * Last Updated: 10/3/2013
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class RINioListener extends AbstractListener {

    private final Logger LOG = LogManager.getLogger(RINioListener.class);

    private SocketAcceptor acceptor;

    private InetSocketAddress address;

    boolean suspended = false;

    private FtpHandler handler = new RIFtpHandler();  // Here's the change to this file.

    private FtpServerContext context;

    private String connectionName="RINioListener";

    /**
     * @deprecated Use the constructor with IpFilter instead.
     * Constructor for internal use, do not use directly. Instead use {@link ListenerFactory}
     */
    @Deprecated
    public RINioListener(String serverAddress, int port,
            boolean implicitSsl,
            SslConfiguration sslConfiguration,
            DataConnectionConfiguration dataConnectionConfig,
            int idleTimeout, List<InetAddress> blockedAddresses, List<Subnet> blockedSubnets) {
        super(serverAddress, port, implicitSsl, sslConfiguration, dataConnectionConfig,
                idleTimeout, blockedAddresses, blockedSubnets);
    }

    /**
     * Constructor for internal use, do not use directly. Instead use {@link ListenerFactory}
     */
    public RINioListener(String serverAddress, int port,
            boolean implicitSsl,
            SslConfiguration sslConfiguration,
            DataConnectionConfiguration dataConnectionConfig,
            int idleTimeout, IpFilter ipFilter) {
        super(serverAddress, port, implicitSsl, sslConfiguration, dataConnectionConfig,
                idleTimeout, ipFilter);
    }

    /**
     * Constructor for internal use, do not use directly. Instead use {@link ListenerFactory}
     */
    public RINioListener(String serverAddress, int port,
            boolean implicitSsl,
            SslConfiguration sslConfiguration,
            DataConnectionConfiguration dataConnectionConfig,
            int idleTimeout, IpFilter ipFilter, String connectionName) {
        super(serverAddress, port, implicitSsl, sslConfiguration, dataConnectionConfig,
                idleTimeout, ipFilter);
    }

    /**
     * Constructor for internal use, do not use directly. Instead use {@link ListenerFactory}
     */
    public RINioListener(String serverAddress, int port,
            boolean implicitSsl,
            SslConfiguration sslConfiguration,
            DataConnectionConfiguration dataConnectionConfig,
            int idleTimeout, IpFilter ipFilter, String connectionName, RIFtpHandler handler) {
        super(serverAddress, port, implicitSsl, sslConfiguration, dataConnectionConfig,
                idleTimeout, ipFilter);
        this.handler = handler;
    }

    /**
     * @see Listener#start(FtpServerContext)
     */
    public synchronized void start(FtpServerContext context) {
        if(!isStopped()) {
            // listener already started, don't allow
            throw new IllegalStateException("Listener already started");
        }

        try {

            this.context = context;

            acceptor = new NioSocketAcceptor(Runtime.getRuntime()
                    .availableProcessors());

            if (getServerAddress() != null) {
                address = new InetSocketAddress(getServerAddress(), getPort());
            } else {
                address = new InetSocketAddress(getPort());
            }

            acceptor.setReuseAddress(true);
            acceptor.getSessionConfig().setReadBufferSize(2048);
            acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,
                    getIdleTimeout());
            // Decrease the default receiver buffer size
            ((SocketSessionConfig) acceptor.getSessionConfig())
                    .setReceiveBufferSize(512);

            MdcInjectionFilter mdcFilter = new MdcInjectionFilter();

            acceptor.getFilterChain().addLast("mdcFilter", mdcFilter);

            IpFilter ipFilter = getIpFilter();
            if(ipFilter != null) {
            // 	add and IP filter to the filter chain.
            	acceptor.getFilterChain().addLast("ipFilter", new MinaIpFilter(ipFilter));
            }

            acceptor.getFilterChain().addLast("threadPool",
                    new ExecutorFilter(context.getThreadPoolExecutor()));
            acceptor.getFilterChain().addLast("codec",
                    new ProtocolCodecFilter(new FtpServerProtocolCodecFactory()));
            acceptor.getFilterChain().addLast("mdcFilter2", mdcFilter);
            acceptor.getFilterChain().addLast("logger", new FtpLoggingFilter());

            if (isImplicitSsl()) {
                SslConfiguration ssl = getSslConfiguration();
                SslFilter sslFilter;
                try {
                    sslFilter = new SslFilter(ssl.getSSLContext());
                } catch (GeneralSecurityException e) {
                    throw new FtpServerConfigurationException("SSL could not be initialized, check configuration");
                }

                if (ssl.getClientAuth() == ClientAuth.NEED) {
                    sslFilter.setNeedClientAuth(true);
                } else if (ssl.getClientAuth() == ClientAuth.WANT) {
                    sslFilter.setWantClientAuth(true);
                }

                if (ssl.getEnabledCipherSuites() != null) {
                    sslFilter.setEnabledCipherSuites(ssl.getEnabledCipherSuites());
                }

                acceptor.getFilterChain().addFirst("sslFilter", sslFilter);
            }

            handler.init(context, this);
            acceptor.setHandler(new FtpHandlerAdapter(context, handler));


            try {
                acceptor.bind(address);

                ListenerManager.getInstance().createServerModeListener(address.getAddress().getHostAddress(),
                                            address.getPort(), acceptor.getLocalAddress().getAddress().getHostAddress(),
                                            acceptor.getLocalAddress().getPort(), isImplicitSsl(), getConnectionName());
            } catch (IOException e) {
                throw new FtpServerConfigurationException("Failed to bind to address " + address + ", check configuration", e);
            } catch (TransportException te){
                throw new FtpServerConfigurationException("Failed to createServerModeListener from address" + address + ", to address ", te);
            }

            updatePort();

        } catch(RuntimeException e) {
            // clean up if we fail to start
            stop();

            throw e;
        }
    }

    private void updatePort() {
        // update the port to the real port bound by the listener
        setPort(acceptor.getLocalAddress().getPort());
    }

    /**
     * @see Listener#stop()
     */
    public synchronized void stop() {
        // close server socket
        if (acceptor != null) {
            acceptor.unbind();
            acceptor.dispose();
            acceptor = null;
        }
        if (ListenerManager.getInstance().listenerExists(getConnectionName())){
            ListenerManager.getInstance().stopListener(getConnectionName());
        }
        context = null;
    }

    /**
     * @see Listener#isStopped()
     */
    public boolean isStopped() {
        return acceptor == null;
    }

    /**
     * @see Listener#isSuspended()
     */
    public boolean isSuspended() {
        return suspended;

    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }



    /**
     * @see Listener#resume()
     */
    public synchronized void resume() {
        if (acceptor != null && suspended) {
            try {
                LOG.debug("Resuming listener");
                acceptor.bind(address);
                LOG.debug("Listener resumed");

                updatePort();

                suspended = false;
            } catch (IOException e) {
                LOG.error("Failed to resume listener", e);
            }
        }
    }

    /**
     * @see Listener#suspend()
     */
    public synchronized void suspend() {
        if (acceptor != null && !suspended) {
            LOG.debug("Suspending listener");
            acceptor.unbind();

            suspended = true;
            LOG.debug("Listener suspended");
        }
    }

    /**
     * @see Listener#getActiveSessions()
     */
    public synchronized Set<FtpIoSession> getActiveSessions() {
        Map<Long, IoSession> sessions = acceptor.getManagedSessions();

        Set<FtpIoSession> ftpSessions = new HashSet<FtpIoSession>();
        for (IoSession session : sessions.values()) {
            ftpSessions.add(new FtpIoSession(session, context));
        }
        return ftpSessions;
    }
}
