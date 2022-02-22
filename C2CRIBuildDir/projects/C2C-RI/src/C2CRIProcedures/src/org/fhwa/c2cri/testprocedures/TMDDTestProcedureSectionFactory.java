/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures;

/**
 *
 * @author TransCore ITS
 */
public class TMDDTestProcedureSectionFactory {

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

    public static TestProcedureSection makeProcedure(String sectionType, String needID, String dialog, String centerMode) throws Exception {
        TestProcedureSection tmddTestProcedureSection;

        if (sectionType.equals(GENERAL_VARIABLE_RECORD_SECTION)) {
                tmddTestProcedureSection = new GeneralVariableRecordProcedureSection(needID, dialog, centerMode);

        } else if (sectionType.equals(GENERAL_REQ_PUB_SUB_MSG_VAR_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(DIALOG_SETUP_SECTION)) {
                tmddTestProcedureSection = new DialogProcedureSection(needID, dialog, centerMode);
        } else if (sectionType.equals(DIALOG_INITIAL_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(RECEIVED_MSG_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(OPTIONAL_MSG_VERIFY_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(VARIABLE_VERIFICATION_SECTION)) {
                tmddTestProcedureSection = new GeneralVariableVerifyProcedureSection(needID, dialog, centerMode);
        } else if (sectionType.equals(RECEIVED_RESPONSE_MSG_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(NORMAL_RECEIVED_SECTION)) {
            throw new Exception("TestProcedureSection:  " + sectionType + " is not yet supported.");
        } else if (sectionType.equals(ERROR_RECEIVED_SECTION)) {
                tmddTestProcedureSection = new GeneralErrorVariableVerifyProcedureSection(needID, dialog, centerMode);
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
        return tmddTestProcedureSection;
    }
}
