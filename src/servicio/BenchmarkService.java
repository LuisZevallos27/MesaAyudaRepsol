package servicio;

import estructuras.ArbolAVL;
import java.util.Random;

/**
 * Análisis empírico de rendimiento. Compara el ArbolAVL propio contra una lista
 * enlazada simple (estructura alternativa) con N = 10^3, 10^4 y 10^5 elementos.
 *
 * - Inserción masiva de N claves aleatorias (sin repetir). - Búsqueda de 1000
 * claves existentes elegidas al azar.
 *
 * Tiempos medidos con System.nanoTime(), promedio de varias repeticiones.
 */
public class BenchmarkService {

    private static final int[] TAMANIOS = {1_000, 10_000, 100_000};
    private static final int BUSQUEDAS = 1_000;
    private static final int REPETICIONES = 3;
    private static final long SEMILLA = 42L; // resultados reproducibles

    // ---------------- Estructura alternativa: lista simple ----------------
    private static class NodoSimple {

        final int clave;
        NodoSimple siguiente;

        NodoSimple(int clave) {
            this.clave = clave;
        }
    }

    private static class ListaSimple {

        private NodoSimple cabeza;

        /**
         * O(1): inserta al inicio.
         */
        void insertar(int clave) {
            NodoSimple nuevo = new NodoSimple(clave);
            nuevo.siguiente = cabeza;
            cabeza = nuevo;
        }

        /**
         * O(n): búsqueda lineal.
         */
        boolean buscar(int clave) {
            NodoSimple actual = cabeza;
            while (actual != null) {
                if (actual.clave == clave) {
                    return true;
                }
                actual = actual.siguiente;
            }
            return false;
        }
    }

    // ---------------- Ejecución ----------------
    public void ejecutar() {
        System.out.println("==========================================================");
        System.out.println("  BENCHMARK: AVL propio vs. Lista enlazada simple");
        System.out.println("  Repeticiones por medición: " + REPETICIONES
                + " | Búsquedas por prueba: " + BUSQUEDAS);
        System.out.println("==========================================================");
        System.out.println("Calentando la JVM...");
        calentar();

        int filas = TAMANIOS.length;
        long[] insAvl = new long[filas];
        long[] insLista = new long[filas];
        long[] busAvl = new long[filas];
        long[] busLista = new long[filas];
        int[] alturas = new int[filas];

        Random azar = new Random(SEMILLA);

        for (int i = 0; i < filas; i++) {
            int n = TAMANIOS[i];
            System.out.println("Midiendo N = " + n + " ...");
            int[] claves = generarClavesMezcladas(n, azar);
            int[] consultas = elegirConsultas(claves, BUSQUEDAS, azar);

            long sumInsAvl = 0, sumBusAvl = 0, sumInsLista = 0, sumBusLista = 0;

            for (int r = 0; r < REPETICIONES; r++) {
                // ----- AVL -----
                ArbolAVL<Integer> avl = new ArbolAVL<>();
                long t0 = System.nanoTime();
                for (int k : claves) {
                    avl.insertar(k);
                }
                sumInsAvl += System.nanoTime() - t0;

                t0 = System.nanoTime();
                int encontrados = 0;
                for (int q : consultas) {
                    if (avl.buscar(q) != null) {
                        encontrados++;
                    }
                }
                sumBusAvl += System.nanoTime() - t0;
                verificar(encontrados, consultas.length);
                alturas[i] = avl.altura();

                // ----- Lista simple -----
                ListaSimple lista = new ListaSimple();
                t0 = System.nanoTime();
                for (int k : claves) {
                    lista.insertar(k);
                }
                sumInsLista += System.nanoTime() - t0;

                t0 = System.nanoTime();
                encontrados = 0;
                for (int q : consultas) {
                    if (lista.buscar(q)) {
                        encontrados++;
                    }
                }
                sumBusLista += System.nanoTime() - t0;
                verificar(encontrados, consultas.length);
            }

            insAvl[i] = sumInsAvl / REPETICIONES;
            insLista[i] = sumInsLista / REPETICIONES;
            busAvl[i] = sumBusAvl / REPETICIONES;
            busLista[i] = sumBusLista / REPETICIONES;
        }

        imprimirTabla(insAvl, insLista, busAvl, busLista, alturas);
        imprimirCsv(insAvl, insLista, busAvl, busLista);
        imprimirConclusiones();
    }

    // ---------------- Utilidades ----------------
    private void calentar() {
        Random azar = new Random(SEMILLA);
        int[] claves = generarClavesMezcladas(2_000, azar);
        for (int vuelta = 0; vuelta < 5; vuelta++) {
            ArbolAVL<Integer> avl = new ArbolAVL<>();
            ListaSimple lista = new ListaSimple();
            for (int k : claves) {
                avl.insertar(k);
                lista.insertar(k);
            }
            for (int k : claves) {
                avl.buscar(k);
                lista.buscar(k);
            }
        }
    }

    /**
     * Claves 0..n-1 en orden aleatorio (Fisher-Yates), sin duplicados.
     */
    private int[] generarClavesMezcladas(int n, Random azar) {
        int[] claves = new int[n];
        for (int i = 0; i < n; i++) {
            claves[i] = i;
        }
        for (int i = n - 1; i > 0; i--) {
            int j = azar.nextInt(i + 1);
            int aux = claves[i];
            claves[i] = claves[j];
            claves[j] = aux;
        }
        return claves;
    }

    private int[] elegirConsultas(int[] claves, int cantidad, Random azar) {
        int[] consultas = new int[cantidad];
        for (int i = 0; i < cantidad; i++) {
            consultas[i] = claves[azar.nextInt(claves.length)];
        }
        return consultas;
    }

    private void verificar(int encontrados, int esperados) {
        if (encontrados != esperados) {
            throw new IllegalStateException("Error en benchmark: se esperaban "
                    + esperados + " aciertos y hubo " + encontrados);
        }
    }

    // ---------------- Reportes ----------------
    private void imprimirTabla(long[] insAvl, long[] insLista,
            long[] busAvl, long[] busLista, int[] alturas) {
        System.out.println();
        System.out.println("RESULTADOS (tiempos en nanosegundos, promedio de "
                + REPETICIONES + " corridas)");
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf("%-9s | %-16s | %-16s | %-18s | %-18s | %-6s%n",
                "N", "Insertar AVL", "Insertar Lista", BUSQUEDAS + " busq. AVL",
                BUSQUEDAS + " busq. Lista", "h(AVL)");
        System.out.println("------------------------------------------------------------------------------------------");
        for (int i = 0; i < TAMANIOS.length; i++) {
            System.out.printf("%-9d | %-16d | %-16d | %-18d | %-18d | %-6d%n",
                    TAMANIOS[i], insAvl[i], insLista[i], busAvl[i], busLista[i], alturas[i]);
        }
        System.out.println("------------------------------------------------------------------------------------------");
    }

    /**
     * Formato CSV para copiar a Excel y generar las gráficas comparativas.
     */
    private void imprimirCsv(long[] insAvl, long[] insLista, long[] busAvl, long[] busLista) {
        System.out.println();
        System.out.println("CSV (copiar a Excel para graficar):");
        System.out.println("N,Insertar_AVL_ns,Insertar_Lista_ns,Buscar_AVL_ns,Buscar_Lista_ns");
        for (int i = 0; i < TAMANIOS.length; i++) {
            System.out.println(TAMANIOS[i] + "," + insAvl[i] + "," + insLista[i]
                    + "," + busAvl[i] + "," + busLista[i]);
        }
    }

    private void imprimirConclusiones() {
        System.out.println();
        System.out.println("ANÁLISIS ESPERADO:");
        System.out.println(" - Búsqueda AVL: O(log n). Al crecer N 10 veces, el tiempo casi no varía.");
        System.out.println(" - Búsqueda en lista: O(n). El tiempo crece ~10 veces cuando N crece 10 veces.");
        System.out.println(" - Inserción en lista (al inicio): O(1) por elemento, más barata que el AVL,");
        System.out.println("   que paga O(log n) por mantener el orden y el balanceo.");
        System.out.println(" - La altura h(AVL) se mantiene cercana a log2(N) gracias a las rotaciones.");
    }
}
