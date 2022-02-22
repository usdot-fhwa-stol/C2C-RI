package org.fhwa.c2cri.gui.propertyeditor;

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
}
