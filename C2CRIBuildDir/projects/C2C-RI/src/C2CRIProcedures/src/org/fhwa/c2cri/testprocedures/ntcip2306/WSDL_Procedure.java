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
public class WSDL_Procedure extends AbstractNTCIP2306Procedure {

    public WSDL_Procedure(String target, String profile, String centerMode) {
        initializeSummaryInformation(target, profile, centerMode);
        

        try{
           // Create the primary subsection
           TestProcedureSection thisSection = NTCIP2306TestProcedureSectionFactory.makeProcedure(
                NTCIP2306TestProcedureSectionFactory.WSDL_MAIN_SECTION,
                NTCIP2306Parameters.NTCIP2306_WSDL_TARGET, "", "");

           subSections.add(thisSection);
/**
           TestProcedureSection thisVerifySection = TMDDTestProcedureSectionFactory.makeProcedure(
                TMDDTestProcedureSectionFactory.VARIABLE_VERIFICATION_SECTION,
                needID, dialog, centerMode);

           subSections.add(thisVerifySection);
*/
           // Trigger a renumbering of each of the underlying test steps.
           System.out.println("WSDL_Procedure: Renumbering the SubSections ");
           Integer currentStep = 1;
           for (Section thisSubsection : subSections){
               System.out.println("WSDL_Procedure: Renumbering Subsection " + thisSubsection.getClass().toString());
               thisSubsection.setStartStepNumber(currentStep);
               currentStep = currentStep + thisSubsection.getStepsCount();
           }

           // Set the test procedure variables from all of the section variables
           for (Section theSubSection : subSections){
               for (Variable thisVariable : theSubSection.getVariablesList()){
                   if (!this.procedureVariables.contains(thisVariable)){
                       this.procedureVariables.add(thisVariable);
                   }
               }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
