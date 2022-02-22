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
public class WSDLHTTPGetSectionTestStep extends TestProcedureStep {

    private NTCIP2306Specification relatedSpecification;

    public WSDLHTTPGetSectionTestStep() {
        this.setDescription("IF NTCIP2306_N2R6_WSDL_Request_Only_HTTP_GET_Flag is equal to True CONTINUE; OTHERWISE skip the following substeps.");
        this.setResults("NA");

        try{
            ArrayList<NTCIP2306Specification> wsdlSpecificationList;
            wsdlSpecificationList = NTCIP2306Specifications.getInstance().getWSDLSpecifications();

            ArrayList<Section> subStepList = new ArrayList<Section>();

            Integer subStepNumber = 1;
            for (NTCIP2306Specification thisSpecification : wsdlSpecificationList) {
                if (thisSpecification.getSection().startsWith("8.1")){
                   TestProcedureStep newStep = NTCIP2306ProcedureStepFactory.makeProcedureStep(NTCIP2306ProcedureStepFactory.SPECIFIC_WSDL_STEP_TYPE,
                           thisSpecification);
                   newStep.setIsSubStep(true);
//                   newStep.setPrimaryStepId(getId());
                   newStep.setStartStepNumber(subStepNumber);
                   subStepList.add(newStep);
                   subStepNumber++;
                }
            }
//            this.setSubSteps(subStepList);

            for (NTCIP2306Specification thisSpecification : wsdlSpecificationList) {
                if (thisSpecification.getSection().startsWith("8.3")){
                   TestProcedureStep newStep = NTCIP2306ProcedureStepFactory.makeProcedureStep(NTCIP2306ProcedureStepFactory.SPECIFIC_WSDL_STEP_TYPE,
                           thisSpecification);
                   newStep.setIsSubStep(true);
//                   newStep.setPrimaryStepId(getId());
                   newStep.setStartStepNumber(subStepNumber);
                   subStepList.add(newStep);
                   subStepNumber++;
                }
            }
            this.setSubSteps(subStepList);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    @Override
    public void setStartStepNumber(Integer stepNumber) {
        System.out.println("WSDLHTTPGetSectionTestStep:  Step Number "+stepNumber);
           // Trigger a renumbering of each of the underlying test steps.
        this.setId(stepNumber.toString());
        for (Section thisSubsection : getSubSteps()){
               ((TestProcedureStep)thisSubsection).setPrimaryStepId(stepNumber.toString());
               System.out.println("WSDLHTTPGetSectionTestStep:  Set Primary Step Number to "+stepNumber+" for step "+((TestProcedureStep)thisSubsection).getId());
         }
//        super.setStartStepNumber(stepNumber);
    }

    public ArrayList<TestProcedureStep> getStepsforDatabase() {
        ArrayList<TestProcedureStep> theList = new ArrayList<TestProcedureStep>();
//        String result;
//        result = relatedSpecification.getTestSteps();

//        theList.add(this);
        theList.add(this);

        for (Section section : this.getSubSteps()){
            theList.addAll(((TestProcedureStep) section).getStepsforDatabase());
        }
        return theList;
    }

    public String getScriptContent() {
        String result = "";

        String subSteps = "";
        for (Section section : this.getSubSteps()){
            subSteps = subSteps.concat(((TestProcedureStep) section).getScriptContent()+"\n");
        }
        result = "<testStep functionId=\"Step "+this.getId()+" IF NTCIP2306_N2R6_WSDL_Request_Only_HTTP_GET_Flag is equal to True CONTINUE; OTHERWISE skip the following substeps.\"   passfailResult=\"False\" />\n<jl:if test=\"${NTCIP2306_N2R6_WSDL_Request_Only_HTTP_GET_Flag}\" >\n"+subSteps+"</jl:if>\n";
        return result;
    }
}
