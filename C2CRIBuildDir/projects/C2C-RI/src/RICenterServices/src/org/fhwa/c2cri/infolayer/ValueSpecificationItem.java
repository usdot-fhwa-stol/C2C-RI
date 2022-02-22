/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.infolayer;


/**
 * Represents the specification of a value within a message.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ValueSpecificationItem {

    /** The value name. */
    private String valueName;
    
    /** The test type. */
    private Integer testType;
    
    /** The num values. */
    private Integer numValues;
    
    /** The values. */
    private String[] values;

    /**
     * Instantiates a new value specification item.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param valueSpecEntry the value spec entry
     * @throws Exception the exception
     */
    public ValueSpecificationItem(String valueSpecEntry) throws Exception{
        if (valueSpecEntry.contains("=")){
            int index = valueSpecEntry.indexOf("=");
            valueName = valueSpecEntry.substring(0, index).trim();
            String[] specParts = valueSpecEntry.substring(index+1).split(",");
            
            if (specParts.length >= 3){
                try{
                    testType = Integer.parseInt(specParts[0].trim());
                } catch (Exception ex){
                    throw new Exception (valueSpecEntry + " is not valid. " + specParts[0] + " does not convert to an Integer.\n"+ex.getMessage());
                }
                
                try{
                    numValues = Integer.parseInt(specParts[1].trim());
                } catch (Exception ex){
                    throw new Exception (valueSpecEntry + " is not valid. " + specParts[1] + " does not convert to an Integer.\n"+ex.getMessage());
                }
                
                values = new String[specParts.length-2];
                for (int ii=2; ii<specParts.length; ii++){
                    values[ii-2] = specParts[ii];
                }
            }
        } else {
            throw new Exception (valueSpecEntry + " is not valid.  (Missing =)");
        }
        
        
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        String result = "";
        String valueList = "";
        for (String thisItem : this.values) {
            if (valueList.isEmpty()) {
                valueList = valueList.concat(thisItem);
            } else {
                valueList = valueList.concat("," + thisItem);
            }
        }

        result = this.valueName + " = " + this.testType + "," + this.numValues + "," + valueList;
        return result;

    }

    
    /**
     * Gets the num values.
     *
     * @return the num values
     */
    public Integer getNumValues() {
        return numValues;
    }

    /**
     * Sets the num values.
     *
     * @param numValues the new num values
     */
    public void setNumValues(Integer numValues) {
        this.numValues = numValues;
    }

    /**
     * Gets the test type.
     *
     * @return the test type
     */
    public Integer getTestType() {
        return testType;
    }

    /**
     * Sets the test type.
     *
     * @param testType the new test type
     */
    public void setTestType(Integer testType) {
        this.testType = testType;
    }

    /**
     * Gets the value name.
     *
     * @return the value name
     */
    public String getValueName() {
        return valueName;
    }

    /**
     * Sets the value name.
     *
     * @param valueName the new value name
     */
    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    /**
     * Gets the values.
     *
     * @return the values
     */
    public String[] getValues() {
        return values;
    }

    /**
     * Sets the values.
     *
     * @param values the new values
     */
    public void setValues(String[] values) {
        this.values = values;
    }
 

}
