package br.unisinos.siead.ds3.ticket.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;

import br.unisinos.siead.ds3.ticket.dto.Papel;
import br.unisinos.siead.ds3.ticket.util.LogUtils;
import java.util.ArrayList;
import java.util.List;

public class PapelDAO {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    private final Connection con;

    public PapelDAO(Connection con) {
        this.con = con;

        LOGGER.debug("PapelDAO");
    }

    public Papel findById(int id) throws SQLException {
        String sql = "SELECT * FROM papel WHERE id = ?;";
        Papel papel;
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            papel = null;
            if (rs.next()) {
                papel = new Papel();
                
                papel.setId(rs.getInt("id"));
                papel.setDesc(rs.getString("descricao"));
            }
        }
        return papel;
    }

    public List<Papel> getAll() throws SQLException {
        LOGGER.debug("PapelDAO.getALl()");
        List<Papel> papeis = new ArrayList<>();

        String sql = "SELECT * FROM papel;";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Papel papel = new Papel();
                papel.setId(rs.getInt("id"));
                papel.setDesc(rs.getString("descricao"));
                
                papeis.add(papel);
            }
            
            LOGGER.debug("Papéis: " + papeis.size());
        }
        return papeis;
    }
}
