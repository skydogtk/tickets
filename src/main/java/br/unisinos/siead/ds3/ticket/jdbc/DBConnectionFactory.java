package br.unisinos.siead.ds3.ticket.jdbc;

import java.sql.Connection;

import org.apache.logging.log4j.Logger;

import br.unisinos.siead.ds3.ticket.util.LogUtils;
import java.sql.Statement;

public class DBConnectionFactory extends DBConnection implements ConnectionFactory {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    public DBConnectionFactory(ConnectionSettingsProvider csp) throws Exception {
        super(csp.getSettings());
    }

    @Override
    public Connection getConnection() throws Exception {
        LOGGER.debug("Criando nova conexão de banco de dados");
        Connection con = super.createConnection();
        String sql = "SET TIMEZONE TO 'America/Sao_Paulo';";
        Statement st = con.createStatement();
        st.execute(sql);
        return con;
    }
}
