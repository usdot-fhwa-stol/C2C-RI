/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.utilities;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Class DateUtils provides a number of date manipulation functions.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class DateUtils {

    /** The Constant DATE_FORMAT_NOW. */
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss:SSS";

    /**
     * Now.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the string
     */
    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());

    }

    /**
     * Time instance to string.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param cal the cal
     * @return the string
     */
    public static String timeInstanceToString(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());

    }

    /**
     * Millisecond to date.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param timeInMilliseconds the time in milliseconds
     * @return the string
     */
    public static String millisecondToDate(Long timeInMilliseconds){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

        Date resultdate = new Date(timeInMilliseconds);
        return sdf.format(resultdate);
    }

    /**
     * Execution time to string.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param time the time
     * @return the string
     */
    public static String executionTimeToString(long time) {
        long hours, mins, secs, tempTime;
        hours = time / 3600000;
        tempTime = time % 3600000;
        mins = tempTime / 60000;
        tempTime = tempTime % 60000;
        secs = tempTime / 1000;
        tempTime = tempTime % 1000;
        String msS = null;
        if (tempTime < 10) {
            msS = "00" + tempTime;
        } else if (tempTime < 100) {
            msS = "0" + time;
        } else {
            msS = tempTime + "";
        }
        String formattedTime = concatNum(hours, "h ");
        formattedTime += concatNum(mins, "m ");
        if (time == 0 || secs > 0 || tempTime > 0) {
            formattedTime += secs + "." + msS + "s";
        }


        return formattedTime.trim();
//        return hours+"h "+mins+"m "+secs+"."+msS+"s";
    }

    /**
     * Concat num.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param num the num
     * @param postfix the postfix
     * @return the string
     */
    private static String concatNum(long num, String postfix) {
        String concat = "";
        if (num > 0) {
            concat = num + postfix;
        }
        return concat;
    }

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param arg the arguments
     */
    public static void main(String arg[]) {
        System.out.println("Now : " + DateUtils.now());
    }
}
