/*
    Jameleon - An automation testing tool..
    Copyright (C) 2006 Christian W. Hargraves (engrean@hotmail.com)
    
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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111AssertLevel.NO_FUNCTION07 USA
*/
package net.sf.jameleon.bean;

import com.mockobjects.dynamic.ConstraintMatcher;
import com.mockobjects.dynamic.Mock;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import net.sf.jameleon.util.TextMatcher;

public class MatchingTextHelperTest extends TestCase {

    public MatchingTextHelperTest( String name ) {
        super( name );
    }

    //JUnit Methods
    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( MatchingTextHelperTest.class );
    }

    public void setUp() {
    }

    public void tearDown(){
    }

    //End JUnit Methods
    public void testSetMatchingTextInContext(){
        final String text = "some text 23243";
        final String regex = "[\\d]+";
        final String varName = "test";
        Mock mockTextMatcher = new Mock(TextMatcher.class);
        mockTextMatcher.expectAndReturn("getCurrentScreenAsText", text);
        mockTextMatcher.expectAndReturn("getMatchingRegexText",new ConstraintMatcher(){
                public boolean matches(Object[] args) {
                    String textL = (String)args[0];
                    String regexL = (String)args[1];
                    int regexGroup = ((Integer)args[2]).intValue();
                    return (regexGroup == 0 && textL.equals(text) && regexL.equals(regex));
                }
                public Object[] getConstraints() {
                    return new Object[]{String.class, String.class, Integer.TYPE};
                }
                }, "23243");
        mockTextMatcher.expect("setVariable",new ConstraintMatcher(){
                public boolean matches(Object[] args) {
                    String key = (String)args[0];
                    String value = (String)args[1];
                    return (key.equals(varName) && value.equals("23243"));
                }
                public Object[] getConstraints() {
                    return new Object[]{String.class, String.class};
                }
                });
        MatchingTextHelper helper = new MatchingTextHelper((TextMatcher)mockTextMatcher.proxy());
        helper.setMatchingTextInContext(varName, regex, 0);
    }
}
