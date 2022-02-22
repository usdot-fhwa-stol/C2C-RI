/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures;

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
public class TMDDTestProcedureDriver {

    public static void main(String[] args) {


        try {
            // Initialize the NRTM - RTM - Design Data up front
            NRTM_RTM_Design_Data theDesign = NRTM_RTM_Design_Data.getInstance();

            Integer needNumber = 1;
            for (Need thisNeed : theDesign.getNeedsList()) {
                if (((needNumber == 101)||(needNumber>3))&&(needNumber==101)) {

                    NeedHandler thisHandler = new NeedHandler(thisNeed);
                    List<String> dialogList = thisHandler.getNeedRelatedDialogs();

                    List<String> trimmedDialogList = new ArrayList<String>();
                    for (String theDialog : dialogList) {

                        //TMDD WSDL Dialogs start with an uppercase "D", but the NRTM/RTM
                        // likely start with a lowercase "d".  Make an adjustment here and see if it carries through.
//                        if (theDialog.startsWith("d")){
//                            theDialog = theDialog.replaceFirst("d", "D");
//                        }
                        if (!trimmedDialogList.contains(theDialog)) {
                            trimmedDialogList.add(theDialog);
                        } else {
                            System.err.println("!!! Make sure Test Cases & Variables account for duplicate " + theDialog + "in need " + thisNeed.getUnID() + "(" + needNumber + ")");
                        }
                    }

                    for (String thisDialogName : trimmedDialogList) {
                        if (!thisDialogName.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                            // Try EC Mode
                            try {
                                TestProcedure firstProcedure = TMDDProcedureFactory.makeProcedure(needNumber.toString(), thisDialogName, TMDDParameters.TMDD_EC_MODE);
                                firstProcedure.toScriptFile("C:\\TMDDv303c\\scripts", firstProcedure.getProcedureID() + ".xml");
                                firstProcedure.toDatabase();
                                for (TestCase thisCase : TMDDTestCaseFactory.makeTestCases(needNumber.toString(), thisDialogName, TMDDParameters.TMDD_EC_MODE, firstProcedure)) {
                                    if (!(thisDialogName.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX) && thisCase.getTestCaseID().contains("InValid-8"))) {
                                        thisCase.toDatabase();
                                        thisCase.toTestCaseMatrix();
                                        thisCase.toTestCaseFile("C:\\TMDDv303c\\data", thisCase.getTestCaseID() + ".data");
                                    } else {
                                        System.out.println("Skipping " + thisCase.getTestCaseID());
                                    }

                                }

                            } catch (Exception ex1) {
                                System.err.println(ex1.getMessage());

                            }

                            // Try OC Mode
                            try {
                                TestProcedure firstProcedure = TMDDProcedureFactory.makeProcedure(needNumber.toString(), thisDialogName, TMDDParameters.TMDD_OC_Mode);
                                firstProcedure.toScriptFile("C:\\TMDDv303c\\scripts", firstProcedure.getProcedureID() + ".xml");
                                firstProcedure.toDatabase();
//                        firstProcedure.toScriptFile("c:\\temp", firstProcedure.getProcedureID() + ".xml");

                                for (TestCase thisCase : TMDDTestCaseFactory.makeTestCases(needNumber.toString(), thisDialogName, TMDDParameters.TMDD_OC_Mode, firstProcedure)) {
                                    if (!(thisDialogName.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX) && thisCase.getTestCaseID().contains("InValid-8"))) {
                                        thisCase.toDatabase();
                                        thisCase.toTestCaseMatrix();
                                        thisCase.toTestCaseFile("C:\\TMDDv303c\\data", thisCase.getTestCaseID() + ".data");
                                    } else {
                                        System.out.println("Skipping " + thisCase.getTestCaseID());
                                    }
                                }

                            } catch (Exception ex2) {
                                System.err.println(ex2.getMessage());

                            }
                        }
                    }
                }
                needNumber++;
//                if (needNumber == 14) break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
        }

    }
}
