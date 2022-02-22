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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111AssertLevel.NO_FUNCTION07 USA
*/
package net.sf.jameleon.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ConfiguratorTest extends TestCase {

    private Configurator config;
    private static final String NON_EXISTENT_CONF = "jameleon.nowaythisexists";
    private static final String UNIT_TEST_CONF = "tst/res/jameleon.unittest";
    private static final String OTHER_ENV_CONF = "tst/res/jameleon.otherenv";

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( ConfiguratorTest.class );
    }

    public ConfiguratorTest( String name ) {
        super( name );
    }

    public void setUp(){
        Configurator.clearInstance();
        config = Configurator.getInstance();
    }

    public void tearDown(){
        Configurator.clearInstance();
    }

    public void testGetInstance(){
        Configurator config2 = Configurator.getInstance();
        assertTrue(config == config2);
        assertEquals("Default config name", Configurator.DEFAULT_CONFIG_NAME, config.configName);
    }

    public void testClearInstance(){
        Configurator.clearInstance();
        Configurator config2 = Configurator.getInstance();
        assertTrue(config != config2);
    }

    public void testGetInputStream() throws Exception{
        FileInputStream is = null;
        boolean exceptionThrown = false;
        try{
            is = (FileInputStream)config.getInputStream(NON_EXISTENT_CONF);
        }catch(FileNotFoundException fnfe){
            exceptionThrown = true;
        }
        assertTrue("an expected exception was not thrown", exceptionThrown);
        is = (FileInputStream)config.getInputStream(UNIT_TEST_CONF);
        assertTrue("File should exist", is.getFD().valid());
    }

    public void testSetConfigName(){
        config.setConfigName(NON_EXISTENT_CONF);
        assertEquals("configName", NON_EXISTENT_CONF, config.configName);
    }

    public void testGetValueWithDefault(){
        assertEquals("true", config.getValue("booleanvar1","true"));
        assertEquals("false", config.getValue("booleanvar1","false"));
        config.setValue("booleanvar1", "true"); 
        assertEquals("true", config.getValue("booleanvar1","false"));
        assertEquals("true", config.getValue("booleanvar1","true"));
        config.setValue("booleanvar1", null); 
        assertEquals("false", config.getValue("booleanvar1","false"));
        assertEquals("true", config.getValue("booleanvar1","true"));
        Configurator.clearInstance();
        config = Configurator.getInstance();
        config.setConfigName(NON_EXISTENT_CONF);
        assertEquals("true", config.getValue("booleanvar1","true"));
    }

    public void testGetValueAsArray(){
        config.setConfigName(OTHER_ENV_CONF);
        config.configure();
        String[] values = config.getValueAsArray("unusedVar");
        assertNotNull("unusedVar should not be null", values);
        assertEquals("# of values for unusedVar", 3, values.length);
        assertEquals("1st value", "val1", values[0]);
        assertEquals("3rd value", "val3", values[2]);
    }

    public void testGetKeys(){
        config.setConfigName(UNIT_TEST_CONF);
        config.configure();
        assertEquals("# of values", 20, config.getKeys().size());
    }

    public void testGetKeysStartingWith(){
        config.setConfigName(UNIT_TEST_CONF);
        config.configure();
        Set keys = config.getKeysStartingWith("assert");
        assertEquals("# of values", 4, keys.size());
        assertNotNull("assertGreaterThanLevel", config.getValue("assertGreaterThanLevel"));
        assertNotNull("assertLessThanLevel", config.getValue("assertLessThanLevel"));
        assertNotNull("assertLevels", config.getValue("assertLevels"));
        assertNotNull("assertLevel", config.getValue("assertLevel"));
        keys = config.getKeysStartingWith("nowayakeystartswiththistext");
        assertEquals("# of keys returned", 0, keys.size());
    }

    public void testConfigure(){
        config.setConfigName(NON_EXISTENT_CONF);
        config.configure();
        assertNotNull(config.props);
        assertEquals("# of keys", 0, config.props.keySet().size());
        Configurator.clearInstance();
        config = Configurator.getInstance();
        config.setConfigName(UNIT_TEST_CONF);
        config.configure();
        assertEquals("KFC",config.props.getProperty("csvCharset"));
        assertNotNull(config.props);
        config.setConfigName(NON_EXISTENT_CONF);
        config.configure();
        assertEquals("KFC",config.props.getProperty("csvCharset"));
        assertNotNull(config.props);
    }

    public void testGetValue(){
        config.setConfigName(NON_EXISTENT_CONF);
        config.configure();
        String bugUrl = config.getValue("bugTrackerUrl");
        assertNull("bugTrackerUrl: ", bugUrl);
        Configurator.clearInstance();
        config = Configurator.getInstance();

        config.setConfigName(UNIT_TEST_CONF);
        config.configure();
        bugUrl = config.getValue("bugTrackerUrl");
        assertEquals("bugTrackerUrl: ", "this is my bug tracker url", bugUrl);
        String noVal = config.getValue("someUnknownKey");
        assertNull("a value of a non-existent key", noVal);
    }

    public void testSetValue(){
        config.setConfigName(UNIT_TEST_CONF);
        config.configure();
        String bugUrl = config.getValue("bugTrackerUrl");
        assertEquals("bugTrackerUrl: ", "this is my bug tracker url", bugUrl);
        config.setValue("bugTrackerUrl", "new bug url");
        bugUrl = config.getValue("bugTrackerUrl");
        assertEquals("bugTrackerUrl: ", "new bug url", bugUrl);
    }
}
