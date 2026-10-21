package com.icechopp.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável por abrir conexões com o banco de dados icechopp.
 * Ajuste URL, USUARIO e SENHA conforme o seu ambiente (MySQL/MariaDB).
 */
public class ConexaoBD {

    private static final String URL = "jdbc:mysql://localhost:3306/icechopp?useTimezone=true&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String SENHA = "";

    private ConexaoBD() {
    }

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do MySQL não encontrado no classpath.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

}


