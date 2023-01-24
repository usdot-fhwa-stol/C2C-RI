/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.wsdl;

import com.ibm.wsdl.ImportImpl;
import com.ibm.wsdl.extensions.http.HTTPOperationImpl;
import com.ibm.wsdl.extensions.http.HTTPUrlEncodedImpl;
import com.ibm.wsdl.extensions.mime.MIMEContentImpl;
import com.ibm.wsdl.extensions.soap.SOAPBodyImpl;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.wsdl.Binding;
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
import javax.wsdl.PortType;
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
import org.apache.log4j.Logger;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Settings;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The Class RIWSDL.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public final class RIWSDL {

    /**
     * The wsdl schemas.
     */
    private Map<String, URL> wsdlSchemas = new HashMap<String, URL>();

    /**
     * The schema documents exist.
     */
    private boolean schemaDocumentsExist = false;

    /**
     * The wsdl.
     */
    private Definition wsdl = null;

    /**
     * The wsdl validated against schema.
     */
    private boolean wsdlValidatedAgainstSchema;

    /**
     * The wsdl can not be read.
     */
    private boolean wsdlConnectionError;

    /**
     * The wsdl schema files can not be read.
     */
    private boolean schemaConnectionError;

    /**
     * The wsdl file name.
     */
    private String wsdlFileName;

    /**
     * The wsdl errors.
     */
    private List<String> wsdlErrors = new ArrayList<String>();

    /**
     * The sts.
     */
    private SchemaTypeSystem sts;

    /**
     * The wsdl schema sections.
     */
    private ArrayList<Node> wsdlSchemaSections = new ArrayList<Node>();

    /**
     * This flag is set true if the schema types associated with the WSDL are properly processed.
     */
    public boolean hasSchemaTypes = false;

    /**
     * This flag is set true if the schema types associated with the WSDL are properly processed.
     */
    public String schemaTypeError = "";
    
    /**
     * The log.
     */
    protected static Logger log = Logger.getLogger(RIWSDL.class.getName());

    /**
     * Clear.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public void clear() {
        wsdlSchemas.clear();
        wsdlErrors.clear();
        if (wsdl != null) {
            wsdl = null;
        }
        if (sts != null) {
            sts = null;
        }
    }

    /**
     * Instantiates a new riwsdl.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param wsdlFileName the wsdl file name
     */
    public RIWSDL(String wsdlFileName) {

        this.wsdlFileName = wsdlFileName;

        String errorMsg = "";
        try {
            WSDLFactory newFactory = WSDLFactory.newInstance();
            WSDLReader newReader = newFactory.newWSDLReader();

            //Validate the WSDL file against the W3C WSDL Schema
            //   wsdlConnectionError is set if the WSDL can not be accessed through TCP/IP or File protocols
            wsdlValidatedAgainstSchema = validateWSDL();

            wsdl = newReader.readWSDL(wsdlFileName);

            setSchemaDocumentsExist(true);
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

        } catch (WSDLException wex) {

            errorMsg = wex.getMessage();
            if ((wex.getMessage().contains("java.net.ConnectException") || wex.getMessage().contains("java.io.FileNotFoundException") || wex.getMessage().contains("java.net.UnknownHostException")) && wex.getMessage().contains("schema")) {
                log.debug(" RIWSDL: Error trying to process the WSDL =>" + wex.getMessage());
                schemaConnectionError = true;
            } else if (wex.getMessage().contains("java.net.ConnectException")|| wex.getMessage().contains("java.io.FileNotFoundException") || wex.getMessage().contains("java.net.UnknownHostException")) {
                log.debug(" RIWSDL:  Exception trying to process the WSDL =>" + wex.getMessage());
                wsdlConnectionError = true;
            }
            wsdlErrors.add(errorMsg);
        } catch (Exception ex) {  // Thrown when error exists creating schema type system loader
            hasSchemaTypes = false;
            schemaTypeError = ex.getMessage();
            ex.printStackTrace();

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
                if (thisOne.getQName() != null) {
                    System.out.println("Service name = " + thisOne.getQName().getLocalPart());
                } else {
                    System.out.println("Service name is null");
                }

                Map tempPorts = thisOne.getPorts();
                Collection c2 = tempPorts.values();
                //obtain an Iterator for Collection
                Iterator itr2 = c2.iterator();
                //iterate through HashMap values iterator
                while (itr2.hasNext()) {
                    Port thisOne2 = (Port) itr2.next();

                    Binding thisPortBinding = thisOne2.getBinding();
                    if (thisPortBinding != null) {
                        if (thisPortBinding.getQName() != null) {
                            System.out.println("This ports binding is named " + thisPortBinding.getQName().getLocalPart());
                        } else {
                            System.out.println("This ports binding is not named");
                        }

                        List portBindingOperations = thisPortBinding.getBindingOperations();
                        for (Object theOperation : portBindingOperations) {
                            if (theOperation != null) {

                                System.out.println("     The operation= " + ((BindingOperation) theOperation).getName());
                                BindingInput theInput = ((BindingOperation) theOperation).getBindingInput();
                                BindingOutput theOutput = ((BindingOperation) theOperation).getBindingOutput();
                                Map theFault = ((BindingOperation) theOperation).getBindingFaults();
                            }
                        }
                    } else {
                        System.out.println("This portbinding is null");
                    }

                    System.out.println("   PortName = " + thisOne2.getName());
                    System.out.println("      num Extensibility Items = " + thisOne2.getExtensibilityElements().size());
                    if (thisOne2.getExtensibilityElements().size() > 0) {
                        List theExtensions = thisOne2.getExtensibilityElements();
                        ExtensibilityElement theElement = (ExtensibilityElement) theExtensions.get(0);
                        System.out.println("     the Name of the Extension " + theElement.getElementType().toString());
                    }
                }
            }
            System.out.println("WSDL Schemas Exist - " + wsdlFileName);

        } else {
            log.debug("WSDL Schemas don't Exist in file - " + wsdlFileName + " -- Reason: " + errorMsg);
            System.out.println("WSDL Schemas don't Exist in file - " + wsdlFileName + " -- Reason: " + errorMsg);
            // If a parsing error was encountered while trying to process the WSDL file, set the valid flag to false.
            if (errorMsg.contains("PARSER_ERROR")) {
                wsdlValidatedAgainstSchema = false;
            }
        }

    }

    /**
     * Generate schema list.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param wsdl the wsdl
     * @return the map
     */
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
                    // If the schema is defined within this section save the schema section as a node to be incorporated later.
                    if (importMap.isEmpty()) {
                        System.out.println("Section does not include imports- so storing now.");
                        wsdlSchemaSections.add(((Schema) thisType).getElement().cloneNode(true));
                    }
                    Iterator importsIterator = importMap.keySet().iterator();

                    while (importsIterator.hasNext()) {
                        String thisKey = (String) importsIterator.next();
                        for (Object thisImportObject : (List) importMap.get(thisKey)) {

                            SchemaImport thisImport = (SchemaImport) thisImportObject;

                            // Try to determine whether this schema has a relative location specified
                            boolean relativeLocation = true;
                            try {
                                URL schemaURL = new URL(thisImport.getSchemaLocationURI());
                                if (!schemaURL.getProtocol().isEmpty()) {
                                    relativeLocation = false;
                                }
                            } catch (Exception ex) {

                                System.out.println("RIWSDL: " + ex.getMessage());
                            }
                            if (thisImport.getReferencedSchema() == null && thisImport.getSchemaLocationURI() == null){
                                throw new Exception("No external schema is specified in the WSDL.");
                            }
                            String schemaLocation = relativeLocation ? thisImport.getReferencedSchema().getDocumentBaseURI() : thisImport.getSchemaLocationURI();
                            System.out.println("Adding Schema -- " + schemaLocation);
                            fullSchemasMap.put(thisKey, new URL(schemaLocation));
                            String thisBasePath = schemaLocation.contains("/")?schemaLocation.substring(0,schemaLocation.lastIndexOf("/"))+"/":schemaLocation;
                            URL tmp = new URL(new URL(baseURI.toString()),thisImport.getSchemaLocationURI());
                            thisBasePath = tmp.toString();
                            getSchemaLocations(schemaLocation, thisBasePath, fullSchemasMap);

                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println(" Error trying to find namespaces and URIs in the types section => " + ex.getMessage());
                wsdlErrors.add(" Error trying to find namespaces and URIs in the types section => " + ex.getMessage());
                setSchemaDocumentsExist(false);
            }
        }

        try {
            // If this wsdl imports other wsdls, process those wsdls as well.
            Map wsdlImportsMap = wsdl.getImports();

            Iterator wsdlImportsIterator = wsdlImportsMap.keySet().iterator();

            while (wsdlImportsIterator.hasNext()) {
                String thisKey = (String) wsdlImportsIterator.next();
                for (Object thisImportObject : (List) wsdlImportsMap.get(thisKey)) {
                    if (thisImportObject instanceof ImportImpl) {
                        ImportImpl thisImport = (ImportImpl) thisImportObject;
                        Map<String, URL> internalMap = generateSchemaList(thisImport.getDefinition());
                        fullSchemasMap.putAll(internalMap);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(" Error trying to find schemas in external WSDLs => " + ex.getMessage());
            wsdlErrors.add(" Error trying to find schemas in external WSDLs => " + ex.getMessage());
            setSchemaDocumentsExist(false);
        }

        return fullSchemasMap;
    }

    /**
     * Gets the schema locations.
     *
     * @param urlLocation the url location
     * @param baseURLPath the base url path
     * @param schemaMap the schema map
     * @return the schema locations
     */
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
            reader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            setSchemaDocumentsExist(false);
            wsdlErrors.add("Error in getting Schema Locations for " + urlLocation + " error = " + e.getMessage());
        } catch (XMLStreamException e) {
            setSchemaDocumentsExist(false);
            e.printStackTrace();
            wsdlErrors.add("Error in getting Schema Locations for " + urlLocation + " error = " + e.getMessage());
        } catch (IOException e) {
            setSchemaDocumentsExist(false);
            e.printStackTrace();
            wsdlErrors.add("Error in getting Schema Locations for " + urlLocation + " error = " + e.getMessage());
        }

        Iterator<String> schemaIterator = foundSchemas.keySet().iterator();
        while (schemaIterator.hasNext()) {
            String thisKey = schemaIterator.next();
            if (!schemaMap.containsKey(thisKey)) {
                try {
                    URL tmpURL = new URL(new URL(baseURLPath),((String) foundSchemas.get(thisKey)));
                    schemaMap.put(thisKey,tmpURL);

                    getSchemaLocations(tmpURL.toString(), tmpURL.toString(), schemaMap);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    setSchemaDocumentsExist(false);
                    wsdlErrors.add("Error in getting Schema Locations for " + urlLocation + " error = " + ex.getMessage());
                }

            }
        }

    }

    /**
     * Inits the schemas.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param schemaFiles the schema files
     * @throws Exception the exception
     */
    public void initSchemas(URL[] schemaFiles) throws Exception {
        boolean dl = true; // enable network downloads for imports and includes
        boolean nopvr = false;  //disable particle valid (restriction) rule
        boolean noupa = false;  //diable unique particle attributeion rule

        // Process Schema files
        List sdocs = new ArrayList();

        for (Node thisSchemaSection : wsdlSchemaSections) {
            try {
                sdocs.add(XmlObject.Factory.parse(thisSchemaSection,
                        (new XmlOptions()).setLoadLineNumbers().setLoadMessageDigest()));
            } catch (Exception e) {
                log.error("Can not load schema Section: " + thisSchemaSection.getBaseURI() + ": ");
                System.err.println("Can not load schema file: " + thisSchemaSection.getBaseURI() + ": ");
                e.printStackTrace();
            }
        }

        for (int i = 0; i < schemaFiles.length; i++) {
            try {
                sdocs.add(XmlObject.Factory.parse(schemaFiles[i],
                        (new XmlOptions()).setLoadLineNumbers().setLoadMessageDigest()));
            } catch (Exception e) {
                log.error("Can not load schema file: " + schemaFiles[i] + ": ");
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
                if (schemaErrors.isEmpty()){
                    schemaErrors = e.getMessage();
                }
                System.out.println(schemaErrors);

                throw new Exception("Schema compilation errors: \n" + schemaErrors);
            }
        }

        if (sts == null) {
            throw new Exception("No Schemas provided to process.");
        }

    }

    /**
     * Validate wsdl.
     *
     * Pre-Conditions: N/A Post-Conditions: Updates wsdlErrors when any
     * validation error occurs. Sets wsdlConnectionError to true if a variety of
     * WSDL connection errors are detected.
     *
     * @return true, if successful
     */
    private boolean validateWSDL() {
        boolean result = false;
        try {
            URL wsdlSchemaURL = RIWSDL.class.getResource("/org/fhwa/c2cri/ntcip2306v109/wsdl/wsdl.xsd");
            List<String> schemaList = new ArrayList<String>();
            schemaList.add(wsdlSchemaURL.toString());

            String errorResult = "";

            WSDLValidator theValidator = new WSDLValidator();
            theValidator.addSchemas(schemaList);

            URI fileURI = new URI(wsdlFileName);
            URL fileURL = fileURI.toURL();

            result = theValidator.checkValidWSDL(fileURL.openStream());
            if (!result) {
                for (String errorMessage : theValidator.getErrors()) {
                    errorResult = errorResult.concat(errorMessage + "\n");
                }
                wsdlErrors.add(errorResult);

            } else if (!theValidator.getErrors().isEmpty()) {
                for (String errorMessage : theValidator.getErrors()) {
                    errorResult = errorResult.concat(errorMessage + "\n");
                }
                wsdlErrors.add(errorResult);
            }

            log.debug(result ? "Passed" : "Failed: \n" + errorResult);
            System.out.println(result ? "Passed" : "Failed: \n" + errorResult);

        } catch (java.io.FileNotFoundException ex) {
            log.debug(" RIWSDL:validateWSDL-FileNotFoundException  Exception trying to validate the WSDL =>" + ex.getMessage());
            wsdlConnectionError = true;
            wsdlErrors.add("WSDL file could not be accessed:  error = " + ex.getMessage());

        } catch (java.net.UnknownHostException ex) {
            log.debug(" RIWSDL:validateWSDL-UnknownHostException  Exception trying to validate the WSDL =>" + ex.getMessage());
            wsdlConnectionError = true;
            wsdlErrors.add("Could not connect to the host specified for the WSDL:  error = " + ex.getMessage());

        } catch (java.net.ConnectException ex) {
            log.debug(" RIWSDL:validateWSDL-ConnectException  Exception trying to validate the WSDL =>" + ex.getMessage());
            wsdlConnectionError = true;
            wsdlErrors.add("WSDL file could not be accessed:  error = " + ex.getMessage());

        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug(" RIWSDL:validateWSDL  Exception trying to validate the WSDL =>" + ex.getMessage());
            System.out.println(" RIWSDL:validateWSDL  Exception trying to validate the WSDL =>" + ex.getMessage());
            wsdlErrors.add("Exception Error in validating WSDL:  error = " + ex.getMessage());
        }

        return result;
    }

    /**
     * Checks if an error occurred when trying to create the Schema Type system from the WSDL.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return false, if a WSDL schema type error occurred.
     */
    public boolean isHasSchemaTypes() {
        return hasSchemaTypes;
    }

    
    /**
     * Returns the error that occurred when trying to create the Schema Type system from the WSDL.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the type of error that occurred.
     */
    public String getSchemaTypeError() {
        return schemaTypeError;
    }
    
    
    /**
     * Checks an error occurred when trying to access the WSDL.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return true, if a WSDL access error occurred.
     */
    public boolean isWsdlConnectionError() {
        return wsdlConnectionError;
    }

    /**
     * Checks if an error occurred when trying to access the Schema.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return true, if a schema access error occurred.
     */
    public boolean isSchemaConnectionError() {
        return schemaConnectionError;
    }
    
    
    /**
     * Checks if is schema documents exist.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return true, if is schema documents exist
     */
    public boolean isSchemaDocumentsExist() {
        return schemaDocumentsExist;
    }

    /**
     * Sets the schema documents exist.
     *
     * @param schemaDocumentsExist the new schema documents exist
     */
    private void setSchemaDocumentsExist(boolean schemaDocumentsExist) {
        this.schemaDocumentsExist = schemaDocumentsExist;
    }

    /**
     * Gets the wsdl schema node sections and ur ls.
     *
     * @return the wsdl schema node sections and ur ls
     */
    public ArrayList getWsdlSchemaNodeSectionsAndURLs() {
        ArrayList wsdlSchemaElements = new ArrayList();
        Iterator schemaURLIterator = wsdlSchemas.values().iterator();
        while (schemaURLIterator.hasNext()) {
            wsdlSchemaElements.add(schemaURLIterator.next());
        }

        for (Node thisNode : wsdlSchemaSections) {
            wsdlSchemaElements.add(thisNode);
        }
        return wsdlSchemaElements;
    }

    /**
     * Gets the wsdl schemas.
     *
     * @return the wsdl schemas
     */
    public Map<String, URL> getWsdlSchemas() {
        return wsdlSchemas;
    }

    /**
     * Gets the wsdl file name.
     *
     * @return the wsdl file name
     */
    public String getWsdlFileName() {
        return wsdlFileName;
    }

    /**
     * Checks if is wsdl validated against schema.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return true, if is wsdl validated against schema
     */
    public boolean isWsdlValidatedAgainstSchema() {
        return wsdlValidatedAgainstSchema;
    }

    /**
     * Gets the wsdl errors.
     *
     * @return the wsdl errors
     */
    public List<String> getWsdlErrors() {
        return wsdlErrors;
    }

    /**
     * Gets the service names.
     *
     * @return the service names
     */
    public Map getServiceNames() {
        Map<String, QName> servicesMap = new HashMap<String, QName>();
        if (this.wsdlValidatedAgainstSchema) {
            Map services = wsdl.getServices();
            Iterator servicesIterator = services.keySet().iterator();
            while (servicesIterator.hasNext()) {
                QName theService = (QName) servicesIterator.next();
                servicesMap.put(theService.getLocalPart(), theService);
            }
        }
        return servicesMap;
    }

    /**
     * Gets the service port names.
     *
     * @param service the service
     * @return the service port names
     */
    public Map getServicePortNames(QName service) {
        Map<String, QName> portMap = new HashMap<String, QName>();
        if (this.wsdlValidatedAgainstSchema) {
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
        }
        return portMap;
    }

    /**
     * Gets the service port address.
     *
     * @param service the service
     * @param port the port
     * @return the service port address
     */
    public String getServicePortAddress(QName service, String port) {
        String servicePortAddress = null;
        if (this.wsdlValidatedAgainstSchema) {
            Service requestedService = wsdl.getService(service);
            if (requestedService != null) {
                Port requestedPort = requestedService.getPort(port);
                if (requestedPort != null) {
                    List<ExtensibilityElement> theExtensions = requestedPort.getExtensibilityElements();
                    if (!theExtensions.isEmpty()) {
                        for (ExtensibilityElement thisElement : theExtensions) {
                            if (thisElement.getElementType().getLocalPart().equals("address")) {

                                if (thisElement instanceof SOAPAddress) {

                                    SOAPAddress theAddress = (SOAPAddress) thisElement;
                                    servicePortAddress = theAddress.getLocationURI();
                                } else if (thisElement instanceof HTTPAddress) {

                                    HTTPAddress theAddress = (HTTPAddress) thisElement;
                                    servicePortAddress = theAddress.getLocationURI();
                                } else if (thisElement.getElementType().getNamespaceURI().contains("ftp")) {

                                    UnknownExtensibilityElement theAddress = (UnknownExtensibilityElement) thisElement;
                                    servicePortAddress = theAddress.getElement().getAttribute("location");
                                }
                            } else {
                                System.out.println("Unknown Address Type??");
                            }
                        }
                    }

                }

            }

        }
        return servicePortAddress;
    }

    /**
     * Gets the service port transport type.
     *
     * @param service the service
     * @param port the port
     * @return the service port transport type
     */
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
                        // Try and treat an unseen/unknown binding command as an FTP Binding.  Don't know how else
                        // to resolve at this point.
                        transportType = "FTP:GET";
                    }
                }
            }
        }
        return transportType;
    }

    /**
     * Gets the service port operation names.
     *
     * @param service the service
     * @param port the port
     * @return the service port operation names
     */
    public List<String> getServicePortOperationNames(QName service, String port) {
        List<String> servicePortOperationNames = new ArrayList<String>();
        if (this.wsdlValidatedAgainstSchema) {
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
        }
        return servicePortOperationNames;
    }

    /**
     * Gets the all operation specifications.
     *
     * @return the all operation specifications
     */
    public ArrayList<OperationSpecification> getAllOperationSpecifications() {
        ArrayList<OperationSpecification> returnList = new ArrayList<OperationSpecification>();

        Map serviceMap = getServiceNames();
        Iterator<String> svcMapIterator = serviceMap.keySet().iterator();
        while (svcMapIterator.hasNext()) {
            QName serviceQName = (QName) serviceMap.get((String) svcMapIterator.next());
            Map portMap = getServicePortNames(serviceQName);
            Iterator<String> portIterator = portMap.keySet().iterator();
            while (portIterator.hasNext()) {
                String thisPort = portIterator.next();
                Map<String, OperationSpecification> opMap = getServicePortOperationInformation(serviceQName, thisPort);
                Iterator<String> opIterator = opMap.keySet().iterator();
                while (opIterator.hasNext()) {
                    String operationName = opIterator.next();
                    OperationSpecification thisOpSpec = opMap.get(operationName);
                    returnList.add(thisOpSpec);
                }
            }
        }
        return returnList;
    }

    /**
     * Gets the operation specification.
     *
     * @param serviceName the service name
     * @param portName the port name
     * @param operationName the operation name
     * @return the operation specification
     * @throws Exception the exception
     */
    public OperationSpecification getOperationSpecification(String serviceName, String portName, String operationName) throws Exception {
        OperationSpecification returnOperation = new OperationSpecification();

        Map serviceMap = getServiceNames();
        Iterator<String> svcMapIterator = serviceMap.keySet().iterator();
        while (svcMapIterator.hasNext()) {
            QName serviceQName = (QName) serviceMap.get((String) svcMapIterator.next());
            if (serviceQName.getLocalPart().equals(serviceName)) {
                Map portMap = getServicePortNames(serviceQName);
                Iterator<String> portIterator = portMap.keySet().iterator();
                while (portIterator.hasNext()) {
                    String thisPort = portIterator.next();
                    if (thisPort.equals(portName)) {
                        Map<String, OperationSpecification> opMap = getServicePortOperationInformation(serviceQName, thisPort);
                        if (opMap.containsKey(operationName)) {
                            returnOperation = opMap.get(operationName);
                            return returnOperation;
                        } else {
                            throw new Exception("No Operation Specification exists for Service: " + serviceName + " Port: " + portName + " Operation: " + operationName);
                        }
                    }
                }
            }
        }
        throw new Exception("No Operation Specification exists for Service: " + serviceName + " Port: " + portName + " Operation: " + operationName);
    }

    /**
     * Gets the operation specification.
     *
     * @param operationName the operation name
     * @return the operation specification
     * @throws Exception the exception
     */
    public ArrayList<OperationSpecification> getOperationSpecification(String operationName) throws Exception {
        ArrayList<OperationSpecification> returnOperations = new ArrayList();

        Map serviceMap = getServiceNames();
        Iterator<String> svcMapIterator = serviceMap.keySet().iterator();
        while (svcMapIterator.hasNext()) {
            QName serviceQName = (QName) serviceMap.get((String) svcMapIterator.next());
            Map portMap = getServicePortNames(serviceQName);
            Iterator<String> portIterator = portMap.keySet().iterator();
            while (portIterator.hasNext()) {
                String thisPort = portIterator.next();
                Map<String, OperationSpecification> opMap = getServicePortOperationInformation(serviceQName, thisPort);
                if (opMap.containsKey(operationName)) {
                    returnOperations.add(opMap.get(operationName));
                }
            }
        }
        if (returnOperations.size() > 0) {
            return returnOperations;
        }
        throw new Exception("No NTCIP2306 Valid Operations exists for Operation: " + operationName);
    }

    /**
     * Gets the service specifications.
     *
     * @return the service specifications
     */
    public ServiceSpecCollection getServiceSpecifications() {
        ServiceSpecCollection returnList = new ServiceSpecCollection();
        Map serviceMap = getServiceNames();
        Iterator<String> svcMapIterator = serviceMap.keySet().iterator();
        while (svcMapIterator.hasNext()) {
            QName serviceQName = (QName) serviceMap.get((String) svcMapIterator.next());
            Map portMap = getServicePortNames(serviceQName);
            Iterator<String> portIterator = portMap.keySet().iterator();
            while (portIterator.hasNext()) {
                String thisPort = portIterator.next();
                Map<String, OperationSpecification> opMap = getServicePortOperationInformation(serviceQName, thisPort);
                Iterator<String> opIterator = opMap.keySet().iterator();
                ServiceSpecification newSpec = new ServiceSpecification(serviceQName.getLocalPart(), getServicePortAddress(serviceQName, thisPort));
                while (opIterator.hasNext()) {
                    String operationName = opIterator.next();
                    OperationSpecification thisOpSpec = opMap.get(operationName);
                    newSpec.addOperationSpecification(thisOpSpec);
                    if (thisOpSpec.isPublicationCallbackOperation()) {
                        newSpec.setServiceType(ServiceSpecification.SERVICETYPE.LISTENER);
                    }
                }
                returnList.add(newSpec);
            }
        }
        return returnList;
    }

    /**
     * Gets the service port operation information.
     *
     * @param service the service
     * @param port the port
     * @return the service port operation information
     */
    public Map<String, OperationSpecification> getServicePortOperationInformation(QName service, String port) {
        Map<String, OperationSpecification> servicePortOperations = new HashMap<String, OperationSpecification>();
        if (this.wsdlValidatedAgainstSchema) {
            Service requestedService = wsdl.getService(service);
            if (requestedService != null) {
                Port requestedPort = requestedService.getPort(port);
                if (requestedPort != null) {

                    List msgPartOrder = new ArrayList();
                    msgPartOrder.add("c2cMsgAdmin");
                    msgPartOrder.add("message");

                    String portAddress = getServicePortAddress(service, port);
                    OperationSpecification.ServiceTransportType transportType = OperationSpecification.ServiceTransportType.UNDEFINED;
                    try {
                        URL tmpURL = new URL(portAddress);
                        if (tmpURL.getProtocol().equalsIgnoreCase("http")) {
                            transportType = OperationSpecification.ServiceTransportType.HTTP;
                        } else if (tmpURL.getProtocol().equalsIgnoreCase("https")) {
                            transportType = OperationSpecification.ServiceTransportType.HTTPS;
                        } else if (tmpURL.getProtocol().equalsIgnoreCase("ftp")) {
                            transportType = OperationSpecification.ServiceTransportType.FTP;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    List<BindingOperation> bindingOperations = requestedPort.getBinding().getBindingOperations();

                    for (BindingOperation thisBindingOperation : bindingOperations) {
                        OperationSpecification thisSpecification = new OperationSpecification();
                        thisSpecification.setOperationName(thisBindingOperation.getName());  // Set the Name
                        thisSpecification.setRelatedToService(service.getLocalPart());
                        thisSpecification.setRelatedToPort(port);
                        thisSpecification.setRelatedServiceTransport(transportType);

                        // Get the encoding type from the output part
                        try {
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
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        // Get the encoding type from the input part
                        try {
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
                                    } else if (encoding.equalsIgnoreCase("application/x-www-form-urlencoded")) {
                                        thisSpecification.setOperationInputEncoding(thisSpecification.XML_ENCODING);
                                    } else {
                                        thisSpecification.setErroneousSpecification(true);
                                        thisSpecification.getErrorsEncountered().add("Did not recognize Encoding type: " + encoding);
                                    }

                                } else if (thisElement instanceof HTTPUrlEncodedImpl) {
                                    if (!thisElement.getElementType().getLocalPart().equals("urlEncoded")) {
                                        thisSpecification.setErroneousSpecification(true);
                                        thisSpecification.getErrorsEncountered().add("Did not recognize the input element encoding type of the binding.");
                                    }
                                } else if (thisElement instanceof UnknownExtensibilityElement) {
                                    if (!thisElement.getElementType().getLocalPart().equals("urlEncoded")) {
                                        thisSpecification.setErroneousSpecification(true);
                                        thisSpecification.getErrorsEncountered().add("Did not recognize the input element encoding type of the binding.");
                                    }
                                } else {
                                    thisSpecification.setErroneousSpecification(true);
                                    thisSpecification.getErrorsEncountered().add("Did not recognize the input element type of the binding.");

                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        // Get the encoding type from the fault part
                        if (thisBindingOperation.getBindingFault("errorReport") != null) {
                            try {
                                List<ExtensibilityElement> faultElements = thisBindingOperation.getBindingFault("errorReport").getExtensibilityElements();
                                for (ExtensibilityElement thisElement : faultElements) {
                                    if (thisElement instanceof UnknownExtensibilityElement) {
                                        if (((UnknownExtensibilityElement) thisElement).getElementType().getLocalPart().equals("body")) {
                                            thisSpecification.setOperationFaultEncoding(thisSpecification.SOAP_ENCODING);
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            thisSpecification.setOperationFaultEncoding(thisSpecification.XML_ENCODING);
                        }

                        // Get the Action or Document attributes of the operation sub element
                        try {
                            List<ExtensibilityElement> bindingOperationElements = thisBindingOperation.getExtensibilityElements();
                            for (ExtensibilityElement thisElement : bindingOperationElements) {
                                if (thisElement instanceof SOAPOperationImpl) {
                                    SOAPOperationImpl theSOAPOperation = (SOAPOperationImpl) thisElement;
                                    thisSpecification.setSoapAction(theSOAPOperation.getSoapActionURI());
//                                System.out.println("Operation "+thisBindingOperation.getName()+" has SOAPAction "+theSOAPOperation.getSoapActionURI());
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
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        Operation opDefinition = thisBindingOperation.getOperation();

                        Input opInput = opDefinition.getInput();
                        if (opInput != null) {
                            Message opInputMessage = opInput.getMessage();
                            if (opInputMessage != null) {
                                List<Part> msgParts = opInputMessage.getOrderedParts(msgPartOrder);
                                for (Part thisPart : msgParts) {
                                    thisSpecification.getInputMessage().add(thisPart.getElementName());
                                }
                                if (msgParts.size() < opInputMessage.getParts().size()) {
                                    thisSpecification.setErroneousSpecification(true);
                                    thisSpecification.getErrorsEncountered().add(" message " + opInputMessage.getQName().toString() + " contains part naming errors.");
                                    wsdlErrors.add(opDefinition.getName() + " message " + opInputMessage.getQName().toString() + " contains part naming errors.");
                                    Iterator opInputIterator = opInput.getMessage().getParts().keySet().iterator();
                                    while (opInputIterator.hasNext()) {
                                        String thisPartName = (String) opInputIterator.next();
                                        Part thisPart = (Part) opInput.getMessage().getParts().get(thisPartName);
                                        if (!msgParts.contains(thisPart)) {
                                            thisSpecification.getInputMessage().add(thisPart.getElementName());
                                            wsdlErrors.add(opDefinition.getName() + " message " + opInputMessage.getQName().toString() + " contains part naming errors.");
                                        }
                                    }
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
                                    thisSpecification.getOutputMessage().add(thisPart.getElementName());
                                }
                                if (msgParts.size() < opOutputMessage.getParts().size()) {
                                    thisSpecification.setErroneousSpecification(true);
                                    thisSpecification.getErrorsEncountered().add(" message " + opOutputMessage.getQName().toString() + " contains part naming errors.");
                                    wsdlErrors.add(opDefinition.getName() + " message " + opOutputMessage.getQName().toString() + " contains part naming errors.");
                                    Iterator opOutputIterator = opOutput.getMessage().getParts().keySet().iterator();
                                    while (opOutputIterator.hasNext()) {
                                        String thisPartName = (String) opOutputIterator.next();
                                        Part thisPart = (Part) opOutput.getMessage().getParts().get(thisPartName);
                                        if (!msgParts.contains(thisPart)) {
                                            thisSpecification.getOutputMessage().add(thisPart.getElementName());
                                        }
                                    }
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
                                thisSpecification.getFaultMessage().add(thisPart.getElementName());
                            }
                            if (msgParts.size() < opFaultMessage.getParts().size()) {
                                thisSpecification.setErroneousSpecification(true);
                                thisSpecification.getErrorsEncountered().add(" message " + opFaultMessage.getQName().toString() + " contains part naming errors.");
                                wsdlErrors.add(opDefinition.getName() + " message " + opFaultMessage.getQName().toString() + " contains part naming errors.");
                                Iterator opFaultIterator = opFault.getMessage().getParts().keySet().iterator();
                                while (opFaultIterator.hasNext()) {
                                    String thisPartName = (String) opFaultIterator.next();
                                    Part thisPart = (Part) opFault.getMessage().getParts().get(thisPartName);
                                    if (!msgParts.contains(thisPart)) {
                                        thisSpecification.getFaultMessage().add(thisPart.getElementName());
                                    }
                                }
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
            }
        }
        return servicePortOperations;
    }


    /**
     * Gets the message.
     *
     * @param message the message
     * @return the message
     */
    public Message getMessage(QName message) {
        return wsdl.getMessage(message);
    }

    /**
     * Gets the schema type system.
     *
     * @return the schema type system
     */
    public SchemaTypeSystem getSchemaTypeSystem() {
        return sts;
    }

    /**
     * Gets the binding.
     *
     * @param namespaceURI the namespace uri
     * @param bindingName the binding name
     * @param operationName the operation name
     * @return the binding
     */
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

    /**
     * Gets the service port binding operation.
     *
     * @param service the service
     * @param port the port
     * @param operationName the operation name
     * @return the service port binding operation
     */
    public BindingOperation getServicePortBindingOperation(QName service, String port, String operationName) {
        BindingOperation requestedBindingOperation = null;

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
                        if (thisBindingOperation.getName().equals(operationName)) {
                            requestedBindingOperation = thisBindingOperation;
                            break;
                        }
                    }
                }
            }
        }
        return requestedBindingOperation;

    }

    /**
     * Gets the schema message names.
     *
     * @return the schema message names
     */
    public ArrayList<QName> getSchemaMessageNames() {
        ArrayList<QName> returnedNameList = new ArrayList<QName>();
        try {
            for (SchemaGlobalElement thisElement : sts.globalElements()) {
                String prefix = wsdl.getPrefix(thisElement.getName().getNamespaceURI());
                QName returnedMessage = new QName(thisElement.getName().getNamespaceURI(),
                        thisElement.getName().getLocalPart(), (prefix==null?"":prefix));
                returnedNameList.add(returnedMessage);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return returnedNameList;
    }

    /**
     * Gets the dialog related messages.
     *
     * @param dialogType the dialog type
     * @return the dialog related messages
     */
    public ArrayList<String> getDialogRelatedMessages(String dialogType) {
        //       PortType tmpPort;
        ArrayList<String> messageList = new ArrayList<String>();
        /**
         * Map messageMap = wsdl.getMessages(); Iterator<Message>
         * messagesIterator = messageMap.values().iterator(); while
         * (messagesIterator.hasNext()){ Message thisMessage =
         * messagesIterator.next(); Map partsMap = thisMessage.getParts();
         * Iterator<Part> partsIterator = partsMap.values().iterator();
         *
         * if (dialogType.equalsIgnoreCase("Request")){ if ((partsMap.size() ==
         * 1)){ Part thisPart = partsIterator.next(); if
         * (thisPart.getName().equals("message")){ if
         * (!messageList.contains(thisMessage.getQName().getLocalPart()))
         * messageList.add(thisMessage.getQName().getLocalPart()); } }
         *
         * } else if (dialogType.equalsIgnoreCase("Subscription")){ if
         * ((partsMap.size() == 2)){ boolean isSubscription = false; String
         * messageName = ""; while (partsIterator.hasNext()){ Part thisPart =
         * partsIterator.next(); if (thisPart.getName().equals("message")){
         * messageName = thisMessage.getQName().getLocalPart(); } else if
         * (thisPart.getName().equals("c2cMsgAdmin")){ if
         * (thisPart.getElementName().getLocalPart().equals("c2cMessageSubscription")){
         * isSubscription = true; } } } if
         * (isSubscription&&!messageName.isEmpty()){ if
         * (!messageList.contains(messageName)){ messageList.add(messageName); }
         * } }
         *
         * } else if
         * ((dialogType.equalsIgnoreCase("Update"))||(dialogType.equalsIgnoreCase("Publication"))){
         * if ((partsMap.size() == 2)){ boolean isPublication = false; String
         * messageName = ""; while (partsIterator.hasNext()){ Part thisPart =
         * partsIterator.next(); if (thisPart.getName().equals("message")){
         * messageName = thisMessage.getQName().getLocalPart(); } else if
         * (thisPart.getName().equals("c2cMsgAdmin")){ if
         * (thisPart.getElementName().getLocalPart().equals("c2cMessagePublication")){
         * isPublication = true; } } } if
         * (isPublication&&!messageName.isEmpty()){ if
         * (!messageList.contains(messageName)){ messageList.add(messageName); }
         * } }
         *
         * }
         * }
         */
        Map portTypesMap = wsdl.getAllPortTypes();
        Iterator<PortType> portTypesIterator = portTypesMap.values().iterator();
        while (portTypesIterator.hasNext()) {
            PortType tmpPort = portTypesIterator.next();
            // Check the operations related to this portType
            List<Operation> operationsList = tmpPort.getOperations();
            for (Operation theOperation : operationsList) {
                // We only care about operations that match the dialog type
                if (theOperation.getName().endsWith(dialogType)) {
                    String message = "";
                    try {
                        if (theOperation.getInput().getMessage().getQName().getLocalPart() != null) {
                            message = theOperation.getInput().getMessage().getQName().getLocalPart();
                            if (!messageList.contains(message)) {
                                messageList.add(message);
                            }
                        }
                    } catch (Exception ex) {
                    }

                    try {
                        if (theOperation.getOutput().getMessage().getQName().getLocalPart() != null) {
                            message = theOperation.getOutput().getMessage().getQName().getLocalPart();
                            if (!messageList.contains(message)) {
                                messageList.add(message);
                            }
                        }
                    } catch (Exception ex) {
                    }

                    try {
                        Map dialogFaultsMap = theOperation.getFaults();
                        Iterator<Fault> faultsMapIterator = dialogFaultsMap.values().iterator();
                        while (faultsMapIterator.hasNext()) {
                            Fault thisFault = faultsMapIterator.next();
                            if ((thisFault.getMessage() != null) && !messageList.contains(thisFault.getMessage().getQName().getLocalPart())) {
                                messageList.add(thisFault.getMessage().getQName().getLocalPart());
                            }
                        }
                    } catch (Exception ex) {
                    }

                }
            }
        }

        return messageList;
    }

    /**
     * Gets the sOAP service operations.
     *
     * @return the sOAP service operations
     */
    public ArrayList<String> getSOAPServiceOperations() {
        ArrayList<String> soapOperations = new ArrayList<String>();

        Iterator<Service> serviceIterator = wsdl.getAllServices().values().iterator();
        while (serviceIterator.hasNext()) {
            Service thisService = serviceIterator.next();
            Iterator<Port> portsIterator = thisService.getPorts().values().iterator();
            while (portsIterator.hasNext()) {
                Port thisPort = portsIterator.next();
                List<ExtensibilityElement> theExtensions = thisPort.getBinding().getExtensibilityElements();
                if (!theExtensions.isEmpty()) {
                    for (ExtensibilityElement thisElement : theExtensions) {
                        if (thisElement instanceof SOAPBinding) {
                            List<String> returnOperations = getServicePortOperationNames(thisService.getQName(), thisPort.getName());
                            ArrayList<String> returnedOperations = new ArrayList<String>(returnOperations);
                            for (String thisOperation : returnedOperations) {
                                if (!soapOperations.contains(thisOperation)) {
                                    soapOperations.add(thisOperation);
                                }
                            }

                        }
                    }
                }

            }
        }
        return soapOperations;
    }

    /**
     * Gets the sOAP service rr operations.
     *
     * @return the sOAP service rr operations
     */
    public ArrayList<String> getSOAPServiceRROperations() {
        ArrayList<String> soapOperations = new ArrayList<String>();

        Iterator<Service> serviceIterator = wsdl.getAllServices().values().iterator();
        while (serviceIterator.hasNext()) {
            Service thisService = serviceIterator.next();
            Iterator<Port> portsIterator = thisService.getPorts().values().iterator();
            while (portsIterator.hasNext()) {
                Port thisPort = portsIterator.next();
                List<ExtensibilityElement> theExtensions = thisPort.getBinding().getExtensibilityElements();
                if (!theExtensions.isEmpty()) {
                    for (ExtensibilityElement thisElement : theExtensions) {
                        if (thisElement instanceof SOAPBinding) {
                            try {
                                Map<String, OperationSpecification> opMap = getServicePortOperationInformation(thisService.getQName(), thisPort.getName());
                                ArrayList<String> returnedOperations = new ArrayList();
                                for (String opName : opMap.keySet()) {
                                    if (opMap.get(opName).isRequestResponseOperation()) {
                                        returnedOperations.add(opName);
                                    }
                                }
                                for (String thisOperation : returnedOperations) {
                                    if (!soapOperations.contains(thisOperation)) {
                                        soapOperations.add(thisOperation);
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }
                    }
                }

            }
        }
        return soapOperations;
    }

    /**
     * Gets the sOAP service sp operations.
     *
     * @return the sOAP service sp operations
     */
    public ArrayList<String> getSOAPServiceSPOperations() {
        ArrayList<String> soapOperations = new ArrayList<String>();

        Iterator<Service> serviceIterator = wsdl.getAllServices().values().iterator();
        while (serviceIterator.hasNext()) {
            Service thisService = serviceIterator.next();
            Iterator<Port> portsIterator = thisService.getPorts().values().iterator();
            while (portsIterator.hasNext()) {
                Port thisPort = portsIterator.next();
                List<ExtensibilityElement> theExtensions = thisPort.getBinding().getExtensibilityElements();
                if (!theExtensions.isEmpty()) {
                    for (ExtensibilityElement thisElement : theExtensions) {
                        if (thisElement instanceof SOAPBinding) {
                            Map<String, OperationSpecification> opMap = getServicePortOperationInformation(thisService.getQName(), thisPort.getName());
                            ArrayList<String> returnedOperations = new ArrayList();
                            for (String opName : opMap.keySet()) {
                                if (opMap.get(opName).isPublicationOperation() || opMap.get(opName).isSubscriptionOperation()) {
                                    returnedOperations.add(opName);
                                }
                            }
                            for (String thisOperation : returnedOperations) {
                                if (!soapOperations.contains(thisOperation)) {
                                    soapOperations.add(thisOperation);
                                }
                            }

                        }
                    }
                }

            }
        }
        return soapOperations;
    }

    /**
     * Gets the sOAP service sp operation specifications.
     *
     * @return the sOAP service sp operation specifications
     */
    public ArrayList<OperationSpecification> getSOAPServiceSPOperationSpecifications() {
        ArrayList<OperationSpecification> soapOperationSpecs = new ArrayList<OperationSpecification>();

        Iterator<Service> serviceIterator = wsdl.getAllServices().values().iterator();
        while (serviceIterator.hasNext()) {
            Service thisService = serviceIterator.next();
            Iterator<Port> portsIterator = thisService.getPorts().values().iterator();
            while (portsIterator.hasNext()) {
                Port thisPort = portsIterator.next();
                List<ExtensibilityElement> theExtensions = thisPort.getBinding().getExtensibilityElements();
                if (!theExtensions.isEmpty()) {
                    for (ExtensibilityElement thisElement : theExtensions) {
                        if (thisElement instanceof SOAPBinding) {
                            Map<String, OperationSpecification> opMap = getServicePortOperationInformation(thisService.getQName(), thisPort.getName());
                            ArrayList<OperationSpecification> returnedOperations = new ArrayList();
                            for (String opName : opMap.keySet()) {
                                if (opMap.get(opName).isPublicationOperation() || opMap.get(opName).isSubscriptionOperation()) {
                                    returnedOperations.add(opMap.get(opName));
                                }
                            }
                            for (OperationSpecification thisOperation : returnedOperations) {
                                if (!soapOperationSpecs.contains(thisOperation)) {
                                    soapOperationSpecs.add(thisOperation);
                                }
                            }

                        }
                    }
                }

            }
        }
        return soapOperationSpecs;
    }

    /**
     * Gets the xMLHTTP operations.
     *
     * @return the xMLHTTP operations
     */
    public ArrayList<String> getXMLHTTPOperations() {
        ArrayList<String> xmlOperations = new ArrayList<String>();

        Iterator<Service> serviceIterator = wsdl.getAllServices().values().iterator();
        while (serviceIterator.hasNext()) {
            Service thisService = serviceIterator.next();
            Iterator<Port> portsIterator = thisService.getPorts().values().iterator();
            while (portsIterator.hasNext()) {
                Port thisPort = portsIterator.next();
                List<ExtensibilityElement> theExtensions = thisPort.getBinding().getExtensibilityElements();
                if (!theExtensions.isEmpty()) {
                    for (ExtensibilityElement thisElement : theExtensions) {
                        if (thisElement instanceof HTTPBinding) {
                            List<String> returnOperations = getServicePortOperationNames(thisService.getQName(), thisPort.getName());
                            ArrayList<String> returnedOperations = new ArrayList<String>(returnOperations);
                            for (String thisOperation : returnedOperations) {
                                if (!xmlOperations.contains(thisOperation)) {
                                    xmlOperations.add(thisOperation);
                                }
                            }

                        }
                    }
                }

            }
        }
        return xmlOperations;
    }

    /**
     * Gets the xMLHTTP operations.
     *
     * @param transportVerb the transport verb
     * @return the xMLHTTP operations
     */
    public ArrayList<String> getXMLHTTPOperations(String transportVerb) {
        ArrayList<String> xmlOperations = new ArrayList<String>();

        Iterator<Service> serviceIterator = wsdl.getAllServices().values().iterator();
        while (serviceIterator.hasNext()) {
            Service thisService = serviceIterator.next();
            Iterator<Port> portsIterator = thisService.getPorts().values().iterator();
            while (portsIterator.hasNext()) {
                Port thisPort = portsIterator.next();
                List<ExtensibilityElement> theExtensions = thisPort.getBinding().getExtensibilityElements();
                if (!theExtensions.isEmpty()) {
                    for (ExtensibilityElement thisElement : theExtensions) {
                        if (thisElement instanceof HTTPBinding) {
                            HTTPBinding thisBinding = (HTTPBinding) thisElement;
                            if (thisBinding.getVerb().equals(transportVerb)) {
                                List<String> returnOperations = getServicePortOperationNames(thisService.getQName(), thisPort.getName());
                                ArrayList<String> returnedOperations = new ArrayList<String>(returnOperations);
                                for (String thisOperation : returnedOperations) {
                                    if (!xmlOperations.contains(thisOperation)) {
                                        xmlOperations.add(thisOperation);
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        return xmlOperations;
    }

    /**
     * Gets the fTP service operations.
     *
     * @return the fTP service operations
     */
    public ArrayList<String> getFTPServiceOperations() {
        ArrayList<String> ftpOperations = new ArrayList<String>();

        Iterator<Service> serviceIterator = wsdl.getAllServices().values().iterator();
        while (serviceIterator.hasNext()) {
            Service thisService = serviceIterator.next();
            Iterator<Port> portsIterator = thisService.getPorts().values().iterator();
            while (portsIterator.hasNext()) {
                Port thisPort = portsIterator.next();
                List<ExtensibilityElement> theExtensions = thisPort.getBinding().getExtensibilityElements();
                if (!theExtensions.isEmpty()) {
                    for (ExtensibilityElement thisElement : theExtensions) {
                        if (thisElement instanceof UnknownExtensibilityElement) {
                            UnknownExtensibilityElement theFTPOperation = (UnknownExtensibilityElement) thisElement;
                            if (theFTPOperation.getElementType().getNamespaceURI().equals("http://schemas.ntcip.org/wsdl/ftp/")) {

                                List<String> returnOperations = getServicePortOperationNames(thisService.getQName(), thisPort.getName());
                                ArrayList<String> returnedOperations = new ArrayList<String>(returnOperations);
                                for (String thisOperation : returnedOperations) {
                                    if (!ftpOperations.contains(thisOperation)) {
                                        ftpOperations.add(thisOperation);
                                    }
                                }

                            }

                        }
                    }
                }

            }
        }
        return ftpOperations;
    }
}
