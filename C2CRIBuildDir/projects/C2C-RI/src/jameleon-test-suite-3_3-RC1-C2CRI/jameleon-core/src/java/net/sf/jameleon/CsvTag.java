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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
/**
 * Iterates over all nested tags one time per row of a CSV file.
 * The name of the CSV file is based on the <code>testEnvironment</code>, <code>organization</code>, and the 
 * <code>name</code> attributes. The directory structure, then follows the <code>testEnvironment</code> 
 * then inside that directory the <code>organization</code> if set. The file name matches the name set in 
 * the csv attribute.
 * 
 * For example, to execute the opening of an application and doing something <b>n</b> number of times:
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon"&gt;
 *   &lt;csv name="some_file_name_without_extension"&gt;
 *     &lt;some-session application="someApp" beginSession="true"&gt;
 *       &lt;some-tag-that-uses-context-variables
 *           functionId="Verify successful navigation, using a different variable."/&gt;
 *     &lt;/some-session&gt;
 *   &lt;/csv&gt;
 * &lt;/testcase&gt;
 * </source></pre>
 * 
 * Maybe opening the application <b>n</b> number of times takes too long, but
 * each of the scenarios still need to be executed. Try putting the csv tag inside
 * the session tag:
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon"&gt;
 *   &lt;some-session application="someApp" beginSession="true"&gt;
 *     &lt;csv name="some_file_name_without_extension"&gt;
 *       &lt;some-tag-that-uses-context-variables
 *           functionId="Verify successful navigation, using a different variable."/&gt;
 *     &lt;/csv&gt;
 *   &lt;/some-session&gt;
 * &lt;/testcase&gt;
 * </source></pre>
 * 
 *
 * All values that are not defined in a CSV file will be considered 'null'. To define an empty String,
 * simply define the value as <b>&quot;&quot;</b>.
 * For example:
 * <pre><source>
 * var1,var2,var3
 * one,"",</source></pre>
 * In the above example, var2 will be an empty string and var3 will be null.
 * @jameleon.function name="csv"
 */
public class CsvTag extends AbstractCsvTag {

    /**
     * Gets the logger used for this tag
     * @return the logger used for this tag.
     */
    protected Logger getLogger(){
        return LogManager.getLogger(CsvTag.class.getName());
    }

    /**
     * Gets the trace message when the execution is beginning and ending.
     * The message displayed will already start with BEGIN: or END:
     * @return the trace message when the execution is just beginning and ending.
     */
    protected String getTagTraceMsg(){
        return "parsing " + getCsvFile();
    }

    /**
     * Describe the tag when error messages occur.
     * The most appropriate message might be the tag name itself.
     * @return A brief description of the tag or the tag name itself.
     */
    public String getTagDescription(){
        return "csv tag";
    }

}
