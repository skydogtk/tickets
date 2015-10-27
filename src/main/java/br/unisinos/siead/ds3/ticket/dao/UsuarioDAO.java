package br.unisinos.siead.ds3.ticket.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;

import br.unisinos.siead.ds3.ticket.dto.Usuario;
import br.unisinos.siead.ds3.ticket.util.LogUtils;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.QueryPreparer;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class UsuarioDAO {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    private final Connection con;

    public UsuarioDAO(Connection con) {
        this.con = con;

        LOGGER.debug("UsuarioDAO");
    }

    public Usuario findById(int id) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id = ?;";
        Usuario usuario;
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            usuario = null;
            if (rs.next()) {
                usuario = new Usuario();
                
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setPapel(new PapelDAO(con).findById(rs.getInt("id_papel")));
                usuario.setAtivo(rs.getBoolean("ativo"));
            }
        }
        return usuario;
    }

    public Usuario findByEmail(String email) throws SQLException {
        LOGGER.debug("UsuarioDAO.findByEmail(" + email + ")");

        String sql = "SELECT * FROM usuario WHERE email = ?;";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, email);
        ResultSet rs = pst.executeQuery();

        Usuario usuario = null;

        if (rs.next()) {
            usuario = new Usuario();

            usuario.setId(rs.getInt("id"));
            usuario.setNome(rs.getString("nome"));
            usuario.setEmail(rs.getString("email"));
            usuario.setSenha(rs.getString("senha"));
            usuario.setPapel(new PapelDAO(con).findById(rs.getInt("id_papel")));
            usuario.setAtivo(rs.getBoolean("ativo"));
        }

        pst.close();
        return usuario;
    }

    public List<Usuario> getAll() throws SQLException {
        LOGGER.debug("UsuarioDAO.getAll()");
        List<Usuario> usuarios = new ArrayList<Usuario>();

        String sql = "SELECT * FROM usuario;";
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            Usuario usuario = new Usuario();
            usuario.setId(rs.getInt("id"));
            usuario.setNome(rs.getString("nome"));
            usuario.setEmail(rs.getString("email"));
//            usuario.setSenha(rs.getString("senha"));
            usuario.setPapel(new PapelDAO(con).findById(rs.getInt("id_papel")));
            usuario.setAtivo(rs.getBoolean("ativo"));

            usuarios.add(usuario);
        }

        LOGGER.debug("Usuarios: " + usuarios.size());
        pst.close();
        return usuarios;
    }

    public boolean alter(Usuario usuario) throws SQLException {
        LOGGER.debug("UsuarioDAO.alter()");

        if (usuario.getId() > 0) {
            DbSpec spec = new DbSpec();
            DbSchema schema = spec.addDefaultSchema();
            DbTable u = schema.addTable("usuario");
            QueryPreparer.MultiPlaceHolder param = new QueryPreparer().getNewMultiPlaceHolder();

            UpdateQuery query = new UpdateQuery(u)
                    .addCondition(BinaryCondition.equalTo(u.addColumn("id"), usuario.getId()))
                    .addSetClause(u.addColumn("ativo"), usuario.isAtivo())
                    .addSetClause(u.addColumn("email"), usuario.getEmail())
                    .addSetClause(u.addColumn("nome"), usuario.getNome())
                    .addSetClause(u.addColumn("id_papel"), usuario.getPapel().getId());

            if (!StringUtils.isEmpty(usuario.getSenha())) {
                query.addSetClause(u.addColumn("senha"), usuario.getSenha());
            }

            String sql = query.validate().toString();

            PreparedStatement pst = con.prepareStatement(sql);
             int cont = pst.executeUpdate();

            pst.close();

            return cont > 0;
        } else {
            throw new IllegalArgumentException("O id do usu�rio deve ser um n�mero inteiro positivo");
        }
    }

    public boolean create(Usuario usuario) throws SQLException {
        LOGGER.debug("UsuarioDAO.create()");

        if (usuario.getId() > 0) {
            throw new IllegalArgumentException("O id do usu�rio deve estar vazio");
        } else {
            DbSpec spec = new DbSpec();
            DbSchema schema = spec.addDefaultSchema();
            DbTable u = schema.addTable("usuario");
            QueryPreparer.MultiPlaceHolder param = new QueryPreparer().getNewMultiPlaceHolder();

            String sql = new InsertQuery(u)
                    .addPreparedColumns(u.addColumn("nome"))
                    .addPreparedColumns(u.addColumn("email"))
                    .addPreparedColumns(u.addColumn("senha"))
                    .addPreparedColumns(u.addColumn("id_papel"))
                    .addPreparedColumns(u.addColumn("ativo"))
                    .validate()
                    .toString();

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, usuario.getNome());
            pst.setString(2, usuario.getEmail());
            pst.setString(3, usuario.getSenha());
            pst.setInt(4, usuario.getPapel().getId());
            pst.setBoolean(5, usuario.isAtivo());

             int cont = pst.executeUpdate();

            pst.close();

            return cont > 0;
        }
    }

    public boolean save(Usuario usuario) throws SQLException {
        LOGGER.debug("UsuarioDAO.save()");
        if (usuario.getId() > 0) {
            return alter(usuario);
        } else {
            return create(usuario);
        }
    }
}
