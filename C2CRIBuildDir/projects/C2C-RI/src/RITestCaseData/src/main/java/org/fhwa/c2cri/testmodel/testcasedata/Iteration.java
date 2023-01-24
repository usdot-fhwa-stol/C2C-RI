package org.fhwa.c2cri.testmodel.testcasedata;


import java.util.Vector;

import javax.swing.JTable;

import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.Group;


/**
 * The Class Iteration.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class Iteration {
    
    /** The name. */
    String name;
    
    /** The group list. */
    Vector groupList;
    
    /** Flag indicating The source for this iteration **/
    boolean baselineIteration;
    
    /**
     * Instantiates a new iteration.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param name the name
     */
    public Iteration(String name) {
        this.name = name;
        groupList = new Vector();

    }
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName(){
        return name;
    }
    
    /**
     * Adds the group.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param g the g
     */
    public void addGroup(Group g){
        groupList.add(g);
    }

    /**
     * Removes the group.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param g the g
     */
    public void removeGroup(String g){
        for (int i=0; i < groupList.size(); i++){
            Group group = (Group)groupList.elementAt(i);
            if (group.getName().equals(g)&&(!group.isBaselineSource())){
                groupList.remove(group);
                break;
            }
        }
    }
    
    
    /**
     * Group at.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param i the i
     * @return the group
     */
    public Group groupAt(int i){
        return (Group)groupList.elementAt(i);
    }

    /**
     * Num groups.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the int
     */
    public int numGroups(){
        return groupList.size();
    }

    /**
     * Sets the iteration name to the new name provided.
     * @param newName 
     */
    public void setName(String newName){
        name = newName;
    }
    
    /**
     * Indicates whether the iteration was sourced from the baseline data file.
     * 
     * @return flag indicating that the iteration was defined in the baseline test case file
     */
    public boolean isBaselineIteration() {
        return baselineIteration;
    }

    /**
     * Indicate whether the iteration was sourced from the baseline data file
     * @param baselineIteration 
     */
    public void setBaselineIteration(boolean baselineIteration) {
        this.baselineIteration = baselineIteration;
    }
    
    
    /**
     * public JTable toVisual(){
     * 
     * String[] columnNames = {"Attribute Name", "Attribute Value"};
     * 
     * //DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
     * 
     * FormattedTableModel tableModel =  new FormattedTableModel();
     * 
     * for(int i=0; i<groupList.size(); i++){
     * if(((Parameter)pList.elementAt(i)).isEditable()){
     * Object[] rowdata = new Object[2];
     * rowdata[0] = parameterAt(i).getName();
     * rowdata[1] = pList.elementAt(i);
     * 
     * tableModel.addRow(rowdata);
     * }
     * }
     * 
     * EditorTable table = new EditorTable(tableModel);
     * 
     * return table;
     * }
     *
     * @return the string
     */
    public String toString(){
        return groupList.toString();
    }
    
    /**
     * To text.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the string
     */
    public String toText(){
        //System.out.println("\tinto group " + name);
        String newLine = System.getProperty("line.separator");
        String s = "#ITERATION NAME = " + name + newLine;
        for (int i=0; i < groupList.size(); i++) s += groupAt(i).toText() + newLine + newLine;
        //System.out.println("\tfinished group " + name);

        return s;
    }
    
}
