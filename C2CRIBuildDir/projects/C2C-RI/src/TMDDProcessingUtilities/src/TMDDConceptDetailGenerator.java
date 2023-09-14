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
import java.io.StringWriter;
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
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.SchemaLocalElement;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.XmlInteger;

public class TMDDConceptDetailGenerator {
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
        tempargs[3] = "C:\\Inout\\TMDD Schemas v9.0\\TMDD.xsd";
        tempargs[4] = "C:\\Inout\\TMDD Schemas v9.0\\C2C.xsd";
        tempargs[5] = "C:\\Inout\\TMDD Schemas v9.0\\NTCIP-References.xsd";
        tempargs[6] = "C:\\Inout\\TMDD Schemas v9.0\\LRMS-Local-02-00-00.xsd";
        tempargs[7] = "C:\\Inout\\TMDD Schemas v9.0\\LRMS-Adopted-02-00-00.xsd";
        tempargs[8] = "C:\\Inout\\TMDD Schemas v9.0\\ITIS-Local-03-00-02.xsd";
        tempargs[9] = "C:\\Inout\\TMDD Schemas v9.0\\ITIS-Adopted-03-00-02.xsd";

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
        SchemaType[] globalTypes = sts.globalTypes();
        System.out.println(" Count of GlobalTypes = " + globalTypes.length);
        for (int ii = 0; ii < globalTypes.length; ii++) {
            String msgType = globalTypes[ii].getName().getLocalPart();
            List<String> typeReqs = getRequirements(globalTypes[ii]);
            if ((typeReqs != null) && (typeReqs.size() > 0)) {
                for (String theRequirement : typeReqs) {
                    System.out.println(ii + "," + msgType + "," + (globalTypes[ii].isSimpleType() ? "data-element" : "data-frame") + "," + theRequirement);
                }

            } else {
                System.out.println(ii + "," + msgType + "," + (globalTypes[ii].isSimpleType() ? "data-element" : "data-frame") + ",");

            }

            /**
            if (!globalTypes[ii].isSimpleType()) {
            SchemaParticle[] frameList = globalTypes[ii].getContentModel().getParticleChildren();
            if (frameList != null) {
            for (int jj = 0; jj < frameList.length; jj++) {
            if (frameList[jj].getParticleType() != SchemaParticle.WILDCARD) {
            try {

            System.out.println("     " + jj + ":  " + frameList[jj].getName().getLocalPart() + "  " + frameList[jj].getType().getName().getLocalPart() + "   " + (frameList[jj].getType().isSimpleType() ? " Element" : " Frame"));
            getRequirements(frameList[jj]);
            } catch (Exception ex) {
            System.out.println("     " + jj + ":  **** ERROR Couldn't handle this one !!!");
            }
            }
            }

            }
            } else System.out.println("Simple Type !!!");

             */
        }


        for (int ii = 0; ii < globalTypes.length; ii++) {
            String msgType = globalTypes[ii].getName().getLocalPart();
            List<String> typeReqs = getRequirements(globalTypes[ii]);
            if ((typeReqs != null) && (typeReqs.size() > 0)) {
                for (String theRequirement : typeReqs) {
//                    System.out.println(ii + "," + msgType + "," + (globalTypes[ii].isSimpleType() ? "data-element" : "data-frame")+","+theRequirement);
                }

            } else {
//                System.out.println(ii + "," + msgType + "," + (globalTypes[ii].isSimpleType() ? "data-element" : "data-frame")+",");
            }


            if (!globalTypes[ii].isSimpleType()) {
                SchemaParticle[] frameList = globalTypes[ii].getContentModel().getParticleChildren();
                if (frameList != null) {
                    for (int jj = 0; jj < frameList.length; jj++) {
                        if (frameList[jj].getParticleType() != SchemaParticle.WILDCARD) {
                            try {
                                List<String> subElementReqs = getRequirements(frameList[jj]);
                                XmlInteger length = (XmlInteger) frameList[jj].getType().getFacet(SchemaType.FACET_LENGTH);
                                XmlInteger min = null;
                                if (length == null) {
                                    min = (XmlInteger) frameList[jj].getType().getFacet(SchemaType.FACET_MIN_LENGTH);
                                    length = (XmlInteger) frameList[jj].getType().getFacet(SchemaType.FACET_MAX_LENGTH);
                                }

                                if (subElementReqs.size() > 0) {
                                    for (String theRequirement : subElementReqs) {
                                        System.out.println(ii + "," + msgType + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + frameList[jj].getType().getName().getLocalPart() + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + "," + theRequirement+","+frameList[jj].getMinOccurs()+","+frameList[jj].getMaxOccurs()+","+frameList[jj].getType().getBaseType().getName().getLocalPart()+","+min.getStringValue()+","+length.getStringValue());
                                    }
                                } else {
                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + frameList[jj].getType().getName().getLocalPart() + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + ","+","+frameList[jj].getMinOccurs()+","+frameList[jj].getMaxOccurs()+","+frameList[jj].getType().getBaseType().getName().getLocalPart()+","+min.getStringValue()+","+length.getStringValue());
                                }

                            } catch (Exception ex) {
                                try {

                                    SchemaLocalElement element = (SchemaLocalElement) frameList[jj];
                                    String type = "Unknown";
                                    if (element.getType().isAnonymousType()) {
                                        SchemaParticle[] thisParticleTry = frameList[jj].getType().getContentModel().getParticleChildren();
                                        for (SchemaParticle thisParticle : thisParticleTry) {
                          //                  List<String> subElementReqs = getRequirements(thisParticle.getType());
                                            type = thisParticle.getType().getName().getLocalPart();

//                                            if (subElementReqs.size() > 0) {
                                            if (typeReqs.size() > 0) {
                                                for (String theRequirement : typeReqs) {
                                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + type + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + "," + theRequirement+","+frameList[jj].getMinOccurs() +","+frameList[jj].getType().getContentModel().getMaxOccurs()+","+frameList[jj].getType().getContentModel().getParticleChild(0).getName().getLocalPart());
//                                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + frameList[jj].getType().getName().getLocalPart() + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + "," + theRequirement);
//                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "Element: " + frameList[jj].getName().getLocalPart() + "Type " + type + ",  :  **** ERROR Couldn't handle this one !!!");
                                                }
                                            } else {
                                                 System.out.println(ii + "," + msgType + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + type + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + "," + ""+","+frameList[jj].getMinOccurs() +","+frameList[jj].getType().getContentModel().getMaxOccurs()+","+frameList[jj].getType().getContentModel().getParticleChild(0).getName().getLocalPart());

                                            }

                                        }
                                        //                                       SchemaType thisTry = frameList[jj].getType();
                                        //                    element.getType().getContentModel().
                                        //                    type = element.getType().getListItemType().getName().getLocalPart();
                                    }
                                } catch (Exception ex1) {
                                    String type = "***UNKNOWN***";
                                    if (frameList[jj].getType() != null) {
                                        type = frameList[jj].getType().getBaseType().getName().getLocalPart();
                                        //                                   ex1.printStackTrace();
                                        System.out.println(ii + "," + msgType + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + type + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + ",");
//                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "Element:  **** ERROR Couldn't handle this one At ALL!!!");
                                    } else {
                                      // Sometimes there is a choice list inside of a sequence.  See if that's happening here
                                       if(frameList[jj].getParticleType() == SchemaParticle.CHOICE){
                                           SchemaParticle[] theParticles = frameList[jj].getParticleChildren();
                                           for (SchemaParticle thisParticle : theParticles){
                                                if ((thisParticle.getType() != null)&&(thisParticle.getType().getName()!=null)){
                                                    type = thisParticle.getType().getName().getLocalPart();
                                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "," + thisParticle.getName().getLocalPart() + "," + type + "," + (thisParticle.getType().isSimpleType() ? "data-element" : "data-frame") + ",");

                                                } else {
                                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "," + thisParticle.getName().getLocalPart() + ",UNKNOWN," + (thisParticle.getType().isSimpleType() ? "data-element" : "data-frame") + ",");

                                                }

                                           }
 //                                          System.out.println(ii + "," + msgType + ", !!!! Yes This is A Choice !!!!");

                                       }else{
                                           // Looks like it is a sequence inside of a choice
                                           SchemaParticle[] theParticles = frameList[jj].getParticleChildren();
                                           for (SchemaParticle thisParticle : theParticles){
                                                if ((thisParticle.getType() != null)&&(thisParticle.getType().getName()!=null)){
                                                    type = thisParticle.getType().getName().getLocalPart();
                                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "," + thisParticle.getName().getLocalPart() + "," + type + "," + (thisParticle.getType().isSimpleType() ? "data-element" : "data-frame") + ",");

                                                } else {
                                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "," + thisParticle.getName().getLocalPart() + ",UNKNOWN," + (thisParticle.getType().isSimpleType() ? "data-element" : "data-frame") + ",");

                                                }

                                           }

//                                       System.out.println(ii + "," + msgType + ", !!!! Figure Out how to handle embedded Complex Types !!!!???");
                                        }
                                    }
                                }
//                                ex.printStackTrace();
                            }
                        }
                    }

                }
            } else {
                List<String> subElementReqs = getRequirements(globalTypes[ii].getBaseType());
                if (subElementReqs.size() > 0) {
                    for (String theRequirement : subElementReqs) {
                        //                      System.out.println(ii+"," +msgType+","+ (ii+1) + "," + globalTypes[ii].getBaseType().getName().getLocalPart()+ "," +"data-element"+"," +theRequirement);
                    }
                } else {
                    //                      System.out.println(ii+"," +msgType+","+ (ii+1) + "," + globalTypes[ii].getBaseType().getName().getLocalPart()+ "," +"data-element"+",");
                }
            }




        }


        SchemaType[] globalElems = sts.documentTypes();
        SchemaType elem = null;
        String msgName = null;
        System.out.println("");
//        System.out.println("processElement,Prefix,type,level,Message,Parent,ParentType,Path");
        System.out.println("Message,DataConcept,Mandatory,Type,ConceptType");

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
            //      String result = SampleXmlUtilDoctored.createSampleForType(elem);
//             System.out.println(result);
            //         addConcept(msgName, result, false, true, false, true);
//            }
        }

        return;
    }

    public static void addConcept(String element, String message, boolean range, boolean normal, boolean negative, boolean soap) {
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            // set this to a MS Access DB you have on your machine
            String filename = "C:\\tmp\\TMDD_TCS_Data.mdb";
            String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
            database += filename.trim() + ";DriverID=22;READONLY=true}"; // add on to the end
            // now we can get the connection from the DriverManager
//        System.out.println(database);
            try (Connection con = DriverManager.getConnection(database);
				 Statement s = con.createStatement())
			{
//        s.execute("create table TEST12345 ( column_name integer )"); // create a table
//        s.execute("insert into TEST12345 values(1)"); // insert some data into the table
            s.execute("insert into TCS_SOAPReady_Messages (Element, MessageData)" + "values('" + element + "','" + message + "')"); // insert the data into the table'
//           s.execute("drop table TEST12345");
			}

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }

    public static List<String> getRequirements(SchemaParticle inputFrame) {
        List<String> reqList = new ArrayList();
        int listIndex = 0;
        QName reqName = new QName("http://www.tmdd.org/3/messages", "requirement");
        try {
            SchemaLocalElement element = (SchemaLocalElement) inputFrame;
            SchemaAnnotation inputFrameAnnotation = element.getAnnotation();
            XmlObject[] inputAnnotationInfo = inputFrameAnnotation.getUserInformation();
            for (int i = 0; i < inputAnnotationInfo.length; i++) {
                XmlObject[] requirementsList = inputAnnotationInfo[i].selectChildren(reqName);
                for (int j = 0; j < requirementsList.length; j++) {
                    reqList.add(requirementsList[j].newCursor().getTextValue());
//                    System.out.println(reqList.get(listIndex));
                    listIndex++;
                }
            }

        } catch (Exception ex) {
//            System.out.println(" No Requirement: " + ex.getMessage());
        }


        /**        SchemaParticle[] frameList = inputFrame.getParticleChildren();
        if (frameList != null) {
        for (int jj = 0; jj < frameList.length; jj++) {
        if (frameList[jj].getParticleType() != SchemaParticle.WILDCARD) {
        try {
        if (frameList[jj].getName().getLocalPart().equals("annotation")) {
        SchemaParticle[] annotationList = frameList[jj].getParticleChildren();
        for (int kk = 0; kk < annotationList.length; kk++) {
        if (annotationList[kk].getParticleType() != SchemaParticle.WILDCARD) {
        try {
        if (frameList[kk].getName().getLocalPart().equals("documentation")) {
        SchemaParticle[] documentationList = annotationList[kk].getParticleChildren();
        for (int ll = 0; ll < documentationList.length; ll++) {
        SchemaParticle[] requirementList = documentationList[ll].getParticleChildren();
        for (int mm=0; mm<requirementList.length; mm++){
        System.out.println("REQUIREMENT: "+ requirementList[mm].getDefaultText());
        }

        }
        }
        } catch (Exception ex) {
        }
        }
        }

        }
        System.out.println("         " + jj + ":  " + frameList[jj].getName().getLocalPart() + "  " + frameList[jj].getType().getName().getLocalPart() + "   " + (frameList[jj].getType().isSimpleType() ? " Element" : " Frame"));
        } catch (Exception ex) {
        System.out.println("         " + jj + ":  **** ERROR Couldn't handle this one !!!");
        }
        }
        }

        }
         */
        return reqList;

    }

    public static List<String> getRequirements(SchemaType inputType) {
        List<String> reqList = new ArrayList();
        int listIndex = 0;

        QName reqName = new QName("http://www.tmdd.org/3/messages", "requirement");
        try {
            SchemaAnnotation inputFrameAnnotation = inputType.getAnnotation();
            XmlObject[] inputAnnotationInfo = inputFrameAnnotation.getUserInformation();
            for (int i = 0; i < inputAnnotationInfo.length; i++) {
                XmlObject[] requirementsList = inputAnnotationInfo[i].selectChildren(reqName);
                for (int j = 0; j < requirementsList.length; j++) {
                    reqList.add(requirementsList[j].newCursor().getTextValue());
//                    System.out.println(reqList.get(listIndex));
                    listIndex++;
                }
            }

        } catch (Exception ex) {
//            System.out.println(" No Requirement: " + ex.getMessage());
        }
        return reqList;
    }
}
