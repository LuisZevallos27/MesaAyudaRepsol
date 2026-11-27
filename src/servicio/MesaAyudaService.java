package servicio;

import excepciones.MesaAyudaException;
import estructuras.ArbolAVL;
import estructuras.TADs.ArbolBusquedaTAD;
import estructuras.ColaPrioridad;
import estructuras.TADs.ColaPrioridadTAD;
import estructuras.PilaEnlazada;
import estructuras.TADs.PilaTAD;
import modelo.Accion;
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
 * ArbolBusquedaTAD<Ticket> (implementado con ArbolAVL): índice de tickets por
 * ID. - ArbolBusquedaTAD<Agente> (implementado con ArbolAVL): índice de agentes
 * por ID. - ColaPrioridadTAD<Ticket> (implementado con ColaPrioridad): despacho
 * por urgencia. - PilaTAD<Accion> (implementado con PilaEnlazada): Undo y Redo.
 * - ListaTAD<String> (implementado con ListaDoble): historial dentro de cada
 * Ticket.
 */
public class MesaAyudaService {

    private final ArbolBusquedaTAD<Ticket> indiceTickets = new ArbolAVL<>();
    private final ArbolBusquedaTAD<Agente> indiceAgentes = new ArbolAVL<>();
    private final ColaPrioridadTAD<Ticket> colaEspera = new ColaPrioridad<>(
            (a, b) -> Integer.compare(a.getPrioridad().getNivel(), b.getPrioridad().getNivel()));
    private final PilaTAD<Accion> pilaUndo = new PilaEnlazada<>();
    private final PilaTAD<Accion> pilaRedo = new PilaEnlazada<>();

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
        registrarAccion(Accion.crearTicket(ticket));
        return ticket;
    }

    /**
     * @return el ticket o null si no existe. O(log n)
     */
    public Ticket buscarTicket(int id) {
        return indiceTickets.buscar(new Ticket(id));
    }

    /**
     * O(n) en el peor caso (sacar el ticket de la cola).
     */
    public Ticket eliminarTicket(int id) {
        Ticket ticket = exigirTicket(id);
        quitarTicket(ticket);
        registrarAccion(Accion.eliminarTicket(ticket));
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
        aplicarAtencion(ticket, agente);
        registrarAccion(Accion.asignacion(ticket, null, agente));
        return ticket;
    }

    /**
     * Reasigna un ticket que ya está EN_ATENCION a otro agente. O(log n)
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
        moverAgente(ticket, actual, nuevo);
        registrarAccion(Accion.asignacion(ticket, actual, nuevo));
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
        ticket.registrarEvento("Estado cambiado de " + anterior + " a " + nuevo);
        registrarAccion(Accion.cambioEstado(ticket, anterior, nuevo));
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
        Prioridad anterior = ticket.getPrioridad();
        if (anterior == nueva) {
            throw new MesaAyudaException("El ticket ya tiene prioridad " + nueva + ".");
        }
        aplicarPrioridad(ticket, nueva);
        registrarAccion(Accion.cambioPrioridad(ticket, anterior, nueva));
    }

    // =====================================================================
    //  UNDO / REDO (pilas enlazadas)
    // =====================================================================
    public boolean puedeDeshacer() {
        return !pilaUndo.estaVacia();
    }

    public boolean puedeRehacer() {
        return !pilaRedo.estaVacia();
    }

    /**
     * @return la acción deshecha.
     */
    public Accion deshacer() {
        if (pilaUndo.estaVacia()) {
            throw new MesaAyudaException("No hay acciones para deshacer.");
        }
        Accion accion = pilaUndo.desapilar();
        Ticket t = accion.getTicket();

        switch (accion.getTipo()) {
            case CREAR_TICKET:
                quitarTicket(t);
                break;
            case ELIMINAR_TICKET:
                restaurarTicket(t);
                break;
            case CAMBIAR_ESTADO:
                t.setEstado(accion.getEstadoAnterior());
                t.registrarEvento("Deshacer: estado vuelve a " + accion.getEstadoAnterior());
                break;
            case CAMBIAR_PRIORIDAD:
                aplicarPrioridad(t, accion.getPrioridadAnterior());
                break;
            case ASIGNAR_AGENTE:
                if (accion.getAgenteAnterior() == null) {
                    revertirAtencion(t, accion.getAgenteNuevo());
                } else {
                    moverAgente(t, accion.getAgenteNuevo(), accion.getAgenteAnterior());
                }
                break;
        }
        pilaRedo.apilar(accion);
        return accion;
    }

    /**
     * @return la acción rehecha.
     */
    public Accion rehacer() {
        if (pilaRedo.estaVacia()) {
            throw new MesaAyudaException("No hay acciones para rehacer.");
        }
        Accion accion = pilaRedo.desapilar();
        Ticket t = accion.getTicket();

        switch (accion.getTipo()) {
            case CREAR_TICKET:
                restaurarTicket(t);
                break;
            case ELIMINAR_TICKET:
                quitarTicket(t);
                break;
            case CAMBIAR_ESTADO:
                t.setEstado(accion.getEstadoNuevo());
                t.registrarEvento("Rehacer: estado vuelve a " + accion.getEstadoNuevo());
                break;
            case CAMBIAR_PRIORIDAD:
                aplicarPrioridad(t, accion.getPrioridadNueva());
                break;
            case ASIGNAR_AGENTE:
                if (accion.getAgenteAnterior() == null) {
                    colaEspera.eliminar(t);
                    aplicarAtencion(t, accion.getAgenteNuevo());
                } else {
                    moverAgente(t, accion.getAgenteAnterior(), accion.getAgenteNuevo());
                }
                break;
        }
        pilaUndo.apilar(accion);
        return accion;
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
     * Historial de acciones realizadas, de la más reciente a la más antigua.
     */
    public void recorrerHistorialAcciones(Consumer<Accion> accion) {
        pilaUndo.recorrer(accion);
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
    /**
     * Carga agentes y tickets de ejemplo; deja las pilas Undo/Redo vacías.
     */
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

        pilaUndo.vaciar();
        pilaRedo.vaciar();
    }

    // =====================================================================
    //  MÉTODOS PRIVADOS DE APOYO
    // =====================================================================
    private void registrarAccion(Accion accion) {
        pilaUndo.apilar(accion);
        pilaRedo.vaciar(); // una acción nueva invalida el Redo
    }

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

    private void aplicarAtencion(Ticket t, Agente agente) {
        t.setEstado(Estado.EN_ATENCION);
        t.setAgenteAsignado(agente);
        agente.incrementarAtendidos();
        t.registrarEvento("Atendido por " + agente.getNombre());
    }

    private void revertirAtencion(Ticket t, Agente agente) {
        agente.decrementarAtendidos();
        t.setAgenteAsignado(null);
        t.setEstado(Estado.ABIERTO);
        colaEspera.encolar(t);
        t.registrarEvento("Deshacer: ticket devuelto a la cola");
    }

    private void moverAgente(Ticket t, Agente de, Agente a) {
        de.decrementarAtendidos();
        a.incrementarAtendidos();
        t.setAgenteAsignado(a);
        t.registrarEvento("Reasignado de " + de.getNombre() + " a " + a.getNombre());
    }

    private void aplicarPrioridad(Ticket t, Prioridad nueva) {
        boolean enCola = t.getEstado() == Estado.ABIERTO;
        if (enCola) {
            colaEspera.eliminar(t);   // se retira ANTES de cambiar su prioridad
        }
        Prioridad anterior = t.getPrioridad();
        t.setPrioridad(nueva);
        if (enCola) {
            colaEspera.encolar(t);
        }
        t.registrarEvento("Prioridad cambiada de " + anterior + " a " + nueva);
    }

    private void quitarTicket(Ticket t) {
        indiceTickets.eliminar(t);
        if (t.getEstado() == Estado.ABIERTO) {
            colaEspera.eliminar(t);
        }
        if (t.getAgenteAsignado() != null) {
            t.getAgenteAsignado().decrementarAtendidos();
        }
    }

    private void restaurarTicket(Ticket t) {
        indiceTickets.insertar(t);
        if (t.getEstado() == Estado.ABIERTO) {
            colaEspera.encolar(t);
        }
        if (t.getAgenteAsignado() != null) {
            t.getAgenteAsignado().incrementarAtendidos();
        }
    }
}
