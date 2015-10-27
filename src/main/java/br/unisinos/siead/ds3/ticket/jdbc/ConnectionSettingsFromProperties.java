package br.unisinos.siead.ds3.ticket.jdbc;

import br.unisinos.siead.ds3.ticket.dto.ConnectionSettings;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import br.unisinos.siead.ds3.ticket.util.LogUtils;

public class ConnectionSettingsFromProperties implements ConnectionSettingsProvider {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    private File arquivo;

    public ConnectionSettingsFromProperties() throws Exception {
        this("db.properties");
    }

    public ConnectionSettingsFromProperties(String arquivo) throws Exception {
        this.arquivo = new File(arquivo);
        try {
            if (!this.arquivo.exists()) {
                LOGGER.error("Arquivo db.properties não existe");
                try (PrintWriter writer = new PrintWriter("db.properties", "UTF-8")) {
                    writer.println("! Propriedades de conexão com o banco de dados");
                }
                LOGGER.error("Criado no caminho: " + new File(arquivo).getAbsolutePath());
            }
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new Exception(ex);
        }

        LOGGER.debug(this.arquivo.getAbsolutePath());
    }

    @Override
    public ConnectionSettings getSettings() throws Exception {
        LOGGER.debug("Carregando dados de conexão a partir do arquivo: " + arquivo);

        Properties prop = new Properties();
        ConnectionSettings dados = new ConnectionSettings();

        try {
            InputStream input = new FileInputStream(arquivo);
            prop.load(input);

            dados.setDriver(prop.getProperty("driver"));
            dados.setSenha(prop.getProperty("senha"));
            dados.setUrl(prop.getProperty("url"));
            dados.setUsuario(prop.getProperty("usuario"));

            return dados;

        } catch (IOException ex) {
            LOGGER.error("Não foi possível carregar os dados de conexão: " + ex);
            throw new Exception(ex);
        }
    }
}
