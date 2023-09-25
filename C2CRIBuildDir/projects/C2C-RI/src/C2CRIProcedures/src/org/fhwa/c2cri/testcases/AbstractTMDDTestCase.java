/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testcases;

import java.io.ByteArrayInputStream;
import org.fhwa.c2cri.testprocedures.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.wsdl.BindingOperation;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.LogManager;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.infolayer.MessageSpecificationItem;
import org.fhwa.c2cri.infolayer.MessageValueTester;
import org.fhwa.c2cri.ntcip2306v109.messaging.MessageSpecificationProcessor;
import org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.encoders.SOAPEncoder;
import org.fhwa.c2cri.testmodel.verification.XSLTransformer;
import tmddv3verification.DesignElement;
import tmddv3verification.utilities.TMDDDatabase;
import tmddv3verification.utilities.TMDDWSDL;

/**
 *
 * @author TransCore ITS
 */
public abstract class AbstractTMDDTestCase implements TestCase {

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
    private String invalidCode = "0";

    protected void initializeTestCaseInformation(String needID, String dialog, String centerMode, boolean valid, String invalidCode) {
        TestProcedureNamer testProcedureNameMaker = new TestProcedureNamer(needID, dialog, centerMode);
        procedureName = testProcedureNameMaker.getProcedureID();
        TestCaseNamer testCaseNameMaker = new TestCaseNamer(needID, dialog, centerMode, valid, invalidCode);
        testCaseID = testCaseNameMaker.getProcedureID();
        testCaseTitle = testCaseNameMaker.getProcedureTitle();
        testCaseDescription = testCaseNameMaker.getProcedureDescription()+(!valid?"":getUserMessageVerificationText(needID,centerMode));
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        testCaseProductionDate = dateFormat.format(date);
        this.needID = needID;
        String sutCenterMode = (centerMode.equals(TMDDParameters.TMDD_EC_MODE) ? TMDDParameters.TMDD_OC_Mode : TMDDParameters.TMDD_EC_MODE);
        relatedRequirements = NRTM_RTM_Design_Data.getInstance().makeTestCaseRequirementsList(needID, sutCenterMode, dialog, valid);
        if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)&&sutCenterMode.equals(TMDDParameters.TMDD_OC_Mode)){
            try{
              String pubDialog = TMDDSubPubMapping.getInstance().getPublicationDialog(dialog, Integer.parseInt(needID));
              ArrayList<String> publicationMsgRequirements = NRTM_RTM_Design_Data.getInstance().makeTestCaseRequirementsList(needID,sutCenterMode,pubDialog,valid);
              for (String thisRequirement : publicationMsgRequirements){
                  if (!relatedRequirements.contains(thisRequirement)){
                      relatedRequirements.add(thisRequirement);
                  }
              }
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
        precondition = NRTM_RTM_Design_Data.getInstance().getDialogPrecondition(needID, sutCenterMode, dialog);
        mode = centerMode;
        this.dialog = dialog;
        this.invalidCode = invalidCode;
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

    public List<TestCaseInput> makeTestCaseInputs(String mode, String dialog, boolean errorCase) {
        List<TestCaseInput> theInputs = new ArrayList<TestCaseInput>();
        DesignElement theElement = (DesignElement) NRTM_RTM_Design_Data.getInstance().getTmddDesign().getDesignMap().get(dialog);
        if (theElement != null) {
            if (mode.equals("EC")) {  // The SUT is an EC
                if (!dialog.endsWith("Update")) { // Req/Response or Subscription Request
                    theInputs.add(new TestCaseInput("Operator Initiatiation of the " + dialog + " dialog.", "From the Test Plan"));
                } else {
                    if (dialog.endsWith("Update")) {  // Publication dialog
                        DesignElement inputMessageHeader = theElement.getSubElements().get(0);
                        theInputs.add(new TestCaseInput(inputMessageHeader.getElementName(), "From the Test Plan"));
                        DesignElement inputMessage = theElement.getSubElements().get(1);
                        theInputs.add(new TestCaseInput(inputMessage.getElementName(), "From the Test Plan"));

//                    } else {
//                        DesignElement outputMessage = theElement.getSubElements().get(0);
//                        theInputs.add(new TestCaseInput(outputMessage.getElementName(), "From the Test Plan"));

                    }
                }
            } else {  // The SUT is an OC
                if (dialog.endsWith("Update")) {  // Publication
                    theInputs.add(new TestCaseInput("Operator Initiatiation of the " + dialog + " dialog.", "From the Test Plan"));
                } else {
                    if (dialog.endsWith("Subscription")) {  // Subscription Dialogs
                        DesignElement outputMessageHeader = theElement.getSubElements().get(0);
                        theInputs.add(new TestCaseInput(outputMessageHeader.getElementName(), "From the Test Plan"));
                        DesignElement outputMessage = theElement.getSubElements().get(1);
                        theInputs.add(new TestCaseInput(outputMessage.getElementName(), "From the Test Plan"));

                    } else {  // Request/Response
                        DesignElement outputMessage = theElement.getSubElements().get(0);
                        theInputs.add(new TestCaseInput(outputMessage.getElementName(), "From the Test Plan"));

                    }
                }
            }
        }
        return theInputs;
    }

    public List<TestCaseOutput> makeTestCaseOutputs(String mode, String dialog, boolean errorCase) {
        List<TestCaseOutput> theOutputs = new ArrayList<TestCaseOutput>();
        DesignElement theElement = (DesignElement) NRTM_RTM_Design_Data.getInstance().getTmddDesign().getDesignMap().get(dialog);
        if (theElement != null) {
            if (mode.equals("EC")) {
                if (dialog.endsWith("Update")) {  // Publication
                    if (errorCase) {
                        if (theElement.getSubElements().size() == 4) {
                            DesignElement outputMessage = theElement.getSubElements().get(3);
                            theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.03c"));
                        } else {
                            theOutputs.add(new TestCaseOutput("***Error in WSDL Specification of " + dialog + "***", "Error in WSDL Specification of " + dialog));
//                            System.out.println("Here's the problem!!!");
                        }

                    } else {
                        DesignElement outputMessage;
                        try {
                            outputMessage = theElement.getSubElements().get(2);
                        } catch (Exception ex) {
                            outputMessage = theElement.getSubElements().get(1);

                        }
                        theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.03c"));
                    }
//                    theOutputs.add(new TestCaseOutput("Operator Initiatiation of the " + dialog + " dialog.", "Per TMDD v3.03c"));
                } else {  // Request or Subscription dialogs
                    if (dialog.endsWith("Subscription")) {
                        DesignElement outputMessageHeader = theElement.getSubElements().get(0);
                        theOutputs.add(new TestCaseOutput(outputMessageHeader.getElementName(), "Per TMDD v3.03c"));
                        DesignElement outputMessage = theElement.getSubElements().get(1);
                        theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.03c"));

                    } else { // Request dialog
                        DesignElement outputMessage = theElement.getSubElements().get(0);
                        theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.03c"));

                    }
                }
            } else {  // SUT is an OC
                if (dialog.endsWith("Update")) {  // Publication
                    DesignElement outputMessageHeader = theElement.getSubElements().get(0);
                    theOutputs.add(new TestCaseOutput(outputMessageHeader.getElementName(), "Per TMDD v3.03c"));
                    DesignElement outputMessage = theElement.getSubElements().get(1);
                    theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.03c"));
//                    theOutputs.add(new TestCaseOutput("Operator Initiatiation of the " + dialog + " dialog.", "Per TMDD v3.03c"));
                } else {
                    if (dialog.endsWith("Subscription")) {  // Subscription
                        if (errorCase) {
                            DesignElement outputMessage = theElement.getSubElements().get(3);
                            theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.03c"));

                        } else {
                            DesignElement outputMessage = theElement.getSubElements().get(2);
                            theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.03c"));

                        }

                    } else {  // Request Dialog
                        if (errorCase) {
                            DesignElement outputMessage = theElement.getSubElements().get(2);
                            theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.03c"));

                        } else {
                            DesignElement outputMessage = theElement.getSubElements().get(1);
                            theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.03c"));

                        }
                    }
                }
            }
        }
        return theOutputs;
    }

    public final void toDatabase() {
        // Write the TestProcedure Summary Info to the Test Procedure Table

        // Write the TestProcedure "steps" to the test procedure steps table

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

        theDatabase.queryNoResult("DELETE * FROM FinalConsilidatedTestCases WHERE ShortTsId = " + "'" + this.testCaseID + "'");

        String requirements = "";
        for (String theRequirement : this.relatedRequirements) {
            requirements = requirements.concat(requirements.isEmpty() ? theRequirement : "\n" + theRequirement);
        }

        String variables = "";
        for (Variable theVariable : this.procedureVariables) {
            variables = variables.concat(variables.isEmpty() ? theVariable.getName() + " : " + theVariable.getSource() : "\n" + theVariable.getName() + " : " + theVariable.getSource());
        }

        String outputText = "Each input execution shall generate an RI Test Result Status of Passed or Failed associated with the matching expected result shown in the Test Case Data Variable Table in the appendix.";
        String inputText = "Refer to the Test Case Data Variable Table in the appendix for the test case input paramaters.";
        theDatabase.queryNoResult("INSERT INTO FinalConsilidatedTestCases (Component,ShortTsId,"
                + "ConsolidatedTestCaseRecordsTable_TsId,TsDsc,ItemList,ConDsc,"
                + "DepDsc,HardwareEnv,SoftwareEnv,OtherEnv,Inputs,Procedures,Outputs,OutputProcedures,OldInputs) VALUES ("
                + "'TMDD',"
                + "'" + this.testCaseID + "',"
                + "'" + this.testCaseID + "',"
                + "'" + this.getTestCaseDescription() + "',"
                + "'" + requirements + "',"
                + "'None',"
                + "'None',"
                + "'See C2C RI SRS',"
                + "'See C2C RI SRS',"
                + "'None'," // OtherEnv
                //             + "'" + testCaseInputs.toString().replace(",", "|") + "'," //Inputs
                + "'" + inputText + "'," //Inputs
                + "'" + procedureName + "'," //Procedures
                + "'" + outputText + "'," //Outputs
                //                + "'" + testCaseOutputs.toString().replace(",", "|") + "'," //Outputs
                //                + "'C2CRI-TestCaseResultsVerfication'," //OutputProcedures
                + "''," //OutputProcedures
                + "'" + testCaseInputs.toString().replace(",", "|") + "')"); //OldInputs

        if (this.testCaseID.equals("TCS-4-dlOrganizationInformationRequest-OC-Valid")){
            theDatabase.queryNoResult("DELETE * FROM TMDDTestCasesTable WHERE TCName = " + "'TCS-2-dlCenterActiveVerificationRequest-EC-Valid'");
            theDatabase.queryNoResult("DELETE * FROM TMDDTestCasesTable WHERE TCName = " + "'TCS-2-dlCenterActiveVerificationRequest-OC-Valid'");
            theDatabase.queryNoResult("DELETE * FROM TMDDTestCasesTable WHERE TCName = " + "'TCS-3-PeriodicSubscription-EC-Valid'");
            theDatabase.queryNoResult("DELETE * FROM TMDDTestCasesTable WHERE TCName = " + "'TCS-3-PeriodicSubscription-OC-Valid'");
            theDatabase.queryNoResult("DELETE * FROM TMDDTestCasesTable WHERE TCName = " + "'TCS-3-EventDrivenSubscription-EC-Valid'");
            theDatabase.queryNoResult("DELETE * FROM TMDDTestCasesTable WHERE TCName = " + "'TCS-3-EventDrivenSubscription-OC-Valid'");

        theDatabase.queryNoResult("INSERT INTO TMDDTestCasesTable (TCName,ScriptFile,"
                + "DataFile,Description,"
                + "Type) VALUES ("
                + "'TCS-2-dlCenterActiveVerificationRequest-EC-Valid',"
                + "'TPS-2-dlCenterActiveVerificationRequest-EC.xml" + "',"
                + "'TCS-2-dlCenterActiveVerificationRequest-EC-Valid.data" + "',"
                + "'This test case is used to verify the SUTs support of the Request-Response dialog pattern (dlCenterActiveVerificationRequest dialog) as an EC using the variable values specified by the Test Plan.  This test case supports verification of requirements related to user need 2.3.1.2 [Need to Support Requests]. This Test Case tests for a Valid response result.',"
                + "'OC')");

        theDatabase.queryNoResult("INSERT INTO TMDDTestCasesTable (TCName,ScriptFile,"
                + "DataFile,Description,"
                + "Type) VALUES ("
                + "'TCS-2-dlCenterActiveVerificationRequest-OC-Valid',"
                + "'TPS-2-dlCenterActiveVerificationRequest-OC.xml" + "',"
                + "'TCS-2-dlCenterActiveVerificationRequest-OC-Valid.data" + "',"
                + "'This test case is used to verify the SUTs support of the Request-Response dialog pattern (dlCenterActiveVerificationRequest dialog) as an OC using the variable values specified by the Test Plan.  This test case supports verification of requirements related to user need 2.3.1.2 [Need to Support Requests]. This Test Case tests for a Valid response result.',"
                + "'EC')");

        theDatabase.queryNoResult("INSERT INTO TMDDTestCasesTable (TCName,ScriptFile,"
                + "DataFile,Description,"
                + "Type) VALUES ("
                + "'TCS-3-PeriodicSubscription-EC-Valid',"
                + "'TPS-3-PeriodicSubscription-EC.xml" + "',"
                + "'TCS-3-PeriodicSubscription-EC-Valid.data" + "',"
                + "'This test case is used to verify the SUTs support of periodic subscriptions (dlCenterActiveVerificationSubscription dialog) as an EC using the variable values specified by the Test Plan.  This test case supports verification of requirements related to user need 2.3.1.3 [Need to Support Subscriptions]. This Test Case tests for a Valid response result.',"
                + "'OC')");

        theDatabase.queryNoResult("INSERT INTO TMDDTestCasesTable (TCName,ScriptFile,"
                + "DataFile,Description,"
                + "Type) VALUES ("
                + "'TCS-3-PeriodicSubscription-OC-Valid',"
                + "'TPS-3-PeriodicSubscription-OC.xml" + "',"
                + "'TCS-3-PeriodicSubscription-OC-Valid.data" + "',"
                + "'This test case is used to verify the SUTs support of periodic subscriptions (dlCenterActiveVerificationSubscription dialog) as an OC using the variable values specified by the Test Plan.  This test case supports verification of requirements related to user need 2.3.1.3 [Need to Support Subscriptions]. This Test Case tests for a Valid response result.',"
                + "'EC')");


        theDatabase.queryNoResult("INSERT INTO TMDDTestCasesTable (TCName,ScriptFile,"
                + "DataFile,Description,"
                + "Type) VALUES ("
                + "'TCS-3-EventDrivenSubscription-EC-Valid',"
                + "'TPS-3-EventDrivenSubscription-EC.xml" + "',"
                + "'TCS-3-EventDrivenSubscription-EC-Valid.data" + "',"
                + "'This test case is used to verify the SUTs support of event driven subscriptions (dlCenterActiveVerificationSubscription dialog) as an EC using the variable values specified by the Test Plan.  This test case supports verification of requirements related to user need 2.3.1.3 [Need to Support Subscriptions]. This Test Case tests for a Valid response result.',"
                + "'OC')");
        
        theDatabase.queryNoResult("INSERT INTO TMDDTestCasesTable (TCName,ScriptFile,"
                + "DataFile,Description,"
                + "Type) VALUES ("
                + "'TCS-3-EventDrivenSubscription-OC-Valid',"
                + "'TPS-3-EventDrivenSubscription-OC.xml" + "',"
                + "'TCS-3-EventDrivenSubscription-OC-Valid.data" + "',"
                + "'This test case is used to verify the SUTs support of event driven subscriptions (dlCenterActiveVerificationSubscription dialog) as an OC using the variable values specified by the Test Plan.  This test case supports verification of requirements related to user need 2.3.1.3 [Need to Support Subscriptions]. This Test Case tests for a Valid response result.',"
                + "'EC')"); 
            
        }

        theDatabase.queryNoResult("DELETE * FROM TMDDTestCasesTable WHERE TCName = " + "'" + this.testCaseID + "'");

        String sutCenterMode = (mode.equals(TMDDParameters.TMDD_EC_MODE) ? TMDDParameters.TMDD_OC_Mode : TMDDParameters.TMDD_EC_MODE);

        theDatabase.queryNoResult("INSERT INTO TMDDTestCasesTable (TCName,ScriptFile,"
                + "DataFile,Description,"
                + "Type) VALUES ("
                + "'" + this.testCaseID + "',"
                + "'" + this.procedureName + ".xml" + "',"
                + "'" + this.testCaseID + ".data" + "',"
                + "'" + this.testCaseDescription.replace("\n", "  ") + "',"
                + "'" + mode + "')");
//                + "'" + sutCenterMode + "')");

        theDatabase.disconnectFromDatabase();
    }

    public final void toTestCaseFile(String path, String fileName) {
        File newFile = new File(path + File.separatorChar + fileName);
        if (newFile.exists()) 
		{
			try
			{
				Files.delete(Paths.get(newFile.getAbsolutePath()));
				newFile = new File(path + File.separatorChar + fileName);
			}
			catch (IOException oEx)
			{
				LogManager.getLogger(getClass()).error(oEx, oEx);
			}

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

        try (Writer out = new OutputStreamWriter(new FileOutputStream(path + File.separatorChar + fileName), StandardCharsets.UTF_8))
		{
            out.write(header);

            out.write(iteration);
            out.write(groupName);

            out.write("#PARAMETER TYPE = Boolean\n");
            out.write("#EDITABLE = true\n");
            out.write("#DOCUMENTATION = This variable represents whether the Test Case is expected to pass.  Valid values are True or False.\n");
            out.write("CheckOutcomePassed = true\n\n");


            out.write("#PARAMETER TYPE = String\n");
            out.write("#EDITABLE = false\n");
            out.write("#DOCUMENTATION = This variable contains a set of element values that are expected in the message to be verified.\n");
            out.write("VerificationSpec = #VALUESPEC#Values#VALUESPEC#\n\n");

            for (Variable theVariable : this.procedureVariables) {
                if (theVariable instanceof DialogVariable) {
                    DialogVariable thisVariable = (DialogVariable) theVariable;
                    if (!thisVariable.isLocalVariable() && !thisVariable.isReturnVariable()) {
                        String thisType = (!thisVariable.getDataType().isEmpty() ? "#PARAMETER TYPE = "+thisVariable.getDataType()+"\n" : "#PARAMETER TYPE = String\n");                        
                        out.write(thisType);
                        out.write(editable);
                        String variables = "";
                        variables = variables.concat("#DOCUMENTATION = " + thisVariable.getDescription() +getInvalidDataDescription(thisVariable.getInvalidCaseDefaultValue(), this.invalidCode)+ "\n");
                        variables = variables.concat(thisVariable.getName() + " = " + (this.getTestCaseID().contains("InValid") ? getInvalidDataValue(thisVariable.getInvalidCaseDefaultValue(), this.invalidCode) : thisVariable.getValidCaseDefaultValue()) + "\n");
                        out.write(variables);
                        out.write("\n");
                    }
                } else if (theVariable instanceof ProcedureVariable) {
                    ProcedureVariable thisVariable = (ProcedureVariable) theVariable;
                    String thisType = (!thisVariable.getDataType().isEmpty() ? "#PARAMETER TYPE = "+thisVariable.getDataType()+"\n" : "#PARAMETER TYPE = String\n");                        
                    out.write(thisType);
                    out.write(editable);
                    String variables = "";
                    variables = variables.concat("#DOCUMENTATION = " + thisVariable.getDescription() + getInvalidDataDescription(thisVariable.getInvalidCaseDefaultValue(), this.invalidCode)+"\n");
                    variables = variables.concat(thisVariable.getName() + " = " + (this.getTestCaseID().contains("InValid") ? getInvalidDataValue(thisVariable.getInvalidCaseDefaultValue(), this.invalidCode) : thisVariable.getValidCaseDefaultValue()) + "\n");
                    out.write(variables);
                    out.write("\n");

                }
            }


            out.write("\n#GROUP NAME = Values\n\n");
            boolean createValues = true;


            boolean createMessage = false;
            if ((mode.equals(TMDDParameters.TMDD_EC_MODE) && (!dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)))) {
                createMessage = true;
                createValues = true;
            }
            if ((mode.equals(TMDDParameters.TMDD_OC_Mode) && (!dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX))) && (!testCaseID.contains("InValid"))) {
                createMessage = true;
                createValues = true;
            }
            if ((mode.equals(TMDDParameters.TMDD_OC_Mode) && (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)))) {
                createMessage = true;
                createValues = true;
            }
            if ((mode.equals(TMDDParameters.TMDD_EC_MODE) && (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)))) {
                createMessage = true;
                createValues = true;
            }

            if (createValues) {
                addValues(out);
            }

            if (createMessage) {
                if (!(this.getTestCaseID().contains("InValid") && this.invalidCode.equals("4"))) {
                    String messageOutput = getMessageOutput(path, fileName);
                    out.write(messageOutput);
                }
                //           addMessage(out);
//                TMDDWSDL riWSDLInstance = NRTM_RTM_Design_Data.getInstance().getTmddDesign().getTmddWSDL();
//
//                // Look for this dialog
//
//
//                Map services = riWSDLInstance.getServiceNames();
//                //           System.out.println(services);
//
//                Iterator serviceIterator = services.keySet().iterator();
//                while (serviceIterator.hasNext()) {
//                    String theService = (String) serviceIterator.next();
//                    QName qService = (QName) services.get(theService);
//                    System.out.println("Checking the Service : " + theService + " for dialog " + dialog);
////                if (theService.equalsIgnoreCase("tmddOCSoapHttpService")) {
//                    // Found the selected service in the WSDL
//
//                    Map ports = riWSDLInstance.getServicePortNames(qService);
//                    Iterator portIterator = ports.keySet().iterator();
//                    while (portIterator.hasNext()) {
//                        String thePort = (String) portIterator.next();
//                        BindingOperation theBindingOperation = riWSDLInstance.getServicePortBindingOperation(qService, thePort, dialog);
//                        if (theBindingOperation != null) {
//                            System.out.println("The BINDING NAME = " + theBindingOperation.getName());
//
//                            OperationMessageBuilder messageBuilder = new OperationMessageBuilder(riWSDLInstance);
//                            System.out.println("\n\n\n NEW INPUT MESSAGE by BINDING OPERATION");
//                            SOAPEncoder theEncoder = new SOAPEncoder("SoapType");
//                            MessageSpecificationProcessor newProcessor = new MessageSpecificationProcessor();
//                            MessageSpecification newSpecification = null;
//                            if ((((dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)&&(mode.equals(TMDDParameters.TMDD_EC_MODE))) || dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) && (mode.equals(TMDDParameters.TMDD_EC_MODE)))
//                                    || (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX) && (mode.equals(TMDDParameters.TMDD_OC_Mode)))) {
////                                byte[] returnMsg = theEncoder.encode(messageBuilder.buildSoapMessageFromInput(theBindingOperation, true, true).getBytes());
//                                byte[] returnMsg = messageBuilder.buildSoapMessageFromInput(theBindingOperation, true, true).getBytes();
//                                newSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
//                                System.out.println(new String(returnMsg));
//                            } else if (dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)){
////                                byte[] returnMsg = theEncoder.encode(messageBuilder.buildEmptyMessageForFault().getBytes());
////                                newSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
////                                System.out.println(new String(returnMsg));
//  //                              byte[] returnMsg = theEncoder.encode(messageBuilder.buildMessageFromOutput(theBindingOperation, true, true).getBytes());
//                                byte[] returnMsg = messageBuilder.buildMessageFromOutput(theBindingOperation, true, true).getBytes();
//                                newSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
//                                System.out.println(new String(returnMsg));
//
//                            }
//
//                            if (newSpecification != null) {
//                                if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX) && mode.endsWith(TMDDParameters.TMDD_OC_Mode)) {
//                                    out.write("#GROUP NAME = SubResponseMessage\n");
//                                } else if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX) && mode.endsWith(TMDDParameters.TMDD_EC_MODE)) {
//                                    out.write("#GROUP NAME = PubResponseMessage\n");
//                                } else {
//                                    out.write("#GROUP NAME = Message\n");
//                                }
//
//                                HashMap<String, MessageDetailDesignMatcher> msgDetailMap = new HashMap<String, MessageDetailDesignMatcher>();
//                                MessageDetailDesignMatcher msgMatch = null;
//                                //                               MessageDetailDesignMatcher msgMatch = new MessageDetailDesignMatcher(String messageName);
//                                for (String parameterEntry : newSpecification.getMessageSpec()) {
//                                    out.write(type);
//                                    out.write(editable);
//
//                                    String message = "";
//                                    String parameter = "";
//                                    if (parameterEntry.contains(".")) {
//                                        message = parameterEntry.substring(parameterEntry.indexOf(":") + 1, parameterEntry.indexOf("."));
//                                        String specPart = parameterEntry.substring(0, parameterEntry.indexOf("="));
//                                        parameter = specPart.substring(specPart.lastIndexOf(".") + 1).trim();
//                                    } else {
//                                        parameter = parameterEntry.substring(0, parameterEntry.indexOf("=")).trim();
//                                        message = parameterEntry.substring(parameterEntry.indexOf(":") + 1, parameterEntry.indexOf("=")).trim();
//                                    }
//
//                                    if (msgDetailMap.containsKey(message)) {
//                                        msgMatch = msgDetailMap.get(message);
//                                    } else {
//                                        msgMatch = new MessageDetailDesignMatcher(message);
//                                        msgDetailMap.put(message, msgMatch);
//                                    }
//
//                                    String documentationString = "";
//                                    if (msgMatch.getMatchesForElementName(parameter).size() > 0) {
//                                        MessageDetailDesignElement theElement = msgMatch.getMatchesForElementName(parameter).get(0);
//                                        documentationString = parameterEntry.substring(0, parameterEntry.indexOf("=")) + " BaseType=" + theElement.getBaseType() + "  ParentType= " + theElement.getParentType();
//                                    } else {
//                                        documentationString = parameterEntry.substring(0, parameterEntry.indexOf("="));
//                                    }
//
//                                    out.write("#DOCUMENTATION = " + documentationString + "\n");
//                                    out.write(parameterEntry + "\n\n");
//                                }
//
//                            }
//
//                        }
//                    }
//                }
//
//
//
//                serviceIterator = services.keySet().iterator();
//                while (serviceIterator.hasNext()) {
//                    String theService = (String) serviceIterator.next();
//                    QName qService = (QName) services.get(theService);
//
//                    String relatedDialog = "";
//                    if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
//                        relatedDialog = dialog.replace(TMDDParameters.TMDD_PUBLICATION_SUFFIX, TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX);
//                    } else {
//                        relatedDialog = dialog.replace(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX, TMDDParameters.TMDD_PUBLICATION_SUFFIX);
//                    }
//
//                    System.out.println("Checking the Service : " + theService + " for dialog " + relatedDialog);
////                if (theService.equalsIgnoreCase("tmddOCSoapHttpService")) {
//                    // Found the selected service in the WSDL
//
//                    Map ports = riWSDLInstance.getServicePortNames(qService);
//                    Iterator portIterator = ports.keySet().iterator();
//                    while (portIterator.hasNext()) {
//                        String thePort = (String) portIterator.next();
//                        BindingOperation theBindingOperation = riWSDLInstance.getServicePortBindingOperation(qService, thePort, relatedDialog);
//                        if (theBindingOperation != null) {
//                            System.out.println("The BINDING NAME = " + theBindingOperation.getName());
//
//                            OperationMessageBuilder messageBuilder = new OperationMessageBuilder(riWSDLInstance);
//                            System.out.println("\n\n\n NEW INPUT MESSAGE by BINDING OPERATION");
//                            SOAPEncoder theEncoder = new SOAPEncoder("SoapType");
//                            MessageSpecificationProcessor newProcessor = new MessageSpecificationProcessor();
//
//                            MessageSpecification responseSpecification = null;
//                            if ((((dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)&&(mode.equals(TMDDParameters.TMDD_OC_Mode))) || dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) && (mode.equals(TMDDParameters.TMDD_EC_MODE)))
//                                    || (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX) && (mode.equals(TMDDParameters.TMDD_OC_Mode)))) {
//  //                              byte[] returnMsg = theEncoder.encode(messageBuilder.buildSoapMessageFromOutput(theBindingOperation, true, true).getBytes());
//                                byte[] returnMsg = messageBuilder.buildSoapMessageFromOutput(theBindingOperation, true, true).getBytes();
//                                responseSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
//                                System.out.println(new String(returnMsg));
//                            } else if (!dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)){
////                                byte[] returnMsg = theEncoder.encode(messageBuilder.buildEmptyMessageForFault().getBytes());
////                                newSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
////                                System.out.println(new String(returnMsg));
//  //                              byte[] returnMsg = theEncoder.encode(messageBuilder.buildMessageFromInput(theBindingOperation, true, true).getBytes());
//                                byte[] returnMsg = messageBuilder.buildMessageFromInput(theBindingOperation, true, true).getBytes();
//                                responseSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
//                                System.out.println(new String(returnMsg));
//
//                            }
//
//                            if (responseSpecification != null) {
//                                if (relatedDialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX) && mode.endsWith(TMDDParameters.TMDD_OC_Mode)) {
//                                    out.write("#GROUP NAME = SubResponseMessage\n");
//                                } else if (relatedDialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX) && mode.endsWith(TMDDParameters.TMDD_EC_MODE)) {
//                                    out.write("#GROUP NAME = PubResponseMessage\n");
//                                } else {
//                                    out.write("#GROUP NAME = Message\n");
//                                }
//
//                                HashMap<String, MessageDetailDesignMatcher> msgDetailMap = new HashMap<String, MessageDetailDesignMatcher>();
//                                MessageDetailDesignMatcher msgMatch = null;
//
//                                for (String parameterEntry : responseSpecification.getMessageSpec()) {
//                                    out.write(type);
//                                    out.write(editable);
//
//                                    String message = "";
//                                    String parameter = "";
//                                    if (parameterEntry.contains(".")) {
//                                        message = parameterEntry.substring(parameterEntry.indexOf(":") + 1, parameterEntry.indexOf("."));
//                                        String specPart = parameterEntry.substring(0, parameterEntry.indexOf("="));
//                                        parameter = specPart.substring(specPart.lastIndexOf(".") + 1).trim();
//                                    } else {
//                                        parameter = parameterEntry.substring(0, parameterEntry.indexOf("=")).trim();
//                                        message = parameterEntry.substring(parameterEntry.indexOf(":") + 1, parameterEntry.indexOf("=")).trim();
//                                    }
//
//                                    if (msgDetailMap.containsKey(message)) {
//                                        msgMatch = msgDetailMap.get(message);
//                                    } else {
//                                        msgMatch = new MessageDetailDesignMatcher(message);
//                                        msgDetailMap.put(message, msgMatch);
//                                    }
//
//                                    String documentationString = "";
//                                    if (msgMatch.getMatchesForElementName(parameter).size() > 0) {
//                                        MessageDetailDesignElement theElement = msgMatch.getMatchesForElementName(parameter).get(0);
//                                        documentationString = parameterEntry.substring(0, parameterEntry.indexOf("=")) + " BaseType=" + theElement.getBaseType() + "  ParentType= " + theElement.getParentType();
//                                    } else {
//                                        documentationString = parameterEntry.substring(0, parameterEntry.indexOf("="));
//                                    }
//
//                                    out.write("#DOCUMENTATION = " + documentationString + "\n");
//                                    out.write(parameterEntry + "\n\n");
//                                }
//
//                            }
//                        }
//                    }
//                }
//
            }
        } catch (Exception ex) {
            System.out.println("****** Writing Error:*******\n");
            ex.printStackTrace();
        } finally {
        }



    }

    public final void toTestCaseMatrix() {
        // Write the TestProcedure Summary Info to the Test Procedure Table

        // Write the TestProcedure "steps" to the test procedure steps table

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

//        theDatabase.queryNoResult("DELETE * FROM FinalConsilidatedTestCases WHERE ShortTsId = " + "'" + this.testCaseID + "'");

        theDatabase.queryNoResult("DELETE * FROM TMDDTestCaseMatrixTable WHERE TCID = " + "'" + this.testCaseID + "'");

        String needID = NRTM_RTM_Design_Data.getInstance().getNeedID(this.needID);

        if (this.testCaseID.equals("TCS-4-dlOrganizationInformationRequest-OC-Valid")){
            theDatabase.queryNoResult("INSERT INTO TMDDTestCaseMatrixTable (NeedID,RequirementID,"
                    + "TCID,ItemType,"
                    + "Precondition) VALUES ("
                    + "'2.3.1.2',"
                    + "'3.3.1.2',"
                    + "'TCS-2-dlCenterActiveVerificationRequest-EC-Valid',"
                    + "'SUT/RI',"
                    + "'" + this.precondition + "')");
            theDatabase.queryNoResult("INSERT INTO TMDDTestCaseMatrixTable (NeedID,RequirementID,"
                    + "TCID,ItemType,"
                    + "Precondition) VALUES ("
                    + "'2.3.1.2',"
                    + "'3.3.1.2',"
                    + "'TCS-2-dlCenterActiveVerificationRequest-OC-Valid',"
                    + "'SUT/RI',"
                    + "'" + this.precondition + "')");
            theDatabase.queryNoResult("INSERT INTO TMDDTestCaseMatrixTable (NeedID,RequirementID,"
                    + "TCID,ItemType,"
                    + "Precondition) VALUES ("
                    + "'2.3.1.2',"
                    + "'3.4.2',"
                    + "'TCS-2-dlCenterActiveVerificationRequest-OC-Valid',"
                    + "'SUT/RI',"
                    + "'" + this.precondition + "')");
            theDatabase.queryNoResult("INSERT INTO TMDDTestCaseMatrixTable (NeedID,RequirementID,"
                    + "TCID,ItemType,"
                    + "Precondition) VALUES ("
                    + "'2.3.1.3',"
                    + "'3.3.1.3.1',"
                    + "'TCS-3-PeriodicSubscription-EC-Valid',"
                    + "'SUT/RI',"
                    + "'Subscription')");
            theDatabase.queryNoResult("INSERT INTO TMDDTestCaseMatrixTable (NeedID,RequirementID,"
                    + "TCID,ItemType,"
                    + "Precondition) VALUES ("
                    + "'2.3.1.3',"
                    + "'3.3.1.3.1',"
                    + "'TCS-3-PeriodicSubscription-OC-Valid',"
                    + "'SUT/RI',"
                    + "'Subscription')");
            theDatabase.queryNoResult("INSERT INTO TMDDTestCaseMatrixTable (NeedID,RequirementID,"
                    + "TCID,ItemType,"
                    + "Precondition) VALUES ("
                    + "'2.3.1.3',"
                    + "'3.4.1',"
                    + "'TCS-3-PeriodicSubscription-OC-Valid',"
                    + "'SUT/RI',"
                    + "'Subscription')");
            theDatabase.queryNoResult("INSERT INTO TMDDTestCaseMatrixTable (NeedID,RequirementID,"
                    + "TCID,ItemType,"
                    + "Precondition) VALUES ("
                    + "'2.3.1.3',"
                    + "'3.3.1.3.2',"
                    + "'TCS-3-EventDrivenSubscription-EC-Valid',"
                    + "'SUT/RI',"
                    + "'Subscription')");
            theDatabase.queryNoResult("INSERT INTO TMDDTestCaseMatrixTable (NeedID,RequirementID,"
                    + "TCID,ItemType,"
                    + "Precondition) VALUES ("
                    + "'2.3.1.3',"
                    + "'3.3.1.3.2',"
                    + "'TCS-3-EventDrivenSubscription-OC-Valid',"
                    + "'SUT/RI',"
                    + "'Subscription')");
            
            
        }
        for (String theRequirement : this.relatedRequirements) {
            theDatabase.queryNoResult("INSERT INTO TMDDTestCaseMatrixTable (NeedID,RequirementID,"
                    + "TCID,ItemType,"
                    + "Precondition) VALUES ("
                    + "'" + needID + "',"
                    + "'" + theRequirement + "',"
                    + "'" + this.testCaseID + "',"
                    + "'" + getTestItemType() + "',"
                    + "'" + this.precondition + "')");


            //           System.out.println("Test Case Matrix Entry: " + this.needID+","+theRequirement+","+this.mode+","+getTestItemType()+","+this.testCaseID);
        }
        theDatabase.disconnectFromDatabase();

        /**
         * theDatabase.queryNoResult("INSERT INTO
         * FinalConsilidatedTestCasesTable (Component,ShortTsId," +
         * "ConsolidatedTestCaseRecordsTable_TsId,TsDsc,ItemList,ConDsc,"
         * +"DepDsc,HardwareEnv,SoftwareEnv,OtherEnv,Inputs,Procedures,Outputs,OutputProcedures,OldInputs)
         * VALUES (" + "'TMDD'," + "'" + this.testCaseID + "'," + "'" +
         * this.testCaseID + "'," + "'" + this.getTestCaseDescription() + "'," +
         * "'" + requirements + "'," + "'None'," + "'None'," + "'See C2C RI
         * SRS'," + "'See C2C RI SRS'," + "'None'," // OtherEnv +
         * "'"+testCaseInputs.toString().replace(",", "|")+"'," //Inputs +
         * "'"+procedureName+"'," //Procedures + "'" +
         * testCaseOutputs.toString().replace(",", "|") + "'," //Outputs +
         * "'C2CRI-TestCaseResultsVerfication'," //OutputProcedures + "'" +
         * testCaseInputs.toString().replace(",", "|") + "')"); //OldInputs
         *
         *
         *
         * theDatabase.disconnectFromDatabase();
         */
    }

    private String getInvalidDataValue(String inputData, String errorType) {
        String returnValue = inputData;
        String[] values = inputData.split(",");
        Integer errorTypeIndex = new Integer(errorType.trim());

        if (values.length >= errorTypeIndex) {
            returnValue = values[errorTypeIndex - 1];
        } else if (values.length > 0) {
            returnValue = values[values.length - 1];
        }
        if (returnValue.equals("#TYPE4#")) {
            returnValue = getMessageOutput();
//            returnValue = "#MESSAGESPEC#Message#MESSAGESPEC#";
        } else if (returnValue.equals("#TYPE2#") || returnValue.equals("#TYPE1#")) {
            returnValue = "#MESSAGESPEC#Message#MESSAGESPEC#";
        } else if (returnValue.equals("#TYPE3#")) {
            returnValue = "#MESSAGESPEC#Message#MESSAGESPEC#";
        } else if (returnValue.equals("#TYPE5#")) {
            returnValue = "#MESSAGESPEC#Message#MESSAGESPEC#";
        } else if (returnValue.equals("#TYPE6#")) {
            returnValue = "#MESSAGESPEC#Message#MESSAGESPEC#";
        } else if (returnValue.equals("#TYPE7#")) {
            returnValue = "#MESSAGESPEC#Message#MESSAGESPEC#";
        } else if (returnValue.equals("#TYPE8#")) {
            returnValue = "#MESSAGESPEC#Message#MESSAGESPEC#";
        }

        return returnValue;
    }

    private String getInvalidDataDescription(String inputData, String errorType) {
        String returnValue = inputData;
        String[] values = inputData.split(",");
        try {
            Integer errorTypeIndex = new Integer(errorType.trim());

            if (values.length >= errorTypeIndex) {
                returnValue = values[errorTypeIndex - 1];
            } else if (values.length > 0) {
                returnValue = values[values.length - 1];
            }

            if (returnValue.equals("#TYPE4#")) {
                returnValue = "  The variable value is set to a string that is not valid XML.";
//            returnValue = "#MESSAGESPEC#Message#MESSAGESPEC#";
            } else if (returnValue.equals("#TYPE2#") || returnValue.equals("#TYPE1#")) {
                returnValue = "  The message name within this variable is set to an erroneous name.  The string ErrorAddOn has been appended to the correct message name.";
            } else if (returnValue.equals("#TYPE3#")) {
                returnValue = "  The variable value is set to a message containing no internal details.";
            } else if (returnValue.equals("#TYPE5#")) {
                returnValue = "  The element values within this message have all be set to &#%& in order to trigger an out of range value response.";
            } else if (returnValue.equals("#TYPE7#")) {
                returnValue = "  The authentication elements within the message are set to values that should trigger an authentication error response.";
            } else {
                returnValue = "";
            }
        } catch (Exception ex) {
            returnValue = "";
        }
        return returnValue;
    }
    
    private void addMessage(Writer out) {
        //RR-OC, RR-EC?, Sub-OC, Pub-EC
        // Type 2 error: add 1 to the end of tmdd:messagename
        // Type 3 error: remove something mandatory?
        // Type 4 error: bad xml -- soap encoded?
        // Type 5 error: out of range value or enumeration
        String type = "#PARAMETER TYPE = String\n";
        String editable = "#EDITABLE = true\n";

        try {
            TMDDWSDL riWSDLInstance = NRTM_RTM_Design_Data.getInstance().getTmddDesign().getTmddWSDL();

            // Look for this dialog


            Map services = riWSDLInstance.getServiceNames();
            //           System.out.println(services);

            Iterator serviceIterator = services.keySet().iterator();
            while (serviceIterator.hasNext()) {
                String theService = (String) serviceIterator.next();
                QName qService = (QName) services.get(theService);
                System.out.println("Checking the Service : " + theService + " for dialog " + dialog);
//                if (theService.equalsIgnoreCase("tmddOCSoapHttpService")) {
                // Found the selected service in the WSDL

                Map ports = riWSDLInstance.getServicePortNames(qService);
                Iterator portIterator = ports.keySet().iterator();
                while (portIterator.hasNext()) {
                    String thePort = (String) portIterator.next();
                    BindingOperation theBindingOperation = riWSDLInstance.getServicePortBindingOperation(qService, thePort, dialog);
                    if (theBindingOperation != null) {
                        System.out.println("The BINDING NAME = " + theBindingOperation.getName());

                        OperationMessageBuilder messageBuilder = new OperationMessageBuilder(riWSDLInstance);
                        System.out.println("\n\n\n NEW INPUT MESSAGE by BINDING OPERATION");
                        SOAPEncoder theEncoder = new SOAPEncoder("SoapType");
                        MessageSpecificationProcessor newProcessor = new MessageSpecificationProcessor();
                        MessageSpecification newSpecification = null;
                        String messageName = "";
                        if ((((dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX) && (mode.equals(TMDDParameters.TMDD_EC_MODE))) || dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) && (mode.equals(TMDDParameters.TMDD_EC_MODE)))
                                || (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX) && (mode.equals(TMDDParameters.TMDD_OC_Mode)))) {
//                                byte[] returnMsg = theEncoder.encode(messageBuilder.buildSoapMessageFromInput(theBindingOperation, true, true).getBytes());
                            byte[] returnMsg = messageBuilder.buildSoapMessageFromInput(theBindingOperation, true, true).getBytes();
                            newSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                            System.out.println(new String(returnMsg));
                            messageName = theBindingOperation.getOperation().getInput().getMessage().getPart("message").getElementName().getLocalPart();
                        } else if (dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)) {
//                                byte[] returnMsg = theEncoder.encode(messageBuilder.buildEmptyMessageForFault().getBytes());
//                                newSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
//                                System.out.println(new String(returnMsg));
                            //                              byte[] returnMsg = theEncoder.encode(messageBuilder.buildMessageFromOutput(theBindingOperation, true, true).getBytes());
                            byte[] returnMsg = messageBuilder.buildMessageFromOutput(theBindingOperation, true, true).getBytes();
                            newSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                            System.out.println(new String(returnMsg));
                            messageName = theBindingOperation.getOperation().getOutput().getMessage().getPart("message").getElementName().getLocalPart();
                        }

                        if (newSpecification != null) {

                            TMDDDatabase theDatabase = new TMDDDatabase();
                            theDatabase.connectToDatabase();

                            ResultSet procedureVariableRS = theDatabase.queryReturnRS("SELECT * FROM TMDDDataValuesLookupTable where NeedNumber =" + this.needID + " and Message = '" + messageName + "'");

                            try {
                                while (procedureVariableRS.next()) {
                                    String parameterName = procedureVariableRS.getString("ParameterName");
                                    String parameterValue = procedureVariableRS.getString("Value");
                                    String requirementID = procedureVariableRS.getString("RequirementID");
                                    //               for (String thisRequirementID : this.relatedRequirements){
                                    //                   if (thisRequirementID.equals(requirementID)){
                                    newSpecification.setElementValue(parameterName, parameterValue);
                                    //                    }
                                    //               }

                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            theDatabase.disconnectFromDatabase();


                            if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX) && mode.endsWith(TMDDParameters.TMDD_OC_Mode)) {
                                out.write("#GROUP NAME = SubResponseMessage\n");
                            } else if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX) && mode.endsWith(TMDDParameters.TMDD_EC_MODE)) {
                                out.write("#GROUP NAME = PubResponseMessage\n");
                            } else {
                                out.write("#GROUP NAME = Message\n");
                            }

                            HashMap<String, MessageDetailDesignMatcher> msgDetailMap = new HashMap<String, MessageDetailDesignMatcher>();
                            MessageDetailDesignMatcher msgMatch = null;
                            //                               MessageDetailDesignMatcher msgMatch = new MessageDetailDesignMatcher(String messageName);
                            for (String parameterEntry : newSpecification.getMessageSpec()) {
                                out.write(type);
                                out.write(editable);

                                String message = "";
                                String parameter = "";
                                if (parameterEntry.contains(".")) {
                                    message = parameterEntry.substring(parameterEntry.indexOf(":") + 1, parameterEntry.indexOf("."));
                                    String specPart = parameterEntry.substring(0, parameterEntry.indexOf("="));
                                    parameter = specPart.substring(specPart.lastIndexOf(".") + 1).trim();
                                } else {
                                    parameter = parameterEntry.substring(0, parameterEntry.indexOf("=")).trim();
                                    message = parameterEntry.substring(parameterEntry.indexOf(":") + 1, parameterEntry.indexOf("=")).trim();
                                }

                                if (msgDetailMap.containsKey(message)) {
                                    msgMatch = msgDetailMap.get(message);
                                } else {
                                    msgMatch = new MessageDetailDesignMatcher(message);
                                    msgDetailMap.put(message, msgMatch);
                                }

                                String documentationString = "";
                                if (msgMatch.getMatchesForElementName(parameter,Integer.parseInt(this.needID)).size() > 0) {
                                    MessageDetailDesignElement theElement = msgMatch.getMatchesForElementName(parameter,Integer.parseInt(this.needID)).get(0);
                                    documentationString = parameterEntry.substring(0, parameterEntry.indexOf("=")) + " BaseType=" + theElement.getBaseType() + "  ParentType= " + theElement.getParentType();
                                } else {
                                    documentationString = parameterEntry.substring(0, parameterEntry.indexOf("="));
                                }

                                out.write("#DOCUMENTATION = " + documentationString + "\n");
                                out.write(parameterEntry + "\n\n");
                            }

                        }

                    }
                }
            }



            serviceIterator = services.keySet().iterator();
            while (serviceIterator.hasNext()) {
                String theService = (String) serviceIterator.next();
                QName qService = (QName) services.get(theService);

                String relatedDialog = "";
                if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                    
            try{
                relatedDialog = TMDDSubPubMapping.getInstance().getSubscriptionDialog(dialog, Integer.parseInt(needID));
            } catch (Exception ex){
                ex.printStackTrace();
                     relatedDialog = dialog.replace(TMDDParameters.TMDD_PUBLICATION_SUFFIX, TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX);
            }
                } else {
            try{
                relatedDialog = TMDDSubPubMapping.getInstance().getPublicationDialog(dialog, Integer.parseInt(needID));
            } catch (Exception ex){
                ex.printStackTrace();
                    relatedDialog = dialog.replace(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX, TMDDParameters.TMDD_PUBLICATION_SUFFIX);
            }
                }

                System.out.println("Checking the Service : " + theService + " for dialog " + relatedDialog);
//                if (theService.equalsIgnoreCase("tmddOCSoapHttpService")) {
                // Found the selected service in the WSDL

                Map ports = riWSDLInstance.getServicePortNames(qService);
                Iterator portIterator = ports.keySet().iterator();
                while (portIterator.hasNext()) {
                    String thePort = (String) portIterator.next();
                    BindingOperation theBindingOperation = riWSDLInstance.getServicePortBindingOperation(qService, thePort, relatedDialog);
                    if (theBindingOperation != null) {
                        System.out.println("The BINDING NAME = " + theBindingOperation.getName());

                        OperationMessageBuilder messageBuilder = new OperationMessageBuilder(riWSDLInstance);
                        System.out.println("\n\n\n NEW INPUT MESSAGE by BINDING OPERATION");
                        SOAPEncoder theEncoder = new SOAPEncoder("SoapType");
                        MessageSpecificationProcessor newProcessor = new MessageSpecificationProcessor();

                        MessageSpecification responseSpecification = null;
                        String messageName = "";
                        if ((((dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX) && (mode.equals(TMDDParameters.TMDD_OC_Mode))) || dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) && (mode.equals(TMDDParameters.TMDD_EC_MODE)))
                                || (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX) && (mode.equals(TMDDParameters.TMDD_OC_Mode)))) {
                            //                              byte[] returnMsg = theEncoder.encode(messageBuilder.buildSoapMessageFromOutput(theBindingOperation, true, true).getBytes());
                            byte[] returnMsg = messageBuilder.buildSoapMessageFromOutput(theBindingOperation, true, true).getBytes();
                            responseSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                            System.out.println(new String(returnMsg));
                            messageName = theBindingOperation.getOperation().getInput().getMessage().getPart("message").getElementName().getLocalPart();
                        } else if (!dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)) {
//                                byte[] returnMsg = theEncoder.encode(messageBuilder.buildEmptyMessageForFault().getBytes());
//                                newSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
//                                System.out.println(new String(returnMsg));
                            //                              byte[] returnMsg = theEncoder.encode(messageBuilder.buildMessageFromInput(theBindingOperation, true, true).getBytes());
                            byte[] returnMsg = messageBuilder.buildMessageFromInput(theBindingOperation, true, true).getBytes();
                            responseSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                            System.out.println(new String(returnMsg));
                            messageName = theBindingOperation.getOperation().getInput().getMessage().getPart("message").getElementName().getLocalPart();

                        }

                        if (responseSpecification != null) {

                            TMDDDatabase theDatabase = new TMDDDatabase();
                            theDatabase.connectToDatabase();

                            ResultSet procedureVariableRS = theDatabase.queryReturnRS("SELECT * FROM TMDDDataValuesLookupTable where NeedNumber =" + this.needID + " and Message = '" + messageName + "'");

                            try {
                                while (procedureVariableRS.next()) {
                                    String parameterName = procedureVariableRS.getString("ParameterName");
                                    String parameterValue = procedureVariableRS.getString("Value");
                                    String requirementID = procedureVariableRS.getString("RequirementID");
//                for (String thisRequirementID : this.relatedRequirements){
//                    if (thisRequirementID.equals(requirementID)){
                                    responseSpecification.setElementValue(parameterName, parameterValue);
//                     }
//                }

                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            theDatabase.disconnectFromDatabase();



                            if (relatedDialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX) && mode.endsWith(TMDDParameters.TMDD_OC_Mode)) {
                                out.write("#GROUP NAME = SubResponseMessage\n");
                            } else if (relatedDialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX) && mode.endsWith(TMDDParameters.TMDD_EC_MODE)) {
                                out.write("#GROUP NAME = PubResponseMessage\n");
                            } else {
                                out.write("#GROUP NAME = Message\n");
                            }

                            HashMap<String, MessageDetailDesignMatcher> msgDetailMap = new HashMap<String, MessageDetailDesignMatcher>();
                            MessageDetailDesignMatcher msgMatch = null;

                            for (String parameterEntry : responseSpecification.getMessageSpec()) {
                                out.write(type);
                                out.write(editable);

                                String message = "";
                                String parameter = "";
                                if (parameterEntry.contains(".")) {
                                    message = parameterEntry.substring(parameterEntry.indexOf(":") + 1, parameterEntry.indexOf("."));
                                    String specPart = parameterEntry.substring(0, parameterEntry.indexOf("="));
                                    parameter = specPart.substring(specPart.lastIndexOf(".") + 1).trim();
                                } else {
                                    parameter = parameterEntry.substring(0, parameterEntry.indexOf("=")).trim();
                                    message = parameterEntry.substring(parameterEntry.indexOf(":") + 1, parameterEntry.indexOf("=")).trim();
                                }

                                if (msgDetailMap.containsKey(message)) {
                                    msgMatch = msgDetailMap.get(message);
                                } else {
                                    msgMatch = new MessageDetailDesignMatcher(message);
                                    msgDetailMap.put(message, msgMatch);
                                }

                                String documentationString = "";
                                if (msgMatch.getMatchesForElementName(parameter,Integer.parseInt(this.needID)).size() > 0) {
                                    MessageDetailDesignElement theElement = msgMatch.getMatchesForElementName(parameter,Integer.parseInt(this.needID)).get(0);
                                    documentationString = parameterEntry.substring(0, parameterEntry.indexOf("=")) + " BaseType=" + theElement.getBaseType() + "  ParentType= " + theElement.getParentType();
                                } else {
                                    documentationString = parameterEntry.substring(0, parameterEntry.indexOf("="));
                                }

                                out.write("#DOCUMENTATION = " + documentationString + "\n");
                                out.write(parameterEntry + "\n\n");
                            }

                        }
                    }
                }
            }

        } catch (Exception ex) {
        }
    }

    private void addValues(Writer out) {
        //RR-OC, RR-EC?, Sub-OC, Pub-EC
        // Type 2 error: add 1 to the end of tmdd:messagename
        // Type 3 error: remove something mandatory?
        // Type 4 error: bad xml -- soap encoded?
        // Type 5 error: out of range value or enumeration
        String type = "#PARAMETER TYPE = String\n";
        String editable = "#EDITABLE = false\n";

        try {
            TMDDWSDL riWSDLInstance = NRTM_RTM_Design_Data.getInstance().getTmddDesign().getTmddWSDL();

            // Look for this dialog


            Map services = riWSDLInstance.getServiceNames();
            //           System.out.println(services);

            Iterator serviceIterator = services.keySet().iterator();
            while (serviceIterator.hasNext()) {
                String theService = (String) serviceIterator.next();
                QName qService = (QName) services.get(theService);
                System.out.println("Checking the Service : " + theService + " for dialog " + dialog);
//                if (theService.equalsIgnoreCase("tmddOCSoapHttpService")) {
                // Found the selected service in the WSDL

                Map ports = riWSDLInstance.getServicePortNames(qService);
                Iterator portIterator = ports.keySet().iterator();
                while (portIterator.hasNext()) {
                    String thePort = (String) portIterator.next();
                    BindingOperation theBindingOperation = riWSDLInstance.getServicePortBindingOperation(qService, thePort, dialog);
                    if (theBindingOperation != null) {
                        System.out.println("The BINDING NAME = " + theBindingOperation.getName());
                        String messageName = "";
                        OperationMessageBuilder messageBuilder = new OperationMessageBuilder(riWSDLInstance);
                        System.out.println("\n\n\n NEW INPUT MESSAGE by BINDING OPERATION");
                        SOAPEncoder theEncoder = new SOAPEncoder("SoapType");
                        MessageSpecificationProcessor newProcessor = new MessageSpecificationProcessor();
                        MessageSpecification newSpecification = null;
                        if ((((dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX) && (mode.equals(TMDDParameters.TMDD_EC_MODE))) || dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) && (mode.equals(TMDDParameters.TMDD_EC_MODE)))
                                || (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX) && (mode.equals(TMDDParameters.TMDD_OC_Mode)))) {
                            messageName = theBindingOperation.getOperation().getOutput().getMessage().getPart("message").getElementName().getLocalPart();


//                                byte[] returnMsg = theEncoder.encode(messageBuilder.buildSoapMessageFromInput(theBindingOperation, true, true).getBytes());
//                                byte[] returnMsg = messageBuilder.buildSoapMessageFromInput(theBindingOperation, true, true).getBytes();
//                                newSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
//                                System.out.println(new String(returnMsg));
                        } else if (dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)) {
//                                byte[] returnMsg = theEncoder.encode(messageBuilder.buildEmptyMessageForFault().getBytes());
//                                newSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
//                                System.out.println(new String(returnMsg));
                            //                              byte[] returnMsg = theEncoder.encode(messageBuilder.buildMessageFromOutput(theBindingOperation, true, true).getBytes());
                            messageName = theBindingOperation.getOperation().getInput().getMessage().getPart("message").getElementName().getLocalPart();
//                                byte[] returnMsg = messageBuilder.buildMessageFromOutput(theBindingOperation, true, true).getBytes();
//                                newSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
//                                System.out.println(new String(returnMsg));

                        }

                        if (!messageName.isEmpty()) {
                            TMDDDatabase theDatabase = new TMDDDatabase();
                            theDatabase.connectToDatabase();

                            ResultSet procedureVariableRS = theDatabase.queryReturnRS("SELECT * FROM TMDDDataValuesLookupTable where NeedNumber =" + this.needID + " and Message = '" + messageName + "'");

                            try {
                                while (procedureVariableRS.next()) {
                                    String parameterName = procedureVariableRS.getString("ParameterName");
                                    String parameterValue = procedureVariableRS.getString("Value");
                                    String requirementID = procedureVariableRS.getString("RequirementID");
                                    for (String thisRequirementID : this.relatedRequirements) {
                                        if (thisRequirementID.equals(requirementID)) {
                                            MessageSpecificationItem msgSpecItem = new MessageSpecificationItem(parameterName + " = " + parameterValue);
                                            ArrayList<MessageSpecificationItem> msgArray = new ArrayList<MessageSpecificationItem>();
                                            msgArray.add(msgSpecItem);
                                            try {
                                                String valueSpecItem = MessageValueTester.getValueSpecFromMessageSpec(1, msgArray);
                                                out.write(type);
                                                out.write(editable);
                                                out.write("#DOCUMENTATION = " + "Testing for Requirement " + thisRequirementID + " in message " + messageName + "\n");
                                                out.write(valueSpecItem + "\n\n");
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    }

                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            theDatabase.disconnectFromDatabase();
                        }



                    }
                }
            }




        } catch (Exception ex) {
        }

    }

    private String getMessageOutput() {
        return getMessageOutput("", "");
    }

    private String getMessageOutput(String path, String filename) {
        String messageOut = "";
        //RR-OC, RR-EC?, Sub-OC, Pub-EC
        // Type 2 error: add 1 to the end of tmdd:messagename
        // Type 3 error: remove something mandatory?
        // Type 4 error: bad xml -- soap encoded?
        // Type 5 error: out of range value or enumeration
        String type = "#PARAMETER TYPE = String\n";
        String editable = "#EDITABLE = true\n";

        try {
            TMDDWSDL riWSDLInstance = NRTM_RTM_Design_Data.getInstance().getTmddDesign().getTmddWSDL();

            // Look for this dialog


            Map services = riWSDLInstance.getServiceNames();
            //           System.out.println(services);

            Iterator serviceIterator = services.keySet().iterator();
            while (serviceIterator.hasNext()) {
                String theService = (String) serviceIterator.next();
                QName qService = (QName) services.get(theService);
                System.out.println("Checking the Service : " + theService + " for dialog " + dialog);
//                if (theService.equalsIgnoreCase("tmddOCSoapHttpService")) {
                // Found the selected service in the WSDL

                Map ports = riWSDLInstance.getServicePortNames(qService);
                Iterator portIterator = ports.keySet().iterator();
                while (portIterator.hasNext()) {
                    String thePort = (String) portIterator.next();
                    BindingOperation theBindingOperation = riWSDLInstance.getServicePortBindingOperation(qService, thePort, dialog);
                    if (theBindingOperation != null) {
                        System.out.println("The BINDING NAME = " + theBindingOperation.getName());

                        OperationMessageBuilder messageBuilder = new OperationMessageBuilder(riWSDLInstance);
                        System.out.println("\n\n\n NEW INPUT MESSAGE by BINDING OPERATION");
                        MessageSpecificationProcessor newProcessor = new MessageSpecificationProcessor();
                        MessageSpecification newSpecification = null;
                        String messageName = "";
                        if ((((dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX) && (mode.equals(TMDDParameters.TMDD_EC_MODE))) || dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) && (mode.equals(TMDDParameters.TMDD_EC_MODE)))
                                || (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX) && (mode.equals(TMDDParameters.TMDD_OC_Mode)))) {
//                                byte[] returnMsg = theEncoder.encode(messageBuilder.buildSoapMessageFromInput(theBindingOperation, true, true).getBytes());
                            messageName = theBindingOperation.getOperation().getInput().getMessage().getPart("message").getElementName().getLocalPart();
                            byte[] returnMsg = modifyMessageValues(messageBuilder.buildSoapMessageFromInput(theBindingOperation, true, true)).getBytes();
                            if (!(path.equals("") && (filename.equals("")))) {
                                toXMLSampleTestCaseFile(path, filename, new String(returnMsg));
                            } else {
                                System.out.println("AbstractTMDDTestCase::getMessageOutput  Path = " + path + " and filename= " + filename);
                                toXMLSampleTestCaseFile("c:\\tmddv303c\\data", this.testCaseID + "-" + mode, new String(returnMsg));
                            }
                            if (this.getTestCaseID().contains("InValid") && this.invalidCode.equals("4")) {
//                                    MessageSpecification tmpSpec = newProcessor.convertXMLtoMessageSpecification(returnMsg);
//                                    ArrayList<String> messageDetail = tmpSpec.getMessageSpec();                                    
//                                    String replaceItem = messageDetail.get(messageDetail.size() -1);
//                                    replaceItem = replaceItem.substring(0, replaceItem.lastIndexOf("=")) + "= <badTag>";
//                                    messageDetail.set(messageDetail.size()-1, replaceItem);
//                                    for (String thisItem : messageDetail) {
//                                        if (newSpecification == null){
//                                            newSpecification = new MessageSpecification(thisItem);                                            
//                                        } else {
//                                            newSpecification.addElementToSpec(thisItem);                                                                                        
//                                        }
//                                    }
                                messageOut = trimMessage(new String(returnMsg));
                                messageOut = messageOut.replace("/mes:" + messageName, "mes:"+messageName);
                            } else {
                                if (this.getTestCaseID().contains("InValid") && this.invalidCode.equals("2")) {
                                    MessageSpecification tmpSpec = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                                    ArrayList<String> messageDetail = tmpSpec.getMessageSpec();
                                    for (String thisItem : messageDetail) {
                                        if (newSpecification == null){
                                            newSpecification = new MessageSpecification(thisItem.replace(messageName, messageName + "ErrorAddOn"));                                            
                                        } else {
                                            newSpecification.addElementToSpec(thisItem.replace(messageName, messageName + "ErrorAddOn"));                                                                                        
                                        }
                                    }
                                } else if (this.getTestCaseID().contains("InValid") && this.invalidCode.equals("3")) {
                                    MessageSpecification tmpSpec = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                                    newSpecification = new MessageSpecification(tmpSpec.getMessageSpec().get(0));
                                } else if (this.getTestCaseID().contains("InValid") && this.invalidCode.equals("5")) {
                                    MessageSpecification tmpSpec = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                                    ArrayList<String> messageDetail = tmpSpec.getMessageSpec();                                    
                                    for (String thisItem : messageDetail) {
                                        String newElement = thisItem.substring(0, thisItem.lastIndexOf("=")) + "= &#%&";
                                        if (newSpecification == null){
                                            newSpecification = new MessageSpecification(newElement);                                            
                                        } else {
                                            newSpecification.addElementToSpec(newElement);                                                                                        
                                        }
                                    }
                                } else {
                                    newSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                                    System.out.println(new String(returnMsg));

                                }
                            }
                        } else if (dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)) {
//                                byte[] returnMsg = theEncoder.encode(messageBuilder.buildEmptyMessageForFault().getBytes());
//                                newSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
//                                System.out.println(new String(returnMsg));
                            //                              byte[] returnMsg = theEncoder.encode(messageBuilder.buildMessageFromOutput(theBindingOperation, true, true).getBytes());
                            messageName = theBindingOperation.getOperation().getOutput().getMessage().getPart("message").getElementName().getLocalPart();
                            byte[] returnMsg = modifyMessageValues(messageBuilder.buildSoapMessageFromOutput(theBindingOperation, true, true)).getBytes();
                            if (!(path.equals("") && (filename.equals("")))) {
                                toXMLSampleTestCaseFile(path, filename, new String(returnMsg));
                            } else {
                                System.out.println("AbstractTMDDTestCase::getMessageOutput  Path = " + path + " and filename= " + filename);
                                toXMLSampleTestCaseFile("c:\\tmddv303c\\data", this.testCaseID + "-" + mode, new String(returnMsg));
                            }
                            if (this.getTestCaseID().contains("InValid") && this.invalidCode.equals("4")) {
//                                    MessageSpecification tmpSpec = newProcessor.convertXMLtoMessageSpecification(returnMsg);
//                                    ArrayList<String> messageDetail = tmpSpec.getMessageSpec();                                    
//                                    String replaceItem = messageDetail.get(messageDetail.size() -1);
//                                    replaceItem = replaceItem.substring(0, replaceItem.lastIndexOf("=")) + "= <badTag>";
//                                    messageDetail.set(messageDetail.size()-1, replaceItem);
//                                    for (String thisItem : messageDetail) {
//                                        if (newSpecification == null){
//                                            newSpecification = new MessageSpecification(thisItem);                                            
//                                        } else {
//                                            newSpecification.addElementToSpec(thisItem);                                                                                        
//                                        }
//                                    }
                                messageOut = trimMessage(new String(returnMsg));
                                messageOut = messageOut.replace("/mes:" + messageName, "mes:"+messageName);
                            } else {
                                if (this.getTestCaseID().contains("InValid") && this.invalidCode.equals("2")) {
                                    MessageSpecification tmpSpec = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                                    ArrayList<String> messageDetail = tmpSpec.getMessageSpec();
                                    for (String thisItem : messageDetail) {
                                        if (newSpecification == null){
                                            newSpecification = new MessageSpecification(thisItem.replace(messageName, messageName + "ErrorAddOn"));                                                                                        
                                        } else {
                                            newSpecification.addElementToSpec(thisItem.replace(messageName, messageName + "ErrorAddOn"));                                            
                                        }
                                    }
                                } else if (this.getTestCaseID().contains("InValid") && this.invalidCode.equals("3")) {
                                    MessageSpecification tmpSpec = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                                    newSpecification = new MessageSpecification(tmpSpec.getMessageSpec().get(0));
                                } else if (this.getTestCaseID().contains("InValid") && this.invalidCode.equals("5")) {
                                    MessageSpecification tmpSpec = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                                    ArrayList<String> messageDetail = tmpSpec.getMessageSpec();                                    
                                    for (String thisItem : messageDetail) {
                                        String newElement = thisItem.substring(0, thisItem.lastIndexOf("=")) + "= &#%&";
                                        if (newSpecification == null){
                                            newSpecification = new MessageSpecification(newElement);                                            
                                        } else {
                                            newSpecification.addElementToSpec(newElement);                                                                                        
                                        }
                                    }
                                } else {
                                    newSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                                    System.out.println(new String(returnMsg));

                                }
                            }
                        }

                        if (newSpecification != null) {

                            TMDDDatabase theDatabase = new TMDDDatabase();
                            theDatabase.connectToDatabase();

                            ResultSet procedureVariableRS = theDatabase.queryReturnRS("SELECT * FROM TMDDDataValuesLookupTable where NeedNumber =" + this.needID + " and Message = '" + messageName + "'");

                            try {
                                while (procedureVariableRS.next()) {
                                    String parameterName = procedureVariableRS.getString("ParameterName");
                                    String parameterValue = procedureVariableRS.getString("Value");
                                    String requirementID = procedureVariableRS.getString("RequirementID");
                                    //               for (String thisRequirementID : this.relatedRequirements){
                                    //                   if (thisRequirementID.equals(requirementID)){
                                    newSpecification.setElementValue(parameterName, parameterValue);
                                    //                    }
                                    //               }

                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            theDatabase.disconnectFromDatabase();


                            if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX) && mode.endsWith(TMDDParameters.TMDD_OC_Mode)) {
                                messageOut = messageOut.concat("#GROUP NAME = SubResponseMessage\n");
                            } else if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX) && mode.endsWith(TMDDParameters.TMDD_EC_MODE)) {
                                messageOut = messageOut.concat("#GROUP NAME = PubResponseMessage\n");
                            } else {
                                messageOut = messageOut.concat("#GROUP NAME = Message\n");
                            }

                            HashMap<String, MessageDetailDesignMatcher> msgDetailMap = new HashMap<String, MessageDetailDesignMatcher>();
                            MessageDetailDesignMatcher msgMatch = null;
                            //                               MessageDetailDesignMatcher msgMatch = new MessageDetailDesignMatcher(String messageName);
                            for (String parameterEntry : newSpecification.getMessageSpec()) {
                                messageOut = messageOut.concat(type);
                                messageOut = messageOut.concat(editable);

                                String message = "";
                                String parameter = "";
                                if (parameterEntry.contains(".")) {
                                    message = parameterEntry.substring(parameterEntry.indexOf(":") + 1, parameterEntry.indexOf("."));
                                    String specPart = parameterEntry.substring(0, parameterEntry.indexOf("="));
                                    parameter = specPart.substring(specPart.lastIndexOf(".") + 1).trim();
                                } else {
                                    parameter = parameterEntry.substring(0, parameterEntry.indexOf("=")).trim();
                                    message = parameterEntry.substring(parameterEntry.indexOf(":") + 1, parameterEntry.indexOf("=")).trim();
                                }

                                if (msgDetailMap.containsKey(message)) {
                                    msgMatch = msgDetailMap.get(message);
                                } else {
                                    msgMatch = new MessageDetailDesignMatcher(message);
                                    msgDetailMap.put(message, msgMatch);
                                }

                                String documentationString = "";
                                if (msgMatch.getMatchesForElementName(parameter,Integer.parseInt(this.needID)).size() > 0) {
                                    MessageDetailDesignElement theElement = msgMatch.getMatchesForElementName(parameter,Integer.parseInt(this.needID)).get(0);
                                    documentationString = parameterEntry.substring(0, parameterEntry.indexOf("=")) + " BaseType=" + theElement.getBaseType() + "  ParentType= " + theElement.getParentType();
                                } else {
                                    documentationString = parameterEntry.substring(0, parameterEntry.indexOf("="));
                                }

                                messageOut = messageOut.concat("#DOCUMENTATION = " + documentationString + "\n");
                                messageOut = messageOut.concat(parameterEntry + "\n\n").replace("mes:", "tmdd:");
                            }

                        }

                    }
                }
            }



            serviceIterator = services.keySet().iterator();
            while (serviceIterator.hasNext()) {
                String theService = (String) serviceIterator.next();
                QName qService = (QName) services.get(theService);

                String relatedDialog = "";
                if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
            try{
                relatedDialog = TMDDSubPubMapping.getInstance().getSubscriptionDialog(dialog, Integer.parseInt(needID));
            } catch (Exception ex){
                ex.printStackTrace();
                     relatedDialog = dialog.replace(TMDDParameters.TMDD_PUBLICATION_SUFFIX, TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX);
            }
//                    relatedDialog = dialog.replace(TMDDParameters.TMDD_PUBLICATION_SUFFIX, TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX);
                } else {
            try{
                relatedDialog = TMDDSubPubMapping.getInstance().getPublicationDialog(dialog, Integer.parseInt(needID));
            } catch (Exception ex){
                ex.printStackTrace();
                relatedDialog = dialog.replace(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX, TMDDParameters.TMDD_PUBLICATION_SUFFIX);
            }
//                    relatedDialog = dialog.replace(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX, TMDDParameters.TMDD_PUBLICATION_SUFFIX);
                }

                System.out.println("Checking the Service : " + theService + " for dialog " + relatedDialog);
//                if (theService.equalsIgnoreCase("tmddOCSoapHttpService")) {
                // Found the selected service in the WSDL

                Map ports = riWSDLInstance.getServicePortNames(qService);
                Iterator portIterator = ports.keySet().iterator();
                while (portIterator.hasNext()) {
                    String thePort = (String) portIterator.next();
                    BindingOperation theBindingOperation = riWSDLInstance.getServicePortBindingOperation(qService, thePort, relatedDialog);
                    if (theBindingOperation != null) {
                        System.out.println("The BINDING NAME = " + theBindingOperation.getName());

                        OperationMessageBuilder messageBuilder = new OperationMessageBuilder(riWSDLInstance);
                        System.out.println("\n\n\n NEW INPUT MESSAGE by BINDING OPERATION");
                        SOAPEncoder theEncoder = new SOAPEncoder("SoapType");
                        MessageSpecificationProcessor newProcessor = new MessageSpecificationProcessor();

                        MessageSpecification responseSpecification = null;
                        String messageName = "";
                        if ((((dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX) && (mode.equals(TMDDParameters.TMDD_OC_Mode))) || dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) && (mode.equals(TMDDParameters.TMDD_EC_MODE)))
                                || (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX) && (mode.equals(TMDDParameters.TMDD_OC_Mode)))) {
                            //                              byte[] returnMsg = theEncoder.encode(messageBuilder.buildSoapMessageFromOutput(theBindingOperation, true, true).getBytes());
                            messageName = theBindingOperation.getOperation().getInput().getMessage().getPart("message").getElementName().getLocalPart();
                            byte[] returnMsg = modifyMessageValues(messageBuilder.buildSoapMessageFromOutput(theBindingOperation, true, true)).getBytes();
                            if (this.getTestCaseID().contains("InValid") && this.invalidCode.equals("4")) {
//                                          MessageSpecification tmpSpec = newProcessor.convertXMLtoMessageSpecification(returnMsg);
//                                    ArrayList<String> messageDetail = tmpSpec.getMessageSpec();                                    
//                                    String replaceItem = messageDetail.get(messageDetail.size() -1);
//                                    replaceItem = replaceItem.substring(0, replaceItem.lastIndexOf("=")) + "= <badTag>";
//                                    messageDetail.set(messageDetail.size()-1, replaceItem);
//                                    for (String thisItem : messageDetail) {
//                                        if (responseSpecification == null){
//                                            responseSpecification = new MessageSpecification(thisItem);                                            
//                                        } else {
//                                            responseSpecification.addElementToSpec(thisItem);                                                                                        
//                                        }
//                                    }
                          if (!dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)){
                            messageOut = trimMessage(new String(returnMsg));
                            messageOut = messageOut.replace("/mes:" + messageName, "mes:"+messageName);
                          }
                            } else {
                                if (this.getTestCaseID().contains("InValid") && this.invalidCode.equals("2")) {
                                    MessageSpecification tmpSpec = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                                    ArrayList<String> messageDetail = tmpSpec.getMessageSpec();
                                    for (String thisItem : messageDetail) {
                                        if (responseSpecification == null){
                                            responseSpecification = new MessageSpecification(thisItem.replace(messageName, messageName + "ErrorAddOn"));
                                        } else {      
                                            responseSpecification.addElementToSpec(thisItem.replace(messageName, messageName + "ErrorAddOn"));
                                        }
                                    }
                                } else if (this.getTestCaseID().contains("InValid") && this.invalidCode.equals("3")) {
                                    MessageSpecification tmpSpec = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                                    responseSpecification = new MessageSpecification(tmpSpec.getMessageSpec().get(0));
                                    
                                } else if (this.getTestCaseID().contains("InValid") && this.invalidCode.equals("5")) {
                                    MessageSpecification tmpSpec = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                                    ArrayList<String> messageDetail = tmpSpec.getMessageSpec();                                    
                                    for (String thisItem : messageDetail) {
                                        String newElement = thisItem.substring(0, thisItem.lastIndexOf("=")) + "= &#%&";
                                        if (responseSpecification == null){
                                            responseSpecification = new MessageSpecification(newElement);                                            
                                        } else {
                                            responseSpecification.addElementToSpec(newElement);                                                                                        
                                        }
                                    }
                                } else {
                                    responseSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                                    System.out.println(new String(returnMsg));

                                }
                            }
                        } else if (!dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)) {
//                                byte[] returnMsg = theEncoder.encode(messageBuilder.buildEmptyMessageForFault().getBytes());
//                                newSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);
//                                System.out.println(new String(returnMsg));
                            //                              byte[] returnMsg = theEncoder.encode(messageBuilder.buildMessageFromInput(theBindingOperation, true, true).getBytes());
                            messageName = theBindingOperation.getOperation().getInput().getMessage().getPart("message").getElementName().getLocalPart();
                            byte[] returnMsg = modifyMessageValues(messageBuilder.buildSoapMessageFromInput(theBindingOperation, true, true)).getBytes();
                            if (this.getTestCaseID().contains("InValid") && this.invalidCode.equals("4")) {
//                                          MessageSpecification tmpSpec = newProcessor.convertXMLtoMessageSpecification(returnMsg);
//                                    ArrayList<String> messageDetail = tmpSpec.getMessageSpec();                                    
//                                    String replaceItem = messageDetail.get(messageDetail.size() -1);
//                                    replaceItem = replaceItem.substring(0, replaceItem.lastIndexOf("=")) + "= <badTag>";
//                                    messageDetail.set(messageDetail.size()-1, replaceItem);
//                                    for (String thisItem : messageDetail) {
//                                        if (responseSpecification == null){
//                                            responseSpecification = new MessageSpecification(thisItem);                                            
//                                        } else {
//                                            responseSpecification.addElementToSpec(thisItem);                                                                                        
//                                        }
//                                    }
                                messageOut = trimMessage(new String(returnMsg));
                                messageOut = messageOut.replace("/mes:" + messageName, "mes:"+messageName);
                            } else {
                                if (this.getTestCaseID().contains("InValid") && this.invalidCode.equals("2")) {
                                    MessageSpecification tmpSpec = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                                    ArrayList<String> messageDetail = tmpSpec.getMessageSpec();
                                    for (String thisItem : messageDetail) {
                                        if (responseSpecification == null){
                                            responseSpecification = new MessageSpecification(thisItem.replace(messageName, messageName + "ErrorAddOn"));
                                        } else {
                                            responseSpecification.addElementToSpec(thisItem.replace(messageName, messageName + "ErrorAddOn"));                                            
                                        }
                                    }
                                } else if (this.getTestCaseID().contains("InValid") && this.invalidCode.equals("3")) {
                                    MessageSpecification tmpSpec = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                                    responseSpecification = new MessageSpecification(tmpSpec.getMessageSpec().get(0));

                                } else if (this.getTestCaseID().contains("InValid") && this.invalidCode.equals("5")) {
                                    MessageSpecification tmpSpec = newProcessor.convertXMLtoMessageSpecification(returnMsg);
                                    ArrayList<String> messageDetail = tmpSpec.getMessageSpec();                                    
                                    for (String thisItem : messageDetail) {
                                        String newElement = thisItem.substring(0, thisItem.lastIndexOf("=")) + "= &#%&";
                                        if (responseSpecification == null){
                                            responseSpecification = new MessageSpecification(newElement);                                            
                                        } else {
                                            responseSpecification.addElementToSpec(newElement);                                                                                        
                                        }
                                    }
                                } else {
                                    responseSpecification = newProcessor.convertXMLtoMessageSpecification(returnMsg);

                                    System.out.println(new String(returnMsg));

                                }
                            }

                        }

                        if (responseSpecification != null) {

                            TMDDDatabase theDatabase = new TMDDDatabase();
                            theDatabase.connectToDatabase();

                            ResultSet procedureVariableRS = theDatabase.queryReturnRS("SELECT * FROM TMDDDataValuesLookupTable where NeedNumber =" + this.needID + " and Message = '" + messageName + "'");

                            try {
                                while (procedureVariableRS.next()) {
                                    String parameterName = procedureVariableRS.getString("ParameterName");
                                    String parameterValue = procedureVariableRS.getString("Value");
                                    String requirementID = procedureVariableRS.getString("RequirementID");
//                for (String thisRequirementID : this.relatedRequirements){
//                    if (thisRequirementID.equals(requirementID)){
                                    responseSpecification.setElementValue(parameterName, parameterValue);
//                     }
//                }

                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            theDatabase.disconnectFromDatabase();



                            if (relatedDialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX) && mode.endsWith(TMDDParameters.TMDD_OC_Mode)) {
                                messageOut = messageOut.concat("#GROUP NAME = SubResponseMessage\n");
                            } else if (relatedDialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX) && mode.endsWith(TMDDParameters.TMDD_EC_MODE)) {
                                messageOut = messageOut.concat("#GROUP NAME = PubResponseMessage\n");
                            } else {
                                messageOut = messageOut.concat("#GROUP NAME = Message\n");
                            }

                            HashMap<String, MessageDetailDesignMatcher> msgDetailMap = new HashMap<String, MessageDetailDesignMatcher>();
                            MessageDetailDesignMatcher msgMatch = null;

                            for (String parameterEntry : responseSpecification.getMessageSpec()) {
                                messageOut = messageOut.concat(type);
                                messageOut = messageOut.concat(editable);

                                String message = "";
                                String parameter = "";
                                if (parameterEntry.contains(".")) {
                                    message = parameterEntry.substring(parameterEntry.indexOf(":") + 1, parameterEntry.indexOf("."));
                                    String specPart = parameterEntry.substring(0, parameterEntry.indexOf("="));
                                    parameter = specPart.substring(specPart.lastIndexOf(".") + 1).trim();
                                } else {
                                    parameter = parameterEntry.substring(0, parameterEntry.indexOf("=")).trim();
                                    message = parameterEntry.substring(parameterEntry.indexOf(":") + 1, parameterEntry.indexOf("=")).trim();
                                }

                                if (msgDetailMap.containsKey(message)) {
                                    msgMatch = msgDetailMap.get(message);
                                } else {
                                    msgMatch = new MessageDetailDesignMatcher(message);
                                    msgDetailMap.put(message, msgMatch);
                                }

                                String documentationString = "";
                                if (msgMatch.getMatchesForElementName(parameter,Integer.parseInt(this.needID)).size() > 0) {
                                    MessageDetailDesignElement theElement = msgMatch.getMatchesForElementName(parameter,Integer.parseInt(this.needID)).get(0);
                                    documentationString = parameterEntry.substring(0, parameterEntry.indexOf("=")) + " BaseType=" + theElement.getBaseType() + "  ParentType= " + theElement.getParentType();
                                } else {
                                    documentationString = parameterEntry.substring(0, parameterEntry.indexOf("="));
                                }

                                messageOut = messageOut.concat("#DOCUMENTATION = " + documentationString + "\n");
                                messageOut = messageOut.concat(parameterEntry + "\n\n").replace("mes:", "tmdd:");
                            }

                        }
                    }
                }
            }

        } catch (Exception ex) {
        }
        return messageOut;
    }

    private String trimMessage(String message) {
        String result = message;

        XmlOptions theOptions = new XmlOptions();
        theOptions.setLoadStripWhitespace();
        theOptions.setLoadStripComments();


        try {
            XmlObject theMessage = XmlObject.Factory.parse(message, theOptions);
            result = theMessage.xmlText();
            int beginTypeIndex = result.indexOf(" xsi:type=");
            while (beginTypeIndex > 0) {
                int endIndex = result.indexOf("\"", beginTypeIndex + 12);
                if (endIndex > 0) {
                    String subString = result.substring(beginTypeIndex, endIndex + 1);
                    result = result.replace(subString, "");
                    beginTypeIndex = result.indexOf(" xsi:type=");
                } else {
                    beginTypeIndex = -1;
                }
            }
        } catch (Exception ex) {
        }
        return result;
    }

    private void toXMLSampleTestCaseFile(String path, String fileName, String message) {
        File newFile = new File(path + File.separatorChar + "XMLSamples" + File.separatorChar + fileName + ".xml");
        if (newFile.exists()) {
			try
			{
				Files.delete(Paths.get(newFile.getAbsolutePath()));
				newFile = new File(path + File.separatorChar + "XMLSamples" + File.separatorChar + fileName + ".xml");
			}
			catch (IOException oEx)
			{
				LogManager.getLogger(getClass()).error(oEx, oEx);
			}
        }

        try (Writer out = new OutputStreamWriter(new FileOutputStream(newFile), StandardCharsets.UTF_8))
		{
            MimeHeaders mhs = new MimeHeaders();
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(message.getBytes());
            SOAPMessage sm = MessageFactory.newInstance().createMessage(mhs, byteInputStream);

            if (!sm.getSOAPBody().hasFault()) {
                Iterator<Node> childElementIterator = sm.getSOAPBody().getChildElements();
                while (childElementIterator.hasNext()) {
                    Object theElement = childElementIterator.next();
                    if (theElement instanceof javax.xml.soap.Text) {
                        javax.xml.soap.Text thisText = (javax.xml.soap.Text) theElement;
                        System.out.println("AbstractTMDDTestCase::getMessage  Soap.Text = " + thisText.toString());
                    } else {
                        SOAPElement thisElement = (SOAPElement) theElement;

                        TransformerFactory tf = TransformerFactory.newInstance();
						tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
						tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
                        Transformer trans = tf.newTransformer();
                        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

                        StringWriter sw = new StringWriter();
                        trans.transform(new DOMSource(thisElement), new StreamResult(sw));
//                    trans.transform(new DOMSource(thisElement), new StreamResult(sw));                   

                        out.write(sw.toString());
                    }
                }

            } else {
                SOAPFault sf = sm.getSOAPBody().getFault();
                Iterator<Node> childElementIterator = sf.getDetail().getChildElements();
                while (childElementIterator.hasNext()) {
                    Object thisElement = childElementIterator.next();

                    TransformerFactory tf = TransformerFactory.newInstance();
					tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
					tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
                    Transformer trans = tf.newTransformer();
                    trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

                    StringWriter sw = new StringWriter();
                    trans.transform(new DOMSource((SOAPElement)thisElement), new StreamResult(sw));
                    out.write(sw.toString());

                }


            }
        } catch (Exception ex) {
            System.out.println("****** Writing Error:*******\n");
            ex.printStackTrace();
        } finally {
        }
    }

    public String modifyMessageValues(String message) {

        try {
            Map parameterMap = new HashMap<String, String>();
            String relatedDialog = "";
            if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
            try{
                relatedDialog = TMDDSubPubMapping.getInstance().getSubscriptionDialog(dialog, Integer.parseInt(needID));
            } catch (Exception ex){
                ex.printStackTrace();
                relatedDialog = dialog.replace(TMDDParameters.TMDD_PUBLICATION_SUFFIX, TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX);
            }
//                relatedDialog = dialog.replace(TMDDParameters.TMDD_PUBLICATION_SUFFIX, TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX);
            } else {
            try{
                relatedDialog = TMDDSubPubMapping.getInstance().getPublicationDialog(dialog, Integer.parseInt(needID));
            } catch (Exception ex){
                ex.printStackTrace();
                     relatedDialog = dialog.replace(TMDDParameters.TMDD_PUBLICATION_SUFFIX, TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX);
            }
//                relatedDialog = dialog.replace(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX, TMDDParameters.TMDD_PUBLICATION_SUFFIX);
            }

            String networkInformationType = "None Given";
            String deviceType = "None Given";
            String deviceInformationType = "None Given";

            TMDDDatabase theDatabase = new TMDDDatabase();
            theDatabase.connectToDatabase();

            ResultSet procedureVariableRS = theDatabase.queryReturnRS("SELECT * FROM TMDDDataValuesLookupTable where NeedNumber =" + this.needID);

            try {
                while (procedureVariableRS.next()) {
                    String elementName = procedureVariableRS.getString("Element");
                    if (elementName.equals("network-information-type")) {
                        networkInformationType = procedureVariableRS.getString("Value");
                    } else if (elementName.equals("device-type")) {
                        deviceType = procedureVariableRS.getString("Value");
                    } else if (elementName.equals("device-information-type")) {
                        deviceInformationType = procedureVariableRS.getString("Value");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            theDatabase.disconnectFromDatabase();

            parameterMap.put("dialog", relatedDialog);
            parameterMap.put("networkInformationType", networkInformationType);
            parameterMap.put("deviceType", deviceType);
            parameterMap.put("deviceInformationType", deviceInformationType);

            XSLTransformer transformer = XSLTransformer.getInstance();
//            String transformResults = transformer.xslTransform(foo_xml, foo_xsl);
            URL xslURL = AbstractTMDDTestCase.class.getResource("C2CRIValueConversion.xsl");
            ByteArrayInputStream bis = new ByteArrayInputStream(message.getBytes());
            return transformer.xslTransform(bis, xslURL, parameterMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }
    
    private String getUserMessageVerificationText(String unid, String mode){
        String response = "";
        
        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

        ResultSet needIDRS = theDatabase.queryReturnRS("Select * from UserMessageVerificationTextQuery where [RIMode] ='" + mode + "' and [NeedNumber] =" + unid);
        try {
            while (needIDRS.next()) {
                response = "  "+needIDRS.getString("TestCaseText");
            }
            needIDRS.close();
            needIDRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }
        theDatabase.disconnectFromDatabase();
        
        
        return response;
    }
}
