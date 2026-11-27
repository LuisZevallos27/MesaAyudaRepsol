package app;

import servicio.DatosEjemplo;
import servicio.MesaAyudaService;
import vista.MenuPrincipal;

/**
 * Mesa de Ayuda Repsol - Punto de entrada.
 */
public class Main {

    public static void main(String[] args) {
        MesaAyudaService servicio = new MesaAyudaService();
        DatosEjemplo.cargar(servicio);
        new MenuPrincipal(servicio).iniciar();
    }
}
