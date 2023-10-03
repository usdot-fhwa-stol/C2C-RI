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

import net.sf.jameleon.ExecuteTestCase;
import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.exception.JameleonException;
import net.sf.jameleon.result.JameleonTestResult;
import net.sf.jameleon.result.TestCaseResult;
import net.sf.jameleon.util.JameleonUtility;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Reports the test run results is a simple form, suitable for STD OUT or a simple text file
 */
public class SimpleTestRunReporter extends AbstractTestRunReporter{

	protected final Logger log = Logger.getLogger(SimpleTestRunReporter.class.getName());

    public void reportScriptResult(TestCaseTag tct, TestCaseCounter counter) {
        try{
            String outputstring = null;
            
        	String scriptName = JameleonUtility.getFileNameFromScriptPath(tct.getFileName());
            getWriter().write("\n"+ scriptName );

            outputstring = outputstring + "\n"+ scriptName;
            
            TestCaseResult tcr = tct.getResults();
            if (tcr.failed()){
                List failedResults = tcr.getFailedResults();
                JameleonTestResult jtr;
                for (Iterator it = failedResults.iterator(); it.hasNext();){
                    jtr = (JameleonTestResult)it.next();
                    getWriter().write("\nFunctionId: " + jtr.getIdentifier());
                    outputstring = outputstring + "\nFunctionId: " + jtr.getIdentifier();
                    getWriter().write("\nLine #: " + jtr.getLineNumber());
                    outputstring = outputstring + "\nLine #: " + jtr.getLineNumber();
                    getWriter().write("\nRow #: " + jtr.getFailedRowNum());
                    outputstring = outputstring + "\nRow #: " + jtr.getFailedRowNum();
                    getWriter().write("\nError Message: " + jtr.getErrorMsg()+"\n");
                    outputstring = outputstring + "\nError Message: " + jtr.getErrorMsg()+"\n";
  
                }
                getWriter().write(scriptName);
                outputstring = outputstring + scriptName;
            }
            getWriter().write(" : " +tct.getResults().getOutcome());
            outputstring = outputstring + " : " +tct.getResults().getOutcome();
            log.warn(outputstring);
            
            getWriter().flush();
        }catch(IOException ioe){
            throw new JameleonException("Could not report simple results: "+ioe.getMessage());
        }
    }

    public void reportTestRunComplete(Calendar startTime, Calendar endTime, TestCaseCounter counter) {
        try{
            String outputstring = null;
        	getWriter().write("______________________________________________________\n\n");
            getWriter().write("Test Run: " + ReporterUtils.formatTime(startTime));
            outputstring = outputstring + "Test Run: " + ReporterUtils.formatTime(startTime);
            getWriter().write("\nTests run: " + counter.getNumRun());
            outputstring = outputstring + "\nTests run: " + counter.getNumRun();
            getWriter().write(" Failed: " + counter.getNumFailed());
            outputstring = outputstring + " Failed: " + counter.getNumFailed();
            getWriter().write(" Time: " + ReporterUtils.getExecutionTime(startTime, endTime));
            outputstring = outputstring + " Time: " + ReporterUtils.getExecutionTime(startTime, endTime);
            log.warn(outputstring);
            getWriter().write("\n______________________________________________________\n");
            getWriter().flush();
        }catch(IOException ioe){
            throw new JameleonException("Could not report summary results: "+ioe.getMessage());
        }
    }

    public void reportTestRunStart(Calendar startTime) {
        // original implementation was empty
    }

}
