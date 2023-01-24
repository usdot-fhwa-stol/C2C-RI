/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.utilities;

/**
 * The Class OptionGroupChecker checks for whether an option group condition is satisfied.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class OptionGroupChecker {

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String args[]) {
        String trial1 = "(1)";
        String trial2 = "(1..2)";
        String trial3 = "(1..*)";
        String trial4 = "O.4(1..*)";
        String trial5 = " O.4  (1..*)";
        String trial6 = " O.4 (1..*)";
        String trial7 = "O.4 (1..*)";
        String trial8 = "O.1(2)";

        processOptionGroup(trial1);
        processOptionGroup(trial2);
        processOptionGroup(trial3);
        processOptionGroup(trial4);
        processOptionGroup(trial5);
        processOptionGroup(trial6);
        processOptionGroup(trial7);
        processOptionGroup(trial8);

    }

    /**
     * Process option group.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param option the option
     */
    private static void processOptionGroup(String option) {
        option = prepareOptionGroupString(option);
        System.out.println(" Option " + option + " has a min of " + getMin(option) + " and a max of " + getMax(option));
    }

    /**
     * Gets the min.
     *
     * @param option the option
     * @return the min
     */
    public static int getMin(String option) {
        int returnVal = -1;
        if (option.contains("(")) {
            if (option.contains("..")) {
                String value = option.substring(option.indexOf("(") + 1, option.indexOf(".."));
                try {
                    returnVal = Integer.parseInt(value);
                } catch (Exception e) {
                    System.err.println("     Value = " + value);
                }
            } else if (option.contains(")")) {
                String value = option.substring(option.indexOf("(") + 1, option.indexOf(")"));
                try {
//                    returnVal = Integer.parseInt(value);
                      returnVal = 0;
                } catch (Exception e) {
                    System.err.println("     Value = " + value);
                }
            }
        }
        return returnVal;
    }

    /**
     * Gets the max.
     *
     * @param option the option
     * @return the max
     */
    public static int getMax(String option) {
        int returnVal = -1;
        if (option.contains(")")) {
            if (option.contains("..")) {
                String value = option.substring(option.indexOf("..") + 2, option.indexOf(")"));
                try {
                    returnVal = Integer.parseInt(value);
                } catch (Exception e) {
                    System.err.println("     Value = " + value);

                }
            } else if (option.contains(")")) {
                String value = option.substring(option.indexOf("(") + 1, option.indexOf(")"));
                try {
                    returnVal = Integer.parseInt(value);

                } catch (Exception e) {
                    System.err.println("     Value = " + value);
                }
            }
        }

        return returnVal;
    }

    /**
     * Checks if is max or zero option.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param option the option
     * @return true, if is max or zero option
     */
    public static boolean isMaxOrZeroOption(String option) {
        boolean returnVal = false;
        if (option.contains("(")) {
            if ((!option.contains("..")) && (option.contains(")"))) {
                returnVal = true;
            }
        }
        return returnVal;
    }


    /**
     * Prepare option group string.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param option the option
     * @return the string
     */
    public static String prepareOptionGroupString(String option){
        String returnVal = option.trim();

        while (returnVal.contains(" (")){
            int location = returnVal.indexOf(" (");
            String frontPart = returnVal.substring(0, location);
            String endPart = returnVal.substring(location +1, returnVal.length());
            returnVal = frontPart.concat(endPart);
        }
         
        return returnVal;
    }
}
