package Proyecto_final;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Vendedores extends JPanel{
	
	private final Color COLOR_FONDO = new Color(35, 35, 35); // gris oscuro
    private final Color COLOR_BOTON = new Color(55, 55, 55); // gris medio
    private final Color COLOR_HOVER = new Color(75, 75, 75); // hover
    private final Color COLOR_TEXTO = new Color(230, 230, 230);
	
	public Vendedores(JFrame frameActual) 
	{
		 setBackground(COLOR_FONDO);
	     setLayout(new GridLayout(5, 1, 0, 0));
	}
	

}
