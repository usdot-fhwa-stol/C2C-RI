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
package net.sf.jameleon.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jameleon.event.DataDrivableEventHandler;
import net.sf.jameleon.event.DataDrivableListener;

import com.mockobjects.dynamic.AnyConstraintMatcher;
import com.mockobjects.dynamic.ConstraintMatcher;
import com.mockobjects.dynamic.Mock;
import com.mockobjects.dynamic.OrderedMock;

public class DataExecuterTest extends TestCase {

    protected DataExecuter dd;
    protected DataDrivableEventHandler eventHandler;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(DataExecuterTest.class);
    }

    public DataExecuterTest(String name) {
        super(name);
    }

    public void setUp(){
        eventHandler = DataDrivableEventHandler.getInstance();
    }

    public void tearDown(){
        eventHandler.clearInstance();
    }

    public void testDefaultConstructor(){
        dd = new DataExecuter();
        assertNotNull("DataDriver constructor returned null!",dd);
        assertNull("Default DataDriver should not be set",dd.getDataDriver());
    }

    public void testConstructorWithDataDrivable(){
        DataDriver dataDriver = (DataDriver)new Mock(DataDriver.class).proxy();
        dd = new DataExecuter(dataDriver);
        assertNotNull("DataDriver constructor returned null!",dd.getDataDriver());
        assertTrue("DataDrivables should be the same object",dataDriver == dd.getDataDriver());
    }

    public void testExecuteDataFalse() throws IOException{
        OrderedMock mockDataDriver = new OrderedMock(DataDriver.class);
        mockDataDriver.expect("open");
        final HashMap vars = new HashMap();
        mockDataDriver.expectAndReturn("hasMoreRows",true);
        mockDataDriver.expectAndReturn("getNextRow", vars);
        mockDataDriver.expectAndReturn("hasMoreRows",false);
        mockDataDriver.expect("close");
        dd = new DataExecuter((DataDriver)mockDataDriver.proxy());
        DataDrivable mockDataDrivable = new DataDrivable(){
                private Map rowData;
                /**
                 * Called with the a key-value pair of variables for each row found in the
                 * data source.
                 * @param variables - the variables key-values to be used in the execution.
                 */
                public void executeDrivableRow(int rowNum) {
                    if (!(rowNum == 1 && rowData.equals(vars))) {
                        fail("executeDrivableRow wasn't called with the correct parameters");
                    }
                }

                /**
                 * Removes the keys from the context and the variable set in addVariablesToRowData()
                 * @param keys - A Set of variable names to clean up.
                 */
                public void destroyVariables(Set keys) {
                    vars.clear();
                }
                /**
                 * Used to keep track of the variables and their original values.
                 * Any variable substitution should also be done here
                 * @param vars - A map of key-value pairs.
                 */
                public void addVariablesToRowData(Map vars) {
                    this.rowData = vars;
                }
                /**
                 * Used to tell whether this DataDrivable object is meant
                 * to report on each row execution or to report on them as
                 * a whole
                 */
                public boolean isCountRow(){
                    return false;
                }

                };
        
        dd.executeData(mockDataDrivable, false);
    }

    public void testExecuteDataTrue() throws IOException{
        OrderedMock mockDataDriver = new OrderedMock(DataDriver.class);
        final HashMap vars = new HashMap();
        dd = new DataExecuter((DataDriver)mockDataDriver.proxy());
        DataDrivable mockDataDrivable = new DataDrivable(){
                private Map rowData;
                /**
                 * Called with the a key-value pair of variables for each row found in the
                 * data source.
                 * @param variables - the variables key-values to be used in the execution.
                 */
                public void executeDrivableRow(int rowNum) {
                    if (!(rowNum == 1 && rowData.equals(vars))) {
                        fail("executeDrivableRow wasn't called with the correct parameters");
                    }
                }

                /**
                 * Removes the keys from the context and the variable set in addVariablesToRowData()
                 * @param keys - A Set of variable names to clean up.
                 */
                public void destroyVariables(Set keys) {
                    vars.clear();
                }
                /**
                 * Used to keep track of the variables and their original values.
                 * Any variable substitution should also be done here
                 * @param vars - A map of key-value pairs.
                 */
                public void addVariablesToRowData(Map vars) {
                    this.rowData = vars;
                }
                /**
                 * Used to tell whether this DataDrivable object is meant
                 * to report on each row execution or to report on them as
                 * a whole
                 */
                public boolean isCountRow(){
                    return false;
                }
                };

        dd.executeData(mockDataDrivable, true);
    }

    public void testExecuteDataClose() throws IOException{
        OrderedMock mockDataDriver = new OrderedMock(DataDriver.class);
        mockDataDriver.expectAndThrow("open", new IOException());
        mockDataDriver.expect("close");
        dd = new DataExecuter((DataDriver)mockDataDriver.proxy());

        OrderedMock mockDataDrivable = new OrderedMock(DataDrivable.class);
        boolean exceptionThrown = false;
        try{
            dd.executeData((DataDrivable)mockDataDrivable.proxy(), false);
        }catch (IOException ioe){
            exceptionThrown = true;
        }
        assertTrue("An exception should have been thrown.",exceptionThrown);
        mockDataDriver.verify();
        mockDataDrivable.verify();
    }

    public void testExecuteDataHasMoreRows() throws IOException{
        OrderedMock mockDataDriver = new OrderedMock(DataDriver.class);
        mockDataDriver.expect("open");
        mockDataDriver.expectAndReturn("hasMoreRows",false);
        mockDataDriver.expect("close");
        dd = new DataExecuter((DataDriver)mockDataDriver.proxy());

        OrderedMock mockDataDrivable = new OrderedMock(DataDrivable.class);
        dd.executeData((DataDrivable)mockDataDrivable.proxy(), false);
        mockDataDrivable.verify();
        mockDataDriver.verify();
    }

    public void testExecuteDataEventHandler() throws IOException{
        OrderedMock mockDataDriver = new OrderedMock(DataDriver.class);
        OrderedMock mockDataDrivableListener = new OrderedMock(DataDrivableListener.class);
        Mock mockDataDrivable= new Mock(DataDrivable.class);
        mockDataDrivableListener.expect("openEvent", new AnyConstraintMatcher());
        mockDataDriver.expect("open");
        mockDataDriver.expectAndReturn("hasMoreRows",true);
        HashMap vars = new HashMap();
        vars.put("var1", "one");
        vars.put("var2", "two");
        mockDataDriver.expectAndReturn("getNextRow",vars);
        mockDataDrivableListener.expect("executeRowEvent", new AnyConstraintMatcher());
        mockDataDrivable.expect("addVariablesToRowData", vars);
        mockDataDrivable.expect("executeDrivableRow", new Integer(1));
        mockDataDrivable.expect("destroyVariables", vars.keySet());
        mockDataDriver.expectAndReturn("hasMoreRows",false);
        mockDataDrivableListener.expect("closeEvent", new AnyConstraintMatcher());
        mockDataDriver.expect("close");
        eventHandler.addDataDrivableListener((DataDrivableListener)mockDataDrivableListener.proxy());
        dd = new DataExecuter((DataDriver)mockDataDriver.proxy());

        dd.executeData((DataDrivable)mockDataDrivable.proxy(), false);
    }

    protected class DataConstraintMatcher implements ConstraintMatcher{

        public boolean matches(Object[] args) {
            Map vars = (Map)args[0];
            int rowNum = ((Integer)args[1]).intValue();
            return (rowNum == 1 && vars.get("var1").equals("one") && vars.get("var2").equals("two"));
        }
        public Object[] getConstraints() {
            return new Object[]{Map.class, Integer.TYPE};
        }
    }

}
