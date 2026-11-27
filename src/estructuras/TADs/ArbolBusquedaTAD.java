package estructuras.TADs;

import java.util.function.Consumer;

public interface ArbolBusquedaTAD<T extends Comparable<T>> {

    boolean insertar(T dato);

    boolean eliminar(T dato);

    T buscar(T dato);

    boolean contiene(T dato);

    int altura();

    void recorridoInorden(Consumer<T> accion);

    void recorridoPreorden(Consumer<T> accion);

    //Metodos basicos
    boolean estaVacio();

    int getTamanio();
}
