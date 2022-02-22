/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2006 Christian W. Hargraves (engrean@hotmail.com)
    
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
import net.sf.jameleon.util.Configurator;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Tag;

public class TestCaseTagLibraryTest extends TestCase {
    private MockTestCaseTagLibrary tctl;

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( TestCaseTagLibraryTest.class );
    }

    public TestCaseTagLibraryTest( String name ) {
        super( name );
    }

    public void setUp() {
        Configurator.clearInstance();
        Configurator.getInstance().setConfigName("NonExistentFile");
        tctl = new MockTestCaseTagLibrary();
    }

    public void tearDown(){
        TestCaseTagLibrary.resetTags();
        tctl = null;
        Configurator.clearInstance();
    }

    public void testSetWarnOnNoPluginsFound(){
        TestCaseTagLibrary.setWarnOnNoPluginsFound(true);
        assertTrue("setWarnOnNoPluginsFound should be set to true", TestCaseTagLibrary.warnOnNoPluginsFound);
        assertTrue("The setWarnOnNoPluginsFound should now be set to true", TestCaseTagLibrary.st.getWarnOnNoPluginsFile());
    }

    public void testResetTags(){
        assertTrue("There should be more than one tag found!",TestCaseTagLibrary.st.getSupportedTags().size() > 0 );
        TestCaseTagLibrary.resetTags();
        assertEquals("# of tags after reseting the tag", 0, TestCaseTagLibrary.st.getTags().size());
    }

    public void testCreateTag() throws Exception{
        tctl.registerTag("testcase", net.sf.jameleon.TestCaseTag.class);
        Tag t = tctl.createTag("testcase", null);
        assertNotNull("testcase tag", t);
        Exception e = null;
        try{
            t = tctl.createTag("foobar", null);
        }catch(JellyException je){
            e = je;
        }
        assertNotNull("JellyException should have been thrown", e);
        assertTrue("JellyException's error message when a tag is not found", e.getMessage().indexOf("null:-1:-1: <foobar> is not a recognized Jameleon tag") >= 0);
    }

/*
*     public void testCreateTagScript() throws Exception{
*         tctl.registerTag("testcase", net.sf.jameleon.TestCaseTag.class);
*         TagScript ts = tctl.createTagScript("testcase", null);
*         assertNotNull("testcase tag", ts);
*         Exception e = null;
*         try{
*             ts= tctl.createTagScript("foobar", null);
*         }catch(JellyException je){
*             e = je;
*         }
*         assertNotNull("JellyException should have been thrown", e);
*         assertTrue("JellyException's error message when a tag is not found", e.getMessage().indexOf("null:-1:-1: <foobar> is not a recognized Jameleon tag") >= 0);
*     }
*/

    public class MockTestCaseTagLibrary extends TestCaseTagLibrary{
        protected void registerTag(String name, Class type){
            super.registerTag(name, type);
        }
    }
}
