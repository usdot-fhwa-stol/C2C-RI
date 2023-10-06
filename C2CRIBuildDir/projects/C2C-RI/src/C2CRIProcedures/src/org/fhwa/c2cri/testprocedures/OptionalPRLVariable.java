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
public class OptionalPRLVariable implements Variable{

private String variableName;
private String needNumber;
private String unID;
private String requirementID;
private String source;
private String description;
private String recordText;
private String verifyText;
private String recordScriptText;
private String verifyScriptText;
private String dataType = "Boolean";
private String validValues="True or False";


    public void OptionalProcedureVariable(){
		// original implementation was empty
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getRecordScriptText() {
        return recordScriptText;
    }

    public void setRecordScriptText(String recordScriptText) {
        this.recordScriptText = recordScriptText;
    }

    public String getRecordText() {
        return recordText;
    }

    public void setRecordText(String recordText) {
        this.recordText = recordText;
    }

    public String getNeedNumber() {
        return needNumber;
    }

    public void setNeedNumber(String needNumber) {
        this.needNumber = needNumber;
    }

    public String getRequirementID() {
        return requirementID;
    }

    public void setRequirementID(String requirementID) {
        this.requirementID = requirementID;
    }

    public String getUnID() {
        return unID;
    }

    public void setUnID(String unID) {
        this.unID = unID;
    }

    public String getRelatedCenterMode() {
        return needNumber;
    }

    public void setRelatedCenterMode(String relatedCenterMode) {
        this.needNumber = relatedCenterMode;
    }

    public String getRelatedDialogType() {
        return unID;
    }

    public void setRelatedDialogType(String relatedDialogType) {
        this.unID = relatedDialogType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getValidCaseDefaultValue() {
        return requirementID;
    }

    public void setValidCaseDefaultValue(String validCaseDefaultValue) {
        this.requirementID = validCaseDefaultValue;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVerifyScriptText() {
        return verifyScriptText;
    }

    public void setVerifyScriptText(String verifyScriptText) {
        this.verifyScriptText = verifyScriptText;
    }

    public String getVerifyText() {
        return verifyText;
    }

    public void setVerifyText(String verifyText) {
        this.verifyText = verifyText;
    }

// Other Variable Interface Methods
    public String getAutomatedPathReference() {
        throw new UnsupportedOperationException("Not applicable for a Procedure Variable.");
    }

    public String getMessageElementComparisonStatement() {
        throw new UnsupportedOperationException("Not applicable for a Procedure Variable.");
    }

    public String getName() {
        return getVariableName();
    }

    public ArrayList<String> getRelatedRequirements() {
        ArrayList<String> blankList = new ArrayList<String>();
        blankList.add(requirementID);
        return blankList;
    }

    public String getSetVariableText() {
        throw new UnsupportedOperationException("Not applicable for a Procedure Variable.");
    }

    public boolean isAutomatedInspection() {
        throw new UnsupportedOperationException("Not applicable for a Procedure Variable.");
    }

    public String getDataType() {
        return this.dataType;
    }

    public String getValidValues() {
        return this.validValues;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setValidValues(String validValues) {
        this.validValues = validValues;
    }
    
    
    public boolean isUserEditable() {
        return true;
    }


}
