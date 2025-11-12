package Proyecto_final;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PanelTrabajadores extends JPanel {

    private final Color COLOR_FONDO = new Color(35, 35, 35);
    private final Color COLOR_TEXTO = new Color(230, 230, 230);
    private final Color COLOR_BOTON = new Color(60, 60, 60);
    private final Color COLOR_HOVER = new Color(80, 80, 80);
    private final Color COLOR_CONTRATAR = new Color(50, 180, 50);
    private final Color COLOR_DESPEDIR = new Color(180, 50, 50);

    private int idEmpleadoSeleccionado = -1;
    private String nombreEmpleadoSeleccionado = "";

    public PanelTrabajadores(Extra panelExtra, JFrame frameActual) {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ---------------- TÍTULO ----------------
        JLabel lblTitulo = new JLabel("Gestión de Trabajadores", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(COLOR_TEXTO);
        add(lblTitulo, BorderLayout.NORTH);

        // ---------------- PANEL DE BOTONES DE EMPLEADOS ----------------
        JPanel panelEmpleados = new JPanel();
        panelEmpleados.setBackground(COLOR_FONDO);
        panelEmpleados.setLayout(new BoxLayout(panelEmpleados, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(panelEmpleados);
        scroll.getViewport().setBackground(COLOR_FONDO);
        add(scroll, BorderLayout.CENTER);

        // ---------------- CARGAR EMPLEADOS ----------------
        try (Connection conn = ConexionDB.obtenerConexion()) {
            if (conn == null) return;

            String sql = "SELECT Id_Empleado, Nombre FROM Empleado";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("Id_Empleado");
                String nombre = rs.getString("Nombre");

                JButton btnEmpleado = new JButton(nombre);
                btnEmpleado.setAlignmentX(Component.CENTER_ALIGNMENT);
                btnEmpleado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                btnEmpleado.setBackground(COLOR_BOTON);
                btnEmpleado.setForeground(COLOR_TEXTO);
                btnEmpleado.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                btnEmpleado.setFocusPainted(false);
                btnEmpleado.setCursor(new Cursor(Cursor.HAND_CURSOR));

                btnEmpleado.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) { btnEmpleado.setBackground(COLOR_HOVER); }
                    @Override
                    public void mouseExited(MouseEvent e) { btnEmpleado.setBackground(COLOR_BOTON); }
                });

                btnEmpleado.addActionListener(e -> {
                    idEmpleadoSeleccionado = id;
                    nombreEmpleadoSeleccionado = nombre;
                    mostrarVentasEmpleado(id, nombre);
                });

                panelEmpleados.add(Box.createRigidArea(new Dimension(0, 5)));
                panelEmpleados.add(btnEmpleado);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar empleados: " + e.getMessage());
        }

        // ---------------- PANEL INFERIOR: CONTRATAR Y DESPEDIR ----------------
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelInferior.setBackground(COLOR_FONDO);

        JButton btnContratar = new JButton("Contratar");
        btnContratar.setBackground(COLOR_CONTRATAR);
        btnContratar.setForeground(Color.WHITE);
        btnContratar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnContratar.setFocusPainted(false);
        btnContratar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnContratar.addActionListener(e -> {
            new ContratarEmpleado(frameActual, panelExtra);
        });

        JButton btnDespedir = new JButton("Despedir");
        btnDespedir.setBackground(COLOR_DESPEDIR);
        btnDespedir.setForeground(Color.WHITE);
        btnDespedir.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnDespedir.setFocusPainted(false);
        btnDespedir.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnDespedir.addActionListener(e -> {
            if (idEmpleadoSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un empleado primero.");
                return;
            }

            // Pedir autorización del gerente
            AutorizarGerente login = new AutorizarGerente((JFrame) SwingUtilities.getWindowAncestor(this));
            if (!login.isAutorizado()) {
                JOptionPane.showMessageDialog(this, "Acceso denegado. Solo gerentes pueden despedir.");
                return;
            }

            // Confirmación del despido
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Desea realmente despedir a " + nombreEmpleadoSeleccionado + "?",
                    "Confirmar despido", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            // Ejecutar despido en la BD
            try (Connection conn = ConexionDB.obtenerConexion()) {
                if (conn == null) return;

                String sql = "UPDATE Empleado SET Activo = 'N', Fecha_Baja = DATE('now') WHERE Id_Empleado = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idEmpleadoSeleccionado);
                ps.executeUpdate();
                ps.close();

                JOptionPane.showMessageDialog(this, "Empleado despedido correctamente.");

                // Refrescar panel
                panelExtra.volverAlMenu(); // tu método existente para refrescar los botones

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al despedir: " + ex.getMessage());
            }
        });

        panelInferior.add(btnContratar);
        panelInferior.add(btnDespedir);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void mostrarVentasEmpleado(int idEmpleado, String nombre) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Ventas de " + nombre);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JTextArea areaVentas = new JTextArea();
        areaVentas.setEditable(false);
        areaVentas.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        try (Connection conn = ConexionDB.obtenerConexion()) {
            if (conn == null) return;

            String sql = "SELECT V.Id_Venta, V.Fecha, P.Nombre, D.Cantidad, D.Precio_Unitario " +
                         "FROM Venta V " +
                         "JOIN Detalle_Venta D ON V.Id_Venta = D.Id_Venta " +
                         "JOIN Producto P ON D.Id_Producto = P.Id_Producto " +
                         "WHERE V.Id_Empleado = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idEmpleado);
            ResultSet rs = ps.executeQuery();

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Venta ID: ").append(rs.getInt("Id_Venta"))
                  .append("| Fecha: ").append(rs.getString("Fecha"))
                  .append("| Producto: ").append(rs.getString("Nombre"))
                  .append("| Cantidad: ").append(rs.getInt("Cantidad"))
                  .append("| Precio: ").append(rs.getDouble("Precio_Unitario"))
                  .append("\n");
            }

            if (sb.length() == 0) sb.append("No hay ventas registradas para este empleado.");

            areaVentas.setText(sb.toString());

        } catch (SQLException e) {
            areaVentas.setText("Error al cargar ventas: " + e.getMessage());
        }

        dialog.add(new JScrollPane(areaVentas));
        dialog.setModal(true);
        dialog.setVisible(true);
    }
}
