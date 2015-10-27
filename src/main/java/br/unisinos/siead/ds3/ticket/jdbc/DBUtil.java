package br.unisinos.siead.ds3.ticket.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;

import br.unisinos.siead.ds3.ticket.util.LogUtils;

public class DBUtil {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    public static void fecha(Connection connection) {
        LOGGER.debug("Fechando conexão");
        if (connection != null) {
            try {
                connection.close();

            } catch (SQLException ex) {
                LOGGER.error("Erro fechando conex�o: " + ex);
            }
            connection = null;
        }
    }
}
