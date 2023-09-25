/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.entitydata;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidValueException;

/**
 *
 * @author TransCore ITS, LLC Created: Jan 31, 2016
 */
public class EntityDataValidator 
{
    // Note we do not test for values of type AnyType (normally used for the mandatory extension data frame).
    public static void validateValue(String baseType, String minLength, String maxLength, String minInclusive, String maxInclusive, String enumeration, String elementValue) throws InvalidValueException 
    {
        if (baseType.equalsIgnoreCase("string")) 
        {
            validateString(minLength, maxLength, elementValue);
        } 
        else if (baseType.equalsIgnoreCase("anySimpleType"))
        {
            validateEnumValue(enumeration, elementValue);
        } 
        else if (baseType.equalsIgnoreCase("dateTime"))
        {
            validateDateTimeValue(elementValue);
        } 
        else if (baseType.equalsIgnoreCase("decimal"))
        {
            validateDecimalValue(minInclusive, maxInclusive, elementValue);
        } 
        else if (baseType.equalsIgnoreCase("float"))
        {
            validateFloatValue(minInclusive, maxInclusive, elementValue);
        } 
        else if (baseType.equalsIgnoreCase("int"))
        {
            validateIntValue(minInclusive, maxInclusive, elementValue);
        } 
        else if (baseType.equalsIgnoreCase("Int-latitude32"))
        {
            validateIntLatitude32Value(minInclusive, maxInclusive, elementValue);
        } 
        else if (baseType.equalsIgnoreCase("Int-longitude32"))
        {
            validateIntLongitude32Value(minInclusive, maxInclusive, elementValue);
        } 
        else if (baseType.equalsIgnoreCase("short"))
        {
            validateShortValue(minInclusive, maxInclusive, elementValue);
        } 
        else if (baseType.equalsIgnoreCase("unsignedByte"))
        {
            validateUnsignedByteValue(minInclusive, maxInclusive, elementValue);
        } 
        else if (baseType.equalsIgnoreCase("unsignedInt"))
        {
            validateUnsignedIntValue(minInclusive,maxInclusive, elementValue);
        } 
        else if (baseType.equalsIgnoreCase("unsignedShort"))
        {
            validateUnsignedShortValue(minInclusive, maxInclusive, elementValue);
        }
    }

    /**
     * Validate that a given value string has a valid number of characters.
     *
     * @param minLength
     * @param maxLength
     * @param elementValue
     * @throws InvalidValueException
     */
    private static void validateString(String minLength, String maxLength, String elementValue) throws InvalidValueException 
    {

        // Throw an exception if the value is null when the length must be greater that 0.
        if ((elementValue == null) && (Integer.parseInt(minLength) > 0)) 
        {
            throw new InvalidValueException("Element Value was null.");
        } else 
        {
			if (elementValue == null)
				throw new InvalidValueException("Null elementValue");
            // If minLength and maxLength = -1, then any non-null value is allowed.
            if ((Integer.parseInt(minLength)==-1) && (Integer.parseInt(maxLength)==-1)) {
                return;
                
            // Throw an exception if the length of the value is outside of the specified range.
            } else if ((elementValue.length() < Integer.parseInt(minLength)) || (elementValue.length() > Integer.parseInt(maxLength))) 
            {
                throw new InvalidValueException("The length of " + elementValue + " (" + elementValue.length() + ") is outside of the defined range - min (" + minLength + ") to max (" + maxLength + ").");
            }
        }
    }
    
    /**
     * Validate that a given value string is within the specified enumeration.
     *
     * @param enumeration
     * @param elementValue
     * @throws InvalidValueException
     */
    public static void validateEnumValue(String enumeration, String elementValue) throws InvalidValueException
    {
        
        //Throw an exception if the value is element is null.
        if ((elementValue == null)) 
        {
            throw new InvalidValueException("The Element definition was null.");
        }
        //Throw an exception if the value of the enumeration is null.
        else if((enumeration == null))
        {
            throw new InvalidValueException("The Enumeration definition was null.");
        }
        else
        {
            //The Regex expression used to parse the string.
            Pattern regPass = Pattern.compile("\\((.*?)\\)\\s(.*?)[;}]");
            
            //The Matcher class used to match the Regex expression against the input string.
            Matcher matcher = regPass.matcher(enumeration);
            
            //A loop control method that searches through the input string.
            while(matcher.find())
            {
                //A condition statement to check if the value found in the input string matches the value from the formal parameter of the method.
                if(matcher.group(1).equals(elementValue) || matcher.group(2).equals(elementValue))
                {
                   break;
                }
            }
            
            //A condition statement that checks if the entire string has been searched and there were not matches.
            if(matcher.hitEnd() && !matcher.matches())
            {
                throw new InvalidValueException("The element value of " + elementValue + " was not defined for the enumeration type.");
            }
        }
    }
    
   /**
     * Validate that a given value string is a valid DateTime for the specified DateTime.
     *
     * @param elementValue
     * @throws InvalidValueException
     */
    public static void validateDateTimeValue(String elementValue)throws InvalidValueException
    {
        //Throw an exception if the value is null.
        if ((elementValue == null)) 
        {
            throw new InvalidValueException("The DateTime value was null.");
        }
        else
        {
            //The Regex expression used to parse the string.
            Pattern regPass = Pattern.compile("-?([1-9][0-9]{3,}|0[0-9]{3})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])T(([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])(\\.[0-9]+)?|(24:00:00(\\.0+)?))(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?");
            
            //The Matcher class used to match the Regex expression against the input string.
            Matcher matcher = regPass.matcher(elementValue);
            
            //A loop control method that searches through the input string.
            if(matcher.find())
            {
                //If a match is found, meaning that the DateTime is correct in the input string, the method returns.
                return;
            }
            
            throw new InvalidValueException("The DateTime value was not correct for the DateTime type.");
        }
    }
    
    /**
     * Validate that a given value string is within the specified range of minInclusive and maxInclusion.
     *
     * @param minInclusion
     * @param maxInclusion
     * @param elementValue
     * @throws InvalidValueException
     */
    public static void validateDecimalValue(String minInclusive, String maxInclusive, String elementValue)throws InvalidValueException
    {
        //Throw an exception if the value is null.
        if(elementValue == null)
        {
            throw new InvalidValueException("Element Value was null.");
        }
        else
        {
            //Throw an exception if the element is outside of the specified range.
            if(Double.parseDouble(elementValue) < Double.parseDouble(minInclusive) || Double.parseDouble(elementValue) > Double.parseDouble(maxInclusive))
            {
                throw new InvalidValueException("The value of " + elementValue + " is outside of the defined range - min (" + minInclusive + ") to max (" + maxInclusive + ").");
            }
        }
    }
    
    /**
     * Validate that a given value string is within the specified range of minInclusive and maxInclusion.
     *
     * @param minInclusion
     * @param maxInclusion
     * @param elementValue
     * @throws InvalidValueException
     */
    public static void validateFloatValue(String minInclusive, String maxInclusive, String elementValue) throws InvalidValueException
    {
        //Throw an exception if the value is null.
        if(elementValue == null)
        {
            throw new InvalidValueException("Element Value was null.");
        }
        else
        {  
            //Throw an exception if the element is outside of the specified range.
            if((Float.parseFloat(minInclusive) < -Float.MIN_VALUE) || (Float.parseFloat(maxInclusive) > Float.MAX_VALUE))
            {
                /*A condition statment to check if the values of minInclusive and maxInclusive are "-1". 
                  If the values are they are considered default and the datatype min and max are assigned
                */
                if(minInclusive.equals("-1") && maxInclusive.equals("-1"))
                {
                    minInclusive = Float.toString(-Float.MIN_VALUE);
                    maxInclusive = Float.toString(Float.MAX_VALUE);
                }
                else
                {
                    throw new InvalidValueException("The value of minInclusive or maxInclusive did not meet the specifications of an Float.");
                }
            }
            //Throw an exception if the element is outside of the specified range.
            if((Float.parseFloat(elementValue) < Float.parseFloat(minInclusive)) || (Float.parseFloat(elementValue) > Float.parseFloat(maxInclusive)))
            {
                throw new InvalidValueException("The value of " + elementValue + " is outside of the defined range - min (" + minInclusive + ") to max (" + maxInclusive + ").");
            }
        }
    }
    
    /**
     * Validate that a given value string is within the specified range of minInclusive and maxInclusion.
     *
     * @param minInclusion
     * @param maxInclusion
     * @param elementValue
     * @throws InvalidValueException
     */
    public static void validateIntValue(String minInclusive, String maxInclusive, String elementValue) throws InvalidValueException
    {
        //Throw an exception if the value is null.
        if(elementValue == null)
        {
            throw new InvalidValueException("Element Value was null.");
        }
        else
        {
            try
            {
                 /*A condition statment to check if the values of minInclusive and maxInclusive are "-1". 
                  If the values are they are considered default and the datatype min and max are assigned
                */
                if(minInclusive.equals("-1") && maxInclusive.equals("-1"))
                {
                    minInclusive = Integer.toString(Integer.MIN_VALUE);
                    maxInclusive = Integer.toString(Integer.MAX_VALUE);
                }
                //Throw an exception if the element is outside of the specified range.
                else if((Integer.parseInt(minInclusive) < Integer.MIN_VALUE) || (Integer.parseInt(maxInclusive) > Integer.MAX_VALUE))
                {
                    throw new InvalidValueException("The value of minInclusive or maxInclusive did not meet the specifications of an Integer.");
                }
                //Throw an exception if the element is outside of the specified range.
                if(Integer.parseInt(elementValue) < Integer.parseInt(minInclusive) || Integer.parseInt(elementValue) > Integer.parseInt(maxInclusive))
                {
                    throw new InvalidValueException("The value of " + elementValue + " is outside of the defined range - min (" + minInclusive + ") to max (" + maxInclusive + ").");
                }
            }
            //Throw an exception if the NumberFormat has an error.
            catch(NumberFormatException Nex)
            {
                throw new InvalidValueException("The input string for minInclusive, maxInclusive, or elementValue is too small or too large for the datatype to process. " + Nex.getMessage());
            }
        }
    }
    
    /**
     * Validate that a given value string is within the specified range of minInclusive and maxInclusion.
     *
     * @param minInclusion
     * @param maxInclusion
     * @param elementValue
     * @throws InvalidValueException
     */
    public static void validateIntLatitude32Value(String minInclusive, String maxInclusive, String elementValue) throws InvalidValueException
    {
        //Throw an exception if the value is null.
        if(elementValue == null)
        {
            throw new InvalidValueException("Element Value was null.");
        }
        else
        {
            try
            {
                /*A condition statment to check if the values of minInclusive and maxInclusive are "-1". 
                      If the values are they are considered default and the datatype min and max are assigned
                */
                if(minInclusive.equals("-1") && maxInclusive.equals("-1"))
                {
                    minInclusive = Integer.toString(-90000000);
                    maxInclusive = Integer.toString(90000000);
                }
                else if((Integer.parseInt(minInclusive) < Integer.MIN_VALUE) || (Integer.parseInt(maxInclusive) > Integer.MAX_VALUE))
                {
                    throw new InvalidValueException("The value of minInclusive or maxInclusive did not meet the specifications of an IntLatitude32.");    
                }
                //Throw an exception if the NumberFormat has an error.
                if(Integer.parseInt(elementValue) < Integer.parseInt(minInclusive) || Integer.parseInt(elementValue) > Integer.parseInt(maxInclusive))
                {
                    throw new InvalidValueException("The value of " + elementValue + " is outside of the defined range - min (" + minInclusive + ") to max (" + maxInclusive + ").");
                }
            }
            catch(NumberFormatException Nex)
            {
                throw new InvalidValueException("The input string for minInclusive, maxInclusive, or elementValue is too small or too large for the datatype to process. " + Nex.getMessage());
            }
        }
    }
    
  /**
     * Validate that a given value string is within the specified range of minInclusive and maxInclusion.
     *
     * @param minInclusion
     * @param maxInclusion
     * @param elementValue
     * @throws InvalidValueException
     */
    public static void validateIntLongitude32Value(String minInclusive, String maxInclusive, String elementValue) throws InvalidValueException
    {
        //Throw an exception if the value is null.
        if(elementValue == null)
        {
            throw new InvalidValueException("Element Value was null.");
        }
        else
        {
            try
            {
                /*A condition statment to check if the values of minInclusive and maxInclusive are "-1". 
                          If the values are they are considered default and the datatype min and max are assigned
                */
                if(minInclusive.equals("-1") && maxInclusive.equals("-1"))
                {
                    minInclusive = Integer.toString(-180000000);
                    maxInclusive = Integer.toString(180000000);
                }
                //Throw an exception if the NumberFormat has an error.
                else if((Integer.parseInt(minInclusive) < Integer.MIN_VALUE) || (Integer.parseInt(maxInclusive) > Integer.MAX_VALUE))
                {
                    throw new InvalidValueException("The value of minInclusive or maxInclusive did not meet the specifications of an IntLongitude32.");
                }
                //Throw an exception if the NumberFormat has an error.
                if(Integer.parseInt(elementValue) < Integer.parseInt(minInclusive) || Integer.parseInt(elementValue) > Integer.parseInt(maxInclusive))
                {
                    throw new InvalidValueException("The value of " + elementValue + " is outside of the defined range - min (" + minInclusive + ") to max (" + maxInclusive + ").");
                }
            }
            catch(NumberFormatException Nex)
            {
                throw new InvalidValueException("The input string for minInclusive, maxInclusive, or elementValue is too small or too large for the datatype to process. " + Nex.getMessage());
            }
        }
    }
    
   /**
     * Validate that a given value string is within the specified range of minInclusive and maxInclusion.
     *
     * @param minInclusion
     * @param maxInclusion
     * @param elementValue
     * @throws InvalidValueException
     */
    public static void validateShortValue(String minInclusive, String maxInclusive, String elementValue) throws InvalidValueException
    {
        //Throw an exception if the value is null.
        if(elementValue == null)
        {
            throw new InvalidValueException("Element Value was null.");
        }
        else
        {
            try
            {
                /*A condition statment to check if the values of minInclusive and maxInclusive are "-1". 
                              If the values are they are considered default and the datatype min and max are assigned
                */
                if(minInclusive.equals("-1") && maxInclusive.equals("-1"))
                {
                    minInclusive = Short.toString(Short.MIN_VALUE);
                    maxInclusive = Short.toString(Short.MAX_VALUE);
                }
                //Throw an exception if the NumberFormat has an error.
                else if((Short.parseShort(minInclusive) < Short.MIN_VALUE) || (Short.parseShort(maxInclusive) > Short.MAX_VALUE))
                {
                        throw new InvalidValueException("The value of minInclusive or maxInclusive did not meet the specifications of an Short.");
                }
                //Throw an exception if the of the value is outside of the specified range.
                if(Short.parseShort(elementValue) < Short.parseShort(minInclusive) || Short.parseShort(elementValue) > Short.parseShort(maxInclusive))
                {
                    throw new InvalidValueException("The value of " + elementValue + " is outside of the defined range - min (" + minInclusive + ") to max (" + maxInclusive + ").");
                }
            }
            catch(NumberFormatException Nex)
            {
                throw new InvalidValueException("The input string for minInclusive, maxInclusive, or elementValue is too small or too large for the datatype to process. " + Nex.getMessage());
            }
        } 

    }
    
   /**
     * Validate that a given value string is within the specified range of minInclusive and maxInclusion.
     *
     * @param minInclusion
     * @param maxInclusion
     * @param elementValue
     * @throws InvalidValueException
     */
    public static void validateUnsignedByteValue(String minInclusive, String maxInclusive, String elementValue) throws InvalidValueException
    {
        //Throw an exception if the value is null.
        if(elementValue == null)
        {
            throw new InvalidValueException("Element Value was null.");
        }
        else
        {
            try
            {
                //Throw an exception if the of the value is less than the minimum that the datatype will allow.
                if(Short.parseShort(elementValue) < 0)
                {
                    throw new InvalidValueException(" The value of " + elementValue + " did not meet the specifications of an Unsigned-Byte.");
                }

                //Throw an exception if the NumberFormat has an error.
                else if((Short.parseShort(minInclusive) < Short.MIN_VALUE) || (Short.parseShort(maxInclusive) > Short.MAX_VALUE))
                {
                        throw new InvalidValueException("The value of minInclusive or maxInclusive did not meet the specifications of an Unsigned-Byte BaseType UnsignedShort.");
                }
                //Throw an exception if the of the value is less than the minimum or greater than the maximum for the specified datatype.
                else if(Short.parseShort(minInclusive) < 0 || Short.parseShort(maxInclusive) > Short.parseShort("255"))
                {
                    /*A condition statment to check if the values of minInclusive and maxInclusive are "-1". 
                                  If the values are they are considered default and the datatype min and max are assigned
                    */
                    if(minInclusive.equals("-1") && maxInclusive.equals("-1"))
                    {
                        minInclusive = Short.toString(Short.parseShort("0"));
                        maxInclusive = Short.toString(Short.parseShort("255"));
                    }
                    throw new InvalidValueException("The value of minInclusive or maxInclusive did not meet the specifications of an Unsigned-Byte.");
                }
                //Throw an exception if the of the value is outside of the specified range.
                if(Short.parseShort(elementValue) < Short.parseShort(minInclusive) || Short.parseShort(elementValue) > Short.parseShort(maxInclusive))
                {
                    throw new InvalidValueException("The value of " + elementValue + " is outside of the defined range - min (" + minInclusive + ") to max (" + maxInclusive + ").");
                }
            }
            catch(NumberFormatException Nex)
            {
                throw new InvalidValueException("The input string for minInclusive, maxInclusive, or elementValue is too small or too large for the datatype to process. " + Nex.getMessage());
            }
        }
    }
    
   /**
     * Validate that a given value string is within the specified range of minInclusive and maxInclusion.
     *
     * @param minInclusion
     * @param maxInclusion
     * @param elementValue
     * @throws InvalidValueException
     */
    public static void validateUnsignedIntValue(String minInclusive, String maxInclusive, String elementValue) throws InvalidValueException
    {
        //Throw an exception if the value is null.
        if(elementValue == null)
        {
            throw new InvalidValueException("Element Value was null.");
        }
        else
        {
            try
            {
                //Throw an exception if the of the value is less than the minimum that the datatype will allow.
                if(Long.parseLong(elementValue) < 0)
                {
                    throw new InvalidValueException(" The value of " + elementValue + " did not meet the specifications of an Unsigned-Int.");
                }
                //Throw an exception if the NumberFormat has an error.
                else if((Long.parseLong(minInclusive) < Long.MIN_VALUE) || (Long.parseLong(maxInclusive) > Long.MAX_VALUE))
                {
                        throw new InvalidValueException("The value of minInclusive or maxInclusive did not meet the specifications of an Unsigned-Int BaseType UnsignedLong.");
                }
                //Throw an exception if the of the value is less than the minimum or greater than the maximum for the specified datatype.
                else if(Long.parseLong(minInclusive) < 0 || Long.parseLong(maxInclusive) > Long.parseLong("4294967295"))
                {
                    /*A condition statment to check if the values of minInclusive and maxInclusive are "-1". 
                                  If the values are they are considered default and the datatype min and max are assigned
                    */
                    if(minInclusive.equals("-1") && maxInclusive.equals("-1"))
                    {
                        minInclusive = Long.toString(0);
                        maxInclusive = Long.toString(Long.parseLong("4294967295"));
                    }
                    else
                    {
                        throw new InvalidValueException("The value of minInclusive or maxInclusive did not meet the specifications of an Unsigned-Int.");
                    }
                }
                //Throw an exception if the of the value is outside of the specified range.
                if(Long.parseLong(elementValue) < Long.parseLong(minInclusive) || Long.parseLong(elementValue) > Long.parseLong(maxInclusive))
                {
                    throw new InvalidValueException("The value of " + elementValue + " is outside of the defined range - min (" + minInclusive + ") to max (" + maxInclusive + ").");
                }
            }
            catch(NumberFormatException Nex)
            {
                throw new InvalidValueException("The input string for minInclusive, maxInclusive, or elementValue is too small or too large for the datatype to process. " + Nex.getMessage());
            }
        }
    }
    
   /**
     * Validate that a given value string is within the specified range of minInclusive and maxInclusion.
     *
     * @param minInclusion
     * @param maxInclusion
     * @param elementValue
     * @throws InvalidValueException
     */
    public static void validateUnsignedShortValue(String minInclusive, String maxInclusive, String elementValue) throws InvalidValueException
    {
        //Throw an exception if the value is null.
        if(elementValue == null)
        {
            throw new InvalidValueException("Element Value was null.");
        }
        else
        {  
            try
            {
                //Throw an exception if the of the value is less than the minimum that the datatype will allow.
                if(Integer.parseInt(elementValue) < 0)
                {
                    throw new InvalidValueException(" The value of " + elementValue + " did not meet the specifications of an Unsigned-Short.");
                }
                
                //Throw an exception if the of the value is less than the minimum or greater than the maximum for the specified datatype.
                else if(Integer.parseInt(minInclusive) < 0 || Integer.parseInt(maxInclusive) > 65535)
                {
                    /*A condition statment to check if the values of minInclusive and maxInclusive are "-1". 
                                  If the values are they are considered default and the datatype min and max are assigned
                    */
                    if(minInclusive.equals("-1") && maxInclusive.equals("-1"))
                    {
                        minInclusive = Integer.toString(0);
                        maxInclusive = Integer.toString(Integer.parseInt("65535"));
                    }
                    throw new InvalidValueException("The value of minInclusive or maxInclusive did not meet the specifications of an Unsigned-Short.");
                }

                //Throw an exception if the NumberFormat has an error.
                if((Integer.parseInt(minInclusive) < Integer.MIN_VALUE) || (Integer.parseInt(maxInclusive) > Integer.MAX_VALUE))
                {
                    throw new InvalidValueException("The value of minInclusive or maxInclusive did not meet the specifications of an Unsigned-Short BaseType UnsignedInt.");
                }
            }
            catch(NumberFormatException Nex)
            {
               throw new InvalidValueException("The input string for minInclusive, maxInclusive, or elementValue is too small or too large for the datatype to process. " + Nex.getMessage());         
            }
            
            //Throw an exception if the of the value is outside of the specified range.
            if(Integer.parseInt(elementValue) < Integer.parseInt(minInclusive) || Integer.parseInt(elementValue) > Integer.parseInt(maxInclusive))
            {
                throw new InvalidValueException("The value of " + elementValue + " is outside of the defined range - min (" + minInclusive + ") to max (" + maxInclusive + ").");
            }
        }
    }

}
