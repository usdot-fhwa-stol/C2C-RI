package org.fhwa.c2cri.gui.testmodel.testcasedata.editor;

import java.io.File;

import javax.swing.JFileChooser;

public class ParameterFile extends Parameter {
    
    public static final int FILE = JFileChooser.FILES_ONLY;
    public static final int DIR = JFileChooser.DIRECTORIES_ONLY;
    
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
    
    public int getFType(){
        return ftype;
    }
    
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
