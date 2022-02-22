/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddv3verification.utilities.inouts;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import tmddv3verification.utilities.TMDDDatabase;
import tmddv3verification.utilities.TMDDWSDL;

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
        String wsdlFileName = "file:\\c:\\c2cri\\testfiles\\tmddv301\\tmdd-fixed.wsdl";
        //    TMDDWSDL tmddWSDL = new TMDDWSDL(wsdlFileName);

        TMDDWSDL thisWSDL = new TMDDWSDL(wsdlFileName);

//        String messageType = "centerActiveVerificationRequestMsg";
        String messageType = "errorReportMsg";

        SchemaType[] globalElems = thisWSDL.getSchemaTypeSystem().documentTypes();
        for (int ii = 0; ii < globalElems.length; ii++) {
            if (globalElems[ii].getDocumentElementName().getLocalPart().equals(messageType)){
             String result = SampleXmlUtilDoctored.createSampleForType(globalElems[ii]);
//             System.out.println(result);
             try{
                  XmlOptions thisOption = new XmlOptions();
                  thisOption.setLoadStripWhitespace();
                  thisOption.setLoadStripComments();

                  XmlObject thisObject1 = XmlObject.Factory.parse(result, thisOption);

                  System.out.println(thisObject1.xmlText(thisOption));
                } catch (Exception ex){

                }
            }
        }

    }


}
