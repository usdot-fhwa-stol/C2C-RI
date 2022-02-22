/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures;

/**
 *
 * @author TransCore ITS
 */
public class TMDDProcedureStepFactory {
    public static String GENERALVARIABLERECORD_STEP_TYPE = "Variable Record Step Type";
    public static String GENERALVARIABLEVERIFY_STEP_TYPE = "Variable Verify Step Type";
    public static String REQUIREDCONTENTVALUEVERIFY_STEP_TYPE = "Required Content Value Verify Step Type";


    public static TestProcedureStep makeProcedureStep(String stepType, Variable theVariable) throws Exception {
        TestProcedureStep tmddTestProcedureStep;

        if (stepType.equals(TMDDProcedureStepFactory.GENERALVARIABLERECORD_STEP_TYPE)) {
            tmddTestProcedureStep = new GeneralVariableRecordTestStep(theVariable);
        }
        else if(stepType.equals(TMDDProcedureStepFactory.GENERALVARIABLEVERIFY_STEP_TYPE)) {
            tmddTestProcedureStep = new GeneralVariableVerifyTestStep(theVariable);
        }  else if(stepType.equals(TMDDProcedureStepFactory.REQUIREDCONTENTVALUEVERIFY_STEP_TYPE)) {
            tmddTestProcedureStep = new RequiredContentValueVerifyTestStep(theVariable);
        } else {
           throw new Exception("STEP TYPE " + stepType + " is not yet supported.");
        }
        return tmddTestProcedureStep;
    }

/**
    public static TestProcedureStep makeProcedureStep(String stepType, String needID, String dialog, String centerMode) throws Exception {
        TestProcedureStep tmddTestProcedureStep;
        
        if (stepType.equals(TMDDProcedureStepFactory.GENERALVARIABLERECORD_STEP_TYPE)) {
            tmddTestProcedureStep = new GeneralVariableRecordTestStep(needID, dialog, centerMode);
        } else {
           throw new Exception("STEP TYPE " + stepType + " is not yet supported.");
        }
        return tmddTestProcedureStep;
    }
*/
 }
