/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddv3verificationold.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TransCore ITS
 */
public class OperationSpecification {
    public static String REQUEST_RESPONSE_OPERATION = "RR";
    public static String SUBSCRIPTION_OPERATION = "SUB";
    public static String PUBLICATION_OPERATION = "PUB";
    public static String SOAP_ENCODING = "SOAP";
    public static String XML_ENCODING = "XML";
    public static String GZIP_ENCODING = "GZIP";
    public static String GET_OPERATION = "GET";
    public static String C2C_PUBLICATION_MESSAGE = "c2cMessagePublication";
    public static String C2C_SUBSCRIPTION_MESSAGE = "c2cMessageSubscription";

    public String operationName;
    public String relatedToPort;
    public String operationInputEncoding;
    public String operationOutputEncoding;
    public String operationFaultEncoding;
    public String soapAction;
    public String documentLocation;
    public List<String> inputMessage=new ArrayList<String>();;
    public List<String> outputMessage = new ArrayList<String>();
    public List<String> faultMessage = new ArrayList<String>();
    public String operationType;
    public List<String> errorsEncountered = new ArrayList<String>();
    public boolean erroneousSpecification=false;

    public boolean isSubscriptionOperation(){
        if(inputMessage.size()==2){
            if(inputMessage.get(0).equals(C2C_SUBSCRIPTION_MESSAGE)){
                return true;
            }
        }
        return false;
    }
    public boolean isPublicationOperation(){
        if(inputMessage.size()==2){
            if(inputMessage.get(0).equals(C2C_PUBLICATION_MESSAGE)){
                return true;
            }
        }
        return false;

    }

        public boolean isRequestResponseOperation(){
        if(inputMessage.size()==1){
            if(outputMessage.size() == 1){
                return true;
            }
        }
        return false;
    }

        public boolean isGetOperation(){
            if((inputMessage.size()==1)&&(outputMessage.size()==0)){
                return true;
            }
            return false;
        }

        public String toString(){
            String returnString = "";
            returnString = returnString.concat("OperationName: "+operationName+"\n");
            returnString = returnString.concat("Erroneous Specificaton?: "+erroneousSpecification+"\n");
            returnString = returnString.concat("RelatedToPort: "+relatedToPort+"\n");
            returnString = returnString.concat("OperationInputEncoding: "+operationInputEncoding+"\n");
            returnString = returnString.concat("OperationOutputEncoding: "+operationOutputEncoding+"\n");
            returnString = returnString.concat("OperationFaultEncoding: "+operationFaultEncoding+"\n");
            returnString = returnString.concat("SOAPAction: "+soapAction+"\n");
            returnString = returnString.concat("DocumentLocation: "+documentLocation+"\n");
            String messageList = "   ";
            for (String theMessage : inputMessage){
                messageList = messageList.concat(theMessage+"\n   ");
            }
            returnString = returnString.concat("InputMessage: " + messageList);
            messageList = "   ";
            for (String theMessage : outputMessage){
                messageList = messageList.concat(theMessage+"\n   ");
            }
            returnString = returnString.concat("\nOutputMessage: " + messageList);

            messageList = "   ";
            for (String theMessage : faultMessage){
                messageList = messageList.concat(theMessage+"\n   ");
            }
            returnString = returnString.concat("\nFaultMessage: " + messageList);

            returnString = returnString.concat("\nOperationType:" + operationType+"\n");

            String errorMessageList = "   ";
            for (String theMessage : errorsEncountered){
                errorMessageList = errorMessageList.concat(theMessage+"\n   ");
            }
            returnString = returnString.concat("\nError Messages:\n" + errorMessageList);

            return returnString;
        }

    public String getDocumentLocation() {
        return documentLocation;
    }

    public void setDocumentLocation(String documentLocation) {
        this.documentLocation = documentLocation;
    }

    public List<String> getFaultMessage() {
        return faultMessage;
    }

    public void setFaultMessage(List<String> faultMessage) {
        this.faultMessage = faultMessage;
    }

    public List<String> getInputMessage() {
        return inputMessage;
    }

    public void setInputMessage(List<String> inputMessage) {
        this.inputMessage = inputMessage;
    }

    public String getOperationInputEncoding() {
        return operationInputEncoding;
    }
    public String getOperationOutputEncoding() {
        return operationOutputEncoding;
    }
    public String getOperationFaultEncoding() {
        return operationFaultEncoding;
    }

    public void setOperationInputEncoding(String operationInputEncoding) {
        this.operationInputEncoding = operationInputEncoding;
    }

    public void setOperationOutputEncoding(String operationOutputEncoding) {
        this.operationOutputEncoding = operationOutputEncoding;
    }

    public void setOperationFaultEncoding(String operationFaultEncoding) {
        this.operationFaultEncoding = operationFaultEncoding;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public List<String> getOutputMessage() {
        return outputMessage;
    }

    public void setOutputMessage(List<String> outputMessage) {
        this.outputMessage = outputMessage;
    }

    public String getRelatedToPort() {
        return relatedToPort;
    }

    public void setRelatedToPort(String relatedToPort) {
        this.relatedToPort = relatedToPort;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public boolean isErroneousSpecification() {
        return erroneousSpecification;
    }

    public void setErroneousSpecification(boolean erroneousSpecification) {
        this.erroneousSpecification = erroneousSpecification;
    }

    public List<String> getErrorsEncountered() {
        return errorsEncountered;
    }

    public void setErrorsEncountered(List<String> errorsEncountered) {
        this.errorsEncountered = errorsEncountered;
    }


}
