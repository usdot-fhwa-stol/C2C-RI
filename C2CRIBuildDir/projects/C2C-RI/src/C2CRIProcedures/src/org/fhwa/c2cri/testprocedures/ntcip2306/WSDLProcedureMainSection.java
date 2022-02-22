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
public class WSDLProcedureMainSection extends TestProcedureSection {

    public WSDLProcedureMainSection() {

        Variable wsdlURLVariable = new WSDLURLVariable();
        ArrayList<Variable>variableList = new ArrayList<Variable>();
        variableList.add(wsdlURLVariable);
        this.variables.addAll(variableList);

        try {
           // Create the first subsection
           TestProcedureSection thisSection = NTCIP2306TestProcedureSectionFactory.makeProcedure(
                NTCIP2306TestProcedureSectionFactory.WSDL_GENERAL_SECTION,
                NTCIP2306Parameters.NTCIP2306_WSDL_TARGET, "", "");

           subSections.add(thisSection);
           this.variables.addAll(thisSection.getVariablesList());
           TestProcedureSection soapRRSection = NTCIP2306TestProcedureSectionFactory.makeProcedure(
                NTCIP2306TestProcedureSectionFactory.WSDL_SOAP_RR_SECTION,
                NTCIP2306Parameters.NTCIP2306_WSDL_TARGET, "", "");

//           soapRRSection.setStartStepNumber(this.getNextStepNumber());
           subSections.add(soapRRSection);
           this.variables.addAll(soapRRSection.getVariablesList());

           TestProcedureSection soapSubPubSection = NTCIP2306TestProcedureSectionFactory.makeProcedure(
                NTCIP2306TestProcedureSectionFactory.WSDL_SOAP_SUBPUB_SECTION,
                NTCIP2306Parameters.NTCIP2306_WSDL_TARGET, "", "");

//           soapRRSection.setStartStepNumber(this.getNextStepNumber());
           subSections.add(soapSubPubSection);
           this.variables.addAll(soapSubPubSection.getVariablesList());

           TestProcedureSection httpPostSection = NTCIP2306TestProcedureSectionFactory.makeProcedure(
                NTCIP2306TestProcedureSectionFactory.WSDL_XML_POST_SECTION,
                NTCIP2306Parameters.NTCIP2306_WSDL_TARGET, "", "");

//           soapRRSection.setStartStepNumber(this.getNextStepNumber());
           subSections.add(httpPostSection);
           this.variables.addAll(httpPostSection.getVariablesList());

           TestProcedureSection httpGetSection = NTCIP2306TestProcedureSectionFactory.makeProcedure(
                NTCIP2306TestProcedureSectionFactory.WSDL_XML_GET_SECTION,
                NTCIP2306Parameters.NTCIP2306_WSDL_TARGET, "", "");

//           soapRRSection.setStartStepNumber(this.getNextStepNumber());
           subSections.add(httpGetSection);
           this.variables.addAll(httpGetSection.getVariablesList());

           TestProcedureSection ftpGetSection = NTCIP2306TestProcedureSectionFactory.makeProcedure(
                NTCIP2306TestProcedureSectionFactory.WSDL_FTP_SECTION,
                NTCIP2306Parameters.NTCIP2306_WSDL_TARGET, "", "");

//           soapRRSection.setStartStepNumber(this.getNextStepNumber());
           subSections.add(ftpGetSection);
           this.variables.addAll(ftpGetSection.getVariablesList());

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
        String scriptContent = "<WSDL-session beginSession=\"True\" wsdlFile=\"${WSDLFile}\">\n";
        for (Section thisSection : subSections) {
            //           for (TestProcedureStep thisStep : thisSection.getStepsforDatabase()) {
            //               scriptContent = scriptContent.concat(thisStep.getId() + " - " + thisStep.getDescription() + "\n");
            //           }

            scriptContent = scriptContent.concat(thisSection.getScriptContent()) + "\n";
        }
        scriptContent = scriptContent.concat("</WSDL-session>\n");
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
