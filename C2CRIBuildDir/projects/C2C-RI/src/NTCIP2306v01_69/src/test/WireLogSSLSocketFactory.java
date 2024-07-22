/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


/**
 * A factory for creating WireLogSSLSocket objects.
 */
public class WireLogSSLSocketFactory extends CleanSSLSocketFactory{

    /** The delegate. */
    private CleanSSLSocketFactory delegate;

    /**
     * Instantiates a new wire log ssl socket factory.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param sf0 the sf0
     */
    public WireLogSSLSocketFactory(CleanSSLSocketFactory sf0) {
        this.delegate = sf0;
    }

    /**
     * Creates a new WireLogSSLSocket object.
     *
     * @param s the s
     * @param host the host
     * @param port the port
     * @param autoClose the auto close
     * @return the socket
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public Socket createSocket(Socket s, String host, int port,
            boolean autoClose) throws IOException {
        return new WireLogSocket((SSLSocket) delegate.createSocket(s, host, port, autoClose));
    }

    /**
     * Creates a new WireLogSSLSocket object.
     *
     * @return the socket
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public Socket createSocket() throws IOException {
        return new WireLogSocket((SSLSocket) delegate.createSocket());
    }



    /**
 * The Class WireLogSocket.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
private static class WireLogSocket extends SSLSocket {

        /** The delegate. */
        private SSLSocket delegate;

        /**
         * Instantiates a new wire log socket.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param s the s
         */
        public WireLogSocket(SSLSocket s) {
            this.delegate = s;
        }

        /* (non-Javadoc)
         * @see java.net.Socket#getOutputStream()
         */
        @Override
        public OutputStream getOutputStream() throws IOException {
            return new LoggingOutputStream(delegate.getOutputStream());
        }

        /* (non-Javadoc)
         * @see java.net.Socket#getInputStream()
         */
        @Override
        public InputStream getInputStream() throws IOException {
            return delegate.getInputStream(); 
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#getSupportedCipherSuites()
         */
        @Override
        public String[] getSupportedCipherSuites() {
            return this.delegate.getSupportedCipherSuites();
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#getEnabledCipherSuites()
         */
        @Override
        public String[] getEnabledCipherSuites() {
            return this.delegate.getEnabledCipherSuites();
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#setEnabledCipherSuites(java.lang.String[])
         */
        @Override
        public void setEnabledCipherSuites(String[] strings) {
            this.delegate.setEnabledCipherSuites(strings);
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#getSupportedProtocols()
         */
        @Override
        public String[] getSupportedProtocols() {
            return this.delegate.getSupportedProtocols();
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#getEnabledProtocols()
         */
        @Override
        public String[] getEnabledProtocols() {
            return this.delegate.getEnabledProtocols();
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#setEnabledProtocols(java.lang.String[])
         */
        @Override
        public void setEnabledProtocols(String[] strings) {
            this.delegate.setEnabledProtocols(strings);
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#getSession()
         */
        @Override
        public SSLSession getSession() {
            return this.delegate.getSession();
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#addHandshakeCompletedListener(javax.net.ssl.HandshakeCompletedListener)
         */
        @Override
        public void addHandshakeCompletedListener(HandshakeCompletedListener hl) {
            this.delegate.addHandshakeCompletedListener(hl);
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#removeHandshakeCompletedListener(javax.net.ssl.HandshakeCompletedListener)
         */
        @Override
        public void removeHandshakeCompletedListener(HandshakeCompletedListener hl) {
            this.delegate.removeHandshakeCompletedListener(hl);
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#startHandshake()
         */
        @Override
        public void startHandshake() throws IOException {
            this.delegate.startHandshake();
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#setUseClientMode(boolean)
         */
        @Override
        public void setUseClientMode(boolean bln) {
            this.delegate.setUseClientMode(bln);
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#getUseClientMode()
         */
        @Override
        public boolean getUseClientMode() {
            return this.delegate.getUseClientMode();
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#setNeedClientAuth(boolean)
         */
        @Override
        public void setNeedClientAuth(boolean bln) {
            this.delegate.setNeedClientAuth(bln);
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#getNeedClientAuth()
         */
        @Override
        public boolean getNeedClientAuth() {
            return this.delegate.getNeedClientAuth();
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#setWantClientAuth(boolean)
         */
        @Override
        public void setWantClientAuth(boolean bln) {
            this.delegate.setWantClientAuth(bln);
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#getWantClientAuth()
         */
        @Override
        public boolean getWantClientAuth() {
            return this.delegate.getWantClientAuth();
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#setEnableSessionCreation(boolean)
         */
        @Override
        public void setEnableSessionCreation(boolean bln) {
            this.delegate.setEnableSessionCreation(bln);
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.SSLSocket#getEnableSessionCreation()
         */
        @Override
        public boolean getEnableSessionCreation() {
            return this.delegate.getEnableSessionCreation();
        }



        /**
         * The Class LoggingOutputStream.
         *
         * @author TransCore ITS, LLC
         * Last Updated:  1/8/2014
         */
        private static class LoggingOutputStream extends FilterOutputStream {
            
            /** The Constant logger. */
            private static final Logger logger = LogManager.getLogger(WireLogSocket.LoggingOutputStream.class);
            //I'm using a fixed charset because my app always uses the same. 
            /** The Constant CHARSET. */
            private static final String CHARSET = "ISO-8859-1";
            
            /** The sb. */
            private StringBuffer sb = new StringBuffer();

            /**
             * Instantiates a new logging output stream.
             * 
             * Pre-Conditions: N/A
             * Post-Conditions: N/A
             *
             * @param out the out
             */
            public LoggingOutputStream(OutputStream out) {
                super(out);
            }

            /* (non-Javadoc)
             * @see java.io.FilterOutputStream#write(byte[], int, int)
             */
            public void write(byte[] b, int off, int len)
                    throws IOException {
                sb.append(new String(b, off, len, CHARSET));
                logger.info("\n" + sb.toString());
                out.write(b, off, len);
            }

            /* (non-Javadoc)
             * @see java.io.FilterOutputStream#write(int)
             */
            public void write(int b) throws IOException {
                sb.append(b);
                logger.info("\n" + sb.toString());
                out.write(b);
            }

            /* (non-Javadoc)
             * @see java.io.FilterOutputStream#close()
             */
            public void close() throws IOException {
                logger.info("\n" + sb.toString());
                super.close();
            }
        }
    }    
}
