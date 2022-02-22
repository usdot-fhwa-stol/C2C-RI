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

import net.sf.jameleon.XMLable;
import net.sf.jameleon.bean.FunctionalPoint;
import net.sf.jameleon.util.JameleonUtility;
import org.apache.commons.jelly.LocationAware;

import java.io.File;
import java.util.Calendar;

/**
 * An abstract Test Result. 
 * Generic <code>toXML</code> and <codetoString</codetoString methods are provided 
 * for easy extension. 
 */
public abstract class JameleonTestResult implements XMLable, LocationAware {
    /**
     * The time it took to execute the entire test step
     */
    protected long executionTime;
    /**
     * Whether this result failed or not
     */
    protected boolean failed;
    /**
     * The tag that this result represents
     */
    protected FunctionalPoint tag;
    /**
     * The stack trace from the failure if one occured
     */
    protected Throwable error;
    /**
     * The file that contains the display of the error
     */
    protected File errorFile;
    /**
     * The parent results
     */
    protected HasChildResults parentResults;
    /**
     * Syntax for opening cdata value
     */
    public final static String S_CDATA = "<![CDATA[";
    /**
     * Syntax for closing cdata value
     */
    public final static String E_CDATA = "]]>";
    protected int lineNumber = -1;
    protected int columnNumber = -1;
    protected String scriptFileName;
    protected String elementTagName;
    private Calendar dateTimeExecuted;

    /**
     * Default Constructor - Does nothing
     */
    public JameleonTestResult(){
        dateTimeExecuted = Calendar.getInstance();        
    }

    /**
     * Constructor 
     * @param tag - the tag of the restults
     */
    public JameleonTestResult(FunctionalPoint tag){
        this();
        this.tag = tag;
    }

    /**
     * Constructor 
     * @param tag - the tag of the restults
     * @param parentResults - the results which are parents to this result
     */
    public JameleonTestResult(FunctionalPoint tag, HasChildResults parentResults){
        this(tag);
        this.parentResults = parentResults;
        parentResults.addChildResult(this);
    }

    public void destroy(){
        error = null;
        errorFile = null;
        failed = false;
    }

    /**
     * @return the error that occured
     */
    public Throwable getError(){
        return this.error;
    }

    /**
     * Sets the error of the function point if one occured
     * @param error - The error of the function point if one occured
     */
    public void setError(Throwable error){
        setFailed();
        this.error = error;
        recordFailureToCountableResult();
    }

    public JameleonTestResult findAncestorByClass(Class clzz){
        JameleonTestResult ancestorResult = null;
        if (parentResults != null && clzz.isInstance(parentResults)) {
            ancestorResult = (JameleonTestResult)parentResults;
        }else if (parentResults != null){
            ancestorResult = ((JameleonTestResult)parentResults).findAncestorByClass(clzz);
        }
        return ancestorResult;
    }

    public int getFailedRowNum(){
    	int rowNum = 0;
        if (failed()){
            DataDrivableRowResult ddr = (DataDrivableRowResult)findAncestorByClass(DataDrivableRowResult.class);
            if (ddr != null){
                rowNum = ddr.getRowNum();
            }
        }
    	return rowNum;
    }


    public void recordFailureToCountableResult(){
        CountableResult countableRes = (CountableResult)findAncestorByClass(CountableResult.class);
        if (countableRes != null) {
            countableRes.countFailure();
        }
    }

    /**
     * @return the stack trace stating was happened
     */
    public String getErrorMsg(){
        String msg = null;
        if (error != null) {
            msg = error.getMessage();
        }
        return msg;
    }

    /**
     * @return the stack trace stating was happened
     */
    public String getHtmlFormattedErrorMsg(){
        String em = "";
        if (getErrorMsg() != null){
            em = JameleonUtility.decodeTextToXML(getErrorMsg());
        }
        return em;
    }

    /**
     * Gets the stack trace of the error formatted HTML-friendly
     * @return an html formatted stack trace
     */
    public String getHtmlFormattedStackTrace() {
        String stack = "";
        if (getError() != null){
            stack = JameleonUtility.getStack(getError());
            stack = JameleonUtility.decodeTextToXML(stack);
        }
        return stack;
    }


    /**
     * @return the time it took to run this step
     */
    public long getExecutionTime(){
        return executionTime;
    }

    /**
     * @return the time it took to run this step
     */
    public String getExecutionTimeToDisplay(){
        return JameleonUtility.executionTimeToString(executionTime);
    }

    /**
     * Set the execution time
     * @param executionTime - The time it took to run this step
     */
    public void setExecutionTime(long executionTime){
        this.executionTime = executionTime;
    }

    /**
     * @return The file that contains the display of the error
     */
    public File getErrorFile(){
        return errorFile;
    }

    /**
     * Sets the file that contains the display of the error
     * @param errorFile - The file that contains the display of the error
     */
    public void setErrorFile(File errorFile){
        this.errorFile = errorFile;
    }

    public String getIdentifier(){
        String id = null;
        if (tag != null && tag.getFunctionId() != null) {
            id = tag.getFunctionId();
        }else if (tag != null) {
            id = tag.getDefaultTagName();
        }
        return id;
    }

    public void setFailed(){
        failed = true;
        if (getParentResults() != null) {
            getParentResults().addFailedResult(this);
        }
    }

    public String getOutcome(){
        String outcome;
        if (passed()) {
            outcome = "PASSED";
        } else {
            outcome = "FAILED";
        }
        return outcome;
    }

    /**
     * Tells whether this result is a parent result or not.
     * @return <code>true</code> if this result is a parent result or <code>false</code> 
     * if it is a leaf node result
     */
    public abstract boolean isParent();

    /**
     * Tells whether this result is data driven or not.
     * @return <code>true</code> if this result is a data driven result or <code>false</code>
     * if it is not.
     */
    public abstract boolean isDataDriven();

    /**
     * Tells whether this result has children or not.
     * @return <code>true</code> if this result has children
     */
    public abstract boolean hasChildren();

    /**
     * Gets the parent results
     * 
     * @return The parent result of this result
     */
    public HasChildResults getParentResults(){
       return parentResults;
    }

    /**
     * Sets the parent results
     * @param parentResults - the parent results of this result
     */
    public void setParentResults(HasChildResults parentResults){
       this.parentResults = parentResults;
    }

    /**
     * Gets the tag that is tied to the results
     * @return The tag that is tied to the results
     */
    public FunctionalPoint getTag(){
        return tag;
    }

    /**
     * Sets the tag that is tied to the results
     * @param tag - The tag that the result is tried to
     */
    public void setTag(FunctionalPoint tag){
        this.tag = tag;
    }

    /**
     * @return true if no errors happened and no asserts failed and at least one asserts were executed
     */
    public boolean passed(){
        return !failed;
    }

    /**
     * @return true if any errors happened or any asserts failed
     */
    public boolean failed(){
        return failed;
    }

    // Append to the given StringBuffer an escaped version of the
    // given text string where XML special characters have been escaped
    // For a null string we appebd "<null>"
    protected String escapeXML(String text) {
        if (text == null) {
            text = "<null>";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '<') {
                sb.append("&lt;");
            } else if (ch == '>') {
                sb.append("&gt;");
            } else if (ch == '&') {
                sb.append("&amp;");
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public boolean equals(Object obj){
        boolean eqls = false;
        if (obj instanceof JameleonTestResult) {
            JameleonTestResult tcr = (JameleonTestResult) obj;
            eqls = tcr.tag != null && tcr.tag.equals(tag);
            eqls &= tcr.executionTime == executionTime;
        }
        return eqls;
    }

    public int hashCode(){
        return super.hashCode();
    }

    public String toString(){
        StringBuffer str = new StringBuffer();
        str.append("Execution Time: ").append(JameleonUtility.executionTimeToString(executionTime)).append("\n");
        if (tag != null) {
            str.append("Tag: ").append(tag.getDefaultTagName()).append("\n");
        }
        str.append("Outcome: ");
        String outcome = getOutcome();
        str.append(outcome);
        if (failed()) {
            str.append("\n").append(getErrorMsg()).append("\n");
        } else str.append("\n");
        if (errorFile != null) {
            str.append("Error File Name:").append(errorFile.getPath()).append("\n");
        }
        str.append("line: ").append(getLineNumber()).append(" column: ").append(getColumnNumber());
        return str.toString();
    }

    public String toXML(){
        StringBuffer str = new StringBuffer();
        String outcome = getOutcome();
        str.append("\t\t<outcome>").append(outcome).append("</outcome>\n");
        str.append("\t\t<execution-time>").append(JameleonUtility.executionTimeToString(executionTime)).append("</execution-time>\n");
        str.append("\t\t<execution-time-millis>").append(executionTime).append("</execution-time-millis>\n");
        if (error != null && error.getMessage() != null) {
            str.append("\t\t<error-message>").append(escapeXML(error.getMessage())).append("</error-message>\n");
            str.append("\t\t<error-stack>").append(escapeXML(JameleonUtility.getStack(error))).append("\t\t</error-stack>\n");
        }
        if (errorFile != null) {
            str.append("\t\t<error-file-name>").append(escapeXML(JameleonUtility.fixFileSeparators(errorFile.getPath()))).append("</error-file-name>\n");
        }
        if (tag != null) {
            str.append(tag.toXML());
        }
        return str.toString();
    }

    public void copyLocationAwareProperties(LocationAware la){
        setLineNumber(la.getLineNumber());
        setColumnNumber(la.getColumnNumber());
        setFileName(la.getFileName());
        setElementName(la.getElementName());
    }

    ////////////////////////////////////////////////////////////////////////////
    //          LocationAware implementation                                  //
    // /////////////////////////////////////////////////////////////////////////
    
    /** 
     * @return the line number of the tag 
     */
    public int getLineNumber(){
        return lineNumber;
    }
    
    /** 
     * Sets the line number of the tag 
     */
    public void setLineNumber(int lineNumber){
        this.lineNumber = lineNumber;
    }

    /** 
     * @return the column number of the tag 
     */
    public int getColumnNumber(){
        return columnNumber;
    }
    
    /** 
     * Sets the column number of the tag 
     */
    public void setColumnNumber(int columnNumber){
        this.columnNumber = columnNumber;
    }

    /** 
     * @return the Jelly file which caused the problem 
     */
    public String getFileName(){
        return scriptFileName;
    }
    
    /** 
     * Sets the Jelly file which caused the problem 
     */
    public void setFileName(String fileName){
        this.scriptFileName = fileName;
    }
    
    /** 
     * @return the element name which caused the problem
     */
    public String getElementName(){
        return elementTagName;
    }

    /**
     * Gets the date and time this result was executed
     * @return The date and time this result was executed
     */
    public Calendar getDateTimeExecuted() {
        return dateTimeExecuted;
    }

    /**
     * Sets the date and time this result was executed
     * @param dateTimeExecuted The date and time this result was executed
     */
    public void setDateTimeExecuted(Calendar dateTimeExecuted) {
        this.dateTimeExecuted = dateTimeExecuted;
    }

    /** 
     * Sets the element name which caused the problem
     */
    public void setElementName(String elementName){
        this.elementTagName = elementName;
    }

    public boolean isA(String clss){
    	boolean isInstance = false;
    	try{
    		isInstance = Class.forName(clss).isInstance(this); 
    	}catch(ClassNotFoundException cnfe){
    		//I guess this means false
    	}
    	return isInstance;
    }
}