package Proyecto_final;

import javax.swing.*;
import java.sql.*;

public class DespedirEmpleado {

    public DespedirEmpleado(int idEmpleado, String nombreEmpleado, Extra panelExtra) {
        int confirm = JOptionPane.showConfirmDialog(null, 
                "¿Está seguro que desea despedir a " + nombreEmpleado + "?",
                "Confirmar Despido", JOptionPane.YES_NO_OPTION);

        if(confirm == JOptionPane.YES_OPTION){
            try(Connection conn = ConexionDB.obtenerConexion()){
                if(conn == null) return;

                String sql = "UPDATE Empleado SET Activo='N', Fecha_Baja=DATE('now') WHERE Id_Empleado=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idEmpleado);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Empleado despedido correctamente.");
                panelExtra.volverAlMenu(); // refresca el panel
            } catch(Exception ex){
                JOptionPane.showMessageDialog(null, "Error al despedir empleado: "+ex.getMessage());
            }
        }
    }
}
