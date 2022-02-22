/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.plugin.c2cri.tags;

import java.util.Iterator;
import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.result.JameleonTestResult;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;

/**
 * Extends the jameleon TestCase Tag for RI usage.
 * Every test case script must have at least one testcase tag containing all other Jameleon tags.
 * <p>
 * Some of this tags attribute may affect every nested Jameleon tag.
 * Many of the attributes in this tag can be set globally via a jameleon.conf file.
 * </p>
 * <p>
 * The order of setting variables in the context follows:
 * <ol>
 * <li> Load the CSV file variables and put them in the context.</li>
 * <li> Load the $testEnvironment-Applications.properties and then Applications.properties and
 *      only set the variables that aren't set in the previous files.  In other words, if there
 *      are variables that are going to be the same ( like the page title ) across multiple test
 *      cases, then first variable set wins.</li>
 * <li> Execute the function tag and set the attributes in the context. If you want key/values in
 *      the CSV and properties files to override the script attribute, then the function point
 *      author uses the setDefaultVariableValue() method in the set method for that attribute.</li>
 * <li> If the function point is using a map-variable, then override all settings to set the
 *      variable to the mapFrom variable name.</li>
 * </ol>
 * </p>
 * @author TransCore ITS, LLC
 * Last Updated:  10/3/2012
 * @jameleon.function name="subscript"
 * @jameleon.function name="sub-script"
 * @jameleon.function name="testprocedure"
 * @jameleon.function name="test-procedure"
 */
public class RISubscriptTag extends TestCaseTag{
    /**
     * This method executes the tags inside the test-case tag. If <code>useCSV=true</code>, then the data for
     * the functional points will be grabbed from a CSV file. Otherwise the attributes are all expected to be set
     * the contained functional points. If the functional points' attributes are set and useCSV is set to true,
     * then the data in the CSV file will override the data set in the function points' attributes.
     */
    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException{
        xmlOut = out;
        init();
        setUp();
        eventHandler.beginTestCase(this);
        //Call this after beginTestCase so that the timestamp directory can be set up
        setStateStoreOptions();
        try{
            testForUnsupportedAttributesCaught();
            broker.transferAttributes(context);
            broker.validate(context);
        }catch(JameleonScriptException jse){
            results.setError(jse);
            log.info(results);

            tearDown();
            throw jse;
        }
        try{
            if (executeTestCase) {
                if (useCSV) {
                    spanCSV();
                }else{
                    executeNoCSV();
                }
                if (maxExecutionTime > 0 && maxExecutionTime < results.getExecutionTime()) {
                    String msg = "The maximum execution time <"+maxExecutionTime+"> was exceeded <"+results.getExecutionTime()+">!";
                    results.setError(new JameleonScriptException(msg, this));
                }
                if ( failOnCSVFileNotFound || (!failOnCSVFileNotFound && !failedOnDataDriver) ) {
                    if (results.failed()) {
                        JellyTagException jte = null;
                        if (results.getError() != null) {
                            jte = createExceptionFromResult(results);
                        }else{
                            StringBuffer sb = new StringBuffer();
                            Iterator it = results.getFailedResults().iterator();
                            JameleonTestResult result;
                            while (it.hasNext()) {
                                result = (JameleonTestResult)it.next();
                                sb.append(result.getTag().getDefaultTagName()).append(":");
                                sb.append(result.getErrorMsg());
                                if (jte == null) {
                                    jte = createExceptionFromResult(result);
                                }
                            }
                        }
                        if (jte != null) {
                            if (results.getError() != null) {
                                results.setError(jte);
                            }
                            throw jte;
                        }
                    }
                }else{
                    setGenTestCaseDocs(false);
                }
            }
        }finally{
            eventHandler.endTestCase(this);
            if ( failOnCSVFileNotFound || (!failOnCSVFileNotFound && !failedOnDataDriver) ) {
                log.info(results);
            }
            tearDown();
        }
    }

}
