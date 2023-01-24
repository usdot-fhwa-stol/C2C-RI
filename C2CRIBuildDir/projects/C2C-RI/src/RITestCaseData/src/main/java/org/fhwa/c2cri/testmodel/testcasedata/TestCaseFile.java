package org.fhwa.c2cri.testmodel.testcasedata;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.Group;
import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.Parameter;

/**
 * The Class TestCaseFile represents the test case data file.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class TestCaseFile extends File {

    /**
     * The iteration array.
     */
    Vector iterationArray = new Vector();

    /**
     * The group array.
     */
    Vector groupArray = new Vector();

    /**
     * The is modified.
     */
    EditorModified isModified = new EditorModified();

    /**
     * The errors.
     */
    String errors;

    /**
     * The url source.
     */
    boolean urlSource = false;

    /**
     * The path uri.
     */
    URI pathURI;
    
    /**
     * The set of groups defined in the baseline test case file
     */
    Map<String, List<String>> baselineGroups = new HashMap<>();
    /**
     * Instantiates a new test case file.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param path the path
     */
    public TestCaseFile(String path) {
        super(path);
    }

    /**
     * Instantiates a new test case file.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param path the path
     */
    public TestCaseFile(URI path) {
        super(path.toString());

        urlSource = true;
        this.pathURI = path;
    }

    /**
     * Checks if is modified.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return true, if is modified
     */
    public boolean isModified() {
        return isModified.getState();
    }

        /**
     * Checks if is refresh is desired.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return true, if is modified with desired refresh
     */
    public boolean isRefreshDesired() {
        return isModified.getRefreshDesired();
    }

    /**
     * Adds the iteration.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param i the i
     */
    public void addIteration(Iteration i) {
        iterationArray.add(i);
    }

    /**
     * Adds the groupto iteration.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param i the i
     * @param g the g
     */
    public void addGrouptoIteration(Iteration i, Group g) {
        i.addGroup(g);
    }

    /**
     * Adds the group.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param g the g
     */
    public void addGroup(Group g) {
        groupArray.add(g);
    }

    /**
     * Inits the.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public void init() {
        iterationArray = new Vector();
        groupArray = new Vector();
        isModified.setState(false);
        errors = "";
    }

    /**
     * Iteration at.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param i the i
     * @return the iteration
     */
    public Iteration iterationAt(int i) {
        return (Iteration) iterationArray.elementAt(i);
    }

    /**
     * Group at.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param i the i
     * @return the group
     */
    public Group groupAt(int i) {
        return (Group) groupArray.elementAt(i);
    }

    /**
     * Num iteration.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the int
     */
    public int numIteration() {
        return iterationArray.size();
    }

    /**
     * Num parameters.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param iteration the iteration
     * @return the int
     */
    public int numParameters(int iteration) {
        int numParameters = 0;
        for (int ii = 0; ii < ((Iteration) iterationArray.get(iteration)).numGroups(); ii++) {
            numParameters = numParameters + ((Iteration) iterationArray.get(iteration)).groupAt(ii).numParameters();
        }
        return numParameters;
    }

    /**
     * Gets the parameters.
     *
     * @param iteration the iteration
     * @param group the group
     * @return the parameters
     */
    public HashMap<String, Parameter> getParameters(int iteration, int group) {
        HashMap<String, Parameter> returnMap = new HashMap<String, Parameter>();
        for (int ii = 0; ii <= (((Iteration) iterationArray.get(iteration)).groupAt(group).numParameters()); ii++) {
            Parameter thisParameter = ((Iteration) iterationArray.get(iteration)).groupAt(group).parameterAt(ii);
            if (!returnMap.containsKey(thisParameter.getName())) {
                returnMap.put(thisParameter.getName(), thisParameter);
            }
        }
        return returnMap;
    }

    /**
     * Num group.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the int
     */
    public int numGroup() {
        return groupArray.size();
    }

    /**
     * Sets the modified.
     *
     * @param b the new modified
     */
    public void setModified(boolean b) {
        isModified.setState(b);
    }
    
    /**
     * Sets the modified.
     *
     * @param b the new modified
     */
    public void setModifiedNoRefresh(boolean b) {
        isModified.setStateNoRefresh(true);
    }
    

    /**
     * Sets the error parsing.
     *
     * @param e the new error parsing
     */
    public void setErrorParsing(String e) {
        errors = e;
    }

    /**
     * Gets the error parsing.
     *
     * @return the error parsing
     */
    public String getErrorParsing() {
        return errors;
    }

    /**
     * Adds the observer.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param o the o
     */
    public void addObserver(Observer o) {
        isModified.addObserver(o);
    }

    /**
     * Checks if is uRL source.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return true, if is uRL source
     */
    public boolean isURLSource() {
        return urlSource;
    }

    /**
     * Gets the path uri.
     *
     * @return the path uri
     */
    public URI getPathURI() {
        return pathURI;
    }

    /**
     * Merge files and provide the updated reults in the User Test Case File.
     * <p>
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public static TestCaseFile mergeFiles(TestCaseFile userTCFile, TestCaseFile baseTCFile) {

        TestCaseFile mergedFile = new TestCaseFile(userTCFile.getPath());
        // Mark all Base Test Case File Parameters as originating from the base file.
        for (int ii = 0; ii < baseTCFile.numIteration(); ii++) {
            baseTCFile.iterationAt(ii).setBaselineIteration(true);
            mergedFile.addIteration(baseTCFile.iterationAt(ii));

            // Iterate through each group that exists within the current iteration of the base Test Case Data File.
            for (int jj = 0; jj < mergedFile.iterationAt(ii).numGroups(); jj++) {
                mergedFile.iterationAt(ii).groupAt(jj).setBaselineSource(true);
                mergedFile.recordBaselineGroup(mergedFile.iterationAt(ii).getName(), mergedFile.iterationAt(ii).groupAt(jj).getName());
                for (int kk = 0; kk < mergedFile.iterationAt(ii).groupAt(jj).numParameters(); kk++) {
                    mergedFile.iterationAt(ii).groupAt(jj).parameterAt(kk).setBaselineProvided(true);
                }
                
            }

        }

        // Add in user test case files elements related to this iteration.
        for (int ii = 0; ii < userTCFile.numIteration(); ii++) {
            Iteration it = userTCFile.iterationAt(ii);
            if ((mergedFile.numIteration() >= ii) && (!it.getName().equalsIgnoreCase(((Iteration) mergedFile.iterationAt(ii)).getName()))) {
                mergedFile.iterationAt(ii).setName(it.getName());
                mergedFile.iterationAt(ii).setBaselineIteration(false);
            }

//            for (int jj = 0; jj < userTCFile.iterationAt(ii).numGroups(); jj++) {
//                if ((mergedFile.iterationAt(ii).numGroups() >= ii) && (!it.groupAt(jj).getName().equalsIgnoreCase(((Iteration) mergedFile.iterationAt(ii)).groupAt(jj).getName()))) {
//                    mergedFile.iterationAt(ii).groupAt(jj).setName(it.groupAt(jj).getName());
//                    mergedFile.iterationAt(ii).groupAt(jj).setBaselineSource(false);
//                }
//            }
        }

        // The user can not add additional iterations or groups to those in the base file, but the user can add a group (for message specification changes).
        for (int ii = 0; ii < mergedFile.numIteration(); ii++) {
            for (int jj = 0; jj < mergedFile.iterationAt(ii).numGroups(); jj++) {
                if (userTCFile != null && userTCFile.numIteration() > ii) // The number of groups should be 1 greater than the group (zero-based) index.
                {
                    if (userTCFile.iterationAt(ii).numGroups() > jj) { //

                        // If the user file changes this group name, then update the groupname stored in the merged File.
                        // Also, clear the existing parameter list at this group index and this iteration.
                        if (!mergedFile.iterationAt(ii).groupAt(jj).getName().equals(userTCFile.iterationAt(ii).groupAt(jj).getName())) {
                            mergedFile.iterationAt(ii).groupAt(jj).setName(userTCFile.iterationAt(ii).groupAt(jj).getName());
                            mergedFile.iterationAt(ii).groupAt(jj).removeParameters();
                            mergedFile.iterationAt(ii).groupAt(jj).setBaselineSource(false);
                        }

                        // Get a copy of the parametr map for this index and group.
                        LinkedHashMap<String, Parameter> parameterMap = new LinkedHashMap<>();
                        for (int kk = 0; kk < mergedFile.iterationAt(ii).groupAt(jj).numParameters(); kk++) {
                            parameterMap.put(mergedFile.iterationAt(ii).groupAt(jj).parameterAt(kk).getName(), mergedFile.iterationAt(ii).groupAt(jj).parameterAt(kk));
                        }

                        // loop through all parameters for this group in the user test case file.
                        for (int ll = 0; ll < userTCFile.iterationAt(ii).groupAt(jj).numParameters(); ll++) {
                            Parameter theParameter = userTCFile.iterationAt(ii).groupAt(jj).parameterAt(ll);

                            // If the user file attempts to replace a base variable then verify that it is editable
                            if (parameterMap.containsKey(theParameter.getName())) {
                                Parameter originalParameter = parameterMap.get(theParameter.getName());
                                if (originalParameter.isEditable()) {
                                    mergedFile.iterationAt(ii).groupAt(jj).replaceParameter(originalParameter, theParameter);
                                    theParameter.setOverwritten(true);
                                    if (originalParameter.isBaselineProvided()) {
                                        theParameter.setBaselineProvided(true);
                                    }

                                    System.out.println("Updating a parameter = " + theParameter.getName() + " ii =" + ii + " jj=" + jj + " kk=" + ll);
                                }
                            } else {  // Add the new user file parameter
                                theParameter.setBaselineProvided(false);
                                theParameter.setOverwritten(true);
                                mergedFile.iterationAt(ii).groupAt(jj).addParameter(theParameter);
                                System.out.println("Adding a brand new user parameter = " + theParameter.getName() + " ii =" + ii + " jj=" + jj + " kk=" + ll);
                            }
                        }

                    } else {  // Skip extra groups
                    }
                } else { // Skip extra iterations
                }
            }
            
//            // If the user test case file has additional groups then add them.
//            int currentGroupCount = mergedFile.iterationAt(ii).numGroups();
//            if (userTCFile.iterationAt(ii).numGroups() > currentGroupCount){
//                for (int jj = currentGroupCount; jj < userTCFile.iterationAt(ii).numGroups(); jj++) {
//                    userTCFile.iterationAt(ii).groupAt(jj).setBaselineSource(false);
//                    for (int kk = 0; kk< userTCFile.iterationAt(ii).groupAt(jj).numParameters(); kk++){
//                        userTCFile.iterationAt(ii).groupAt(jj).parameterAt(kk).setBaselineProvided(false);
//                    }
//                    System.out.println("Adding a brand new user group "+userTCFile.iterationAt(ii).groupAt(jj).getName());
//                    mergedFile.iterationAt(ii).addGroup(userTCFile.iterationAt(ii).groupAt(jj));                    
//                }                
//            }
        }
        return mergedFile;
    }

    /**
     * Provide a new name for the iteration provided.
     *
     * @param oldName
     * @param newName
     */
    public void renameIteration(String oldName, String newName) {
        for (int ii = 0; ii < numIteration(); ii++) {
            if (iterationAt(ii).getName().equals(oldName)) {
                iterationAt(ii).setName(newName);
                if (baselineGroups.containsKey(oldName)){
                    List<String> groupList = baselineGroups.get(oldName);
                    baselineGroups.remove(oldName);
                    baselineGroups.put(newName, groupList);
                }
                isModified.setState(true);
                break;
            }
        }
    }

    /**
     * Provide a new name for the group provided.
     *
     * @param iterationName
     * @param oldGroupName
     * @param newGroupName
     */
    public void replaceGroup(String iterationName, String oldGroupName, String newGroupName, boolean clearExistingParameters) {
        for (int ii = 0; ii < numIteration(); ii++) {
            if (iterationAt(ii).getName().equals(iterationName)) {
                for (int jj = 0; jj < iterationAt(ii).numGroups(); jj++) {
                    if (iterationAt(ii).groupAt(jj).getName().equals(oldGroupName)) {
                        iterationAt(ii).groupAt(jj).setName(newGroupName);
                        if (clearExistingParameters){
                            iterationAt(ii).groupAt(jj).removeParameters();
                        } else {
                            for (int kk = 0; kk < iterationAt(ii).groupAt(jj).numParameters();kk++){
                                iterationAt(ii).groupAt(jj).parameterAt(kk).setOverwritten(true);
                                iterationAt(ii).groupAt(jj).parameterAt(kk).setBaselineProvided(false);
                            }
                        }
                        isModified.setState(true);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Provide a new group for the name provided.
     *
     * @param iterationName
     * @param oldGroupName
     * @param newGroupName
     */
    public void addGroup(String iterationName, String newGroupName) {
        for (int ii = 0; ii < numIteration(); ii++) {
            if (iterationAt(ii).getName().equals(iterationName)) {
                iterationAt(ii).addGroup(new Group(newGroupName));
                isModified.setState(true);
                break;
            }
        }
    }

    /**
     * Remove the group with the name provided.
     *
     * @param iterationName
     * @param groupName
     * @param newGroupName
     */
    public void removeGroup(String iterationName, String groupName) {
        for (int ii = 0; ii < numIteration(); ii++) {
            if (iterationAt(ii).getName().equals(iterationName)) {
                for (int jj = 0; jj < iterationAt(ii).numGroups(); jj++) {
                    if (iterationAt(ii).groupAt(jj).getName().equals(groupName)) {
                        iterationAt(ii).removeGroup(groupName);
                        isModified.setState(true);
                        break;
                    }
                }
            }
        }
    }    
    
    /**
     * Provide a new name for the group provided.
     *
     * @param iterationName
     * @param oldGroupName
     * @param newGroupName
     */
    public boolean isGroupRemovable(String iterationName, String groupName) {
        for (int ii = 0; ii < numIteration(); ii++) {
            if (iterationAt(ii).getName().equals(iterationName)) {
                for (int jj = 0; jj < iterationAt(ii).numGroups(); jj++) {
                    if (iterationAt(ii).groupAt(jj).getName().equals(groupName)) {
                        if (iterationAt(ii).groupAt(jj).isBaselineSource()){
                            return false;
                        } else return true;
                    }
                }
            }
        }
        return false;
    }        

    
/**
 * Records the iteration-groupname pair in the baseline groups map
 * @param iteration
 * @param groupName 
 */
    private void recordBaselineGroup(String iteration, String groupName){
        if (baselineGroups.containsKey(iteration)){
            List groupList = baselineGroups.get(iteration);
            groupList.add(groupName);
        } else {
            List<String> groupList = new ArrayList<>();
            groupList.add(groupName);
            baselineGroups.put(iteration, groupList);
        }
    }

    /**
     * Provide a new name for the group provided.
     *
     * @param iterationName
     * @param oldGroupName
     * @param newGroupName
     */
    public boolean isGroupNameAvailable(String iterationName, String groupName) {
        if (baselineGroups.containsKey(iterationName)){
            List<String> groupList = baselineGroups.get(iterationName);
            for (String groupNameItem : groupList){
                if (groupNameItem.equalsIgnoreCase(groupName)) return false;
            }
        }
         return true;     
    }        
    
    /**
     * Remove the parameter provided, from the given iteration and group.
     *
     * @param iterationName
     * @param groupName
     * @param existingParameter
     */
    public void removeParameter(String iterationName, String groupName, Parameter existingParameter) {
        for (int ii = 0; ii < numIteration(); ii++) {
            if (iterationAt(ii).getName().equals(iterationName)) {
                for (int jj = 0; jj < iterationAt(ii).numGroups(); jj++) {
                    if (iterationAt(ii).groupAt(jj).getName().equals(groupName)) {
                        iterationAt(ii).groupAt(jj).removeParameter(existingParameter);
                        isModified.setState(true);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Remove the Group provided, from the given iteration.
     *
     * @param iterationName
     * @param group
     */
    public void removeParameter(String iterationName, Group group) {
        for (int ii = 0; ii < numIteration(); ii++) {
            if (iterationAt(ii).getName().equals(iterationName)) {
                iterationAt(ii).groupList.remove(group);
                isModified.setState(true);
                break;
            }
        }
    }

    /**
     * Move the given parameter up in the list of parameters for a given
     * iteration and group.
     *
     * @param iterationName
     * @param groupName
     * @param existingParameter
     */
    public void moveParameterUp(String iterationName, String groupName, Parameter existingParameter) {
        for (int ii = 0; ii < numIteration(); ii++) {
            if (iterationAt(ii).getName().equals(iterationName)) {
                for (int jj = 0; jj < iterationAt(ii).numGroups(); jj++) {
                    if (iterationAt(ii).groupAt(jj).getName().equals(groupName)) {
                        boolean result = iterationAt(ii).groupAt(jj).moveParameterUp(existingParameter);
                        if (result) {
                            isModified.setState(true);
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * Move the given parameter down in the list of parameters for a given
     * iteration and group.
     *
     * @param iterationName
     * @param groupName
     * @param existingParameter
     */
    public void moveParameterDown(String iterationName, String groupName, Parameter existingParameter) {
        for (int ii = 0; ii < numIteration(); ii++) {
            if (iterationAt(ii).getName().equals(iterationName)) {
                for (int jj = 0; jj < iterationAt(ii).numGroups(); jj++) {
                    if (iterationAt(ii).groupAt(jj).getName().equals(groupName)) {
                        boolean result = iterationAt(ii).groupAt(jj).moveParameterDown(existingParameter);
                        if (result) {
                            isModified.setState(true);
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * Add the given parameter to the list of parameters for a given iteration
     * and group after the given previousParmeter (if provided).
     *
     * @param iterationName
     * @param groupName
     * @param previousParameter
     * @param newParameter
     */
    public void addParameter(String iterationName, String groupName, Parameter previousParameter, Parameter newParameter) {
        for (int ii = 0; ii < numIteration(); ii++) {
            if (iterationAt(ii).getName().equals(iterationName)) {
                for (int jj = 0; jj < iterationAt(ii).numGroups(); jj++) {
                    if (iterationAt(ii).groupAt(jj).getName().equals(groupName)) {                        
                        if (previousParameter == null) {
                            iterationAt(ii).groupAt(jj).addParameter(newParameter);
                        } else {
                            iterationAt(ii).groupAt(jj).addParameter(previousParameter, newParameter);
                        }
                        isModified.setState(true);
                        break;
                    }
                }
            }
        }
    }

}

class EditorModified extends Observable {

    boolean isModified = false;
    boolean isRefreshDesired = true;

    public void setState(boolean b) {
        isModified = b;
        isRefreshDesired = true;
        setChanged();
        notifyObservers();
    }

    public void setStateNoRefresh(boolean b) {
        isModified = b;
        isRefreshDesired = false;
        setChanged();
        notifyObservers();
    }
    
    public boolean getState() {
        return isModified;
    }

    public boolean getRefreshDesired(){
        return isRefreshDesired;
    }
}
