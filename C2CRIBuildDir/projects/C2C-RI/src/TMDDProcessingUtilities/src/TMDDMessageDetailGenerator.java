/*   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 */


import org.apache.xmlbeans.XmlObject;
import java.util.ArrayList;
import java.io.File;
import java.util.HashSet;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.SchemaType;
//import org.apache.xmlbeans.impl.xsd2inst.SampleXmlUtil;
import java.util.Set;
import org.apache.xmlbeans.XmlException;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import org.apache.xmlbeans.impl.tool.CommandLine;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.apache.xmlbeans.SchemaLocalElement;

public class TMDDMessageDetailGenerator {

    public static void printUsage() {
        System.out.println("Generates a document based on the given Schema file");
        System.out.println("having the given element as root.");
        System.out.println("The tool makes reasonable attempts to create a valid document,");
        System.out.println("but this is not always possible since, for example, ");
        System.out.println("there are schemas for which no valid instance document ");
        System.out.println("can be produced.");
        System.out.println("Usage: xsd2inst [flags] schema.xsd -name element_name");
        System.out.println("Flags:");
        System.out.println("    -name    the name of the root element");
        System.out.println("    -dl      enable network downloads for imports and includes");
        System.out.println("    -nopvr   disable particle valid (restriction) rule");
        System.out.println("    -noupa   diable unique particle attributeion rule");
        System.out.println("    -license prints license information");
    }

    public static void main(String[] args) {
        Set flags = new HashSet();
        Set opts = new HashSet();
        flags.add("h");
        flags.add("help");
        flags.add("usage");
        flags.add("license");
        flags.add("version");
        flags.add("dl");
        flags.add("noupa");
        flags.add("nopvr");
        flags.add("partial");
        opts.add("name");

        String[] tempargs = new String[10];
        tempargs[0] = "-name";
        tempargs[1] = "deviceInformationRequestMsg";
        tempargs[2] = "-dl";
        tempargs[3] = "C:\\Inout\\TMDDSchemasv302\\TMDD.xsd";
        tempargs[4] = "C:\\Inout\\TMDDSchemasv302\\C2C.xsd";
        tempargs[5] = "C:\\Inout\\TMDDSchemasv302\\NTCIP-References.xsd";
        tempargs[6] = "C:\\Inout\\TMDDSchemasv302\\LRMS-Local-02-00-00.xsd";
        tempargs[7] = "C:\\Inout\\TMDDSchemasv302\\LRMS-Adopted-02-00-00.xsd";
        tempargs[8] = "C:\\Inout\\TMDDSchemasv302\\ITIS-Local-03-00-02.xsd";
        tempargs[9] = "C:\\Inout\\TMDDSchemasv302\\ITIS-Adopted-03-00-02.xsd";

        CommandLine cl = new CommandLine(tempargs, flags, opts);

        if (cl.getOpt("h") != null || cl.getOpt("help") != null || cl.getOpt("usage") != null) {
            printUsage();
            return;
        }

        String[] badOpts = cl.getBadOpts();
        if (badOpts.length > 0) {
            for (int i = 0; i < badOpts.length; i++) {
                System.out.println("Unrecognized option: " + badOpts[i]);
            }
            printUsage();
            return;
        }

        if (cl.getOpt("license") != null) {
            CommandLine.printLicense();
            System.exit(0);
            return;
        }

        if (cl.getOpt("version") != null) {
            CommandLine.printVersion();
            System.exit(0);
            return;
        }

        boolean dl = (cl.getOpt("dl") != null);
        boolean nopvr = (cl.getOpt("nopvr") != null);
        boolean noupa = (cl.getOpt("noupa") != null);

        File[] schemaFiles = cl.filesEndingWith(".xsd");
        String rootName = cl.getOpt("name");

        if (rootName == null) {
            System.out.println("Required option \"-name\" must be present");
            return;
        }

        // Process Schema files
        List sdocs = new ArrayList();
        for (int i = 0; i < schemaFiles.length; i++) {
            try {
                sdocs.add(XmlObject.Factory.parse(schemaFiles[i],
                        (new XmlOptions()).setLoadLineNumbers().setLoadMessageDigest()));
            } catch (Exception e) {
                System.err.println("Can not load schema file: " + schemaFiles[i] + ": ");
                e.printStackTrace();
            }
        }

        XmlObject[] schemas = (XmlObject[]) sdocs.toArray(new XmlObject[sdocs.size()]);

        SchemaTypeSystem sts = null;
        if (schemas.length > 0) {
            Collection errors = new ArrayList();
            XmlOptions compileOptions = new XmlOptions();
            if (dl) {
                compileOptions.setCompileDownloadUrls();
            }
            if (nopvr) {
                compileOptions.setCompileNoPvrRule();
            }
            if (noupa) {
                compileOptions.setCompileNoUpaRule();
            }

            try {
                sts = XmlBeans.compileXsd(schemas, XmlBeans.getBuiltinTypeSystem(), compileOptions);
            } catch (Exception e) {
                if (errors.isEmpty() || !(e instanceof XmlException)) {
                    e.printStackTrace();
                }

                System.out.println("Schema compilation errors: ");
                for (Iterator i = errors.iterator(); i.hasNext();) {
                    System.out.println(i.next());
                }
            }
        }

        if (sts == null) {
            System.out.println("No Schemas to process.");
            return;
        }

        SchemaType[] globalElems = sts.documentTypes();
        SchemaType elem = null;
        String msgName = null;
        System.out.println("");
//        System.out.println("processElement,Prefix,type,level,Message,Parent,ParentType,Path");
        System.out.println("elementName,Prefix,DataConcept,Level,Message,ParentElement,ParentType,Path,BaseType,minOccurs,maxOccurs,minLength,maxLengh,minInclusive,maxInclusive,enumeration,valueStored,value");

        for (int ii = 0; ii < globalElems.length; ii++) {
            msgName = globalElems[ii].getDocumentElementName().getLocalPart();
//            System.out.println("Message "+ msgName);
            for (int i = 0; i < globalElems.length; i++) {
//            if (rootName.equals(globalElems[i].getDocumentElementName().getLocalPart()))
                if (msgName.equals(globalElems[i].getDocumentElementName().getLocalPart())) {
                    elem = globalElems[i];
                    break;
                }
            }

            if (elem == null) {
                System.out.println("Could not find a global element with name \"" + rootName + "\"");
                return;
            }

            // Now generate it
//        String result = SampleXmlUtil.createSampleForType(elem);
//            if (msgName.equals("cCTVInventoryMsg")){
//                System.out.println("Message Match");
   //         String result = SampleXmlUtilMessageAnalysis.createSampleForType(elem);
 //            String result = SampleXmlUtilDoctored.createSampleForType(elem);
//             System.out.println(result);
    //         addConcept(msgName, result, false, true, false, true);
//            }
        }

        return;
    }

public static void addConcept(String element, String message, boolean range, boolean normal, boolean negative, boolean soap){
    try {
        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        // set this to a MS Access DB you have on your machine
        String filename = "C:\\tmp\\TMDD_TCS_Data.mdb";
        String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
        database+= filename.trim() + ";DriverID=22;READONLY=true}"; // add on to the end
        // now we can get the connection from the DriverManager
//        System.out.println(database);
        try (Connection con = DriverManager.getConnection( database);
			 Statement s = con.createStatement())
		{
//        s.execute("create table TEST12345 ( column_name integer )"); // create a table
//        s.execute("insert into TEST12345 values(1)"); // insert some data into the table
        s.execute("insert into TCS_SOAPReady_Messages (Element, MessageData)" + "values('" + element+"','" + message+"')"); // insert the data into the table'
//           s.execute("drop table TEST12345");
		}

    }


        catch (Exception e) {
        System.out.println("Error: " + e);
    }

}

}

