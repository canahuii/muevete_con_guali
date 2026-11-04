 import java.awt.*;

public class Figuras {
    private String tipo;   // "circulo", "cuadrado", "piramide"
    private Color color;
    private int x, y;      // posición en el tablero

    public Figuras(String tipo, Color color, int x, int y) {
        this.tipo = tipo;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public void dibujar(Graphics g, int cellSize) {
        g.setColor(color);
        int px = x * cellSize + 10; // coordenada X en píxeles
        int py = y * cellSize + 10; // coordenada Y en píxeles

        switch (tipo) {
            case "circulo" -> g.fillOval(px, py, cellSize - 20, cellSize - 20);
            case "cuadrado" -> g.fillRect(px, py, cellSize - 20, cellSize - 20);
            case "piramide" -> {
                int[] xs = {px, px + (cellSize / 2), px + cellSize - 20};
                int[] ys = {py + cellSize - 20, py, py + cellSize - 20};
                g.fillPolygon(xs, ys, 3);
            }
        }
    }

    public String getDescripcion() {
        return tipo + " de color " + color.toString();
    }
}
