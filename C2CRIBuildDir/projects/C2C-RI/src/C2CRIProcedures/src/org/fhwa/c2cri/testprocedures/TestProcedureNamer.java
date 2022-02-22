/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testprocedures;

import java.io.File;

/**
 *
 * @author TransCore ITS
 */
public class TestProcedureNamer {
    private String procedureName;
    private String procedureTitle;
    private String procedureID;
    private String procedureDescription;

    public TestProcedureNamer(String needNumber, String dialog, String centerMode){
        String sutCenterMode = (centerMode.equals(TMDDParameters.TMDD_EC_MODE)?TMDDParameters.TMDD_OC_Mode: TMDDParameters.TMDD_EC_MODE);
        procedureID = "TPS-" + needNumber + "-" + dialog + "-" + sutCenterMode;                              
        procedureTitle = "Need " + needNumber + " " + dialog + " for a SUT in " + sutCenterMode + " Mode";

        String needID = NRTM_RTM_Design_Data.getInstance().getNeedID(needNumber);
        String needText = NRTM_RTM_Design_Data.getInstance().getNeedText(needNumber);
        procedureDescription = "This test procedure is called by a test case and is used to verify the SUTs support of the " + dialog + " dialog as an " + sutCenterMode + " using variables provided by the calling test case.  \n"+
          "This procedure supports verification of requirements related to user need "+needID+" ["+needText+"]"+ " and is used for both valid and invalid test cases.";
        procedureName = procedureTitle;
    }

    public String getProcedureDescription() {
        return procedureDescription;
    }

    public String getProcedureID() {
        return procedureID;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public String getProcedureTitle() {
        return procedureTitle;
    }

}
