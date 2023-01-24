/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd;

import org.fhwa.c2cri.applayer.ApplicationLayerOperationResults;
import org.fhwa.c2cri.applayer.InformationLayerAdapter;
import org.fhwa.c2cri.infolayer.InformationLayerController;
import org.fhwa.c2cri.infolayer.InformationLayerOperationResults;
import org.fhwa.c2cri.messagemanager.Message;

/**
 * The Class TMDDController wraps the InformationLayerAdapter provided by all Application Layer Standards,
 into an Information Layer Controller.
 *
 * @author TransCore ITS, LLC
 * Last Updated: 1/8/2014
 */
public class TMDDController
        implements InformationLayerController
{

    /**
     * The adapter.
     */
    private InformationLayerAdapter adapter;

    /**
     * Instantiates a new tMD dv303 controller.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param controller the controller
     */
    public TMDDController(InformationLayerAdapter controller)
    {
        this.adapter = controller;
    }

    /**
     * Perform get ec.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     *
     * @return the information layer operation results
     *
     * @throws Exception the exception
     */
    @Override
    public InformationLayerOperationResults performGetEC(String dialog) throws Exception
    {
        throw new UnsupportedOperationException("The GET dialog pattern is not supported by TMDD v3.03d."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Perform get oc.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog          the dialog
     * @param responseMessage the response message
     *
     * @return the information layer operation results
     *
     * @throws Exception the exception
     */
    @Override
    public InformationLayerOperationResults performGetOC(String dialog, Message responseMessage) throws Exception
    {
        throw new UnsupportedOperationException("The GET dialog pattern is not supported by TMDD v3.03d"); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Perform request response ec.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog         the dialog
     * @param requestMessage the request message
     *
     * @return the information layer operation results
     *
     * @throws Exception the exception
     */
    @Override
    public InformationLayerOperationResults performRequestResponseEC(String dialog, Message requestMessage) throws Exception
    {
        setDisableAppLayerEncoding(requestMessage.isSkipApplicationLayerEncoding());
        ApplicationLayerOperationResults results = adapter.performRequestResponseEC(dialog, requestMessage);
        if (results.isTransportErrorEncountered())
            if (!results.getTransportErrorDescription().equals("406")&&(!results.getTransportErrorDescription().equals("500")&&(results.getResponseMessage()!=null) && (!results.getResponseMessage().getEncodedMessageType().contains(("errorReportMsg")))))
                throw new Exception("Application Layer Error: " + results.getTransportErrorDescription());
        return processResults(dialog, results);
    }

    /**
     * Perform request response oc receive.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     *
     * @return the information layer operation results
     *
     * @throws Exception the exception
     */
    @Override
    public InformationLayerOperationResults performRequestResponseOCReceive(String dialog) throws Exception
    {
        ApplicationLayerOperationResults results = adapter.performRequestResponseOCReceive(dialog);
        if (results.isTransportErrorEncountered())
            throw new Exception("Application Layer Error: " + results.getTransportErrorDescription());
        return processResults(dialog, results);
    }

    /**
     * Perform request response oc response.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog          the dialog
     * @param responseMessage the response message
     * @param isErrorResponse the is error response
     *
     * @return the information layer operation results
     *
     * @throws Exception the exception
     */
    @Override
    public InformationLayerOperationResults performRequestResponseOCResponse(String dialog, Message responseMessage, boolean isErrorResponse) throws Exception
    {
        setDisableAppLayerEncoding(responseMessage.isSkipApplicationLayerEncoding());
        ApplicationLayerOperationResults results = adapter.performRequestResponseOCResponse(dialog, responseMessage, isErrorResponse);
        if (results.isTransportErrorEncountered())
            if (!results.getTransportErrorDescription().equals("406")&&(!results.getTransportErrorDescription().equals("500")&&(results.getResponseMessage()!=null) && (!results.getResponseMessage().getEncodedMessageType().contains(("errorReportMsg")))))
                throw new Exception("Application Layer Error: " + results.getTransportErrorDescription());
        return processResults(dialog, results);
    }

    /**
     * Perform subscription ec.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog         the dialog
     * @param requestMessage the request message
     *
     * @return the information layer operation results
     *
     * @throws Exception the exception
     */
    @Override
    public InformationLayerOperationResults performSubscriptionEC(String dialog, Message requestMessage) throws Exception
    {
        setDisableAppLayerEncoding(requestMessage.isSkipApplicationLayerEncoding());
        ApplicationLayerOperationResults results = adapter.performSubscriptionEC(dialog, requestMessage);
        if (results.isTransportErrorEncountered())
            if (!results.getTransportErrorDescription().equals("406")&&(!results.getTransportErrorDescription().equals("500")&&(results.getResponseMessage()!=null) && (!results.getResponseMessage().getEncodedMessageType().contains(("errorReportMsg")))))
                throw new Exception("Application Layer Error: " + results.getTransportErrorDescription());
        return processResults(dialog, results);
    }

    /**
     * Perform subscription oc receive.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     *
     * @return the information layer operation results
     *
     * @throws Exception the exception
     */
    @Override
    public InformationLayerOperationResults performSubscriptionOCReceive(String dialog) throws Exception
    {
        ApplicationLayerOperationResults results = adapter.performSubscriptionOCReceive(dialog);
        if (results.isTransportErrorEncountered())
            throw new Exception("Application Layer Error: " + results.getTransportErrorDescription());
        return processResults(dialog, results);
    }

    /**
     * Perform subscription oc response.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog          the dialog
     * @param responseMessage the response message
     * @param isErrorResponse the is error response
     *
     * @return the information layer operation results
     *
     * @throws Exception the exception
     */
    @Override
    public InformationLayerOperationResults performSubscriptionOCResponse(String dialog, Message responseMessage, boolean isErrorResponse) throws Exception
    {
        setDisableAppLayerEncoding(responseMessage.isSkipApplicationLayerEncoding());
        ApplicationLayerOperationResults results = adapter.performSubscriptionOCResponse(dialog, responseMessage, isErrorResponse);
        if (results.isTransportErrorEncountered() && !isErrorResponse)
            if (!results.getTransportErrorDescription().equals("406")&&(!results.getTransportErrorDescription().equals("500")&&(results.getResponseMessage()!=null) && (!results.getResponseMessage().getEncodedMessageType().contains(("errorReportMsg")))))
                throw new Exception("Application Layer Error: " + results.getTransportErrorDescription());
        return processResults(dialog, results);
    }

    /**
     * Perform publication oc.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog         the dialog
     * @param requestMessage the request message
     *
     * @return the information layer operation results
     *
     * @throws Exception the exception
     */
    @Override
    public InformationLayerOperationResults performPublicationOC(String dialog, Message requestMessage) throws Exception
    {
        setDisableAppLayerEncoding(requestMessage.isSkipApplicationLayerEncoding());
        ApplicationLayerOperationResults results = adapter.performPublicationOC(dialog, requestMessage);
        if (results.isTransportErrorEncountered())
            if (!results.getTransportErrorDescription().equals("406")&&(!results.getTransportErrorDescription().equals("500")&&(results.getResponseMessage()!=null) && (!results.getResponseMessage().getEncodedMessageType().contains(("errorReportMsg")))))
                throw new Exception("Application Layer Error: " + results.getTransportErrorDescription());
        return processResults(dialog, results);
    }

    /**
     * Perform publication ec receive.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     *
     * @return the information layer operation results
     *
     * @throws Exception the exception
     */
    @Override
    public InformationLayerOperationResults performPublicationECReceive(String dialog) throws Exception
    {
        ApplicationLayerOperationResults results = adapter.performPublicationECReceive(dialog);
        if (results.isTransportErrorEncountered())
            throw new Exception("Application Layer Error: " + results.getTransportErrorDescription());
        return processResults(dialog, results);
    }

    /**
     * Perform publication ec response.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog          the dialog
     * @param responseMessage the response message
     * @param isErrorResponse the is error response
     *
     * @return the information layer operation results
     *
     * @throws Exception the exception
     */
    @Override
    public InformationLayerOperationResults performPublicationECResponse(String dialog, Message responseMessage, boolean isErrorResponse) throws Exception
    {
        setDisableAppLayerEncoding(responseMessage.isSkipApplicationLayerEncoding());
        ApplicationLayerOperationResults results = adapter.performPublicationECResponse(dialog, responseMessage, isErrorResponse);
        if (results.isTransportErrorEncountered())
            if (!results.getTransportErrorDescription().equals("406")&&(!results.getTransportErrorDescription().equals("500")&&(results.getResponseMessage()!=null) && (!results.getResponseMessage().getEncodedMessageType().contains(("errorReportMsg")))))
                throw new Exception("Application Layer Error: " + results.getTransportErrorDescription());
        return processResults(dialog, results);
    }

    /**
     * Sets the disable app layer encoding.
     *
     * @param disableAppLayerEncoding the new disable app layer encoding
     */
    @Override
    public void setDisableAppLayerEncoding(boolean disableAppLayerEncoding)
    {
        adapter.setDisableAppLayerEncoding(disableAppLayerEncoding);
    }

    /**
     * Shutdown.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    @Override
    public void shutdown()
    {
        adapter.shutdown();
    }

    /**
     * Process results.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog    the dialog
     * @param apResults the ap results
     *
     * @return the information layer operation results
     */
    private InformationLayerOperationResults processResults(String dialog, ApplicationLayerOperationResults apResults)
    {
        InformationLayerOperationResults opResults = new TMDDOperationResults(dialog, apResults);

//        if (apResults instanceof NTCIP2306ApplicationLayerOperationResults){
//        }
        // 1 Create Message Requirement Inspection results
        //      - need the trace between requirements and message elements
        //      - if no matching message exists in the standard pass this off to the extension?
        //      - if the selected needs are known then the trace message can be reduced
        //      -    to only the required elements
        // 2 Create dialog timing Inspection results
        //    - need the values specified for requirements?
        // 3 Verify that the dialog uses the messages specified by TMDD
        //     - need the input/output/error definitions for the dialog
        //     - if dialog not defined for the standard then verify that the messages associated with it don't match an existing dialog
        //     - if the dialog can't be matched to an existing one pass it off to an extension for checking
        // 4 Verify that the message is valid per the appropriate TMDD specification
        //     - need the design specification document(s) for the standard
        //     - perform validation
        //     - store any parsing/value/structure errors
        // 5 If there are any registered TMDD extensions give the operation results to them let the perform additional processing.
        //      - send initial conformance results to the TMDD extension.
        return opResults;
    }

}
