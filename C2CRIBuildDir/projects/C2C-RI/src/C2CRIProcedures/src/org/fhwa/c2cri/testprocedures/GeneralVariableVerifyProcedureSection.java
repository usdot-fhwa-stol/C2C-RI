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
public class GeneralVariableVerifyProcedureSection extends TestProcedureSection {

    public GeneralVariableVerifyProcedureSection(String needNumber, String dialog, String centerMode) {
        String optionalList = "";
//        OptionalMessageContent.getInstance().clearOptionalList();
        try {

            Integer subStepNumber = 0;

            ArrayList<MandatoryPRLVariable> mandatoryResponseList;
//            OptionalPRLVariables optionalPRLVariables = new OptionalPRLVariables();
            if (needNumber.equals("6") && dialog.equals("dlFullEventUpdateRequest")) {
                System.out.println("Debug Step here...");
            }
            ArrayList<String> manRequirementList = NRTM_RTM_Design_Data.getInstance().makeMessageMandatoryRequirementsList(needNumber, centerMode, dialog);

            for (String requirementID : manRequirementList) {
            if (needNumber.equals("101") && requirementID.equals("3.3.5.1.7.2.1")) {
                System.out.println("Debug Step here...");
            }
                mandatoryResponseList = MandatoryPRLVariables.getInstance().getMatchingMandatoryPRLVariables(needNumber, requirementID);
//                this.variables.addAll(optionalResponseList);
                // This section includes the procedure and optional NRTM variables that need to be recorded.

                subStepNumber = 1;
                for (Variable thisVariable : mandatoryResponseList) {

                    TestProcedureStep newStep = TMDDProcedureStepFactory.makeProcedureStep(TMDDProcedureStepFactory.GENERALVARIABLEVERIFY_STEP_TYPE,
                            thisVariable);
                    Integer primaryStepNumber = getNextStepNumber();
                    newStep.setStartStepNumber(primaryStepNumber);

                    // Add subSteps to this GeneralVariableVerifyTestStep
                    ArrayList<String> relatedMessageList = NRTM_RTM_Design_Data.getInstance().getRelatedMessagesList(needNumber, centerMode, dialog);
                    ArrayList<Section> stepSubSections = new ArrayList<Section>();
                    for (String thisMessage : relatedMessageList) {
                        ArrayList<String> elementList = NRTM_RTM_Design_Data.getInstance().makeMessageMandatoryElementsList(needNumber, thisMessage, requirementID);
                        MessageDetailDesignMatcher theMatcher = new MessageDetailDesignMatcher(thisMessage);
                        for (String thisElement : elementList) {
                            ArrayList<MessageDetailDesignElement> designElementList = theMatcher.getMatchesForElementName(thisElement, Integer.parseInt(needNumber), requirementID);
                            for (MessageDetailDesignElement thisDesignElement : designElementList) {
                                TestProcedureStep newDetailStep = new MessageContentVerifyTestSubStep(thisDesignElement, subStepNumber, requirementID, (dialog.endsWith("Update") ? true : false));
                                //                               optionalList = optionalList.concat(thisVariable.getName()+","+thisDesignElement.getElementName()+",tmdd:"+thisDesignElement.getFullParentVariableName()+";");
                                newDetailStep.setPrimaryStepId(primaryStepNumber.toString());
                                newDetailStep.setIsSubStep(false);
                                subStepNumber++;
                                stepSubSections.add(newDetailStep);
                                subSections.add(newDetailStep);
                            }
                        }
                    }


//                    newStep.setSubSteps(stepSubSections);
//                    subSections.add(newStep);
                }
            }

            if (centerMode.equals("EC")) {
                ArrayList<RequiredContentVariable> requiredContentList;
//            OptionalPRLVariables optionalPRLVariables = new OptionalPRLVariables();
                requiredContentList = RequiredContentVariables.getInstance().getMatchingVariables(needNumber);
//                this.variables.addAll(requiredContentList);
                // This section includes the procedure and optional NRTM variables that need to be recorded.

                for (RequiredContentVariable thisVariable : requiredContentList) {
                    if (dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)&&(!thisVariable.getMessage().contains("Request"))){
                        System.out.println("********************************************************\n"+
                                           "*  Skipped "+thisVariable.getName()+" in message + "+thisVariable.getMessage()+" on need " +needNumber + "  *"+
                                           "\n*********************************************************");
                    } else {
                       
                    
                    subStepNumber++;
                    
                    TestProcedureStep newStep = TMDDProcedureStepFactory.makeProcedureStep(TMDDProcedureStepFactory.REQUIREDCONTENTVALUEVERIFY_STEP_TYPE,
                            thisVariable);
                    Integer primaryStepNumber = getNextStepNumber();
                    newStep.setStartStepNumber(primaryStepNumber);

                    // Add subSteps to this GeneralVariableVerifyTestStep
//                    ArrayList<String> relatedMessageList = NRTM_RTM_Design_Data.getInstance().getRelatedMessagesList(needNumber, centerMode, dialog);
                    ArrayList<Section> stepSubSections = new ArrayList<Section>();
//                    for (String thisMessage : relatedMessageList) {
//                        ArrayList<String> elementList = NRTM_RTM_Design_Data.getInstance().makeMessageMandatoryElementsList(needNumber, thisMessage, requirementID);
                    MessageDetailDesignMatcher theMatcher = new MessageDetailDesignMatcher(thisVariable.getMessage());
//                        for (String thisElement : elementList) {
                    ArrayList<MessageDetailDesignElement> designElementList = theMatcher.getMatchesForElementName(thisVariable.getName(), Integer.parseInt(needNumber));
                    for (MessageDetailDesignElement thisDesignElement : designElementList) {
                        TestProcedureStep newDetailStep = new RequiredValueVerifyTestSubStep(thisDesignElement, subStepNumber, thisVariable.getRequirementID(), (dialog.endsWith("Update") ? true : false), thisVariable.getValidValues());
                        //                              optionalList = optionalList.concat(thisVariable.getName()+","+thisDesignElement.getElementName()+",tmdd:"+thisDesignElement.getFullParentVariableName()+";");
                        newDetailStep.setPrimaryStepId(primaryStepNumber.toString());
                        newDetailStep.setIsSubStep(false);
                        subStepNumber++;
                        subSections.add(newDetailStep);
                        stepSubSections.add(newDetailStep);
                    }

                    //                      }


//                    newStep.setSubSteps(stepSubSections);
//                    subSections.add(newStep);
                    }
                }
//            }            
            }

            ArrayList<OptionalPRLVariable> optionalResponseList;
//            OptionalPRLVariables optionalPRLVariables = new OptionalPRLVariables();
            ArrayList<String> requirementList = NRTM_RTM_Design_Data.getInstance().makeMessageOptionalRequirementsList(needNumber, centerMode, dialog);

            for (String requirementID : requirementList) {
                optionalResponseList = OptionalPRLVariables.getInstance().getMatchingOptionalPRLVariables(needNumber, requirementID);
                this.variables.addAll(optionalResponseList);
                // This section includes the procedure and optional NRTM variables that need to be recorded.

                subStepNumber++;
                for (Variable thisVariable : optionalResponseList) {
                    TestProcedureStep newStep = TMDDProcedureStepFactory.makeProcedureStep(TMDDProcedureStepFactory.GENERALVARIABLEVERIFY_STEP_TYPE,
                            thisVariable);
                    Integer primaryStepNumber = getNextStepNumber();
                    newStep.setStartStepNumber(primaryStepNumber);

                    // Add subSteps to this GeneralVariableVerifyTestStep
                    ArrayList<String> relatedMessageList = NRTM_RTM_Design_Data.getInstance().getRelatedMessagesList(needNumber, centerMode, dialog);
                    ArrayList<Section> stepSubSections = new ArrayList<Section>();
                    for (String thisMessage : relatedMessageList) {
                        ArrayList<String> elementList = NRTM_RTM_Design_Data.getInstance().makeMessageOptionalElementsList(needNumber, thisMessage, requirementID);
                        MessageDetailDesignMatcher theMatcher = new MessageDetailDesignMatcher(thisMessage);
                        for (String thisElement : elementList) {
                            ArrayList<MessageDetailDesignElement> designElementList = theMatcher.getMatchesForElementName(thisElement, Integer.parseInt(needNumber), requirementID);
                            for (MessageDetailDesignElement thisDesignElement : designElementList) {
                                TestProcedureStep newDetailStep = new MessageContentVerifyTestSubStep(thisDesignElement, subStepNumber, requirementID, (dialog.endsWith("Update") ? true : false));
                                optionalList = optionalList.concat(thisVariable.getName() + "," + thisDesignElement.getElementName() + ",tmdd:" + thisDesignElement.getFullParentVariableName() + ";");
                                newDetailStep.setPrimaryStepId(primaryStepNumber.toString());
                                subStepNumber++;
                                stepSubSections.add(newDetailStep);
                            }

                        }
                    }


                    newStep.setSubSteps(stepSubSections);
                    subSections.add(newStep);
                }
            }

            OptionalMessageContent.getInstance().setOptionalList(optionalList);
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
                //               for (Section subTestProcedureSection : thisStep.getSubSteps()){
                //                   for (TestProcedureStep thisSubStep : subTestProcedureSection.getStepsforDatabase()){
                //                      thisList.add(thisSubStep);
                //                   }
                //               }
            }
        }

        return thisList;
    }
}
