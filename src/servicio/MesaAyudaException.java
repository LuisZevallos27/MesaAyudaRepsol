package servicio;

/**
 * Excepción de reglas de negocio de la mesa de ayuda (ticket inexistente,
 * transición de estado inválida, datos vacíos, etc.).
 */
public class MesaAyudaException extends RuntimeException {

    public MesaAyudaException(String mensaje) {
        super(mensaje);
    }
}
