/*
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

package net.sf.jameleon.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import net.sf.jameleon.XMLable;
import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.JameleonUtility;
import net.sf.jameleon.util.XMLHelper;

/**
 * This class represents a test case. This is currently used only for test case
 * documentation generation
 * A TestCase consists of:
 * <ul>
 *  <li>name - The name of the test case</li> 
 *  <li>summary - A brief description of the test case</li>
 *  <li>author - The person that wrote the test case</li>
 *  <li>funcationPointTested - The functional point being tested</li>
 *  <li>bug - The bug this test case was written for.</li>
 *  <li>sessions - List of Sessions that then contain a list of FunctionalPoints</li>
 *  <li>testLevels - List of test levels that caterogize the test case</li>
 * </ul>
 */
public class TestCase implements XMLable {
    private static final long serialVersionUID = 1L;
    /**
     * The name of the test case
     */
    protected String name;
    /**
     * A brief description of the test case
     */
    protected String summary;
    /**
     * The person that wrote the test case
     */
    protected String author;
    /**
     * The application being tested
     */
    protected String application;
    /**
     * The bug related to this test case
     */
    protected Set bugs;
    /**
     * The functional point tested by the test case
     */
    protected String functionalPointTested;
    /**
     * The level(s) of the test case - SMOKE, FUNCTIONAL, REGRESSION, ACCEPTANCE, to list a few
     */
    protected List testLevels;
    /**
     * The test case file
     */
    protected String file;
    /**
     * The encoding to use in the toXML() method
     */
    protected String encoding;
    /**
     * The unique id of the test case.
     */
    protected String testCaseId;
    /**
     * The organization the test case is set to execute against.
     */
    protected String organization;
    /**
     * The link to the requirement being tested
     */
    protected String testCaseRequirement;
    /**
     * The test evironment this test case is being test against
     */
    protected String testEnvironment;
    /**
     * A list of sessions in this test case
     */
    protected List sessions;

    /**
     * Default constructor only used to initialize variables
     */
    public TestCase() {
        sessions = new LinkedList();
        testLevels = new LinkedList();
        bugs = new HashSet();
    }

    public void addBug(String bug){
        bugs.add(bug);
    }

    public void addSession(Session s){
        sessions.add(s);
    }

    public void addTestLevel(String testLevel){
    	if (testLevels != null && !testLevels.contains(testLevel)){
    		testLevels.add(testLevel);
    	}
    }

    public String getApplication(){
        return application;
    }

    public String getAuthor(){
        return author;
    }

    public Set getBugs(){
        return bugs;
    }

    /**
     * @return the encoding style to use in the toXML() method
     */
    public String getEncoding(){
        return encoding;
    }

    public String getFile(){
        return file;
    }

    public String getScriptContents(){
    	StringBuffer contents = new StringBuffer();
    	if (file != null){
	        File f = new File(file);
	        if (f.exists() && f.isFile()) {
	            try (BufferedReader reader = new BufferedReader(new FileReader(f)))
				{
	                String line = null;
	                while((line = reader.readLine()) != null){
	                    contents.append(line+"\n");
	                }
	            }catch(Exception e){
	                e.printStackTrace();
	            }
	        }
    	}
        return JameleonUtility.decodeTextToXML(contents.toString());
    }

    public String getFunctionalPointTested(){
        return functionalPointTested;
    }

    public String getName(){
        return name;
    }

    public String getOrganization(){
        return organization;
    }

    public List getSessions(){
        return sessions;
    }

    public String getSummary(){
        return summary;
    }

    public String getTestCaseId(){
        return testCaseId;
    }

    public String getTestCaseRequirement(){
        return testCaseRequirement;
    }

    public String getTestEnvironment(){
        return testEnvironment;
    }

    public List getTestLevels(){
        return testLevels;
    }

    /**
     * Populates this test case object from a test script script
     */
    public void readFromScript(String script){
        Configurator config = Configurator.getInstance();
        organization = config.getValue("organization");
        testEnvironment = config.getValue("testEnvironment");
        try{
            URL scriptUrl = new URL(script);
            XMLHelper xmlHelper = new XMLHelper(scriptUrl);
            author = xmlHelper.getValueFromXPath("//jm:test-case-author");
            application = xmlHelper.getValueFromXPath("//jm:application-tested");
            functionalPointTested = xmlHelper.getValueFromXPath("//jm:functional-point-tested");
            String tmpName = xmlHelper.getValueFromXPath("/jm:testcase/@name");
            if (tmpName != null) {
                name = tmpName;
            }else{
                name = JameleonUtility.getFileNameFromPath(scriptUrl.getFile());
            }
            summary = xmlHelper.getValueFromXPath("//jm:test-case-summary");
            testCaseId = xmlHelper.getValueFromXPath("//jm:test-case-id");
            testCaseRequirement = xmlHelper.getValueFromXPath("//jm:test-case-requirement");
            file = scriptUrl.getFile();
            bugs = new HashSet(xmlHelper.getValuesFromXPath("//jm:test-case-bug"));
            testLevels = xmlHelper.getValuesFromXPath("//jm:test-case-level");
            List fps = xmlHelper.getValuesFromXPath("//@functionId");
            Session s = new Session();
            for (Iterator it = fps.iterator(); it.hasNext(); ) {
                FunctionalPoint fp = new FunctionalPoint();
                fp.setFunctionId((String)it.next());
                s.addFunctionalPoint(fp);
            }
            addSession(s);
        }catch(IOException ioe){
            throw new JameleonScriptException(ioe.getMessage(), ioe);
        }
    }

    public void setApplication(String application){
        this.application = application;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    /**
     * Sets the encoding to use in the toXML() results
     * @param encoding - the encoding to use in the toXML() results
     */
    public void setEncoding(String encoding){
        this.encoding = encoding;
    }

    public void setFile(String file){
        this.file = file;
    }

    public void setFunctionalPointTested(String functionalPointTested){
        this.functionalPointTested = functionalPointTested;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setOrganization(String organization){
        this.organization = organization;
    }
    public void setSummary(String summary){
        this.summary = summary;
    }

    public void setTestCaseId(String testCaseId){
        this.testCaseId = testCaseId;
    }

    public void setTestCaseRequirement(String testCaseRequirement){
        this.testCaseRequirement = testCaseRequirement;
    }

    public void setTestEnvironment(String testEnvironment){
        this.testEnvironment = testEnvironment;
    }

    public void setTestLevels(LinkedList testLevels){
        this.testLevels = testLevels;
    }

    public String toXML(){
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"");
        sb.append(encoding).append("\"?>\n");
        sb.append("<test-case>\n");
        sb.append("\t<test-case-name>").append(name).append("</test-case-name>\n");
        if (summary != null && summary.length() > 0) {
            sb.append("\t<test-case-summary>").append(summary).append("</test-case-summary>\n");
        }
        if (application != null && application.length() > 0) {
            sb.append("\t<application-tested>").append(application).append("</application-tested>\n");
        }
        if (testEnvironment != null && testEnvironment.length() > 0) {
            sb.append("\t<test-environment>").append(testEnvironment).append("</test-environment>\n");
        }
        if (author != null && author.length() > 0) {
            sb.append("\t<test-case-author>").append(author).append("</test-case-author>\n");
        }
        if (functionalPointTested != null && functionalPointTested.length() > 0) {
            sb.append("\t<functional-point-tested>").append(functionalPointTested).append("</functional-point-tested>\n");
        }
        if (testCaseId != null && testCaseId.length() > 0) {
            sb.append("\t<test-case-id>").append(testCaseId).append("</test-case-id>\n");
        }
        if (testCaseRequirement != null && testCaseRequirement.length() > 0) {
            sb.append("\t<test-case-requirement>").append(testCaseRequirement).append("</test-case-requirement>\n");
        }
        if (testLevels != null && testLevels.size() > 0) {
            Iterator it = testLevels.iterator();
            while (it.hasNext()) {
                sb.append("\t<test-case-level>").append((String)it.next()).append("</test-case-level>\n");
            }
        }
        if (bugs != null && bugs.size() > 0) {
            Iterator it = bugs.iterator();
            while (it.hasNext()) {
                sb.append("\t<test-case-bug>").append((String)it.next()).append("</test-case-bug>\n");
            }
        }
        if (file != null && file.length() > 0) {
            sb.append("\t<test-case-file>").append(file).append("</test-case-file>\n");
        }
        Iterator it = sessions.iterator();
        while (it.hasNext()) {
            sb.append(((XMLable)it.next()).toXML());
        }
        sb.append("</test-case>\n");
        return sb.toString();
    }

}