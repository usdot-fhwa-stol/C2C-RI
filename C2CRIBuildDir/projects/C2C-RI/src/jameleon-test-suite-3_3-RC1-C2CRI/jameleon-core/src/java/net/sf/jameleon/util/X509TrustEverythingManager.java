/*
    Jameleon - An automation testing tool..
    Copyright (C) 2006 Christian W. Hargraves (engrean@hotmail.com)
    
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

import javax.net.ssl.X509TrustManager;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * A X509TrustManager that trusts everything. This is used for those sites where the 
 * SSL is known to be invalid.
 */
public class X509TrustEverythingManager implements X509TrustManager {

    /**
     * Constructor for EasyX509TrustManager.
     */
    public X509TrustEverythingManager() {
    }

    /**
     * Constructor for EasyX509TrustManager.
     */
    public X509TrustEverythingManager(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
    }

    public boolean isClientTrusted(X509Certificate[] chain){ 
        return true;
    }

    public boolean isServerTrusted(X509Certificate[] chain){
        return true;
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        //Yes the server is trusted
    }                

    public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        //Yes the client is trusted
    }            
}
