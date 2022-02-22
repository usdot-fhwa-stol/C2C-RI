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
public class DialogContentVerifyTestStep extends TestProcedureStep{
    private String dialog;
    private String dialogVariableName;

    public DialogContentVerifyTestStep(String dialog){
        this.dialog = dialog;
        if (dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)){
            dialogVariableName = "ErrorResponseExpected";
        } else if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)){
            dialogVariableName = "PublicationErrorResponseExpected";

        } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)){
            dialogVariableName = "SubscriptionErrorResponseExpected";

        }

        this.setDescription("IF "+dialogVariableName+" is not equal to TRUE THEN CONTINUE, OTHERWISE skip the following substeps.  Note: If a verification step fails, then test execution will proceed at the next subsequent Post Condition step, if present.  Otherwise, test execution will proceed to the final Exit step.");
        this.setResults("NA");
    }

    public ArrayList<TestProcedureStep> getStepsforDatabase() {
        ArrayList<TestProcedureStep> theList = new ArrayList<TestProcedureStep>();
        theList.add(this);
        for (Section subStep : this.getSubSteps()){
            if (subStep instanceof TestProcedureStep){
//               theList.add((TestProcedureStep)subStep);
               theList.addAll(subStep.getStepsforDatabase());
            } else {
                for (Section thisStep : subStep.getStepsforDatabase()){
                    if (thisStep instanceof TestProcedureStep) {

                        theList.add((TestProcedureStep)thisStep);
                    } else theList.addAll(thisStep.getStepsforDatabase());
                }
            }
        }
        return theList;
    }

    public String getScriptContent() {
        String result;
        result = "<testStep functionId=\"Step "+this.getId()+" "+this.getDescription()+"\"   passfailResult=\"False\"/>\n   <jl:if test=\"${!"+dialogVariableName+"}\">\n   <SubSteps>\n   </jl:if>";

        String subStepResult = "";
        for (Section thisSubSection : this.getSubSteps() ){
            if (thisSubSection instanceof TestProcedureStep){
               TestProcedureStep thisSubStep = (TestProcedureStep) thisSubSection;
               subStepResult = subStepResult.concat(thisSubStep.getScriptContent()+"\n");

            } else {
                for (Section thisStep : thisSubSection.getStepsforDatabase()){
                    subStepResult = subStepResult.concat(thisStep.getScriptContent())+"\n";
                }
            }

//            TestProcedureStep thisSubStep = (TestProcedureStep) thisSubSection;
//            subStepResult = subStepResult.concat(thisSubStep.getScriptContent()+"\n");
        }
        result = result.replace("<SubSteps>", subStepResult);

        return result;
    }

}
