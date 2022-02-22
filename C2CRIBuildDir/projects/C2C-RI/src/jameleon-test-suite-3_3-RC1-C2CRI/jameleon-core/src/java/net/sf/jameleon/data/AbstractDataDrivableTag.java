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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon.data;

import net.sf.jameleon.JameleonTagSupport;
import net.sf.jameleon.SessionTag;
import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.event.BreakPoint;
import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.result.*;
import net.sf.jameleon.util.StateStorer;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.LocationAware;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * This is a basic implementation of DataDrivable. This is data source
 * independent as possible for now.
 */
public abstract class AbstractDataDrivableTag 
    extends 
        JameleonTagSupport 
    implements 
        DataDrivable, 
        BreakPoint,
        DataDrivableResultRecordable,
        FunctionResultRecordable,
        SessionResultRecordable
        {
    
    protected TestCaseTag tct;
    protected AbstractDataDrivableTag addt;
    protected DataExecuter executer = new DataExecuter(getDataDriver());
    protected XMLOutput xmlOut;
    protected Map rowData = new HashMap();
    protected Map vars;

    protected DataDrivableResultContainer resultContainer;
    protected DataDrivableRowResult dataDrivableRowResult;
    protected boolean countRow;
    protected StateStorer stateStorer = StateStorer.getInstance();
    protected File previousStateDir;
    protected int numOfRowFailures;
    protected boolean failedOnCurrentRow;
    protected boolean stopTestExecutionOnFailure = true;
    protected boolean parentFailed;
    protected boolean breakPoint;
    protected DataDrivableResultRecordable resultRecorder;
    
    protected final Logger log = getLogger();

    /**
     * Gets the logger used for this tag
     * @return the logger used for this tag.
     */
    protected abstract Logger getLogger();
    /**
     * Gets the DataDriver used for this tag.
     * @return the DataDriver used for this tag.
     */
    protected abstract DataDriver getDataDriver();
    /**
     * Sets up the DataDriver by calling any implementation-dependent
     * methods.
     */
    protected abstract void setupDataDriver();
    /**
     * Gets the trace message when the execution is beginning and ending.
     * The message displayed will already start with BEGIN: or END:
     * @return the trace message when the execution is just beginning and ending.
     */
    protected abstract String getTagTraceMsg();
    /**
     * Describe the tag when error messages occur.
     * The most appropriate message might be the tag name itself.
     * @return A brief description of the tag or the tag name itself.
     */
    public abstract String getTagDescription();
    /**
     * Gets an error message to be displayed when a error occurs due to the DataDriver.
     * @return an error message to be displayed when a error occurs due to the DataDriver.
     */
    protected abstract String getDataExceptionMessage();
    /**
     * Calculates the location of the state to be stored for any tags under this tag.
     * The result should be a relative path that simply has the row number in it along
     * with some unique indentifier for this tag like the handle name or something.
     * @return the location of the state to be stored for any tags under this tag minus the baseDir calculation stuff.
     */
    protected abstract String getNewStateStoreLocation(int rowNum);

    /**
     * Gets the TestCaseTag for this tag. The default implementation searches
     * for it as an ancestor and throws a ClassCastException if it can't find it.
     * @return the TestCaseTag this tag is nested in.
     * @throws ClassCastException if the parent TestCaseTag can not be found.
     */
    protected TestCaseTag getTestCaseTag(){
        Object obj =  findAncestorWithClass(TestCaseTag.class);
        if (obj instanceof TestCaseTag) {
            return (TestCaseTag)obj;
        }else{
            throw new ClassCastException("Can only execute the "+getTagDescription()+" inside a testcase tag! "+getClass().getName());
        }
    }

    /**
     * Gets whether each row in the data source should be considered a separate test case or not.
     * @return true if each row in the data source should be considered a separate test case.
     */
    public boolean isCountRow(){
        return countRow;
    }

    /**
     * Sets the DataDrivable tag to increment the number of times the test case was executed
     * for each row executed.
     * @param countRow - Set to true to increment the test case results for every row executed
     * @jameleon.attribute default="false"
     */
    public void setCountRow(boolean countRow){
        this.countRow = countRow;
    }

    public void setFailedOnCurrentRow(boolean failedOnCurrentRow){
        if (stopTestExecutionOnFailure) {
            this.failedOnCurrentRow = failedOnCurrentRow;
            if (addt != null && addt != this) {
                addt.setFailedOnCurrentRow(failedOnCurrentRow);
            }
        }
    }

    public boolean getFailedOnCurrentRow(){
        return parentFailed || failedOnCurrentRow ;
    }

    /**
     * Removes the keys from the context and the variable set in addVariablesToRowData()
     * @param keys - A Set of variable names to clean up.
     */
    public void destroyVariables(Set keys){
        if (tct != this && tct != null) {
            tct.destroyVariables(keys);
        }
        Iterator keysIt = keys.iterator();
        String key = null;
        while (keysIt.hasNext()) {
            key = (String)keysIt.next();
            context.removeVariable(key);
            rowData.remove(key);
        }
    }

    /**
     * Used to keep track of the variables and their original values.
     * This is mostly used for variable substitution.
     * @param rowData - A map of key-value pairs.
     */
    public void addVariablesToRowData(Map rowData){
        this.vars = rowData;
        mapKeys(vars);
        setVariablesInContext(vars);
        tct.addVariablesToRowData(vars);
        Object obj =  findAncestorWithClass(SessionTag.class);
        if (obj instanceof SessionTag) {
            tct.substituteKeyValues();
            SessionTag st = (SessionTag)obj;
            String app = st.getApplication();
            if (app != null && app.trim().length() > 0) {
                tct.getPropertiesForApplication(app);
            }
        }
    }

    /**
     * This method is used to set up anything the tag may need and gets
     * called after all set methods have been called.
     */
    public void setUpDataDrivable(){
        tct = getTestCaseTag();
        previousStateDir = stateStorer.getStoreDir();
        if (tct != this && stopTestExecutionOnFailure) {
            parentFailed = tct.getFailedOnCurrentRow();
        }
        setupDataDriver();
    }

    /**
     * Maps the keys to the keys returned from {@link #getKeyMapping()}
     * This method is called from executeDrivableRow(HashMap vars, int rowNum)
     * and is used to allow subclasses to map the variable names defined as
     * the keys in the vars Map to new keys which will then in turn be stored in the context.
     * @param vars - the original map read from the DataDriver
     */
    public void mapKeys(Map vars){
        Map keys = getKeyMapping();
        if (keys != null) {
            Iterator it = keys.keySet().iterator();
            String oldKey,newKey;
            while (it.hasNext()) {
                oldKey = (String) it.next();
                newKey = (String) keys.get(oldKey);
                vars.put(newKey, vars.get(oldKey));
                vars.remove(oldKey);
            }
        }
    }

    /**
     * Gets the new names of the desired context names. 
     * The map should contain a key-value pair where the key is
     * the original key and the value is the new key desired.
     * The default implementation returns null.
     * @return a key-value pair representing the new variable names.
     */
    protected Map getKeyMapping(){
        return null;
    }

    /**
     * A DataDrivable implementation method that gets called once for every row in the data source.
     */
    public void executeDrivableRow(int rowNum){
        traceMsg("BEGIN executing row number "+rowNum+" AbstractDataDrivableTag::executeDrivableRow");
        try{
            //The first result has already been added.
            if (rowNum > 1){
                dataDrivableRowResult = createNewResult();
                recordThisResult();
            }
            dataDrivableRowResult.setRowData(vars);
            dataDrivableRowResult.setRowNum(rowNum);
            stateStorer.setStoreDir(new File(stateStorer.getStoreDir(), getNewStateStoreLocation(rowNum)));
            try {
                invokeBody(xmlOut);
            } catch (ThreadDeath td){
                throw td;
            } catch (Throwable t) {
                LocationAware la = null;
                Throwable err = t;
                if (t.getCause() != null && t.getCause() instanceof LocationAware) {
                    err = t.getCause();
                    la = (LocationAware)err;
                }else if (t instanceof LocationAware) {
                    la = (LocationAware)t;
                }
                if (la != null) {
                    la.setColumnNumber(getColumnNumber());
                    la.setLineNumber(getLineNumber());
                    la.setFileName(getFileName());
                    JameleonScriptException jse = new JameleonScriptException(err.getMessage(), la);
                    dataDrivableRowResult.setError(jse);
                    throw jse;
                }else{
                    dataDrivableRowResult.setError(err);
                }
            } finally {
                stateStorer.setStoreDir(previousStateDir);
                if (getFailedOnCurrentRow() && (isCountRow() || numOfRowFailures < 1 )){
                    numOfRowFailures++;
                }
            }
        }finally{
            removeChildlessResult(dataDrivableRowResult);
            failedOnCurrentRow = false;
            traceMsg("END executing row number "+rowNum+" AbstractDataDrivableTag::executeDrivableRow");
        }
    }

    public DataDrivableResultContainer getResultContainer(){
        return resultContainer;
    }

    /**
     * To continue on a normal execution path even though an error occurs, set this to false
     * @jameleon.attribute
     */
    public void setStopTestExecutionOnFailure(boolean stopTestExecutionOnFailure){
        this.stopTestExecutionOnFailure = stopTestExecutionOnFailure;
    }

    /**
     * Places the given variables in the context
     */
    public void setVariablesInContext(Map vars){
        Iterator it = vars.keySet().iterator();
        String key;
        while (it.hasNext()) {
            key = (String) it.next();
            context.setVariable(key, vars.get(key));
        }
    }

    /**
     * Traces the key value pairs to screen for debugging purposes
     * This should get called after the key substition is done
     */
    public String getTraceKeyValuePairs(Set keys, Map rowData){
        Iterator it = keys.iterator();
        String key;
        String traceMsg = "key=>value: ";
        while (it.hasNext()) {
            key = (String) it.next();
            traceMsg += "'"+key+"'=>'"+rowData.get(key)+"' ";
        }
        return traceMsg;
    }

    /**
     * Used for the trace functionality. Only sends info to the log if trace is enabled.
     */
    protected void traceMsg(String msg){
        if (tct != null && tct.getTrace()) {
            System.out.println("\n"+msg+"\n");
        }else{
            log.debug(msg);        	
        }
    }

    public void init() throws MissingAttributeException{
        addt = (AbstractDataDrivableTag)findAncestorWithClass(AbstractDataDrivableTag.class);
        if (addt == null) {
            addt = this;
        }
        if (isCountRow()) {
            resultContainer = new CountableDataDrivableResultContainer(fp.cloneFP());
        }else{
            resultContainer = new DataDrivableResultContainer(fp.cloneFP());
        }
        dataDrivableRowResult = createNewResult();
        Object obj = findAncestorWithClass(DataDrivableResultRecordable.class);
        if (obj != null) {
            resultRecorder = (DataDrivableResultRecordable)obj;
            resultContainer.copyLocationAwareProperties(this);
            resultRecorder.recordDataDrivableResult(resultContainer);
        }
        recordThisResult();
    }

    protected void recordThisResult(){
        dataDrivableRowResult.copyLocationAwareProperties(this);
        if (resultRecorder != null) {
            resultContainer.addChildResult(dataDrivableRowResult);
            dataDrivableRowResult.setParentResults(resultContainer);
        }
    }

    /**
     * This method executes the tags inside the csv tag one time for every row in the CSV file used.
     */
    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException{
        xmlOut = out;
        setUpDataDrivable();
        init();
        testForUnsupportedAttributesCaught();
        broker.transferAttributes(context);
        broker.validate(context);
        try{
            traceMsg("BEGIN: "+getTagTraceMsg());
            executer.executeData(this, false);
            traceMsg("END: "+getTagTraceMsg());
        }catch(FileNotFoundException fnfe){
            tct.setFailedOnDataDriver(true);
            setResultError(fnfe);
        }catch(IOException ioe){
            setResultError(new JameleonScriptException(getDataExceptionMessage(), ioe));
        }catch(ThreadDeath td){
            throw td;
        }catch(Throwable t){
            setResultError(new JameleonScriptException(t));
        }finally{
            dataDrivableRowResult.setTag(fp.cloneFP());
            resetFunctionalPoint();
        }
    }

    /**
     * Removes the current rowResult from its parent if it has no children, meaning the tags weren't actually run
     * @param rowResult - The rowResult to remove
     */
    protected void removeChildlessResult(DataDrivableRowResult rowResult){
        if (rowResult.passed() &&
            ( rowResult.getChildrenResults() == null ||
              rowResult.getChildrenResults().size() == 0 ) )  {
            if (rowResult.getParentResults() != null) {
                List results = rowResult.getParentResults().getChildrenResults();
                if (results != null) {
                    int index = results.lastIndexOf(rowResult);
                    results.remove(index);
                }
            }
        }
    }

    protected void setResultError(Exception e){
    	dataDrivableRowResult.setError(e);
        resultContainer.setError(e);
    }
    
    protected DataDrivableRowResult createNewResult(){
    	DataDrivableRowResult ddr = null;
    	if (isCountRow()){
    		ddr = new CountableDataDrivableRowResult(fp.cloneFP());
    	}else{
    		ddr = new DataDrivableRowResult(fp.cloneFP());
    	}
    	return ddr;
    }


    public Map getRowData(){
        return rowData;
    }
    
    public DataDrivableRowResult getDataDrivableRowResult(){
    	return dataDrivableRowResult;
    }
    
    /////////////////////////////////////////////////////////
    //          ResultRecordable Methods       //
    /////////////////////////////////////////////////////////

    /**
     * Records a child result. Used as a helper method for the ResultRecordable
     * implementation methods.
     * @param result - the result to record
     */
    protected void recordResult(JameleonTestResult result){
        if (dataDrivableRowResult != null) {
            dataDrivableRowResult.addChildResult(result);
            result.setParentResults(dataDrivableRowResult);
        }
    }
    /**
     * Records a DataDrivableRowResult to the tag's results
     * @param result
     */
    public void recordDataDrivableResult(DataDrivableResultContainer result){
        recordResult(result);
    }

    /**
     * Records a FunctionResult to the tag's results and sets the FunctionResult's parent
     * result to itself
     * @param result
     */
    public void recordFunctionResult(FunctionResult result){
        recordResult(result);
    }

    /**
     * Removes a FunctionResult from the list of recorded results
     * @param result - the result to remove
     */
    public void recordSessionResult(SessionResult result){
        recordResult(result);
    }
/////////////////////////////////////////////////////////////
////                 BreakPoint Methods                //////
/////////////////////////////////////////////////////////////
    /**
     * Sets a pause point to this class.
     * @param breakPoint - Set to false to enable debug mode.
     * @jameleon.attribute
     */
    public void setBreakPoint(boolean breakPoint){
        this.breakPoint = breakPoint;
    }

    /**
     * Tells if this class is supposed to pause
     * @return true if class should pause and wait for user interaction.
     */
    public boolean isBreakPoint(){
        return breakPoint;
    }

}
