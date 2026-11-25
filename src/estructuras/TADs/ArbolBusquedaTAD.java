package estructuras.TADs;

import java.util.function.Consumer;

public interface ArbolBusquedaTAD<T extends Comparable<T>> {

    boolean insertar(T dato);

    T buscar(T dato);

    boolean contiene(T dato);

    boolean eliminar(T dato);

    boolean estaVacio();

    int getTamanio();

    int altura();

    void recorridoInorden(Consumer<T> accion);

    void recorridoPreorden(Consumer<T> accion);
}
