/*
    Jameleon - An automation testing tool..
    Copyright (C) 2006-2007 Christian W. Hargraves (engrean@hotmail.com)
    
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

import java.util.LinkedList;
import java.util.List;

/**
 * A test suite result is a result which contains at least one TestCaseResult for every test case run.
 */
public class TestSuiteResult {
	private static final long serialVersionUID = 1L;

    /**
     * The TestCaseResults for this test suite.
     */
    protected List testCaseResults;

    public TestSuiteResult(){
        testCaseResults = new LinkedList();
    }

    /**
     * Adds a TestCaseResult to the suite of test case results
     * @param tcRes - The TestCaseResult of the test case executed.
     */
    public void addTestCaseResult(TestCaseResult tcRes){
        testCaseResults.add(tcRes);
    }

    /**
     * Gets all of the TestCaseResults recorded in this test suite.
     * 
     * @return List - All of the TestCaseResults recorded in this test suite.
     */
    public List getTestCaseResults(){
        return testCaseResults;
    }

}
