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
public class GeneralVariableRecordProcedureSection extends TestProcedureSection {

    public GeneralVariableRecordProcedureSection(String needNumber, String dialog, String centerMode) {
        try {

            ArrayList<ProcedureVariable> responseList;
            ProcedureVariables procedureVariables = new ProcedureVariables();
            responseList = procedureVariables.getMatchingProcedureVariables(dialog, centerMode);
            this.variables.addAll(responseList);
            // This section includes the procedure and optional NRTM variables that need to be recorded.

            for (Variable thisVariable : responseList) {
                if (thisVariable instanceof DialogVariable) {
                    DialogVariable dlgVariable = (DialogVariable) thisVariable;
                    if (!dlgVariable.isReturnVariable() && !dlgVariable.isLocalVariable()) {
                        TestProcedureStep newStep = TMDDProcedureStepFactory.makeProcedureStep(TMDDProcedureStepFactory.GENERALVARIABLERECORD_STEP_TYPE,
                                thisVariable);
                        newStep.setStartStepNumber(getNextStepNumber());
                        subSections.add(newStep);

                    }
                } else {
                    TestProcedureStep newStep = TMDDProcedureStepFactory.makeProcedureStep(TMDDProcedureStepFactory.GENERALVARIABLERECORD_STEP_TYPE,
                            thisVariable);
                    newStep.setStartStepNumber(getNextStepNumber());
                    subSections.add(newStep);

                }
            }

            ArrayList<OptionalPRLVariable> optionalResponseList;
            //           OptionalPRLVariables optionalPRLVariables = OptionalPRLVariables().getIN;
            ArrayList<String> requirmentList = NRTM_RTM_Design_Data.getInstance().makeMessageOptionalRequirementsList(needNumber, centerMode, dialog);


            ArrayList<String> allRequirementList = NRTM_RTM_Design_Data.getInstance().makeMessageMandatoryRequirementsList(needNumber, centerMode, dialog);
            allRequirementList.addAll(requirmentList);
            String referencedRequirements = "";
            for (String thisRequirement : allRequirementList) {
                if (referencedRequirements.isEmpty()) {
                    referencedRequirements = referencedRequirements.concat(thisRequirement);
                } else {
                    referencedRequirements = referencedRequirements.concat(", " + thisRequirement);
                }
            }

            ArrayList<NRTM_RTM_Design_Data.DialogRecord> dialogList = NRTM_RTM_Design_Data.getDialogRequirementVariableNames(needNumber);

//            String timeOutParameter = NRTM_RTM_Design_Data.getOtherReqVariableName(needNumber, referencedRequirements);
//            if (!timeOutParameter.isEmpty()){
            for (NRTM_RTM_Design_Data.DialogRecord thisRecord : dialogList) {
                if (!thisRecord.getRequirementParameter().isEmpty()&&!thisRecord.getOtherRequirementParameter().isEmpty()) {
                    if ((dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX) && thisRecord.getRequirement().contains("Request"))
                            || (!dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX) && thisRecord.getRequirement().contains("Subscribe"))
                            || (!dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX) && thisRecord.getRequirement().contains("Publish"))) {

                        OptionalPRLVariable thisVariable = new OptionalPRLVariable();
                        thisVariable.setVariableName(thisRecord.getOtherRequirementParameter());
                        thisVariable.setRequirementID(thisRecord.getRequirementID());
                        thisVariable.setDataType("Integer");
                        thisVariable.setRecordText("CONFIGURE: Determine the dialog performance requirement for "+thisRecord.getRequirement()+" (NRTM "+thisRecord.getRequirementID()+").  RECORD this value as:  "+thisRecord.getOtherRequirementParameter());
                        thisVariable.setRecordScriptText("<testStep functionId = \"<Step> CONFIGURE: Determine the dialog performance requirement for "+thisRecord.getRequirement()+" (NTRM "+thisRecord.getRequirementID()+"}.  RECORD this value as: "+thisRecord.getOtherRequirementParameter()+" = ${"+thisRecord.getOtherRequirementParameter()+"}\" passfailResult= \"False\"/>");
                        this.variables.add(thisVariable);

                    TestProcedureStep newStep = TMDDProcedureStepFactory.makeProcedureStep(TMDDProcedureStepFactory.GENERALVARIABLERECORD_STEP_TYPE,
                            thisVariable);
                    newStep.setStartStepNumber(getNextStepNumber());
                    subSections.add(newStep);
                    }
                }

            }



            for (String requirementID : requirmentList) {
                optionalResponseList = OptionalPRLVariables.getInstance().getMatchingOptionalPRLVariables(needNumber, requirementID);
                this.variables.addAll(optionalResponseList);
                // This section includes the procedure and optional NRTM variables that need to be recorded.

                for (Variable thisVariable : optionalResponseList) {
                    TestProcedureStep newStep = TMDDProcedureStepFactory.makeProcedureStep(TMDDProcedureStepFactory.GENERALVARIABLERECORD_STEP_TYPE,
                            thisVariable);
                    newStep.setStartStepNumber(getNextStepNumber());
                    subSections.add(newStep);
                }
            }

            // Add any applicable optional error message elements
            requirmentList = NRTM_RTM_Design_Data.getInstance().makeErrorMessageOptionalRequirementsList(needNumber, centerMode, dialog);
            for (String requirementID : requirmentList) {
                if ((centerMode.equals(TMDDParameters.TMDD_OC_Mode) && !dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX))
                        || (centerMode.equals(TMDDParameters.TMDD_EC_MODE) && !dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX))) {
                    optionalResponseList = OptionalPRLVariables.getInstance().getMatchingOptionalPRLVariables(needNumber, requirementID);
                    this.variables.addAll(optionalResponseList);
                    // This section includes the procedure and optional NRTM variables that need to be recorded.

                    for (Variable thisVariable : optionalResponseList) {
                        TestProcedureStep newStep = TMDDProcedureStepFactory.makeProcedureStep(TMDDProcedureStepFactory.GENERALVARIABLERECORD_STEP_TYPE,
                                thisVariable);
                        newStep.setStartStepNumber(getNextStepNumber());
                        subSections.add(newStep);
                    }
                }
            }
            
            
            if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX) || dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                String relatedDialog = NRTM_RTM_Design_Data.getInstance().getPartnerDialog(needNumber, centerMode, dialog);
                requirmentList = NRTM_RTM_Design_Data.getInstance().makeMessageOptionalRequirementsList(needNumber, centerMode, relatedDialog);

                for (String requirementID : requirmentList) {
                    optionalResponseList = OptionalPRLVariables.getInstance().getMatchingOptionalPRLVariables(needNumber, requirementID);
                    this.variables.addAll(optionalResponseList);
                    // This section includes the procedure and optional NRTM variables that need to be recorded.

                    for (Variable thisVariable : optionalResponseList) {
                        TestProcedureStep newStep = TMDDProcedureStepFactory.makeProcedureStep(TMDDProcedureStepFactory.GENERALVARIABLERECORD_STEP_TYPE,
                                thisVariable);
                        newStep.setStartStepNumber(getNextStepNumber());
                        subSections.add(newStep);
                    }
                }

            }
            
//            
//            ArrayList<NRTM_RTM_Design_Data.DialogRecord> errorOptList = NRTM_RTM_Design_Data.getOptionalErrorRequirementVariableNames(needNumber);
//
////            String timeOutParameter = NRTM_RTM_Design_Data.getOtherReqVariableName(needNumber, referencedRequirements);
////            if (!timeOutParameter.isEmpty()){
//            for (NRTM_RTM_Design_Data.DialogRecord thisRecord : errorOptList) {
//                if (!thisRecord.getRequirementParameter().isEmpty()) {
//                    if ((centerMode.equals(TMDDParameters.TMDD_OC_Mode) && !dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX))
//                       || (centerMode.equals(TMDDParameters.TMDD_EC_MODE) && !dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX))){
//                        
//                        OptionalPRLVariable thisVariable = new OptionalPRLVariable();
//                        thisVariable.setVariableName(thisRecord.getOtherRequirementParameter());
//                        thisVariable.setRequirementID(thisRecord.getRequirementID());
//                        thisVariable.setDataType("Boolean");
//                        this.variables.add(thisVariable);
//                    }
//                }
//
//            }

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
            }
        }


        return thisList;
    }
}
