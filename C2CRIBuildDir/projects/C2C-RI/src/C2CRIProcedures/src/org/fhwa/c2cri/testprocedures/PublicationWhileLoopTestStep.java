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
public class PublicationWhileLoopTestStep extends TestProcedureStep{
    private String dialog;
    private String dialogVariableName;

    public PublicationWhileLoopTestStep(String needNumber, String centerMode, String dialog, ArrayList<Variable> variableList, String referencedRequirements){
            ArrayList<Section> subSteps = new ArrayList<Section>();
            Integer stepNumber = 1;

            if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
/**
                String subscriptionDialog = NRTM_RTM_Design_Data.getInstance().getPartnerDialog(needNumber, centerMode, dialog);
                if (!subscriptionDialog.equals("")) {
                    // Must do a subscription First
                    DialogTestStep thisSubDialogStep1 = new DialogTestStep(subscriptionDialog, centerMode, variableList, true, referencedRequirements, false);
                    if (thisSubDialogStep1.isValid()) {
                        thisSubDialogStep1.setStartStepNumber(stepNumber);
                        stepNumber++;
                        subSteps.add(thisSubDialogStep1);
                    }
                    DialogTestStep thisSubDialogStep2 = new DialogTestStep(subscriptionDialog, centerMode, variableList, false, referencedRequirements, false);
                    if (thisSubDialogStep2.isValid()) {
                        OptionalContentVerifiedSetTestStep optionalVerificationStep = new OptionalContentVerifiedSetTestStep();
                        optionalVerificationStep.setStartStepNumber(stepNumber);
                        stepNumber++;
                        subSteps.add(optionalVerificationStep);
                        thisSubDialogStep2.setStartStepNumber(stepNumber);
                        stepNumber++;
                        subSteps.add(thisSubDialogStep2);
                    }
                }
*/
                DialogTestStep thisDialogStep = new DialogTestStep(needNumber, dialog, centerMode, variableList, true, referencedRequirements, false);
                if (thisDialogStep.isValid()) {
                    thisDialogStep.setStartStepNumber(stepNumber);
                    stepNumber++;
                    subSteps.add(thisDialogStep);
                }

                // Meat and Potatoes Here !!!

               DialogContentVerifyProcedureSection thisVerifySection = new DialogContentVerifyProcedureSection(needNumber, dialog, centerMode, variableList, false, referencedRequirements);
 //                TestProcedureSection thisVerifySection = TMDDTestProcedureSectionFactory.makeProcedure(
 //               TMDDTestProcedureSectionFactory.VARIABLE_VERIFICATION_SECTION,
 //               needNumber, dialog, centerMode);
                thisVerifySection.setStartStepNumber(stepNumber);
                stepNumber++;


                subSteps.add(thisVerifySection);

        }

        this.setSubSteps(subSteps);
        this.setDescription("WHILE CONTINUEPUBLICATION is equal to TRUE then CONTINUE, OTHERWISE skip the following substeps.  Note:If a verification step fails, then test execution will proceed at the next subsequent Post Condition step, if present.  Otherwise, test execution will proceed to the final Exit step.");
        this.setResults("NA");
    }

    public ArrayList<TestProcedureStep> getStepsforDatabase() {
        ArrayList<TestProcedureStep> theList = new ArrayList<TestProcedureStep>();
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
//        result = "<testStep functionId=\"Step "+this.getId()+" "+this.getDescription()+"\"/>\n   <jl:set var=\"PUBLICATIONCOMPLETE\" value=\"false\"/>\n   <jl:while test=\"${!PUBLICATIONCOMPLETE}\">\n   <SubSteps>\n   </jl:while>";
        result = "<testStep functionId=\"Step "+this.getId()+" "+this.getDescription()+"\"/>\n   <jl:while test=\"${CONTINUEPUBLICATION}\">\n   <SubSteps>\n   </jl:while>";

        String subStepResult = "";
        for (Section thisSubSection : this.getSubSteps() ){
            if (thisSubSection instanceof DialogContentVerifyProcedureSection) {
               subStepResult = subStepResult.concat(thisSubSection.getScriptContent()+"\n");
            } else {
               TestProcedureStep thisSubStep = (TestProcedureStep) thisSubSection;
               subStepResult = subStepResult.concat(thisSubStep.getScriptContent()+"\n");
            }
            }
        result = result.replace("<SubSteps>", subStepResult);

        return result;
    }

}
