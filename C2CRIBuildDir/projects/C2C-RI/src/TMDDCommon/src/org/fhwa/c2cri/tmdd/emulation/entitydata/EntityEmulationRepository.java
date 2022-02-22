/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.entitydata;

import java.sql.Connection;
import java.sql.SQLException;
import org.fhwa.c2cri.tmdd.dbase.TMDDConnectionPool;

/**
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class EntityEmulationRepository {


    public static Connection getConnection() throws ClassNotFoundException, SQLException, Exception {
        return TMDDConnectionPool.getConnection();

    }

    public static void releaseConnection(Connection conn) throws SQLException {
        TMDDConnectionPool.releaseConnection(conn);

    }
}
