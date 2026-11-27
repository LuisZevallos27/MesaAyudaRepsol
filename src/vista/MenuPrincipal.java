package vista;

import excepciones.EstructuraVaciaException;
import excepciones.MesaAyudaException;
import servicio.BenchmarkService;
import servicio.MesaAyudaService;

/**
 * Muestra el menú y dirige cada opción a su pantalla.
 */
public class MenuPrincipal {

    private final Consola consola = new Consola();
    private final TicketVista tickets;
    private final AgenteVista agentes;

    public MenuPrincipal(MesaAyudaService servicio) {
        this.tickets = new TicketVista(servicio, consola);
        this.agentes = new AgenteVista(servicio, consola);
    }

    public void iniciar() {
        System.out.println("==========================================================");
        System.out.println("        MESA DE AYUDA TI - REPSOL");
        System.out.println("==========================================================");

        int opcion;
        do {
            mostrarMenu();
            opcion = consola.leerEntero("Seleccione una opcion: ");
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

    private void mostrarMenu() {
        System.out.println();
        System.out.println("---------------- MENU PRINCIPAL ----------------");
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
        System.out.println("---------------- AGENTES Y REPORTES ----------------");
        System.out.println(" 11. Listar agentes");
        System.out.println(" 12. Registrar agente");
        System.out.println(" 13. Estadisticas");
        System.out.println(" 14. Ejecutar Benchmark (AVL vs Lista)");
        System.out.println("  0. Salir");
        System.out.println("------------------------------------------------");
    }

    private void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1:
                tickets.registrarTicket();
                break;
            case 2:
                tickets.buscarTicket();
                break;
            case 3:
                tickets.listarTickets();
                break;
            case 4:
                tickets.listarPorEstado();
                break;
            case 5:
                tickets.verCola();
                break;
            case 6:
                tickets.atenderSiguiente();
                break;
            case 7:
                tickets.cambiarEstado();
                break;
            case 8:
                tickets.cambiarPrioridad();
                break;
            case 9:
                tickets.reasignarAgente();
                break;
            case 10:
                tickets.eliminarTicket();
                break;
            case 11:
                agentes.listarAgentes();
                break;
            case 12:
                agentes.registrarAgente();
                break;
            case 13:
                agentes.estadisticas();
                break;
            case 14:
                new BenchmarkService().ejecutar();
                break;
            case 0:
                break;
            default:
                System.out.println(">> Opción no valida.");
        }
    }
}
