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
package net.sf.jameleon.bean;

import net.sf.jameleon.exception.JameleonException;
import net.sf.jameleon.util.TextMatcher;

/**
 * This class is used to be a generic regex text matcher helper to be used in
 * plug-ins wanting the regex (and maybe later the xpath) functionality to match
 * the given expression and set the matching text in the provided context variable.
 */
public class MatchingTextHelper{

    protected TextMatcher matcher;

    /**
     * Creates a MatchingTextHelper with a TextMatcher that is used for the following.
     * <ol>
     *     <li>get the current screen as text.</li>
     *     <li>Get the matching regular expression.</li>
     *     <li>Set the variable in the context.</li>
     * </ol>
     * You should find the last two are methods that are already implemented in the FunctionTag.
     * @param matcher - The interface used to communicate with the plug-in.
     */
    public MatchingTextHelper(TextMatcher matcher){
        this.matcher = matcher;
    }

    /**
     * Sets the matching text in the provided context variable.
     * @param varName - The context variable name
     * @param regex - The regular with the grouping representing the text to match
     * @param regexGroup - The number of the group to match.
     */
    public void setMatchingTextInContext(String varName, String regex, int regexGroup){
        String screenText = matcher.getCurrentScreenAsText();
        // Finding the matching pattern to extract the required data
        String matched = matcher.getMatchingRegexText(screenText, regex, regexGroup);
        if (matched != null) {
            // Setting the matched pattern as a Context variable
            matcher.setVariable(varName, matched);
        }else{
            throw new JameleonException("No match found for ["+regex+"]!");
        }
    }
}
