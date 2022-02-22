/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.infolayer;

import org.fhwa.c2cri.applayer.*;
import net.xeoh.plugins.base.Plugin;


/**
 * The Interface InformationLayerStandard defines the methods that all pre-defined InformationLayerStandard plugins must implement.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public interface InformationLayerStandard extends Plugin {

    /**
     * Gets the name.
     *
     * @return The name of the standard.
     */
    public String getName();

    /**
     * Initialize standard.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testCaseName the test case name
     * @param centerMode the center mode
     * @param appStandard the app standard
     * @throws Exception the exception
     */
    public void initializeStandard(String testCaseName,
                                   String centerMode,
                                   ApplicationLayerStandard appStandard) throws Exception;

    /**
     * Gets the information layer controller.
     *
     * @return The information layer adapter that is associated with the application layer standard of this instance.
     */
    public InformationLayerController getInformationLayerController();
       
       
    /**
     * Stop services.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void stopServices();
}
