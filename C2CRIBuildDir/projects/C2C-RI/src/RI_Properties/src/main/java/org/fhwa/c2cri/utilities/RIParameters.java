/**
 *
 */
package org.fhwa.c2cri.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.fhwa.c2cri.gui.propertyeditor.EditorFile;
import org.fhwa.c2cri.gui.propertyeditor.Group;
import org.fhwa.c2cri.gui.propertyeditor.Parser;

/**
 * This class stores and manages the user designated parameters that apply to
 * the RI. Thes parameters include the location to find user-defined test
 * suites, locations to find user-defined tag libraries, locations to store log
 * and configuration files, etc.
 *
 *
 * @author TransCore ITS, LLC
 * Last Update:  9/2/2012
 *
 *
 */
public class RIParameters {

    /** The ri parameters. */
    private static RIParameters riParameters;
    
    /** The Constant DEFAULT_CONFIG_NAME. */
    public static final String DEFAULT_CONFIG_NAME = "c2cri.properties";
    
    /** The Constant RI_VERSION_NUMBER. */
    public static final String RI_VERSION_NUMBER = "Open Source 1.0";
    
    /** The test target. */
    public static String TEST_TARGET = "TestTarget";
    
    /** The config name. */
    private static String configName = DEFAULT_CONFIG_NAME;
    
    /** The Constant RI_USER_PARAMETER_GROUP. */
    public static final String RI_USER_PARAMETER_GROUP = "General";
    
    /** The Constant RI_USER_PARAMETER. */
    public static final String RI_USER_PARAMETER = "C2CRIUserName";
    
    /** The Constant DEFAULT_RI_USER_PARAMETER_VALUE. */
    public static final String DEFAULT_RI_USER_PARAMETER_VALUE="";
    
    /** The e file. */
    public static EditorFile eFile;

    /** The Constant RI_USER_PARAMETER_GROUP. */
    public static final String RI_TESTING_PARAMETER_GROUP = "Testing";
    
    /** The Constant RI_USER_PARAMETER. */
    public static final String RI_AUTOTEST_PARAMETER = "AutomatedTestMode";
    
    /** The Constant RI_WIZARDMODE_PARAMETER. */
    public static final String RI_WIZARDMODE_PARAMETER = "C2CRIWizardMode";    
    /**
     * Instantiates a new rI parameters.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private RIParameters() {
    }

    /**
     * Gets an instance Configurator if one has already been created or creates
     * a new one if not.
     *
     * @return An instance Configurator if one has already been created or
     * creates a new one if not.
     */
    public synchronized static RIParameters getInstance() {
        if (riParameters == null) {
            riParameters = new RIParameters();
        }
        return riParameters;
    }

    /**
     * Gets an instance Configurator if one has already been created or creates
     * a new one using the specified file name if not.
     *
     * @param configFile the config file
     * @return An instance Configurator if one has already been created or
     * creates a new one if not.
     */
    public synchronized static RIParameters getInstance(String configFile) {
        if (riParameters == null) {
            riParameters = new RIParameters();
        }
        configName = configFile;
        return riParameters;
    }

    /**
     * Set everything up by reading the configuration file. This method is
     * called by {getValue} and is only executed if things aren't already
     * set up. In other words, once a file has been read in, this method will no
     * longer do anything. To read in a new file, call {clearInstance}
     * first.
     */
    public void configure() {
            
        if (eFile == null) {
            File selectedFile = new File(configName);
            eFile = new EditorFile(selectedFile.getPath());
            synchronized(eFile){
            eFile.init();

            int n = Parser.parsePropertyFile(eFile);

            if (n > 0) {
            } else {
                JOptionPane.showMessageDialog(null,
                        "Could not parse property file:\n" + selectedFile.getAbsolutePath(),
                        "Error in Property File",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        }
    }

    /**
     * Gets the num groups.
     *
     * @return the num groups
     */
    public int getNumGroups() {
        int numGroups;
        synchronized (eFile){
            numGroups = eFile.numGroup();
        }
        return numGroups;
    }

    /**
     * Gets the parameter value.
     *
     * @param parameterName the parameter name
     * @return the parameter value
     */
    public String getParameterValue(String parameterName) {
        configure();
        synchronized (eFile){
        for (int ii = 0; ii < eFile.numGroup(); ii++) {
            Group thisGroup = eFile.groupAt(ii);
            for (int jj = 0; jj < thisGroup.numParameters(); jj++) {
                if (thisGroup.parameterAt(jj).getName().equals(parameterName)) {
                    return thisGroup.parameterAt(jj).getValue();
                }
            }
        }
        }
        return "";
    }

    /**
     * Gets the parameter value.
     *
     * @param group the group
     * @param parameterName the parameter name
     * @param defaultValue the default value
     * @return the parameter value
     */
    public String getParameterValue(String group, String parameterName, String defaultValue) {
        configure();
        synchronized (eFile){
        for (int ii = 0; ii < eFile.numGroup(); ii++) {
            Group thisGroup = eFile.groupAt(ii);
            if (thisGroup.getName().equalsIgnoreCase(group)) {
                for (int jj = 0; jj < thisGroup.numParameters(); jj++) {
                    if (thisGroup.parameterAt(jj).getName().equals(parameterName)) {
                        return thisGroup.parameterAt(jj).getValue();
                    }
                }
            }
        }
        }
        return defaultValue;
    }

    /**
     * Gets the parameter value as integer.
     *
     * @param group the group
     * @param parameterName the parameter name
     * @param defaultValue the default value
     * @return the parameter value as integer
     * @throws NumberFormatException the number format exception
     */
    public Integer getParameterValueAsInteger(String group, String parameterName, String defaultValue) throws NumberFormatException {
        configure();
        synchronized (eFile){
        for (int ii = 0; ii < eFile.numGroup(); ii++) {
            Group thisGroup = eFile.groupAt(ii);
            if (thisGroup.getName().equalsIgnoreCase(group)) {
                for (int jj = 0; jj < thisGroup.numParameters(); jj++) {
                    if (thisGroup.parameterAt(jj).getName().equals(parameterName)) {
                        return Integer.parseInt(thisGroup.parameterAt(jj).getValue());
                    }
                }
            }
        }
        }
        return Integer.parseInt(defaultValue);
    }

    /**
     * Gets the parameter value.
     *
     * @param group the group
     * @param parameterName the parameter name
     * @param defaultValue the default value
     * @return the parameter value
     */
    public void setParameterValue(String group, String parameterName, String parameterValue) {
        configure();
        synchronized (eFile){
        for (int ii = 0; ii < eFile.numGroup(); ii++) {
            Group thisGroup = eFile.groupAt(ii);
            if (thisGroup.getName().equalsIgnoreCase(group)) {
                for (int jj = 0; jj < thisGroup.numParameters(); jj++) {
                    if (thisGroup.parameterAt(jj).getName().equals(parameterName)) {
                        thisGroup.parameterAt(jj).setValue(parameterValue);
                        eFile.setModified(true);
                        return;
                    }
                }
            }
        }
        }
    }    
    
    /**
     * Save file.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public static void saveFile() {
        synchronized (eFile){
        if (eFile.exists() && eFile.isModified()) {
            try {
                String newLine = System.getProperty("line.separator");
                BufferedWriter bw = new BufferedWriter(new FileWriter(eFile));
                System.out.println(eFile.numGroup());
                for (int i = 0; i < eFile.numGroup(); i++) {
                    bw.write(eFile.groupAt(i).toText() + newLine);
                }
                bw.close();

                eFile.setModified(false);
            } catch (Exception e) {
                //System.out.println("Error when saving");
                JOptionPane.showMessageDialog(null,
                        "Could not save properties:\n" + eFile.getAbsolutePath(),
                        "Error in Saving Properties",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        }
    }

    /**
     * Prints the parameters.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public static void printParameters() {
        if (eFile != null) {
        synchronized (eFile){
            if (eFile.exists()) {
                try {
                    String newLine = System.getProperty("line.separator");
                    BufferedWriter bw = new BufferedWriter(new FileWriter(eFile));
                    System.out.println(eFile.numGroup());
                    for (int i = 0; i < eFile.numGroup(); i++) {
                        System.out.println(eFile.groupAt(i).toText() + newLine);
                    }
                    bw.close();

                } catch (Exception e) {
                    //System.out.println("Error when saving");
                    JOptionPane.showMessageDialog(null,
                            "Could not print properties at path " + eFile.getAbsolutePath(),
                            "Error in Printing Properties",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Could not print properties at path " + configName + " because the eFile is null.",
                    "Error in Printing Properties",
                    JOptionPane.ERROR_MESSAGE);
        }
        
    }

    /**
     * Sets the Parameter Map based on the users settings on the OptionsUI.
     *
     * @param parameterMap the new parameter map
     */
    public void setParameterMap(HashMap parameterMap) {
    }

    /**
     * Returns the parent map to be used on the OptionsUI.
     *
     * @return the parameter map
     */
    public HashMap getParameterMap() {
        return new HashMap();
    }

    /**
     * Stores the user selected parameters into a parameters file.
     */
    public void storeParametersMap() {
    }

    /**
     * Loads the user selected parameters from the parameters file.
     */
    public void loadParametersMap() {
    }
}
