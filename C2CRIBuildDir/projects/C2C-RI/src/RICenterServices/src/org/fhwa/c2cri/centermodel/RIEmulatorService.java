/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.centermodel;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.ServiceLoader;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.PluginManagerUtil;


/**
 * The Class RIEmulatorService returns the Information Layer Standard RIEmulator plugin with a given name.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class RIEmulatorService {

    /** The service. */
    private static RIEmulatorService service;
    
    /** The loader. */
    private ServiceLoader<RIEmulator> loader;

    /**
     * Creates a new instance of DictionaryService.
     */
    private RIEmulatorService() {
        loader = ServiceLoader.load(RIEmulator.class);
    }

    /**
     * Retrieve the singleton static instance of RIEmulatorService.
     *
     * @return single instance of RIEmulatorService
     */
    public static synchronized RIEmulatorService getInstance() {
        if (service == null) {
            service = new RIEmulatorService();
        }
        return service;
    }

    /**
     * Clear the singleton static instance of InformationLayerService.
     */
    public static synchronized void clearInstance() {
        if (service != null) {
            service = null;
        }
    }

    /**
     * Retrieve the RIEmulator from the first provider that
     * matches the provided standard name.
     *
     * @param standard the standard
     * @return the standard
     */
    public RIEmulator getEmulator(String standard) {
        RIEmulator theEmulator = null;

        try {
            System.out.println("RIEmulatorService::getEmulator The Standard Requested = "+standard);
            PluginManager pm = PluginManagerFactory.createPluginManager();
            File pluginFile = new File("TestSuites/");
            System.out.println("RIEmulatorService::getEmulator  After first Plugin File"+pluginFile.getAbsolutePath());
            pm.addPluginsFrom(pluginFile.toURI());
//            File pluginFile2 = new File("../TMDDv303/dist/");
//            System.out.println("RIEmulatorService::getEmulator  After Second Plugin File"+pluginFile.getAbsolutePath());
//            pm.addPluginsFrom(pluginFile2.toURI());
            PluginManagerUtil pluginUtil = new PluginManagerUtil(pm);
            Collection<RIEmulator> plugins = new HashSet<RIEmulator>(pluginUtil.getPlugins(RIEmulator.class));
            for (RIEmulator thisPlugin : plugins){
                if (thisPlugin.getEmulatorStandard().equals(standard)){
                    theEmulator = thisPlugin;
                    break;
                } else {
                    System.out.println("Passing over the plugin for "+thisPlugin.getEmulatorStandard());                    
                }
            }
            
//            theEmulator = pm.getPlugin(RIEmulator.class);
//            if ((theEmulator != null)&&(!theEmulator.getEmulatorStandard().equals(standard))){
//                if (theEmulator != null){
//                    System.out.println("RIEmulatorService:: The Emulator Standard "+theEmulator.getEmulatorStandard() + " did not match the Standard Requested "+standard);
//                } else {
//                    System.out.println("RIEmulatorService:: No Matching Plugins were found.  theEmulator = null!! ");                   
//                }
//                theEmulator = null;
//            } else System.out.println("RIEmulatorService:: A Matching Plugin was found.  theEmulator Standard = "+theEmulator.getEmulatorStandard());                   



        } catch (Exception serviceError) {
            theEmulator = null;
            serviceError.printStackTrace();

        }
        return theEmulator;
    }
}
