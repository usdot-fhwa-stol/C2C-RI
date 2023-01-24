package org.fhwa.c2cri.gui.testmodel.testcasedata.editor;

import java.util.Vector;

import javax.swing.JTable;

import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.formattedtable.FormattedTableModel;

/**
 * The Class Group.
 *
 * @author open source community Last Updated: 1/8/2014
 */
public class Group {

    /**
     * The name.
     */
    String name;

    /**
     * The p list.
     */
    Vector pList; //List of parameters

    /**
     * flag indicating whether the group was defined in the baseline data file.
     * *
     */
    boolean baselineSource;

    /**
     * Instantiates a new group.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param name the name
     */
    public Group(String name) {
        this.name = name;
        pList = new Vector();
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     */
    public void setName(String newName) {
        name = newName;
    }
    
    /**
     * Adds the parameter.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param p the p
     */
    public void addParameter(Parameter p) {
        pList.add(p);
    }

    /**
     * Adds the new parameter after the given parameter.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param p the p
     */
    public void addParameter(Parameter existingParameter, Parameter p) {
        int insertIndex = pList.indexOf(existingParameter);
        
        // If the next parameter is a baseline parameter, then add the new parameter to the end of the list.
        if ((pList.size()>1) && (pList.size() > insertIndex+1)){
            Parameter nextParam = (Parameter)pList.elementAt(insertIndex + 1);
            if (nextParam.isBaselineProvided()){
                pList.add(p);
                return;
            }
        }
        // Add the new parameter after the given parameter.
        pList.add(insertIndex + 1, p);
    }

    /**
     * Replaces an existing parameter with a new one provided.
     *
     * @param original
     * @param newParameter
     */
    public void replaceParameter(Parameter original, Parameter newParameter) {
        pList.set(pList.indexOf(original), newParameter);
    }

    /**
     * Removes an existing parameter.
     *
     * @param original
     */
    public void removeParameter(Parameter original) {
        pList.remove(original);
    }

    /**
     * Parameter at.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param i the i
     * @return the parameter
     */
    public Parameter parameterAt(int i) {
        return (Parameter) pList.elementAt(i);
    }

    /**
     * Num parameters.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the int
     */
    public int numParameters() {
        return pList.size();
    }

    /**
     * Removes the parameters.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public void removeParameters() {
        pList.removeAllElements();
    }

    /**
     * Moves the parameter up on the list. Pre-Conditions: N/A Post-Conditions:
     * The order of the parameters is changed.
     *
     * @param parameter the parameter to be moved up.
     * @return true if the parameter was actually moved
     */
    public boolean moveParameterUp(Parameter parameter) {
        if (!parameter.isBaselineProvided()) {
            int currentIndex = pList.indexOf(parameter);
            if (currentIndex > 0) {
                Parameter originalParameter = (Parameter) pList.elementAt(currentIndex - 1);
                if (!originalParameter.isBaselineProvided()) {
                    pList.set(currentIndex - 1, parameter);
                    pList.set(currentIndex, originalParameter);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Moves the parameter down on the list. Pre-Conditions: N/A
     * Post-Conditions: The order of the parameters is changed.
     *
     * @param parameter the parameter to be moved down.
     * @return true if the parameter was actually moved.
     */
    public boolean moveParameterDown(Parameter parameter) {
        if (!parameter.isBaselineProvided()) {
            int currentIndex = pList.indexOf(parameter);
            if (currentIndex < pList.size() - 1) {
                Parameter originalParameter = (Parameter) pList.elementAt(currentIndex + 1);
                pList.set(currentIndex + 1, parameter);
                pList.set(currentIndex, originalParameter);
                return true;
            }
        }
        return false;
    }

    /**
     * To visual.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the j table
     */
    public JTable toVisual() {

        String[] columnNames = {"Attribute Name", "Attribute Value"};

        //DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        FormattedTableModel tableModel = new FormattedTableModel();

        for (int i = 0; i < pList.size(); i++) {
            Object[] rowdata = new Object[2];
            rowdata[0] = parameterAt(i).getName();
            rowdata[1] = pList.elementAt(i);

            tableModel.addRow(rowdata);
        }

        EditorTable table = new EditorTable(tableModel);
        // Allow the created tables to be edited using the keyboard only.
        table.setSurrendersFocusOnKeystroke(true);

        return table;
    }

    /**
     * Determine whether this group was defined in the baseline test case data
     * file.
     *
     */
    public boolean isBaselineSource() {
        return baselineSource;
    }

    /**
     * Specify whether this group was defined in the baseline test case data
     * file.
     *
     * @param baselineSource
     */
    public void setBaselineSource(boolean baselineSource) {
        this.baselineSource = baselineSource;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return pList.toString();
    }

    /**
     * To text.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the string
     */
    public String toText() {
        //System.out.println("\tinto group " + name);
        String newLine = System.getProperty("line.separator");
        String s = "#GROUP NAME = " + name + newLine;
        for (int i = 0; i < pList.size(); i++) {
            s += parameterAt(i).toText() + newLine + newLine;
        }
        //System.out.println("\tfinished group " + name);

        return s;
    }

    /**
     * To text.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the string
     */
    public String toText(boolean userOnly) {
        //System.out.println("\tinto group " + name);
        String newLine = System.getProperty("line.separator");
        String s = "#GROUP NAME = " + name + newLine;
        if (userOnly) {
            for (int i = 0; i < pList.size(); i++) {
                if (!parameterAt(i).isBaselineProvided()) {
                    s += parameterAt(i).toText() + newLine + newLine;
                }
            }
        } else {
            for (int i = 0; i < pList.size(); i++) {
                s += parameterAt(i).toText() + newLine + newLine;
            }
        }
        //System.out.println("\tfinished group " + name);

        return s;
    }

}
