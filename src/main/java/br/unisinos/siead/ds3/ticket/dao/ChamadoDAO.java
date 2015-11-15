package br.unisinos.siead.ds3.ticket.dao;

import br.unisinos.siead.ds3.ticket.dto.Chamado;
import br.unisinos.siead.ds3.ticket.dto.ChamadoHistorico;
import br.unisinos.siead.ds3.ticket.dto.Usuario;
import br.unisinos.siead.ds3.ticket.util.LogUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Fabrício
 */
public class ChamadoDAO {

    private static final Logger LOGGER = LogUtils.loggerForThisClass();

    private final Connection con;

    public ChamadoDAO(Connection con) {
        this.con = con;

        LOGGER.debug("ChamadoDAO");
    }

    public List<Chamado> getAll() throws SQLException {
        LOGGER.debug("ChamadoDAO.getALl()");
        List<Chamado> chamados = new ArrayList<>();

        String sql = "SELECT * FROM chamado ORDER BY id DESC;";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Chamado chamado = new Chamado();
                chamado.setAssunto(rs.getString("assunto"));
                chamado.setDatahora(rs.getTimestamp("datahora").getTime());
                chamado.setDescricao(rs.getString("descricao"));
                chamado.setId(rs.getInt("id"));
                chamado.setTipoChamado(new TipoChamadoDAO(con).findById(rs.getInt("id_tipo_chamado")));
                chamado.setTipoFalha(new TipoFalhaDAO(con).findById(rs.getInt("id_tipo_falha")));
                chamado.setTipoSituacao(new TipoSituacaoDAO(con).findById(rs.getInt("id_tipo_situacao")));
                chamado.setUsuarioAtendimento(new UsuarioDAO(con).findById(rs.getInt("id_usuario_atendimento")));
                chamado.setUsuarioAutor(new UsuarioDAO(con).findById(rs.getInt("id_usuario_autor")));

                chamados.add(chamado);
            }

            LOGGER.debug("Chamados: " + chamados.size());
        }
        return chamados;
    }

    public Chamado findById(int idChamado) throws SQLException {
        Chamado chamado = new Chamado();
        String sql = "SELECT * FROM chamado WHERE id = ?;";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idChamado);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                chamado.setAssunto(rs.getString("assunto"));
                chamado.setDatahora(rs.getTimestamp("datahora").getTime());
                chamado.setDescricao(rs.getString("descricao"));
                chamado.setId(rs.getInt("id"));
                chamado.setTipoChamado(new TipoChamadoDAO(con).findById(rs.getInt("id_tipo_chamado")));
                chamado.setTipoFalha(new TipoFalhaDAO(con).findById(rs.getInt("id_tipo_falha")));
                chamado.setTipoSituacao(new TipoSituacaoDAO(con).findById(rs.getInt("id_tipo_situacao")));
                chamado.setUsuarioAtendimento(new UsuarioDAO(con).findById(rs.getInt("id_usuario_atendimento")));
                chamado.setUsuarioAutor(new UsuarioDAO(con).findById(rs.getInt("id_usuario_autor")));
            }
        }
        return chamado;
    }

    public List<Chamado> findByUsuario(Usuario usuario) throws SQLException {
        LOGGER.debug("ChamadoDAO.findByUsuario()");
        List<Chamado> chamados = new ArrayList<>();

        String condicao = "";

        if (usuario.getPapel().getId() == 3) {
            /* 
             * Usuário 
             * Listar apenas os chamados deste usuário
             */
            condicao = "WHERE id_usuario_autor = " + usuario.getId();
        } else if (usuario.getPapel().getId() == 2) {
            /* 
             * Analista:
             * Listar chamados que este usuário é o autor, que ele atende ou que aguardem atendimento
             */
            condicao = "WHERE id_usuario_autor = " + usuario.getId() + " OR id_usuario_atendimento = " + usuario.getId() + " OR id_tipo_situacao = 1";
        }

        String sql = "SELECT * FROM chamado " + condicao + " ORDER BY id DESC;";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Chamado chamado = new Chamado();
                chamado.setAssunto(rs.getString("assunto"));
                chamado.setDatahora(rs.getTimestamp("datahora").getTime());
                chamado.setDescricao(rs.getString("descricao"));
                chamado.setId(rs.getInt("id"));
                chamado.setTipoChamado(new TipoChamadoDAO(con).findById(rs.getInt("id_tipo_chamado")));
                chamado.setTipoFalha(new TipoFalhaDAO(con).findById(rs.getInt("id_tipo_falha")));
                chamado.setTipoSituacao(new TipoSituacaoDAO(con).findById(rs.getInt("id_tipo_situacao")));
                chamado.setUsuarioAtendimento(new UsuarioDAO(con).findById(rs.getInt("id_usuario_atendimento")));
                chamado.setUsuarioAutor(new UsuarioDAO(con).findById(rs.getInt("id_usuario_autor")));

                chamados.add(chamado);
            }

            LOGGER.debug("Chamados: " + chamados.size());
        }
        return chamados;
    }

    private boolean alter(Chamado chamado) throws SQLException {
        LOGGER.debug("ChamadoDAO.alter()");
        if (chamado.getId() == 0) {
            throw new IllegalArgumentException("O chamado deve possuir id");
        } else {
            int cont;
            String sql = "UPDATE chamado SET id_usuario_atendimento = ?, id_tipo_situacao = ? WHERE id = ?;";
            PreparedStatement pst = con.prepareStatement(sql);
            if (chamado.getUsuarioAtendimento().getId() > 0) {
                pst.setInt(1, chamado.getUsuarioAtendimento().getId());
            } else {
                pst.setNull(1, Types.INTEGER);
            }
            pst.setInt(2, chamado.getTipoSituacao().getId());
            pst.setInt(3, chamado.getId());

            cont = pst.executeUpdate();
            return cont > 0;
        }
    }

    private boolean create(Chamado chamado) throws SQLException {
        LOGGER.debug("ChamadoDAO.create()");

        if (chamado.getId() > 0) {
            throw new IllegalArgumentException("O id do chamado deve estar vazio");
        } else {
            con.setAutoCommit(false);
            String sql = "INSERT INTO chamado (id_tipo_chamado, id_usuario_autor, id_tipo_situacao, id_tipo_falha, assunto, descricao) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pst.setInt(1, chamado.getTipoChamado().getId());
                pst.setInt(2, chamado.getUsuarioAutor().getId());
                pst.setInt(3, 1);

                if (chamado.getTipoChamado().getId() == 1) {
                    pst.setNull(4, Types.INTEGER);
                } else {
                    pst.setInt(4, chamado.getTipoFalha().getId());
                }
                pst.setString(5, chamado.getAssunto());
                pst.setString(6, chamado.getDescricao());
                pst.executeUpdate();

                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    chamado.setId(rs.getInt(1));
                    ChamadoHistorico chamadoHistorico = new ChamadoHistorico();
                    chamadoHistorico.setChamado(chamado);
                    chamadoHistorico.setDesc("Abertura do chamado");
                    chamadoHistorico.setUsuario(chamado.getUsuarioAutor());

                    ChamadoHistoricoDAO chamadoHistoricoDAO = new ChamadoHistoricoDAO(con);
                    chamadoHistoricoDAO.save(chamadoHistorico);

                    con.commit();
                    return true;
                }
            }
            return false;
        }
    }

    public boolean save(Chamado chamado) throws SQLException {
        LOGGER.debug("ChamadoDAO.save()");
        if (chamado.getId() > 0) {
            return alter(chamado);
        } else {
            return create(chamado);
        }
    }
}
