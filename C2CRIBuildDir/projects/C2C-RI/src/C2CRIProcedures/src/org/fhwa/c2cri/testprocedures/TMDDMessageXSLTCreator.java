/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.ArrayList;
import tmddv3verification.utilities.TMDDDatabase;

/**
 *
 * @author TransCore ITS, LLC
 */
public class TMDDMessageXSLTCreator {

    private static boolean tmddv301Flag = true;
    
    public static void main(String args[]) {
        TMDDMessageXSLTCreator xsltCreator = new TMDDMessageXSLTCreator();
        xsltCreator.makeTMDDMessageXSLT("C:\\Users\\c2cri\\Documents","TMDDMessageVerifyNew.xsl");        
    }

    public void makeTMDDMessageXSLT(String path, String fileName){

        try (Writer out = new OutputStreamWriter(new FileOutputStream(path + File.separatorChar + fileName), StandardCharsets.UTF_8))
		{
        
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<!--\n" +
"    Document   : C2CRIValueConversion.xsl\n" +
"    Created on : Jan 14, 2014, 3:21 PM\n" +
"    Author     : TransCore ITS\n" +
"    Description:\n" +
"        Purpose of transformation follows.\n" +
"-->\n" +
"<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" xmlns:tmdd=\"http://www.tmdd.org/301/messages\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:c2c=\"http://www.ntcip.org/c2c-message-administration\" xmlns:ext=\"http://exslt.org/common\">\n" +
"\t<xsl:output method=\"text\" version=\"1.0\" encoding=\"UTF-8\" indent=\"no\" doctype-public=\"yes\"/>\n" +
"\n" +
"\t<xsl:template match=\"node()\">\n" +
"\t\t<xsl:apply-templates select=\"node()\"/>\n" +
"\t</xsl:template>\t\n");
        
        for (String message : getMessageList()) {
            out.write(createMessageTemplateXML(message));
            System.out.println(message);
        }
        
        out.write("\t<xsl:template name=\"requirementHandler\">\n" +
"\t\t<xsl:param name=\"requirements\"/>\n" +
"\t\t<xsl:param name=\"message\"/>\n" +
"\t\t<xsl:for-each select=\"$requirements/requirement\">\n" +
"\t\t<xsl:variable name=\"requirementNumber\" select=\"@number\"/>\n" +
"\t\t<xsl:variable name=\"requirementPassed\" select=\"count(test[condition=&quot;false&quot;])=0\"/>\n" +
"\t\t<xsl:for-each select=\"test\">\n" +
"\t\t\t<xsl:variable name=\"conditionresult\" select=\"condition\"/>\n" +
"\t\t\t<xsl:choose>\n" +
"\t\t\t\t<xsl:when test=\"$conditionresult=&quot;false&quot;\"><xsl:value-of select=\"$requirementNumber\"/>,<xsl:value-of select=\"$requirementPassed\"/>,<xsl:value-of select=\"$message\"/>,<xsl:value-of select=\"elementName\"/>,false,<xsl:value-of select=\"errorDescription\"/>|\n" +
"</xsl:when>\n" +
"\t\t\t\t<xsl:otherwise>\t<xsl:value-of select=\"$requirementNumber\"/>,<xsl:value-of select=\"$requirementPassed\"/>,<xsl:value-of select=\"$message\"/>,<xsl:value-of select=\"elementName\"/>,true, \n" +
"</xsl:otherwise>\n" +
"\t\t\t</xsl:choose>\n" +
"\t\t</xsl:for-each>\n" +
"\t</xsl:for-each>\t\n" +
"\t</xsl:template>\n" +
"</xsl:stylesheet>");
        
//        System.out.println(results.toString());
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
   private String createMessageTemplateXML(String message){
       StringBuffer results = new StringBuffer();
       results.append("<xsl:template match=\"tmdd:"+message+"\">\n");
       results.append("   <xsl:variable name=\"requirement-mapper\">\n");
       for (String requirementID : getRequirementsList(message)) {
           results.append(createRequirementTestsXML(requirementID,getMessageDetailDesignElements(message,requirementID)));
       }       
       results.append("\t\t</xsl:variable>\n" +
"\t\t<xsl:variable name=\"requirements\" select=\"$requirement-mapper\"/>\n" +
"\t\t<xsl:call-template name=\"requirementHandler\">\n" +
"\t\t\t<xsl:with-param name=\"requirements\" select=\"$requirements\"/>\n" +
"\t\t\t<xsl:with-param name=\"message\" select=\"'"+message+"'\"/>\t\t\t\n" +
"\t\t</xsl:call-template>\n" +
"\t</xsl:template>\n");
       return results.toString();
   }
    
    private String createRequirementTestsXML(String requirementID, ArrayList<MessageDetailDesignElement> elements){
        StringBuffer results = new StringBuffer();
        String finalRequirementID = requirementID;
        if (tmddv301Flag) {
         TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

        ResultSet procedureVariableRS = theDatabase.queryReturnRS("SELECT * FROM RTMTov303RTMTable where [Req ID]='"+requirementID+"'");

        try {           
            while (procedureVariableRS.next()) {
                String v301Req = procedureVariableRS.getString("v301ReqID");
                String v303Req = procedureVariableRS.getString("Req ID");
                if (v303Req != null){
                    finalRequirementID = requirementID;
                    String before = finalRequirementID;
                finalRequirementID =  finalRequirementID.replace(v303Req, v301Req);
                    if (!before.equals(finalRequirementID))System.out.println("!!!!!Replaced v303c requirement "+v303Req + " with v301 requirement "+v301Req);
                }
                 }
//                     }            
//                }
        
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        theDatabase.disconnectFromDatabase();
       
        
        }
        
        
        
        results.append("      <requirement number = \""+finalRequirementID+"\">\n");
        
        for (MessageDetailDesignElement thisElement:elements){
        String fullPath = thisElement.getPath().replace("[*]","").replace("tmdd303", "tmdd");
        String fullParentPath = thisElement.getFullParentVariableName().replace("[*]","").replace("tmdd303", "tmdd");
        String conditionStatement;
        if (fullParentPath.equals("")){
            conditionStatement="true";
        } else {
            conditionStatement = (thisElement.getElementName().contains(".")?"(count("+fullPath+") - (count("+ fullParentPath+"))=0)":
                "count("+fullPath+")>0");
        }
        if ((thisElement.getValue() == null) || thisElement.getValue().isEmpty()){
        results.append("         <test>\n");
        results.append("            <elementName>"+thisElement.getElementName()+"</elementName>\n");
        results.append("            <condition type=\"presence\">\n");
        results.append("               <xsl:value-of select=\""+conditionStatement+"\"/>\n");
        results.append("            </condition>\n");
        results.append("            <errorDescription>Instance "+thisElement.getElementName()+" was not present in "+(fullParentPath.contains(".")?" each occurance of "+fullParentPath:fullParentPath)+"</errorDescription>\n");
        results.append("         </test>\n");            

        } else if ((thisElement.getValue()!=null) && !thisElement.getValue().isEmpty()){
            conditionStatement = (thisElement.getElementName().contains(".")?"(count("+fullPath+"='"+thisElement.getValue()+"') - (count("+ fullPath+"))=0)":
                fullPath+"='"+thisElement.getValue()+"'");

        results.append("         <test>\n");
        results.append("            <elementName>"+thisElement.getElementName()+"</elementName>\n");
        results.append("            <condition type=\"value\">\n");
        results.append("               <xsl:value-of select=\""+conditionStatement+"\"/>\n");
        results.append("            </condition>\n");
        results.append("            <errorDescription>Instance "+thisElement.getElementName()+" is not set to value "+thisElement.getValue()+(fullParentPath.contains(".")?" for each occurance of "+fullPath:"")+" as required.</errorDescription>\n");
        results.append("         </test>\n");            

            
            
        }
        }            
        
        
        results.append("</requirement>\n");
        
        return results.toString();
    } 
               
    
    
    private ArrayList<String> getMessageList() {
        ArrayList<String> messages = new ArrayList();
        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

//        ResultSet messageRS = theDatabase.queryReturnRS("SELECT distinct Instance FROM tempTestFileTable where DCType ='message' order by Instance");
//        ResultSet messageRS = theDatabase.queryReturnRS("SELECT distinct RTMDCInstanceName FROM TestFileUniqueRowForReviewQuery where DCType ='message' order by RTMDCInstanceName");
        ResultSet messageRS = theDatabase.queryReturnRS("SELECT distinct RTMDCInstanceName FROM NRTM_RTM_Schema_withID_Final where DCType ='message' order by RTMDCInstanceName");

        try {
            while (messageRS.next()) {
//                messages.add(messageRS.getString("Instance"));
                messages.add(messageRS.getString("RTMDCInstanceName"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        theDatabase.disconnectFromDatabase();
        return messages;
    }

    private ArrayList<String> getRequirementsList(String message) {
        ArrayList<String> requirements = new ArrayList();
        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

//        ResultSet messageRS = theDatabase.queryReturnRS("SELECT distinct RequirementID,NRTMID FROM tempTestFileTable where Message ='" + message + "' order by NRTMID");
//        ResultSet messageRS = theDatabase.queryReturnRS("SELECT distinct RequirementID FROM TestFileUniqueRowForReviewQuery where Message ='" + message + "'");
        ResultSet messageRS = theDatabase.queryReturnRS("SELECT distinct RequirementID FROM NRTM_RTM_Schema_withID_Final where Message ='" + message + "'");

        try {
            while (messageRS.next()) {
                if (message.equals("cCTVControlRequestMsg")){
                    System.out.println("Here we are...");
                }
                requirements.add(messageRS.getString("RequirementID"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        theDatabase.disconnectFromDatabase();
        return requirements;
    }
    
    private ArrayList<MessageDetailDesignElement> getMessageDetailDesignElements(String message, String requirementID) {
        ArrayList<MessageDetailDesignElement> elementList = new ArrayList();
        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

//        ResultSet messageElementRS = theDatabase.queryReturnRS("SELECT distinct RequirementID, Requirement, RequirementType, RTMID, Conformance, minOccurs, DCType, RTMDCInstanceName, RTMReferenceInstance, RTMReferenceParent, Instance, Reference, Path, SchemaMatch, ConformanceFlag, DataConcept, DataConceptType, ParentType, BaseType, maxOccurs, minLength, maxLength, minInclusive, maxInclusive, enumeration, valueStored, value, Instance, DCType FROM tempTestFileTable where Message ='" + message + "' and RequirementID='"+requirementID+"' order by RTMID");
//        ResultSet messageElementRS = theDatabase.queryReturnRS("SELECT distinct RequirementID, Requirement, RequirementType, RTMID, Conformance, SchemaminOccurs, DCType, RTMDCInstanceName, RTMReferenceInstance, RTMReferenceParent, Reference, Path, SchemaMatch, DataConcept, DataConceptType, ParentType, BaseType, maxOccurs, minLength, maxLength, minInclusive, maxInclusive, enumeration, valueStored, value, DCType FROM TestFileUniqueRowForReviewQuery where Message ='" + message + "' and RequirementID='"+requirementID+"' order by RTMID");
        ResultSet messageElementRS = theDatabase.queryReturnRS("SELECT distinct RequirementID, Requirement, RequirementType, RTMID, Conformance, minOccurs, DCType, RTMDCInstanceName, RTMReferenceInstance, RTMReferenceParentInstance, ReferenceDC, Path, DataConcept, DataConceptType, ParentType, BaseType, maxOccurs, minLength, maxLength, minInclusive, maxInclusive FROM NRTM_RTM_Schema_withID_Final where Message ='" + message + "' and [RequirementID]='"+requirementID+"' order by RTMID");
        try {
            while (messageElementRS.next()) {

//                String message = messageElementRS.getString("Message");
                String elementName = messageElementRS.getString("RTMDCInstanceName");
//                String dataConcept = messageElementRS.getString("DataConcept");
//                String dataConceptType = messageElementRS.getString("DataConceptType");
                String path = messageElementRS.getString("Path");
//                String prefix = messageElementRS.getString("Prefix");
                String prefix = "Prefix";
                String parentType = messageElementRS.getString("ParentType");
                String baseType = messageElementRS.getString("BaseType");
//                String dataType = messageElementRS.getString("DataType");
                String dataType = "TMDDMessageXSLTCreator::Don't know if Used!!";
                String minOccurs = messageElementRS.getString("minOccurs");
//                String minOccurs = messageElementRS.getString("SchemaMinOccurs");
                String maxOccurs = messageElementRS.getString("maxOccurs");
                String minLength = messageElementRS.getString("minLength");
                String maxLengh = messageElementRS.getString("maxLength");
                String minInclusive = messageElementRS.getString("minInclusive");
                String maxInclusive = messageElementRS.getString("maxInclusive");
//                String enumeration = messageElementRS.getString("enumeration");
//                String valueStored = messageElementRS.getString("valueStored");
//                String value = messageElementRS.getString("value");
//                String relatedRequirement = messageElementRS.getString("RelatedReqID");

                MessageDetailDesignElement thisMessageDesignElement = new MessageDetailDesignElement();
                thisMessageDesignElement.setMessage(message);
                thisMessageDesignElement.setElementName(elementName);
//                thisMessageDesignElement.setDataConcept(dataConcept);
//                thisMessageDesignElement.setDataConceptType(dataConceptType);
                thisMessageDesignElement.setPath(path);
                thisMessageDesignElement.setPrefix(prefix);
                thisMessageDesignElement.setParentType(parentType);
                thisMessageDesignElement.setBaseType(baseType);
                thisMessageDesignElement.setDataType(dataType);
                thisMessageDesignElement.setMinOccurs(minOccurs);
                thisMessageDesignElement.setMaxOccurs(maxOccurs);
                thisMessageDesignElement.setMinLength(minLength);
                thisMessageDesignElement.setMaxLengh(maxLengh);
                thisMessageDesignElement.setMinInclusive(minInclusive);
                thisMessageDesignElement.setMaxInclusive(maxInclusive);
//                thisMessageDesignElement.setEnumeration(enumeration);
//                thisMessageDesignElement.setValueStored(valueStored);
                // The stored value is actually related to a different requirement in TMDD
 //               thisMessageDesignElement.setValue("");
 //               thisMessageDesignElement.setRelatedRequirement(relatedRequirement);

                elementList.add(thisMessageDesignElement);
                
        if ((thisMessageDesignElement.getRelatedRequirement()== null) || thisMessageDesignElement.getRelatedRequirement().isEmpty()){        
 //       ResultSet relatedMessageElementRS = theDatabase.queryReturnRS("SELECT ID, RequirementID, Path, Element, ElementValue, RefReqID FROM tmpValueRequirementsToMsgQuery where Message ='" + message + "' and RefReqID='"+requirementID+"' order by ID");
            long start = System.currentTimeMillis();
            ResultSet relatedMessageElementRS = theDatabase.queryReturnRS("SELECT ID, RequirementID, Path, Element, ElementValue, RefReqID FROM TMDDRequiredMessageValuesTable where Message ='" + message + "' and RefReqID='"+requirementID+"' order by ID");
            System.out.println("Query took "+(System.currentTimeMillis()-start)+" ms to complete.");
         try {
            while (relatedMessageElementRS.next()) {

//                String message = messageElementRS.getString("Message");
                String relatedelementName = relatedMessageElementRS.getString("Element");
//                String relateddataConcept = relatedMessageElementRS.getString("DataConcept");
//                String relateddataConceptType = relatedMessageElementRS.getString("DataConceptType");
                String relatedpath = relatedMessageElementRS.getString("Path");
//                String prefix = messageElementRS.getString("Prefix");
//                String relatedprefix = "Prefix";
//                String relatedparentType = relatedMessageElementRS.getString("ParentType");
//                String relatedbaseType = relatedMessageElementRS.getString("BaseType");
//                String dataType = messageElementRS.getString("DataType");
//                String relateddataType = "TMDDMessageXSLTCreator::Don't know if Used!!";
//                String relatedminOccurs = relatedMessageElementRS.getString("minOccurs");
//                String relatedmaxOccurs = relatedMessageElementRS.getString("maxOccurs");
//                String relatedminLength = relatedMessageElementRS.getString("minLength");
//                String relatedmaxLengh = relatedMessageElementRS.getString("maxLength");
//                String relatedminInclusive = relatedMessageElementRS.getString("minInclusive");
//                String relatedmaxInclusive = relatedMessageElementRS.getString("maxInclusive");
//                String relatedenumeration = relatedMessageElementRS.getString("enumeration");
//                String relatedvalueStored = relatedMessageElementRS.getString("valueStored");
                String relatedvalue = relatedMessageElementRS.getString("ElementValue");
//                String relatedrelatedRequirement = relatedMessageElementRS.getString("RelatedReqID");

                MessageDetailDesignElement thisRelatedMessageDesignElement = new MessageDetailDesignElement();
//                thisRelatedMessageDesignElement.setMessage(relatedmessage);
                thisRelatedMessageDesignElement.setElementName(relatedelementName);
//                thisRelatedMessageDesignElement.setDataConcept(relateddataConcept);
//                thisRelatedMessageDesignElement.setDataConceptType(relateddataConceptType);
                thisRelatedMessageDesignElement.setPath(relatedpath);
//                thisRelatedMessageDesignElement.setPrefix(relatedprefix);
//                thisRelatedMessageDesignElement.setParentType(relatedparentType);
//                thisRelatedMessageDesignElement.setBaseType(relatedbaseType);
//                thisRelatedMessageDesignElement.setDataType(relateddataType);
//                thisRelatedMessageDesignElement.setMinOccurs(relatedminOccurs);
//                thisRelatedMessageDesignElement.setMaxOccurs(relatedmaxOccurs);
//                thisRelatedMessageDesignElement.setMinLength(relatedminLength);
//                thisRelatedMessageDesignElement.setMaxLengh(relatedmaxLengh);
//                thisRelatedMessageDesignElement.setMinInclusive(relatedminInclusive);
//                thisRelatedMessageDesignElement.setMaxInclusive(relatedmaxInclusive);
//                thisRelatedMessageDesignElement.setEnumeration(relatedenumeration);
//                thisRelatedMessageDesignElement.setValueStored(relatedvalueStored);
                thisRelatedMessageDesignElement.setValue(relatedvalue);
//                thisRelatedMessageDesignElement.setRelatedRequirement(relatedrelatedRequirement);
                
                elementList.add(thisRelatedMessageDesignElement);
            }
            relatedMessageElementRS.close();
            relatedMessageElementRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
            }                
                
            }
            messageElementRS.close();
            messageElementRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        
        
        theDatabase.disconnectFromDatabase();
        
       return elementList;
    }    

}
