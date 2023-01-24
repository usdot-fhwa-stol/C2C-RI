/*
 * Copyright 2004 - 2007 University of Cardiff.
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
package org.fhwa.c2cri.utilities;

import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.util.jar.JarFile;

/**
 * Verifies a signed jar file given an keystore.  For open source pass all.
 *
 */
public class RISignedJarVerifier {


    /**
     * Check to ensure the the jar file is properly signed.
     * 
     * @param jf - the jar file to be inspected
     * @param ks - the keystore file containing certificates
     * @throws IOException
     * @throws CertificateException 
     */
    public static void verify(JarFile jf, KeyStore ks) throws IOException, CertificateException {
        // For open source all jar files will pass this test.
        
    }

}
