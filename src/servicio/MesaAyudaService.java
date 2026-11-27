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

/**
 * Lógica central de la Mesa de Ayuda de Repsol.
 *
 * El servicio depende de los TADs (interfaces), no de las implementaciones: -
 * ArbolBusquedaTAD<Ticket> (ArbolAVL) : índice de tickets por ID. -
 * ArbolBusquedaTAD<Agente> (ArbolAVL) : índice de agentes por ID. -
 * ColaPrioridadTAD<Ticket> (ColaPrioridad): despacho de tickets por urgencia.
 */
public class MesaAyudaService {

    private final ArbolBusquedaTAD<Ticket> indiceTickets = new ArbolAVL<>();
    private final ArbolBusquedaTAD<Agente> indiceAgentes = new ArbolAVL<>();
    private final ColaPrioridadTAD<Ticket> colaEspera = new ColaPrioridad<>(
            (a, b) -> Integer.compare(a.getPrioridad().getNivel(), b.getPrioridad().getNivel()));

    private int siguienteIdTicket = 1001;
    private int siguienteIdAgente = 1;

    // =====================================================================
    //  AGENTES
    // =====================================================================
    /**
     * O(log n)
     */
    public Agente registrarAgente(String nombre, Categoria especialidad) {
        validarTexto(nombre, "El nombre del agente");
        if (especialidad == null) {
            throw new MesaAyudaException("La especialidad es obligatoria.");
        }
        Agente agente = new Agente(siguienteIdAgente++, nombre.trim(), especialidad);
        indiceAgentes.insertar(agente);
        return agente;
    }

    /**
     * @return el agente o null si no existe. O(log n)
     */
    public Agente buscarAgente(int id) {
        return indiceAgentes.buscar(new Agente(id));
    }

    // =====================================================================
    //  TICKETS: CRUD
    // =====================================================================
    /**
     * O(n) por la inserción ordenada en la cola; O(log n) en el AVL.
     */
    public Ticket crearTicket(String titulo, String descripcion, String solicitante,
            Categoria categoria, Prioridad prioridad) {
        validarTexto(titulo, "El título");
        validarTexto(descripcion, "La descripción");
        validarTexto(solicitante, "El solicitante");
        if (categoria == null || prioridad == null) {
            throw new MesaAyudaException("La categoría y la prioridad son obligatorias.");
        }
        Ticket ticket = new Ticket(siguienteIdTicket++, titulo.trim(), descripcion.trim(),
                solicitante.trim(), categoria, prioridad);
        indiceTickets.insertar(ticket);
        colaEspera.encolar(ticket);
        return ticket;
    }

    /**
     * @return el ticket o null si no existe. O(log n)
     */
    public Ticket buscarTicket(int id) {
        return indiceTickets.buscar(new Ticket(id));
    }

    /**
     * Eliminación definitiva. O(n) en el peor caso (sacar el ticket de la
     * cola).
     */
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

    // =====================================================================
    //  TICKETS: FLUJO DE ATENCIÓN
    // =====================================================================
    /**
     * Despacha el ticket más urgente de la cola y lo asigna al agente más
     * adecuado (misma especialidad y menor carga). O(a) por la selección del
     * agente (recorrido Inorden); extraer de la cola es O(1).
     */
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

    /**
     * Reasigna un ticket EN_ATENCION a otro agente. O(log n)
     */
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

    /**
     * O(log n)
     */
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

    /**
     * O(n) si el ticket está en la cola (se reubica según su nueva urgencia).
     */
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
            colaEspera.eliminar(ticket);   // se retira ANTES de cambiar su prioridad
        }
        ticket.setPrioridad(nueva);
        if (enCola) {
            colaEspera.encolar(ticket);
        }
    }

    // =====================================================================
    //  CONSULTAS Y RECORRIDOS
    // =====================================================================
    /**
     * Listado ordenado por ID (recorrido Inorden del AVL). O(n)
     */
    public void recorrerTickets(Consumer<Ticket> accion) {
        indiceTickets.recorridoInorden(accion);
    }

    /**
     * Listado filtrado por estado, ordenado por ID. O(n)
     */
    public void recorrerTicketsPorEstado(Estado estado, Consumer<Ticket> accion) {
        indiceTickets.recorridoInorden(t -> {
            if (t.getEstado() == estado) {
                accion.accept(t);
            }
        });
    }

    /**
     * Tickets en espera, del más urgente al menos urgente. O(n)
     */
    public void recorrerCola(Consumer<Ticket> accion) {
        colaEspera.recorrer(accion);
    }

    public void recorrerAgentes(Consumer<Agente> accion) {
        indiceAgentes.recorridoInorden(accion);
    }

    /**
     * Conteo de tickets por estado; el índice es Estado.ordinal(). O(n)
     */
    public int[] contarPorEstado() {
        int[] conteo = new int[Estado.values().length];
        indiceTickets.recorridoInorden(t -> conteo[t.getEstado().ordinal()]++);
        return conteo;
    }

    public int getCantidadTickets() {
        return indiceTickets.getTamanio();
    }

    public int getCantidadAgentes() {
        return indiceAgentes.getTamanio();
    }

    public int getTicketsEnEspera() {
        return colaEspera.getTamanio();
    }

    /**
     * Altura actual del AVL de tickets (útil para mostrar el balanceo).
     */
    public int getAlturaIndice() {
        return indiceTickets.altura();
    }

    // =====================================================================
    //  DATOS DE EJEMPLO
    // =====================================================================
    public void precargarDatos() {
        registrarAgente("Carlos Mendoza", Categoria.REDES);
        registrarAgente("Lucía Paredes", Categoria.SOFTWARE);
        registrarAgente("Jorge Quispe", Categoria.HARDWARE);
        registrarAgente("María Salazar", Categoria.SAP_Y_APLICACIONES);
        registrarAgente("Andrea Ríos", Categoria.ACCESOS_Y_CUENTAS);

        crearTicket("Caída de VPN en Refinería", "Los operadores no pueden conectarse por VPN a los sistemas de planta.",
                "Ricardo Flores", Categoria.REDES, Prioridad.CRITICA);
        crearTicket("Bloqueo de cuenta de dominio", "Usuario bloqueado tras varios intentos fallidos.",
                "Patricia Vega", Categoria.ACCESOS_Y_CUENTAS, Prioridad.MEDIA);
        crearTicket("Error SAP módulo MM", "No permite registrar entrada de mercancías.",
                "Luis Camacho", Categoria.SAP_Y_APLICACIONES, Prioridad.ALTA);
        crearTicket("Laptop no enciende", "Equipo de oficina San Isidro sin respuesta al botón de encendido.",
                "Sofía Delgado", Categoria.HARDWARE, Prioridad.MEDIA);
        crearTicket("Impresora de despacho sin conexión", "No imprime guías de remisión en el terminal.",
                "Miguel Torres", Categoria.HARDWARE, Prioridad.ALTA);
        crearTicket("Actualización de Office", "Solicitud de instalación de la versión corporativa.",
                "Elena Rojas", Categoria.SOFTWARE, Prioridad.BAJA);
        crearTicket("Sin acceso a carpeta compartida", "Permisos denegados en la carpeta de Logística.",
                "Diego Salinas", Categoria.ACCESOS_Y_CUENTAS, Prioridad.MEDIA);
        crearTicket("Caída de Wi-Fi en estación de servicio", "Sin red inalámbrica en el área de caja.",
                "Valeria Núñez", Categoria.REDES, Prioridad.ALTA);
        crearTicket("Alta de usuario SAP", "Nuevo analista requiere usuario y roles de Finanzas.",
                "Héctor Ponce", Categoria.SAP_Y_APLICACIONES, Prioridad.BAJA);
        crearTicket("Lentitud en sistema de facturación", "Tiempos de respuesta superiores a 30 segundos.",
                "Natalia Cruz", Categoria.SOFTWARE, Prioridad.CRITICA);

        // Algunos tickets ya en curso para que las capturas muestren distintos estados
        Ticket t1 = atenderSiguiente();
        atenderSiguiente();
        atenderSiguiente();
        cambiarEstado(t1.getId(), Estado.RESUELTO);
    }

    // =====================================================================
    //  MÉTODOS PRIVADOS DE APOYO
    // =====================================================================
    private Ticket exigirTicket(int id) {
        Ticket ticket = buscarTicket(id);
        if (ticket == null) {
            throw new MesaAyudaException("No existe el ticket con ID " + id + ".");
        }
        return ticket;
    }

    private void validarTexto(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new MesaAyudaException(campo + " no puede estar vacío.");
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

    /**
     * Prefiere agente de la misma especialidad; desempata por menor carga.
     */
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
}
