/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.util.Iterator;
import java.util.Map;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.PortType;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

/**
 * The Class OperationInputsAndOutputs.
 *
 * @author TransCore ITS, LLC
 * @deprecated
 * Last Updated:  1/8/2014
 */
public class OperationInputsAndOutputs {
    
    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String[] args) {


        try {
            WSDLFactory newFactory = WSDLFactory.newInstance();
            WSDLReader newReader = newFactory.newWSDLReader();


            Definition wsdl = newReader.readWSDL("C:\\Documents and Settings\\RI\\InitialDeployment\\TMDD\\FixTMDD\\TMDDv302\\tmdd.wsdl");

            Map portTypesMap = wsdl.getPortTypes();
            Iterator portTypesIter = portTypesMap.values().iterator();
            while (portTypesIter.hasNext()){
                PortType thisPortType = (PortType)portTypesIter.next();

                for (Object thisObject : thisPortType.getOperations()){
                    Operation thisOperation = (Operation)thisObject;

                    Input thisInput = thisOperation.getInput();
                    String inputMsg = thisInput.getMessage().getPart("message").getElementName().getLocalPart();

                    Output thisOutput = thisOperation.getOutput();
                    String outputMsg = thisOutput.getMessage().getPart("message").getElementName().getLocalPart();

                    System.out.println(thisOperation.getName()+","+ inputMsg +","+ outputMsg);
    
                }

            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
