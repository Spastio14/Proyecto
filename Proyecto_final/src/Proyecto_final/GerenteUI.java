package Proyecto_final;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GerenteUI extends JFrame {

    private int mouseX, mouseY;

    // Panel principal donde se mostrarán los contenidos según la opción del menú
    private JPanel panelPrincipal;

    // --- COLORES USADOS EN LA INTERFAZ ---
    private final Color COLOR_BARRA_SUPERIOR = new Color(36, 37, 42);
    private final Color COLOR_FONDO_CONTENIDO = new Color(45, 45, 45);
    private final Color COLOR_TEXTO = new Color(220, 220, 220);
    private final Color COLOR_AZUL_OSCURO_CRUD = new Color(15, 25, 40);
    private final Color COLOR_MIN_ORIGINAL = new Color(70, 70, 70);
    private final Color COLOR_CERRAR_ORIGINAL = new Color(180, 30, 30);

    public GerenteUI() {
        // CONFIGURACIÓN BÁSICA DE LA VENTANA
        setUndecorated(true);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ---------------- BARRA SUPERIOR ----------------
        JPanel barraSuperior = crearBarraSuperior();
        add(barraSuperior, BorderLayout.NORTH);

        // ---------------- PANEL IZQUIERDO (MENÚ) ----------------
        JPanel panelMenu = crearPanelMenu();
        add(panelMenu, BorderLayout.WEST);

        // ---------------- PANEL PRINCIPAL ----------------
        panelPrincipal = new JPanel();
        panelPrincipal.setBackground(COLOR_FONDO_CONTENIDO);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panelPrincipal.setLayout(new BorderLayout());

        JLabel lblBienvenida = new JLabel(
            "<html><div style='text-align: center;'>"
            + "<h1 style='color:#E0E0E0;'>Bienvenido, Gerente.</h1>"
            + "<p style='color:#B0B0B0;'>Sistema de Administración de Inventario y Ventas.</p>"
            + "<p style='color:#909090;'>Seleccione una opción del menú lateral para comenzar.</p>"
            + "</div></html>",
            SwingConstants.CENTER
        );
        lblBienvenida.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        panelPrincipal.add(lblBienvenida, BorderLayout.CENTER);

        add(panelPrincipal, BorderLayout.CENTER);

        // MOSTRAR VENTANA
        setVisible(true);
    }

    // ---------------- MÉTODO PARA CREAR LA BARRA SUPERIOR ----------------
    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel();
        barra.setBackground(COLOR_BARRA_SUPERIOR);
        barra.setPreferredSize(new Dimension(getWidth(), 50));
        barra.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        JLabel lblTitulo = new JLabel("Sistema de Gestión Gerencial  ");
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel panelBarraContenedor = new JPanel(new BorderLayout());
        panelBarraContenedor.add(lblTitulo, BorderLayout.WEST);
        panelBarraContenedor.add(barra, BorderLayout.EAST);
        panelBarraContenedor.setBackground(COLOR_BARRA_SUPERIOR);

        // Botones de control
        barra.add(crearBotonControl("-", COLOR_MIN_ORIGINAL, e -> setState(JFrame.ICONIFIED), new Color(90, 90, 90)));
        barra.add(crearBotonControl("X", COLOR_CERRAR_ORIGINAL, e -> System.exit(0), new Color(200, 50, 50)));

        // Arrastrar ventana
        panelBarraContenedor.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        panelBarraContenedor.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
            }
        });

        return panelBarraContenedor;
    }

    // ---------------- MÉTODO PARA CREAR BOTONES DE CONTROL ----------------
    private JButton crearBotonControl(String texto, Color colorOriginal, ActionListener accion, Color colorHover) {
        JButton boton = new JButton(texto);
        boton.setForeground(Color.WHITE);
        boton.setBackground(colorOriginal);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setPreferredSize(new Dimension(50, 35));
        boton.setFont(new Font("SansSerif", Font.BOLD, 16));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addActionListener(accion);
        boton.addMouseListener(new ControlButtonHover(boton, colorHover, colorOriginal));
        return boton;
    }

    // ---------------- MÉTODO PARA CREAR EL PANEL DE MENÚ ----------------
    private JPanel crearPanelMenu() {
        JPanel panelMenu = new JPanel();
        panelMenu.setBackground(COLOR_AZUL_OSCURO_CRUD);
        panelMenu.setPreferredSize(new Dimension(220, getHeight()));
        panelMenu.setLayout(new GridLayout(5, 1, 0, 1)); 
        panelMenu.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        String[] opciones = {
            "Agregar Producto", "Eliminar Producto", "Actualizar Producto",
            "Consultar Producto", "Extra"
        };

        Color hoverColor = new Color(25, 40, 60);
        Color clickColor = new Color(35, 50, 75);

        for (String texto : opciones) {
            JButton boton = new JButton(texto);
            boton.setFocusPainted(false);
            boton.setBackground(COLOR_AZUL_OSCURO_CRUD);
            boton.setForeground(COLOR_TEXTO);
            boton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            boton.setBorder(BorderFactory.createEmptyBorder(18, 15, 18, 15));
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            boton.setHorizontalAlignment(SwingConstants.LEFT);

            boton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { boton.setBackground(hoverColor); }
                public void mouseExited(MouseEvent e) { boton.setBackground(COLOR_AZUL_OSCURO_CRUD); }
                public void mousePressed(MouseEvent e) { boton.setBackground(clickColor); }
                public void mouseReleased(MouseEvent e) { boton.setBackground(hoverColor); }
            });

            // Acción: mostrar panel "Extra" con animación
            boton.addActionListener(e -> {
                switch (texto) {
                    case "Agregar Producto":
                        JPanel panelAgregar = new AgregarProducto(GerenteUI.this);
                        animarCambioPanel(panelPrincipal, panelAgregar);
                        break;
                    case "Extra":
                        JPanel panelExtra = new Extra(GerenteUI.this);
                        animarCambioPanel(panelPrincipal, panelExtra);
                        break;
                    case "Eliminar Producto":
                        JPanel panelEliminar = new EliminarProducto(GerenteUI.this);
                        animarCambioPanel(panelPrincipal, panelEliminar);
                        break;

                    case "Actualizar Producto":
                        JPanel panelActualizar = new ActualizarProducto(GerenteUI.this);
                        animarCambioPanel(panelPrincipal, panelActualizar);
                        break;
                    case "Consultar Producto":
                    	GerenteUI.this.setVisible(false);

                        // Abrir TrabajadorUI como ventana de ventas para el gerente
                        // Aquí pasamos un id y nombre ficticio porque el gerente puede usarlo como “Vendedor” temporal
                        TrabajadorUI ventanaVentas = new TrabajadorUI(1, "Gerente");

                        // Cuando se cierre TrabajadorUI, volver a mostrar GerenteUI
                        ventanaVentas.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent e) {
                                GerenteUI.this.setVisible(true);
                            }

                            @Override
                            public void windowClosed(WindowEvent e) {
                                GerenteUI.this.setVisible(true);
                            }
                        });
                        break;
                }
            });

            panelMenu.add(boton);
        }

        return panelMenu;
    }

    // ---------------- EFECTO DE ANIMACIÓN (DESLIZAMIENTO) ----------------
    private void animarCambioPanel(JPanel panelActual, JPanel nuevoPanel) {
        int ancho = panelActual.getWidth();
        int alto = panelActual.getHeight();

        nuevoPanel.setSize(ancho, alto);
        nuevoPanel.setLocation(ancho, 0);
        nuevoPanel.setVisible(true);

        JPanel contenedor = new JPanel(null);
        contenedor.setBackground(COLOR_FONDO_CONTENIDO);
        panelActual.setBounds(0, 0, ancho, alto);
        nuevoPanel.setBounds(ancho, 0, ancho, alto);
        contenedor.add(panelActual);
        contenedor.add(nuevoPanel);

        add(contenedor, BorderLayout.CENTER);
        revalidate();
        repaint();

        Timer timer = new Timer(5, null);
        timer.addActionListener(new ActionListener() {
            int x = ancho;
            float opacidad = 0f;
            public void actionPerformed(ActionEvent e) {
                x -= 25;
                opacidad += 0.05f;
                nuevoPanel.setLocation(x, 0);
                nuevoPanel.setOpaque(true);
                nuevoPanel.repaint();

                if (x <= 0) {
                    timer.stop();
                    remove(contenedor);
                    panelPrincipal.removeAll();
                    panelPrincipal.add(nuevoPanel, BorderLayout.CENTER);
                    add(panelPrincipal, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                }
            }
        });
        timer.start();
    }

    // ---------------- CLASE HOVER DE BOTONES DE CONTROL ----------------
    private class ControlButtonHover extends MouseAdapter {
        private final JButton button;
        private final Color hoverColor;
        private final Color originalColor;
        public ControlButtonHover(JButton button, Color hoverColor, Color originalColor) {
            this.button = button;
            this.hoverColor = hoverColor;
            this.originalColor = originalColor;
        }
        @Override
        public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
        @Override
        public void mouseExited(MouseEvent e) { button.setBackground(originalColor); }
    }
}
