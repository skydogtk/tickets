package br.unisinos.siead.ds3.ticket.api;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;

import br.unisinos.siead.ds3.ticket.dto.Usuario;
import br.unisinos.siead.ds3.ticket.jdbc.DBUtil;
import br.unisinos.siead.ds3.ticket.security.service.AuthenticationService;
import br.unisinos.siead.ds3.ticket.util.LogUtils;
import com.sun.jersey.api.core.HttpContext;
import java.sql.Connection;
import javax.ws.rs.core.Context;

@PermitAll
@Path("auth")
public class AuthResource {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    @Context
    private HttpContext context;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(JSONObject json) {
        Connection con = (Connection) context.getProperties().get("conexao");

        String email = json.optString("email");
        String senha = json.optString("senha");

        try {
            JSONObject resposta = new JSONObject();

            Usuario usuario = new AuthenticationService(con).authenticate(email, senha);
            if (usuario != null) {
                JSONObject papel = new JSONObject();
                papel.put("id", usuario.getPapel().getId());
                papel.put("desc", usuario.getPapel().getDesc());
                
                resposta.put("sucesso", true);
                resposta.put("mensagem", "Ok");
                resposta.put("nome", usuario.getNome());
                resposta.put("papel", papel);
            } else {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "Usuário ou senha incorretos");
            }
            
            return Response
                    .status(Response.Status.OK)
                    .entity(resposta.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            DBUtil.fecha(con);
        }
    }
}