/*
    Jameleon - An automation testing tool..
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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * Used for mapping a List to another variable. 
 * The implementation of this class will be to decide if the List is a list of values or a List of variabl names.
 * NOTE:<br>
 * No setters or getters defined for the instances variables defined below. If these variables are to be defined 
 * in the tag as attributes, then in the subclass of this class, the setters and getters must be defined for the 
 * appropriate attributes.
 */
public abstract class VariableTag extends TagSupport {
    
    protected static final String LIST = "list";
    protected static final String STRING = "string";

    protected TestCaseTag tct;
    /**
     * The variable name to map from. This is the variable name is normally <b>not</b> supported by the function tag.
     */
    protected String fromVariable;
    /**
     * The value of the fromVariable.
     */
    protected String value;
    /**
     * The variable name to map the <code>fromVariable</code> to. This is the variable name <b>is</b> supported by the function tag.
     */
    protected String toVariable;
    /**
     * The type of the variable being stored. Currently, this only supports List and String (the default).
     */
    protected String variableType = STRING;

    /**
     * An implementation of the <code>doTag</code> method provided by the <code>TagSupport</code> class.
     * Maps the value in the <code>fromVariable</code> over to the original variable name, <code>toVariable</code>.
     */
    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException{
        //To support children elements
        init();
        validate();
        setVariableValue(toVariable, value);
    }

    public void setVariableValue(String toVariable, Object value){
        if ( variableType.equalsIgnoreCase(STRING) ) {
            context.setVariable(toVariable, value);
        }else if ( variableType.equalsIgnoreCase(LIST) ) {
            List l = null;
            Object to = context.getVariable(toVariable);
            if ( to != null && to instanceof List ) {
                l = (List)to;
            }else if ( to == null || to instanceof String ) {
                l = new ArrayList();
                if (to instanceof String ) {
                    //Add the original String type to the beginning of the list.
                    l.add(to);
                }
            }
            if (value instanceof List) {
                Iterator it = ((List)value).iterator();
                while (it.hasNext()) {
                    l.add(it.next());
                }
            }else{
                l.add(value);
            }
            setVariable(toVariable,l);
        }
    }

    /**
     * Used to validate everything is set up correctly.
     * @throws MissingAttributeException - When a required attribute isn't set.
     * @throws JellyTagException - When this tag is used out of context.
     */
    protected void validate()throws MissingAttributeException, JellyTagException{
        if (toVariable == null){
            throw new MissingAttributeException("toVariable is a required attribute!");
        }else if ( !(STRING.equalsIgnoreCase(variableType)) && !(LIST.equalsIgnoreCase(variableType)) ) {
            throw new MissingAttributeException("variableType must be set to either string or list!");
        }
    }

    protected void setVariable(String key, Object value){
        init();
        context.setVariable(key, value);
        if (tct != null){
            tct.getKeySet().add(key);
        }
    }

    protected void init(){
        if (tct == null) {
            Object obj =  findAncestorWithClass(TestCaseTag.class);
            if (obj != null){
                tct = (TestCaseTag)obj;
            }
        }
    }
}
