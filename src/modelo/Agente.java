package modelo;

public class Agente implements Comparable<Agente> {

    private int id;
    private String nombre;
    private Categoria especialidad;
    private int ticketsAtendidos;

    //Constructores
    public Agente(int id, String nombre, Categoria especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.ticketsAtendidos = 0;
    }

    public Agente(int id) {
        this.id = id;
    }

    //Getters y Setters
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

    //Metodos basicos
    public void incrementarAtendidos() {
        ticketsAtendidos++;
    }

    public void decrementarAtendidos() {
        if (ticketsAtendidos > 0) {
            ticketsAtendidos--;
        }
    }

    //Comparar y formato de impresion
    @Override
    public int compareTo(Agente otro) {
        return Integer.compare(this.id, otro.id);
    }

    @Override
    public boolean equals(Object valor) {
        if (this == valor) {
            return true;
        }
        if (!(valor instanceof Agente)) {
            return false;
        }
        return this.id == ((Agente) valor).id;
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
