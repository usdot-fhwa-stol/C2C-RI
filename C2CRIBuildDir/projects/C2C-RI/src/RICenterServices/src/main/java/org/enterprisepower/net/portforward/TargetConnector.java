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

import java.io.FileInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.net.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.SocketFactory;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author Kenneth Xu
 *
 */
public class TargetConnector {

    private static Log log = LogFactory.getLog(TargetConnector.class);
    private InetSocketAddress to;

    public TargetConnector(InetSocketAddress to) {
        log.trace("new ServerConnectionManager( InetSocketAddress )");
        this.to = to;
    }

    public Socket openSocket() throws java.io.IOException {
        return new Socket(to.getAddress(), to.getPort());
    }

    public Socket openSocket(boolean enableSSL) throws java.io.IOException {
        if (enableSSL) {
            SocketFactory socketFactory = SSLSocketFactory.getDefault();
            return socketFactory.createSocket(to.getAddress(), to.getPort());
//                return getSSLSocket();
        } else {
            return new Socket(to.getAddress(), to.getPort());
        }
    }

    public Socket getSSLSocket() throws java.io.IOException {
        char[] passwKey = "c2cri1".toCharArray();
        SSLSocket socket = null;
        try {
// KeyStore ts = KeyStore.getInstance("PKCS12");
            KeyStore ts = KeyStore.getInstance("jks");
            ts.load(new FileInputStream("C:\\C2CRI\\keystore\\keystore"), passwKey);
            KeyManagerFactory tmf = KeyManagerFactory.getInstance("SunX509");
            tmf.init(ts, passwKey);
// SSLContext sslContext = SSLContext.getInstance("TLS");
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(tmf.getKeyManagers(), null, null);
            SSLSocketFactory factory = sslContext.getSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(factory);
            socket = (SSLSocket) factory.createSocket(to.getAddress(), to.getPort()); // Create the ServerSocket
            String[] suites = socket.getSupportedCipherSuites();
            socket.setEnabledCipherSuites(suites);
            //start handshake
            socket.startHandshake();
        } catch (KeyStoreException kex) {
            System.err.println("TargetConnector KEX Excption: " + kex.getMessage());
            SocketFactory socketFactory = SSLSocketFactory.getDefault();
            return socketFactory.createSocket(to.getAddress(), to.getPort());

        } catch (NoSuchAlgorithmException nsaex) {
            System.err.println("TargetConnector NSAEX Excption: " + nsaex.getMessage());
            SocketFactory socketFactory = SSLSocketFactory.getDefault();
            return socketFactory.createSocket(to.getAddress(), to.getPort());

        } catch (CertificateException cex) {
            System.err.println("TargetConnector CEX Excption: " + cex.getMessage());
            SocketFactory socketFactory = SSLSocketFactory.getDefault();
            return socketFactory.createSocket(to.getAddress(), to.getPort());

        } catch (KeyManagementException kme) {
            System.err.println("TargetConnector KME Excption: " + kme.getMessage());
            SocketFactory socketFactory = SSLSocketFactory.getDefault();
            return socketFactory.createSocket(to.getAddress(), to.getPort());

        } catch (UnrecoverableKeyException uke) {
            System.err.println("TargetConnector UKE Excption: " + uke.getMessage());
            SocketFactory socketFactory = SSLSocketFactory.getDefault();
            return socketFactory.createSocket(to.getAddress(), to.getPort());

        }
        return socket;

    }
}