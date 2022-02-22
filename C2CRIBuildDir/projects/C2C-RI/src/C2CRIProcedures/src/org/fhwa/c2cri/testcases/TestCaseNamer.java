/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testcases;

import java.io.File;
import java.util.HashMap;
import org.fhwa.c2cri.testprocedures.*;

/**
 *
 * @author TransCore ITS
 */
public class TestCaseNamer {

    private String testCaseName;
    private String testCaseTitle;
    private String testCaseID;
    private String testCaseDescription;
    private HashMap<String, String> codeMap = new HashMap();

    public TestCaseNamer(String needNumber, String dialog, String centerMode, Boolean valid, String invalidCode) {
        codeMap.put("1", "unknown processing error");
        codeMap.put("2", "center does not support this type message");
        codeMap.put("3", "missing information prevents processing message");
        codeMap.put("4", "message is not well formed or cannot be parsed");
        codeMap.put("5", "out of range values");
        codeMap.put("6", "permission not granted for request");
        codeMap.put("7", "authentication not recognized");
        codeMap.put("8", "no valid data available");
        codeMap.put("9", "other");
        String sutCenterMode = (centerMode.equals(TMDDParameters.TMDD_EC_MODE) ? TMDDParameters.TMDD_OC_Mode : TMDDParameters.TMDD_EC_MODE);
        testCaseID = "TCS-" + needNumber + "-" + dialog + "-" + sutCenterMode + "-" + (valid ? "Valid" : "InValid-" + invalidCode);        
        testCaseTitle = (valid ? "Valid" : "InValid (" + invalidCode + ")") + " Test Case of Need " + needNumber + " " + dialog + " for a SUT in " + sutCenterMode + " Mode";

        String needID = NRTM_RTM_Design_Data.getInstance().getNeedID(needNumber);
        String needText = NRTM_RTM_Design_Data.getInstance().getNeedText(needNumber);
        testCaseDescription = "This test case is used to verify the SUTs support of the " + dialog + " dialog as an " + sutCenterMode + " using the variable values specified by the Test Plan.  \n"
                + "This test case supports verification of requirements related to user need " + needID + " [" + needText + "]. This Test Case tests for " + (valid ? "a valid response result." : "an invalid response result. An error response message is expected be returned with an error-code set to " + invalidCodeDescriptionLookup(invalidCode) + ".");
        testCaseName = testCaseTitle;
    }

    public String getProcedureDescription() {
        return testCaseDescription;
    }

    public String getProcedureID() {
        return testCaseID;
    }

    public String getProcedureName() {
        return testCaseName;
    }

    public String getProcedureTitle() {
        return testCaseTitle;
    }

    private String invalidCodeDescriptionLookup(String invalidCode) {
        StringBuilder returnString = new StringBuilder();
        returnString.append(invalidCode).append(" (").append(codeMap.get(invalidCode)).append(")");
        return returnString.toString();
    }
}
