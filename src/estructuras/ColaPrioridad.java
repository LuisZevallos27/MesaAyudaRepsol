package estructuras;

import excepciones.EstructuraVaciaException;
import estructuras.tads.ColaPrioridadTAD;
import java.util.Comparator;
import java.util.function.Consumer;

public class ColaPrioridad<T> implements ColaPrioridadTAD<T> {

    //Clase molde
    private class Nodo {

        private T dato;
        private Nodo siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    private Nodo frente;
    private int tamanio;
    private Comparator<T> comparador;

    //Constructor
    public ColaPrioridad(Comparator<T> comparador) {
        if (comparador == null) {
            throw new IllegalArgumentException("El comparador no puede ser nulo.");
        }
        this.comparador = comparador;
    }

    //Metodos basicos
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
            // avanza mientras el siguiente tenga igual o mayor prioridad
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
            throw new EstructuraVaciaException("La cola de prioridad esta vacia.");
        }
        T dato = frente.dato;
        frente = frente.siguiente;
        tamanio--;
        return dato;
    }

    @Override
    public T verFrente() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La cola de prioridad esta vacia.");
        }
        return frente.dato;
    }

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

    //Recorrer para listar por prioridad
    @Override
    public void recorrer(Consumer<T> accion) {
        Nodo actual = frente;
        while (actual != null) {
            accion.accept(actual.dato);
            actual = actual.siguiente;
        }
    }
}
