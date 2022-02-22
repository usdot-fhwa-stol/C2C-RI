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

/**
 * Documents the test case's author. 
 * This tag is used directly inside the &lt;testcase/&gt; tag.
 * 
 * A example might be:
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon"&gt;
 *     &lt;test-case-summary&gt;Tests some feature&lt;/test-case-summary&gt;
 *     &lt;test-case-bug&gt;3423&lt;/test-case-bug&gt;
 *     &lt;test-case-author&gt;Christian Hargraves&lt;/test-case-author&gt;
 *     &lt;test-case-level&gt;ACCEPTANCE&lt;/test-case-level&gt;
 *     &lt;test-case-level&gt;REGRESSION&lt;/test-case-level&gt;
 *     &lt;functional-point-tested&gt;csv&lt;/functional-point-tested&gt;
 *     &lt;application-tested&gt;framework&lt;/application-tested&gt;
 *     &lt;test-case-requirement&gt;AP-123&lt;/test-case-requirement&gt;
 *     &lt;test-case-id&gt;TC-123&lt;/test-case-id&gt;
 * &lt;/testcase"&gt;
 * </source></pre>
 * 
 * @jameleon.function name="test-case-author"
 */
public class TestCaseAuthorTag extends AbstractTestCaseDocAttribute {
    
    protected void setTestCaseValue(String text){
        tct.getTestCase().setAuthor(text);
    }
}
