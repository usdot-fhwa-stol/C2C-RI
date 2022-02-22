/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JOptionPane;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.fhwa.c2cri.infolayer.SubPubMapper;
import org.fhwa.c2cri.infolayer.SubPubMappingException;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.ntcip2306v109.utilities.XMLFileReader;
import org.fhwa.c2cri.tmdd.dbase.TMDDConnectionPool;

/**
 * This class maintains a map between the subscriptions and related publication dialogs defined within TMDD v3.03d.
 *
 * @author TransCore ITS, LLC
 * Last Updated: 1/8/2014
 */
public class TMDDSubPubMapping
        implements SubPubMapper
{

    private String  subPubTableName;
    
    /**
     * 
     * @param subPubTableName 
     */
    public TMDDSubPubMapping(String subPubTableName){
        this.subPubTableName = subPubTableName;        
    }
    
    /**
     * Given an subscription dialog name and the message associated with the description return the appropriate publication dialog.
     *
     * @param subscriptionOperationName the subscription operation name
     * @param inputMsg                  the input msg
     *
     * @return the related publication name
     *
     * @throws SubPubMappingException the sub pub mapping exception
     */
    @Override
    public String getRelatedPublicationName(String subscriptionOperationName, Message inputMsg) throws SubPubMappingException
    {
        return lookupSubscription(subscriptionOperationName, inputMsg);
    }

    /**
     * Given an publication dialog name return the appropriate subscription dialog as defined by the standard.
     *
     * @param publicationOperationName the publication operation name
     *
     * @return the related subscription name
     *
     * @throws SubPubMappingException the sub pub mapping exception
     */
    @Override
    public String getRelatedSubscriptionName(String publicationOperationName) throws SubPubMappingException
    {
        return lookupPublication(publicationOperationName);
    }

    /**
     * Given an subscription dialog name and the message associated with the description return the appropriate publication dialog.
     *
     * @param subOperation the sub operation
     * @param inputMessage the input message
     *
     * @return the string
     *
     * @throws SubPubMappingException the sub pub mapping exception
     */
    public String lookupSubscription(String subOperation, Message inputMessage) throws SubPubMappingException
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            // Create a SQLite connection
            conn = TMDDConnectionPool.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM "+subPubTableName + " where SubDialog = '" + subOperation + "'");

            ArrayList<MatchCandidate> matchCandidateList = new ArrayList();

            while (rs.next())
            {
                MatchCandidate thisMatch = new MatchCandidate();
                thisMatch.setRequirementID(rs.getString("ValueReqID"));
                thisMatch.setElement(rs.getString("Element"));
//                    }
                thisMatch.setPath(rs.getString("Path"));
                thisMatch.setValue(rs.getString("Value"));
                thisMatch.setPublication(rs.getString("PubDialog"));
                matchCandidateList.add(thisMatch);
                //              }
            }

            if (matchCandidateList.isEmpty())
                return "";
            else if (matchCandidateList.size() == 1)
                return matchCandidateList.get(0).getPublication();
            else
            {  // Figure out which publication dialog applies
                HashMap<String, ArrayList<MatchCandidate>> candidatesMap = new HashMap();
                for (MatchCandidate match: matchCandidateList)
                    if ((match.getRequirementID() == null) || match.getRequirementID().isEmpty())
                        return match.getPublication();
                    else if (candidatesMap.containsKey(match.getRequirementID()))
                        candidatesMap.get(match.requirementID).add(match);
                    else
                    {
                        ArrayList<MatchCandidate> matchList = new ArrayList();
                        matchList.add(match);
                        candidatesMap.put(match.requirementID, matchList);
                    }

                Iterator mapIterator = candidatesMap.keySet().iterator();
                while (mapIterator.hasNext())
                {
                    String thisRequirement = (String)mapIterator.next();
                    if (isRequirementFulfilledNTCIP2306(candidatesMap.get(thisRequirement), inputMessage))
                        return candidatesMap.get(thisRequirement).get(0).getPublication();;
                }
            }

            return "";
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                                          "Reading the TMDD Database failed: \n"
                                          + ex.getMessage(),
                                          "TMDD",
                                          JOptionPane.ERROR_MESSAGE);
            throw new SubPubMappingException(ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception ex){                
            }
            
            try{
                if (stmt != null) stmt.close();
            } catch (Exception ex){                
            }

            try{
                if (conn != null) TMDDConnectionPool.releaseConnection(conn);
            } catch (Exception ex){                
            }
        }
    }

    /**
     * Given an publication dialog name return the appropriate subscription dialog as defined by the standard.
     *
     * @param pubOperation the pub operation
     *
     * @return the string
     *
     * @throws SubPubMappingException the sub pub mapping exception
     */
    public String lookupPublication(String pubOperation) throws SubPubMappingException
    {
        Connection conn = null;
        Statement stmt = null;

        try
        {
            // Create a SQLite connection
            conn = TMDDConnectionPool.getConnection();

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM "+subPubTableName + " where PubDialog = '" + pubOperation + "'");

            ArrayList<String> subscriptions = new ArrayList();
            ArrayList<String> requirements = new ArrayList();

            while (rs.next())
            {
                subscriptions.add(rs.getString("SubDialog"));
                if (rs.getString("ValueReqID") != null)
                    requirements.add(rs.getString("ValueReqID"));
            }
            rs.close();
            stmt.close();
            TMDDConnectionPool.releaseConnection(conn);

            if (subscriptions.size() == 1)
                return (subscriptions.get(0));
            else if (subscriptions.size() > 1)
            {
                // Figure out which publication matches
                for (String thisRequirement: requirements)
                    System.out.println(thisRequirement);
                return subscriptions.get(0);
            }
            else
                return "";

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                                          "Reading the TMDD Database failed: \n"
                                          + ex.getMessage(),
                                          "TMDD",
                                          JOptionPane.ERROR_MESSAGE);
            throw new SubPubMappingException(ex.getMessage());
        }

    }

    /**
     * This procedure verifies that the content of the inputMessage satisfies each of the conditions defined within the matchList.
     *
     * @param matchList    the match list
     * @param inputMessage the input message
     *
     * @return true, if is requirement fulfilled ntci p2306
     */
    public boolean isRequirementFulfilledNTCIP2306(ArrayList<MatchCandidate> matchList, Message inputMessage)
    {
        boolean result = false;

        if (inputMessage.getMessageBodyParts().size() == 2)
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(inputMessage.getMessageBodyParts().get(1));

            // Declare the namespace that will be used.
            String xqNamespace
                   = "declare namespace mes='http://www.tmdd.org/303/messages'; ";
            XmlCursor cursor = null;

            try
            {
                XmlObject xml = XmlObject.Factory.parse(bais);
                // Insert a cursor and move it to the first element.
                cursor = xml.newCursor();

                for (MatchCandidate thisMatch: matchList)
                {
                    cursor.toStartDoc();

                    // Query for zip elements.
                    String xPathStatement = "/mes:" + thisMatch.getPath().replaceFirst("/", "") + "/" + thisMatch.getElement();
                    cursor.selectPath(xqNamespace + "$this/" + xPathStatement);

                    if ((cursor.getSelectionCount() > 0))
                    {
                        result = true;
                        cursor.toNextSelection();
                        cursor.toNextToken();
                        if (cursor.getChars().equals(thisMatch.getValue()))
                            result = true;
                        else
                        {
                            cursor.dispose();
                            return false;
                        }
//                            System.out.println(cursor.getChars()+" = "+thisMatch.getValue()+"?");
                    }
                    else
                    {  // This requirement is not a match.  Return false.
                        cursor.dispose();
//                        System.out.println(thisMatch.getRequirementID() + " was not a match!!");
//                        System.out.println("Rejection: " + xqNamespace + "$this/" + xPathStatement);
                        return result = false;
                    }

                    // Loop through the list of selections, getting the value of
                    // each element.
                    // Pop the saved location off the stack.
                    cursor.pop();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                // Dispose of the cursor.
                if (cursor != null)
                    cursor.dispose();
            }

        }
        return result;
    }


    /**
     * The Class MatchCandidate.
     *
     * @author TransCore ITS, LLC
     * Last Updated: 1/8/2014
     */
    class MatchCandidate
    {

        /**
         * The requirement id.
         */
        private String requirementID = "";

        /**
         * The element.
         */
        private String element = "";

        /**
         * The path.
         */
        private String path = "";

        /**
         * The value.
         */
        private String value = "";

        /**
         * The publication.
         */
        private String publication = "";

        /**
         * Gets the requirement id.
         *
         * @return the requirement id
         */
        public String getRequirementID()
        {
            return requirementID;
        }

        /**
         * Sets the requirement id.
         *
         * @param requirementID the new requirement id
         */
        public void setRequirementID(String requirementID)
        {
            this.requirementID = requirementID;
        }

        /**
         * Gets the element.
         *
         * @return the element
         */
        public String getElement()
        {
            return element;
        }

        /**
         * Sets the element.
         *
         * @param element the new element
         */
        public void setElement(String element)
        {
            this.element = element;
        }

        /**
         * Gets the path.
         *
         * @return the path
         */
        public String getPath()
        {
            return path;
        }

        /**
         * Sets the path.
         *
         * @param path the new path
         */
        public void setPath(String path)
        {
            this.path = path;
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public String getValue()
        {
            return value;
        }

        /**
         * Sets the value.
         *
         * @param value the new value
         */
        public void setValue(String value)
        {
            this.value = value;
        }

        /**
         * Gets the publication.
         *
         * @return the publication
         */
        public String getPublication()
        {
            return publication;
        }

        /**
         * Sets the publication.
         *
         * @param publication the new publication
         */
        public void setPublication(String publication)
        {
            this.publication = publication;
        }

    }
}
