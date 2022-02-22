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
public class WSDLGeneralSpecProcedureSection extends TestProcedureSection {

    public WSDLGeneralSpecProcedureSection() {
        try {

            ArrayList<NTCIP2306Specification> wsdlSpecificationList;
            wsdlSpecificationList = NTCIP2306Specifications.getInstance().getWSDLSpecifications();
            System.out.println("Initial Subsections Count = "+ subSections.size());
            for (NTCIP2306Specification thisSpecification : wsdlSpecificationList) {
                if ((thisSpecification.getProfile() != null) && thisSpecification.getProfile().equals(NTCIP2306Parameters.NTCIP2306_GENERAL_PROFILE)) {
                    TestProcedureStep newStep = NTCIP2306ProcedureStepFactory.makeProcedureStep(NTCIP2306ProcedureStepFactory.GENERAL_WSDL_STEP_TYPE,
                            thisSpecification);
                    newStep.setStartStepNumber(getNextStepNumber());
                    subSections.add(newStep);
                }
            }
            System.out.println("Final Subsections Count = "+ subSections.size());


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getScriptContent() {
        String scriptContent = "";
        for (Section thisSection : subSections) {
            //           for (TestProcedureStep thisStep : thisSection.getStepsforDatabase()) {
            //               scriptContent = scriptContent.concat(thisStep.getId() + " - " + thisStep.getDescription() + "\n");
            //           }

            scriptContent = scriptContent.concat(thisSection.getScriptContent()) + "\n\n";
        }
        return scriptContent;
    }

    @Override
    public ArrayList<TestProcedureStep> getStepsforDatabase() {
        ArrayList<TestProcedureStep> thisList = new ArrayList<TestProcedureStep>();

        for (Section thisSection : subSections) {
            for (TestProcedureStep thisStep : thisSection.getStepsforDatabase()) {
                thisList.add(thisStep);
            }
        }


        return thisList;
    }
}
