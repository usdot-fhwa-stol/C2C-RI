package org.fhwa.c2cri.gui.propertyeditor;

public class ParameterBoolean extends ParameterSymbol {
    
    public static String[] booleanValues = {"true", "false"};
    
    /**
     * Instantiates a new parameter boolean.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param name the name
     * @param value the value
     * @param editable the editable
     * @param doc the doc
     */
    public ParameterBoolean(String name, String value, boolean editable, String doc){
        super(name, value, booleanValues, editable, doc);
        type = "BOOLEAN";
    }
    
    public String toText(){
        //System.out.println("\t\tadding parameter " + name);
		String newLine = System.getProperty("line.separator");
        return toTextType() + newLine + toTextValue();
    }

}
