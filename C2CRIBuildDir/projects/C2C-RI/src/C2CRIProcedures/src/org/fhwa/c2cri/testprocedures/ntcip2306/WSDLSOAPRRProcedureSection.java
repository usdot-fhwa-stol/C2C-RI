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
public class WSDLSOAPRRProcedureSection extends TestProcedureSection {

    public WSDLSOAPRRProcedureSection() {
        try {
        Variable wsdlNRTMVariable = new WSDLNRTMVariable("NTCIP2306_N1R1_WSDL_Request_Response_Flag", "From the NTCIP 2306 NRTM (1.a)", "NRTM Flag for the SOAP RR WSDL");
        ArrayList<Variable>variableList = new ArrayList<Variable>();
        variableList.add(wsdlNRTMVariable);
        this.variables.addAll(variableList);

            ArrayList<NTCIP2306Specification> wsdlSpecificationList;
            wsdlSpecificationList = NTCIP2306Specifications.getInstance().getWSDLSpecifications();

//            for (NTCIP2306Specification thisSpecification : wsdlSpecificationList) {
//                if (thisSpecification.getSection().startsWith("7.1")){
                   TestProcedureStep newStep = NTCIP2306ProcedureStepFactory.makeProcedureStep(NTCIP2306ProcedureStepFactory.SOAP_RR_WSDL_SECTION_STEP_TYPE);
   //                newStep.setPrimaryStepId(getNextStepNumber().toString());
                   newStep.setStartStepNumber(getNextStepNumber());
                   subSections.add(newStep);
//                }
//            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setStartStepNumber(Integer stepNumber) {
           // Trigger a renumbering of each of the underlying test steps.
           Integer currentStep = stepNumber;
           for (Section thisSubsection : subSections){
               thisSubsection.setStartStepNumber(currentStep);
               currentStep = currentStep + thisSubsection.getStepsCount();
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
