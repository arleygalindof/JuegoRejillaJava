package validation;

public class Validador {

    // Colores disponibles en consola para distinguir jugadores
    private static final int MAX_JUGADORES_SOPORTADOS = 6;

    // Tamaño visual manejable en pantalla
    private static final int MAX_FILAS    = 10;
    private static final int MAX_COLUMNAS = 10;

    /**
     * Valida todos los parametros de entrada antes de iniciar.
     * Si algo falla lanza IllegalArgumentException con mensaje claro.
     */
    public static void validarParametros(
            int filas,
            int columnas,
            int jugadores,
            int hilos) {

        validarFilas(filas);
        validarColumnas(columnas);
        validarJugadores(jugadores);
        validarHilos(hilos);
    }

    private static void validarFilas(int filas) {

        if (filas < 3) {
            throw new IllegalArgumentException(
                "[ERROR] Filas invalidas: minimo permitido es 3. " +
                "Valor recibido: " + filas);
        }

        if (filas > MAX_FILAS) {
            throw new IllegalArgumentException(
                "[ERROR] Filas invalidas: maximo permitido es "
                + MAX_FILAS + ". Valor recibido: " + filas);
        }
    }

    private static void validarColumnas(int columnas) {

        if (columnas < 3) {
            throw new IllegalArgumentException(
                "[ERROR] Columnas invalidas: minimo permitido es 3. " +
                "Valor recibido: " + columnas);
        }

        if (columnas > MAX_COLUMNAS) {
            throw new IllegalArgumentException(
                "[ERROR] Columnas invalidas: maximo permitido es "
                + MAX_COLUMNAS + ". Valor recibido: " + columnas);
        }
    }

    private static void validarJugadores(int jugadores) {

        if (jugadores < 2) {
            throw new IllegalArgumentException(
                "[ERROR] Jugadores invalidos: minimo permitido es 2. " +
                "Valor recibido: " + jugadores);
        }

        if (jugadores > MAX_JUGADORES_SOPORTADOS) {
            throw new IllegalArgumentException(
                "[ERROR] Jugadores invalidos: maximo soportado por consola es "
                + MAX_JUGADORES_SOPORTADOS + ". Valor recibido: " + jugadores);
        }
    }

    private static void validarHilos(int hilos) {

        if (hilos < 1) {
            throw new IllegalArgumentException(
                "[ERROR] Hilos invalidos: debe ser mayor a 0. " +
                "Valor recibido: " + hilos);
        }
    }

}