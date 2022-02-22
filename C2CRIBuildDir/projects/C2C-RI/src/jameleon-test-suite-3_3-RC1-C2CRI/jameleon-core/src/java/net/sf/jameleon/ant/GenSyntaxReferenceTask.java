/*
    Jameleon - An automation testing tool..
    Copyright (C) 2006 Christian W. Hargraves (engrean@hotmail.com)
    
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Parameter;

import net.sf.jameleon.util.GenSyntaxReference;

/**
 * An Ant task that takes the registered FunctionalPoints and generates a syntax reference for each one.
 */
public class GenSyntaxReferenceTask extends Task{

    protected File outputFile;
    protected String plugin = "TestCaseTagDefs";
    protected String templateName = "syntaxReference.txt";
    protected List parameters;

    public GenSyntaxReferenceTask(){
        parameters = new ArrayList();
    }

    /**
     * Adds a template parameter to be passed to the Velocity template
     *
     * @param parameter a name/value parameter.
     */
    public void addTemplateParam(Parameter parameter){
        parameters.add(parameter);
    }

    /**
     * Sets the name of the file, not including path of the template to use. Defaults to 'syntaxReference.txt'.
     * @param templateName - the name of the file, not including path of the template to use.
     * NOTE: This fileName is loaded from the CLASSPATH.
     */
    public void setTemplateName(String templateName){
        this.templateName = templateName;
    }

    /**
     * Sets the plugin to generate the syntax reference from. Defaults to TestCaseTagDefs
     * @param plugin - the plugin to generate the syntax reference from. For the htmlunit-plugin, pass in 'htmlunit-plugin'.
     */
    public void setPlugin(String plugin){
        this.plugin = plugin;
    }

    /**
     * Set the file where the syntax reference will be generated.
     * @param fileName - The name of the file.
     */
    public void setOutputFile(File fileName){
        outputFile = fileName;
    }

    /**
     * Jameleon's implementation of Task.execute().
     *
     * @exception BuildException  Ant's way of reporting exception
     */
    public final void execute() throws BuildException {
        if (outputFile == null) {
            outputFile = new File(getProject().getBaseDir(), "xdocs/syntax-reference.xml");
        }
        if (plugin == null) {
            throw new BuildException("plugin must be set!");
        }
        Map params = new HashMap();
        GenSyntaxReference generator = new GenSyntaxReference();
        if (parameters != null) {
            Iterator it = parameters.iterator();
            Parameter param;
            while (it.hasNext()) {
                param = (Parameter)it.next();
                params.put(param.getName(), param.getValue());
            }
        }
        try{
            System.out.println("Generating "+outputFile.getAbsolutePath());
            generator.genReferenceForPlugin(plugin, templateName, outputFile, params);
        }catch(Exception e){
            e.printStackTrace();
            throw new BuildException("An error occured while generating the syntax reference file: "+e.getMessage(), e);
        }
    }

}
