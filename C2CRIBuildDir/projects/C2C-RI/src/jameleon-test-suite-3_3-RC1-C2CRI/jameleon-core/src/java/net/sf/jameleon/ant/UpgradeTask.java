/*
    Jameleon - An automation testing tool..
    Copyright (C) 2006 Christian W. Hargraves (engrean@hotmail.com)
    
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import net.sf.jameleon.util.Configurator;

/**
 * An Ant task that takes the registered FunctionalPoints and generates a syntax reference for each one.
 */
public class UpgradeTask extends Task{


    protected File environmentProperties = new File("res/Environment.properties");
    protected File jameleonGUIProperties = new File("lib/jameleon-gui.properties");

    public UpgradeTask(){
    }

    /**
     * The location of the Enivornment.properties
     * @param f The location of the Enivornment.properties
     */
    public void setEnvironmentProperties(File f){
        environmentProperties = f;
    }

    /**
     * The location of jameleon-gui.properties
     * @param f The location of jameleon-gui.properties
     */
    public void setJameleonGUIProperties(File f){
        jameleonGUIProperties = f;
    }

    /**
     * Jameleon's implementation of Task.execute().
     *
     * @exception BuildException  Ant's way of reporting exception
     */
    public final void execute() throws BuildException {
        throwExceptionOnNoFile(environmentProperties, "environmentProperties");
        File config = new File("jameleon.conf");
        if (config.exists()) {
            throw new BuildException(config.getPath()+" already exists! No need to update. If you need to update,"+
                                     " but you copied jameleon.conf from another location, then simply either remove the"+
                                     " file and rerun this operation or try your luck with the existing jameleon.conf." );
        }
        if ( environmentProperties.renameTo(config) ){
            System.out.println("Renamed '"+environmentProperties.getPath() + "' to '"+config.getPath()+"'.");
            throwExceptionOnNoFile(jameleonGUIProperties, "jameleonGUIProperties");

            Properties uiProps = updateClasspathEntries();
            Properties confProps = new Properties();
            try{
				try (FileInputStream oFis = new FileInputStream(config))
				{
					confProps.load(oFis);
				}
                confProps.putAll(uiProps);
				try (FileOutputStream oFos = new FileOutputStream(config))
				{
					confProps.store(oFos, null);
				}
                System.out.println("Applied properties in '"+jameleonGUIProperties.getPath() + "' towards '"+config.getPath()+"'.");
            }catch(IOException ioe){
                throw new BuildException(ioe);
            }

            if (!jameleonGUIProperties.delete()){
                throw new BuildException("Could not delete "+jameleonGUIProperties.getPath());
            }
            System.out.println("Removed '"+jameleonGUIProperties.getPath()+"'.");
        }else{
            throw new BuildException("Could not move "+environmentProperties.getPath()+
                                     " to "+ config.getPath());
        }
    }

    private Properties updateClasspathEntries(){
        Configurator.clearInstance();
        Configurator cfg = Configurator.getInstance();
        cfg.setConfigName(jameleonGUIProperties.getPath());
        Set keys = cfg.getKeysStartingWith("classpath.dir");
        Iterator it = keys.iterator();
        String key;
        int entries = 1;
        int maxNum = keys.size();
        for (; it.hasNext(); entries++){
            key = (String)it.next();
            cfg.setValue("classpath.entry"+padNumber(entries, maxNum), cfg.getValue(key));
            cfg.setValue(key, null);
        }
        keys = cfg.getKeysStartingWith("classpath.file");
        it = keys.iterator();
        maxNum += keys.size();
        for (; it.hasNext(); entries++){
            key = (String)it.next();
            cfg.setValue("classpath.entry"+padNumber(entries, maxNum), cfg.getValue(key));
            cfg.setValue(key, null);
        }
        cfg.setValue("sourceDir", null);
        return cfg.getProperties();
    }

    private void throwExceptionOnNoFile(File f, String attributeName) throws BuildException{
        if (f == null || !f.exists() || f.isDirectory()) {
            throw new BuildException("'"+attributeName+"': "+f.getPath() + 
                                     ": is either a directory or is non-existent!");
        }
    }

    protected String padNumber(int num, int maxNum){
        String numS = ""+num;
        String maxNumS = ""+maxNum;
        int numOfZeros = maxNumS.length() - numS.length();
        for (int i = 0; i < numOfZeros; i++) {
            numS = 0 + numS;
        }
        return numS;
    }

}
