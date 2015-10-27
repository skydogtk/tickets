package br.unisinos.siead.ds3.ticket.dto;

public class Recurso {

    private int id;
    private String referencia;
    private String icone;
    private String titulo;
    private boolean ativo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Recurso{" + "id=" + id + ", referencia=" + referencia + ", icone=" + icone + ", titulo=" + titulo + ", ativo=" + ativo + '}';
    }
}
