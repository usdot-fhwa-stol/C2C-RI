/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.infolayer;

import java.util.ArrayList;

/**
 * MessageValueTester provides methods to test for values of specified message elements.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class MessageValueTester {

    /** The error list. */
    private static ArrayList<String> errorList = new ArrayList<String>();

    /**
     * Perform value test.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param mvs the mvs
     * @param ms the ms
     * @return true, if successful
     */
    public static boolean performValueTest(ValueSpecification mvs, MessageSpecification ms) {
        boolean result = true;
        errorList.clear();
        for (ValueSpecificationItem thisMVS : mvs.getValueSpec()) {
            if (thisMVS.getTestType() == 1) {
                if (!performTypeOneTest(thisMVS, ms)) {
                    result = false;
                }
            } else {
                result = false;
                errorList.add("Test Type " + thisMVS.getTestType() + " is not defined!");
            }
        }

        return result;
    }

    /**
     * Perform type one test.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param vsi the vsi
     * @param ms the ms
     * @return true, if successful
     */
    private static boolean performTypeOneTest(ValueSpecificationItem vsi, MessageSpecification ms) {
        boolean result = true;
        ArrayList<MessageSpecificationItem> matchingItems = new ArrayList<MessageSpecificationItem>();

        // boolean type defaults to false.
        boolean[] testResults = new boolean[vsi.getValues().length];
        int instanceCounter = 0;

        for (MessageSpecificationItem thisItem : ms.getMessageSpecItems()) {
            if (ms.isInstanceof(vsi.getValueName(), thisItem.getValueName())) {
                instanceCounter++;

                boolean valueFoundInValueSpec = false;
                for (int ii = 0; ii < vsi.getValues().length; ii++) {
                    if (vsi.getValues()[ii].equals(thisItem.getValue())) {
                        // A specified value should only exist once
                        if (testResults[ii]) {
                            errorList.add("Value " + vsi.getValues()[ii] + " was found multiple time for instance " + vsi.getValueName());
                            result = false;
                        }
                        testResults[ii] = true;
                        valueFoundInValueSpec = true;
                    }

                }
                if (!valueFoundInValueSpec) {
                    errorList.add("Value " + thisItem.getValue() + " exists, but was not included in the value test list. ");
                    result = false;
                }
            }
        }

        // Check to confirm that each of the test values were found.
        for (int ii = 0; ii < vsi.getValues().length; ii++) {
            if (!testResults[ii]) {
                errorList.add("Value " + vsi.getValues()[ii] + " was not found for instance " + vsi.getValueName());
                result = false;
            }
        }

        return result;
    }

    /**
     * Gets the error list.
     *
     * @return the error list
     */
    public static ArrayList<String> getErrorList() {
        return errorList;
    }

    /**
     * Gets the part name.
     *
     * @param part the part
     * @return the part name
     */
    private static String getPartName(String part) {
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
    private static String getPartIndex(String part) {
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
     * Gets the part index equivalent.
     *
     * @param part1 the part1
     * @param part2 the part2
     * @return the part index equivalent
     */
    private static String getPartIndexEquivalent(String part1, String part2) {
        String result = "";
        if (getPartIndex(part1).equals(getPartIndex(part2))) {
            result = getPartIndex(part1);
        } else if (getPartIndex(part1).equals("1") && getPartIndex(part2).isEmpty()) {
            result = "";
        } else if (getPartIndex(part1).isEmpty() && getPartIndex(part2).equals("1")) {
            result = "";
        } else {
            result = "";
        }

        return result;
    }


    /**
     * Gets the value spec item from message spec.
     *
     * @param testType the test type
     * @param msgSpecList the msg spec list
     * @return the value spec item from message spec
     * @throws Exception the exception
     */
    public static ValueSpecificationItem getValueSpecItemFromMessageSpec(int testType, ArrayList<MessageSpecificationItem> msgSpecList) throws Exception {

        String valueSpec = getValueSpecFromMessageSpec(testType, msgSpecList);
        ValueSpecificationItem result = new ValueSpecificationItem(valueSpec);
        return result;
    }

    /**
     * Gets the value spec from message spec.
     *
     * @param testType the test type
     * @param msgSpecList the msg spec list
     * @return the value spec from message spec
     * @throws Exception the exception
     */
    public static String getValueSpecFromMessageSpec(int testType, ArrayList<MessageSpecificationItem> msgSpecList) throws Exception {
        String result = "";

        int expectedNumParts;
        String[] partsReference = null;
        String valueName = "";
        for (MessageSpecificationItem thisItem : msgSpecList) {
            String tempName = "";
            String[] itemParts = thisItem.getValueName().split("\\.");
            expectedNumParts = itemParts.length;
            if (partsReference == null) {
                partsReference = itemParts;
            }
            for (int ii = 0; ii < expectedNumParts; ii++) {
                if (getPartName(partsReference[ii]).equals(getPartName(itemParts[ii]))) {
                    String index = getPartIndexEquivalent(partsReference[ii], itemParts[ii]);
                    String indexChar = "";
                    if (!index.isEmpty()) {
                        indexChar = "[" + index + "]";
                    } else {
                        partsReference[ii] = getPartName(itemParts[ii]);
                    }
                } else {
                    throw new Exception("Elements do not reference the same value type.");
                }
            }
        }

        for (int ii = 0; ii < partsReference.length; ii++) {
            if (valueName.isEmpty()) {
                valueName = valueName.concat(partsReference[ii]);
            } else {
                valueName = valueName.concat("." + partsReference[ii]);
            }
        }

        if (valueName.isEmpty()) {
            throw new Exception("Error processing the value element names.");
        }

        int numValues = msgSpecList.size();
        if (numValues == 0) {
            throw new Exception("No Message Specifications were provided. ");
        }


        String valueList = "";
        for (MessageSpecificationItem thisItem : msgSpecList) {
            if (valueList.isEmpty()) {
                valueList = valueList.concat(thisItem.getValue());
            } else {
                valueList = valueList.concat("," + thisItem.getValue());
            }
        }
        if (valueList.isEmpty()) {
            throw new Exception("No values were specified. ");
        }

        result = valueName + " = " + testType + "," + numValues + "," + valueList;
        return result;

    }
}
