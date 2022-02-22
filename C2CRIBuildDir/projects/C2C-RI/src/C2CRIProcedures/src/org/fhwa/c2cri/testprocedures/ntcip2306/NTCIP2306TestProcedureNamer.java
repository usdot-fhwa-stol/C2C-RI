/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures.ntcip2306;


/**
 *
 * @author TransCore ITS
 */
public class NTCIP2306TestProcedureNamer {

    private String procedureName;
    private String procedureTitle;
    private String procedureID;
    private String procedureDescription;

    public NTCIP2306TestProcedureNamer(String target, String profile, String centerMode) {
        if (target.equals(NTCIP2306Parameters.NTCIP2306_WSDL_TARGET)) {
            procedureID = "TPS-WSDL";
            procedureTitle = "NTCIP 2306 Web Service Definition Language File Verfication Procedure";

            String needID = "Profile 1, 2 and 3";
            String needText = "SOAP, XML Direct and FTP Profiles";
            procedureDescription = "This test procedure is called by a test case and is used to verify a WSDL files conformance to NTCIP 2306."
                    + "This procedure supports verification of requirements related to user needs 1, 2 and 3 (SOAP, XML Direct and FTP Profiles) and is used for both valid and invalid test cases.";
            procedureName = procedureTitle;

        } else {
            procedureID = "TPS-"+target+"-"+profile+"-"+centerMode;
            procedureTitle = "NTCIP 2306 "+target+" "+profile+" "+centerMode+" Verfication Procedure";
            String needID = "Not yet Specified in Test Procedure Namer";
            String needText = "Not yet Specified in Test Procedure Namer";
            procedureDescription = "This test procedure is called by a test case and is used to verify a SUT "+profile+" conformance to NTCIP 2306."
                    + "This procedure supports verification of requirements related to "+profile+" and is used for both valid and invalid test cases.";
            procedureName = procedureTitle;
        }
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
