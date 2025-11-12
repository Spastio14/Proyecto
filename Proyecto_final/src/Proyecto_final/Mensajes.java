package Proyecto_final;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Mensajes extends JWindow {

    private Timer fadeInTimer;
    private Timer fadeOutTimer;
    private Timer pauseTimer;
    private float currentOpacity = 0f;
    
    // --- NUEVO ---
    // Esta variable guardará la "tarea" a ejecutar al final
    private Runnable onFinishedCallback;

    /**
     * Constructor simple (el que tenías)
     */
    public Mensajes(JFrame parent, String texto) {
        // Llama al constructor principal sin ninguna tarea
        this(parent, texto, null);
    }
    
    /**
     * --- NUEVO ---
     * Constructor principal que acepta una "tarea" (callback)
     */
    public Mensajes(JFrame parent, String texto, Runnable onFinishedCallback) {
        super(parent);
        this.onFinishedCallback = onFinishedCallback; // Guarda la tarea

        // --- Panel principal (Tu código original) ---
        JPanel panel = new JPanel();
        panel.setBackground(new Color(36, 37, 42));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 2));
        panel.setLayout(new BorderLayout());

        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        lbl.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        panel.add(lbl, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        // --- Centrar el mensaje (Tu código original) ---
        if (parent != null) {
            int x = parent.getX() + (parent.getWidth() - getWidth()) / 2;
            int y = parent.getY() + (parent.getHeight() - getHeight()) / 2;
            setLocation(x, y);
        } else {
            setLocationRelativeTo(null);
        }

        // --- Configurar animación con Timers ---
        setOpacity(0f); 

        // 1. Timer para FADE-IN
        fadeInTimer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentOpacity += 0.1f;
                if (currentOpacity >= 1.0f) {
                    currentOpacity = 1.0f;
                    setOpacity(currentOpacity);
                    ((Timer) e.getSource()).stop(); 
                    pauseTimer.start();
                } else {
                    setOpacity(currentOpacity);
                }
            }
        });

        // 2. Timer para PAUSA
        pauseTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fadeOutTimer.start();
            }
        });
        pauseTimer.setRepeats(false); 

        // 3. Timer para FADE-OUT
        fadeOutTimer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentOpacity -= 0.1f;
                if (currentOpacity <= 0.0f) {
                    currentOpacity = 0.0f;
                    setOpacity(currentOpacity);
                    ((Timer) e.getSource()).stop();
                    
                    // --- MODIFICACIÓN CLAVE ---
                    // Antes de cerrar, ejecuta la tarea si existe
                    if (onFinishedCallback != null) {
                        onFinishedCallback.run();
                    }
                    
                    // Al terminar, destruir la ventana
                    dispose(); 
                } else {
                    setOpacity(currentOpacity);
                }
            }
        });

        // --- Iniciar el proceso ---
        setVisible(true);
        fadeInTimer.start();
    }
}