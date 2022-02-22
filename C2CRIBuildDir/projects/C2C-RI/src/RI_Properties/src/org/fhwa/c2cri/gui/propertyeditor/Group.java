package org.fhwa.c2cri.gui.propertyeditor;


import java.util.Vector;

import javax.swing.JTable;

import org.fhwa.c2cri.gui.propertyeditor.formattedtable.FormattedTableModel;


/**
 * The Class Group.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class Group {
    
    /** The name. */
    String name;
    
    /** The p list. */
    Vector pList; //List of parameters
    
    /**
     * Instantiates a new group.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
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
    public String getName(){
        return name;
    }
    
    /**
     * Adds the parameter.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param p the p
     */
    public void addParameter(Parameter p){
        pList.add(p);
    } 
 
    /**
     * Parameter at.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param i the i
     * @return the parameter
     */
    public Parameter parameterAt(int i){
        return (Parameter)pList.elementAt(i);
    }
   
    /**
     * Num parameters.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the int
     */
    public int numParameters(){
        return pList.size();
    }


    /**
     * To visual.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the j table
     */
    public JTable toVisual(){
        
        String[] columnNames = {"Attribute Name", "Attribute Value"};
        
        //DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        
        FormattedTableModel tableModel =  new FormattedTableModel();
        
        for(int i=0; i<pList.size(); i++){
            if(((Parameter)pList.elementAt(i)).isEditable()){
                Object[] rowdata = new Object[2];
                rowdata[0] = parameterAt(i).getName();
                rowdata[1] = pList.elementAt(i);
                
                tableModel.addRow(rowdata);
            }
        }
        
        EditorTable table = new EditorTable(tableModel);
        
        return table;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return pList.toString();
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
        String s = "#GROUP NAME = " + name + newLine;
        for (int i=0; i < pList.size(); i++) s += parameterAt(i).toText() + newLine + newLine;
        //System.out.println("\tfinished group " + name);

        return s;
    }
    
}
