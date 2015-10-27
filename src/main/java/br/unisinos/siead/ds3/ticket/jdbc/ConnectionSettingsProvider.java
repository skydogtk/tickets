package br.unisinos.siead.ds3.ticket.jdbc;

import br.unisinos.siead.ds3.ticket.dto.ConnectionSettings;

public interface ConnectionSettingsProvider {
	public ConnectionSettings getSettings() throws Exception;
}
