/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Iterator;
import java.util.Map;
import javax.wsdl.BindingOperation;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.encoders.SOAPEncoder;
import org.fhwa.c2cri.testcases.OperationMessageBuilder;
//import tmddv3verification.utilities.TMDDDatabase;
import tmddv3verification.utilities.TMDDWSDL;
import tmddv3verification.utilities.inouts.SampleXmlUtilDoctored;

/**
 *
 * @author TransCore ITS
 */
public class CreateTMDDMessage {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        String wsdlFileName = "file:\\c:\\inout\\tmp\\tmdd_modified.wsdl";
//        String wsdlFileName = "file:\\c:\\c2cri\\testfiles\\tmddv301\\tmdd-fixed.wsdl";
        String wsdlFileName = "file:/c:/c2cri/testfiles/release3-final.wsdl";
        //    TMDDWSDL tmddWSDL = new TMDDWSDL(wsdlFileName);

        TMDDWSDL thisWSDL = new TMDDWSDL(wsdlFileName);

//        String messageType = "centerActiveVerificationRequestMsg";
        String messageType = "errorReportMsg";



        Map services = thisWSDL.getServiceNames();
        Iterator serviceIterator = services.keySet().iterator();
        while (serviceIterator.hasNext()) {
            String theService = (String) serviceIterator.next();
            QName qService = (QName) services.get(theService);

            String relatedDialog = "OP_DeviceInformationUpdate";

            System.out.println("Checking the Service : " + theService + " for dialog " + relatedDialog);
//                if (theService.equalsIgnoreCase("tmddOCSoapHttpService")) {
            // Found the selected service in the WSDL

            Map ports = thisWSDL.getServicePortNames(qService);
            Iterator portIterator = ports.keySet().iterator();
            while (portIterator.hasNext()) {
                String thePort = (String) portIterator.next();
                System.out.println("Checking the Port : " + thePort);
                BindingOperation theBindingOperation = thisWSDL.getServicePortBindingOperation(qService, thePort, relatedDialog);
                if (theBindingOperation != null) {

                    OperationMessageBuilder messageBuilder = new OperationMessageBuilder(thisWSDL);
                    SOAPEncoder theEncoder = new SOAPEncoder("SoapType");

                    //             System.out.println(result);
                    try {
                        byte[] returnMsg = theEncoder.encode(messageBuilder.buildMessageFromInput(theBindingOperation, true, true).getBytes());
//                        String encodedString = new String(returnMsg);
                        String encodedString = new String(messageBuilder.buildMessageFromInput(theBindingOperation, true, true).getBytes());
                        XmlOptions thisOption = new XmlOptions();
                        thisOption.setLoadStripWhitespace();
                        thisOption.setLoadStripComments();

                        XmlObject thisObject1 = XmlObject.Factory.parse(encodedString, thisOption);

                        System.out.println(thisObject1.xmlText(thisOption));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }


//        SchemaType[] globalElems = thisWSDL.getSchemaTypeSystem().documentTypes();
//        for (int ii = 0; ii < globalElems.length; ii++) {
//            if (globalElems[ii].getDocumentElementName().getLocalPart().equals(messageType)){
//             String result = SampleXmlUtilDoctored.createSampleForType(globalElems[ii]);
//             SOAPEncoder theEncoder = new SOAPEncoder("SoapType");
//
//                 //             System.out.println(result);
//             try{
//                 byte[] returnMsg = theEncoder.encode(result.getBytes());
//                 String encodedString = new String (returnMsg);
//             XmlOptions thisOption = new XmlOptions();
//                  thisOption.setLoadStripWhitespace();
//                  thisOption.setLoadStripComments();
//
//                  XmlObject thisObject1 = XmlObject.Factory.parse(encodedString, thisOption);
//
//                  System.out.println(thisObject1.xmlText(thisOption));
//                } catch (Exception ex){
//
//                }
//            }
//    }
}

}
