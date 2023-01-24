package org.fhwa.c2cri.gui.propertyeditor;

public class ParameterSymbol extends Parameter {
        
    String[] allowedValues;
    
    /**
     * Instantiates a new parameter symbol.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param name the name
     * @param value the value
     * @param allowedValues the allowed values
     * @param editable the editable
     * @param doc the doc
     */
    public ParameterSymbol(String name, String value, String[] allowedValues, boolean editable, String doc){
        super(name, value, editable, doc);
        this.allowedValues = allowedValues;
        type = "Symbol";
    }
    
    public String[] getAllowedValues (){
        return allowedValues;
    }
    
    public String toText(){
        //System.out.println("\t\tadding parameter " + name);
		String newLine = System.getProperty("line.separator");

        String s = "#ALLOWED VALUES = ";
        
        //if(allowedValues[0] != null) {
        try{
            s += allowedValues[0];
            for(int i=1; i < allowedValues.length; i++) s+= ',' + allowedValues[i];
        } catch(NullPointerException e){s = "#NO ALLOWED VALUES";}
        //}
        
        //else s = "#NO ALLOWED VALUES";
        
        return toTextType() + newLine + s + newLine + toTextValue();
    }
    
	/**
	 * @param allowedValues The allowedValues to set.
	 */
	public void setAllowedValues(String[] allowedValues) {
		this.allowedValues = allowedValues;
	}
}


