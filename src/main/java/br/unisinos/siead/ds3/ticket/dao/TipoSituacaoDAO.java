package br.unisinos.siead.ds3.ticket.dao;

import br.unisinos.siead.ds3.ticket.dto.TipoFalha;
import br.unisinos.siead.ds3.ticket.dto.TipoSituacao;
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
public class TipoSituacaoDAO {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    private final Connection con;

    public TipoSituacaoDAO(Connection con) {
        this.con = con;

        LOGGER.debug("TipoSituacaoDAO");
    }

    public TipoSituacao findById(int id) throws SQLException {
        String sql = "SELECT * FROM tipo_situacao WHERE id = ? ORDER BY descricao ASC;";
        TipoSituacao tipoSituacao;
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            tipoSituacao = null;
            if (rs.next()) {
                tipoSituacao = new TipoSituacao();

                tipoSituacao.setId(rs.getInt("id"));
                tipoSituacao.setDesc(rs.getString("descricao"));
            }
        }
        return tipoSituacao;
    }

    public List<TipoSituacao> getAll() throws SQLException {
        LOGGER.debug("TipoSituacaoDAO.getALl()");
        List<TipoSituacao> situacoes = new ArrayList<>();

        String sql = "SELECT * FROM tipo_situacao ORDER BY descricao ASC;";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                TipoSituacao tipoSituacao = new TipoSituacao();
                tipoSituacao.setId(rs.getInt("id"));
                tipoSituacao.setDesc(rs.getString("descricao"));

                situacoes.add(tipoSituacao);
            }

            LOGGER.debug("Tipos de situações: " + situacoes.size());
        }
        return situacoes;
    }

}
