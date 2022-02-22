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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111AssertLevel.NO_FUNCTION07 USA
*/
package net.sf.jameleon.bean;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.*;
import net.sf.jameleon.exception.JameleonException;
import net.sf.jameleon.util.InstanceSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Javadoc2Bean {

    protected File sourceDir;
    protected String isA = "net.sf.jameleon.function.FunctionTag";
    protected JavaDocBuilder docBuilder;
    protected Map serializedFiles = new HashMap();

    /**
     * Default constructor only used to initialize variables
     */
    public Javadoc2Bean() {
    	docBuilder = new JavaDocBuilder();
    	docBuilder.getClassLibrary().addClassLoader(getClass().getClassLoader());
    }
    
    public String getClassNameFromSource(String sourceFileName) throws MissingResourceException{
        if (sourceFileName.startsWith(sourceDir.getPath())) {
            sourceFileName = sourceFileName.substring(sourceDir.getPath().length() + 1);
        }
        String className = sourceFileName.substring(0, sourceFileName.lastIndexOf(".java"));
        className = className.replace(File.separatorChar, '.');
        return className;
    }

    public File getSourceFile(String className){
        String sourceName = convertClassNameToSourceName(className,true);
        sourceName = sourceDir.getPath() + File.separator + sourceName;
        return new File(sourceName);
    }

    public String convertClassNameToSourceName(String className, boolean appendJava){
        int index = className.indexOf(".class");
        String sourceName = className;
        if (index > -1) {
            sourceName = sourceName.substring(0,index);
        }
        sourceName = sourceName.replace('.',File.separatorChar);
        if (appendJava) {
            sourceName += ".java";
        }
        return sourceName;
    }

    public File getSourceDir(){
        return this.sourceDir;
    }

    public void setSourceDir(File sourceDir){
        this.sourceDir = sourceDir;
    }

    public String getIsA(){
        return isA;
    }

    public void setIsA(String isA){
        this.isA = isA;
    }
    
    public FunctionalPoint getFunctionalPointWithClass(String className) throws FileNotFoundException{
        return getFunctionalPoint(getJavaClassWithClass(className));
    }

    public FunctionalPoint getFunctionalPointWithSource(File sourceFile) throws FileNotFoundException{
        return getFunctionalPoint(getJavaClassWithSource(sourceFile));
    }

    public FunctionalPoint getFunctionalPoint(JavaClass clss){
        FunctionalPoint fp = null;
        if ( clss != null && clss.isA(isA)) {
            fp  = new FunctionalPoint();
            fp.setClassName(clss.getFullyQualifiedName());
            addClassDocs(fp, clss);
            addApplications(fp, clss);
            addSteps(fp, clss);
            addFields(fp, clss);
            addMethods(fp, clss);
            addDocsFromSerializedFP(fp,clss);
//        } else {
//            System.out.println("Javadoc2Bean:: class is "+clss.getFullyQualifiedName()+" was not an "+isA);
        }
        return fp;
    }

    protected FunctionalPoint getFunctionalPointFromSerializedFile(JavaClass clss){
        String qName = clss.getFullyQualifiedName();
        FunctionalPoint fp = null;
        if (serializedFiles.containsKey(qName)) {
            fp = (FunctionalPoint)serializedFiles.get(qName);
        } else {
            String fileName = qName.replace('.', '/')+".dat";
            InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);
            if (in != null) {
                try {
                    fp = (FunctionalPoint) InstanceSerializer.deserialize(in);
                    serializedFiles.put(qName,fp);
                } catch (IOException ioe) {
                    //This simply means no accompanying .dat file was found
                } catch (ClassNotFoundException cnfe) {
                    throw new JameleonException("Can not find FunctionalPoint! "+cnfe);
                }
            }
        }
        return fp;
    }

    protected void addDocsFromSerializedFP(FunctionalPoint fp, JavaClass clss){
        FunctionalPoint serFp;
        for (JavaClass superClass = clss; superClass != null && superClass.isA(isA); superClass = superClass.getSuperJavaClass()) {
            serFp = getFunctionalPointFromSerializedFile(superClass);
            if (serFp != null) {
                String key;
                for (Iterator it = serFp.getAttributes().keySet().iterator(); it.hasNext();) {
                    key = (String)it.next();
                    if ( !fp.getAttributes().containsKey(key) ) {
                        fp.addAttribute((Attribute)serFp.getAttributes().get(key));
                    }
                }
            }
        }
    }

    protected void addClassDocs(FunctionalPoint fp, JavaClass clss) {
    	String comment = clss.getComment();
        fp.setDescription(comment);
        fp.setShortDescription(getShortDescription(comment));
        DocletTag[] tagNames = clss.getTagsByName("jameleon.function");
        for (int i = 0; i < tagNames.length; i++) {
            fp.addTagName(convertNullToString(tagNames[i].getNamedParameter("name")));
            fp.setType(convertNullToString(tagNames[i].getNamedParameter("type")));
        }
        DocletTag tag = clss.getTagByName("author");
        if (tag != null) {
            fp.setAuthor(convertNullToString(tag.getValue()));
        }
    }
    
    protected String getShortDescription(String description){
    	String desc = description;
    	if (desc != null && desc.trim().length() > 0){
    		desc = description.replaceAll("(?s)^(.+?)[.\\n\\r]+.*", "$1")+".";
    	}
        return desc;
    }

    protected void addApplications(FunctionalPoint fp, JavaClass clss) {
        DocletTag[] applicationTags = clss.getTagsByName("jameleon.application");
        for (int i = 0; i < applicationTags.length; i++) {
            fp.addApplication(convertNullToString(applicationTags[i].getValue()));
        }
    }

    protected void addSteps(FunctionalPoint fp, JavaClass clss) {
        DocletTag[] tags = clss.getTagsByName("jameleon.step");
        for (int i = 0; i < tags.length; i++) {
            fp.addStep(convertNullToString(tags[i].getValue()));
        }
    }

    protected void addMethods(FunctionalPoint fp, JavaClass clss) {
        String name, type;
        JavaMethod[] methods = clss.getMethods(true);
        Attribute attr;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].isPropertyMutator() && methods[i].getTagByName("jameleon.attribute") != null) {
                type =  getFirstParameterTypeFromJavaMethod(methods[i]);
                name = methods[i].getPropertyName();
                if (fp.getAttributes().containsKey(name)) {
                    attr = (Attribute)fp.getAttributes().get(name);
                    fp.addAttribute(createAttributeFromTag(methods[i], name, attr.getType(), attr.getContextName(), false));
                } else {
                    fp.addAttribute(createAttributeFromTag(methods[i], name, type, "",false));
                }
            }
        }
    }

    protected void addFields(FunctionalPoint fp, JavaClass clss) {
        JavaField[] fields = getFields(clss);        
        processAttributes(fields, fp);
    }

    protected JavaField[] getFields(JavaClass clss) {
        Set signatures = new HashSet();
    	List fields = new ArrayList();
        addFieldsFromSuperclassAndInterfaces(signatures, fields, clss);
        return (JavaField[]) fields.toArray(new JavaField[fields.size()]);
    }

    protected void addFieldsFromSuperclassAndInterfaces(Set signatures, List fieldList, JavaClass clazz) {
    	JavaField[] fields = clazz.getFields();
    	
    	addNewFields(signatures, fieldList, fields);
    	
    	JavaClass superclass = clazz.getSuperJavaClass();
    	
    	// workaround for a bug in getSuperJavaClass
    	if ((superclass != null) && (superclass != clazz)) {
    	    addFieldsFromSuperclassAndInterfaces(signatures, fieldList, superclass);
    	}
    	
    	JavaClass[] implementz = clazz.getImplementedInterfaces();
    	
    	for (int i = 0; i < implementz.length; i++) {
    	    if (implementz[i] != null) {
    	        addFieldsFromSuperclassAndInterfaces(signatures, fieldList, implementz[i]);
    	    }
    	}
    }
    
    private void addNewFields(Set signatures, List fieldList, JavaField[] fields) {
    	for (int i = 0; i < fields.length; i++) {
	        String signature = fields[i].getName();
	        if (!signatures.contains(signature)) {
	            fieldList.add(fields[i]);
	            signatures.add(signature);
	        }
    	}
    }
    
    private void processAttributes(JavaField[] attributes, FunctionalPoint fp){
        String name, contextName, type;
        for (int i = 0; i < attributes.length; i++){
            if (attributes[i].getTagByName("jameleon.attribute") != null) {
                contextName = attributes[i].getNamedParameter("jameleon.attribute","contextName");
                type =  attributes[i].getType().getValue();
                name = attributes[i].getName();
                fp.addAttribute(createAttributeFromTag(attributes[i], name, type, contextName,true));
            }
        }
    }

    protected Attribute createAttributeFromTag(AbstractJavaEntity aje, String name, String type, String contextName, boolean instanceVariable) {
        Attribute attr = new Attribute();
        attr.setName(convertNullToString(name));
        attr.setContextName(contextName);
        attr.setType(convertNullToString(type));
        attr.setDescription(convertNullToString(aje.getComment()));
        attr.setRequired(Boolean.valueOf(aje.getNamedParameter("jameleon.attribute", "required")).booleanValue());
        //This needs to default to null
        attr.setDefaultValue(aje.getNamedParameter("jameleon.attribute","default"));
        attr.setInstanceVariable(instanceVariable);
        return attr;
    }

    public JavaDocBuilder getJavaDocBuilder(){
    	return setUpSourceSet();
    }
    
    protected JavaDocBuilder setUpSourceSet(){
        if (docBuilder == null) {
        	docBuilder = new JavaDocBuilder();
            docBuilder.getClassLibrary().addClassLoader(getClass().getClassLoader());
            docBuilder.addSourceTree(sourceDir);
        }
        return docBuilder;
    }
    
    protected JavaClass getJavaClassWithClass(String className) throws FileNotFoundException{
    	return getJavaClass(className, className);
    }
    
    private JavaClass getJavaClass(String className, String resource) throws FileNotFoundException{
        try {
            getSourceFile(className);
        } catch (MissingResourceException mre) {
            throw new FileNotFoundException("File corresponding to "+resource +" not found");
        }
        return getJavaDocBuilder().getClassByName(className);
    }

    protected JavaClass getJavaClassWithSource(File sourceFile) throws FileNotFoundException{
        if (!sourceFile.exists()) {
            throw new FileNotFoundException("File \""+sourceFile.getPath() +"\" not found");
        }
        return getJavaDocBuilder().getClassByName(getClassNameFromSource(sourceFile.getPath()));
    }

    protected String convertNullToString(String str){
        String rtrStr = ""; 
        if (str != null && str.length() > 0) {
            rtrStr = str;
        }
        return rtrStr;
    }

    /**
     * XJavadoc only gets the unqualified type of the instance variable, e.g. String not java.lang.String
     * But on method params, XJavadoc gets the fully qualified name
     * Since we use both, we want to make it consistent
     * @param method - the method that represents the attribute
     * @return the first parameter type as a String, fully qualified.
     */
    protected String getFirstParameterTypeFromJavaMethod(JavaMethod method) {
        String type = "";
        JavaParameter[] params = method.getParameters();
        if (params.length > 0) {
            JavaParameter param = params[0];
            type = param.getType().getValue();
            int index = type.indexOf(" ");
            if (index > -1) {
                type = type.substring(0, index);
            }
        }
        return type;
    }

}



