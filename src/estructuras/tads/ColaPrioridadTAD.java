package estructuras.tads;

import java.util.function.Consumer;

public interface ColaPrioridadTAD<T> {

    void encolar(T dato);

    T desencolar();

    boolean eliminar(T dato);

    T verFrente();

    void recorrer(Consumer<T> accion);

    //Metodos basicos
    boolean estaVacia();

    int getTamanio();

}
