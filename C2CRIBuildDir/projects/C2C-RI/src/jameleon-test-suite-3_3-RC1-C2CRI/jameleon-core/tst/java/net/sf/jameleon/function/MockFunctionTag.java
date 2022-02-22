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
package net.sf.jameleon.function;

import java.io.File;
import java.io.IOException;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

/**
 * @jameleon.function name="mock.function.tag"
 */
public class MockFunctionTag extends FunctionTag{

    protected int executionTime = 0;
    protected int numTearDownCalls = 0;
    protected int numPluginTearDownCalls = 0;
    protected boolean simplifiedTestBlockCalled = false;

    public MockFunctionTag() {
        context = new JellyContext();
    }

    public void testBlock(){
        simplifiedTestBlockCalled = true;
        try{
            Thread.sleep(executionTime);
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
        assertTrue(true);
    }

    public Script getBody() {
        return new org.apache.commons.jelly.impl.TextScript();
    }

    public AttributeBroker getBroker(){
        return broker;
    }

    public void store(File dir, int event) throws IOException{
    }

    /**
     * @jameleon.attribute required="false"
     */
    public void setExecutionTime(int executionTime){
        this.executionTime = executionTime;
    }

    public void tearDown() {
        numTearDownCalls++;
    }

    public void pluginTearDown() {
        numPluginTearDownCalls++;
    }

    protected void setBroker(AttributeBroker broker){
        this.broker = broker;
    }

    public void resetFunctionalPoint(){
        super.resetFunctionalPoint();
    }

    /**
     * @jameleon.attribute required="false"
     */
    public void setSomeVariable(String someVariable){
        setVariable("someVariable",someVariable);
    }

    class MockScript implements Script {
        public Script compile() throws JellyException {
            return this;
        }

        public void run(JellyContext context, XMLOutput output) throws JellyTagException {
        }
    }

}
