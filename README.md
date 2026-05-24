# 🎮 Juego de la Rejilla — Java Threads

Simulación concurrente de un juego sobre una rejilla donde múltiples jugadores compiten por cerrar una curva sobre el tablero. Implementado en **Java** usando **Threads nativos** como parte de un análisis comparativo de rendimiento y escalabilidad entre modelos de concurrencia.

> Proyecto académico — Maestría en Ingeniería de Software Concurrente  
> Comparativa: Java Threads vs C + OpenMP

---

## 📐 Descripción del Juego

La rejilla es un **grafo de adyacencia** donde cada nodo es un punto y cada arista es una conexión entre dos puntos vecinos. Los jugadores compiten por ocupar aristas hasta que uno logra formar un **ciclo cerrado** con sus conexiones.

- Cada turno, el jugador activo aplica una jugada oficial
- Mientras tanto, los jugadores inactivos **piensan en paralelo** usando `H` hilos cada uno
- Gana el primero en cerrar una curva
- Si el tablero se llena sin ganador, el resultado es **empate**

---

## 🏗️ Arquitectura del Proyecto

```
JuegoRejillaJava/
│
├── src/
│   ├── app/
│   │   └── SistemaSimulacion.java     # Orquestador principal
│   │
│   ├── concurrent/
│   │   └── MotorConcurrente.java      # Motor de hilos por jugador
│   │
│   ├── model/
│   │   ├── Rejilla.java               # Grafo de adyacencia + visualización
│   │   ├── Jugada.java                # Modelo de una jugada (puntoA, puntoB)
│   │   ├── Jugador.java               # Jugador con color ANSI y candidatas
│   │   └── ListaJugadas.java          # Pool global de candidatas (thread-safe)
│   │
│   ├── validation/
│   │   └── Validador.java             # Validación de parámetros de entrada
│   │
│   └── metrics/
│       └── MedidorRendimiento.java    # Métricas de rendimiento y throughput
│
└── bin/                               # Clases compiladas
```

---

## 📊 Diagrama de Clases
<img width="1536" height="1024" alt="diagrama de clases" src="https://github.com/user-attachments/assets/c8660252-f848-4a2a-8f0f-9aa4276dc363" />

---

## ⚙️ Modelo Concurrente

```
TURNO DE J1 (activo):
  ├── J1 → aplica jugada oficial (secuencial)
  ├── J2 → H hilos generando candidatas (paralelo)
  ├── J3 → H hilos generando candidatas (paralelo)
  └── J4 → H hilos generando candidatas (paralelo)

TURNO DE J2 (activo):
  ├── J2 → aplica candidatas precalculadas (secuencial)
  ├── J1 → H hilos generando candidatas (paralelo)
  ├── J3 → H hilos generando candidatas (paralelo)
  └── J4 → H hilos generando candidatas (paralelo)
```

### Sincronización
| Recurso | Mecanismo |
|---|---|
| `ListaJugadas` | `synchronized` en todos los métodos |
| `Rejilla` | `synchronized` en lectura y escritura |
| `Jugador.jugadasCandidatas` | `synchronized` en agregar y leer |
| Coordinación de hilos | `Thread.join()` |

---

## 🚀 Instalación y Ejecución

### Requisitos

- Java JDK 11 o superior
- Sistema operativo: Windows, Linux o macOS
- Terminal con soporte ANSI (para colores por jugador)

> En Windows se recomienda usar **Windows Terminal** o **VS Code Terminal** para ver los colores correctamente.

### Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/JuegoRejillaJava.git
cd JuegoRejillaJava
```

### Compilar

```bash
javac -d bin src/app/*.java src/model/*.java src/concurrent/*.java src/validation/*.java src/metrics/*.java
```

### Ejecutar

```bash
java -cp bin app.SistemaSimulacion <filas> <columnas> <jugadores> <hilos>
```

### Ejemplos

```bash
# Rejilla 4x4, 2 jugadores, 3 hilos
java -cp bin app.SistemaSimulacion 4 4 2 3

# Rejilla 5x5, 3 jugadores, 4 hilos
java -cp bin app.SistemaSimulacion 5 5 3 4

# Rejilla 6x6, 4 jugadores, 8 hilos
java -cp bin app.SistemaSimulacion 6 6 4 8

# Rejilla mínima, 2 jugadores, 1 hilo
java -cp bin app.SistemaSimulacion 3 3 2 1
```

---

## 📋 Parámetros

| Parámetro | Descripción | Mínimo | Máximo |
|---|---|---|---|
| `filas` | Número de filas de la rejilla | 3 | 10 |
| `columnas` | Número de columnas de la rejilla | 3 | 10 |
| `jugadores` | Número de jugadores | 2 | 6 |
| `hilos` | Hilos por jugador inactivo por turno | 1 | sin límite |

### Jugadores y colores ANSI

| Jugador | Color |
|---|---|
| Jugador 1 | 🔴 Rojo |
| Jugador 2 | 🔵 Azul |
| Jugador 3 | 🟢 Verde |
| Jugador 4 | 🟡 Amarillo |
| Jugador 5 | 🟣 Magenta |
| Jugador 6 | 🩵 Cyan |

---

## 📈 Métricas de Rendimiento

Al finalizar cada partida se imprime un reporte:

```
╔══════════════════════════════════════╗
║      REPORTE DE RENDIMIENTO          ║
╠══════════════════════════════════════╣
║  Hilos configurados  : 3             ║
║  Total jugadas       : 21            ║
║  Tiempo total (ms)   : 115.862       ║
║  Promedio/turno (ms) : 1.873         ║
║  Throughput (j/s)    : 181.25        ║
╚══════════════════════════════════════╝
```

### Análisis de escalabilidad recomendado

Ejecutar con la misma rejilla variando únicamente el número de hilos:

```bash
java -cp bin app.SistemaSimulacion 6 6 2 1
java -cp bin app.SistemaSimulacion 6 6 2 2
java -cp bin app.SistemaSimulacion 6 6 2 4
java -cp bin app.SistemaSimulacion 6 6 2 8
```

Comparar el throughput resultante para analizar el impacto de la concurrencia.

---

## 🔬 Contexto Académico

Este proyecto es la **Implementación 1** de un análisis comparativo de concurrencia:

| Implementación | Tecnología | Repositorio |
|---|---|---|
| Implementación 1 | Java Threads | *(este repositorio)* |
| Implementación 2 | C + OpenMP | *(próximamente)* |

Ambas implementaciones ejecutan exactamente la misma lógica para permitir comparación objetiva de tiempo de ejecución, escalabilidad, uso de hilos, eficiencia concurrente y comportamiento cuando la rejilla aumenta de tamaño.

---

## 📁 Visualización del Tablero

```
  ( 0)─────( 1)─────( 2)─────( 3)
   │        │        │        │
   │        │        │        │
   │        │        │        │
  ( 4)─────( 5)─────( 6)─────( 7)
   │        │        │        │
   │        │        │        │
   │        │        │        │
  ( 8)─────( 9)─────(10)─────(11)
   │        │        │        │
   │        │        │        │
   │        │        │        │
  (12)─────(13)─────(14)─────(15)
```

Las conexiones ocupadas se muestran en el color del jugador que las tomó.
