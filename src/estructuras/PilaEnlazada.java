package estructuras;

import java.util.function.Consumer;

/**
 * Pila (LIFO) genérica implementada con nodos enlazados. Uso: historial de
 * acciones y Undo.
 */
public class PilaEnlazada<T> {

    private class Nodo {

        T dato;
        Nodo siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    private Nodo cima;
    private int tamanio;

    public boolean estaVacia() {
        return cima == null;
    }

    public int getTamanio() {
        return tamanio;
    }

    /**
     * O(1)
     */
    public void apilar(T dato) {
        Nodo nuevo = new Nodo(dato);
        nuevo.siguiente = cima;
        cima = nuevo;
        tamanio++;
    }

    /**
     * O(1)
     */
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
    public T verCima() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La pila está vacía.");
        }
        return cima.dato;
    }

    public void vaciar() {
        cima = null;
        tamanio = 0;
    }

    /**
     * Recorre desde la cima hacia el fondo. O(n)
     */
    public void recorrer(Consumer<T> accion) {
        Nodo actual = cima;
        while (actual != null) {
            accion.accept(actual.dato);
            actual = actual.siguiente;
        }
    }
}
