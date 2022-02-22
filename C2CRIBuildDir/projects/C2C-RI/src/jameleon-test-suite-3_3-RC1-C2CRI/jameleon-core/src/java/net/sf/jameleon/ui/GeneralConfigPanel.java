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

public class GeneralConfigPanel extends AbstractConfigPanel{

    public void registerFields(){
        addFieldProperty(true, "Plug-ins:", "plugins", "", "A space-separated list of plug-ins to enable");
        addFieldProperty(true, "Bug Tracker URL:", "bugTrackerUrl", "", "The complete URL of the Bug Tracker to a bug minus the bug id");
        addFieldProperty(true, "CSV Directory:", "csvDir", "data", "The directory where CSV files are stored. Default is data");
        addFieldProperty(true, "Base Directory:", "baseDir", ".", "Tells Jameleon where the project being executed is located. This setting is used to calculate the CSV and Results directories");
        addFieldProperty(true, "Results Directory:", "resultsDir", "jameleon_test_results", "Tells Jameleon where to put the recorded states of the applications changes");
        addFieldProperty(true, "Test Case Encoding:", "genTestCaseDocsEncoding", "UTF-8", "The encoding to use when generating the test case documentation. Default is UTF-8");
        addFieldProperty(true, "CSV Character Set:", "csvCharset", "UTF-8", "The CSV character set that the CSV files are in. Default is UTF-8");
        addFieldProperty(true, "Assert Levels:", "assertLevels", "", "A comma separated list of assertLevels which will be executed in the functional points");
        addFieldProperty(true, "Assert Less Levels:", "assertLessThanLevel", "", "The level of asserts which must be less than OR equal to in order to be executed in the functional points");
        addFieldProperty(true, "Assert Greater Levels:", "assertGreaterThanLevel", "", "The level of asserts which must be greater than OR equal to in order to be executed in the functional points");
    }

}
