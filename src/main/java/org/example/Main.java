package org.example;

import org.example.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        DBConnection dbConnection = new DBConnection();

        try {
            Connection conn = dbConnection.getDBConnection();
            System.out.println("Connexion établie avec succès!");


            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT version()");

            if (rs.next()) {
                System.out.println("Version PostgreSQL: " + rs.getString(1));
            }

            // Fermer les ressources
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}