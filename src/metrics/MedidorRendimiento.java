package metrics;

import java.util.ArrayList;
import java.util.List;

public class MedidorRendimiento {

    private long tiempoInicio;
    private long tiempoFin;
    private List<Long> tiemposPorTurno;
    private int totalJugadas;
    private int numeroHilos;

    public MedidorRendimiento(int numeroHilos) {
        this.numeroHilos      = numeroHilos;
        this.tiemposPorTurno  = new ArrayList<>();
        this.totalJugadas     = 0;
    }

    public void iniciarSimulacion() {
        tiempoInicio = System.nanoTime();
    }

    public void finalizarSimulacion() {
        tiempoFin = System.nanoTime();
    }

    public void registrarTurno(long tiempoNanos) {
        tiemposPorTurno.add(tiempoNanos);
        totalJugadas++;
    }

    public double getTiempoTotalMs() {
        return (tiempoFin - tiempoInicio) / 1_000_000.0;
    }

    public double getTiempoPromedioTurnoMs() {
        if (tiemposPorTurno.isEmpty()) return 0;
        long suma = 0;
        for (long t : tiemposPorTurno) suma += t;
        return (suma / (double) tiemposPorTurno.size()) / 1_000_000.0;
    }

    public double getThroughput() {
        double totalSegundos = (tiempoFin - tiempoInicio) / 1_000_000_000.0;
        if (totalSegundos == 0) return 0;
        return totalJugadas / totalSegundos;
    }

    public void imprimirReporte() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║      REPORTE DE RENDIMIENTO          ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf( "║  Hilos configurados  : %-14d║%n", numeroHilos);
        System.out.printf( "║  Total jugadas       : %-14d║%n", totalJugadas);
        System.out.printf( "║  Tiempo total (ms)   : %-14.3f║%n", getTiempoTotalMs());
        System.out.printf( "║  Promedio/turno (ms) : %-14.3f║%n", getTiempoPromedioTurnoMs());
        System.out.printf( "║  Throughput (j/s)    : %-14.2f║%n", getThroughput());
        System.out.println("╚══════════════════════════════════════╝");
    }

}