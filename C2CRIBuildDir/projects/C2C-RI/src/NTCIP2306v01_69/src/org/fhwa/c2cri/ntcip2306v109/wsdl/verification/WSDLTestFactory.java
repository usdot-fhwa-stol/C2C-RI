/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.wsdl.verification;

import java.util.HashMap;
import java.util.Map;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;

/**
 * Factory for {@link WSDLTest} instances.  Provides a WSDL Test Class for the WSDL related normative requirements of NTCIP 2306.
 *
 * @author TransCore ITS,LLC
 * Last Updated: 9/12/2013
 */
public class WSDLTestFactory {

    /** The test map. */
    private static Map<String, WSDLTest> testMap = new HashMap<String, WSDLTest>();
    
    /** The riwsdl. */
    private RIWSDL theRIWSDL;
    
    /** The wsdl file. */
    private String wsdlFile;

    /**
     * Instantiates a new wSDL test factory.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTestFactory(RIWSDL wsdlSpec, String wsdlURL) {
        this.theRIWSDL = wsdlSpec;
        this.wsdlFile = wsdlURL;

        testMap.put("6.1.1.1", new WSDLTest_6_1_1_1(theRIWSDL, wsdlFile));
        testMap.put("6.1.1.2", new WSDLTest_6_1_1_2(theRIWSDL, wsdlFile));
        testMap.put("6.1.2", new WSDLTest_6_1_2(theRIWSDL, wsdlFile));
        testMap.put("6.2.1", new WSDLTest_6_2_1(theRIWSDL, wsdlFile));
        testMap.put("6.3.1", new WSDLTest_6_3_1(theRIWSDL, wsdlFile));
        testMap.put("6.3.2", new WSDLTest_6_3_2(theRIWSDL, wsdlFile));
        testMap.put("6.3.3", new WSDLTest_6_3_3(theRIWSDL, wsdlFile));
        testMap.put("6.4.1.1", new WSDLTest_6_4_1_1(theRIWSDL, wsdlFile));
        testMap.put("6.4.1.2", new WSDLTest_6_4_1_2(theRIWSDL, wsdlFile));
        testMap.put("6.4.1.3", new WSDLTest_6_4_1_3(theRIWSDL, wsdlFile));
        testMap.put("6.4.1.4", new WSDLTest_6_4_1_4(theRIWSDL, wsdlFile));
        testMap.put("6.4.2.1", new WSDLTest_6_4_2_1(theRIWSDL, wsdlFile));
        testMap.put("6.4.2.2", new WSDLTest_6_4_2_2(theRIWSDL, wsdlFile));
        testMap.put("6.4.2.3", new WSDLTest_6_4_2_3(theRIWSDL, wsdlFile));
        testMap.put("6.4.2.4", new WSDLTest_6_4_2_4(theRIWSDL, wsdlFile));
        testMap.put("6.4.3.1", new WSDLTest_6_4_3_1(theRIWSDL, wsdlFile));
        testMap.put("6.4.3.2", new WSDLTest_6_4_3_2(theRIWSDL, wsdlFile));
        testMap.put("6.4.3.3", new WSDLTest_6_4_3_3(theRIWSDL, wsdlFile));
        testMap.put("6.4.3.4", new WSDLTest_6_4_3_4(theRIWSDL, wsdlFile));
        testMap.put("6.5.1", new WSDLTest_6_5_1(theRIWSDL, wsdlFile));
        testMap.put("7.1.1.1", new WSDLTest_7_1_1_1(theRIWSDL, wsdlFile));
        testMap.put("7.1.1.2", new WSDLTest_7_1_1_2(theRIWSDL, wsdlFile));
        testMap.put("7.1.1.3", new WSDLTest_7_1_1_3(theRIWSDL, wsdlFile));
        testMap.put("7.1.1.4", new WSDLTest_7_1_1_4(theRIWSDL, wsdlFile));
        testMap.put("7.1.2.1", new WSDLTest_7_1_2_1(theRIWSDL, wsdlFile));
        testMap.put("7.1.2.2", new WSDLTest_7_1_2_2(theRIWSDL, wsdlFile));
        testMap.put("7.1.2.3", new WSDLTest_7_1_2_3(theRIWSDL, wsdlFile));
        testMap.put("7.1.2.4", new WSDLTest_7_1_2_4(theRIWSDL, wsdlFile));
        testMap.put("7.1.2.5", new WSDLTest_7_1_2_5(theRIWSDL, wsdlFile));
        testMap.put("7.1.2.6", new WSDLTest_7_1_2_6(theRIWSDL, wsdlFile));
        testMap.put("7.1.2.7", new WSDLTest_7_1_2_7(theRIWSDL, wsdlFile));
        testMap.put("7.1.2.8", new WSDLTest_7_1_2_8(theRIWSDL, wsdlFile));
        testMap.put("7.1.2.9", new WSDLTest_7_1_2_9(theRIWSDL, wsdlFile));
        testMap.put("7.1.2.10", new WSDLTest_7_1_2_10(theRIWSDL, wsdlFile));
        testMap.put("7.1.3.1", new WSDLTest_7_1_3_1(theRIWSDL, wsdlFile));
        testMap.put("7.1.3.3", new WSDLTest_7_1_3_3(theRIWSDL, wsdlFile));
        testMap.put("7.1.3.4", new WSDLTest_7_1_3_4(theRIWSDL, wsdlFile));
        testMap.put("7.1.3.5", new WSDLTest_7_1_3_5(theRIWSDL, wsdlFile));
        testMap.put("7.2.1.2.3", new WSDLTest_7_2_1_2_3(theRIWSDL, wsdlFile));
        testMap.put("7.2.1.2.4", new WSDLTest_7_2_1_2_4(theRIWSDL, wsdlFile));
        testMap.put("7.2.2.1", new WSDLTest_7_2_2_1(theRIWSDL, wsdlFile));
        testMap.put("7.2.2.2", new WSDLTest_7_2_2_2(theRIWSDL, wsdlFile));
        testMap.put("7.2.2.3", new WSDLTest_7_2_2_3(theRIWSDL, wsdlFile));
        testMap.put("7.2.2.4", new WSDLTest_7_2_2_4(theRIWSDL, wsdlFile));
        testMap.put("7.2.2.5", new WSDLTest_7_2_2_5(theRIWSDL, wsdlFile));
        testMap.put("7.2.3.1", new WSDLTest_7_2_3_1(theRIWSDL, wsdlFile));
        testMap.put("7.2.4.1", new WSDLTest_7_2_4_1(theRIWSDL, wsdlFile));
        testMap.put("7.2.4.3", new WSDLTest_7_2_4_3(theRIWSDL, wsdlFile));
        testMap.put("7.2.4.4", new WSDLTest_7_2_4_4(theRIWSDL, wsdlFile));
        testMap.put("8.1.1.1", new WSDLTest_8_1_1_1(theRIWSDL, wsdlFile));
        testMap.put("8.1.1.2", new WSDLTest_8_1_1_2(theRIWSDL, wsdlFile));
        testMap.put("8.1.1.3", new WSDLTest_8_1_1_3(theRIWSDL, wsdlFile));
        testMap.put("8.1.1.4", new WSDLTest_8_1_1_4(theRIWSDL, wsdlFile));
        testMap.put("8.1.1.5", new WSDLTest_8_1_1_5(theRIWSDL, wsdlFile));
        testMap.put("8.1.2.1", new WSDLTest_8_1_2_1(theRIWSDL, wsdlFile));
        testMap.put("8.1.2.2", new WSDLTest_8_1_2_2(theRIWSDL, wsdlFile));
        testMap.put("8.1.2.3", new WSDLTest_8_1_2_3(theRIWSDL, wsdlFile));
        testMap.put("8.1.2.4", new WSDLTest_8_1_2_4(theRIWSDL, wsdlFile));
        testMap.put("8.1.2.5", new WSDLTest_8_1_2_5(theRIWSDL, wsdlFile));
        testMap.put("8.1.2.6", new WSDLTest_8_1_2_6(theRIWSDL, wsdlFile));
        testMap.put("8.1.2.7", new WSDLTest_8_1_2_7(theRIWSDL, wsdlFile));
        testMap.put("8.1.2.8", new WSDLTest_8_1_2_8(theRIWSDL, wsdlFile));
        testMap.put("8.1.2.9", new WSDLTest_8_1_2_9(theRIWSDL, wsdlFile));
        testMap.put("8.1.2.10", new WSDLTest_8_1_2_10(theRIWSDL, wsdlFile));
        testMap.put("8.2.1", new WSDLTest_8_2_1(theRIWSDL, wsdlFile));
        testMap.put("8.2.2.1", new WSDLTest_8_2_2_1(theRIWSDL, wsdlFile));
        testMap.put("8.2.2.2", new WSDLTest_8_2_2_2(theRIWSDL, wsdlFile));
        testMap.put("8.3.1", new WSDLTest_8_3_1(theRIWSDL, wsdlFile));
        testMap.put("8.3.1.1", new WSDLTest_8_3_1_1(theRIWSDL, wsdlFile));
        testMap.put("9.1.2.1", new WSDLTest_9_1_2_1(theRIWSDL, wsdlFile));
        testMap.put("9.1.3.1", new WSDLTest_9_1_3_1(theRIWSDL, wsdlFile));
        testMap.put("9.1.3.2", new WSDLTest_9_1_3_2(theRIWSDL, wsdlFile));
        testMap.put("9.1.3.3", new WSDLTest_9_1_3_3(theRIWSDL, wsdlFile));
        testMap.put("9.1.3.4", new WSDLTest_9_1_3_4(theRIWSDL, wsdlFile));
        testMap.put("9.1.3.5", new WSDLTest_9_1_3_5(theRIWSDL, wsdlFile));
        testMap.put("9.1.3.6", new WSDLTest_9_1_3_6(theRIWSDL, wsdlFile));
        testMap.put("9.1.3.7", new WSDLTest_9_1_3_7(theRIWSDL, wsdlFile));
        testMap.put("9.1.4.1", new WSDLTest_9_1_4_1(theRIWSDL, wsdlFile));
        testMap.put("9.1.4.4", new WSDLTest_9_1_4_4(theRIWSDL, wsdlFile));


    }

    /**
     * Gets the test.
     *
     * @param test the test
     * @return the test
     * @throws Exception the exception
     */
    public WSDLTest getTest(String test) throws Exception {
        if (testMap.containsKey(test)) {
            return testMap.get(test);
        }
        throw new Exception("No Test has been implemented for NTCIP 2306 requirement "+test+".");
    }

    /**
     * Clear.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void clear(){
        testMap.clear();
    }
}
