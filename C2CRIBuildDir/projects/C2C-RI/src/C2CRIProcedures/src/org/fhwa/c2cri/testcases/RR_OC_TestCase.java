/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testcases;

import java.util.ArrayList;
import org.fhwa.c2cri.testprocedures.*;


/**
 *
 * @author TransCore ITS
 */
public class RR_OC_TestCase extends AbstractTMDDTestCase {

    public RR_OC_TestCase(String needID, String dialog, String centerMode, boolean valid, String invalidCode, ArrayList<Variable>procedureVariables) {
        initializeTestCaseInformation(needID, dialog, centerMode, valid, invalidCode);
        this.procedureVariables = procedureVariables;
        try{
            if (valid){
                // Create the Valid Test Case
                
             setTestItemType("SUT/RI");
           //setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            setTestCaseInputs(makeTestCaseInputs("OC", dialog, false));
            setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, false));
            // Make the Default Test Case Values
               
                
            } else {
                // Create the Invalid Test Case
              setTestItemType("RI");
             //setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
              setTestCaseInputs(makeTestCaseInputs("OC", dialog, true));
              setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, true));
            // Make the Default Test Case Values
               
                
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }
        }
}
