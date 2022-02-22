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
public class ProcedureVariable implements Variable{

private String variableName;
private String relatedCenterMode;
private String relatedDialogType;
private String source;
private String description;
private String validCaseDefaultValue;
private String invalidCaseDefaultValue;
private String recordText;
private String verifyText;
private String recordScriptText;
private String verifyScriptText;
private String dataType;
private String validValues;
private boolean userEditable;

    public void ProcedureVariable(){
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

    public String getInvalidCaseDefaultValue() {
        return invalidCaseDefaultValue;
    }

    public void setInvalidCaseDefaultValue(String invalidCaseDefaultValue) {
        this.invalidCaseDefaultValue = invalidCaseDefaultValue;
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

    public String getRelatedCenterMode() {
        return relatedCenterMode;
    }

    public void setRelatedCenterMode(String relatedCenterMode) {
        this.relatedCenterMode = relatedCenterMode;
    }

    public String getRelatedDialogType() {
        return relatedDialogType;
    }

    public void setRelatedDialogType(String relatedDialogType) {
        this.relatedDialogType = relatedDialogType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getValidCaseDefaultValue() {
        return validCaseDefaultValue;
    }

    public void setValidCaseDefaultValue(String validCaseDefaultValue) {
        this.validCaseDefaultValue = validCaseDefaultValue;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setUserEditable(boolean userEditable) {
        this.userEditable = userEditable;
    }

    public void setValidValues(String validValues) {
        this.validValues = validValues;
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

    public boolean isUserEditable() {
        return this.userEditable;
    }

}
