/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testcases.ntcip2306;

import org.fhwa.c2cri.testcases.*;
import org.fhwa.c2cri.testprocedures.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author TransCore ITS
 */
public abstract class AbstractNTCIP2306TestCase implements TestCase {

    protected ArrayList<String> relatedRequirements = new ArrayList<String>();
    protected ArrayList<Variable> procedureVariables = new ArrayList<Variable>();
    private List<TestCaseInput> testCaseInputs = new ArrayList<TestCaseInput>();
    private List<TestCaseOutput> testCaseOutputs = new ArrayList<TestCaseOutput>();
    private String testCaseID;
    private String testCaseTitle;
    private String testCaseDescription;
    private String testCaseProductionDate;
    private String needID;
    private String mode;
    private String dialog;
    private String procedureName;
    private String testItemType;
    private String precondition = "";

    protected void initializeTestCaseInformation(String testCaseTitle, String testCaseDescription) {
//        TestProcedureNamer testProcedureNameMaker = new TestProcedureNamer(needID, dialog, centerMode);
//        procedureName = testProcedureNameMaker.getProcedureID();
        testCaseID = testCaseTitle;
        this.testCaseTitle = testCaseTitle;
        this.testCaseDescription = testCaseDescription;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        testCaseProductionDate = dateFormat.format(date);
//        this.needID = needID;
//        String sutCenterMode = (centerMode.equals(TMDDParameters.TMDD_EC_MODE) ? TMDDParameters.TMDD_OC_Mode : TMDDParameters.TMDD_EC_MODE);
//        relatedRequirements = NRTM_RTM_Design_Data.getInstance().makeTestCaseRequirementsList(needID, sutCenterMode, dialog, valid);
//        precondition = NRTM_RTM_Design_Data.getInstance().getDialogPrecondition(needID, sutCenterMode, dialog);
//        mode = centerMode;
//        this.dialog = dialog;
    }

    public String getTestItemType() {
        return testItemType;
    }

    public void setTestItemType(String testItemType) {
        this.testItemType = testItemType;
    }

    public void setTestCaseInputs(List<TestCaseInput> testCaseInputs) {
        this.testCaseInputs = testCaseInputs;
    }

    public void setTestCaseOutputs(List<TestCaseOutput> testCaseOutputs) {
        this.testCaseOutputs = testCaseOutputs;
    }

    public String getNeedID() {
        return needID;
    }

    public ArrayList<Variable> getProcedureVariables() {
        return procedureVariables;
    }

    public ArrayList<String> getRelatedRequirements() {
        return relatedRequirements;
    }

    public String getTestCaseProductionDate() {
        return testCaseProductionDate;
    }

    public String getTestCaseDescription() {
        return this.testCaseDescription;
    }

    public String getTestCaseID() {
        return this.testCaseID;
    }

    public String getTestCaseTitle() {
        return this.testCaseTitle;
    }

    public ArrayList<Variable> getTestCaseVariables() {
        return this.getProcedureVariables();
    }

    public String getExactFileName(File f) {
        String returnVal;
        try {
            returnVal = f.getCanonicalPath();
            returnVal =
                    returnVal.substring(returnVal.lastIndexOf(File.separator) + 1);
        } catch (IOException e) {
            returnVal = "";
        }
        return returnVal;
    }

    public boolean fileExistsCaseSensative(File f) {
        return getExactFileName(f).equals(f.toString());
    }

    public final void toTestCaseFile(String path, String fileName) {
        File newFile = new File(path + File.separatorChar + fileName);
        if (fileExistsCaseSensative(newFile)) {

            //          newFile.delete();
            newFile = new File(path + File.separatorChar + fileName);

        }
        String header = " \n"
                + "#<!--   \n"
                + "#       Title:  " + this.testCaseID + "\n"
                + "#       Description:  " + this.testCaseDescription.replace("\n", " ") + "\n"
                + "#       Related Procedure:  " + this.procedureName + "\n"
                + "#       Version/Date: " + this.testCaseProductionDate + "\n"
                + "#-->\n\n";


        String iteration = "#ITERATION NAME = One\n";
        String groupName = "#GROUP NAME = General\n";
        String type = "#PARAMETER TYPE = String\n";
        String editable = "#EDITABLE = true\n";

        try (Writer out = new OutputStreamWriter(new FileOutputStream(newFile))){
            //      Writer out = new OutputStreamWriter(new FileOutputStream(path + File.separatorChar + fileName), "UTF-8");
            System.out.println("Writing File: " + newFile.getName());
            out.write(header);

            if (fileName.contains("WSDL")) {
                out.write(iteration);
                out.write(groupName);
                out.write(type);
                out.write(editable);
                out.write("#DOCUMENTATION = This variable represents the WSDL File to be verified.\n");
                out.write("WSDLFile = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = boolean\n");
                out.write(editable);
                out.write("#DOCUMENTATION = This variable represents whether the Test Case is expected to pass.\n");
                out.write("CheckOutcomePassed = true\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = boolean\n");
                out.write(editable);
                out.write("#DOCUMENTATION = This variable represents whether the Test Case is expected to fail.\n");
                out.write("#CheckOutcomeFailed = true\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = String\n");
                out.write(editable);
                out.write("#DOCUMENTATION = This variable represents the text of the test step where a failure is expected.\n");
                out.write("#FailedTestStepText = TBD\n");
                out.write("\n");

            } else if (fileName.contains("SHRR")) {
                out.write(iteration);
                out.write(groupName);
                out.write(type);
                out.write(editable);
                out.write("#DOCUMENTATION = This variable represents the WSDL File to be verified.\n");
                out.write("WSDLFile = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = String\n");
                out.write(editable);
                out.write("#DOCUMENTATION = The Service defined in the WSDL that will be used during the test.\n");
                out.write("ServiceName = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = String\n");
                out.write(editable);
                out.write("#DOCUMENTATION = The Port defined in the WSDL that is related to the selected Service Name and will be used during the test.\n");
                out.write("PortName = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = String\n");
                out.write(editable);
                out.write("#DOCUMENTATION = The Operation defined in the WSDL that is related to the selected Service Name and will be used during the test.\n");
                out.write("OperationName = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = boolean\n");
                out.write(editable);
                out.write("#DOCUMENTATION = This variable represents whether the Test Case is expected to pass.\n");
                out.write("CheckOutcomePassed = true\n");
                out.write("\n");

                if (fileName.contains("-OC-")) {
                    out.write("#PARAMETER TYPE = String\n");
                    out.write(editable);
                    out.write("#DOCUMENTATION = Enter the XML message to be returned during this test. \n");
                    out.write("ResponseMessage = #USERDEFINED#\n");
                    out.write("\n");

                    out.write("#PARAMETER TYPE = String\n");
                    out.write(editable);
                    out.write("#DOCUMENTATION = Enter the response code that should be returned during this test. \n");
                    out.write("ResponseCode = #USERDEFINED#\n");
                    out.write("\n");

                    out.write("#PARAMETER TYPE = boolean\n");
                    out.write(editable);
                    out.write("#DOCUMENTATION = Enter (True or False) whether an erroneous message request is expected during this test. \n");
                    out.write("MessageErrorExpected = #USERDEFINED#\n");
                    out.write("\n");

                } else {

                    out.write("#PARAMETER TYPE = String\n");
                    out.write(editable);
                    out.write("#DOCUMENTATION = Enter the Request-URI to be used during this test. \n");
                    out.write("Request_URI = #USERDEFINED#\n");
                    out.write("\n");

                    out.write("#PARAMETER TYPE = boolean\n");
                    out.write(editable);
                    out.write("#DOCUMENTATION = Enter (True or False) whether an erroneous transport exchange is expected during this test. \n");
                    out.write("TransportErrorExpected = #USERDEFINED#\n");
                    out.write("\n");

                    out.write("#PARAMETER TYPE = String\n");
                    out.write(editable);
                    out.write("#DOCUMENTATION = Enter the type of Transport Error (if applicable) that is expected during this test. \n");
                    out.write("TransportErrorTypeExpected = #USERDEFINED#\n");
                    out.write("\n");

                    out.write("#PARAMETER TYPE = boolean\n");
                    out.write(editable);
                    out.write("#DOCUMENTATION = Enter (True or False) whether an erroneous message request is expected during this test. \n");
                    out.write("MessageErrorExpected = #USERDEFINED#\n");
                    out.write("\n");

                    out.write("#PARAMETER TYPE = String\n");
                    out.write(editable);
                    out.write("#DOCUMENTATION = Enter the XML message to be sent as a request during this test. \n");
                    out.write("RequestMessage = #USERDEFINED#\n");
                    out.write("\n");

                    out.write("#PARAMETER TYPE = boolean\n");
                    out.write(editable);
                    out.write("#DOCUMENTATION = Enter (True or False) whether an encoding error is expected during this test. \n");
                    out.write("EncodingErrorExpected = #USERDEFINED#\n");
                    out.write("\n");

                    out.write("#PARAMETER TYPE = String\n");
                    out.write(editable);
                    out.write("#DOCUMENTATION = Enter the type of message error (if applicable) is expected during this test. \n");
                    out.write("MessageErrorTypeExpected = #USERDEFINED#\n");
                    out.write("\n");


                }

            } else if (fileName.contains("SHSP")) {
                out.write(iteration);
                out.write(groupName);
                out.write(type);
                out.write(editable);
                out.write("#DOCUMENTATION = This variable represents the WSDL File to be verified.\n");
                out.write("WSDLFile = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = String\n");
                out.write(editable);
                out.write("#DOCUMENTATION = The Service defined in the WSDL that will be used during the test.\n");
                out.write("ServiceName = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = String\n");
                out.write(editable);
                out.write("#DOCUMENTATION = The Port defined in the WSDL that is related to the selected Service Name and will be used during the test.\n");
                out.write("PortName = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = String\n");
                out.write(editable);
                out.write("#DOCUMENTATION = The Operation defined in the WSDL that is related to the selected Service Name and will be used during the test.\n");
                out.write("OperationName = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = boolean\n");
                out.write(editable);
                out.write("#DOCUMENTATION = This variable represents whether the Test Case is expected to pass.\n");
                out.write("CheckOutcomePassed = true\n");
                out.write("\n");

            } else if (fileName.contains("XHRR")) {
                out.write(iteration);
                out.write(groupName);
                out.write(type);
                out.write(editable);
                out.write("#DOCUMENTATION = This variable represents the WSDL File to be verified.\n");
                out.write("WSDLFile = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = String\n");
                out.write(editable);
                out.write("#DOCUMENTATION = The Service defined in the WSDL that will be used during the test.\n");
                out.write("ServiceName = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = String\n");
                out.write(editable);
                out.write("#DOCUMENTATION = The Port defined in the WSDL that is related to the selected Service Name and will be used during the test.\n");
                out.write("PortName = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = String\n");
                out.write(editable);
                out.write("#DOCUMENTATION = The Operation defined in the WSDL that is related to the selected Service Name and will be used during the test.\n");
                out.write("OperationName = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = boolean\n");
                out.write(editable);
                out.write("#DOCUMENTATION = This variable represents whether the Test Case is expected to pass.\n");
                out.write("CheckOutcomePassed = true\n");
                out.write("\n");

            } else if (fileName.contains("XHRO")) {
                out.write(iteration);
                out.write(groupName);
                out.write(type);
                out.write(editable);
                out.write("#DOCUMENTATION = This variable represents the WSDL File to be verified.\n");
                out.write("WSDLFile = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = String\n");
                out.write(editable);
                out.write("#DOCUMENTATION = The Service defined in the WSDL that will be used during the test.\n");
                out.write("ServiceName = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = String\n");
                out.write(editable);
                out.write("#DOCUMENTATION = The Port defined in the WSDL that is related to the selected Service Name and will be used during the test.\n");
                out.write("PortName = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = String\n");
                out.write(editable);
                out.write("#DOCUMENTATION = The Operation defined in the WSDL that is related to the selected Service Name and will be used during the test.\n");
                out.write("OperationName = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = boolean\n");
                out.write(editable);
                out.write("#DOCUMENTATION = This variable represents whether the Test Case is expected to pass.\n");
                out.write("CheckOutcomePassed = true\n");
                out.write("\n");

            } else if (fileName.contains("XFRO")) {
                out.write(iteration);
                out.write(groupName);
                out.write(type);
                out.write(editable);
                out.write("#DOCUMENTATION = This variable represents the WSDL File to be verified.\n");
                out.write("WSDLFile = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = String\n");
                out.write(editable);
                out.write("#DOCUMENTATION = The Service defined in the WSDL that will be used during the test.\n");
                out.write("ServiceName = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = String\n");
                out.write(editable);
                out.write("#DOCUMENTATION = The Port defined in the WSDL that is related to the selected Service Name and will be used during the test.\n");
                out.write("PortName = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = String\n");
                out.write(editable);
                out.write("#DOCUMENTATION = The Operation defined in the WSDL that is related to the selected Service Name and will be used during the test.\n");
                out.write("OperationName = #USERDEFINED#\n");
                out.write("\n");

                out.write("#PARAMETER TYPE = boolean\n");
                out.write(editable);
                out.write("#DOCUMENTATION = This variable represents whether the Test Case is expected to pass.\n");
                out.write("CheckOutcomePassed = true\n");
                out.write("\n");

            } else {
                out.write(iteration);
                out.write(groupName);
                out.write(type);
                out.write(editable);
                out.write("#DOCUMENTATION = This variable represents the WSDL File to be verified.\n");
                out.write("WSDLFile = #USERDEFINED#\n");
                out.write("\n");

                out.write(type);
                out.write(editable);
                out.write("#DOCUMENTATION = TBD\n");
                out.write("#variable = variablevalue\n");
                out.write("\n");

            }
        } catch (Exception ex) {
            System.out.println("****** Writing Error:*******\n");
            ex.printStackTrace();
        } finally {
        }



    }
}
