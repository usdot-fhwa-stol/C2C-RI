/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.plugin.c2cri.ntcip2306;

import org.fhwa.c2cri.applayer.DialogResults;
import org.fhwa.c2cri.messagemanager.Message;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.fhwa.c2cri.applayer.ApplicationLayerStandard;
import org.fhwa.c2cri.applayer.InformationLayerAdapter;
import org.fhwa.c2cri.applayer.ListenerManager;
import org.fhwa.c2cri.applayer.MessageContentGenerator;
import org.fhwa.c2cri.center.CenterMonitor;
import org.fhwa.c2cri.infolayer.SubPubMapper;
import org.fhwa.c2cri.messagemanager.MessageBodyGenerator;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.ntcip2306v109.messaging.DefaultMessageContentGenerator;
import org.fhwa.c2cri.ntcip2306v109.subpub.DefaultSubPubMapper;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Controller;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306InformationLayerAdapter;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Results;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationResults;
import org.fhwa.c2cri.ntcip2306v109.encoding.XMLValidator;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.Center;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.CenterConfigurationController;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;

/**
 * The NTCIP 2306 Application Layer Standard implementation.  It is capable of being accessed as a plugin.
 * 
 * @author TransCore ITS
 */
@PluginImplementation
public class NTCIP2306ApplicationLayerStandard implements ApplicationLayerStandard {

    public static String APPLICATION_STANDARD_NAME = "NTCIP2306v01";
    /**
     *
     */
    public static String ExternalCenterMode = "EC";
    /**
     *
     */
    public static String OwnerCenterMode = "OC";
    public static String NTCIP2306Transport = "NTCIP2306v1";
    public static String NTCIP2306FTPTransport = "FTP";
    public static String NTCIP2306HTTPTransport = "HTTP";
    public static String NTCIP2306GZIPEncoding = "GZipXML";
    public static String NTCIP2306XMLEncoding = "XML";
    public static String NTCIP2306SOAPEncoding = "SOAP_XML";
    public String serviceName;
    public String centerMode;
    public String portName;
    public Map<String, Map> transportOperations = new HashMap<String, Map>(); // String is the portName and the Map is <operationName, OperationSpec>
    private RIWSDL testConfigWSDL = null;
    private RIWSDL testSuiteWSDL = null;
    private MessageBodyGenerator messageBodyGenerator = null;
    private DialogResults dialogResults;
    private String testCase;
    private String applicationLayerStandard;
    private String informationLayerStandard;
    private String requestDialog;
    private String subscriptionDialog;
    private String publicationDialog;
    private boolean useWSDL;
    protected static Logger log = LogManager.getLogger(NTCIP2306ApplicationLayerStandard.class.getName());
    private CenterConfigurationController ccc2;

    /**
     * Creates a new Instance of NTCIP2306ApplicationLayerStandard
     */
    public NTCIP2306ApplicationLayerStandard() {
		// original implementation was empty
    }

    public void configureCenterServiceFromWSDL(String wsdlURL) throws Exception {
        testConfigWSDL = new RIWSDL(wsdlURL);

        if (!testConfigWSDL.isWsdlValidatedAgainstSchema())throw new Exception ("The WSDL File at "+wsdlURL+" is not valid: "+(testConfigWSDL.getWsdlErrors().size()>0?testConfigWSDL.getWsdlErrors().get(0):""));

        
        if (!testConfigWSDL.isHasSchemaTypes()){
            throw new Exception("Error processing the schemas associated with the WSDL: "+testConfigWSDL.getSchemaTypeError());
        }
        
        
        if ((requestDialog != null) && (!requestDialog.isEmpty())) {
            // Figure out the service and port name for this dialog
            ArrayList<OperationInfo> serviceInfo = getInfoForOperation(requestDialog);
            // Create the Request DialogHandler
            if (serviceInfo.size() > 0) {
                boolean isPublicationOperation = false;
                String serviceName = serviceInfo.get(0).getServiceName();
                String portName = serviceInfo.get(0).getPortName();
                configureCenterServiceFromWSDL(wsdlURL, testCase, centerMode, serviceName, portName, requestDialog, isPublicationOperation);
            } else {
                throw new Exception("NTCIP2306ApplicationLayerStandard: Could not find a matching service for operation " + requestDialog + " in the WSDL File.");
            }
        }

        if ((subscriptionDialog != null) && (!subscriptionDialog.isEmpty())) {
            // Figure out the service and port name for this dialog
            ArrayList<OperationInfo> subServiceInfo = getInfoForOperation(subscriptionDialog);
            // Create the subscription DialogHandler
            if (subServiceInfo.size() > 0) {
                boolean isPublicationOperation = false;
                String serviceName = subServiceInfo.get(0).getServiceName();
                String portName = subServiceInfo.get(0).getPortName();
                configureCenterServiceFromWSDL(wsdlURL, testCase, centerMode, serviceName, portName, subscriptionDialog, isPublicationOperation);

            } else {
                throw new Exception("NTCIP2306ApplicationLayerStandard: Could not find a matching service for operation " + subscriptionDialog + " in the WSDL File.");
            }
        }

        if ((publicationDialog != null) && (!publicationDialog.isEmpty())) {
            // Figure out the service and port name for this dialog
            ArrayList<OperationInfo> pubServiceInfo = getInfoForOperation(publicationDialog);
            // Create the publication DialogHandler
            if (pubServiceInfo.size() > 0) {
                boolean isPublicationOperation = true;
                String serviceName = pubServiceInfo.get(0).getServiceName();
                String portName = pubServiceInfo.get(0).getPortName();
                configureCenterServiceFromWSDL(wsdlURL, testCase, centerMode, serviceName, portName, publicationDialog, isPublicationOperation);
            } else {
                throw new Exception("NTCIP2306ApplicationLayerStandard: Could not find a matching service for operation " + publicationDialog + " in the WSDL File.");
            }
        }

    }

    public ArrayList<OperationInfo> getInfoForOperation(String operation) {
        ArrayList<OperationInfo> infoList = new ArrayList<OperationInfo>();
        Map services = testConfigWSDL.getServiceNames();
        Iterator serviceIterator = services.keySet().iterator();
        while (serviceIterator.hasNext()) {
            String theService = (String) serviceIterator.next();

            Map wsdlPorts = testConfigWSDL.getServicePortNames((QName) services.get(theService));
            Iterator portsIterator = wsdlPorts.keySet().iterator();
            while (portsIterator.hasNext()) {
                String thisPortName = (String) portsIterator.next();
                Map<String, OperationSpecification> thisSpecification = testConfigWSDL.getServicePortOperationInformation((QName) services.get(theService), thisPortName);
                if (thisSpecification.containsKey(operation)) {
                    OperationInfo thisOperation = new OperationInfo();
                    thisOperation.setServiceName(theService);
                    thisOperation.setPortName(thisPortName);
                    infoList.add(thisOperation);
                    System.out.println(operation + " " + theService + " : " + thisPortName);
                }
            }

        }


        return infoList;
    }

    public OperationSpecification getOperationSpecification(String operation, RIWSDL targetWSDL) {
        OperationSpecification theSpecification = null;
        Map services = targetWSDL.getServiceNames();
        Iterator serviceIterator = services.keySet().iterator();
        while (serviceIterator.hasNext()) {
            String theService = (String) serviceIterator.next();

            Map wsdlPorts = targetWSDL.getServicePortNames((QName) services.get(theService));
            Iterator portsIterator = wsdlPorts.keySet().iterator();
            while (portsIterator.hasNext()) {
                String thisPortName = (String) portsIterator.next();
                Map<String, OperationSpecification> thisSpecification = targetWSDL.getServicePortOperationInformation((QName) services.get(theService), thisPortName);
                if (thisSpecification.containsKey(operation)) {
                    theSpecification = thisSpecification.get(operation);
                    System.out.println(operation + " " + theService + " : " + thisPortName);
                }
            }

        }

        return theSpecification;
    }

    public String getErrorTypeForOperation(String operation, RIWSDL targetWSDL) {
        String result = "";
        OperationSpecification theSpecification = getOperationSpecification(operation, targetWSDL);
        if ((theSpecification != null) && (theSpecification.getFaultMessage().size() > 0)) {
            result = theSpecification.getFaultMessage().get(0).getLocalPart();
        }
        return result;
    }

    public String getInputTypeForOperation(String operation, RIWSDL targetWSDL, boolean pubSub) {
        String result = "";
        OperationSpecification theSpecification = getOperationSpecification(operation, targetWSDL);
        if (theSpecification != null) {
            if (!pubSub && (theSpecification.getInputMessage().size() > 0)) {
                result = theSpecification.getInputMessage().get(0).getLocalPart();
            } else if (theSpecification.getInputMessage().size() > 1) {
                result = theSpecification.getInputMessage().get(1).getLocalPart();
            }
        }
        return result;
    }

    public String getOutputTypeForOperation(String operation, RIWSDL targetWSDL, boolean pubSub) {
        String result = "";
        OperationSpecification theSpecification = getOperationSpecification(operation, targetWSDL);
        if (theSpecification != null) {
            if (!pubSub && (theSpecification.getOutputMessage().size() > 0)) {
                result = theSpecification.getOutputMessage().get(0).getLocalPart();
            } else if (theSpecification.getOutputMessage().size() > 1) {
                result = theSpecification.getOutputMessage().get(1).getLocalPart();
            }
        }
        return result;
    }

    public void configureCenterServiceFromWSDL(String wsdlURL, String serviceName, String portName, String operationName, String pathName) throws Exception {
        boolean isPublicationOperation = operationName.endsWith("Update");
        configureCenterServiceFromWSDL(wsdlURL, testCase, centerMode, serviceName, portName, operationName, isPublicationOperation);
    }

    private void configureCenterServiceFromWSDL(String wsdlURL, String testCase, String mode, String serviceName, String portName, String operationName, boolean isPublication) throws Exception {
        String transportType = "";
        String transportVerb = "";
        String inputEncodingType = "";
        String outputEncodingType = "";
        String faultEncodingType = "";
        Map<String, OperationSpecification> thisSpecification;
        String portAddressURL = "";
        String portAddress = "";
        int portNumber = 0;
        String portProtocol = "";
        String addressFile = "";
        String soapAction = "";
        String localFile = "";

        this.serviceName = serviceName;
        this.portName = portName;
        // If this is a publication operation then switch the mode so that the EC becomes the listener, so server transports are started instead of clients
        if (isPublication) {
            this.centerMode = mode.equals(NTCIP2306ApplicationLayerStandard.ExternalCenterMode) ? NTCIP2306ApplicationLayerStandard.OwnerCenterMode : NTCIP2306ApplicationLayerStandard.ExternalCenterMode;
        } else {
            this.centerMode = mode;
        }
        if ((testConfigWSDL == null) || (!testConfigWSDL.getWsdlFileName().equals(wsdlURL))) {
            testConfigWSDL = new RIWSDL(wsdlURL);
        }
        
        if (!testConfigWSDL.isHasSchemaTypes()){
            throw new Exception("Error processing the schemas associated with the WSDL: "+testConfigWSDL.getSchemaTypeError());
        }
        
        Map services = testConfigWSDL.getServiceNames();
        //           System.out.println(services);

        boolean portFound = false;
        boolean serviceFound = false;
        Iterator serviceIterator = services.keySet().iterator();
        while (serviceIterator.hasNext()) {
            String theService = (String) serviceIterator.next();
            if (theService.equalsIgnoreCase(serviceName)) {
                // Found the selected service in the WSDL
                serviceFound = true;
                System.out.println("Service " + theService + " has ports :");
                Map wsdlPorts = testConfigWSDL.getServicePortNames((QName) services.get(theService));
                Iterator portsIterator = wsdlPorts.keySet().iterator();
                while (portsIterator.hasNext()) {
                    String thisPortName = (String) portsIterator.next();
                    if (thisPortName.equalsIgnoreCase(portName)) {
                        //Found the selected port in the WSDL
                        portFound = true;
                        // Get the specification and store the result
                        thisSpecification = testConfigWSDL.getServicePortOperationInformation((QName) services.get(theService), thisPortName);

                        // Confirm that the operation provided exists within the service and port specified
                        if (thisSpecification.get(operationName) == null) {
                            throw new Exception("Unable to find  Operation : " + operationName + " within Service: " + serviceName + " in the WSDL at " + wsdlURL);
                        }

                        log.debug("The port name = " + portName);
                        log.debug("Is thisSpecification = null? " + (thisSpecification == null ? "True" : "False"));
                        log.debug("this.transportOperations = null? " + (this.transportOperations == null ? "True" : "False"));
						if (this.transportOperations != null)
							this.transportOperations.put(portName, thisSpecification);
                        transportType = testConfigWSDL.getServicePortTransportType((QName) services.get(theService), portName);
                        if (transportType != null) {
                            if (transportType.equals("SOAP")) {
                                transportType = NTCIP2306ApplicationLayerStandard.NTCIP2306HTTPTransport;
                                soapAction = thisSpecification.get(operationName).getSoapAction();
                                transportVerb = "POST";
                            } else if (transportType.equals("HTTP")) {
                                transportType = NTCIP2306ApplicationLayerStandard.NTCIP2306HTTPTransport;
                                localFile = thisSpecification.get(operationName).getDocumentLocation();
                            } else if (transportType.equals("HTTP:GET")) {
                                transportType = NTCIP2306ApplicationLayerStandard.NTCIP2306HTTPTransport;
                                localFile = thisSpecification.get(operationName).getDocumentLocation();
                                transportVerb = "GET";
                            } else if (transportType.equals("HTTP:POST")) {
                                transportType = NTCIP2306ApplicationLayerStandard.NTCIP2306HTTPTransport;
                                localFile = thisSpecification.get(operationName).getDocumentLocation();
                                transportVerb = "POST";
                            } else if (transportType.equals("FTP:GET")) {
                                transportType = NTCIP2306ApplicationLayerStandard.NTCIP2306FTPTransport;
                                localFile = thisSpecification.get(operationName).getDocumentLocation();
                                transportVerb = "GET";
                            }

                        }
                        portAddressURL = testConfigWSDL.getServicePortAddress((QName) services.get(theService), portName);
                        try {
                            URL theAddress = new URL(portAddressURL);
                            portNumber = theAddress.getPort();
                            portAddress = theAddress.getHost();
                            portProtocol = theAddress.getProtocol();
                            if (theAddress.getFile() != null) {
                                addressFile = theAddress.getFile();
                            }
                        } catch (Exception ex) {
                            throw new Exception("Invalid URL " + portAddress + " specified for Service: " + serviceName + " in the WSDL at " + wsdlURL);
                        }

                        inputEncodingType = thisSpecification.get(operationName).getOperationInputEncoding();
                        if (inputEncodingType != null) {
                            if (inputEncodingType.equals(OperationSpecification.GZIP_ENCODING)) {
                                inputEncodingType = NTCIP2306ApplicationLayerStandard.NTCIP2306GZIPEncoding;
                            } else if (inputEncodingType.equals(OperationSpecification.XML_ENCODING)) {
                                inputEncodingType = NTCIP2306ApplicationLayerStandard.NTCIP2306XMLEncoding;

                            } else if (inputEncodingType.equals(OperationSpecification.SOAP_ENCODING)) {
                                inputEncodingType = NTCIP2306ApplicationLayerStandard.NTCIP2306SOAPEncoding;
                            } else {
                                inputEncodingType = NTCIP2306ApplicationLayerStandard.NTCIP2306XMLEncoding;
                            }



                        }

                        outputEncodingType = thisSpecification.get(operationName).getOperationOutputEncoding();
                        if (outputEncodingType != null) {
                            if (outputEncodingType.equals(OperationSpecification.GZIP_ENCODING)) {
                                outputEncodingType = NTCIP2306ApplicationLayerStandard.NTCIP2306GZIPEncoding;
                            } else if (outputEncodingType.equals(OperationSpecification.XML_ENCODING)) {
                                outputEncodingType = NTCIP2306ApplicationLayerStandard.NTCIP2306XMLEncoding;

                            } else if (outputEncodingType.equals(OperationSpecification.SOAP_ENCODING)) {
                                outputEncodingType = NTCIP2306ApplicationLayerStandard.NTCIP2306SOAPEncoding;
                            } else {
                                outputEncodingType = NTCIP2306ApplicationLayerStandard.NTCIP2306XMLEncoding;
                            }

                        }

                        faultEncodingType = thisSpecification.get(operationName).getOperationFaultEncoding();
                        if (faultEncodingType != null) {
                            if (faultEncodingType.equals(OperationSpecification.GZIP_ENCODING)) {
                                faultEncodingType = NTCIP2306ApplicationLayerStandard.NTCIP2306GZIPEncoding;
                            } else if (outputEncodingType != null && outputEncodingType.equals(OperationSpecification.XML_ENCODING)) {
                                faultEncodingType = NTCIP2306ApplicationLayerStandard.NTCIP2306XMLEncoding;

                            } else if (faultEncodingType.equals(OperationSpecification.SOAP_ENCODING)) {
                                faultEncodingType = NTCIP2306ApplicationLayerStandard.NTCIP2306SOAPEncoding;
                            } else {
                                faultEncodingType = NTCIP2306ApplicationLayerStandard.NTCIP2306XMLEncoding;
                            }
                        }

                    }
                }
                if (!portFound) {
                    throw new Exception("Unable to find port: " + portName + " for Service " + serviceName + " in the WSDL at " + wsdlURL);
                }

            }

        }
        if (!serviceFound) {
            throw new Exception("Unable to find  Service: " + serviceName + " in the WSDL at " + wsdlURL);

        }

    }

    public void configureTestConfigurationWSDL(String wsdlURL) {
        testConfigWSDL = new RIWSDL(wsdlURL);
    }

    public void configureTestSuiteWSDL(String wsdlURL) {
        testSuiteWSDL = new RIWSDL(wsdlURL);
    }

    public RIWSDL getTestConfigWSDL() {
        return testConfigWSDL;
    }

    public RIWSDL getTestSuiteWSDL() {
        return testSuiteWSDL;
    }

    public MessageBodyGenerator getMessageBodyGenerator() throws Exception {
        if (messageBodyGenerator != null) {
            return messageBodyGenerator;
        }
        throw new Exception("The Message Body Generator is not Available");
    }

    public String createMessageBody(String operation, String messageType) {
        String result = "";
        if (messageBodyGenerator != null) {

            Map operationMap = this.transportOperations.get(portName);
            if (operationMap.containsKey(operation)) {
                OperationSpecification theOperation = (OperationSpecification) operationMap.get(operation);
                List<QName> messages = new ArrayList();
                if (messageType.equalsIgnoreCase("Input")) {
                    messages = theOperation.getInputMessage();

                } else if (messageType.equalsIgnoreCase("Output")) {
                    messages = theOperation.getInputMessage();

                } else if (messageType.equalsIgnoreCase("Fault")) {
                    messages = theOperation.getInputMessage();
                }

                for (QName theMessage : messages) {
                    try {
                        result = result.concat(messageBodyGenerator.createMessage(theMessage.getLocalPart()));
                    } catch (Exception ex) {
                        System.out.println("Error generating message - " + theMessage + " : " + ex.getMessage());
                    }
                }

            } else {
                System.out.println("Operation " + operation + " does not exist in the Map!");
            }

        }

        return result;
    }

    @Override
    public void stopServices() {
        ccc2.stopServices();
        CenterMonitor.getInstance().unRegisterApplicationStandard();
        transportOperations = null;
        if (testConfigWSDL != null)testConfigWSDL.clear();
        testConfigWSDL = null;
        if (testSuiteWSDL != null)testSuiteWSDL.clear();
        testSuiteWSDL = null;
        messageBodyGenerator = null;
        try {
            MessageManager.getInstance().clear();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return APPLICATION_STANDARD_NAME;
    }

    @Override
    public void initializeStandard(URL serviceSpecificationURL, URL baseStandardSpecificationURL, String centerMode, String informationLayerStandard, String testCaseName, String requestDialog, String subscriptionDialog, String publicationDialog) throws Exception {
        this.centerMode = centerMode;
        this.testCase = testCaseName;
        this.applicationLayerStandard = APPLICATION_STANDARD_NAME;
        this.informationLayerStandard = informationLayerStandard;
        this.requestDialog = requestDialog;
        this.subscriptionDialog = subscriptionDialog;
        this.publicationDialog = publicationDialog;

        this.dialogResults = new DialogResults();

        ListenerManager.getInstance().setTestCaseID(this.testCase);
        configureCenterServiceFromWSDL(serviceSpecificationURL.toString());


        if (testConfigWSDL.isWsdlConnectionError())throw new Exception((testConfigWSDL.getWsdlErrors().size()>0?testConfigWSDL.getWsdlErrors().get(0):"Unable to connect to the referenced WSDL file at "+testConfigWSDL.getWsdlFileName() + "."));
        if (testConfigWSDL.isSchemaConnectionError())throw new Exception((testConfigWSDL.getWsdlErrors().size()>0?testConfigWSDL.getWsdlErrors().get(0):"Unable to connect to a schema file referenced by the WSDL file at "+testConfigWSDL.getWsdlFileName() + "."));
        if (!testConfigWSDL.isWsdlValidatedAgainstSchema())throw new Exception("The wsdl file reference provided "+testConfigWSDL.getWsdlFileName()+" is not a valid WSDL document.\n"+(testConfigWSDL.getWsdlErrors().size()>0?testConfigWSDL.getWsdlErrors().get(0):""));
        Center.RICENTERMODE mode = centerMode.contains("EC") || centerMode.contains("ec") ? Center.RICENTERMODE.EC : Center.RICENTERMODE.OC;
        ccc2 = new CenterConfigurationController(testSuiteWSDL, testConfigWSDL, mode);
        try{
        ccc2.initialzeServices();
        } catch (Exception ex){
			if (ex instanceof InterruptedException)
				Thread.currentThread().interrupt();
            throw new Exception("Error establishing the Application Layer Services: "+ex.getMessage());
        }
    }

    @Override
    public InformationLayerAdapter getInformationLayerAdapter(){
        NTCIP2306Controller ntcip = new NTCIP2306Controller(testConfigWSDL, ccc2);

        InformationLayerAdapter ila = new NTCIP2306InformationLayerAdapter(ntcip);
        return ila;
    }

    @Override
    public Message publication_ec_receive(String dialog) throws Exception {

        OperationSpecification pubOpSpec = getOperationSpecification(dialog, testConfigWSDL);
        OperationIdentifier opId = new OperationIdentifier(pubOpSpec.getRelatedToService(), pubOpSpec.getRelatedToPort(), dialog, OperationIdentifier.SOURCETYPE.HANDLER);
        System.out.println("NTCIP2306ApplicationLayerStandard::publication_ec_receive Starting Operation " + opId);
        OperationIdentifier listenerOpId = new OperationIdentifier(pubOpSpec.getRelatedToService(), pubOpSpec.getRelatedToPort(), dialog, OperationIdentifier.SOURCETYPE.LISTENER);
        ccc2.registerForOperation(opId);
        long start = System.currentTimeMillis();
        System.out.println("NTCIP2306ApplicationLayerStandard::publication_ec_receive  starting request wait for operation opID:" + opId + "\n@" + start + "\n");
        NTCIP2306Message returnMsg = ccc2.getRequest(opId, 180000);
        System.out.println("NTCIP2306ApplicationLayerStandard::publication_ec_receive  request received for operation opID:" + opId + "\nDuration = " + (System.currentTimeMillis()-start) + "\n");
        if (returnMsg == null)throw new Exception("NTCIP2306ApplicationLayerStandard::publication_ec_receive  No publication message was received.");
        MessageManager theManager = MessageManager.getInstance();
        Message requestMessage = theManager.createMessage(requestDialog);
        requestMessage.setMessageName("REQUEST");
        ArrayList<byte[]> messageParts = new ArrayList<>();
        for (int ii = 1; ii<=returnMsg.getNumberMessageParts();ii++){
            messageParts.add(returnMsg.getMessagePartContent(ii));
        }
        requestMessage.setMessageBodyParts(messageParts);
        requestMessage.setMessageEncoding(returnMsg.getEncodingType().toString());
        requestMessage.setViaTransportProtocol(returnMsg.getProtocolType().toString());
        requestMessage.setMessageType(Message.REQUEST_MESSAGE_TYPE);
        theManager.addMessage(requestMessage, true);
        if (returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTP)
                || returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTPS)) {
            requestMessage.setMessageSourceAddress(
                    ListenerManager.getInstance().getExternalAddress(returnMsg.getHttpStatus().getDestination()));
            requestMessage.setMessageDestinationAddress(
                    ListenerManager.getInstance().getExternalAddress(returnMsg.getHttpStatus().getSource()));
        }
        dialogResults.setMessageFieldsVerified(true);
        dialogResults.setNumberOfMessagePartsReceived(1);
        dialogResults.setOneMessageReceived(true);
        dialogResults.setParsingErrorsEncountered(false);
        dialogResults.setMessageValuesVerified(true);
        return requestMessage;

    }

    @Override
    public void publication_ec_reply(String dialog, Message responseMessage) throws Exception {
        OperationSpecification pubOpSpec = getOperationSpecification(dialog, testConfigWSDL);
        OperationIdentifier opId = new OperationIdentifier(pubOpSpec.getRelatedToService(), pubOpSpec.getRelatedToPort(), dialog, OperationIdentifier.SOURCETYPE.HANDLER);
        System.out.println("NTCIP2306ApplicationLayerStandard::publication_ec_reply Starting Operation " + opId);
        OperationIdentifier listenerOpId = new OperationIdentifier(pubOpSpec.getRelatedToService(), pubOpSpec.getRelatedToPort(), dialog, OperationIdentifier.SOURCETYPE.LISTENER);

        QName fakeMsgQName = getMessageQName(responseMessage.getMessageBody());
        NTCIP2306Message fakeMsg = new NTCIP2306Message(fakeMsgQName.getNamespaceURI(), fakeMsgQName.getLocalPart(), responseMessage.getMessageBody());
        fakeMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);
        fakeMsg.getXmlStatus().setUTF8CharSet(true);
        fakeMsg.getXmlStatus().setXMLv1Document(true);

        System.out.println("NTCIP2306ApplicationLayerStandard::publication_ec_reply  response sent for opID:" + listenerOpId + "\n@" + System.currentTimeMillis() + "\n");
        ccc2.sendResponse(listenerOpId, fakeMsg);
        System.out.println("NTCIP2306ApplicationLayerStandard::publication_ec_reply  requesting results for opID:" + opId + "\n@" + System.currentTimeMillis() + "\n");
        OperationResults opResults = ccc2.getOperationResults(opId, 15000);
        if (opResults != null)
        {        NTCIP2306Results resultAnalyzer = new NTCIP2306Results(opResults);
                System.err.println(resultAnalyzer.toString());
                responseMessage.setTransportTimeInMillis(opResults.getResponseMessage().getTransportedTimeInMs());
        }
        System.out.println("NTCIP2306ApplicationLayerStandard::publication_ec_reply  received results for opID:" + opId + "\n@" + System.currentTimeMillis() + "\n");
        ccc2.unregisterForOperation(opId);
        System.out.println("NTCIP2306ApplicationLayerStandard::publication_ec_reply  unregistered for operation opID:" + opId + "\n@" + System.currentTimeMillis() + "\n");

        responseMessage.setMessageEncoding(fakeMsg.getEncodingType().toString());
        responseMessage.setViaTransportProtocol(fakeMsg.getProtocolType().toString());
        if (fakeMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTP)
                || fakeMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTPS)) {
            responseMessage.setMessageDestinationAddress(
                    ListenerManager.getInstance().getExternalAddress(fakeMsg.getHttpStatus().getDestination()));
            responseMessage.setMessageSourceAddress(
                    ListenerManager.getInstance().getExternalAddress(fakeMsg.getHttpStatus().getSource()));
        }
        responseMessage.setMessageType(Message.RESPONSE_MESSAGE_TYPE);

        dialogResults.setMessageFieldsVerified(true);
        dialogResults.setNumberOfMessagePartsReceived(1);
        dialogResults.setOneMessageReceived(true);
        dialogResults.setParsingErrorsEncountered(false);
        dialogResults.setMessageValuesVerified(true);

    }

    @Override
    public Message publication_oc(String dialog, Message publicationMessage) throws Exception {
        OperationSpecification pubOpSpec = getOperationSpecification(dialog, testConfigWSDL);
        OperationIdentifier opId = new OperationIdentifier(pubOpSpec.getRelatedToService(), pubOpSpec.getRelatedToPort(), dialog, OperationIdentifier.SOURCETYPE.HANDLER);
        System.out.println("NTCIP2306ApplicationLayerStandard::request_response_ec Starting Operation " + opId);
        OperationIdentifier listenerOpId = new OperationIdentifier(pubOpSpec.getRelatedToService(), pubOpSpec.getRelatedToPort(), dialog, OperationIdentifier.SOURCETYPE.LISTENER);
        ccc2.registerForOperation(opId);

        NTCIP2306Message fakeMsg = null;
        ArrayList<byte[]> messageParts = publicationMessage.getMessageBodyParts();
        if (messageParts.size() > 1) {
            QName fakeMsgQName = getMessageQName(messageParts.get(0));
            fakeMsg = new NTCIP2306Message(fakeMsgQName.getNamespaceURI(), fakeMsgQName.getLocalPart(), messageParts.get(0));
            for (int ii = 1; ii < messageParts.size(); ii++) {
                fakeMsgQName = getMessageQName(messageParts.get(ii));
                fakeMsg.addMessagePart(fakeMsgQName.getNamespaceURI(), fakeMsgQName.getLocalPart(), messageParts.get(ii));
            }
        } else {
            QName fakeMsgQName = getMessageQName(publicationMessage.getMessageBody());
            fakeMsg = new NTCIP2306Message(fakeMsgQName.getNamespaceURI(), fakeMsgQName.getLocalPart(), publicationMessage.getMessageBody());
        }
        fakeMsg.getXmlStatus().setUTF8CharSet(true);
        fakeMsg.getXmlStatus().setXMLv1Document(true);
        fakeMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.REQUEST);
        fakeMsg.setPublicationMessage(true);



        System.out.println("NTCIP2306ApplicationLayerStandard::publication_oc  sent request for listenerOpId: " + listenerOpId + "\n @" + System.currentTimeMillis());
        ccc2.sendRequest(listenerOpId, fakeMsg);
        NTCIP2306Message returnMsg = ccc2.getResponse(opId, 15000);
        System.out.println("NTCIP2306ApplicationLayerStandard::publication_oc  response returned from opID:" + opId + " response = null?" + (returnMsg == null ? "Yes" : "No") + "\n@" + System.currentTimeMillis() + "\n");
        OperationResults opResults = ccc2.getOperationResults(opId,500);
        if (opResults != null)
        {        NTCIP2306Results resultAnalyzer = new NTCIP2306Results(opResults);
                System.err.println(resultAnalyzer.toString());
                publicationMessage.setTransportTimeInMillis(opResults.getRequestMessage().getTransportedTimeInMs());
        }

        if (returnMsg != null) {
            publicationMessage.setMessageEncoding(fakeMsg.getEncodingType().toString());
            publicationMessage.setViaTransportProtocol(fakeMsg.getProtocolType().toString());
            if (returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTP)
                    || returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTPS)) {
                publicationMessage.setMessageDestinationAddress(
                        ListenerManager.getInstance().getExternalAddress(fakeMsg.getHttpStatus().getDestination()));
                publicationMessage.setMessageSourceAddress(
                        ListenerManager.getInstance().getExternalAddress(fakeMsg.getHttpStatus().getSource()));
            }
            publicationMessage.setMessageType(Message.REQUEST_MESSAGE_TYPE);

            MessageManager theManager = MessageManager.getInstance();
            Message responseMessage = theManager.createMessage(requestDialog);
            responseMessage.setMessageName("RESPONSE");
            if (opResults != null)responseMessage.setTransportTimeInMillis(opResults.getResponseMessage().getTransportedTimeInMs());
        ArrayList<byte[]> respMessageParts = new ArrayList<>();
        for (int ii = 1; ii<=returnMsg.getNumberMessageParts();ii++){
            respMessageParts.add(returnMsg.getMessagePartContent(ii));
        }
        responseMessage.setMessageBodyParts(respMessageParts);
            responseMessage.setMessageEncoding(returnMsg.getEncodingType().toString());
            responseMessage.setViaTransportProtocol(returnMsg.getProtocolType().toString());
            responseMessage.setMessageType(Message.RESPONSE_MESSAGE_TYPE);
            theManager.addMessage(responseMessage, true);
            if (returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTP)
                    || returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTPS)) {
                responseMessage.setMessageSourceAddress(
                        ListenerManager.getInstance().getExternalAddress(returnMsg.getHttpStatus().getDestination()));
                responseMessage.setMessageDestinationAddress(
                        ListenerManager.getInstance().getExternalAddress(returnMsg.getHttpStatus().getSource()));
            }
            dialogResults.setMessageFieldsVerified(true);
            dialogResults.setNumberOfMessagePartsReceived(1);
            dialogResults.setOneMessageReceived(true);
            dialogResults.setParsingErrorsEncountered(false);

            ccc2.unregisterForOperation(opId);
            System.out.println("NTCIP2306ApplicationLayerStandard::publication_oc  unregistered operation @" + System.currentTimeMillis() + "\n");

            return responseMessage;
        } else {
            ccc2.unregisterForOperation(opId);
            System.out.println("NTCIP2306ApplicationLayerStandard::publication_oc  unregistered operation @" + System.currentTimeMillis() + "\n");

            throw new Exception("No response Message was received");
        }

    }

    @Override
    public Message request_response_ec(String dialog, Message requestMessage) throws Exception {
        OperationIdentifier opId = new OperationIdentifier(serviceName, portName, dialog, OperationIdentifier.SOURCETYPE.HANDLER);
        System.out.println("NTCIP2306ApplicationLayerStandard::request_response_ec Starting Operation " + opId);
        OperationIdentifier listenerOpId = new OperationIdentifier(serviceName, portName, dialog, OperationIdentifier.SOURCETYPE.LISTENER);
        ccc2.registerForOperation(opId);

        QName fakeMsgQName = getMessageQName(requestMessage.getMessageBody());
        NTCIP2306Message fakeMsg = new NTCIP2306Message(fakeMsgQName.getNamespaceURI(), fakeMsgQName.getLocalPart(), requestMessage.getMessageBody());
        fakeMsg.getXmlStatus().setUTF8CharSet(true);
        fakeMsg.getXmlStatus().setXMLv1Document(true);
        fakeMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.REQUEST);
        ccc2.sendRequest(listenerOpId, fakeMsg);
        NTCIP2306Message returnMsg = ccc2.getResponse(opId, 15000);
        OperationResults opResults = ccc2.getOperationResults(opId,500);
        if (opResults != null)
        {        NTCIP2306Results resultAnalyzer = new NTCIP2306Results(opResults);
                System.err.println(resultAnalyzer.toString());
                long deltaTime = opResults.getResponseMessage().getTransportedTimeInMs()-opResults.getRequestMessage().getTransportedTimeInMs();
                System.err.println("Operation response time in Millis (without removing network latency) = "+deltaTime);
                requestMessage.setTransportTimeInMillis(opResults.getRequestMessage().getTransportedTimeInMs());
        }
        ccc2.unregisterForOperation(opId);

        requestMessage.setMessageEncoding(fakeMsg.getEncodingType().toString());
        requestMessage.setViaTransportProtocol(fakeMsg.getProtocolType().toString());
        if (returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTP)
                || returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTPS)) {
            requestMessage.setMessageDestinationAddress(
                    ListenerManager.getInstance().getExternalAddress(fakeMsg.getHttpStatus().getDestination()));
            requestMessage.setMessageSourceAddress(
                    ListenerManager.getInstance().getExternalAddress(fakeMsg.getHttpStatus().getSource()));
        }
        requestMessage.setMessageType(Message.REQUEST_MESSAGE_TYPE);
        
        MessageManager theManager = MessageManager.getInstance();
        Message responseMessage = theManager.createMessage(requestDialog);
        responseMessage.setMessageName("RESPONSE");
        if (opResults != null)responseMessage.setTransportTimeInMillis(opResults.getResponseMessage().getTransportedTimeInMs());
        ArrayList<byte[]> messageParts = new ArrayList<>();
        for (int ii = 1; ii<=returnMsg.getNumberMessageParts();ii++){
            messageParts.add(returnMsg.getMessagePartContent(ii));
        }
        responseMessage.setMessageBodyParts(messageParts);
        responseMessage.setMessageBody(returnMsg.getMessagePartContent(1));
        responseMessage.setMessageEncoding(returnMsg.getEncodingType().toString());
        responseMessage.setViaTransportProtocol(returnMsg.getProtocolType().toString());
        responseMessage.setMessageType(Message.RESPONSE_MESSAGE_TYPE);

        if (returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTP)
                || returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTPS)) {
            responseMessage.setMessageSourceAddress(
                    ListenerManager.getInstance().getExternalAddress(returnMsg.getHttpStatus().getDestination()));
            responseMessage.setMessageDestinationAddress(
                    ListenerManager.getInstance().getExternalAddress(returnMsg.getHttpStatus().getSource()));
        }
        dialogResults.setMessageFieldsVerified(true);
        dialogResults.setNumberOfMessagePartsReceived(1);
        dialogResults.setOneMessageReceived(true);
        dialogResults.setMessageValuesVerified(true);
        dialogResults.setParsingErrorsEncountered(false);
        return responseMessage;

    }

    @Override
    public Message request_response_oc_receive(String dialog) throws Exception {

        OperationIdentifier opId = new OperationIdentifier(serviceName, portName, dialog, OperationIdentifier.SOURCETYPE.HANDLER);
        System.out.println("NTCIP2306ApplicationLayerStandard::request_response_ec Starting Operation " + opId);
        OperationIdentifier listenerOpId = new OperationIdentifier(serviceName, portName, dialog, OperationIdentifier.SOURCETYPE.LISTENER);
        ccc2.registerForOperation(opId);
        NTCIP2306Message returnMsg = ccc2.getRequest(opId, 120000);

        MessageManager theManager = MessageManager.getInstance();
        Message requestMessage = theManager.createMessage(requestDialog);
        requestMessage.setMessageName("REQUEST");
        if (returnMsg != null) {
        ArrayList<byte[]> messageParts = new ArrayList<>();
        for (int ii = 1; ii<=returnMsg.getNumberMessageParts();ii++){
            messageParts.add(returnMsg.getMessagePartContent(ii));
        }
        requestMessage.setMessageBodyParts(messageParts);
            requestMessage.setMessageEncoding(returnMsg.getEncodingType().toString());
            requestMessage.setViaTransportProtocol(returnMsg.getProtocolType().toString());
            requestMessage.setMessageType(Message.REQUEST_MESSAGE_TYPE);

            if (returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTP)
                    || returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTPS)) {
                requestMessage.setMessageSourceAddress(
                        ListenerManager.getInstance().getExternalAddress(returnMsg.getHttpStatus().getDestination()));
                requestMessage.setMessageDestinationAddress(
                        ListenerManager.getInstance().getExternalAddress(returnMsg.getHttpStatus().getSource()));
            }
        dialogResults.setMessageFieldsVerified(true);
        dialogResults.setNumberOfMessagePartsReceived(1);
        dialogResults.setOneMessageReceived(true);
        dialogResults.setParsingErrorsEncountered(false);
        dialogResults.setMessageValuesVerified(true);

        } else {
            dialogResults.setOtherErrorsEncountered(true);
            ArrayList<String> errorList = new ArrayList<>();
            errorList.add("No message was received from the external center.");
            dialogResults.setOtherErrors(errorList);
        dialogResults.setMessageFieldsVerified(false);
        dialogResults.setNumberOfMessagePartsReceived(0);
        dialogResults.setOneMessageReceived(false);
        dialogResults.setParsingErrorsEncountered(false);
        dialogResults.setMessageValuesVerified(false);
         }
        return requestMessage;


    }

    @Override
    public void request_response_oc_reply(String dialog, Message responseMessage) throws Exception {

        OperationIdentifier opId = new OperationIdentifier(serviceName, portName, dialog, OperationIdentifier.SOURCETYPE.HANDLER);
        System.out.println("NTCIP2306ApplicationLayerStandard::request_response_ec Starting Operation " + opId);
        OperationIdentifier listenerOpId = new OperationIdentifier(serviceName, portName, dialog, OperationIdentifier.SOURCETYPE.LISTENER);

        QName fakeMsgQName = getMessageQName(responseMessage.getMessageBody());
        NTCIP2306Message fakeMsg = new NTCIP2306Message(fakeMsgQName.getNamespaceURI(), fakeMsgQName.getLocalPart(), responseMessage.getMessageBody());
        fakeMsg.getXmlStatus().setUTF8CharSet(true);
        fakeMsg.getXmlStatus().setXMLv1Document(true);
        fakeMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);
        ccc2.sendResponse(listenerOpId, fakeMsg);
        OperationResults opResults = ccc2.getOperationResults(opId, 15000);
        if (opResults != null)
        {        NTCIP2306Results resultAnalyzer = new NTCIP2306Results(opResults);
                System.err.println(resultAnalyzer.toString());
        }
        ccc2.unregisterForOperation(opId);

        responseMessage.setMessageEncoding(fakeMsg.getEncodingType().toString());
        responseMessage.setViaTransportProtocol(fakeMsg.getProtocolType().toString());
        if (fakeMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTP)
                || fakeMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTPS)) {
            responseMessage.setMessageDestinationAddress(
                    ListenerManager.getInstance().getExternalAddress(fakeMsg.getHttpStatus().getDestination()));
            responseMessage.setMessageSourceAddress(
                    ListenerManager.getInstance().getExternalAddress(fakeMsg.getHttpStatus().getSource()));
        }
        responseMessage.setMessageType(Message.RESPONSE_MESSAGE_TYPE);

        dialogResults.setMessageFieldsVerified(true);
        dialogResults.setNumberOfMessagePartsReceived(1);
        dialogResults.setOneMessageReceived(true);
        dialogResults.setParsingErrorsEncountered(false);
        dialogResults.setMessageValuesVerified(true);
        dialogResults.setMessageValuesVerified(true);

    }

    @Override
    public Message subcription_oc_receive(String dialog) throws Exception {
        OperationSpecification subOpSpec = getOperationSpecification(dialog, testConfigWSDL);
        OperationSpecification pubOpSpec = getOperationSpecification(publicationDialog, testConfigWSDL);
        OperationIdentifier opId = new OperationIdentifier(subOpSpec.getRelatedToService(), subOpSpec.getRelatedToPort(), dialog, OperationIdentifier.SOURCETYPE.HANDLER);
        System.out.println("NTCIP2306ApplicationLayerStandard::request_response_ec Starting Operation " + opId);
        OperationIdentifier pubOpId = new OperationIdentifier(pubOpSpec.getRelatedToService(), pubOpSpec.getRelatedToPort(), publicationDialog, OperationIdentifier.SOURCETYPE.LISTENER);
        OperationIdentifier pubOpIdScriptControl = new OperationIdentifier(pubOpSpec.getRelatedToService(), pubOpSpec.getRelatedToPort(), publicationDialog, OperationIdentifier.SOURCETYPE.HANDLER);
        HashMap<OperationIdentifier, OperationIdentifier> tmpMap = new HashMap();
        tmpMap.put(opId, pubOpId);
        ccc2.setSubPubMapping(tmpMap);
        ccc2.registerForOperation(opId);
        ccc2.registerForOperation(pubOpIdScriptControl);
        NTCIP2306Message returnMsg = ccc2.getRequest(opId, 20000);

        if (returnMsg == null) {
            ccc2.unregisterForOperation(opId);
            throw new Exception("subscription_oc_receive:: No returnMsg was received");
        }

        MessageManager theManager = MessageManager.getInstance();
        Message requestMessage = theManager.createMessage(requestDialog);
        requestMessage.setMessageName("REQUEST");
        ArrayList<byte[]> reqMsg = new ArrayList<>();
        for (int ii = 1; ii <= returnMsg.getNumberMessageParts(); ii++) {
            reqMsg.add(returnMsg.getMessagePartContent(ii));
        }
        requestMessage.setMessageBodyParts(reqMsg);
        requestMessage.setMessageEncoding(returnMsg.getEncodingType().toString());
        requestMessage.setViaTransportProtocol(returnMsg.getProtocolType().toString());
        requestMessage.setMessageType(Message.REQUEST_MESSAGE_TYPE);

        if (returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTP)
                || returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTPS)) {
            requestMessage.setMessageSourceAddress(
                    ListenerManager.getInstance().getExternalAddress(returnMsg.getHttpStatus().getDestination()));
            requestMessage.setMessageDestinationAddress(
                    ListenerManager.getInstance().getExternalAddress(returnMsg.getHttpStatus().getSource()));
        }
        dialogResults.setMessageFieldsVerified(true);
        dialogResults.setNumberOfMessagePartsReceived(1);
        dialogResults.setOneMessageReceived(true);
        dialogResults.setParsingErrorsEncountered(false);
        dialogResults.setMessageValuesVerified(true);
        return requestMessage;

    }

    private QName getMessageQName(byte[] message) {
        QName defaultName = new QName("Error", "Error");
        try {
            XMLValidator thisValidator = new XMLValidator();
            defaultName = thisValidator.getXMLMessageType(new String(message));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return defaultName;
    }

    @Override
    public Message subscription_ec(String dialog, Message subscriptionMessage) throws Exception {
        OperationSpecification subOpSpec = getOperationSpecification(dialog, testConfigWSDL);
        OperationSpecification pubOpSpec = getOperationSpecification(publicationDialog, testConfigWSDL);
        OperationIdentifier opId = new OperationIdentifier(subOpSpec.getRelatedToService(), subOpSpec.getRelatedToPort(), dialog, OperationIdentifier.SOURCETYPE.HANDLER);
        System.out.println("NTCIP2306ApplicationLayerStandard::subscription_ec Starting Operation " + opId);
        OperationIdentifier listenerOpId = new OperationIdentifier(subOpSpec.getRelatedToService(), subOpSpec.getRelatedToPort(), dialog, OperationIdentifier.SOURCETYPE.LISTENER);
        OperationIdentifier pubOpId = new OperationIdentifier(pubOpSpec.getRelatedToService(), pubOpSpec.getRelatedToPort(), publicationDialog, OperationIdentifier.SOURCETYPE.LISTENER);
        OperationIdentifier pubOpIdScriptControl = new OperationIdentifier(pubOpSpec.getRelatedToService(), pubOpSpec.getRelatedToPort(), publicationDialog, OperationIdentifier.SOURCETYPE.HANDLER);
        HashMap<OperationIdentifier, OperationIdentifier> tmpMap = new HashMap();
        try {
            tmpMap.put(listenerOpId, pubOpId);
            ccc2.setSubPubMapping(tmpMap);
            ccc2.registerForOperation(opId);
            ccc2.registerForOperation(pubOpIdScriptControl);

            NTCIP2306Message fakeMsg = null;
            ArrayList<byte[]> messageParts = subscriptionMessage.getMessageBodyParts();
            if (messageParts.size() > 1) {
                QName fakeMsgQName = getMessageQName(messageParts.get(0));
                fakeMsg = new NTCIP2306Message(fakeMsgQName.getNamespaceURI(), fakeMsgQName.getLocalPart(), messageParts.get(0));
                for (int ii = 1; ii < messageParts.size(); ii++) {
                    fakeMsgQName = getMessageQName(messageParts.get(ii));
                    fakeMsg.addMessagePart(fakeMsgQName.getNamespaceURI(), fakeMsgQName.getLocalPart(), messageParts.get(ii));
                }
            } else {
                QName fakeMsgQName = getMessageQName(subscriptionMessage.getMessageBody());
                fakeMsg = new NTCIP2306Message(fakeMsgQName.getNamespaceURI(), fakeMsgQName.getLocalPart(), subscriptionMessage.getMessageBody());
            }
            fakeMsg.getXmlStatus().setUTF8CharSet(true);
            fakeMsg.getXmlStatus().setXMLv1Document(true);
            fakeMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.REQUEST);
            ccc2.sendRequest(listenerOpId, fakeMsg);
            System.err.println("NTCIP2306ApplicationLayerStandard::subscription_ec  Waiting for the response.");                
            NTCIP2306Message returnMsg = ccc2.getResponse(opId, 15000);
            System.err.println("NTCIP2306ApplicationLayerStandard::subscription_ec  Response returned or timeout.");                
            OperationResults opResults = ccc2.getOperationResults(opId, 2000);
            System.err.println("NTCIP2306ApplicationLayerStandard::subscription_ec  OpResults returned or timeout.");                
            if (opResults != null)
             {    NTCIP2306Results resultAnalyzer = new NTCIP2306Results(opResults);
                  System.err.println(resultAnalyzer.toString());
             } else {
                  System.err.println("NTCIP2306ApplicationLayerStandard::subscription_ec  No opResults returned.  ResponseMessage? "+(returnMsg==null?"False":"True"));                
            }
            
            ccc2.unregisterForOperation(opId);

            subscriptionMessage.setMessageEncoding(fakeMsg.getEncodingType().toString());
            subscriptionMessage.setViaTransportProtocol(fakeMsg.getProtocolType().toString());
            if (returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTP)
                    || returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTPS)) {
                subscriptionMessage.setMessageDestinationAddress(
                        ListenerManager.getInstance().getExternalAddress(fakeMsg.getHttpStatus().getDestination()));
                subscriptionMessage.setMessageSourceAddress(
                        ListenerManager.getInstance().getExternalAddress(fakeMsg.getHttpStatus().getSource()));
            }
            subscriptionMessage.setMessageType(Message.REQUEST_MESSAGE_TYPE);

            MessageManager theManager = MessageManager.getInstance();
            Message responseMessage = theManager.createMessage(dialog);
            responseMessage.setMessageName("RESPONSE");
            responseMessage.setMessageBody(returnMsg.getMessagePartContent(1));
        ArrayList<byte[]> returnMessageParts = new ArrayList<>();
        for (int ii = 1; ii<=returnMsg.getNumberMessageParts();ii++){
            returnMessageParts.add(returnMsg.getMessagePartContent(ii));
        }
        responseMessage.setMessageBodyParts(returnMessageParts);
            responseMessage.setMessageEncoding(returnMsg.getEncodingType().toString());
            responseMessage.setViaTransportProtocol(returnMsg.getProtocolType().toString());
            responseMessage.setMessageType(Message.RESPONSE_MESSAGE_TYPE);

            if (returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTP)
                    || returnMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTPS)) {
                responseMessage.setMessageSourceAddress(
                        ListenerManager.getInstance().getExternalAddress(returnMsg.getHttpStatus().getDestination()));
                responseMessage.setMessageDestinationAddress(
                        ListenerManager.getInstance().getExternalAddress(returnMsg.getHttpStatus().getSource()));
            }
            dialogResults.setMessageFieldsVerified(true);
            dialogResults.setNumberOfMessagePartsReceived(1);
            dialogResults.setOneMessageReceived(true);
            dialogResults.setMessageValuesVerified(true);
            dialogResults.setParsingErrorsEncountered(false);
            return responseMessage;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception(ex);
        } finally {
            ccc2.unregisterForOperation(opId);
        }

    }

    @Override
    public void subscription_oc_reply(String dialog, Message responseMessage) throws Exception {
        OperationSpecification subOpSpec = getOperationSpecification(dialog, testConfigWSDL);
        OperationIdentifier opId = new OperationIdentifier(subOpSpec.getRelatedToService(), subOpSpec.getRelatedToPort(), dialog, OperationIdentifier.SOURCETYPE.HANDLER);
        System.out.println("NTCIP2306ApplicationLayerStandard::subscription_oc_reply Starting Operation " + opId);
        OperationIdentifier listenerOpId = new OperationIdentifier(subOpSpec.getRelatedToService(), subOpSpec.getRelatedToPort(), dialog, OperationIdentifier.SOURCETYPE.LISTENER);

        QName fakeMsgQName = getMessageQName(responseMessage.getMessageBody());
        NTCIP2306Message fakeMsg = new NTCIP2306Message(fakeMsgQName.getNamespaceURI(), fakeMsgQName.getLocalPart(), responseMessage.getMessageBody());
        fakeMsg.getXmlStatus().setUTF8CharSet(true);
        fakeMsg.getXmlStatus().setXMLv1Document(true);
        fakeMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);
        ccc2.sendResponse(listenerOpId, fakeMsg);
        OperationResults opResults = ccc2.getOperationResults(opId, 10000);
        if (opResults != null)
        {        NTCIP2306Results resultAnalyzer = new NTCIP2306Results(opResults);
                System.err.println(resultAnalyzer.toString());
        }
        ccc2.unregisterForOperation(opId);

        responseMessage.setMessageEncoding(fakeMsg.getEncodingType().toString());
        responseMessage.setViaTransportProtocol(fakeMsg.getProtocolType().toString());
        if (fakeMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTP)
                || fakeMsg.getProtocolType().equals(NTCIP2306Message.PROTOCOLTYPE.HTTPS)) {
            responseMessage.setMessageDestinationAddress(
                    ListenerManager.getInstance().getExternalAddress(fakeMsg.getHttpStatus().getDestination()));
            responseMessage.setMessageSourceAddress(
                    ListenerManager.getInstance().getExternalAddress(fakeMsg.getHttpStatus().getSource()));
        }
        responseMessage.setMessageType(Message.RESPONSE_MESSAGE_TYPE);

        dialogResults.setMessageFieldsVerified(true);
        dialogResults.setNumberOfMessagePartsReceived(1);
        dialogResults.setOneMessageReceived(true);
        dialogResults.setParsingErrorsEncountered(false);
        dialogResults.setMessageValuesVerified(true);

    }

    @Override
    public DialogResults getDialogResults() {
        return dialogResults;
    }

    @Override
    public void setMessageContentGenerator(MessageContentGenerator msgGenerator) throws Exception {
        DefaultMessageContentGenerator.setMessageContentGenerator(msgGenerator);
    }

    @Override
    public void setSubPubMapper(SubPubMapper subPubMapper) {
        DefaultSubPubMapper.setExternalSubPubMapper(subPubMapper);
    }

    ;

    private class OperationInfo {

        private String serviceName;
        private String portName;

        public String getPortName() {
            return portName;
        }

        public void setPortName(String portName) {
            this.portName = portName;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }
    }
}
