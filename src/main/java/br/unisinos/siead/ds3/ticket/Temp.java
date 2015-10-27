package br.unisinos.siead.ds3.ticket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Temp {

    public static void main(String args[]) throws SQLException {
        String HOST = System.getenv("OPENSHIFT_POSTGRESQL_DB_HOST");
        String PORT = System.getenv("OPENSHIFT_POSTGRESQL_DB_PORT");
        String USERNAME = System.getenv("OPENSHIFT_POSTGRESQL_DB_USERNAME");
        String PASSWORD = System.getenv("OPENSHIFT_POSTGRESQL_DB_PASSWORD");
        String DB_NAME = System.getenv("OPENSHIFT_APP_NAME");
        String FORNAME_URL = "org.postgresql.Driver";
        String URL = "jdbc://" + HOST + ":" + PORT + "/" + DB_NAME;
        Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        
    }
}
