package br.unisinos.siead.ds3.ticket.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.unisinos.siead.ds3.ticket.annotation.Authenticated;
import br.unisinos.siead.ds3.ticket.dao.PapelDAO;
import br.unisinos.siead.ds3.ticket.dao.UsuarioDAO;
import br.unisinos.siead.ds3.ticket.dto.Usuario;
import br.unisinos.siead.ds3.ticket.jdbc.DBUtil;
import br.unisinos.siead.ds3.ticket.util.LogUtils;
import com.sun.jersey.api.core.HttpContext;
import java.sql.Connection;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author fcsilva
 */
@Authenticated
@Path("usuarios")
public class UsuariosResource {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    @Context
    private HttpContext context;

    @GET
    @Path("eu")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response listaUsuarioAutenticado() {
        Connection con = (Connection) context.getProperties().get("conexao");
        try {
            Usuario usuario = (Usuario) context.getProperties().get("usuario");
            usuario.setSenha("");
            return Response
                    .status(Response.Status.OK)
                    .entity(usuario)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            DBUtil.fecha(con);
        }
    }

    @POST
    @Path("eu")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response alteraUsuarioAutenticado(JSONObject json) {
        Connection con = (Connection) context.getProperties().get("conexao");
        JSONObject resposta = new JSONObject();

        try {
            Usuario usuario = (Usuario) context.getProperties().get("usuario");

            if (usuario.getSenha().equals(json.getString("anterior"))) {
                UsuarioDAO usuarioDAO = new UsuarioDAO(con);
                usuario.setSenha(json.getString("nova"));
                if (usuarioDAO.save(usuario)) {
                    resposta.put("sucesso", true);
                    resposta.put("mensagem", "Senha alterada.");
                } else {
                    resposta.put("sucesso", false);
                    resposta.put("mensagem", "Não foi possível alterar.");
                }
            } else {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "A senha não confere.");
            }

            return Response
                    .status(Response.Status.OK)
                    .entity(resposta)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            DBUtil.fecha(con);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Supervisor"})
    public Response listaUsuarios() {
        Connection con = (Connection) context.getProperties().get("conexao");

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO(con);
            List<Usuario> usuarios = usuarioDAO.getAll();
            return Response
                    .status(Response.Status.OK)
                    .entity(usuarios.toArray(new Usuario[0]))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            DBUtil.fecha(con);
        }

    }

    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Supervisor"})
    public Response alteraUsuario(@PathParam("id") int id, JSONObject json) {
        Connection con = (Connection) context.getProperties().get("conexao");
        JSONObject resposta = new JSONObject();

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO(con);
            Usuario usuario = usuarioDAO.findById(id);
            usuario.setAtivo(json.getBoolean("ativo"));
            usuario.setEmail(json.getString("email"));
            usuario.setNome(json.getString("nome"));
            usuario.setPapel(new PapelDAO(con).findById(json.getJSONObject("papel").getInt("id")));
            if (!StringUtils.isEmpty(json.getString("senha"))) {
                usuario.setSenha(json.getString("senha"));
            }

            resposta.put("sucesso", usuarioDAO.save(usuario));
            resposta.put("mensagem", "Ok");

            return Response
                    .status(Response.Status.OK)
                    .entity(resposta.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            DBUtil.fecha(con);
        }

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Supervisor"})
    public Response novoUsuario(JSONObject json) {
        Connection con = (Connection) context.getProperties().get("conexao");
        JSONObject resposta = new JSONObject();

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO(con);
            Usuario usuario = new Usuario();
            usuario.setAtivo(json.getBoolean("ativo"));
            usuario.setEmail(json.getString("email"));
            usuario.setNome(json.getString("nome"));
            usuario.setPapel(new PapelDAO(con).findById(json.getJSONObject("papel").getInt("id")));
            usuario.setSenha(json.getString("senha"));

            resposta.put("sucesso", usuarioDAO.save(usuario));
            resposta.put("mensagem", "Ok");

            return Response
                    .status(Response.Status.OK)
                    .entity(resposta.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            DBUtil.fecha(con);
        }
    }

    @GET
    @Path("atendentes")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Supervisor"})
    public Response listaUsuarioAtendente() {
        Connection con = (Connection) context.getProperties().get("conexao");
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO(con);
            List<Usuario> usuarios = usuarioDAO.findAtendente();
            return Response
                    .status(Response.Status.OK)
                    .entity(usuarios.toArray(new Usuario[0]))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            DBUtil.fecha(con);
        }
    }
}
