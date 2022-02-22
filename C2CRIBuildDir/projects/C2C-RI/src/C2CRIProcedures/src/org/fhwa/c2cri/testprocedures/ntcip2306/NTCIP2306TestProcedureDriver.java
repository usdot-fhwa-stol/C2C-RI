/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures.ntcip2306;

import org.fhwa.c2cri.testprocedures.*;
import java.util.ArrayList;
import java.util.List;
import org.fhwa.c2cri.testcases.TMDDTestCaseFactory;
import org.fhwa.c2cri.testcases.TestCase;
import tmddv3verification.Need;
import tmddv3verification.NeedHandler;

/**
 *
 * @author TransCore ITS
 */
public class NTCIP2306TestProcedureDriver {

    public static void main(String[] args) {


        try {
            // Initialize the NTCIP 2306 NRTM - RTM - Design Data up front
            NTCIP2306Specifications theDesign = NTCIP2306Specifications.getInstance();

            //WSDL Test Procedure  -- for this procedure the profile and mode don't matter
            TestProcedure firstProcedure = NTCIP2306ProcedureFactory.makeProcedure(NTCIP2306Parameters.NTCIP2306_WSDL_TARGET,"", "");
            firstProcedure.toScriptFile("c:\\temp", firstProcedure.getProcedureID() + ".xml");
            firstProcedure.toDatabase();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
        }

    }
}
