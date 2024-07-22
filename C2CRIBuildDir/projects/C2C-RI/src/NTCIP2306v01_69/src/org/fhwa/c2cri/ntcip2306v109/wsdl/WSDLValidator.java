/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.wsdl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * The Class WSDLValidator performs validation of a WSDL document against the WSDL Schema.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLValidator implements ErrorHandler {

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
    protected boolean Parse_Result = false;
    //
    // Data
    //
    /** The out. */
    protected PrintWriter fOut = new PrintWriter(System.out);
    
    /** The schemas. */
    private ArrayList<String> schemas = null;
    
    /** The error list. */
    private List<String> errorList = new ArrayList<String>();
    
    /** The log. */
    protected static Logger log = LogManager.getLogger(WSDLValidator.class.getName());

    //
    // Data
    //
    /**
     * Adds the schemas.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param schemaList the schema list
     */
    public void addSchemas(List<String> schemaList) {
        schemas = new ArrayList<String>();

        for (int ii = 0; ii < schemaList.size(); ii++) {
            schemas.add(schemaList.get(ii));
        }

    }

    /**
     * Check valid wsdl.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param xmlSource the xml source
     * @return true, if successful
     */
    public boolean checkValidWSDL(InputStream xmlSource) {

        // variables
        String schemaLanguage = DEFAULT_SCHEMA_LANGUAGE;
        int repetition = DEFAULT_REPETITION;
        String validationSource = DEFAULT_VALIDATION_SOURCE;
        boolean schemaFullChecking = DEFAULT_SCHEMA_FULL_CHECKING;
        boolean honourAllSchemaLocations = DEFAULT_HONOUR_ALL_SCHEMA_LOCATIONS;
        boolean validateAnnotations = DEFAULT_VALIDATE_ANNOTATIONS;
        boolean generateSyntheticAnnotations = DEFAULT_GENERATE_SYNTHETIC_ANNOTATIONS;
        boolean memoryUsage = DEFAULT_MEMORY_USAGE;

        try {
            errorList.clear();
            // Create SchemaFactory and configure
            SchemaFactory factory = SchemaFactory.newInstance(schemaLanguage);
            log.debug("WSDLValidator SchemaFactory package: " + factory.getClass().getPackage() + "  Class Name: " + factory.getClass().getName() + " To URL: " + factory.getClass().getClassLoader().getResource(factory.getClass().getName().replace(".", "/") + ".class"));
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
            Schema schema;
            if (schemas != null && schemas.size() > 0) {
                final int length = schemas.size();
                StreamSource[] sources = new StreamSource[length];
                for (int j = 0; j < length; ++j) {
                    sources[j] = new StreamSource((String) schemas.get(j));
                }
                schema = factory.newSchema(sources);
            } else {
                schema = factory.newSchema();
            }

            log.debug("WSDLValidator Schema package: " + schema.getClass().getPackage() + "  Class Name: " + schema.getClass().getName() + " To URL: " + schema.getClass().getClassLoader().getResource(schema.getClass().getName().replace(".", "/") + ".class"));
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
            // Validate instance documents
            if (validationSource.equals("sax")) {
                // SAXSource
                XMLReader reader;
                try {
                    reader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
                    log.debug("WSDLValidator XMLReader package: " + reader.getClass().getPackage() + "  Class Name: " + reader.getClass().getName() + " To URL: " + reader.getClass().getClassLoader().getResource(reader.getClass().getName().replace(".", "/") + ".class"));
                } catch (Exception ex) {
                    reader = XMLReaderFactory.createXMLReader();
                    log.debug("Due To: " + ex.getMessage() + ": WSDLValidator XMLReader package: " + reader.getClass().getPackage() + "  Class Name: " + reader.getClass().getName() + " To URL: " + reader.getClass().getClassLoader().getResource(reader.getClass().getName().replace(".", "/") + ".class"));
                }

                SAXSource source = new SAXSource(reader, new InputSource(xmlSource));
                log.debug("WSDLValidator SAXSource package: " + source.getClass().getPackage() + "  Class Name: " + source.getClass().getName());
                log.debug("WSDLValidator Validator package: " + validator.getClass().getPackage() + "  Class Name: " + validator.getClass().getName() + " To URL: " + validator.getClass().getClassLoader().getResource(validator.getClass().getName().replace(".", "/") + ".class"));
                try {
                    boolean validationResults = isvalid(validator, source, "RI XML Input", repetition, memoryUsage);
                    Parse_Result = (errorList.size()>0?false:true);
                } catch (Exception e) {
                    System.err.println("error: Parse error occurred - " + e.getMessage());
                    if (e instanceof SAXException) {
                        Exception nested = ((SAXException) e).getException();
                        if (nested != null) {
                            e = nested;
                        }
                    }
                    e.printStackTrace(System.err);
                    //	                    }
                } finally {
                    validator = null;
                    schema = null;
                    fOut.flush();
                }
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
    } // checkValidWSDL

    /**
     * Check valid string.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param xmlSource the xml source
     * @return true, if successful
     */
    public boolean checkValidString(String xmlSource) {

        // variables
        String schemaLanguage = DEFAULT_SCHEMA_LANGUAGE;
        int repetition = DEFAULT_REPETITION;
        String validationSource = DEFAULT_VALIDATION_SOURCE;
        boolean schemaFullChecking = DEFAULT_SCHEMA_FULL_CHECKING;
        boolean honourAllSchemaLocations = DEFAULT_HONOUR_ALL_SCHEMA_LOCATIONS;
        boolean validateAnnotations = DEFAULT_VALIDATE_ANNOTATIONS;
        boolean generateSyntheticAnnotations = DEFAULT_GENERATE_SYNTHETIC_ANNOTATIONS;
        boolean memoryUsage = DEFAULT_MEMORY_USAGE;

        try {
            // Create new instance of SourceValidator.
            WSDLValidator sourceValidator = new WSDLValidator();

            errorList.clear();
            // Create SchemaFactory and configure
            SchemaFactory factory = SchemaFactory.newInstance(schemaLanguage);
            log.debug("WSDLValidator SchemaFactory package: " + factory.getClass().getPackage() + "  Class Name: " + factory.getClass().getName() + " To URL: " + factory.getClass().getClassLoader().getResource(factory.getClass().getName().replace(".", "/") + ".class"));
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
            Schema schema;
            if (schemas != null && schemas.size() > 0) {
                final int length = schemas.size();
                StreamSource[] sources = new StreamSource[length];
                for (int j = 0; j < length; ++j) {
                    sources[j] = new StreamSource((String) schemas.get(j));
                }
                schema = factory.newSchema(sources);
            } else {
                schema = factory.newSchema();
            }

            log.debug("WSDLValidator Schema package: " + schema.getClass().getPackage() + "  Class Name: " + schema.getClass().getName() + " To URL: " + schema.getClass().getClassLoader().getResource(schema.getClass().getName().replace(".", "/") + ".class"));
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
            // Validate instance documents

            if (validationSource.equals("sax")) {
                // SAXSource
                XMLReader reader;
                try {
                    reader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
                    log.debug("WSDLValidator XMLReader package: " + reader.getClass().getPackage() + "  Class Name: " + reader.getClass().getName() + " To URL: " + reader.getClass().getClassLoader().getResource(reader.getClass().getName().replace(".", "/") + ".class"));
                } catch (Exception ex) {
                    reader = XMLReaderFactory.createXMLReader();
                    log.debug("Due To: " + ex.getMessage() + ": WSDLValidator XMLReader package: " + reader.getClass().getPackage() + "  Class Name: " + reader.getClass().getName() + " To URL: " + reader.getClass().getClassLoader().getResource(reader.getClass().getName().replace(".", "/") + ".class"));
                }

                byte inputBytes[] = xmlSource.getBytes("utf-8");
                if (inputBytes.length >= 2) {
                    log.debug("First 2 Bytes of source = [Bytes]" + inputBytes[0] + " " + inputBytes[1] + "Characters: " + xmlSource.substring(0, 1));
                }

                SAXSource source = new SAXSource(reader, new InputSource(new ByteArrayInputStream(xmlSource.getBytes("utf-8"))));
                log.debug("WSDLValidator SAXSource package: " + source.getClass().getPackage() + "  Class Name: " + source.getClass().getName());
                log.debug("WSDLValidator Validator package: " + validator.getClass().getPackage() + "  Class Name: " + validator.getClass().getName() + " To URL: " + validator.getClass().getClassLoader().getResource(validator.getClass().getName().replace(".", "/") + ".class"));
                try {
                    boolean validationResults = isvalid(validator, source, "RI XML Input", repetition, memoryUsage);
                    Parse_Result = true;
                } catch (Exception e) {
                    System.err.println("error: Parse error occurred - " + e.getMessage());
                    if (e instanceof SAXException) {
                        Exception nested = ((SAXException) e).getException();
                        if (nested != null) {
                            e = nested;
                        }
                    }
                    e.printStackTrace(System.err);
                    //	                    }
                } finally {
                    validator = null;
                    schema = null;
                    fOut.flush();
                }
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
     * Errors are present if the validation failed.  This method
     * returns the error list.
     *
     * @return List - the list of errors found during validation.
     */
    public List<String> getErrors() {
        return errorList;
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

    } // warning(SAXParseException)

    /**
     * Error.
     *
     * @param ex the ex
     * @throws SAXException the sAX exception
     */
    public void error(SAXParseException ex) throws SAXException {
        printError("Error", ex);

        Parse_Result = false;
    } // error(SAXParseException)

    /**
     * Fatal error.
     *
     * @param ex the ex
     * @throws SAXException the sAX exception
     */
    public void fatalError(SAXParseException ex) throws SAXException {
        printError("Fatal Error", ex);
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));

        log.debug("fatalError: " + ex.getMessage() + "\n" + sw.toString());


        Parse_Result = false;
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
        errorList.add(errorString);
        System.out.println("*** Added Error :" + errorString);

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
