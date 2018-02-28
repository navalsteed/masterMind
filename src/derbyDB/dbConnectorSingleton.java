/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package derbyDB;

import java.io.Console;
import java.net.InetAddress;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.derby.drda.NetworkServerControl;

/**
 *
 * @author Elev
 */
public final class dbConnectorSingleton {

    private static final dbConnectorSingleton instance = new dbConnectorSingleton();

    private dbConnectorSingleton() {

    }

    public static dbConnectorSingleton getInstance() {
        return instance;
    }

    /*
    *connect to derby database 
     */
    private static Connection getConnection() throws ClassNotFoundException, SQLException {
        NetworkServerControl server;
        try {
            server = new NetworkServerControl();
            server.start(null);
        } catch (Exception ex) {
            Logger.getLogger(dbConnectorSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }

        String driver = "org.apache.derby.jdbc.EmbeddedDriver";

        Class.forName(driver);
        String dbURL = "";
        String workingDir = System.getProperty("user.dir");
        System.out.println(workingDir);
        if (workingDir.contains("dist")) {
            dbURL = "jdbc:derby:../mastermind";
        } else {
            dbURL = "jdbc:derby:" + workingDir + "\\mastermind";
        }
        String user = "lexicon";
        String pw = "password";

        Connection conn = DriverManager.getConnection(dbURL, user, pw);
        System.out.println("succeed");
        return conn;
    }

    /**
     * Insert data into database if user break the code
     *
     * @param name beong to the player
     * @param time taken for the player to finish the
     * @param results that consisit four color peg with its initial letter
     * @throws java.sql.SQLException
     */
    public int insert(String name, String time, String results) throws SQLException {
        Connection c = null;
        System.out.println("time is " + time);
        PreparedStatement ps = null;

        int recordCounter = 0;
        try {
            c = dbConnectorSingleton.getConnection();
            ps = c.prepareStatement("insert into STATS(FIRST_NAME,RESULTS,TIMESPENT)values(?,?,?)");
            ps.setString(1, name.toUpperCase());
            ps.setString(2, results);
            ps.setTime(3, java.sql.Time.valueOf(time));
            recordCounter = ps.executeUpdate();

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (c != null) {
                c.close();
            }
        }
        return recordCounter;
    }

    /*
    *Slect best 10 players' names and their results from database
    *@ return 2d string array consists of name and time
     */
    public String[][] selectData() throws ClassNotFoundException {
        String[][] arrayResults = new String[10][10];
        arrayResults[0][0] = " Player Name";
        arrayResults[0][1] = " Best Time";
        try {
            Connection c = dbConnectorSingleton.getConnection();
            String selectSQL = "SELECT FIRST_NAME,min(TIMESPENT) from STATS Group by FIRST_NAME HAVING COUNT(First_Name)<11 ORDER by min(TIMESPENT)";
            PreparedStatement ps = c.prepareStatement(selectSQL);
            ResultSet rs = ps.executeQuery();
            int count = 1;
            while (rs.next()) {
                String name = rs.getString(1);
                Time time = rs.getTime(2);
                arrayResults[count][0] = " " + name;
                arrayResults[count][1] = " " + time;
                count++;
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return arrayResults;
    }

}
