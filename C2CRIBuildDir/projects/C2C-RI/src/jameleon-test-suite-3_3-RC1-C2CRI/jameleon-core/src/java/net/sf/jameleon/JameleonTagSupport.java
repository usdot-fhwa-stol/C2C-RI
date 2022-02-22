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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jameleon.bean.Attribute;
import net.sf.jameleon.bean.FunctionalPoint;
import net.sf.jameleon.exception.JameleonException;
import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.function.Attributable;
import net.sf.jameleon.function.AttributeBroker;
import net.sf.jameleon.function.ContextHelper;
import net.sf.jameleon.util.JameleonUtility;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.jelly.DynaTag;
import org.apache.commons.jelly.JellyTagException;

/** 
 * <p><code>JameleonTagSupport</code> is an implementation of DynaTag. This tag
 * throws the variable name as the XML attribute into the context. It then
 * attempts to call to corresponding set method. There is currently no checking
 * on whether the set method was called or not. If it's there, then it gets
 * called. Calling the set method is mostly for backward compatability. However,
 * it can be used for set methods that need to do more than put the variable in the
 * context.</p>
 * <p>
 * Currently, this class is only used by FunctionTag. It was intended to be the base class for all
 * Jameleon tags.
 * </p>
 */
public abstract class JameleonTagSupport extends LocationAwareTagSupport implements DynaTag, Attributable {

    /**
     * A map of class attributes and their corresponding types.
     */
    protected Map attributes = null;
    /**
     * A list of variable names that were stored in the context.
     */
    protected List contextVars = new LinkedList();
    /**
     * Used to transfer context variables to instance variables.
     */
    protected AttributeBroker broker;
    /**
     * Represents this tag's attributes
     */
    protected FunctionalPoint fp;

    protected List unsupportedAttributes = new LinkedList();

    public JameleonTagSupport(){
        super();
        broker = new AttributeBroker(this);
        fp = loadFunctionalPoint();
        //This calls describeAttributes()
        broker.setUp();
    }

    public FunctionalPoint loadFunctionalPoint(){
        FunctionalPoint functionalPoint = null;
        try{
            functionalPoint = JameleonUtility.loadFunctionalPoint(this.getClass().getName(), this);
        }catch(JameleonScriptException jse){
            throw new JameleonScriptException(jse.getMessage(), this);
        }
        return functionalPoint;
    }

    public List getUnsupportedAttributes(){
        return unsupportedAttributes;
    }

    protected void testForUnsupportedAttributesCaught(){
        if (unsupportedAttributes.size() > 0) {
            Iterator it = unsupportedAttributes.iterator();
            String msg = "The following attributes are not supported by this tag:\n";
            while (it.hasNext()) {
                msg += "'"+it.next()+"'";
                if (it.hasNext()) {
                    msg += ", ";
                }
            }
            msg += ".\n This could also be that Jameleon could not find the tag's corresponding .dat file\n"+
                   "that is generated when the tags are registered and should be in the CLASSPATH.\n\n";
            throw new JameleonScriptException(msg, this);
        }
    }

    /** 
     * Sets an attribute value of this tag before the tag is invoked
     */
    public void setAttribute(String name, Object value) {
        if (fp != null) {
            Attribute attr = fp.getAttribute(name);
            if (attr != null) {
                if (attr.isInstanceVariable()){
                    if (!attr.isContextVariable() ||
                        name.equals(attr.getName()) ) {
                        try{
                            broker.setConsumerAttribute(attr, value);
                        }catch(JameleonException je){
                            throw new JameleonScriptException(je, this);
                        }
                        attr.setValue(value);
                    } else if (attr.isContextVariable()){
                        setVariableInContext(attr.getContextName(), value);
                    }
                }else{
                    try{
                        //There is currently no reasonable way through the commons beanutils API to know whether the method was set or not.
                        //So I have to assume it was indeed set.
                        BeanUtils.copyProperty(this, name, value);

                        //This won't get set because it may not set a context variable
                        fp.getAttributes().put(name, attr);
                        attr.setValue(value);
                    }catch(Exception e){
                        throw new JameleonScriptException(e, this);
                    }
                }
            }else{
                unsupportedAttributes.add(name);
            }
        }else{
            unsupportedAttributes.add(name);
        }
    }

    protected void setVariableInContext(String name, Object value){
        context.setVariable(name,value);
        contextVars.add(name);
    }

    protected void cleanVariablesInContext(){
        ContextHelper.removeVariables(context, contextVars);
    }

    /** 
     * Helper method which allows derived tags to access the attributes
     * associated with this tag
     * @return the context of the tag.
     */
    protected Map getAttributes() {
        return context.getVariables();
    }

    public AttributeBroker getAttributeBroker(){
        return broker;
    }

    /**
     * Simply returns the context for this tag
     */
    protected Map createAttributes() {
        return context.getVariables();
    }

    /**
     * Gets the attributes or fields of the tag
     */
    protected Map getClassAttributes(){
        if (attributes == null) {
            attributes = new HashMap();
            Class clzz = this.getClass();
            do {
                Field[] fields = clzz.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    attributes.put(fields[i].getName(),fields[i].getType());
                }
                clzz = clzz.getSuperclass();
            }while(!clzz.equals(JameleonTagSupport.class));
        }
        return attributes;
    }

    /**
     * @return the type of the given attribute. If we can't figure out
     * the type of variable, simply return Object.class
     * Required for dyna tag support.
     */
    public Class getAttributeType(String name) throws JellyTagException {
        getClassAttributes();
        Class clzz = Object.class;
        if (attributes.containsKey(name)) {
            clzz = (Class)attributes.get(name);
        }
        return clzz;
    }

    protected void resetFunctionalPoint(){
        cleanVariablesInContext();
        Map attrs = fp.getAttributes();
        Iterator it = attrs.keySet().iterator();
        String key;
        Attribute attr;
        Field f = null;
        while (it.hasNext()) {
            key = (String)it.next();
            attr = (Attribute)attrs.get(key);
            if (attr.isInstanceVariable()) {
                f = broker.getConsumerField(attr);
                if (f.getType().isPrimitive()) {
                    broker.setConsumerAttributeAsPrimitive(f, attr.getDefaultValue());
                }else{
                    broker.setConsumerAttributeAsObject(f, attr.getDefaultValue());
                }
            }else if (attr.getDefaultValue() != null){
                try{
                    //There is currently no reasonable way through the commons beanutils API to know whether the method was set or.
                    //So I have to assume it was indeed set.
                    BeanUtils.copyProperty(this, key, attr.getDefaultValue());
                }catch(Exception e){
                    throw new JameleonScriptException(e, this);
                }
            }
            attr.setValue(null);
        }
    }

    public FunctionalPoint getFunctionalPoint(){
        return fp;
    }

    // -------------- Begin Attributable methods
    public void describeAttributes(AttributeBroker broker) {
        if (fp != null) {
            Map attrs = fp.getAttributes();
            if (attrs != null) {
                Set keys = attrs.keySet();
                if (keys != null) {
                    Iterator it = keys.iterator();
                    while (it != null && it.hasNext()) {
                        broker.registerAttribute((Attribute)fp.getAttributes().get(it.next()));
                    }
                }
            }
        }
    }

}
