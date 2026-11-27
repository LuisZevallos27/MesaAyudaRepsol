package estructuras;

import excepciones.EstructuraVaciaException;
import estructuras.TADs.PilaTAD;
import java.util.function.Consumer;

/**
 * Pila (LIFO) genérica implementada con nodos enlazados. Uso: historial de
 * acciones y Undo/Redo.
 */
public class PilaEnlazada<T> implements PilaTAD<T> {

    private class Nodo {

        T dato;
        Nodo siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    private Nodo cima;
    private int tamanio;

    @Override
    public boolean estaVacia() {
        return cima == null;
    }

    @Override
    public int getTamanio() {
        return tamanio;
    }

    /**
     * O(1)
     */
    @Override
    public void apilar(T dato) {
        Nodo nuevo = new Nodo(dato);
        nuevo.siguiente = cima;
        cima = nuevo;
        tamanio++;
    }

    /**
     * O(1)
     */
    @Override
    public T desapilar() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La pila está vacía.");
        }
        T dato = cima.dato;
        cima = cima.siguiente;
        tamanio--;
        return dato;
    }

    /**
     * O(1)
     */
    @Override
    public T verCima() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La pila está vacía.");
        }
        return cima.dato;
    }

    @Override
    public void vaciar() {
        cima = null;
        tamanio = 0;
    }

    /**
     * Recorre desde la cima hacia el fondo. O(n)
     */
    @Override
    public void recorrer(Consumer<T> accion) {
        Nodo actual = cima;
        while (actual != null) {
            accion.accept(actual.dato);
            actual = actual.siguiente;
        }
    }
}
