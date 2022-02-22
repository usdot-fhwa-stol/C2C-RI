/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddprocedures;

/**
 *
 * @author TransCore ITS
 */
public class ProcedureStep {
    String stepNumber="";
    String stepText="";
    String action="";
    String results="";
    String requirements="";
    String passFail="";
    String procedureName="";
    
    public ProcedureStep(String procedureName){
        this.procedureName = procedureName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPassFail() {
        return passFail;
    }

    public void setPassFail(String passFail) {
        this.passFail = passFail;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(String stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getStepText() {
        return stepText;
    }

    public void setStepText(String stepText) {
        this.stepText = stepText;
    }


}
