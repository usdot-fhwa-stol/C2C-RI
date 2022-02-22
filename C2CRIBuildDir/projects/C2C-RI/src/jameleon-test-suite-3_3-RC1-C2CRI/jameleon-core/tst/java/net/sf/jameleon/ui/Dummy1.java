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
package net.sf.jameleon.ui;

import net.sf.jameleon.plugin.junit.JUnitFunctionTag;

import org.apache.commons.jelly.XMLOutput;

/**
 * Joe Schmoe
 * @jameleon.function name="ui-dummy1" type="validation"
 * @author ABC
 * @jameleon.step Find the girl
 * @jameleon.step Attempt to date the girl
 * @jameleon.step Beg
 **/
public class Dummy1 extends JUnitFunctionTag {
    /**
     * Joe's brain
     * @jameleon.attribute required="false" contextName="ifIOnlyHadA"
     **/
    protected String brain;
    /**
     * Joe's IQ
     * @jameleon.attribute required="true" contextName="joesHighIq" default="75"
     */
    protected byte iq;

    /**
     * Joe's smile
     * @jameleon.attribute required="true" default="happy"
     */
    public void setJoeSchmoeSmile(String smile) {
    }

    public void testBlock(XMLOutput out) {}

}
