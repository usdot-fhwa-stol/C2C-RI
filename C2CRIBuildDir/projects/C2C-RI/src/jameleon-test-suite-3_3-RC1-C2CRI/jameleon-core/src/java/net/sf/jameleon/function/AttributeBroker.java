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
package net.sf.jameleon.function;

import net.sf.jameleon.JameleonTagSupport;
import net.sf.jameleon.bean.Attribute;
import net.sf.jameleon.exception.JameleonException;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.MissingAttributeException;

import java.io.File;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.lang.reflect.Field;

/**
 * The <code>AttributeBroker</code> class is used to copy values from
 * a <code>JellyContext</code> or the script directory to instance variables 
 * in an <code>Attributable</code>.
 */
public class AttributeBroker {
    /**
     * The instance of the Attributable to which the values will be transfered to
     */
    protected Attributable consumer;
    /**
     * A List of attributes or instance variables of the consumer
     */
    protected Map attributes = new HashMap();

    public static final boolean REQUIRED = true;
    public static final boolean NOT_REQUIRED = false;
    public static final boolean OPTIONAL = false;

    /**
     * An Attributable is required to instaniate this class.
     */
    public AttributeBroker(Attributable consumer) {
        this.consumer = consumer;
    }

    /**
     * Gets the attributes registered for this Attributable
     */
    public Map getAttributes(){
        return attributes;
    }

    /**
     * Add an attribute to the list of instance variables supported by the <code>consumer</code>
     */
    public void registerAttribute(Attribute attr) {
        if (attributes.get(attr.getName()) == null) {
            attributes.put(attr.getName(), attr);
        }
    }

    /**
     * Calls the <code>Attributable.describeAttributes()</code> method.
     * This needs to be called before anything else is called
     */
    public void setUp() {
        consumer.describeAttributes(this);
    }

    /**
     * Copy all variables from the context (JellyContext) to instances variables registered in the
     * <code>Attributable</code> instance.
     * @param context - The context that stores the <code>Attributable</code>-independent key/value
     *                  pairs which will be used to set the instance variables of the <code>Attributable</code>
     *                  to.
     */
    public void transferAttributes(JellyContext context) {
        if (attributes == null || attributes.size() == 0) return;
        Iterator it = attributes.keySet().iterator();
        Attribute attr = null;
        while (it.hasNext()) {
            attr = (Attribute)attributes.get(it.next());
            String name = attr.getName();
            if ( name != null && !attr.isValueSet() ) {
                if (attr.isInstanceVariable() && (attr.isContextVariable() || attr.getDefaultValue() != null)) {
                    Object value = getValueFromContext(context, attr.getContextName(), attr.getDefaultValue());
                    setConsumerAttribute(attr, value);
                }else if (!attr.isInstanceVariable() && attr.getValue() == null) {
                    //Only set the value if it hasn't already been set. This is basically for attributes that are 
                    //read in from external data sources (CSV, properties ... )
                    //This means the attribute is a set method. Basically, we are setting the value to the value 
                    //in the context.
                    attr.setValue(ContextHelper.getVariable(context, name));
                }
            }
        }
    }

    /**
     * Validates that all context variables marked as <code>required</code> are set.
     * @param context The JellyContext of the tag.
     * @throws MissingAttributeException if any required attributes were not set.
     */
    public void validate(JellyContext context) throws MissingAttributeException {
        Iterator it = attributes.keySet().iterator();
        StringBuffer errors = new StringBuffer();
        Attribute attr = null;
        while (it.hasNext()) {
            attr = (Attribute)attributes.get(it.next());
            if (attr.isRequired() && !attr.isValueSet()) {
                if (getAttributeValue(attr, context) == null) {
                    if (errors.length() > 0) {
                        errors.append(",");
                    }
                    errors.append(attr);
                }
            }
        }
        if (errors.length() > 0) {
            throw new MissingAttributeException(errors.toString());
        }
    }

    // Implementation
    
    /**
     * Attempts to get the value for the given key (<code>contextName</code>) from the context.
     * If a value is not found from the context, then a defaultValue is used.
     * @param context - A JellyContext of key/value pairs.
     * @param contextName - The key from the context
     * @param defaultValue - A value to return only if the variable is not set in the context.
     * @return The value for the given context variable.
     */
    protected Object getValueFromContext(JellyContext context, String contextName, String defaultValue) {
        Object value = ContextHelper.getVariable(context, contextName);

        if (value == null) {
            value = defaultValue;
        }
        return value;
    }
    
    /**
     * Gets the real-time value of <code>attr</code> for the consumer
     * @param attr - The attr that represents the property's value to be returned.
     * @return The real-time value of the attribute.
     */
    public Object getAttributeValue(Attribute attr, JellyContext context){
        Object value = null;
        if (attr.isInstanceVariable()) {
            value = getAttributeValueFromInstance(attr);
        }else if (attr.getContextName() != null && attr.getContextName().length() > 0) {
            value = ContextHelper.getVariable(context, attr.getContextName());
        }else{
            //Used to support set methods. This only works when the method  is set via the 
        	//script directly.
            //To always keep the value up to date call setAttribute();
            value = attr.getValue();
        }
        if (value == null) {
            value = attr.getDefaultValue();
        }
        return value;
    }

    public Field getConsumerField(Attribute attr){
        Field field = null;
        String name = attr.getName();
        Class c = consumer.getClass();
        //Get a field matching the given name by searching through all super classes that extend
        //JameleonTagSupport
        while ( c != null &&
                ! c.equals(JameleonTagSupport.class) ) {
            try{    
                field = c.getDeclaredField(name);
                field.setAccessible(true);
                break;
            }catch(NoSuchFieldException nsfe){
                c = c.getSuperclass();
            }
        }
        return field;
    }

    protected Object getAttributeValueFromInstance(Attribute attr){
        Object value = null;
        if (attr.isInstanceVariable()) {
            Field field = getConsumerField(attr);
            if (field != null) {
                try{
                    Class type = field.getType();
                    value = field.get(consumer);
                    if (type.isPrimitive()) {
                        boolean nullValue = false;
                        if (value instanceof Number && ((Number)value).byteValue() == 0) {
                            nullValue = true;
                        }else if (value instanceof Boolean && !((Boolean)value).booleanValue() ) {
                            nullValue = true;
                        }else if (value instanceof Character && ((Character)value).charValue() == 0) {
                            nullValue = true;
                        }
                        if (nullValue) {
                            value = null;
                        }
                    }
                }catch (IllegalAccessException iae){
                    throw new JameleonException("Please report this problem along with as much info as you can give to the Jameleon bugtracker.", iae);
                }
            }
        }
        return value;
    }

    /**
     * Sets the instance variable of the <code>Attributable</code> to the value with the correct type.
     * @param attr - The Attribute representing the variable to set.
     * @param objValue - The value to the instance variable to.
     */
    public void setConsumerAttribute(Attribute attr, Object objValue) {
        String name = attr.getName();
        Class cOrig = consumer.getClass();
        Field f = getConsumerField(attr);
        boolean valueSet = true;
        if (f != null){
            //Found a field, now let's set it to objValue after finding it's appropriate type
            Class type = f.getType();
            if (type.isPrimitive()) {
                //Looks like the instance is a primitive
                if (objValue != null) {
                    setConsumerAttributeAsPrimitive(f, objValue);
                }else{
                    valueSet = false;
                }
            }else if ( objValue != null) {
                //Looks like the instance is an object.
                setConsumerAttributeAsObject(f, objValue);
            }else{
                valueSet = false;
            }
        }else{
            throw new JameleonException("Instance variable " + name + " does not exist in " + cOrig);
        }
        if (valueSet) {
            attr.setValue(objValue);
        }
    }

    public void setConsumerAttributeAsPrimitive(Field f, Object objValue){
        if (f != null) {
            try{
                Class type = f.getType();
                Object o = f.get(consumer);
                String value = null;
                if (type.isPrimitive()) {
                    if (objValue != null) {
                        value = (String)objValue;
                    }else{
                        value = "0";
                    }
                    if (o instanceof Byte) {
                        f.setByte(consumer,Byte.parseByte(value));
                    }else if (o instanceof Integer) {
                        f.setInt(consumer, Integer.parseInt(value));
                    }else if (o instanceof Long) {
                        f.setLong(consumer,Long.parseLong(value));
                    }else if (o instanceof Short) {
                        f.setShort(consumer, Short.parseShort(value));
                    }else if (o instanceof Double) {
                        f.setDouble(consumer, Double.parseDouble(value));
                    }else if (o instanceof Float) {
                        f.setFloat(consumer,Float.parseFloat(value));
                    }else if (o instanceof Boolean) {
                        if ("true".equals(value) || "yes".equals(value)) {
                            f.setBoolean(consumer, true);
                        }else{
                            f.setBoolean(consumer, false);
                        }
                    }else if (o instanceof Character) {
                        char cValue = '\u0000';
                        if (objValue != null) {
                            cValue = value.charAt(0);
                        }
                        f.setChar(consumer, cValue);
                    }else{
                        //How did I get here?
                        throw new JameleonException(f.getName() + " of type " + f.getType() +
                                                           ", not a supported type");
                    }
                }
            } catch (IllegalAccessException e) {
                throw new JameleonException("Instance variable " + f.getName() + " is not settable (may be final) in " + consumer.getClass(), e);
            }
        }else{
            throw new JameleonException("Cannot set a null field!");
        }

    }

    public void setConsumerAttributeAsObject(Field f, Object objValue){
        if (f != null) {
            try{
                Class type = f.getType();
                if ( objValue != null) {
                    //Looks like the instance is an object.
                    if (objValue instanceof String) {
                        if (type.getName().equals(List.class.getName())) {
                            //If objvalue isn't a List, let's make it one.
                            f.set(consumer,ContextHelper.makeList(objValue));
                        }else if (type.getName().equals(Boolean.class.getName())) {
                            f.set(consumer, Boolean.valueOf((String)objValue));
                        }else if (type.getName().equals(Byte.class.getName())){
                            f.set(consumer, Byte.valueOf((String)objValue));
                        }else if (type.getName().equals(Short.class.getName())){
                            f.set(consumer, Short.valueOf((String)objValue));
                        }else if (type.getName().equals(Character.class.getName())){
                            f.set(consumer, new Character(((String)objValue).charAt(0)));
                        }else if (type.getName().equals(Integer.class.getName())){
                            f.set(consumer, Integer.valueOf((String)objValue));
                        }else if (type.getName().equals(Long.class.getName())){
                            f.set(consumer, Long.valueOf((String)objValue));
                        }else if (type.getName().equals(Float.class.getName())){
                            f.set(consumer, Float.valueOf((String)objValue));
                        }else if (type.getName().equals(Double.class.getName())){
                            f.set(consumer, Double.valueOf((String)objValue));
                        }else if (type.getName().equals(File.class.getName())){
                            f.set(consumer, new File((String)objValue));
                        }else{
                            f.set(consumer, objValue);
                        }
                    }else{
                        f.set(consumer, objValue);
                    }
                }else{
                    f.set(consumer, objValue);
                }
            } catch (IllegalAccessException e) {
                throw new JameleonException("Instance variable " + f.getName() + " is not settable (may be final) in " + consumer.getClass(), e);
            }
        }else{
            throw new JameleonException("Cannot set a null field!");
        }
    }

}
