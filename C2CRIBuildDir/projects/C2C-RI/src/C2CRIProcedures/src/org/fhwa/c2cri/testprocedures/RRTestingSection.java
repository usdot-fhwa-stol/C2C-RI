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
public class RRTestingSection extends TestProcedureSection {

    public RRTestingSection(String needNumber, String dialog, String centerMode) {
        try {

            ArrayList<DialogVariable> dialogVariableList;
            DialogVariables dialogVariables = new DialogVariables();
            String sutCenterMode = centerMode.equals(TMDDParameters.TMDD_EC_MODE)?TMDDParameters.TMDD_OC_Mode:TMDDParameters.TMDD_EC_MODE;
            ArrayList<String> requirementList = NRTM_RTM_Design_Data.getInstance().makeDialogRequirementsList(needNumber, sutCenterMode, dialog);

            dialogVariableList = dialogVariables.getMatchingDialogVariables(dialog, centerMode);
            this.variables.addAll(dialogVariableList);

//            Integer subStepNumber = 1;
//            for (Variable thisVariable : dialogVariableList) {
//                TestProcedureStep newStep = TMDDProcedureStepFactory.makeProcedureStep(TMDDProcedureStepFactory.GENERALVARIABLERECORD_STEP_TYPE,
//                        thisVariable);
//                newStep.setStartStepNumber(getNextStepNumber());
//                subSections.add(newStep);
//            }



// Dialog Step/Section Definition Goes here!!!!
            ArrayList<Variable> variableList = new ArrayList<Variable>();
            for (Variable thisVariable : dialogVariableList) {
                variableList.add(thisVariable);
            }


            String referencedRequirements = "";
            for (String thisRequirement : requirementList) {
                if (referencedRequirements.isEmpty()) {
                    referencedRequirements = referencedRequirements.concat(thisRequirement);
                } else {
                    referencedRequirements = referencedRequirements.concat(", " + thisRequirement);
                }
            }


            DialogTestStep thisDialogStep = new DialogTestStep(needNumber, dialog, centerMode, variableList, true, referencedRequirements, false);
            if (thisDialogStep.isValid()) {
                subSections.add(thisDialogStep);
            }
            DialogContentVerifyProcedureSection thisVerifySection = new DialogContentVerifyProcedureSection(needNumber, dialog, centerMode, variableList, false, referencedRequirements);

            subSections.add(thisVerifySection);

            ExitTestStep thisExitStep = new ExitTestStep();
            subSections.add(thisExitStep);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getScriptContent() {
        String scriptContent = "";
        for (Section thisSection : subSections) {
            //           for (TestProcedureStep thisStep : thisSection.getStepsforDatabase()) {
            //               scriptContent = scriptContent.concat(thisStep.getId() + " - " + thisStep.getDescription() + "\n");
            //           }

            /**

            String subScriptContent = "";

            for (TestProcedureStep thisStep : thisSection.getStepsforDatabase()) {

            for (Section subTestProcedureSection : thisStep.getSubSteps()){
            for (TestProcedureStep thisSubStep : subTestProcedureSection.getStepsforDatabase()){
            subScriptContent = subScriptContent.concat("   "+thisSubStep.getScriptContent()+"\n");
            }
            }
            }

            scriptContent = scriptContent.replace("<SubSteps>", subScriptContent);
             */
            scriptContent = scriptContent.concat(thisSection.getScriptContent()) + "\n\n";
        }
        return scriptContent;
    }

    @Override
    public ArrayList<TestProcedureStep> getStepsforDatabase() {
        ArrayList<TestProcedureStep> thisList = new ArrayList<TestProcedureStep>();

        for (Section thisSection : subSections) {
            for (TestProcedureStep thisStep : thisSection.getStepsforDatabase()) {
                thisList.add(thisStep);
     //           for (Section subTestProcedureSection : thisStep.getSubSteps()) {
     //               for (TestProcedureStep thisSubStep : subTestProcedureSection.getStepsforDatabase()) {
     //                   thisList.add(thisSubStep);
     //               }
     //           }
            }
        }

        return thisList;
    }
}
