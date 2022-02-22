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

import net.sf.jameleon.bean.FunctionalPoint;

/**
 * An implementation of @see TestResult that represents the result of a function point
 */
public class FunctionResult extends JameleonTestResult {
	private static final long serialVersionUID = 1L;

    /**
     * The name of the file where the results of the display were written to if any
     */
    protected String resultsFileName;
    /**
     * If the function is a precondition
     */
    protected boolean precondition;
    /**
     * If the function is a postcondition
     */
    protected boolean postcondition;
    /**
     * If the function is a testStep
     */
    protected boolean testStep;

    /**
     * The default constructor
     */
    public FunctionResult(){
        super();
    }

    /**
     * @param tag - The functional point tied to the results
     */
    public FunctionResult(FunctionalPoint tag){
        super(tag);
    }

    /**
     * @param tag - The functional point tied to the results
     * @param parentTestResult - The parent test results to update
     */
    public FunctionResult(FunctionalPoint tag, HasChildResults parentTestResult){
        super(tag, parentTestResult);
    }

    protected String getErrorMsgPrefix(){
        String prefix = "";
        if (precondition) {
            prefix = "PRECONDITION FAILURE: ";
        } else if (postcondition) {
            prefix = "POSTCONDITION FAILURE: ";
        } else if (testStep) {
            prefix = "TESTSTEP FAILURE: ";
        }
        return prefix;
    }

    public String getErrorMsg(){
        String errMsg = super.getErrorMsg();
        if (errMsg != null) {
            errMsg = getErrorMsgPrefix() + errMsg;
        }
        return errMsg;
    }

	public boolean isParent() {
		return false;
	}

    public boolean isDataDriven() {
        return false;
    }

    public boolean hasChildren() {
        return false;
    }

    /**
     * Tells whether the functional point is a precondition
     * @return true if this is a precondition, otherwise false
     */
    public boolean isPrecondition(){
        return precondition;
    }

    /**
     * Sets the FunctionalPoint as a precondition
     * @param precondition - set to true to make this a precondition
     */
    public void setPrecondition(boolean precondition){
        this.precondition = precondition;
    }

    /**
     * Tells whether the functional point is a postcondition
     * @return true if this is a postcondition, otherwise false
     */
    public boolean isPostcondition(){
        return postcondition;
    }

    /**
     * Sets the FunctionalPoint as a postcondition
     * @param postcondition - set to true to make this a postcondition
     */
    public void setPostcondition(boolean postcondition){
        this.postcondition = postcondition;
    }

    /**
     * Tells whether the functional point is a testStep
     * @return true if this is a testStep, otherwise false
     */
    public boolean isTestStep(){
        return testStep;
    }

    /**
     * Sets the FunctionalPoint as a testStep
     * @param testSTep - set to true to make this a testStep
     */
    public void setTestStep(boolean testStep){
        this.testStep = testStep;
    }
    
    /**
     * @return a String representation of the function point results
     */
    public String toString(){
        StringBuffer sb = new StringBuffer();
        if (tag != null) {
            sb.append(tag.toString());
        }
        sb.append(super.toString());
        return sb.toString();
    }

    /**
     * @return an XML representation of the function point results
     */
    public String toXML(){
        StringBuffer str = new StringBuffer("\n");
        str.append("\t<function-point>\n");
        if (tag != null && tag.getFunctionId() != null) {
            str.append("\t\t<functionId>").append(escapeXML(tag.getFunctionId())).append("</functionId>\n");
        }
        str.append(super.toXML());
        if (tag != null) {
            str.append(tag.toXML());
        }
        str.append("\t</function-point>\n");
        return str.toString();
    }
}
