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
public class OptionalContentVerifiedVariable implements Variable{

private String variableName="ContentVerified";
private String source="Local";
private String description="";
private String recordText="";
private String verifyText="";
private String recordScriptText="";
private String verifyScriptText="";


    public void OptionalContentVerifiedVariable(){
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
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


    public String getVerifyScriptText() {
        return verifyScriptText;
    }


    public String getVerifyText() {
        return verifyText;
    }

// Other Variable Interface Methods
    public String getAutomatedPathReference() {
        throw new UnsupportedOperationException("Not applicable for a Procedure Variable.");
    }

    public String getMessageElementComparisonStatement() {
        throw new UnsupportedOperationException("Not applicable for a Procedure Variable.");
    }

    public String getName() {
        return variableName;
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
        return "Boolean";
    }

    public String getValidValues() {
        return "True or False";
    }

    public boolean isUserEditable() {
        return false;
    }

    public String getSource() {
        return source;
    }


}
