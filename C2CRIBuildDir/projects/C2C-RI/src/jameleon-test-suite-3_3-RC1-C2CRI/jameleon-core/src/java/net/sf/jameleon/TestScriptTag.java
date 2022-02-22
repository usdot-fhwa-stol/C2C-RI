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
package net.sf.jameleon;

import java.io.File;
import java.util.Map;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;

/**
 * This is currently a tag meant to be nested inside the test-suite tag.
 * It represents one of the scripts to be included and executed in the test-suite.
 * If a test-script nested inside a <code>precondition</code> tag fails, then 
 * none of the following scripts will be executed even if they are nested inside
 * a postcondition tag.
 * @jameleon.function name="ri-test-script"
 */
public class TestScriptTag extends JameleonTagSupport {


    protected TestSuiteTag tst;
    /**
     * The script to execute.
     * @jameleon.attribute required="true"
     */
    protected File script;
    /**
     * Do not continue executing any following scripts if a script
     * marked as a precondition fails.
     * @jameleon.attribute default="false"
     */
    protected boolean precondition;

    public void init() throws MissingAttributeException{
/*  Added to allow the test script called to have access to the parent's context values */
        try{
        Map tempMap = context.getParent().getVariables();
        System.out.println("The map of the parent " + tempMap.toString());
        } catch (Exception ex){
          System.err.println("Failure getting the map of the parent. If this method was called by a jameleon junit test this is expected." + ex.getMessage());            
        }
//        context.setVariables(tempMap);
/*  End RI Mods */
        testForUnsupportedAttributesCaught();
        broker.transferAttributes(context);
        broker.validate(context);
        
    }

    public void setUpEnvironment(){
        Object obj =  findAncestorWithClass(TestSuiteTag.class);
        if (obj != null){
            tst = (TestSuiteTag)obj;
        }else{
            throw new ClassCastException("Can only execute a test-script tag under the test-suite tag! "+getClass().getName());
        }
        obj =  findAncestorWithClass(PreconditionTag.class);
        if (obj != null){
            precondition = true;
        }
    }

    /**
     * This method executes the script.
     */
    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException{
        init();
        setUpEnvironment();
        ExecuteTestCase executor = tst.getExecuteTestCase();
        String errMsg = null;
        try{
        	/*  Added to allow the test script called to have access to the parent's context values */
        	Map tempMap = context.getParent().getVariables();
            System.out.println("The map of the parent " + tempMap.toString());

            System.out.println(" Size of the Executor ContextVars Variable = " + executor.getContextVars().size() + "  ** Note if this value is <= 0 no copy can occur due to execute_test_case code");
            executor.setContextVariablesforRI(context.getParent());
    /*  End RI Mods */
       	    System.out.println("\n\n *** The Test Script context contains " + executor.getContextVars().toString());
            errMsg = executor.execute(script);
        }catch (Exception e){
            //Do nothing here because we want to continue running the next script
        }
        if (precondition && errMsg != null && errMsg.length() > 0) {
            throw new JellyTagException("PRECONDITION FAILURE: "+errMsg, getFileName(), getElementName(), getColumnNumber(), getLineNumber());
        }
        
    }

}
