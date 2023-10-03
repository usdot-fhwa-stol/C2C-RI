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
package net.sf.jameleon.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sf.jameleon.bean.FunctionalPoint;
import net.sf.jameleon.bean.Javadoc2Bean;
import net.sf.jameleon.exception.JameleonException;
import net.sf.jameleon.util.InstanceSerializer;

import org.apache.tools.ant.BuildException;

import com.thoughtworks.qdox.ant.AbstractQdoxTask;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * An Ant task that registers all functional points (defined in <code>isA</code>, which defaults to
 * <code>FunctionTag</code>) so they can be recognized by the Jameleon engine.
 */
public class JameleonXDoclet extends AbstractQdoxTask{

    protected File outputDir = null;
    protected String outputFileName = new String("TestCaseTagDefs.properties");
    protected String isA = "net.sf.jameleon.function.FunctionTag";
    protected boolean quiet = false;
    protected Javadoc2Bean j2b;

    /**
     * Set the directory where TestCaseTagDefs.properties will be generated to
     * @param outputDir - The directory where TestCaseTagDefs.properties will be generated to. 
     */
    public void setOutputDir(File outputDir){
        this.outputDir = outputDir;
    }

    /**
     * Restricts the javadocs to be extracted to all classes that implement this class
     * @param isA - The class to restrict by.
     */
    public void setIsA(String isA){
        this.isA = isA;
    }

    /**
     * Sets the messages to be printed to stdout to true/false
     * @param quiet - Set to true to be quite. Defaults to false
     */
    public void setQuiet(boolean quiet){
        this.quiet = quiet;
    }

    /**
     * Set the file where the function points will be registered to. The default is TestCaseTagDefs.properties if this is not set
     * @param fileName - The name of the file where the information will be sent to. This does not include the directory as that is set
     * via the outputDir property.
     */
    public void setOutputFileName(String fileName){
        outputFileName = fileName;
    }

    /**
     * Implement this method and play with _xJavaDoc
     *
     * @exception BuildException  Ant's way of reporting exception
     */
    public void execute() throws BuildException{
        if (outputDir == null) {
            throw new BuildException("outputDir must be set to the directory where TestCaseTagDefs.properties will be generated to.");
        }else if (!outputDir.isDirectory()) {
            throw new BuildException("outputDir is not a valid directory! Set outputDir to the directory where TestCaseTagDefs.properties will be generated to.");
        }
        try{
	        validateAttributes();
	        buildFileMap();
                System.out.println("JameleonXDoclet:: FileMap Size = "+fileMap.size());
	        j2b = new Javadoc2Bean();
	        j2b.setIsA(isA);
	        mergeBuilderSources(j2b);
	        Properties props = new Properties();
	        JavaClass[] classes = j2b.getJavaDocBuilder().getClasses();
	        for (int i = 0; i < classes.length; i++) {
                    System.out.println("JameleonXDoclet: "+i+"  "+ classes[i].getName());
	            setClassAttributes(classes[i],props,j2b);
	        }
                System.out.println("JameleonXDoclet: Saving the tag defs. Number properties = "+props.size());
	    	saveTagDefs(props, getTagDefsFileName());
        }catch(RuntimeException re){
        	re.printStackTrace();
        }
    }
    
    protected void mergeBuilderSources(Javadoc2Bean j2b) {
    	for (Iterator iterator = fileMap.keySet().iterator(); iterator.hasNext();) {
    		String sourceFile = (String) iterator.next();
    		j2b.getJavaDocBuilder().addSourceTree((File) fileMap.get(sourceFile));
                System.out.println("JameleonXDoclet:: Added "+sourceFile+" to the sourceTree.");
    	}
    }

    /**
     * Simply constructs a file from a String that represents the fully qualified class
     * name. The path to the file will be prefixed by the <code>outputDir</code>.
     * @param className - a String representing the name and partial (package) location of the file.
     * @return A file that starts with <code>outputDir</code> and is followed by the directory 
     *         structure (package) of the <code>className</code>
     */
    protected File constructFileFromClassName(String className){
        String sourceName = new String(className);
        if (sourceName.endsWith(".class")) {
            sourceName = sourceName.substring(0,sourceName.indexOf(".class"));
        }
        sourceName = sourceName.replace('.',File.separatorChar);
        sourceName += InstanceSerializer.SERIALIZED_EXT;
        return new File(outputDir, sourceName);
    }

    /**
     * @return the complete path to the output file for the tag definitions of functional points
     */
    protected String getTagDefsFileName(){
        String fileName = outputDir.getPath();
        if (fileName.lastIndexOf(File.separator) < (fileName.length() -1) ) {
            fileName += File.separator;
        }
        fileName += outputFileName;
        return fileName;
    }

    /**
     * Saves the function point names and their corresponding class names to a file
     * @param props - The properties to save to a file.
     */
    protected void saveTagDefs(Properties props, String fileName) throws BuildException{
        try (FileOutputStream fos = new FileOutputStream(fileName))
		{
            props.store(fos,"Function Tag Definitions generated by Jameleon."); 
        }catch(IOException ioe){
            throw new BuildException("Could not write to file "+fileName,ioe);
        }
    }

    /**
     * Set the class-specific attributes
     */
    protected void setClassAttributes(JavaClass clazz, Properties props, Javadoc2Bean j2b) throws BuildException{
        String className = clazz.getFullyQualifiedName();
        FunctionalPoint fp = null;
        try{
            fp = j2b.getFunctionalPoint(clazz);
            if (fp != null){
                //validate that the type is correct
                fp.getType();
//            } else {
//                System.out.println("JameleonXDoclet:: Did not get FunctionalPoint for "+className);
            }
        } catch (JameleonException iae){
            throw new BuildException(className + ": "+iae.getMessage());
        } 
        String warningMsg = "WARNING: "+className+" is a valid function point, but it will NOT be automatically registered! "+
                            "Please add a @jameleon.function name=\"functionName\" to the class javadocs of the function point";
        if (fp != null) {
            List tagNames = fp.getTagNames();
            Iterator it = tagNames.iterator();
            while (it.hasNext()) {
                String tagName = (String)it.next();
                System.out.println("JameleonXDoclet:: Processing tagName "+tagName);
                if ( tagName != null && tagName.length() > 0 && props.getProperty(tagName) == null) {
                    props.setProperty(tagName,className);
                    serializeFunctionalPoint(fp, className);
                }else if(tagName != null && props.getProperty(tagName) != null){
                    throw new BuildException(className +" and "+ props.getProperty(tagName) +" both have the same function point name registered! "+
                                             "Change the @jameleon.function name=\"\" attribute of one of these classes to something unique and try again.");
                }else{
                    if (!quiet) {
                        System.out.println(warningMsg);
                    }
                }
            }
            if (tagNames.size() == 0) {
                serializeFunctionalPoint(fp, className);
                if (!quiet) {
                    System.out.println(warningMsg);
                }
            }
        } 

    }

    protected void serializeFunctionalPoint(FunctionalPoint fp, String className) throws BuildException{
        try{
        	InstanceSerializer.serialize(fp,constructFileFromClassName(className));
        }catch(IOException ioe){
            throw new BuildException(ioe.toString());
        }
    }

}
