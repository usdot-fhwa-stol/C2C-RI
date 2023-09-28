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
public class MessageContentVerifyTestSubStep extends TestProcedureStep{
    private MessageDetailDesignElement designElement;
    private String requirementID;
    private boolean relatedToPublication;

    public MessageContentVerifyTestSubStep(MessageDetailDesignElement theDesignElement, Integer subStepNumber, String requirementID, boolean relatedToPublication){

        this.setIsSubStep(true);
//        this.setId(Integer.toString(subStepNumber));
        this.designElement = theDesignElement;
        this.requirementID = requirementID;
        this.relatedToPublication = relatedToPublication;
        String parentVariable = "";
        if (this.designElement.getDataConceptType().equalsIgnoreCase("Message")){
            parentVariable = this.designElement.getElementName();
        } else {
            parentVariable = this.designElement.getFullParentVariableName();
        }

        String parentVariableType = "";
        if (this.designElement.getDataConceptType().equalsIgnoreCase("Message")){
            parentVariableType = "message";
        } else {
            if (parentVariable.indexOf(".")>=0) {
                parentVariableType = "data frame";
            } else {
                parentVariableType = "message";
            }
        }

        String variableType = "";
        if (this.designElement.getDataConceptType().equalsIgnoreCase("Message")){
            variableType = "message";
        } else {
            if (this.designElement.getBaseType().equals("anyType")) {
                variableType = "data frame";
            } else {
                variableType = "data element";
            }
        }
        
        
        this.setDescription("VERIFY that "+(parentVariable.indexOf(".")>=0 ? (beginsWithVowel(this.designElement.getElementName())?"an ":"a ")+this.designElement.getElementName()+" "+variableType+ " exists within each instance of "+parentVariableType+" "+parentVariable: (beginsWithVowel(this.designElement.getElementName())?"an ":"a ") + this.designElement.getElementName()+" "+variableType+(parentVariable.equals(this.designElement.getElementName())?" was received.":" exists within "+parentVariableType+" "+parentVariable+".")));
        this.setResults("PASS/FAIL ("+requirementID+")");
    }

    public ArrayList<TestProcedureStep> getStepsforDatabase() {
        ArrayList<TestProcedureStep> theList = new ArrayList<TestProcedureStep>();
        String result;
        result = this.getDescription();

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
        String parentInstance = this.designElement.getFullParentVariableName();
        String result;
        
        String parentVariable = "";
        if (this.designElement.getDataConceptType().equalsIgnoreCase("Message")){
            parentVariable = this.designElement.getElementName();
        } else {
            parentVariable = this.designElement.getFullParentVariableName();
        }

        String parentVariableType = "";
        if (this.designElement.getDataConceptType().equalsIgnoreCase("Message")){
            parentVariableType = "message";
        } else {
            if (parentVariable.indexOf(".")>=0) {
                parentVariableType = "data frame";
            } else {
                parentVariableType = "message";
            }
        }

        String variableType = "";
        if (this.designElement.getDataConceptType().equalsIgnoreCase("Message")){
            variableType = "message";
        } else {
            if (this.designElement.getBaseType().equals("anyType")) {
                variableType = "data frame";
            } else {
                variableType = "data element";
            }
        }
        
        
        String messageName = "";
        if (parentVariable.contains("errorReportMsg")){
            messageName = "RESPONSE";            
        } else if (parentInstance.contains("RequestMsg")){
            messageName = "REQUEST";
        } else if (this.designElement.getElementName().contains("RequestMsg")){
            messageName = "REQUEST";
        } else if (this.designElement.getElementName().contains("ResponseMsg")){
            if (!this.relatedToPublication){
                messageName = "RESPONSE";
            }else if (this.designElement.getElementName().contains("errorReportMsg")){
                messageName = "RESPONSE";
            }else{
                messageName = "REQUEST";
            }
        } else {
            // The initial message of a publication is a REQUEST, but it does not include RequestMsg in its title.
             if (this.designElement.getElementName().contains("errorReportMsg")){
                messageName = "RESPONSE";
            } else if (this.relatedToPublication){
                messageName = "REQUEST";
            } else {
                messageName = "RESPONSE";
            }
        }

        result = "   <testStep functionId= \"Step "+this.getId()+" VERIFY that "+(parentVariable.indexOf(".")>=0 ? (beginsWithVowel(this.designElement.getElementName())?"an ":"a ")+this.designElement.getElementName()+" "+variableType+ " exists within each instance of "+parentVariableType+" "+parentVariable: (beginsWithVowel(this.designElement.getElementName())?"an ":"a ") + this.designElement.getElementName()+" "+variableType+(parentVariable.equals(this.designElement.getElementName())?" was received.":" exists within "+parentVariableType+" "+parentVariable+"."))+"\""+ " passfailResult=\"True\">\n"+
                "        <ri-MessageVerify msgType=\""+messageName+"\" elementName=\""+ this.designElement.getElementName()+"\" instanceName=\"tmdd:"+ parentVariable+"\" elementType=\""+this.designElement.getDataConceptType()+"\" functionId=\"Looking for "+parentVariable+"\" />\n"+
                "    </testStep>\n";
        return result;
    }

/**
    @Override
    public void setStartStepNumber(Integer stepNumber) {
 //       this.setPrimaryStepId(stepNumber.toString());
        this.setId(stepNumber.toString());
//        super.setStartStepNumber(stepNumber);
    }
*/
//    @Override
//    public Integer getStepsCount() {
//        return 1;
//    }


private boolean beginsWithVowel (String s)
{
 return ( s.startsWith ("a") || s.startsWith ("e") || s.startsWith ("i") || s.startsWith ("o") || s.startsWith ("u") );
}
}
