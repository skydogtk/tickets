package br.unisinos.siead.ds3.ticket.jdbc;

import java.sql.Connection;

import org.apache.logging.log4j.Logger;

import br.unisinos.siead.ds3.ticket.util.LogUtils;

public class DBConnectionFactory extends DBConnection implements ConnectionFactory {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    public DBConnectionFactory(ConnectionSettingsProvider csp) throws Exception {
        super(csp.getSettings());
    }

    @Override
    public Connection getConnection() throws Exception {
        LOGGER.debug("Criando nova conexão de banco de dados");
        return super.createConnection();
    }
}
