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
public interface Variable {

    public boolean isAutomatedInspection();
    public String getAutomatedPathReference();
    public String getMessageElementComparisonStatement();
    public String getName();
    public String getDescription();
    public String getSource();
    public String getRecordScriptText();
    public String getVerifyScriptText();
    public String getVerifyText();
    public String getRecordText();
    public String getSetVariableText();
    public ArrayList<String> getRelatedRequirements();
    public String getDataType();
    public String getValidValues();
    public boolean isUserEditable();
}
