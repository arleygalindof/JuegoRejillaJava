package model;

import java.util.ArrayList;
import java.util.List;

public class Jugador {

    // Identificador unico del jugador (1, 2, 3...)
    private int id;

    // Nombre para mostrar en consola
    private String nombre;

    // Color ANSI para distinguir visualmente en consola
    private String colorAnsi;

    // Indica si es el turno actual de este jugador
    private boolean esTurnoActual;

    // Jugadas candidatas precalculadas por sus hilos
    private List<Jugada> jugadasCandidatas;

    // Colores ANSI disponibles indexados por ID de jugador
    public static final String[] COLORES = {
        "\u001B[31m",   // Rojo    - jugador 1
        "\u001B[34m",   // Azul    - jugador 2
        "\u001B[32m",   // Verde   - jugador 3
        "\u001B[33m",   // Amarillo- jugador 4
        "\u001B[35m",   // Magenta - jugador 5
        "\u001B[36m"    // Cyan    - jugador 6
    };

    public static final String RESET = "\u001B[0m";

    public Jugador(int id, String nombre) {
        this.id              = id;
        this.nombre          = nombre;
        this.colorAnsi       = COLORES[id - 1];
        this.esTurnoActual   = false;
        this.jugadasCandidatas = new ArrayList<>();
    }

    // --- Getters ---

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getColorAnsi() {
        return colorAnsi;
    }

    public boolean isEsTurnoActual() {
        return esTurnoActual;
    }

    // --- Control de turno ---

    public void activarTurno() {
        this.esTurnoActual = true;
    }

    public void desactivarTurno() {
        this.esTurnoActual = false;
    }

    // --- Jugadas candidatas ---

    public synchronized void agregarCandidata(Jugada jugada) {
        jugadasCandidatas.add(jugada);
    }

    public synchronized List<Jugada> obtenerCandidatas() {
        return new ArrayList<>(jugadasCandidatas);
    }

    public synchronized void limpiarCandidatas() {
        jugadasCandidatas.clear();
    }

    // --- Representacion visual ---

    public String getNombreColoreado() {
        return colorAnsi + nombre + RESET;
    }

    @Override
    public String toString() {
        return getNombreColoreado()
             + " (ID=" + id
             + ", turno=" + esTurnoActual + ")";
    }

}