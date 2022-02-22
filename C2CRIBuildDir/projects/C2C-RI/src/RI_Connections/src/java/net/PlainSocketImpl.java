/*
 * Copyright (c) 2007, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package java.net;

import java.io.IOException;
import java.io.FileDescriptor;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import sun.security.action.GetPropertyAction;
import jdk.internal.misc.SharedSecrets;
import jdk.internal.misc.JavaIOFileDescriptorAccess;
import org.fhwa.c2cri.java.net.ConnectionInformation;
import org.fhwa.c2cri.java.net.ConnectionsDirectory;
import org.fhwa.c2cri.java.net.OverTheWireLogger;
import sun.net.ConnectionResetException;

/**
 * On Windows system we simply delegate to native methods.
 *
 * @author Chris Hegarty
 */

class PlainSocketImpl extends AbstractPlainSocketImpl {

    private static final JavaIOFileDescriptorAccess fdAccess =
        SharedSecrets.getJavaIOFileDescriptorAccess();

    private static final boolean preferIPv4Stack =
            Boolean.parseBoolean(AccessController.doPrivileged(
                new GetPropertyAction("java.net.preferIPv4Stack", "false")));

    /**
     * Empty value of sun.net.useExclusiveBind is treated as 'true'.
     */
    private static final boolean useExclusiveBind;

    static {
        String exclBindProp = AccessController.doPrivileged(
                new GetPropertyAction("sun.net.useExclusiveBind", ""));
        useExclusiveBind = exclBindProp.isEmpty()
                || Boolean.parseBoolean(exclBindProp);
    }

    // emulates SO_REUSEADDR when useExclusiveBind is true
    private boolean isReuseAddress;
    
 //  ------ C2C RI -------------------   
    /* The connection information related to the Socket */
    private ConnectionInformation socketInformation;

    /* Flag indicating whether this is a Server Socket */
    private boolean serverSocketFlag = false;
    private LoggingInputStream logInputStream;
    private LoggingOutputStream logOutputStream;
    
    private boolean shut_rd = false;
    private boolean shut_wr = false;

    private SocketInputStream socketInputStream = null;
    private SocketOutputStream socketOutputStream = null;
    
    
 //  ------ C2C RI -------------------   
    

    /**
     * Constructs an empty instance.
     */
    public PlainSocketImpl() {
    }

    /**
     * Constructs an instance with the given file descriptor.
     */
    public PlainSocketImpl(FileDescriptor fd) {
        this.fd = fd;
    }

    @Override
    void socketCreate(boolean stream) throws IOException {
        System.out.println("PlainSocketImpl::socketCreate Stack Trace:");
        for (StackTraceElement ste :Thread.currentThread().getStackTrace()){
            System.out.println(ste.toString());
        }
        if (fd == null)
            throw new SocketException("Socket closed");

        int newfd = socket0(stream);

        fdAccess.set(fd, newfd);
    }

    @Override
    void socketConnect(InetAddress address, int port, int timeout)
        throws IOException {
        System.out.println("PlainSocketImpl::socketConnect Stack Trace:");
        for (StackTraceElement ste :Thread.currentThread().getStackTrace()){
            System.out.println(ste.toString());
        }
        int nativefd = checkAndReturnNativeFD();

        if (address == null)
            throw new NullPointerException("inet address argument is null.");

        if (preferIPv4Stack && !(address instanceof Inet4Address))
            throw new SocketException("Protocol family not supported");

        int connectResult;
        if (timeout <= 0) {
            connectResult = connect0(nativefd, address, port);
        } else {
            configureBlocking(nativefd, false);
            try {
                connectResult = connect0(nativefd, address, port);
                if (connectResult == WOULDBLOCK) {
                    waitForConnect(nativefd, timeout);
                }
            } finally {
                configureBlocking(nativefd, true);
            }
        }
        /*
         * We need to set the local port field. If bind was called
         * previous to the connect (by the client) then localport field
         * will already be set.
         */
        if (localport == 0)
            localport = localPort0(nativefd);
    }

    @Override
    void socketBind(InetAddress address, int port) throws IOException {
        int nativefd = checkAndReturnNativeFD();

        if (address == null)
            throw new NullPointerException("inet address argument is null.");

        if (preferIPv4Stack && !(address instanceof Inet4Address))
            throw new SocketException("Protocol family not supported");

        bind0(nativefd, address, port, useExclusiveBind);
        if (port == 0) {
            localport = localPort0(nativefd);
        } else {
            localport = port;
        }

        this.address = address;
    }

    @Override
    void socketListen(int backlog) throws IOException {
        int nativefd = checkAndReturnNativeFD();

        listen0(nativefd, backlog);
    }

    @Override
    void socketAccept(SocketImpl s) throws IOException {
        int nativefd = checkAndReturnNativeFD();

        if (s == null)
            throw new NullPointerException("socket is null");

        int newfd = -1;
        InetSocketAddress[] isaa = new InetSocketAddress[1];
        if (timeout <= 0) {
            newfd = accept0(nativefd, isaa);
        } else {
            configureBlocking(nativefd, false);
            try {
                waitForNewConnection(nativefd, timeout);
                newfd = accept0(nativefd, isaa);
                if (newfd != -1) {
                    configureBlocking(newfd, true);
                }
            } finally {
                configureBlocking(nativefd, true);
            }
        }
        /* Update (SocketImpl)s' fd */
        fdAccess.set(s.fd, newfd);
        /* Update socketImpls remote port, address and localport */
        InetSocketAddress isa = isaa[0];
        s.port = isa.getPort();
        s.address = isa.getAddress();
        s.localport = localport;
        if (preferIPv4Stack && !(s.address instanceof Inet4Address))
            throw new SocketException("Protocol family not supported");
    }

    @Override
    int socketAvailable() throws IOException {
        int nativefd = checkAndReturnNativeFD();
        return available0(nativefd);
    }

    @Override
    void socketClose0(boolean useDeferredClose/*unused*/) throws IOException {
        if (fd == null)
            throw new SocketException("Socket closed");

        if (!fd.valid())
            return;

        final int nativefd = fdAccess.get(fd);
        fdAccess.set(fd, -1);
        close0(nativefd);
    }

    @Override
    void socketShutdown(int howto) throws IOException {
        int nativefd = checkAndReturnNativeFD();
        shutdown0(nativefd, howto);
    }

    // Intentional fallthrough after SO_REUSEADDR
    @SuppressWarnings("fallthrough")
    @Override
    void socketSetOption(int opt, boolean on, Object value)
        throws SocketException {

        // SO_REUSEPORT is not supported on Windows.
        if (opt == SO_REUSEPORT) {
            throw new UnsupportedOperationException("unsupported option");
        }

        int nativefd = checkAndReturnNativeFD();

        if (opt == SO_TIMEOUT) {
            if (preferIPv4Stack) {
                // Don't enable the socket option on ServerSocket as it's
                // meaningless (we don't receive on a ServerSocket).
                if (serverSocket == null) {
                    setSoTimeout0(nativefd, ((Integer)value).intValue());
                }
            } // else timeout is implemented through select.
            return;
        }

        int optionValue = 0;

        switch(opt) {
            case SO_REUSEADDR:
                if (useExclusiveBind) {
                    // SO_REUSEADDR emulated when using exclusive bind
                    isReuseAddress = on;
                    return;
                }
                // intentional fallthrough
            case TCP_NODELAY:
            case SO_OOBINLINE:
            case SO_KEEPALIVE:
                optionValue = on ? 1 : 0;
                break;
            case SO_SNDBUF:
            case SO_RCVBUF:
            case IP_TOS:
                optionValue = ((Integer)value).intValue();
                break;
            case SO_LINGER:
                if (on) {
                    optionValue = ((Integer)value).intValue();
                } else {
                    optionValue = -1;
                }
                break;
            default :/* shouldn't get here */
                throw new SocketException("Option not supported");
        }

        setIntOption(nativefd, opt, optionValue);
    }

    @Override
    int socketGetOption(int opt, Object iaContainerObj)
        throws SocketException {

        // SO_REUSEPORT is not supported on Windows.
        if (opt == SO_REUSEPORT) {
            throw new UnsupportedOperationException("unsupported option");
        }

        int nativefd = checkAndReturnNativeFD();

        // SO_BINDADDR is not a socket option.
        if (opt == SO_BINDADDR) {
            localAddress(nativefd, (InetAddressContainer)iaContainerObj);
            return 0;  // return value doesn't matter.
        }

        // SO_REUSEADDR emulated when using exclusive bind
        if (opt == SO_REUSEADDR && useExclusiveBind)
            return isReuseAddress ? 1 : -1;

        int value = getIntOption(nativefd, opt);

        switch (opt) {
            case TCP_NODELAY:
            case SO_OOBINLINE:
            case SO_KEEPALIVE:
            case SO_REUSEADDR:
                return (value == 0) ? -1 : 1;
        }
        return value;
    }

    @Override
    void socketSendUrgentData(int data) throws IOException {
        int nativefd = checkAndReturnNativeFD();
        sendOOB(nativefd, data);
    }

    private int checkAndReturnNativeFD() throws SocketException {
        if (fd == null || !fd.valid())
            throw new SocketException("Socket closed");

        return fdAccess.get(fd);
    }

    static final int WOULDBLOCK = -2;       // Nothing available (non-blocking)

    static {
        initIDs();
    }

    /* Native methods */

    static native void initIDs();

    static native int socket0(boolean stream) throws IOException;

    static native void bind0(int fd, InetAddress localAddress, int localport,
                             boolean exclBind)
        throws IOException;

    static native int connect0(int fd, InetAddress remote, int remotePort)
        throws IOException;

    static native void waitForConnect(int fd, int timeout) throws IOException;

    static native int localPort0(int fd) throws IOException;

    static native void localAddress(int fd, InetAddressContainer in) throws SocketException;

    static native void listen0(int fd, int backlog) throws IOException;

    static native int accept0(int fd, InetSocketAddress[] isaa) throws IOException;

    static native void waitForNewConnection(int fd, int timeout) throws IOException;

    static native int available0(int fd) throws IOException;

    static native void close0(int fd) throws IOException;

    static native void shutdown0(int fd, int howto) throws IOException;

    static native void setIntOption(int fd, int cmd, int optionValue) throws SocketException;

    static native void setSoTimeout0(int fd, int timeout) throws SocketException;

    static native int getIntOption(int fd, int cmd) throws SocketException;

    static native void sendOOB(int fd, int data) throws IOException;

    static native void configureBlocking(int fd, boolean blocking) throws IOException;
    
    
    /**
     * Creates a socket and connects it to the specified port on
     * the specified host.
     * @param host the specified host
     * @param port the specified port
     */
    @Override
    protected void connect(String host, int port)
        throws UnknownHostException, IOException
    {
        boolean connected = false;
        try {
            InetAddress address = InetAddress.getByName(host);
            this.port = port;
            this.address = address;

            connectToAddress(address, port, timeout);
            connected = true;
            
        if (getSocket() != null) {
            System.out.println(System.currentTimeMillis()+"  PlainSocketImpl::connect(String, port)     Address="+address.toString());
            socketInformation = new ConnectionInformation(getSocket().getLocalAddress(), getSocket().getLocalPort(), getSocket().getInetAddress(), getSocket().getPort(), false);
            socketInformation.setTestCaseName(ConnectionsDirectory.getInstance().getTestCaseName());
            ConnectionsDirectory.getInstance().addConnection(socketInformation);
        }
        
        } finally {
            if (!connected) {
                try {
                    close();
                } catch (IOException ioe) {
                    /* Do nothing. If connect threw an exception then
                       it will be passed up the call stack */
                }
            }
        }
    }

    /**
     * Creates a socket and connects it to the specified address on
     * the specified port.
     * @param address the address
     * @param port the specified port
     */
    @Override
    protected void connect(InetAddress address, int port) throws IOException {
        this.port = port;
        this.address = address;

        try {
            connectToAddress(address, port, timeout);
            if (getSocket() != null) {
                System.out.println(System.currentTimeMillis() + "  PlainSocketImpl::connect(InetAddress, port)   Address=" + address.toString());
                socketInformation = new ConnectionInformation(getSocket().getLocalAddress(), getSocket().getLocalPort(), getSocket().getInetAddress(), getSocket().getPort(), false);
                socketInformation.setTestCaseName(ConnectionsDirectory.getInstance().getTestCaseName());
                ConnectionsDirectory.getInstance().addConnection(socketInformation);
            }            
            return;
        } catch (IOException e) {
            // everything failed
            close();
            
            System.out.println("PlainSocketImpl::connect This is the problem I'm looking for!!");
            e.printStackTrace();

            System.out.print("PlainSocketImpl::connect StackTrace\n");
            for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
                System.out.println(element.toString());
            }            
            throw e;
        }
    }

    /**
     * Creates a socket and connects it to the specified address on
     * the specified port.
     * @param address the address
     * @param timeout the timeout value in milliseconds, or zero for no timeout.
     * @throws IOException if connection fails
     * @throws  IllegalArgumentException if address is null or is a
     *          SocketAddress subclass not supported by this socket
     * @since 1.4
     */
    @Override
    protected void connect(SocketAddress address, int timeout)
            throws IOException {
        boolean connected = false;
        try {
            if (address == null || !(address instanceof InetSocketAddress))
                throw new IllegalArgumentException("unsupported address type");
            InetSocketAddress addr = (InetSocketAddress) address;
            if (addr.isUnresolved())
                throw new UnknownHostException(addr.getHostName());
            this.port = addr.getPort();
            this.address = addr.getAddress();

            connectToAddress(this.address, port, timeout);
            connected = true;
            
            if (getSocket() != null) {
                System.out.println(System.currentTimeMillis() + "  PlainSocketImpl::connect(SocketAddress, timeout)   Address=" + this.address.toString() + "  Local/Remote Addresses=" + getSocket().getLocalAddress() + ":" + getSocket().getLocalPort() + " / " + getSocket().getInetAddress() + ":" + getSocket().getPort());
                socketInformation = new ConnectionInformation(getSocket().getLocalAddress(), getSocket().getLocalPort(), getSocket().getInetAddress(), getSocket().getPort(), false);
                socketInformation.setTestCaseName(ConnectionsDirectory.getInstance().getTestCaseName());
                ConnectionsDirectory.getInstance().addConnection(socketInformation);
            }            
        } finally {
            if (!connected) {
                try {
                    close();
                } catch (IOException ioe) {
                    
            System.out.println("PlainSocketImpl::connect This is the problem I'm looking for!!");
            ioe.printStackTrace();

            System.out.print("PlainSocketImpl::connect StackTrace\n");
            for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
                System.out.println(element.toString());
            }
                    
                    
                    /* Do nothing. If connect threw an exception then
                       it will be passed up the call stack */
                }
            }
        }
    }
 
    private void connectToAddress(InetAddress address, int port, int timeout) throws IOException {
        if (address.isAnyLocalAddress()) {
            doConnect(InetAddress.getLocalHost(), port, timeout);
        } else {
            doConnect(address, port, timeout);
        }
    }
    
    /**
     * Accepts connections.
     * @param s the connection
     */
    @Override
    protected void accept(SocketImpl s) throws IOException {
        acquireFD();
        try {
            socketAccept(s);
            serverSocketFlag = true;
        } finally {
            releaseFD();
        }
    }

     /**
     * Gets an InputStream for this socket.
     */
    protected synchronized InputStream getInputStream() throws IOException {
        System.out.println(System.currentTimeMillis()+"  PlainSocketImpl::getInputStream()   serverSocket? "+serverSocket);
        if (serverSocketFlag) {
            socketInformation = new ConnectionInformation(getSocket().getLocalAddress(), getSocket().getLocalPort(), getSocket().getInetAddress(), getSocket().getPort(), serverSocketFlag);
            socketInformation.setTestCaseName(ConnectionsDirectory.getInstance().getTestCaseName());
            ConnectionsDirectory.getInstance().addConnection(socketInformation);
        }
                
        synchronized (fdLock) {
            if (isClosedOrPending())
                throw new IOException("Socket Closed");
            if (shut_rd)
                throw new IOException("Socket input is shutdown");
            if (socketInputStream == null)
                socketInputStream = new SocketInputStream(this);
        }
        logInputStream = new LoggingInputStream(socketInputStream, socketInformation);
        return logInputStream;
    }

    void setInputStream(SocketInputStream in) {
        socketInputStream = in;
    }

    /**
     * Gets an OutputStream for this socket.
     */
    protected synchronized OutputStream getOutputStream() throws IOException {
        System.out.println(System.currentTimeMillis()+"  PlainSocketImpl::getOutputStream()   serverSocket? "+serverSocket+ "   socketInformation? "+socketInformation);
        if (serverSocketFlag && (socketInformation == null)) {
            socketInformation = new ConnectionInformation(getSocket().getLocalAddress(), getSocket().getLocalPort(), getSocket().getInetAddress(), getSocket().getPort(), serverSocketFlag);
            socketInformation.setTestCaseName(ConnectionsDirectory.getInstance().getTestCaseName());
            ConnectionsDirectory.getInstance().addConnection(socketInformation);
        }
        
        synchronized (fdLock) {
            if (isClosedOrPending())
                throw new IOException("Socket Closed");
            if (shut_wr)
                throw new IOException("Socket output is shutdown");
            if (socketOutputStream == null)
                socketOutputStream = new SocketOutputStream(this);
        }
        
        logOutputStream = new LoggingOutputStream(socketOutputStream, socketInformation);
        return logOutputStream;        
    }

    /**
     * Returns the number of bytes that can be read without blocking.
     */
    @Override
    protected synchronized int available() throws IOException {
        if (isClosedOrPending()) {
            throw new IOException("Stream closed.");
        }

        /*
         * If connection has been reset or shut down for input, then return 0
         * to indicate there are no buffered bytes.
         */
        if (isConnectionReset() || shut_rd) {
            return 0;
        }

        /*
         * If no bytes available and we were previously notified
         * of a connection reset then we move to the reset state.
         *
         * If are notified of a connection reset then check
         * again if there are bytes buffered on the socket.
         */
        int n = 0;
        try {
            n = socketAvailable();
        } catch (ConnectionResetException exc1) {
            setConnectionReset();
        }
        return n;
    }    


    protected void close() throws IOException {
        try{
        super.close();
        } finally{        
            System.out.println(System.currentTimeMillis()+"  PlainSocketImpl::close()  Address = "+address +
            (socketInformation==null?"socketInformation = null":"Connection: " + (socketInformation.getConnectionName()==null?"null":socketInformation.getConnectionName())+"Test Case: " + (socketInformation.getTestCaseName()==null?"null":socketInformation.getTestCaseName())+" Local Address: "+(socketInformation.getLocalAddress()==null?"null":socketInformation.getLocalAddress())+"Remote Address: "+(socketInformation.getRemoteAddress()==null?"null":socketInformation.getRemoteAddress())));
            // set fd to delegate's fd to be compatible with older releases
            if (socketInformation == null){
                System.out.print("\nPlainSocketImpl::close() StackTrace\n");
                for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
                  System.out.println(element.toString());
                }               
            }
            this.fd = null;
            ConnectionsDirectory.getInstance().removeConnection(socketInformation);
            socketInformation = null;        
        }
    }    
    
    private class LoggingOutputStream extends OutputStream {

//        private static final Logger logger = Logger.getLogger(PlainSocketImpl.LoggingOutputStream.class);
        private OutputStream sktOutputStream;
        private final ConnectionInformation connInfo;
        private OverTheWireLogger otwLogger;

        public LoggingOutputStream(OutputStream out, ConnectionInformation connInfo) {
            this.sktOutputStream = out;
            this.connInfo = connInfo;
            this.otwLogger = new OverTheWireLogger();
        }

        @Override
        public void write(byte[] b, int off, int len)
                throws IOException {
            System.out.println("PlainSocketImpl::write Look Here num bytes " + len);
            sktOutputStream.write(b, off, len);
            if ((connInfo != null)&&(connInfo.isLiveTestConnection())) {
                connInfo.incrementSequenceCount();
                otwLogger.streamUpdate(Arrays.copyOfRange(b, off, len), len - off, len, false, connInfo);
            }

        }

        @Override
        public void write(int b) throws IOException {
            System.out.println("PlainSocketImpl::write Look Here num bytes " + 1);
            sktOutputStream.write(b);
            if ((connInfo != null)&&(connInfo.isLiveTestConnection())) {
                connInfo.incrementSequenceCount();
                otwLogger.streamUpdate(ByteBuffer.allocate(1).putInt(b).array(), 1, 1, false, connInfo);
            }
        }

        @Override
        public void write(byte[] b) throws IOException {
            System.out.println("PlainSocketImpl::write Look Here num bytes " + b.length);
            sktOutputStream.write(b);
            if ((connInfo != null)&&(connInfo.isLiveTestConnection())) {
                connInfo.incrementSequenceCount();
                otwLogger.streamUpdate(b, b.length, 0, false, connInfo);
            }
        }

        @Override
        public void close() throws IOException {
            sktOutputStream.close();
        }

        @Override
        public void flush() throws IOException {
            sktOutputStream.flush();
        }
    }

    private class LoggingInputStream extends InputStream {

        private InputStream sktInputStream;
        private ConnectionInformation connInfo;  
        private OverTheWireLogger otwLogger;

        public LoggingInputStream(InputStream in, ConnectionInformation connInfo) {
            sktInputStream = in;
            this.connInfo = connInfo;

            otwLogger = new OverTheWireLogger();
        }

        @Override
        public int read() throws IOException {
            System.out.println("PlainSocketImpl::read Look Here.");

            int count = sktInputStream.read();
            if ((connInfo != null)&&(connInfo.isLiveTestConnection())) {
                connInfo.incrementSequenceCount();
                otwLogger.streamUpdate(ByteBuffer.allocate(1).putInt(count).array(), 1, 0, true, connInfo);
                            System.out.print("StackTrace::");
                            for (StackTraceElement element:Thread.currentThread().getStackTrace()){
                                System.out.println(element.toString());
                            }
                
            }
            return count;
        }

        @Override
        public int read(byte[] b) throws IOException {
            System.out.println("PlainSocketImpl::write Look Here " + b.length);
            int count = sktInputStream.read(b);
            if ((connInfo != null)&&(connInfo.isLiveTestConnection())) {
                connInfo.incrementSequenceCount();
            }
            if (connInfo != null) {
                otwLogger.streamUpdate(b, count, 0, true, connInfo);
                            System.out.print("StackTrace::");
                            for (StackTraceElement element:Thread.currentThread().getStackTrace()){
                                System.out.println(element.toString());
                            }
                
            }
            return count;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            System.out.println("PlainSocketImpl::write Look Here " + b.length);
            int count = sktInputStream.read(b, off, len);
            if ((connInfo != null)&&(connInfo.isLiveTestConnection())) {
                connInfo.incrementSequenceCount();
            }
            int length = 0;
            if ((count > 0) && (off == 0) && (count < len)) {
                length = count;
            } else if ((count > 0) && (off > 0) && (count + off <= b.length)) {
                length = count + off;
            } else {
                length = len;
            }

            if ((connInfo != null)&&(connInfo.isLiveTestConnection())) {
                try{
                otwLogger.streamUpdate(Arrays.copyOfRange(b, off, length), count, 0, true, connInfo);
                            System.out.print("StackTrace::");
                            for (StackTraceElement element:Thread.currentThread().getStackTrace()){
                                System.out.println(element.toString());
                            }
                }catch (IllegalArgumentException ex){
                    if (connInfo.getConnectionName()==null)throw new IllegalArgumentException(ex);
                }
                        
            }
            return count;
        }

        /**
         *
         * @param b
         * @param length
         * @throws IOException
         */
        public void finish(byte[] b, int length) throws IOException {
            System.out.println("PlainSocketImpl::finish Look Here num bytes " + b.length);
            if ((connInfo != null)&&(connInfo.isLiveTestConnection())) {
                connInfo.incrementSequenceCount();
                otwLogger.streamUpdate(Arrays.copyOfRange(b, 0, length), length, 0, true, connInfo);
            }
        }

        @Override
        public long skip(long n) throws IOException {
            return sktInputStream.skip(n);
        }

        @Override
        public int available() throws IOException {
            return sktInputStream.available();
        }

        @Override
        public void close() throws IOException {
            sktInputStream.close();
        }

        @Override
        public synchronized void mark(int readlimit) {
            sktInputStream.mark(readlimit);
        }

        @Override
        public synchronized void reset() throws IOException {
            sktInputStream.reset();
        }

        @Override
        public boolean markSupported() {
            return sktInputStream.markSupported();
        }
    }
    
    
}