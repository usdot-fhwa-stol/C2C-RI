/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;
import org.fhwa.c2cri.automation.AutomationHandler;
import org.fhwa.c2cri.automation.AutomationListener;
import org.fhwa.c2cri.ntcip2306v109.c2cadmin.C2CMessageSubscription;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationResults;
import org.fhwa.c2cri.ntcip2306v109.operations.NTCIP2306OperationException;
import org.fhwa.c2cri.ntcip2306v109.subpub.DefaultSubPubMapper;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306MessageException;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306MessageValidationException;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.CenterConfigurationController;
import org.fhwa.c2cri.ntcip2306v109.status.NTCIP2306Status;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * Provides the main controller function for NTCIP 2306 related operations. It
 * is provides a controlled interface to the CenterConfigurationController
 * object.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class NTCIP2306Controller {

    /**
     * the RIWSDL object which represents the wsdl file.
     */
    private RIWSDL riWSDL;
    /**
     * the referenced Center Configuration Controller object.
     */
    private CenterConfigurationController ccc;

    /** The log. */
    protected static Logger log = Logger.getLogger(NTCIP2306Controller.class.getName());
     
    /**
     * Constructor.
     *
     * @param theWSDL - reference to the WSDL file
     * @param ccc - reference to the CenterConfigurationController
     */
    public NTCIP2306Controller(RIWSDL theWSDL, CenterConfigurationController ccc) {
        this.riWSDL = theWSDL;
        this.ccc = ccc;
    }

    /**
     * Gets the ri wsdl.
     *
     * @return the wsdl object
     */
    public RIWSDL getRiWSDL() {
        return riWSDL;
    }

    /**
     * Perform an FTP Get EC Operation.
     *
     * @param service - the related service from the WSDL
     * @param port - the related port from the WSDL
     * @param operation - the related operation from the WSDL
     * @return - the results of the operation.
     * @throws Exception the exception
     */
    public NTCIP2306ControllerResults performFTPGetEC(String service, String port, String operation) throws Exception {
        OperationIdentifier opId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.HANDLER);
        OperationIdentifier listenerOpId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.LISTENER);

        Integer responseTimeOut = 10000;
        try {
            responseTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.FTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.FTP_CLIENT_RESPONSE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.FTP_CLIENT_RESPONSE_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ccc.registerForOperation(opId);
        // The FTP Get request does not have text that is sent to the OC.
        // However, we need to send a message to the FTP Client listener to start the process.
        NTCIP2306Message fakeMsg = new NTCIP2306Message("", "", "".getBytes());
        ccc.sendRequest(listenerOpId, fakeMsg);


        OperationResults opResults = ccc.getOperationResults(opId, responseTimeOut);
        fakeMsg = ccc.getResponse(opId, 500);
        ccc.unregisterForOperation(opId);

        if (ccc.isRequestPresent(listenerOpId)) {
            throw new Exception("Internal C2CRI Error:  The request was not processed by the FTP Handler.");
        }

        return processFTPGetECMsg(opResults, responseTimeOut);
    }

    /**
     * Perform an FTP Get OC Operation.
     *
     * @param service - the related service from the WSDL
     * @param port - the related port from the WSDL
     * @param operation - the related operation from the WSDL
     * @param responseMessage - the response message to be transmitted
     * @param skipEncoding - send the response message without additional
     * encoding
     * @return - the results of the operation.
     * the results of the operation.
     * @throws Exception the exception
     */
    public NTCIP2306ControllerResults performFTPGetOC(String service, String port, String operation,
            String responseMessage, boolean skipEncoding) throws Exception {

        OperationIdentifier opId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.HANDLER);
        OperationIdentifier responseOpId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.LISTENER);
        OperationResults opResults = null;

        NTCIP2306Message responseMsg = new NTCIP2306Message(responseMessage.getBytes());
        responseMsg.setSkipEncoding(skipEncoding);
        ccc.registerForOperation(opId);

        Integer requestTimeOut = 15000;
        try {
            requestTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.FTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.FTP_SERVER_RECEIVE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.FTP_SERVER_RECEIVE_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        // Skip further processing if we are testing in an automated mode.
        if(RIParameters.getInstance().getParameterValue(RIParameters.RI_TESTING_PARAMETER_GROUP, RIParameters.RI_AUTOTEST_PARAMETER, "false").equalsIgnoreCase("true")) {
            AutomationHandler.getInstance().publishAutomationUpdate(AutomationListener.AutomationEventType.OWNER_CENTER_READY, "The FTP GET OC is ready to receive...");
        } else
            javax.swing.JOptionPane.showMessageDialog(null, "The FTP GET OC is ready to receive...");

        NTCIP2306Message requestMsg = ccc.getRequest(opId, requestTimeOut);
        if (requestMsg != null) {
            ccc.sendResponse(responseOpId, responseMsg);
            opResults = ccc.getOperationResults(opId, 10000);
        }
        ccc.unregisterForOperation(opId);

        if (ccc.isResponsePresent(responseOpId)) {
            throw new Exception("Internal C2CRI Error:  The response was not processed by the FTP Handler.");
        }

        return processFTPGetOCOperation(opResults, requestTimeOut);
    }

    /**
     * Perform an HTTP Get EC Operation.
     *
     * @param service - the related service from the WSDL
     * @param port - the related port from the WSDL
     * @param operation - the related operation from the WSDL
     * @return - the results of the operation.
     * @throws Exception the exception
     */
    public NTCIP2306ControllerResults performHTTPGetEC(String service, String port, String operation) throws Exception {
        OperationIdentifier opId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.HANDLER);
        OperationIdentifier listenerOpId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.LISTENER);

        Integer responseTimeOut = 10000;
        try {
            responseTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.HTTP_CLIENT_RESPONSE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_CLIENT_RESPONSE_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ccc.registerForOperation(opId);
        // The FTP Get request does not have text that is sent to the OC.
        // However, we need to send a message to the FTP Client listener to start the process.
        NTCIP2306Message fakeMsg = new NTCIP2306Message("", "", "".getBytes());
        ccc.sendRequest(listenerOpId, fakeMsg);


        OperationResults opResults = ccc.getOperationResults(opId, responseTimeOut);
        fakeMsg = ccc.getResponse(opId, 500);
        ccc.unregisterForOperation(opId);

        if (ccc.isRequestPresent(listenerOpId)) {
            throw new Exception("Internal C2CRI Error:  The request was not processed by the HTTP Handler.");
        }


        return processHTTPGetECOperation(opResults, responseTimeOut);
    }

    /**
     * Perform and HTTP Get OC Operation.
     *
     * @param service - the related service from the WSDL
     * @param port - the related port from the WSDL
     * @param operation - the related operation from the WSDL
     * @param responseMessage - the response message to be sent to the SUT
     * @param skipEncoding - flag indicating whether to encode the
     * responseMessage
     * @return - the results of the operation
     * @throws Exception the exception
     */
    public NTCIP2306ControllerResults performHTTPGetOC(String service, String port, String operation,
            String responseMessage, boolean skipEncoding) throws Exception {

        OperationIdentifier opId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.HANDLER);
        OperationIdentifier responseOpId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.LISTENER);
        OperationResults opResults = null;

        NTCIP2306Message responseMsg = new NTCIP2306Message(responseMessage.getBytes());
        responseMsg.setSkipEncoding(skipEncoding);
        ccc.registerForOperation(opId);

        Integer requestTimeOut = 15000;
        try {
            requestTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Skip further processing if we are testing in an automated mode.
        if(RIParameters.getInstance().getParameterValue(RIParameters.RI_TESTING_PARAMETER_GROUP, RIParameters.RI_AUTOTEST_PARAMETER, "false").equalsIgnoreCase("true")) {
            AutomationHandler.getInstance().publishAutomationUpdate(AutomationListener.AutomationEventType.OWNER_CENTER_READY, "The HTTP GET OC is ready to receive...");
        } else
            javax.swing.JOptionPane.showMessageDialog(null, "The HTTP GET OC is ready to receive...");

        NTCIP2306Message requestMsg = ccc.getRequest(opId, requestTimeOut);
        if (requestMsg != null) {
            ccc.sendResponse(responseOpId, responseMsg);
            opResults = ccc.getOperationResults(opId, 10000);
        }
        ccc.unregisterForOperation(opId);

        if (ccc.isResponsePresent(responseOpId)) {
            throw new Exception("Internal C2CRI Error:  The response was not processed by the HTTP Handler.");
        }

        return processHTTPGetOCOperation(opResults, requestTimeOut);
    }

    /**
     * Perform the HTTP Post EC Operation.
     *
     * @param service - the related service from the WSDL
     * @param port - the related port from the WSDL
     * @param operation - the related operation from the WSDL
     * @param requestMessage - the request message to be sent to the SUT
     * @param skipEncoding - flag indicating whether to encode the
     * requestMessage
     * @return the results of the operation
     * @throws Exception the exception
     */
    public NTCIP2306ControllerResults performHTTPPostEC(String service, String port, String operation, String requestMessage, boolean skipEncoding) throws Exception {
        OperationIdentifier opId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.HANDLER);
        OperationIdentifier listenerOpId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.LISTENER);

        Integer responseTimeOut = 10000;
        try {
            responseTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.HTTP_CLIENT_RESPONSE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_CLIENT_RESPONSE_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        NTCIP2306Message requestMsg = new NTCIP2306Message(requestMessage.getBytes());
        requestMsg.setSkipEncoding(skipEncoding);

        ccc.registerForOperation(opId);
        ccc.sendRequest(listenerOpId, requestMsg);


        OperationResults opResults = ccc.getOperationResults(opId, responseTimeOut);
        NTCIP2306Message fakeMsg = ccc.getResponse(opId, 500);
        ccc.unregisterForOperation(opId);

        if (ccc.isRequestPresent(listenerOpId)) {
            throw new Exception("Internal C2CRI Error:  The request was not processed by the HTTP Handler.");
        }

        return processHTTPPostECOperation(opResults, responseTimeOut);
    }

    /**
     * Perform the HTTP Post OC Request Operation.
     *
     * @param service - the related service from the WSDL
     * @param port - the related port from the WSDL
     * @param operation - the related operation from the WSDL
     * @return - the results of the operation.
     * @throws Exception the exception
     */
    public NTCIP2306ControllerResults performHTTPPostOCRequest(String service, String port, String operation) throws Exception {

        OperationIdentifier opId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.HANDLER);

        ccc.registerForOperation(opId);

        Integer requestTimeOut = 15000;
        try {
            requestTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Skip further processing if we are testing in an automated mode.
        if(RIParameters.getInstance().getParameterValue(RIParameters.RI_TESTING_PARAMETER_GROUP, RIParameters.RI_AUTOTEST_PARAMETER, "false").equalsIgnoreCase("true")) {
            AutomationHandler.getInstance().publishAutomationUpdate(AutomationListener.AutomationEventType.OWNER_CENTER_READY, "The HTTP POST OC is ready to receive...");
        } else
            javax.swing.JOptionPane.showMessageDialog(null, "The HTTP POST OC is ready to receive...");

        NTCIP2306Message requestMsg = ccc.getRequest(opId, requestTimeOut);

        return processHTTPPostOCRequestOperation(riWSDL.getOperationSpecification(service, port, operation), requestMsg, requestTimeOut);
    }

    /**
     * Perform the HTTP Post OC Response Operation.
     *
     * @param service - the related service from the WSDL
     * @param port - the related port from the WSDL
     * @param operation - the related operation from the WSDL
     * @param responseMessage - the response message to send to the SUT
     * @param skipEncoding - flag indication whether to encode the
     * responseMessage
     * @return - the results of the operation.
     * the results of the operation
     * @throws Exception the exception
     */
    public NTCIP2306ControllerResults performHTTPPostOCResponse(String service, String port, String operation,
            String responseMessage, boolean skipEncoding) throws Exception {

        OperationIdentifier opId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.HANDLER);
        OperationIdentifier responseOpId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.LISTENER);
        OperationResults opResults = null;

        NTCIP2306Message responseMsg = new NTCIP2306Message(responseMessage.getBytes());
        responseMsg.setSkipEncoding(skipEncoding);

        Integer responseTimeOut = 15000;
        try {
            responseTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ccc.sendResponse(responseOpId, responseMsg);
        opResults = ccc.getOperationResults(opId, 10000);
        ccc.unregisterForOperation(opId);

        if (ccc.isResponsePresent(responseOpId)) {
            throw new Exception("Internal C2CRI Error:  The response was not processed by the HTTP Handler.");
        }

        return processHTTPPostOCResponseOperation(opResults, responseTimeOut);
    }

    /**
     * Perform the SOAP Request Response EC Operation.
     *
     * @param service - the related service from the WSDL
     * @param port - the related port from the WSDL
     * @param operation - the related operation from the WSDL
     * @param requestMessage - the request message to be sent to the SUT
     * @param skipEncoding - flag indicating whether to encode the request
     * message
     * @return - the results of the operation
     * @throws Exception the exception
     */
    public NTCIP2306ControllerResults performSOAPRREC(String service, String port, String operation, String requestMessage, boolean skipEncoding) throws Exception {
        OperationIdentifier opId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.HANDLER);
        OperationIdentifier listenerOpId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.LISTENER);

        Integer responseTimeOut = 10000;
        try {
            responseTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.HTTP_CLIENT_RESPONSE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_CLIENT_RESPONSE_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        NTCIP2306Message requestMsg = new NTCIP2306Message(requestMessage.getBytes(), skipEncoding);
        requestMsg.setSkipEncoding(skipEncoding);

        ccc.registerForOperation(opId);
        ccc.sendRequest(listenerOpId, requestMsg);


        OperationResults opResults = ccc.getOperationResults(opId, responseTimeOut);
        NTCIP2306Message fakeMsg = ccc.getResponse(opId, 500);
        ccc.unregisterForOperation(opId);

        if (ccc.isRequestPresent(listenerOpId)) {
            throw new Exception("Internal C2CRI Error:  The request was not processed by the HTTP Handler.");
        }

        return processSOAPRRECOperation(opResults, responseTimeOut);
    }

    /**
     * Perform the Request portion of a SOAP Request Response OC operation.
     *
     * @param service - the related service from the WSDL
     * @param port - the related port from the WSDL
     * @param operation - the related operation from the WSDL
     * @return - the results of the operation.
     * @throws Exception the exception
     */
    public NTCIP2306ControllerResults performSOAPRROCRequest(String service, String port, String operation) throws Exception {

        OperationIdentifier opId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.HANDLER);

        ccc.registerForOperation(opId);

        Integer requestTimeOut = 15000;
        try {
            requestTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        // Skip further processing if we are testing in an automated mode.
        if(RIParameters.getInstance().getParameterValue(RIParameters.RI_TESTING_PARAMETER_GROUP, RIParameters.RI_AUTOTEST_PARAMETER, "false").equalsIgnoreCase("true")) {
            AutomationHandler.getInstance().publishAutomationUpdate(AutomationListener.AutomationEventType.OWNER_CENTER_READY, "The SOAP RR OC is ready to receive...");
        } else
            javax.swing.JOptionPane.showMessageDialog(null, "The SOAP RR OC is ready to receive...");

        NTCIP2306Message requestMsg = ccc.getRequest(opId, requestTimeOut);

        return processSOAPRROCRequestOperation(riWSDL.getOperationSpecification(service, port, operation), requestMsg, requestTimeOut);
    }

    /**
     * Perform the response portion of a SOAP Request Response OC operation.
     *
     * @param service - the related service from the WSDL
     * @param port - the related port from the WSDL
     * @param operation - the related operation from the WSDL
     * @param responseMessage - the response message to be sent to the SUT
     * @param skipEncoding - flag indicating whether the responseMessage should
     * be encoded
     * @param isErrorResponse - indicate whether the response message reports an
     * error
     * @return the results of the operation.
     * @throws Exception the exception
     */
    public NTCIP2306ControllerResults performSOAPRROCResponse(String service, String port, String operation,
            String responseMessage, boolean skipEncoding, boolean isErrorResponse) throws Exception {

        OperationIdentifier opId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.HANDLER);
        OperationIdentifier responseOpId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.LISTENER);
        OperationResults opResults = null;

        NTCIP2306Message responseMsg = new NTCIP2306Message(responseMessage.getBytes(), skipEncoding);
        responseMsg.setSkipEncoding(skipEncoding);
        if (isErrorResponse) {
            responseMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.ERROR);
        }

        Integer responseTimeOut = 15000;
        try {
            responseTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ccc.sendResponse(responseOpId, responseMsg);
        opResults = ccc.getOperationResults(opId, 10000);
        ccc.unregisterForOperation(opId);

        if (ccc.isResponsePresent(responseOpId)) {
            throw new Exception("Internal C2CRI Error:  The response was not processed by the HTTP Handler.");
        }

        return processSOAPRROCResponseOperation(opResults, responseTimeOut);
    }

    /**
     * Perform a SOAP Subscription operation as an EC.
     *
     * @param service - the related service from the WSDL
     * @param port - the related port from the WSDL
     * @param operation - the related operation from the WSDL
     * @param requestMessage - the request message to be sent to the SUT.
     * @param skipEncoding - flag indicating whether additional encoding should
     * be performed on the request message.
     * @return - the results of the operation.
     * the results of the operation.
     * @throws Exception the exception
     */
    public NTCIP2306ControllerResults performSOAPSUBEC(String service, String port, String operation, String requestMessage, boolean skipEncoding) throws Exception {
        OperationIdentifier opId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.HANDLER);
        OperationIdentifier listenerOpId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.LISTENER);

        Integer responseTimeOut = 10000;
        try {
            responseTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.HTTP_CLIENT_RESPONSE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_CLIENT_RESPONSE_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        NTCIP2306Message requestMsg = new NTCIP2306Message(requestMessage.getBytes(), skipEncoding);
        requestMsg.setSkipEncoding(skipEncoding);

        ccc.registerForOperation(opId);
        ccc.sendRequest(listenerOpId, requestMsg);


        OperationResults opResults = ccc.getOperationResults(opId, responseTimeOut);
        NTCIP2306Message fakeMsg = ccc.getResponse(opId, 500);
        ccc.unregisterForOperation(opId);

        if (ccc.isRequestPresent(listenerOpId)) {
            throw new Exception("Internal C2CRI Error:  The request was not processed by the HTTP Handler.");
        }

        NTCIP2306ControllerResults results = processSOAPSUBECOperation(opResults, responseTimeOut);
        results.setSubscriptionActive(ccc.isSubscriptionActive(listenerOpId));
        results.setSubscriptionPeriodicFrequency(getMessageSubscriptionFrequency(results.getRequestMessage()));
        try {
            OperationIdentifier pubOpId = DefaultSubPubMapper.getRelatedPublicationOperationId(operation, requestMsg);
            results.setPublicationComplete(!ccc.isPublicationActive(pubOpId));
            results.setPublicationCount(ccc.getPublicationCount(pubOpId));
            results.setMillisSinceLastPeriodicPublication(ccc.getMillisSinceLastPeriodicPublication(pubOpId));
        } catch (Exception ex) {
            results.setPublicationComplete(true);
            results.setPublicationCount(0);
            ex.printStackTrace();
            log.debug("Publication Error: "+ex.getMessage());                    
            
        }
        return results;
    }

    /**
     * Perform the request portion of a SOAP subscription as an OC.
     *
     * @param service - the related service from the WSDL
     * @param port - the related port from the WSDL
     * @param operation - the related operation from the WSDL
     * @return - the results of the operation.
     * @throws Exception the exception
     */
    public NTCIP2306ControllerResults performSOAPSUBOCRequest(String service, String port, String operation) throws Exception {

        OperationIdentifier opId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.HANDLER);

        ccc.registerForOperation(opId);

        Integer requestTimeOut = 15000;
        try {
            requestTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        // Skip further processing if we are testing in an automated mode.
        if(RIParameters.getInstance().getParameterValue(RIParameters.RI_TESTING_PARAMETER_GROUP, RIParameters.RI_AUTOTEST_PARAMETER, "false").equalsIgnoreCase("true")) {
            AutomationHandler.getInstance().publishAutomationUpdate(AutomationListener.AutomationEventType.OWNER_CENTER_READY, "The SOAP SUB OC is ready to receive...");
        } else
            javax.swing.JOptionPane.showMessageDialog(null, "The SOAP SUB OC is ready to receive...");

        NTCIP2306Message requestMsg = ccc.getRequest(opId, requestTimeOut);
        NTCIP2306ControllerResults results = processSOAPSUBOCRequestOperation(riWSDL.getOperationSpecification(service, port, operation), requestMsg, requestTimeOut);
        results.setSubscriptionActive(ccc.isSubscriptionActive(opId));
        results.setSubscriptionPeriodicFrequency(getMessageSubscriptionFrequency(results.getRequestMessage()));

        return results;
    }

    /**
     * Perform the response portion of a SOAP Subscription operation as an OC.
     *
     * @param service - the related service from the WSDL
     * @param port - the related port from the WSDL
     * @param operation - the related operation from the WSDL
     * @param responseMessage - the response message to send to the SUT
     * @param skipEncoding - flag indicating whether to perform encoding on the
     * responseMessage
     * @param isErrorResponse - flag indicating whether the responseMessage
     * reports an error.
     * @return the results of the operation.
     * @throws Exception the exception
     */
    public NTCIP2306ControllerResults performSOAPSUBOCResponse(String service, String port, String operation,
            String responseMessage, boolean skipEncoding, boolean isErrorResponse) throws Exception {

        OperationIdentifier opId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.HANDLER);
        OperationIdentifier responseOpId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.LISTENER);
        OperationResults opResults = null;

        NTCIP2306Message responseMsg = new NTCIP2306Message(responseMessage.getBytes(), skipEncoding);
        responseMsg.setSkipEncoding(skipEncoding);
        if (isErrorResponse) {
            responseMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.ERROR);
        }

        Integer responseTimeOut = 15000;
        try {
            responseTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ccc.sendResponse(responseOpId, responseMsg);
        opResults = ccc.getOperationResults(opId, 10000);
        ccc.unregisterForOperation(opId);

        if (ccc.isRequestPresent(responseOpId)) {
            throw new Exception("Internal C2CRI Error:  The response was not processed by the HTTP Handler.");
        }

        NTCIP2306ControllerResults results = processSOAPSUBOCResponseOperation(opResults, responseTimeOut);
        results.setSubscriptionActive(ccc.isSubscriptionActive(opId));
        results.setSubscriptionPeriodicFrequency(getMessageSubscriptionFrequency(results.getRequestMessage()));
        try {
            OperationIdentifier pubOpId = DefaultSubPubMapper.getRelatedPublicationOperationId(operation, opResults.getRequestMessage());
            results.setPublicationComplete(!ccc.isPublicationActive(pubOpId));
            results.setPublicationCount(ccc.getPublicationCount(pubOpId));
            results.setMillisSinceLastPeriodicPublication(ccc.getMillisSinceLastPeriodicPublication(pubOpId));
        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("Subscription Error: "+ex.getMessage());                    
        }
        return results;
    }

    /**
     * Perform a SOAP Publication operation as an OC.
     *
     * @param service - the related service from the WSDL
     * @param port - the related port from the WSDL
     * @param operation - the related operation from the WSDL
     * @param requestMessage - the request message to be sent to the SUT.
     * @param skipEncoding - flag indicating whether to encode the
     * requestMessage.
     * @return - the results of the operation.
     * @throws Exception the exception
     */
    public NTCIP2306ControllerResults performSOAPPUBOC(String service, String port, String operation, String requestMessage, boolean skipEncoding) throws Exception {
        OperationIdentifier opId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.HANDLER);
        OperationIdentifier listenerOpId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.LISTENER);

        Integer responseTimeOut = 10000;
        try {
            responseTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.HTTP_CLIENT_PUBLICATION_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_CLIENT_PUBLICATION_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        NTCIP2306Message requestMsg = new NTCIP2306Message(requestMessage.getBytes(), skipEncoding);
        requestMsg.setSkipEncoding(skipEncoding);

        ccc.registerForOperation(opId);
        ccc.sendRequest(listenerOpId, requestMsg);


        NTCIP2306Message fakeMsg = ccc.getResponse(opId, responseTimeOut);
        ccc.unregisterForOperation(opId);
        OperationResults opResults = new OperationResults(riWSDL.getOperationSpecification(service, port, operation), requestMsg, fakeMsg);

        if (ccc.isRequestPresent(listenerOpId)&&(fakeMsg==null)) {
            throw new Exception("Internal C2CRI Error:  The publication request was not processed by the HTTP Handler within the configured " +responseTimeOut+" ms.");
        }

        NTCIP2306ControllerResults results = processSOAPPUBOCOperation(opResults, responseTimeOut);
        try {
            OperationIdentifier subOpId = DefaultSubPubMapper.getRelatedSubscriptionOperationId(operation);
            results.setSubscriptionActive(ccc.isSubscriptionActive(subOpId));
        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("Subscription Error: "+ex.getMessage());                                
        }
        results.setPublicationComplete(!ccc.isPublicationActive(opId));
        results.setPublicationCount(ccc.getPublicationCount(opId));
        results.setMillisSinceLastPeriodicPublication(ccc.getMillisSinceLastPeriodicPublication(opId));
        return results;
    }

    /**
     * Perform the Request portion of a SOAP Publication operation as an EC.
     *
     * @param service - the related service from the WSDL
     * @param port - the related port from the WSDL
     * @param operation - the related operation from the WSDL
     * @return - the results of the operation.
     * @throws Exception the exception
     */
    public NTCIP2306ControllerResults performSOAPPUBECRequest(String service, String port, String operation) throws Exception {

        OperationIdentifier opId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.HANDLER);

        ccc.registerForOperation(opId);

        Integer requestTimeOut = 15000;
        try {
            requestTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        NTCIP2306Message requestMsg = ccc.getRequest(opId, requestTimeOut);
        NTCIP2306ControllerResults results = processSOAPPUBECRequestOperation(riWSDL.getOperationSpecification(service, port, operation), requestMsg, requestTimeOut);
        results.setPublicationComplete(!ccc.isPublicationActive(opId));
        results.setPublicationCount(ccc.getPublicationCount(opId));
        results.setMillisSinceLastPeriodicPublication(ccc.getMillisSinceLastPeriodicPublication(opId));        
        results.setSubscriptionPeriodicFrequency(ccc.getPublicationFrequency(opId));

        return results;
    }

    /**
     * Perform the response portion of a SOAP Publication operation as an EC.
     *
     * @param service - the related service from the WSDL
     * @param port - the related port from the WSDL
     * @param operation - the related operation from the WSDL
     * @param responseMessage - the response message to be sent to the SUT.
     * @param skipEncoding - flag indicating whether to encode the response
     * Message.
     * @param isErrorResponse - flag indicating whether the responseMessage
     * reports an error.
     * @return the results of the operation.
     * @throws Exception the exception
     */
    public NTCIP2306ControllerResults performSOAPPUBECResponse(String service, String port, String operation,
            String responseMessage, boolean skipEncoding, boolean isErrorResponse) throws Exception {

        OperationIdentifier opId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.HANDLER);
        OperationIdentifier responseOpId = new OperationIdentifier(service, port, operation, OperationIdentifier.SOURCETYPE.LISTENER);
        OperationResults opResults = null;

        NTCIP2306Message responseMsg = new NTCIP2306Message(responseMessage.getBytes(), skipEncoding);
        responseMsg.setSkipEncoding(skipEncoding);
        if (isErrorResponse) {
            responseMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.ERROR);
        }

        Integer responseTimeOut = 15000;
        try {
            responseTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_RECEIVE_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ccc.sendResponse(responseOpId, responseMsg);
        opResults = ccc.getOperationResults(responseOpId, responseTimeOut);
        ccc.unregisterForOperation(opId);

        if (ccc.isResponsePresent(responseOpId)) {
            throw new Exception("Internal C2CRI Error:  The response was not processed by the HTTP Handler.");
        }

        NTCIP2306ControllerResults results = processSOAPPUBECResponseOperation(opResults, responseTimeOut);
        try {
            OperationIdentifier subOpId = DefaultSubPubMapper.getRelatedSubscriptionOperationId(operation);
            results.setSubscriptionActive(ccc.isSubscriptionActive(subOpId));
        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("Publication Error: "+ex.getMessage());                                
        }
        results.setPublicationComplete(!ccc.isPublicationActive(opId));
        results.setPublicationCount(ccc.getPublicationCount(opId));
        results.setMillisSinceLastPeriodicPublication(ccc.getMillisSinceLastPeriodicPublication(opId));
        return results;
    }

    /**
     * Process the operation results and include them in an
     * NTCIP2306ControllerResults.
     *
     * @param opResults - the results from an NTCIP 2306 operation
     * @param controllerResults - the original NTCIP2306ControllerResult
     * @param targetMessageType - designates whether a Request, Response or
     * Error message type is to be processed.
     * @return the updated controller results.
     */
    private NTCIP2306ControllerResults processOpResults(OperationResults opResults, NTCIP2306ControllerResults controllerResults, NTCIP2306Message.MESSAGETYPE targetMessageType) {
        NTCIP2306Message targetMessage = null;
        if (targetMessageType.equals(NTCIP2306Message.MESSAGETYPE.REQUEST)) {
            targetMessage = opResults.getRequestMessage();
        } else if (targetMessageType.equals(NTCIP2306Message.MESSAGETYPE.RESPONSE)) {
            targetMessage = opResults.getResponseMessage();
        } else if (targetMessageType.equals(NTCIP2306Message.MESSAGETYPE.ERROR)) {
            targetMessage = opResults.getResponseMessage();
        } else {
            targetMessage = opResults.getResponseMessage();
        }

        controllerResults = processMessageResults(opResults.getOperationSpecification(), controllerResults, targetMessage, targetMessageType);
        controllerResults.setRequestMessage(opResults.getRequestMessage());
        controllerResults.setResponseMessage(opResults.getResponseMessage());

        return controllerResults;
    }

    /**
     * Incorporate the results of message diagnostics into the controller
     * results.
     *
     * @param opSpec - the specification that defines the operation
     * @param controllerResults - the controller results that are to be updated
     * @param targetMessage - the message that is to be evaluated.
     * @param targetMessageType - indicates whether the target message is a
     * Request, Response or Error message.
     * @return the updated controller results.
     */
    public static NTCIP2306ControllerResults processMessageResults(OperationSpecification opSpec, NTCIP2306ControllerResults controllerResults, NTCIP2306Message targetMessage, NTCIP2306Message.MESSAGETYPE targetMessageType) {

        if (targetMessage != null) {
            boolean isGetRequest = false;
            String operationEncoding = "";
            if (targetMessageType.equals(NTCIP2306Message.MESSAGETYPE.REQUEST)) {
                operationEncoding = opSpec.getOperationInputEncoding();
                if (opSpec.isGetOperation()) {
                    isGetRequest = true;
                }
            } else if (targetMessageType.equals(NTCIP2306Message.MESSAGETYPE.RESPONSE)) {
                operationEncoding = opSpec.getOperationOutputEncoding();
            } else if (targetMessageType.equals(NTCIP2306Message.MESSAGETYPE.ERROR)) {
                operationEncoding = opSpec.getOperationFaultEncoding();
            } else {  // Not really sure what to do about this?
                operationEncoding = opSpec.getOperationOutputEncoding();
            }

            // Check out the Encoding Results.  Skip a Get Request, but otherwise start with GZIP First
            if (isGetRequest) {
                controllerResults.setEncodingError(false);
                controllerResults.setEncodingErrorDescription("");
                controllerResults.setEncodingErrorType(NTCIP2306Status.ENCODINGERRORTYPES.NONE.toString());
            } else if (operationEncoding.equals(OperationSpecification.GZIP_ENCODING)) {
                controllerResults.setEncodingError(!targetMessage.getGzipStatus().isNTCIP2306ValidGZIP());
                controllerResults.setEncodingErrorDescription(targetMessage.getGzipStatus().getGZIPErrors());
                controllerResults.setEncodingErrorType(targetMessage.getGzipStatus().isNTCIP2306ValidGZIP()
                        ? NTCIP2306Status.ENCODINGERRORTYPES.NONE.toString() : NTCIP2306Status.ENCODINGERRORTYPES.GZIPERROR.toString());
            } else if (operationEncoding.equals(OperationSpecification.SOAP_ENCODING)) {
                controllerResults.setEncodingError(!targetMessage.getSoapStatus().isNTCIP2306ValidSOAP());
                controllerResults.setEncodingErrorDescription(targetMessage.getSoapStatus().getSOAPErrors());
                controllerResults.setEncodingErrorType(targetMessage.getSoapStatus().isNTCIP2306ValidSOAP()
                        ? NTCIP2306Status.ENCODINGERRORTYPES.NONE.toString() : NTCIP2306Status.ENCODINGERRORTYPES.SOAPERROR.toString());
            } else {
                controllerResults.setEncodingError(!targetMessage.getXmlStatus().isNTCIP2306ValidXML());
                controllerResults.setEncodingErrorDescription(targetMessage.getXmlStatus().getXMLErrors());
                controllerResults.setEncodingErrorType(targetMessage.getXmlStatus().isNTCIP2306ValidXML()
                        ? NTCIP2306Status.ENCODINGERRORTYPES.NONE.toString() : NTCIP2306Status.ENCODINGERRORTYPES.XMLERROR.toString());
            }

            //Check out the Message Results
            try {
                targetMessage.verifyMessageToOpSpec(opSpec, targetMessageType);
                controllerResults.setMessageError(false);
                controllerResults.setOperationError(false);
                controllerResults.setMessageValidationError(false);
                controllerResults.setMessageErrorType(NTCIP2306Status.MESSAGEERRORTYPES.NONE.toString());
            } catch (NTCIP2306MessageException mex) {
                controllerResults.setMessageError(true);
                controllerResults.setMessageErrorDescription(mex.getMessage());
                controllerResults.setMessageErrorType(NTCIP2306Status.MESSAGEERRORTYPES.INVALIDMESSAGE.toString());
            } catch (NTCIP2306OperationException opEx) {
                controllerResults.setMessageError(false);
                controllerResults.setOperationError(true);
                controllerResults.setOperationErrorDescription(opEx.getMessage());
                // Moved behind the operation exception since not all NTCIP 2306 subprofiles
                // require that the message be validated against a schema
            } catch (NTCIP2306MessageValidationException mvex) {
                controllerResults.setMessageError(false);
                controllerResults.setOperationError(false);
                controllerResults.setMessageValidationError(true);
                controllerResults.setMessageValidationErrorDescription(mvex.getMessage());           //Ignore the other exception types that might be generated.
                controllerResults.setMessageValidationErrorType(NTCIP2306Status.ENCODINGERRORTYPES.SCHEMAVALIDATIONERROR.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (targetMessage.getMessageType().equals(NTCIP2306Message.MESSAGETYPE.REQUEST)) {
                controllerResults.setRequestMessage(targetMessage);
            } else {
                controllerResults.setResponseMessage(targetMessage);
            }
        }
        return controllerResults;
    }

    /**
     * Process the message of an FTP Get operation as an EC.
     *
     * @param opResults - the results from the FTP Get operation
     * @param responseTimeOut - the response timeout value setting.
     * @return the results of the processing.
     */
    private NTCIP2306ControllerResults processFTPGetECMsg(OperationResults opResults, Integer responseTimeOut) {
        NTCIP2306ControllerResults controllerResults = null;

        if (opResults == null) {
            controllerResults = new NTCIP2306ControllerResults();
            controllerResults.setTransportError(true);
            controllerResults.setTransportErrorDescription("No FTP Get Response was received before the " + responseTimeOut + " ms timeout.");
            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETCONNECTERROR.toString());
            return controllerResults;
        } else {
            controllerResults = new NTCIP2306ControllerResults(opResults.getOperationSpecification().getOperationName());
            if (opResults.getResponseMessage() == null) {
                controllerResults.setTransportError(true);
                controllerResults.setTransportErrorDescription("No FTP Get Response was received before the " + responseTimeOut + " ms timeout.");
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETCONNECTERROR.toString());
            } else {
                // Check out the Transport Results
                if (opResults.getResponseMessage().getFtpStatus().getStatusCode() == 226) {
                    controllerResults.setTransportError(false);
                    controllerResults.setTransportErrorDescription("226");
                    controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
                } else {
                    controllerResults.setTransportError(true);
                    controllerResults.setTransportErrorDescription(String.valueOf(opResults.getResponseMessage().getFtpStatus().getStatusCode()));
                    controllerResults.setTransportErrorType(String.valueOf(opResults.getResponseMessage().getFtpStatus().getStatusCode()));
                }

            }
        }
        return processOpResults(opResults, controllerResults, NTCIP2306Message.MESSAGETYPE.RESPONSE);
    }

    /**
     * Process the message of an FTP Get operation as an OC.
     *
     * @param opResults - the results from the FTP Get operation
     * @param requestTimeOut the request time out
     * @return the results of the processing.
     */
    private NTCIP2306ControllerResults processFTPGetOCOperation(OperationResults opResults, Integer requestTimeOut) {
        NTCIP2306ControllerResults controllerResults = null;

        if (opResults == null) {
            controllerResults = new NTCIP2306ControllerResults();
            controllerResults.setTransportError(true);
            controllerResults.setTransportErrorDescription("No FTP Get Request was received before the " + requestTimeOut + " ms timeout.");
            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETRECEIVEERROR.toString());
            return controllerResults;
        } else {
            controllerResults = new NTCIP2306ControllerResults(opResults.getOperationSpecification().getOperationName());
            if (opResults.getResponseMessage() == null) {
                controllerResults.setTransportError(true);
                controllerResults.setTransportErrorDescription("No FTP Get Request was received before the " + requestTimeOut + " ms timeout.");
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETRECEIVEERROR.toString());
            } else {
                // Check out the Transport Results
                if (opResults.getResponseMessage().getFtpStatus().getStatusCode() == 226) {
                    controllerResults.setTransportError(false);
                    controllerResults.setTransportErrorDescription("226");
                    controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
                } else {
                    controllerResults.setTransportError(true);
                    controllerResults.setTransportErrorDescription(String.valueOf(opResults.getResponseMessage().getFtpStatus().getStatusCode()));
                    controllerResults.setTransportErrorType(String.valueOf(opResults.getResponseMessage().getFtpStatus().getStatusCode()));
                }

            }

        }

        return processOpResults(opResults, controllerResults, NTCIP2306Message.MESSAGETYPE.REQUEST);
    }

    /**
     * Process the message of an HTTP Get operation as an EC.
     *
     * @param opResults - the results from the HTTP Get operation
     * @param responseTimeOut - the response timeout value setting.
     * @return the results of the processing.
     */
    private NTCIP2306ControllerResults processHTTPGetECOperation(OperationResults opResults, Integer responseTimeOut) {
        NTCIP2306ControllerResults controllerResults = null;

        if (opResults == null) {
            controllerResults = new NTCIP2306ControllerResults();
            controllerResults.setTransportError(true);
            controllerResults.setTransportErrorDescription("No HTTP Get Response was received before the " + responseTimeOut + " ms timeout.");
            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETCONNECTERROR.toString());
            return controllerResults;
        } else {
            controllerResults = new NTCIP2306ControllerResults(opResults.getOperationSpecification().getOperationName());
            if (opResults.getResponseMessage() == null) {
                controllerResults.setTransportError(true);
                controllerResults.setTransportErrorDescription("No HTTP Get Response was received before the " + responseTimeOut + " ms timeout.");
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETCONNECTERROR.toString());
            } else {
                // Check out the Transport Results
                if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 200) {
                    controllerResults.setTransportError(false);
                    controllerResults.setTransportErrorDescription("200");
                    controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
                } else {
                    controllerResults.setTransportError(true);
                    controllerResults.setTransportErrorDescription(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                    if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 500) {
                        controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.INTERNALSERVERERROR.toString());
                    } else {
                        controllerResults.setTransportErrorType(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                    }
                }
            }
        }
        return processOpResults(opResults, controllerResults, NTCIP2306Message.MESSAGETYPE.RESPONSE);
    }

    /**
     * Process the message of an HTTP Get operation as an OC.
     *
     * @param opResults - the results from the HTTP Get operation
     * @param requestTimeOut the request time out
     * @return the results of the processing.
     */
    private NTCIP2306ControllerResults processHTTPGetOCOperation(OperationResults opResults, Integer requestTimeOut) {
        NTCIP2306ControllerResults controllerResults = null;

        if (opResults == null) {
            controllerResults = new NTCIP2306ControllerResults();
            controllerResults.setTransportError(true);
            controllerResults.setTransportErrorDescription("No HTTP Get Request was received before the " + requestTimeOut + " ms timeout.");
            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETRECEIVEERROR.toString());
            return controllerResults;
        } else {
            controllerResults = new NTCIP2306ControllerResults(opResults.getOperationSpecification().getOperationName());
            if (opResults.getResponseMessage() == null) {
                controllerResults.setTransportError(true);
                controllerResults.setTransportErrorDescription("No HTTP Get Request was received before the " + requestTimeOut + " ms timeout.");
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETRECEIVEERROR.toString());
            } else {
                // Check out the Transport Results
                if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 200) {
                    controllerResults.setTransportError(false);
                    controllerResults.setTransportErrorDescription("200");
                    controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
                } else {
                    controllerResults.setTransportError(true);
                    controllerResults.setTransportErrorDescription(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                    controllerResults.setTransportErrorType(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                }

            }

        }

        return processOpResults(opResults, controllerResults, NTCIP2306Message.MESSAGETYPE.REQUEST);
    }

    /**
     * Process the message of an HTTP Post operation as an EC.
     *
     * @param opResults - the results from the HTTP Post operation
     * @param responseTimeOut - the response timeout value setting.
     * @return the results of the processing.
     */
    private NTCIP2306ControllerResults processHTTPPostECOperation(OperationResults opResults, Integer responseTimeOut) {
        NTCIP2306ControllerResults controllerResults = null;

        if (opResults == null) {
            controllerResults = new NTCIP2306ControllerResults();
            controllerResults.setTransportError(true);
            controllerResults.setTransportErrorDescription("No HTTP Post Response was received before the " + responseTimeOut + " ms timeout.");
            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETCONNECTERROR.toString());
            return controllerResults;
        } else {
            controllerResults = new NTCIP2306ControllerResults(opResults.getOperationSpecification().getOperationName());
            if (opResults.getResponseMessage() == null) {
                controllerResults.setTransportError(true);
                controllerResults.setTransportErrorDescription("No HTTP Post Response was received before the " + responseTimeOut + " ms timeout.");
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETCONNECTERROR.toString());
            } else {
                // Check out the Transport Results
                if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 200) {
                    controllerResults.setTransportError(false);
                    controllerResults.setTransportErrorDescription("200");
                    controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
                } else {
                    controllerResults.setTransportError(true);
                    controllerResults.setTransportErrorDescription(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                    controllerResults.setTransportErrorType(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                }

            }
        }
        return processOpResults(opResults, controllerResults, NTCIP2306Message.MESSAGETYPE.RESPONSE);
    }

    /**
     * Process the request message of an HTTP Get operation as an OC.
     *
     * @param opSpec - the specification for the operation.
     * @param requestMsg - the request message being processed.
     * @param requestTimeOut - the request timeout value setting.
     * @return the results of the processing.
     */
    private NTCIP2306ControllerResults processHTTPPostOCRequestOperation(OperationSpecification opSpec, NTCIP2306Message requestMsg, Integer requestTimeOut) {
        NTCIP2306ControllerResults controllerResults = new NTCIP2306ControllerResults(opSpec.getOperationName());

        if (requestMsg == null) {
            controllerResults.setTransportError(true);
            controllerResults.setTransportErrorDescription("No HTTP Post Request was received before the " + requestTimeOut + " ms timeout.");
            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETRECEIVEERROR.toString());
            return controllerResults;
        } else {
            // Check out the Transport Results
            if (!requestMsg.isTransportErrorEncountered()) {
                controllerResults.setTransportError(false);
                controllerResults.setTransportErrorDescription("");
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
            } else {
                controllerResults.setTransportError(true);
                controllerResults.setTransportErrorDescription(requestMsg.getTransportErrorDescription());
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETRECEIVEERROR.toString());
            }

        }

        return processMessageResults(opSpec, controllerResults, requestMsg, NTCIP2306Message.MESSAGETYPE.REQUEST);
    }

    /**
     * Process the results of an HTTP Post operation as an OC.
     *
     * @param opResults - the results from the HTTP Post operation
     * @param responseTimeOut - the response timeout value setting.
     * @return the results of the processing.
     */
    private NTCIP2306ControllerResults processHTTPPostOCResponseOperation(OperationResults opResults, Integer responseTimeOut) {
        NTCIP2306ControllerResults controllerResults = null;

        if (opResults == null) {
            controllerResults = new NTCIP2306ControllerResults();
            controllerResults.setTransportError(true);
            controllerResults.setTransportErrorDescription("No HTTP Post Response was sent before the " + responseTimeOut + " ms timeout.");
            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.DATATRANSFERERROR.toString());
            return controllerResults;
        } else {
            controllerResults = new NTCIP2306ControllerResults(opResults.getOperationSpecification().getOperationName());
            if (opResults.getResponseMessage() == null) {
                controllerResults.setTransportError(true);
                controllerResults.setTransportErrorDescription("No HTTP Post Response was sent before the " + responseTimeOut + " ms timeout.");
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.DATATRANSFERERROR.toString());
            } else {
                // Check out the Transport Results
                if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 200) {
                    controllerResults.setTransportError(false);
                    controllerResults.setTransportErrorDescription("200");
                    controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
                } else {
                    controllerResults.setTransportError(true);
                    controllerResults.setTransportErrorDescription(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                    controllerResults.setTransportErrorType(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                }

            }

        }

        return processOpResults(opResults, controllerResults, NTCIP2306Message.MESSAGETYPE.RESPONSE);
    }

    /**
     * Process the operation results of an SOAP Request Response operation as an
     * EC.
     *
     * @param opResults - the results from the SOAP Request Response operation
     * @param responseTimeOut - the response timeout value setting.
     * @return the results of the processing.
     */
    private NTCIP2306ControllerResults processSOAPRRECOperation(OperationResults opResults, Integer responseTimeOut) {
        NTCIP2306ControllerResults controllerResults = null;

        if (opResults == null) {
            controllerResults = new NTCIP2306ControllerResults();
            controllerResults.setTransportError(true);
            controllerResults.setTransportErrorDescription("No SOAP RR Response was received before the " + responseTimeOut + " ms timeout.");
            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETCONNECTERROR.toString());
            return controllerResults;
        } else {
            controllerResults = new NTCIP2306ControllerResults(opResults.getOperationSpecification().getOperationName());
            if (opResults.getResponseMessage() == null) {
                controllerResults.setTransportError(true);
                controllerResults.setTransportErrorDescription("No SOAP RR Post Response was received before the " + responseTimeOut + " ms timeout.");
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETCONNECTERROR.toString());
            } else {
                // Check out the Transport Results
                if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 200) {
                    controllerResults.setTransportError(false);
                    controllerResults.setTransportErrorDescription("200");
                    controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
                } else {
                    controllerResults.setTransportError(true);
                    if (opResults.getResponseMessage().getHttpStatus().getStatusCode() > 0) {
                        controllerResults.setTransportErrorDescription(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                        if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 500) {
                            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.INTERNALSERVERERROR.toString());
                        } else {
                            controllerResults.setTransportErrorType(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                        }
                    } else {
                        controllerResults.setTransportErrorDescription(opResults.getResponseMessage().getTransportErrorDescription());
                        controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETCONNECTERROR.toString());
                    }
                }

            }
        }
        return processOpResults(opResults, controllerResults, NTCIP2306Message.MESSAGETYPE.RESPONSE);
    }

    /**
     * Process the request message of a SOAP Request Response operation as an
     * OC.
     *
     * @param opSpec - the specification for the operation.
     * @param requestMsg - the request message being processed.
     * @param requestTimeOut - the request timeout value setting.
     * @return the results of the processing.
     */
    private NTCIP2306ControllerResults processSOAPRROCRequestOperation(OperationSpecification opSpec, NTCIP2306Message requestMsg, Integer requestTimeOut) {
        NTCIP2306ControllerResults controllerResults = new NTCIP2306ControllerResults(opSpec.getOperationName());

        if (requestMsg == null) {
            controllerResults.setTransportError(true);
            controllerResults.setTransportErrorDescription("No SOAP RR Request was received before the " + requestTimeOut + " ms timeout.");
            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETRECEIVEERROR.toString());
            return controllerResults;
        } else {
            // Check out the Transport Results
            if (!requestMsg.isTransportErrorEncountered()) {
                controllerResults.setTransportError(false);
                controllerResults.setTransportErrorDescription("");
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
            } else {
                controllerResults.setTransportError(true);
                controllerResults.setTransportErrorDescription(requestMsg.getTransportErrorDescription());
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.INTERNALSERVERERROR.toString());
            }

        }

        return processMessageResults(opSpec, controllerResults, requestMsg, NTCIP2306Message.MESSAGETYPE.REQUEST);
    }

    /**
     * Process the operation results of the response portion of an SOAP Request
     * Response operation as an OC.
     *
     * @param opResults - the results from the SOAP Request Response operation
     * @param responseTimeOut - the response timeout value setting.
     * @return the results of the processing.
     */
    private NTCIP2306ControllerResults processSOAPRROCResponseOperation(OperationResults opResults, Integer responseTimeOut) {
        NTCIP2306ControllerResults controllerResults = null;

        if (opResults == null) {
            controllerResults = new NTCIP2306ControllerResults();
            controllerResults.setTransportError(true);
            controllerResults.setTransportErrorDescription("No SOAP RR Response was sent before the " + responseTimeOut + " ms timeout.");
            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.INTERNALSERVERERROR.toString());
            return controllerResults;
        } else {
            controllerResults = new NTCIP2306ControllerResults(opResults.getOperationSpecification().getOperationName());
            if (opResults.getResponseMessage() == null) {
                controllerResults.setTransportError(true);
                controllerResults.setTransportErrorDescription("No HTTP Post Response was sent before the " + responseTimeOut + " ms timeout.");
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.INTERNALSERVERERROR.toString());
            } else {
                // Check out the Transport Results
                if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 200) {
                    controllerResults.setTransportError(false);
                    controllerResults.setTransportErrorDescription("200");
                    controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
                } else {
                    controllerResults.setTransportError(true);
                    controllerResults.setTransportErrorDescription(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                    if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 500) {
                        controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.INTERNALSERVERERROR.toString());
                    } else {
                        controllerResults.setTransportErrorType(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                    }
                }

            }

        }

        return processOpResults(opResults, controllerResults, NTCIP2306Message.MESSAGETYPE.RESPONSE);
    }

    /**
     * Process the operation results of a SOAP Subscription operation as an EC.
     *
     * @param opResults - the results from the SOAP Subscription operation
     * @param responseTimeOut - the response timeout value setting.
     * @return the results of the processing.
     */
    private NTCIP2306ControllerResults processSOAPSUBECOperation(OperationResults opResults, Integer responseTimeOut) {
        NTCIP2306ControllerResults controllerResults = null;

        if (opResults == null) {
            controllerResults = new NTCIP2306ControllerResults();
            controllerResults.setTransportError(true);
            controllerResults.setTransportErrorDescription("No SOAP SUB Response was received before the " + responseTimeOut + " ms timeout.");
            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETCONNECTERROR.toString());
            return controllerResults;
        } else {
            controllerResults = new NTCIP2306ControllerResults(opResults.getOperationSpecification().getOperationName());
            if (opResults.getResponseMessage() == null) {
                controllerResults.setTransportError(true);
                controllerResults.setTransportErrorDescription("No SOAP SUB Post Response was received before the " + responseTimeOut + " ms timeout.");
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETCONNECTERROR.toString());
            } else {
                // Check out the Transport Results
                if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 200) {
                    controllerResults.setTransportError(false);
                    controllerResults.setTransportErrorDescription("200");
                    controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
                } else {
                    controllerResults.setTransportError(true);
                    controllerResults.setTransportErrorDescription(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                    if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 500) {
                        controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.INTERNALSERVERERROR.toString());
                    } else {
                        controllerResults.setTransportErrorType(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                    }
                }

            }
        }
        return processOpResults(opResults, controllerResults, NTCIP2306Message.MESSAGETYPE.RESPONSE);
    }

    /**
     * Process the request message of an SOAP Subscription operation as an OC.
     *
     * @param opSpec - the specification for the operation.
     * @param requestMsg - the request message being processed.
     * @param requestTimeOut - the request timeout value setting.
     * @return the results of the processing.
     */
    private NTCIP2306ControllerResults processSOAPSUBOCRequestOperation(OperationSpecification opSpec, NTCIP2306Message requestMsg, Integer requestTimeOut) {
        NTCIP2306ControllerResults controllerResults = new NTCIP2306ControllerResults(opSpec.getOperationName());

        if (requestMsg == null) {
            controllerResults.setTransportError(true);
            controllerResults.setTransportErrorDescription("No SOAP SUB Request was received before the " + requestTimeOut + " ms timeout.");
            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETRECEIVEERROR.toString());
            return controllerResults;
        } else {
            // Check out the Transport Results
            if (!requestMsg.isTransportErrorEncountered()) {
                controllerResults.setTransportError(false);
                controllerResults.setTransportErrorDescription("");
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
            } else {
                controllerResults.setTransportError(true);
                controllerResults.setTransportErrorDescription(requestMsg.getTransportErrorDescription());
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETCONNECTERROR.toString());
            }

        }

        return processMessageResults(opSpec, controllerResults, requestMsg, NTCIP2306Message.MESSAGETYPE.REQUEST);
    }

    /**
     * Process the operation results of the response portion of a SOAP
     * Subscription operation as an OC.
     *
     * @param opResults - the results from the SOAP Subscription operation
     * @param responseTimeOut - the response timeout value setting.
     * @return the results of the processing.
     */
    private NTCIP2306ControllerResults processSOAPSUBOCResponseOperation(OperationResults opResults, Integer responseTimeOut) {
        NTCIP2306ControllerResults controllerResults = null;

        if (opResults == null) {
            controllerResults = new NTCIP2306ControllerResults();
            controllerResults.setTransportError(true);
            controllerResults.setTransportErrorDescription("No SOAP SUB Response was sent before the " + responseTimeOut + " ms timeout.");
            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.INTERNALSERVERERROR.toString());
            return controllerResults;
        } else {
            controllerResults = new NTCIP2306ControllerResults(opResults.getOperationSpecification().getOperationName());
            if (opResults.getResponseMessage() == null) {
                controllerResults.setTransportError(true);
                controllerResults.setTransportErrorDescription("No HTTP SOAP SUB Response was sent before the " + responseTimeOut + " ms timeout.");
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.INTERNALSERVERERROR.toString());
            } else {
                // Check out the Transport Results
                if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 200) {
                    controllerResults.setTransportError(false);
                    controllerResults.setTransportErrorDescription("200");
                    controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
                } else {
                    controllerResults.setTransportError(true);
                    controllerResults.setTransportErrorDescription(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                    if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 500) {
                        controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.INTERNALSERVERERROR.toString());
                    } else {
                        controllerResults.setTransportErrorType(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                    }
                }

            }

        }

        return processOpResults(opResults, controllerResults, NTCIP2306Message.MESSAGETYPE.RESPONSE);
    }

    /**
     * Process the operation results of the response portion of a SOAP
     * Publication operation as an OC.
     *
     * @param opResults - the results from the SOAP Publication operation
     * @param responseTimeOut - the response timeout value setting.
     * @return the results of the processing.
     */
    private NTCIP2306ControllerResults processSOAPPUBOCOperation(OperationResults opResults, Integer responseTimeOut) {
        NTCIP2306ControllerResults controllerResults = null;

        if (opResults == null) {
            controllerResults = new NTCIP2306ControllerResults();
            controllerResults.setTransportError(true);
            controllerResults.setTransportErrorDescription("No SOAP PUB Response was received before the " + responseTimeOut + " ms timeout.");
            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETCONNECTERROR.toString());
            return controllerResults;
        } else {
            controllerResults = new NTCIP2306ControllerResults(opResults.getOperationSpecification().getOperationName());
            if (opResults.getResponseMessage() == null) {
                controllerResults.setTransportError(true);
                controllerResults.setTransportErrorDescription("No SOAP PUB Response was received before the " + responseTimeOut + " ms timeout.");
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETCONNECTERROR.toString());
            } else {
                // Check out the Transport Results
                if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 200) {
                    controllerResults.setTransportError(false);
                    controllerResults.setTransportErrorDescription("200");
                    controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
                } else {
                    controllerResults.setTransportError(true);
                    controllerResults.setTransportErrorDescription(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                    if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 500) {
                        controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.INTERNALSERVERERROR.toString());
                    } else {
                        controllerResults.setTransportErrorType(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                    }
                }

            }
        }
        return processOpResults(opResults, controllerResults, NTCIP2306Message.MESSAGETYPE.RESPONSE);
    }

    /**
     * Process the request message of a SOAP Publication operation as an EC.
     *
     * @param opSpec - the specification for the operation.
     * @param requestMsg - the request message being processed.
     * @param requestTimeOut - the request timeout value setting.
     * @return the results of the processing.
     */
    private NTCIP2306ControllerResults processSOAPPUBECRequestOperation(OperationSpecification opSpec, NTCIP2306Message requestMsg, Integer requestTimeOut) {
        NTCIP2306ControllerResults controllerResults = new NTCIP2306ControllerResults(opSpec.getOperationName());

        if (requestMsg == null) {
            controllerResults.setTransportError(true);
            controllerResults.setTransportErrorDescription("No SOAP PUB Request was received before the " + requestTimeOut + " ms timeout.");
            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETRECEIVEERROR.toString());
            return controllerResults;
        } else {
            // Check out the Transport Results
            if (!requestMsg.isTransportErrorEncountered()) {
                controllerResults.setTransportError(false);
                controllerResults.setTransportErrorDescription("");
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
            } else {
                controllerResults.setTransportError(true);
                controllerResults.setTransportErrorDescription(requestMsg.getTransportErrorDescription());
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETRECEIVEERROR.toString());
            }

        }

        return processMessageResults(opSpec, controllerResults, requestMsg, NTCIP2306Message.MESSAGETYPE.REQUEST);
    }

    /**
     * Process the operation results of the response portion of a SOAP
     * Publication operation as an EC.
     *
     * @param opResults - the results from the SOAP Publication operation
     * @param responseTimeOut - the response timeout value setting.
     * @return the results of the processing.
     */
    private NTCIP2306ControllerResults processSOAPPUBECResponseOperation(OperationResults opResults, Integer responseTimeOut) {
        NTCIP2306ControllerResults controllerResults;

        if (opResults == null) {
            controllerResults = new NTCIP2306ControllerResults();
            controllerResults.setTransportError(true);
            controllerResults.setTransportErrorDescription("No SOAP PUB Response was sent before the " + responseTimeOut + " ms timeout.");
            controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETCONNECTERROR.toString());
            return controllerResults;
        } else {
            controllerResults = new NTCIP2306ControllerResults(opResults.getOperationSpecification().getOperationName());
            if (opResults.getResponseMessage() == null) {
                controllerResults.setTransportError(true);
                controllerResults.setTransportErrorDescription("No SOAP PUB Response was sent before the " + responseTimeOut + " ms timeout.");
                controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETCONNECTERROR.toString());
            } else {
                // Check out the Transport Results
                if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 200) {
                    controllerResults.setTransportError(false);
                    controllerResults.setTransportErrorDescription("200");
                    controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
                } else {
                    controllerResults.setTransportError(true);
                    controllerResults.setTransportErrorDescription(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                    if (opResults.getResponseMessage().getHttpStatus().getStatusCode() == 500) {
                        controllerResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.INTERNALSERVERERROR.toString());
                    } else {
                        controllerResults.setTransportErrorType(String.valueOf(opResults.getResponseMessage().getHttpStatus().getStatusCode()));
                    }
                }

            }

        }

        return processOpResults(opResults, controllerResults, NTCIP2306Message.MESSAGETYPE.RESPONSE);
    }

    /**
     * Shutdown all of the NTCIP 2306 services defined by the WSDL.
     */
    public void shutdown() {
        ccc.stopServices();
    }

    /**
     * Gets the message subscription frequency.
     *
     * @param message the message
     * @return the message subscription frequency
     */
    private long getMessageSubscriptionFrequency(NTCIP2306Message message) {
        long frequency = -1;
        C2CMessageSubscription subscription = null;
        try {
            JAXBContext jc = JAXBContext.newInstance(C2CMessageSubscription.class.getPackage().getName());
            Unmarshaller um = jc.createUnmarshaller();
            System.err.println("NTCIP2306Controller::produceC2cMessageSubscription --- length = " + message.getMessagePartContent(1).length + "\n"
                    + new String(message.getMessagePartContent(1)) + "\n Bytes: \n" + message.getMessagePartContent(1));
            ByteArrayInputStream buffer = new ByteArrayInputStream(message.getMessagePartContent(1));
            subscription = (C2CMessageSubscription) ((javax.xml.bind.JAXBElement) um.unmarshal(buffer)).getValue();
            if (subscription.getSubscriptionType().getSubscriptionTypeItem().equalsIgnoreCase("PERIODIC")){
                frequency = subscription.getSubscriptionFrequency();                
            }
            buffer.close();

        } catch (IOException ioex) {
            ioex.printStackTrace();
        } catch (JAXBException jaxbex) {
            jaxbex.printStackTrace();
            System.err.println("Error Unmarshalling message: " + new String(message.getMessagePartContent(1)));
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return frequency;
    }
}
