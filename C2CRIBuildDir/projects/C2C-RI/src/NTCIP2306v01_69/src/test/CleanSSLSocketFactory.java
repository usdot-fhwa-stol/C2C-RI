/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

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


import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Layered socket factory for TLS/SSL connections, based on JSSE.
 * 
 * <p>
 * SSLSocketFactory can be used to validate the identity of the HTTPS 
 * server against a list of trusted certificates and to authenticate to
 * the HTTPS server using a private key. 
 * </p>
 * 
 * <p>
 * SSLSocketFactory will enable server authentication when supplied with
 * a {@link KeyStore truststore} file containing one or several trusted
 * certificates. The client secure socket will reject the connection during
 * the SSL session handshake if the target HTTPS server attempts to
 * authenticate itself with a non-trusted certificate.
 * </p>
 * 
 * <p>
 * Use JDK keytool utility to import a trusted certificate and generate a truststore file:    
 *    <pre>
 *     keytool -import -alias "my server cert" -file server.crt -keystore my.truststore
 *    </pre>
 * </p>
 * 
 * <p>
 * The following parameters can be used to customize the behavior of this 
 * class: 
 * <ul>
 *  <li>{@link org.apache.http.params.CoreConnectionPNames#CONNECTION_TIMEOUT}</li>
 *  <li>{@link org.apache.http.params.CoreConnectionPNames#SO_TIMEOUT}</li>
 * </ul>
 * </p>
 * 
 * <p>
 * SSLSocketFactory will enable client authentication when supplied with
 * a {@link KeyStore keystore} file containg a private key/public certificate
 * pair. The client secure socket will use the private key to authenticate
 * itself to the target HTTPS server during the SSL session handshake if
 * requested to do so by the server.
 * The target HTTPS server will in its turn verify the certificate presented
 * by the client in order to establish client's authenticity
 * </p>
 * 
 * <p>
 * Use the following sequence of actions to generate a keystore file
 * </p>
 *   <ul>
 *     <li>
 *      <p>
 *      Use JDK keytool utility to generate a new key
 *      <pre>keytool -genkey -v -alias "my client key" -validity 365 -keystore my.keystore</pre>
 *      For simplicity use the same password for the key as that of the keystore
 *      </p>
 *     </li>
 *     <li>
 *      <p>
 *      Issue a certificate signing request (CSR)
 *      <pre>keytool -certreq -alias "my client key" -file mycertreq.csr -keystore my.keystore</pre>
 *     </p>
 *     </li>
 *     <li>
 *      <p>
 *      Send the certificate request to the trusted Certificate Authority for signature. 
 *      One may choose to act as her own CA and sign the certificate request using a PKI 
 *      tool, such as OpenSSL.
 *      </p>
 *     </li>
 *     <li>
 *      <p>
 *       Import the trusted CA root certificate
 *       <pre>keytool -import -alias "my trusted ca" -file caroot.crt -keystore my.keystore</pre> 
 *      </p>
 *     </li>
 *     <li>
 *      <p>
 *       Import the PKCS#7 file containg the complete certificate chain
 *       <pre>keytool -import -alias "my client key" -file mycert.p7 -keystore my.keystore</pre> 
 *      </p>
 *     </li>
 *     <li>
 *      <p>
 *       Verify the content the resultant keystore file
 *       <pre>keytool -list -v -keystore my.keystore</pre> 
 *      </p>
 *     </li>
 *   </ul>
 *
 * @since 4.0
 */
public class CleanSSLSocketFactory implements LayeredSocketFactory {

    public static final String TLS   = "TLS";
    public static final String SSL   = "SSL";
    public static final String SSLV2 = "SSLv2";
    
    public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER 
        = new AllowAllHostnameVerifier();
    
    public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER 
        = new BrowserCompatHostnameVerifier();
    
    public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER 
        = new StrictHostnameVerifier();

    /**
     * The default factory using the default JVM settings for secure connections.
     */
    private static final CleanSSLSocketFactory DEFAULT_FACTORY = new CleanSSLSocketFactory();
    
    /**
     * Gets the default factory, which uses the default JVM settings for secure 
     * connections.
     * 
     * @return the default factory
     */
    public static CleanSSLSocketFactory getSocketFactory() {
        return DEFAULT_FACTORY;
    }
    
    private final SSLContext sslcontext;
    private final javax.net.ssl.SSLSocketFactory socketfactory;
    private final HostNameResolver nameResolver;
    
    private String remoteAdd;
    private String localAdd;
    private boolean secureMode=false;
    private String listenerName="No Name Provided";
    private Integer listenerId=-1;
    
    
    // volatile is needed to guarantee thread-safety of the setter/getter methods under all usage scenarios
    private volatile X509HostnameVerifier hostnameVerifier = BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

    public CleanSSLSocketFactory(
        String algorithm, 
        final KeyStore keystore, 
        final String keystorePassword, 
        final KeyStore truststore,
        final SecureRandom random,
        final HostNameResolver nameResolver) 
        throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
    {
        super();
        if (algorithm == null) {
            algorithm = TLS;
        }
        KeyManager[] keymanagers = null;
        if (keystore != null) {
            keymanagers = createKeyManagers(keystore, keystorePassword);
        }
        TrustManager[] trustmanagers = null;
        if (truststore != null) {
            trustmanagers = createTrustManagers(truststore);
        }
        this.sslcontext = SSLContext.getInstance(algorithm);
        this.sslcontext.init(keymanagers, trustmanagers, random);
        this.socketfactory = this.sslcontext.getSocketFactory();
        this.nameResolver = nameResolver;
    }

    public CleanSSLSocketFactory(
            final KeyStore keystore, 
            final String keystorePassword, 
            final KeyStore truststore) 
            throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
    {
        this(TLS, keystore, keystorePassword, truststore, null, null);
    }

    public CleanSSLSocketFactory(final KeyStore keystore, final String keystorePassword) 
            throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
    {
        this(TLS, keystore, keystorePassword, null, null, null);
    }

    public CleanSSLSocketFactory(final KeyStore truststore) 
            throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
    {
        this(TLS, null, null, truststore, null, null);
    }

    public CleanSSLSocketFactory(
        final SSLContext sslContext,
        final HostNameResolver nameResolver)
    {
        this.sslcontext = sslContext;
        this.socketfactory = this.sslcontext.getSocketFactory();
        this.nameResolver = nameResolver;
    }

    public CleanSSLSocketFactory(final SSLContext sslContext)
    {
        this(sslContext, null);
    }

    /**
     * Creates the default SSL socket factory.
     * This constructor is used exclusively to instantiate the factory for
     * {@link #getSocketFactory getSocketFactory}.
     */
    public CleanSSLSocketFactory() {
        super();
        this.sslcontext = null;
        this.socketfactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        this.nameResolver = null;
    }

    private static KeyManager[] createKeyManagers(final KeyStore keystore, final String password)
        throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        if (keystore == null) {
            throw new IllegalArgumentException("Keystore may not be null");
        }
        KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(
            KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, password != null ? password.toCharArray(): null);
        return kmfactory.getKeyManagers(); 
    }

    private static TrustManager[] createTrustManagers(final KeyStore keystore)
        throws KeyStoreException, NoSuchAlgorithmException { 
        if (keystore == null) {
            throw new IllegalArgumentException("Keystore may not be null");
        }
        TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init(keystore);
        return tmfactory.getTrustManagers();
    }


    // non-javadoc, see interface org.apache.http.conn.SocketFactory
    @SuppressWarnings("cast")
    public Socket createSocket()
        throws IOException {

        // the cast makes sure that the factory is working as expected
        return new WireLogSocket((SSLSocket) this.socketfactory.createSocket());
    }


    // non-javadoc, see interface org.apache.http.conn.SocketFactory
    public Socket connectSocket(
        final Socket sock,
        final String host,
        final int port,
        final InetAddress localAddress,
        int localPort,
        final HttpParams params
    ) throws IOException {

        if (host == null) {
            throw new IllegalArgumentException("Target host may not be null.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null.");
        }

        SSLSocket sslsock = (SSLSocket)
            ((sock != null) ? sock : createSocket());

        if ((localAddress != null) || (localPort > 0)) {

            // we need to bind explicitly
            if (localPort < 0)
                localPort = 0; // indicates "any"

            InetSocketAddress isa =
                new InetSocketAddress(localAddress, localPort);
            sslsock.bind(isa);
        }

        int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
        int soTimeout = HttpConnectionParams.getSoTimeout(params);

        InetSocketAddress remoteAddress;
        if (this.nameResolver != null) {
            remoteAddress = new InetSocketAddress(this.nameResolver.resolve(host), port); 
        } else {
            remoteAddress = new InetSocketAddress(host, port);            
        }
        try {
            sslsock.connect(remoteAddress, connTimeout);
        } catch (SocketTimeoutException ex) {
            throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
        }
        sslsock.setSoTimeout(soTimeout);
        try {
            hostnameVerifier.verify(host, sslsock);
            // verifyHostName() didn't blowup - good!
        } catch (IOException iox) {
            // close the socket before re-throwing the exception
            try { sslsock.close(); } catch (Exception x) { /*ignore*/ }
            throw iox;
        }

        return sslsock;
    }


    /**
     * Checks whether a socket connection is secure.
     * This factory creates TLS/SSL socket connections
     * which, by default, are considered secure.
     * <br/>
     * Derived classes may override this method to perform
     * runtime checks, for example based on the cypher suite.
     *
     * @param sock      the connected socket
     *
     * @return  <code>true</code>
     *
     * @throws IllegalArgumentException if the argument is invalid
     */
    public boolean isSecure(Socket sock)
        throws IllegalArgumentException {

        if (sock == null) {
            throw new IllegalArgumentException("Socket may not be null.");
        }
        // This instanceof check is in line with createSocket() above.
        if (!(sock instanceof SSLSocket)) {
            throw new IllegalArgumentException
                ("Socket not created by this factory.");
        }
        // This check is performed last since it calls the argument object.
        if (sock.isClosed()) {
            throw new IllegalArgumentException("Socket is closed.");
        }

        return true;

    } // isSecure


    // non-javadoc, see interface LayeredSocketFactory
    public Socket createSocket(
        final Socket socket,
        final String host,
        final int port,
        final boolean autoClose
    ) throws IOException, UnknownHostException {
        SSLSocket sslSocket = new WireLogSocket((SSLSocket) this.socketfactory.createSocket(
              socket,
              host,
              port,
              autoClose
        ));
        hostnameVerifier.verify(host, sslSocket);
        // verifyHostName() didn't blowup - good!
        return sslSocket;
    }

    public void setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
        if ( hostnameVerifier == null ) {
            throw new IllegalArgumentException("Hostname verifier may not be null");
        }
        this.hostnameVerifier = hostnameVerifier;
    }

    public X509HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    
    private static class WireLogSocket extends SSLSocket {

        private SSLSocket delegate;

        public WireLogSocket(SSLSocket s) {
            this.delegate = s;
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return new LoggingOutputStream(delegate.getOutputStream());
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new LoggingInputStream(delegate.getInputStream()); 
        }
        
        @Override
        public String[] getSupportedCipherSuites() {
            return this.delegate.getSupportedCipherSuites();
        }

        @Override
        public String[] getEnabledCipherSuites() {
            return this.delegate.getEnabledCipherSuites();
        }

        @Override
        public void setEnabledCipherSuites(String[] strings) {
            this.delegate.setEnabledCipherSuites(strings);
        }

        @Override
        public String[] getSupportedProtocols() {
            return this.delegate.getSupportedProtocols();
        }

        @Override
        public String[] getEnabledProtocols() {
            return this.delegate.getEnabledProtocols();
        }

        @Override
        public void setEnabledProtocols(String[] strings) {
            this.delegate.setEnabledProtocols(strings);
        }

        @Override
        public SSLSession getSession() {
            return this.delegate.getSession();
        }

        @Override
        public void addHandshakeCompletedListener(HandshakeCompletedListener hl) {
            this.delegate.addHandshakeCompletedListener(hl);
        }

        @Override
        public void removeHandshakeCompletedListener(HandshakeCompletedListener hl) {
            this.delegate.removeHandshakeCompletedListener(hl);
        }

        @Override
        public void startHandshake() throws IOException {
            this.delegate.startHandshake();
        }

        @Override
        public void setUseClientMode(boolean bln) {
            this.delegate.setUseClientMode(bln);
        }

        @Override
        public boolean getUseClientMode() {
            return this.delegate.getUseClientMode();
        }

        @Override
        public void setNeedClientAuth(boolean bln) {
            this.delegate.setNeedClientAuth(bln);
        }

        @Override
        public boolean getNeedClientAuth() {
            return this.delegate.getNeedClientAuth();
        }

        @Override
        public void setWantClientAuth(boolean bln) {
            this.delegate.setWantClientAuth(bln);
        }

        @Override
        public boolean getWantClientAuth() {
            return this.delegate.getWantClientAuth();
        }

        @Override
        public void setEnableSessionCreation(boolean bln) {
            this.delegate.setEnableSessionCreation(bln);
        }

        @Override
        public boolean getEnableSessionCreation() {
            return this.delegate.getEnableSessionCreation();
        }



        private static class LoggingOutputStream extends FilterOutputStream {
            private static final Logger logger = LogManager.getLogger(WireLogSocket.LoggingOutputStream.class);
            //I'm using a fixed charset because my app always uses the same. 
            private static final String CHARSET = "ISO-8859-1";
            private StringBuffer sb = new StringBuffer();

            public LoggingOutputStream(OutputStream out) {
                super(out);
            }

            public void write(byte[] b, int off, int len)
                    throws IOException {
                sb.append(new String(b, off, len, CHARSET));
                logger.info(sb.toString());
                System.out.println("--- LOG OUTPUT ---");
                System.out.println("\n" + sb.toString());
                System.out.println("--- END LOG OUTPUT ---\nbufLength:"+b.length+"\noff:"+off+"\nlen:"+len);
                out.write(b, off, len);
            }

            public void write(int b) throws IOException {
                sb.append(b);
                logger.info(sb.toString());
                System.out.println("--- LOG OUTPUT ---");
                System.out.println("\n" + sb.toString());
                System.out.println("--- END LOG OUTPUT ---");
                out.write(b);
            }

            public void close() throws IOException {
                logger.info(sb.toString());
                super.close();
            }
        }
        
         private static class LoggingInputStream extends FilterInputStream {
            private static final Logger logger = LogManager.getLogger(WireLogSocket.LoggingOutputStream.class);
            //I'm using a fixed charset because my app always uses the same. 
            private static final String CHARSET = "ISO-8859-1";
            private StringBuffer sb = new StringBuffer();

            public LoggingInputStream(InputStream in) {
                super(in);
            }

            public void write(byte[] b, int off, int len)
                    throws IOException {
                sb.append(new String(b, off, len, CHARSET));
                System.out.println("--- LOG INPUT ---");
                System.out.println("\n"+sb.toString());
                System.out.println("--- END LOG INPUT ---");
                logger.info(sb.toString());
                in.read(b, off, len);
            }

            public void write(int b) throws IOException {
                sb.append(b);
                 System.out.println("--- LOG OUTPUT ---");
                System.out.println("\n"+sb.toString());
                System.out.println("--- END LOG OUTPUT ---");
               logger.info(sb.toString());
                in.read();
            }

            public void close() throws IOException {
                logger.info(sb.toString());
                super.close();
            }
        }
       
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
