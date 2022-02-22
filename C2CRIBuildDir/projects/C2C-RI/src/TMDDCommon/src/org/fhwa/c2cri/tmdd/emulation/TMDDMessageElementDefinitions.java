/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.tmdd.emulation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @author TransCore ITS, LLC
 * Created: Feb 18, 2016
 */
public class TMDDMessageElementDefinitions {
    
    public static int lookupElementId(Connection connection, String entityElement) throws Exception {
        int schemaId = 0;

        try {

            // Remove any index indicators from the entityElement.
            String simplifiedElement = entityElement.replaceAll("\\[\\d+\\]", "").replace("[]", "").replace("[*]", "");
            String schemaIdCommand = "select schemaId, BaseType, minOccurs, maxOccurs, minLength, maxLength, minInclusive, maxInclusive, enumeration from TMDDMessageElementDefinitionQuery where (ShortPath = \"" + simplifiedElement + "\")";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(schemaIdCommand);

            if (!rs.isBeforeFirst()) {
                stmt.close();
                rs.close();
                throw new Exception("Unable to locate a defined element similar to " + entityElement);
            } else {
                schemaId = rs.getInt("schemaId");
                stmt.close();
                rs.close();
            }

        } catch (ClassNotFoundException ex) {
            throw new Exception(ex);
        } catch (SQLException ex) {
            throw new Exception(ex);
        }
        return schemaId;
    }

}
