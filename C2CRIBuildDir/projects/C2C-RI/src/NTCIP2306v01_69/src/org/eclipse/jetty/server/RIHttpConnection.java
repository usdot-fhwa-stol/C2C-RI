// ========================================================================
// Copyright (c) 2004-2009 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// and Apache License v2.0 which accompanies this distribution.
// The Eclipse Public License is available at
// http://www.eclipse.org/legal/epl-v10.html
// The Apache License v2.0 is available at
// http://www.opensource.org/licenses/apache2.0.php
// You may elect to redistribute this code under either of these licenses.
// ========================================================================

package org.eclipse.jetty.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.servlet.ServletOutputStream;
import org.eclipse.jetty.http.Generator;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpGenerator;
import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.http.Parser;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.UncheckedPrintWriter;
import org.fhwa.c2cri.java.net.ConnectionInformation;
import org.fhwa.c2cri.java.net.ConnectionsDirectory;
import org.fhwa.c2cri.java.net.OverTheWireLogger;

/**
 * <p>A HttpConnection represents the connection of a HTTP client to the server
 * and is created by an instance of a {@link Connector}. It's prime function is
 * to associate {@link Request} and {@link Response} instances with a {@link EndPoint}.
 * </p>
 * <p>
 * A connection is also the prime mechanism used by jetty to recycle objects without
 * pooling.  The {@link Request},  {@link Response}, {@link HttpParser}, {@link HttpGenerator}
 * and {@link HttpFields} instances are all recycled for the duraction of
 * a connection. Where appropriate, allocated buffers are also kept associated
 * with the connection via the parser and/or generator.
 * </p>
 * <p>
 * The connection state is held by 3 separate state machines: The request state, the
 * response state and the continuation state.  All three state machines must be driven
 * to completion for every request, and all three can complete in any order.
 * </p>
 * <p>
 * The HttpConnection support protocol upgrade.  If on completion of a request, the
 * response code is 101 (switch protocols), then the org.eclipse.jetty.io.Connection
 * request attribute is checked to see if there is a new Connection instance. If so,
 * the new connection is returned from {@link #handle()} and is used for future
 * handling of the underlying connection.   Note that for switching protocols that
 * don't use 101 responses (eg CONNECT), the response should be sent and then the
 * status code changed to 101 before returning from the handler.  Implementors
 * of new Connection types should be careful to extract any buffered data from
 * (HttpParser)http.getParser()).getHeaderBuffer() and
 * (HttpParser)http.getParser()).getBodyBuffer() to initialise their new connection.
 * </p>
 * 
 *  Modified Date: 12/15/2013
 */
public class RIHttpConnection extends HttpConnection implements Connection
{
    
    private static LoggingOutputStream logOutputStream;
    
    /* ------------------------------------------------------------ */
    /** Constructor
     *
     * @param connector - the connector for this connection
     * @param endpoint  - the endpoint for this connection
     * @param server  - the server object for this connection
     */
    public RIHttpConnection(Connector connector, EndPoint endpoint, Server server)
    {
        super(connector, endpoint,server);
    }

    /* ------------------------------------------------------------ */
    /**
     *  Construction for RIHttpConnection which includes parser, generator and request objects.
     * @param connector
     * @param endpoint
     * @param server
     * @param parser
     * @param generator
     * @param request
     */
    protected RIHttpConnection(Connector connector, EndPoint endpoint, Server server,
            Parser parser, Generator generator, Request request)
    {
        super(connector, endpoint, server, parser, generator, request);
    }


    /* ------------------------------------------------------------ */
    /**
     * @return The output stream for this connection. The stream will be created if it does not already exist.
     */
    public ServletOutputStream getOutputStream()
    {
        if (_out == null){
            try{
            InetAddress localAddress = InetAddress.getByName(super.getEndPoint().getLocalAddr());
            InetAddress remoteAddress = InetAddress.getByName(super.getEndPoint().getRemoteAddr());
            ConnectionInformation socketInformation = new ConnectionInformation(localAddress, super.getEndPoint().getLocalPort(), remoteAddress, super.getEndPoint().getRemotePort(), true);
            socketInformation.setTestCaseName(ConnectionsDirectory.getInstance().getTestCaseName());
            
            ConnectionsDirectory.getInstance().addConnection(socketInformation);
            _out = new Output();
            logOutputStream = new LoggingOutputStream(_out, socketInformation);
            } catch (Exception ex){
                ex.printStackTrace();
            } 
            
        }
        return logOutputStream;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param encoding 
     * @return A {@link PrintWriter} wrapping the {@link #getOutputStream output stream}. The writer is created if it
     *    does not already exist.
     */
    public PrintWriter getPrintWriter(String encoding)
    {
        getOutputStream();
        if (_writer==null)
        {
            _writer=new OutputWriter();
            _printWriter=new UncheckedPrintWriter(_writer);
        }
        _writer.setCharacterEncoding(encoding);
        return _printWriter;
    }


    private static class LoggingOutputStream extends ServletOutputStream {

        private ServletOutputStream sktOutputStream;
        private final ConnectionInformation connInfo;
        private OverTheWireLogger otwLogger;

        public LoggingOutputStream(ServletOutputStream out, ConnectionInformation connInfo) {
            this.sktOutputStream = out;
            this.connInfo = connInfo;
            this.otwLogger = new OverTheWireLogger();
        }

        @Override
        public void write(byte[] b, int off, int len)
                throws IOException {
            sktOutputStream.write(b, off, len);
            connInfo.incrementSequenceCount();
            otwLogger.streamUpdate(Arrays.copyOfRange(b, off, len), len - off, len, false, connInfo);


        }

        @Override
        public void write(int b) throws IOException {
            sktOutputStream.write(b);
            connInfo.incrementSequenceCount();
            otwLogger.streamUpdate(ByteBuffer.allocate(1).putInt(b).array(), 1, 1, false, connInfo);
        }

        @Override
        public void write(byte[] b) throws IOException {
            sktOutputStream.write(b);
            connInfo.incrementSequenceCount();
            otwLogger.streamUpdate(b, b.length, 0, false, connInfo);
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
    
    
    
    

}
