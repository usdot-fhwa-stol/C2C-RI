/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddv3verification.utilities.inouts;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
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
             createSampleForType(globalElems[ii]);
        }
        SampleXmlUtilDoctored.storeResults();

    }
	
	    public static String createSampleForType(SchemaType sType) {
        XmlObject object = XmlObject.Factory.newInstance();
        XmlCursor cursor = object.newCursor();
        // Skip the document node
        cursor.toNextToken();
        // Using the type and the cursor, call the utility method to get a
        // sample XML payload for that Schema element
		SampleXmlUtilDoctored oSample = new SampleXmlUtilDoctored(false);
		SampleXmlUtilDoctored.messageName = sType.getDocumentElementName().getLocalPart();
		oSample.lastParentType = SampleXmlUtilDoctored.messageName;
        oSample.createSampleForType(sType, cursor);
        // Cursor now contains the sample payload
        // Pretty print the result.  Note that the cursor is positioned at the
        // end of the doc so we use the original xml object that the cursor was
        // created upon to do the xmlText() against.
        XmlOptions options = new XmlOptions();
        options.put(XmlOptions.SAVE_PRETTY_PRINT);
        options.put(XmlOptions.SAVE_PRETTY_PRINT_INDENT, 2);
        options.put(XmlOptions.SAVE_AGGRESSIVE_NAMESPACES);
        return object.xmlText(options);
    }

    public static void clearTable() {

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();


            theDatabase.queryNoResult("delete * from TMDDDesignDataDetail");


        theDatabase.disconnectFromDatabase();
    }

}
