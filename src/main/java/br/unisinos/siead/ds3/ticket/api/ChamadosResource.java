package br.unisinos.siead.ds3.ticket.api;

import br.unisinos.siead.ds3.ticket.annotation.Authenticated;
import br.unisinos.siead.ds3.ticket.dao.ChamadoDAO;
import br.unisinos.siead.ds3.ticket.dao.ChamadoHistoricoDAO;
import br.unisinos.siead.ds3.ticket.dao.TipoChamadoDAO;
import br.unisinos.siead.ds3.ticket.dao.TipoSituacaoDAO;
import br.unisinos.siead.ds3.ticket.dto.Chamado;
import br.unisinos.siead.ds3.ticket.dto.ChamadoHistorico;
import br.unisinos.siead.ds3.ticket.dto.TipoFalha;
import br.unisinos.siead.ds3.ticket.dto.Usuario;
import br.unisinos.siead.ds3.ticket.jdbc.DBUtil;
import br.unisinos.siead.ds3.ticket.util.LogUtils;
import com.sun.jersey.api.core.HttpContext;
import java.sql.Connection;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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

    @GET
    @Path("{id}")
    @RolesAllowed({"Analista", "Supervisor"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChamado(@PathParam("id") int idChamado) {
        Connection con = (Connection) context.getProperties().get("conexao");

        try {
            ChamadoDAO chamadoDAO = new ChamadoDAO(con);
            Chamado chamado = chamadoDAO.findById(idChamado);
            return Response
                    .status(Response.Status.OK)
                    .entity(chamado)
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
    @Path("atender/{id}")
    @RolesAllowed({"Analista"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response atenderChamado(@PathParam("id") int idChamado) {
        Connection con = (Connection) context.getProperties().get("conexao");

        try {
            JSONObject resposta = new JSONObject();

            Usuario usuario = (Usuario) context.getProperties().get("usuario");

            ChamadoDAO chamadoDAO = new ChamadoDAO(con);
            Chamado chamado = chamadoDAO.findById(idChamado);

            if (chamado.getTipoSituacao().getId() == 1) {
                chamado.setTipoSituacao(new TipoSituacaoDAO(con).findById(2));
                chamado.setUsuarioAtendimento(usuario);

                if (chamadoDAO.save(chamado)) {
                    ChamadoHistorico chamadoHistorico = new ChamadoHistorico();
                    chamadoHistorico.setChamado(chamado);
                    chamadoHistorico.setUsuario(usuario);
                    chamadoHistorico.setDesc("Atendimento iniciado");

                    ChamadoHistoricoDAO chamadoHistoricoDAO = new ChamadoHistoricoDAO(con);
                    chamadoHistoricoDAO.save(chamadoHistorico);
                }

                resposta.put("sucesso", true);
                resposta.put("mensagem", "Ok");
            } else {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "O chamado já está em atendimento.");

            }

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

    @PUT
    @Path("encerrar/{id}")
    @RolesAllowed({"Analista"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response encerrarChamado(@PathParam("id") int idChamado) {
        Connection con = (Connection) context.getProperties().get("conexao");

        try {
            JSONObject resposta = new JSONObject();

            Usuario usuario = (Usuario) context.getProperties().get("usuario");

            ChamadoDAO chamadoDAO = new ChamadoDAO(con);
            Chamado chamado = chamadoDAO.findById(idChamado);

            if (chamado.getTipoSituacao().getId() == 2) {
                chamado.setTipoSituacao(new TipoSituacaoDAO(con).findById(3));

                if (chamadoDAO.save(chamado)) {
                    ChamadoHistorico chamadoHistorico = new ChamadoHistorico();
                    chamadoHistorico.setChamado(chamado);
                    chamadoHistorico.setUsuario(usuario);
                    chamadoHistorico.setDesc("Atendimento encerrado");

                    ChamadoHistoricoDAO chamadoHistoricoDAO = new ChamadoHistoricoDAO(con);
                    chamadoHistoricoDAO.save(chamadoHistorico);
                }

                resposta.put("sucesso", true);
                resposta.put("mensagem", "Ok");
            } else {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "O chamado não está em atendimento.");

            }

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

    @PUT
    @Path("retomar/{id}")
    @RolesAllowed({"Supervisor"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response retomarChamado(@PathParam("id") int idChamado) {
        Connection con = (Connection) context.getProperties().get("conexao");

        try {
            JSONObject resposta = new JSONObject();

            Usuario usuario = (Usuario) context.getProperties().get("usuario");

            ChamadoDAO chamadoDAO = new ChamadoDAO(con);
            Chamado chamado = chamadoDAO.findById(idChamado);

            if (chamado.getTipoSituacao().getId() == 3) {
                chamado.setTipoSituacao(new TipoSituacaoDAO(con).findById(1));
                chamado.setUsuarioAtendimento(null);

                if (chamadoDAO.save(chamado)) {
                    ChamadoHistorico chamadoHistorico = new ChamadoHistorico();
                    chamadoHistorico.setChamado(chamado);
                    chamadoHistorico.setUsuario(usuario);
                    chamadoHistorico.setDesc("Chamado retomado. Aguarda atendimento.");

                    ChamadoHistoricoDAO chamadoHistoricoDAO = new ChamadoHistoricoDAO(con);
                    chamadoHistoricoDAO.save(chamadoHistorico);
                }

                resposta.put("sucesso", true);
                resposta.put("mensagem", "Ok");
            } else {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "O chamado não está encerrado.");

            }

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
