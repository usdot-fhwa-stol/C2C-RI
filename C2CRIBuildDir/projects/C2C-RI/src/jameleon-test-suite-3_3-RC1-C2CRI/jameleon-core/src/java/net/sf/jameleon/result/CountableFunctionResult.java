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
package net.sf.jameleon.result;

import net.sf.jameleon.bean.FunctionalPoint;

/**
 * An implementation of @see TestResult that represents the result of a function point
 */
public class CountableFunctionResult extends FunctionResult implements CountableResult {
    private static final long serialVersionUID = 1L;

    /**
     * The default constructor
     */
    public CountableFunctionResult(){
        super();
    }

    /**
     * @param tag - The functional point tied to the results
     */
    public CountableFunctionResult(FunctionalPoint tag){
        super(tag);
    }

    /**
     * @param tag - The functional point tied to the results
     * @param parentTestResult - The parent test results to update
     */
    public CountableFunctionResult(FunctionalPoint tag, HasChildResults parentTestResult){
        super(tag, parentTestResult);
    }


    /**
     * Mark this result a failed
     */
    public void countFailure(){
        setFailed();
    }
    /**
     * Get whether this result failed or not
     * @return true if the result failed
     */
    public boolean isCountableResultFailed(){
        return failed();
    }
}
