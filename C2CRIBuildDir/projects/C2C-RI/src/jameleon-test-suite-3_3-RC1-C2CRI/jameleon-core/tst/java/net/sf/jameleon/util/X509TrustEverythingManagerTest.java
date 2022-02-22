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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class X509TrustEverythingManagerTest extends TestCase {

    private X509TrustEverythingManager trustManager;

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( X509TrustEverythingManagerTest.class );
    }

    public X509TrustEverythingManagerTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception{
        trustManager = new X509TrustEverythingManager();
    }

    public void tearDown(){
    }

    
    public void testIsClientTrusted(){
        assertTrue(trustManager.isClientTrusted(null));
    }

    public void testIsServerTrusted(){
        assertTrue(trustManager.isServerTrusted(null));
    }

    public void testGetAcceptedIssuers() {
        assertEquals(0, trustManager.getAcceptedIssuers().length);
    }

    public void testCheckServerTrusted() throws Exception {
        trustManager.checkServerTrusted(null,null);
    }                

    public void testCheckClientTrusted() throws Exception {
        trustManager.checkClientTrusted(null,null);
    }            

}
