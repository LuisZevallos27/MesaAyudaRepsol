package vista;

import modelo.Categoria;
import modelo.Estado;
import modelo.Prioridad;
import modelo.Ticket;
import servicio.MesaAyudaService;

/**
 * Pantallas relacionadas con los tickets (opciones 1 a 10).
 */
public class TicketVista {

    private final MesaAyudaService servicio;
    private final Consola consola;

    public TicketVista(MesaAyudaService servicio, Consola consola) {
        this.servicio = servicio;
        this.consola = consola;
    }

    public void registrarTicket() {
        System.out.println("\n--- Registrar nuevo ticket ---");
        String titulo = consola.leerTexto("Título: ");
        String descripcion = consola.leerTexto("Descripción: ");
        String solicitante = consola.leerTexto("Solicitante: ");
        Categoria categoria = consola.elegirEnum("Categoría:", Categoria.values());
        Prioridad prioridad = consola.elegirEnum("Prioridad:", Prioridad.values());

        Ticket t = servicio.crearTicket(titulo, descripcion, solicitante, categoria, prioridad);
        System.out.println(">> Ticket creado con ID #" + t.getId() + " y puesto en la cola de espera.");
    }

    public void buscarTicket() {
        System.out.println("\n--- Buscar ticket ---");
        int id = consola.leerEntero("ID del ticket: ");
        Ticket t = servicio.buscarTicket(id);
        if (t == null) {
            System.out.println(">> No se encontró el ticket #" + id + ".");
        } else {
            mostrarDetalle(t);
        }
    }

    public void listarTickets() {
        System.out.println("\n--- Todos los tickets (recorrido Inorden del AVL) ---");
        int[] contador = {0};
        servicio.recorrerTickets(t -> {
            System.out.println(t);
            contador[0]++;
        });
        System.out.println("Total: " + contador[0] + " ticket(s).");
    }

    public void listarPorEstado() {
        System.out.println("\n--- Tickets por estado ---");
        Estado estado = consola.elegirEnum("Estado:", Estado.values());
        int[] contador = {0};
        servicio.recorrerTicketsPorEstado(estado, t -> {
            System.out.println(t);
            contador[0]++;
        });
        System.out.println("Total en estado " + estado + ": " + contador[0] + " ticket(s).");
    }

    public void verCola() {
        System.out.println("\n--- Cola de espera (del más urgente al menos urgente) ---");
        int[] posicion = {0};
        servicio.recorrerCola(t -> {
            posicion[0]++;
            System.out.println(posicion[0] + ". " + t);
        });
        if (posicion[0] == 0) {
            System.out.println("No hay tickets en espera.");
        }
    }

    public void atenderSiguiente() {
        System.out.println("\n--- Atender siguiente ticket ---");
        Ticket t = servicio.atenderSiguiente();
        System.out.println(">> Ticket despachado:");
        System.out.println(t);
    }

    public void cambiarEstado() {
        System.out.println("\n--- Cambiar estado ---");
        int id = consola.leerEntero("ID del ticket: ");
        Estado nuevo = consola.elegirEnum("Nuevo estado:", Estado.values());
        servicio.cambiarEstado(id, nuevo);
        System.out.println(">> Estado del ticket #" + id + " actualizado a " + nuevo + ".");
    }

    public void cambiarPrioridad() {
        System.out.println("\n--- Cambiar prioridad ---");
        int id = consola.leerEntero("ID del ticket: ");
        Prioridad nueva = consola.elegirEnum("Nueva prioridad:", Prioridad.values());
        servicio.cambiarPrioridad(id, nueva);
        System.out.println(">> Prioridad del ticket #" + id + " actualizada a " + nueva + ".");
    }

    public void reasignarAgente() {
        System.out.println("\n--- Reasignar agente ---");
        int idTicket = consola.leerEntero("ID del ticket: ");
        System.out.println("Agentes disponibles:");
        servicio.recorrerAgentes(System.out::println);
        int idAgente = consola.leerEntero("ID del nuevo agente: ");
        servicio.reasignarAgente(idTicket, idAgente);
        System.out.println(">> Ticket #" + idTicket + " reasignado correctamente.");
    }

    public void eliminarTicket() {
        System.out.println("\n--- Eliminar ticket ---");
        int id = consola.leerEntero("ID del ticket: ");
        Ticket t = servicio.buscarTicket(id);
        if (t == null) {
            System.out.println(">> No se encontró el ticket #" + id + ".");
            return;
        }
        System.out.println(t);
        String confirmacion = consola.leerTexto("¿Confirmar eliminación definitiva? (S/N): ");
        if (confirmacion.equalsIgnoreCase("S")) {
            servicio.eliminarTicket(id);
            System.out.println(">> Ticket #" + id + " eliminado.");
        } else {
            System.out.println(">> Operación cancelada.");
        }
    }

    private void mostrarDetalle(Ticket t) {
        System.out.println("\n===== TICKET #" + t.getId() + " =====");
        System.out.println("Título       : " + t.getTitulo());
        System.out.println("Descripción  : " + t.getDescripcion());
        System.out.println("Solicitante  : " + t.getSolicitante());
        System.out.println("Categoría    : " + t.getCategoria());
        System.out.println("Prioridad    : " + t.getPrioridad());
        System.out.println("Estado       : " + t.getEstado());
        System.out.println("Agente       : "
                + (t.getAgenteAsignado() == null ? "Sin asignar" : t.getAgenteAsignado().getNombre()));
        System.out.println("Creado       : " + t.getFechaFormateada());
    }
}
