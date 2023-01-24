/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;

/**
 *
 * @author TransCore ITS, LLC
 */
public class TestCaseSpec {
    private String wsdlFile;
    private String serviceName;
    private String portName;
    private String operationName;
    private byte[] requestMessage;
    private byte[] responseMessage;
    private ArrayList<byte[]> multiPartRequestMessage;
    private ArrayList<byte[]> multiPartResponseMessage;
    private boolean skipEncoding;
    private boolean transportErrorExpected;
    private String transportErrorDescription;
    private boolean encodingErrorExpected;
    private String encodingErrorDescription;
    private boolean messageErrorExpected;
    private String messageErrorDescription;
    private boolean messageValidationErrorExpected;
    private String messageValidationErrorDescription;
    private boolean operationErrorExptected;
    private String operationErrorDescription;
    private String relatedServiceName;
    private String relatedPortName;
    private String relatedOperationName;
    private byte[] relatedRequestMessage;
    private byte[] relatedResponseMessage;
    private ArrayList<byte[]> multiPartRelatedRequestMessage;
    private ArrayList<byte[]> multiPartRelatedResponseMessage;
    private int numPublicationsExpected;
    private String testCaseName;
    private String testCaseDataFile;
    private String testCaseDescription;
    private String testCasePubDataFile;
    private String testCasePubDescription;
    private String needId;
    private OPERATIONTYPE operationType;
    public static enum OPERATIONTYPE{FTPGET,HTTPGET,HTTPPOST,HTTPSOAPRR,HTTPSOAPSUB,HTTPSOAPPUB}
    public static String SOAPENVELOPE="<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body>BODYHERE</SOAP-ENV:Body></SOAP-ENV:Envelope>";
    public static String BODYPLACEHOLDERTEXT = "BODYHERE";
    
    public String getWsdlFile() {
        return wsdlFile;
    }

    public void setWsdlFile(String wsdlFile) {
        this.wsdlFile = wsdlFile;
    }   

    public OPERATIONTYPE getOperationType() {
        return operationType;
    }

    public void setOperationType(OPERATIONTYPE operationType) {
        this.operationType = operationType;
    }
    
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public byte[] getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(byte[] requestMessage) {
        this.requestMessage = requestMessage;
    }

    public byte[] getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(byte[] responseMessage) {
        this.responseMessage = responseMessage;
    }

    public boolean isSkipEncoding() {
        return skipEncoding;
    }

    public void setSkipEncoding(boolean skipEncoding) {
        this.skipEncoding = skipEncoding;
    }

    public boolean isTransportErrorExpected() {
        return transportErrorExpected;
    }

    public void setTransportErrorExpected(boolean transportErrorExpected) {
        this.transportErrorExpected = transportErrorExpected;
    }

    public String getTransportErrorDescription() {
        return transportErrorDescription;
    }

    public void setTransportErrorDescription(String transportErrorDescription) {
        this.transportErrorDescription = transportErrorDescription;
    }

    public boolean isEncodingErrorExpected() {
        return encodingErrorExpected;
    }

    public void setEncodingErrorExpected(boolean encodingErrorExpected) {
        this.encodingErrorExpected = encodingErrorExpected;
    }

    public String getEncodingErrorDescription() {
        return encodingErrorDescription;
    }

    public void setEncodingErrorDescription(String encodingErrorDescription) {
        this.encodingErrorDescription = encodingErrorDescription;
    }

    public boolean isMessageErrorExpected() {
        return messageErrorExpected;
    }

    public void setMessageErrorExpected(boolean messageErrorExpected) {
        this.messageErrorExpected = messageErrorExpected;
    }

    public String getMessageErrorDescription() {
        return messageErrorDescription;
    }

    public void setMessageErrorDescription(String messageErrorDescription) {
        this.messageErrorDescription = messageErrorDescription;
    }

    public boolean isMessageValidationErrorExpected() {
        return messageValidationErrorExpected;
    }

    public void setMessageValidationErrorExpected(boolean messageValidationErrorExpected) {
        this.messageValidationErrorExpected = messageValidationErrorExpected;
    }

    public String getMessageValidationErrorDescription() {
        return messageValidationErrorDescription;
    }

    public void setMessageValidationErrorDescription(String messageValidationErrorDescription) {
        this.messageValidationErrorDescription = messageValidationErrorDescription;
    }

    public boolean isOperationErrorExptected() {
        return operationErrorExptected;
    }

    public void setOperationErrorExptected(boolean operationErrorExptected) {
        this.operationErrorExptected = operationErrorExptected;
    }

    public String getOperationErrorDescription() {
        return operationErrorDescription;
    }

    public void setOperationErrorDescription(String operationErrorDescription) {
        this.operationErrorDescription = operationErrorDescription;
    }

    public String getRelatedServiceName() {
        return relatedServiceName;
    }

    public void setRelatedServiceName(String relatedServiceName) {
        this.relatedServiceName = relatedServiceName;
    }

    public String getRelatedPortName() {
        return relatedPortName;
    }

    public void setRelatedPortName(String relatedPortName) {
        this.relatedPortName = relatedPortName;
    }

    public String getRelatedOperationName() {
        return relatedOperationName;
    }

    public void setRelatedOperationName(String relatedOperationName) {
        this.relatedOperationName = relatedOperationName;
    }

    public byte[] getRelatedRequestMessage() {
        return relatedRequestMessage;
    }

    public void setRelatedRequestMessage(byte[] relatedRequestMessage) {
        this.relatedRequestMessage = relatedRequestMessage;
    }

    public byte[] getRelatedResponseMessage() {
        return relatedResponseMessage;
    }

    public void setRelatedResponseMessage(byte[] relatedResponseMessage) {
        this.relatedResponseMessage = relatedResponseMessage;
    }

    public int getNumPublicationsExpected() {
        return numPublicationsExpected;
    }

    public void setNumPublicationsExpected(int numPublicationsExpected) {
        this.numPublicationsExpected = numPublicationsExpected;
    }           

    public String getTestCaseName() {
        return testCaseName;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public String getTestCaseDataFile() {
        return testCaseDataFile;
    }

    public void setTestCaseDataFile(String testCaseDataFile) {
        this.testCaseDataFile = testCaseDataFile;
    }

    public String getTestCaseDescription() {
        return testCaseDescription;
    }

    public void setTestCaseDescription(String testCaseDescription) {
        this.testCaseDescription = testCaseDescription;
    }

    public String getTestCasePubDataFile() {
        return testCasePubDataFile;
    }

    public void setTestCasePubDataFile(String testCasePubDataFile) {
        this.testCasePubDataFile = testCasePubDataFile;
    }

    public String getTestCasePubDescription() {
        return testCasePubDescription;
    }

    public void setTestCasePubDescription(String testCasePubDescription) {
        this.testCasePubDescription = testCasePubDescription;
    }

    public String getNeedId() {
        return needId;
    }

    public void setNeedId(String needId) {
        this.needId = needId;
    }
     
}
