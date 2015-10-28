package br.unisinos.siead.ds3.ticket.jdbc;

/**
 *
 * @author fcsilva
 */
public enum DBType {

    POSTGRESQL(1);

    private final int codigo;

    DBType(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public static DBType porCodigo(int codigo) {
        for (DBType authType : DBType.values()) {
            if (codigo == authType.getCodigo()) {
                return authType;
            }
        }
        throw new IllegalArgumentException("Código inválido");
    }
}
