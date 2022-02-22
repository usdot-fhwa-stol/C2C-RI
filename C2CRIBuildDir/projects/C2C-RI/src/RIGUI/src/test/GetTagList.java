/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.sf.jameleon.TestCaseTagLibrary;
import net.sf.jameleon.bean.Attribute;
import net.sf.jameleon.bean.FunctionalPoint;
import net.sf.jameleon.util.InstanceSerializer;
import net.sf.jameleon.util.SupportedTags;

/**
 * The Class GetTagList.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class GetTagList {

        /**
         * The main method.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param args the arguments
         */
        public static void main(String[] args) {

                  TestCaseTagLibrary.resetTags();
                SupportedTags st = new SupportedTags();
                st.setWarnOnNoPluginsFile(true);
                //Get all supported tags
                Map tags = st.getSupportedTags();
                populateTree(tags, Thread.currentThread().getContextClassLoader());

    }
    
    /**
     * Populate tree.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param tags the tags
     * @param cl the cl
     */
    protected static void populateTree(Map tags,  ClassLoader cl) {
        Set usedClasses = new HashSet();
        Iterator it = tags.keySet().iterator();
        String className = null;
        int count = 0;
        System.out.println("number" + "," + "TagName"+","+"ClassName"+","+"Description"+","+
                       "AttributeName"+","+"AttributeisRequired"+","+"AttributeDescription");

        while ( it.hasNext() ) {
            className = (String)tags.get((String)it.next());
            if ( usedClasses.add(className) ) {
                addFunctionalPointToTree(count, className, cl);
                count++;
            }
        }
    }

    /**
     * Adds the functional point to tree.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param number the number
     * @param qName the q name
     * @param cl the cl
     */
    protected static void addFunctionalPointToTree(int number, String qName,  ClassLoader cl) {
        String dirNode = null;
        String className = qName;
        for ( int index = className.indexOf('.'); index > -1; index = className.indexOf('.') ) {
            dirNode = className.substring(0,index);
 /**
            node = new DefaultMutableTreeNode(dirNode, true);
            DefaultMutableTreeNode nodeFound = null;
            Enumeration e = pNode.children();
            while ( e.hasMoreElements() ) {
                nodeFound = (DefaultMutableTreeNode)e.nextElement();
                if ( !nodeFound.isLeaf() || nodeFound.getUserObject() instanceof String ) {
                    if ( ((String)nodeFound.getUserObject()).equals(dirNode) ) {
                        node = nodeFound;
                    }
                }
            }
            if ( pNode.getIndex(node) == -1 ) {
                pNode.add(node);
            }
            pNode = node;
  */
            className = className.substring(index+1);
        }
        String fileName = qName.replace('.', '/')+InstanceSerializer.SERIALIZED_EXT;
        try {
            FunctionalPoint fp = (FunctionalPoint)InstanceSerializer.deserialize(cl.getResourceAsStream(fileName));
            String attributes = "";
            Map attributeMap = fp.getAttributes();
            Iterator it = attributeMap.keySet().iterator();
            int count = 0;
            while ( it.hasNext() ) {
               String attributeKey = ((String)it.next());
               Attribute myAttribute = ((Attribute)attributeMap.get(attributeKey));
               System.out.println(number + "," + fp.getDefaultTagName()+","+fp.getClassName()+","+fp.getDescription().replace("\n","<LF>").replace(",", "<Comma>")+
                       ","+myAttribute.getName()+","+myAttribute.isRequired()+","+myAttribute.getDescription().replace("\n", "<LF>").replace(",", "<Comma>"));
            }

        } catch (InvalidClassException ice){
            System.err.println("Could not load tag for "+qName+"!");
            ice.printStackTrace();
        } catch ( IOException ioe ) {
            ioe.printStackTrace();//Simply can't read in the source dat file
        } catch ( ClassNotFoundException cnfe ) {
            cnfe.printStackTrace();
            System.err.print(cnfe.getMessage());
        }
    }

}
