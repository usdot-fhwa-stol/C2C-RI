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
public class DialogTestStep extends TestProcedureStep {

    private ArrayList<Variable> relatedVariables;
    String stepDescription;
    String stepType;
    String stepDialog;
    String stepDialogType;
    String needNumber;
    String timeOutParameter;
    boolean valid = true;
    boolean initialStep;

    public DialogTestStep(String needNumber, String dialog, String centerMode, ArrayList<Variable> theVariables, boolean initialStep, String requirements, boolean isSubStep) {
        this.relatedVariables = theVariables;
        this.stepDialog = dialog;
        this.initialStep = initialStep;
        this.setIsSubStep(isSubStep);
        this.needNumber = needNumber;
        
        
        this.timeOutParameter = NRTM_RTM_Design_Data.getOtherReqVariableName(needNumber, requirements);
        
        if (dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)) {
            stepDialogType = TMDDParameters.TMDD_REQUEST_SUFFIX;
        } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)){
            stepDialogType = TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX;
        } else if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)){
//            stepDialogType = TMDDParameters.TMDD_PUBLICATION_SUFFIX;
            stepDialogType = "Publication";
        }

//        String parameters = "DIALOG=" + dialog + "<LF>"+(!dialog.endsWith("Subscription")?"RESPONSETIMEREQUIRED="+timeOutParameter+"<LF>":"");
        String parameters = "DIALOG=" + dialog + "<LF>"+"RESPONSETIMEREQUIRED="+timeOutParameter+"<LF>";
        String returnParameters = "";
        if (!initialStep)parameters = parameters.concat("CONTENTVERIFIED = CONTENTVERIFIED<LF>");
        for (Variable thisVariable : theVariables) {
            if (initialStep) {
                if (thisVariable instanceof DialogVariable) {
                    DialogVariable dlgVariable = (DialogVariable) thisVariable;
                    if (dlgVariable.isUseInFirstPart()&&(dlgVariable.getRelatedDialogType().contains(stepDialogType))&&(!dlgVariable.isLocalVariable())) {
                        if (!dlgVariable.isReturnVariable()){
                           parameters = parameters.concat(dlgVariable.getRelatedKeyword()+" = "+thisVariable.getName() + "<LF>");
                        } else {
                            if (!returnParameters.contains(dlgVariable.getName()))
                               returnParameters = returnParameters.concat(dlgVariable.getName() + "<LF>");
                        }
                    }
                }
            } else {
                if (thisVariable instanceof DialogVariable) {
                    DialogVariable dlgVariable = (DialogVariable) thisVariable;
                    if (dlgVariable.isUseInSecondPart()&&(dlgVariable.getRelatedDialogType().contains(stepDialogType)&&(!dlgVariable.isLocalVariable()))) {
                        if (!dlgVariable.isReturnVariable()){
                           parameters = parameters.concat(dlgVariable.getRelatedKeyword()+" = "+thisVariable.getName() + "<LF>");
                        } else {
                            if (!returnParameters.contains(dlgVariable.getName()))
                              returnParameters = returnParameters.concat(dlgVariable.getName() + "<LF>");
                        }
                    }
                }

            }
        }
        parameters = parameters.concat(returnParameters.isEmpty()?"":"<LF>Returns:<LF>"+returnParameters);

        if (dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)) {
//            stepDialogType = TMDDParameters.TMDD_REQUEST_SUFFIX;
            stepDescription = (centerMode.equals(TMDDParameters.TMDD_OC_Mode) ? (initialStep ? "" : "POSTCONDITION: ") : "")+"REQUEST-RESPONSE-" + centerMode + (centerMode.equals(TMDDParameters.TMDD_OC_Mode) ? (initialStep ? "-RECEIVE" : "-REPLY") : "") + " with the following parameters:  <LF>" + parameters;
            stepType = "REQUEST-RESPONSE-" + centerMode + (centerMode.equals(TMDDParameters.TMDD_OC_Mode) ? (initialStep ? "-RECEIVE" : "-REPLY") : "");
            if ((centerMode.equals(TMDDParameters.TMDD_EC_MODE)) && (!initialStep)) {
                valid = false;
            }
        } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
//            stepDialogType = TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX;
            stepDescription = (centerMode.equals(TMDDParameters.TMDD_OC_Mode) ? (initialStep ? "" : "POSTCONDITION: ") : "")+"SUBSCRIPTION-" + centerMode + (centerMode.equals(TMDDParameters.TMDD_OC_Mode) ? (initialStep ? "-RECEIVE" : "-REPLY") : "")+ " with the following parameters:  <LF>" + parameters;
            stepType = "SUBSCRIPTION-" + centerMode+ (centerMode.equals(TMDDParameters.TMDD_OC_Mode) ? (initialStep ? "-RECEIVE" : "-REPLY") : "");
            if ((centerMode.equals(TMDDParameters.TMDD_EC_MODE)) && (!initialStep)) {
                valid = false;
            }

        } else if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
//            stepDialogType = TMDDParameters.TMDD_PUBLICATION_SUFFIX;
            stepDescription = (centerMode.equals(TMDDParameters.TMDD_EC_MODE) ? (initialStep ? "" : "POSTCONDITION: ") : "")+"PUBLICATION-" + centerMode +(centerMode.equals(TMDDParameters.TMDD_EC_MODE) ? (initialStep ? "-RECEIVE" : "-REPLY") : "")+" with the following parameters:  <LF>" + parameters;
            stepType = "PUBLICATION-" + centerMode+(centerMode.equals(TMDDParameters.TMDD_EC_MODE) ? (initialStep ? "-RECEIVE" : "-REPLY") : "");
            if ((centerMode.equals(TMDDParameters.TMDD_OC_Mode)) && (!initialStep)) {
                valid = false;
            }
        }

        this.setDescription(stepDescription);
        this.setResults((requirements.isEmpty() ? "NA" : "PASS/FAIL (" + requirements + ")"));
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

//        String stepParameters = "Dialog = \"" + stepDialog + "\" functionId = \""+stepDialog+"\" "+(!stepDialog.endsWith("Subscription")?"RESPONSETIMEREQUIRED = \"${"+this.timeOutParameter+ "}\" ":"");
        String stepParameters = (this.timeOutParameter != null && !this.timeOutParameter.isEmpty())?"Dialog = \"" + stepDialog + "\" functionId = \""+stepDialog+"\" RESPONSETIMEREQUIRED = \"${"+this.timeOutParameter+ "}\" ":
                                                "Dialog = \"" + stepDialog + "\" functionId = \""+stepDialog+"\" RESPONSETIMEREQUIRED = \"60000\" ";
        if (!initialStep)stepParameters = stepParameters.concat("CONTENTVERIFIED=\"${CONTENTVERIFIED}\" ");
        for (Variable thisVariable : relatedVariables) {
            if (initialStep) {
                if (thisVariable instanceof DialogVariable) {
                    DialogVariable dlgVariable = (DialogVariable) thisVariable;
                    if (dlgVariable.isUseInFirstPart()&&(dlgVariable.getRelatedDialogType().contains(stepDialogType))&&(!dlgVariable.isLocalVariable())) {
                        if (!dlgVariable.isReturnVariable()){
                           stepParameters = stepParameters.concat(dlgVariable.getRelatedKeyword() + "= \"${" + thisVariable.getName() + "}\" ");
                        }
                     }
                }
            } else {
                if (thisVariable instanceof DialogVariable) {
                    DialogVariable dlgVariable = (DialogVariable) thisVariable;
                    if (dlgVariable.isUseInSecondPart()&&(dlgVariable.getRelatedDialogType().contains(stepDialogType))&&(!dlgVariable.isLocalVariable())){
//                    if (dlgVariable.isUseInSecondPart()&&((dlgVariable.getRelatedDialogType().contains(stepDialogType))||
//                            ((dlgVariable.getRelatedDialogType().endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)||
//                            dlgVariable.getRelatedDialogType().endsWith("Publication"))))) {
                        if (!dlgVariable.isReturnVariable()){
                           stepParameters = stepParameters.concat(dlgVariable.getRelatedKeyword() + "= \"${" + thisVariable.getName() + "}\" ");
                        }
                    }
                }

            }

        }

        result = stepType.endsWith("-REPLY")?"<postcondition>\n":"";
        result = result.concat("<testStep functionId=\"Step " + this.getId() + " " + this.stepDescription.replace("<LF>", " ") + "\" >"
                + "\n   <" + stepType + " " + stepParameters + " />"
                + "\n</testStep>\n");
        result = result.concat(stepType.endsWith("-REPLY")?"</postcondition>\n":"");


        return result;
    }

    public boolean isValid() {
        return valid;
    }
}
