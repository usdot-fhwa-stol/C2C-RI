/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2006 Christian W. Hargraves (engrean@hotmail.com)
    
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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon;

import net.sf.jameleon.function.FunctionTag;
import java.util.ArrayList;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * Used to give a function tag a parameter or a list of parameters.
 * 
 * For example:
 * <pre>
 *      &lt;param&gt;
 *          &lt;param-name&gt;someName&lt;/param-name&gt;
 *          &lt;param-value&gt;foo&lt;/param-value&gt;
 *          &lt;param-type&gt;text&lt;/param-type&gt;
 *      &lt;/param&gt;
 * </pre>
 * This tag adds itself to a list of param tags to the parent FunctionTag.
 * It's up to the FunctionTag to use the parameters. They can be accessed via
 * the {@link net.sf.jameleon.function.FunctionTag#getParams()} method.
 * 
 * @jameleon.function name="param"
 */
public class ParamTag extends TagSupport {
    
    /**
     * The value of the param.
     */
    protected ArrayList values;
    /**
     * The name of param.
     */
    protected String name;
    /**
     * The input type of the param (text, checkbox, listbox...)
     */
    protected String paramType;

    protected String fromVariable;

    public ParamTag(){
        super();
        values = new ArrayList();
    }

    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException{
        values = new ArrayList();
        //To support child elements
        invokeBody(out);
        validate();

        FunctionTag tag = (FunctionTag) findAncestorWithClass(FunctionTag.class);
        tag.addParam(this);        
    }

    /**
     * Used to validate everything is set up correctly.
     * @throws MissingAttributeException - When a required attribute isn't set.
     * @throws JellyTagException - When this tag is used out of context.
     */
    protected void validate()throws MissingAttributeException, JellyTagException{
        if (name == null){
            throw new MissingAttributeException("name is a required tag!");
        }
    }

    /**
     * @return The param type
     */
    public String getParamType(){
        return this.paramType;
    }

    /**
     * @return The first param value from the list of values
     */
    public String getValue(){
        return (String)values.get(0);
    }

    /**
     * @return The param values
     */
    public ArrayList getParamValues(){
        return values;
    }

    /**
     * Adds a value to the list of values supplied for this parameter.
     * @param value The param value
     */
    public void addParamValue(String value){
        values.add(value);
    }

    /**
     * @return The param name
     */
    public String getName(){
        return this.name;
    }

    /**
     * @return The param name
     */
    public String getParamName(){
        return this.name;
    }

    public void setParamName(String paramName){
        name = paramName;
    }

    public void setParamValue(String value){
        values = new ArrayList();
        values.add(value);
    }

    public void setParamType(String paramType){
        this.paramType = paramType;
    }

    public String getFromVariable(){
        return fromVariable;
    }

    public void setFromVariable(String fromVar){
        fromVariable = fromVar;
    }

}
