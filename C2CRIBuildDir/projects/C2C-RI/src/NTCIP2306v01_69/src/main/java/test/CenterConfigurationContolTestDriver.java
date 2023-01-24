/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javax.xml.datatype.DatatypeFactory;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationResults;
import org.fhwa.c2cri.ntcip2306v109.c2cadmin.C2CMessageSubscription;
import org.fhwa.c2cri.ntcip2306v109.c2cadmin.ObjectFactory;
import org.fhwa.c2cri.ntcip2306v109.c2cadmin.SubscriptionAction;
import org.fhwa.c2cri.ntcip2306v109.c2cadmin.SubscriptionTimeFrame;
import org.fhwa.c2cri.ntcip2306v109.c2cadmin.SubscriptionType;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.Center;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.CenterConfigurationController;
import org.fhwa.c2cri.ntcip2306v109.subpub.Subscription;
import org.fhwa.c2cri.ntcip2306v109.utilities.XMLFileReader;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;



/**
 * The Class CenterConfigurationContolTestDriver.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class CenterConfigurationContolTestDriver {

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        RIWSDL theWSDL = new RIWSDL("file:/c:/c2cri/support/tmdd.wsdl");
        CenterConfigurationController ccc2 = new CenterConfigurationController(null, theWSDL, Center.RICENTERMODE.EC);
        ccc2.initialzeServices();

        OperationIdentifier opId = new OperationIdentifier("tmddHTTPPostService", "tmddHTTPPostPort", "OP_ManageDMSInventoryRequest", OperationIdentifier.SOURCETYPE.HANDLER);
        NTCIP2306Message fakeMsg;
        System.out.println("CenterConfigurationControlTestDriver::main Starting Operation " + opId);
        OperationIdentifier listenerOpId = new OperationIdentifier("tmddHTTPPostService", "tmddHTTPPostPort", "OP_ManageDMSInventoryRequest", OperationIdentifier.SOURCETYPE.LISTENER);

        ObjectFactory c2cFactory = new ObjectFactory();
        C2CMessageSubscription sub =c2cFactory.createC2CMessageSubscription();
        sub.setSubscriptionID("tmddECSoapHttpService.tmddECSoapHttpServicePort.dlDetectorInventoryUpdate");
        SubscriptionAction actions = c2cFactory.createSubscriptionAction();
        actions.getSubscriptionActionItem().add("newSubscription");
        sub.setSubscriptionAction(actions);
        SubscriptionType types = c2cFactory.createSubscriptionType();
        types.setSubscriptionTypeItem("periodic");
        sub.setSubscriptionType(types);
        SubscriptionTimeFrame subtf = new SubscriptionTimeFrame();
        GregorianCalendar gc = new GregorianCalendar();        
        Date nowTime =Calendar.getInstance().getTime(); 
        gc.setTime(nowTime);
        subtf.setStart(DatatypeFactory.newInstance().newXMLGregorianCalendar(gc));
        Calendar myCal = Calendar.getInstance();
        myCal.add(Calendar.MILLISECOND, 60000);
        gc.setTime(myCal.getTime());
        subtf.setEnd(DatatypeFactory.newInstance().newXMLGregorianCalendar(gc));
        sub.setSubscriptionTimeFrame(subtf);
        sub.setSubscriptionFrequency(5);
        sub.setReturnAddress("http://localhost:8086/TMDDWS/TmddWS.svc/EC");
        sub.setSubscriptionName("CenterConfiguraitonControlTestDriver Test 2");
        byte[] subContent = Subscription.produceC2cMessageSubscriptionXML(sub);
        fakeMsg = new NTCIP2306Message("http://www.ntcip.org/c2c-message-administration", "c2cMessageSubscription", subContent);
        fakeMsg.addMessagePart("http://www.tmdd.org/301/messages", "deviceInformationRequestMsg", XMLFileReader.readFile("C:\\c2cri\\support\\TestDeviceInformationRequestMsg.xml"));
        fakeMsg.setSkipEncoding(false);
        opId = new OperationIdentifier("tmddOCSoapHttpService", "tmddOCSoapHttpServicePort", "dlDeviceInformationSubscription", OperationIdentifier.SOURCETYPE.HANDLER);
        listenerOpId = new OperationIdentifier("tmddOCSoapHttpService", "tmddOCSoapHttpServicePort", "dlDeviceInformationSubscription", OperationIdentifier.SOURCETYPE.LISTENER);

        HashMap<OperationIdentifier,OperationIdentifier> thisMap = new HashMap();
        OperationIdentifier pubOpId = new OperationIdentifier("tmddECSoapHttpService",
                    "tmddECSoapHttpServicePort", "dlDetectorInventoryUpdate", OperationIdentifier.SOURCETYPE.LISTENER);
        thisMap.put(listenerOpId, pubOpId);
        ccc2.setSubPubMapping(thisMap);
        
        System.out.println("CenterConfigurationControlTestDriver::main Starting Operation " + opId);
        ccc2.registerForOperation(opId);
        ccc2.sendRequest(listenerOpId, fakeMsg);
        OperationResults opResults = ccc2.getOperationResults(opId,7000);
        fakeMsg = ccc2.getResponse(opId,7000);
        ccc2.unregisterForOperation(opId);
        System.out.println("CenterConfigurationControlTestDriver::main Finished Operation " + opId);

        
        Thread.sleep(15000);
        
        actions = c2cFactory.createSubscriptionAction();
        actions.getSubscriptionActionItem().add("cancelSubscription");
        sub.setSubscriptionAction(actions);
        subContent = Subscription.produceC2cMessageSubscriptionXML(sub);
        fakeMsg = new NTCIP2306Message("http://www.ntcip.org/c2c-message-administration", "c2cMessageSubscription", subContent);
        fakeMsg.addMessagePart("http://www.tmdd.org/301/messages", "deviceInformationRequestMsg", XMLFileReader.readFile("C:\\c2cri\\support\\TestDeviceInformationRequestMsg.xml"));
        fakeMsg.setSkipEncoding(false);
        ccc2.registerForOperation(opId);
        ccc2.sendRequest(listenerOpId, fakeMsg);
        opResults = ccc2.getOperationResults(opId,7000);
        fakeMsg = ccc2.getResponse(opId,7000);
        ccc2.unregisterForOperation(opId);
        System.out.println("CenterConfigurationControlTestDriver::main Finished Operation (Cancelled)" + opId);
        
    }

}
