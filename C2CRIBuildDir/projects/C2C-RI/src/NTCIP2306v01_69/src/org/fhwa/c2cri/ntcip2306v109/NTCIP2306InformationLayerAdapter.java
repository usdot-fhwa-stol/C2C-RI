/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109;

import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.fhwa.c2cri.applayer.ApplicationLayerOperationResults;
import org.fhwa.c2cri.applayer.InformationLayerAdapter;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;

/**
* This class adapts the NTC IP 2306 application layer services for use  by
* information layer standards.
*
* @author TransCore ITS, LLC
*  11/15/2013
*
*/
public class NTCIP2306InformationLayerAdapter implements InformationLayerAdapter{

    /** reference to the NTCIP2306controller. */
    private NTCIP2306Controller controller;
    
    /** flag which indicates whether encoding should be disabled. */
    private boolean disableAppLayerEncoding = false;
    
    /** a collection of the information layer standard name spaces. */
    private static ConcurrentHashMap<String,String> infoLayerNameSpaceMap = new ConcurrentHashMap();
    
    /**
     * constructor.
     *
     * @param controller the controller
     */
    public NTCIP2306InformationLayerAdapter(NTCIP2306Controller controller){
        this.controller = controller;
    }    
    
    /**
     * perform a get operation as a EC.
     *
     * @param dialog the dialog
     * @return the operation results
     * @throws Exception the exception
     */
    @Override
    public ApplicationLayerOperationResults performGetEC(String dialog) throws Exception {
        ArrayList<OperationSpecification> opSpecs = controller.getRiWSDL().getOperationSpecification(dialog);
        // Select the first opSpec available
        OperationSpecification opSpec = opSpecs.get(0);
        
        NTCIP2306ControllerResults results=null;
        if (opSpec.getRelatedServiceTransport().equals(OperationSpecification.ServiceTransportType.FTP)){
            results = controller.performFTPGetEC(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog);            
        } else if (opSpec.getRelatedServiceTransport().equals(OperationSpecification.ServiceTransportType.HTTP) ||
                opSpec.getRelatedServiceTransport().equals(OperationSpecification.ServiceTransportType.HTTPS)){
            results = controller.performHTTPGetEC(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog);                        
        }
        
        System.out.println(results);
        return new NTCIP2306ApplicationLayerOperationResults(results);
    }

    /**
     * Perform a GET operation as an OC.
     *
     * @param dialog – the name of the dialogue
     * @param responseMessage – the response message to be sent
     * @return the operation results
     * @throws Exception the exception
     */
    @Override
    public ApplicationLayerOperationResults performGetOC(String dialog, Message responseMessage) throws Exception {
        setDisableAppLayerEncoding(responseMessage.isSkipApplicationLayerEncoding()||disableAppLayerEncoding);
        C2CRIMessageAdapter.updateMessageFromSpec(responseMessage, getInfoLayerNameSpaceMap());
        ArrayList<OperationSpecification> opSpecs = controller.getRiWSDL().getOperationSpecification(dialog);
        // Select the first opSpec available
        OperationSpecification opSpec = opSpecs.get(0);        
        
        NTCIP2306ControllerResults results=null;
        if (opSpec.getRelatedServiceTransport().equals(OperationSpecification.ServiceTransportType.FTP)){
            results = controller.performFTPGetOC(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog,
                    new String(responseMessage.getMessageBody(),"UTF-8"),disableAppLayerEncoding);            
        } else if (opSpec.getRelatedServiceTransport().equals(OperationSpecification.ServiceTransportType.HTTP) ||
                opSpec.getRelatedServiceTransport().equals(OperationSpecification.ServiceTransportType.HTTPS)){
            results = controller.performHTTPGetOC(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog,
                    new String(responseMessage.getMessageBody(),"UTF-8"),disableAppLayerEncoding);            
        }
//        System.out.println(results);
        if (results.getResponseMessage()!= null)C2CRIMessageAdapter.updateC2CRIMessage(responseMessage, results.getResponseMessage());
        return new NTCIP2306ApplicationLayerOperationResults(results);
    }

    /**
     * Perform a request response message exchange as an EC.
     *
     * @param dialog – the name of the dialogue
     * @param requestMessage the request message
     * @return the operation results
     * @throws Exception the exception
     */
    @Override
    public ApplicationLayerOperationResults performRequestResponseEC(String dialog, Message requestMessage) throws Exception {
        setDisableAppLayerEncoding(requestMessage.isSkipApplicationLayerEncoding()||disableAppLayerEncoding);
        C2CRIMessageAdapter.updateMessageFromSpec(requestMessage, getInfoLayerNameSpaceMap());
        ArrayList<OperationSpecification> opSpecs = controller.getRiWSDL().getOperationSpecification(dialog);
        // Select the first opSpec available
        OperationSpecification opSpec = opSpecs.get(0);
        NTCIP2306ControllerResults results = null;
        
        if (opSpec.getOperationInputEncoding().equalsIgnoreCase("soap")){
               results = controller.performSOAPRREC(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog,
                       new String (requestMessage.getMessageBody(),"UTF-8"),disableAppLayerEncoding);                            
        } else if (opSpec.getOperationInputEncoding().equalsIgnoreCase("xml")){
               results = controller.performHTTPPostEC(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog,
                       new String (requestMessage.getMessageBody(),"UTF-8"),disableAppLayerEncoding);                                        
        }
//        System.out.println(results);
        if (results.getRequestMessage()!= null)C2CRIMessageAdapter.updateC2CRIMessage(requestMessage, results.getRequestMessage());
        return new NTCIP2306ApplicationLayerOperationResults(results);
    }

    /**
     * Perform the request portion of the request response exchange as an OC.
     *
     * @param dialog –  the dialogue name
     * @return the operations results
     * @throws Exception the exception
     */
    @Override
    public ApplicationLayerOperationResults performRequestResponseOCReceive(String dialog) throws Exception {
        ArrayList<OperationSpecification> opSpecs = controller.getRiWSDL().getOperationSpecification(dialog);
        // Select the first opSpec available
        OperationSpecification opSpec = opSpecs.get(0);
        NTCIP2306ControllerResults results=null;
        if (opSpec.getOperationInputEncoding().equalsIgnoreCase("soap")){
               results = controller.performSOAPRROCRequest(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog);                            
        } else if (opSpec.getOperationInputEncoding().equalsIgnoreCase("xml")){
               results = controller.performHTTPPostOCRequest(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog);                            
        }
        System.out.println(results);
        return new NTCIP2306ApplicationLayerOperationResults(results);
    }

    /**
     * Perform the response portion of the request response dialogue as an OC.
     *
     * @param dialog the dialog
     * @param responseMessage the response message
     * @param isErrorResponse the is error response
     * @return the operation results
     * @throws Exception the exception
     */
    @Override
    public ApplicationLayerOperationResults performRequestResponseOCResponse(String dialog, Message responseMessage, boolean isErrorResponse) throws Exception {
        setDisableAppLayerEncoding(responseMessage.isSkipApplicationLayerEncoding()||disableAppLayerEncoding);
        C2CRIMessageAdapter.updateMessageFromSpec(responseMessage, getInfoLayerNameSpaceMap());
        ArrayList<OperationSpecification> opSpecs = controller.getRiWSDL().getOperationSpecification(dialog);
        // Select the first opSpec available
        OperationSpecification opSpec = opSpecs.get(0);
        NTCIP2306ControllerResults results=null;
        if (opSpec.getOperationInputEncoding().equalsIgnoreCase("soap")){
               results = controller.performSOAPRROCResponse(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog,
                       new String (responseMessage.getMessageBody(),"UTF-8"),disableAppLayerEncoding, isErrorResponse);                            
        } else if (opSpec.getOperationInputEncoding().equalsIgnoreCase("xml")){
               results = controller.performHTTPPostOCResponse(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog,
                       new String (responseMessage.getMessageBody(),"UTF-8"),disableAppLayerEncoding);                                        
        }

//        results = controller.performFTPGetEC(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog);
//        System.out.println(results);
        if (results.getResponseMessage()!= null)C2CRIMessageAdapter.updateC2CRIMessage(responseMessage, results.getResponseMessage());
        return new NTCIP2306ApplicationLayerOperationResults(results);
    }

    /**
     * Perform the subscription operation as an EC.
     *
     * @param dialog – the name of the dialogue
     * @param requestMessage the request message
     * @return the operations results
     * @throws Exception the exception
     */
    @Override
    public ApplicationLayerOperationResults performSubscriptionEC(String dialog, Message requestMessage) throws Exception {
        setDisableAppLayerEncoding(requestMessage.isSkipApplicationLayerEncoding()||disableAppLayerEncoding);
        C2CRIMessageAdapter.updateSubPubMessageFromSpec(requestMessage, getInfoLayerNameSpaceMap());
        ArrayList<OperationSpecification> opSpecs = controller.getRiWSDL().getOperationSpecification(dialog);
        // Select the first opSpec available
        OperationSpecification opSpec = opSpecs.get(0);
        NTCIP2306ControllerResults results = null;
        results = controller.performSOAPSUBEC(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog,
                       new String (requestMessage.getMessageBody(),"UTF-8"),disableAppLayerEncoding);                            
//        System.out.println(results);
        if (results.getRequestMessage()!= null)C2CRIMessageAdapter.updateC2CRIMessage(requestMessage, results.getRequestMessage());        
        ApplicationLayerOperationResults apResults = new NTCIP2306ApplicationLayerOperationResults(results);
        return apResults;
    }

    /**
     * Perform the request portion of a subscription operation as an OC.
     *
     * @param dialog the dialog
     * @return the operation results
     * @throws Exception the exception
     */
    @Override
    public ApplicationLayerOperationResults performSubscriptionOCReceive(String dialog) throws Exception {
        ArrayList<OperationSpecification> opSpecs = controller.getRiWSDL().getOperationSpecification(dialog);
        // Select the first opSpec available
        OperationSpecification opSpec = opSpecs.get(0);
        NTCIP2306ControllerResults results = null;
        results = controller.performSOAPSUBOCRequest(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog);                            
        System.out.println(results);
        ApplicationLayerOperationResults apResults = new NTCIP2306ApplicationLayerOperationResults(results);
        return apResults;
    }

    /**
     * Perform the response portion of a subscription exchange as an OC.
     *
     * @param dialog – the name of the dialogue
     * @param responseMessage the response message
     * @param isErrorResponse the is error response
     * @return the operation results
     * @throws Exception the exception
     */
    @Override
    public ApplicationLayerOperationResults performSubscriptionOCResponse(String dialog, Message responseMessage, boolean isErrorResponse) throws Exception {
        setDisableAppLayerEncoding(responseMessage.isSkipApplicationLayerEncoding()||disableAppLayerEncoding);
        C2CRIMessageAdapter.updateMessageFromSpec(responseMessage, getInfoLayerNameSpaceMap());
        ArrayList<OperationSpecification> opSpecs = controller.getRiWSDL().getOperationSpecification(dialog);
        // Select the first opSpec available
        OperationSpecification opSpec = opSpecs.get(0);
        NTCIP2306ControllerResults results = null;
        results = controller.performSOAPSUBOCResponse(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog,
                       new String (responseMessage.getMessageBody(),"UTF-8"),disableAppLayerEncoding, isErrorResponse);                            

        // Update the Response Message
        if (results.getResponseMessage()!= null)C2CRIMessageAdapter.updateC2CRIMessage(responseMessage, results.getResponseMessage());
        ApplicationLayerOperationResults apResults = new NTCIP2306ApplicationLayerOperationResults(results);
        return apResults;
    }

    /**
     * Perform a publication operation as an OC.
     *
     * @param dialog the dialog
     * @param requestMessage the request message
     * @return the operation results
     * @throws Exception the exception
     */
    @Override
    public ApplicationLayerOperationResults performPublicationOC(String dialog, Message requestMessage) throws Exception {
        setDisableAppLayerEncoding(requestMessage.isSkipApplicationLayerEncoding()||disableAppLayerEncoding);
        C2CRIMessageAdapter.updateSubPubMessageFromSpec(requestMessage, getInfoLayerNameSpaceMap());
        ArrayList<OperationSpecification> opSpecs = controller.getRiWSDL().getOperationSpecification(dialog);
        // Select the first opSpec available
        OperationSpecification opSpec = opSpecs.get(0);
        NTCIP2306ControllerResults results = null;
        results = controller.performSOAPPUBOC(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog,
                       new String (requestMessage.getMessageBody(),"UTF-8"),disableAppLayerEncoding);                            
//        System.out.println(results);
        // Update the Request Message
        if (results.getRequestMessage()!= null)C2CRIMessageAdapter.updateC2CRIMessage(requestMessage, results.getRequestMessage());
        ApplicationLayerOperationResults apResults = new NTCIP2306ApplicationLayerOperationResults(results);
        return apResults;
    }

    /**
     * Perform the received portion of a publication exchange as an EC.
     *
     * @param dialog the dialog
     * @return the operation results
     * @throws Exception the exception
     */
    @Override
    public ApplicationLayerOperationResults performPublicationECReceive(String dialog) throws Exception {
        ArrayList<OperationSpecification> opSpecs = controller.getRiWSDL().getOperationSpecification(dialog);
        // Select the first opSpec available
        OperationSpecification opSpec = opSpecs.get(0);
        NTCIP2306ControllerResults results = null;
        results = controller.performSOAPPUBECRequest(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog);                            
        System.out.println(results);
        ApplicationLayerOperationResults apResults = new NTCIP2306ApplicationLayerOperationResults(results);
        return apResults;
    }

    /**
     * Perform the response portion of a publication exchange as an EC.
     *
     * @param dialog the dialog
     * @param responseMessage the response message
     * @param isErrorResponse the is error response
     * @return the operation results
     * @throws Exception the exception
     */
    @Override
    public ApplicationLayerOperationResults performPublicationECResponse(String dialog, Message responseMessage, boolean isErrorResponse) throws Exception{
        setDisableAppLayerEncoding(responseMessage.isSkipApplicationLayerEncoding()||disableAppLayerEncoding);
        C2CRIMessageAdapter.updateMessageFromSpec(responseMessage, getInfoLayerNameSpaceMap());
        ArrayList<OperationSpecification> opSpecs = controller.getRiWSDL().getOperationSpecification(dialog);
        // Select the first opSpec available
        OperationSpecification opSpec = opSpecs.get(0);
        NTCIP2306ControllerResults results = null;
        results = controller.performSOAPPUBECResponse(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), dialog,
                       new String (responseMessage.getMessageBody(),"UTF-8"),disableAppLayerEncoding, isErrorResponse);                            
        // Update the Response Message
        if (results.getResponseMessage()!= null)C2CRIMessageAdapter.updateC2CRIMessage(responseMessage, results.getResponseMessage());
        ApplicationLayerOperationResults apResults = new NTCIP2306ApplicationLayerOperationResults(results);
        return apResults;
    }

    /**
     * setter for the disable app layer encoding field.
     *
     * @param disableAppLayerEncoding the new disable app layer encoding
     */
    @Override
    public void setDisableAppLayerEncoding(boolean disableAppLayerEncoding) {
        this.disableAppLayerEncoding = disableAppLayerEncoding;
    }

    /**
     * setter for the namespace map.
     *
     * @param prefixToURIMap the prefix to uri map
     */
    @Override
    public void setNameSpaceMap(HashMap<String, String> prefixToURIMap) {
        this.infoLayerNameSpaceMap.putAll(prefixToURIMap);
    }
    
    /*
     * getter for the namespace map
     */
    /**
     * Gets the info layer name space map.
     *
     * @return the info layer name space map
     */
    private HashMap<String,String> getInfoLayerNameSpaceMap(){
        HashMap<String, String> tmpMap = new HashMap();
        tmpMap.putAll(this.infoLayerNameSpaceMap);
        return tmpMap;
    }
         
    /**
     * shut down all services.
     */
    @Override
    public void shutdown(){
        controller.shutdown();
    }
}
