/*

    Jameleon - An automation testing tool..
    Copyright (C) 2003 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon;

import net.sf.jameleon.function.FunctionTag;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.XMLOutput;

/**
 * Used to map the value in one variable over to the value of another variable. 
 * 
 * sThis is useful when the same functional point is being used more than once or 
 * when the same variable name is used between different functional points that are 
 * in the same test-case.
 * 
 * The following is an example of creating a list and using it in a tag.
 * 
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon"&gt;
 *       &lt;map-variable toVariable="resultsText" variableType="list"&gt;
 *           &lt;variable-value&gt;value 1&lt;/variable-value&gt;
 *           &lt;variable-value&gt;value 2&lt;/variable-value&gt;
 *           &lt;variable-value&gt;value 3&lt;/variable-value&gt;
 *           &lt;variable-value&gt;value 4&lt;/variable-value&gt;
 *       &lt;/map-variable/&gt;
 *
 *   &lt;some-session application="someApp" beginSession="true"&gt;
 *       &lt;some-tag-that-uses-context-variables
 *           functionId="Verify successful navigation, using a different variable."
 *           someVariable="${resultsText}/&gt;
 *   &lt;/some-session&gt;
 * &lt;/testcase&gt;
 * </source></pre>
 * 
 * The following is an example of mapping one value of a variable to another variable (pretty much useless).
 * 
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon"&gt;
 *
 *   &lt;some-session application="someApp" beginSession="true"&gt;
 *       &lt;some-tag-that-uses-context-variables
 *           functionId="Verify successful navigation, using a different variable."&gt;
 *           &lt;map-variable
 *                 fromVariable="resultsText1"
 *                 toVariable="resultsText"/&gt;
 *       &lt;/some-tag-that-uses-context-variables&gt;
 *   &lt;/some-session&gt;
 * &lt;/testcase&gt;
 * </source></pre>
 * 
 * The following is an example of using the ${varName} notation (recommended)
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon"&gt;
 *
 *   &lt;some-session application="someApp" beginSession="true"&gt;
 *       &lt;some-tag-that-uses-context-variables
 *           functionId="Verify successful navigation, using a different variable."
 *           resultsText="${resultsText1"/&gt;
 *   &lt;/some-session&gt;
 * &lt;/testcase&gt;
 * </source></pre>
 * 
 * The following is an example of mapping multiple variable values to a single variable. 
 * Notice it's variableType is set to "list":
 * 
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon"&gt;
 *
 *   &lt;some-session application="someApp" beginSession="true"&gt;
 *       &lt;some-tag-that-uses-context-variables
 *           functionId="Verify successful navigation, using a different variable."&gt;
 *           &lt;map-variable
 *                 fromVariable="resultsText1"
 *                 toVariable="resultsText"
 *                 variableType="list"/&gt;
 *           &lt;map-variable
 *                 fromVariable="resultsText2"
 *                 toVariable="resultsText"
 *                 variableType="list"/&gt;
 *           &lt;map-variable
 *                 fromVariable="resultsText3"
 *                 toVariable="resultsText"
 *                 variableType="list"/&gt;
 *       &lt;/some-tag-that-uses-context-variables&gt;
 *   &lt;/some-session&gt;
 * &lt;/testcase&gt;
 * </source></pre>
 * 
 * The value(s) can be entered directly like so:
 * 
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon"&gt;
 *
 *   &lt;some-session application="someApp" beginSession="true"&gt;
 *       &lt;some-tag-that-uses-context-variables
 *           functionId="Verify successful navigation, using a different variable."&gt;
 *           &lt;map-variable toVariable="resultsText" variableType="list"&gt;
 *               &lt;variable-value&gt;value 1&lt;/variable-value&gt;
 *               &lt;variable-value&gt;value 2&lt;/variable-value&gt;
 *               &lt;variable-value&gt;value 3&lt;/variable-value&gt;
 *               &lt;variable-value&gt;value 4&lt;/variable-value&gt;
 *           &lt;/map-variable/&gt;
 *       &lt;/some-tag-that-uses-context-variables&gt;
 *   &lt;/some-session&gt;
 * &lt;/testcase&gt;
 * </source></pre>
 * 
 * @jameleon.function name="map-variable"
 */
public class VariableMappingTag extends VariableTag {
    
    protected boolean childExecuted = false;

    /**
     * An implementation of the <code>doTag</code> method provided by the <code>TagSupport</code> class.
     * Maps the value in the <code>fromVariable</code> over to the original variable name, <code>toVariable</code>.
     */
    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException{
        init();
        invokeBody(out);
        if (fromVariable != null && 
            fromVariable.length() > 0 &&
            toVariable != null &&
            toVariable.length() > 0 &&
            variableType != null &&
            variableType.length() >0) {
            setVariableValue(toVariable, context.getVariable(fromVariable));
        }else if (!childExecuted) {
            super.doTag(out);
        }
    }

    /**
     * Used to validate everything is set up correctly.
     * @throws MissingAttributeException - When a required attribute isn't set.
     * @throws JellyTagException - When this tag is used out of context.
     */
    protected void validate()throws MissingAttributeException, JellyTagException{
        Tag tag = getParent();
        if ( ! (tag instanceof FunctionTag) ) {
            throw new JellyTagException("This tag can only be imbedded in a tag represented by a class that extends FunctionTag.");
        }
        if ( fromVariable == null || fromVariable.trim().equals("") )  {
            throw new MissingAttributeException("fromVariable is a required attribute!");
        }else{
            value = (String)context.getVariable(fromVariable);
        }
        super.validate();
    }

    /**
     *  Gets the variable name in the CSV file being mapped to the officially supported function tag variable name..
     *  @return the variable name to map from or the variable that does not a <code>set</code> method for in the function point.
     */
    public String getFromVariable(){
        return this.fromVariable;
    }

    /**
     * Sets the variable name in the CSV file being mapped to the officially supported function tag variable name
     * method. This variable does not a <code>set</code> method for in the function point.
     * @param fromVariable - The variable name to map from.
     */
    public void setFromVariable(String fromVariable){
        this.fromVariable = fromVariable;
    }

    /**
     * @return The variable name to be mapped to the <code>fromVariable</code> or the originally supported variable name that has a set method for it
     * in the function point.
     */
    public String getToVariable(){
        return this.toVariable;
    }

    /**
     * Sets the variable name to map the <code>fromVariable</code> to. This is the originally supported variable name that has a set method for it
     * in the function point.
     * @param toVariable - the variable name to be mapped to the <code>fromVariable</code>.
     */
    public void setToVariable(String toVariable){
        this.toVariable = toVariable;
    }

    /**
     * Gets the type of the variable being stored. Currently, this only supports List.
     * @return The type of the variable being stored. Currently, this only supports List.
     */
    public String getVariableType(){
        return this.variableType;
    }

    /**
     * Sets the type of the variable being stored. Currently, this only supports List.
     * @param variableType - The type of the variable being stored. Currently, this only supports List.
     */
    public void setVariableType(String variableType){
        this.variableType = variableType;
    }

    protected void setChildExecuted(boolean childExecuted){
        this.childExecuted = childExecuted;
    }

}
