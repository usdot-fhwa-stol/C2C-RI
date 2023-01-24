/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.infolayer;

import test.transports.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.TokenType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.xpathgen.XPathGenerator;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;

/**
 * The Class MessageSpecification represents the set of message elements within a message.  The order of the 
 * elements matters.
 *
 * In addition elements from one message part must be kept together as a group.  The different message parts are
 * represented by having different prefixes.
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class MessageSpecification {

    /** The errors found. */
    private ArrayList<String> errorsFound = new ArrayList<String>();
    
    /** The message spec. */
    private ArrayList<MessageSpecificationItem> messageSpec = new ArrayList<MessageSpecificationItem>();

    /**
     * Instantiates a new message specification.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param messageSpecification the message specification
     */
    public MessageSpecification(ArrayList<String> messageSpecification) {
        for (String thisItem : messageSpecification) {
            try {
                MessageSpecificationItem msi = new MessageSpecificationItem(thisItem);
                messageSpec.add(msi);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Instantiates a new message specification.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param messageSpecification the message specification
     */
    public MessageSpecification(String messageSpecification) {
        String[] messageArray = messageSpecification.split("\n");
        for (int ii = 0; ii < messageArray.length; ii++) {
            try {
                MessageSpecificationItem msi = new MessageSpecificationItem(messageArray[ii]);
                messageSpec.add(msi);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Gets the instances of.
     *
     * @param messageSpec the message spec
     * @param messageSpecList the message spec list
     * @return the instances of
     */
    private HashMap<String, ArrayList<String>> getInstancesOf(String messageSpec, ArrayList<MessageSpecificationItem> messageSpecList) {
        HashMap<String, ArrayList<String>> returnMap = new HashMap<String, ArrayList<String>>();

        for (MessageSpecificationItem thisMessageElement : messageSpecList) {
            if (isInstanceof(thisMessageElement.getValueName(), messageSpec)) {
                String instanceName = getInstanceName(messageSpec, thisMessageElement.getValueName());
                if (returnMap.containsKey(instanceName)) {
                    returnMap.get(instanceName).add(thisMessageElement.getValueName());
                } else {
                    ArrayList<String> returnList = new ArrayList<String>();
                    returnList.add(thisMessageElement.getValueName());
                    returnMap.put(instanceName, returnList);
                }

            }
        }

        return returnMap;
    }

    /**
     * Gets the instances of.
     *
     * @param messageSpecName the message spec name
     * @return the instances of
     */
    private ArrayList<MessageSpecificationItem> getInstancesOf(String messageSpecName) {
        ArrayList<MessageSpecificationItem> returnList = new ArrayList<MessageSpecificationItem>();

        for (MessageSpecificationItem thisMessageElement : messageSpec) {
            if (isInstanceof(messageSpecName, thisMessageElement.getValueName())) {
                returnList.add(thisMessageElement);
            }
        }

        return returnList;
    }

    /**
     * Checks whether an element exists in every instance of a defined parent element within a message.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dataConceptInstanceName the data concept instance name
     * @param messageInstancesMap the message instances map
     * @return true, if successful
     */
    private boolean existsInEveryInstance(String dataConceptInstanceName, HashMap<String, ArrayList<String>> messageInstancesMap) {
        boolean results = true;

        Iterator instanceIterator = messageInstancesMap.keySet().iterator();
        errorsFound.clear();
        while (instanceIterator.hasNext()) {
            String thisInstance = (String) instanceIterator.next();
            ArrayList<String> instanceElements = messageInstancesMap.get(thisInstance);
            boolean foundMatchForInstance = false;
            for (String thisElement : instanceElements) {
                // Strip out the intance part and see if the element exists
                String strippedInstance = thisElement.substring(thisInstance.length());

                if (strippedInstance.startsWith(".")) {  // remove the leading "."
                    strippedInstance = strippedInstance.substring(".".length());
                }

                if (strippedInstance.contains(".")) {  // Strip off any additional elemements
                    strippedInstance = strippedInstance.substring(0, strippedInstance.indexOf("."));
                }

                if (strippedInstance.contains("[")) {  // Strip off any additional content includeing the index
                    strippedInstance = strippedInstance.substring(0, strippedInstance.indexOf("["));
                }

                if (strippedInstance.equals(dataConceptInstanceName)) {
                    foundMatchForInstance = true;
                    break;
                }


            }

            if (!foundMatchForInstance) {
                errorsFound.add("Unable to find " + dataConceptInstanceName + " in " + thisInstance + ".");
                results = false;
            }
        }

        return results;
    }

    /**
     * Checks whether an element or alternate element exists in every instance of a defined parent element within a message.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dataConceptInstanceName the data concept instance name
     * @param messageInstancesMap the message instances map
     * @return true, if successful
     */
    private boolean existsInEveryInstance(String dataConceptInstanceName, String alternateDataConceptInstanceName, HashMap<String, ArrayList<String>> messageInstancesMap) {
        boolean results = true;

        Iterator instanceIterator = messageInstancesMap.keySet().iterator();
        errorsFound.clear();
        while (instanceIterator.hasNext()) {
            String thisInstance = (String) instanceIterator.next();
            ArrayList<String> instanceElements = messageInstancesMap.get(thisInstance);
            boolean foundMatchForInstance = false;
            for (String thisElement : instanceElements) {
                // Strip out the intance part and see if the element exists
                String strippedInstance = thisElement.substring(thisInstance.length());

                if (strippedInstance.startsWith(".")) {  // remove the leading "."
                    strippedInstance = strippedInstance.substring(".".length());
                }

                if (strippedInstance.contains(".")) {  // Strip off any additional elemements
                    strippedInstance = strippedInstance.substring(0, strippedInstance.indexOf("."));
                }

                if (strippedInstance.contains("[")) {  // Strip off any additional content includeing the index
                    strippedInstance = strippedInstance.substring(0, strippedInstance.indexOf("["));
                }

                if (strippedInstance.equals(dataConceptInstanceName)||strippedInstance.equals(alternateDataConceptInstanceName)) {
                    foundMatchForInstance = true;
                    break;
                }


            }

            if (!foundMatchForInstance) {
                errorsFound.add("Unable to find " + dataConceptInstanceName +" or " + alternateDataConceptInstanceName+ " in " + thisInstance + ".");
                results = false;
            }
        }

        return results;
    }

    /**
     * Checks whether any one of an array of element exists in every instance of a defined parent element within a message.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dataConceptInstanceName the data concept instance name
     * @param messageInstancesMap the message instances map
     * @return true, if successful
     */
    private boolean existsInEveryInstance(String[] dataConceptInstanceName, HashMap<String, ArrayList<String>> messageInstancesMap) {
        boolean results = true;

        StringBuilder sb = new StringBuilder();
        
        Iterator instanceIterator = messageInstancesMap.keySet().iterator();
        errorsFound.clear();
        while (instanceIterator.hasNext()) {
            String thisInstance = (String) instanceIterator.next();
            ArrayList<String> instanceElements = messageInstancesMap.get(thisInstance);
            boolean foundMatchForInstance = false;
            for (String thisElement : instanceElements) {
                // Strip out the instance part and see if the element exists
                String strippedInstance = thisElement.substring(thisInstance.length());

                if (strippedInstance.startsWith(".")) {  // remove the leading "."
                    strippedInstance = strippedInstance.substring(".".length());
                }

                if (strippedInstance.contains(".")) {  // Strip off any additional elemements
                    strippedInstance = strippedInstance.substring(0, strippedInstance.indexOf("."));
                }

                if (strippedInstance.contains("[")) {  // Strip off any additional content includeing the index
                    strippedInstance = strippedInstance.substring(0, strippedInstance.indexOf("["));
                }

                for (int ii = 0; ii<dataConceptInstanceName.length; ii++){
                    if (strippedInstance.equals(dataConceptInstanceName[ii])) {
                        foundMatchForInstance = true;
                        break;
                    }
                }
                // If we've already found a match then skip to the next instance
                if (foundMatchForInstance) break;

            }

            if (!foundMatchForInstance) {
                if (sb.length() == 0){
                    for (int ii = 0; ii < dataConceptInstanceName.length; ii++){
                        sb.append(sb.length()==0?dataConceptInstanceName[ii]:sb.append(",").append(dataConceptInstanceName[ii]));
                    }
                }
                errorsFound.add("Unable to find any of " + sb.toString() + " in " + thisInstance + ".");
                results = false;
            }
        }

        return results;
    }
    
    /**
     * Gets the instance name.
     *
     * @param messageSpec the message spec
     * @param messageSpecInstance the message spec instance
     * @return the instance name
     */
    private String getInstanceName(String messageSpec, String messageSpecInstance) {
        String instanceName = "";

        // process the message spec instance
        String[] specInstanceList = messageSpecInstance.split("\\.");

        // check the instance against the spec
        String[] specList = messageSpec.split("\\.");

        if (specInstanceList.length > specList.length) {
            for (int ii = 0; ii < specList.length; ii++) {
                if (instanceName.equals("")) {
                    instanceName = instanceName.concat(specInstanceList[ii]);
                } else {
                    instanceName = instanceName.concat("." + specInstanceList[ii]);
                }
            }
        }

        return instanceName;
    }
//    /**
//     *
//     * @param messageSpec
//     * @param messageSpecInstance
//     * @return
//     */
//    private boolean isInstanceof(String messageSpec, String messageSpecInstance) {
//        boolean instanceMatch = true;
//
//        // process the message spec instance
//        String[] specInstanceList = messageSpecInstance.split("\\.");
//
//        // check the instance against the spec
//        String[] specList = messageSpec.split("\\.");
//
//        if (specInstanceList.length > specList.length) {
//            for (int ii = 0; ii < specList.length; ii++) {
//                if (!specInstanceList[ii].startsWith(specList[ii])) {
//                    instanceMatch = false;
//                    break;
//                }
//            }
//        } else {
//            instanceMatch = false;
//        }
//
//        return instanceMatch;
//    }
    /** The exact match. */
private static int EXACT_MATCH = 0;
    
    /** The no match. */
    private static int NO_MATCH = -1;
    
    /** The equivalent match. */
    private static int EQUIVALENT_MATCH = 2;
    
    /** The relative match. */
    private static int RELATIVE_MATCH = 3;

    /**
     * Checks if is instanceof.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param inputInstance the input instance
     * @param compareToInstance the compare to instance
     * @return true, if is instanceof
     */
    public boolean isInstanceof(String inputInstance, String compareToInstance) {
        boolean instanceMatch = true;

        // process the message spec instance
        String[] compareToInstancePartsList = compareToInstance.split("\\.");

        // check the instance against the spec
        String[] inputInstancePartsList = inputInstance.split("\\.");

        // Make sure the compareToInstance has at least as many parts as the inputInstance
        // Example:  part1.part2 (inputInstance) can not be an instance of  part1.part2.part3 (compareToInstance)
        if (compareToInstancePartsList.length <= inputInstancePartsList.length) {
            for (int ii = 0; ii < compareToInstancePartsList.length; ii++) {
                int result = compareParts(inputInstancePartsList[ii], compareToInstancePartsList[ii]);
                if (result == NO_MATCH) {
                    instanceMatch = false;
                    break;
                }
            }
        } else {
            instanceMatch = false;
        }

        return instanceMatch;
    }

    /**
     * Compare parts.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param inputPart the input part
     * @param compareToPart the compare to part
     * @return the int
     */
    private int compareParts(String inputPart, String compareToPart) {
        if (inputPart.trim().equals(compareToPart.trim())) {
            return EXACT_MATCH;
        } else {
            // Check to see if the Parts don't match First.
            if (!getPartName(inputPart).equals(getPartName(compareToPart))) {
                return NO_MATCH;
            } else {
                // Check indexes and wildcards
                String index = getPartIndex(inputPart);

                // Wildcard -- any index is a match
                if (index.equals("*")) {
                    return RELATIVE_MATCH;
                    // No Index -- matches an index of 1
                } else if (index.equals("")) {
                    if (getPartIndex(compareToPart).equals("1")) {
                        return EXACT_MATCH;
                    }
                } else if (index.equals("1")) {
                    if (getPartIndex(compareToPart).equals("")) {
                        return EXACT_MATCH;
                    }
                    // Indexes exactly match
                } else if (index.equals(getPartIndex(compareToPart))) {
                    return EXACT_MATCH;
                } else {
                    return NO_MATCH;
                }

            }
        }
        return NO_MATCH;
    }

    /**
     * Gets the part name.
     *
     * @param part the part
     * @return the part name
     */
    private String getPartName(String part) {
        if (part.contains(("["))) {
            int index = part.indexOf("[");
            if (index > 0) {
                return part.substring(0, index);
            }
            return "";
        } else {
            return part;
        }
    }

    /**
     * Gets the part index.
     *
     * @param part the part
     * @return the part index
     */
    private String getPartIndex(String part) {
        if (part.contains(("["))) {
            int startIndex = part.indexOf("[");
            int endIndex = part.indexOf("]");
            String index = part.substring(startIndex + 1, endIndex);
            if (index.length() > 0) {
                return index.trim();
            } else {
                return "";
            }

        } else {
            return "";
        }

    }

 /**
 * Verify exists in every instance.
 * 
 * Pre-Conditions: N/A
 * Post-Conditions: N/A
 *
 * @param elementName the element name
 * @param instanceName the instance name
 * @return true, if successful
 */
    public boolean verifyExistsInEveryInstance(String elementName, String instanceName) {
        HashMap<String, ArrayList<String>> instanceMap = getInstancesOf(instanceName, messageSpec);
        if (instanceMap.isEmpty()){
           errorsFound.add("Unable to find " + instanceName + ".");     
           return false;
        }
        return existsInEveryInstance(elementName, instanceMap);
    }

 /**
 * Verify exists in every instance.
 * 
 * Pre-Conditions: N/A
 * Post-Conditions: N/A
 *
 * @param elementName the element name
 * @param alternateElementName the alternate element name that is valid
 * @param instanceName the instance name
 * @return true, if successful
 */
    public boolean verifyExistsInEveryInstance(String elementName, String alternateElementName, String instanceName) {
        HashMap<String, ArrayList<String>> instanceMap = getInstancesOf(instanceName, messageSpec);
        return existsInEveryInstance(elementName, alternateElementName, instanceMap);
    }
 
 /**
 * Verify exists in every instance.
 * 
 * Pre-Conditions: N/A
 * Post-Conditions: N/A
 *
 * @param elementName the element name
 * @param instanceName the instance name
 * @return true, if successful
 */
    public boolean verifyExistsInEveryInstance(String[] elementName, String instanceName) {
        HashMap<String, ArrayList<String>> instanceMap = getInstancesOf(instanceName, messageSpec);
        return existsInEveryInstance(elementName, instanceMap);
    }    
    /**
     * Element exists in specification.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param element the element
     * @return true, if successful
     */
    public boolean elementExistsInSpecification(String element) {
        for (MessageSpecificationItem thisMessageElement : messageSpec) {
            if (element.equals(thisMessageElement.getValueName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the element value.
     *
     * @param element the element
     * @return the element value
     */
    public String getElementValue(String element) {
        for (MessageSpecificationItem thisMessageElement : messageSpec) {
            if (!element.startsWith("*")) {
                if (element.equals(thisMessageElement.getValueName())) {
                    return thisMessageElement.getValue();
                }
            } else {  // Wildcard Message Name - search the remainder of the message specification
                if (thisMessageElement.getValueName().contains(element.substring(1))) {
                    return thisMessageElement.getValue();
                }
            }
        }
        return "";
    }

    /**
     * Sets the element value.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param element the element
     * @param value the value
     */
    public void setElementValue(String element, String value) {
        for (MessageSpecificationItem thisMessageElement : messageSpec) {
            if (element.equals(thisMessageElement.getValueName())) {
                thisMessageElement.setValue(value);
            }
        }
    }

    /**
     * Adds the element to spec.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param elementSpec the element spec
     */
    public void addElementToSpec(String elementSpec) {
        try {
            MessageSpecificationItem msi = new MessageSpecificationItem(elementSpec);
            messageSpec.add(msi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Removes the element from spec.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param element the element
     */
    public void removeElementFromSpec(String element) {
        for (MessageSpecificationItem thisMessageElement : messageSpec) {
            if (element.equals(thisMessageElement.getValueName())) {
                messageSpec.remove(thisMessageElement);
                break;
            }
        }

    }

    /**
     * Contains message of type.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param messageType the message type
     * @return true, if successful
     */
    public boolean containsMessageOfType(String messageType) {
        boolean result = false;

        for (MessageSpecificationItem messageSpecElement : messageSpec) {
            String[] messageElements = messageSpecElement.getValueName().split("\\.");
            if ((messageElements.length > 0) && (messageElements[0].equals(messageType))) {
                result = true;
                break;
            }
        }

        return result;
    }

    /**
     * Gets the message types.
     *
     * @return the message types
     */
    public ArrayList<String> getMessageTypes() {
        ArrayList<String> result = new ArrayList<String>();

        for (MessageSpecificationItem messageSpecElement : messageSpec) {
            String[] messageElements = messageSpecElement.getValueName().split("\\.");
            if ((messageElements.length > 0) && (!result.contains(messageElements[0]))) {
                result.add(messageElements[0]);
            }
        }

        return result;
    }


    /**
 * Gets the errors found.
 *
 * @return the errors found
 */
    public ArrayList<String> getErrorsFound() {
        return errorsFound;
    }

 /**
 * Clear errors previously found.
 *
 */
    public void clearErrorsFound() {
        errorsFound.clear();
    }
    
    /**
     * Gets the message spec.
     *
     * @return the message spec
     */
    public ArrayList<String> getMessageSpec() {
        ArrayList<String> returnArray = new ArrayList<String>();
        for (MessageSpecificationItem thisItem : messageSpec) {
            returnArray.add(thisItem.getValueName() + " = " + thisItem.getValue());
        }
        return returnArray;
    }

    /**
     * Gets the message spec items.
     *
     * @return the message spec items
     */
    public ArrayList<MessageSpecificationItem> getMessageSpecItems() {
        return messageSpec;
    }

    /**
     * Update specification optional flags.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param messageNRTMList the message nrtm list
     */
    public void updateSpecificationOptionalFlags(MessageNRTMList messageNRTMList) {
        if (messageNRTMList != null) {
            for (MessageNRTMItem thisItem : messageNRTMList.getMessageNRTMList()) {
                if (!thisItem.isSelected()) {
                    for (MessageSpecificationItem thisSpecItem : messageSpec) {
                        if (isInstanceof(thisSpecItem.getValueName(), thisItem.getParentInstance())) {
                            if (thisSpecItem.getValueName().contains("." + thisItem.getElement()) || thisSpecItem.getValueName().endsWith("." + thisItem.getElement())) {
                                System.out.println("Setting Project Optional True for :" + thisSpecItem.getValueName());
                                thisSpecItem.setProjectOptional(true);
                            } else {
                                System.out.println("Leaving Project Optional False for :" + thisSpecItem.getValueName());
                            }
                        }
                    }
                }
            }
        }
    }

    
    /**
     * Gets all values assigned to instances of given message element.
     *
     * @param messageSpecName the message element
     * @return the values associated with the the instances found
     */
    public ArrayList<String> getValuesOfInstances(String messageSpecName) {
        ArrayList<String> returnList = new ArrayList<String>();

        for (MessageSpecificationItem thisMessageElement : messageSpec) {
            if (isInstanceof(messageSpecName, thisMessageElement.getValueName())) {
                returnList.add(thisMessageElement.getValue());
            }
        }

        return returnList;
    }
    
    
    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

//        String xmlFileName = "c:/inout/tmdd.xml";
//        String xmlFileName = "c:/c2cri/testfiles/deviceinformationrequestmsg.xml";
        String xmlFileName = "c:/c2cri/testfiles/deviceinventorysubscription.xml";

        ArrayList<String> messageSpecList = new ArrayList<String>();
        try {
            XmlOptions newXmlOptions = new XmlOptions();
            newXmlOptions.setLoadStripWhitespace();
            XmlObject thisObject1 = XmlObject.Factory.parse(new File(xmlFileName), newXmlOptions);
            XmlCursor thisCursor1 = thisObject1.newCursor();
            thisCursor1.selectPath("./messageElement/messagElement1[1]/messageElement24");
            System.out.println(thisCursor1.getSelectionCount());
            System.out.println(thisObject1.toString());
            System.out.println(thisObject1.xmlText(newXmlOptions).replace("\n", ""));
            thisCursor1.clearSelections();
            thisCursor1.dispose();

            System.out.println("\n\n");

            XmlCursor thisCursor2 = thisObject1.newCursor();
            while (thisCursor2.hasNextToken()) {
                TokenType thisToken = thisCursor2.toNextToken();


                if (thisToken.isStart()) {
                    thisCursor2.push();

                    String elementPath = XPathGenerator.generateXPath(thisCursor2, null, new TempMessageNameSpaceContext());

                    thisCursor2.pop();
                    thisCursor2.toNextToken();
                    if (!thisCursor2.getChars().trim().equals("")) {
                        String originalPath = elementPath;
                        elementPath = elementPath.substring(elementPath.lastIndexOf(":") + 1).replace("/", ".");
                        if (elementPath.startsWith(".")) {
                            elementPath = elementPath.substring(1);
                        }


                        originalPath = originalPath.substring(originalPath.lastIndexOf("/", originalPath.lastIndexOf(":") - 1) + 1).replace("/", ".");
                        messageSpecList.add(originalPath + " = " + thisCursor2.getTextValue());
                        System.out.println(originalPath + " = " + thisCursor2.getTextValue());

                    }
                }

            }
            thisCursor2.dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        MessageSpecification thisSpecification = new MessageSpecification(messageSpecList);

        System.out.println("\n\n\n");

        System.out.println("\n\n\n");
        System.out.println("Exists in Every Instance?");
        System.out.println("device-type  " + thisSpecification.verifyExistsInEveryInstance("device-type", "tmdd:deviceInformationRequestMsg"));
        System.out.println("device-type2  " + thisSpecification.verifyExistsInEveryInstance("device-type2", "tmdd:deviceInformationRequestMsg"));
        System.out.println("device-filter  " + thisSpecification.verifyExistsInEveryInstance("device-filter", "tmdd:deviceInformationRequestMsg"));
        System.out.println("device-filter2  " + thisSpecification.verifyExistsInEveryInstance("device-filter2", "tmdd:deviceInformationRequestMsg"));

        System.out.println("\n\n\n");
        System.out.println(thisSpecification.getErrorsFound());

        if (thisSpecification.elementExistsInSpecification("actionLogMsg.log-entry[1].action-time.offset")) {
            System.out.println("Current Value = " + thisSpecification.getElementValue("actionLogMsg.log-entry[1].action-time.offset"));
            thisSpecification.setElementValue("actionLogMsg.log-entry[1].action-time.offset", "NEWVALUE");
            System.out.println("After Set Value to \"NEWVALUE\"= " + thisSpecification.getElementValue("actionLogMsg.log-entry[1].action-time.offset"));
        } else {
            System.out.println("Couldn't find element " + "actionLogMsg.log-entry[1].action-time.offset" + " in the message Spec!!!");
        }

        if (thisSpecification.elementExistsInSpecification("XactionLogMsg.log-entry[1].action-time.offset")) {
            System.out.println("Current Value = " + thisSpecification.getElementValue("XactionLogMsg.log-entry[1].action-time.offset"));
            thisSpecification.setElementValue("XactionLogMsg.log-entry[1].action-time.offset", "NEWVALUE");
            System.out.println("After Set Value to \"NEWVALUE\"= " + thisSpecification.getElementValue("XactionLogMsg.log-entry[1].action-time.offset"));
        } else {
            System.out.println("Couldn't find element " + "XactionLogMsg.log-entry[1].action-time.offset" + " in the message Spec!!!");
        }

        thisSpecification.addElementToSpec("XactionLogMsg.log-entry[1].action-time.offset = 55");
        if (thisSpecification.elementExistsInSpecification("XactionLogMsg.log-entry[1].action-time.offset")) {
            System.out.println("Current Value = " + thisSpecification.getElementValue("XactionLogMsg.log-entry[1].action-time.offset"));
            thisSpecification.setElementValue("XactionLogMsg.log-entry[1].action-time.offset", "NEWVALUE");
            System.out.println("After Set Value to \"NEWVALUE\"= " + thisSpecification.getElementValue("XactionLogMsg.log-entry[1].action-time.offset"));
        } else {
            System.out.println("Couldn't find element " + "XactionLogMsg.log-entry[1].action-time.offset" + " in the message Spec!!!");
        }

        thisSpecification.removeElementFromSpec("XactionLogMsg.log-entry[1].action-time.offset");
        if (thisSpecification.elementExistsInSpecification("XactionLogMsg.log-entry[1].action-time.offset")) {
            System.out.println("Current Value = " + thisSpecification.getElementValue("XactionLogMsg.log-entry[1].action-time.offset"));
            thisSpecification.setElementValue("XactionLogMsg.log-entry[1].action-time.offset", "NEWVALUE");
            System.out.println("After Set Value to \"NEWVALUE\"= " + thisSpecification.getElementValue("XactionLogMsg.log-entry[1].action-time.offset"));
        } else {
            System.out.println("Couldn't find element " + "XactionLogMsg.log-entry[1].action-time.offset" + " in the message Spec!!!");
        }

        System.out.println("Does spec contain a message of type errorReportMsg? " + thisSpecification.containsMessageOfType("errorReportMsg"));
        System.out.println("Does spec contain a message of type c2cMessagePublication? " + thisSpecification.containsMessageOfType("c2cMessagePublication"));
        System.out.println("Does spec contain a message of type deviceInformationRequestMsg? " + thisSpecification.containsMessageOfType("deviceInformationRequestMsg"));
        System.out.println("Does spec contain a message of type actionLogMsg? " + thisSpecification.containsMessageOfType("actionLogMsg"));

        System.out.println("Wildcard Value Match = " + thisSpecification.isInstanceof("tmdd:part1[*].part2[*]", "tmdd:part1[3].part2"));
        System.out.println("Wildcard Value MisMatch = " + thisSpecification.isInstanceof("tmdd:part1[*].part2[*]", "tmdd:part1[3].part2.part3"));
        System.out.println("Default Index Value Match = " + thisSpecification.isInstanceof("tmdd:part1.part2", "tmdd:part1[1].part2[1]"));
        System.out.println("Default Index Value MisMatch = " + thisSpecification.isInstanceof("tmdd:part1.part2[2]", "tmdd:part1[1].part2"));
        System.out.println("Default Index Value - Reverse Match = " + thisSpecification.isInstanceof("tmdd:part1[1].part2[1]", "tmdd:part1.part2"));
        System.out.println("Default Index Value - Reverse MisMatch = " + thisSpecification.isInstanceof("tmdd:part1[1].part2[2]", "tmdd:part1.part2"));
        System.out.println("Specific Index Value Match = " + thisSpecification.isInstanceof("tmdd:part1[2].part2[4]", "tmdd:part1[2].part2[4]"));
        System.out.println("Specific Value MisMatch = " + thisSpecification.isInstanceof("tmdd:part1[2].part2[4]", "tmdd:part1[2].part2[3]"));
        System.out.println("Specific Index and Wildcard Value Match = " + thisSpecification.isInstanceof("tmdd:part1[*].part2[3]", "tmdd:part1[3].part2[3]"));
        System.out.println("Specific Index and Wildcard Value MisMatch = " + thisSpecification.isInstanceof("tmdd:part1[*].part2[3]", "tmdd:part1[3].part2[2]"));
        System.out.println("All Three Match = " + thisSpecification.isInstanceof("tmdd:part1[*].part2.part3[4]", "tmdd:part1[3].part2[1].part3[4]"));
        System.out.println("All Three MisMatch = " + thisSpecification.isInstanceof("tmdd:part1[*].part2.part3[4]", "tmdd:part1[3].part2[2].part3[5]"));
        System.out.println("All Three - Extended Match = " + thisSpecification.isInstanceof("tmdd:part1[*].part2.part3[4].part5", "tmdd:part1[3].part2[1].part3[4]"));
        System.out.println("All Three - Extended MisMatch = " + thisSpecification.isInstanceof("tmdd:part1[*].part2.part3[4].part5", "tmdd:part1[3].part2[2].part3[5]"));


        System.out.println("\n\n\n");

        ValueSpecification mvs = new ValueSpecification("tmdd:deviceInformationRequestMsg.organization-requesting.center-contact-list[*].center-contact-details[*].center-type = 1,1,2");
        System.out.println("Result of the first value test = " + MessageValueTester.performValueTest(mvs, thisSpecification) + " -->" + MessageValueTester.getErrorList());
        System.out.println("\n");
        mvs.addValueSpecification("tmdd:deviceInformationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-type = 1,1,3");
        System.out.println("Result of the second value test = " + MessageValueTester.performValueTest(mvs, thisSpecification) + " -->" + MessageValueTester.getErrorList());
        System.out.println("\n");

        mvs.addValueSpecification("tmdd:deviceInformationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-type = 1,3,1,2,3");
        System.out.println("Result of the third value test = " + MessageValueTester.performValueTest(mvs, thisSpecification) + " -->" + MessageValueTester.getErrorList());
        System.out.println("\n");


        MessageManager theManager = MessageManager.getInstance();
        try {
            Message newMessage = theManager.createMessage("SAMPLE");
            newMessage.setMessageBody(new String("Fake Body").getBytes());


            MessageNRTMList theList = new MessageNRTMList();
            theList.addItem(false, "informationalText", "c2c:c2cMessageSubscription");
            theList.addItem(false, "start", "c2c:c2cMessageSubscription.subscriptionTimeFrame");

            thisSpecification.updateSpecificationOptionalFlags(theList);

            newMessage.setMessageSpecification(thisSpecification);

            VerificationDialog thisDialog = new VerificationDialog(null, true);
            thisDialog.setVerificationInstruction("Fake Instructions ...");
            thisDialog.setRawMessage(new String(newMessage.getMessageBody()));

            String messageSpec = "";
            for (String messageElement : newMessage.getMessageSpecification().getMessageSpec()) {
                messageSpec = messageSpec.concat(messageElement + "\n");
            }
            thisDialog.setMessageSpecification(newMessage.getMessageSpecification());

            thisDialog.setVisible(true);
            thisDialog.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }
}
