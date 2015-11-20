package br.unisinos.siead.ds3.ticket.type;

/**
 *
 * @author Fabrício
 */
public enum TipoRelatorioEnum {

    S("Chamados por Situção"),
    A("Chamados em atendimento por analista"),
    E("Chamados encerrados por analista");

    private final String rotulo;

    private TipoRelatorioEnum(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
