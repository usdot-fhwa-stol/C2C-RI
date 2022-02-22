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
public class GeneralVariableRecordTestStep extends TestProcedureStep{
    private Variable stepVariable;

    public GeneralVariableRecordTestStep(Variable theVariable){
        this.stepVariable = theVariable;
        this.setDescription(theVariable.getRecordText());
        this.setResults("NA");
    }

    public ArrayList<TestProcedureStep> getStepsforDatabase() {
        ArrayList<TestProcedureStep> theList = new ArrayList<TestProcedureStep>();
        String result;
        result = stepVariable.getRecordText();
        result = result.replace("<Text>", stepVariable.getRecordText());
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
        result = stepVariable.getRecordScriptText().replace("<Step>", "Step "+this.getId());
        result = result.replace("<Text>", stepVariable.getRecordText());
        result = result.replace("<VariableName>", stepVariable.getName());
        result = result.replace("<LF>", "\n");
        return result;
    }

}
