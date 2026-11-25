package modelo;

/**
 * Registro de una acción reversible. Guarda el estado previo del ticket para
 * poder deshacer (Undo) y rehacer (Redo) desde las pilas.
 */
public class Accion {

    public enum Tipo {
        CREAR_TICKET,
        CAMBIAR_ESTADO,
        CAMBIAR_PRIORIDAD,
        ASIGNAR_AGENTE,
        ELIMINAR_TICKET
    }

    private final Tipo tipo;
    private final Ticket ticket;

    // Valores anteriores (según el tipo de acción)
    private final Estado estadoAnterior;
    private final Prioridad prioridadAnterior;
    private final Agente agenteAnterior;

    // Valores nuevos (necesarios para Redo)
    private final Estado estadoNuevo;
    private final Prioridad prioridadNueva;
    private final Agente agenteNuevo;

    private Accion(Tipo tipo, Ticket ticket,
            Estado estadoAnterior, Estado estadoNuevo,
            Prioridad prioridadAnterior, Prioridad prioridadNueva,
            Agente agenteAnterior, Agente agenteNuevo) {
        this.tipo = tipo;
        this.ticket = ticket;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.prioridadAnterior = prioridadAnterior;
        this.prioridadNueva = prioridadNueva;
        this.agenteAnterior = agenteAnterior;
        this.agenteNuevo = agenteNuevo;
    }

    // ---------------- Fábricas ----------------
    public static Accion crearTicket(Ticket t) {
        return new Accion(Tipo.CREAR_TICKET, t, null, null, null, null, null, null);
    }

    public static Accion eliminarTicket(Ticket t) {
        return new Accion(Tipo.ELIMINAR_TICKET, t, null, null, null, null, null, null);
    }

    public static Accion cambioEstado(Ticket t, Estado anterior, Estado nuevo) {
        return new Accion(Tipo.CAMBIAR_ESTADO, t, anterior, nuevo, null, null, null, null);
    }

    public static Accion cambioPrioridad(Ticket t, Prioridad anterior, Prioridad nueva) {
        return new Accion(Tipo.CAMBIAR_PRIORIDAD, t, null, null, anterior, nueva, null, null);
    }

    public static Accion asignacion(Ticket t, Agente anterior, Agente nuevo) {
        return new Accion(Tipo.ASIGNAR_AGENTE, t, null, null, null, null, anterior, nuevo);
    }

    // ---------------- Getters ----------------
    public Tipo getTipo() {
        return tipo;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Estado getEstadoAnterior() {
        return estadoAnterior;
    }

    public Estado getEstadoNuevo() {
        return estadoNuevo;
    }

    public Prioridad getPrioridadAnterior() {
        return prioridadAnterior;
    }

    public Prioridad getPrioridadNueva() {
        return prioridadNueva;
    }

    public Agente getAgenteAnterior() {
        return agenteAnterior;
    }

    public Agente getAgenteNuevo() {
        return agenteNuevo;
    }

    @Override
    public String toString() {
        return tipo + " -> Ticket #" + ticket.getId();
    }
}
