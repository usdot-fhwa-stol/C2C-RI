package net.sf.jameleon.util;

import java.io.File;

public final class JameleonDefaultValues{

    private JameleonDefaultValues(){}

    public static final String JAMELEON_VERSION_FILE = "jameleon-core-version.properties";
    public static final File BASE_DIR = new File(".");
    public static final File DATA_DIR = new File("data");
    public static final File SCRIPT_DIR = new File("scripts");
    public static final String SCRIPT_DIR_CONFIG_NAME = "scriptDir";

    public static final String RESULTS_DIR = "jameleon_test_results";
    public static final String FILE_CHARSET = "UTF-8";
    public static final String ENTRIES_CONFIG_NAME = "classpath.entry";    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////                 RESULTS TEMPLATES
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String TEST_RUN_SUMMARY_FILE_NAME = "TestRunsSummary.html";
    public static final String TEST_CASE_RESULT_MAIN_PAGE_TEMPLATE =  "templates/results/testcase/TestCaseResultMainPage.html";
    public static final String TEST_CASE_SUMMARY_TEMPLATE = "templates/results/testcase/TestCaseSummary.html";
    public static final String TEST_CASE_RESULT_SUMMARY_TEMPLATE = "templates/results/testcase/TestCaseResultSummary.html";
    public static final String TEST_CASE_RESULT_TEMPLATE = "templates/results/testcase/TestCaseResult.html";
    public static final String TEST_CASE_RESULT_DATA_ROW_TEMPLATE = "templates/results/testcase/TestCaseDataRowResult.html";
    public static final String TEST_CASE_RESULT_SESSION_TEMPLATE = "templates/results/testcase/TestCaseSessionResult.html";
    public static final String TEST_CASE_RESULT_FUNCTION_TEMPLATE = "templates/results/testcase/TestCaseFunctionResult.html";

}
