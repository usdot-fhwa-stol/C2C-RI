/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddv3verification.utilities.inouts;

import org.apache.xmlbeans.SchemaType;
import tmddv3verification.utilities.TMDDDatabase;
import tmddv3verification.utilities.TMDDWSDL;

/**
 *
 * @author TransCore ITS
 */
public class PopulateDesignDetailTable {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        String wsdlFileName = "file:\\c:\\inout\\tmp\\tmdd_modified.wsdl";
        String wsdlFileName = "file:\\C:\\TMDDSchemasv301d6\\tmdd.wsdl";
        //    TMDDWSDL tmddWSDL = new TMDDWSDL(wsdlFileName);

        TMDDWSDL thisWSDL = new TMDDWSDL(wsdlFileName);

        clearTable();

        SchemaType[] globalElems = thisWSDL.getSchemaTypeSystem().documentTypes();
        for (int ii = 0; ii < globalElems.length; ii++) {
             String result = SampleXmlUtilDoctored.createSampleForType(globalElems[ii]);
        }
        SampleXmlUtilDoctored.storeResults();

    }

    public static void clearTable() {

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();


            theDatabase.queryNoResult("delete * from TMDDDesignDataDetail");


        theDatabase.disconnectFromDatabase();
    }

}
