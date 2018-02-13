/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package derbyDB;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.TimeZone;

/**
 *
 * @author Elev
 */
public final class dbConnectorSingleton {

    private static final dbConnectorSingleton instance = new dbConnectorSingleton();

    private dbConnectorSingleton() {

        try {

            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/mastermind", "lexicon", "password");
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM LEXICON.Stats");
            System.out.println("successed");
            while (rs.next()) {
                String s = rs.getString("First_Name");
                String n = rs.getString("RESULTS");
                System.out.println(s + "   " + n);
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    public static dbConnectorSingleton getInstance() {
        return instance;
    }

    private static Connection getConnection() throws ClassNotFoundException, SQLException {

        String dbURL = "jdbc:derby://localhost:1527/mastermind";
//        String user = "tom";
//        String password = "secret";
//        Connection conn = DriverManager.getConnection(dbURL, user, password);

        //  String db = "mastermind";
        String user = "lexicon";
        String pw = "password";

        Connection conn = DriverManager.getConnection(dbURL, user, pw);
        return conn;

    }

    public int insert(String name, String time, String results) throws SQLException {
        Connection c = null;
      
        System.out.println("time is " + time);
        PreparedStatement ps = null;

        int recordCounter = 0;

        // dbConnectorSingleton.getInstance().insert("cai", java.sql.Time.valueOf(LocalTime.now()), "YOBG");
        try {
            c = dbConnectorSingleton.getConnection();
            ps = c.prepareStatement("insert into STATS(FIRST_NAME,RESULTS,TIMESPENT)values(?,?,?)");
            ps.setString(1, name);
            ps.setString(2, results);
            ps.setString(3, time);
            recordCounter = ps.executeUpdate();

        } catch (ClassNotFoundException | SQLException e) {
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

}
