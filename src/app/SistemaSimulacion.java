package app;

import concurrent.MotorConcurrente;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import metrics.MedidorRendimiento;
import model.Jugada;
import model.Jugador;
import model.ListaJugadas;
import model.Rejilla;
import validation.Validador;

public class SistemaSimulacion {

    public static void main(String[] args) {

        // ══════════════════════════════════════════
        // 1. PARAMETROS DE ENTRADA
        // ══════════════════════════════════════════

        if (args.length < 4 || args.length > 5) {
            System.out.println("[ERROR] Uso correcto:");
            System.out.println("  java -cp bin app.SistemaSimulacion <filas> <columnas> <jugadores> <hilos> [benchmark]");
            System.out.println("  Ejemplo: java -cp bin app.SistemaSimulacion 4 4 2 3");
            System.out.println("  Benchmark: java -cp bin app.SistemaSimulacion 8 8 2 4 benchmark");
            System.exit(1);
            return;
        }

        int filas;
        int columnas;
        int jugadores;
        int hilos;

        try {
            filas     = Integer.parseInt(args[0]);
            columnas  = Integer.parseInt(args[1]);
            jugadores = Integer.parseInt(args[2]);
            hilos     = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Todos los parametros deben ser enteros positivos.");
            System.out.println("  Ejemplo: java -cp bin app.SistemaSimulacion 4 4 2 3");
            System.exit(1);
            return;
        }

        boolean modoBenchmark = args.length == 5
            && args[4].equalsIgnoreCase("benchmark");

        // Numero exacto de aristas disponibles en la rejilla
        int turnosMaximos = modoBenchmark
            ? (filas * (columnas - 1)) + ((filas - 1) * columnas)
            : Integer.MAX_VALUE;

        // ══════════════════════════════════════════
        // 2. VALIDACION
        // ══════════════════════════════════════════

        try {
            Validador.validarParametros(filas, columnas, jugadores, hilos);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(1);
            return;
        }

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║       JUEGO DE LA REJILLA            ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf( "║  Rejilla    : %dx%-22d║%n", filas, columnas);
        System.out.printf( "║  Jugadores  : %-23d║%n", jugadores);
        System.out.printf( "║  Hilos      : %-23d║%n", hilos);
        if (modoBenchmark) {
            System.out.printf("║  [BENCHMARK] Turnos fijos : %-10d║%n",
                turnosMaximos);
        }
        System.out.println("╚══════════════════════════════════════╝");

        // ══════════════════════════════════════════
        // 3. INICIALIZACION
        // ══════════════════════════════════════════

        Rejilla rejilla            = new Rejilla(filas, columnas);
        ListaJugadas listaJugadas  = new ListaJugadas();
        MedidorRendimiento medidor = new MedidorRendimiento(hilos);

        List<Jugador> listaJugadores = new ArrayList<>();
        for (int i = 1; i <= jugadores; i++) {
            listaJugadores.add(new Jugador(i, "Jugador" + i));
        }

        int turnoActual        = 0;
        boolean juegoTerminado = false;
        int jugadorGanadorId   = -1;

        // ══════════════════════════════════════════
        // 4. CICLO PRINCIPAL
        // ══════════════════════════════════════════

        medidor.iniciarSimulacion();

        while (!juegoTerminado) {

            Jugador jugadorActivo = listaJugadores.get(
                turnoActual % jugadores);

            jugadorActivo.activarTurno();

            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.printf( "║  TURNO %-2d - %-33s     ║%n",
                (turnoActual + 1),
                jugadorActivo.getNombreColoreado() + "\u001B[0m");
            System.out.println("╚══════════════════════════════════════╝");

            long inicioTurno = System.nanoTime();

            // ── 4.1 FASE PARALELA ──────────────────
            // Jugadores INACTIVOS piensan con H hilos cada uno

            listaJugadas.limpiar();
            List<MotorConcurrente> motores = new ArrayList<>();

            for (Jugador jugadorInactivo : listaJugadores) {

                if (jugadorInactivo.getId() == jugadorActivo.getId()) continue;

                jugadorInactivo.limpiarCandidatas();

                System.out.println("\n  >> "
                    + jugadorInactivo.getNombreColoreado()
                    + " pensando con " + hilos + " hilo(s)...");

                for (int h = 0; h < hilos; h++) {

                    MotorConcurrente motor = new MotorConcurrente(
                        jugadorInactivo,
                        listaJugadas,
                        rejilla,
                        1
                    );

                    motores.add(motor);
                    motor.start();
                }
            }

            // Esperar que todos los hilos terminen
            for (MotorConcurrente motor : motores) {
                try {
                    motor.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("[ERROR] Hilo interrumpido.");
                }
            }

            // ── 4.2 FASE SECUENCIAL ────────────────
            // Jugador activo aplica candidatas precalculadas

            boolean jugadaAplicada = false;

            List<Jugada> candidatasActivo = jugadorActivo.obtenerCandidatas();

            // Fallback 1: filtrar pool global por jugador activo
            if (candidatasActivo.isEmpty()) {
                candidatasActivo = new ArrayList<>();
                for (Jugada j : listaJugadas.obtenerTodas()) {
                    if (j.getJugadorId() == jugadorActivo.getId()) {
                        candidatasActivo.add(j);
                    }
                }
            }

            // Fallback 2: busqueda aleatoria
            if (candidatasActivo.isEmpty()) {
                Random rnd      = new Random();
                int total       = rejilla.getTotalPuntos();
                int maxIntentos = total * total * 10;
                int intentos    = 0;
                while (intentos < maxIntentos) {
                    intentos++;
                    int a = rnd.nextInt(total);
                    int b = rnd.nextInt(total);
                    if (a == b) continue;
                    if (!rejilla.esConexionValida(a, b)) continue;
                    if (!rejilla.estaLibre(a, b)) continue;
                    candidatasActivo = new ArrayList<>();
                    candidatasActivo.add(
                        new Jugada(a, b, jugadorActivo.getId()));
                    break;
                }
            }

            // Fallback 3: escaneo lineal garantizado
            if (candidatasActivo.isEmpty()) {
                int total = rejilla.getTotalPuntos();
                outer:
                for (int a = 0; a < total; a++) {
                    for (int b = a + 1; b < total; b++) {
                        if (rejilla.estaLibre(a, b)) {
                            candidatasActivo = new ArrayList<>();
                            candidatasActivo.add(
                                new Jugada(a, b, jugadorActivo.getId()));
                            break outer;
                        }
                    }
                }
            }

            System.out.println("\n  >> "
                + jugadorActivo.getNombreColoreado()
                + " aplica su jugada oficial...");

            for (Jugada candidata : candidatasActivo) {

                if (rejilla.estaLibre(
                        candidata.getPuntoA(),
                        candidata.getPuntoB())) {

                    boolean conectado = rejilla.conectar(
                        candidata.getPuntoA(),
                        candidata.getPuntoB(),
                        jugadorActivo.getId());

                    if (conectado) {
                        System.out.println(
                            "  Jugada oficial: "
                            + candidata.getPuntoA()
                            + " -> "
                            + candidata.getPuntoB()
                            + " por " + jugadorActivo.getNombreColoreado());

                        jugadaAplicada = true;
                        break;
                    }
                }
            }

            listaJugadas.limpiar();

            // ── 4.3 VERIFICACION DE ESTADO ─────────

            long finTurno = System.nanoTime();
            medidor.registrarTurno(finTurno - inicioTurno);

            rejilla.imprimir();

            // Verificar empate y victoria solo fuera de benchmark
            if (!modoBenchmark) {

                if (!jugadaAplicada || !rejilla.hayJugadasDisponibles()) {
                    System.out.println("\n  EMPATE: no hay mas jugadas disponibles.");
                    juegoTerminado = true;
                    break;
                }

                if (rejilla.tieneCurvaCerrada(jugadorActivo.getId())) {
                    System.out.println(
                        "\n  VICTORIA: "
                        + jugadorActivo.getNombreColoreado()
                        + " cerro una curva!");
                    jugadorGanadorId = jugadorActivo.getId();
                    juegoTerminado   = true;
                    break;
                }
            }

            // Verificar limite de turnos en benchmark
            if (modoBenchmark && (turnoActual + 1) >= turnosMaximos) {
                System.out.println("\n  [BENCHMARK] Limite de turnos alcanzado.");
                juegoTerminado = true;
                break;
            }

            // Tablero lleno en benchmark
            if (modoBenchmark && !rejilla.hayJugadasDisponibles()) {
                System.out.println("\n  [BENCHMARK] Tablero lleno. Simulacion completa.");
                juegoTerminado = true;
                break;
            }

            jugadorActivo.desactivarTurno();
            turnoActual++;
        }

        // ══════════════════════════════════════════
        // 5. RESULTADO FINAL Y METRICAS
        // ══════════════════════════════════════════

        medidor.finalizarSimulacion();

        System.out.println("\n╔══════════════════════════════════════╗");
        if (jugadorGanadorId != -1) {
            System.out.printf("║  GANADOR: %-40s║%n",
                listaJugadores.get(jugadorGanadorId - 1).getNombreColoreado()
                + "\u001B[0m");
        } else if (modoBenchmark) {
            System.out.println("║  RESULTADO: BENCHMARK COMPLETADO     ║");
        } else {
            System.out.println("║  RESULTADO: EMPATE                   ║");
        }
        System.out.println("╚══════════════════════════════════════╝");

        medidor.imprimirReporte();
    }
}