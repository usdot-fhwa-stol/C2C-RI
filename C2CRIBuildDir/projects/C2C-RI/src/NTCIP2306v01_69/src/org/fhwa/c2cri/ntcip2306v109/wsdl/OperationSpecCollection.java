/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.wsdl;

import java.util.ArrayList;


/**
 * The Class OperationSpecCollection provides a collection of related operations.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class OperationSpecCollection {

    /** The operation collection. */
    private ArrayList<OperationSpecification> operationCollection = new ArrayList<OperationSpecification>();

    /**
     * Adds the.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param os the os
     * @return true, if successful
     */
    public boolean add(OperationSpecification os) {
        return operationCollection.add(os);
    }

    /**
     * Contains.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param serviceName the service name
     * @param portName the port name
     * @param operationName the operation name
     * @return true, if successful
     */
    public boolean contains(String serviceName, String portName, String operationName) {
        boolean result = false;
        for (OperationSpecification thisSpec : operationCollection) {
            if (thisSpec.getOperationName().equals(operationName)
                    && thisSpec.getRelatedToPort().equals(portName)
                    && thisSpec.getRelatedToService().equals(serviceName)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Gets the.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param serviceName the service name
     * @param portName the port name
     * @param operationName the operation name
     * @return the operation specification
     */
    public OperationSpecification get(String serviceName, String portName, String operationName) {
        OperationSpecification result = null;
        for (OperationSpecification thisSpec : operationCollection) {
            if (thisSpec.getOperationName().equals(operationName)
                    && thisSpec.getRelatedToPort().equals(portName)
                    && thisSpec.getRelatedToService().equals(serviceName)) {
                result = thisSpec;
                break;
            }
        }
        return result;
    }

    /**
     * Gets the.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param index the index
     * @return the operation specification
     */
    public OperationSpecification get(int index) {
        OperationSpecification result = null;
        try {
            result = operationCollection.get(index);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Gets the specs with soap action.
     *
     * @param soapAction the soap action
     * @return the specs with soap action
     */
    public ArrayList<OperationSpecification> getSpecsWithSOAPAction(String soapAction) {
        ArrayList<OperationSpecification> returnList = new ArrayList<OperationSpecification>();
        if (soapAction != null) {
            for (OperationSpecification thisSpec : operationCollection) {
                //CTCRI-718 some clients send post messages where the soapaction value is enclosed
                // in quotes.  This is correct per the standard.  However, the quotes are not stored with the
                // SOAPAction value in the C2C RI.  Ensure that we appropriately recognize both cases to 
                // processing the field.
                String enclosedSoapAction = soapAction;
                if(!soapAction.startsWith("\"")&&!soapAction.endsWith("\"")){
                    enclosedSoapAction = "\""+enclosedSoapAction+"\"";
                } else {
                    // String the leading and ending doublequotes from the soapAction provided.
                    int textLength = soapAction.length();                    
                    if (textLength >= 2 && soapAction.charAt(0) == '"' && soapAction.charAt(textLength - 1) == '"') {
                        soapAction = soapAction.substring(1, textLength - 1);
                    }                    
                }
                if (thisSpec.getSoapAction().equals(soapAction)||thisSpec.getSoapAction().equals(enclosedSoapAction)) {
                    returnList.add(thisSpec);
                }
            }
        }
        return returnList;
    }

    /**
     * Gets the specs with location.
     *
     * @param location the location
     * @return the specs with location
     */
    public ArrayList<OperationSpecification> getSpecsWithLocation(String location) {
        ArrayList<OperationSpecification> returnList = new ArrayList<OperationSpecification>();
        if (location != null) {
            for (OperationSpecification thisSpec : operationCollection) {
                System.out.println("OperationSpecCollection::getSpecsWithLocation:  Checking whether operation: " + thisSpec.getOperationName() + " at location " + thisSpec.getDocumentLocation() + " = " + location);
                if (thisSpec.getDocumentLocation().equals(location)) {
                    System.out.println("OperationSpecCollection::getSpecsWithLocation:  MATCH!!!");
                    returnList.add(thisSpec);
                }
            }
        }
        return returnList;
    }

    /**
     * Gets the specs with operation name.
     *
     * @param operationName the operation name
     * @return the specs with operation name
     */
    public ArrayList<OperationSpecification> getSpecsWithOperationName(String operationName) {
        ArrayList<OperationSpecification> returnList = new ArrayList<OperationSpecification>();
        if (operationName != null) {
            for (OperationSpecification thisSpec : operationCollection) {
                if (thisSpec.getOperationName().equals(operationName)) {
                    returnList.add(thisSpec);
                }
            }
        }
        return returnList;
    }
    
    
    /**
     * Gets the copy as list.
     *
     * @return the copy as list
     */
    public ArrayList<OperationSpecification> getCopyAsList() {
        return (ArrayList) operationCollection.clone();
    }

    /**
     * Checks if is empty.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is empty
     */
    public boolean isEmpty() {
        return operationCollection.isEmpty();
    }

    /**
     * Size.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the int
     */
    public int size() {
        return operationCollection.size();
    }
}
