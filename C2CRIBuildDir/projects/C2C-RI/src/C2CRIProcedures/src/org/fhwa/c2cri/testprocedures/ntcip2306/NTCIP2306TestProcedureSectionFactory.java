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
public class NTCIP2306TestProcedureSectionFactory {

    public static String WSDL_MAIN_SECTION="Top Level WSDL Section";
    public static String WSDL_GENERAL_SECTION="General WSDL Section";
    public static String WSDL_SOAP_RR_SECTION="WSDL SOAP RR Section";
    public static String WSDL_SOAP_SUBPUB_SECTION="WSDL SOAP SUBPUB Section";
    public static String WSDL_XML_POST_SECTION="WSDL XML Post Section";
    public static String WSDL_XML_GET_SECTION="WSDL XML Get Section";
    public static String WSDL_FTP_SECTION="WSDL FTP Section";
    public static String GENERAL_VARIABLE_RECORD_SECTION = "General Variable Record Section";
    public static String GENERAL_REQ_PUB_SUB_MSG_VAR_SECTION = "Request/Pub/Sub Msg (s) Variable Record Setup Section";
    public static String DIALOG_SETUP_SECTION = "Dialog Setup Section";
    public static String DIALOG_INITIAL_SECTION = "Dialog Initial Section";
    public static String RECEIVED_MSG_SECTION = "Received Msg Section";
    public static String OPTIONAL_MSG_VERIFY_SECTION = "Optional Msg Verification";
    public static String VARIABLE_VERIFICATION_SECTION = "Variable Verification Section";
    public static String RECEIVED_RESPONSE_MSG_SECTION = "Received Response Msg Section";
    public static String NORMAL_RECEIVED_SECTION = "Normal Received Section";
    public static String ERROR_RECEIVED_SECTION = "Error Received Section";
    public static String ERROR_CODE_VERIFY_SECTION = "Error Code Verification";
    public static String DIALOG_COMPLETE_SECTION = "Dialog Complete Section (if Necessary)";
    public static String NORMAL_RESPONSE_MSG_SECTION = "Normal Response Msg Section";
    public static String RESPONSE_MSG_SELECTION_SECTION = "Response Msg Selection/Generation";
    public static String ERROR_MSG_RESPONSE_SECTION = "Error Msg Response Section";
    public static String ERROR_CODE_CALCULATE_SECTION = "Error Code Calculation/Set";
    public static String RESPOND_SECTION = "Respond Section";

    public static TestProcedureSection makeProcedure(String sectionType, String target, String profile, String centerMode) throws Exception {
        TestProcedureSection ntcip2306TestProcedureSection;

        if (sectionType.equals(WSDL_GENERAL_SECTION)) {
                ntcip2306TestProcedureSection = new WSDLGeneralSpecProcedureSection();
        } else if (sectionType.equals(WSDL_MAIN_SECTION)) {
                ntcip2306TestProcedureSection = new WSDLProcedureMainSection();
        } else if (sectionType.equals(WSDL_SOAP_RR_SECTION)) {
                ntcip2306TestProcedureSection = new WSDLSOAPRRProcedureSection();
        } else if (sectionType.equals(WSDL_SOAP_SUBPUB_SECTION)) {
                ntcip2306TestProcedureSection = new WSDLSOAPSubPubProcedureSection();
        } else if (sectionType.equals(WSDL_XML_POST_SECTION)) {
                ntcip2306TestProcedureSection = new WSDLHTTPPostProcedureSection();
        } else if (sectionType.equals(WSDL_XML_GET_SECTION)) {
                ntcip2306TestProcedureSection = new WSDLHTTPGetProcedureSection();
        } else if (sectionType.equals(WSDL_FTP_SECTION)) {
                ntcip2306TestProcedureSection = new WSDLFTPGetProcedureSection();
        } else if (sectionType.equals(DIALOG_SETUP_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(DIALOG_INITIAL_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(RECEIVED_MSG_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(OPTIONAL_MSG_VERIFY_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(VARIABLE_VERIFICATION_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(RECEIVED_RESPONSE_MSG_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(NORMAL_RECEIVED_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(ERROR_RECEIVED_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(ERROR_CODE_VERIFY_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(DIALOG_COMPLETE_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(NORMAL_RESPONSE_MSG_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(RESPONSE_MSG_SELECTION_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(ERROR_MSG_RESPONSE_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(ERROR_CODE_CALCULATE_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(RESPOND_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        }
        return ntcip2306TestProcedureSection;
    }
}
