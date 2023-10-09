/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testmodel;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * The Class TestCase represents the definition of a single test case, its test procedure script and its associated data source location.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestCase implements Serializable{
    
    /** The name. */
    @XmlElement
    private String name;
    
    /** The description. */
    private String description;
    
    /** The script. */
    @XmlAttribute
    private String script;
    
    /** The data. */
    @XmlAttribute
    private String data;
    
    /** The custom data location. */
    private String customDataLocation;
    
    /** The type. */
    @XmlAttribute
    private String type;
    
    /** The test suite. */
    @XmlAttribute
    private String testSuite;
    
    /** The user overide. */
    @XmlAttribute
    private boolean userOveride;
    
    /** The properties. */
    private transient Map properties;

    //Create private empty constructor to prevent creation without providing parameters.
    /**
     * Instantiates a new test case.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private TestCase(){
    }

    /**
     * Instantiates a new test case.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param name the name
     * @param script the script
     * @param data the data
     * @param description the description
     * @param type the type
     * @param testSuite the test suite
     */
    public TestCase(String name, String script, String data, String description, String type, String testSuite){
        this.name = name;
        this.script = script;
        this.data = data;
        this.customDataLocation = null;
        this.type = type;
        this.userOveride = false;
        this.testSuite = testSuite;
        this.description = description;
        properties = new HashMap();
    }

    /**
     * Gets the test case properties.
     *
     * @return the test case properties
     */
    public Map getTestCaseProperties(){
        return properties;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return this.name;
    }

    /**
     * Gets the script url location.
     *
     * @return the script url location
     * @throws Exception the exception
     */
    public URL getScriptUrlLocation() throws Exception{
            String tempURLAsString = "";
            if (!this.testSuite.isEmpty()){
                tempURLAsString = TestSuites.getInstance().getTestSuiteTestScriptPathsString(this.testSuite);                
            }
            URL scriptURL = new URL(tempURLAsString + this.script);
            return  scriptURL;
    }

    /**
     * Gets the data url location.
     *
     * @return the data url location
     * @throws Exception the exception
     */
    public URL getDataUrlLocation() throws Exception{
            String tempURLAsString = "";
            String dataFile = "";
            if (!this.testSuite.isEmpty()){
                tempURLAsString = TestSuites.getInstance().getTestSuiteTestDataPathasString(this.testSuite);
                dataFile = this.data;
            } else {
                if (this.userOveride){
                    dataFile = this.customDataLocation;
                }
            }
            
            URL dataURL = new URL(tempURLAsString + dataFile);
            return  dataURL;
    }

    /**
     * Checks if is overriden.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is overriden
     */
    public boolean isOverriden(){
        return this.userOveride;
    }

    /**
     * Gets the custom data location.
     *
     * @return the custom data location
     */
    public String getCustomDataLocation(){
        return this.customDataLocation;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription(){
        return this.description;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName(){
        return this.name;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType(){
        return this.type;
    }

/**
 * Method used to designate a different location from the default test case path.
 *
 * @param location the new custom data location
 */
    public void setCustomDataLocation(String location){
        this.customDataLocation = location;
        this.userOveride = true;
    }
}
