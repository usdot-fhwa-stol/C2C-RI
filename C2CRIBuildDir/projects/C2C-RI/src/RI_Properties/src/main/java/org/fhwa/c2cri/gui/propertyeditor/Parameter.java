package org.fhwa.c2cri.gui.propertyeditor;

/**
 * The Class Parameter.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class Parameter {
    
    /** The name. */
    protected String name;
	
	/** The value. */
	protected String value;
	
	/** The type. */
	protected String type = "WRONG TYPE";
	
	/** The comments. */
	protected String comments = "";
	
	/** The editable. */
	protected boolean editable;
	
	/** The doc. */
	protected String doc = "";
     
    /**
     * Instantiates a new parameter.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param name the name
     * @param value the value
     * @param editable the editable
     * @param doc the doc
     */
    public Parameter(String name, String value, boolean editable, String doc) {
        this.name     = name;
        this.value    = value;
        this.editable = editable;
        this.doc      = doc;
    }
    
    /**
     * Instantiates a new parameter.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param name the name
     * @param value the value
     * @param editable the editable
     * @param doc the doc
     * @param type the type
     */
    public Parameter(String name, String value, boolean editable, String doc, String type) {
        this(name, value, editable, doc);
        this.type = type;
    }
    
    /**
     * Checks if is editable.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is editable
     */
    public boolean isEditable(){
        return editable;
    }
    
	/**
	 * Gets the doc.
	 *
	 * @return the doc
	 */
	public String getDoc(){
		return doc;
	}

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName(){
        return name;
    }
    
    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType(){
        return type;
    }
    
    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    /**
     * Sets the comments.
     *
     * @param comments the new comments
     */
    public void setComments(String comments){
    	this.comments = comments;
    }
    
    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue(){
        return value;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return value;
    }
    
    /**
     * To text type.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the string
     */
    public String toTextType(){
		String newLine = System.getProperty("line.separator");
        return comments + "#PARAMETER TYPE = " + type + newLine + 
			"#EDITABLE = " + editable + newLine + "#DOCUMENTATION = " + doc;
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
        return name + " = " + value;
    }
    
    /**
     * To text.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the string
     */
    public String toText(){
		String newLine = System.getProperty("line.separator");
        return toTextType() + newLine + toTextValue();
    }
}
