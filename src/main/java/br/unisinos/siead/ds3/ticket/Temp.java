package br.unisinos.siead.ds3.ticket;

import java.sql.SQLException;
import java.util.TimeZone;

public class Temp {

    public static void main(String args[]) throws SQLException {
        System.out.println(System.getenv("TICKET_DB_VENDOR"));
//        TimeZone tz = TimeZone.getDefault();
        TimeZone tz = TimeZone.getTimeZone("America/Sao_Paulo");
        System.out.println(tz);

        /*
         String HOST = System.getenv("OPENSHIFT_POSTGRESQL_DB_HOST");
         String PORT = System.getenv("OPENSHIFT_POSTGRESQL_DB_PORT");
         String USERNAME = System.getenv("OPENSHIFT_POSTGRESQL_DB_USERNAME");
         String PASSWORD = System.getenv("OPENSHIFT_POSTGRESQL_DB_PASSWORD");
         String DB_NAME = System.getenv("OPENSHIFT_APP_NAME");
         String FORNAME_URL = "org.postgresql.Driver";
         String URL = "jdbc://" + HOST + ":" + PORT + "/" + DB_NAME;
         Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
         */
    }
}
