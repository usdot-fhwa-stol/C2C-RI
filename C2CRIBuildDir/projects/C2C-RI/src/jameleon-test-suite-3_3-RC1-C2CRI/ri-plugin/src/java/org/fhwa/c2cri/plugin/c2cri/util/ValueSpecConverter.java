/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.plugin.c2cri.util;

import java.util.ArrayList;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.infolayer.ValueSpecification;
import org.fhwa.c2cri.infolayer.ValueSpecificationItem;


/**
 * The Class ValueSpecConverter.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/1/2012
 */
public class ValueSpecConverter {

    /**
     * Provides utility methods to convert input objects to value specifications.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param inputData the input data
     * @return the value specification
     */
    public static ValueSpecification convertToValueSpec(Object inputData) {
        ValueSpecification returnSpec = null;
        ArrayList<ValueSpecificationItem> valueSpecArray = new ArrayList<ValueSpecificationItem>();
        if (inputData instanceof String) {
            String request = (String) inputData;
            if (request.startsWith("#RIValueSpec#") && (request.endsWith("#RIValueSpec#"))) {
                request = request.replace("#RIValueSpec#", "");
                String[] requestArray = request.split("; ");
                for (int ii = 0; ii < requestArray.length; ii++) {
                    try{
                    ValueSpecificationItem thisItem = new ValueSpecificationItem(requestArray[ii]);
                    valueSpecArray.add(thisItem);
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                returnSpec = new ValueSpecification(valueSpecArray);
            }
        } else if (inputData instanceof ArrayList) {
            ArrayList<String> thisArray = (ArrayList<String>) inputData;
            for (String thisString : thisArray){
                    try{
                    ValueSpecificationItem thisItem = new ValueSpecificationItem(thisString);
                    valueSpecArray.add(thisItem);
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
            }

            returnSpec = new ValueSpecification(valueSpecArray);
        }

        return returnSpec;
    }

    /**
     * Convert to message spec.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param inputData the input data
     * @return the message specification
     */
    public static MessageSpecification convertToMessageSpec(Object inputData){
        MessageSpecification returnSpec = null;
        ArrayList<String> messageSpecArray = new ArrayList<String>();
                if (inputData instanceof String) {
                String request = (String) inputData;

                if (request.startsWith("#RIMessageSpec#") && (request.endsWith("#RIMessageSpec#"))) {
                    request = request.replace("#RIMessageSpec#", "");
                    String[] requestArray = request.split("; ");
                    for (int ii = 0; ii < requestArray.length; ii++) {
                        messageSpecArray.add(requestArray[ii]);
                    }
                    returnSpec = new MessageSpecification(messageSpecArray);
                    }
                } else if (inputData instanceof ArrayList) {
                    messageSpecArray = (ArrayList<String>) inputData;
                    returnSpec = new MessageSpecification(messageSpecArray);
                }

        return returnSpec;
    }
}
