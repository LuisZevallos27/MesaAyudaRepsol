package estructuras;

import estructuras.tads.ArbolBusquedaTAD;
import java.util.function.Consumer;

public class ArbolAVL<T extends Comparable<T>> implements ArbolBusquedaTAD<T> {

    //Clase molde 
    private static class NodoAVL<E> {

        private E dato;
        private NodoAVL<E> izquierdo;
        private NodoAVL<E> derecho;
        private int altura;

        private NodoAVL(E dato) {
            this.dato = dato;
            this.altura = 1;
        }
    }

    private NodoAVL<T> raiz;
    private int tamanio;
    private boolean cambio;

    //Constructor
    public ArbolAVL() {
        raiz = null;
        tamanio = 0;
    }

    //Consultas básicas
    @Override
    public boolean estaVacio() {
        return raiz == null;
    }

    @Override
    public int getTamanio() {
        return tamanio;
    }

    @Override
    public int altura() {
        return altura(raiz);
    }

    //Insertar, true si fue exitoso, false si existe uno igual
    @Override
    public boolean insertar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("No se puede insertar un dato nulo.");
        }
        cambio = false;
        raiz = insertar(raiz, dato);
        if (cambio) {
            tamanio++;
        }
        return cambio;
    }

    private NodoAVL<T> insertar(NodoAVL<T> nodo, T dato) {
        if (nodo == null) {
            cambio = true;
            return new NodoAVL<>(dato);
        }
        int cmp = dato.compareTo(nodo.dato);
        if (cmp < 0) {
            nodo.izquierdo = insertar(nodo.izquierdo, dato);
        } else if (cmp > 0) {
            nodo.derecho = insertar(nodo.derecho, dato);
        } else {
            return nodo; // duplicado
        }
        return balancear(nodo);
    }

    //Búsqueda, retorna el elemento buscado o null si no existe
    @Override
    public T buscar(T dato) {
        NodoAVL<T> actual = raiz;
        while (actual != null) {
            int cmp = dato.compareTo(actual.dato);
            if (cmp == 0) {
                return actual.dato;
            }
            actual = (cmp < 0) ? actual.izquierdo : actual.derecho;
        }
        return null;
    }

    @Override
    public boolean contiene(T dato) {
        return buscar(dato) != null;
    }

    // ---------------- Eliminación ----------------
    /**
     * @return true si se eliminó, false si no existía.
     */
    @Override
    public boolean eliminar(T dato) {
        if (dato == null) {
            return false;
        }
        cambio = false;
        raiz = eliminar(raiz, dato);
        if (cambio) {
            tamanio--;
        }
        return cambio;
    }

    private NodoAVL<T> eliminar(NodoAVL<T> nodo, T dato) {
        if (nodo == null) {
            return null;
        }
        int cmp = dato.compareTo(nodo.dato);
        if (cmp < 0) {
            nodo.izquierdo = eliminar(nodo.izquierdo, dato);
        } else if (cmp > 0) {
            nodo.derecho = eliminar(nodo.derecho, dato);
        } else {
            cambio = true;
            if (nodo.izquierdo == null) {
                return nodo.derecho;
            }
            if (nodo.derecho == null) {
                return nodo.izquierdo;
            }
            // Dos hijos: reemplazar por el sucesor inorden (mínimo del subárbol derecho)
            NodoAVL<T> sucesor = minimo(nodo.derecho);
            nodo.dato = sucesor.dato;
            nodo.derecho = eliminarMinimo(nodo.derecho);
        }
        return balancear(nodo);
    }

    private NodoAVL<T> minimo(NodoAVL<T> nodo) {
        while (nodo.izquierdo != null) {
            nodo = nodo.izquierdo;
        }
        return nodo;
    }

    private NodoAVL<T> eliminarMinimo(NodoAVL<T> nodo) {
        if (nodo.izquierdo == null) {
            return nodo.derecho;
        }
        nodo.izquierdo = eliminarMinimo(nodo.izquierdo);
        return balancear(nodo);
    }

    // ---------------- Recorridos ----------------
    /**
     * Recorrido Inorden: visita los elementos en orden ascendente.
     */
    @Override
    public void recorridoInorden(Consumer<T> accion) {
        inorden(raiz, accion);
    }

    private void inorden(NodoAVL<T> nodo, Consumer<T> accion) {
        if (nodo == null) {
            return;
        }
        inorden(nodo.izquierdo, accion);
        accion.accept(nodo.dato);
        inorden(nodo.derecho, accion);
    }

    @Override
    public void recorridoPreorden(Consumer<T> accion) {
        preorden(raiz, accion);
    }

    private void preorden(NodoAVL<T> nodo, Consumer<T> accion) {
        if (nodo == null) {
            return;
        }
        accion.accept(nodo.dato);
        preorden(nodo.izquierdo, accion);
        preorden(nodo.derecho, accion);
    }

    // ---------------- Balanceo AVL ----------------
    private int altura(NodoAVL<T> nodo) {
        return (nodo == null) ? 0 : nodo.altura;
    }

    private int factorBalance(NodoAVL<T> nodo) {
        return (nodo == null) ? 0 : altura(nodo.izquierdo) - altura(nodo.derecho);
    }

    private void actualizarAltura(NodoAVL<T> nodo) {
        nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));
    }

    private NodoAVL<T> rotarDerecha(NodoAVL<T> y) {
        NodoAVL<T> x = y.izquierdo;
        y.izquierdo = x.derecho;
        x.derecho = y;
        actualizarAltura(y);
        actualizarAltura(x);
        return x;
    }

    private NodoAVL<T> rotarIzquierda(NodoAVL<T> x) {
        NodoAVL<T> y = x.derecho;
        x.derecho = y.izquierdo;
        y.izquierdo = x;
        actualizarAltura(x);
        actualizarAltura(y);
        return y;
    }

    private NodoAVL<T> balancear(NodoAVL<T> nodo) {
        actualizarAltura(nodo);
        int fb = factorBalance(nodo);

        if (fb > 1) {                                   // pesado a la izquierda
            if (factorBalance(nodo.izquierdo) < 0) {    // caso Izquierda-Derecha
                nodo.izquierdo = rotarIzquierda(nodo.izquierdo);
            }
            return rotarDerecha(nodo);                  // caso Izquierda-Izquierda
        }
        if (fb < -1) {                                  // pesado a la derecha
            if (factorBalance(nodo.derecho) > 0) {      // caso Derecha-Izquierda
                nodo.derecho = rotarDerecha(nodo.derecho);
            }
            return rotarIzquierda(nodo);                // caso Derecha-Derecha
        }
        return nodo;
    }
}
