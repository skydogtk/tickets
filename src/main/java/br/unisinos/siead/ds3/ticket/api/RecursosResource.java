package br.unisinos.siead.ds3.ticket.api;

import br.unisinos.siead.ds3.ticket.annotation.Authenticated;
import br.unisinos.siead.ds3.ticket.dao.RecursoDAO;
import br.unisinos.siead.ds3.ticket.dto.Recurso;
import br.unisinos.siead.ds3.ticket.dto.Usuario;
import br.unisinos.siead.ds3.ticket.jdbc.DBUtil;
import com.sun.jersey.api.core.HttpContext;
import java.sql.Connection;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author fcsilva
 */
@Authenticated
@Path("recursos")
public class RecursosResource {

    @Context
    private HttpContext context;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        Connection con = (Connection) context.getProperties().get("conexao");
        Usuario usuario = (Usuario) context.getProperties().get("usuario");

        try {
            RecursoDAO recursoDAO = new RecursoDAO(con);
            List<Recurso> recursos = recursoDAO.findByPapel(usuario.getPapel());
            return Response
                    .status(Response.Status.OK)
                    .entity(recursos.toArray(new Recurso[0]))
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
