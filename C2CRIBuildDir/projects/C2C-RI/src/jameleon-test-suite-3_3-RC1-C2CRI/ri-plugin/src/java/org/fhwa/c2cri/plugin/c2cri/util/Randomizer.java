package org.fhwa.c2cri.plugin.c2cri.util;

import java.util.*;

/**
 * The Class Randomizer provides a number of methods for generating random numbers.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  11/13/2012
 */
public class Randomizer {
	
	/** The rn. */
	private static Random rn = new Random();
	
	/**
	 * Instantiates a new randomizer.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 */
	private Randomizer()
	{
		
	}
	
	/**
	 * Rand.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param lo the lo
	 * @param hi the hi
	 * @return the int
	 */
	public static int rand(int lo, int hi)
	{
		int n = hi - lo + 1;

        int tempint = rn.nextInt();

        int i = tempint %n;

        if (i <0)
			i = -i;
		return lo + i;
	}

	/**
	 * Randenum.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param selections the selections
	 * @return the string
	 */
	public static String randenum(List selections)
	{
            int hi = selections.size() - 1;
            int lo = 0;
            int n = hi - lo + 1;
            System.out.println("the value of n = " + n);
            int tempint = rn.nextInt();
            System.out.println("the value of the random value is = " + tempint);
            int i = tempint %n;
            System.out.println("the value of the modulus operation = " + i);
            if (i <0)
                            i = -i;

            return (String)(selections.get(lo + i));
        }

	
	/**
	 * Randomstring.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param lo the lo
	 * @param hi the hi
	 * @return the string
	 */
	public static String randomstring(int lo, int hi)
	{
            int n = rand(lo,hi);
            byte b[] = new byte[n];
            for (int i = 0; i<n; i++)
                    b[i] = (byte)rand('a','z');
            return new String (b,0);
	}
	
	/**
	 * Randomstring.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param length the length
	 * @return the string
	 */
	public static String randomstring(int length)
	{
            byte b[] = new byte[length];
            for (int i = 0; i<length; i++)
                    b[i] = (byte)rand('a','z');
            return new String (b,0);
	}
	
	/**
	 * Randomboolean.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @return true, if successful
	 */
	public static boolean randomboolean()
	{
            boolean result;

            int n = rand(0,1);
            switch (n){
            case 0: 
                    result = false;
                    break;
            case 1:
                    result = true;
                    break;
            default:
                    result = true;
            }

            return result;		
	}
	
	/**
	 * Randomstring.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @return the string
	 */
	public static String randomstring()
	{
            return randomstring(5, 25);
	}
}
