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

import java.io.Writer;
import java.util.Calendar;

/**
 * Reports on a test run, including each script result.
 */
public interface TestRunSummaryReporter {

    /**
     * Reports the start of a test run.
     * @param startTime The time the test run was kicked off
     */
    void reportTestRunStart(Calendar startTime);

    /**
     * Reports the completion of a test run.
     * @param startTime The time the test run began
     * @param endTime The time the test run was completed
     * @param counter The number of test cases run passed and failed.
     */
    void reportTestRunComplete(Calendar startTime, Calendar endTime, TestCaseCounter counter);

    /**
     * Cleans up any resources used
     */
    void cleanUp();

    /**
     * Gets the Writer to use to write to results to
     * @return The Writer to use to write to results to
     */
    Writer getWriter();

    /**
     * Sets the Writer to use to write to results to
     * @param writer The Writer to use to write to results to
     */
    void setWriter(Writer writer);

    /**
     * Tells whether to print the header or not.
     * @return true to print the header.
     */
    boolean isPrintHeader();

    /**
     * Sets the option to print the header or not.
     * @param printHeader Set to true to print the header.
     */
    void setPrintHeader(boolean printHeader);
}
