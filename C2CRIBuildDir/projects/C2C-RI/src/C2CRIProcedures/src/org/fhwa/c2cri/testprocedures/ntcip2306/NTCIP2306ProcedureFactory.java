/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures.ntcip2306;

import org.fhwa.c2cri.testprocedures.*;

/**
 *
 * @author TransCore ITS
 */
public class NTCIP2306ProcedureFactory {

    public static TestProcedure makeProcedure(String target, String profile, String centerMode) throws Exception {
        TestProcedure ntcip2306TestProcedure;
        if (target.equals(NTCIP2306Parameters.NTCIP2306_WSDL_TARGET)) {
                ntcip2306TestProcedure = new WSDL_Procedure(target, profile, centerMode);
        } else {
           throw new Exception("Target " + target + " with Profile "+profile+" and CenterMode "+centerMode+" is not yet supported.");

        }
        return ntcip2306TestProcedure;
    }
}
