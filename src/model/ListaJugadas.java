package model;

import java.util.ArrayList;
import java.util.List;

public class ListaJugadas {

    private List<Jugada> jugadas;

    public ListaJugadas() {

        jugadas = new ArrayList<>();

    }

    public synchronized void agregarJugada(Jugada jugada) {

        jugadas.add(jugada);

    }   
    
    public synchronized int obtenerCantidadJugadas() {

        return jugadas.size();

    }

    public synchronized List<Jugada> obtenerTodas() {

        return new ArrayList<>(jugadas);

    }    

    public synchronized Jugada obtenerJugada(int indice) {

        return jugadas.get(indice);

    }    

    public synchronized void limpiar() {

        jugadas.clear();

    }    

}