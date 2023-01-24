/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.center;

import org.fhwa.c2cri.applayer.ApplicationLayerStandard;
import org.fhwa.c2cri.infolayer.InformationLayerStandard;

/**
 * Maintains a reference to any active Application and/or Information Layer Standards
 * and shuts them down when the test is terminated.
 * 
 * @author TransCore ITS, LLC
 * Created: Dec 11, 2013
 */
public class CenterMonitor {

    private static CenterMonitor monitor;
    private InformationLayerStandard infoStandard;
    private ApplicationLayerStandard appStandard;
    private boolean infoStandardRegistered = false;
    private boolean appStandardRegistered = false;
    
    /**
     * Get a reference to the Center Monitor
     * @return the center monitor
     */
    public static CenterMonitor getInstance(){
        if (monitor==null){
            monitor = new CenterMonitor();
        }
        return monitor;
    }
    
    /*
     * Constructor
     */
     private CenterMonitor(){         
     }
     

     /**
      *  Register an Application Layer Standard 
      */
     public void registerApplicationStandard(ApplicationLayerStandard inputAppStandard){
         appStandardRegistered = true;
         appStandard = inputAppStandard;
     }

     /**
      *  UnRegister an Application Layer Standard 
      */
     public void unRegisterApplicationStandard(){
         appStandardRegistered = false;
         appStandard = null;
     }
     

     /**
      *  Register an Information Layer Standard 
      */
     public void registerInformationStandard(InformationLayerStandard inputInfoStandard){
         infoStandard = inputInfoStandard;
         infoStandardRegistered = true;
     }

     /**
      *  UnRegister an Information Layer Standard 
      */
     public void unRegisterInformationStandard(){
         infoStandardRegistered = false;
         infoStandard = null;
     }
     
     /**
      * Ensure that any current standard services are stopped when the test is terminated.
      */
     public void terminateTest(){
         if (infoStandardRegistered&&(!(infoStandard==null))){
             infoStandard.stopServices();
         } else if (appStandardRegistered&&(!(appStandard==null))){
             appStandard.stopServices();
         }
     }    
     
}
