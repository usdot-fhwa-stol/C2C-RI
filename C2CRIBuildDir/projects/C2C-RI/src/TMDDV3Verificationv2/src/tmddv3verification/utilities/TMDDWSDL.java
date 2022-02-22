/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmddv3verification.utilities;

import com.ibm.wsdl.extensions.http.HTTPOperationImpl;
import com.ibm.wsdl.extensions.mime.MIMEContentImpl;
import com.ibm.wsdl.extensions.soap.SOAPBodyImpl;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.wsdl.extensions.http.HTTPBinding;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaImport;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Settings;
import org.fhwa.c2cri.ntcip2306v109.wsdl.WSDLValidator;
import org.w3c.dom.Element;

/**
 *
 * @author TransCore ITS
 */
public final class TMDDWSDL {

    private Map<String, URL> wsdlSchemas = new HashMap<String, URL>();
    private boolean schemaDocumentsExist = false;
    private Definition wsdl = null;
    private boolean wsdlValidatedAgainstSchema;
    private String wsdlFileName;
    private List<String> wsdlErrors = new ArrayList<String>();
    private SchemaTypeSystem sts;
    public boolean hasSchemaTypes = false;

    public TMDDWSDL(String wsdlFileName) {

        this.wsdlFileName = wsdlFileName;

        String errorMsg = "";
//        String wsdlFileName = "c:\\inout\\tmp\\tmdd.wsdl";
        try {
            WSDLFactory newFactory = WSDLFactory.newInstance();
            WSDLReader newReader = newFactory.newWSDLReader();
//            File newFile = new File(wsdlFileName);

            //Validate the WSDL file against the W3C WSDL Schema
            wsdlValidatedAgainstSchema = validateWSDL();

            wsdl = newReader.readWSDL(wsdlFileName);

            wsdlSchemas = generateSchemaList(wsdl);


            URL[] schemaFiles = new URL[wsdlSchemas.size()];

            Iterator<String> importKey = wsdlSchemas.keySet().iterator();
            int ii = 0;
            while (importKey.hasNext()) {
                String theKey = importKey.next();
                schemaFiles[ii] = (URL) wsdlSchemas.get(theKey);
                ii++;
                System.out.println("URL File => " + wsdlSchemas.get(theKey));
            }
            initSchemas(schemaFiles);
            hasSchemaTypes = true;

            schemaDocumentsExist = true;


        } catch (WSDLException wex) {
            //           wex.printStackTrace();
//            System.out.println("WSDL OK = False "+ wex.getMessage());
            errorMsg = wex.getMessage();
            wsdlErrors.add(errorMsg);
//        } catch (MalformedURLException murlex) {
//            murlex.printStackTrace();
//            errorMsg = murlex.getMessage();
//            System.out.println("WSDL OK = False"+ murlex.getMessage());
            wsdlErrors.add(errorMsg);
        } catch (Exception ex) {  // Thrown when error exists creating schema type system loader
            hasSchemaTypes = false;

        }
        // If wsdl is valid and related schemas exist then extract key WSDL information.
        if (schemaDocumentsExist && wsdlValidatedAgainstSchema) {
            Map tempMap = wsdl.getServices();

            Collection c = tempMap.values();
            //obtain an Iterator for Collection
            Iterator itr = c.iterator();
            //iterate through HashMap values iterator
            while (itr.hasNext()) {
                Service thisOne = (Service) itr.next();
                System.out.println("Service name = " + thisOne.getQName().getLocalPart());

                Map tempPorts = thisOne.getPorts();
                Collection c2 = tempPorts.values();
                //obtain an Iterator for Collection
                Iterator itr2 = c2.iterator();
                //iterate through HashMap values iterator
                while (itr2.hasNext()) {
                    Port thisOne2 = (Port) itr2.next();

                    Binding thisPortBinding = thisOne2.getBinding();
                    System.out.println("This ports binding is named " + thisPortBinding.getQName().getLocalPart());

                    List portBindingOperations = thisPortBinding.getBindingOperations();
                    for (Object theOperation : portBindingOperations) {
                        if (theOperation != null) {

                            System.out.println("     The operation= " + ((BindingOperation) theOperation).getName());
                            BindingInput theInput = ((BindingOperation) theOperation).getBindingInput();
                            System.out.println("            Input = " + theInput.getName());
                            BindingOutput theOutput = ((BindingOperation) theOperation).getBindingOutput();
                            System.out.println("            Output = " + theOutput.getName());
                            Map theFault = ((BindingOperation) theOperation).getBindingFaults();
                            System.out.println("            Fault = " + theFault);
                        }
                    }

                    System.out.println("   PortName = " + thisOne2.getName());
                    System.out.println("      num Extensibility Items = " + thisOne2.getExtensibilityElements().size());
                    if (thisOne2.getExtensibilityElements().size() > 0) {
                        List theExtensions = thisOne2.getExtensibilityElements();
                        ExtensibilityElement theElement = (ExtensibilityElement) theExtensions.get(0);
                        System.out.println("     the Name of the Extension " + theElement.getElementType().toString());
//                        SOAPAddress theAddress = (SOAPAddress) theElement;
//                        System.out.println("     the Location of the SOAP Address " + theAddress.getLocationURI());

                    }
                }
            }
            System.out.println("WSDL Schemas Exist - " + wsdlFileName);

        } else {
            System.out.println("WSDL Schemas don't Exit in file - " + wsdlFileName + " -- Reason" + errorMsg);
        }


    }

    private Map<String, URL> generateSchemaList(Definition wsdl) {
        Map<String, URL> fullSchemasMap = new HashMap<String, URL>();
        Types referencedTypes = wsdl.getTypes();
        String filePath = "";

        List typeList = referencedTypes.getExtensibilityElements();

        if (typeList.size() > 0) {
            try {
                System.out.println("Document Base URI = " + wsdl.getDocumentBaseURI());
                URI baseURI = new URI(wsdl.getDocumentBaseURI());
                File baseFile = new File(baseURI.toURL().getFile());
                filePath = baseURI.toString().substring(0, baseURI.toString().lastIndexOf(baseFile.getName()));

                for (Object thisType : typeList) {
                    Map importMap = ((Schema) thisType).getImports();
                    Iterator importsIterator = importMap.keySet().iterator();

                    while (importsIterator.hasNext()) {
                        String thisKey = (String) importsIterator.next();
                        for (Object thisImportObject : (List) importMap.get(thisKey)) {

                            SchemaImport thisImport = (SchemaImport) thisImportObject;
                            System.out.println("Adding Schema -- " + filePath + thisImport.getSchemaLocationURI());

                            fullSchemasMap.put(thisKey, new URL(filePath + thisImport.getSchemaLocationURI()));
                            getSchemaLocations(filePath + thisImport.getSchemaLocationURI(), filePath, fullSchemasMap);

                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println(" Error trying to find namespaces and URIs in the types section => " + ex.getMessage());
                wsdlErrors.add(" Error trying to find namespaces and URIs in the types section => " + ex.getMessage());
            }
        }

        return fullSchemasMap;
    }

    private void getSchemaLocations(String urlLocation, String baseURLPath, Map<String, URL> schemaMap) {

        Map foundSchemas = new HashMap<String, String>();
        // create the XMLInputFactory object
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try {
            //create a XMLStreamReader object
            URL schemaURL = new URL(urlLocation);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(schemaURL.openStream());

            while (reader.hasNext()) {
                int eventType = reader.getEventType();
                if (eventType == XMLStreamConstants.START_ELEMENT) {
                    String elementName = reader.getLocalName();
                    if (elementName.equals("import")) {
                        int count = reader.getAttributeCount();
                        String namespaceValue = "";
                        String schemalocationValue = "";
                        for (int i = 0; i < count; i++) {
                            if (reader.getAttributeLocalName(i).equals("namespace")) {
                                namespaceValue = reader.getAttributeValue(i);
                            } else if (reader.getAttributeLocalName(i).equals("schemaLocation")) {
                                schemalocationValue = reader.getAttributeValue(i);
                            }
                        }
                        if (!schemalocationValue.isEmpty()) {
                            if (!foundSchemas.containsKey(namespaceValue)) {
                                foundSchemas.put(namespaceValue, schemalocationValue);
                                System.out.println("GETSCHEMALOCATIONS:" + namespaceValue + " @ location " + schemalocationValue);
                            }
                        }
                    }


                }
                reader.next();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            wsdlErrors.add("Error in getting Schema Locations for " + urlLocation + " error = " + e.getMessage());
        } catch (XMLStreamException e) {
            e.printStackTrace();
            wsdlErrors.add("Error in getting Schema Locations for " + urlLocation + " error = " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            wsdlErrors.add("Error in getting Schema Locations for " + urlLocation + " error = " + e.getMessage());
        }

        Iterator<String> schemaIterator = foundSchemas.keySet().iterator();
        while (schemaIterator.hasNext()) {
            String thisKey = schemaIterator.next();
            if (!schemaMap.containsKey(thisKey)) {
                try {
                    schemaMap.put(thisKey, (new URL(baseURLPath + ((String) foundSchemas.get(thisKey)))));
                    getSchemaLocations(baseURLPath + (String) foundSchemas.get(thisKey), baseURLPath, schemaMap);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    wsdlErrors.add("Error in getting Schema Locations for " + urlLocation + " error = " + ex.getMessage());
                }

            }
        }

    }

    public void initSchemas(URL[] schemaFiles) throws Exception {
        boolean dl = true; // enable network downloads for imports and includes
        boolean nopvr = false;  //disable particle valid (restriction) rule
        boolean noupa = false;  //diable unique particle attributeion rule

        // Process Schema files
        List sdocs = new ArrayList();
        for (int i = 0; i < schemaFiles.length; i++) {
            try {
                sdocs.add(XmlObject.Factory.parse(schemaFiles[i],
                        (new XmlOptions()).setLoadLineNumbers().setLoadMessageDigest()));
            } catch (Exception e) {
                System.err.println("Can not load schema file: " + schemaFiles[i] + ": ");
                e.printStackTrace();
            }
        }

        XmlObject[] schemas = (XmlObject[]) sdocs.toArray(new XmlObject[sdocs.size()]);

        if (schemas.length > 0) {
            Collection errors = new ArrayList();
            XmlOptions compileOptions = new XmlOptions();
            if (dl) {
                compileOptions.setCompileDownloadUrls();
            }
            if (nopvr) {
                compileOptions.setCompileNoPvrRule();
            }
            if (noupa) {
                compileOptions.setCompileNoUpaRule();
            }

            try {
                sts = XmlBeans.compileXsd(schemas, XmlBeans.getBuiltinTypeSystem(), compileOptions);
            } catch (Exception e) {
                if (errors.isEmpty() || !(e instanceof XmlException)) {
                    e.printStackTrace();
                }

                System.out.println("Schema compilation errors: ");
                String schemaErrors = "";
                for (Iterator i = errors.iterator(); i.hasNext();) {
                    schemaErrors = schemaErrors.concat((String) i.next() + "\n");
                }
                System.out.println(schemaErrors);

                throw new Exception("Schema compilation errors: \n" + schemaErrors);
            }
        }

        if (sts == null) {
            throw new Exception("No Schemas provided to process.");
        }

    }

    private boolean validateWSDL() {
        boolean result = false;
//        String schemaValidDir = System.getProperty("user.dir") + "/testfiles/";  // Get the directory the application was started from
        URL wsdlSchemaURL = TMDDWSDL.class.getResource("/org/fhwa/c2cri/plugin/c2cri/ntcip2306/support/verification/v01_69/wsdl.xsd");
        List<String> schemaList = new ArrayList<String>();
//        schemaList.add(schemaValidDir + "Soap-Envelope.xsd");
//        schemaList.add(schemaValidDir + "wsdl.xsd");
        schemaList.add(wsdlSchemaURL.toString());

        String errorResult = "";

        WSDLValidator theValidator = new WSDLValidator();
        theValidator.addSchemas(schemaList);
        try {
            result = theValidator.checkValidString(readFileAsString(wsdlFileName));
            if (!result) {
                for (String errorMessage : theValidator.getErrors()) {
                    errorResult = errorResult.concat(errorMessage + "\n");
                    wsdlErrors.add(errorResult);
                }
            }
            System.out.println(result ? "Passed" : "Failed: \n" + errorResult);

        } catch (Exception ex) {
            System.out.println(" RIWSDL:validateWSDL  Exception trying to validate the WSDL =>" + ex.getMessage());
            wsdlErrors.add("Exception Error in validating WSDL:  error = " + ex.getMessage());
        }

        return result;
    }

    public boolean isSchemaDocumentsExist() {
        return schemaDocumentsExist;
    }

    public Map<String, URL> getWsdlSchemas() {
        return wsdlSchemas;
    }

    public String getWsdlFileName() {
        return wsdlFileName;
    }

    public boolean isWsdlValidatedAgainstSchema() {
        return wsdlValidatedAgainstSchema;
    }

    public List<String> getWsdlErrors() {
        return wsdlErrors;
    }

    public Map getServiceNames() {
        Map<String, QName> servicesMap = new HashMap<String, QName>();
 //       if (this.wsdlValidatedAgainstSchema) {
            Map services = wsdl.getServices();
            Iterator servicesIterator = services.keySet().iterator();
            while (servicesIterator.hasNext()) {
                QName theService = (QName) servicesIterator.next();
                servicesMap.put(theService.getLocalPart(), theService);
            }
 //       }
        return servicesMap;
    }

    public Map getServicePortNames(QName service) {
        Map<String, QName> portMap = new HashMap<String, QName>();
//        if (this.wsdlValidatedAgainstSchema) {
            Service requestedService = wsdl.getService(service);
            if (requestedService != null) {
                Map ports = requestedService.getPorts();
                Iterator portsIterator = ports.keySet().iterator();
                while (portsIterator.hasNext()) {
                    String thePort = (String) portsIterator.next();
                    QName thePortQName = new QName(thePort);
                    portMap.put(thePort, thePortQName);
                }

            }
  //      }
        return portMap;
    }

    public String getServicePortAddress(QName service, String port) {
        String servicePortAddress = null;
//        if (this.wsdlValidatedAgainstSchema) {
            Service requestedService = wsdl.getService(service);
            if (requestedService != null) {
                Port requestedPort = requestedService.getPort(port);
                if (requestedPort != null) {
                    List<ExtensibilityElement> theExtensions = requestedPort.getExtensibilityElements();
                    if (!theExtensions.isEmpty()) {
                        for (ExtensibilityElement thisElement : theExtensions) {
                            if (thisElement.getElementType().getLocalPart().equals("address")) {
                                System.out.println("The Address Type = " + thisElement.getElementType());
                                if (thisElement instanceof SOAPAddress) {
                                    System.out.println("SOAP Address " + thisElement.getClass().toString());
                                    SOAPAddress theAddress = (SOAPAddress) thisElement;
                                    servicePortAddress = theAddress.getLocationURI();
                                } else if (thisElement instanceof HTTPAddress) {
                                    System.out.println("HTTP Address " + thisElement.getClass().toString());
                                    HTTPAddress theAddress = (HTTPAddress) thisElement;
                                    servicePortAddress = theAddress.getLocationURI();
                                } else if (thisElement.getElementType().getNamespaceURI().contains("ftp")) {
                                    System.out.println("FTP Address " + thisElement.getClass().toString());
                                    UnknownExtensibilityElement theAddress = (UnknownExtensibilityElement) thisElement;
                                    servicePortAddress = theAddress.getElement().getAttribute("location");
                                }
                            } else {
                                System.out.println("Unknown Address Type??");
                            }
                        }
                    }

                }

//            }

        }
        return servicePortAddress;
    }

    public String getServicePortTransportType(QName service, String port) {
        String transportType = null;
        if (this.wsdlValidatedAgainstSchema) {
            Service requestedService = wsdl.getService(service);
            if (requestedService != null) {
                Port requestedPort = requestedService.getPort(port);
                if (requestedPort != null) {
                    List<ExtensibilityElement> theExtensions = requestedPort.getBinding().getExtensibilityElements();
                    if (!theExtensions.isEmpty()) {
                        for (ExtensibilityElement thisElement : theExtensions) {
                            if (thisElement instanceof SOAPBinding) {
                                transportType = "SOAP";
                            } else if (thisElement instanceof HTTPBinding) {
                                HTTPBinding httpBinding = (HTTPBinding) thisElement;
                                transportType = "HTTP:" + httpBinding.getVerb();
                            } else if (thisElement instanceof UnknownExtensibilityElement) {
                                UnknownExtensibilityElement unknownElement = (UnknownExtensibilityElement) thisElement;
                                if (unknownElement.getElement().getNamespaceURI().equals(NTCIP2306Settings.FTP_NAMESPACE_URI)) {
                                    transportType = "FTP:GET";
                                } else {
                                    transportType = unknownElement.toString();
                                }
                            }
                        }
                    } else {
                        //TODO
                        // Try and treat an unseen binding command as an FTP Binding.  Don't know how else
                        // to resolve at this point.
                        transportType = "FTP:GET";
                    }
                }
            }
        }
        return transportType;
    }

    public List<String> getServicePortOperationNames(QName service, String port) {
        List<String> servicePortOperationNames = new ArrayList<String>();
//        if (this.wsdlValidatedAgainstSchema) {
            Service requestedService = wsdl.getService(service);
            if (requestedService != null) {
                Port requestedPort = requestedService.getPort(port);
                if (requestedPort != null) {
                    List<BindingOperation> operations = requestedPort.getBinding().getBindingOperations();
                    for (BindingOperation thisOperation : operations) {
                        servicePortOperationNames.add(thisOperation.getName());
                    }
                }
            }
//        }
        return servicePortOperationNames;
    }

    public Map<String, OperationSpecification> getServicePortOperationInformation(QName service, String port) {
        Map<String, OperationSpecification> servicePortOperations = new HashMap<String, OperationSpecification>();
//        if (this.wsdlValidatedAgainstSchema) {
            Service requestedService = wsdl.getService(service);
            if (requestedService != null) {
                Port requestedPort = requestedService.getPort(port);
                if (requestedPort != null) {

                    List msgPartOrder = new ArrayList();
                    msgPartOrder.add("c2cMsgAdmin");
                    msgPartOrder.add("message");

                    List<BindingOperation> bindingOperations = requestedPort.getBinding().getBindingOperations();

                    for (BindingOperation thisBindingOperation : bindingOperations) {
                        OperationSpecification thisSpecification = new OperationSpecification();
                        thisSpecification.setOperationName(thisBindingOperation.getName());  // Set the Name
                        thisSpecification.setRelatedToPort(port);

                        // Get the encoding type from the output part
                        List<ExtensibilityElement> outputElements = thisBindingOperation.getBindingOutput().getExtensibilityElements();
                        for (ExtensibilityElement thisElement : outputElements) {
                            if (thisElement instanceof SOAPBodyImpl) {
                                thisSpecification.setOperationOutputEncoding(thisSpecification.SOAP_ENCODING);
                            } else if (thisElement instanceof MIMEContentImpl) {
                                String encoding = ((MIMEContentImpl) thisElement).getType();
                                if (encoding.equalsIgnoreCase("text/xml")) {
                                    thisSpecification.setOperationOutputEncoding(thisSpecification.XML_ENCODING);
                                } else if (encoding.equalsIgnoreCase("gzip")) {
                                    thisSpecification.setOperationOutputEncoding(thisSpecification.GZIP_ENCODING);
                                } else {
                                    thisSpecification.setErroneousSpecification(true);
                                    thisSpecification.getErrorsEncountered().add("Did not recognize Encoding type: " + encoding);
                                }

                            } else {
                                thisSpecification.setErroneousSpecification(true);
                                thisSpecification.getErrorsEncountered().add("Did not recognize the output element type of the binding.");

                            }

                        }

                        // Get the encoding type from the input part
                        List<ExtensibilityElement> inputElements = thisBindingOperation.getBindingInput().getExtensibilityElements();
                        for (ExtensibilityElement thisElement : inputElements) {
                            if (thisElement instanceof SOAPBodyImpl) {
                                thisSpecification.setOperationInputEncoding(thisSpecification.SOAP_ENCODING);
                            } else if (thisElement instanceof MIMEContentImpl) {
                                String encoding = ((MIMEContentImpl) thisElement).getType();
                                if (encoding.equalsIgnoreCase("text/xml")) {
                                    thisSpecification.setOperationInputEncoding(thisSpecification.XML_ENCODING);
                                } else if (encoding.equalsIgnoreCase("gzip")) {
                                    thisSpecification.setOperationInputEncoding(thisSpecification.GZIP_ENCODING);
                                } else if (encoding.equalsIgnoreCase("application/x-wwwform-urlencoded")) {  //TODO is the correct?
                                    thisSpecification.setOperationInputEncoding(thisSpecification.XML_ENCODING);
                                } else {
                                    thisSpecification.setErroneousSpecification(true);
                                    thisSpecification.getErrorsEncountered().add("Did not recognize Encoding type: " + encoding);
                                }

                            } else {
                                thisSpecification.setErroneousSpecification(true);
                                thisSpecification.getErrorsEncountered().add("Did not recognize the input element type of the binding.");

                            }
                        }

                        // Get the encoding type from the fault part
                        if (thisBindingOperation.getBindingFault("errorReport") != null) {

                            List<ExtensibilityElement> faultElements = thisBindingOperation.getBindingFault("errorReport").getExtensibilityElements();
                            for (ExtensibilityElement thisElement : faultElements) {
                                if (thisElement instanceof UnknownExtensibilityElement) {
                                    if(((UnknownExtensibilityElement) thisElement).getElementType().getLocalPart().equals("body")){
                                        thisSpecification.setOperationFaultEncoding(thisSpecification.SOAP_ENCODING);
                                    }
                                }
                            }
                        } else thisSpecification.setOperationFaultEncoding("");


                        // Get the Action or Document attributes of the operation sub element
                        List<ExtensibilityElement> bindingOperationElements = thisBindingOperation.getExtensibilityElements();
                        for (ExtensibilityElement thisElement : bindingOperationElements) {
                            if (thisElement instanceof SOAPOperationImpl) {
                                SOAPOperationImpl theSOAPOperation = (SOAPOperationImpl) thisElement;
                                thisSpecification.setSoapAction(theSOAPOperation.getSoapActionURI());
                            } else if (thisElement instanceof HTTPOperationImpl) {
                                HTTPOperationImpl theHTTPOperation = (HTTPOperationImpl) thisElement;
                                thisSpecification.setDocumentLocation(theHTTPOperation.getLocationURI());
                            } else if (thisElement instanceof UnknownExtensibilityElement) {
                                UnknownExtensibilityElement theFTPOperation = (UnknownExtensibilityElement) thisElement;
                                if (theFTPOperation.getElementType().getNamespaceURI().equals("http://schemas.ntcip.org/wsdl/ftp/")) {
                                    Element theFTPOperationElement = theFTPOperation.getElement();
                                    thisSpecification.setDocumentLocation(theFTPOperationElement.getAttribute("location"));
                                } else {
                                    thisSpecification.setErroneousSpecification(true);
                                    thisSpecification.getErrorsEncountered().add("Did not recognize the Operation element type of the binding operation.  Only SOAP, HTTP and FTP are recognized.");
                                }
                                ;
                            } else {
                                thisSpecification.setErroneousSpecification(true);
                                thisSpecification.getErrorsEncountered().add("Did not recognize the Operation element type of the binding operation.  Only SOAP and HTTP are recognized.");
                            }
                        }

                        Operation opDefinition = thisBindingOperation.getOperation();

                        Input opInput = opDefinition.getInput();
                        if (opInput != null) {
                            Message opInputMessage = opInput.getMessage();
                            if (opInputMessage != null) {
                                List<Part> msgParts = opInputMessage.getOrderedParts(msgPartOrder);
                                for (Part thisPart : msgParts) {
                                    thisSpecification.getInputMessage().add(thisPart.getElementName().getLocalPart());
                                }

                            } else {
                                thisSpecification.setErroneousSpecification(true);
                                thisSpecification.getErrorsEncountered().add("Expected an input message in the operation definition but could not find it.");
                                System.out.println("WARNING: No InputMessage for this operation:::: IS Something Wrong with the WSDL??");
                            }

                        }

                        Output opOutput = opDefinition.getOutput();
                        if (opOutput != null) {

                            Message opOutputMessage = opOutput.getMessage();
                            if (opOutputMessage != null) {
                                List<Part> msgParts = opOutputMessage.getOrderedParts(msgPartOrder);
                                for (Part thisPart : msgParts) {
                                    thisSpecification.getOutputMessage().add(thisPart.getElementName().getLocalPart());
                                }

                            } else {
                                thisSpecification.setErroneousSpecification(true);
                                thisSpecification.getErrorsEncountered().add("Expected an output message in the operation definition but could not find it.");
                                System.out.println("WARNING: No OutputMessage for this operation:::: IS Something Wrong with the WSDL??");
                            }

                        }


                        Fault opFault = opDefinition.getFault("errorReport");
                        if (opFault != null) {
                            Message opFaultMessage = opFault.getMessage();
                            List<Part> msgParts = opFaultMessage.getOrderedParts(msgPartOrder);
                            for (Part thisPart : msgParts) {
                                thisSpecification.getFaultMessage().add(thisPart.getElementName().getLocalPart());
                            }

                        }

                        // Store the type of operation this specification describes
                        if (thisSpecification.isRequestResponseOperation()) {
                            thisSpecification.setOperationType(thisSpecification.REQUEST_RESPONSE_OPERATION);
                        } else if (thisSpecification.isSubscriptionOperation()) {
                            thisSpecification.setOperationType(thisSpecification.SUBSCRIPTION_OPERATION);

                        } else if (thisSpecification.isPublicationOperation()) {
                            thisSpecification.setOperationType(thisSpecification.PUBLICATION_OPERATION);

                        } else if (thisSpecification.isGetOperation()) {
                            thisSpecification.setOperationType(thisSpecification.GET_OPERATION);
                        }

                        servicePortOperations.put(thisBindingOperation.getName(), thisSpecification);
                    }

                }
//            }
        }
        return servicePortOperations;
    }

    /** @param filePath the name of the file to open. Not sure if it can accept URLs or just filenames. Path handling could be better, and buffer sizes are hardcoded
     */
    private static String readFileAsString(String filePath)
            throws java.io.IOException, java.net.URISyntaxException {

        URI fileURI = new URI(filePath);
        URL fileURL = fileURI.toURL();
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(fileURL.openStream()));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }

    public Message getMessage(QName message) {
        return wsdl.getMessage(message);
    }

    public SchemaTypeSystem getSchemaTypeSystem() {
        return sts;
    }

    public BindingOperation getBinding(String namespaceURI, String bindingName, String operationName) {
        try {
            QName binding = new QName(namespaceURI, bindingName);

            //       QName operation = new QName(operationName);
            List<BindingOperation> bindingOperationList = wsdl.getBinding(binding).getBindingOperations();
            for (BindingOperation thisOperation : bindingOperationList) {
                if (thisOperation.getName().equals(operationName)) {
                    return thisOperation;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;  // Didn't find a match
    }


    public BindingOperation getServicePortBindingOperation(QName service, String port, String operationName) {
        BindingOperation requestedBindingOperation=null;

        if (this.wsdlValidatedAgainstSchema) {
            Service requestedService = wsdl.getService(service);
            if (requestedService != null) {
                Port requestedPort = requestedService.getPort(port);
                if (requestedPort != null) {

                    List msgPartOrder = new ArrayList();
                    msgPartOrder.add("c2cMsgAdmin");
                    msgPartOrder.add("message");

                    List<BindingOperation> bindingOperations = requestedPort.getBinding().getBindingOperations();

                    for (BindingOperation thisBindingOperation : bindingOperations) {
                        if (thisBindingOperation.getName().equals(operationName)){
                            requestedBindingOperation = thisBindingOperation;
                            break;
                        }
                    }
                }
            }
        }
        return requestedBindingOperation;

    }

}
