package br.unisinos.siead.ds3.ticket.jdbc;

import br.unisinos.siead.ds3.ticket.dto.ConnectionSettings;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;

import br.unisinos.siead.ds3.ticket.util.LogUtils;

public abstract class DBConnection {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    private String url, usuario, senha;

    public DBConnection(ConnectionSettings cs) throws Exception {

        LOGGER.debug("Iniciando connection factory");
        LOGGER.debug(cs.toString());

        this.url = cs.getUrl();
        this.usuario = cs.getUsuario();
        this.senha = cs.getSenha();

        try {
            Class.forName(cs.getDriver());
        } catch (ClassNotFoundException ex) {
            LOGGER.error("Erro no connection factory: " + ex.getMessage());
            throw new Exception(ex);
        }
    }

    protected Connection createConnection() throws Exception {
        LOGGER.debug("Criando conexão");
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException ex) {
            LOGGER.error("Erro na conexão: " + ex.getMessage());
            throw new Exception(ex);
        }

        return con;
    }
}