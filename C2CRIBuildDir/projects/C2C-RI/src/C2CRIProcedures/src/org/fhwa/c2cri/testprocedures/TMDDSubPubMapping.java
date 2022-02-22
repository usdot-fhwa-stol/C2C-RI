/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import tmddv3verification.utilities.TMDDDatabase;

/**
 *
 * @author TransCore ITS
 */
public class TMDDSubPubMapping {

    private static TMDDSubPubMapping thisMapping;
    private static HashMap<Integer, ArrayList<SubPubMatch>> subPubMap = new HashMap();
    
    public static TMDDSubPubMapping getInstance() {
        if (thisMapping == null) {
            thisMapping = new TMDDSubPubMapping();
        }
        return thisMapping;
    }

    private TMDDSubPubMapping() {

        TMDDDatabase theDatabase = new TMDDDatabase();
        try {
            theDatabase.connectToDatabase();

            ResultSet subPubRS = theDatabase.queryReturnRS("SELECT * FROM TMDDv303SubPubLookupQuery");

            try {
                while (subPubRS.next()) {
                   SubPubMatch thisMatch = new SubPubMatch();
                   thisMatch.setSubscription(subPubRS.getString("SubDialog"));
                   thisMatch.setPublication(subPubRS.getString("PubDialog"));
                   thisMatch.setRelatedMessageRequirement(subPubRS.getString("ValueReqID"));
                   Integer needNumber = subPubRS.getInt("NeedNumber");
                   if (subPubMap.containsKey(needNumber)){
                       subPubMap.get(needNumber).add(thisMatch);
                   } else {
                       ArrayList<SubPubMatch> newList = new ArrayList();
                       newList.add(thisMatch);
                   subPubMap.put(needNumber, newList);
                       
                   }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                subPubRS.close();
                System.out.println("TMDDSubPubMapping: Initialized!!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            theDatabase.disconnectFromDatabase();
        }

    }

    public String getPublicationDialog(String subDialog, int needNumber) throws Exception{
        if (subPubMap.containsKey(needNumber)){
            for (SubPubMatch thisMatch : subPubMap.get(needNumber)){
                if (thisMatch.getSubscription().equals(subDialog)){
                    return thisMatch.getPublication();
                }
            }
        }
        throw new Exception("No Publication Match Found for "+subDialog);
    }

    public String getSubscriptionDialog(String pubDialog, int needNumber) throws Exception {
        System.out.println("The needNumber is "+needNumber);
        if (subPubMap.containsKey(needNumber)){
            for (SubPubMatch thisMatch : subPubMap.get(needNumber)){
                if (thisMatch.getPublication().equals(pubDialog)){
                    return thisMatch.getSubscription();
                }
            }
        }
        throw new Exception("No Subscription Match Found for "+pubDialog);
    }

    public String getRelatedMessageRequirement(String pubDialog, int needNumber) throws Exception {
        if (subPubMap.containsKey(needNumber)){
            for (SubPubMatch thisMatch : subPubMap.get(needNumber)){
                if (thisMatch.getPublication().equals(pubDialog)){
                    return thisMatch.getRelatedMessageRequirement();
                }
            }
        }
        throw new Exception("No Publication Match Found for "+pubDialog);
    }
    
    class SubPubMatch {

        private String subscription;
        private String relatedMessageRequirement;
        private String publication;

        public String getSubscription() {
            return subscription;
        }

        public void setSubscription(String subscription) {
            this.subscription = subscription;
        }

        public String getPublication() {
            return publication;
        }

        public void setPublication(String publication) {
            this.publication = publication;
        }

        public String getRelatedMessageRequirement() {
            return relatedMessageRequirement;
        }

        public void setRelatedMessageRequirement(String relatedMessageRequirement) {
            this.relatedMessageRequirement = relatedMessageRequirement;
        }
    }
}
