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
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class Configurator {

    private static Configurator config;
    public static final String DEFAULT_CONFIG_NAME = "jameleon.conf";
    protected String configName = DEFAULT_CONFIG_NAME;
    //Static because the Configurator instance gets cleared everytime between
    //test case executions.
    protected static boolean warningMsgGiven = false;

    protected Properties props;

    /**
     * This is a singleton, use {@link #getInstance} instead
     */
    private Configurator(){
    }

    /**
     * Gets an instance Configurator if one has already been created or creates a new one if not.
     * @return An instance Configurator if one has already been created or creates a new one if not.
     */
    public synchronized static Configurator getInstance(){
        if (config == null) {
            config = new Configurator();
        }
        return config;
    }

    public Set getKeys(){
        configure();
        Set keys = null;
        if (props != null) {
            keys = props.keySet();
        }
        return keys;
    }

    public Properties getProperties(){
        configure();
        return props;
    }

    public Set getKeysStartingWith(String prefix){
        configure();
        Set matchingKeys = Collections.synchronizedSortedSet(new TreeSet());
        if (props != null) {
            Set keys = props.keySet();
            Iterator it = keys.iterator();
            String key;
            while(it.hasNext()){
                key = (String)it.next();
                if (key.startsWith(prefix)) {
                     matchingKeys.add(key);
                }
            }
        }
        return matchingKeys;
    }

    /**
     * Gets a value from the provided configuration file or if it's not set, then set it's default value
     * @param key - the name of the variable in the configuration file.
     * @param defaultValue - the default value to set if the variable is not set in the configuration file.
     * @return A value from the provided configuration file or a default value
     */
    public String getValue(String key, String defaultValue){
        configure();
        String value = defaultValue;
        if (props != null) {
            value = props.getProperty(key, defaultValue);
        }
        return value;
    }


    /**
     * Gets a value from the provided configuration file.
     * @param key - the name of the variable in the configuration file.
     * @return A value from the provided configuration file.
     */
    public String getValue(String key){
        configure();
        String value = null;
        if (props != null) {
            value = props.getProperty(key);
        }
        return value;
    }

    /**
     * Gets a value from the provided configuration file as an array.
     * This is used for space-separated variables that have values which represent arrays
     * @return A value from the provided configuration file.
     */
    public String[] getValueAsArray(String key){
        return getValueAsArray(key, " ");
    }

    /**
     * Gets a value from the provided configuration file as an array.
     * This is used for space-separated variables that have values which represent arrays
     * @return A value from the provided configuration file.
     */
    public String[] getValueAsArray(String key, String separator){
        configure();
        String[] values = null;
        if (props != null) {
            String value = props.getProperty(key);
            if (value != null) {
                values = value.split(separator);
            }
        }
        return values;
    }

    /**
     * Sets a value of a given property
     * @param key - the name of the variable to set
     * @param value - the value to the variable to.
     */
    public void setValue(String key, String value){
        configure();
        if (props != null) {
            if (value != null) {
                props.setProperty(key, value);
            }else{
                props.remove(key);
            }
        }
    }

    /**
     * Set everything up by reading the configuration file. This method is called
     * by {@link #getValue} and is only executed if things aren't already set up.
     * In other words, once a file has been read in, this method will no longer do 
     * anything. To read in a new file, call {@link #clearInstance} first.
     */
    protected void configure(){
        if (props == null) {
            props = new Properties();
            InputStream in = null;
            try{
                in = getInputStream(configName);
                props.load(in);
            }catch(IOException e){
                if (!warningMsgGiven) {
                    System.err.println(configName+" not found. Using default settings.");
                    warningMsgGiven = true;
                }
            }catch(NullPointerException npe){
                //this is just an annoying message
            }finally{
                if (in != null) {
                    try{
                        in.close();
                    }catch(IOException ioe){
                        ioe.printStackTrace();
                    }
                }
            }
        }
    }

    protected InputStream getInputStream(String fileName) throws IOException{
        return new FileInputStream(fileName);
    }

    /**
     * Sets the name of the configuration file.
     * @param configName - the name and path of the config file. The default location is from where the JVM was started from.
     */
    public void setConfigName(String configName){
        this.configName = configName;
    }

    /**
     * Gets the name of the configuration file.
     * @return the name and path of the config file.
     */
    public String getConfigName(){
        return configName;
    }

    /**
     * Cleans everything up and sets the current instance to null
     */
    public synchronized static void clearInstance(){
        if (config != null) {
            if (config.props != null) {
                config.props.clear();
                config.props = null;
            }
            config.configName = null;
            config = null;
        }
    }
}
