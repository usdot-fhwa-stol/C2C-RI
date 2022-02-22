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
package net.sf.jameleon.result;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jameleon.bean.Attribute;
import net.sf.jameleon.bean.FunctionalPoint;
import net.sf.jameleon.util.XMLHelper;

public class DataDrivableRowResultTest extends TestCase {

    private DataDrivableRowResult ddRowResult;
    private static final FunctionalPoint tag = new FunctionalPoint();

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( DataDrivableRowResultTest.class );
    }

    public DataDrivableRowResultTest( String name ) {
        super( name );
    }

    public void setUp(){
        ddRowResult = new DataDrivableRowResult(tag);
    }

    public void testConstructor(){
        assertNotNull("DataDrivableRowResult after call to constructor", ddRowResult);
        assertEquals("# of child results", 0, ddRowResult.childrenResults.size());
        assertTrue(tag == ddRowResult.tag);
    }

    public void testConstructor2(){
        DataDrivableRowResult dr = new DataDrivableRowResult(tag, ddRowResult);
        assertNotNull("DataDrivableRowResult after call to constructor", dr);
        assertEquals("# of child results", 0, dr.childrenResults.size());
        assertTrue(tag == dr.tag);
        assertTrue(ddRowResult == dr.parentResults);
    }

    public void testIsDataDriven(){
        assertTrue(ddRowResult.isDataDriven());
    }
    
    public void testGetSetRowNum(){
    	ddRowResult.setRowNum(1);
    	assertEquals("rowNum", 1, ddRowResult.getRowNum());
    	ddRowResult.setRowNum(2);
    	assertEquals("rowNum", 2, ddRowResult.getRowNum());
    }
    
    public void testGetSetRowData(){
    	Map vars = new HashMap();
    	vars.put("1", "one");
    	vars.put("2", "two");
    	ddRowResult.setRowData(vars);
    	assertEquals("# of variables in data-drivable vars", 2, ddRowResult.getRowData().size());
    }

    public void testToXML(){
        FunctionalPoint fp = new FunctionalPoint();
        fp.addTagName("some tag");
        FunctionResult res = new FunctionResult(fp);
        res.getTag().setFunctionId("some id");
        File f = new File("some/file.txt");
        res.setErrorFile(f);
        ddRowResult.addChildResult(res);
        String xml = ddRowResult.toXML();
        XMLHelper xmlHelper = new XMLHelper(xml);
        String value = xmlHelper.getValueFromXPath("/data-drivable-row/children-results/function-point/functionId/text()");
        assertEquals("functionId", "some id", value);
        value = xmlHelper.getValueFromXPath("/data-drivable-row/children-results/function-point/error-file-name/text()");
        assertEquals("resulsFileName", f.getPath(), value);

        SessionResult sessionRes = new SessionResult(new FunctionalPoint());
        Attribute attr = new Attribute();
        attr.setName("application");
        attr.setValue("app2");
        sessionRes.getTag().addAttribute(attr);
        FunctionResult res2 = new FunctionResult();
        res2.setError(new RuntimeException("some error message"));
        
        sessionRes.addChildResult(res2);
        ddRowResult.addChildResult(sessionRes);

        xml = ddRowResult.toXML();
        xmlHelper = new XMLHelper(xml);
        value = xmlHelper.getValueFromXPath("/data-drivable-row/children-results/session-result/application/text()");
        assertEquals("session application", "app2", value);
    }

}
