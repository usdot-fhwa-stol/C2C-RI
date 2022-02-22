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
package net.sf.jameleon.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SupportedTagsTest extends TestCase {

    private SupportedTags st = null;
    private static final String PLUGINS_TEST = "tst/res/plugins-Test.properties";

    public static void main( String args[] ) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( SupportedTagsTest.class );
    }

    public SupportedTagsTest( String name ) {
        super( name );
    }

    public void setUp(){
    	Configurator.getInstance().setConfigName(PLUGINS_TEST);
        st = new SupportedTags();
        st.setPluginsFile(PLUGINS_TEST);
    }
    
    public void testGetPluginsFile(){
    	Configurator.clearInstance();
        st = new SupportedTags();
        assertEquals("Default plugins file", Configurator.DEFAULT_CONFIG_NAME, st.getPluginsFile());
    }

    public void testSetPluginsFile(){
        assertEquals("plugins file", PLUGINS_TEST, st.getPluginsFile());
    }

    public void testGetPropertiesFile(){
        assertEquals("plugins full file name", "plugins-Test.properties", st.getPropertiesFile("plugins-Test"));
    }

    public void testSetWarnOnNoPluginsFile(){
        assertTrue("Warn on no plugins file", st.warnOnNoPluginsFile);
        st.setWarnOnNoPluginsFile(true);
        assertTrue("Warn on no plugins file", st.warnOnNoPluginsFile);
        st.setWarnOnNoPluginsFile(false);
        assertFalse("No Warn on no plugins file", st.warnOnNoPluginsFile);
    }

    public void testGetPropertiesFromFile(){
        Properties props = st.getPropertiesFromFile("plugins-Test");
        assertEquals("# of properties found", 1, props.size());
        assertEquals("value of plugins", "plugin1 plugin2 plugin3", props.getProperty("plugins"));
    }

    public void testGetPluginsToRegister(){
        String[] plugins = st.getPlugins();
        assertNotNull("plugins should be configured", plugins);
        assertEquals("# of plugins ", 3, plugins.length);
        assertEquals("1st plugin", "plugin1", plugins[0]);
        assertEquals("2nd plugin", "plugin2", plugins[1]);
        assertEquals("3rd plugin", "plugin3", plugins[2]);
    }

    public void testGetTagsFromFile(){
        Map tags = st.getTagsFromFile("test-tags");
        assertEquals("# of tags", 3, tags.size());
        assertEquals("tag1", "some.package.SomeClass1", tags.get("tag1"));
        assertEquals("tag1", "some.package.SomeClass2", tags.get("tag2"));
        assertEquals("tag1", "some.package.SomeClass3", tags.get("tag3"));
    }

    public void testSetTagsFor(){
        Map tags = new HashMap();
        st.setTagsFor(tags, "test-jameleon-tags");
        assertEquals("# of jmln tags", 2, tags.size());
        assertEquals("jmln1", "TestCaseTag", tags.get("jmln1"));
        assertEquals("jmln2", "ParamTag", tags.get("jmln2"));
        st.setTagsFor(tags, "test-custom-tags");
        assertEquals("# of jmln tags", 4, tags.size());
        assertEquals("custom-tag1", "custom1", tags.get("custom-tag1"));
        assertEquals("custom-tag2", "custom2", tags.get("custom-tag2"));

    }

    public void testGetSupportedTags(){
        st.jameleonTags = "test-jameleon-tags";
        st.customTags = "test-custom-tags";
        Map tags = st.getSupportedTags();
        assertEquals("Total # of tags", 8, tags.size());
        assertEquals("tag1", "some.package.SomeClass1", tags.get("tag1"));
        assertEquals("tag2", "some.package.SomeClass2", tags.get("tag2"));
        assertEquals("tag3", "some.package.SomeClass3", tags.get("tag3"));
        assertEquals("tag4", "some.package.SomeClass4", tags.get("tag4"));
        assertEquals("custom-tag1", "custom1", tags.get("custom-tag1"));
        assertEquals("custom-tag2", "custom2", tags.get("custom-tag2"));
        assertEquals("jmln1", "TestCaseTag", tags.get("jmln1"));
        assertEquals("jmln2", "ParamTag", tags.get("jmln2"));
    }

}
