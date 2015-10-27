package br.unisinos.siead.ds3.ticket.security;

/**
 *
 * @author fcsilva
 */
public enum AuthType {
    DENNY_ALL_METHOD(1), 
    ROLES_ALLOWED_METHOD(2), 
    PERMIT_ALL_CLASS(3), 
    ROLES_ALLOWED_CLASS(4), 
    PERMIT_ALL_METHOD(5),
    UNAUTHENTICATED(6);

	private final int codigo;

	AuthType(int codigo) {
		this.codigo = codigo;
	}

	public int getCodigo() {
		return codigo;
	}

	public static AuthType porCodigo(int codigo) {
		for (AuthType authType : AuthType.values()) {
			if (codigo == authType.getCodigo())
				return authType;
		}
		throw new IllegalArgumentException("C�digo inv�lido");
	}
}
