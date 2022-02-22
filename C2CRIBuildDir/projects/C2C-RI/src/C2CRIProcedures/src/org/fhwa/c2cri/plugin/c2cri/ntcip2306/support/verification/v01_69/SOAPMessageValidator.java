/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.verification.v01_69;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.SoapVersion;
//import org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.wsdl.RIWSDL;

/**
 *
 * @author TransCore ITS
 */
public class SOAPMessageValidator {

    public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";
    public static final String XML_NS = "http://www.w3.org/2000/xmlns/";
    public static final String WSDL11_NS = "http://schemas.xmlsoap.org/wsdl/";
    public static final String SOAP_ENCODING_NS = "http://schemas.xmlsoap.org/soap/encoding/";
    public static final String SOAP11_ENVELOPE_NS = "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String SOAP_HTTP_TRANSPORT = "http://schemas.xmlsoap.org/soap/http";
    public static final String SOAP_HTTP_BINDING_NS = "http://schemas.xmlsoap.org/wsdl/soap/";
    public static final String SOAP12_HTTP_BINDING_NS = "http://www.w3.org/2003/05/soap/bindings/HTTP/";
    public static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String SOAP12_ENVELOPE_NS = "http://www.w3.org/2003/05/soap-envelope";
    public static final String WADL10_NS = "http://research.sun.com/wadl/2006/10";
    public static final String WADL11_NS = "http://wadl.dev.java.net/2009/02";
    private final static QName envelopeQName = new QName(SOAP11_ENVELOPE_NS, "Envelope");
    private final static QName bodyQName = new QName(SOAP11_ENVELOPE_NS, "Body");
    private final static QName faultQName = new QName(SOAP11_ENVELOPE_NS, "Fault");
    private final static QName headerQName = new QName(SOAP11_ENVELOPE_NS, "Header");
    protected static Logger log = Logger.getLogger(SOAPMessageValidator.class.getName());
    /**
    SchemaTypeLoader soapSchema;
    SchemaType soapEnvelopeType;
    private XmlObject soapSchemaXml;
    //    private XmlObject soapEncodingXml;
    private SchemaType soapFaultType;
     */
//    private SchemaTypeSystem sts;
    private RIWSDL riWSDL;

    public SOAPMessageValidator(RIWSDL riWSDL) {
        this.riWSDL = riWSDL;
//        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
//        Thread.currentThread().setContextClassLoader(SOAPValidationTest.class.getClassLoader());

        /**
        try {
        XmlOptions options = new XmlOptions();
        options.setCompileNoValidation();
        options.setCompileNoPvrRule();
        options.setCompileDownloadUrls();
        options.setCompileNoUpaRule();
        options.setValidateTreatLaxAsSkip();

        soapSchemaXml = XmlObject.Factory.parse(new File("C:\\tmp\\components-TreeIconDemo2Project\\components-TreeIconDemo2Project\\src\\schematest\\resources\\xsds\\soapEnvelope.xsd"), options);
        soapSchema = XmlBeans.loadXsd(new XmlObject[]{soapSchemaXml});

        soapEnvelopeType = soapSchema.findDocumentType(envelopeQName);
        soapFaultType = soapSchema.findDocumentType(faultQName);

        //            soapEncodingXml = XmlObject.Factory.parse(new File("C:\\tmp\\components-TreeIconDemo2Project\\components-TreeIconDemo2Project\\src\\schematest\\resources\\xsds\\soapEncoding.xsd"), options);
        } catch (Exception e) {
        e.printStackTrace();
        //                    SoapUI.logError( e );
        //       } finally {
        //           Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
         */
    }

    @SuppressWarnings("unchecked")
    public void validateSoapEnvelope(String soapMessage, List<XmlError> errors) {
        List<XmlError> errorList = new ArrayList<XmlError>();

        try {
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setLoadLineNumbers();
            xmlOptions.setValidateTreatLaxAsSkip();
            xmlOptions.setLoadLineNumbers(XmlOptions.LOAD_LINE_NUMBERS_END_ELEMENT);
            log.debug("validateSoapEnvelope: Creating the xmlObject");
            XmlObject xmlObject = SoapVersion.Soap11.getSoapEnvelopeSchemaLoader().parse(soapMessage, SoapVersion.Soap11.getEnvelopeType(), xmlOptions);
//            XmlObject xmlObject = soapSchema.parse(soapMessage, soapEnvelopeType, xmlOptions);
            xmlOptions.setErrorListener(errorList);
            log.debug("validateSoapEnvelope: Validating the xmlObject");
            xmlObject.validate(xmlOptions);
        } catch (XmlException e) {
            e.printStackTrace();
            log.debug("validateSoapEnvelope: " + e.getMessage());

            if (e.getErrors() != null) {
                errorList.addAll(e.getErrors());
            }

            errors.add(XmlError.forMessage(e.getMessage()));
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));

            log.debug("validateSoapEnvelope: " + e.getMessage() + "\n" + sw.toString());
            errors.add(XmlError.forMessage(e.getMessage()));
        } finally {
            for (XmlError error : errorList) {
                if (error instanceof XmlValidationError && shouldIgnore((XmlValidationError) error)) {
//					log.warn( "Ignoring validation error: " + error.toString() );
                    continue;
                }

                errors.add(error);
            }
        }
    }

    /**
     * Test the message to determine whether it is a SOAP Encoded Message.
     *
     * @param message
     * @return true if the message is made up of a SOAP Envelope
     */
    public boolean isSOAPEncodedMessage(String message) {
        boolean isSOAPEncoded = false;
        try {

            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setLoadLineNumbers();
            xmlOptions.setSaveOuter();
            xmlOptions.setLoadLineNumbers(XmlOptions.LOAD_LINE_NUMBERS_END_ELEMENT);

            XmlObject xml = XmlObject.Factory.parse(message, xmlOptions);

            // Identify all elements within the soap body
            XmlObject[] paths = xml.selectPath("declare namespace env='"
                    + "http://schemas.xmlsoap.org/soap/envelope/" + "';"
                    + "$this/env:Envelope");

            // Try to validate each element within the SOAP body
            if (paths.length > 0) {
                isSOAPEncoded = true;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("isSOAPEncodedMessage: Exception Message: " + ex.getMessage());

        }
        return isSOAPEncoded;

    }

    public String[] getSOAPMessageParts(String message) {
        String[] returnParts = null;

        try {

//            soapSchema = XmlBeans.typeLoaderUnion(new SchemaTypeLoader[]{
//                        sts, soapSchema
//                    });

            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setLoadLineNumbers();
            xmlOptions.setSaveOuter();
            xmlOptions.setLoadLineNumbers(XmlOptions.LOAD_LINE_NUMBERS_END_ELEMENT);

            XmlObject xml = XmlObject.Factory.parse(message, xmlOptions);

            // Identify all elements within the soap body
            XmlObject[] paths = xml.selectPath("declare namespace env='"
                    + "http://schemas.xmlsoap.org/soap/envelope/" + "';"
                    + "$this/env:Envelope/env:Body/*");

            // Try to validate each element within the SOAP body
            if (paths.length > 0) {
                returnParts = new String[paths.length];
                log.debug("getSoapMessageParts: Items within SOAP Message " + paths.length);
                int ii = 0;
                for (XmlObject thisPath : paths) {
                    ii++;
                    QName tempQName = new QName(thisPath.getDomNode().getNamespaceURI(),
                            thisPath.getDomNode().getLocalName(),
                            thisPath.getDomNode().getPrefix());

                    SchemaType elementType = riWSDL.getSchemaTypeSystem().findDocumentType(tempQName);
//                    if (elementType != null) {
                    List<XmlError> errorList = new ArrayList<XmlError>();
                    xmlOptions.setErrorListener(errorList);
                    XmlObject localXml = riWSDL.getSchemaTypeSystem().parse(thisPath.getDomNode(), elementType, xmlOptions);
                    returnParts[ii - 1] = localXml.toString();
                    log.debug("getSoapMessageParts: Validation results for " + tempQName.getLocalPart() + " = " + localXml.validate(xmlOptions));
                    for (XmlError thisError : errorList) {
                        log.debug(thisError.getMessage() + " @ line " + thisError.getLine() + "\n" + thisError.toString());
                    }
//                    } else {
//                        System.out.println(" No type match found for element type " + tempQName.toString());
//                    }

                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("getSoapMessageParts: Exception Message: " + ex.getMessage());
        }
        return returnParts;
    }

    public String[] getSoapMessageTypes(String message) {

        String[] fullParts = getSOAPMessageParts(message);
        String[] messageTypes = new String[fullParts.length];

        for (int ii = 0; ii < fullParts.length; ii++) {
            try {
                XmlObject thisObject = XmlObject.Factory.parse(fullParts[ii]);
                XmlCursor theCursor = thisObject.newCursor();
                theCursor.toParent();
                theCursor.toFirstChild();
                messageTypes[ii] = theCursor.getName().getLocalPart();

            } catch (Exception ex) {
                ex.printStackTrace();
                log.debug("getSoapMessageTypes: Exception Message: " + ex.getMessage());
            }

        }

        return messageTypes;
    }

    public boolean shouldIgnore(XmlValidationError error) {
        QName offendingQName = error.getOffendingQName();
        if (offendingQName != null) {
            if (offendingQName.equals(new QName("http://schemas.xmlsoap.org/soap/envelope/", "encodingStyle"))) {
                return true;
            } else if (offendingQName.equals(new QName("http://schemas.xmlsoap.org/soap/envelope/", "mustUnderstand"))) {
                return true;
            }
        }

        return false;
    }
}
