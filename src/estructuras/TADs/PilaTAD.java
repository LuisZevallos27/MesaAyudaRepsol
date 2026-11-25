package estructuras.TADs;

import java.util.function.Consumer;

public interface PilaTAD<T> {

    void apilar(T dato);

    T desapilar();

    T verCima();

    boolean estaVacia();

    int getTamanio();

    void vaciar();

    void recorrer(Consumer<T> accion);
}
