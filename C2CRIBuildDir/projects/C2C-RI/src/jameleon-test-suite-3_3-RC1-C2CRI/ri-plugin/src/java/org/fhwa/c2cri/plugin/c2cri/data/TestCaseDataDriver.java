
package org.fhwa.c2cri.plugin.c2cri.data;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.sf.jameleon.data.DataDriver;
import org.fhwa.c2cri.testmodel.testcasedata.TestCaseDataSource;

/**
 * Manages test case data files as an input source to a Test Case.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/4/2012
 */
public class TestCaseDataDriver implements DataDriver {

    /** The data source. */
    protected TestCaseDataSource dataSource;
    
    /** The base tc file. */
    protected URL baseTCFile;
    
    /** The user tc file. */
    protected File userTCFile;
    
    /** The more rows available. */
    protected boolean moreRowsAvailable = false;
    
    /** The current row. */
    protected Integer currentRow = 0;
    
    /** The context map. */
    protected Map contextMap;


     /**
      * Instantiates a new test case data driver.
      * 
      * Pre-Conditions: N/A
      * Post-Conditions: N/A
      */
     public TestCaseDataDriver() {
        contextMap = new HashMap();
    }
   
   /**
    * Default constructor.
    *
    * @param baseTCFile the base tc file
    * @param contextVariables the context variables
    */
    public TestCaseDataDriver(URL baseTCFile, Map contextVariables) {
        contextMap = contextVariables;
        this.baseTCFile = baseTCFile;
    }

    /**
     * Default constructor. After calling this constructor,
     * setFile() will need to be set.
     *
     * @param baseTCFile the base tc file
     * @param contextVariables the context variables
     * @param overrideFile the override file
     */
    public TestCaseDataDriver(URL baseTCFile, Map contextVariables, File overrideFile) {
        contextMap = contextVariables;
        this.baseTCFile = baseTCFile;
        this.userTCFile = overrideFile;
    }

    /**
     * Gets the user provided properties file used as a datasource.
     * @return the user provided properties file used as a datasource.
     */
    public File getFile() {
        return userTCFile;
    }

    /**
     * Sets the user provided properties file used as a datasource.
     * @param overrideFile - the user provided properties file used as a datasource.
     */
    public void setFile(File overrideFile) {
        this.userTCFile = overrideFile;
    }

    /**
     * Gets the base tc file.
     *
     * @return the base tc file
     */
    public URL getBaseTCFile() {
        return baseTCFile;
    }

    /**
     * Sets the base tc file.
     *
     * @param baseTCFile the new base tc file
     */
    public void setBaseTCFile(URL baseTCFile) {
        this.baseTCFile = baseTCFile;
    }

    /**
     * Gets the context map.
     *
     * @return the context map
     */
    public Map getContextMap() {
        return contextMap;
    }

    /**
     * Sets the context map.
     *
     * @param contextMap the new context map
     */
    public void setContextMap(Map contextMap) {
        this.contextMap = contextMap;
    }



    /**
     * Opens the handle to the data source.
     *
     * @throws IOException when the data source can not be found.
     */
    public void open() throws IOException {
        if ((userTCFile != null)&& userTCFile.exists()){
           dataSource = new TestCaseDataSource(baseTCFile, userTCFile);            
        } else {
           dataSource = new TestCaseDataSource(baseTCFile);                        
        }
        moreRowsAvailable = true;
    }

    /**
     * Closes the handle to the data source.
     */
    public void close() {
		// original implementation was empty
    }

    /**
     * Gets the next row from the data source.
     *
     * @return a key-value HashMap representing the data row or null if
     * no data row is available
     */
    public Map getNextRow() {
        Map vars = null;

        if ((dataSource.getIterationCount() > 0)&&(currentRow<dataSource.getIterationCount())) {

            vars = dataSource.getIteration(currentRow);

            // Check for Context Variable Parameter
            Iterator varIterator = vars.keySet().iterator();
            while (varIterator.hasNext()){
                try{
                    String thisVar = (String)varIterator.next();
                    try{
                        String thisValue = (String) vars.get(thisVar);
                        if (thisValue.contains("#CONTEXTVARIABLE#")){
                            String contextVariable = thisValue.replace("#CONTEXTVARIABLE#", "");
                            if (contextMap.containsKey(contextVariable)){
                                vars.put(thisVar, contextMap.get(contextVariable));
                            }
                        }
                    } catch (Exception ex){

                    }
                } catch (Exception ex){
                }
            }
            currentRow++;
        }

        if (currentRow == dataSource.getIterationCount())moreRowsAvailable = false;
        return vars;
    }

    /**
     * Tells whether the data source has another row.
     *
     * @return true if the data source still has more rows
     */
    public boolean hasMoreRows() {
        return moreRowsAvailable;
    }
}
