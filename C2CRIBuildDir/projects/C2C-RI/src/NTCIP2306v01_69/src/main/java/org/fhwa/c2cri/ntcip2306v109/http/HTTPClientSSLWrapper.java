/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.http;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Settings;
import org.fhwa.c2cri.utilities.RIParameters;
import test.CleanSSLSocketFactory;

/**
 *When developing a https application, your test server often doesn’t have a (valid) SSL certificate.
 *This will cause the following exception to be thrown when connecting your client to the test server:
 * "javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated".

 * We will need to tell the client to use a different TrustManager. A TrustManager (http://download.oracle.com/docs/cd/E17476_01/javase/1.5.0/docs/api/javax/net/ssl/TrustManager.html)
 * is a class that checks if given credentials (or certificates) are valid. The scheme used by SSL is called X.509 (http://en.wikipedia.org/wiki/X.509), and Java has a specific TrustManager
 * for this scheme, called X509TrustManager.

 * @author Transcore ITS (developed using mathiasdegroof's web contribution)
 * Last Update:  11/3/2013
 */
public class HTTPClientSSLWrapper {

    /**
     * Wrap client.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param base the base
     * @return the default http client
     */
    public static DefaultHttpClient wrapClient(DefaultHttpClient base) {

        try {

            String tlsProtocolVersion = "TLSv1.2";
            try {
                tlsProtocolVersion = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.HTTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.HTTP_CLIENT_CONNECTION_TLS_PARAMETER, NTCIP2306Settings.HTTP_CLIENT_CONNECTION_TLS_DEFAULT_VALUE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            SSLContext ctx = SSLContext.getInstance(tlsProtocolVersion);

            X509TrustManager tm = new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {

                    return null;

                }
            };

            ctx.init(null, new TrustManager[]{tm}, null);

           CleanSSLSocketFactory ssf = new CleanSSLSocketFactory(null, "c2cri1", null);

            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ssf.setSecureMode(false);
            ClientConnectionManager ccm = base.getConnectionManager();

            SchemeRegistry sr = ccm.getSchemeRegistry();

            sr.register(new Scheme("https", ssf, 443));

            return new DefaultHttpClient(ccm, base.getParams());

        } catch (Exception ex) {

            ex.printStackTrace();

            return null;

        }

    }
}


