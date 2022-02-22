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

import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class SupportedTags {

    public final static String DEFAULT_JAMELEON_TAGS = "jameleon-core";
    public final static String DEFAULT_CUSTOM_TAGS = "TestCaseTagDefs";

    protected String pluginsFile = Configurator.getInstance().getConfigName();
    protected String jameleonTags = DEFAULT_JAMELEON_TAGS;
    protected String customTags = DEFAULT_CUSTOM_TAGS;
    protected boolean warnOnNoPluginsFile = true;
    protected Map tags = Collections.synchronizedMap(new TreeMap());

    public synchronized Map getSupportedTags() {
        if (tags.size() == 0) {
            setTagsFor(tags, jameleonTags);
            setTagsFor(tags, customTags);
            String [] plugins = getPlugins();
            for (int i = 0; plugins != null && i < plugins.length; i++) {
                setTagsFor(tags,plugins[i]);
            }
        }
        return tags;
    }

    /**
     * A short-circuit method used to just get the tags for a specific file.
     * First call setTagsFor followed by this file. To use this repeatedly,
     * be sure to clear the Map returned by this method or the tags will
     * be appended to the Map being returned.
     */
    public Map getTags(){
        return tags;
    }

    public void setTagsFor(Map tags, String propName) {
        Map fileTags = getTagsFromFile(propName);
        if (fileTags != null && fileTags.size() > 0) {
            tags.putAll(fileTags);
        }
    }

    public void setPluginsFile(String pluginsFile) {
        this.pluginsFile = pluginsFile;
    }

    public String getPluginsFile() {
        return pluginsFile;
    }

    protected String getPropertiesFile(String propsName) {
        return propsName+".properties";
    }

    public void setWarnOnNoPluginsFile(boolean warnOnNoPluginsFile) {
        this.warnOnNoPluginsFile = warnOnNoPluginsFile;
    }

    public boolean getWarnOnNoPluginsFile() {
        return warnOnNoPluginsFile;
    }

    protected Properties getPropertiesFromFile(String filename) {
        Properties props = new Properties();
        String name = getPropertiesFile(filename);
        try {
            props.load(getInputStreamFromClassPath(name));
        } catch (IOException ioe) {
        } catch (NullPointerException npe) {
        }
        return props;
    }

    private InputStream getInputStreamFromClassPath(String name){
        InputStream input = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
            input= classLoader.getResourceAsStream( name );
        } else {
            input= classLoader.getResourceAsStream( name );
            if (input == null) {
                classLoader = this.getClass().getClassLoader();
                input= classLoader.getResourceAsStream( name );
            }
        }
        return input;
    }

    public String[] getPlugins() {
        String[] pluginProps = Configurator.getInstance().getValueAsArray("plugins");
        if ( (pluginProps == null || pluginProps.length == 0 ) &&
        	  warnOnNoPluginsFile) {
            System.err.println("WARNING: Could not find 'plugins' entry in "+pluginsFile+".\nNo plug-ins will be enabled!\n");
            warnOnNoPluginsFile = false;
        }
        return pluginProps;
    }

    public Map getTagsFromFile(String fileName) {
        Map tags = new HashMap();
        Properties props = getPropertiesFromFile(fileName);
        Iterator it = props.keySet().iterator();
        String tag = null;
        String value = null;
        while (it.hasNext()) {
            tag = (String)it.next();
            value = props.getProperty(tag);
            tags.put(tag, value);
        }
        props = null;
        return tags;
    }

}
