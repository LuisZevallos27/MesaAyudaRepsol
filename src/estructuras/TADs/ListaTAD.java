package estructuras.TADs;

import java.util.function.Consumer;

public interface ListaTAD<T> {

    void agregarInicio(T dato);

    void agregarFinal(T dato);

    boolean eliminar(T dato);

    T obtener(int indice);

    boolean estaVacia();

    int getTamanio();

    // Navegación bidireccional con cursor
    T actual();

    boolean haySiguiente();

    boolean hayAnterior();

    T siguiente();

    T anterior();

    void irAlInicio();

    void irAlFinal();

    void recorrer(Consumer<T> accion);

    void recorrerInverso(Consumer<T> accion);
}
