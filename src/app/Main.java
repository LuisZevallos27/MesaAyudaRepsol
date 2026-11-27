package app;

import excepciones.EstructuraVaciaException;
import modelo.Agente;
import modelo.Categoria;
import modelo.Estado;
import modelo.Prioridad;
import modelo.Ticket;
import servicio.BenchmarkService;
import excepciones.MesaAyudaException;
import servicio.MesaAyudaService;
import java.util.Scanner;

/**
 * Mesa de Ayuda Repsol - Interfaz de consola.
 */
public class Main {

    private static final Scanner SC = new Scanner(System.in);
    private static final MesaAyudaService servicio = new MesaAyudaService();

    public static void main(String[] args) {
        servicio.precargarDatos();

        System.out.println("==========================================================");
        System.out.println("        MESA DE AYUDA TI - REPSOL");
        System.out.println("        Datos de ejemplo cargados correctamente.");
        System.out.println("==========================================================");

        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opción: ");
            try {
                ejecutarOpcion(opcion);
            } catch (MesaAyudaException | EstructuraVaciaException
                    | IllegalStateException | IllegalArgumentException e) {
                System.out.println();
                System.out.println(">> ERROR: " + e.getMessage());
            }
        } while (opcion != 0);

        System.out.println("Hasta pronto.");
    }

    // =====================================================================
    //  MENÚ
    // =====================================================================
    private static void mostrarMenu() {
        System.out.println();
        System.out.println("---------------- MENÚ PRINCIPAL ----------------");
        System.out.println(" TICKETS");
        System.out.println("  1. Registrar nuevo ticket");
        System.out.println("  2. Buscar ticket por ID");
        System.out.println("  3. Listar todos los tickets (ordenados por ID)");
        System.out.println("  4. Listar tickets por estado");
        System.out.println("  5. Ver cola de espera (por urgencia)");
        System.out.println("  6. Atender siguiente ticket");
        System.out.println("  7. Cambiar estado de un ticket");
        System.out.println("  8. Cambiar prioridad de un ticket");
        System.out.println("  9. Reasignar agente");
        System.out.println(" 10. Eliminar ticket");
        System.out.println(" AGENTES Y REPORTES");
        System.out.println(" 11. Listar agentes");
        System.out.println(" 12. Registrar agente");
        System.out.println(" 13. Estadísticas");
        System.out.println(" 14. Ejecutar Benchmark (AVL vs Lista)");
        System.out.println("  0. Salir");
        System.out.println("------------------------------------------------");
    }

    private static void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1:
                registrarTicket();
                break;
            case 2:
                buscarTicket();
                break;
            case 3:
                listarTickets();
                break;
            case 4:
                listarPorEstado();
                break;
            case 5:
                verCola();
                break;
            case 6:
                atenderSiguiente();
                break;
            case 7:
                cambiarEstado();
                break;
            case 8:
                cambiarPrioridad();
                break;
            case 9:
                reasignarAgente();
                break;
            case 10:
                eliminarTicket();
                break;
            case 11:
                listarAgentes();
                break;
            case 12:
                registrarAgente();
                break;
            case 13:
                estadisticas();
                break;
            case 14:
                new BenchmarkService().ejecutar();
                break;
            case 0:
                break;
            default:
                System.out.println(">> Opción no válida.");
        }
    }

    // =====================================================================
    //  OPERACIONES DE TICKETS
    // =====================================================================
    private static void registrarTicket() {
        System.out.println("\n--- Registrar nuevo ticket ---");
        String titulo = leerTexto("Título: ");
        String descripcion = leerTexto("Descripción: ");
        String solicitante = leerTexto("Solicitante: ");
        Categoria categoria = elegirEnum("Categoría:", Categoria.values());
        Prioridad prioridad = elegirEnum("Prioridad:", Prioridad.values());

        Ticket t = servicio.crearTicket(titulo, descripcion, solicitante, categoria, prioridad);
        System.out.println(">> Ticket creado con ID #" + t.getId() + " y puesto en la cola de espera.");
    }

    private static void buscarTicket() {
        System.out.println("\n--- Buscar ticket ---");
        int id = leerEntero("ID del ticket: ");
        Ticket t = servicio.buscarTicket(id);
        if (t == null) {
            System.out.println(">> No se encontró el ticket #" + id + ".");
        } else {
            mostrarDetalle(t);
        }
    }

    private static void listarTickets() {
        System.out.println("\n--- Todos los tickets (recorrido Inorden del AVL) ---");
        int[] contador = {0};
        servicio.recorrerTickets(t -> {
            System.out.println(t);
            contador[0]++;
        });
        System.out.println("Total: " + contador[0] + " ticket(s).");
    }

    private static void listarPorEstado() {
        System.out.println("\n--- Tickets por estado ---");
        Estado estado = elegirEnum("Estado:", Estado.values());
        int[] contador = {0};
        servicio.recorrerTicketsPorEstado(estado, t -> {
            System.out.println(t);
            contador[0]++;
        });
        System.out.println("Total en estado " + estado + ": " + contador[0] + " ticket(s).");
    }

    private static void verCola() {
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

    private static void atenderSiguiente() {
        System.out.println("\n--- Atender siguiente ticket ---");
        Ticket t = servicio.atenderSiguiente();
        System.out.println(">> Ticket despachado:");
        System.out.println(t);
    }

    private static void cambiarEstado() {
        System.out.println("\n--- Cambiar estado ---");
        int id = leerEntero("ID del ticket: ");
        Estado nuevo = elegirEnum("Nuevo estado:", Estado.values());
        servicio.cambiarEstado(id, nuevo);
        System.out.println(">> Estado del ticket #" + id + " actualizado a " + nuevo + ".");
    }

    private static void cambiarPrioridad() {
        System.out.println("\n--- Cambiar prioridad ---");
        int id = leerEntero("ID del ticket: ");
        Prioridad nueva = elegirEnum("Nueva prioridad:", Prioridad.values());
        servicio.cambiarPrioridad(id, nueva);
        System.out.println(">> Prioridad del ticket #" + id + " actualizada a " + nueva + ".");
    }

    private static void reasignarAgente() {
        System.out.println("\n--- Reasignar agente ---");
        int idTicket = leerEntero("ID del ticket: ");
        listarAgentes();
        int idAgente = leerEntero("ID del nuevo agente: ");
        servicio.reasignarAgente(idTicket, idAgente);
        System.out.println(">> Ticket #" + idTicket + " reasignado correctamente.");
    }

    private static void eliminarTicket() {
        System.out.println("\n--- Eliminar ticket ---");
        int id = leerEntero("ID del ticket: ");
        Ticket t = servicio.buscarTicket(id);
        if (t == null) {
            System.out.println(">> No se encontró el ticket #" + id + ".");
            return;
        }
        System.out.println(t);
        String confirmacion = leerTexto("¿Confirmar eliminación definitiva? (S/N): ");
        if (confirmacion.equalsIgnoreCase("S")) {
            servicio.eliminarTicket(id);
            System.out.println(">> Ticket #" + id + " eliminado.");
        } else {
            System.out.println(">> Operación cancelada.");
        }
    }

    // =====================================================================
    //  AGENTES Y REPORTES
    // =====================================================================
    private static void listarAgentes() {
        System.out.println("\n--- Agentes registrados ---");
        servicio.recorrerAgentes(System.out::println);
    }

    private static void registrarAgente() {
        System.out.println("\n--- Registrar agente ---");
        String nombre = leerTexto("Nombre completo: ");
        Categoria especialidad = elegirEnum("Especialidad:", Categoria.values());
        Agente a = servicio.registrarAgente(nombre, especialidad);
        System.out.println(">> Agente registrado con ID " + a.getId() + ".");
    }

    private static void estadisticas() {
        System.out.println("\n--- Estadísticas de la Mesa de Ayuda ---");
        int[] conteo = servicio.contarPorEstado();
        Estado[] estados = Estado.values();
        System.out.println("Tickets totales      : " + servicio.getCantidadTickets());
        for (int i = 0; i < estados.length; i++) {
            System.out.printf("  %-12s       : %d%n", estados[i], conteo[i]);
        }
        System.out.println("Tickets en espera    : " + servicio.getTicketsEnEspera());
        System.out.println("Agentes registrados  : " + servicio.getCantidadAgentes());
        System.out.println("Altura del AVL       : " + servicio.getAlturaIndice());
    }

    // =====================================================================
    //  UTILIDADES DE ENTRADA / SALIDA
    // =====================================================================
    private static void mostrarDetalle(Ticket t) {
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

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return SC.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = SC.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println(">> Ingrese un número entero válido.");
            }
        }
    }

    private static <E extends Enum<E>> E elegirEnum(String titulo, E[] valores) {
        System.out.println(titulo);
        for (int i = 0; i < valores.length; i++) {
            System.out.println("  " + (i + 1) + ". " + valores[i]);
        }
        while (true) {
            int opcion = leerEntero("Elija una opción: ");
            if (opcion >= 1 && opcion <= valores.length) {
                return valores[opcion - 1];
            }
            System.out.println(">> Opción fuera de rango.");
        }
    }
}
