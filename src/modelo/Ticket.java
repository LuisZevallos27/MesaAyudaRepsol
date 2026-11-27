package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket implements Comparable<Ticket> {

    private static DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private int id;
    private String titulo;
    private String descripcion;
    private String solicitante;

    private Categoria categoria;
    private Prioridad prioridad;
    private Estado estado;
    private Agente agenteAsignado;
    private LocalDateTime fechaCreacion;

    //Constructores
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
    }

    public Ticket(int id) {
        this.id = id;
        this.fechaCreacion = null;
    }

    //Getters y Setters
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

    //Comparar y formato de impresion
    @Override
    public int compareTo(Ticket otro) {
        return Integer.compare(this.id, otro.id);
    }

    @Override
    public boolean equals(Object valor) {
        if (this == valor) {
            return true;
        }
        if (!(valor instanceof Ticket)) {
            return false;
        }
        return this.id == ((Ticket) valor).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("[#%d] %-40.40s | %-7s | %-11s | %-18s | Agente: %s",
                id, titulo, prioridad, estado, categoria,
                agenteAsignado == null ? "Sin asignar" : agenteAsignado.getNombre());
    }
}
