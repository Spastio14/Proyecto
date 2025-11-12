package Proyecto_final;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Interfaz extends JFrame {

    private int mouseX, mouseY;

    public Interfaz() {
        setUndecorated(true);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        
        //hola

        // ---------------- BARRA SUPERIOR ----------------
        JPanel barra = new JPanel();
        barra.setBackground(new Color(36, 37, 42));
        barra.setPreferredSize(new Dimension(getWidth(), 50));
        barra.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        JButton btnMin = new JButton("-");
        btnMin.setForeground(Color.WHITE);
        btnMin.setBackground(new Color(70, 70, 70));
        btnMin.setFocusPainted(false);
        btnMin.setBorderPainted(false);
        btnMin.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnMin.setPreferredSize(new Dimension(50, 35));
        btnMin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));

        JButton btnCerrar = new JButton("X");
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setBackground(new Color(180, 30, 30));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnCerrar.setPreferredSize(new Dimension(50, 35));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> System.exit(0));

        barra.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        barra.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
            }
        });

        barra.add(btnMin);
        barra.add(btnCerrar);
        getContentPane().add(barra, BorderLayout.NORTH);

        // ---------------- PANEL LOGIN ----------------
        JPanel panelLogin = new JPanel();
        panelLogin.setBackground(new Color(52, 58, 64));
        panelLogin.setPreferredSize(new Dimension(400, getHeight()));
        panelLogin.setLayout(null);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setBounds(50, 100, 100, 25);

        JTextField txtUsuario = new JTextField();
        txtUsuario.setBounds(50, 130, 250, 30);
        txtUsuario.setBorder(null);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setForeground(Color.WHITE);
        lblContrasena.setBounds(50, 180, 100, 25);

        JPasswordField txtContrasena = new JPasswordField();
        txtContrasena.setBounds(50, 210, 250, 30);
        txtContrasena.setBorder(null);
        txtContrasena.setEchoChar('●');

        JButton btnMostrar = new JButton("👁️");
        btnMostrar.setForeground(new Color(255, 255, 255));
        btnMostrar.setFont(new Font("Segoe UI Symbol", Font.BOLD, 20));
        btnMostrar.setBounds(310, 210, 60, 44);
        btnMostrar.setBackground(new Color(100, 100, 100));
        btnMostrar.setBorderPainted(false);
        btnMostrar.setFocusPainted(false);
        btnMostrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnMostrar.addActionListener(new ActionListener() {
            boolean activo = false;
            public void actionPerformed(ActionEvent e) {
                activo = !activo;
                txtContrasena.setEchoChar(activo ? (char) 0 : '●');
                btnMostrar.setBackground(activo ? new Color(0, 120, 215) : new Color(100, 100, 100));
            }
        });

        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBounds(50, 270, 250, 35);
        btnLogin.setBackground(new Color(67, 74, 81));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogin.addActionListener(e -> {
            String user = txtUsuario.getText().trim();
            String pass = new String(txtContrasena.getPassword()).trim();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe ingresar usuario y contraseña");
                return;
            }

            Connection con = ConexionDB.obtenerConexion();
            if (con == null) return;

            try {
                String sql = "SELECT Id_Empleado, Nombre, Rol, Activo FROM Empleado WHERE Usuario = ? AND Contrasenia = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, user);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int idEmpleado = rs.getInt("Id_Empleado");         // <-- Obtenemos el ID
                    String nombreEmpleado = rs.getString("Nombre");   // <-- Obtenemos el nombre
                    String rol = rs.getString("Rol");
                    String activo = rs.getString("Activo");

                    if (!activo.equals("S")) {
                        JOptionPane.showMessageDialog(null, "Este usuario está inactivo.");
                        return;
                    }

                    if (rol.equals("GERENTE")) {
                        Interfaz.this.setEnabled(false);
                        Runnable abrirVentanaGerente = () -> {
                            GerenteUI gerente = new GerenteUI();
                            gerente.setVisible(true);
                            Interfaz.this.dispose();
                        };
                        new Mensajes(Interfaz.this, "¡Bienvenido Don Jairo!", abrirVentanaGerente);

                    } else if (rol.equals("VENDEDOR")) {
                        Interfaz.this.setEnabled(false);
                        Runnable abrirVentanaVendedor = () -> {
                            // <-- PASAMOS ID Y NOMBRE al constructor
                            TrabajadorUI trabajador = new TrabajadorUI(idEmpleado, nombreEmpleado);
                            trabajador.setVisible(true);
                            Interfaz.this.dispose();
                        };
                        new Mensajes(Interfaz.this, "¡Bienvenido, " + nombreEmpleado + "!", abrirVentanaVendedor);

                    } else {
                        JOptionPane.showMessageDialog(null, "Rol no reconocido.");
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
                }

                rs.close();
                ps.close();
                con.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al consultar la base de datos");
            }
        });

        panelLogin.add(lblUsuario);
        panelLogin.add(txtUsuario);
        panelLogin.add(lblContrasena);
        panelLogin.add(txtContrasena);
        panelLogin.add(btnMostrar);
        panelLogin.add(btnLogin);
        getContentPane().add(panelLogin, BorderLayout.WEST);

        // ---------------- PANEL IMAGEN ----------------
        JPanel panelImagen = new JPanel();
        panelImagen.setBackground(new Color(220, 220, 220));
        panelImagen.setLayout(new BorderLayout());

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/Proyecto_final/imagenes/edificio.jpeg"));
            Image img = icon.getImage();
            Image imgEscalada = img.getScaledInstance(500, 600, Image.SCALE_SMOOTH);
            JLabel etiquetaImagen = new JLabel(new ImageIcon(imgEscalada));
            etiquetaImagen.setHorizontalAlignment(SwingConstants.CENTER);
            panelImagen.add(etiquetaImagen, BorderLayout.CENTER);
        } catch (Exception e) {
            panelImagen.add(new JLabel("Imagen no encontrada", SwingConstants.CENTER));
        }

        getContentPane().add(panelImagen, BorderLayout.CENTER);
    }
}
