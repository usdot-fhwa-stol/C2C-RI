/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures;

/**
 *
 * @author TransCore ITS
 */
public class TMDDProcedureFactory {

    public static TestProcedure makeProcedure(String needID, String dialog, String centerMode) throws Exception {
        OptionalMessageContent.getInstance().clearOptionalList();
        TestProcedure tmddTestProcedure;
        if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
            if (centerMode.equals(TMDDParameters.TMDD_EC_MODE)) {
                tmddTestProcedure = new PUB_EC_Procedure(needID, dialog, centerMode);
            } else if (centerMode.equals(TMDDParameters.TMDD_OC_Mode)){
                tmddTestProcedure = new PUB_OC_Procedure(needID, dialog, centerMode);
            }else {
                throw new Exception("Unknwown Center Mode "+centerMode+" not yet supported.");
            }
        } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
            if (centerMode.equals(TMDDParameters.TMDD_EC_MODE)) {
                tmddTestProcedure = new SUB_EC_Procedure(needID, dialog, centerMode);
            } else if (centerMode.equals(TMDDParameters.TMDD_OC_Mode)){
                tmddTestProcedure = new SUB_OC_Procedure(needID, dialog, centerMode);
            }else {
                throw new Exception("Unknwown Center Mode "+centerMode+" not yet supported.");
            }
        } else if (dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)){
            if (centerMode.equals(TMDDParameters.TMDD_EC_MODE)) {
                tmddTestProcedure = new RR_EC_Procedure(needID, dialog, centerMode);
            } else if (centerMode.equals(TMDDParameters.TMDD_OC_Mode)){
                tmddTestProcedure = new RR_OC_Procedure(needID, dialog, centerMode);
            }else {
                throw new Exception("Unknwown Center Mode "+centerMode+" not yet supported.");
            }
        } else {
            
           throw new Exception("Dialog" + dialog + " is not in named with a recognizable RR, Sub or Pub Suffix format.");

        }
        return tmddTestProcedure;
    }
}
