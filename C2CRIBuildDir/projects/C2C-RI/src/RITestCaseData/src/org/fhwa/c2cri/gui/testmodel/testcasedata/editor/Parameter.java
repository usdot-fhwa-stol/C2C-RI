package org.fhwa.c2cri.gui.testmodel.testcasedata.editor;

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
	
	/** The min. */
	protected String min = "";
	
	/** The max. */
	protected String max = "";
	
	/** The comments. */
	protected String comments = "";
	
	/** The editable. */
	protected boolean editable;
	
	/** The doc. */
	protected String doc = "";

	/** Parameter is provided by a baseline source. */
	protected boolean baselineProvided;
        
        private boolean userOverwritten=false;
        
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
     * @param min the min
     * @param max the max
     */
    public Parameter(String name, String value, boolean editable, String doc, String type, String min, String max) {
        this(name, value, editable, doc);
        this.type = type;
        this.min = min;
        this.max = max;
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

    /**
     * Gets the max.
     *
     * @return the max
     */
    public String getMax() {
        return max;
    }

    /**
     * Sets the max.
     *
     * @param max the new max
     */
    public void setMax(String max) {
        this.max = max;
    }

    /**
     * Gets the min.
     *
     * @return the min
     */
    public String getMin() {
        return min;
    }

    /**
     * Sets the min.
     *
     * @param min the new min
     */
    public void setMin(String min) {
        this.min = min;
    }

    /**
     * Gets the flag of whether the parameter was provided by the baseline source.
     * @return flag indicating if this parameter was defined by the baseline source. 
     */
    public boolean isBaselineProvided() {
        return baselineProvided;
    }

    public void setOverwritten(boolean flag){
        userOverwritten= true;
    }
    
    public boolean isOverwritten(){
        return userOverwritten;
    }
    
/**
 * Sets the flag of whether the parameter was provided by the baseline source.
 * 
 * @param baselineProvided flag indicating if this parameter was defined by the baseline source.
 */
    public void setBaselineProvided(boolean baselineProvided) {
        if (!baselineProvided)userOverwritten = true;
        this.baselineProvided = baselineProvided;
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
                        (((min != null)&&!min.trim().isEmpty())? "#MIN = " + min + newLine:"") +
                        (((max != null)&&!max.trim().isEmpty())? "#MAX = " + max + newLine:"") +
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
