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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A JameleonTestResult that can have children
 */
public abstract class TestResultWithChildren extends JameleonTestResult implements HasChildResults {

    /**
     * A list of Child Results
     */
    protected transient List childrenResults = new ArrayList();
    /**
     * A list of failed Child Results
     */
    protected transient List failedResults = new ArrayList();

    public TestResultWithChildren(){
        super();
    }

    public TestResultWithChildren(FunctionalPoint tag){
        super(tag);
    }

    public TestResultWithChildren(FunctionalPoint tag, HasChildResults parentResults){
        super(tag, parentResults);
    }

    /**
     * Adds a JameleonTestResult to the list of JameleonTestResult int the TestCase
     * @param childResult - a child result
     */
    public void addChildResult(JameleonTestResult childResult){
        childrenResults.add(childResult);
    }

    /**
     * Adds a failed JameleonTestResult to the list of failed results
     * @param failedResult - A failed child result
     */
    public void addFailedResult(JameleonTestResult failedResult){
        failed = true;
        failedResults.add(failedResult);
        if (parentResults != null) {
            parentResults.addFailedResult(failedResult);
        }
    }

    /**
     * Gets all the ancestor children results
     * 
     * @return List
     */
    public List getAllChildrenResults(){
        List allResults = new ArrayList();
        JameleonTestResult jtr;
        for (Iterator it = getChildrenResults().iterator(); it.hasNext();) {
            jtr = (JameleonTestResult)it.next();
            allResults.add(jtr);
            if (jtr instanceof HasChildResults) {
                allResults.addAll(((HasChildResults)jtr).getAllChildrenResults());
            }
        }
        return allResults;
    }

    /**
     * Gets all the ancestor leaf children results that have failed
     *
     * @return List
     */
    public List getAllFailedLeafChildrenResults(){
        List allFailedLeafResults = new ArrayList();
        List allResults = getAllChildrenResults();
        JameleonTestResult jtr;
        for (Iterator it = allResults.iterator(); it.hasNext();) {
            jtr = (JameleonTestResult)it.next();
            if (jtr.failed() && !jtr.isParent()) {
                allFailedLeafResults.add(jtr);
            }
        }
        return allFailedLeafResults;
    }

    /**
     * Gets all the countable ancestor children results that can be counted as
     * test case results
     * 
     * @return List
     */
    public List getCountableResults(){
        List countabeResults = new ArrayList();
        Object obj;
        if (this instanceof CountableResult) {
            countabeResults.add(this);
        }
        countabeResults.addAll(getCountableChildResults());
        return countabeResults;
    }

    protected List getCountableChildResults(){
        List countabeResults = new ArrayList();
        Object obj;
        for (Iterator it = getChildrenResults().iterator(); it.hasNext();) {
            obj = it.next();
            if (obj instanceof HasChildResults) {
                countabeResults.addAll(((HasChildResults)obj).getCountableResults());
            }else if (obj instanceof CountableResult) {
                countabeResults.add(obj);
            }
        }
        return countabeResults;
    }

    /**
     * Gets all the failed ancestor children results that can be counted as
     * test case results
     * 
     * @return List
     */
    public List getFailedCountableResults(){
        List failedCountableResults = new ArrayList();
        CountableResult cr;
        for (Iterator it = getCountableResults().iterator(); it.hasNext();) {
            cr = (CountableResult)it.next();
            if (cr.isCountableResultFailed()) {
                failedCountableResults.add(cr);
            }
        }
        return failedCountableResults;
    }

    /**
     * Gets a list of child results
     * @return a list of child results
     */
    public List getChildrenResults(){
        return childrenResults;
    }

    /**
     * Gets a list of failed child results
     * @return a list of failed child results
     */
    public List getFailedResults(){
        return failedResults;
    }

    public void destroy(){
        super.destroy();
        childrenResults.clear();
    }

	public boolean isParent() {
		return true;
	}

    /**
     * @return an XML representation of the results
     */
    public String toXML(){
        StringBuffer str = new StringBuffer("\n");
        str.append(super.toXML());
        if (getChildrenResults().size() > 0) {
            str.append("<children-results>\n");
        }
        for (Iterator it = getChildrenResults().iterator(); it.hasNext();) {
            str.append(((XMLable)it.next()).toXML());
        }
        if (getChildrenResults().size() > 0) {
            str.append("</children-results>\n");
        }

        return str.toString();
    }

    public boolean hasChildren(){
        return true;
    }

}
