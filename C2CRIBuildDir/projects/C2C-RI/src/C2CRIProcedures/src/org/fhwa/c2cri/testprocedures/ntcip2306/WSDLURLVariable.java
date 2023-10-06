/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testprocedures.ntcip2306;

import org.fhwa.c2cri.testprocedures.*;
import java.util.ArrayList;

/**
 *
 * @author TransCore ITS
 */
public class WSDLURLVariable implements Variable{

private String variableName="WSDLFile";
private String source="Defined by the calling test case";
private String description="The URL for the WSDL file to be tested.";
private String recordText="";
private String verifyText="";
private String recordScriptText="";
private String verifyScriptText="";

    public void WSDLURLVariable(){
		// original implementation was empty
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


    public String getRecordText() {
        return recordText;
    }


    public String getSource() {
        return source;
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
        return "String";
    }

    public String getValidValues() {
        return "NA";
    }

    public boolean isUserEditable() {
        return false;
    }

}
