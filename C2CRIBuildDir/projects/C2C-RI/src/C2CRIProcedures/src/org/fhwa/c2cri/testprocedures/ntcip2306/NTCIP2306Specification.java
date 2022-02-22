/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testprocedures.ntcip2306;

/**
 *
 * @author TransCore ITS
 */
public class NTCIP2306Specification {

private String rqmtID;
private String requirementDescription;
private String section;
private String pRLTraceID;
private String profile;
private String mandatory;
private String primaryTarget;
private String verificationTime;
private String verificationApproach;
private String preconditionAssertion;
private String testSteps;
private String subRequirments;
private String scriptText;
private String xPathText;
private String xslTarget;
private String xslPredicate;


    public void WSDLStepDescription(){
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public String getpRLTraceID() {
        return pRLTraceID;
    }

    public void setpRLTraceID(String pRLTraceID) {
        this.pRLTraceID = pRLTraceID;
    }

    public String getPreconditionAssertion() {
        return preconditionAssertion;
    }

    public void setPreconditionAssertion(String preconditionAssertion) {
        this.preconditionAssertion = preconditionAssertion;
    }

    public String getPrimaryTarget() {
        return primaryTarget;
    }

    public void setPrimaryTarget(String primaryTarget) {
        this.primaryTarget = primaryTarget;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getRequirementDescription() {
        return requirementDescription;
    }

    public void setRequirementDescription(String requirementDescription) {
        this.requirementDescription = requirementDescription;
    }

    public String getRqmtID() {
        return rqmtID;
    }

    public void setRqmtID(String rqmtID) {
        this.rqmtID = rqmtID;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSubRequirments() {
        return subRequirments;
    }

    public void setSubRequirments(String subRequirments) {
        this.subRequirments = subRequirments;
    }

    public String getTestSteps() {
        return testSteps;
    }

    public void setTestSteps(String testSteps) {
        this.testSteps = testSteps;
    }

    public String getVerificationApproach() {
        return verificationApproach;
    }

    public void setVerificationApproach(String verificationApproach) {
        this.verificationApproach = verificationApproach;
    }

    public String getVerificationTime() {
        return verificationTime;
    }

    public void setVerificationTime(String verificationTime) {
        this.verificationTime = verificationTime;
    }

    public String getScriptText() {
        return scriptText;
    }

    public void setScriptText(String scriptText) {
        this.scriptText = scriptText;
    }

    public String getxPathText() {
        return xPathText;
    }

    public void setxPathText(String xPathText) {
        this.xPathText = xPathText;
    }

    public String getXslPredicate() {
        return xslPredicate;
    }

    public void setXslPredicate(String xslPredicate) {
        this.xslPredicate = xslPredicate;
    }

    public String getXslTarget() {
        return xslTarget;
    }

    public void setXslTarget(String xslTarget) {
        this.xslTarget = xslTarget;
    }



}
