/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import tmddv3verification.DataConcept;
import tmddv3verification.DesignDetail;
import tmddv3verification.Need;
import tmddv3verification.Predicate;
import tmddv3verification.Requirement;
import tmddv3verification.utilities.TMDDDatabase;

/**
 *
 * @author TransCore ITS
 */
public class MessageDetailDesignMatcher {

    private String message;
    private ArrayList<MessageDetailDesignElement> elementList = new ArrayList<MessageDetailDesignElement>();

    public MessageDetailDesignMatcher(String messageName) {
        this.message = messageName;

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

//        ResultSet needIDRS = theDatabase.queryReturnRS("Select distinct UNID, UserNeed, UNSelected from NRTM order by UNID");
        ResultSet messageElementRS = theDatabase.queryReturnRS("SELECT distinct Message, elementName, DataConcept, DataConceptType, Path, Prefix, ParentType,"+
                " BaseType, DataType, minOccurs, maxOccurs, minLength, maxLength, minInclusive, maxInclusive, enumeration, valueStored, value, "+
                " RequirementID, RequirementNumber, UNID, NeedNumber FROM DesignMessageElementDetailsQuery where Message ='" + messageName + "' order by RequirementNumber");
        try {
            while (messageElementRS.next()) {

                String message = messageElementRS.getString("Message");
                String elementName = messageElementRS.getString("elementName");
                String dataConcept = messageElementRS.getString("DataConcept");
                String dataConceptType = messageElementRS.getString("DataConceptType");
                String path = messageElementRS.getString("Path");
//                String path = "This Element was removed from the query in MessageDetailDesignMatcher";
                String prefix = messageElementRS.getString("Prefix");
                String parentType = messageElementRS.getString("ParentType");
                String baseType = messageElementRS.getString("BaseType");
                String dataType = messageElementRS.getString("DataType");
                String minOccurs = messageElementRS.getString("minOccurs");
                String maxOccurs = messageElementRS.getString("maxOccurs");
                String minLength = messageElementRS.getString("minLength");
                String maxLengh = messageElementRS.getString("maxLength");
                String minInclusive = messageElementRS.getString("minInclusive");
                String maxInclusive = messageElementRS.getString("maxInclusive");
                String enumeration = messageElementRS.getString("enumeration");
                String valueStored = messageElementRS.getString("valueStored");
                String value = messageElementRS.getString("value");
                String requirementID = "";
                Integer requirementListID = 0;
                String needID = "";
                Integer needListID = 0;

                try{
                    requirementID = messageElementRS.getString("RequirementID");
                    requirementListID = messageElementRS.getInt("RequirementNumber");
                    needID = messageElementRS.getString("UNID");
                    needListID = messageElementRS.getInt("NeedNumber");
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                
                MessageDetailDesignElement thisMessageDesignElement = new MessageDetailDesignElement();
                thisMessageDesignElement.setMessage(message);
                thisMessageDesignElement.setElementName(elementName);
                thisMessageDesignElement.setDataConcept(dataConcept);
                thisMessageDesignElement.setDataConceptType(dataConceptType);
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
                thisMessageDesignElement.setEnumeration(enumeration);
                thisMessageDesignElement.setValueStored(valueStored);
                thisMessageDesignElement.setValue(value);
                thisMessageDesignElement.setRequirementID(requirementID);
                thisMessageDesignElement.setRequirementListID(requirementListID);
                thisMessageDesignElement.setNeedID(needID);
                thisMessageDesignElement.setNeedListID(needListID);

                elementList.add(thisMessageDesignElement);
            }
            messageElementRS.close();
            messageElementRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        theDatabase.disconnectFromDatabase();


        // TODO code application logic here
        System.out.println("MessageDetailDesignMatcher: Number of Elements Added for message "+messageName+" = " + elementList.size());

    }

    public ArrayList<MessageDetailDesignElement> getMatchesForElementName(String elementName) {
        ArrayList<MessageDetailDesignElement> returnList = new ArrayList<MessageDetailDesignElement>();

        for (MessageDetailDesignElement thisElement : elementList) {
            if (thisElement.getElementName().equals(elementName)) {
                boolean alreadyThere = false;
                for (MessageDetailDesignElement thiselement:returnList){
                    if (thiselement.getElementName().equals(elementName)&&(thiselement.getNeedListID()==thisElement.getNeedListID())&&
                        thiselement.getRequirementID().equals(thisElement.getRequirementID())){
                        alreadyThere = true;
                    }
                }
                if (!alreadyThere)   
                returnList.add(thisElement);
            }
        }
        
// Sometimes the NRTM mapping contains mappings to Data Types instead of Data Elements.
// If we didn't find a match looking at the elements see if we can find matches with the types.
        if (returnList.size() <= 0) {
            for (MessageDetailDesignElement thisElement : elementList) {
                if (thisElement.getDataConcept().equals(elementName)) {
                boolean alreadyThere = false;
                for (MessageDetailDesignElement thiselement:returnList){
                    if (thiselement.getElementName().equals(elementName)&&(thiselement.getNeedListID()==thisElement.getNeedListID())&&
                        thiselement.getRequirementID().equals(thisElement.getRequirementID())){
                        alreadyThere = true;
                    }
                }
                if (!alreadyThere)   
                    returnList.add(thisElement);
                }
            }

        }

        return returnList;
    }

    public ArrayList<MessageDetailDesignElement> getMatchesForElementName(String elementName, Integer needNumber) {
        ArrayList<MessageDetailDesignElement> returnList = new ArrayList<MessageDetailDesignElement>();

        for (MessageDetailDesignElement thisElement : elementList) {
//            System.out.println("Is "+ thisElement.getElementName() + " = "+ elementName+ "? "+thisElement.getElementName().equals(elementName) +
//                               "   Is " + thisElement.getNeedListID() + " = " + needNumber + " ? "+(thisElement.getNeedListID()==needNumber));
            if (thisElement.getElementName().equals(elementName) && (thisElement.getNeedListID()==needNumber)) {
                boolean alreadyThere = false;
                for (MessageDetailDesignElement thiselement:returnList){
                    if (thiselement.getElementName().equals(elementName)&&(thiselement.getNeedListID()==needNumber)&&
                        thiselement.getRequirementID().equals(thisElement.getRequirementID())){
                        alreadyThere = true;
                    }
                }
                if (!alreadyThere)   
                returnList.add(thisElement);
            }
        }
        
// Sometimes the NRTM mapping contains mappings to Data Types instead of Data Elements.
// If we didn't find a match looking at the elements see if we can find matches with the types.
        if (returnList.size() <= 0) {
            for (MessageDetailDesignElement thisElement : elementList) {
                if (thisElement.getDataConcept().equals(elementName)) {
                boolean alreadyThere = false;
                for (MessageDetailDesignElement thiselement:returnList){
                    if (thiselement.getElementName().equals(elementName)&&(thiselement.getNeedListID()==needNumber)&&
                        thiselement.getRequirementID().equals(thisElement.getRequirementID())){
                        alreadyThere = true;
                    }
                }
                if (!alreadyThere&&(thisElement.getNeedListID()==needNumber))
                    returnList.add(thisElement);
                }
            }

        }

        return returnList;
    }

     public ArrayList<MessageDetailDesignElement> getMatchesForElementName(String elementName, Integer needNumber, String requirementID) {
        ArrayList<MessageDetailDesignElement> returnList = new ArrayList<MessageDetailDesignElement>();

        for (MessageDetailDesignElement thisElement : elementList) {
            System.out.println("MessageDetailDesignMatcher:getMatchesForElementName::  Element: "+thisElement.getElementName() + "   needNumber: "+thisElement.getNeedID()+"   requirementID: "+thisElement.getRequirementID());
            if (thisElement.getElementName().equals(elementName) && (thisElement.getNeedListID()==needNumber) &&
                    thisElement.getRequirementID().equals(requirementID)) {
                boolean alreadyThere = false;
                for (MessageDetailDesignElement thiselement:returnList){
                    if (thiselement.getElementName().equals(elementName)&&(thiselement.getNeedListID()==needNumber)&&
                        thiselement.getRequirementID().equals(requirementID)){
                        alreadyThere = true;
                    }
                }
                if (!alreadyThere)
                    returnList.add(thisElement);
            }
        }
        
// Sometimes the NRTM mapping contains mappings to Data Types instead of Data Elements.
// If we didn't find a match looking at the elements see if we can find matches with the types.
        if (returnList.size() <= 0) {
            for (MessageDetailDesignElement thisElement : elementList) {
                if (thisElement.getDataConcept().equals(elementName)) {
                boolean alreadyThere = false;
                for (MessageDetailDesignElement thiselement:returnList){
                    if (thiselement.getElementName().equals(elementName)&&(thiselement.getNeedListID()==needNumber)&&
                        thiselement.getRequirementID().equals(requirementID)){
                        alreadyThere = true;
                    }
                }
                if (!alreadyThere&&(thisElement.getNeedListID()==needNumber)&&thisElement.getRequirementID().equals(requirementID))
                    returnList.add(thisElement);
                }
            }
            System.err.println("***THIS IS WHAT YOU DON'T WANT!!!*****\nCheck elementName: "+elementName+" NeedNumber: "+needNumber + " RequirementID: "+requirementID+"\n Returned "+returnList.size() + " elements.");

        }

        return returnList;
    }

     public ArrayList<MessageDetailDesignElement> getMatchesForElementName(String elementName, String requirementID) {
        ArrayList<MessageDetailDesignElement> returnList = new ArrayList<MessageDetailDesignElement>();

        for (MessageDetailDesignElement thisElement : elementList) {
            if (thisElement.getElementName().equals(elementName) && thisElement.getRequirementID().equals(requirementID)) {
                boolean alreadyThere = false;
                for (MessageDetailDesignElement thiselement:returnList){
                    if (thiselement.getElementName().equals(elementName)&&
                        thisElement.getRequirementID().equals(requirementID)){
                        alreadyThere = true;
                    }
                }
                if (!alreadyThere)
                returnList.add(thisElement);
            }
        }
        
// Sometimes the NRTM mapping contains mappings to Data Types instead of Data Elements.
// If we didn't find a match looking at the elements see if we can find matches with the types.
        if (returnList.size() <= 0) {
            for (MessageDetailDesignElement thisElement : elementList) {
                if (thisElement.getDataConcept().equals(elementName)) {
                boolean alreadyThere = false;
                for (MessageDetailDesignElement thiselement:returnList){
                    if (thiselement.getElementName().equals(elementName)&&(thiselement.getNeedListID()==thisElement.getNeedListID())&&
                        thiselement.getRequirementID().equals(requirementID)){
                        alreadyThere = true;
                    }
                }
                if (!alreadyThere&&thisElement.getRequirementID().equals(requirementID))
                if (!alreadyThere)
                    returnList.add(thisElement);
                }
            }
            System.err.println("***THIS IS WHAT YOU DON'T WANT!!!*****\nCheck elementName: "+elementName+ " RequirementID: "+requirementID+"\n Returned "+returnList.size() + " elements.");

        }

        return returnList;
    }

    
    public static void main(String[] args) {
        // Test Out this Class
        ArrayList<MessageDetailDesignElement> responseList;

        MessageDetailDesignMatcher firstOne = new MessageDetailDesignMatcher("c2cMessageSubscription");
        responseList = firstOne.getMatchesForElementName("broadcastAlert");
        printResult(responseList);

        MessageDetailDesignMatcher secondOne = new MessageDetailDesignMatcher("deviceInformationRequestMsg");
        responseList = secondOne.getMatchesForElementName("center-type");
        printResult(responseList);
        responseList = secondOne.getMatchesForElementName("ContactDetails");
        printResult(responseList);
        responseList = secondOne.getMatchesForElementName("I bet you cant");
        printResult(responseList);

    }

    public static void printResult(ArrayList<MessageDetailDesignElement> reportList){
        if (reportList.size() == 0){
            System.out.println("Nothing returned!!");
        } else if (reportList.size() == 1){
            System.out.println("Returned "+reportList.get(0).getElementName()+" @ Path=> "+reportList.get(0).getFullVariableName()+" @Parent =>"+reportList.get(0).getFullParentVariableName());
        } else {
            System.out.println("!!! Multiples Returned !!!!");
            for (MessageDetailDesignElement thisElement : reportList){
                System.out.println("Returned "+thisElement.getElementName()+" @ Path=> "+thisElement.getFullVariableName()+" @Parent=> "+thisElement.getFullParentVariableName());
            }
        }

    }
}
