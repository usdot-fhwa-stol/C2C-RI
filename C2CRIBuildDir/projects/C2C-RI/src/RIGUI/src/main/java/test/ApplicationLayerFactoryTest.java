/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.ServiceLoader;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import org.fhwa.c2cri.applayer.ApplicationLayerStandard;
import org.fhwa.c2cri.applayer.ApplicationLayerStandardFactory;
import org.fhwa.c2cri.infolayer.InformationLayerStandard;

/**
 * The Class ApplicationLayerFactoryTest.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ApplicationLayerFactoryTest {

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String args[]) {
    
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        
         dumpClasspath(cl);

//        URL[] urls = ((URLClassLoader)cl).getURLs();
// 
//        for(URL url: urls){
//        	System.out.println(url.getFile());
//        }
        
    ServiceLoader<ApplicationLayerStandard> implementation = ServiceLoader.load(ApplicationLayerStandard.class,cl);
    for (ApplicationLayerStandard impl : implementation) {
       System.out.println(impl.getClass().getName());
    } 
      
    try{
 PluginManager pm = PluginManagerFactory.createPluginManager();
 File pluginFile = new File("TestSuites/");
 System.out.println(pluginFile.getAbsolutePath());
 pm.addPluginsFrom(pluginFile.toURI());  
 ApplicationLayerStandard theAppStd = pm.getPlugin(ApplicationLayerStandard.class);
 System.out.println(theAppStd.getName());
 InformationLayerStandard theInfoStd = pm.getPlugin(InformationLayerStandard.class);
 System.out.println(theInfoStd.getName());
    } catch (Exception ex){
        ex.printStackTrace();
    }
 
        ApplicationLayerStandardFactory theStandardFactory = ApplicationLayerStandardFactory.getInstance();
        theStandardFactory.setApplicationLayerStandard("NTCIP2306v01");
        theStandardFactory.setCenterMode("EC");
        theStandardFactory.setInformationLayerStandard("TMDDV301");
        theStandardFactory.setTestCase("TestCaseName");
        theStandardFactory.setRequestDialog("OP_DlCenterActiveVerificationRequest");
        theStandardFactory.setSubscriptionDialog("OP_DlCenterActiveVerificationSubscription");
        theStandardFactory.setPublicationDialog("DlDMSInventoryUpdate");
        theStandardFactory.setTestConfigSpecificationURL("file:/c:/c2cri/testfiles/release3.wsdl");
        theStandardFactory.setTestSuiteSpecificationURL(null);
        try{
            
//            Enumeration<URL> list = theStandardFactory.getClass().getClassLoader().getResources("org.fhwa.c2cri.applayer.ApplicationLayerStandard");
            Enumeration<URL> list = Thread.currentThread().getContextClassLoader().getResources("org/fhwa/c2cri/applayer/ApplicationLayerFactory");
//            Enumeration<URL> list = theStandardFactory.getClass().getClassLoader().getResources("org.fhwa.c2cri.applayer.ApplicationLayerFactory");
            if (list.hasMoreElements()){
                System.out.println("yes!!!  Found some.");
            } else {
                System.out.println("Nope!!!  Found none.");                
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        try {
            ApplicationLayerStandard theStandard = theStandardFactory.getApplicationStandard();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    

    /**
     * Dump classpath.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param loader the loader
     */
    public static void dumpClasspath(ClassLoader loader)
    {
        System.out.println("Classloader " + loader + ":");

        if (loader instanceof URLClassLoader)
        {
            URLClassLoader ucl = (URLClassLoader)loader;
        for(URL url: ucl.getURLs()){
        	System.out.println("      "+url.getFile());                
        }
//            System.out.println("\t" + Arrays.toString(ucl.getURLs()));
        }
        else
            System.out.println("\t(cannot display components as not a URLClassLoader)");

        if (loader.getParent() != null)
            dumpClasspath(loader.getParent());
    }

}
