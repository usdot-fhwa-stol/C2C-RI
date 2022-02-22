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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon.data;

import java.util.HashMap;
import java.util.Map;
import java.io.File;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

import com.mockobjects.dynamic.AnyConstraintMatcher;
import com.mockobjects.dynamic.Mock;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import net.sf.jameleon.MockSessionTag;
import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.util.StateStorer;
import net.sf.jameleon.result.*;


public class AbstractDataDrivableTagTest extends TestCase {

    private MockDataDrivableTag mockDdTag;
    private TestCaseTag testCaseTag;
    private JellyContext context;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(AbstractDataDrivableTagTest.class);
    }

    public AbstractDataDrivableTagTest(String name) {
        super(name);
    }

    public void setUp() throws JellyTagException{
        context = new JellyContext();
        testCaseTag = new TestCaseTag();
        testCaseTag.setContext(context);
        testCaseTag.setUpDataDrivable();
        testCaseTag.setGenTestCaseDocs(false);
        testCaseTag.setName("some name");
        testCaseTag.setUp();
        mockDdTag = new MockDataDrivableTag();
        mockDdTag.setContext(context);
        MockSessionTag mockST = new MockSessionTag();
        mockST.setParent(testCaseTag);
        mockDdTag.setParent(mockST);
    }

    public void testInit() throws Exception{
        assertNull("result container should be null", mockDdTag.getResultContainer());
        mockDdTag.init();
        assertFalse("result container should not be countable", mockDdTag.getResultContainer().isCountable());
        mockDdTag.setCountRow(true);
        mockDdTag.init();
        assertTrue("result container should be countable", mockDdTag.getResultContainer().isCountable());
    }

    public void testCreateNewResult(){
    	assertFalse("result should not be countable", mockDdTag.createNewResult() instanceof CountableDataDrivableRowResult);
    	mockDdTag.countRow = true;
    	assertTrue("result should not be countable", mockDdTag.createNewResult() instanceof CountableDataDrivableRowResult);
    }

    public void testSetUpDataDrivable(){
        StateStorer.getInstance().setStoreDir(new File("."));
        assertNull("test case tag", mockDdTag.tct);
        assertNull("previousStateDir", mockDdTag.previousStateDir);
        mockDdTag.setUpDataDrivable();
        assertNotNull("test case tag", mockDdTag.tct);
        assertEquals("test case tag", testCaseTag, mockDdTag.tct);
        assertNotNull("previousStateDir", mockDdTag.previousStateDir);
        assertFalse("parentFailed", mockDdTag.parentFailed);

        //Test that the parentFailed field gets updated appropriately
        mockDdTag.setStopTestExecutionOnFailure(false);
        testCaseTag.setFailedOnCurrentRow(true);
        mockDdTag.setUpDataDrivable();
        assertFalse("parentFailed", mockDdTag.parentFailed);
        mockDdTag.setStopTestExecutionOnFailure(true);
        mockDdTag.setUpDataDrivable();
        assertTrue("parentFailed", mockDdTag.parentFailed);

        mockDdTag.setCountRow(true);
        mockDdTag.setUpDataDrivable();
    }

    public void testGetDataDrivableResult(){
    	DataDrivableRowResult ddr = new DataDrivableRowResult(null);
    	mockDdTag.dataDrivableRowResult = ddr;
    	assertTrue(ddr == mockDdTag.getDataDrivableRowResult());
    }

    public void testAddVariablesToRowData(){
        Map vars = new HashMap();
        vars.put("1", "one");
        mockDdTag.setUpDataDrivable();
        mockDdTag.addVariablesToRowData(vars);
        assertEquals("data-driven variables", "one", context.getVariable("1"));
    }

    public void testExecuteDrivableRow(){
        Map vars = new HashMap();
        vars.put("1", "one");
        mockDdTag.setUpDataDrivable();
        mockDdTag.addVariablesToRowData(vars);
        mockDdTag.xmlOut = XMLOutput.createDummyXMLOutput();
        mockDdTag.setTrim(false);

        Mock mockScript = new Mock(Script.class);
        mockScript.expect("run", new AnyConstraintMatcher());
        mockDdTag.setBody((Script)mockScript.proxy());
        mockDdTag.executeDrivableRow(2);
        mockScript.verify();
    }
    
    public void testExecuteDrivableRowNullDataDrivableResult(){
        assertNull("dataDrivableResult should be null", mockDdTag.dataDrivableRowResult);
        Map vars = new HashMap();
        vars.put("1", "one");
        mockDdTag.setUpDataDrivable();
        mockDdTag.addVariablesToRowData(vars);
        DataDrivableRowResult ddrr =  new DataDrivableRowResult(null);
        DataDrivableResultContainer ddr =  new DataDrivableResultContainer(null);
        mockDdTag.dataDrivableRowResult = ddrr;
        mockDdTag.resultContainer = ddr;
        mockDdTag.executeDrivableRow(1);
        assertNotNull("dataDrivableResult should not be null", mockDdTag.dataDrivableRowResult);
        assertTrue("dataDrivableResult should not have changed", ddrr == mockDdTag.dataDrivableRowResult);
        assertFalse("dataDrivableResult should not be countable", mockDdTag.dataDrivableRowResult instanceof CountableDataDrivableRowResult);
        assertEquals("row data '1'", vars.get("1"), ddrr.getRowData().get("1"));
        assertEquals("row #", 1, ddrr.getRowNum());
        mockDdTag.executeDrivableRow(2);
        DataDrivableRowResult ddr2 = mockDdTag.getDataDrivableRowResult();
        //Since countRow defaults to false, the dataDrivableResult should not have changed since the last execution
        assertFalse("dataDrivableResult should still not be countable", mockDdTag.dataDrivableRowResult instanceof CountableDataDrivableRowResult);
        assertTrue("a new result should have been created", ddrr != mockDdTag.dataDrivableRowResult);
        assertEquals("row #", 2, ddr2.getRowNum());
        mockDdTag.setCountRow(true);
        mockDdTag.executeDrivableRow(3);
        ddr2 = mockDdTag.getDataDrivableRowResult();
        assertNotNull("dataDrivableResult should not be null", mockDdTag.dataDrivableRowResult);
        assertTrue("dataDrivableResult should be countable", mockDdTag.dataDrivableRowResult instanceof CountableDataDrivableRowResult);
        assertEquals("row #", 3, ddr2.getRowNum());
    }

}
