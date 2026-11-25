package estructuras;

import estructuras.TADs.ListaTAD;
import java.util.function.Consumer;

/**
 * Lista doblemente enlazada genérica con cursor de navegación bidireccional
 * (anterior / siguiente).
 */
public class ListaDoble<T> implements ListaTAD<T> {

    private class Nodo {

        T dato;
        Nodo anterior;
        Nodo siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    private Nodo cabeza;
    private Nodo cola;
    private Nodo cursor;
    private int tamanio;

    @Override
    public boolean estaVacia() {
        return cabeza == null;
    }

    @Override
    public int getTamanio() {
        return tamanio;
    }

    // ---------------- Inserción ----------------
    /**
     * O(1)
     */
    @Override
    public void agregarInicio(T dato) {
        Nodo nuevo = new Nodo(dato);
        if (cabeza == null) {
            cabeza = cola = cursor = nuevo;
        } else {
            nuevo.siguiente = cabeza;
            cabeza.anterior = nuevo;
            cabeza = nuevo;
        }
        tamanio++;
    }

    /**
     * O(1)
     */
    public void agregarFinal(T dato) {
        Nodo nuevo = new Nodo(dato);
        if (cola == null) {
            cabeza = cola = cursor = nuevo;
        } else {
            nuevo.anterior = cola;
            cola.siguiente = nuevo;
            cola = nuevo;
        }
        tamanio++;
    }

    // ---------------- Eliminación ----------------
    /**
     * Elimina la primera ocurrencia (por equals). O(n)
     */
    public boolean eliminar(T dato) {
        Nodo actual = cabeza;
        while (actual != null) {
            if (actual.dato.equals(dato)) {
                desenlazar(actual);
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    private void desenlazar(Nodo nodo) {
        if (nodo.anterior != null) {
            nodo.anterior.siguiente = nodo.siguiente;
        } else {
            cabeza = nodo.siguiente;
        }
        if (nodo.siguiente != null) {
            nodo.siguiente.anterior = nodo.anterior;
        } else {
            cola = nodo.anterior;
        }
        if (cursor == nodo) {
            cursor = (nodo.siguiente != null) ? nodo.siguiente : nodo.anterior;
        }
        tamanio--;
    }

    // ---------------- Acceso ----------------
    /**
     * O(n)
     */
    @Override
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        Nodo actual;
        if (indice < tamanio / 2) {
            actual = cabeza;
            for (int i = 0; i < indice; i++) {
                actual = actual.siguiente;
            }
        } else {
            actual = cola;
            for (int i = tamanio - 1; i > indice; i--) {
                actual = actual.anterior;
            }
        }
        return actual.dato;
    }

    // ---------------- Navegación con cursor (O(1)) ----------------
    @Override
    public T actual() {
        if (cursor == null) {
            throw new EstructuraVaciaException("La lista está vacía.");
        }
        return cursor.dato;
    }

    @Override
    public boolean haySiguiente() {
        return cursor != null && cursor.siguiente != null;
    }

    @Override
    public boolean hayAnterior() {
        return cursor != null && cursor.anterior != null;
    }

    @Override
    public T siguiente() {
        if (!haySiguiente()) {
            throw new IllegalStateException("No hay un registro siguiente.");
        }
        cursor = cursor.siguiente;
        return cursor.dato;
    }

    @Override
    public T anterior() {
        if (!hayAnterior()) {
            throw new IllegalStateException("No hay un registro anterior.");
        }
        cursor = cursor.anterior;
        return cursor.dato;
    }

    @Override
    public void irAlInicio() {
        cursor = cabeza;
    }

    @Override
    public void irAlFinal() {
        cursor = cola;
    }

    // ---------------- Recorridos ----------------
    public void recorrer(Consumer<T> accion) {
        Nodo actual = cabeza;
        while (actual != null) {
            accion.accept(actual.dato);
            actual = actual.siguiente;
        }
    }

    public void recorrerInverso(Consumer<T> accion) {
        Nodo actual = cola;
        while (actual != null) {
            accion.accept(actual.dato);
            actual = actual.anterior;
        }
    }
}
