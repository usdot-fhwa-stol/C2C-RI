/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.entitydata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.tmdd.emulation.TMDDEntityType;
import org.fhwa.c2cri.tmdd.emulation.TMDDEntityType.EntityType;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityControlRequestStatusException;

/**
 *
 * @author TransCore ITS, LLC Created: Feb 14, 2016
 */
public class EntityControlRequestStatus {

    public static void initialize() throws EntityEmulationException {
        try {
            Connection conn = EntityEmulationRepository.getConnection();
            PreparedStatement clearControlRequestStatusCommand = conn.prepareStatement("delete from ControlRequestStatus");
            clearControlRequestStatusCommand.execute();
            clearControlRequestStatusCommand.close();
            conn.commit();
            EntityEmulationRepository.releaseConnection(conn);

        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        }

    }

    public int getLastIdentifier(String reqOrgId, String requestId, String entityId) {
        int result = 0;

        return result;
    }

/**
 * 
 * @param reqOrgId
 * @param entityId
 * @param requestId
 * @return a status record if one exists matching the parameters supplied.  Otherwise a null is returned.
 * @throws InvalidEntityControlRequestStatusException
 * @throws EntityEmulationException 
 */
    public static EntityControlRequestStatusRecord getControlRequestStatus(String reqOrgId, String entityId, String requestId) throws InvalidEntityControlRequestStatusException, EntityEmulationException {
        EntityControlRequestStatusRecord theResult = null;

        try {

            String selectControlRequestStatusCommand = "Select ID, OrgId, EntityId, RequestId, OperatorId, Status, EntityType, LockStatus, OperatorLockId, Priority, LastRevisedDate, LastRevisedTime, LastRevisedOffset from ControlRequestStatus where EntityId = \"" + entityId + "\" and RequestId = \"" + requestId + "\" and OrgId = \"" + reqOrgId + "\"";
            Connection conn = EntityEmulationRepository.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectControlRequestStatusCommand);

            // Assuming you are working with a newly returned ResultSet whose cursor is pointing before the first row, an easier way to check this is to just call isBeforeFirst(). This avoids having to back-track if the data is to be read.
            // As explained in the documentation, this returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            if (!rs.isBeforeFirst()) {
                rs.close();
                stmt.close();
                throw new InvalidEntityControlRequestStatusException("No matching control requests were found.");
            } else {
                String orgId = rs.getString("OrgId");
                String entityType = rs.getString("EntityType");
                theResult = new EntityControlRequestStatusRecord(orgId, entityId, requestId, entityType);
                theResult.setIdentifier(rs.getInt("ID"));
                theResult.setOperatorId(rs.getString("OperatorId"));
                theResult.setStatus(rs.getString("Status"));
                theResult.setLockStatus(rs.getInt("LockStatus"));
                theResult.setOperatorLockId(rs.getString("OperatorLockId"));
                theResult.setPriority(rs.getInt("Priority"));
                theResult.setLastRevisedDate(rs.getString("LastRevisedDate"));
                theResult.setLastRevisedTime(rs.getString("LastRevisedTime"));
                theResult.setLastRevisedOffset(rs.getString("LastRevisedOffset"));
                if (theResult.getLastRevisedOffset().compareToIgnoreCase("Z") == 0)
                    theResult.setLastRevisedOffset("00:00");

                rs.close();
                stmt.close();
            }
            EntityEmulationRepository.releaseConnection(conn);
            return theResult;

        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);        
        }
    }

    public static TMDDEntityType.EntityType getControlRequestEntityType(String reqOrgId, String entityId, String requestId) throws InvalidEntityControlRequestStatusException, EntityEmulationException {
        EntityControlRequestStatusRecord statusRecord = getControlRequestStatus(reqOrgId, entityId, requestId);

        return TMDDEntityType.EntityType.valueOf(statusRecord.getEntityType());
    }

/**
 * 
 * @param entityType
 * @param recordThreshold
 * @return a status record if one exists matching the parameters supplied.  Otherwise a null is returned.
 * @throws InvalidEntityControlRequestStatusException
 * @throws EntityEmulationException 
 */    
    public static EntityControlRequestStatusRecord getQueuedControlRequestStatus(TMDDEntityType.EntityType entityType, int recordThreshold) throws InvalidEntityControlRequestStatusException, EntityEmulationException {
        EntityControlRequestStatusRecord theResult = null;

        try {
            Connection conn = EntityEmulationRepository.getConnection();
            PreparedStatement selectQueuedEntityCountCommand = conn.prepareStatement("Select count(ID) from ControlRequestStatus where EntityType = ? and (Status = \"3\" or Status = \"request queued/not implemented\")");
            selectQueuedEntityCountCommand.setString(1, entityType.name());
            ResultSet rs = selectQueuedEntityCountCommand.executeQuery();
            int commandsQueued = 0;

            // Assuming you are working with a newly returned ResultSet whose cursor is pointing before the first row, an easier way to check this is to just call isBeforeFirst(). This avoids having to back-track if the data is to be read.
            // As explained in the documentation, this returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            if (!rs.isBeforeFirst()) {
                rs.close();
                selectQueuedEntityCountCommand.close();
                throw new InvalidEntityControlRequestStatusException("No matching control requests were found.");
            } else {
                commandsQueued = rs.getInt(1);
                rs.close();
                selectQueuedEntityCountCommand.close();
            }

                // return the Control Request Status record with the lowest Identifier.
                PreparedStatement selectControlRequestStatusCommand = conn.prepareStatement("Select ID, OrgId, EntityId, RequestId, OperatorId, Status, EntityType, LockStatus, OperatorLockId, Priority, LastRevisedDate, LastRevisedTime, LastRevisedOffset from ControlRequestStatus where EntityType = ? and (Status = \"3\" or Status = \"request queued/not implemented\") order by Priority, LastRevisedDate, LastRevisedTime, LastRevisedOffset asc");
                selectControlRequestStatusCommand.setString(1, entityType.name());
                rs = selectControlRequestStatusCommand.executeQuery();

                // Assuming you are working with a newly returned ResultSet whose cursor is pointing before the first row, an easier way to check this is to just call isBeforeFirst(). This avoids having to back-track if the data is to be read.
                // As explained in the documentation, this returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
                if (!rs.isBeforeFirst()) {
                    rs.close();
                    selectControlRequestStatusCommand.close();
                    throw new InvalidEntityControlRequestStatusException("No matching control requests were found.");
                } else {
                    String orgId = rs.getString("OrgId");
                    String entityId = rs.getString("EntityId");
                    String requestId = rs.getString("RequestId");
                    theResult = new EntityControlRequestStatusRecord(orgId, entityId, requestId, entityType.name());
                    theResult.setIdentifier(rs.getInt("ID"));
                    theResult.setOperatorId(rs.getString("OperatorId"));
                    theResult.setStatus(rs.getString("Status"));
                    theResult.setLockStatus(rs.getInt("LockStatus"));
                    theResult.setOperatorLockId(rs.getString("OperatorLockId"));
                    theResult.setPriority(rs.getInt("Priority"));
                    theResult.setLastRevisedDate(rs.getString("LastRevisedDate"));
                    theResult.setLastRevisedTime(rs.getString("LastRevisedTime"));
                    theResult.setLastRevisedOffset(rs.getString("LastRevisedOffset"));

                    rs.close();
                    selectControlRequestStatusCommand.close();
                }
                
            EntityEmulationRepository.releaseConnection(conn);
            // Per TMDD priority is a value selected from 1 to 10 where 1 is the highest and 10 is the lowest priority. 
            // 0 is used if the command request is not to be queued or there is no queue.
            if ((commandsQueued > recordThreshold)|| (theResult.getPriority()==0)){
                return theResult;
            } else {
                return null;
            }
            
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);        
        }
    }

    public static ArrayList<EntityControlRequestStatusRecord> getQueuedControlRequests(EntityType entityType, String deviceID) throws InvalidEntityControlRequestStatusException, EntityEmulationException {
        ArrayList<EntityControlRequestStatusRecord> results = null;

        try {

            // return the Control Request Status record with the lowest Identifier.
            Connection conn = EntityEmulationRepository.getConnection();
            PreparedStatement selectControlRequestStatusCommand = conn.prepareStatement("Select ID, OrgId, EntityId, RequestId, OperatorId, Status, EntityType, LockStatus, OperatorLockId, Priority, LastRevisedDate, LastRevisedTime, LastRevisedOffset from ControlRequestStatus where EntityType = ? and (Status = \"3\" or Status = \"request queued/not implemented\") and EntityId = ? order by ID asc");
            selectControlRequestStatusCommand.setString(1, entityType.name());
            selectControlRequestStatusCommand.setString(2, deviceID);
            System.out.println("EntityControlRequestStatus::getQueuedControlRequests: selectControlRequestStatusCommand = "+selectControlRequestStatusCommand.toString());
            ResultSet rs = selectControlRequestStatusCommand.executeQuery();

            // Assuming you are working with a newly returned ResultSet whose cursor is pointing before the first row, an easier way to check this is to just call isBeforeFirst(). This avoids having to back-track if the data is to be read.
            // As explained in the documentation, this returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            if (!rs.isBeforeFirst()) {
                rs.close();
                selectControlRequestStatusCommand.close();
                throw new InvalidEntityControlRequestStatusException("No matching control requests were found for device id "+deviceID+".");
            } else {

                results = new ArrayList<EntityControlRequestStatusRecord>();
                
                while (rs.next()) {
                    String orgId = rs.getString("OrgId");
                    String entityId = rs.getString("EntityId");
                    String requestId = rs.getString("RequestId");
                    EntityControlRequestStatusRecord theResult = new EntityControlRequestStatusRecord(orgId, entityId, requestId, entityType.name());
                    theResult.setIdentifier(rs.getInt("ID"));
                    theResult.setOperatorId(rs.getString("OperatorId"));
                    theResult.setStatus(rs.getString("Status"));
                    theResult.setLockStatus(rs.getInt("LockStatus"));
                    theResult.setPriority(rs.getInt("Priority"));
                    theResult.setOperatorLockId(rs.getString("OperatorLockId"));
                    theResult.setLastRevisedDate(rs.getString("LastRevisedDate"));
                    theResult.setLastRevisedTime(rs.getString("LastRevisedTime"));
                    theResult.setLastRevisedOffset(rs.getString("LastRevisedOffset"));
                    results.add(theResult);
                }
                rs.close();
                selectControlRequestStatusCommand.close();
            }
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);        
        }

        return results;

    }

    public static void updateControlRequestStatus(EntityControlRequestStatusRecord statusRecord, MessageSpecification requestMsg, boolean logNewRequest) throws EntityEmulationException {
        // Store the requestMsg in the ControlRequestLog
        try {
            // First check whether a matching record already exists.  
            Connection conn = EntityEmulationRepository.getConnection();
            PreparedStatement selectExistingRequestStatusCommand = conn.prepareStatement("Select ID from ControlRequestStatus where OrgId = ? and EntityId = ? and RequestId = ?");
            selectExistingRequestStatusCommand.setString(1, statusRecord.getOrgId());
            selectExistingRequestStatusCommand.setString(2, statusRecord.getEntityId());
            selectExistingRequestStatusCommand.setString(3, statusRecord.getRequestId());
            ResultSet requestStatusSet = selectExistingRequestStatusCommand.executeQuery();
            if (!requestStatusSet.isBeforeFirst()) {
                requestStatusSet.close();
                selectExistingRequestStatusCommand.close();

                // Create a new one                                
                PreparedStatement insertControlRequestStatusCommand = conn.prepareStatement("Insert into ControlRequestStatus (OrgId, EntityId, RequestId, OperatorId, Status, EntityType, LockStatus, OperatorLockId, LastRevisedDate, LastRevisedTime, LastRevisedOffset,Priority) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
                insertControlRequestStatusCommand.setString(1, (statusRecord.getOrgId()));
                insertControlRequestStatusCommand.setString(2, (statusRecord.getEntityId()));
                insertControlRequestStatusCommand.setString(3, (statusRecord.getRequestId()));
                insertControlRequestStatusCommand.setString(4, (statusRecord.getOperatorId() == null ? "" : statusRecord.getOperatorId()));
                insertControlRequestStatusCommand.setString(5, (statusRecord.getStatus()));
                insertControlRequestStatusCommand.setString(6, (statusRecord.getEntityType()));
                insertControlRequestStatusCommand.setInt(7, (statusRecord.getLockStatus()));
                insertControlRequestStatusCommand.setString(8, ((statusRecord.getOperatorLockId() == null ? "" : statusRecord.getOperatorLockId())));
                insertControlRequestStatusCommand.setString(9, (statusRecord.getLastRevisedDate()));
                insertControlRequestStatusCommand.setString(10, (statusRecord.getLastRevisedTime()));
                insertControlRequestStatusCommand.setString(11, (statusRecord.getLastRevisedOffset()));
                insertControlRequestStatusCommand.setInt(12, statusRecord.getPriority());
                insertControlRequestStatusCommand.execute();
                insertControlRequestStatusCommand.close();

            } else {
                requestStatusSet.close();
                selectExistingRequestStatusCommand.close();

                // If so update that one.
                PreparedStatement updateControlRequestStatusCommand = conn.prepareStatement("Update ControlRequestStatus Set OperatorId = ?, Status = ?, EntityType = ?, LockStatus = ?, OperatorLockId = ?, LastRevisedDate = ?, LastRevisedTime = ?, LastRevisedOffset = ?, Priority = ? where EntityId = ? and RequestId = ? and OrgId = ?");
                updateControlRequestStatusCommand.setString(1, (statusRecord.getOperatorId() == null ? "" : statusRecord.getOperatorId()));
                updateControlRequestStatusCommand.setString(2, (statusRecord.getStatus()));
                updateControlRequestStatusCommand.setString(3, (statusRecord.getEntityType()));
                updateControlRequestStatusCommand.setInt(4, (statusRecord.getLockStatus()));
                updateControlRequestStatusCommand.setString(5, ((statusRecord.getOperatorLockId() == null ? "" : statusRecord.getOperatorLockId())));
                updateControlRequestStatusCommand.setString(6, (statusRecord.getLastRevisedDate()));
                updateControlRequestStatusCommand.setString(7, (statusRecord.getLastRevisedTime()));
                updateControlRequestStatusCommand.setString(8, (statusRecord.getLastRevisedOffset()));
                updateControlRequestStatusCommand.setInt(9, (statusRecord.getPriority())); 
                updateControlRequestStatusCommand.setString(10, (statusRecord.getEntityId()));
                updateControlRequestStatusCommand.setString(11, (statusRecord.getRequestId()));
                updateControlRequestStatusCommand.setString(12, (statusRecord.getOrgId()));
                updateControlRequestStatusCommand.execute();

                updateControlRequestStatusCommand.close();
            }
            conn.commit();
            EntityEmulationRepository.releaseConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);        
        }

        statusRecord = getControlRequestStatus(statusRecord.getOrgId(), statusRecord.getEntityId(), statusRecord.getRequestId());
        if (logNewRequest) {
            EntityControlRequestLog.storeControlRequest(statusRecord.getIdentifier(), requestMsg);
        }

    }

}
