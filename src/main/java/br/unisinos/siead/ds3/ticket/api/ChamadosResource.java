package br.unisinos.siead.ds3.ticket.api;

import br.unisinos.siead.ds3.ticket.annotation.Authenticated;
import br.unisinos.siead.ds3.ticket.dao.ChamadoDAO;
import br.unisinos.siead.ds3.ticket.dao.TipoChamadoDAO;
import br.unisinos.siead.ds3.ticket.dto.Chamado;
import br.unisinos.siead.ds3.ticket.dto.TipoFalha;
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
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Fabrício
 */
@Authenticated
@Path("chamados")
public class ChamadosResource {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    @Context
    private HttpContext context;

    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChamados() {
        Connection con = (Connection) context.getProperties().get("conexao");

        try {
            Usuario usuario = (Usuario) context.getProperties().get("usuario");
            ChamadoDAO chamadoDAO = new ChamadoDAO(con);
            List<Chamado> chamados = chamadoDAO.findByUsuario(usuario);
            return Response
                    .status(Response.Status.OK)
                    .entity(chamados.toArray(new Chamado[0]))
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
    @PermitAll
    public Response novoChamado(JSONObject json) {
        Connection con = (Connection) context.getProperties().get("conexao");
        JSONObject resposta = new JSONObject();

        try {
            Usuario usuario = (Usuario) context.getProperties().get("usuario");
            TipoFalha tipoFalha = new TipoFalha();
            try {
                tipoFalha.setId(json.getJSONObject("tipoFalha").getInt("id"));
            } catch (JSONException ex) {
                
            }
            
            Chamado chamado = new Chamado();
            chamado.setAssunto(json.getString("assunto"));
            chamado.setDescricao(json.getString("descricao"));
            chamado.setTipoChamado(new TipoChamadoDAO(con).findByDescricao(json.getString("tipoChamado")));
            chamado.setUsuarioAutor(usuario);
            chamado.setTipoFalha(tipoFalha);

            ChamadoDAO chamadoDAO = new ChamadoDAO(con);

            resposta.put("sucesso", chamadoDAO.save(chamado));
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
