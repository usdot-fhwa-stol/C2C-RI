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
package net.sf.jameleon.function;

import net.sf.jameleon.function.MockFunctionTag;

/**
 * Some dummy tag with docs
 * @jameleon.function name="tag-attributable" type="action"
 * @jameleon.step step 1
 * @jameleon.step step 2
 **/
public class DummyTagAttributable extends MockFunctionTag {
   /**
    * String variable
    * @jameleon.attribute required="true"
    * contextName="dmmy_string"
    */
    protected String myString;

   /**
    * The Answer
    * @jameleon.attribute required="false"
    * contextName="dmmy_answer" default="42"
    */
    protected int fortyTwo;

}
