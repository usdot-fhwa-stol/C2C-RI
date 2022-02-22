/*
 */
package org.fhwa.c2cri.gui;

import java.io.File;
import java.lang.reflect.Method;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;
import org.apache.log4j.Level;


import org.apache.tools.ant.launch.Locator;
import org.apache.log4j.Logger;
import org.fhwa.c2cri.testmodel.TestSuites;

/**
 * This is the launcher for the C2CRI. It loads in all required
 * libraries.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class C2CRILauncher {

    /** The Constant C2CRI_HOME_PROPERTY. */
    public static final String C2CRI_HOME_PROPERTY = "c2cri.home";
    
    /** The Constant MAIN_CLASS. */
    public static final String MAIN_CLASS = "org.fhwa.c2cri.gui.C2CMainUI";

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String args[]) throws Exception {

        C2CRILauncher c2cri = new C2CRILauncher();
        try {
            c2cri.launch();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Launch.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @throws MalformedURLException the malformed url exception
     * @throws Exception the exception
     */
    public void launch() throws MalformedURLException, Exception {
        File c2criHome = calculateC2CRIHome();
        List pathUrls = new ArrayList();
        addPath(c2criHome.getAbsolutePath() + File.separator + "dist" + File.separator + "lib", true, pathUrls);

        File subDirs = new File(c2criHome.getAbsolutePath() + File.separator + "dist" + File.separator + "lib");
        if (subDirs.exists() && subDirs.isDirectory()) {
            File[] subFiles = subDirs.listFiles();
            for (int ii = 0; ii < subFiles.length; ii++) {
                if (subFiles[ii].isDirectory()) {
                    addPath(subFiles[ii].getAbsolutePath(), true, pathUrls);
                }
            }
        }

        URL[] jars = (URL[]) pathUrls.toArray(new URL[0]);

        StringBuffer baseClassPath = new StringBuffer(System.getProperty("java.class.path"));
        if (baseClassPath.charAt(baseClassPath.length() - 1)
                == File.pathSeparatorChar) {
            baseClassPath.setLength(baseClassPath.length() - 1);
        }
        Logger.getLogger(C2CRILauncher.class.getName()).log(Level.INFO, "*** OriginalBaseClassPath ****\n" + baseClassPath);

        Logger.getLogger(C2CRILauncher.class.getName()).log(Level.INFO, "*** Found Jars List: ****");
        for (int i = 0; i < jars.length; ++i) {
            baseClassPath.append(File.pathSeparatorChar);
            baseClassPath.append(Locator.fromURI(jars[i].toString()));
            Logger.getLogger(C2CRILauncher.class.getName()).log(Level.INFO, jars[i].toString());

        }

        System.setProperty("java.class.path", baseClassPath.toString());
        Logger.getLogger(C2CRILauncher.class.getName()).log(Level.INFO, "\n\n*** FinalBaseClassPath ****\n" + baseClassPath);

        ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();
        URLClassLoader loader = new URLClassLoader(jars,sysClassLoader);
        Thread.currentThread().setContextClassLoader(loader);
        Class<URLClassLoader> classLoaderClass = URLClassLoader.class;
        for (URL url : jars) {
            try {
                Method method = classLoaderClass.getDeclaredMethod("addURL", new Class[]{URL.class});
                method.setAccessible(true);
                method.invoke(loader, new Object[]{url});
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        
//        /** Hack to add the URLs to the SystemClassLoader */
//        ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();
//        URLClassLoader sysURLClassLoader = (URLClassLoader) sysClassLoader;
//        Class<URLClassLoader> classLoaderClass = URLClassLoader.class;
//        for (URL url : jars) {
//            try {
//                Method method = classLoaderClass.getDeclaredMethod("addURL", new Class[]{URL.class});
//                method.setAccessible(true);
//                method.invoke(sysURLClassLoader, new Object[]{url});
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }

        try{
           Executor taskExecutor = Executors.newFixedThreadPool(1);
           taskExecutor.execute(TestSuites.getInstance());
           
        } catch (Throwable t) {
            t.printStackTrace();
        }

        try {


            Class mainClass = loader.loadClass(MAIN_CLASS);
            final C2CMainUI main = (C2CMainUI) mainClass.newInstance();
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    main.setVisible(true);
                }
            });

        } catch (Throwable t) {
            t.printStackTrace();
        }
        
        
    }

    /**
     * Add a CLASSPATH or -lib to lib path urls.
     * This method was directly copied over from
     * org.apache.tools.ant.launch.Launcher
     *
     * @param path        the classpath or lib path to add to the libPathULRLs
     * @param getJars     if true and a path is a directory, add the jars in
     * the directory to the path urls
     * @param libPathURLs the list of paths to add to
     * @throws MalformedURLException the malformed url exception
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

            libPathURLs.add(element.toURI().toURL());
        }
    }

    /**
     * Calculate c2 cri home.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the file
     * @throws Exception the exception
     */
    public File calculateC2CRIHome() throws Exception {
        String c2criHomeValue = System.getProperty(C2CRI_HOME_PROPERTY);
        File c2criHome = null;

        File launcherJar = Locator.getClassSource(getClass());

        //Default the lib directory to the location of the launcherJar because the class may not
        //be in a jar file.
        File libDir = launcherJar;
        if (launcherJar.exists() && !launcherJar.isDirectory()) {
            libDir = launcherJar.getParentFile();
        }

        if (c2criHomeValue != null) {
            c2criHome = new File(c2criHomeValue);
        }

        if (c2criHome == null || !c2criHome.exists()) {
            c2criHome = libDir.getParentFile();
            System.setProperty(C2CRI_HOME_PROPERTY, c2criHome.getAbsolutePath());
        }

        if (!c2criHome.exists()) {
            throw new Exception("C2CRI home '" + C2CRI_HOME_PROPERTY
                    + "' is set incorrectly or C2CRI could not be located");
        }
        return c2criHome;
    }
}
