/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * The Class TestCSV.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestCSV {

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
    String trial="this,that,,afternothing,";
    String[] trialArray = trial.split(",",256);

    System.out.println(trialArray.length);

        for (int ii = 0; ii < trialArray.length; ii++) {
            System.out.println(ii+" "+trialArray[ii]);
        }

                StringTokenizer st = new StringTokenizer(trial, ",", true);
                String lastToken = "";
                ArrayList<String> tempArray = new ArrayList<String>();
                System.out.println("\n\n"+st.countTokens());
                while (st.hasMoreTokens()) {
//display csv values
                    String tokenValue = st.nextToken();
                    if (!tokenValue.equals(","))System.out.println(tokenValue);
        }
    }
}
