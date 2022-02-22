/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testprocedures;

import java.util.ArrayList;

/**
 *
 * @author TransCore ITS
 */
public class GeneralVariableVerifyTestStep extends TestProcedureStep{
    private Variable stepVariable;

    public GeneralVariableVerifyTestStep(Variable theVariable){
        this.stepVariable = theVariable;
        this.setDescription(theVariable.getVerifyText());
//        String results = "PASS/FAIL "+theVariable.getRelatedRequirements();
        String results = "NA";
        this.setResults(results.replace("[", "(").replace("]", ")"));
        this.setIsSubStep(true);
    }

    public ArrayList<TestProcedureStep> getStepsforDatabase() {
        ArrayList<TestProcedureStep> theList = new ArrayList<TestProcedureStep>();
        String result;
        result = stepVariable.getVerifyText();
        result = result.replace("<Text>", stepVariable.getVerifyText());
        result = result.replace("<VariableName>", stepVariable.getName());

        this.setDescription(result);
        theList.add(this);
        for (Section subStep : this.getSubSteps()){
            if (subStep instanceof TestProcedureStep){
               theList.add((TestProcedureStep)subStep);
            } else {
                theList.addAll(subStep.getStepsforDatabase());
            }
        }

        return theList;
    }

    public String getScriptContent() {
        String result;
        result = stepVariable.getVerifyScriptText().replace("<Step>", "Step "+this.getId());

        String subStepResult = "";
        for (Section thisSubSection : this.getSubSteps() ){
            if (thisSubSection instanceof TestProcedureStep){
            TestProcedureStep thisSubStep = (TestProcedureStep) thisSubSection;
            subStepResult = subStepResult.concat(thisSubStep.getScriptContent()+"\n");
            } else {
                for (Section thisSection : thisSubSection.getStepsforDatabase()){
                    subStepResult = subStepResult.concat(thisSection.getScriptContent()+"\n");
                }
            }

//            TestProcedureStep thisSubStep = (TestProcedureStep) thisSubSection;
//            subStepResult = subStepResult.concat(thisSubStep.getScriptContent()+"\n");
        }
        result = result.replace("<SubSteps>", subStepResult);
        result = result.replace("<Text>", stepVariable.getVerifyText());
        result = result.replace("<VariableName>", stepVariable.getName());
        result = result.replace("<LF>", "\n");

        return result;
    }

}
