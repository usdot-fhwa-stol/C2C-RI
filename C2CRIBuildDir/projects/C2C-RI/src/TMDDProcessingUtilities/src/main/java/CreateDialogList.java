/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;


/**
 *
 * @author TransCore ITS, LLC
 */
public class CreateDialogList {

    public static void main(String args[]) {
    String wsdlFile = "file:/c:/inout/TMDDv303brPS/tmdd.wsdl";

        if ((args != null)&&(args.length!=0)) wsdlFile = args[0];
        RIWSDL theRIWSDL = new RIWSDL(wsdlFile);

        System.out.println("dialog,input,output,error,dialogType");

        Map serviceMap = theRIWSDL.getServiceNames();
        Iterator mapIterator = serviceMap.keySet().iterator();
        while (mapIterator.hasNext()){
            String serviceName = (String) mapIterator.next();
            QName serviceQName = (QName)serviceMap.get(serviceName);
            Map portMap = theRIWSDL.getServicePortNames(serviceQName);
            Iterator portIterator = portMap.keySet().iterator();
            while (portIterator.hasNext()){
                String portName = (String)portIterator.next();
//               System.out.println(serviceName+","+portName);
               Map operationMap = theRIWSDL.getServicePortOperationInformation(serviceQName, portName);
               Iterator operationIterator = operationMap.keySet().iterator();
               while (operationIterator.hasNext()){
                   OperationSpecification opSpec = (OperationSpecification)operationMap.get(operationIterator.next());
                   String inputMsg = getPrimaryMessageName(opSpec.getInputMessage());
                   String outputMsg = getPrimaryMessageName(opSpec.getOutputMessage());
                   String errorMsg = getPrimaryMessageName(opSpec.getFaultMessage());
                   String opType = "";
                   if (opSpec.getOperationType().equals("RR")) {
                           opType = "RR";
                   }
                   else if (opSpec.getOperationType().equals("SUB")) {
                           opType = "S";
                   }
                    else {
                        opType = "P";
                   }

                   System.out.println(opSpec.getOperationName()+","+inputMsg+","+outputMsg+","+errorMsg+","+opType);
               }

            }
        }


//        ArrayList<String> soapOperations = theRIWSDL.getSOAPServiceOperations();

//        for (String thisOperation: soapOperations) {
//            ArrayList<String> messages = theRIWSDL.getDialogRelatedMessages(thisOperation);
//            for (String thisMessage: messages) {
//                ArrayList<QName> wsdlMessageList = theRIWSDL.getSchemaMessageNames();
//            }
//            System.out.println(thisOperation+","+ messages.get(0)+","+messages.get(1)+","+messages.get(2));
//        }
    }

    public static String getPrimaryMessageName(List<QName> messageList){
        if (messageList.size() > 1){
            return messageList.get(1).getLocalPart();
        } else
            return messageList.get(0).getLocalPart();
    }
}
