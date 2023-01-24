package org.fhwa.c2cri.gui.testmodel.testcasedata.editor;

/**
 * The Class ParameterString.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class ParameterString extends Parameter {

    /**
     * Instantiates a new parameter string.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param name the name
     * @param value the value
     * @param editable the editable
     * @param doc the doc
     */
    public ParameterString(String name, String value, boolean editable, String doc) {
        super(name, value, editable, doc);
        type = "String";
    }

    /**
     * Instantiates a new parameter string.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param name the name
     * @param value the value
     * @param editable the editable
     * @param doc the doc
     * @param min the min
     * @param max the max
     */
    public ParameterString(String name, String value, boolean editable, String doc, String min, String max) {
        super(name, value, editable, doc, "String", min, max);
//        type = "String";
    }

}
