/*
    Jameleon - An automation testing tool.
    Copyright (C) 2003 Christian W. Hargraves (engrean@hotmail.com)
    
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.
    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    Lesser General Public License for more details.
    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111AssertLevel.NO_FUNCTION07 USA
*/
package net.sf.jameleon.bean;

import net.sf.jameleon.XMLable;

import org.apache.commons.jelly.expression.Expression;

public class Attribute implements XMLable, Cloneable{
    private static final long serialVersionUID = 1L;
   /**
     * The attribute's name
     */
    protected String name;
    /**
     * A description about how the attribute is used
     */
    protected String description;
    /**
     * The type of the attribute
     */
    protected String type;
    /**
     * Whether this attribute is required or not
     */
    protected boolean required;
    /**
     * The value of the attribute
     */
    protected Object value;
    /**
     * The default value of the attribute
     */
    protected String defaultValue;
    /**
     * The name of this attribute's instance variable in the function tag. Used with reflection to magically set the instance variable
     */
    protected String contextName;
    /**
     * Describes whether this attribute represents an instance variable or set method. If both a set method and instance variable exist for
     * this attribute, then the set method should be called, and this should be set to <code>false</code> 
     */
    protected boolean instanceVariable;
    /**
     * Used to keep track if whether the value of this attribute was set or not
     */
    protected boolean valueSet = false;

    /**
     * Default constructor only used to initialize variables
     */
    public Attribute() {
        name = new String();
        description = new String();
        type = new String();
        required = false;
        contextName = new String();
    }

    /**
     * @return The attribute's name
     */
    public String getName(){
        return name;
    }

    /**
     * Sets the attribute's name
     * @param name - The attribute's name
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * @return A description about how the attribute is used
     */
    public String getDescription(){
        return description;
    }

    /**
     * Sets the description about how the attribute is used
     * @param description - The description about how the attribute is used
     */
    public void setDescription(String description){
        this.description = description;
    }

    /**
     * @return The type of the attribute
     */
    public String getType(){
        return type;
    }

    /**
     * Sets the type of the attribute
     * @param type - The type of the attribute
     */
    public void setType(String type){
        this.type = type;
    }

    /**
     * @return <code>true</code> if this attribute is required
     */
    public boolean isRequired(){
        return required;
    }

    /**
     * @return <code>true</code> if the value of the attribute was set
     */
    public boolean isValueSet(){
        return valueSet;
    }

    /**
     * Sets the attribute to a required or non-required attribute
     * @param required - <code>true</code> if this attribute is required
     */
    public void setRequired(boolean required){
        this.required = required;
    }

    /**
     * @return The attribute's value
     */
    public Object getValue(){
        return value;
    }

    /**
     * Sets the attribute's value
     * @param value - The attribute's value
     */
    public void setValue(Object value){
        this.value = value;
        if (value != null) {
            valueSet = true;
        }else{
            valueSet = false;
        }
    }

    /**
     * @return The attribute's default value
     */
    public String getDefaultValue(){
        return defaultValue;
    }

    /**
     * Sets the attribute's default value
     * @param defaultValue - The attribute's default value
     */
    public void setDefaultValue(String defaultValue){
        this.defaultValue = defaultValue;
    }

    /**
     * @return The displayable value of the attribute
     */
    public String getDisplayedValue(){
    	String returnValue = value.toString();
    	if (value instanceof Expression && value != null){
    		returnValue = ((Expression)value).getExpressionText();
    	}
        return returnValue;
    }

    /**
     * Gets the name of the context variable that the value of the this attribute will be bound to
     * @return The name of context variable that the value of this attribute will be bound to
     */
    public String getContextName(){
        return contextName;
    }

    /**
     * Sets the name of the context variable that the value of the this attribute will be bound to
     * @param contextName - The name of context variable that the value of this attribute will be bound to
     */
    public void setContextName(String contextName){
        this.contextName = contextName;
    }

    /**
     * Tells if this attribute represents an instance variable or set method
     * @return <code>true</code> if this attribute represents an instance variable and <code>false</code>
     *         if it represents a set method. If both a set method and instance variable exist for
     *         this attribute, then the set method should be called, and this should return <code>false</code>
     */
    public boolean isInstanceVariable(){
        return instanceVariable;
    }

    /**
     * Tells if this attribute represents something that is bound to a context variable
     * @return true if the contextName is set
     */
    public boolean isContextVariable(){
        boolean isContextVariable = false;
        if (contextName != null && contextName.length() > 0 ) {
            isContextVariable = true;
        }
        return isContextVariable;
    }

    /**
     * Sets this attribute to an instance variable <code>true</code> or a set method <code>false</code>
     * @param instanceVariable - Set to <code>true</code> if an instance variable or <code>false</code>
     *        if this attribute represents a set method. If both a set method and instance variable exist for
     *        this attribute, then the set method should be called, and this should be set to <code>false</code>
     */
    public void setInstanceVariable(boolean instanceVariable){
        this.instanceVariable = instanceVariable;
    }

    public String toXML(){
        StringBuffer str = new StringBuffer();
        str.append("\t\t\t\t<attribute>\n");
        str.append("\t\t\t\t\t<attribute-name>").append(name).append("</attribute-name>\n");
        str.append("\t\t\t\t\t<attribute-instancevariable>").append(new Boolean(instanceVariable)).append("</attribute-instancevariable>\n");
        str.append("\t\t\t\t\t<attribute-description>").append(description).append("</attribute-description>\n");
        str.append("\t\t\t\t\t<attribute-type>").append(type).append("</attribute-type>\n");;
        str.append("\t\t\t\t\t<attribute-required>").append(new Boolean(required)).append("</attribute-required>\n");
        str.append("\t\t\t\t\t<attribute-value>").append(value).append("</attribute-value>\n");
        str.append("\t\t\t\t\t<attribute-defaultvalue>").append(defaultValue).append("</attribute-defaultvalue>\n");
        str.append("\t\t\t\t\t<attribute-contextname>").append(contextName).append("</attribute-contextname>\n");
        str.append("\t\t\t\t</attribute>\n");
        return str.toString();
    }

    public String toString(){
        String str = null;
        if (contextName != null && contextName.length() > 0) {
            if (name != null && name.length() > 0) {
                str = name +" or "+contextName+"";
            }else{
                str = contextName;
            }
        }else if (name != null && name.length() > 0) {
            str = name;
        }
        return str;
    }
    
    public Object clone() throws CloneNotSupportedException {
    	Attribute attr = null;
    	try{
    		attr = (Attribute) super.clone();
    		attr.value = value;
    	}catch(CloneNotSupportedException cnse){
    		System.err.println("Could not create a clone of "+toXML());
    	}
    	return attr;
    }
}
