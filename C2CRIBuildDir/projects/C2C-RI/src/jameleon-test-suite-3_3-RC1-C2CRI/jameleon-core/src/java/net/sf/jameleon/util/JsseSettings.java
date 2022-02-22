/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2006 Christian W. Hargraves (engrean@hotmail.com)
    
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon.util;

import com.sun.net.ssl.HostnameVerifier;
import com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.KeyManagementException;

/**
 * Utility to override the JSSE settings for SSL certificate checking
 * This can be used to disable the SSL certificate checking normally done by Java
 */
public class JsseSettings {
    
    /**
     * don't instantiate this class
     */
    private JsseSettings(){
    }
    
    public static void disableSslCertificateChecking() {

        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustEverythingManager()};

        HostnameVerifier doNotVerifyHost = new HostnameVerifier() {
            public boolean verify(String hostname, String session) { return true; }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnectionOldImpl.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnectionOldImpl.setDefaultHostnameVerifier(doNotVerifyHost);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

}
