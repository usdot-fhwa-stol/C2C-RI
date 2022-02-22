/*
    Jameleon - An automation testing tool..
    Copyright (C) 2007 Christian W. Hargraves (engrean@hotmail.com)
    
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

import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jameleon.bean.FunctionalPoint;

public class GenSyntaxReferenceTest extends TestCase {

    private GenSyntaxReference genReference;

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( GenSyntaxReferenceTest.class );
    }

    public GenSyntaxReferenceTest( String name ) {
        super( name );
    }

    public void setUp() {
        genReference = new GenSyntaxReference();
    }

    public void tearDown(){
        genReference = null;
    }

    public void testConstructor(){
        assertNotNull("supportedTags", genReference.supportedTags);
    }

    public void testGetTagsForPlugin(){
        Map tags = genReference.getTagsForPlugin("test-plugin");
        Set keys = tags.keySet();
        Object[] tagsA = keys.toArray();
        assertTrue("There should be at least one key ", tagsA.length > 1); 
        //Now check that the order is being returned.
        assertEquals("1st tag", "bean-dummy-muliple-apps", (String)tagsA[0]);
        assertEquals("2nd tag", "bean-dummy-single-app", (String)tagsA[1]);
        assertEquals("last tag", "young-joe-schmoe", (String)tagsA[tagsA.length-1]);
        FunctionalPoint fp = (FunctionalPoint)tags.get("young-joe-schmoe");
        assertNotNull("young-joe-schmoe tag", fp);
        assertEquals("tag name", "joe-schmoe-jr", (String)fp.getTagNames().get(1));
        assertNotNull("brain attribute", fp.getAttribute("brain").getName());
    }

}
