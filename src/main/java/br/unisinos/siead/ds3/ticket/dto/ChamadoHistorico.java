package br.unisinos.siead.ds3.ticket.dto;

/**
 *
 * @author Fabrício
 */
public class ChamadoHistorico {

    private int id;
    private long datahora;
    private Chamado chamado;
    private Usuario usuario;
    private String desc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDatahora() {
        return datahora;
    }

    public void setDatahora(long datahora) {
        this.datahora = datahora;
    }

    public Chamado getChamado() {
        return chamado;
    }

    public void setChamado(Chamado chamado) {
        this.chamado = chamado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "ChamadoHistorico{" + "id=" + id + ", datahora=" + datahora + ", chamado=" + chamado + ", usuario=" + usuario + ", desc=" + desc + '}';
    }
}
