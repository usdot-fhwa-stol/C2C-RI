/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.infolayer;

import org.fhwa.c2cri.applayer.ApplicationLayerStandard;
import org.fhwa.c2cri.center.CenterMonitor;



/**
 * The factory for creating InformationLayerStandard objects.
 * 
 * @author TransCore ITS, LLC
 * Last updated 8/2/2013 
 *
 */
public class InformationLayerStandardFactory {
    
    /** The this factory. */
    private static InformationLayerStandardFactory thisFactory;
    
    /** The center mode. */
    private String centerMode;
    
    /** The test case. */
    private String testCase;
    
    /** The information layer standard. */
    private InformationLayerStandard informationLayerStandard;
    
    /** The application layer standard. */
    private ApplicationLayerStandard applicationLayerStandard;
    
    /** The information layer standard name. */
    private String informationLayerStandardName;



    /**
     * Instantiates a new information layer standard factory.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private InformationLayerStandardFactory(){
        
    }
    
    /**
     * Gets the single instance of InformationLayerStandardFactory.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return single instance of InformationLayerStandardFactory
     */
    public static InformationLayerStandardFactory getInstance(){
        if (thisFactory == null){
            thisFactory = new InformationLayerStandardFactory();
        }
        return thisFactory;
    }
    
    
    /**
     * Gets the information standard.
     *
     * @return the information standard
     * @throws Exception the exception
     */
    public InformationLayerStandard getInformationStandard() throws Exception{

       if (informationLayerStandardName == null) throw new Exception("InformationLayerStandardFactory:: The Information Layer Standard Name must be specified.");
       if (applicationLayerStandard == null) throw new Exception("InformationLayerStandardFactory:: Application Layer Standard must be provided.");
       if (centerMode == null) throw new Exception("InformationLayerStandardFactory:: The RI Center Mode must be specified.");
       if (testCase == null) throw new Exception("InformationLayerStandardFactory:: The Test Case Name must be specified.");
//        return new NTCIP2306ApplicationLayerStandard(applicationLayerStandard, informationLayerStandard,
//                                                     requestDialog, subscriptionDialog, publicationDialog,
//                                                     centerMode, testCase, true);
        InformationLayerService.clearInstance();
        InformationLayerStandard theStandard = InformationLayerService.getInstance().getStandard(informationLayerStandardName);
        if (theStandard == null) throw new Exception("No Instances of "+informationLayerStandardName+" were found.");
        theStandard.initializeStandard(testCase, centerMode, applicationLayerStandard);
        CenterMonitor.getInstance().registerInformationStandard(theStandard);

        return theStandard;
    }

//    public String getInformationLayerStandard() {
//        return informationLayerStandard.getName();
//    }
//
//    public void setInformationLayerStandard(ApplicationLayerStandard applicationLayerStandard) {
//        this.informationLayerStandard = informationLayerStandard;
//    }

    /**
 * Gets the application layer standard.
 *
 * @return the application layer standard
 */
public String getApplicationLayerStandard() {
        return applicationLayerStandard.getName();
    }

    /**
     * Sets the application layer standard.
     *
     * @param applicationLayerStandard the new application layer standard
     */
    public void setApplicationLayerStandard(ApplicationLayerStandard applicationLayerStandard) {
        this.applicationLayerStandard = applicationLayerStandard;
    }
    
    /**
     * Gets the center mode.
     *
     * @return the center mode
     */
    public String getCenterMode() {
        return centerMode;
    }

    /**
     * Sets the center mode.
     *
     * @param centerMode the new center mode
     */
    public void setCenterMode(String centerMode) {
        this.centerMode = centerMode;
    }

    /**
     * Gets the information layer standard name.
     *
     * @return the information layer standard name
     */
    public String getInformationLayerStandardName() {
        return informationLayerStandardName;
    }

    /**
     * Sets the information layer standard name.
     *
     * @param informationLayerStandardName the new information layer standard name
     */
    public void setInformationLayerStandardName(String informationLayerStandardName) {
        this.informationLayerStandardName = informationLayerStandardName;
    }


    /**
     * Gets the test case.
     *
     * @return the test case
     */
    public String getTestCase() {
        return testCase;
    }

    /**
     * Sets the test case.
     *
     * @param testCase the new test case
     */
    public void setTestCase(String testCase) {
        this.testCase = testCase;
    }

}
