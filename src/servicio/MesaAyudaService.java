package servicio;

import estructuras.ArbolAVL;
import estructuras.ColaPrioridad;
import estructuras.tads.ColaPrioridadTAD;
import estructuras.tads.ArbolBusquedaTAD;
import excepciones.MesaAyudaException;
import modelo.Agente;
import modelo.Categoria;
import modelo.Estado;
import modelo.Prioridad;
import modelo.Ticket;
import java.util.function.Consumer;

public class MesaAyudaService {

    private final ArbolBusquedaTAD<Ticket> indiceTickets = new ArbolAVL<>();
    private final ArbolBusquedaTAD<Agente> indiceAgentes = new ArbolAVL<>();
    private final ColaPrioridadTAD<Ticket> colaEspera = new ColaPrioridad<>(
            (a, b) -> Integer.compare(a.getPrioridad().getNivel(), b.getPrioridad().getNivel()));

    private int siguienteIdTicket = 1;
    private int siguienteIdAgente = 1;

    //Metodos del agente
    public Agente registrarAgente(String nombre, Categoria especialidad) {
        validarTexto(nombre, "El nombre del agente");
        if (especialidad == null) {
            throw new MesaAyudaException("La especialidad es obligatoria.");
        }
        Agente agente = new Agente(siguienteIdAgente++, nombre.trim(), especialidad);
        indiceAgentes.insertar(agente);
        return agente;
    }

    public Agente buscarAgente(int id) {
        return indiceAgentes.buscar(new Agente(id));
    }

    //Metodos de los tickets
    public Ticket crearTicket(String titulo, String descripcion, String solicitante,
            Categoria categoria, Prioridad prioridad) {
        validarTexto(titulo, "El titulo");
        validarTexto(descripcion, "La descripcion");
        validarTexto(solicitante, "El solicitante");
        if (categoria == null || prioridad == null) {
            throw new MesaAyudaException("La categoria y la prioridad son obligatorias.");
        }
        Ticket ticket = new Ticket(siguienteIdTicket++, titulo.trim(), descripcion.trim(),
                solicitante.trim(), categoria, prioridad);
        indiceTickets.insertar(ticket);
        colaEspera.encolar(ticket);
        return ticket;
    }

    public Ticket buscarTicket(int id) {
        return indiceTickets.buscar(new Ticket(id));
    }

    public Ticket eliminarTicket(int id) {
        Ticket ticket = exigirTicket(id);
        indiceTickets.eliminar(ticket);
        if (ticket.getEstado() == Estado.ABIERTO) {
            colaEspera.eliminar(ticket);
        }
        if (ticket.getAgenteAsignado() != null) {
            ticket.getAgenteAsignado().decrementarAtendidos();
        }
        return ticket;
    }

    public Ticket atenderSiguiente() {
        if (colaEspera.estaVacia()) {
            throw new MesaAyudaException("No hay tickets en espera.");
        }
        if (indiceAgentes.estaVacio()) {
            throw new MesaAyudaException("No hay agentes registrados para atender.");
        }
        Ticket ticket = colaEspera.desencolar();
        Agente agente = seleccionarAgente(ticket.getCategoria());
        ticket.setEstado(Estado.EN_ATENCION);
        ticket.setAgenteAsignado(agente);
        agente.incrementarAtendidos();
        return ticket;
    }

    public void reasignarAgente(int idTicket, int idAgente) {
        Ticket ticket = exigirTicket(idTicket);
        if (ticket.getEstado() != Estado.EN_ATENCION) {
            throw new MesaAyudaException("Solo se pueden reasignar tickets EN_ATENCION.");
        }
        Agente nuevo = buscarAgente(idAgente);
        if (nuevo == null) {
            throw new MesaAyudaException("No existe el agente con ID " + idAgente + ".");
        }
        Agente actual = ticket.getAgenteAsignado();
        if (actual.equals(nuevo)) {
            throw new MesaAyudaException("El ticket ya está asignado a ese agente.");
        }
        actual.decrementarAtendidos();
        nuevo.incrementarAtendidos();
        ticket.setAgenteAsignado(nuevo);
    }

    public void cambiarEstado(int id, Estado nuevo) {
        Ticket ticket = exigirTicket(id);
        Estado anterior = ticket.getEstado();
        if (nuevo == null || !transicionValida(anterior, nuevo)) {
            throw new MesaAyudaException("Transición no permitida: " + anterior + " -> " + nuevo
                    + ". (Los tickets ABIERTO se toman con 'atender siguiente'; "
                    + "CERRADO es un estado final.)");
        }
        ticket.setEstado(nuevo);
    }

    public void cambiarPrioridad(int id, Prioridad nueva) {
        Ticket ticket = exigirTicket(id);
        if (nueva == null) {
            throw new MesaAyudaException("La prioridad es obligatoria.");
        }
        if (ticket.getEstado() == Estado.CERRADO) {
            throw new MesaAyudaException("No se puede modificar un ticket CERRADO.");
        }
        if (ticket.getPrioridad() == nueva) {
            throw new MesaAyudaException("El ticket ya tiene prioridad " + nueva + ".");
        }
        boolean enCola = ticket.getEstado() == Estado.ABIERTO;
        if (enCola) {
            colaEspera.eliminar(ticket);
        }
        ticket.setPrioridad(nueva);
        if (enCola) {
            colaEspera.encolar(ticket);
        }
    }

    //Listar tickets por ID 
    public void recorrerTickets(Consumer<Ticket> accion) {
        indiceTickets.recorridoInorden(accion);
    }

    //Listar tickets por estado
    public void recorrerTicketsPorEstado(Estado estado, Consumer<Ticket> accion) {
        indiceTickets.recorridoInorden(t -> {
            if (t.getEstado() == estado) {
                accion.accept(t);
            }
        });
    }

    //Listar por urgencia
    public void recorrerCola(Consumer<Ticket> accion) {
        colaEspera.recorrer(accion);
    }

    //Listar agentes
    public void recorrerAgentes(Consumer<Agente> accion) {
        indiceAgentes.recorridoInorden(accion);
    }

    //Contar tickets por estado
    public int[] contarPorEstado() {
        int[] conteo = new int[Estado.values().length];
        indiceTickets.recorridoInorden(t -> conteo[t.getEstado().ordinal()]++);
        return conteo;
    }

    //Metodos auxiliares
    private Ticket exigirTicket(int id) {
        Ticket ticket = buscarTicket(id);
        if (ticket == null) {
            throw new MesaAyudaException("No existe el ticket con ID " + id + ".");
        }
        return ticket;
    }

    private void validarTexto(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new MesaAyudaException(campo + " no puede estar vacio.");
        }
    }

    private boolean transicionValida(Estado de, Estado a) {
        switch (de) {
            case EN_ATENCION:
                return a == Estado.RESUELTO;
            case RESUELTO:
                return a == Estado.CERRADO || a == Estado.EN_ATENCION;
            default:
                return false;
        }
    }

    private Agente seleccionarAgente(Categoria categoria) {
        Agente[] mejor = new Agente[1];
        boolean[] mejorEsEspecialista = new boolean[1];

        indiceAgentes.recorridoInorden(a -> {
            boolean especialista = a.getEspecialidad() == categoria;
            Agente actual = mejor[0];
            if (actual == null
                    || (especialista && !mejorEsEspecialista[0])
                    || (especialista == mejorEsEspecialista[0]
                    && a.getTicketsAtendidos() < actual.getTicketsAtendidos())) {
                mejor[0] = a;
                mejorEsEspecialista[0] = especialista;
            }
        });
        return mejor[0];
    }

    //Getters
    public int getCantidadTickets() {
        return indiceTickets.getTamanio();
    }

    public int getCantidadAgentes() {
        return indiceAgentes.getTamanio();
    }

    public int getTicketsEnEspera() {
        return colaEspera.getTamanio();
    }

    public int getAlturaIndice() {
        return indiceTickets.altura();
    }
}
