package estructuras;

/**
 * Excepción lanzada cuando se intenta extraer o consultar un elemento de una
 * estructura que no contiene datos.
 */
public class EstructuraVaciaException extends RuntimeException {

    public EstructuraVaciaException(String mensaje) {
        super(mensaje);
    }
}
