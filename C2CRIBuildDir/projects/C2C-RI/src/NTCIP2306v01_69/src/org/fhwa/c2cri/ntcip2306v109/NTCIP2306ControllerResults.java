/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109;

import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message;
import org.fhwa.c2cri.ntcip2306v109.status.NTCIP2306Status;

/**
 * Contains the results of a NTCIP 2306 Controller service operation.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class NTCIP2306ControllerResults {
    /** Flag indicating whether a transport error was encountered. */
    private boolean transportError=true;
    /** description of the transport error encountered. */
    private String transportErrorDescription="";
    
    /** the type of transport error encountered. */
    private String transportErrorType = NTCIP2306Status.TRANSPORTERRORTYPES.NONE.name();
    /** Flag indicating whether an encoding error was encountered. */
    private boolean encodingError=true;
    /** description of the encoding error encountered. */
    private String encodingErrorDescription="";
    
    /** the type of encoding error encountered. */
    private String encodingErrorType = NTCIP2306Status.ENCODINGERRORTYPES.NONE.name();
    /** Flag indicating whether a message error was encountered. */
    private boolean messageError=true;
    /** description of the message error encountered. */
    private String messageErrorDescription="";
    
    /** the type of message error encountered. */
    private String messageErrorType = NTCIP2306Status.MESSAGEERRORTYPES.NONE.name();
    /** Flag indicating whether a message validation error was encountered. */
    private boolean messageValidationError=true;
    /** description of the message validation error encountered. */
    private String messageValidationErrorDescription="";
    
    /** the type of message validation error encountered. */
    private String messageValidationErrorType = NTCIP2306Status.MESSAGEERRORTYPES.NONE.name();
    /** Flag indicating whether a operation error was encountered. */
    private boolean operationError=true;
    /** description of the transport error encountered. */
    private String operationErrorDescription="";
    /** flag indication whether a subscription is active. */
    private boolean subscriptionActive = false;
    /** flag indication whether a publication is complete. */
    private boolean publicationComplete = true;
    
    /** the number of milliseconds that have passed since the last periodic publication was received. */
    private long millisSinceLastPeriodicPublication=-1;
    /** the number of publications created. */
    private long publicationCount = -1;
    
    /** the subscription specified periodic publication frequency. */
    private long subscriptionPeriodicFrequency = -1;
    
    /** flag indicating whether the subscription is complete. */
    private boolean subscriptionComplete = true;
    
    /** the name of the operation. */
    private String operationName;
    
    /** the operation request Message. */
    private NTCIP2306Message requestMessage;
    
    /** the operation response Message. */
    private NTCIP2306Message responseMessage;


    /**
     * Constructor.
     */
    public NTCIP2306ControllerResults(){
    }

    /**
     * Constructor.
     *
     * @param operationName the operation name that these controller results are associated with.
     */
    public NTCIP2306ControllerResults(String operationName){
        this.operationName = operationName;
    }
    
    /**
     * Provides the contents of the NTCIP2306ControllerResults as a string.
     * @return the NTCIP2306ControllerResults
     */
    @Override
    public String toString(){
        StringBuilder outString = new StringBuilder();
        
        outString.append("TransportError: ").append(transportError).append("\n");
        outString.append("TransportErrorDescription: ").append(transportErrorDescription).append("\n");
        outString.append("EncodingError: ").append(encodingError).append("\n");
        outString.append("EncodingErrorDescription: ").append(encodingErrorDescription).append("\n");
        outString.append("MessageError: ").append(messageError).append("\n");
        outString.append("MessageErrorDescription: ").append(messageErrorDescription).append("\n");
        outString.append("MessageValidationError: ").append(messageValidationError).append("\n");
        outString.append("MessageValidationErrorDescription: ").append(messageValidationErrorDescription).append("\n");
        outString.append("OperationError: ").append(operationError).append("\n");
        outString.append("OperationErrorDescription: ").append(operationErrorDescription).append("\n\n");        
        outString.append("SubscriptionActive: ").append(subscriptionActive).append("\n\n");        
        outString.append("PublicationActive: ").append(!publicationComplete).append("\n\n");        
        outString.append("PublicationCount: ").append(publicationCount).append("\n\n");        
        if (requestMessage != null)outString.append(requestMessage.getTestAssertionReport(5)).append("\n");
        if (responseMessage != null)outString.append(responseMessage.getTestAssertionReport(5)).append("\n");
        return outString.toString();        
    }
    
    /**
     * getter method for the transport error.
     *
     * @return the transport error status
     */
    public boolean isTransportError() {
        return transportError;
    }

    /**
     * Set the transport Error Status.
     *
     * @param transportError the new transport error
     */
    public void setTransportError(boolean transportError) {
        this.transportError = transportError;
    }

    /**
     * getter for the transport error description.
     *
     * @return the transport error description.
     */
    public String getTransportErrorDescription() {
        return transportErrorDescription;
    }

    /**
     * setter for the transport error description.
     *
     * @param transportErrorDescription the new transport error description
     */
    public void setTransportErrorDescription(String transportErrorDescription) {
        this.transportErrorDescription = transportErrorDescription;
    }

    /**
     * getter for the encoding error.
     *
     * @return true, if is encoding error
     */
    public boolean isEncodingError() {
        return encodingError;
    }

    /**
     * Setter for the encoding error.
     *
     * @param encodingError the new encoding error
     */
    public void setEncodingError(boolean encodingError) {
        this.encodingError = encodingError;
    }

    /**
     * getter for the encoding error description.
     *
     * @return the encoding error description
     */
    public String getEncodingErrorDescription() {
        return encodingErrorDescription;
    }

    /**
     * Setter for the encoding error description.
     *
     * @param encodingErrorDescription the new encoding error description
     */
    public void setEncodingErrorDescription(String encodingErrorDescription) {
        this.encodingErrorDescription = encodingErrorDescription;
    }

    /**
     * getter for the message error.
     *
     * @return true, if is message error
     */
    public boolean isMessageError() {
        return messageError;
    }

    /**
     * Setter for the message error.
     *
     * @param messageError the new message error
     */
    public void setMessageError(boolean messageError) {
        this.messageError = messageError;
    }

    /**
     * getter for the message error description.
     *
     * @return the message error description
     */
    public String getMessageErrorDescription() {
        return messageErrorDescription;
    }

    /**
     * setter for the message error description.
     *
     * @param messageErrorDescription the new message error description
     */
    public void setMessageErrorDescription(String messageErrorDescription) {
        this.messageErrorDescription = messageErrorDescription;
    }

    /**
     * getter for the message validation error.
     *
     * @return true, if is message validation error
     */
    public boolean isMessageValidationError() {
        return messageValidationError;
    }

    /**
     * setter for the message validation error.
     *
     * @param messageValidationError the new message validation error
     */
    public void setMessageValidationError(boolean messageValidationError) {
        this.messageValidationError = messageValidationError;
    }

    /**
     * getter for the message validation error description.
     *
     * @return the message validation error description
     */
    public String getMessageValidationErrorDescription() {
        return messageValidationErrorDescription;
    }

    /**
     * setter for the message validation error description.
     *
     * @param messageValidationErrorDescription the new message validation error description
     */
    public void setMessageValidationErrorDescription(String messageValidationErrorDescription) {
        this.messageValidationErrorDescription = messageValidationErrorDescription;
    }
    
    
    /**
     * getter for the operation error.
     *
     * @return true, if is operation error
     */
    public boolean isOperationError() {
        return operationError;
    }

    /**
     * setter for the operation error.
     *
     * @param operationError the new operation error
     */
    public void setOperationError(boolean operationError) {
        this.operationError = operationError;
    }

    /**
     * getter for the operation error description.
     *
     * @return the operation error description
     */
    public String getOperationErrorDescription() {
        return operationErrorDescription;
    }

    /**
     * setter for the operation error description.
     *
     * @param operationErrorDescription the new operation error description
     */
    public void setOperationErrorDescription(String operationErrorDescription) {
        this.operationErrorDescription = operationErrorDescription;
    }

    /**
     * getter for the publication complete field.
     *
     * @return true, if is publication complete
     */
    public boolean isPublicationComplete() {
        return publicationComplete;
    }

    /**
     * setter for the publication complete field.
     *
     * @param publicationComplete the new publication complete
     */
    public void setPublicationComplete(boolean publicationComplete) {
        this.publicationComplete = publicationComplete;
    }

    /**
     * getter for the subscription complete variable.
     *
     * @return true, if is subscription complete
     */
    public boolean isSubscriptionComplete() {
        return subscriptionComplete;
    }

    /**
     * Setter for the subscription complete variable.
     *
     * @param subscriptionComplete the new subscription complete
     */
    public void setSubscriptionComplete(boolean subscriptionComplete) {
        this.subscriptionComplete = subscriptionComplete;
    }

    /**
     * getter for the publication count variable.
     *
     * @return the publication count
     */
    public long getPublicationCount() {
        return publicationCount;
    }

    /**
     * setter for the publication count variable.
     *
     * @param publicationCount the new publication count
     */
    public void setPublicationCount(long publicationCount) {
        this.publicationCount = publicationCount;
    }

    /**
     * getter for the milliseconds since the last periodic publication variable.
     *
     * @return the millis since last periodic publication
     */
    public long getMillisSinceLastPeriodicPublication() {
        return millisSinceLastPeriodicPublication;
    }

    /**
     * Setter for the milliseconds since last. Publication variable
     *
     * @param millisSinceLastPeriodicPublication the new millis since last periodic publication
     */
    public void setMillisSinceLastPeriodicPublication(long millisSinceLastPeriodicPublication) {
        this.millisSinceLastPeriodicPublication = millisSinceLastPeriodicPublication;
    }

    /**
     * getter for the subscription periodic frequency.
     *
     * @return the subscription periodic frequency
     */
    public long getSubscriptionPeriodicFrequency() {
        return subscriptionPeriodicFrequency;
    }

    /**
     * setter for the subscription periodic frequency.
     *
     * @param subscriptionPeriodicFrequency the new subscription periodic frequency
     */
    public void setSubscriptionPeriodicFrequency(long subscriptionPeriodicFrequency) {
        this.subscriptionPeriodicFrequency = subscriptionPeriodicFrequency;
    }        
    
    /**
     * getter for the request message.
     *
     * @return the request message
     */
    public NTCIP2306Message getRequestMessage() {
        return requestMessage;
    }

    /**
     * setter for the request message.
     *
     * @param requestMessage the new request message
     */
    public void setRequestMessage(NTCIP2306Message requestMessage) {
        this.requestMessage = requestMessage;
    }

    /**
     * getter for the response message.
     *
     * @return the response message
     */
    public NTCIP2306Message getResponseMessage() {
        return responseMessage;
    }

    /**
     * setter for the response message.
     *
     * @param responseMessage the new response message
     */
    public void setResponseMessage(NTCIP2306Message responseMessage) {
        this.responseMessage = responseMessage;
    }

    /**
     * getter for the subscription active flag.
     *
     * @return true, if is subscription active
     */
    public boolean isSubscriptionActive() {
        return subscriptionActive;
    }

    /**
     * setter for the subscription active flag.
     *
     * @param subscriptionActive the new subscription active
     */
    public void setSubscriptionActive(boolean subscriptionActive) {
        this.subscriptionActive = subscriptionActive;
    }

    /**
     * getter for the operation name.
     *
     * @return the operation name
     */
    public String getOperationName() {
        return operationName;
    }

    /**
     * setter for the operation name.
     *
     * @param operationName the new operation name
     */
    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    /**
     * getter for the transport error type.
     *
     * @return the transport error type
     */
    public String getTransportErrorType() {
        return transportErrorType;
    }

    /**
     * setter for the transport error type.
     *
     * @param transportErrorType the new transport error type
     */
    public void setTransportErrorType(String transportErrorType) {
        this.transportErrorType = transportErrorType;
    }

    /**
     * getter for the encoding error type.
     *
     * @return the encoding error type
     */
    public String getEncodingErrorType() {
        return encodingErrorType;
    }

    /**
     * setter for the encoding error type.
     *
     * @param encodingErrorType the new encoding error type
     */
    public void setEncodingErrorType(String encodingErrorType) {
        this.encodingErrorType = encodingErrorType;
    }

    /**
     * getter for the message error type.
     *
     * @return the message error type
     */
    public String getMessageErrorType() {
        return messageErrorType;
    }

    /**
     * setter for the message error type.
     *
     * @param messageErrorType the new message error type
     */
    public void setMessageErrorType(String messageErrorType) {
        this.messageErrorType = messageErrorType;
    }

    /**
     * getter for the message validation error type.
     *
     * @return the message validation error type
     */
    public String getMessageValidationErrorType() {
        return messageValidationErrorType;
    }

    /**
     * setter for the message validation error type.
     *
     * @param messageValidationErrorType the new message validation error type
     */
    public void setMessageValidationErrorType(String messageValidationErrorType) {
        this.messageValidationErrorType = messageValidationErrorType;
    }

    
    
}
