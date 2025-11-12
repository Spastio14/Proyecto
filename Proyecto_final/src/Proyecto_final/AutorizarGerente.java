package Proyecto_final;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AutorizarGerente extends JDialog {

    private boolean autorizado = false;

    public AutorizarGerente(JFrame parent) {
        super(parent, "Autorización Gerente", true);
        setSize(350, 180);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(3, 2, 10, 10));
        getContentPane().setBackground(new Color(35,35,35));

        JTextField txtUsuario = new JTextField();
        JPasswordField txtContrasenia = new JPasswordField();

        add(new JLabel("Usuario:")).setForeground(Color.WHITE);
        add(txtUsuario);
        add(new JLabel("Contraseña:")).setForeground(Color.WHITE);
        add(txtContrasenia);

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.setBackground(new Color(50,180,50));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);
        add(new JLabel());
        add(btnIngresar);

        btnIngresar.addActionListener(e -> {
            String user = txtUsuario.getText().trim();
            String pass = new String(txtContrasenia.getPassword()).trim();

            if(user.isEmpty() || pass.isEmpty()){
                JOptionPane.showMessageDialog(this, "Complete usuario y contraseña.");
                return;
            }

            try(Connection conn = ConexionDB.obtenerConexion()){
                if(conn == null) return;
                String sql = "SELECT Rol, Activo FROM Empleado WHERE Usuario = ? AND Contrasenia = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, user);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();

                if(rs.next()){
                    String rol = rs.getString("Rol");
                    String activo = rs.getString("Activo");

                    if(!activo.equals("S")){
                        JOptionPane.showMessageDialog(this, "Este usuario está inactivo.");
                    } else if("GERENTE".equals(rol)){
                        autorizado = true;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Solo un gerente puede realizar esta acción.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.");
                }

                rs.close();
                ps.close();
            } catch(Exception ex){
                JOptionPane.showMessageDialog(this, "Error BD: " + ex.getMessage());
            }
        });

        setVisible(true);
    }

    public boolean isAutorizado() {
        return autorizado;
    }
}

