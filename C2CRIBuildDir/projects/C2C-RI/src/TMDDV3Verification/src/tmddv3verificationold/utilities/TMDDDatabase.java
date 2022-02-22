/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmddv3verificationold.utilities;

import java.sql.*;

/**
 *
 * @author Transcore ITS
 */
public class TMDDDatabase {

    Connection tmddConnection;

    public TMDDDatabase() {
    }

    public static void main(String[] args) {
        TMDDDatabase thisDatabase = new TMDDDatabase();

        try {
            thisDatabase.connectToDatabase();
            thisDatabase.query("Select distinct Procedure from TestProcedureStepsTable");

            thisDatabase.queryNoResult("INSERT INTO TestProcedureStepsTable ([Procedure],StepNumber,"
                    + "Step,[Action],Result,Requirements,PassFail) VALUES ('INSERTEDPROCEDURE','22','OneTwoStep',"
                    + "'STEP ACTION', 'STEPRESULT','STEPREQUIREMENTS','Passed')");
            thisDatabase.queryNoResult("INSERT INTO TestProcedureStepsTable ([Procedure],StepNumber,"
                    + "Step,[Action],Result,Requirements,PassFail) VALUES ('INSERTEDPROCEDURE2','22','OneTwoStep',"
                    + "'STEP ACTION', 'STEPRESULT','STEPREQUIREMENTS','Passed')");

            thisDatabase.queryNoResult("DELETE FROM TestProcedureStepsTable WHERE Procedure = 'INSERTEDPROCEDURE'");
            thisDatabase.disconnectFromDatabase();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void query(String strQry) {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // SQL query command
            String SQL = strQry;
            stmt = tmddConnection.createStatement();
            rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                System.out.println(rs.getString("Procedure"));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public ResultSet queryReturnRS(String strQry) {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // SQL query command
            String SQL = strQry;
            stmt = tmddConnection.createStatement();
            rs = stmt.executeQuery(SQL);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Problem with Query:"+strQry);
            
        }
        return rs;
    }

    public void queryNoResult(String strQry) {
        Statement stmt = null;
        try {
            // SQL query command
            String SQL = strQry;
            stmt = tmddConnection.createStatement();
            stmt.executeUpdate(SQL);


        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Problem with Query:"+strQry);
        }

    }

    public Connection connect() {
        Connection con = null;
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            String url = "jdbc:odbc:Driver={Microsoft Access Driver "
                    + "(*.mdb, *.accdb)};DBQ=C:\\Projects\\RI\\InitialDeployment\\TMDD\\TMDDv303bpsr\\Tmddv303b.accdb";
            con = DriverManager.getConnection(url);
            System.out.println("Connected!");

        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (ClassNotFoundException cE) {
            System.out.println("Class Not Found Exception: "
                    + cE.toString());
        }
        return con;
    }

    public void connectToDatabase() {
        Connection con = null;
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            String url = "jdbc:odbc:Driver={Microsoft Access Driver "
                    + "(*.mdb, *.accdb)};DBQ=C:\\Projects\\RI\\InitialDeployment\\TMDD\\TMDDv303bpsr\\Tmddv303b.accdb";
            con = DriverManager.getConnection(url);
            System.out.println("Connected!");


            tmddConnection = con;
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (ClassNotFoundException cE) {
            System.out.println("Class Not Found Exception: "
                    + cE.toString());
        }
    }

    public void connectToDatabase(String databaseFile) {
        Connection con = null;
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            String url = "jdbc:odbc:Driver={Microsoft Access Driver "
                    + "(*.mdb, *.accdb)};DBQ="+databaseFile;
            con = DriverManager.getConnection(url);
            System.out.println("Connected!");

            tmddConnection = con;
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (ClassNotFoundException cE) {
            System.out.println("Class Not Found Exception: "
                    + cE.toString());
        }
    }

    public void disconnectFromDatabase() {
        try {
            if (tmddConnection != null) {
                tmddConnection.close();
                System.out.println("Disconnected");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void setTmddConnection(Connection tmddConnection) {
        this.tmddConnection = tmddConnection;
    }
}
