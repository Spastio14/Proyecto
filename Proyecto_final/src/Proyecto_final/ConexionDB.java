package Proyecto_final;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.*;

public class ConexionDB {

    private static final String DB_URL = "jdbc:sqlite:C:\\sqlite\\Farmacia.db";

    public static Connection obtenerConexion() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar a la base de datos: " + e.getMessage());
            return null;
        }
    }
}
