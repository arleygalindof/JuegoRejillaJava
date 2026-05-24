package concurrent;

import java.util.Random;
import model.Jugada;
import model.Jugador;
import model.ListaJugadas;
import model.Rejilla;

public class MotorConcurrente extends Thread {

    // Jugador al que pertenece este hilo
    private Jugador jugador;

    // Lista global de candidatas del turno actual
    private ListaJugadas listaJugadas;

    // Referencia al tablero actual
    private Rejilla rejilla;

    // Cuantas candidatas debe generar este hilo
    private int cantidadCandidatas;

    public MotorConcurrente(
            Jugador jugador,
            ListaJugadas listaJugadas,
            Rejilla rejilla,
            int cantidadCandidatas) {

        this.jugador           = jugador;
        this.listaJugadas      = listaJugadas;
        this.rejilla           = rejilla;
        this.cantidadCandidatas = cantidadCandidatas;
    }

@Override
public void run() {

    Random random = new Random();
    int totalPuntos  = rejilla.getTotalPuntos();
    int generadas    = 0;
    int intentos     = 0;
    int maxIntentos  = totalPuntos * totalPuntos;

    while (generadas < cantidadCandidatas && intentos < maxIntentos) {

        intentos++;

        int puntoA = random.nextInt(totalPuntos);
        int puntoB = random.nextInt(totalPuntos);

        if (puntoA == puntoB) continue;
        if (!rejilla.esConexionValida(puntoA, puntoB)) continue;
        if (!rejilla.estaLibre(puntoA, puntoB)) continue;

        Jugada jugada = new Jugada(puntoA, puntoB, jugador.getId());

        listaJugadas.agregarJugada(jugada);
        jugador.agregarCandidata(jugada);

        System.out.println(
            jugador.getNombreColoreado()
            + " | Hilo " + this.getId()
            + " | candidata: " + puntoA
            + " -> " + puntoB);

        generadas++;
    }
}

}