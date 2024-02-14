/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.encoding;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.fhwa.c2cri.ntcip2306v109.status.XMLStatus;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * The Class XMLValidator performs xml and schema validation of xml documents.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class XMLValidator extends DefaultHandler implements ErrorHandler {

    /** Schema full checking feature id (http://apache.org/xml/features/validation/newSchema-full-checking). */
    protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID = "http://apache.org/xml/features/validation/schema-full-checking";
    /** Honour all newSchema locations feature id (http://apache.org/xml/features/honour-all-schemaLocations). */
    protected static final String HONOUR_ALL_SCHEMA_LOCATIONS_ID = "http://apache.org/xml/features/honour-all-schemaLocations";
    /** Validate newSchema annotations feature id (http://apache.org/xml/features/validate-annotations) */
    protected static final String VALIDATE_ANNOTATIONS_ID = "http://apache.org/xml/features/validate-annotations";
    /** Generate synthetic newSchema annotations feature id (http://apache.org/xml/features/generate-synthetic-annotations). */
    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS_ID = "http://apache.org/xml/features/generate-synthetic-annotations";
    // default settings
    /** Default newSchema language (http://www.w3.org/2001/XMLSchema). */
    protected static final String DEFAULT_SCHEMA_LANGUAGE = XMLConstants.W3C_XML_SCHEMA_NS_URI;
    /** Default repetition (1). */
    protected static final int DEFAULT_REPETITION = 1;
    /** Default validation source. Options are sax, dom and stream*/
    protected static final String DEFAULT_VALIDATION_SOURCE = "sax";
    /** Default newSchema full checking support (false). */
    protected static final boolean DEFAULT_SCHEMA_FULL_CHECKING = false;
    /** Default honor all newSchema locations (false). */
    protected static final boolean DEFAULT_HONOUR_ALL_SCHEMA_LOCATIONS = false;
    /** Default validate newSchema annotations (false). */
    protected static final boolean DEFAULT_VALIDATE_ANNOTATIONS = false;
    /** Default generate synthetic newSchema annotations (false). */
    protected static final boolean DEFAULT_GENERATE_SYNTHETIC_ANNOTATIONS = false;
    /** Default memory usage report (false). */
    protected static final boolean DEFAULT_MEMORY_USAGE = false;
    
    /** The parse result. */
    protected boolean parseResult = false;
    
    /** The out. */
    protected PrintWriter fOut = new PrintWriter(System.out);
    
    /** The valid xmlv1. */
    private boolean validXmlv1 = false;
    
    /** The utf8 encoded. */
    private boolean utf8Encoded = false;
    
    /** The valid to schemas. */
    private boolean validToSchemas = false;
    
    /** The message type. */
    private QName messageType;
    
    /** The first invalid char. */
    private int firstInvalidChar = -1;
    
    /** The total invalid characters. */
    private int totalInvalidCharacters = 0;
    
    /** The xml valid errors. */
    private String xmlValidErrors = "";
    
    /** The schema. */
    private Schema schema = null;
    
    /** The performing schema validation. */
    private boolean performingSchemaValidation = false;
    
    /** The error list. */
    private final List<String> errorList = new ArrayList<String>();
    
    /** The parser error list. */
    private final ArrayList<String> parserErrorList = new ArrayList<String>();
    
    /** The field error list. */
    private final ArrayList<String> fieldErrorList = new ArrayList<String>();
    
    /** The value error list. */
    private final ArrayList<String> valueErrorList = new ArrayList<String>();
    
    /** The schema validation error list. */
    private final ArrayList<String> schemaValidationOtherErrorList = new ArrayList<String>();
    
    /** The schema validation content error list. */
    private final ArrayList<String> schemaValidationContentErrorList = new ArrayList();
    
    /** The schema validation value error list. */
    private final ArrayList<String> schemaValidationValueErrorList = new ArrayList();
    
    /** The test assertion list. */
    private final ArrayList<TestAssertion> testAssertionList = new ArrayList<>();

    /**
     * Instantiates a new xML validator.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public XMLValidator() {
        super();
    }

    /**
     * Instantiates a new xML validator.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param schemas the schemas
     */
    public XMLValidator(ArrayList schemas) {
        super();
        setSchema(createSchema(schemas));
    }

    /**
     * Checks if is valid xml.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param encodedMessage the encoded message
     * @return true, if is valid xml
     */
    public boolean isValidXML(byte[] encodedMessage) {
        performingSchemaValidation = false;
        // Check for XML version 1.0 document
        setValidXmlv1(isValidXML(new String(encodedMessage)));
        for (String xmlError : getErrors()) {
            setXmlValidErrors(getXmlValidErrors().concat(xmlError) + "\n");
        }
    testAssertionList.add(new TestAssertion("4.1.1.a",isValidXmlv1(),"The XML message or file shall be an XML version 1.0 document. ",getXmlValidErrors()));	   
        
        setMessageType(getXMLMessageType(new String(encodedMessage)));

        // Check for UTF-8 encoding
        //utf8Encoded = theValidator.isUTF8Encoded(encodedMessage);
        String utf8EncodingException = "";
        try {
            setUtf8Encoded(isValidUTF8(new String(encodedMessage, "UTF-8")));
        } catch (Exception ex) {
            ex.printStackTrace();
            utf8EncodingException = ex.getMessage();
            setUtf8Encoded(false);
        }
       testAssertionList.add(new TestAssertion("4.1.1.b",isUtf8Encoded(),"The character set shall be UTF-8. ",utf8EncodingException));	   
       return isValidXmlv1() && isUtf8Encoded() && (getXmlValidErrors().equals(""));
    }

    /**
     * Sets the schema reference list.
     *
     * @param schemaReferences the new schema reference list
     */
    public void setSchemaReferenceList(ArrayList schemaReferences) {
        setSchema(createSchema(schemaReferences));
    }

    /**
     * Creates the schema.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param schemas the schemas
     * @return the schema
     */
    private Schema createSchema(ArrayList schemas) {
        // variables
        String schemaLanguage = DEFAULT_SCHEMA_LANGUAGE;
  //      boolean schemaFullChecking = DEFAULT_SCHEMA_FULL_CHECKING;
        boolean schemaFullChecking = true;
        boolean honourAllSchemaLocations = DEFAULT_HONOUR_ALL_SCHEMA_LOCATIONS;
        boolean validateAnnotations = DEFAULT_VALIDATE_ANNOTATIONS;
        boolean generateSyntheticAnnotations = DEFAULT_GENERATE_SYNTHETIC_ANNOTATIONS;

        setValidToSchemas(false);
        Schema newSchema = null;
        try {
            // Create SchemaFactory and configure
            SchemaFactory factory = SchemaFactory.newInstance(schemaLanguage);
            factory.setErrorHandler(this);

            try {
                factory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, schemaFullChecking);
            } catch (SAXNotRecognizedException e) {
                System.err.println("warning: SchemaFactory does not recognize feature (" + SCHEMA_FULL_CHECKING_FEATURE_ID + ")");
            } catch (SAXNotSupportedException e) {
                System.err.println("warning: SchemaFactory does not support feature (" + SCHEMA_FULL_CHECKING_FEATURE_ID + ")");
            }
            try {
                factory.setFeature(HONOUR_ALL_SCHEMA_LOCATIONS_ID, honourAllSchemaLocations);
            } catch (SAXNotRecognizedException e) {
                System.err.println("warning: SchemaFactory does not recognize feature (" + HONOUR_ALL_SCHEMA_LOCATIONS_ID + ")");
            } catch (SAXNotSupportedException e) {
                System.err.println("warning: SchemaFactory does not support feature (" + HONOUR_ALL_SCHEMA_LOCATIONS_ID + ")");
            }
            try {
                factory.setFeature(VALIDATE_ANNOTATIONS_ID, validateAnnotations);
            } catch (SAXNotRecognizedException e) {
                System.err.println("warning: SchemaFactory does not recognize feature (" + VALIDATE_ANNOTATIONS_ID + ")");
            } catch (SAXNotSupportedException e) {
                System.err.println("warning: SchemaFactory does not support feature (" + VALIDATE_ANNOTATIONS_ID + ")");
            }
            try {
                factory.setFeature(GENERATE_SYNTHETIC_ANNOTATIONS_ID, generateSyntheticAnnotations);
            } catch (SAXNotRecognizedException e) {
                System.err.println("warning: SchemaFactory does not recognize feature (" + GENERATE_SYNTHETIC_ANNOTATIONS_ID + ")");
            } catch (SAXNotSupportedException e) {
                System.err.println("warning: SchemaFactory does not support feature (" + GENERATE_SYNTHETIC_ANNOTATIONS_ID + ")");
            }

            // Build Schema from sources
            Source[] schemaSources = new Source[schemas.size()];

            if (schemas != null && schemas.size() > 0) {
                final int length = schemas.size();

//                StreamSource[] sources = new StreamSource[length];
                for (int j = 0; j < length; ++j) {
                    if (schemas.get(j) instanceof String) {
                        schemaSources[j] = new StreamSource((String) schemas.get(j));
                        System.out.println("XMLValidator: Added schema ["+j+"] "+schemas.get(j));
                    } else if (schemas.get(j) instanceof URL) {
                        schemaSources[j] = new StreamSource(((URL) schemas.get(j)).toString());
                        System.out.println("XMLValidator: Added schema ["+j+"] "+((URL)schemas.get(j)).toString());
                    } else {
                        schemaSources[j] = new DOMSource((org.w3c.dom.Node) schemas.get(j));
                        System.out.println("XMLValidator: Added schema ["+j+"] "+((org.w3c.dom.Node)schemas.get(j)).getLocalName());
                    }
                }
                newSchema = factory.newSchema(schemaSources);

            } else {
                newSchema = factory.newSchema();
            }
        } catch (SAXException saxe) {
            errorList.add("XMLValidator.createSchema: " + saxe.getMessage());
        }
        return newSchema;
    }

    /**
     * Checks if is schema validated xml.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param XMLSource the xML source
     * @param schemas the schemas
     * @return true, if is schema validated xml
     */
    public boolean isSchemaValidatedXML(String XMLSource, List<String> schemas) {
        ArrayList convertedList = new ArrayList();
        for (String thisString : schemas) {
            convertedList.add(thisString);
        }
        return isXMLValidatedToSchema(convertedList, XMLSource);
    }

    /**
     * Checks if is xML validated to schema.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param schemas the schemas
     * @param xmlSource the xml source
     * @return true, if is xML validated to schema
     */
    public boolean isXMLValidatedToSchema(ArrayList schemas, String xmlSource) {
        clearErrors();
        setSchema(createSchema(schemas));
//            instances = new Vector();
//            instances.add(xmlSource);

        return isXMLValidatedToSchema(xmlSource);
    } // checkValid(String)

    /**
     * Checks if is xML validated to schema.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param xmlSource the xml source
     * @return true, if is xML validated to schema
     */
    public boolean isXMLValidatedToSchema(String xmlSource) {

        int repetition = DEFAULT_REPETITION;
        String validationSource = DEFAULT_VALIDATION_SOURCE;
        boolean schemaFullChecking = true;
        boolean honourAllSchemaLocations = DEFAULT_HONOUR_ALL_SCHEMA_LOCATIONS;
        boolean validateAnnotations = DEFAULT_VALIDATE_ANNOTATIONS;
        boolean generateSyntheticAnnotations = DEFAULT_GENERATE_SYNTHETIC_ANNOTATIONS;
        boolean memoryUsage = DEFAULT_MEMORY_USAGE;
        boolean validationResults = false;
        
        performingSchemaValidation = true;
        setValidToSchemas(false);

        setParseResult(false);
        if (schema != null) {
            try {

                // Setup validator and input source.
                Validator validator = schema.newValidator();
                validator.setErrorHandler(this);

                try {
                    validator.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, schemaFullChecking);
                } catch (SAXNotRecognizedException e) {
                    System.err.println("warning: Validator does not recognize feature (" + SCHEMA_FULL_CHECKING_FEATURE_ID + ")");
                } catch (SAXNotSupportedException e) {
                    System.err.println("warning: Validator does not support feature (" + SCHEMA_FULL_CHECKING_FEATURE_ID + ")");
                }
                try {
                    validator.setFeature(HONOUR_ALL_SCHEMA_LOCATIONS_ID, honourAllSchemaLocations);
                } catch (SAXNotRecognizedException e) {
                    System.err.println("warning: Validator does not recognize feature (" + HONOUR_ALL_SCHEMA_LOCATIONS_ID + ")");
                } catch (SAXNotSupportedException e) {
                    System.err.println("warning: Validator does not support feature (" + HONOUR_ALL_SCHEMA_LOCATIONS_ID + ")");
                }
                try {
                    validator.setFeature(VALIDATE_ANNOTATIONS_ID, validateAnnotations);
                } catch (SAXNotRecognizedException e) {
                    System.err.println("warning: Validator does not recognize feature (" + VALIDATE_ANNOTATIONS_ID + ")");
                } catch (SAXNotSupportedException e) {
                    System.err.println("warning: Validator does not support feature (" + VALIDATE_ANNOTATIONS_ID + ")");
                }
                try {
                    validator.setFeature(GENERATE_SYNTHETIC_ANNOTATIONS_ID, generateSyntheticAnnotations);
                } catch (SAXNotRecognizedException e) {
                    System.err.println("warning: Validator does not recognize feature (" + GENERATE_SYNTHETIC_ANNOTATIONS_ID + ")");
                } catch (SAXNotSupportedException e) {
                    System.err.println("warning: Validator does not support feature (" + GENERATE_SYNTHETIC_ANNOTATIONS_ID + ")");
                }

                validationSource = "sax";
                setParseResult(true);
                // Validate instance documents
                if (validationSource.equals("sax")) {
                    // SAXSource
                    XMLReader reader = XMLReaderFactory.createXMLReader();
                    SAXSource source = new SAXSource(reader, new InputSource(new ByteArrayInputStream(xmlSource.getBytes("UTF-8"))));
                    validationResults = isvalid(validator, source, "RI XML Input", repetition, memoryUsage);
                }

            } catch (SAXParseException e) {
                e.printStackTrace();
                // ignore
            } catch (Exception e) {
                System.err.println("error: Parse error occurred - " + e.getMessage());
                if (e instanceof SAXException) {
                    Exception nested = ((SAXException) e).getException();
                    if (nested != null) {
                        e = nested;
                    }
                }
                e.printStackTrace(System.err);
            }

            setMessageType(getXMLMessageType(xmlSource));
        } else {
            System.err.println("XMLValidator::isXMLValidatedToSchema  No Schemas Were Loaded.");
        }
        // Check for UTF-8 encoding
        setUtf8Encoded(isValidUTF8(xmlSource));

        setValidToSchemas(isParseResult());

        return isParseResult();
    }

    /**
     * Checks if is xML validated to schema.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param xmlSource the xml source
     * @return true, if is xML validated to schema
     */
    public boolean isXMLValidatedToSchema(byte[] xmlSource) {

        int repetition = DEFAULT_REPETITION;
        String validationSource = DEFAULT_VALIDATION_SOURCE;
//        boolean schemaFullChecking = DEFAULT_SCHEMA_FULL_CHECKING;
        boolean schemaFullChecking = true;
        boolean honourAllSchemaLocations = DEFAULT_HONOUR_ALL_SCHEMA_LOCATIONS;
        boolean validateAnnotations = DEFAULT_VALIDATE_ANNOTATIONS;
        boolean generateSyntheticAnnotations = DEFAULT_GENERATE_SYNTHETIC_ANNOTATIONS;
        boolean memoryUsage = DEFAULT_MEMORY_USAGE;
        boolean validationResults = false;
        
        performingSchemaValidation = true;
        setValidToSchemas(false);

        setParseResult(false);
        if (schema != null) {
            try {


                // Setup validator and input source.
                Validator validator = schema.newValidator();
                validator.setErrorHandler(this);

                try {
                    validator.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, schemaFullChecking);
                } catch (SAXNotRecognizedException e) {
                    System.err.println("warning: Validator does not recognize feature (" + SCHEMA_FULL_CHECKING_FEATURE_ID + ")");
                } catch (SAXNotSupportedException e) {
                    System.err.println("warning: Validator does not support feature (" + SCHEMA_FULL_CHECKING_FEATURE_ID + ")");
                }
                try {
                    validator.setFeature(HONOUR_ALL_SCHEMA_LOCATIONS_ID, honourAllSchemaLocations);
                } catch (SAXNotRecognizedException e) {
                    System.err.println("warning: Validator does not recognize feature (" + HONOUR_ALL_SCHEMA_LOCATIONS_ID + ")");
                } catch (SAXNotSupportedException e) {
                    System.err.println("warning: Validator does not support feature (" + HONOUR_ALL_SCHEMA_LOCATIONS_ID + ")");
                }
                try {
                    validator.setFeature(VALIDATE_ANNOTATIONS_ID, validateAnnotations);
                } catch (SAXNotRecognizedException e) {
                    System.err.println("warning: Validator does not recognize feature (" + VALIDATE_ANNOTATIONS_ID + ")");
                } catch (SAXNotSupportedException e) {
                    System.err.println("warning: Validator does not support feature (" + VALIDATE_ANNOTATIONS_ID + ")");
                }
                try {
                    validator.setFeature(GENERATE_SYNTHETIC_ANNOTATIONS_ID, generateSyntheticAnnotations);
                } catch (SAXNotRecognizedException e) {
                    System.err.println("warning: Validator does not recognize feature (" + GENERATE_SYNTHETIC_ANNOTATIONS_ID + ")");
                } catch (SAXNotSupportedException e) {
                    System.err.println("warning: Validator does not support feature (" + GENERATE_SYNTHETIC_ANNOTATIONS_ID + ")");
                }

                validationSource = "sax";
                setParseResult(true);
                // Validate instance documents
                if (validationSource.equals("sax")) {
                    // SAXSource
                    XMLReader reader = XMLReaderFactory.createXMLReader();
                    SAXSource source = new SAXSource(reader, new InputSource(new ByteArrayInputStream(xmlSource)));
                    validationResults = isvalid(validator, source, "RI XML Input", repetition, memoryUsage);
                    //	                    }
                }

            } catch (SAXParseException e) {
                e.printStackTrace();
                // ignore
            } catch (Exception e) {
                System.err.println("error: Parse error occurred - " + e.getMessage());
                if (e instanceof SAXException) {
                    Exception nested = ((SAXException) e).getException();
                    if (nested != null) {
                        e = nested;
                    }
                }
                e.printStackTrace(System.err);
            }

            setMessageType(getXMLMessageType(new ByteArrayInputStream(xmlSource)));
        } else {
            System.err.println("XMLValidator::isXMLValidatedToSchema  No Schemas Were Loaded.");
        }
        // Check for UTF-8 encoding
        String stringVersion = "";
        try{
            stringVersion = new String(xmlSource,"UTF-8");
        } catch (Exception ex){
            
        }
        setUtf8Encoded(isValidUTF8(stringVersion));

        setValidToSchemas(isParseResult());

        return isParseResult();
    }
    
    
    /**
     * This method ensures that the output String has only     
     * valid XML unicode characters as specified by the     
     * XML 1.0 standard. For reference, please see     
     * <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the     
     * standard</a>. This method will return an empty      
     * String if the input is null or empty.     
     * @param in The String whose non-valid characters we want to remove.
     */
    public boolean isValidUTF8(String in) {
        boolean validInput = true;
        char current; // Used to reference the current character.
        if (in == null || ("".equals(in))) {
            return true; // vacancy test.
        }
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i);
            // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
            if (!((current == 0x9)
                    || (current == 0xA)
                    || (current == 0xD)
                    || ((current >= 0x20) && (current <= 0xD7FF))
                    || ((current >= 0xE000) && (current <= 0xFFFD))
                    || ((current >= 0x10000) && (current <= 0x10FFFF)))) {
                validInput = false;
                totalInvalidCharacters++;
                if (firstInvalidChar < 0) {
                    firstInvalidChar = i + 1;
                }
            }
        }
        return validInput;
    }

    /**
     * Checks if is valid xml.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param XMLSource the xML source
     * @return true, if is valid xml
     */
    public boolean isValidXML(String XMLSource) {


        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();
            System.out.println("Parser is validating " + sp.isValidating());
            System.out.println("Parser is namespaceaware " + sp.isNamespaceAware());
            //parse the file and also register this class for call backs

            System.out.println("Parsing: " + XMLSource);
            InputStream is = new ByteArrayInputStream(XMLSource.getBytes("UTF-8"));


            clearErrors();
            parseResult = true;

            sp.parse(is, this);
            is.close();

        } catch (SAXException se) {
            se.printStackTrace();
            parseResult = false;
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            parseResult = false;
        } catch (IOException ie) {
            ie.printStackTrace();
            parseResult = false;
        }

        return parseResult;
    } // checkValid(String)

    /**
     * Gets the xML message type.
     *
     * @param encodedMessage the encoded message
     * @return the xML message type
     */
    public QName getXMLMessageType(String encodedMessage) {
        QName result = null;
        try {
            XmlObject thisObject = XmlObject.Factory.parse(encodedMessage);
            XmlCursor xmlc = thisObject.newCursor();
            xmlc.toParent();
            xmlc.toFirstChild();
            result = xmlc.getName();


            xmlc.dispose();
        } catch (Exception ex) {
        }
        return result;
    }

    /**
     * Gets the xML message type.
     *
     * @param encodedMessage the encoded message
     * @return the xML message type
     */
    public QName getXMLMessageType(InputStream encodedMessage) {
        QName result = null;
        try {
            XmlObject thisObject = XmlObject.Factory.parse(encodedMessage);
            XmlCursor xmlc = thisObject.newCursor();
            xmlc.toParent();
            xmlc.toFirstChild();
            result = xmlc.getName();


            xmlc.dispose();
        } catch (Exception ex) {
        }
        return result;
    }

    
    /**
     * Checks if is valid.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param validator the validator
     * @param source the source
     * @param systemId the system id
     * @param repetitions the repetitions
     * @param memoryUsage the memory usage
     * @return true, if is valid
     */
    public boolean isvalid(Validator validator,
            Source source, String systemId,
            int repetitions, boolean memoryUsage) {
        try {
            long timeBefore = System.currentTimeMillis();
            long memoryBefore = Runtime.getRuntime().freeMemory();
            for (int j = 0; j < repetitions; ++j) {
                validator.validate(source);
            }
            long memoryAfter = Runtime.getRuntime().freeMemory();
            long timeAfter = System.currentTimeMillis();

            long time = timeAfter - timeBefore;
            long memory = memoryUsage
                    ? memoryBefore - memoryAfter : Long.MIN_VALUE;
            printResults(fOut, systemId, time, memory, repetitions);
        } catch (SAXParseException e) {
            // ignore
            return false;
        } catch (Exception e) {
            System.err.println("error: Parse error occurred - " + e.getMessage());
            Exception se = e;
            if (e instanceof SAXException) {
                se = ((SAXException) e).getException();
            }
            if (se != null) {
                se.printStackTrace(System.err);
            } else {
                e.printStackTrace(System.err);
            }
            return false;
        }
        return true;
    } // validate(Validator,Source,String,int,boolean)

    /**
     * Gets the test assertion list.
     *
     * @return the test assertion list
     */
    public ArrayList<TestAssertion> getTestAssertionList() {
        return testAssertionList;
    }   
    
    /**
     * Gets the xML status.
     *
     * @return the xML status
     */
    public XMLStatus getXMLStatus() {
        XMLStatus xmlStatus = new XMLStatus();

        xmlStatus.setUTF8CharSet(isUtf8Encoded());

        xmlStatus.setXMLv1Document(isValidXmlv1());
        xmlStatus.setXMLv1Header(parseResult);
        xmlStatus.setXmlValidatedToWSDLSchemas(isValidToSchemas());

        StringBuilder xmlErrors = new StringBuilder();

        xmlErrors.append("General Errors: \n");
        for (String error : errorList) {
            xmlErrors.append(error).append("\n");
        }
        
        if (!xmlStatus.isUTF8CharSet()){
            xmlErrors.append("The character at index "+getFirstInvalidChar() + " violates UTF-8 encoding conventions.  A total of "+getTotalInvalidCharacters()+ " invalid characters were found in the message.");
        };
        
        xmlErrors.append("Parsing Errors: \n");
        for (String error : parserErrorList) {
            xmlErrors.append(error).append("\n");
        }
        xmlErrors.append("Field Errors: \n");
        for (String error : fieldErrorList) {
            xmlErrors.append(error).append("\n");
        }
        xmlErrors.append("Value Errors: \n");
        for (String error : valueErrorList) {
            xmlErrors.append(error).append("\n");
        }

        xmlStatus.addXMLError(xmlErrors.toString());

        
        StringBuilder xmlSchemaValidationErrors = new StringBuilder();

        xmlSchemaValidationErrors.append("SchemaValidation Errors: \n");
        for (String error : schemaValidationOtherErrorList) {
            xmlSchemaValidationErrors.append(error).append("\n");
        }
        
        xmlStatus.addXMLSchemaValidationError(xmlSchemaValidationErrors.toString());
        xmlStatus.setTestAssertionList(testAssertionList);
        
        return xmlStatus;
    }

    /**
     * Checks if is valid to schemas.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is valid to schemas
     */
    public boolean isValidToSchemas() {
        return validToSchemas;
    }

    /**
     * Sets the valid to schemas.
     *
     * @param validToSchemas the new valid to schemas
     */
    private void setValidToSchemas(boolean validToSchemas) {
        this.validToSchemas = validToSchemas;
    }

    /**
     * Gets the message type.
     *
     * @return the message type
     */
    public QName getMessageType() {
        return messageType;
    }

    /**
     * Checks if is utf8 encoded.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is utf8 encoded
     */
    public boolean isUtf8Encoded() {
        return utf8Encoded;
    }

    /**
     * Sets the message type.
     *
     * @param messageType the new message type
     */
    private void setMessageType(QName messageType) {
        this.messageType = messageType;
    }

    /**
     * Sets the utf8 encoded.
     *
     * @param utf8Encoded the new utf8 encoded
     */
    private void setUtf8Encoded(boolean utf8Encoded) {
        this.utf8Encoded = utf8Encoded;
    }

    /**
     * Sets the valid xmlv1.
     *
     * @param validXmlv1 the new valid xmlv1
     */
    private void setValidXmlv1(boolean validXmlv1) {
        this.validXmlv1 = validXmlv1;
    }

    /**
     * Checks if is valid xmlv1.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is valid xmlv1
     */
    public boolean isValidXmlv1() {
        return validXmlv1;
    }

    /**
     * Gets the first invalid char.
     *
     * @return the first invalid char
     */
    public int getFirstInvalidChar() {
        return firstInvalidChar;
    }

    /**
     * Sets the first invalid char.
     *
     * @param firstInvalidChar the new first invalid char
     */
    private void setFirstInvalidChar(int firstInvalidChar) {
        this.firstInvalidChar = firstInvalidChar;
    }

    /**
     * Checks if is parses the result.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is parses the result
     */
    private boolean isParseResult() {
        return parseResult;
    }

    /**
     * Sets the parses the result.
     *
     * @param parseResult the new parses the result
     */
    private void setParseResult(boolean parseResult) {
        this.parseResult = parseResult;
    }

    /**
     * Gets the total invalid characters.
     *
     * @return the total invalid characters
     */
    public int getTotalInvalidCharacters() {
        return totalInvalidCharacters;
    }

    /**
     * Sets the total invalid characters.
     *
     * @param totalInvalidCharacters the new total invalid characters
     */
    private void setTotalInvalidCharacters(int totalInvalidCharacters) {
        this.totalInvalidCharacters = totalInvalidCharacters;
    }

    /**
     * Sets the xml valid errors.
     *
     * @param xmlValidErrors the new xml valid errors
     */
    private void setXmlValidErrors(String xmlValidErrors) {
        this.xmlValidErrors = xmlValidErrors;
    }

    /**
     * Gets the xml valid errors.
     *
     * @return the xml valid errors
     */
    public String getXmlValidErrors() {
        return xmlValidErrors;
    }

    /**
     * Gets the schema.
     *
     * @return the schema
     */
    private Schema getSchema() {
        return schema;
    }

    /**
     * Sets the schema.
     *
     * @param schema the new schema
     */
    private void setSchema(Schema schema) {
        this.schema = schema;
    }

    /**
     * Errors are present if the validation failed.  This method
     * returns the error list.
     *
     * @return List - the list of errors found during validation.
     */
    public List<String> getErrors() {
        return errorList;
    }

    /**
     * Gets the field error list.
     *
     * @return the field error list
     */
    public ArrayList<String> getFieldErrorList() {
        return fieldErrorList;
    }

    /**
     * Gets the parser error list.
     *
     * @return the parser error list
     */
    public ArrayList<String> getParserErrorList() {
        return parserErrorList;
    }

    /**
     * Gets the value error list.
     *
     * @return the value error list
     */
    public ArrayList<String> getValueErrorList() {
        return valueErrorList;
    }

    /**
     * Adds the parser error.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param error the error
     */
    private void addParserError(String error) {
        parserErrorList.add(error);
    }

    /**
     * Adds the value error.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param error the error
     */
    private void addValueError(String error) {
        valueErrorList.add(error);
    }

    /**
     * Adds the field error.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param error the error
     */
    private void addFieldError(String error) {
        fieldErrorList.add(error);
    }

    /**
     * Gets the schema validation content error list.
     *
     * @return the schema validation content error list
     */
    public ArrayList<String> getSchemaValidationContentErrorList() {
        return schemaValidationContentErrorList;
    }

    /**
     * Gets the schema validation value error list.
     *
     * @return the schema validation value error list
     */
    public ArrayList<String> getSchemaValidationValueErrorList() {
        return schemaValidationValueErrorList;
    }

    /**
     * Gets the schema validation other (not value or content) error list.
     *
     * @return the schema validation other error list
     */
    public ArrayList<String> getSchemaValidationOtherErrorList() {
        return schemaValidationOtherErrorList;
    }

    
    /**
     * Clear errors.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private void clearErrors() {
        errorList.clear();
        parserErrorList.clear();
        fieldErrorList.clear();
        valueErrorList.clear();
        schemaValidationOtherErrorList.clear();
        schemaValidationContentErrorList.clear();
        schemaValidationValueErrorList.clear();
    }

    /**
     * Prints the results.
     *
     * @param out the out
     * @param uri the uri
     * @param time the time
     * @param memory the memory
     * @param repetition the repetition
     */
    public void printResults(PrintWriter out, String uri, long time,
            long memory, int repetition) {

        // filename.xml: 631 ms
        out.print(uri);
        out.print(": ");
        if (repetition == 1) {
            out.print(time);
        } else {
            out.print(time);
            out.print('/');
            out.print(repetition);
            out.print('=');
            out.print(((float) time) / repetition);
        }
        out.print(" ms");
        if (memory != Long.MIN_VALUE) {
            out.print(", ");
            out.print(memory);
            out.print(" bytes");
        }
        out.println();
        out.flush();

    } // printResults(PrintWriter,String,long,long,int)

    //
    // ErrorHandler methods
    //
    /**
     * Warning.
     *
     * @param ex the ex
     * @throws SAXException the sAX exception
     */
    public void warning(SAXParseException ex) throws SAXException {
        printError("Warning", ex);
//        System.out.println("Warning" + ex.getMessage());
    } // warning(SAXParseException)

    /**
     * Error.
     *
     * @param ex the ex
     * @throws SAXException the sAX exception
     */
    public void error(SAXParseException ex) throws SAXException {
        printError("Error", ex);
//        System.out.println("Error" + ex.getMessage());
        parseResult = false;
    } // error(SAXParseException)

    /**
     * Fatal error.
     *
     * @param ex the ex
     * @throws SAXException the sAX exception
     */
    public void fatalError(SAXParseException ex) throws SAXException {
        printError("Fatal Error", ex);
//        System.out.println("Fatal Error" + ex.getMessage());
        parseResult = false;
        throw ex;
    } // fatalError(SAXParseException)

    //
    // Protected methods
    //
    /**
     * Prints the error message.
     *
     * @param type the type
     * @param ex the ex
     */
    protected void printError(String type, SAXParseException ex) {
        String errorString = "[";
        errorString = errorString.concat(type);
        errorString = errorString.concat("]");
        
        System.err.print("[");
        System.err.print(type);
        System.err.print("] ");
        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1) {
                systemId = systemId.substring(index + 1);
            }
            System.err.print(systemId);
            errorString = errorString.concat(systemId);
        }
        
        errorString = errorString.concat(":");
        errorString = errorString.concat(((Integer) ex.getLineNumber()).toString());
        errorString = errorString.concat(":");
        errorString = errorString.concat(((Integer) ex.getColumnNumber()).toString());
        errorString = errorString.concat(":");
        errorString = errorString.concat(ex.getMessage());
        if (!performingSchemaValidation) {
            errorList.add(errorString);
            if ((errorString.contains("parse")) || (errorString.contains("[Fatal Error]"))) {
                addParserError(errorString);
                System.out.println("**PARSE ERROR **");
            }
            if (errorString.contains("value")) {
                addValueError(errorString);
                System.out.println("**VALUE ERROR **");
            }
            if ((errorString.contains("field")) || (errorString.contains("Invalid content"))) {
                addFieldError(errorString);
                System.out.println("**FIELD ERROR **");
            }
        } else {
            if (errorString.contains("is not complete.")||errorString.contains("Invalid content")){
                schemaValidationContentErrorList.add(errorString);
            } else if (errorString.contains("value")){
                schemaValidationValueErrorList.add(errorString);
            } else {
                schemaValidationOtherErrorList.add(errorString);                
            }
        }

        System.err.print(':');
        System.err.print(ex.getLineNumber());
        System.err.print(':');
        System.err.print(ex.getColumnNumber());
        System.err.print(": ");
        System.err.print(ex.getMessage());
        System.err.println();
        System.err.flush();

    } // printError(String,SAXParseException)
}
