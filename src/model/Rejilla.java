package model;

public class Rejilla {

    private int filas;
    private int columnas;

    private int[][] conexiones;

    public Rejilla(int filas, int columnas) {
        this.filas    = filas;
        this.columnas = columnas;

        int cantidadPuntos = filas * columnas;
        conexiones = new int[cantidadPuntos][cantidadPuntos];

        inicializarConexiones();
        habilitarConexionesValidas();
    }

    // ══════════════════════════════════════════
    // INICIALIZACION
    // ══════════════════════════════════════════

    private void inicializarConexiones() {
        for (int i = 0; i < conexiones.length; i++) {
            for (int j = 0; j < conexiones[i].length; j++) {
                conexiones[i][j] = -1;
            }
        }
    }

    private void habilitarConexionesValidas() {
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {

                int puntoActual = fila * columnas + columna;

                if (columna < columnas - 1) {
                    int derecha = puntoActual + 1;
                    conexiones[puntoActual][derecha] = 0;
                    conexiones[derecha][puntoActual] = 0;
                }

                if (fila < filas - 1) {
                    int abajo = puntoActual + columnas;
                    conexiones[puntoActual][abajo] = 0;
                    conexiones[abajo][puntoActual] = 0;
                }
            }
        }
    }

    // ══════════════════════════════════════════
    // CONSULTAS
    // ══════════════════════════════════════════

    public int getTotalPuntos() {
        return filas * columnas;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public synchronized boolean esConexionValida(int puntoA, int puntoB) {
        if (puntoA < 0 || puntoB < 0)       return false;
        if (puntoA >= conexiones.length)     return false;
        if (puntoB >= conexiones.length)     return false;
        return conexiones[puntoA][puntoB] != -1;
    }

    public synchronized boolean estaLibre(int puntoA, int puntoB) {
        if (!esConexionValida(puntoA, puntoB)) return false;
        return conexiones[puntoA][puntoB] == 0;
    }

    public synchronized boolean hayJugadasDisponibles() {
        for (int i = 0; i < conexiones.length; i++) {
            for (int j = i + 1; j < conexiones[i].length; j++) {
                if (conexiones[i][j] == 0) return true;
            }
        }
        return false;
    }

    // ══════════════════════════════════════════
    // MODIFICACION
    // ══════════════════════════════════════════

    public synchronized boolean conectar(
            int puntoA,
            int puntoB,
            int jugadorId) {

        if (!estaLibre(puntoA, puntoB)) return false;

        conexiones[puntoA][puntoB] = jugadorId;
        conexiones[puntoB][puntoA] = jugadorId;

        return true;
    }

    // ══════════════════════════════════════════
    // DETECCION DE VICTORIA
    // ══════════════════════════════════════════

    public synchronized boolean tieneCurvaCerrada(int jugadorId) {
        int n = conexiones.length;
        boolean[] visitado = new boolean[n];

        for (int inicio = 0; inicio < n; inicio++) {
            for (int k = 0; k < n; k++) visitado[k] = false;
            if (dfsCicloCerrado(inicio, -1, inicio, jugadorId, visitado)) {
                return true;
            }
        }
        return false;
    }

    private boolean dfsCicloCerrado(
            int actual,
            int padre,
            int inicio,
            int jugadorId,
            boolean[] visitado) {

        visitado[actual] = true;

        for (int vecino = 0; vecino < conexiones[actual].length; vecino++) {

            if (conexiones[actual][vecino] != jugadorId) continue;

            if (!visitado[vecino]) {
                if (dfsCicloCerrado(vecino, actual, inicio,
                                    jugadorId, visitado)) {
                    return true;
                }
            } else if (vecino != padre && vecino == inicio) {
                return true;
            }
        }
        return false;
    }

    // ══════════════════════════════════════════
    // VISUALIZACION
    // ══════════════════════════════════════════
public void imprimir() {

    System.out.println();

    for (int fila = 0; fila < filas; fila++) {

        StringBuilder lineaPuntos = new StringBuilder("  ");

        for (int col = 0; col < columnas; col++) {

            int punto = fila * columnas + col;
            lineaPuntos.append(String.format("(%2d)", punto));

            if (col < columnas - 1) {
                int estado = conexiones[punto][punto + 1];
                lineaPuntos.append(obtenerHorizontal(estado));
            }
        }

        System.out.println(lineaPuntos);

if (fila < filas - 1) {

            StringBuilder lineaVertical = new StringBuilder("  ");

            for (int col = 0; col < columnas; col++) {

                int punto = fila * columnas + col;
                int abajo = punto + columnas;
                int estado = conexiones[punto][abajo];

                lineaVertical.append(" ");
                lineaVertical.append(obtenerVertical(estado));

                if (col < columnas - 1) {
                    lineaVertical.append("       ");
                }
            }

            String vertical = lineaVertical.toString();
            System.out.println(vertical);
            System.out.println(vertical);
            System.out.println(vertical);
        }
    }

    System.out.println();
}

private String obtenerHorizontal(int estado) {
    switch (estado) {
        case  0: return "\u2500\u2500\u2500\u2500\u2500";
        default:
            return Jugador.COLORES[estado - 1]
                 + "\u2550\u2550\u2550\u2550\u2550"
                 + Jugador.RESET;
    }
}

private String obtenerVertical(int estado) {
    switch (estado) {
        case  0: return "\u2502";
        default:
            return Jugador.COLORES[estado - 1]
                 + "\u2551"
                 + Jugador.RESET;
    }
}



}