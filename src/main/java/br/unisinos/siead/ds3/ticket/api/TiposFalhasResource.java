package br.unisinos.siead.ds3.ticket.api;

import br.unisinos.siead.ds3.ticket.annotation.Authenticated;
import br.unisinos.siead.ds3.ticket.dao.TipoFalhaDAO;
import br.unisinos.siead.ds3.ticket.dto.TipoFalha;
import br.unisinos.siead.ds3.ticket.jdbc.DBUtil;
import br.unisinos.siead.ds3.ticket.util.LogUtils;
import com.sun.jersey.api.core.HttpContext;
import java.sql.Connection;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author fcsilva
 */
@Authenticated
@Path("tiposfalhas")
public class TiposFalhasResource {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    @Context
    private HttpContext context;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getTiposFalhas() {
        Connection con = (Connection) context.getProperties().get("conexao");

        try {
            TipoFalhaDAO tipoFalhaDAO = new TipoFalhaDAO(con);
            List<TipoFalha> falhas = tipoFalhaDAO.getAll();
            return Response
                    .status(Response.Status.OK)
                    .entity(falhas.toArray(new TipoFalha[0]))
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
