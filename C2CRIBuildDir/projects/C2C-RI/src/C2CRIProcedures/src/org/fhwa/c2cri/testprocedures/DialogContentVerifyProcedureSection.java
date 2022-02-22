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
public class DialogContentVerifyProcedureSection extends TestProcedureSection {

    public DialogContentVerifyProcedureSection(String needNumber, String dialog, String centerMode, ArrayList<Variable> variableList, boolean initialStep, String referencedRequirements) {
        try {


            // Meat and Potatoes Here !!!
            // We need to reverse the center mode to get the right verification message to match up with the testing
            // this is because we will verify what we get from the other center not what we send to it
            String sutCenterMode = centerMode.equals(TMDDParameters.TMDD_EC_MODE) ? TMDDParameters.TMDD_OC_Mode : TMDDParameters.TMDD_EC_MODE;
            TestProcedureSection thisVerifySection = TMDDTestProcedureSectionFactory.makeProcedure(
                    TMDDTestProcedureSectionFactory.VARIABLE_VERIFICATION_SECTION,
                    needNumber, dialog, sutCenterMode);
            thisVerifySection.setStartStepNumber(this.getNextStepNumber());




            if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                String subscriptionDialog = NRTM_RTM_Design_Data.getInstance().getPartnerDialog(needNumber, centerMode, dialog);
                /**
                 * ArrayList<String> subscriptionRequirementList =
                 * NRTM_RTM_Design_Data.getInstance().makeDialogRequirementsList(needNumber,
                 * sutCenterMode, subscriptionDialog); String
                 * subReferencedRequirements = ""; for (String thisRequirement :
                 * subscriptionRequirementList) { if
                 * (subReferencedRequirements.isEmpty()) {
                 * subReferencedRequirements =
                 * subReferencedRequirements.concat(thisRequirement); } else {
                 * subReferencedRequirements =
                 * subReferencedRequirements.concat(", " + thisRequirement); } }
                 */
                ArrayList<String> publicationRequirementList = NRTM_RTM_Design_Data.getInstance().makeDialogRequirementsList(needNumber, sutCenterMode, dialog);
                String pubReferencedRequirements = "";
                for (String thisRequirement : publicationRequirementList) {
                    if (pubReferencedRequirements.isEmpty()) {
                        pubReferencedRequirements = pubReferencedRequirements.concat(thisRequirement);
                    } else {
                        pubReferencedRequirements = pubReferencedRequirements.concat(", " + thisRequirement);
                    }
                }

                DialogTestStep thisWrapupDialogStep = new DialogTestStep(needNumber, dialog, centerMode, variableList, false, pubReferencedRequirements, true);
                if (thisWrapupDialogStep.isValid()) {
                    //       OptionalContentVerifiedVariable thisVariable = new OptionalContentVerifiedVariable();
                    //       this.variables.add(thisVariable);

                    OptionalContentVerifiedSetTestStep optionalVerificationStep = new OptionalContentVerifiedSetTestStep();
                    String msgName = (centerMode.equals(TMDDParameters.TMDD_EC_MODE) ? "REQUEST" : "RESPONSE");
                    MessageContentVerifyValueTestStep msgContentStep = new MessageContentVerifyValueTestStep(msgName);
                    thisVerifySection.addSection(msgContentStep);

                    thisVerifySection.addSection(optionalVerificationStep);
                    thisVerifySection.addSection(thisWrapupDialogStep);
//                    subSections.add(thisWrapupDialogStep);
                }

            } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                String publicationDialog = NRTM_RTM_Design_Data.getInstance().getPartnerDialog(needNumber, centerMode, dialog);
                ArrayList<String> subscriptionRequirementList = NRTM_RTM_Design_Data.getInstance().makeDialogRequirementsList(needNumber, sutCenterMode, dialog);
                String subReferencedRequirements = "";
                for (String thisRequirement : subscriptionRequirementList) {
                    if (subReferencedRequirements.isEmpty()) {
                        subReferencedRequirements = subReferencedRequirements.concat(thisRequirement);
                    } else {
                        subReferencedRequirements = subReferencedRequirements.concat(", " + thisRequirement);
                    }
                }
                DialogTestStep thisWrapupDialogStep = new DialogTestStep(needNumber, dialog, centerMode, variableList, false, subReferencedRequirements, true);
                if (thisWrapupDialogStep.isValid()) {
                    String msgName = (!centerMode.equals(TMDDParameters.TMDD_EC_MODE) ? "REQUEST" : "RESPONSE");
                    MessageContentVerifyValueTestStep msgContentStep = new MessageContentVerifyValueTestStep(msgName);
                    thisVerifySection.addSection(msgContentStep);

                    //       OptionalContentVerifiedVariable thisVariable = new OptionalContentVerifiedVariable();
                    //       this.variables.add(thisVariable);
                    OptionalContentVerifiedSetTestStep optionalVerificationStep = new OptionalContentVerifiedSetTestStep();
                    thisVerifySection.addSection(optionalVerificationStep);
                    thisVerifySection.addSection(thisWrapupDialogStep);
//                    subSections.add(thisWrapupDialogStep);
                }

                // Must also do a publication
                if (!publicationDialog.equals("")) {
                    // Must do a subscription First
//                    DialogTestStep thisPubDialogStep1 = new DialogTestStep(publicationDialog, centerMode, variableList, true, referencedRequirements, true);
                    ArrayList<String> pubscriptionRequirementList = NRTM_RTM_Design_Data.getInstance().makeDialogRequirementsList(needNumber, sutCenterMode, publicationDialog);
                    String pubReferencedRequirements = "";
                    for (String thisRequirement : pubscriptionRequirementList) {
                        if (pubReferencedRequirements.isEmpty()) {
                            pubReferencedRequirements = pubReferencedRequirements.concat(thisRequirement);
                        } else {
                            pubReferencedRequirements = pubReferencedRequirements.concat(", " + thisRequirement);
                        }
                    }
                    PublicationWhileLoopTestStep publicationTestStep = new PublicationWhileLoopTestStep(needNumber, centerMode, publicationDialog, variableList, pubReferencedRequirements);
                    thisVerifySection.addSection(publicationTestStep);

//                    if (thisPubDialogStep1.isValid()) {
//                        thisVerifySection.addSection(thisPubDialogStep1);
//                    }
//                    DialogTestStep thisPubDialogStep2 = new DialogTestStep(publicationDialog, centerMode, variableList, false, referencedRequirements, true);
//                    if (thisPubDialogStep2.isValid()) {
//                        OptionalContentVerifiedSetTestStep optionalVerificationStep = new OptionalContentVerifiedSetTestStep();
//                        thisVerifySection.addSection(optionalVerificationStep);
//                        thisVerifySection.addSection(thisPubDialogStep2);
//                    }
                }


            } else {
                DialogTestStep thisWrapupDialogStep = new DialogTestStep(needNumber, dialog, centerMode, variableList, false, referencedRequirements, true);
                if (thisWrapupDialogStep.isValid()) {
                    String msgName = (!centerMode.equals(TMDDParameters.TMDD_EC_MODE) ? "REQUEST" : "RESPONSE");
                    MessageContentVerifyValueTestStep msgContentStep = new MessageContentVerifyValueTestStep(msgName);
                    thisVerifySection.addSection(msgContentStep);


                    //       OptionalContentVerifiedVariable thisVariable = new OptionalContentVerifiedVariable();
                    //       this.variables.add(thisVariable);
                    OptionalContentVerifiedSetTestStep optionalVerificationStep = new OptionalContentVerifiedSetTestStep();
                    thisVerifySection.addSection(optionalVerificationStep);
                    thisVerifySection.addSection(thisWrapupDialogStep);
                } else {
                    String msgName = (!centerMode.equals(TMDDParameters.TMDD_EC_MODE) ? "REQUEST" : "RESPONSE");
                    MessageContentVerifyValueTestStep msgContentStep = new MessageContentVerifyValueTestStep(msgName);
                    thisVerifySection.addSection(msgContentStep);
                }
            }

            if (thisVerifySection.subSections.size() > 0) {
                // We don't add a content verification section to the procedure for checking the response
                // to a publication.  If we ever want to check for a c2cAdminReceipt message then we should remove this if condition.
                // This occurs when we have a publication dialog and the center mode is OC.
                if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)&&centerMode.equals(TMDDParameters.TMDD_OC_Mode)){
                    // Do Nothing
                } else {
                    DialogContentVerifyTestStep theStep = new DialogContentVerifyTestStep(dialog);
                    ArrayList<Section> stepSubSections = new ArrayList<Section>();
                    stepSubSections = thisVerifySection.subSections;

//                String msgName = (centerMode.equals(TMDDParameters.TMDD_EC_MODE)?"REQUEST":"RESPONSE");
//                MessageContentVerifyValueTestStep msgContentStep = new MessageContentVerifyValueTestStep(msgName);
//                stepSubSections.add(msgContentStep);

                    theStep.setSubSteps(stepSubSections);



                    addSection(theStep);
                }
            }

            // Error Message checking is applicable when an EC is checking a 
            // Subscription or Request response from an OC.  It is also applicable when
            // an OC is checking a Publication response from an EC.
            if ((centerMode.equals(TMDDParameters.TMDD_EC_MODE) && (dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX) || (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX))))
                    || (centerMode.equals(TMDDParameters.TMDD_OC_Mode) && (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)))) {
                TestProcedureSection thisErrorVerifySection = TMDDTestProcedureSectionFactory.makeProcedure(
                        TMDDTestProcedureSectionFactory.ERROR_RECEIVED_SECTION,
                        needNumber, dialog, sutCenterMode);
                thisErrorVerifySection.setStartStepNumber(this.getNextStepNumber());
                if (thisErrorVerifySection.subSections.size() > 0) {
                    DialogErrorContentVerifyTestStep theStep = new DialogErrorContentVerifyTestStep(dialog);
                    ArrayList<Section> stepSubSections = new ArrayList<Section>();
                    stepSubSections = thisErrorVerifySection.subSections;

                    theStep.setSubSteps(stepSubSections);

                    addSection(theStep);
                }
            }

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
             *
             * String subScriptContent = "";
             *
             * for (TestProcedureStep thisStep :
             * thisSection.getStepsforDatabase()) {
             *
             * for (Section subTestProcedureSection : thisStep.getSubSteps()){
             * for (TestProcedureStep thisSubStep :
             * subTestProcedureSection.getStepsforDatabase()){ subScriptContent
             * = subScriptContent.concat("
             * "+thisSubStep.getScriptContent()+"\n"); } } }
             *
             * scriptContent = scriptContent.replace("<SubSteps>",
             * subScriptContent);
             */
            scriptContent = scriptContent.concat(thisSection.getScriptContent()) + "\n";
//            for (TestProcedureStep thisStep :thisSection.getStepsforDatabase()){
//                scriptContent = scriptContent.concat(thisStep.getScriptContent())+"\n";
//            }
        }
        return scriptContent;
    }

    @Override
    public ArrayList<TestProcedureStep> getStepsforDatabase() {
        ArrayList<TestProcedureStep> thisList = new ArrayList<TestProcedureStep>();

        for (Section thisSection : subSections) {
            for (TestProcedureStep thisStep : thisSection.getStepsforDatabase()) {
                thisList.add(thisStep);
//                for (Section subTestProcedureSection : thisStep.getSubSteps()){
//                    for (TestProcedureStep thisSubStep : subTestProcedureSection.getStepsforDatabase()){
//                       thisList.add(thisSubStep);
//                    }
//                }
            }
        }

        return thisList;
    }
}
