package org.fhwa.c2cri.gui.propertyeditor;

import java.io.File;

import javax.swing.JFileChooser;

/**
 * The Class ParameterFile.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class ParameterFile extends Parameter {
    
    /** The Constant FILE. */
    public static final int FILE = JFileChooser.FILES_ONLY;
    
    /** The Constant DIR. */
    public static final int DIR = JFileChooser.DIRECTORIES_ONLY;
    
    /** The ftype. */
    int ftype;   
    
    /**
     * Instantiates a new parameter file.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param name the name
     * @param value the value
     * @param ftype the ftype
     * @param editable the editable
     * @param doc the doc
     */
    public ParameterFile(String name, String value, int ftype, boolean editable, String doc){
        super(name, value, editable, doc);
        this.ftype = ftype;
        
        type = (ftype == FILE) ? "FILE" : "DIRECTORY" ;
        
    }
    
    /**
     * Gets the f type.
     *
     * @return the f type
     */
    public int getFType(){
        return ftype;
    }
    
	/**
	 * To text value.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @return the string
	 */
	public String toTextValue(){
		String tempValue = value;
		if(tempValue == null) tempValue = "";
		if(!tempValue.equals("")) {
			File tempFile = new File(value);
			if(tempFile != null) {
				tempValue = tempFile.toURI().toString();
			}
		}
		return name + " = " + tempValue;
	}
}
