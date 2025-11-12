package Proyecto_final;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Extra extends JPanel {

    // --- COLORES ---
    private final Color COLOR_FONDO = new Color(35, 35, 35); // gris oscuro
    private final Color COLOR_BOTON = new Color(55, 55, 55); // gris medio
    private final Color COLOR_HOVER = new Color(75, 75, 75); // hover
    private final Color COLOR_TEXTO = new Color(230, 230, 230);

    // Referencia al JFrame (GerenteUI)
    private JFrame frameActual;

    public Extra(JFrame frameActual) {
        this.frameActual = frameActual;

        // --- PANEL PRINCIPAL ---
        setBackground(COLOR_FONDO);
        setLayout(new GridLayout(5, 1, 0, 0)); // 5 filas, ocupa todo el panel

        // --- BOTONES ---
        JButton btnVendedores = crearBoton("Vendedores");
        JButton btnActualizarPerfil = crearBoton("Actualizar Perfil Gerente");
        JButton btnCerrarSesion = crearBoton("Cerrar Sesión");
        JButton btnGenerarFicheros = crearBoton("Generar Ficheros");
        JButton btnVerVentas = crearBoton("Ver Ventas");

        // Acción botón cerrar sesión
        btnCerrarSesion.addActionListener(e -> {
            frameActual.dispose();
            new Interfaz().setVisible(true);
        });

        // Acción botón "Vendedores" → muestra el panel de trabajadores
        btnVendedores.addActionListener(e -> {
            mostrarPanelTrabajadores();
        });

        // --- AGREGAR BOTONES ---
        add(btnVendedores);
        add(btnActualizarPerfil);
        add(btnCerrarSesion);
        add(btnGenerarFicheros);
        add(btnVerVentas);
    }

    // --- MÉTODO DE CREACIÓN DE BOTONES ---
    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(COLOR_BOTON);
        boton.setForeground(COLOR_TEXTO);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        boton.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(COLOR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(COLOR_BOTON);
            }
        });

        return boton;
    }

    // --- MÉTODO PARA REEMPLAZAR ESTE PANEL CON EL PANEL DE TRABAJADORES ---
    private void mostrarPanelTrabajadores() {
        removeAll();
        setLayout(new BorderLayout());
        add(new PanelTrabajadores(this, frameActual), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // --- MÉTODO PARA VOLVER AL MENÚ ORIGINAL ---
    public void volverAlMenu() {
        removeAll();
        setLayout(new GridLayout(5, 1, 0, 0));

        JButton btnVendedores = crearBoton("Vendedores");
        JButton btnActualizarPerfil = crearBoton("Actualizar Perfil Gerente");
        JButton btnCerrarSesion = crearBoton("Cerrar Sesión");
        JButton btnGenerarFicheros = crearBoton("Generar Ficheros");
        JButton btnVerVentas = crearBoton("Ver Ventas");

        btnCerrarSesion.addActionListener(e -> {
            frameActual.dispose();
            new Interfaz().setVisible(true);
        });

        btnVendedores.addActionListener(e -> {
            mostrarPanelTrabajadores();
        });

        add(btnVendedores);
        add(btnActualizarPerfil);
        add(btnCerrarSesion);
        add(btnGenerarFicheros);
        add(btnVerVentas);

        revalidate();
        repaint();
    }
}
