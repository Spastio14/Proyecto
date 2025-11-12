package Proyecto_final;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ContratarEmpleado extends JDialog {

    public ContratarEmpleado(JFrame parent, Extra panelExtra) {
        super(parent, "Contratar Nuevo Empleado", true);
        setSize(400, 400);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(6, 2, 10, 10));
        getContentPane().setBackground(new Color(35,35,35));

        JTextField txtNombre = new JTextField();
        JTextField txtUsuario = new JTextField();
        JPasswordField txtContrasenia = new JPasswordField();
        JComboBox<String> comboRol = new JComboBox<>(new String[]{"VENDEDOR"});

        add(new JLabel("Nombre:")).setForeground(Color.WHITE);
        add(txtNombre);
        add(new JLabel("Usuario:")).setForeground(Color.WHITE);
        add(txtUsuario);
        add(new JLabel("Contraseña:")).setForeground(Color.WHITE);
        add(txtContrasenia);
        add(new JLabel("Rol:")).setForeground(Color.WHITE);
        add(comboRol);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(new Color(50,180,50));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);

        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String usuario = txtUsuario.getText().trim();
            String contrasenia = new String(txtContrasenia.getPassword());
            String rol = (String) comboRol.getSelectedItem();

            if(nombre.isEmpty() || usuario.isEmpty() || contrasenia.isEmpty()){
                JOptionPane.showMessageDialog(this, "Complete todos los campos.");
                return;
            }

            try(Connection conn = ConexionDB.obtenerConexion()){
                if(conn == null) return;
                String sql = "INSERT INTO Empleado(Nombre, Usuario, Contrasenia, Rol, Activo, Fecha_Contrato) " +
                             "VALUES(?,?,?,?, 'S', DATE('now'))";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, nombre);
                ps.setString(2, usuario);
                ps.setString(3, contrasenia);
                ps.setString(4, rol);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Empleado contratado correctamente!");
                dispose();
                panelExtra.volverAlMenu(); // refresca el panel
            } catch(Exception ex){
                JOptionPane.showMessageDialog(this, "Error al contratar: "+ex.getMessage());
            }
        });

        add(new JLabel());
        add(btnGuardar);

        setVisible(true);
    }
}
