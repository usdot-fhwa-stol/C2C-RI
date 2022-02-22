/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.verification.v01_69;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
import org.mozilla.universalchardet.UniversalDetector;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author TransCore ITS
 */
public class MessageValidator extends DefaultHandler implements ErrorHandler {

    /** Schema full checking feature id (http://apache.org/xml/features/validation/schema-full-checking). */
    protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID = "http://apache.org/xml/features/validation/schema-full-checking";
    /** Honour all schema locations feature id (http://apache.org/xml/features/honour-all-schemaLocations). */
    protected static final String HONOUR_ALL_SCHEMA_LOCATIONS_ID = "http://apache.org/xml/features/honour-all-schemaLocations";
    /** Validate schema annotations feature id (http://apache.org/xml/features/validate-annotations) */
    protected static final String VALIDATE_ANNOTATIONS_ID = "http://apache.org/xml/features/validate-annotations";
    /** Generate synthetic schema annotations feature id (http://apache.org/xml/features/generate-synthetic-annotations). */
    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS_ID = "http://apache.org/xml/features/generate-synthetic-annotations";
    // default settings
    /** Default schema language (http://www.w3.org/2001/XMLSchema). */
    protected static final String DEFAULT_SCHEMA_LANGUAGE = XMLConstants.W3C_XML_SCHEMA_NS_URI;
    /** Default repetition (1). */
    protected static final int DEFAULT_REPETITION = 1;
    /** Default validation source. Options are sax, dom and stream*/
    protected static final String DEFAULT_VALIDATION_SOURCE = "sax";
    /** Default schema full checking support (false). */
    protected static final boolean DEFAULT_SCHEMA_FULL_CHECKING = false;
    /** Default honour all schema locations (false). */
    protected static final boolean DEFAULT_HONOUR_ALL_SCHEMA_LOCATIONS = false;
    /** Default validate schema annotations (false). */
    protected static final boolean DEFAULT_VALIDATE_ANNOTATIONS = false;
    /** Default generate synthetic schema annotations (false). */
    protected static final boolean DEFAULT_GENERATE_SYNTHETIC_ANNOTATIONS = false;
    /** Default memory usage report (false). */
    protected static final boolean DEFAULT_MEMORY_USAGE = false;
    /** Parse Error Variable (false). */
    protected static boolean Parse_Result = false;
    //
    // Data
    //
    protected PrintWriter fOut = new PrintWriter(System.out);
    private Vector schemas = null;
    private static List<String> errorList = new ArrayList<String>();
    private static ArrayList<String> parserErrorList = new ArrayList<String>();
    private static ArrayList<String> fieldErrorList = new ArrayList<String>();
    private static ArrayList<String> valueErrorList = new ArrayList<String>();
    //
    // Data
    //

    /**
     *
     * @param schemaList
     */
    public void addSchemas(List<String> schemaList) {
        schemas = new Vector();

        for (int ii = 0; ii < schemaList.size(); ii++) {
            schemas.add(schemaList.get(ii));
        }

    }

    public boolean checkValidString(String XMLSource) {

        // variables
        Vector instances = null;
        String schemaLanguage = DEFAULT_SCHEMA_LANGUAGE;
        int repetition = DEFAULT_REPETITION;
        String validationSource = DEFAULT_VALIDATION_SOURCE;
        boolean schemaFullChecking = DEFAULT_SCHEMA_FULL_CHECKING;
        boolean honourAllSchemaLocations = DEFAULT_HONOUR_ALL_SCHEMA_LOCATIONS;
        boolean validateAnnotations = DEFAULT_VALIDATE_ANNOTATIONS;
        boolean generateSyntheticAnnotations = DEFAULT_GENERATE_SYNTHETIC_ANNOTATIONS;
        boolean memoryUsage = DEFAULT_MEMORY_USAGE;

        boolean validationResults = false;
        errorList.clear();
        parserErrorList.clear();
        fieldErrorList.clear();
        valueErrorList.clear();


//            instances = new Vector();
//            instances.add(XMLSource);



        try {
            // Create new instance of SourceValidator.
            MessageValidator sourceValidator = new MessageValidator();

            // Create SchemaFactory and configure
            SchemaFactory factory = SchemaFactory.newInstance(schemaLanguage);
            factory.setErrorHandler(sourceValidator);

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
            Schema schema;
            if (schemas != null && schemas.size() > 0) {
                final int length = schemas.size();
                StreamSource[] sources = new StreamSource[length];
                for (int j = 0; j < length; ++j) {
                    sources[j] = new StreamSource((String) schemas.elementAt(j));
                }
                schema = factory.newSchema(sources);
            } else {
                schema = factory.newSchema();
            }

            // Setup validator and input source.
            Validator validator = schema.newValidator();
            validator.setErrorHandler(sourceValidator);

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
            Parse_Result = true;
            // Validate instance documents
//	            if (instances != null && instances.size() > 0) {
//	                final int length = instances.size();
            if (validationSource.equals("sax")) {
                // SAXSource
                XMLReader reader = XMLReaderFactory.createXMLReader();
//	                    for (int j = 0; j < length; ++j) {
//	                        String systemId = (String) instances.elementAt(j);
                SAXSource source = new SAXSource(reader, new InputSource(new ByteArrayInputStream(XMLSource.getBytes("UTF-8"))));
                validationResults = sourceValidator.isvalid(validator, source, "RI XML Input", repetition, memoryUsage);
//	                    }
            }

        } catch (SAXParseException e) {
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
        return Parse_Result;
    } // checkValid(String)

    public boolean isValidXML(String XMLSource) {


        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            System.out.println("MessageValidator SAXParser package: " + sp.getClass().getPackage() + "  Class Name: " + sp.getClass().getName() + " To URL: " + sp.getClass().getClassLoader().getResource(sp.getClass().getName().replace(".", "/") + ".class"));
            System.out.println("Parser is validating " + sp.isValidating());
            System.out.println("Parser is namespaceaware " + sp.isNamespaceAware());
            //parse the file and also register this class for call backs

            InputStream is = new ByteArrayInputStream(XMLSource.getBytes());

//            int byteRead;
//            while ((byteRead = is.read()) != -1) {
//                System.out.print((char)byteRead);
//            }
//            System.out.println();

            errorList.clear();
            Parse_Result = true;

            sp.parse(is, this);
            is.close();

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        return Parse_Result;
    } // checkValid(String)

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

    public boolean isUTF8Encoded(byte[] encodedMessage) {
        boolean result = false;
        byte[] buf = new byte[4096];

        UniversalDetector detector = new UniversalDetector(null);

        try {
            InputStream is = new ByteArrayInputStream(encodedMessage);

            StringBuffer buffer = new StringBuffer();

            int ch;
            while ((ch = is.read(buf)) > 0 && !detector.isDone()) {
                buffer.append((char) ch);
                // (2)
                detector.handleData(buf, 0, ch);
            }
            is.close();
            // (3)
            detector.dataEnd();
            // (4)
            String encoding = detector.getDetectedCharset();
            if (encoding != null) {
                if (encoding.equals("UTF-8")) {
                    result = true;
                } else {
                    result = false;
                }
                System.out.println("Detected encoding = " + encoding);
            } else {
                result = true;
                System.out.println("No encoding detected.");
            }
            // (5)
            detector.reset();

        } catch (Exception ex) {
            result = false;
        }

        return result;
    }

    public String getXMLMessageType(String encodedMessage) {
        String result = "";
        try {
            XmlObject thisObject = XmlObject.Factory.parse(encodedMessage);
            XmlCursor xmlc = thisObject.newCursor();
            xmlc.toParent();
            xmlc.toFirstChild();
            result = xmlc.getName().getLocalPart();


            xmlc.dispose();
        } catch (Exception ex) {
        }
        return result;
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

    public ArrayList<String> getFieldErrorList() {
        return fieldErrorList;
    }

    public ArrayList<String> getParserErrorList() {
        return parserErrorList;
    }

    public ArrayList<String> getValueErrorList() {
        return valueErrorList;
    }



    /** Prints the results. */
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
    /** Warning. */
    public void warning(SAXParseException ex) throws SAXException {
        printError("Warning", ex);
//        System.out.println("Warning" + ex.getMessage());
    } // warning(SAXParseException)

    /** Error. */
    public void error(SAXParseException ex) throws SAXException {
        printError("Error", ex);
//        System.out.println("Error" + ex.getMessage());
        Parse_Result = false;
    } // error(SAXParseException)

    /** Fatal error. */
    public void fatalError(SAXParseException ex) throws SAXException {
        printError("Fatal Error", ex);
//        System.out.println("Fatal Error" + ex.getMessage());
        Parse_Result = false;
        throw ex;
    } // fatalError(SAXParseException)

    //
    // Protected methods
    //
    /** Prints the error message. */
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
        errorList.add(errorString);
        if ((errorString.contains("parse"))||(errorString.contains("[Fatal Error]"))){
            this.parserErrorList.add(errorString);
            System.out.println("**PARSE ERROR **");
        }
        if (errorString.contains("value")){
            this.valueErrorList.add(errorString);
            System.out.println("**VALUE ERROR **");
        }
        if ((errorString.contains("field"))||(errorString.contains("Invalid content"))){
            this.fieldErrorList.add(errorString);
            System.out.println("**FIELD ERROR **");
        }
//        System.out.println("*** Added Error :" + errorString);

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
