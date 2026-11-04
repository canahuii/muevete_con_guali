import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

public class Interfazz extends JFrame {
    private JPanel tablero;
    private JTextArea movimientosArea;

    public Interfazz() {
        setTitle("Codifica con Guali");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel del tablero
        tablero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarObjetos(g);
            }
        };
        tablero.setPreferredSize(new Dimension(500, 600));

        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(6, 1));
        movimientosArea = new JTextArea();
        movimientosArea.setEditable(false);

        JButton avanzarBtn = new JButton("Avanzar");
        JButton izquierdaBtn = new JButton("Girar Izquierda");
        JButton derechaBtn = new JButton("Girar Derecha");
        JButton reiniciarBtn = new JButton("Reiniciar");

        avanzarBtn.addActionListener(e -> agregarMovimiento("Avanzar"));
        izquierdaBtn.addActionListener(e -> agregarMovimiento("Girar Izquierda"));
        derechaBtn.addActionListener(e -> agregarMovimiento("Girar Derecha"));
        reiniciarBtn.addActionListener(this::reiniciarTablero);

        panelBotones.add(avanzarBtn);
        panelBotones.add(izquierdaBtn);
        panelBotones.add(derechaBtn);
        panelBotones.add(reiniciarBtn);
        panelBotones.add(new JLabel("Movimientos Seleccionados:"));
        panelBotones.add(new JScrollPane(movimientosArea));

        add(tablero, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.EAST);
    }

    private void agregarMovimiento(String mov) {
        movimientosArea.append(mov + "\n");
    }

    private void reiniciarTablero(ActionEvent e) {
        tablero.repaint();
        movimientosArea.setText("");
    }

    private void dibujarObjetos(Graphics g) {
        Random rand = new Random();
        String[] formas = {"Pelota", "Cubo", "Piramide"};
        Color[] colores = {Color.RED, Color.BLUE, Color.YELLOW};

        for (int i = 0; i < 3; i++) {
            int x = rand.nextInt(400);
            int y = rand.nextInt(400);
            g.setColor(colores[i]);

            switch (formas[i]) {
                case "Pelota" -> g.fillOval(x, y, 40, 40);
                case "Cubo" -> g.fillRect(x, y, 40, 40);
                case "Piramide" -> {
                    int[] xs = {x, x + 20, x + 40};
                    int[] ys = {y + 40, y, y + 40};
                    g.fillPolygon(xs, ys, 3);
                }
            }
        }
    }
}
