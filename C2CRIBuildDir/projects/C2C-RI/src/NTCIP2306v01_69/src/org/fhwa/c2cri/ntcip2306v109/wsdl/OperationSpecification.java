/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;


/**
 * The Class OperationSpecification defines the key parts of an operation as defined in the WSDL.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class OperationSpecification {

    /** The request response operation. */
    public static String REQUEST_RESPONSE_OPERATION = "RR";
    
    /** The subscription operation. */
    public static String SUBSCRIPTION_OPERATION = "SUB";
    
    /** The publication operation. */
    public static String PUBLICATION_OPERATION = "PUB";
    
    /** The soap encoding. */
    public static String SOAP_ENCODING = "SOAP";
    
    /** The xml encoding. */
    public static String XML_ENCODING = "XML";
    
    /** The gzip encoding. */
    public static String GZIP_ENCODING = "GZIP";
    
    /** The get operation. */
    public static String GET_OPERATION = "GET";
    
    /** The C2 c_ publicatio n_ message. */
    public static String C2C_PUBLICATION_MESSAGE = "c2cMessagePublication";
    
    /** The C2 c_ subscriptio n_ message. */
    public static String C2C_SUBSCRIPTION_MESSAGE = "c2cMessageSubscription";
    
    /** The header part. */
    private final int headerPart = 0;

    /**
     * The Enum EncodingType.
     */
    public static enum EncodingType {

        /** The xml. */
        XML, /** The soap. */
 SOAP, /** The gzip. */
 GZIP
    };

    /**
     * The Enum OperationType.
     */
    public static enum OperationType {

        /** The get. */
        GET, /** The rr. */
 RR, /** The sub. */
 SUB, /** The pub. */
 PUB
    };

    /**
     * The Enum ServiceTransportType.
     */
    public static enum ServiceTransportType {

        /** The http. */
        HTTP, /** The https. */
 HTTPS, /** The ftp. */
 FTP, /** The undefined. */
 UNDEFINED
    };

    /**
     * The Enum ProfileType.
     */
    public static enum ProfileType {

        /** The unknown. */
        UNKNOWN, /** The get. */
 GET, /** The rr. */
 RR, /** The sub. */
 SUB, /** The pub. */
 PUB
    };
    
    /** The operation name. */
    private String operationName;
    
    /** The related to port. */
    private String relatedToPort;
    
    /** The related to service. */
    private String relatedToService;
    
    /** The operation input encoding. */
    private EncodingType operationInputEncoding;
    
    /** The operation output encoding. */
    private EncodingType operationOutputEncoding;
    
    /** The operation fault encoding. */
    private EncodingType operationFaultEncoding;
    
    /** The related service transport. */
    private ServiceTransportType relatedServiceTransport;
    
    /** The soap action. */
    private String soapAction;
    
    /** The document location. */
    private String documentLocation;
    
    /** The input message. */
    private List<QName> inputMessage = new ArrayList<QName>();
    
    /** The output message. */
    private List<QName> outputMessage = new ArrayList<QName>();
    
    /** The fault message. */
    private List<QName> faultMessage = new ArrayList<QName>();
    
    /** The operation type. */
    private OperationType operationType;
    
    /** The profile type. */
    private ProfileType profileType = ProfileType.UNKNOWN;
    
    /** The errors encountered. */
    private List<String> errorsEncountered = new ArrayList<String>();
    
    /** The erroneous specification. */
    private boolean erroneousSpecification = false;

    /**
     * Checks if is subscription operation.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is subscription operation
     */
    public boolean isSubscriptionOperation() {
        if (inputMessage.size() == 2) {
            if (inputMessage.get(headerPart).getLocalPart().equals(C2C_SUBSCRIPTION_MESSAGE)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if is publication operation.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is publication operation
     */
    public boolean isPublicationOperation() {
        if (inputMessage.size() == 2) {
            if (inputMessage.get(headerPart).getLocalPart().equals(C2C_PUBLICATION_MESSAGE)) {
                return true;
            }
        }
        return false;

    }

    /**
     * Checks if is request response operation.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is request response operation
     */
    public boolean isRequestResponseOperation() {
        if (inputMessage.size() == 1) {
            if (outputMessage.size() == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if is gets the operation.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is gets the operation
     */
    public boolean isGetOperation() {
        if ((inputMessage.size() == 1) && (outputMessage.size() == 0)) {
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String returnString = "";
        returnString = returnString.concat("OperationName: " + operationName + "\n");
        returnString = returnString.concat("Erroneous Specificaton?: " + erroneousSpecification + "\n");
        returnString = returnString.concat("RelatedToService: " + relatedToService + "\n");
        returnString = returnString.concat("RelatedToPort: " + relatedToPort + "\n");
        returnString = returnString.concat("RelatedServiceTansport: " + relatedServiceTransport + "\n");
        returnString = returnString.concat("OperationInputEncoding: " + operationInputEncoding + "\n");
        returnString = returnString.concat("OperationOutputEncoding: " + operationOutputEncoding + "\n");
        returnString = returnString.concat("OperationFaultEncoding: " + operationFaultEncoding + "\n");
        returnString = returnString.concat("SOAPAction: " + soapAction + "\n");
        returnString = returnString.concat("DocumentLocation: " + documentLocation + "\n");
        StringBuilder messageList = new StringBuilder();
        messageList.append("InputMessage:  ");
        for (QName s : inputMessage) {
            messageList.append(s).append("\n");
        }
        messageList.append("OutputMessage:  ");
        for (QName s : outputMessage) {
            messageList.append(s).append("\n");
        }
        messageList.append("FaultMessage:  ");
        for (QName s : faultMessage) {
            messageList.append(s).append("\n");
        }

        messageList.append("\nOperationType:").append(operationType).append("\n");
        messageList.append("Error Messages:  ");
        for (String s : errorsEncountered) {
            messageList.append(s).append("\n");
        }

        returnString = messageList.toString();

        return returnString;
    }

    /**
     * Gets the document location.
     *
     * @return the document location
     */
    public String getDocumentLocation() {
        return documentLocation;
    }

    /**
     * Sets the document location.
     *
     * @param documentLocation the new document location
     */
    public void setDocumentLocation(String documentLocation) {
        this.documentLocation = documentLocation;
    }

    /**
     * Gets the fault message.
     *
     * @return the fault message
     */
    public List<QName> getFaultMessage() {
        return faultMessage;
    }

    /**
     * Sets the fault message.
     *
     * @param faultMessage the new fault message
     */
    public void setFaultMessage(List<QName> faultMessage) {
        this.faultMessage = faultMessage;
    }

    /**
     * Gets the input message.
     *
     * @return the input message
     */
    public List<QName> getInputMessage() {
        return inputMessage;
    }

    /**
     * Sets the input message.
     *
     * @param inputMessage the new input message
     */
    public void setInputMessage(List<QName> inputMessage) {
        this.inputMessage = inputMessage;
    }

    /**
     * Gets the operation input encoding.
     *
     * @return the operation input encoding
     */
    public String getOperationInputEncoding() {
        return (operationInputEncoding == null ? "" : operationInputEncoding.name());
    }

    /**
     * Gets the operation output encoding.
     *
     * @return the operation output encoding
     */
    public String getOperationOutputEncoding() {
        return operationOutputEncoding == null ? "" : operationOutputEncoding.name();
    }

    /**
     * Gets the operation fault encoding.
     *
     * @return the operation fault encoding
     */
    public String getOperationFaultEncoding() {
        return operationFaultEncoding == null ? "" : operationFaultEncoding.name();
    }

    /**
     * Sets the operation input encoding.
     *
     * @param operationInputEncoding the new operation input encoding
     */
    public void setOperationInputEncoding(String operationInputEncoding) {
        this.operationInputEncoding = EncodingType.valueOf(operationInputEncoding);
    }

    /**
     * Sets the operation output encoding.
     *
     * @param operationOutputEncoding the new operation output encoding
     */
    public void setOperationOutputEncoding(String operationOutputEncoding) {
        this.operationOutputEncoding = EncodingType.valueOf(operationOutputEncoding);
    }

    /**
     * Sets the operation fault encoding.
     *
     * @param operationFaultEncoding the new operation fault encoding
     */
    public void setOperationFaultEncoding(String operationFaultEncoding) {
        this.operationFaultEncoding = EncodingType.valueOf(operationFaultEncoding);
    }

    /**
     * Gets the operation name.
     *
     * @return the operation name
     */
    public String getOperationName() {
        return operationName;
    }

    /**
     * Sets the operation name.
     *
     * @param operationName the new operation name
     */
    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    /**
     * Gets the operation type.
     *
     * @return the operation type
     */
    public String getOperationType() {
        return operationType.name();
    }

    /**
     * Sets the operation type.
     *
     * @param operationType the new operation type
     */
    public void setOperationType(String operationType) {
        this.operationType = OperationType.valueOf(operationType);
    }

    /**
     * Gets the profile type.
     *
     * @return the profile type
     */
    public ProfileType getProfileType() {
        if (isRequestResponseOperation()) {
            profileType = ProfileType.RR;
        } else if (isGetOperation()) {
            profileType = ProfileType.GET;
        } else if (isSubscriptionOperation()) {
            profileType = ProfileType.SUB;
        } else if (isPublicationOperation()) {
            profileType = ProfileType.PUB;
        } else {
            profileType = ProfileType.UNKNOWN;
        }
        return profileType;
    }

    /**
     * Gets the output message.
     *
     * @return the output message
     */
    public List<QName> getOutputMessage() {
        return outputMessage;
    }

    /**
     * Sets the output message.
     *
     * @param outputMessage the new output message
     */
    public void setOutputMessage(List<QName> outputMessage) {
        this.outputMessage = outputMessage;
    }

    /**
     * Gets the related to port.
     *
     * @return the related to port
     */
    public String getRelatedToPort() {
        return relatedToPort;
    }

    /**
     * Sets the related to port.
     *
     * @param relatedToPort the new related to port
     */
    public void setRelatedToPort(String relatedToPort) {
        this.relatedToPort = relatedToPort;
    }

    /**
     * Gets the related to service.
     *
     * @return the related to service
     */
    public String getRelatedToService() {
        return relatedToService;
    }

    /**
     * Sets the related to service.
     *
     * @param relatedToService the new related to service
     */
    public void setRelatedToService(String relatedToService) {
        this.relatedToService = relatedToService;
    }

    /**
     * Gets the related service transport.
     *
     * @return the related service transport
     */
    public ServiceTransportType getRelatedServiceTransport() {
        return relatedServiceTransport;
    }

    /**
     * Sets the related service transport.
     *
     * @param relatedServiceTransport the new related service transport
     */
    public void setRelatedServiceTransport(ServiceTransportType relatedServiceTransport) {
        this.relatedServiceTransport = relatedServiceTransport;
    }

    /**
     * Gets the soap action.
     *
     * @return the soap action
     */
    public String getSoapAction() {
        return soapAction;
    }

    /**
     * Sets the soap action.
     *
     * @param soapAction the new soap action
     */
    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    /**
     * Checks if is erroneous specification.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is erroneous specification
     */
    public boolean isErroneousSpecification() {
        return erroneousSpecification;
    }

    /**
     * Sets the erroneous specification.
     *
     * @param erroneousSpecification the new erroneous specification
     */
    public void setErroneousSpecification(boolean erroneousSpecification) {
        this.erroneousSpecification = erroneousSpecification;
    }

    /**
     * Gets the errors encountered.
     *
     * @return the errors encountered
     */
    public List<String> getErrorsEncountered() {
        return errorsEncountered;
    }

    /**
     * Sets the errors encountered.
     *
     * @param errorsEncountered the new errors encountered
     */
    public void setErrorsEncountered(List<String> errorsEncountered) {
        this.errorsEncountered = errorsEncountered;
    }

    /**
     * Checks if is publication callback operation.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is publication callback operation
     */
    public boolean isPublicationCallbackOperation() {
        if (getSoapAction() == null) {
            return false;
        }
        return ((getSoapAction().equals("") || getSoapAction().equals(" ")) ? true : false);
    }

    /**
     * The Class OperationMessageSpecification.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    private class OperationMessageSpecification {

        /** The num parts. */
        private int numParts;
        
        /** The part names. */
        private String[] partNames;
        
        /** The part name space. */
        private String[] partNameSpace;

        /**
         * Gets the num parts.
         *
         * @return the num parts
         */
        public int getNumParts() {
            return numParts;
        }

        /**
         * Sets the num parts.
         *
         * @param numParts the new num parts
         */
        public void setNumParts(int numParts) {
            this.numParts = numParts;
        }

        /**
         * Gets the part name space.
         *
         * @return the part name space
         */
        public String[] getPartNameSpace() {
            return partNameSpace;
        }

        /**
         * Sets the part name space.
         *
         * @param partNameSpace the new part name space
         */
        public void setPartNameSpace(String[] partNameSpace) {
            this.partNameSpace = partNameSpace;
        }

        /**
         * Gets the part names.
         *
         * @return the part names
         */
        public String[] getPartNames() {
            return partNames;
        }

        /**
         * Sets the part names.
         *
         * @param partNames the new part names
         */
        public void setPartNames(String[] partNames) {
            this.partNames = partNames;
        }
    }
}
