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

import java.util.Calendar;

public class ReporterUtilsTest extends TestCase {

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(ReporterUtilsTest.class);
    }

    public ReporterUtilsTest(String name) {
        super(name);
    }

    public void testGetExecutionTime(){
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = (Calendar)startTime.clone();
        endTime.set(Calendar.SECOND, startTime.get(Calendar.SECOND) + 3);
        endTime.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE) + 2);
        endTime.set(Calendar.HOUR, startTime.get(Calendar.HOUR) + 1);
        String execTime = ReporterUtils.getExecutionTime(startTime, endTime);
        assertEquals("execution time", "1h 2m 3.000s", execTime);
    }
}
