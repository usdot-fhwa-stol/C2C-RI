/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2006 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon.ant;

import net.sf.jameleon.ExecuteTestCase;
import net.sf.jameleon.util.Configurator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Parameter;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ExecuteTestCaseTask extends Task {

    protected final LinkedList fileSets = new LinkedList();
    protected static final String LB = new String("\n##############################################################\n");
    protected static final String DASH = new String("\n-------------------------------------------------------------\n");
    protected static final String US = new String("\n_______________________________________________________________\n");
    protected boolean debug = false;
    protected boolean throwExceptionOnFailure = true;
    protected boolean printTestSuiteSummary = true;
    protected long waitTimeBetweenScripts = 0;
    protected final LinkedList contextVariables = new LinkedList();
    protected final LinkedList environmentSettings = new LinkedList();

    /**
     * Jameleon's implementation of Task.execute().
     *
     * @exception BuildException  If anything goes wrong.
     */
    public final void execute() throws BuildException {
        try{
        Configurator.clearInstance();
        validateOptions();
        ExecuteTestCase exec = new ExecuteTestCase(debug);
        exec.setWaitTimeBetweenScripts(waitTimeBetweenScripts);
        setEnvironmentVariables(environmentSettings);
        exec.registerEventListeners(false);
        setParametersInContext(exec.getContextVars());
        for ( int i = 0; i < fileSets.size(); i++ ) {
            FileSet fs = ( FileSet ) fileSets.get( i );
            DirectoryScanner ds = fs.getDirectoryScanner( getProject() );
            String[] files = ds.getIncludedFiles();
            for (int j = 0; j < files.length; j++) {
//                delay();
                exec.addFile(new File(ds.getBasedir().getPath()+File.separator+files[j]));
            }
        }
        //This needs to be at each script because each script clears it's instance of 
        //jameleon.conf
        setEnvironmentVariables(environmentSettings);
        String errMsg = exec.executeFiles();
        exec.deregisterEventListeners();
        if (printTestSuiteSummary) {
            ExecuteTestCase.closeAllLogs();
        }


        if ( errMsg.length() > 0 ) {
            if (throwExceptionOnFailure) {
                throw new BuildException(LB+"The following test cases failed:"+errMsg+
                                        "\n\n"+"See 'TestResults.xml' and 'TestResults.html' for more details on the failure(s)", getLocation());
            }else{
                System.err.println(LB+"The following test cases failed:"+errMsg+
                                        "\n\n"+"See 'TestResults.xml' and 'TestResults.html' for more details on the failure(s)");
            }
        }}catch(RuntimeException re){
            re.printStackTrace();
            throw re;
        }
    }

    public void setPrintTestSuiteSummary(boolean printTestSuiteSummary){
        this.printTestSuiteSummary = printTestSuiteSummary;
    }

    public void setThrowExceptionOnFailure(boolean throwExceptionOnFailure){
        this.throwExceptionOnFailure = throwExceptionOnFailure;
    }

    public void setDebug(boolean debug){
        this.debug = debug;
    }

    public void setWaitTimeBetweenScripts(long waitTimeBetweenScripts){
        this.waitTimeBetweenScripts = waitTimeBetweenScripts;
    }

    /**
     * Adds a variable to be set in the context
     *
     * @param parameter a name/value parameter.
     */
    public void addVariable(Parameter parameter){
        contextVariables.add(parameter);
    }

    /**
     * Adds a setting to jameleon.conf
     *
     * @param parameter a name/value parameter.
     */
    public void addConfig(Parameter parameter){
        environmentSettings.add(parameter);
    }

    /**
     * Ant's &lt;fileset&gt; definition. To define the files to parse.
     *
     * @param set  a fileset to add
     */
    public void addFileset( FileSet set ) {
        fileSets.add( set );
    }

    /**
     * Validate required options are set.
     *
     * @exception BuildException  if a fileset isn't set.
     */
    protected void validateOptions() throws BuildException {          
        if ( fileSets.size() == 0 ) {
            throw new BuildException( "At least one fileset must be specified", getLocation() );
        }
    }

    protected void setParametersInContext(Map contextVars){
        Parameter param = null;
        for (Iterator it = contextVariables.iterator(); it.hasNext(); ) {
            param = (Parameter)it.next();
            contextVars.put(param.getName(), param.getValue());
        }
    }

    protected void setEnvironmentVariables(List environmentSettings){
        Configurator config = Configurator.getInstance();
        Parameter param = null;
        for (Iterator it = environmentSettings.iterator(); it.hasNext(); ) {
            param = (Parameter) it.next();
            config.setValue(param.getName(), param.getValue());
        }
    }

}

