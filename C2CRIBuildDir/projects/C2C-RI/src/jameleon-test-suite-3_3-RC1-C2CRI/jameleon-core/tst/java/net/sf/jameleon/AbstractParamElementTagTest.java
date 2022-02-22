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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import net.sf.jameleon.AbstractParamElementTag;
import net.sf.jameleon.ParamTag;
import net.sf.jameleon.ParamNameTag;

public class AbstractParamElementTagTest extends TestCase {

    private ParamTag paramTag;
    private AbstractParamElementTag nameTag;

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( AbstractParamElementTagTest.class );
    }

    public AbstractParamElementTagTest( String name ) {
        super( name );
    }

    public void setUp() {
        paramTag = new ParamTag();
        nameTag = new ParamNameTag();
        nameTag.setParent(paramTag);
    }

    public void tearDown(){
        paramTag = null;
        nameTag = null;
    }

    public void testValidateParentTagSuccess() throws Exception{
        nameTag.validateParentTag();
        MockCsvTag csvTag = new MockCsvTag();
        nameTag.setParent(csvTag);
        csvTag.setParent(paramTag);
        nameTag.validateParentTag();
    }

    public void testValidateParentTagFailure(){
        MockCsvTag csvTag = new MockCsvTag();
        nameTag.setParent(csvTag);
        try{
            nameTag.validateParentTag();
            fail("nameTag no longer has an ancestor of a paramTag. validateParentTag should have failed!");
        }catch(Exception e){
            assertTrue("good job. we failed the validation!", true);
        }

    }

}
