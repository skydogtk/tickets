package br.unisinos.siead.ds3.ticket.dao;

import br.unisinos.siead.ds3.ticket.dto.ChamadoHistorico;
import br.unisinos.siead.ds3.ticket.util.LogUtils;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Fabrício
 */
public class ChamadoHistoricoDAO {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    private final Connection con;

    public ChamadoHistoricoDAO(Connection con) {
        this.con = con;

        LOGGER.debug("ChamadoHistoricoDAO");
    }

    public List<ChamadoHistorico> findByIdChamado(int idChamado) throws SQLException {
        LOGGER.debug("ChamadoHistoricoDAO.findByIdChamado()");
        List<ChamadoHistorico> historico = new ArrayList<>();

        String sql = "SELECT * FROM chamado_historico WHERE id_chamado = ? ORDER BY id DESC;";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idChamado);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                ChamadoHistorico chamadoHistorico = new ChamadoHistorico();
                chamadoHistorico.setId(rs.getInt("id"));
                chamadoHistorico.setChamado(new ChamadoDAO(con).findById(rs.getInt("id_chamado")));
                chamadoHistorico.setDatahora(rs.getTimestamp("datahora").getTime());
                chamadoHistorico.setDesc(rs.getString("descricao"));
                chamadoHistorico.setUsuario(new UsuarioDAO(con).findById(rs.getInt("id_usuario")));

                historico.add(chamadoHistorico);
            }

            LOGGER.debug("Histórico: " + historico.size());
        }
        return historico;
    }

    private boolean alter(ChamadoHistorico chamadoHistorico) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private boolean create(ChamadoHistorico chamadoHistorico) throws SQLException {
        LOGGER.debug("UsuarioDAO.create()");

        if (chamadoHistorico.getId() > 0) {
            throw new IllegalArgumentException("O id do histórico deve estar vazio");
        } else {
            DbSpec spec = new DbSpec();
            DbSchema schema = spec.addDefaultSchema();
            DbTable ch = schema.addTable("chamado_historico");

            String sql = new InsertQuery(ch)
                    .addPreparedColumns(ch.addColumn("id_chamado"))
                    .addPreparedColumns(ch.addColumn("id_usuario"))
                    .addPreparedColumns(ch.addColumn("descricao"))
                    .validate()
                    .toString();

            int cont;
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, chamadoHistorico.getChamado().getId());
                pst.setInt(2, chamadoHistorico.getUsuario().getId());
                pst.setString(3, chamadoHistorico.getDesc());
                cont = pst.executeUpdate();
            }

            return cont > 0;
        }
    }

    public boolean save(ChamadoHistorico chamadoHistorico) throws SQLException {
        LOGGER.debug("ChamadoHistoricoDAO.save()");
        if (chamadoHistorico.getId() > 0) {
            return alter(chamadoHistorico);
        } else {
            return create(chamadoHistorico);
        }
    }
}
