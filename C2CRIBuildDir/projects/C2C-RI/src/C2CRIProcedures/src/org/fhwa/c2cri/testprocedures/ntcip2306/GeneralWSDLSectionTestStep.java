/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures.ntcip2306;

import org.fhwa.c2cri.testprocedures.*;
import java.util.ArrayList;

/**
 *
 * @author TransCore ITS
 */
public class GeneralWSDLSectionTestStep extends TestProcedureStep {

    private NTCIP2306Specification relatedSpecification;

    public GeneralWSDLSectionTestStep(NTCIP2306Specification theSpecification) {
        this.relatedSpecification = theSpecification;
        this.setDescription(theSpecification.getTestSteps());
        this.setResults("PASS/FAIL"+ " ("+theSpecification.getRqmtID()+")");
    }

    public ArrayList<TestProcedureStep> getStepsforDatabase() {
        ArrayList<TestProcedureStep> theList = new ArrayList<TestProcedureStep>();
        String result;
        result = relatedSpecification.getTestSteps();

        this.setDescription(result);
        theList.add(this);
        return theList;
    }

    public String getScriptContent() {
        String result = "";
        if (relatedSpecification.getScriptText() != null) {
            result = relatedSpecification.getScriptText().replace("<Step>", "Step " + this.getId());
            result = result.replace("<ReqId>", relatedSpecification.getRqmtID());
            result = result.replace("<LF>", "\n");
            result = result.replace("<RequirementDescription>", relatedSpecification.getRequirementDescription().replace("<", "").replace(">", ""));
            result = result.replace("<Text>", relatedSpecification.getTestSteps().replace("<", "").replace(">", ""));
        }
        return result;
    }
}
