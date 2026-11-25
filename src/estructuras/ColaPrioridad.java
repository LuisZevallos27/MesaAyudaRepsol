package estructuras;

import estructuras.TADs.ColaPrioridadTAD;
import java.util.Comparator;
import java.util.function.Consumer;

/**
 * Cola de prioridad genérica con lista enlazada ordenada. El comparador define
 * la urgencia: si comparar(a, b) < 0, 'a' sale antes que 'b'. A igual prioridad
 * se respeta el orden de llegada (FIFO, estable).
 *
 * encolar: O(n) | desencolar: O(1) | verFrente: O(1)
 */
public class ColaPrioridad<T> implements ColaPrioridadTAD<T> {

    private class Nodo {

        T dato;
        Nodo siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    private Nodo frente;
    private int tamanio;
    private final Comparator<T> comparador;

    public ColaPrioridad(Comparator<T> comparador) {
        if (comparador == null) {
            throw new IllegalArgumentException("El comparador no puede ser nulo.");
        }
        this.comparador = comparador;
    }

    @Override
    public boolean estaVacia() {
        return frente == null;
    }

    @Override
    public int getTamanio() {
        return tamanio;
    }

    @Override
    public void encolar(T dato) {
        Nodo nuevo = new Nodo(dato);
        if (frente == null || comparador.compare(dato, frente.dato) < 0) {
            nuevo.siguiente = frente;
            frente = nuevo;
        } else {
            Nodo actual = frente;
            // avanza mientras el siguiente tenga igual o mayor prioridad (estable)
            while (actual.siguiente != null
                    && comparador.compare(dato, actual.siguiente.dato) >= 0) {
                actual = actual.siguiente;
            }
            nuevo.siguiente = actual.siguiente;
            actual.siguiente = nuevo;
        }
        tamanio++;
    }

    @Override
    public T desencolar() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La cola de prioridad está vacía.");
        }
        T dato = frente.dato;
        frente = frente.siguiente;
        tamanio--;
        return dato;
    }

    @Override
    public T verFrente() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La cola de prioridad está vacía.");
        }
        return frente.dato;
    }

    /**
     * Elimina un elemento específico (por equals). O(n)
     */
    @Override
    public boolean eliminar(T dato) {
        Nodo anterior = null;
        Nodo actual = frente;
        while (actual != null) {
            if (actual.dato.equals(dato)) {
                if (anterior == null) {
                    frente = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }
                tamanio--;
                return true;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return false;
    }

    /**
     * Recorre del más urgente al menos urgente sin extraer. O(n)
     */
    @Override
    public void recorrer(Consumer<T> accion) {
        Nodo actual = frente;
        while (actual != null) {
            accion.accept(actual.dato);
            actual = actual.siguiente;
        }
    }
}
