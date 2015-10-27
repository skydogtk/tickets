package br.unisinos.siead.ds3.ticket.jdbc;

import java.sql.Connection;

public interface ConnectionFactory {
	public Connection getConnection() throws Exception;
}
