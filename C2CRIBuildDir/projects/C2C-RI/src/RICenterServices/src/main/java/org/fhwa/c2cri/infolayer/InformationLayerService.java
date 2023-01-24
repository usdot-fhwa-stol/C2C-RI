/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.infolayer;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.ServiceLoader;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.PluginManagerUtil;


/**
 * The Class InformationLayerService returns the Information Layer Standard plugin with a given name.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class InformationLayerService {

    /** The service. */
    private static InformationLayerService service;
    
    /** The loader. */
    private ServiceLoader<InformationLayerStandard> loader;

    /**
     * Creates a new instance of DictionaryService.
     */
    private InformationLayerService() {
        loader = ServiceLoader.load(InformationLayerStandard.class);
    }

    /**
     * Retrieve the singleton static instance of ApplicationLayerService.
     *
     * @return single instance of InformationLayerService
     */
    public static synchronized InformationLayerService getInstance() {
        if (service == null) {
            service = new InformationLayerService();
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
     * Retrieve the information layer standard from the first provider that
     * matches the provided standard name.
     *
     * @param standard the standard
     * @return the standard
     */
    public InformationLayerStandard getStandard(String standard) {
        InformationLayerStandard theStandard = null;

        try {
            PluginManager pm = PluginManagerFactory.createPluginManager();
            File pluginFile = new File("TestSuites/");
            System.out.println(pluginFile.getAbsolutePath());
            pm.addPluginsFrom(pluginFile.toURI());
            PluginManagerUtil pluginUtil = new PluginManagerUtil(pm);
            Collection<InformationLayerStandard> plugins = new HashSet<InformationLayerStandard>(pluginUtil.getPlugins(InformationLayerStandard.class));
            for (InformationLayerStandard thisPlugin : plugins){
                if (thisPlugin.getName().equals(standard)){
                    theStandard = thisPlugin;
                    break;
                } else {
                    System.out.println("Passing over the plugin for "+thisPlugin.getName());                    
                }
            }
            
//            Iterator<InformationLayerStandard> standards = loader.iterator();
//            while (theStandard == null && standards.hasNext()) {
//                InformationLayerStandard ils = standards.next();
//                if (ils.getName().equals(standard)){
//                   theStandard = ils;
//                }
//            }
//        } catch (ServiceConfigurationError serviceError) {
        } catch (Exception serviceError) {
            theStandard = null;
            serviceError.printStackTrace();

        }
        return theStandard;
    }
}
