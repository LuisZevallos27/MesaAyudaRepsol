package modelo;

//Guardar y volver a un estado previo de un ticket y deshacer esa accion
public class Accion {

    private Tipo tipo;
    private Ticket ticket;

    // Valores anteriores
    private Estado estadoAnterior;
    private Prioridad prioridadAnterior;
    private Agente agenteAnterior;

    // Valores nuevos
    private Estado estadoNuevo;
    private Prioridad prioridadNueva;
    private Agente agenteNuevo;

    //Constructor    
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
    
    //Operaciones   
    public static Accion crearTicket(Ticket ticket) {
        return new Accion(Tipo.CREAR_TICKET, ticket, null, null, null, null, null, null);
    }

    public static Accion eliminarTicket(Ticket ticket) {
        return new Accion(Tipo.ELIMINAR_TICKET, ticket, null, null, null, null, null, null);
    }

    public static Accion cambioEstado(Ticket ticket, Estado anterior, Estado nuevo) {
        return new Accion(Tipo.CAMBIAR_ESTADO, ticket, anterior, nuevo, null, null, null, null);
    }

    public static Accion cambioPrioridad(Ticket ticket, Prioridad anterior, Prioridad nueva) {
        return new Accion(Tipo.CAMBIAR_PRIORIDAD, ticket, null, null, anterior, nueva, null, null);
    }

    public static Accion asignacion(Ticket ticket, Agente anterior, Agente nuevo) {
        return new Accion(Tipo.ASIGNAR_AGENTE, ticket, null, null, null, null, anterior, nuevo);
    }

    //Getters
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

    //Impresion
    @Override
    public String toString() {
        return tipo + " -> Ticket #" + ticket.getId();
    }
}
