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

package org.fhwa.c2cri.ntcip2306v109.http;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.http.HttpSchemes;
import org.eclipse.jetty.http.security.Password;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.Buffers;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ThreadLocalBuffers;
import org.eclipse.jetty.io.bio.SocketEndPoint;
import org.eclipse.jetty.io.nio.DirectNIOBuffer;
import org.eclipse.jetty.io.nio.IndirectNIOBuffer;
import org.eclipse.jetty.io.nio.SelectChannelEndPoint;
import org.eclipse.jetty.io.nio.SslSelectChannelEndPoint;
import org.eclipse.jetty.io.nio.SelectorManager.SelectSet;
import org.eclipse.jetty.server.RIHttpConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.ssl.SslCertificates;
import org.eclipse.jetty.server.ssl.SslConnector;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.resource.Resource;
import org.fhwa.c2cri.ntcip2306v109.http.server.RISelectChannelConnector;
//import org.fhwa.c2cri.ntcip2306v109.RISelectChannelConnector;

/* ------------------------------------------------------------ */
/**
 * SslSelectChannelConnector.
 * 
 * @org.apache.xbean.XBean element="sslConnector" description="Creates an NIO ssl connector"
 *
 * 
 * 
 */
public class RISslSelectChannelConnector extends RISelectChannelConnector implements SslConnector
{
    /** Default value for the excluded cipher Suites. */
    private String _excludeCipherSuites[]=null;
    /** Default value for the included cipher Suites. */
    private String _includeCipherSuites[]=null;

    /** Default value for the keystore location path. */
    private String _keystorePath=DEFAULT_KEYSTORE;
    
    /** The _keystore type. */
    private String _keystoreType="JKS"; // type of the key store

    /** Set to true if we require client certificate authentication. */
    private boolean _needClientAuth=false;
    
    /** The _want client auth. */
    private boolean _wantClientAuth=false;
    
    /** The _allow renegotiate. */
    private boolean _allowRenegotiate=false;

    /** The _password. */
    private transient Password _password;
    
    /** The _key password. */
    private transient Password _keyPassword;
    
    /** The _trust password. */
    private transient Password _trustPassword;
    
    /** The _protocol. */
    private String _protocol="TLS";
    
    /** The _provider. */
    private String _provider;
    
    /** The _secure random algorithm. */
    private String _secureRandomAlgorithm; // cert algorithm
    
    /** The _ssl key manager factory algorithm. */
    private String _sslKeyManagerFactoryAlgorithm=DEFAULT_KEYSTORE_ALGORITHM; 
    
    /** The _ssl trust manager factory algorithm. */
    private String _sslTrustManagerFactoryAlgorithm=DEFAULT_TRUSTSTORE_ALGORITHM; 
    
    /** The _truststore path. */
    private String _truststorePath;
    
    /** The _truststore type. */
    private String _truststoreType="JKS"; // type of the key store
    
    /** The _context. */
    private SSLContext _context;
    
    /** The _ssl buffers. */
    Buffers _sslBuffers;

    /* ------------------------------------------------------------ */
    /**
     * Instantiates a new rI ssl select channel connector.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public RISslSelectChannelConnector()
    {
        setUseDirectBuffers(false);
    }
    
    /* ------------------------------------------------------------ */
    /**
     * Instantiates a new rI ssl select channel connector.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param location the location
     */
    public RISslSelectChannelConnector(String location)
    {
        super(location);
        setUseDirectBuffers(false);
    }
  
    /* ------------------------------------------------------------ */
    /**
     * Allow the Listener a chance to customise the request. before the server
     * does its stuff. <br>
     * This allows the required attributes to be set for SSL requests. <br>
     * The requirements of the Servlet specs are:
     * <ul>
     * <li> an attribute named "javax.servlet.request.ssl_session_id" of type
     * String (since Servlet Spec 3.0).</li>
     * <li> an attribute named "javax.servlet.request.cipher_suite" of type
     * String.</li>
     * <li> an attribute named "javax.servlet.request.key_size" of type Integer.</li>
     * <li> an attribute named "javax.servlet.request.X509Certificate" of type
     * java.security.cert.X509Certificate[]. This is an array of objects of type
     * X509Certificate, the order of this array is defined as being in ascending
     * order of trust. The first certificate in the chain is the one set by the
     * client, the next is the one used to authenticate the first, and so on.
     * </li>
     * </ul>
     *
     * @param endpoint The Socket the request arrived on. This should be a
     * @param request HttpRequest to be customised.
     * @throws IOException Signals that an I/O exception has occurred.
     * {@link SocketEndPoint} wrapping a {@link SSLSocket}.
     */
    @Override
    public void customize(EndPoint endpoint, Request request) throws IOException
    {
        request.setScheme(HttpSchemes.HTTPS);
        super.customize(endpoint,request);

        SslSelectChannelEndPoint sslHttpChannelEndpoint=(SslSelectChannelEndPoint)endpoint;
        SSLEngine sslEngine=sslHttpChannelEndpoint.getSSLEngine();
        SSLSession sslSession=sslEngine.getSession();
        
        SslCertificates.customize(sslSession,endpoint,request);
    }

    /* ------------------------------------------------------------ */
    /**
     * Checks if is allow renegotiate.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return True if SSL re-negotiation is allowed (default false)
     */
    public boolean isAllowRenegotiate()
    {
        return _allowRenegotiate;
    }

    /* ------------------------------------------------------------ */
    /**
     * Set if SSL re-negotiation is allowed. CVE-2009-3555 discovered
     * a vulnerability in SSL/TLS with re-negotiation.  If your JVM
     * does not have CVE-2009-3555 fixed, then re-negotiation should 
     * not be allowed.
     * @param allowRenegotiate true if re-negotiation is allowed (default false)
     */
    public void setAllowRenegotiate(boolean allowRenegotiate)
    {
        _allowRenegotiate = allowRenegotiate;
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the exclude cipher suites.
     *
     * @return the exclude cipher suites
     * @see org.eclipse.jetty.server.ssl.SslConnector#getExcludeCipherSuites()
     */
    public String[] getExcludeCipherSuites()
    {
        return _excludeCipherSuites;
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the exclude cipher suites.
     *
     * @param cipherSuites the new exclude cipher suites
     * @see org.eclipse.jetty.server.ssl.SslConnector#setExcludeCipherSuites(java.lang.String[])
     */
    public void setExcludeCipherSuites(String[] cipherSuites)
    {
        this._excludeCipherSuites=cipherSuites;
    }
    
    /* ------------------------------------------------------------ */
    /**
     * Gets the include cipher suites.
     *
     * @return the include cipher suites
     * @see org.eclipse.jetty.server.ssl.SslConnector#getExcludeCipherSuites()
     */
    public String[] getIncludeCipherSuites()
    {
        return _includeCipherSuites;
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the include cipher suites.
     *
     * @param cipherSuites the new include cipher suites
     * @see org.eclipse.jetty.server.ssl.SslConnector#setExcludeCipherSuites(java.lang.String[])
     */
    public void setIncludeCipherSuites(String[] cipherSuites)
    {
        this._includeCipherSuites=cipherSuites;
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the password.
     *
     * @param password the new password
     * @see org.eclipse.jetty.server.ssl.SslConnector#setPassword(java.lang.String)
     */
    public void setPassword(String password)
    {
        _password=Password.getPassword(PASSWORD_PROPERTY,password,null);
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the trust password.
     *
     * @param password the new trust password
     * @see org.eclipse.jetty.server.ssl.SslConnector#setTrustPassword(java.lang.String)
     */
    public void setTrustPassword(String password)
    {
        _trustPassword=Password.getPassword(PASSWORD_PROPERTY,password,null);
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the key password.
     *
     * @param password the new key password
     * @see org.eclipse.jetty.server.ssl.SslConnector#setKeyPassword(java.lang.String)
     */
    public void setKeyPassword(String password)
    {
        _keyPassword=Password.getPassword(KEYPASSWORD_PROPERTY,password,null);
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the algorithm.
     *
     * @return the algorithm
     * @deprecated use {@link #getSslKeyManagerFactoryAlgorithm()} or
     * {@link #getSslTrustManagerFactoryAlgorithm()}
     */
    @Deprecated
    public String getAlgorithm()
    {
        return getSslKeyManagerFactoryAlgorithm();
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the algorithm.
     *
     * @param algorithm the new algorithm
     * @deprecated use {@link #setSslKeyManagerFactoryAlgorithm(String)} or
     * {@link #setSslTrustManagerFactoryAlgorithm(String)}
     */
    @Deprecated
    public void setAlgorithm(String algorithm)
    {
        setSslKeyManagerFactoryAlgorithm(algorithm);
        setSslTrustManagerFactoryAlgorithm(algorithm);
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the protocol.
     *
     * @return the protocol
     * @see org.eclipse.jetty.server.ssl.SslConnector#getProtocol()
     */
    public String getProtocol()
    {
        return _protocol;
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the protocol.
     *
     * @param protocol the new protocol
     * @see org.eclipse.jetty.server.ssl.SslConnector#setProtocol(java.lang.String)
     */
    public void setProtocol(String protocol)
    {
        _protocol=protocol;
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the keystore.
     *
     * @param keystore the new keystore
     * @see org.eclipse.jetty.server.ssl.SslConnector#setKeystore(java.lang.String)
     */
    public void setKeystore(String keystore)
    {
        _keystorePath=keystore;
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the keystore.
     *
     * @return the keystore
     * @see org.eclipse.jetty.server.ssl.SslConnector#getKeystore()
     */
    public String getKeystore()
    {
        return _keystorePath;
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the keystore type.
     *
     * @return the keystore type
     * @see org.eclipse.jetty.server.ssl.SslConnector#getKeystoreType()
     */
    public String getKeystoreType()
    {
        return (_keystoreType);
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the need client auth.
     *
     * @return the need client auth
     * @see org.eclipse.jetty.server.ssl.SslConnector#getNeedClientAuth()
     */
    public boolean getNeedClientAuth()
    {
        return _needClientAuth;
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the want client auth.
     *
     * @return the want client auth
     * @see org.eclipse.jetty.server.ssl.SslConnector#getWantClientAuth()
     */
    public boolean getWantClientAuth()
    {
        return _wantClientAuth;
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the need client auth.
     *
     * @param needClientAuth the new need client auth
     * @see org.eclipse.jetty.server.ssl.SslConnector#setNeedClientAuth(boolean)
     */
    public void setNeedClientAuth(boolean needClientAuth)
    {
        _needClientAuth=needClientAuth;
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the want client auth.
     *
     * @param wantClientAuth the new want client auth
     * @see org.eclipse.jetty.server.ssl.SslConnector#setWantClientAuth(boolean)
     */
    public void setWantClientAuth(boolean wantClientAuth)
    {
        _wantClientAuth=wantClientAuth;
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the keystore type.
     *
     * @param keystoreType the new keystore type
     * @see org.eclipse.jetty.server.ssl.SslConnector#setKeystoreType(java.lang.String)
     */
    public void setKeystoreType(String keystoreType)
    {
        _keystoreType=keystoreType;
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the provider.
     *
     * @return the provider
     * @see org.eclipse.jetty.server.ssl.SslConnector#getProvider()
     */
    public String getProvider()
    {
        return _provider;
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the secure random algorithm.
     *
     * @return the secure random algorithm
     * @see org.eclipse.jetty.server.ssl.SslConnector#getSecureRandomAlgorithm()
     */
    public String getSecureRandomAlgorithm()
    {
        return (this._secureRandomAlgorithm);
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the ssl key manager factory algorithm.
     *
     * @return the ssl key manager factory algorithm
     * @see org.eclipse.jetty.server.ssl.SslConnector#getSslKeyManagerFactoryAlgorithm()
     */
    public String getSslKeyManagerFactoryAlgorithm()
    {
        return (this._sslKeyManagerFactoryAlgorithm);
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the ssl trust manager factory algorithm.
     *
     * @return the ssl trust manager factory algorithm
     * @see org.eclipse.jetty.server.ssl.SslConnector#getSslTrustManagerFactoryAlgorithm()
     */
    public String getSslTrustManagerFactoryAlgorithm()
    {
        return (this._sslTrustManagerFactoryAlgorithm);
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the truststore.
     *
     * @return the truststore
     * @see org.eclipse.jetty.server.ssl.SslConnector#getTruststore()
     */
    public String getTruststore()
    {
        return _truststorePath;
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the truststore type.
     *
     * @return the truststore type
     * @see org.eclipse.jetty.server.ssl.SslConnector#getTruststoreType()
     */
    public String getTruststoreType()
    {
        return _truststoreType;
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the provider.
     *
     * @param provider the new provider
     * @see org.eclipse.jetty.server.ssl.SslConnector#setProvider(java.lang.String)
     */
    public void setProvider(String provider)
    {
        _provider=provider;
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the secure random algorithm.
     *
     * @param algorithm the new secure random algorithm
     * @see org.eclipse.jetty.server.ssl.SslConnector#setSecureRandomAlgorithm(java.lang.String)
     */
    public void setSecureRandomAlgorithm(String algorithm)
    {
        this._secureRandomAlgorithm=algorithm;
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the ssl key manager factory algorithm.
     *
     * @param algorithm the new ssl key manager factory algorithm
     * @see org.eclipse.jetty.server.ssl.SslConnector#setSslKeyManagerFactoryAlgorithm(java.lang.String)
     */
    public void setSslKeyManagerFactoryAlgorithm(String algorithm)
    {
        this._sslKeyManagerFactoryAlgorithm=algorithm;
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the ssl trust manager factory algorithm.
     *
     * @param algorithm the new ssl trust manager factory algorithm
     * @see org.eclipse.jetty.server.ssl.SslConnector#setSslTrustManagerFactoryAlgorithm(java.lang.String)
     */
    public void setSslTrustManagerFactoryAlgorithm(String algorithm)
    {
        this._sslTrustManagerFactoryAlgorithm=algorithm;
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the truststore.
     *
     * @param truststore the new truststore
     * @see org.eclipse.jetty.server.ssl.SslConnector#setTruststore(java.lang.String)
     */
    public void setTruststore(String truststore)
    {
        _truststorePath=truststore;
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the truststore type.
     *
     * @param truststoreType the new truststore type
     * @see org.eclipse.jetty.server.ssl.SslConnector#setTruststoreType(java.lang.String)
     */
    public void setTruststoreType(String truststoreType)
    {
        _truststoreType=truststoreType;
    }

    /* ------------------------------------------------------------ */
    /**
     * Sets the ssl context.
     *
     * @param sslContext the new ssl context
     * @see org.eclipse.jetty.server.ssl.SslConnector#setSslContext(javax.net.ssl.SSLContext)
     */
    public void setSslContext(SSLContext sslContext) 
    {
        _context = sslContext;
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the ssl context.
     *
     * @return the ssl context
     * @see org.eclipse.jetty.server.ssl.SslConnector#setSslContext(javax.net.ssl.SSLContext)
     */
    public SSLContext getSslContext()
    {
        try
        {
            if (_context == null)
                _context=createSSLContext();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
         
        return _context;
    }

    /* ------------------------------------------------------------ */
    /**
     * By default, we're confidential, given we speak SSL. But, if we've been
     * told about an confidential port, and said port is not our port, then
     * we're not. This allows separation of listeners providing INTEGRAL versus
     * CONFIDENTIAL constraints, such as one SSL listener configured to require
     * client certs providing CONFIDENTIAL, whereas another SSL listener not
     * requiring client certs providing mere INTEGRAL constraints.
     *
     * @param request the request
     * @return true, if is confidential
     */
    @Override
    public boolean isConfidential(Request request)
    {
        final int confidentialPort=getConfidentialPort();
        return confidentialPort==0||confidentialPort==request.getServerPort();
    }

    /* ------------------------------------------------------------ */
    /**
     * By default, we're integral, given we speak SSL. But, if we've been told
     * about an integral port, and said port is not our port, then we're not.
     * This allows separation of listeners providing INTEGRAL versus
     * CONFIDENTIAL constraints, such as one SSL listener configured to require
     * client certs providing CONFIDENTIAL, whereas another SSL listener not
     * requiring client certs providing mere INTEGRAL constraints.
     *
     * @param request the request
     * @return true, if is integral
     */
    @Override
    public boolean isIntegral(Request request)
    {
        final int integralPort=getIntegralPort();
        return integralPort==0||integralPort==request.getServerPort();
    }

    /* ------------------------------------------------------------------------------- */
    /**
     * New end point.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param channel the channel
     * @param selectSet the select set
     * @param key the key
     * @return the select channel end point
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    protected SelectChannelEndPoint newEndPoint(SocketChannel channel, SelectSet selectSet, SelectionKey key) throws IOException
    {
        SslSelectChannelEndPoint endp = new SslSelectChannelEndPoint(_sslBuffers,channel,selectSet,key,createSSLEngine(), RISslSelectChannelConnector.this._maxIdleTime);
        endp.setAllowRenegotiate(_allowRenegotiate);
        return endp;
    }

    /* ------------------------------------------------------------------------------- */
    /**
     * New connection.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param channel the channel
     * @param endpoint the endpoint
     * @return the connection
     */
    @Override
    protected Connection newConnection(SocketChannel channel, SelectChannelEndPoint endpoint)
    {
        RIHttpConnection connection=(RIHttpConnection)super.newConnection(channel,endpoint);
        ((HttpParser)connection.getParser()).setForceContentBuffer(true);
        return connection;
    }

    /* ------------------------------------------------------------ */
    /**
     * Creates the ssl engine.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the sSL engine
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected SSLEngine createSSLEngine() throws IOException
    {
        SSLEngine engine = null;
        try
        {
            engine = _context.createSSLEngine();
            engine.setUseClientMode(false);

            if (_wantClientAuth)
                engine.setWantClientAuth(_wantClientAuth);
            if (_needClientAuth)
                engine.setNeedClientAuth(_needClientAuth);

            if ((_excludeCipherSuites != null && _excludeCipherSuites.length > 0) || (_includeCipherSuites != null && _includeCipherSuites.length > 0))
            {
                List<String> includedCSList;
                if (_includeCipherSuites != null)
                {
                    includedCSList = Arrays.asList(_includeCipherSuites);
                }
                else
                {
                    includedCSList = new ArrayList<String>();
                }
                List<String> excludedCSList;
                if (_excludeCipherSuites != null)
                {
                    excludedCSList = Arrays.asList(_excludeCipherSuites);
                }
                else
                {
                    excludedCSList = new ArrayList<String>();
                }
                String[] enabledCipherSuites = engine.getEnabledCipherSuites();
                List<String> enabledCSList = new ArrayList<String>(Arrays.asList(enabledCipherSuites));

                String[] supportedCipherSuites = engine.getSupportedCipherSuites();
                List<String> supportedCSList = Arrays.asList(supportedCipherSuites);

                for (String cipherName : includedCSList)
                {
                    if ((!enabledCSList.contains(cipherName)) && supportedCSList.contains(cipherName))
                    {
                        enabledCSList.add(cipherName);
                    }
                }

                for (String cipherName : excludedCSList)
                {
                    if (enabledCSList.contains(cipherName))
                    {
                        enabledCSList.remove(cipherName);
                    }
                }
                enabledCipherSuites = enabledCSList.toArray(new String[0]);

                engine.setEnabledCipherSuites(enabledCipherSuites);
            }
        }
        catch (Exception e)
        {
            Log.warn("Error creating sslEngine -- closing this connector",e);
            close();
            throw new IllegalStateException(e);
        }
        return engine;
    }
   
    /**
     * Do start.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @throws Exception the exception
     */
    @Override
    protected void doStart() throws Exception
    {
    	if (_context == null)
           _context=createSSLContext();
        
        SSLEngine engine=createSSLEngine();
        SSLSession ssl_session=engine.getSession();
        
        ThreadLocalBuffers buffers = new ThreadLocalBuffers()
        {
            @Override
            protected Buffer newBuffer(int size)
            {
                if (getUseDirectBuffers())
                    return new DirectNIOBuffer(size);
                return new IndirectNIOBuffer(size);
            }
            @Override
            protected Buffer newHeader(int size)
            {
                if (getUseDirectBuffers())
                    return new DirectNIOBuffer(size);
                return new IndirectNIOBuffer(size);
            }
            @Override
            protected boolean isHeader(Buffer buffer)
            {
                return true;
            }
        };
        buffers.setBufferSize(ssl_session.getApplicationBufferSize());
        buffers.setHeaderSize(ssl_session.getApplicationBufferSize());
        _sslBuffers=buffers;
        
        if (getRequestHeaderSize()<ssl_session.getApplicationBufferSize())
            setRequestHeaderSize(ssl_session.getApplicationBufferSize());
        if (getRequestBufferSize()<ssl_session.getApplicationBufferSize())
            setRequestBufferSize(ssl_session.getApplicationBufferSize());
        
        super.doStart();
    }

    /* ------------------------------------------------------------ */
    /**
     * Creates the ssl context.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the sSL context
     * @throws Exception the exception
     */
    protected SSLContext createSSLContext() throws Exception
    {
        KeyManager[] keyManagers=getKeyManagers();

        TrustManager[] trustManagers=getTrustManagers();

        SecureRandom secureRandom=_secureRandomAlgorithm==null?null:SecureRandom.getInstance(_secureRandomAlgorithm);
        SSLContext context=_provider==null?SSLContext.getInstance(_protocol):SSLContext.getInstance(_protocol,_provider);
        context.init(keyManagers,trustManagers,secureRandom);
        return context;
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the key managers.
     *
     * @return the key managers
     * @throws Exception the exception
     */
    protected KeyManager[] getKeyManagers() throws Exception
    {
        KeyStore keyStore = getKeyStore(_keystorePath, _keystoreType, _password==null?null:_password.toString());
        
        KeyManagerFactory keyManagerFactory=KeyManagerFactory.getInstance(_sslKeyManagerFactoryAlgorithm);
        keyManagerFactory.init(keyStore,_keyPassword==null?(_password==null?null:_password.toString().toCharArray()):_keyPassword.toString().toCharArray());
        return keyManagerFactory.getKeyManagers();
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the trust managers.
     *
     * @return the trust managers
     * @throws Exception the exception
     */
    protected TrustManager[] getTrustManagers() throws Exception
    {        
        if (_truststorePath==null)
        {
            _truststorePath=_keystorePath;
            _truststoreType=_keystoreType;
            _trustPassword = _password;
            _sslTrustManagerFactoryAlgorithm = _sslKeyManagerFactoryAlgorithm;
        }
        
        KeyStore trustStore = getKeyStore(_truststorePath, _truststoreType, _trustPassword==null?null:_trustPassword.toString());

        TrustManagerFactory trustManagerFactory=TrustManagerFactory.getInstance(_sslTrustManagerFactoryAlgorithm);
        trustManagerFactory.init(trustStore);
        return trustManagerFactory.getTrustManagers();
    }

    /* ------------------------------------------------------------ */
    /**
     * Gets the key store.
     *
     * @param keystorePath the keystore path
     * @param keystoreType the keystore type
     * @param keystorePassword the keystore password
     * @return the key store
     * @throws Exception the exception
     */
    protected KeyStore getKeyStore(String keystorePath, String keystoreType, String keystorePassword) throws Exception
    {
    	KeyStore keystore;
    	InputStream keystoreInputStream = null;
    	try
        {
            if (keystorePath!=null)
                keystoreInputStream = Resource.newResource(keystorePath).getInputStream();
            keystore=KeyStore.getInstance(keystoreType);
            keystore.load(keystoreInputStream,keystorePassword==null?null:keystorePassword.toString().toCharArray());
            return keystore;
        }
        finally
        {
            if (keystoreInputStream != null)
            	keystoreInputStream.close();
        }
    }

}
