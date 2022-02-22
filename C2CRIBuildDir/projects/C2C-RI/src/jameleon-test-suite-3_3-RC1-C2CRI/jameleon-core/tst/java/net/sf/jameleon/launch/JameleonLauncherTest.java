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
package net.sf.jameleon.launch;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;

public class JameleonLauncherTest extends TestCase {

    private JameleonLauncher launcher;

    public static Test suite() {
        return new TestSuite(JameleonLauncherTest.class);
    }

    public void setUp(){
        launcher = new JameleonLauncher();
    }

    public void testCalculateJameleonHome() throws Exception{
        File jameleonHome = launcher.calculateJameleonHome();
        File cwd = new File(".");
        String absolutePath = cwd.getAbsolutePath();
        absolutePath = absolutePath.substring(0, absolutePath.length() - 2);
        assertTrue("jameleon-launcher location based on jar file", absolutePath.equalsIgnoreCase(jameleonHome.getAbsolutePath()));
        File tstDir = new File(cwd, "tst/java");
        System.setProperty(JameleonLauncher.JAMELEON_HOME_PROPERTY, tstDir.getAbsolutePath());
        absolutePath = tstDir.getAbsolutePath();
        jameleonHome = launcher.calculateJameleonHome();
        assertTrue("jameleon-launcher location based on property setting", absolutePath.equalsIgnoreCase(jameleonHome.getAbsolutePath()));
    }

}
