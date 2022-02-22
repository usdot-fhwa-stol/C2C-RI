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

import java.io.File;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.jameleon.function.FunctionTag;

import org.apache.tools.ant.launch.Locator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * This is the launcher for Jameleon. It loads in all required
 * libraries.
 * NOTE: I started this code from Apache Ant Launcher as an
 * example.
 */
public class JameleonLauncher {
    /**
     * The system property name that is used to specify the location
     * of the jameleon installation
     */ 
    public static final String JAMELEON_HOME_PROPERTY = "jameleon.home";
    public static final String MAIN_CLASS = "net.sf.jameleon.ui.JameleonUI";

    public static void main (String args[]) throws Exception{

    	JameleonLauncher jameleon = new JameleonLauncher();
        try{
            jameleon.launch();
        }catch (JameleonLaunchException jle){
            System.err.println(jle.getMessage());
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

    public void launch() throws JameleonLaunchException, MalformedURLException{
        File jameleonHome = calculateJameleonHome();
        List pathUrls = new ArrayList();
        addPath(jameleonHome.getAbsolutePath()+File.separator+"lib", true, pathUrls);

        URL[] jars = (URL[]) pathUrls.toArray(new URL[0]);

        StringBuffer baseClassPath
            = new StringBuffer(System.getProperty("java.class.path"));
        if (baseClassPath.charAt(baseClassPath.length() - 1)
                == File.pathSeparatorChar) {
            baseClassPath.setLength(baseClassPath.length() - 1);
        }

        for (int i = 0; i < jars.length; ++i) {
            baseClassPath.append(File.pathSeparatorChar);
            baseClassPath.append(Locator.fromURI(jars[i].toString()));
        }

        System.setProperty("java.class.path", baseClassPath.toString());

        URLClassLoader loader = new URLClassLoader(jars);
        Thread.currentThread().setContextClassLoader(loader);
        try {

        /*
         *   *****************Added for RI Debugging*****************8
         */
    	PropertyConfigurator.configure("log4j.properties");
        /*
         *   *****************End of Added for RI Debugging*****************8
         */

            Class mainClass = loader.loadClass(MAIN_CLASS);
            JameleonMain main = (JameleonMain) mainClass.newInstance();
            main.startJameleon();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
      * Add a CLASSPATH or -lib to lib path urls.
      * This method was directly copied over from
      * org.apache.tools.ant.launch.Launcher
      * @param path        the classpath or lib path to add to the libPathULRLs
      * @param getJars     if true and a path is a directory, add the jars in
      *                    the directory to the path urls
      * @param libPathURLs the list of paths to add to
      */
    private void addPath(String path, boolean getJars, List libPathURLs)
        throws MalformedURLException {
        StringTokenizer myTokenizer = 
            new StringTokenizer(path, System.getProperty("path.separator"));

        while (myTokenizer.hasMoreElements()) {
            String elementName = myTokenizer.nextToken();
            File element = new File(elementName);
            if (elementName.indexOf("%") != -1 && !element.exists()) {
                continue;
            }
            if (getJars && element.isDirectory()) {
                // add any jars in the directory
                URL[] dirURLs = Locator.getLocationURLs(element);
                for (int j = 0; j < dirURLs.length; ++j) {
                    libPathURLs.add(dirURLs[j]);
                }
            }

            libPathURLs.add(element.toURL());
        }
    }

    public File calculateJameleonHome() throws JameleonLaunchException{
        String jameleonHomeValue = System.getProperty(JAMELEON_HOME_PROPERTY);
        File jameleonHome = null;

        File launcherJar = Locator.getClassSource(getClass());
        
        //Default the lib directory to the location of the launcherJar because the class may not
        //be in a jar file.
        File libDir = launcherJar;
        if (launcherJar.exists() && !launcherJar.isDirectory()){
        	libDir = launcherJar.getParentFile();
        }

        if (jameleonHomeValue != null) {
            jameleonHome = new File(jameleonHomeValue);
        }

        if (jameleonHome == null || !jameleonHome.exists()) {
            jameleonHome = libDir.getParentFile();
            System.setProperty(JAMELEON_HOME_PROPERTY, jameleonHome.getAbsolutePath());
        }

        if (!jameleonHome.exists()) {
            throw new JameleonLaunchException("Jameleon home '"+JAMELEON_HOME_PROPERTY+
                                              "' is set incorrectly or Jameleon could not be located");
        }
        return jameleonHome;
    }
}
