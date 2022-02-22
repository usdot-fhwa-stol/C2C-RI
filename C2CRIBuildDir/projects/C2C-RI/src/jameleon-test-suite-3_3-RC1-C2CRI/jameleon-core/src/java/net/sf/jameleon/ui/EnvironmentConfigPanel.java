/*
    Jameleon - An automation testing tool..
    Copyright (C) 2006 Christian W. Hargraves (engrean@hotmail.com)

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

public class EnvironmentConfigPanel extends AbstractConfigPanel{

    public void registerFields(){
        addFieldProperty(true, "Test Environment:", "testEnvironment", "", "The name of the environment the test case is to be executed against. Some examples of testEnvironment are: prod, beta, dev, and localhost.");
        addFieldProperty(true, "Organization:", "organization", "", "The name of the organization to run the test scripts against");
        addFieldProperty(true, "Function Delay:", "functionDelay", "", "This is a numerical setting in milliseconds. Whatever this is set to, each functional point will wait that period after execution.");
        addFieldProperty(false, "Trace:", "trace", "false", "Print out execution of most tags in a test case to std out. This is very helpful for debugging which functional point is getting executed and when");
        addFieldProperty(true, "Store State Event:", "storeStateEvent", "", "Sets the event which must occur in order for the application's state to be recorded");
        addFieldProperty(true, "Test Case Listeners:", "TestCaseListeners", "", "To hook into the Jameleon Test Case Event Model, define a space-separated list of classes that implement net.sf.jameleon.event.TestCaseListener");
        addFieldProperty(false, "Execute Test Case:", "executeTestCase", "true", "Tells Jameleon to execute the code in each tag or to do a dry run. The default is true");
        addFieldProperty(false, "Fail on no CSV File:", "failOnCSVFileNotFound", "true", "If set to false, and an error due to the CSV file not being found happens, then the test case will not get logged");
        addFieldProperty(false, "Generate Docs:", "genTestCaseDocs", "true", "Generates the test case documentation and creates links on the TestResults.html page for each test case name");
        addFieldProperty(false, "Enable SSL Cert Check:", "enableSslCertCheck", "true", "This is an old setting for the HttpUnit plug-in. We need to change this to be HttpUnit specific.");
    }


}
