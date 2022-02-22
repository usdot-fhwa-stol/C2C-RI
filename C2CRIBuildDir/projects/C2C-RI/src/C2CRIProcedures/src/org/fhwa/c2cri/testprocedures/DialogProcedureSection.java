/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author TransCore ITS
 */
public class DialogProcedureSection extends TestProcedureSection {

    private String dialog;
    private String needNumber;
    private String centerMode;

    public DialogProcedureSection(String needNumber, String dialog, String centerMode) {
        try {
            this.dialog = dialog;
            this.needNumber = needNumber;
            this.centerMode = centerMode;

            ArrayList<DialogVariable> dialogVariableList;
            DialogVariables dialogVariables = new DialogVariables();
            String sutCenterMode = centerMode.equals(TMDDParameters.TMDD_EC_MODE) ? TMDDParameters.TMDD_OC_Mode : TMDDParameters.TMDD_EC_MODE;
            ArrayList<String> requirementList = NRTM_RTM_Design_Data.getInstance().makeDialogRequirementsList(needNumber, sutCenterMode, dialog);

            String referencedRequirements = "";
            for (String thisRequirement : requirementList) {
                if (referencedRequirements.isEmpty()) {
                    referencedRequirements = referencedRequirements.concat(thisRequirement);
                } else {
                    referencedRequirements = referencedRequirements.concat(", " + thisRequirement);
                }
            }
            
            dialogVariableList = dialogVariables.getMatchingDialogVariables(dialog, centerMode);
            this.variables.addAll(dialogVariableList);

//            String maxTime = "";
//            if (!timeOutParameter.isEmpty()){
//                DialogVariable timeOutVariable = new DialogVariable();
//                timeOutVariable.setVariableName(timeOutParameter);
//                timeOutVariable.setRelatedKeyword("RESPONSETIMEREQUIRED");
//                timeOutVariable.setRelatedCenterMode(centerMode);
//                String dlgType;
//                timeOutVariable.setUserEditable(false);
//                if (dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)){
//                    dlgType = "Request";
//                    maxTime = "1 Hour";
//                } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)){
//                    dlgType = "Subscription";
//                    maxTime = "24 Hours";
//                } else {
//                    dlgType= "Publication";
//                    maxTime = "24 Hours";
//                }
//                timeOutVariable.setUseInFirstPart(true);
//                timeOutVariable.setUseInSecondPart(true);
//                timeOutVariable.setRelatedDialogType(dlgType);
//                timeOutVariable.setDescription("This variable specifies the maximum time (in milliseconds) for a center to provide a response for a request.  Valid values are between 100ms and "+maxTime+".");
//                timeOutVariable.setSource("Defined by the calling test case.");
//                timeOutVariable.setDataType("Integer");
//                timeOutVariable.setLocalVariable(true);
//                dialogVariableList.add(timeOutVariable);
//                this.variables.add(timeOutVariable);
//            }

            Integer subStepNumber = 1;
            for (Variable thisVariable : dialogVariableList) {
                DialogVariable thisDlgVariable = (DialogVariable) thisVariable;
                // Only include variables that are inputs to dialog keywords.  Do not record output keyword variable values.
                if ((thisDlgVariable.isUseInFirstPart() || thisDlgVariable.isUseInSecondPart()) &&(!thisDlgVariable.isLocalVariable()&&!thisDlgVariable.isReturnVariable())) {
                    TestProcedureStep newStep = TMDDProcedureStepFactory.makeProcedureStep(TMDDProcedureStepFactory.GENERALVARIABLERECORD_STEP_TYPE,
                            thisVariable);
                    newStep.setStartStepNumber(getNextStepNumber());
                    super.addSection(newStep);
                }
            }



// Dialog Step/Section Definition Goes here!!!!
            ArrayList<Variable> variableList = new ArrayList<Variable>();
            for (Variable thisVariable : dialogVariableList) {
                variableList.add(thisVariable);
            }


            if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
//                PublicationWhileLoopTestStep publicationTestStep = new PublicationWhileLoopTestStep(needNumber, centerMode, dialog, variableList, referencedRequirements);
//                super.addSection(publicationTestStep);

                String subscriptionDialog = NRTM_RTM_Design_Data.getInstance().getPartnerDialog(needNumber, centerMode, dialog);
                ArrayList<String> subscriptionRequirementList = NRTM_RTM_Design_Data.getInstance().makeDialogRequirementsList(needNumber, sutCenterMode, subscriptionDialog);
                String subReferencedRequirements = "";
                for (String thisRequirement : subscriptionRequirementList) {
                    if (subReferencedRequirements.isEmpty()) {
                        subReferencedRequirements = subReferencedRequirements.concat(thisRequirement);
                    } else {
                        subReferencedRequirements = subReferencedRequirements.concat(", " + thisRequirement);
                    }
                }

                DialogTestStep thisDialogStep = new DialogTestStep(needNumber, subscriptionDialog, centerMode, variableList, true, subReferencedRequirements, false);
                if (thisDialogStep.isValid()) {
                    super.addSection(thisDialogStep);
                }

                DialogContentVerifyProcedureSection thisVerifySection = new DialogContentVerifyProcedureSection(needNumber, subscriptionDialog, centerMode, variableList, false, referencedRequirements);
                //               TestProcedureSection thisVerifySection = TMDDTestProcedureSectionFactory.makeProcedure(
                //               TMDDTestProcedureSectionFactory.VARIABLE_VERIFICATION_SECTION,
                //               needNumber, dialog, centerMode);
                this.variables.addAll(thisVerifySection.getVariablesList());
                thisVerifySection.setStartStepNumber(this.getNextStepNumber());
                super.addSection(thisVerifySection);

                /**
                String subscriptionDialog = NRTM_RTM_Design_Data.getInstance().getPartnerDialog(needNumber, centerMode, dialog);
                if (!subscriptionDialog.equals("")) {
                // Must do a subscription First
                DialogTestStep thisSubDialogStep1 = new DialogTestStep(subscriptionDialog, centerMode, variableList, true, referencedRequirements, false);
                if (thisSubDialogStep1.isValid()) {
                subSections.add(thisSubDialogStep1);
                }
                DialogTestStep thisSubDialogStep2 = new DialogTestStep(subscriptionDialog, centerMode, variableList, false, referencedRequirements, false);
                if (thisSubDialogStep2.isValid()) {
                OptionalContentVerifiedSetTestStep optionalVerificationStep = new OptionalContentVerifiedSetTestStep();
                subSections.add(optionalVerificationStep);
                subSections.add(thisSubDialogStep2);
                }
                }

                DialogTestStep thisDialogStep = new DialogTestStep(dialog, centerMode, variableList, true, referencedRequirements, false);
                if (thisDialogStep.isValid()) {
                subSections.add(thisDialogStep);
                }

                // Meat and Potatoes Here !!!
                
                DialogContentVerifyProcedureSection thisVerifySection = new DialogContentVerifyProcedureSection(needNumber, dialog, centerMode, variableList, false, referencedRequirements);
                //                TestProcedureSection thisVerifySection = TMDDTestProcedureSectionFactory.makeProcedure(
                //               TMDDTestProcedureSectionFactory.VARIABLE_VERIFICATION_SECTION,
                //               needNumber, dialog, centerMode);
                thisVerifySection.setStartStepNumber(this.getNextStepNumber());


                subSections.add(thisVerifySection);
                 */
                /**
                DialogTestStep thisWrapupDialogStep = new DialogTestStep(dialog, centerMode, variableList, false, referencedRequirements);
                if (thisWrapupDialogStep.isValid()) {
                thisVerifySection.subSections.add(thisWrapupDialogStep);
                //                    subSections.add(thisWrapupDialogStep);
                }
                 */
            } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                String publicationDialog = NRTM_RTM_Design_Data.getInstance().getPartnerDialog(needNumber, centerMode, dialog);
                ArrayList<String> pubscriptionRequirementList = NRTM_RTM_Design_Data.getInstance().makeDialogRequirementsList(needNumber, sutCenterMode, publicationDialog);
                String pubReferencedRequirements = "";
                for (String thisRequirement : pubscriptionRequirementList) {
                    if (pubReferencedRequirements.isEmpty()) {
                        pubReferencedRequirements = pubReferencedRequirements.concat(thisRequirement);
                    } else {
                        pubReferencedRequirements = pubReferencedRequirements.concat(", " + thisRequirement);
                    }
                }

                DialogTestStep thisDialogStep = new DialogTestStep(needNumber, dialog, centerMode, variableList, true, referencedRequirements, false);
                if (thisDialogStep.isValid()) {
                    super.addSection(thisDialogStep);
                }

                DialogContentVerifyProcedureSection thisVerifySection = new DialogContentVerifyProcedureSection(needNumber, dialog, centerMode, variableList, false, referencedRequirements);
                //               TestProcedureSection thisVerifySection = TMDDTestProcedureSectionFactory.makeProcedure(
                //               TMDDTestProcedureSectionFactory.VARIABLE_VERIFICATION_SECTION,
                //               needNumber, dialog, centerMode);
                this.variables.addAll(thisVerifySection.getVariablesList());
          thisVerifySection.setStartStepNumber(this.getNextStepNumber());
                super.addSection(thisVerifySection);

            ExitTestStep thisExitStep = new ExitTestStep();
            subSections.add(thisExitStep);

                // Meat and Potatoes Here !!!
                /**
                TestProcedureSection thisVerifySection = TMDDTestProcedureSectionFactory.makeProcedure(
                TMDDTestProcedureSectionFactory.VARIABLE_VERIFICATION_SECTION,
                needNumber, dialog, centerMode);

                subSections.add(thisVerifySection);
                 */
                /**
                DialogTestStep thisWrapupDialogStep = new DialogTestStep(dialog, centerMode, variableList, false, referencedRequirements);
                if (thisWrapupDialogStep.isValid()) {
                thisVerifySection.subSections.add(thisWrapupDialogStep);
                //                    subSections.add(thisWrapupDialogStep);
                }

                // Must also do a publication
                if (!publicationDialog.equals("")) {
                // Must do a subscription First
                DialogTestStep thisPubDialogStep1 = new DialogTestStep(publicationDialog, centerMode, variableList, true, referencedRequirements);
                if (thisPubDialogStep1.isValid()) {
                subSections.add(thisPubDialogStep1);
                }
                DialogTestStep thisPubDialogStep2 = new DialogTestStep(publicationDialog, centerMode, variableList, false, referencedRequirements);
                if (thisPubDialogStep2.isValid()) {
                OptionalContentVerifiedSetTestStep optionalVerificationStep = new OptionalContentVerifiedSetTestStep();
                subSections.add(optionalVerificationStep);
                subSections.add(thisPubDialogStep2);
                }
                }
                 */
            } else {
                RRTestingSection rrSection = new RRTestingSection(needNumber, dialog, centerMode);
                super.addSection(rrSection);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getScriptContent() {
        String scriptContent = "";
        String dialogList;
        if (dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)) {
            dialogList = " requestDialog = \"" + dialog + "\" ";
        } else {
            String partnerDialog = NRTM_RTM_Design_Data.getInstance().getPartnerDialog(needNumber, centerMode, dialog);
            if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                dialogList = " publicationDialog = \"" + dialog + "\" subscriptionDialog = \"" + partnerDialog + "\" ";
            } else {
                dialogList = " subscriptionDialog = \"" + dialog + "\" publicationDialog = \"" + partnerDialog + "\" ";
            }
        }
        dialogList = "";
        scriptContent = "<C2CRI-session beginSession=\"true\" infoStd=\"TMDDv3.03c\" appStd=\"${ApplicationLayerStandard}\" useWSDL=\"true\" testCaseIdentifier=\"${C2CRITestCaseID}\"" + dialogList + " riMode=\"" + centerMode + "\" >\n";
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
            scriptContent = scriptContent.concat(thisSection.getScriptContent()) + "\n";
            if (thisSection instanceof TestProcedureStep) {
                TestProcedureStep thisStep = (TestProcedureStep) thisSection;
                for (Section thisOne : thisStep.getSubSteps()) {
                    scriptContent = scriptContent.concat(thisOne.getScriptContent()) + "\n";
                }
            }
        }
        scriptContent = scriptContent.concat("</C2CRI-session>\n");
        return scriptContent;
    }

    @Override
    public ArrayList<TestProcedureStep> getStepsforDatabase() {
        ArrayList<TestProcedureStep> thisList = new ArrayList<TestProcedureStep>();

        for (Section thisSection : subSections) {
            for (TestProcedureStep thisStep : thisSection.getStepsforDatabase()) {
                thisList.add(thisStep);
//                for (Section subTestProcedureSection : thisStep.getSubSteps()) {
//                    for (TestProcedureStep thisSubStep : subTestProcedureSection.getStepsforDatabase()) {
//                        thisList.add(thisSubStep);
//                    }
//                }
            }
        }

        return thisList;
    }
}
