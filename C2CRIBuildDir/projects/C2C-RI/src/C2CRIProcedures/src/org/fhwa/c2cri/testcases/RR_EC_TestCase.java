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
public class RR_EC_TestCase extends AbstractTMDDTestCase {

    public RR_EC_TestCase(String needID, String dialog, String centerMode, boolean valid, String invalidCode, ArrayList<Variable>procedureVariables) {
        initializeTestCaseInformation(needID, dialog, centerMode, valid, invalidCode);
        this.procedureVariables = procedureVariables;
        try{
            if (valid){
                // Create the Valid Test Case
                
             setTestItemType("SUT/RI");
           //setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            setTestCaseInputs(makeTestCaseInputs("EC", dialog, false));
            setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, false));
            // Make the Default Test Case Values
               
                
            } else {
                // Create the Invalid Test Case
              setTestItemType("SUT/RI");
             //setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
              setTestCaseInputs(makeTestCaseInputs("EC", dialog, true));
              setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, true));
            // Make the Default Test Case Values
               
                
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }
        }
}
