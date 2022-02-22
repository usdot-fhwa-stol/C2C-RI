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

import java.util.List;

/**
 * Describes a result that can have other results
 */
public interface HasChildResults {

    /**
     * Adds a child result to the list of results
     * @param childResult - the child JameleonTestResult to add
     */
    public void addChildResult(JameleonTestResult childResult);
    /**
     * Gets all the ancestor children results
     * 
     * @return List
     */
    public List getAllChildrenResults();
    /**
     * Gets all the ancestor leaf children results that have failed
     *
     * @return List
     */
    public List getAllFailedLeafChildrenResults();
    /**
     * Gets all the ancestor children results that can be counted as
     * test case results
     * 
     * @return List
     */
    public List getCountableResults();
    /**
     * Gets all the failed ancestor children results that can be counted as
     * test case results
     * 
     * @return List
     */
    public List getFailedCountableResults();
    /**
     * Gets the children results
     * 
     * @return List
     */
    public List getChildrenResults();
    /**
     * Sets the result as failed
     */
    public void setFailed();
    /**
     * Adds a failed result
     */
    public void addFailedResult(JameleonTestResult result);
    /**
     * Gets the failed results
     * 
     * @return List
     */
    public List getFailedResults();

}
