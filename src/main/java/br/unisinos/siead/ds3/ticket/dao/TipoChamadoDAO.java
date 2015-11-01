package br.unisinos.siead.ds3.ticket.dao;

import br.unisinos.siead.ds3.ticket.dto.TipoChamado;
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
public class TipoChamadoDAO {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    private final Connection con;

    public TipoChamadoDAO(Connection con) {
        this.con = con;

        LOGGER.debug("TipoChamadoDAO");
    }

    public TipoChamado findById(int id) throws SQLException {
        String sql = "SELECT * FROM tipo_chamado WHERE id = ? ORDER BY id ASC;";
        TipoChamado tipoChamado;
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            tipoChamado = null;
            if (rs.next()) {
                tipoChamado = new TipoChamado();

                tipoChamado.setId(rs.getInt("id"));
                tipoChamado.setDesc(rs.getString("descricao"));
            }
        }
        return tipoChamado;
    }

    public List<TipoChamado> getAll() throws SQLException {
        LOGGER.debug("TipoChamadoDAO.getALl()");
        List<TipoChamado> chamados = new ArrayList<>();

        String sql = "SELECT * FROM tipo_chamado ORDER BY id;";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                TipoChamado tipoChamado = new TipoChamado();
                tipoChamado.setId(rs.getInt("id"));
                tipoChamado.setDesc(rs.getString("descricao"));

                chamados.add(tipoChamado);
            }

            LOGGER.debug("Tipos de chamado: " + chamados.size());
        }
        return chamados;
    }
}
