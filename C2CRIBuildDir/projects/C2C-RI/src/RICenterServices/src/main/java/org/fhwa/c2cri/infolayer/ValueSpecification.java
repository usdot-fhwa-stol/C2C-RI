/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.infolayer;

import java.util.ArrayList;


/**
 * Represents the collection of value tests that need to be performed on a message.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ValueSpecification {

    /** The errors found. */
    private ArrayList<String> errorsFound = new ArrayList<String>();
    
    /** The value spec. */
    private ArrayList<ValueSpecificationItem> valueSpec = new ArrayList<ValueSpecificationItem>();

    /**
     * Instantiates a new value specification.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param valueSpecifications the value specifications
     */
    public ValueSpecification(ArrayList<ValueSpecificationItem> valueSpecifications) {
        valueSpec = valueSpecifications;
    }

    /**
     * Instantiates a new value specification.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param valueSpecificationText the value specification text
     */
    public ValueSpecification(String valueSpecificationText) {
        String[] messageArray = valueSpecificationText.split("\n");
        for (int ii=0; ii<messageArray.length; ii++){
             try{
               ValueSpecificationItem vsi = new ValueSpecificationItem(messageArray[ii]);
               valueSpec.add(vsi);
            } catch (Exception ex){
               ex.printStackTrace();
               errorsFound.add(ex.getMessage());
            }
        }
    }


//    public ValueSpecification(String valueSpecificationText) {
//        try{
//            ValueSpecificationItem valueSpecInstance = new ValueSpecificationItem(valueSpecificationText);
//            valueSpec.add(valueSpecInstance);
//        } catch (Exception ex){
//            ex.printStackTrace();
//            errorsFound.add(ex.getMessage());
//        }
//    }


    /**
 * Adds the value specification.
 * 
 * Pre-Conditions: N/A
 * Post-Conditions: N/A
 *
 * @param valueSpecificationText the value specification text
 */
public void addValueSpecification(String valueSpecificationText) {
        try{
            ValueSpecificationItem valueSpecInstance = new ValueSpecificationItem(valueSpecificationText);
            valueSpec.add(valueSpecInstance);
        } catch (Exception ex){
            ex.printStackTrace();
            errorsFound.add(ex.getMessage());
        }
    }

    /**
     * Adds the value specification.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param valueSpecificationInstance the value specification instance
     */
    public void addValueSpecification(ValueSpecificationItem valueSpecificationInstance) {
            valueSpec.add(valueSpecificationInstance);
    }

    /**
     * Removes the value specification.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param valueSpecificationInstance the value specification instance
     */
    public void removeValueSpecification(ValueSpecificationItem valueSpecificationInstance) {
        if (valueSpec.contains(valueSpecificationInstance)){
           valueSpec.remove(valueSpecificationInstance);
        }
    }

    /**
     * Gets the value spec.
     *
     * @return the value spec
     */
    public ArrayList<ValueSpecificationItem> getValueSpec() {
        return valueSpec;
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
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

    }
}
