/*
*/
package org.fhwa.c2cri.plugin.c2cri.tags;

import org.fhwa.c2cri.plugin.c2cri.util.Randomizer;
import java.util.*;
import org.fhwa.c2cri.plugin.c2cri.RIFunctionTag;

/**
 * Gets the matching text against the currently active IE and sets it in the context.
 *
 * @author TransCore ITS, LLC
 * Last Updated: 10/9/2012
 * 
 * @jameleon.function name="ri-get-random-var" type="action"
 * @jameleon.step Get a random value of the given type
 * @jameleon.step Store the returned value in the provided variable name.
 */
public class RIGetRandomVarTag extends RIFunctionTag {
    /**
     * The name by which a random context variable will be created
     *  
     * @jameleon.attribute contextName="RIGetRandomVarName" required="true"
     */
    protected String varName;
    /**
     * The type of value to return
     * 
     * @jameleon.attribute contextName="RIGetRandomVarType"
     */
    protected String varType;
    
    /**
     * The length of the variable to return (if string)
     * @jameleon.attribute contextName="RIGetRandomVarLength"
     */
    protected int varLength;

    /**
     * The length of the variable to return (if string)
     * @jameleon.attribute contextName="RIGetRandomVarHigh"
     */
    protected int highVal;
    
    /**
     * The length of the variable to return (if string)
     * @jameleon.attribute contextName="RIGetRandomVarLow"
     */
    protected int lowVal;
    
    /**
     * The variable returned
     * @jameleon.attribute contextName="RIGetRandomVarReturn"
     */
    protected String returnVal;
    
    public void testBlock()
    {
        if (varName != null)
      	   {

        	if (varType.equals("boolean")) {
                  boolean tempresult = Randomizer.randomboolean();
                  System.out.println(" The returned boolean => " + Boolean.toString(tempresult));
                  setVariable(varName, Boolean.toString(tempresult));
        	  }
        	else if (varType.equals("String")) {        	 
        	  	  String tempstring = Randomizer.randomstring(lowVal,highVal);
                  System.out.println(" The returned string => " + tempstring);
                  setVariable(varName, tempstring);
        	  }
        	else if (varType.equals("integer")) {
        		  int tempint = Randomizer.rand(lowVal, highVal);
                  System.out.println(" The returned integer => " + Integer.toString(tempint));
        		  setVariable(varName, Integer.toString(tempint));
        	  }
        	else if (varType.equals("enum")) {
        		try {
                    System.out.println(" Getting the object for varName");
                    List copy = getVariableAsList(varName);

                   System.out.println(" After Getting the object for varName");

                    System.out.println(" From RIGetRandomVarTag... Item => "+ varName + " maps to an instance of List with size "+ copy.size());
      		        String tempResult = Randomizer.randenum(copy);
                    System.out.println(" The returned Enum => " + tempResult);
      		        setVariable(returnVal, tempResult);

        		} 
        		catch(Exception e){
        			System.out.println("Experienced an error setting enum "+ e.getMessage());
        		}
        	 }
        	else
        	  {
        	     setVariable(varName,"Value");
        	  }
    	   }

    }
}