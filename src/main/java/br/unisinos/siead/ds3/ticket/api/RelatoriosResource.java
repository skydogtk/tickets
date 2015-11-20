package br.unisinos.siead.ds3.ticket.api;

import br.unisinos.siead.ds3.ticket.annotation.Authenticated;
import br.unisinos.siead.ds3.ticket.dto.Usuario;
import br.unisinos.siead.ds3.ticket.jdbc.DBUtil;
import br.unisinos.siead.ds3.ticket.type.TipoRelatorioEnum;
import br.unisinos.siead.ds3.ticket.util.LogUtils;
import com.sun.jersey.api.core.HttpContext;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.swing.JFrame;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author FabrÃ­cio
 */
@Authenticated
@Path("relatorios")
public class RelatoriosResource {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    @Context
    private HttpContext context;

    @GET
    @Path("{tipo}")
    @RolesAllowed({"Supervisor"})
    @Produces("application/pdf;charset=utf-8")
    public Response geraRelatorio(@PathParam("tipo") String tipoRelatorio) {
        TipoRelatorioEnum tipoRelatorioEnum = TipoRelatorioEnum.valueOf(tipoRelatorio);

        Connection con = (Connection) context.getProperties().get("conexao");
        Usuario usuario = (Usuario) context.getProperties().get("usuario");

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("usuario", usuario.getNome());

        JasperReport relatorio;
        JasperReport subReport;
        InputStream is;

        try {
            switch (tipoRelatorioEnum) {
                case A:
                    is = getClass().getClassLoader().getResourceAsStream("relatorios/atendimento_detalhe.jrxml");
                    subReport = JasperCompileManager.compileReport(is);
                    parametros.put("subreport", subReport);

                    is = getClass().getClassLoader().getResourceAsStream("relatorios/chamados_atendimento_por_analista.jrxml");
                    relatorio = JasperCompileManager.compileReport(is);
                    break;

                case S:
                    is = getClass().getClassLoader().getResourceAsStream("relatorios/chamados_por_situacao.jrxml");
                    relatorio = JasperCompileManager.compileReport(is);
                    break;

                case E:
                    is = getClass().getClassLoader().getResourceAsStream("relatorios/encerrado_detalhe.jrxml");
                    subReport = JasperCompileManager.compileReport(is);
                    parametros.put("subreport", subReport);

                    is = getClass().getClassLoader().getResourceAsStream("relatorios/chamados_encerrado_por_analista.jrxml");
                    relatorio = JasperCompileManager.compileReport(is);
//                    is = getClass().getClassLoader().getResourceAsStream("relatorios/chamados_encerrado_por_analista.jasper");
//                    relatorio = (JasperReport) JRLoader.loadObject(is);
                    break;

                default:
                    throw new AssertionError(tipoRelatorioEnum.name());
            }

            JasperPrint jasperPrint = JasperFillManager.fillReport(relatorio, parametros, con);
                       
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

            return Response
                    .status(Response.Status.OK)
                    .entity(baos.toByteArray())
                    .type("application/pdf")
                    .build();
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            DBUtil.fecha(con);
        }
    }

    @GET
    @RolesAllowed({"Supervisor"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTipoRelatorio() throws JSONException {
        JSONArray resposta = new JSONArray();

        for (TipoRelatorioEnum tipoRelatorioEnum : TipoRelatorioEnum.values()) {
            JSONObject obj = new JSONObject();
            obj.put("rotulo", tipoRelatorioEnum.getRotulo());
            obj.put("nome", tipoRelatorioEnum.toString());
            resposta.put(obj);
        }

        try {
            return Response
                    .status(Response.Status.OK)
                    .entity(resposta)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
