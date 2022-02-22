/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testmodel.testcasedata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.Parameter;

/**
 * The Class TestCaseDataSource.
 *
 * @author TransCore ITS, LLC
 * Last Updated: 1/8/2014
 */
public class TestCaseDataSource
{

    /**
     * The base tc file.
     */
    TestCaseFile baseTCFile;

    /**
     * The user tc file.
     */
    TestCaseFile userTCFile;

    /**
     * The more rows available.
     */
    boolean moreRowsAvailable;

    /**
     * The iteration list.
     */
    ArrayList<ArrayList<LinkedHashMap<String, Parameter>>> iterationGroupParameterList = new ArrayList<ArrayList<LinkedHashMap<String, Parameter>>>();
//    ArrayList<HashMap<String, Parameter>> groupList = new ArrayList<HashMap<String, Parameter>>();
    /**
     * The group array list.
     */
    ArrayList<String[]> iterationGroupArrayList = new ArrayList<String[]>();

    /**
     * The userdefinedvalue.
     */
    public static String USERDEFINEDVALUE = "#USERDEFINED#";

    /**
     * The randomvalue.
     */
    public static String RANDOMVALUE = "#RANDOM#";

    /**
     * The messagespecvalue.
     */
    public static String MESSAGESPECVALUE = "#MESSAGESPEC#";

    /**
     * The valuespecvalue.
     */
    public static String VALUESPECVALUE = "#VALUESPEC#";

    /**
     * The messagelistvalue.
     */
    public static String MESSAGELISTVALUE = "#MESSAGELIST#";

    /**
     * The current date and time offset by the minutes provided *
     */
    public static String CURRENTDATETIMEOFFSETINMINUTES = "#CURRENTDATETIMEOFFSETINMINUTES#";

    /**
     * The file-stored value.
     */
    public static String FROMFILEVALUE = "#FILE#";    // CTCRI-707: Let parameter values be read from file

    /**
     * Instantiates a new test case data source.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private TestCaseDataSource()
    {
    }

    /**
     * Instantiates a new test case data source.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param baseTestCaseSpec the base test case spec
     * @param userTestCaseSpec the user test case spec
     */
    public TestCaseDataSource(URL baseTestCaseSpec, File userTestCaseSpec)
    {
        if (baseTCFile == null)
            try
            {
                baseTCFile = new TestCaseFile(baseTestCaseSpec.toURI());

                baseTCFile.init();

                int n = TestCaseDataParser.parsePropertyFile(baseTCFile);

                if (n > 0)
                {

                    if (userTestCaseSpec != null)
                    {
                        userTCFile = new TestCaseFile(userTestCaseSpec.getPath());

                        userTCFile.init();

                        n = TestCaseDataParser.parsePropertyFile(userTCFile);

                        if (n > 0)
                        {
                        }
                        else
                            javax.swing.JOptionPane.showMessageDialog(null,
                                                                      "Could not parse property file:\n" + userTestCaseSpec.getAbsolutePath(),
                                                                      "Error in Property File",
                                                                      javax.swing.JOptionPane.ERROR_MESSAGE);

                    }

                    mergeFiles();

                }
                else
                    javax.swing.JOptionPane.showMessageDialog(null,
                                                              "Could not parse property file:\n" + baseTestCaseSpec.toString(),
                                                              "Error in Property File",
                                                              javax.swing.JOptionPane.ERROR_MESSAGE);

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
    }

    /**
     * Instantiates a new test case data source.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param baseTestCaseSpec the base test case spec
     */
    public TestCaseDataSource(URL baseTestCaseSpec)
    {
        if (baseTCFile == null)
            try
            {
                baseTCFile = new TestCaseFile(baseTestCaseSpec.toURI());

                baseTCFile.init();

                int n = TestCaseDataParser.parsePropertyFile(baseTCFile);

                if (n > 0)
                    mergeFiles();
                else
                    javax.swing.JOptionPane.showMessageDialog(null,
                                                              "Could not parse property file:\n" + baseTestCaseSpec.toString(),
                                                              "Error in Property File",
                                                              javax.swing.JOptionPane.ERROR_MESSAGE);

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
    }

    /**
     * Instantiates a new test case data source.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param userTestCaseSpec the user test case spec
     */
    public TestCaseDataSource(String userTestCaseSpec)
    {
        if (baseTCFile == null)
            try
            {
                baseTCFile = new TestCaseFile(userTestCaseSpec);

                baseTCFile.init();

                int n = TestCaseDataParser.parsePropertyFile(baseTCFile);

                if (n > 0)
                    mergeFiles();
                else
                    javax.swing.JOptionPane.showMessageDialog(null,
                                                              "Could not parse property file:\n" + userTestCaseSpec,
                                                              "Error in Property File",
                                                              javax.swing.JOptionPane.ERROR_MESSAGE);

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
    }

    /**
     * CTCRI-707: A method to read contents of a given file as the value of a parameter
     *
     * @param fileAddress The address of the file containing the parameter value
     *
     * @return
     */
    private String getValueFromFile(String fileAddress)
    {
        try
        {
            BufferedReader file = new BufferedReader(new FileReader(fileAddress));
            String line = file.readLine();
            String value = "";

            while (line != null)
            {
                value += line + "\n";
                line = file.readLine();
            }

            return value.trim();    // There is an extra EOL because of the way we add lines to value in the loop above
        }
        catch (FileNotFoundException ex)
        {
            JOptionPane.showMessageDialog(null, "Invalid file address to read parameter value.\n Please select the file from the following dialog.");

            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION)
                return getValueFromFile(chooser.getSelectedFile().getAbsolutePath());
        }
        catch (IOException ex)
        {
            Logger.getLogger(TestCaseDataSource.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Merge files.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private void mergeFiles()
    {
        // Create a Map of the Base Test Case Data File
        for (int ii = 0; ii < baseTCFile.numIteration(); ii++)
        {

            // Create an Array of groups and a HashMap of parameters (maintaining order) for each iteration.
            String[] iterationGroupArray = new String[baseTCFile.iterationAt(ii).numGroups()];
            ArrayList<LinkedHashMap<String, Parameter>> groupParameterList = new ArrayList<LinkedHashMap<String, Parameter>>();

            // Iterate through each group that exists within the current iteration of the base Test Case Data File.
            for (int jj = 0; jj < baseTCFile.iterationAt(ii).numGroups(); jj++)
            {
                // Add the group to the array
                iterationGroupArray[jj] = baseTCFile.iterationAt(ii).groupAt(jj).getName();

                // Create a parameter map which stores each parameter defined within this group and this iteration.
                LinkedHashMap<String, Parameter> parameterMap = new LinkedHashMap<String, Parameter>();
                for (int kk = 0; kk < baseTCFile.iterationAt(ii).groupAt(jj).numParameters(); kk++)
                {
                    Parameter theParameter = baseTCFile.iterationAt(ii).groupAt(jj).parameterAt(kk);
                    parameterMap.put(theParameter.getName(), theParameter);
                }

                // Add the parameter name and all of its details to the group list.
                groupParameterList.add(parameterMap);
            }
            // Store the current set of groups and parameters.
            iterationGroupArrayList.add(iterationGroupArray);
            iterationGroupParameterList.add(groupParameterList);
        }

        // The user can not add additional iterations or groups to those in the base file, but the user can rename a group (for message specification changes).
        for (int ii = 0; ii < baseTCFile.numIteration(); ii++)
            for (int jj = 0; jj < baseTCFile.iterationAt(ii).numGroups(); jj++)
                if (userTCFile != null && userTCFile.numIteration() > ii)
                    // The number of groups should be 1 greater than the group (zero-based) index.
                    if (userTCFile.iterationAt(ii).numGroups() > jj)
                    { //

                        // If the user file changes this group name, then update the groupname stored in the array.
                        // Also, clear the existing parameter list at this group index and this iteration.
                        if (!iterationGroupArrayList.get(ii)[jj].equals(userTCFile.iterationAt(ii).groupAt(jj).getName()))
                        {
                            iterationGroupArrayList.get(ii)[jj] = userTCFile.iterationAt(ii).groupAt(jj).getName();
                            iterationGroupParameterList.get(ii).get(jj).clear();
                        }

                        // Get a copy of the parametr map fir this index and group.
                        LinkedHashMap<String, Parameter> parameterMap = iterationGroupParameterList.get(ii).get(jj);

                        // loop through all parameters for this group in the user test case file.
                        for (int kk = 0; kk < userTCFile.iterationAt(ii).groupAt(jj).numParameters(); kk++)
                        {
                            Parameter theParameter = userTCFile.iterationAt(ii).groupAt(jj).parameterAt(kk);

                            // If the user file attempts to replace a base variable then verify that it is editable
                            if (parameterMap.containsKey(theParameter.getName()))
                            {
                                Parameter originalParameter = parameterMap.get(theParameter.getName());
                                if (originalParameter.isEditable())
                                {
                                    iterationGroupParameterList.get(ii).get(jj).put(theParameter.getName(), theParameter);
                                    System.out.println("Updating a parameter = " + theParameter.getName() + " ii =" + ii + " jj=" + jj + " kk=" + kk);
                                }
                            }
                            else
                            {  // Add the new user file parameter
                                iterationGroupParameterList.get(ii).get(jj).put(theParameter.getName(), theParameter);
                                System.out.println("Adding a brand new user parameter = " + theParameter.getName() + " ii =" + ii + " jj=" + jj + " kk=" + kk);
                            }
                        }

                    }
                    else
                    {  // Skip extra groups
                    }
                else
                { // Skip extra iterations
                }
    }

    /**
     * Gets the iteration.
     *
     * @param iterationNumber the iteration number
     *
     * @return the iteration
     */
    public Map getIteration(Integer iterationNumber)
    {
        Map returnMap = new LinkedHashMap();
        if (iterationGroupParameterList != null)
        {
            // Iterate through this map.  If a value is Random or UserDefined compute a replacement value or prompt the user.
            // If a parameter type is message list then generate a list object.  If the value of the parameter indicates that additional groups define the message.
            // Create a list of values for each of the groups referenced.  Store this List into the list associated with this parameter name.
            // Each entry in the list is either a string or another list containing message parameter values.
            //
            LinkedHashMap<String, Parameter> thisMap = iterationGroupParameterList.get(iterationNumber).get(0);
            Iterator mapIterator = thisMap.values().iterator();

            while (mapIterator.hasNext())
            {
                Parameter thisParameter = (Parameter)mapIterator.next();

                if (thisParameter.getValue().startsWith(USERDEFINEDVALUE))
                {
                    // Prompt user to supply the value
                    String response = JOptionPane.showInputDialog(null, "Enter the value for Parameter " + thisParameter.getName() + " for Iteration " + (iterationNumber + 1) + "\n\n" + thisParameter.getDoc(), "");
                    returnMap.put(thisParameter.getName(), response);
                }
                else if (thisParameter.getValue().startsWith(RANDOMVALUE))
                {
                    // Calculate a Random Value
                    String randomString = Randomizer.randomstring();
                    returnMap.put(thisParameter.getName(), randomString);
                }
                else if (thisParameter.getValue().startsWith(MESSAGESPECVALUE))
                {
                    // Associate this parameter with the Message Spec
                    String groupName = thisParameter.getValue().replace(MESSAGESPECVALUE, "");
                    returnMap.put(thisParameter.getName(), getMessageSpec(iterationNumber, groupName));
                }
                else if (thisParameter.getValue().startsWith(VALUESPECVALUE))
                {
                    // Associate this parameter with the Value Spec
                    String groupName = thisParameter.getValue().replace(VALUESPECVALUE, "");
                    returnMap.put(thisParameter.getName(), getValueSpec(iterationNumber, groupName));
                }
                else if (thisParameter.getValue().startsWith(MESSAGELISTVALUE))
                {
                    // Associate this parameter with the Message List
                    String listContent = thisParameter.getValue().replace(MESSAGELISTVALUE, "");
                    returnMap.put(thisParameter.getName(), getMessageList(iterationNumber, listContent));
                }
                else if (thisParameter.getValue().startsWith(CURRENTDATETIMEOFFSETINMINUTES))
                {
                    // Calculate the time with the given minutes offset.
                    String timeOffsetInMinutes = thisParameter.getValue().replace(CURRENTDATETIMEOFFSETINMINUTES, "");

                    Date currentTime = new Date();

                    int offset = 0;
                    final long ONE_MINUTE_IN_MILLIS = 60000; //millisecs

                    try
                    {
                        offset = Integer.parseInt(timeOffsetInMinutes);
                    }
                    catch (Exception ex)
                    {
                    }

                    long currentTimeInMs = currentTime.getTime();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    df.setTimeZone(TimeZone.getDefault());
                    Date afterOffsetTime = new Date(currentTimeInMs + (offset * ONE_MINUTE_IN_MILLIS));
                    returnMap.put(thisParameter.getName(), getMessageList(iterationNumber, df.format(afterOffsetTime)));
                }
                else if (thisParameter.getValue().startsWith(FROMFILEVALUE)) // CTCRI-707: Read parameter value from a file
                    // Reads the parameter value from a file,
                    // prompts user to supply the right file if the value in the data source doesn't point to an existing file
                    returnMap.put(thisParameter.getName(), getValueFromFile(thisParameter.getValue().replace(FROMFILEVALUE, "")));
                else // Add the parameter to the map
                    returnMap.put(thisParameter.getName(), thisParameter.getValue());
            }
        }
//        iterationList.get(iterationNumber).get(jj).put(theParameter.getName(), theParameter);
        return returnMap;
    }

    /**
     * Gets the parameter value.
     *
     * @param theParameter the the parameter
     *
     * @return the parameter value
     */
    public String getParameterValue(Parameter theParameter)
    {
        if (theParameter.getValue().startsWith(USERDEFINEDVALUE))
        {
            // Prompt user to supply the value
            String response = JOptionPane.showInputDialog(null, "Enter the value for Parameter " + theParameter.getName() + "\n\n" + theParameter.getDoc(), "");
            return response;
        }
        else if (theParameter.getValue().startsWith(RANDOMVALUE))
        {
            // Calculate a Random Value
            String randomString = Randomizer.randomstring();
            return randomString;
        }
        else if (theParameter.getValue().startsWith(CURRENTDATETIMEOFFSETINMINUTES))
        {
            // Calculate the time with the given minutes offset.
            String timeOffsetInMinutes = theParameter.getValue().replace(CURRENTDATETIMEOFFSETINMINUTES, "");

            Date currentTime = new Date();

            int offset = 0;
            final long ONE_MINUTE_IN_MILLIS = 60000; //millisecs

            try
            {
                offset = Integer.parseInt(timeOffsetInMinutes);
            }
            catch (Exception ex)
            {
            }

            long currentTimeInMs = currentTime.getTime();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            df.setTimeZone(TimeZone.getDefault());
            Date afterOffsetTime = new Date(currentTimeInMs + (offset * ONE_MINUTE_IN_MILLIS));
            return df.format(afterOffsetTime);
        }
        else if (theParameter.getValue().startsWith(FROMFILEVALUE)) // CTCRI-707: Read parameter value from a file
            // Reads the parameter value from a file,
            // prompts user to supply the right file if the value in the data source doesn't point to an existing file
            return getValueFromFile(theParameter.getValue().replace(FROMFILEVALUE, ""));
        else // Add the parameter to the map
            return theParameter.getValue();
    }

//    public ArrayList<String> getMessageSpec(int iteration, String group) {
//        ArrayList<String> valueSpec = new ArrayList<String>();
//        for (int ii = 0; ii < groupArrayList.get(iteration).length; ii++) {
//            if (groupArrayList.get(iteration)[ii].equals(group)) {
//                Iterator hashMapIterator = iterationList.get(iteration).get(ii).values().iterator();
//                while (hashMapIterator.hasNext()) {
//                    Parameter thisParameter = (Parameter) hashMapIterator.next();
////                    valueSpec.add(thisParameter.getName() + " = "+ thisParameter.getValue());
//                    valueSpec.add(thisParameter.getName() + " = " + getParameterValue(thisParameter));
//                }
//                break;
//            }
//        }
//
//        return valueSpec;
//    }
    /**
     * Gets the message spec.
     *
     * @param iteration the iteration
     * @param group     the group
     *
     * @return the message spec
     */
    public String getMessageSpec(int iteration, String group)
    {
        String messageSpec = "";
        for (int ii = 0; ii < iterationGroupArrayList.get(iteration).length; ii++)
            if (iterationGroupArrayList.get(iteration)[ii].equals(group))
            {
                Iterator hashMapIterator = iterationGroupParameterList.get(iteration).get(ii).values().iterator();
                while (hashMapIterator.hasNext())
                {
                    Parameter thisParameter = (Parameter)hashMapIterator.next();
//                    valueSpec.add(thisParameter.getName() + " = "+ thisParameter.getValue());
                    if (!messageSpec.equals(""))
                        messageSpec = messageSpec.concat("; " + thisParameter.getName() + " = " + getParameterValue(thisParameter));
                    else
                        messageSpec = messageSpec.concat("#RIMessageSpec#" + thisParameter.getName() + " = " + getParameterValue(thisParameter));
                    //                   valueSpec.add(thisParameter.getName() + " = " + getParameterValue(thisParameter));
                }
                break;
            }
        if (!messageSpec.equals(""))
            messageSpec = messageSpec.concat("#RIMessageSpec#");
        return messageSpec;
    }

    /**
     * Gets the value spec.
     *
     * @param iteration the iteration
     * @param group     the group
     *
     * @return the value spec
     */
    public String getValueSpec(int iteration, String group)
    {
        String valueSpec = "";
        for (int ii = 0; ii < iterationGroupArrayList.get(iteration).length; ii++)
            if (iterationGroupArrayList.get(iteration)[ii].equals(group))
            {
                Iterator hashMapIterator = iterationGroupParameterList.get(iteration).get(ii).values().iterator();
                while (hashMapIterator.hasNext())
                {
                    Parameter thisParameter = (Parameter)hashMapIterator.next();
//                    valueSpec.add(thisParameter.getName() + " = "+ thisParameter.getValue());
                    if (!valueSpec.equals(""))
                        valueSpec = valueSpec.concat("; " + thisParameter.getName() + " = " + getParameterValue(thisParameter));
                    else
                        valueSpec = valueSpec.concat("#RIValueSpec#" + thisParameter.getName() + " = " + getParameterValue(thisParameter));
                    //                   valueSpec.add(thisParameter.getName() + " = " + getParameterValue(thisParameter));
                }
                break;
            }
        if (!valueSpec.equals(""))
            valueSpec = valueSpec.concat("#RIValueSpec#");
        return valueSpec;
    }

    /**
     * Gets the iteration count.
     *
     * @return the iteration count
     */
    public int getIterationCount()
    {
        return iterationGroupParameterList.size();
    }

    /**
     * Gets the message list.
     *
     * @param iteration         the iteration
     * @param messageListString the message list string
     *
     * @return the message list
     */
    public ArrayList<String> getMessageList(int iteration, String messageListString)
    {
        String[] variableArray = messageListString.split(";");
        ArrayList<String> messageList = new ArrayList<String>();

        // Loop for each variable referenced
        for (int ii = 0; ii < variableArray.length; ii++)
            if (iterationGroupParameterList.get(iteration).get(0).containsKey(variableArray[ii]))
            {
                Parameter thisParameter = iterationGroupParameterList.get(iteration).get(0).get(variableArray[ii]);
                messageList.add(thisParameter.getValue());
            }
            else
                System.err.println("Could not find Variable " + variableArray[ii] + " in iteration " + iteration);

        return messageList;
    }

    /**
     * Sets the value test spec.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param valueName the value name
     * @param valueTest the value test
     * @param iteration the iteration
     */
    public void setValueTestSpec(String valueName, String valueTest, int iteration)
    {
        boolean valueFound = false;
        String group = "";
        int groupIndex = -1;

        // Find a group that contains the Values Specification
        for (int ii = 0; ii < this.baseTCFile.iterationAt(iteration).numGroups(); ii++)
            for (int jj = 0; jj < this.baseTCFile.iterationAt(iteration).groupAt(ii).numParameters(); jj++)
                if (this.baseTCFile.iterationAt(iteration).groupAt(ii).parameterAt(jj).getName().equalsIgnoreCase("VerificationSpec"))
                {
                    group = this.baseTCFile.iterationAt(iteration).groupAt(ii).parameterAt(jj).getValue().replace(VALUESPECVALUE, "");
                    groupIndex = ii;
                }

        if (!group.isEmpty())
            for (int ii = 0; ii < this.baseTCFile.iterationAt(iteration).numGroups(); ii++)
                if (this.baseTCFile.iterationAt(iteration).groupAt(ii).getName().equals(group))
                {
                    groupIndex = ii;
                    for (int jj = 0; jj < this.baseTCFile.iterationAt(iteration).groupAt(groupIndex).numParameters(); jj++)
                        if (this.baseTCFile.iterationAt(iteration).groupAt(groupIndex).parameterAt(jj).getName().equals(valueName))
                            if (this.baseTCFile.iterationAt(iteration).groupAt(groupIndex).parameterAt(jj).isEditable())
                            {
                                this.baseTCFile.iterationAt(iteration).groupAt(groupIndex).parameterAt(jj).setValue(valueTest);
                                valueFound = true;
                                break;
                            }

                    if (!valueFound)
                    {
                        Parameter thisParameter = new Parameter(valueName, valueTest, true, "User Generated Value Test Parameter", "String");
                        this.baseTCFile.iterationAt(iteration).groupAt(groupIndex).addParameter(thisParameter);
                    }

                }
    }

    /**
     * Update tc file.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param fileName the file name
     */
    public void updateTCFile(String fileName)
    {
        try
        {
            File updateFile = new File(fileName);
            String newLine = System.getProperty("line.separator");
            BufferedWriter bw = new BufferedWriter(new FileWriter(updateFile));
            for (int ii = 0; ii < baseTCFile.numIteration(); ii++)
                bw.write(baseTCFile.iterationAt(ii).toText() + newLine);

            bw.flush();
            bw.close();

            baseTCFile.setModified(false);
        }
        catch (Exception e)
        {
            //System.out.println("Error when saving");
            JOptionPane.showMessageDialog(null,
                                          "Could not save Data File:\n" + baseTCFile.getAbsolutePath(),
                                          "Error in Saving Value Verification Data",
                                          JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * The main method.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     *
     * @throws Exception the exception
     */
    public static void main(String args[]) throws Exception
    {
        TestCaseDataSource newSource = new TestCaseDataSource(new URL("file:/C:/TRANSCOM/c2cri.properties"));
//        newSource.mergeFiles();
//        newSource.iterationList.clear();

        System.out.println("Now trying both");
        File blankFile = new File("c:/temp/userc2cri.properties");
        TestCaseDataSource newCombinedSource = new TestCaseDataSource(new URL("file:/C:/TRANSCOM/c2cri.properties"), blankFile);
//        newCombinedSource.mergeFiles();

        for (int ii = 0; ii < newSource.iterationGroupParameterList.size(); ii++)
        {
            System.out.println("ITERATION " + ii);
            System.out.println(newSource.getIteration(ii));
            System.out.println("");
            System.out.println("");
        }
        System.exit(0);
    }
}
