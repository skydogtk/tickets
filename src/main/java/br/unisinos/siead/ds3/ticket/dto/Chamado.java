package br.unisinos.siead.ds3.ticket.dto;

/**
 *
 * @author Fabrício
 */
public class Chamado {

    private int id;
    private long datahora;
    private TipoChamado tipoChamado;
    private Usuario usuarioAutor;
    private Usuario usuarioAtendimento;
    private TipoSituacao tipoSituacao;
    private TipoFalha tipoFalha;
    private String assunto;
    private String descricao;

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

    public TipoChamado getTipoChamado() {
        return tipoChamado;
    }

    public void setTipoChamado(TipoChamado tipoChamado) {
        this.tipoChamado = tipoChamado;
    }

    public Usuario getUsuarioAutor() {
        return usuarioAutor;
    }

    public void setUsuarioAutor(Usuario usuarioAutor) {
        this.usuarioAutor = usuarioAutor;
    }

    public Usuario getUsuarioAtendimento() {
        return usuarioAtendimento;
    }

    public void setUsuarioAtendimento(Usuario usuarioAtendimento) {
        this.usuarioAtendimento = usuarioAtendimento;
    }

    public TipoSituacao getTipoSituacao() {
        return tipoSituacao;
    }

    public void setTipoSituacao(TipoSituacao tipoSituacao) {
        this.tipoSituacao = tipoSituacao;
    }

    public TipoFalha getTipoFalha() {
        return tipoFalha;
    }

    public void setTipoFalha(TipoFalha tipoFalha) {
        this.tipoFalha = tipoFalha;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "Chamado{" + "id=" + id + ", datahora=" + datahora + ", tipoChamado=" + tipoChamado + ", usuarioAutor=" + usuarioAutor + ", usuarioAtendimento=" + usuarioAtendimento + ", tipoSituacao=" + tipoSituacao + ", tipoFalha=" + tipoFalha + ", assunto=" + assunto + ", descricao=" + descricao + '}';
    }

}
