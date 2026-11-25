package modelo;

/**
 * Técnico de la mesa de ayuda. Comparable por ID (indexable en el AVL).
 */
public class Agente implements Comparable<Agente> {

    private final int id;
    private String nombre;
    private Categoria especialidad;
    private int ticketsAtendidos;

    public Agente(int id, String nombre, Categoria especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.ticketsAtendidos = 0;
    }

    /**
     * Constructor ligero para búsquedas por ID.
     */
    public Agente(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Categoria getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Categoria especialidad) {
        this.especialidad = especialidad;
    }

    public int getTicketsAtendidos() {
        return ticketsAtendidos;
    }

    public void incrementarAtendidos() {
        ticketsAtendidos++;
    }

    public void decrementarAtendidos() {
        if (ticketsAtendidos > 0) {
            ticketsAtendidos--;
        }
    }

    @Override
    public int compareTo(Agente otro) {
        return Integer.compare(this.id, otro.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Agente)) {
            return false;
        }
        return this.id == ((Agente) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("[A%d] %-20s | Especialidad: %-18s | Atendidos: %d",
                id, nombre, especialidad, ticketsAtendidos);
    }
}
