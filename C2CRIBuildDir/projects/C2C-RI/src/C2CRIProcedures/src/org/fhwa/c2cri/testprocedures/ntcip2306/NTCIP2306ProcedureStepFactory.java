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
public class NTCIP2306ProcedureStepFactory {
    public static String GENERAL_WSDL_STEP_TYPE = "General WSDL Step Type";
    public static String SPECIFIC_WSDL_STEP_TYPE = "Specific WSDL Step Type";
    public static String SOAP_RR_WSDL_SECTION_STEP_TYPE = "WSDL SOAP RR Section Step Type";
    public static String SOAP_SUBPUB_WSDL_SECTION_STEP_TYPE = "WSDL SOAP Suh Pub Section Step Type";
    public static String HTTP_POST_WSDL_SECTION_STEP_TYPE = "WSDL HTTP Post Section Step Type";
    public static String HTTP_GET_WSDL_SECTION_STEP_TYPE = "WSDL HTTP Get Section Step Type";
    public static String FTP_GET_WSDL_SECTION_STEP_TYPE = "WSDL FTP Get Section Step Type";

    public static TestProcedureStep makeProcedureStep(String stepType, NTCIP2306Specification theSpecification) throws Exception {
        TestProcedureStep ntcip2306TestProcedureStep;

        if (stepType.equals(NTCIP2306ProcedureStepFactory.GENERAL_WSDL_STEP_TYPE)) {
            ntcip2306TestProcedureStep = new GeneralWSDLSectionTestStep(theSpecification);
        } else if (stepType.equals(NTCIP2306ProcedureStepFactory.SPECIFIC_WSDL_STEP_TYPE)){
            ntcip2306TestProcedureStep = new SpecificWSDLSectionTestStep(theSpecification);
        }
         else {
           throw new Exception("STEP TYPE " + stepType + " is not yet supported.");
        }
        return ntcip2306TestProcedureStep;
    }

        public static TestProcedureStep makeProcedureStep(String stepType) throws Exception {
        TestProcedureStep ntcip2306TestProcedureStep;

        if (stepType.equals(NTCIP2306ProcedureStepFactory.SOAP_RR_WSDL_SECTION_STEP_TYPE)) {
            ntcip2306TestProcedureStep = new WSDLSOAPRRSectionTestStep();
        } else if (stepType.equals(NTCIP2306ProcedureStepFactory.SOAP_SUBPUB_WSDL_SECTION_STEP_TYPE)){
            ntcip2306TestProcedureStep = new WSDLSOAPSubPubSectionTestStep();
        } else if (stepType.equals(NTCIP2306ProcedureStepFactory.HTTP_POST_WSDL_SECTION_STEP_TYPE)){
            ntcip2306TestProcedureStep = new WSDLHTTPPostSectionTestStep();
        } else if (stepType.equals(NTCIP2306ProcedureStepFactory.HTTP_GET_WSDL_SECTION_STEP_TYPE)){
            ntcip2306TestProcedureStep = new WSDLHTTPGetSectionTestStep();
        } else if (stepType.equals(NTCIP2306ProcedureStepFactory.FTP_GET_WSDL_SECTION_STEP_TYPE)){
            ntcip2306TestProcedureStep = new WSDLFTPGetSectionTestStep();
        } else {
           throw new Exception("STEP TYPE " + stepType + " is not yet supported.");
        }
        return ntcip2306TestProcedureStep;
    }

/**
    public static TestProcedureStep makeProcedureStep(String stepType, String needID, String dialog, String centerMode) throws Exception {
        TestProcedureStep tmddTestProcedureStep;
        
        if (stepType.equals(TMDDProcedureStepFactory.GENERALVARIABLERECORD_STEP_TYPE)) {
            tmddTestProcedureStep = new GeneralVariableRecordTestStep(needID, dialog, centerMode);
        } else {
           throw new Exception("STEP TYPE " + stepType + " is not yet supported.");
        }
        return tmddTestProcedureStep;
    }
*/
 }
