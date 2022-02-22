/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures;


/**
 *
 * @author TransCore ITS
 */
public class SUB_EC_Procedure extends AbstractTMDDProcedure {

    public SUB_EC_Procedure(String needID, String dialog, String centerMode) {
        initializeSummaryInformation(needID, dialog, centerMode);
        String sutCenterMode = centerMode.equals(TMDDParameters.TMDD_EC_MODE)?TMDDParameters.TMDD_OC_Mode:TMDDParameters.TMDD_EC_MODE;

        try{
            // Create the first subsection
           TestProcedureSection thisSection = TMDDTestProcedureSectionFactory.makeProcedure(
                TMDDTestProcedureSectionFactory.GENERAL_VARIABLE_RECORD_SECTION,
                needID, dialog, sutCenterMode);

           subSections.add(thisSection);

           TestProcedureSection thisDialogSection = TMDDTestProcedureSectionFactory.makeProcedure(
                TMDDTestProcedureSectionFactory.DIALOG_SETUP_SECTION,
                needID, dialog, centerMode);
           subSections.add(thisDialogSection);

/**
           TestProcedureSection thisVerifySection = TMDDTestProcedureSectionFactory.makeProcedure(
                TMDDTestProcedureSectionFactory.VARIABLE_VERIFICATION_SECTION,
                needID, dialog, centerMode);

           subSections.add(thisVerifySection);
*/
           // Trigger a renumbering of each of the underlying test steps.
           Integer currentStep = 1;
           for (Section thisSubsection : subSections){
               thisSubsection.setStartStepNumber(currentStep);
               currentStep = currentStep + thisSubsection.getStepsCount();
               thisSubsection.setParentStepPath(currentStep.toString());
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
