/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003 Christian W. Hargraves (engrean@hotmail.com)
    
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

import java.util.LinkedList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.ExitException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteJava;
import org.apache.tools.ant.taskdefs.ExecuteWatchdog;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Environment;

import java.io.File;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Vector;


public class ExecuteTestCaseSeparateVMTask extends Task {

    private CommandlineJava cmdl = new CommandlineJava();
    private Environment env = new Environment();
    private boolean fork = true;
    private boolean newEnvironment = false;
    private File dir = null;
    private File out;
    private PrintStream outStream = null;
    private boolean failOnError = false;
    private boolean separateVmPerScript = false;
    private boolean append = false;
    private boolean printFooter = true;
    private Long timeout = null;
    
    protected final LinkedList fileSets = new LinkedList();
    protected static final String LB = new String("\n##############################################################\n");
    protected static final String DASH = new String("\n-------------------------------------------------------------\n");
    protected static final String US = new String("\n_______________________________________________________________\n");

    /**
     * Jameleon's implementation of Task.execute().
     *
     * @exception BuildException  If anything goes wrong.
     */
    public final void execute() throws BuildException {
        validateOptions();
        cmdl.setClassname("net.sf.jameleon.ExecuteTestCase");
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            dir = getProject().getBaseDir();
        }
        if (!printFooter) {
            cmdl.createArgument().setLine("false");
        }
        for ( int i = 0; i < fileSets.size(); i++ ) {
            FileSet fs = ( FileSet ) fileSets.get( i );

            DirectoryScanner ds = fs.getDirectoryScanner( getProject() );
            String[] files = ds.getIncludedFiles();
            for (int j = 0; j < files.length; j++) {
                cmdl.createArgument().setLine(ds.getBasedir().getPath()+File.separator+files[j]);
                if (separateVmPerScript) {
                    runScript();
                    if (!printFooter) {
                        cmdl.createArgument().setLine("false");
                    }
                }
            }
        }
        if (!separateVmPerScript) {
            runScript();
        }
    }

    private void runScript(){
        int err = -1;
        try {
            if ((err = executeJava()) != 0) { 
                if (failOnError) {
                    throw new BuildException("Java returned: " + err, getLocation());
                } else {
                    log("Java Result: " + err, Project.MSG_ERR);
                }
            }
        } finally {
            cmdl.clearJavaArgs();
        }
    }

    public void setPrintFooter(boolean printFooter){
        this.printFooter = printFooter;
    }

    public void setSeparateVmPerScript(boolean separateVmPerScript){
        this.separateVmPerScript = separateVmPerScript;
    }

    public void setDebug(boolean debug){
    }

    public void setBaseDir(File dir){
        this.dir = dir;
    }

    public void setFork(boolean fork){
        this.fork = fork;
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

    /**
     * Do the execution and return a return code.
     *
     * @return the return code from the execute java class if it was
     * executed in a separate VM (fork = "yes").
     */
    public int executeJava() throws BuildException {
        String classname = cmdl.getClassname();
        if (classname == null && cmdl.getJar() == null) {
            throw new BuildException("Classname must not be null.");
        }

        if (!fork && cmdl.getJar() != null){
            throw new BuildException("Cannot execute a jar in non-forked mode."
                                     + " Please set fork='true'. ");
        }

        if (fork) {
            log(cmdl.describeCommand(), Project.MSG_VERBOSE);
        } else {
            if (cmdl.getVmCommand().size() > 1) {
                log("JVM args ignored when same JVM is used.", 
                    Project.MSG_WARN);
            }
            if (dir != null) {
                log("Working directory ignored when same JVM is used.", 
                    Project.MSG_WARN);
            }

            if (newEnvironment || null != env.getVariables()) {
                log("Changes to environment variables are ignored when same "
                    + "JVM is used.", Project.MSG_WARN);
            }

            log("Running in same VM " + cmdl.describeJavaCommand(), 
                Project.MSG_VERBOSE);
        }
        
        try {
            if (fork) {
                return run(cmdl.getCommandline());
            } else {
                try {
                    run(cmdl);
                    return 0;
                } catch (ExitException ex) {
                    return ex.getStatus();
                }
            }
        } catch (BuildException e) {
            if (failOnError) {
                throw e;
            } else {
                log(e.getMessage(), Project.MSG_ERR);
                return 0;
            }
        } catch (Throwable t) {
            if (failOnError) {
                throw new BuildException(t);
            } else {
                log(t.getMessage(), Project.MSG_ERR);
                return 0;
            }
        }
    }

    /**
     * Set the classpath to be used when running the Java class
     * 
     * @param s an Ant Path object containing the classpath.
     */
    public void setClasspath(Path s) {
        createClasspath().append(s);
    }
    
    /**
     * Adds a path to the classpath.
     */
    public Path createClasspath() {
        return cmdl.createClasspath(getProject()).createPath();
    }

    /**
     * Classpath to use, by reference.
     */
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }

    /**
     * Sets the Java class to execute.
     */
    public void setClassname(String s) throws BuildException {
        if (cmdl.getJar() != null){
            throw new BuildException("Cannot use 'jar' and 'classname' "
                                     + "attributes in same command");
        }
        cmdl.setClassname(s);
    }

    /**
     * Adds a command-line argument.
     */
    public Commandline.Argument createArg() {
        return cmdl.createArgument();
    }

    /**
     * Set the command line arguments for the JVM.
     */
    public void setJvmargs(String s) {
        log("The jvmargs attribute is deprecated. " +
            "Please use nested jvmarg elements.",
            Project.MSG_WARN);
        cmdl.createVmArgument().setLine(s);
    }
        
    /**
     * Adds a JVM argument.
     */
    public Commandline.Argument createJvmarg() {
        return cmdl.createVmArgument();
    }

    /**
     * Set the command used to start the VM (only if not forking).
     */
    public void setJvm(String s) {
        cmdl.setVm(s);
    }
        
    /**
     * Adds a system property.
     */
    public void addSysproperty(Environment.Variable sysp) {
        cmdl.addSysproperty(sysp);
    }

    /**
     * If true, then fail if the command exits with a
     * returncode other than 0
     */
    public void setFailOnError(boolean fail) {
        failOnError = fail;
    }

    /**
     * File the output of the process is redirected to.
     */
    public void setOutput(File out) {
        this.out = out;
    }

    /**
     * Corresponds to -mx or -Xmx depending on VM version.
     */
    public void setMaxmemory(String max){
        cmdl.setMaxmemory(max);
    }

    /**
     * Adds an environment variable.
     *
     * <p>Will be ignored if we are not forking a new VM.
     *
     * @since Ant 1.5
     */
    public void addEnv(Environment.Variable var) {
        env.addVariable(var);
    }

    /**
     * If true, use a completely new environment.
     *
     * <p>Will be ignored if we are not forking a new VM.
     *
     * @since Ant 1.5
     */
    public void setNewenvironment(boolean newenv) {
        newEnvironment = newenv;
    }

    /**
     * If true, append output to existing file.
     *
     * @since Ant 1.5
     */
    public void setAppend(boolean append) {
        this.append = append;
    }

    /**
     * Timeout in milliseconds after which the process will be killed.
     *
     * @since Ant 1.5
     */
    public void setTimeout(Long value) {
        timeout = value;
    }

    /**
     * Pass output sent to System.out to specified output file.
     *
     * @since Ant 1.5
     */
    protected void handleOutput(String line) {
        if (outStream != null) {
            outStream.println(line);
        } else {
            super.handleOutput(line);
        }
    }
    
    /**
     * Pass output sent to System.out to specified output file.
     *
     * @since Ant 1.5.2
     */
    protected void handleFlush(String line) {
        if (outStream != null) {
            outStream.print(line);
        } else {
            super.handleFlush(line);
        }
    }
    
    /**
     * Pass output sent to System.err to specified output file.
     *
     * @since Ant 1.5
     */
    protected void handleErrorOutput(String line) {
        if (outStream != null) {
            outStream.println(line);
        } else {
            super.handleErrorOutput(line);
        }
    }
    
    /**
     * Pass output sent to System.err to specified output file.
     *
     * @since Ant 1.5.2
     */
    protected void handleErrorFlush(String line) {
        if (outStream != null) {
            outStream.println(line);
        } else {
            super.handleErrorOutput(line);
        }
    }
    
    /**
     * Executes the given classname with the given arguments as it
     * was a command line application.
     */
    private void run(CommandlineJava command) throws BuildException {
        ExecuteJava exe = new ExecuteJava();
        exe.setJavaCommand(command.getJavaCommand());
        exe.setClasspath(command.getClasspath());
        exe.setSystemProperties(command.getSystemProperties());
        exe.setTimeout(timeout);
        if (out != null) {
            try {
                outStream = 
                    new PrintStream(new FileOutputStream(out.getAbsolutePath(),
                                                         append));
                exe.execute(getProject());
                System.out.flush();
                System.err.flush();
            } catch (IOException io) {
                throw new BuildException(io, getLocation());
            } finally {
                if (outStream != null) {
                    outStream.close();
                }
            }
        } else {
            exe.execute(getProject());
        }
    }

    /**
     * Executes the given classname with the given arguments in a separate VM.
     */
    private int run(String[] command) throws BuildException {
        FileOutputStream fos = null;
        try {
            Execute exe = null;
            if (out == null) {
                exe = new Execute(new LogStreamHandler(this, Project.MSG_INFO,
                                                       Project.MSG_WARN), 
                                  createWatchdog());
            } else {
                fos = new FileOutputStream(out.getAbsolutePath(), append);
                exe = new Execute(new PumpStreamHandler(fos),
                                  createWatchdog());
            }
            
            exe.setAntRun(getProject());
            
            if (dir == null) {
                dir = getProject().getBaseDir();
            } else if (!dir.exists() || !dir.isDirectory()) {
                throw new BuildException(dir.getAbsolutePath()
                                         + " is not a valid directory",
                                         getLocation());
            }
            
            exe.setWorkingDirectory(dir);
            
            String[] environment = env.getVariables();
            if (environment != null) {
                for (int i = 0; i < environment.length; i++) {
                    log("Setting environment variable: " + environment[i],
                        Project.MSG_VERBOSE);
                }
            }
            exe.setNewenvironment(newEnvironment);
            exe.setEnvironment(environment);

            exe.setCommandline(command);
            try {
                int rc = exe.execute();
                if (exe.killedProcess()) {
                    log("Timeout: killed the sub-process", Project.MSG_WARN); 
                }
                return rc;
            } catch (IOException e) {
                throw new BuildException(e, getLocation());
            }
        } catch (IOException io) {
            throw new BuildException(io, getLocation());
        } finally {
            if (fos != null) {
                try {fos.close();} catch (IOException io) {}
            }
        }
    }

    /**
     * Executes the given classname with the given arguments as it
     * was a command line application.
     */
    protected void run(String classname, Vector args) throws BuildException {
        CommandlineJava cmdj = new CommandlineJava();
        cmdj.setClassname(classname);
        for (int i = 0; i < args.size(); i++) {
            cmdj.createArgument().setValue((String) args.elementAt(i));
        }
        run(cmdj);
    }

    /**
     * Clear out the arguments to this java task.
     */
    public void clearArgs() {
        cmdl.clearJavaArgs();
    }

    /**
     * Create the Watchdog to kill a runaway process.
     *
     * @since Ant 1.5
     */
    protected ExecuteWatchdog createWatchdog() throws BuildException {
        if (timeout == null) {
            return null;
        }
        return new ExecuteWatchdog(timeout.longValue());
    }

}

