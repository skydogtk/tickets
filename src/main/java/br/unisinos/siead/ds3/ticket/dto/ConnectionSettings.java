package br.unisinos.siead.ds3.ticket.dto;

public class ConnectionSettings {

    private String driver;
    private String url;
    private String usuario;
    private String senha;

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return "ConnectionSettings{" + "driver=" + driver + ", url=" + url + ", usuario=" + usuario + ", senha=" + senha + '}';
    }

}
