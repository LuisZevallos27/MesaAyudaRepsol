package vista;

import modelo.Agente;
import modelo.Categoria;
import modelo.Estado;
import servicio.MesaAyudaService;

public class AgenteVista {

    private final MesaAyudaService servicio;
    private final Consola consola;

    public AgenteVista(MesaAyudaService servicio, Consola consola) {
        this.servicio = servicio;
        this.consola = consola;
    }

    public void listarAgentes() {
        System.out.println("\n--- Agentes registrados ---");
        servicio.recorrerAgentes(System.out::println);
    }

    public void registrarAgente() {
        System.out.println("\n--- Registrar agente ---");
        String nombre = consola.leerTexto("Nombre completo: ");
        Categoria especialidad = consola.elegirEnum("Especialidad:", Categoria.values());
        Agente a = servicio.registrarAgente(nombre, especialidad);
        System.out.println(">> Agente registrado con ID " + a.getId() + ".");
    }

    public void estadisticas() {
        System.out.println("\n--- Estadisticas de la Mesa de Ayuda ---");
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
}
