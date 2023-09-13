/*
Jameleon - An automation testing tool..
Copyright (C) 2003-2007 Christian W. Hargraves (engrean@hotmail.com)

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
package net.sf.jameleon.function;

import net.sf.jameleon.ExecuteTestCase;
import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.event.FunctionEventHandler;
import net.sf.jameleon.event.TestCaseEvent;
import net.sf.jameleon.event.TestCaseEventHandler;
import net.sf.jameleon.event.TestCaseListener;
import net.sf.jameleon.reporting.ResultsReporter;
import net.sf.jameleon.reporting.TestCaseCounter;
import net.sf.jameleon.result.*;
import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.JameleonUtility;
import net.sf.jameleon.util.StateStorer;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.varia.DenyAllFilter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sf.jameleon.exception.JameleonScriptException;

/**
 * Used to consilidate a set of test actions into a single logical test step.
 * @jameleon.function name="testStep"  
 */
public class TestStepTag extends FunctionTag {

    /**
     * The result expected from this test step.
     * @jameleon.attribute
     */
    protected String expectedResult;
    /**
     * The number associated with this test step.
     * @jameleon.attribute
     */
    protected String stepNumber;
    /**
     * The needs verified by the successful completion of this test step (separated by semi-colon).
     * @jameleon.attribute
     */
    protected String needsVerified;
    /**
     * The requirements verified by the successful completion of this test step (separated by semi-colon).
     * @jameleon.attribute
     */
    protected String requirementsVerified;
    /**
     * The requirements verified by the successful completion of this test step (separated by semi-colon).
     * @jameleon.attribute
     */
    protected String passfailResult = "True";
    private boolean skipped = false;

    public List<String> getNeedsVerified() {
        List<String> needsList = new ArrayList<String>();
        if (needsVerified != null) {
            String[] needsArray = needsVerified.split(";");
            for (int ii = 0; ii < needsArray.length; ii++) {
                needsList.add(needsArray[ii]);
            }
        }
        return needsList;
    }

    public List<String> getRequirementsVerified() {
        List<String> requirementsList = new ArrayList<String>();
        if (requirementsVerified != null) {
            String[] requirementsArray = requirementsVerified.split(";");
            for (int ii = 0; ii < requirementsArray.length; ii++) {
                requirementsList.add(requirementsArray[ii]);
            }
        }
        return requirementsList;
    }

    public boolean isPassFailResult() {
        if (passfailResult.equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSkipped() {
        return skipped;
    }

    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

    @Override
    public void setup() {
        super.setup();
        testStep = true;
    }

    public void testBlock(XMLOutput out) throws MissingAttributeException {
        //Do nothing since this functional point is used for documentation only.
    }

    /**
     * An implementation of the <code>doTag</code> method provided by the <code>TagSupport</code> class.
     */
    public void doTag(XMLOutput out) throws MissingAttributeException {
        setupEnvironment();
        FunctionEventHandler eventHandler = FunctionEventHandler.getInstance();
        if (!addt.getFailedOnCurrentRow() || postcondition) {

            traceMsg("BEGIN: " + functionId);
            long startTime = System.currentTimeMillis();
            try {
                this.setUpFunctionResults();
                fResults.setTestStep(true);
                eventHandler.beginFunction(this, 1);
//            init();

                invokeBody(out);
//            System.out.println(" Before Reset - This is a testStep Tag? " + fResults.isTestStep() + " and has children? " + fResults.hasChildren());
                //           fResults.setTestStep(true);
                System.out.println(" This is a testStep Tag? " + fResults.isTestStep() + " Error in Child =" + addt.getFailedOnCurrentRow() + " and has children? " + fResults.hasChildren());

				recordFunctionResult(startTime);
				eventHandler.endFunction(this, 1);
				traceMsg("END: " + functionId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is called before anything else is called.
     */
    protected void setupEnvironment() {
        getParentTags();
        params = new ArrayList();
        state = StateStorer.getInstance();
        state.addStorable(this);
        fp.setFunctionId(functionId);
        fResults.setPrecondition(precondition);
        fResults.setPostcondition(postcondition);
        fResults.setTestStep(true);

        Configurator config = Configurator.getInstance();
        String fDelay = config.getValue("functionDelay");
        if (functionDelay == NO_DELAY
                && fDelay != null
                && fDelay.trim().length() > 0) {
            try {
                setAttribute("functionDelay", fDelay);
            } catch (NumberFormatException nfe) {
                traceMsg("functionDelay variable in jameleon.conf is not a valid number");
            }

        }
    }

    public void store(File f, int event) {
        //Nothing to do here
    }
}
