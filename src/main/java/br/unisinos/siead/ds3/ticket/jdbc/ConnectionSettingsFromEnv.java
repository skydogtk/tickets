package br.unisinos.siead.ds3.ticket.jdbc;

import br.unisinos.siead.ds3.ticket.dto.ConnectionSettings;

import org.apache.logging.log4j.Logger;

import br.unisinos.siead.ds3.ticket.util.LogUtils;

public class ConnectionSettingsFromEnv implements ConnectionSettingsProvider {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    private final DBType dbType;

    public ConnectionSettingsFromEnv() throws Exception {
        this.dbType = DBType.valueOf(System.getenv("TICKET_DB_VENDOR"));
    }

    @Override
    public ConnectionSettings getSettings() throws Exception {
        ConnectionSettings dados = new ConnectionSettings();
        switch (dbType) {
            case POSTGRESQL:
                String HOST = System.getenv("OPENSHIFT_POSTGRESQL_DB_HOST");
                String PORT = System.getenv("OPENSHIFT_POSTGRESQL_DB_PORT");
                String USUARIO = System.getenv("OPENSHIFT_POSTGRESQL_DB_USERNAME");
                String SENHA = System.getenv("OPENSHIFT_POSTGRESQL_DB_PASSWORD");
                String DB_NAME = System.getenv("OPENSHIFT_APP_NAME");
                String DRIVER = "org.postgresql.Driver";
                String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME;

                dados.setDriver(DRIVER);
                dados.setSenha(SENHA);
                dados.setUrl(URL);
                dados.setUsuario(USUARIO);
                break;
        }
        return dados;
    }
}
