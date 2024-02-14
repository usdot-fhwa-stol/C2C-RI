/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testmodel;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;
import org.fhwa.c2cri.applayer.ListenerManager;

/**
 * This class extends the Jameleon ExecuteTestCase class in order
 * to be able to pass a URL for a script file to the underlying Jelly
 * framework.  It already had a method that accepts the URL, but Jameleon
 * did not provide a means to utilize it.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class RIExecuteTestCase extends net.sf.jameleon.ExecuteTestCase {

    /** The test configuration. */
    private TestConfiguration testConfiguration;
    
    /** The test case script url. */
    private URL testCaseScriptURL = RIExecuteTestCase.class.getResource("/org/fhwa/c2cri/testmodel/TestCaseLauncher.xml");

    /** The Information Layer Standard to be used in Emulation. */
    private String emulationInfoStandard;
    
    /** The Application Layer Standard to be used in Emulation. */
    private String emulationAppStandard;
    
    /**
     * Instantiates a new rI execute test case.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private RIExecuteTestCase(){
        super();
        testCaseScriptURL = RIExecuteTestCase.class.getResource("/org/fhwa/c2cri/testmodel/TestCaseLauncher.xml");
    }

    /**
     * Instantiates a new rI execute test case.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param theConfig the the config
     */
    public RIExecuteTestCase(TestConfiguration theConfig, String emulationInformationLayerStandard, String emulationApplicationLayerStandard){
        super();
        testConfiguration = theConfig;
        emulationInfoStandard = emulationInformationLayerStandard;
        emulationAppStandard = emulationApplicationLayerStandard;
//        testCaseScriptURL = RIExecuteTestCase.class.getResource("/org/fhwa/c2cri/testmodel/TestCaseLauncher.xml");
    }

/**
 * This method is added to allow for the running of a script referenced as a URL
 * instead of a File object.  It is basically the same method as the runScrpt method included in
 * ExecuteTestCase, with that one exception.
 *
 * @param tc the tc
 * @return - JellyContext
 * @throws JellyException the jelly exception
 * @throws FileNotFoundException the file not found exception
 */
   public JellyContext runScript(TestCase tc) throws JellyException, FileNotFoundException{
       // See if the URL is valid before running the script.
       URL file;
       String filePath=tc.getName();
       try {
         filePath = tc.getScriptUrlLocation().getPath();
         file = tc.getScriptUrlLocation();
         InputStream is =  file.openStream();
         is.close();
       } catch (Exception e){
            throw new FileNotFoundException(filePath +" not found!");
       }
        XMLOutput out = XMLOutput.createDummyXMLOutput();
        JellyContext context = new JellyContext();
        HashMap combinedMap = testConfiguration.getAppLayerParams().getProperties();
        HashMap infoMap = testConfiguration.getInfoLayerParams().getProperties();
        combinedMap.putAll(infoMap);

        HashMap appTestCaseMap = new HashMap();
        // Add App Layer Test Case Data Sources
        for (TestCase thisCase : testConfiguration.getAppLayerParams().getTestCases().testCases){
            if (thisCase.isOverriden()){
                appTestCaseMap.put(thisCase.getName().replace("-", "_")+"_data", thisCase.getCustomDataLocation());
            } else
                try{
                   appTestCaseMap.put(thisCase.getName().replace("-", "_")+"_data", thisCase.getDataUrlLocation().toExternalForm());
                }catch (Exception ex) {
                   System.err.println("Unable to add test case data location to the hashmap");
                   ex.printStackTrace();
                }
        }
        // Add App Layer Test Case Data Sources
        HashMap infoTestCaseMap = new HashMap();
        for (TestCase thisCase : testConfiguration.getInfoLayerParams().getTestCases().testCases){
            if (thisCase.isOverriden()){
                infoTestCaseMap.put(thisCase.getName().replace("-", "_")+"_data", thisCase.getCustomDataLocation());
            } else
                try{
                   infoTestCaseMap.put(thisCase.getName().replace("-", "_")+"_data", thisCase.getDataUrlLocation().toExternalForm());
                }catch (Exception ex) {
                   System.err.println("Unable to add test case data location to the hashmap");
                   ex.printStackTrace();
                }
        }
        combinedMap.putAll(appTestCaseMap);
        combinedMap.putAll(infoTestCaseMap);

        HashMap sutParametersMap = new HashMap();
        sutParametersMap.put("RI_HOSTNAME", testConfiguration.getSutParams().getHostName());
        sutParametersMap.put("RI_IPADDRESS", testConfiguration.getSutParams().getIpAddress());
        sutParametersMap.put("RI_PORT", testConfiguration.getSutParams().getIpPort());
        sutParametersMap.put("RI_USERNAME", testConfiguration.getSutParams().getUserName());
        sutParametersMap.put("RI_PASSWORD", testConfiguration.getSutParams().getPassword());
        sutParametersMap.put("RI_WEBSERVICEURL", testConfiguration.getSutParams().getWebServiceURL());
        combinedMap.putAll(sutParametersMap);

        try{
           combinedMap.put("C2CRITestCaseID", tc.getName());
           combinedMap.put("RI_BASETCDATAFILE", tc.getDataUrlLocation().toString());
           combinedMap.put("RI_USERTCDATAFILE", tc.getCustomDataLocation());
           combinedMap.put("C2CRITestCaseScriptFile", tc.getScriptUrlLocation().toString());
           if (emulationAppStandard != null){
               combinedMap.put("C2CRIEmulationApplicationLayerStandard", emulationAppStandard);               
           }
           if (emulationInfoStandard != null){
               combinedMap.put("C2CRIEmulationInformationLayerStandard", emulationInfoStandard);                              
           }
        } catch (Exception ex) {
           ex.printStackTrace();
        }
        
        
        System.out.println("\n\n\n");
        try (ScanResult scanResult =                // Assign scanResult in try-with-resources
                new ClassGraph()                    // Create a new ClassGraph instance
//            .verbose()                      // If you want to enable logging to stderr
            .enableAllInfo()                // Scan classes, methods, fields, annotations
            .whitelistPackages("org.fhwa.c2cri.applayer")   // Scan com.xyz and subpackages
            .scan()) {                      // Perform the scan and return a ScanResult
            // Use the ScanResult within the try block, e.g.
           try (FileWriter writer = new FileWriter("ClassFile.txt", true))
		   {
			for (ClassInfo widgetClassInfo : scanResult.getAllClasses()){
				 System.out.println(widgetClassInfo.getClasspathElementFile().getPath()+": "+widgetClassInfo.getName());
				 writer.write("RIExecuteTestCase-  " + widgetClassInfo.getClasspathElementFile().getPath()+": "+widgetClassInfo.getName()+"\n");
			 }
		   }
        } catch (IOException ioex){
            ioex.printStackTrace();
        }
        System.out.println("\n\n\n");
        
        //This seems to cause jelly to max out on memory if set.
        //context.setCacheTags(false);
        JellyContext scriptContext = null;
        try{
            ListenerManager.getInstance().setTestCaseID(tc.getName());
            context.setVariables(combinedMap);
            context.registerTagLibrary("jelly:jameleon", tagLibrary);
            setContextVariables(context);
//            ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();
//            URL[] urls = ((URLClassLoader) sysClassLoader).getURLs();
//            String urlList = "";
//            for (int i = 0; i < urls.length; i++) {
//                urlList = urlList.concat(urls[i].getFile() + "\n");
//            }
//            log.debug("RIExecuteTestCase:  List of URLS Available: \n" + urlList);
//            urlList = "";
            scriptContext = context.runScript(testCaseScriptURL, out);
        } catch (Error er){
            System.out.println(er.getMessage());
            er.printStackTrace();            
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }finally{
            try{
                out.close();
                context.clear();
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
            out = null;
        }
        if (scriptContext != null){
            scriptContext.clear();
        } else {
            System.out.println("RIExecuteTestCase:runScript scriptContext==null.");
        }
        combinedMap.clear();
        appTestCaseMap.clear();
        infoTestCaseMap.clear();
        return scriptContext;
    }
}
