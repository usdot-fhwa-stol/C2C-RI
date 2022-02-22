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
import java.util.ArrayList;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.infolayer.MessageSpecificationItem;
import org.fhwa.c2cri.tmdd.emulation.TMDDMessageElementDefinitions;

/**
 *
 * @author TransCore ITS, LLC Created: Feb 18, 2016
 */
public class EntityControlRequestLog {

    public static void initialize() throws EntityEmulationException {
        try {
            Connection conn = EntityEmulationRepository.getConnection();
            PreparedStatement clearControlRequestStatusCommand = conn.prepareStatement("delete from ControlRequestLog");
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

    public static void storeControlRequest(int controlRequestId, MessageSpecification requestMsg) throws EntityEmulationException {
        try {
            int sequenceIndex = 1;

            Connection conn = EntityEmulationRepository.getConnection();
            PreparedStatement getLastSequenceIndexCommand = conn.prepareStatement("Select max(SequenceIndex) from ControlRequestLog where ControlRequestStatusId = ?");
            getLastSequenceIndexCommand.setInt(1, controlRequestId);
            ResultSet rs = getLastSequenceIndexCommand.executeQuery();

            // Assuming you are working with a newly returned ResultSet whose cursor is pointing before the first row, an easier way to check this is to just call isBeforeFirst(). This avoids having to back-track if the data is to be read.
            // As explained in the documentation, this returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            if (!rs.isBeforeFirst()) {
                rs.close();
                getLastSequenceIndexCommand.close();
            } else {
                sequenceIndex = rs.getInt(1) + 1;
                rs.close();
                getLastSequenceIndexCommand.close();
            }

            PreparedStatement insertControlRequestLogCommand = conn.prepareStatement("Insert into ControlRequestLog (ControlRequestStatusId, SequenceIndex, ElementName, ElementValue, SchemaId) VALUES (?,?,?,?,?)");
            for (MessageSpecificationItem thisItem : requestMsg.getMessageSpecItems()) {
                insertControlRequestLogCommand.setInt(1, controlRequestId);
                insertControlRequestLogCommand.setInt(2, sequenceIndex);
                insertControlRequestLogCommand.setString(3, thisItem.getValueName());
                insertControlRequestLogCommand.setString(4, thisItem.getValue());

                int schemaId;
                try {
                    schemaId = TMDDMessageElementDefinitions.lookupElementId(conn, thisItem.getValueName());
                } catch (Exception ex) {
                    schemaId = 0;
                }
                insertControlRequestLogCommand.setInt(5, schemaId);
                insertControlRequestLogCommand.addBatch();
            }
            insertControlRequestLogCommand.executeBatch();
            insertControlRequestLogCommand.close();

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

    public static MessageSpecification retrieveControlRequest(int controlRequestId) throws EntityEmulationException {
        MessageSpecification commandMessage = null;

        try {
            int sequenceIndex = 0;

            Connection conn = EntityEmulationRepository.getConnection();
            PreparedStatement getLastSequenceIndexCommand = conn.prepareStatement("Select max(SequenceIndex) from ControlRequestLog where ControlRequestStatusId = ?");
            getLastSequenceIndexCommand.setInt(1, controlRequestId);
            System.out.println("EntityControlRequestLog::retrieveControlRequest: getLastSequenceIndexCommand = "+getLastSequenceIndexCommand.toString());
            ResultSet rs = getLastSequenceIndexCommand.executeQuery();

            // Assuming you are working with a newly returned ResultSet whose cursor is pointing before the first row, an easier way to check this is to just call isBeforeFirst(). This avoids having to back-track if the data is to be read.
            // As explained in the documentation, this returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            if (!rs.isBeforeFirst()) {
                rs.close();
                getLastSequenceIndexCommand.close();
            } else {
                sequenceIndex = rs.getInt(1);
                rs.close();
                getLastSequenceIndexCommand.close();
            }

            PreparedStatement selectControlRequestLogCommand = conn.prepareStatement("Select ControlRequestStatusId, SequenceIndex, ElementName, ElementValue, SchemaId from ControlRequestLog where  ControlRequestStatusId = ? and SequenceIndex = ? order by ID ASC");
            selectControlRequestLogCommand.setInt(1, controlRequestId);
            selectControlRequestLogCommand.setInt(2, sequenceIndex);
            System.out.println("EntityControlRequestLog::retrieveControlRequest: selectControlRequestLogCommand = "+selectControlRequestLogCommand.toString());
            rs = selectControlRequestLogCommand.executeQuery();
            if (!rs.isBeforeFirst()) {
                rs.close();
                selectControlRequestLogCommand.close();
            } else {
                ArrayList<String> messageSpecText = new ArrayList();
                while (rs.next()) {
                    messageSpecText.add(rs.getString(3) + "=" + rs.getString(4));
                }
                System.out.println("EntityControlRequestLog::retrieveControlRequest:  messageSpecText Size = "+messageSpecText.size());                
                commandMessage = new MessageSpecification(messageSpecText);
                rs.close();
                selectControlRequestLogCommand.close();
            }
            EntityEmulationRepository.releaseConnection(conn);

        } catch (ClassNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (SQLException ex) {
            throw new EntityEmulationException(ex);
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        }

        if (commandMessage == null) throw new EntityEmulationException("No Matching Control Requests were found.");
        return commandMessage;

    }

}
