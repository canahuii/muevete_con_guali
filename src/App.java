import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class App extends JFrame {
    private JPanel jpPanelPrincipal;
    private JTable jtTablero;
    private JButton btnAvanzar;
    private JButton ejecutarRecorridoButton;
    private JButton btnDerecha;
    private JButton btnIzquierda;
    private JButton reiniciarButton;
    private JButton cargarButton;
    private JList<ImageIcon> listaMovimientos;
    DefaultTableModel modeloHistorial = new DefaultTableModel(
            new Object[]{"Inicio movimientos", "Fin recorrido", "Resultado"}, 0
    );

    private JPanel crearPanelEstadisticas(List<Intento> historial) {
        int victorias = 0;
        int derrotas = 0;
        long minDuracion = Long.MAX_VALUE;
        long maxDuracion = Long.MIN_VALUE;

        for (Intento intento : historial) {
            if (intento.gano) victorias++;
            else derrotas++;

            if (intento.fechaInicioMov != null && intento.fechaFin != null) {
                long duracion = java.time.Duration.between(intento.fechaInicioMov, intento.fechaFin).toSeconds();
                minDuracion = Math.min(minDuracion, duracion);
                maxDuracion = Math.max(maxDuracion, duracion);
            }
        }

        // 🔹 Formatear duración mínima
        String textoMin = "-";
        if (minDuracion != Long.MAX_VALUE) {
            if (minDuracion < 60) {
                textoMin = minDuracion + " segundos";
            } else {
                textoMin = (minDuracion / 60) + " minutos";
            }
        }

        // 🔹 Formatear duración máxima
        String textoMax = "-";
        if (maxDuracion != Long.MIN_VALUE) {
            if (maxDuracion < 60) {
                textoMax = maxDuracion + " segundos";
            } else {
                textoMax = (maxDuracion / 60) + " minutos";
            }
        }

        // Panel con etiquetas
        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.add(new JLabel("Total misiones cumplidas: " + victorias));
        panel.add(new JLabel("Total misiones fallidas: " + derrotas));
        panel.add(new JLabel("Misión más corta: " + textoMin));
        panel.add(new JLabel("Misión más larga: " + textoMax));

        return panel;
    }



    private int robotFila = 0;
    private int robotColumna = 0;
    private int direccion = 1; // 0=arriba, 1=derecha, 2=abajo, 3=izquierda
    private int objetosRecogidos = 0;

    boolean gano = false;
    boolean salioDelLimite = false;

    ArrayList<Integer> listaInstrucciones = new ArrayList<>();
    // Lista para guardar las figuras con posición y descripción
    List<Figura> figuras = new ArrayList<>();
    List<Intento> historialIntentos = new ArrayList<>();

    public App() {
        configurarVentana();
        inicializarTablero();

        // Modelo para almacenar imágenes en la lista
        DefaultListModel<ImageIcon> modeloLista = new DefaultListModel<>();


        listaMovimientos.setModel(modeloLista);

        // Renderer para mostrar imágenes centradas en la lista
        listaMovimientos.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = new JLabel();
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setOpaque(true);
                lbl.setBackground(isSelected ? Color.DARK_GRAY : Color.BLACK);
                if (value instanceof ImageIcon icon) {
                    lbl.setIcon(icon);
                }
                return lbl;
            }
        });

        // Botones que agregan imágenes en lugar de texto
        btnAvanzar.addActionListener(e ->{
            // Registrar inicio de movimientos solo la primera vez
            if (historialIntentos.isEmpty() || historialIntentos.get(historialIntentos.size()-1).fechaFin != null) {
                // Nuevo intento porque el anterior ya terminó
                LocalDateTime inicioMov = LocalDateTime.now();
                historialIntentos.add(new Intento(inicioMov, null, false));
            }

                modeloLista.addElement(escalarImagen("src/figuras/direccion/adelante.png", 50, 50));
                listaInstrucciones.add(1);
                });

        btnDerecha.addActionListener(e ->{
            // Registrar inicio de movimientos solo la primera vez
            if (historialIntentos.isEmpty() || historialIntentos.get(historialIntentos.size()-1).fechaFin != null) {
                //  Nuevo intento porque el anterior ya terminó
                LocalDateTime inicioMov = LocalDateTime.now();
                historialIntentos.add(new Intento(inicioMov, null, false));
            }

            modeloLista.addElement(escalarImagen("src/figuras/direccion/derecha.png", 50, 50));
            listaInstrucciones.add(2);
        });

        btnIzquierda.addActionListener(e ->{
            // Registrar inicio de movimientos solo la primera vez
            if (historialIntentos.isEmpty() || historialIntentos.get(historialIntentos.size()-1).fechaFin != null) {
                //  Nuevo intento porque el anterior ya terminó
                LocalDateTime inicioMov = LocalDateTime.now();
                historialIntentos.add(new Intento(inicioMov, null, false));
            }

            modeloLista.addElement(escalarImagen("src/figuras/direccion/izquierda.png", 50, 50));
            listaInstrucciones.add(3);
        });

        cargarButton.addActionListener(e -> {
            JTable tablaHistorial = new JTable(modeloHistorial);

            // Renderer para pintar filas según resultado
            tablaHistorial.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    String resultado = (String) table.getValueAt(row, 2);
                    if ("Victoria".equals(resultado)) {
                        c.setBackground(Color.GREEN);
                        c.setForeground(Color.BLACK);
                    } else if ("Derrota".equals(resultado)) {
                        c.setBackground(Color.RED);
                        c.setForeground(Color.WHITE);
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                    return c;
                }
            });

            JScrollPane scrollPane = new JScrollPane(tablaHistorial);

            // Panel principal con tabla + estadísticas
            JPanel panelPrincipal = new JPanel(new BorderLayout());
            panelPrincipal.add(scrollPane, BorderLayout.CENTER);
            panelPrincipal.add(crearPanelEstadisticas(historialIntentos), BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(
                    this,
                    panelPrincipal,
                    "Historial de intentos",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });





        ejecutarRecorridoButton.addActionListener(e -> {
            // Bloquear botones...
            btnAvanzar.setEnabled(false);
            btnIzquierda.setEnabled(false);
            btnDerecha.setEnabled(false);
            ejecutarRecorridoButton.setEnabled(false);
            reiniciarButton.setEnabled(false);
            cargarButton.setEnabled(false);

            final int[] index = {0};
            Timer timer = new Timer(800, ev -> {
                if (index[0] < listaInstrucciones.size()) {
                    int instruccion = listaInstrucciones.get(index[0]);
                    ejecutarRobot(instruccion);
                    listaMovimientos.setSelectedIndex(index[0]);
                    listaMovimientos.ensureIndexIsVisible(index[0]);
                    index[0]++;
                } else {
                    ((Timer) ev.getSource()).stop();

                    // 🚨 Aquí ya sabemos si ganó o perdió
                    LocalDateTime fin = LocalDateTime.now();
                    Intento ultimo = historialIntentos.get(historialIntentos.size()-1);
                    ultimo.fechaFin = fin;
                    ultimo.gano = gano;

                    if (salioDelLimite){gano = false;}
                    modeloHistorial.addRow(new Object[]{
                            ultimo.fechaInicioMov,
                            ultimo.fechaFin,
                            ultimo.gano ? "Victoria" : "Derrota"
                    });
                    if (salioDelLimite) {gano = true;}
                    // Desbloquear botones...
                    btnAvanzar.setEnabled(true);
                    btnIzquierda.setEnabled(true);
                    btnDerecha.setEnabled(true);
                    ejecutarRecorridoButton.setEnabled(true);
                    reiniciarButton.setEnabled(true);
                    cargarButton.setEnabled(true);

                    if (!gano) {
                        JOptionPane.showMessageDialog(this, "Game Over: No has recogido todas las figuras.");
                        inicializarTablero();
                        listaInstrucciones.clear();
                        ((DefaultListModel<ImageIcon>) listaMovimientos.getModel()).clear();
                        objetosRecogidos = 0;
                    }

                    modeloLista.clear();
                    inicializarTablero();
                    listaInstrucciones.clear();
                    gano = false;
                    salioDelLimite = false;
                }
            });
            timer.start();
        });




        reiniciarButton.addActionListener(e -> {
            modeloLista.clear();
            inicializarTablero();
            listaInstrucciones.clear();
        });

    }

    private void configurarVentana() {
        setContentPane(jpPanelPrincipal);
        setTitle("Muevete con Guali");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);
        setLocationRelativeTo(null);

        // 🔹 Renderer para resaltar el movimiento actual en la lista (usa imágenes)
        listaMovimientos.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel lbl = new JLabel();
                lbl.setHorizontalAlignment(JLabel.CENTER);

                // Si el elemento es una imagen, la mostramos
                if (value instanceof ImageIcon icon) {
                    lbl.setIcon(icon);
                }

                // Estilo cuando está seleccionado (ejecutándose)
                if (isSelected) {
                    lbl.setOpaque(true);
                    lbl.setBackground(Color.YELLOW); // fondo amarillo
                    lbl.setBorder(BorderFactory.createLineBorder(Color.RED, 3)); // borde rojo
                } else {
                    lbl.setOpaque(true);
                    lbl.setBackground(Color.BLACK); // fondo normal
                    lbl.setBorder(null);
                }

                return lbl;
            }
        });
    }


    // Método para escalar imágenes
    private ImageIcon escalarImagen(String ruta, int ancho, int alto) {
        ImageIcon icon = new ImageIcon(ruta);
        Image img = icon.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void actualizarRobot() {
        int tamanioCelda = 100;



        // Selecciona la imagen según orientación
        String ruta = switch (direccion) {
            case 0 -> "src/figuras/robotArriba.png";
            case 1 -> "src/figuras/robotDerecha.png";
            case 2 -> "src/figuras/robotAbajo.png";
            case 3 -> "src/figuras/robotIzquierda.png";
            default -> "src/figuras/robotDerecha.png";
        };

        // Dibuja el robot en su posición actual SIN borrar las demás figuras
        jtTablero.setValueAt(escalarImagen(ruta, tamanioCelda, tamanioCelda), robotFila, robotColumna);
    }

    private void inicializarTablero() {
        int tamanioCelda = 100;

        DefaultTableModel modelo = new DefaultTableModel(5, 5);
        jtTablero.setModel(modelo);
        jtTablero.setRowHeight(tamanioCelda);

        for (int col = 0; col < jtTablero.getColumnCount(); col++) {
            jtTablero.getColumnModel().getColumn(col).setPreferredWidth(tamanioCelda);
        }

        Color fondoNegro = Color.BLACK;
        jtTablero.setBackground(fondoNegro);
        jtTablero.setGridColor(Color.DARK_GRAY);

        // Robot inicial en (0,0) mirando a la derecha
        robotFila = 0;
        robotColumna = 0;
        direccion = 1; // derecha
        actualizarRobot();

        // Colores disponibles
        String[] colores = {"Rojo", "Azul", "Amarillo"};
        java.util.List<String> listaColores = java.util.Arrays.asList(colores);
        java.util.Collections.shuffle(listaColores);

        // Banco de imágenes según figura y color
        String circulo = "src/figuras/circulo/ci" + listaColores.get(0) + ".png";
        String cuadrado = "src/figuras/cuadrado/c" + listaColores.get(1) + ".png";
        String triangulo = "src/figuras/triangulo/t" + listaColores.get(2) + ".png";

        // Posiciones aleatorias distintas
        java.util.List<int[]> posicionesLibres = new java.util.ArrayList<>();
        for (int fila = 0; fila < 5; fila++) {
            for (int col = 0; col < 5; col++) {
                if (!(fila == 0 && col == 0)) {
                    posicionesLibres.add(new int[]{fila, col});
                }
            }
        }
        java.util.Collections.shuffle(posicionesLibres);

        // Limpiar lista de figuras
        figuras.clear();

        // Crear y guardar las figuras con descripción
        Figura f1 = new Figura(posicionesLibres.get(0)[0], posicionesLibres.get(0)[1],
                "Círculo " + listaColores.get(0), circulo);
        Figura f2 = new Figura(posicionesLibres.get(1)[0], posicionesLibres.get(1)[1],
                "Cuadrado " + listaColores.get(1), cuadrado);
        Figura f3 = new Figura(posicionesLibres.get(2)[0], posicionesLibres.get(2)[1],
                "Triángulo " + listaColores.get(2), triangulo);

        figuras.add(f1);
        figuras.add(f2);
        figuras.add(f3);

        // Colocar las figuras en el tablero
        for (Figura fig : figuras) {
            jtTablero.setValueAt(escalarImagen(fig.ruta, tamanioCelda, tamanioCelda), fig.fila, fig.columna);
        }

        // Renderer para mostrar imágenes centradas
        jtTablero.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                if (value instanceof ImageIcon icon) {
                    JLabel lbl = new JLabel(icon);
                    lbl.setHorizontalAlignment(JLabel.CENTER);
                    lbl.setOpaque(true);
                    lbl.setBackground(fondoNegro);
                    return lbl;
                }
                JLabel lbl = new JLabel(value != null ? value.toString() : "");
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setOpaque(true);
                lbl.setBackground(fondoNegro);
                return lbl;
            }
        });
    }

    private void ejecutarRobot(int movimiento) {
        int filaAnterior = robotFila;
        int columnaAnterior = robotColumna;

        // Limpia la celda anterior del robot
        jtTablero.setValueAt(null, robotFila, robotColumna);

        // Actualiza posición
        switch (movimiento) {
            case 1 -> {
                if (direccion == 0) robotFila--;
                else if (direccion == 1) robotColumna++;
                else if (direccion == 2) robotFila++;
                else if (direccion == 3) robotColumna--;
            }
            case 2 -> direccion = (direccion + 1) % 4;
            case 3 -> direccion = (direccion + 3) % 4;
        }

        //  Verificar límites
        if (robotFila < 0 || robotFila >= jtTablero.getRowCount() ||
                robotColumna < 0 || robotColumna >= jtTablero.getColumnCount()) {
            JOptionPane.showMessageDialog(this, "¡Game Over! El robot salió del tablero.");
            inicializarTablero();
            listaInstrucciones.clear();
            ((DefaultListModel<ImageIcon>) listaMovimientos.getModel()).clear();
            objetosRecogidos = 0;
            salioDelLimite = true;
            return;
        }

        // 🔹 Redibujar figura en la celda anterior SIEMPRE (independientemente de recogida)
        for (Figura fig : figuras) {
            if (fig.fila == filaAnterior && fig.columna == columnaAnterior) {
                jtTablero.setValueAt(escalarImagen(fig.ruta, 100, 100), fig.fila, fig.columna);
            }
        }

        // 🔹 Verificar si hay figura en la nueva celda
        for (Figura fig : figuras) {
            if (fig.fila == robotFila && fig.columna == robotColumna && !fig.recogida) {
                JOptionPane.showMessageDialog(this, "Has encontrado el: " + fig.descripcion);
                objetosRecogidos++;
                fig.recogida = true;
                // ❌ No la borramos permanentemente, porque debe seguir existiendo en el tablero
                jtTablero.setValueAt(null, fig.fila, fig.columna); // se oculta mientras el robot está encima
            }
        }

        // 🎉 Verificar victoria
        if (objetosRecogidos == figuras.size()) {
            JOptionPane.showMessageDialog(this, "¡Has recogido todos los objetos!\n🎉 ¡Has ganado el juego!");
            inicializarTablero();
            listaInstrucciones.clear();
            ((DefaultListModel<ImageIcon>) listaMovimientos.getModel()).clear();
            objetosRecogidos = 0;

            gano = true;
        }

        // Dibujar robot en nueva posición
        actualizarRobot();
    }


    public static class Intento {
        LocalDateTime fechaInicioMov;   // cuando el jugador empieza a ingresar movimientos
        LocalDateTime fechaFin;         // cuando se ejecuta recorrido
        boolean gano;                   // true = victoria, false = derrota

        Intento(LocalDateTime inicioMov, LocalDateTime fin, boolean gano) {
            this.fechaInicioMov = inicioMov;
            this.fechaFin = fin;
            this.gano = gano;
        }

        @Override
        public String toString() {
            return "Inicio movimientos: " + fechaInicioMov +
                    "\nFin recorrido: " + fechaFin +
                    "\nResultado: " + (gano ? "Victoria" : "Derrota");
        }
    }





    public static class Figura {
        int fila;
        int columna;
        String descripcion; // Ej: "Triángulo Rojo"
        String ruta;        // ruta de la imagen
        boolean recogida = false;
        boolean visible = true;

        Figura(int fila, int columna, String descripcion, String ruta) {
            this.fila = fila;
            this.columna = columna;
            this.descripcion = descripcion;
            this.ruta = ruta;
        }
    }

    private void createUIComponents() {
        jtTablero = new JTable(new DefaultTableModel(5, 5));
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App ventana = new App();
            ventana.setVisible(true);
        });
    }
}
