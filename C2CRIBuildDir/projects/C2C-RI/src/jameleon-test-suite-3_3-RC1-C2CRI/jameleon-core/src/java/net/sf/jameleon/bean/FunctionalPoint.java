/*
    Jameleon - An automation testing tool.
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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111AssertLevel.NO_FUNCTION07 USA
*/
package net.sf.jameleon.bean;

import net.sf.jameleon.XMLable;
import net.sf.jameleon.exception.JameleonException;

import java.util.*;

/**
 * This class represents a Functional Point in a TestCase. This is currently used only for test case
 * documentation generation. Please see the docs on the instance variables for a full understanding of what
 * a FunctionalPoint is
 */
public class FunctionalPoint implements XMLable, Cloneable {
    private static final long serialVersionUID = 1L;
    /**
     * The name of the developer that wrote this <code>FunctionalPoint</code>
     */
    protected String author;
    /**
     * The applications supported by this functional point
     */
    protected ArrayList applications;
    /**
     * The name of the class that this tag represents.
     */
    protected String className;
    /**
     * The tags to be used in the test case macro language
     */
    protected ArrayList tagNames;
    /**
     * A short summary about how this functional can be used
     */
    protected String description;
    /**
     * The first line of the description.
     */
    protected String shortDescription;
    /**
     * The type of the functional point. Valid values are <code>action</code>,
     * <code>validation</code>, <code>navigation</code>
     */
    protected String type;
    /**
     * The description of how the functional point is being used
     */
    protected String functionId;
    /**
     * A list of attributes that this functional point uses
     */
    protected Map attributes;
    /**
     * A list of steps that this FunctionalPoint uses during execution
     */
    protected ArrayList steps;

    /**
     * Default constructor only used to initialize variables
     */
    public FunctionalPoint() {
        author = new String();
        tagNames = new ArrayList();
        description = new String();
        shortDescription = new String();
        type = new String();
        attributes = new LinkedHashMap();
        steps = new ArrayList();
        applications = new ArrayList();
        className = new String();
    }

    /**
     * Adds an <code>Attribute</code> to the list of attributes used by this <cod>FunctionalPoint</code>
     * @param attr - An attribute used by this <code>FunctionalPoint</code>
     */
    public void addAttribute(Attribute attr){
        if (attr == null || attr.getName() == null || attr.getName().length() < 1) {
            throw new JameleonException("Attribute must have a name");
        }
        attributes.put(attr.getName(), attr);
    }

    /**
     * Adds an application to the list of applications supported by this <cod>FunctionalPoint</code>
     * @param applicationName - The name of the application supported
     */
    public void addApplication(String applicationName){
        if (applicationName != null && applicationName.trim().length() > 0 && !applications.contains(applicationName)) {
            applications.add(applicationName);
        }
    }

    /**
     * Adds a step to the list of steps required to execute this <cod>FunctionalPoint</code>
     * @param step - A simple instruction to execute this functional point
     */
    public void addStep(String step){
        steps.add(step);
    }

    /**
     * Gets a particular attribute. It first tries by searching for the name (method name or instance name), 
     * then by searching via the contextName
     * @return an attribute matching the given name or contextName
     */
    public Attribute getAttribute(String name){
        Attribute attr = (Attribute)attributes.get(name);
        Attribute attrTmp = null;
        if (attr == null) {
            Iterator it = attributes.keySet().iterator();
            while (it.hasNext() && attr == null) {
                attrTmp = (Attribute)attributes.get(it.next());
                if (attrTmp.getContextName() != null && attrTmp.getContextName().equals(name)) {
                    attr = attrTmp;
                }
            }
        }
        return attr;
    }

    /**
     * Gets the applications supported by this functional point
     * @return The applications supported by this functional point
     */
    public ArrayList getApplications(){
        return applications;
    }

    /**
     * @return The name of the developer that wrote this <code>FunctionalPoint</code>
     */
    public String getAuthor(){
        return author;
    }

    /**
     * Sets the name of the developer that wrote this <code>FunctionalPoint</code>
     * @param author - The name of the developer that wrote this <code>FunctionalPoint</code>
     */
    public void setAuthor(String author){
        this.author = author;
    }

    /**
     * @return The name of the class that represents this <code>FunctionalPoint</code>
     */
    public String getClassName(){
        return className;
    }

    /**
     * Sets the name of the class that represents this <code>FunctionalPoint</code>
     * @param className - the name of the class that represents this <code>FunctionalPoint</code>
     */
    public void setClassName(String className){
        this.className = className;
    }

    /**
     * @return The default tag name to be used in the test case macro language
     */
    public String getDefaultTagName(){
        return (tagNames.size() > 0) ? (String)tagNames.get(0) : "";
    }

    /**
     * Gets all tag names registered for this functional point
     * Functional points can have multiple tag names
     * @return The tag name to be used in the test case macro language
     */
    public List getTagNames(){
        return tagNames;
    }

    /**
     * Adds a tag name - a name used in the test case macro language
     * Functional points can have multiple tag names
     * @param tagName - The tag name to be used in the test case macro language
     */
    public void addTagName(String tagName){
        tagNames.add(tagName);
    }

    /**
     * @return A short summary about how this functional can be used
     */
    public String getDescription(){
        return description;
    }

    /**
     * Sets a short summary about how this functional can be used
     * @param description - A short summary about how this functional can be used
     */
    public void setDescription(String description){
        this.description = description;
    }

    /**
     * Gets the first line of the javadoc class comments.
     * @return The first line of the javadoc class comments.
     */
    public String getShortDescription(){
        return shortDescription;
    }

    /**
     * Sets the first line of the description
     * @param shortDescription - the first line on the javadoc class comment.
     */
    public void setShortDescription(String shortDescription){
        this.shortDescription = shortDescription;
    }

    /**
     * @return A short summary about how this functional point is used
     */
    public String getFunctionId(){
        return functionId;
    }

    /**
     * Sets a short summary about how this functional point is used
     * @param functionId - A short summary about how this functional point is used
     */
    public void setFunctionId(String functionId){
        this.functionId = functionId;
    }

    /**
     * @return The type of the functional point
     * @throws JameleonException if the type is set and it isn't <code>action, validation, nor navigation</code>
     */
    public String getType() throws JameleonException{
        if (!( type == null ||
               "".equals(type) ||
               "action".equalsIgnoreCase(type) || 
               "validation".equalsIgnoreCase(type) ||
               "navigation".equalsIgnoreCase(type) ||
               "session".equalsIgnoreCase(type)) ) {
            throw new JameleonException(type +" is not a valid functional point type."+
                                               " Only action, validation, and navigation are valid types");
        }
        return this.type;
    }

    /**
     * Sets he type of the functional point
     * @param type - Valid values are <code>action</code>, <code>validation</code>, <code>navigation</code>
     */
    public void setType(String type){
        this.type = type;
    }

    /**
     * @return A list of attributes that this functional point uses
     */
    public Map getAttributes(){
        return attributes;
    }

    /**
     * Sets A list of attributes that this functional point uses
     * @param attributes A list of attributes that this functional point uses
     */
    public void setAttributes(Map attributes){
        this.attributes = attributes;
    }

    /**
     * @return A list of steps that this FunctionalPoint uses during execution
     */
    public List getSteps(){
        return steps;
    }

    /**
     * Sets a list of steps that this FunctionalPoint uses during execution
     * @param steps - A list of steps that this FunctionalPoint uses during execution
     */
    public void setSteps(ArrayList steps){
        this.steps = steps;
    }
    
    public Object clone() throws CloneNotSupportedException {
    	FunctionalPoint fp = null;
    	try{
    		fp = (FunctionalPoint)super.clone();
    		fp.applications = (ArrayList) applications.clone();
    		fp.steps = (ArrayList)steps.clone();
    		fp.tagNames = (ArrayList)tagNames.clone();
    		fp.attributes = new LinkedHashMap(attributes.size());
    		Attribute attr;
    		for (Iterator it = attributes.keySet().iterator(); it.hasNext(); ){
    			attr = (Attribute)attributes.get(it.next());
    			attr = (Attribute)attr.clone();
    			fp.addAttribute(attr);
    		}
    	}catch(CloneNotSupportedException cnse){
    		throw new JameleonException("Could not clone this tag " + fp.getDefaultTagName() +":", cnse);
    	}
    	return fp;
    }

    public FunctionalPoint cloneFP(){
        FunctionalPoint fp;
        try{
            fp = (FunctionalPoint)clone();
        }catch(CloneNotSupportedException cnse){
            fp = this;
        }
        return fp;
    }

    public String toXML(){
        Iterator it = null;
        StringBuffer str = new StringBuffer();
        str.append("\t\t<functional-point-info>\n");
        if (author != null && author.length() > 0) {
            str.append("\t\t\t<author>").append(author).append("</author>\n");
        }
        it = getTagNames().iterator();
        while (it.hasNext()) {
            str.append("\t\t\t<tag-name>").append(it.next()).append("</tag-name>\n");
        }
        if (description != null && description.length() > 0) {
            str.append("\t\t\t<description>").append(description).append("</description>\n");
        }
        if (functionId != null && functionId.length() > 0) {
            str.append("\t\t\t<function-id>").append(functionId).append("</function-id>\n");
        }
        if (type != null && type.length() > 0) {
            str.append("\t\t\t<type>").append(type).append("</type>\n");
        }
        if (steps.size() > 0) {
            str.append("\t\t\t<steps>\n");
            it = steps.iterator();
            while (it.hasNext()) {
                str.append("\t\t\t\t<step>").append(it.next()).append("</step>\n");
            }
            str.append("\t\t\t</steps>\n");
        }
        if (applications.size() > 0) {
            str.append("\t\t\t<applications>\n");
            it = applications.iterator();
            while (it.hasNext()) {
                str.append("\t\t\t\t<application>").append(it.next()).append("</application>\n");
            }
            str.append("\t\t\t</applications>\n");
        }
        if (attributes.size() > 0) {
            it = attributes.keySet().iterator();
            str.append("\t\t\t<attributes>\n");
            while (it.hasNext()) {
                str.append(((XMLable)attributes.get(it.next())).toXML());
            }
            str.append("\t\t\t</attributes>\n");
        }
        str.append("\t\t</functional-point-info>\n");
        return str.toString();
    }

    /**
     * @return The first (the default) tag name
     */
    public String toString(){
        return getDefaultTagName();
    }

    public boolean equals(Object obj){
        boolean equals = false;
        if (obj instanceof FunctionalPoint) {
            List otherTagNames = (((FunctionalPoint)obj).getTagNames());
            equals = getTagNames().size() == otherTagNames.size();
            for (Iterator it = otherTagNames.iterator(); it.hasNext();) {
                equals &= getTagNames().contains(it.next());
            }
        }
        return equals;
    }

    public int hashCode(){
        return ( getTagNames().size() + attributes.size() + steps.size() );
    }

}