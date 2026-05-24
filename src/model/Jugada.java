package model;

public class Jugada {

    private int puntoA;
    private int puntoB;
    private int jugadorId;

    public Jugada(int puntoA, int puntoB, int jugadorId) {

        this.puntoA = puntoA;
        this.puntoB = puntoB;
        this.jugadorId = jugadorId;

    }

public int getPuntoA() {

    return puntoA;

}

public int getPuntoB() {

    return puntoB;

}

public int getJugadorId() {

    return jugadorId;

}

}