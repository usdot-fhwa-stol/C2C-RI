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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon.util;

/**
 * A way to communicate with the plugin for matching text, and setting it in the context
 */
public interface TextMatcher{

    /**
     * Gets the current screen as text. This method should define logic needed
     * to get the text representation of the screen.
     * 
     * @return String
     */
    public String getCurrentScreenAsText();
    /**
     * Gets the matching regular expression text.
     * @param text - The text to run the regular expression against
     * @param regex - The regular expression
     * @param group - The group number of the regular expression to match against.
     * If the regex has no parens, then the group would be zero.
     * 
     * @return String
     */
    public String getMatchingRegexText(String text, String regex, int group);
    /**
     * Sets the variable in the context.
     * @param key - The context variable name
     * @param value - The variable value.
     */
    public void setVariable(String key, Object value);

}
