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
public class RequiredValueVerifyTestSubStep extends TestProcedureStep{
    private MessageDetailDesignElement designElement;
    private String requirementID;
    private boolean relatedToPublication;
    private String requiredValue;

    public RequiredValueVerifyTestSubStep(MessageDetailDesignElement theDesignElement, Integer subStepNumber, String requirementID, boolean relatedToPublication, String requiredValue){

        this.setIsSubStep(true);
//        this.setId(Integer.toString(subStepNumber));
        this.designElement = theDesignElement;
        this.requirementID = requirementID;
        this.relatedToPublication = relatedToPublication;
        this.requiredValue = requiredValue;
        String parentVariable = "";
        if (this.designElement.getDataConceptType().equalsIgnoreCase("Message")){
            parentVariable = this.designElement.getElementName();
        } else {
            parentVariable = this.designElement.getFullParentVariableName();
        }
        this.setDescription("VERIFY that data element "+ this.designElement.getElementName()+" is set to " + this.requiredValue+".");
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
        
        String messageName = "";
        if (parentInstance.contains("RequestMsg")){
            messageName = "REQUEST";
        } else {
            // The initial message of a publication is a REQUEST, but it does not include RequestMsg in its title.
            if (parentInstance.contains("errorReportMsg")){  // Always a response message
                messageName = "RESPONSE";
            } else if (this.relatedToPublication){
                messageName = "REQUEST";
            } else {
                messageName = "RESPONSE";
            }
        }
        String parentVariable = "";
        if (this.designElement.getDataConceptType().equalsIgnoreCase("Message")){
            parentVariable = this.designElement.getElementName();
        } else {
            parentVariable = this.designElement.getFullParentVariableName();
        }
        
        result = "   <testStep functionId= \"Step "+this.getId()+" VERIFY that data element "+ this.designElement.getElementName()+" is set to "+this.requiredValue+".\""+ " passfailResult=\"True\">\n"+
                "        <ri-MessageVerify msgType=\""+messageName+"\" elementName=\""+ this.designElement.getElementName()+"\" instanceName=\"tmdd:"+ parentVariable+"\" instanceValue=\""+ this.requiredValue+"\" elementType=\""+this.designElement.getDataConceptType()+"\" functionId=\"Looking for "+parentVariable+"\" />\n"+
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



}
