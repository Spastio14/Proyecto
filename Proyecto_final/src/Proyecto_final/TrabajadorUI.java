package Proyecto_final;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.event.*;
import java.awt.*;
import java.sql.*;

public class TrabajadorUI extends JFrame {

    private JTextField textBuscar;
    private JTable tablaProductos, tablaCarrito;
    private DefaultTableModel modeloProductos, modeloCarrito;
    private TableRowSorter<DefaultTableModel> sorter;
    private JLabel lblTotal;
    private int xMouse, yMouse;

    private int idEmpleado;          // ID del trabajador
    private String nombreEmpleado;   // Nombre del trabajador

    public TrabajadorUI(int idEmpleado, String nombreEmpleado) {
        this.idEmpleado = idEmpleado;
        this.nombreEmpleado = nombreEmpleado;

        setUndecorated(true);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        SupCompo();
        compBusquedaVenta();

        setVisible(true);
    }

    private void SupCompo() {
        JPanel barraSuperior = new JPanel();
        barraSuperior.setBackground(Color.DARK_GRAY);
        barraSuperior.setBounds(0, 0, 1000, 50);
        getContentPane().add(barraSuperior);
        barraSuperior.setLayout(null);

        JButton btnCerrar = new JButton("X");
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setBackground(new Color(180, 30, 30));
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnCerrar.setBounds(940, 7, 50, 35);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> this.dispose());
        barraSuperior.add(btnCerrar);

        JButton btnMin = new JButton("-");
        btnMin.setForeground(Color.WHITE);
        btnMin.setBackground(new Color(70, 70, 70));
        btnMin.setBorderPainted(false);
        btnMin.setFocusPainted(false);
        btnMin.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnMin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMin.setBounds(880, 7, 50, 35);
        btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));
        barraSuperior.add(btnMin);

        barraSuperior.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                xMouse = e.getX();
                yMouse = e.getY();
            }
        });

        barraSuperior.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - xMouse, e.getYOnScreen() - yMouse);
            }
        });

        // Mostrar el nombre del vendedor en la barra
        JLabel lblEmpleado = new JLabel("Vendedor: " + nombreEmpleado);
        lblEmpleado.setForeground(Color.WHITE);
        lblEmpleado.setFont(new Font("Arial", Font.BOLD, 16));
        lblEmpleado.setBounds(10, 10, 300, 30);
        barraSuperior.add(lblEmpleado);
    }

    private void compBusquedaVenta() {
        JLabel lblBuscar = new JLabel("Buscar producto:");
        lblBuscar.setFont(new Font("Arial", Font.BOLD, 14));
        lblBuscar.setBounds(10, 61, 150, 20);
        getContentPane().add(lblBuscar);

        textBuscar = new JTextField();
        textBuscar.setBounds(10, 82, 388, 27);
        getContentPane().add(textBuscar);

        JButton btnBuscar = new JButton("🔍");
        btnBuscar.setFont(new Font("Segoe UI Symbol", Font.BOLD, 20));
        btnBuscar.setBorderPainted(false);
        btnBuscar.setContentAreaFilled(false);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBounds(408, 82, 63, 30);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        getContentPane().add(btnBuscar);

        // --- TABLA PRODUCTOS ---
        modeloProductos = new DefaultTableModel(new Object[]{"ID", "Nombre", "Precio", "Expira", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaProductos = new JTable(modeloProductos);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaProductos.setRowHeight(30);
        tablaProductos.setSelectionBackground(new Color(0, 97, 141));
        tablaProductos.setSelectionForeground(Color.WHITE);
        tablaProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tablaProductos.getTableHeader().setBackground(new Color(30, 30, 30));
        tablaProductos.getTableHeader().setForeground(Color.WHITE);
        tablaProductos.getTableHeader().setReorderingAllowed(false);
        tablaProductos.getTableHeader().setResizingAllowed(false);

        cargarProductos();

        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        scrollTabla.setBounds(10, 120, 488, 264);
        scrollTabla.setBorder(BorderFactory.createEmptyBorder());
        getContentPane().add(scrollTabla);

        sorter = new TableRowSorter<>(modeloProductos);
        tablaProductos.setRowSorter(sorter);

        textBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        btnBuscar.addActionListener(e -> filtrar());

        // --- TABLA CARRITO ---
        modeloCarrito = new DefaultTableModel(new Object[]{"ID", "Nombre", "Precio", "Cant."}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaCarrito = new JTable(modeloCarrito);
        tablaCarrito.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaCarrito.setRowHeight(30);
        tablaCarrito.setSelectionBackground(new Color(0, 97, 141));
        tablaCarrito.setSelectionForeground(Color.WHITE);
        tablaCarrito.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tablaCarrito.getTableHeader().setBackground(new Color(30, 30, 30));
        tablaCarrito.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollCarrito = new JScrollPane(tablaCarrito);
        scrollCarrito.setBounds(10, 395, 798, 200);
        scrollCarrito.setBorder(BorderFactory.createEmptyBorder());
        getContentPane().add(scrollCarrito);

        // --- BOTONES ---
        JButton btnAgregar = new JButton("🛒");
        btnAgregar.setFont(new Font("Segoe UI Symbol", Font.BOLD, 50));
        btnAgregar.setBorderPainted(false);
        btnAgregar.setContentAreaFilled(false);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAgregar.setBounds(508, 120, 97, 50);
        getContentPane().add(btnAgregar);
        btnAgregar.addActionListener(e -> agregarAlCarrito());

        JButton btnQuitar = new JButton("❌");
        btnQuitar.setFont(new Font("Segoe UI Symbol", Font.BOLD, 45));
        btnQuitar.setBorderPainted(false);
        btnQuitar.setContentAreaFilled(false);
        btnQuitar.setFocusPainted(false);
        btnQuitar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuitar.setBounds(508, 181, 97, 50);
        getContentPane().add(btnQuitar);
        btnQuitar.addActionListener(e -> quitarDelCarrito());

        JButton btnVender = new JButton("💰");
        btnVender.setFont(new Font("Segoe UI Symbol", Font.BOLD, 50));
        btnVender.setBorderPainted(false);
        btnVender.setContentAreaFilled(false);
        btnVender.setFocusPainted(false);
        btnVender.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVender.setBounds(508, 242, 97, 50);
        getContentPane().add(btnVender);
        btnVender.addActionListener(e -> ejecutarVenta());

        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setBounds(10, 605, 798, 30);
        getContentPane().add(lblTotal);

        JButton btnCerrarSesion = new JButton("🚪");
        btnCerrarSesion.setBounds(908, 581, 82, 48);
        getContentPane().add(btnCerrarSesion);
        btnCerrarSesion.setFont(new Font("Segoe UI Symbol", Font.BOLD, 50));
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.setContentAreaFilled(false);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrarSesion.addActionListener(e -> {
            this.dispose();
            new Interfaz().setVisible(true);
        });
    }

    private void filtrar() {
        String texto = textBuscar.getText();
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
    }

    private void cargarProductos() {
        try (Connection conn = ConexionDB.obtenerConexion()) {
            if (conn == null) return;

            String sql = "SELECT Id_Producto, Nombre, Precio_Venta, Fecha_Caducidad, Stock FROM Producto";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modeloProductos.addRow(new Object[]{
                    rs.getInt("Id_Producto"),
                    rs.getString("Nombre"),
                    rs.getDouble("Precio_Venta"),
                    rs.getString("Fecha_Caducidad"),
                    rs.getInt("Stock")
                });
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage());
        }
    }

    private void agregarAlCarrito() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto");
            return;
        }

        int filaModelo = tablaProductos.convertRowIndexToModel(fila);
        int id = (int) modeloProductos.getValueAt(filaModelo, 0);
        String nombre = (String) modeloProductos.getValueAt(filaModelo, 1);
        double precio = (double) modeloProductos.getValueAt(filaModelo, 2);

        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            if ((int) modeloCarrito.getValueAt(i, 0) == id) {
                int qty = (int) modeloCarrito.getValueAt(i, 3) + 1;
                modeloCarrito.setValueAt(qty, i, 3);
                actualizarTotal();
                return;
            }
        }
        modeloCarrito.addRow(new Object[]{id, nombre, precio, 1});
        actualizarTotal();
    }

    private void quitarDelCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto del carrito para quitar");
            return;
        }
        modeloCarrito.removeRow(fila);
        actualizarTotal();
    }

    private void ejecutarVenta() {
        if (modeloCarrito.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío");
            return;
        }

        try (Connection conn = ConexionDB.obtenerConexion()) {
            if (conn == null) return;
            conn.setAutoCommit(false);

            // INSERT con el idEmpleado dinámico
            PreparedStatement psVenta = conn.prepareStatement(
                "INSERT INTO Venta (Fecha, Id_Empleado) VALUES (DATE('now'), ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            psVenta.setInt(1, idEmpleado);
            psVenta.executeUpdate();
            ResultSet rsKeys = psVenta.getGeneratedKeys();
            int idVenta = rsKeys.next() ? rsKeys.getInt(1) : 0;
            psVenta.close();

            String sqlDetalle = "INSERT INTO Detalle_Venta (Id_Venta, Id_Producto, Cantidad, Precio_Unitario) VALUES (?,?,?,?)";
            PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);

            StringBuilder factura = new StringBuilder("========= FACTURA =========\n");
            factura.append("ID Venta: ").append(idVenta).append("\nVendedor: ").append(nombreEmpleado)
                    .append("\n\nProducto\tCant\tPrecio\n");
            double total = 0;

            for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
                int idProd = (int) modeloCarrito.getValueAt(i, 0);
                String nombre = (String) modeloCarrito.getValueAt(i, 1);
                double precio = (double) modeloCarrito.getValueAt(i, 2);
                int cant = (int) modeloCarrito.getValueAt(i, 3);

                psDetalle.setInt(1, idVenta);
                psDetalle.setInt(2, idProd);
                psDetalle.setInt(3, cant);
                psDetalle.setDouble(4, precio);
                psDetalle.addBatch();

                PreparedStatement psStock = conn.prepareStatement("UPDATE Producto SET Stock = Stock - ? WHERE Id_Producto = ?");
                psStock.setInt(1, cant);
                psStock.setInt(2, idProd);
                psStock.executeUpdate();
                psStock.close();

                factura.append(nombre).append("\t").append(cant).append("\t$").append(precio * cant).append("\n");
                total += precio * cant;
            }

            psDetalle.executeBatch();
            psDetalle.close();
            conn.commit();

            factura.append("\nTOTAL: $").append(total).append("\n==========================\n");
            JTextArea areaFactura = new JTextArea(factura.toString());
            areaFactura.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(areaFactura), "Factura", JOptionPane.INFORMATION_MESSAGE);

            modeloCarrito.setRowCount(0);
            actualizarTotal();
            modeloProductos.setRowCount(0);
            cargarProductos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al registrar venta: " + e.getMessage());
        }
    }

    private void actualizarTotal() {
        double total = 0;
        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            double precio = (double) modeloCarrito.getValueAt(i, 2);
            int qty = (int) modeloCarrito.getValueAt(i, 3);
            total += precio * qty;
        }
        lblTotal.setText(String.format("Total: $%.2f", total));
    }

   
}
