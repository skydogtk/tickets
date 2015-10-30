package br.unisinos.siead.ds3.ticket.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;

import br.unisinos.siead.ds3.ticket.dto.Papel;
import br.unisinos.siead.ds3.ticket.dto.Recurso;
import br.unisinos.siead.ds3.ticket.util.LogUtils;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.OrderObject;
import com.healthmarketscience.sqlbuilder.QueryPreparer;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import java.util.ArrayList;
import java.util.List;

public class RecursoDAO {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    private final Connection con;

    public RecursoDAO(Connection con) {
        this.con = con;

        LOGGER.debug("RecursoDAO");
    }

    public Recurso findById(int id) throws SQLException {
        String sql = "SELECT * FROM recurso WHERE id = ?;";
        Recurso recurso;
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            recurso = null;
            if (rs.next()) {
                recurso = new Recurso();
                
                recurso.setId(rs.getInt("id"));
                recurso.setAtivo(rs.getBoolean("ativo"));
                recurso.setReferencia(rs.getString("referencia"));
                recurso.setTitulo(rs.getString("titulo"));
            }
        }
        return recurso;
    }

    public List<Recurso> findByPapel(Papel papel) throws SQLException {
        LOGGER.debug("RecursoDAO.getALl()");
        List<Recurso> recursos = new ArrayList<>();

        DbSpec spec = new DbSpec();
        DbSchema schema = spec.addDefaultSchema();
        DbTable r = schema.addTable("recurso");
        DbTable rp = schema.addTable("recurso_papel");
        QueryPreparer.MultiPlaceHolder param = new QueryPreparer().getNewMultiPlaceHolder();

        String sql = new SelectQuery(true)
                .addAllTableColumns(r)
                .addFromTable(r)
                .addJoin(SelectQuery.JoinType.INNER, r, rp, r.addColumn("id"), rp.addColumn("id_recurso"))
                .addCondition(BinaryCondition.equalTo(r.addColumn("ativo"), true))
                .addCondition(BinaryCondition.equalTo(rp.addColumn("id_papel"), param))
                .addOrdering(r.addColumn("id"), OrderObject.Dir.ASCENDING)
                .validate()
                .toString();

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, papel.getId());
            
            LOGGER.debug("Consulta: \n" + pst);
            
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Recurso recurso = new Recurso();
                
                recurso.setId(rs.getInt("id"));
                recurso.setAtivo(rs.getBoolean("ativo"));
                recurso.setReferencia(rs.getString("referencia"));
                recurso.setTitulo(rs.getString("titulo"));
                
                recursos.add(recurso);
            }
            
            LOGGER.debug("Recursos: " + recursos.size());
        }
        return recursos;
    }

    public List<Recurso> getAll() throws SQLException {
        LOGGER.debug("RecursoDAO.getALl()");
        List<Recurso> recursos = new ArrayList<>();

        String sql = "SELECT * FROM recurso where ativo IS true;";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Recurso recurso = new Recurso();
                recurso.setId(rs.getInt("id"));
                recurso.setAtivo(rs.getBoolean("ativo"));
                recurso.setReferencia(rs.getString("referencia"));
                recurso.setTitulo(rs.getString("titulo"));
                
                recursos.add(recurso);
            }
            
            LOGGER.debug("Recursos: " + recursos.size());
        }
        return recursos;
    }
}