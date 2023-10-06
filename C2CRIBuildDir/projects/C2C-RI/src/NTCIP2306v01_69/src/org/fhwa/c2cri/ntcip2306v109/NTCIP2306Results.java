/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109;

import org.fhwa.c2cri.ntcip2306v109.operations.OperationResults;
import java.util.HashMap;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;

/**
 * Represents the results of an NTCIP2306 operation.
 *
 * @author TransCore ITS, LLC
 * 11/15/2013
 */public class NTCIP2306Results {

    /** the operation specified in the WSDL. */
    private String operation;
    
    /** the service specified in the WSDL. */
    private String service;
    
    /** the port specified in the WSDL. */
    private String port;
    
    /** the collection of operation results. */
    private HashMap<String, RequirementResult> resultsMap = new HashMap();
    
    /** the collection of request message results. */
    private HashMap<String, RequirementResult> requestMap = new HashMap();
    
    /** the collection of response message results. */
    private HashMap<String, RequirementResult> responseMap = new HashMap();
    
    /** the request message. */
    private static String REQUESTMESSAGE="REQUEST";
    
    /** the response message. */
    private static String RESPONSEMESSAGE="RESPONSE";

    /**
     * constructor.
     *
     * @param opResults – the results of the operation
     */
    public NTCIP2306Results(OperationResults opResults) {
        this.operation = opResults.getOperationSpecification().getOperationName();
        this.service = opResults.getOperationSpecification().getRelatedToService();
        this.port = opResults.getOperationSpecification().getRelatedToPort();
        
        
        processRequestMessage(opResults);
        processResponseMessage(opResults);
        processOperation(opResults);

    }

    /**
     * Performs testing of the request message.
     *
     * @param opResults the op results
     */
    private void processRequestMessage(OperationResults opResults) {
        // A one way operation does not contain message content in the request
        // so skip processing for one way operations.
        if (!OperationSpecification.OperationType.valueOf(opResults.getOperationSpecification().getOperationType()).equals(OperationSpecification.OperationType.GET)) {

            processXMLMessageEncoding(opResults, REQUESTMESSAGE);
            // 
            if (opResults.getOperationSpecification().getOperationInputEncoding().equals(
                    OperationSpecification.SOAP_ENCODING)) {

                if (OperationSpecification.OperationType.valueOf(opResults.getOperationSpecification().getOperationType()).equals(
                        OperationSpecification.OperationType.PUB)) {
                    processSOAPPub(opResults,REQUESTMESSAGE);
                }

                if (OperationSpecification.OperationType.valueOf(opResults.getOperationSpecification().getOperationType()).equals(
                        OperationSpecification.OperationType.SUB)) {
                    processSOAPSub(opResults,REQUESTMESSAGE);
                }
                if (OperationSpecification.OperationType.valueOf(opResults.getOperationSpecification().getOperationType()).equals(
                        OperationSpecification.OperationType.RR)) {
                    processSOAPRR(opResults,REQUESTMESSAGE);
                }

            } else if (opResults.getOperationSpecification().getOperationInputEncoding().equals(
                    OperationSpecification.GZIP_ENCODING)) {
                processGZIPMessageEncoding(opResults,REQUESTMESSAGE);
            } else {
                if (OperationSpecification.OperationType.valueOf(opResults.getOperationSpecification().getOperationType()).equals(
                        OperationSpecification.OperationType.RR)) {
                    processRR(opResults,REQUESTMESSAGE);
                }

            }

        }

    }

    /**
     * performs testing of the response message.
     *
     * @param opResults – the operation results
     */
    private void processResponseMessage(OperationResults opResults) {

            processXMLMessageEncoding(opResults,RESPONSEMESSAGE);
            // 
            if (opResults.getOperationSpecification().getOperationInputEncoding().equals(
                    OperationSpecification.SOAP_ENCODING)) {

                if (OperationSpecification.OperationType.valueOf(opResults.getOperationSpecification().getOperationType()).equals(
                        OperationSpecification.OperationType.PUB)) {
                    processSOAPSubPubReceipt(opResults,RESPONSEMESSAGE);
                }

                if (OperationSpecification.OperationType.valueOf(opResults.getOperationSpecification().getOperationType()).equals(
                        OperationSpecification.OperationType.SUB)) {
                    processSOAPSubPubReceipt(opResults,RESPONSEMESSAGE);
                }
                if (OperationSpecification.OperationType.valueOf(opResults.getOperationSpecification().getOperationType()).equals(
                        OperationSpecification.OperationType.RR)) {
                    processSOAPRR(opResults,RESPONSEMESSAGE);
                }

            } else if (opResults.getOperationSpecification().getOperationInputEncoding().equals(
                    OperationSpecification.GZIP_ENCODING)) {
                processGZIPMessageEncoding(opResults,RESPONSEMESSAGE);
            } else {
                if (OperationSpecification.OperationType.valueOf(opResults.getOperationSpecification().getOperationType()).equals(
                        OperationSpecification.OperationType.RR)) {
                    processRR(opResults,RESPONSEMESSAGE);
                }

            }


    }

    /**
     * performs XML messaging encoding testing.
     *
     * @param opResults the op results
     * @param messageType – the type of message
     */
    private void processXMLMessageEncoding(OperationResults opResults, String messageType) {
        if (messageType.equals(REQUESTMESSAGE)){
            boolean xmlv1Flag = opResults.getRequestMessage().getXmlStatus().isXMLv1Document();
            requestMap.put("4.1.1.a",
                        new RequirementResult(xmlv1Flag,
                        "4.1.1.a", opResults.getRequestMessage().getXmlStatus().getXMLErrors()));        
            boolean utf8Flag = opResults.getRequestMessage().getXmlStatus().isUTF8CharSet();
            requestMap.put("4.1.1.b",
                        new RequirementResult(utf8Flag,
                        "4.1.1.b", opResults.getRequestMessage().getXmlStatus().getXMLErrors()));        
            
        } else {
            boolean xmlv1Flag = opResults.getResponseMessage().getXmlStatus().isXMLv1Document();
            responseMap.put("4.1.1.a",
                        new RequirementResult(xmlv1Flag,
                        "4.1.1.a", opResults.getResponseMessage().getXmlStatus().getXMLErrors()));        
            boolean utf8Flag = opResults.getResponseMessage().getXmlStatus().isUTF8CharSet();
            responseMap.put("4.1.1.b",
                        new RequirementResult(utf8Flag,
                        "4.1.1.b", opResults.getResponseMessage().getXmlStatus().getXMLErrors()));        
            
        }
    }

    /**
     * performs testing of the operation results.
     *
     * @param opResults – the results of the operation
     */
    private void processOperation(OperationResults opResults) {
        if (opResults.getOperationSpecification().getRelatedServiceTransport().equals(OperationSpecification.ServiceTransportType.HTTP)){
            processHTTPTransport(opResults);
        } else if (opResults.getOperationSpecification().getRelatedServiceTransport().equals(OperationSpecification.ServiceTransportType.HTTPS)){
            processHTTPSTransport(opResults);
            
        } else if (opResults.getOperationSpecification().getRelatedServiceTransport().equals(OperationSpecification.ServiceTransportType.FTP)){       
            processFTPTransport(opResults);
            
        }
    }
    
    /**
     * performs testing of gzip message encoding.
     *
     * @param opResults the op results
     * @param messageType – the message type
     */
    private void processGZIPMessageEncoding(OperationResults opResults,  String messageType) {      
        if (messageType.equals(REQUESTMESSAGE)){
            boolean result = opResults.getRequestMessage().getGzipStatus().isNTCIP2306ValidGZIP()
                        && opResults.getRequestMessage().getXmlStatus().isNTCIP2306ValidXML();
            requestMap.put("4.1.2",
                        new RequirementResult(result,
                        "4.1.2", opResults.getRequestMessage().getGzipStatus().getGZIPErrors()));        
        } else {
            boolean result = opResults.getResponseMessage().getGzipStatus().isNTCIP2306ValidGZIP()
                        && opResults.getResponseMessage().getXmlStatus().isNTCIP2306ValidXML();
            responseMap.put("4.1.2",
                        new RequirementResult(result,
                        "4.1.2", opResults.getResponseMessage().getGzipStatus().getGZIPErrors()));                    
        }        
    }

    /**
     * processes a soap request response operation.
     *
     * @param opResults the op results
     * @param messageType – the type of message
     */
    private void processSOAPRR(OperationResults opResults,  String messageType) {
        if (messageType.equals(REQUESTMESSAGE)){
            requestMap.put("4.2.1.a", 
                    new RequirementResult(opResults.getRequestMessage().getSoapStatus().isValidRRSOAPEncoding(),
                    "4.2.1.a",opResults.getRequestMessage().getSoapStatus().getSOAPErrors()));
            
            requestMap.put("4.2.1.c", 
                    new RequirementResult(opResults.getRequestMessage().getXmlStatus().isXmlValidatedToWSDLSchemas(),
                    "4.2.1.c",opResults.getRequestMessage().getXmlStatus().getXMLErrors()));

            requestMap.put("5.1.2", 
                    new RequirementResult(opResults.getRequestMessage().getSoapStatus().isValidAgainstSchemas(),
                    "5.1.2",opResults.getRequestMessage().getSoapStatus().getSOAPErrors()));
            
            requestMap.put("7.1.2.f", 
                    new RequirementResult(opResults.getRequestMessage().getHttpStatus().isMatchingSOAPAction(),
                    "7.1.2.f",opResults.getRequestMessage().getHttpStatus().getHTTPErrors()));
            
            resultsMap.put("7.1.3.e", new RequirementResult(!opResults.getRequestMessage().isTransportErrorEncountered(),"7.1.3.e",
                    opResults.getRequestMessage().getTransportErrorDescription()));
        } else {
            responseMap.put("4.2.1.a", 
                    new RequirementResult(opResults.getResponseMessage().getSoapStatus().isValidRRSOAPEncoding(),
                    "4.2.1.a",opResults.getResponseMessage().getSoapStatus().getSOAPErrors()));
            
            responseMap.put("4.2.1.c", 
                    new RequirementResult(opResults.getResponseMessage().getXmlStatus().isXmlValidatedToWSDLSchemas(),
                    "4.2.1.c",opResults.getResponseMessage().getXmlStatus().getXMLErrors()));

            requestMap.put("5.1.2", 
                    new RequirementResult(opResults.getResponseMessage().getSoapStatus().isValidAgainstSchemas(),
                    "5.1.2",opResults.getResponseMessage().getSoapStatus().getSOAPErrors()));
            
        }
    }

    /**
     * Perform processing of the soap subscription operation.
     *
     * @param opResults the op results
     * @param messageType – the type of message
     */
    private void processSOAPSub(OperationResults opResults,  String messageType) {
            requestMap.put("4.2.2.1.a", 
                    new RequirementResult(opResults.getRequestMessage().getSoapStatus().isValidSubSOAPEncoding(),
                    "4.2.2.1.a",opResults.getRequestMessage().getSoapStatus().getSOAPErrors()));
            
            requestMap.put("4.2.2.1.c", 
                    new RequirementResult(opResults.getRequestMessage().getSoapStatus().isValidAgainstSchemas(),
                    "4.2.2.1.c",opResults.getRequestMessage().getSoapStatus().getSOAPErrors()));

            boolean subRules = true;
            String subErrorDescription = "";
            if (opResults.getOperationSpecification().getInputMessage().size() != opResults.getRequestMessage().getNumberMessageParts()){
                subRules = false;
                subErrorDescription = "The number of message parts in the REQUEST message did not match the number specified by the WSDL.";                
            } else {
                for (int ii = 0; ii < opResults.getOperationSpecification().getInputMessage().size();ii++){
                    if (!opResults.getOperationSpecification().getInputMessage().get(ii).getLocalPart().equals(
                        opResults.getRequestMessage().getMessagePartName(ii+1))){
                        subRules = false;
                        subErrorDescription = subErrorDescription.concat("\nThe message part "+opResults.getRequestMessage().getMessagePartName(ii+1)+"in the REQUEST message did not match the part specified by the WSDL.");                
                    }
                }
            }
            requestMap.put("7.2.1.1.a", 
                    new RequirementResult(subRules,
                    "7.2.1.1.a",subErrorDescription));
 
            requestMap.put("7.2.1.1.c", 
                    new RequirementResult(opResults.getRequestMessage().getXmlStatus().isXmlValidatedToWSDLSchemas(),
                    "7.2.1.1.c","The message is not valid to the WSDL Schema."));
            
            requestMap.put("7.2.4.d", 
                    new RequirementResult(opResults.getRequestMessage().getXmlStatus().isXmlValidatedToWSDLSchemas(),
                    "7.2.4.d","The subscriber endpoint was not determined due to invalid message content."));
    }

    /**
     * process a soap publication operation.
     *
     * @param opResults the op results
     * @param messageType – the message type
     */
    private void processSOAPPub(OperationResults opResults,  String messageType) {
            requestMap.put("4.2.2.2.a", 
                    new RequirementResult(opResults.getRequestMessage().getSoapStatus().isValidPubSOAPEncoding(),
                    "4.2.2.2.a",opResults.getRequestMessage().getSoapStatus().getSOAPErrors()));
            
            requestMap.put("4.2.2.2.c", 
                    new RequirementResult(opResults.getRequestMessage().getSoapStatus().isValidAgainstSchemas(),
                    "4.2.2.2.c",opResults.getRequestMessage().getSoapStatus().getSOAPErrors()));

            requestMap.put("7.2.1.1.b", 
                    new RequirementResult(opResults.getRequestMessage().isTransportErrorEncountered(),
                    "7.2.1.1.b","An error was encountered when sending a request to the Subscriber Callback Listener."));
        
            boolean pubRules = true;
            String pubErrorDescription = "";
            if (opResults.getOperationSpecification().getInputMessage().size() != opResults.getRequestMessage().getNumberMessageParts()){
                pubRules = false;
                pubErrorDescription = "The number of message parts in the REQUEST message did not match the number specified by the WSDL.";                
            } else {
                for (int ii = 0; ii < opResults.getOperationSpecification().getInputMessage().size();ii++){
                    if (!opResults.getOperationSpecification().getInputMessage().get(ii).getLocalPart().equals(
                        opResults.getRequestMessage().getMessagePartName(ii+1))){
                        pubRules = false;
                        pubErrorDescription = pubErrorDescription.concat("\nThe message part "+opResults.getRequestMessage().getMessagePartName(ii+1)+"in the REQUEST message did not match the part specified by the WSDL.");                
                    }
                }
            }
            requestMap.put("7.2.1.2.a", 
                    new RequirementResult(pubRules,
                    "7.2.1.2.a",pubErrorDescription));
 
            requestMap.put("7.2.1.2.b", 
                    new RequirementResult(pubRules,
                    "7.2.1.2.b",pubErrorDescription));

            requestMap.put("7.2.1.2.e", 
                    new RequirementResult(pubRules,
                    "7.2.1.2.e",pubErrorDescription));

            requestMap.put("7.2.1.1.f", 
                    new RequirementResult(opResults.getRequestMessage().getXmlStatus().isXmlValidatedToWSDLSchemas(),
                    "7.2.1.1.f","The message is not valid to the WSDL Schema."));

            requestMap.put("7.2.2.b", 
                    new RequirementResult(!opResults.getRequestMessage().isTransportErrorEncountered(),
                    "7.2.2.b","Could not determine if the publishing center supports the operation defined in the WSDL."));
            
    }

    /**
     * process the message for a subscription or publication receipt.
     *
     * @param opResults the op results
     * @param messageType – the message type
     */
    private void processSOAPSubPubReceipt(OperationResults opResults,  String messageType) {
            responseMap.put("4.2.2.3.a", 
                    new RequirementResult(opResults.getResponseMessage().getSoapStatus().isValidSubPubReceiptEncoding(),
                    "4.2.2.3.a",opResults.getResponseMessage().getSoapStatus().getSOAPErrors()));
            
            responseMap.put("4.2.2.3.c", 
                    new RequirementResult(opResults.getResponseMessage().getSoapStatus().isValidAgainstSchemas(),
                    "4.2.2.3.c",opResults.getResponseMessage().getSoapStatus().getSOAPErrors()));
       
            if (opResults.getOperationSpecification().isSubscriptionOperation()){
               responseMap.put("7.2.1.1.Publish.a", 
                    new RequirementResult(!opResults.getResponseMessage().isTransportErrorEncountered(),
                    "7.2.1.1.Publish.a","No subscription message was received from the Subscribing center."));

               boolean returnCodeSent = opResults.getResponseMessage().getHttpStatus().getStatusCode()>400;
               
               responseMap.put("7.2.1.1.Publish.b", 
                    new RequirementResult(returnCodeSent,
                    "7.2.1.1.Publish.b","No valid HTTP return code was sent."));
               
               returnCodeSent = opResults.getResponseMessage().getHttpStatus().getStatusCode()>0;
               responseMap.put("7.2.2.e", 
                    new RequirementResult(returnCodeSent,
                    "7.2.2.e","No valid HTTP return code was sent."));
               
            } else {
               responseMap.put("7.2.1.2.Subscribe.a", 
                    new RequirementResult(!opResults.getResponseMessage().isTransportErrorEncountered(),
                    "7.2.1.2.Subscribe.a","No publication message was received from the Subscribing center."));

               boolean returnCodeSent = opResults.getResponseMessage().getHttpStatus().getStatusCode()>400;
               
               responseMap.put("7.2.1.2.Subscribe.b", 
                    new RequirementResult(returnCodeSent,
                    "7.2.1.2.Subscribe.b","No HTTP error return code was sent."));
                
            }
    }

    /**
     * evaluate HTTP transport results.
     *
     * @param opResults – the operation results
     */
    private void processHTTPTransport(OperationResults opResults) {
            boolean result = opResults.getRequestMessage().getHttpStatus().isValidHTTPHeaders() && opResults.getResponseMessage().getHttpStatus().isValidHTTPHeaders();
            String resultErrors = opResults.getRequestMessage().getHttpStatus().getHTTPErrors() +"\n"+ opResults.getResponseMessage().getHttpStatus().getHTTPErrors();
            resultsMap.put("5.1.1", 
                    new RequirementResult(result,
                    "5.1.1",resultErrors));
       
    }

    /**
     * process secure HTTP transports.
     *
     * @param opResults – the operation results
     */
    private void processHTTPSTransport(OperationResults opResults) {
         boolean result = opResults.getRequestMessage().getHttpStatus().isValidHTTPHeaders() && opResults.getResponseMessage().getHttpStatus().isValidHTTPHeaders();
         String resultErrors = opResults.getRequestMessage().getHttpStatus().getHTTPErrors() +"\n"+ opResults.getResponseMessage().getHttpStatus().getHTTPErrors();
         resultsMap.put("5.1.1", 
                    new RequirementResult(result,
                    "5.1.1",resultErrors));
       
         resultsMap.put("5.1.3", 
                    new RequirementResult(result,
                    "5.1.3",resultErrors));        

   }

    /**
     * process and FTP transport.
     *
     * @param opResults – the operation results
     */
    private void processFTPTransport(OperationResults opResults) {
         boolean result = opResults.getRequestMessage().getFtpStatus().isValidFTPProcessing() && opResults.getResponseMessage().getFtpStatus().isValidFTPProcessing();
         String resultErrors = opResults.getRequestMessage().getFtpStatus().getFTPErrors() +"\n"+ opResults.getResponseMessage().getFtpStatus().getFTPErrors();
         resultsMap.put("5.2.1", 
                    new RequirementResult(result,
                    "5.2.1",resultErrors));
                    
    }

    /**
     * Process HTTP post.
     *
     * @param opResults – the operation results
     */
    private void processHTTPPost(OperationResults opResults) {
		// original implementation was empty
    }

    /**
     * process HTTP get.
     *
     * @param opResults – the operation results
     */
    private void processHTTPGet(OperationResults opResults) {
            responseMap.put("8.1.1.c", 
                    new RequirementResult(opResults.getRequestMessage().getSoapStatus().isValidPubSOAPEncoding(),
                    "8.1.1.c",opResults.getRequestMessage().getSoapStatus().getSOAPErrors()));
        
    }

    /**
     * process FTP get.
     *
     * @param opResults the op results
     */
    private void processFTPGet(OperationResults opResults) {
		// original implementation was empty
    }

    /**
     * process request response.
     *
     * @param opResults the op results
     * @param messageType – the type of message
     */
    private void processRR(OperationResults opResults,  String messageType) {
		// original implementation was empty
    }

    /**
     * process the get operation.
     *
     * @param opResults the op results
     * @param messageType the message type
     */
    private void processGet(OperationResults opResults,  String messageType) {
		// original implementation was empty
    }
    
    /**
     * represent the map as a string.
     *
     * @param inputMap the input map
     * @return the map represented as a string
     */
    private String mapToString(HashMap<String, RequirementResult> inputMap){
        String results = "";
        for (String mapKey : inputMap.keySet()) {
            results = results.concat(inputMap.get(mapKey).toString()).concat("\n");
        }
        return results;
    }
        
    /**
     * Represent all maps as a string.
     * 
     * @return the representation
     */
    @Override
    public String toString(){
        String results = "";
        results = results.concat("Operation Name: "+getOperation()+"\n");
        results = results.concat("Service Name: "+getService()+"\n");
        results = results.concat("Port Name: "+getPort()+"\n\n");

        // Process general results
        results = results.concat("OperationalResults:\n");
        results = results.concat(mapToString(resultsMap));

        // Process request message results
        results = results.concat("\nRequestResults:\n");
        results = results.concat(mapToString(requestMap));
        
        // Process response message results
        results = results.concat("\nResponseResults:\n");
        results = results.concat(mapToString(responseMap));
        
        return results;
    }
    
    /**
     * represent all maps as XML.
     *
     * @return the XML representation as a string
     */
    public String toXML(){
        String results = "<ntcip2306results operation=\""+getOperation()+"\" service=\""+getService()+"\" port=\"" + getPort()+"\">";
        // Process general results
        results = results.concat("<operationalResults>\n");
        results = results.concat(mapToString(resultsMap));
        results = results.concat("</operationalResults>\n");

        // Process request message results
        results = results.concat("<requestResults>\n");
        results = results.concat(mapToString(requestMap));
        results = results.concat("</requestResults>\n");
        
        // Process response message results
        results = results.concat("<responseResults>\n");
        results = results.concat(mapToString(responseMap));
        results = results.concat("</responseResults>\n");
                
        results = results.concat("</ntcip2306results>");
        return results;        
    }
    
    /**
     * perform a check on a particular requirement.
     *
     * @param requirement –the requirement to be verified
     * @return true if the requirement was successfully tested
     */
    public boolean checkRequirement(String requirement){
        boolean results = false;
        if (resultsMap.containsKey(requirement)){
            results = resultsMap.get(requirement).isPassed();
            return results;
        }
        if (requestMap.containsKey(requirement)){
            results = requestMap.get(requirement).isPassed();
            return results;
        }
        if (responseMap.containsKey(requirement)){
            results = responseMap.get(requirement).isPassed();
            return results;
        }
        return results;
    }
    
    
    /**
     * check a particular message type for a particular requirement.
     *
     * @param requirement – the requirement to be checked
     * @param messageType the message type
     * @return the result
     */
    public boolean checkRequirementForMessageType(String requirement, String messageType){
        boolean results = false;
        if (messageType.equalsIgnoreCase("request")){
            if (requestMap.containsKey(requirement)){
                results = requestMap.get(requirement).isPassed();
                return results;
            }
            
        } else if (messageType.equalsIgnoreCase("response")){
            if (responseMap.containsKey(requirement)){
                results = responseMap.get(requirement).isPassed();
                return results;
            }            
        }
        
        return results;        
    }

    /**
     * get the description of any errors associated with a requirement.
     *
     * @param requirement the requirement
     * @return the description
     */
    public String getErrorDescription(String requirement){
        String results = "This requirement was not confirmed through testing.";
        if (resultsMap.containsKey(requirement)){
            results = resultsMap.get(requirement).getDetails();
            return results;
        }
        if (requestMap.containsKey(requirement)){
            results = requestMap.get(requirement).getDetails();
            return results;
        }
        if (responseMap.containsKey(requirement)){
            results = responseMap.get(requirement).getDetails();
            return results;
        }
                
        return results;        
        
    }

    /**
     * get the description of any errors associated with a particular requirement and type of message.
     *
     * @param requirement the requirement
     * @param messageType the message type
     * @return any errors found
     */
    public String getErrorDescription(String requirement, String messageType){
        String results = "This requirement was not confirmed through testing.";
        if (messageType.equalsIgnoreCase("request")){
            if (requestMap.containsKey(requirement)){
                results = requestMap.get(requirement).getDetails();
                return results;
            }
            
        } else if (messageType.equalsIgnoreCase("response")){
            if (responseMap.containsKey(requirement)){
                results = responseMap.get(requirement).getDetails();
                return results;
            }            
        }
                
        return results;                
    }

    /**
     * getter method for the operation.
     *
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * getter method for the service.
     *
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * getter method for the port.
     *
     * @return the port
     */
    public String getPort() {
        return port;
    }
    
    
    /**
     * represent the results of a requirement test.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    class RequirementResult {

        /** the associated requirements. */
        private String requirement;
        
        /** indicates whether the requirement test passed or failed. */
        private boolean passed;
        
        /** the details of any errors. */
        private String details;

        /**
         * constructor.
         *
         * @param passed – whether the requirement test passed
         * @param requirement the requirement
         * @param details – the details of any errors
         */
        public RequirementResult(boolean passed, String requirement, String details) {
            this.passed = passed;
            this.requirement = requirement;
            this.details = details;
        }

        /**
         * getter for the requirements feel.
         *
         * @return the requirements
         */
        public String getRequirement() {
            return requirement;
        }

        /**
         * getter for the passed field.
         *
         * @return the passed status
         */
        public boolean isPassed() {
            return passed;
        }

        /**
         * getter for the details field.
         *
         * @return the details of any errors
         */
        public String getDetails() {
            return details;
        }
        
        /**
         * represents the results as a string.
         *
         * @return the results
         */
        public String toString(){
            return "Requirement: "+requirement + "    Satisfied: "+passed+"    Details: "+(passed?"":details);
        }
        
        /**
         * represents the results as XML.
         *
         * @return the results
         */
        public String toXML(){
            return "<requirementResult><requirement>"+requirement + 
                    "</requirement><satisfied>"+passed+"</satisfied>"+
                    (passed?"<details/>":"<details>"+details+"</details>")+
                    "</requirementResult>";            
        }
    }
}
