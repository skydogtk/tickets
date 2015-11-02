package br.unisinos.siead.ds3.ticket.api;

import br.unisinos.siead.ds3.ticket.annotation.Authenticated;
import br.unisinos.siead.ds3.ticket.dao.ChamadoDAO;
import br.unisinos.siead.ds3.ticket.dao.ChamadoHistoricoDAO;
import br.unisinos.siead.ds3.ticket.dto.ChamadoHistorico;
import br.unisinos.siead.ds3.ticket.dto.Usuario;
import br.unisinos.siead.ds3.ticket.jdbc.DBUtil;
import br.unisinos.siead.ds3.ticket.util.LogUtils;
import com.sun.jersey.api.core.HttpContext;
import java.sql.Connection;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author fcsilva
 */
@Authenticated
@Path("chamadohistorico")
public class ChamadoHistoricoResource {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    @Context
    private HttpContext context;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getHistorico(@PathParam("id") int idChamado) {
        Connection con = (Connection) context.getProperties().get("conexao");

        try {
            ChamadoHistoricoDAO chamadoHistoricoDAO = new ChamadoHistoricoDAO(con);
            List<ChamadoHistorico> historico = chamadoHistoricoDAO.findByIdChamado(idChamado);
            return Response
                    .status(Response.Status.OK)
                    .entity(historico.toArray(new ChamadoHistorico[0]))
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
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response addHistorico(@PathParam("id") int idChamado, JSONObject json) {
        Connection con = (Connection) context.getProperties().get("conexao");
        JSONObject resposta = new JSONObject();

        try {
            Usuario usuario = (Usuario) context.getProperties().get("usuario");
            usuario.setSenha("");

            ChamadoHistorico chamadoHistorico= new ChamadoHistorico();
            chamadoHistorico.setChamado(new ChamadoDAO(con).findById(idChamado));
            chamadoHistorico.setUsuario(usuario);
            chamadoHistorico.setDesc(json.getString("mensagem"));
            
            ChamadoHistoricoDAO chamadoHistoricoDAO = new ChamadoHistoricoDAO(con);
            
            resposta.put("sucesso", chamadoHistoricoDAO.save(chamadoHistorico));
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
}