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

/**
 * Keeps track of the test cases run and test case number in a given test run:
 * <ul>
 *   <li>Tests Run</li>
 *   <li>Tests Failed</li>
 *   <li>Tests Passed</li>
 *   <li>Curent Test Script Number</li>
 * </ul>
 */
public class TestCaseCounter {
    private int testCaseNum = 1;
    private int numPassed;
    private int numFailed;

    /**
     * Increment the number of tests passed.
     * @param numPassed The number to increment by
     */
    public void incrementPassed(int numPassed) {
        this.numPassed += numPassed;
    }

    /**
     * Increment the number of tests failed.
     * @param numFailed The number to increment by
     */
    public void incrementFailed(int numFailed) {
        this.numFailed += numFailed;
    }

    /**
     * Increment the test script number
     * @param testCaseNum The number to increment by
     */
    public void incrementTestCaseNum(int testCaseNum) {
        this.testCaseNum += testCaseNum;
    }

    /**
     * Gest the total passed test cases
     * @return the total passed test cases
     */
    public int getNumPassed() {
        return numPassed;
    }

    /**
     * Gets the total failed tests cases
     * @return the total failed tests cases
     */
    public int getNumFailed() {
        return numFailed;
    }

    /**
     * Gets the current test script number
     * @return the current test script number
     */
    public int getTestCaseNum() {
        return testCaseNum;
    }

    public int getNumRun(){
       return getNumFailed() + getNumPassed(); 
    }
}
