package modelo;

import estructuras.ListaDoble;
import estructuras.TADs.ListaTAD;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Solicitud de soporte de la mesa de ayuda. Es Comparable por ID para poder
 * indexarse en el árbol de búsqueda. Incluye un historial propio (ListaTAD)
 * navegable hacia adelante y atrás.
 */
public class Ticket implements Comparable<Ticket> {

    private static final DateTimeFormatter FORMATO
            = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final int id;
    private String titulo;
    private String descripcion;
    private String solicitante;
    private Categoria categoria;
    private Prioridad prioridad;
    private Estado estado;
    private Agente agenteAsignado;
    private final LocalDateTime fechaCreacion;
    private final ListaTAD<String> historial;

    public Ticket(int id, String titulo, String descripcion, String solicitante,
            Categoria categoria, Prioridad prioridad) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.solicitante = solicitante;
        this.categoria = categoria;
        this.prioridad = prioridad;
        this.estado = Estado.ABIERTO;
        this.fechaCreacion = LocalDateTime.now();
        this.historial = new ListaDoble<>();
        registrarEvento("Ticket creado con prioridad " + prioridad);
    }

    /**
     * Constructor ligero para búsquedas por ID en el árbol.
     */
    public Ticket(int id) {
        this.id = id;
        this.fechaCreacion = null;
        this.historial = null;
    }

    // ---------------- Historial ----------------
    public void registrarEvento(String evento) {
        if (historial != null) {
            historial.agregarFinal(LocalDateTime.now().format(FORMATO) + " - " + evento);
        }
    }

    public ListaTAD<String> getHistorial() {
        return historial;
    }

    // ---------------- Getters / Setters ----------------
    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Agente getAgenteAsignado() {
        return agenteAsignado;
    }

    public void setAgenteAsignado(Agente agenteAsignado) {
        this.agenteAsignado = agenteAsignado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public String getFechaFormateada() {
        return fechaCreacion == null ? "-" : fechaCreacion.format(FORMATO);
    }

    // ---------------- Comparable / equals ----------------
    @Override
    public int compareTo(Ticket otro) {
        return Integer.compare(this.id, otro.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ticket)) {
            return false;
        }
        return this.id == ((Ticket) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("[#%d] %-30s | %-7s | %-11s | %-18s | Agente: %s",
                id, titulo, prioridad, estado, categoria,
                agenteAsignado == null ? "Sin asignar" : agenteAsignado.getNombre());
    }
}
