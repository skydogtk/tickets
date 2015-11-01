package br.unisinos.siead.ds3.ticket.dao;

import br.unisinos.siead.ds3.ticket.dto.TipoFalha;
import br.unisinos.siead.ds3.ticket.util.LogUtils;
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
public class TipoFalhaDAO {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    private final Connection con;

    public TipoFalhaDAO(Connection con) {
        this.con = con;

        LOGGER.debug("TipoFalhaDAO");
    }

    public TipoFalha findById(int id) throws SQLException {
        String sql = "SELECT * FROM tipo_falha WHERE id = ? ORDER BY id ASC;";
        TipoFalha tipoFalha;
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            tipoFalha = null;
            if (rs.next()) {
                tipoFalha = new TipoFalha();

                tipoFalha.setId(rs.getInt("id"));
                tipoFalha.setDesc(rs.getString("descricao"));
            }
        }
        return tipoFalha;
    }

    public List<TipoFalha> getAll() throws SQLException {
        LOGGER.debug("TipoFalhaDAO.getALl()");
        List<TipoFalha> falhas = new ArrayList<>();

        String sql = "SELECT * FROM tipo_falha ORDER BY id;";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                TipoFalha tipoFalha = new TipoFalha();
                tipoFalha.setId(rs.getInt("id"));
                tipoFalha.setDesc(rs.getString("descricao"));

                falhas.add(tipoFalha);
            }

            LOGGER.debug("Tipos de falha: " + falhas.size());
        }
        return falhas;
    }

}
