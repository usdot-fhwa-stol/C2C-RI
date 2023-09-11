/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationRepository;
import org.fhwa.c2cri.tmdd.emulation.exceptions.NRTMViolationException;

/**
 *
 * @author TransCore ITS, LLC Created: Feb 11, 2016
 */
public class TMDDNRTMSelections {

    private static TMDDNRTMSelections nrtmSelections;
    private static String tMDDSchemaDetailTableName = "TMDDSchemaDetail";

    private TMDDNRTMSelections() {
    }

    ;
    
    public static TMDDNRTMSelections getInstance() {
        if (nrtmSelections == null) {
            nrtmSelections = new TMDDNRTMSelections();
        }
        return nrtmSelections;
    }

    public void setTMDDSchemaDetailTableName(String tableName){
        tMDDSchemaDetailTableName = tableName;
    }
    
    public void initialize() throws EntityEmulationException {
        Connection conn = null;
		EntityEmulationException oEEE = null;
        try {
            conn = EntityEmulationRepository.getConnection();

            // Clear all existing records from the entity data type table
            String deleteEntityElementsCommand = "delete from TMDDSelectedNeedsAndRequirements";
            Statement stmt = conn.createStatement();
            stmt.execute(deleteEntityElementsCommand);
            stmt.close();

            conn.commit();
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex){
            throw new EntityEmulationException(ex);            
        } finally{
            if (conn != null) {
                try{
                    EntityEmulationRepository.releaseConnection(conn);                
                } catch (Exception ex){
                    oEEE = new EntityEmulationException(ex);
                }
            }
        }
		if (oEEE != null)
			throw oEEE;
    }

    public void addNRTM(String needID, String requirementID) throws EntityEmulationException {
        Connection conn = null;
		EntityEmulationException oEEE = null;
        try {
            // Clear all existing records from the entity data type table
            String insertNRTMSelectionsCommand = "INSERT INTO TMDDSelectedNeedsAndRequirements ( NeedID, RequirementID) VALUES (\""
                    + needID + "\",\"" + requirementID + "\")";
            System.out.println(insertNRTMSelectionsCommand);

            conn = EntityEmulationRepository.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute(insertNRTMSelectionsCommand);
            stmt.close();
            conn.commit();
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex){
            throw new EntityEmulationException(ex); 
        } finally {
            if (conn != null){
                try{
                   EntityEmulationRepository.releaseConnection(conn);                    
                } catch (Exception ex){
                    oEEE = new EntityEmulationException(ex);
                }
            }
        }
		if (oEEE != null)
			throw oEEE;
    }

    // For each required element of a message (per NRTM selections) perform an inspection of the inputMessage to confirm
    // that it satisfies the presence requirements (i.e. the element must be present in the message, present in all instances of elements, etc)
    public void verifyMessageNRTM(String dialog, MessageSpecification inputMessage, TMDDEntityType.EntityType entityType) throws NRTMViolationException, EntityEmulationException {
        ArrayList<String> messageErrors = new ArrayList();
        // Retrieve the set of elements that are required for this message/dialog combination

        Connection conn = null;
        PreparedStatement verifyMessagreNRTMCommand = null;
        ResultSet rs = null;
		EntityEmulationException oEEE = null;
        
        //TODO  Add code to restrict the query in cases where there are multiple needs that match the combination of dialog, message, entityType
        try {
            conn = EntityEmulationRepository.getConnection();
            verifyMessagreNRTMCommand = conn.prepareStatement("select \"UN ID\",\"Requirement ID\",\"DC Type\", ParentType, elementName, Path from TMDDNRTMSelectionsToDesignLookupQuery, "+tMDDSchemaDetailTableName+" where TMDDNRTMSelectionsToDesignLookupQuery.Dialog = ? and TMDDNRTMSelectionsToDesignLookupQuery.Message = ? and TMDDNRTMSelectionsToDesignLookupQuery.EntityType = ? and TMDDNRTMSelectionsToDesignLookupQuery.id = "+tMDDSchemaDetailTableName+".id");
            verifyMessagreNRTMCommand.setString(1, dialog);
            String messageName = "";
            String messageInstanceName = "";
            switch (inputMessage.getMessageTypes().size()) {
                case 1:
                    messageInstanceName = inputMessage.getMessageTypes().get(0);
                    messageName = messageInstanceName.replace("tmdd:", "");
                    break;
                case 2:
                    messageInstanceName = inputMessage.getMessageTypes().get(1);
                    messageName = messageInstanceName.replace("tmdd:", "");
                    break;
                default:
                    throw new EntityEmulationException("Could not Verify message against NRTM.  It contains more than 2 message parts.");
            }
            verifyMessagreNRTMCommand.setString(2, messageName);
            verifyMessagreNRTMCommand.setString(3, entityType.name());
            rs = verifyMessagreNRTMCommand.executeQuery();

            // Assuming you are working with a newly returned ResultSet whose cursor is pointing before the first row, an easier way to check this is to just call isBeforeFirst(). This avoids having to back-track if the data is to be read.
            // As explained in the documentation, this returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            if (!rs.isBeforeFirst()) {
                rs.close();
                verifyMessagreNRTMCommand.close();
            } else {
                while (rs.next()) {
                    String path = rs.getString("Path");
                    path = path.substring(1).replace("/", ".");
                    String elementType = rs.getString("DC Type");
                    String needID = rs.getString("UN ID");
                    String reqID = rs.getString("Requirement ID");
                    String elementName = rs.getString(("elementName"));
                    //TODO add an alternateElementName option in case of option choice values (currently only dms-message and message-number of dMSControlRequestMsg
                    String alternateElementName = null;
                    String instanceName = path.replace("[*]", "");
                    instanceName = path.substring(0, path.lastIndexOf(elementName)).replace("[*]", "");
                    if (instanceName.endsWith(".")){
                        instanceName = instanceName.substring(0, instanceName.length()-1);
                    }

                    if (elementType.equalsIgnoreCase("data-element")) {
                        if (!inputMessage.verifyExistsInEveryInstance(elementName, instanceName)) {
                            ArrayList<String> tempErrors = new ArrayList();
                            for (String thisError : inputMessage.getErrorsFound()) {
                                String errorsFound = "NeedID: " + needID + " RequirementID: " + reqID + " " + thisError;
                                tempErrors.add(errorsFound);
                            }
                            if (alternateElementName != null) {
                                if (!inputMessage.verifyExistsInEveryInstance(elementName, alternateElementName, instanceName)) {
                                    for (String thisError : inputMessage.getErrorsFound()) {
                                        String errorsFound = "NeedID: " + needID + " RequirementID: " + reqID + " " + thisError;
                                        messageErrors.add(errorsFound);
                                    }
                                }
                            } else {
                                messageErrors.addAll(tempErrors);
                            }
                        }
// TODO If we ever need to add checks for instance values here.
//                if ((instanceValue != null)&&(!instanceValue.isEmpty())) {
//                    if (!instanceValue.equals(inputMessage.getElementValue(instanceName+"."+elementName))) {
//                        String errorsFound = "Element "+ elementName + " was set to "+inputMessage.getElementValue(elementName)+" instead of "+instanceValue;
//                        if (alternateInstanceValue != null){
//                            if (!alternateInstanceValue.equals(inputMessage.getElementValue(elementName))){
//                               errorsFound = errorsFound.concat(" or "+alternateInstanceValue + ".");
//                            }
//                        } else {
//                             errorsFound = errorsFound.concat(".");
//                        }
//                    }
//                }
                    }

                    if (elementType.equalsIgnoreCase("message")) {
                        ArrayList<String> messagesPresent = inputMessage.getMessageTypes();
                        boolean messageFound = false;
                        for (String message : messagesPresent) {
                            // If message contains a prefix, then strip it.
                            message = message.substring(message.indexOf(":") + 1);
                            if (message.equals(messageName)) {
                                messageFound = true;
                            }
                        }
                        if (!messageFound) {
                            String errorsFound = "NeedID: " + needID + " RequirementID: " + reqID + " Message " + messageName + " was not present.";
                            messageErrors.add(errorsFound);
                        }

                    } else if (elementType.equalsIgnoreCase("data-frame")) {
                        if (!inputMessage.verifyExistsInEveryInstance(elementName, instanceName)) {
                            for (String thisError : inputMessage.getErrorsFound()) {
                                String errorsFound = "NeedID: " + needID + " RequirementID: " + reqID + " " + thisError;
                                messageErrors.add(errorsFound);
                            }
                        }
                    }

                }
                rs.close();
                verifyMessagreNRTMCommand.close();
            }

            if (messageErrors.size() > 0) {
                System.out.println("**** NRTM Errors Found : " + messageErrors);
                throw new NRTMViolationException(messageErrors.toString());
            }
            
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new EntityEmulationException(ex);
        } finally {
            
            if (verifyMessagreNRTMCommand != null){
                try{
                    verifyMessagreNRTMCommand.close();
                } catch (Exception ex){
                    oEEE = new EntityEmulationException(ex);
                }
            }
            
            if (rs != null){
                try{
                    rs.close();
                } catch (Exception ex){
                    oEEE = new EntityEmulationException(ex);
                }
            }            
            if (conn != null){
                try{
                    EntityEmulationRepository.releaseConnection(conn);                    
                } catch (Exception ex){
                    oEEE = new EntityEmulationException(ex);
                }
            }
        }
		if (oEEE != null)
			throw oEEE;
    }

    public boolean isAuthenticationRequired(String dialog, MessageSpecification inputMessage, TMDDEntityType.EntityType entityType) throws NRTMViolationException, EntityEmulationException {
        boolean result = false;
        Connection conn = null;
        PreparedStatement checkAuthenticationRequirementCommand = null;
        ResultSet rs = null;
		EntityEmulationException oEEE = null;
        
        // Check the TMDDNRTMSelectionsToDesignLookupQuery to determine whether a need/requirement selection exists with Dialog = dialogname (provided), Message = Message Name (provided) and DC Instance Name = "authentication"
        try {
            conn = EntityEmulationRepository.getConnection();
            checkAuthenticationRequirementCommand = conn.prepareStatement("select count(*) from TMDDNRTMSelectionsToDesignLookupQuery where Dialog = ? and Message = ? and TMDDNRTMSelectionsToDesignLookupQuery.\"DC Instance Name\" = ?");
            checkAuthenticationRequirementCommand.setString(1, dialog);
            String messageName = "";
            switch (inputMessage.getMessageTypes().size()) {
                case 1:
                    messageName = inputMessage.getMessageTypes().get(0).replace("tmdd:", "");
                    break;
                case 2:
                    messageName = inputMessage.getMessageTypes().get(1).replace("tmdd:", "");
                    break;
                default:
                    throw new EntityEmulationException("Could not Authenticate message.  It contains more than 2 message parts.");
            }
            checkAuthenticationRequirementCommand.setString(2, messageName);
            checkAuthenticationRequirementCommand.setString(3, "authentication");
            rs = checkAuthenticationRequirementCommand.executeQuery();

            // Assuming you are working with a newly returned ResultSet whose cursor is pointing before the first row, an easier way to check this is to just call isBeforeFirst(). This avoids having to back-track if the data is to be read.
            // As explained in the documentation, this returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            if (!rs.isBeforeFirst()) {
                rs.close();
                checkAuthenticationRequirementCommand.close();
                result = false;
            } else {
                int rowCount = rs.getInt(1);
                if (rowCount > 0) {
                    result = true;
                } else {
                    result = false;
                }
                rs.close();
                checkAuthenticationRequirementCommand.close();
            }

        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally{
            if (rs != null){
                try{
                    rs.close();            
                } catch (Exception ex){
                    oEEE = new EntityEmulationException(ex);
                }
            }            
            
            if (checkAuthenticationRequirementCommand != null){
                try{
                    checkAuthenticationRequirementCommand.close();            
                } catch (Exception ex){
                    oEEE = new EntityEmulationException(ex);
                }
            }            
            
            if (conn != null){
                try{
                    EntityEmulationRepository.releaseConnection(conn);            
                } catch (Exception ex){
                    oEEE = new EntityEmulationException(ex);
                }
            }
        }
		if (oEEE != null)
			throw oEEE;
        return result;
    }

    public boolean isOperatorIdRequired(String dialog, MessageSpecification inputMessage, TMDDEntityType.EntityType entityType) throws NRTMViolationException, EntityEmulationException {
        boolean result = false;

        Connection conn = null;
        PreparedStatement checkAuthenticationRequirementCommand = null;
        ResultSet rs = null;
		EntityEmulationException oEEE = null;
        
        // Check the TMDDNRTMSelectionsToDesignLookupQuery to determine whether a need/requirement selection exists with Dialog = dialogname (provided), Message = Message Name (provided) and DC Instance Name = "operator-id"
        try {
            conn = EntityEmulationRepository.getConnection();
            checkAuthenticationRequirementCommand = conn.prepareStatement("select count(*) from TMDDNRTMSelectionsToDesignLookupQuery where Dialog = ? and Message = ? and TMDDNRTMSelectionsToDesignLookupQuery.\"DC Instance Name\" = ?");
            checkAuthenticationRequirementCommand.setString(1, dialog);
            String messageName = "";
            switch (inputMessage.getMessageTypes().size()) {
                case 1:
                    messageName = inputMessage.getMessageTypes().get(0).replace("tmdd:", "");
                    break;
                case 2:
                    messageName = inputMessage.getMessageTypes().get(1).replace("tmdd:", "");
                    break;
                default:
                    throw new EntityEmulationException("Could not Authenticate message.  It contains more than 2 message parts.");
            }
            checkAuthenticationRequirementCommand.setString(2, messageName);
            checkAuthenticationRequirementCommand.setString(3, "operator-id");
            rs = checkAuthenticationRequirementCommand.executeQuery();

            // Assuming you are working with a newly returned ResultSet whose cursor is pointing before the first row, an easier way to check this is to just call isBeforeFirst(). This avoids having to back-track if the data is to be read.
            // As explained in the documentation, this returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            if (!rs.isBeforeFirst()) {
                rs.close();
                checkAuthenticationRequirementCommand.close();
                result = false;
            } else {
                int rowCount = rs.getInt(1);
                if (rowCount > 0) {
                    result = true;
                } else {
                    result = false;
                }
                rs.close();
                checkAuthenticationRequirementCommand.close();
            }
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        } finally {
            if (rs != null){
                try{
                    rs.close();                    
                } catch (Exception ex){
                    oEEE = new EntityEmulationException(ex);
                }
            }
            
            if (checkAuthenticationRequirementCommand != null){
                try{
                    checkAuthenticationRequirementCommand.close();                    
                } catch (Exception ex){
                    oEEE = new EntityEmulationException(ex);
                }
            }


            if (conn != null){
                try{
                    EntityEmulationRepository.releaseConnection(conn);                    
                } catch (Exception ex){
                    oEEE = new EntityEmulationException(ex);
                }
            }
        }
		if (oEEE != null)
			throw oEEE;
        return result;
    }

}
