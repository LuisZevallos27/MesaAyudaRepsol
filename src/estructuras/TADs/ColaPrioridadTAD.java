package estructuras.TADs;

import java.util.function.Consumer;

public interface ColaPrioridadTAD<T> {

    void encolar(T dato);

    T desencolar();

    T verFrente();

    boolean eliminar(T dato);

    boolean estaVacia();

    int getTamanio();

    void recorrer(Consumer<T> accion);
}
