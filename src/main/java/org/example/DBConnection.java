package org.example;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public Connection getDBConnection(){
        try {
            Dotenv dotenv = Dotenv.load();
            String JDBC_URL_ENV = dotenv.get("JDBC_URL_ENV");
            String USERNAME_ENV = dotenv.get("USERNAME_ENV");
            String PASSWORD_ENV = dotenv.get("PASSWORD_ENV");

            return DriverManager.getConnection(JDBC_URL_ENV, USERNAME_ENV, PASSWORD_ENV);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    };
    public void   closeDBConnection(Connection con){
        try {
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


  }