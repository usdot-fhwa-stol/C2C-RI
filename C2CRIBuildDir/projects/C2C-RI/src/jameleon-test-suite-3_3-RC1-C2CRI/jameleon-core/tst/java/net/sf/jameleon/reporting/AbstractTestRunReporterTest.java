/*
    Jameleon - An automation testing tool..
    Copyright (C) 2007 Christian W. Hargraves (engrean@hotmail.com)

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
package net.sf.jameleon.reporting;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jameleon.TestCaseTag;

import java.io.StringWriter;
import java.util.Calendar;

/**
 * User: hargravescw
 * Date: Jun 19, 2007
 * Time: 8:53:38 AM
 */
public class AbstractTestRunReporterTest extends TestCase {

    AbstractTestRunReporter reporter;
    StringWriter writer;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(AbstractTestRunReporterTest.class);
    }

    public AbstractTestRunReporterTest(String name) {
        super(name);
    }

    public void setUp(){
        reporter = new AbstractTestRunReporter(){
            public void reportScriptResult(TestCaseTag tct, TestCaseCounter counter) {}
            public void reportTestRunStart(Calendar startTime) {}
            public void reportTestRunComplete(Calendar startTime, Calendar endTime, TestCaseCounter counter) {}
        };
        writer = new StringWriter();
        reporter.setWriter(writer);
    }

    public void testCleanUp() throws Exception{
        assertNotNull("Writer should not be null", reporter.getWriter());
        reporter.getWriter().write("some text");
        reporter.cleanUp();
        assertNull("writer should be null", reporter.getWriter());
    }

    public void testGetSetWriter(){
        reporter.setWriter(writer);
        assertTrue("writer should be the same", writer == reporter.getWriter());
    }


}
