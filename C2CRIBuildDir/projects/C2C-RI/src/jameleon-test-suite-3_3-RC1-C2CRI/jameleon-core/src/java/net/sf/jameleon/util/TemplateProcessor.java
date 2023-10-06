/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2007 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon.util;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.io.VelocityWriter;
import org.apache.velocity.app.Velocity;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

public class TemplateProcessor {

    protected Template template;
    protected String templateName;

    public TemplateProcessor(String templateName){
        Velocity.setProperty("resource.loader", "classpath");
        Velocity.setProperty("classpath.resource.loader.class", "net.sf.jameleon.util.VelocityClasspathResourceLoader");
        this.templateName = templateName;
        initVelocity();
    }

    private void initVelocity(){
        try{
            Velocity.init();
        }catch(Exception rnfe){
            //The tests can still run. The results simply won't be outputted. Why would this happen?
            System.err.println("Can not write results to file: ");
            rnfe.printStackTrace();
        }
    }

    public void transform(File toFile, Map params){
        JameleonUtility.createDirStructure(toFile.getParentFile());
        try (VelocityWriter vw = new VelocityWriter(new FileWriter(toFile)))
		{
            transformToWriter(vw, params);
        }catch(IOException ioe){ioe.printStackTrace();}
    }

    public String transformToString(Map params){
        StringWriter sw = new StringWriter();
        transformToWriter(sw, params);
        return sw.getBuffer().toString();
    }

    public void transformToWriter(Writer w, Map params){
        try{
            VelocityContext context = new VelocityContext();
            Iterator it = params.keySet().iterator();
            String key;
            while (it.hasNext()) {
                key = (String)it.next();
                context.put(key, params.get(key));
            }
            template = Velocity.getTemplate(templateName);
            template.merge( context, w );
        }catch(Exception e){
            System.err.println("ERROR: Loading Velocity Template: " + templateName);            
            e.printStackTrace();
        }
    }

}
