package br.unisinos.siead.ds3.ticket.security.service;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;

import br.unisinos.siead.ds3.ticket.dao.UsuarioDAO;
import br.unisinos.siead.ds3.ticket.dto.Usuario;
import br.unisinos.siead.ds3.ticket.util.LogUtils;

/**
 *
 * @author fcsilva
 */
public class AuthenticationService {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    private final Connection con;

    public AuthenticationService(Connection con) {
        this.con = con;
    }

    public Usuario authenticate(String authCredentials) throws SQLException, UnsupportedEncodingException {
        if (null != authCredentials) {
            final String encodedUserPassword = authCredentials.replaceFirst("Basic" + " ", "");
            String usernameAndPassword = null;
            byte[] decodedBytes = Base64.decodeBase64(encodedUserPassword);
            usernameAndPassword = new String(decodedBytes, "UTF-8");

            final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
            final String email = tokenizer.nextToken();
            final String senha = tokenizer.nextToken();

            return this.authenticate(email, senha);

        }
        return null;
    }

    public Usuario authenticate(String email, String senha) throws SQLException {
        Usuario usuario;
        if (email != null) {
            UsuarioDAO usuarioDAO = new UsuarioDAO(con);
            usuario = usuarioDAO.findByEmail(email);
            if (usuario != null && usuario.getSenha().equals(senha) && usuario.isAtivo()) {
                return usuario;
            }
        }
        return null;
    }
}
