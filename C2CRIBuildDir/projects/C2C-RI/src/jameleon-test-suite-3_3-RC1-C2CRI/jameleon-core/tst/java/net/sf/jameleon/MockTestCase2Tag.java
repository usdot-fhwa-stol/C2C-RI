package net.sf.jameleon;

import net.sf.jameleon.result.JameleonTestResult;

/**
 * @jameleon.function name="mock-test-case-2"
 */
public class MockTestCase2Tag extends TestCaseTag {

	protected int invokeChildrenRowNum = -1;
	protected JameleonTestResult mockTagDDResult;
	
	public void invokeChildren(int rowNum, JameleonTestResult result){
		invokeChildrenRowNum = rowNum;
		mockTagDDResult = result;
	}
	
}