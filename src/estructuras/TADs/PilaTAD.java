package estructuras.TADs;

import java.util.function.Consumer;

public interface PilaTAD<T> {

    void apilar(T dato);

    T desapilar();

    T verCima();

    void vaciar();

    void recorrer(Consumer<T> accion);

    //Metodos basicos
    boolean estaVacia();

    int getTamanio();
}
