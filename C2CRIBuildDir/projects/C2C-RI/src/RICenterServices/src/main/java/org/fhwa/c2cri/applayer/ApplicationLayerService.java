/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.applayer;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.PluginManagerUtil;

/**
 * ApplicationLayerService looks for the specified application layer standard among the set of found plugins.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ApplicationLayerService {

    /** The service. */
    private static ApplicationLayerService service;
    
    /** The loader. */
    private ServiceLoader<ApplicationLayerStandard> loader;

    /**
     * Creates a new instance of DictionaryService.
     */
    private ApplicationLayerService() {
        loader = ServiceLoader.load(ApplicationLayerStandard.class);
    }

    /**
     * Retrieve the singleton static instance of ApplicationLayerService.
     *
     * @return single instance of ApplicationLayerService
     */
    public static synchronized ApplicationLayerService getInstance() {
        if (service == null) {
            service = new ApplicationLayerService();
        }
        return service;
    }

    /**
     * Clear the singleton static instance of ApplicationLayerService.
     */
    public static synchronized void clearInstance() {
        if (service != null) {
            service=null;
        }
    }

    /**
     * Retrieve the application layer standard from the first provider
     * that matches the provided standard name.
     *
     * @param standard the standard
     * @return the standard
     */
    public ApplicationLayerStandard getStandard(String standard) {
        ApplicationLayerStandard theStandard = null;

        try {
            PluginManager pm = PluginManagerFactory.createPluginManager();
            File pluginFile = new File("TestSuites/");
            System.out.println(pluginFile.getAbsolutePath());
            pm.addPluginsFrom(pluginFile.toURI());
            PluginManagerUtil pluginUtil = new PluginManagerUtil(pm);
            Collection<ApplicationLayerStandard> plugins = new HashSet<ApplicationLayerStandard>(pluginUtil.getPlugins(ApplicationLayerStandard.class));
            for (ApplicationLayerStandard thisPlugin : plugins){
                if (thisPlugin.getName().equals(standard)){
                    theStandard = thisPlugin;
                    break;
                } else {
                    System.out.println("Passing over the plugin for "+thisPlugin.getName());                    
                }
            }
        } catch (ServiceConfigurationError serviceError) {
            theStandard = null;
            serviceError.printStackTrace();

        }
        return theStandard;
    }

}
