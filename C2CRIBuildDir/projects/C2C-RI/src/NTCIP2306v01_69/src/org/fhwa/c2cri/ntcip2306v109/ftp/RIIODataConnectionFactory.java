/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.ftp;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.DataConnectionException;
import org.apache.ftpserver.ftplet.DataConnection;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.ServerDataConnectionFactory;
import org.apache.ftpserver.ssl.ClientAuth;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.fhwa.c2cri.applayer.ListenerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * A factory for creating RIIODataConnection objects.
 * 
 * @author TransCore ITS, LLC
 * Last Updated: 8/1/2012
 */
public class RIIODataConnectionFactory implements ServerDataConnectionFactory{

    /** The log. */
    private final Logger LOG = LoggerFactory
            .getLogger(RIIODataConnectionFactory.class);

    /** The server context. */
    private FtpServerContext serverContext;

    /** The data soc. */
    private Socket dataSoc;

    /** The serv soc. */
    ServerSocket servSoc;

    /** The address. */
    InetAddress address;

    /** The port. */
    int port = 0;

    /** The external address. */
    InetAddress externalAddress;

    /** The external port. */
    int externalPort = 0;

    /** The request time. */
    long requestTime = 0L;

    /** The passive. */
    boolean passive = false;

    /** The secure. */
    boolean secure = false;

    /** The is zip. */
    private boolean isZip = false;

    /** The server control address. */
    InetAddress serverControlAddress;

    /** The session. */
    FtpIoSession session;

    /**
     * Instantiates a new rIIO data connection factory.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param serverContext the server context
     * @param session the session
     */
    public RIIODataConnectionFactory(final FtpServerContext serverContext,
            final FtpIoSession session) {
        this.session = session;
        this.serverContext = serverContext;
        if (session.getListener().getDataConnectionConfiguration()
                .isImplicitSsl()) {
            secure = true;
        }
    }

    /**
     * Close data socket.
     * This method must be idempotent as we might call it multiple times during disconnect.
     */
    public synchronized void closeDataConnection() {

        // close client socket if any
        if (dataSoc != null) {
            try {
                dataSoc.close();
            } catch (Exception ex) {
                LOG.warn("FtpDataConnection.closeDataSocket()", ex);
            }
            dataSoc = null;

            if (ListenerManager.getInstance().listenerExists("FTP DATA CONNECTION ACTIVE")){
              ListenerManager.getInstance().stopListener("FTP DATA CONNECTION ACTIVE");
            }
        }

        // close server socket if any
        if (servSoc != null) {
            try {
                servSoc.close();
            } catch (Exception ex) {
                LOG.warn("FtpDataConnection.closeDataSocket()", ex);
            }

            if (session != null) {
                DataConnectionConfiguration dcc = session.getListener()
                        .getDataConnectionConfiguration();
                if (dcc != null) {
                    dcc.releasePassivePort(port);
                }
            }

            servSoc = null;

            if (ListenerManager.getInstance().listenerExists("FTP DATA CONNECTION PASSIVE")){
               ListenerManager.getInstance().stopListener("FTP DATA CONNECTION PASSIVE");
            }

        }


        // reset request time
        requestTime = 0L;
    }

    /**
     * Port command.
     *
     * @param address the address
     */
    public synchronized void initActiveDataConnection(
            final InetSocketAddress address) {

        // close old sockets if any
        closeDataConnection();

        if (ListenerManager.getInstance().listenerExists("FTP DATA CONNECTION ACTIVE")){
           ListenerManager.getInstance().stopListener("FTP DATA CONNECTION ACTIVE");
        }

        // set variables
        passive = false;
        this.externalAddress = address.getAddress();
        this.externalPort = address.getPort();
        this.address = address.getAddress();
        port = address.getPort();
        requestTime = System.currentTimeMillis();
    }

    /**
     * Gets the ssl configuration.
     *
     * @return the ssl configuration
     */
    private SslConfiguration getSslConfiguration() {
        DataConnectionConfiguration dataCfg = session.getListener()
                .getDataConnectionConfiguration();

        SslConfiguration configuration = dataCfg.getSslConfiguration();

        // fall back if no configuration has been provided on the data connection config
        if (configuration == null) {
            configuration = session.getListener().getSslConfiguration();
        }

        return configuration;
    }

    /**
     * Initiate a data connection in passive mode (server listening).
     *
     * @return the inet socket address
     * @throws DataConnectionException the data connection exception
     */
    public synchronized InetSocketAddress initPassiveDataConnection()
            throws DataConnectionException {
        LOG.debug("Initiating passive data connection");
        // close old sockets if any
        closeDataConnection();

        if (ListenerManager.getInstance().listenerExists("FTP DATA CONNECTION PASSIVE")){
           ListenerManager.getInstance().stopListener("FTP DATA CONNECTION PASSIVE");
        }
        // get the passive port
        int passivePort = session.getListener()
                .getDataConnectionConfiguration().requestPassivePort();
        if (passivePort == -1) {
            servSoc = null;
            throw new DataConnectionException(
                    "Cannot find an available passive port.");
        }

        // open passive server socket and get parameters
        try {
            DataConnectionConfiguration dataCfg = session.getListener()
                    .getDataConnectionConfiguration();

            String passiveAddress = dataCfg.getPassiveAddress();

            if (passiveAddress == null) {
                externalAddress = serverControlAddress;
                address = serverControlAddress;
            } else {
                externalAddress = resolveAddress(dataCfg.getPassiveAddress());
                address = resolveAddress(dataCfg.getPassiveAddress());
            }
			if (address == null)
				throw new DataConnectionException("Null address");
            if (secure) {
                LOG
                        .debug(
                                "Opening SSL passive data connection on address \"{}\" and port {}",
                                address, passivePort);
                SslConfiguration ssl = getSslConfiguration();
                if (ssl == null) {
                    throw new DataConnectionException(
                            "Data connection SSL required but not configured.");
                }

                // this method does not actually create the SSL socket, due to a JVM bug
                // (https://issues.apache.org/jira/browse/FTPSERVER-241).
                // Instead, it creates a regular
                // ServerSocket that will be wrapped as a SSL socket in createDataSocket()
                servSoc = new ServerSocket(passivePort, 0, address);
				
                ListenerManager.getInstance().createServerModeListener(address.getHostAddress(),
                                            passivePort, address.getHostAddress(),
                                            passivePort, secure, "FTP DATA CONNECTION PASSIVE");

                LOG
                        .debug(
                                "SSL Passive data connection created on address \"{}\" and port {}",
                                address, passivePort);
            } else {
                LOG
                        .debug(
                                "Opening passive data connection on address \"{}\" and port {}",
                                address, passivePort);
                servSoc = new ServerSocket(passivePort, 0, address);

                ListenerManager.getInstance().createServerModeListener(address.getHostAddress(),
                                            passivePort, address.getHostAddress(),
                                            passivePort, secure, "FTP DATA CONNECTION PASSIVE");
                LOG
                        .debug(
                                "Passive data connection created on address \"{}\" and port {}",
                                address, passivePort);
            }
            port = servSoc.getLocalPort();
            servSoc.setSoTimeout(dataCfg.getIdleTime() * 1000);

            // set different state variables
            passive = true;
            requestTime = System.currentTimeMillis();

            return new InetSocketAddress(address, port);
        } catch (Exception ex) {
            servSoc = null;
            closeDataConnection();
            throw new DataConnectionException(
                    "Failed to initate passive data connection: "
                            + ex.getMessage(), ex);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.ftpserver.FtpDataConnectionFactory2#getInetAddress()
     */
    /**
     * Gets the inet address.
     *
     * @return the inet address
     */
    public InetAddress getInetAddress() {
        return address;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.ftpserver.FtpDataConnectionFactory2#getPort()
     */
    /**
     * Gets the port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.ftpserver.FtpDataConnectionFactory2#openConnection()
     */
    /**
     * Open connection.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the data connection
     * @throws Exception the exception
     */
    public DataConnection openConnection() throws Exception {
        return new RIIODataConnection(createDataSocket(), session, this);
    }

    /**
     * Get the data socket. In case of error returns null.
     *
     * @return the socket
     * @throws Exception the exception
     */
    private synchronized Socket createDataSocket() throws Exception {

        // get socket depending on the selection
        dataSoc = null;
        DataConnectionConfiguration dataConfig = session.getListener()
                .getDataConnectionConfiguration();
        try {
            if (!passive) {
                if (secure) {
                    LOG.debug("Opening secure active data connection");
                    SslConfiguration ssl = getSslConfiguration();
                    if (ssl == null) {
                        throw new FtpException(
                                "Data connection SSL not configured");
                    }

                    // get socket factory
                    SSLContext ctx = ssl.getSSLContext();
                    SSLSocketFactory socFactory = ctx.getSocketFactory();

                    // create socket
                    SSLSocket ssoc = (SSLSocket) socFactory.createSocket();
                    ssoc.setUseClientMode(false);

                    // initialize socket
                    if (ssl.getEnabledCipherSuites() != null) {
                        ssoc.setEnabledCipherSuites(ssl.getEnabledCipherSuites());
                    }
                    dataSoc = ssoc;

                } else {
                    LOG.debug("Opening active data connection");
                    dataSoc = new Socket();

                }

                dataSoc.setReuseAddress(true);

                InetAddress localAddr = resolveAddress(dataConfig
                        .getActiveLocalAddress());

                // if no local address has been configured, make sure we use the same as the client connects from
                if(localAddr == null) {
                    localAddr = ((InetSocketAddress)session.getLocalAddress()).getAddress();
                }

                SocketAddress localSocketAddress = new InetSocketAddress(localAddr, dataConfig.getActiveLocalPort());

                LOG.debug("Binding active data connection to {}", localSocketAddress);

                dataSoc.bind(null);
                ListenerManager.getInstance().createClientModeListener(address.getHostAddress(),
                                            port, secure, "FTP DATA CONNECTION ACTIVE");

                dataSoc.connect(new InetSocketAddress(address, port));

            } else {

                if (secure) {
                    LOG.debug("Opening secure passive data connection");
                    // this is where we wrap the unsecured socket as a SSLSocket. This is
                    // due to the JVM bug described in FTPSERVER-241.

                    // get server socket factory
                    SslConfiguration ssl = getSslConfiguration();

                    // we've already checked this, but let's do it again
                    if (ssl == null) {
                        throw new FtpException(
                                "Data connection SSL not configured");
                    }

                    SSLContext ctx = ssl.getSSLContext();
                    SSLSocketFactory ssocketFactory = ctx.getSocketFactory();

                    Socket serverSocket = servSoc.accept();

                    SSLSocket sslSocket = (SSLSocket) ssocketFactory
                            .createSocket(serverSocket, serverSocket
                                    .getInetAddress().getHostName(),
                                    serverSocket.getPort(), true);
                    sslSocket.setUseClientMode(false);

                    // initialize server socket
                    if (ssl.getClientAuth() == ClientAuth.NEED) {
                        sslSocket.setNeedClientAuth(true);
                    } else if (ssl.getClientAuth() == ClientAuth.WANT) {
                        sslSocket.setWantClientAuth(true);
                    }

                    if (ssl.getEnabledCipherSuites() != null) {
                        sslSocket.setEnabledCipherSuites(ssl
                                .getEnabledCipherSuites());
                    }

                    dataSoc = sslSocket;
                } else {
                    LOG.debug("Opening passive data connection");

                    dataSoc = servSoc.accept();
                }
                DataConnectionConfiguration dataCfg = session.getListener()
                    .getDataConnectionConfiguration();

                dataSoc.setSoTimeout(dataCfg.getIdleTime() * 1000);
                LOG.debug("Passive data connection opened");
            }
        } catch (Exception ex) {
            closeDataConnection();
            LOG.warn("FtpDataConnection.getDataSocket()", ex);
            throw ex;
        }
        dataSoc.setSoTimeout(dataConfig.getIdleTime() * 1000);

        // Make sure we initiate the SSL handshake, or we'll
        // get an error if we turn out not to send any data
        // e.g. during the listing of an empty directory
        if (dataSoc instanceof SSLSocket) {
            ((SSLSocket) dataSoc).startHandshake();
        }

        return dataSoc;
    }

    /*
     *  (non-Javadoc)
     *   Returns an InetAddress object from a hostname or IP address.
     */
    /**
     * Resolve address.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param host the host
     * @return the inet address
     * @throws DataConnectionException the data connection exception
     */
    private InetAddress resolveAddress(String host)
            throws DataConnectionException {
        if (host == null) {
            return null;
        } else {
            try {
                return InetAddress.getByName(host);
            } catch (UnknownHostException ex) {
                throw new DataConnectionException("Failed to resolve address", ex);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.ftpserver.DataConnectionFactory#isSecure()
     */
    /**
     * Checks if is secure.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is secure
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * Set the security protocol.
     *
     * @param secure the new secure
     */
    public void setSecure(final boolean secure) {
        this.secure = secure;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.ftpserver.DataConnectionFactory#isZipMode()
     */
    /**
     * Checks if is zip mode.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is zip mode
     */
    public boolean isZipMode() {
        return isZip;
    }

    /**
     * Set zip mode.
     *
     * @param zip the new zip mode
     */
    public void setZipMode(final boolean zip) {
        isZip = zip;
    }

    /**
     * Check the data connection idle status.
     *
     * @param currTime the curr time
     * @return true, if is timeout
     */
    public synchronized boolean isTimeout(final long currTime) {

        // data connection not requested - not a timeout
        if (requestTime == 0L) {
            return false;
        }

        // data connection active - not a timeout
        if (dataSoc != null) {
            return false;
        }

        // no idle time limit - not a timeout
        int maxIdleTime = session.getListener()
                .getDataConnectionConfiguration().getIdleTime() * 1000;
        if (maxIdleTime == 0) {
            return false;
        }

        // idle time is within limit - not a timeout
        if ((currTime - requestTime) < maxIdleTime) {
            return false;
        }

        return true;
    }

    /**
     * Dispose data connection - close all the sockets.
     */
    public void dispose() {
        closeDataConnection();
    }

    /**
     * Sets the server's control address.
     *
     * @param serverControlAddress the new server control address
     */
    public void setServerControlAddress(final InetAddress serverControlAddress) {
        this.serverControlAddress = serverControlAddress;
    }
}
