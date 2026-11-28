package app;

import servicio.DatosEjemplo;
import servicio.MesaAyudaService;
import vista.MenuPrincipal;

public class Main {

    public static void main(String[] args) {
        
        //Cargar datos de ejemplo
        MesaAyudaService servicio = new MesaAyudaService();
        DatosEjemplo.cargar(servicio);
        
        //Cargar menú
        MenuPrincipal menu = new MenuPrincipal(servicio);
        menu.iniciar();
    }
}
